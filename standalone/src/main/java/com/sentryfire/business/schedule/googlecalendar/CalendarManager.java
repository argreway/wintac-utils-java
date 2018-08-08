 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= * Author:    Tony Greway
  * File:      CalendarManager.java
  * Created:   7/13/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.googlecalendar;

 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.security.GeneralSecurityException;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;

 import com.google.api.client.auth.oauth2.Credential;
 import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
 import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
 import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
 import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
 import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
 import com.google.api.client.googleapis.batch.BatchRequest;
 import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
 import com.google.api.client.http.HttpTransport;
 import com.google.api.client.http.javanet.NetHttpTransport;
 import com.google.api.client.json.JsonFactory;
 import com.google.api.client.json.jackson2.JacksonFactory;
 import com.google.api.client.util.store.FileDataStoreFactory;
 import com.google.api.services.calendar.Calendar;
 import com.google.api.services.calendar.CalendarScopes;
 import com.google.api.services.calendar.model.CalendarList;
 import com.google.api.services.calendar.model.CalendarListEntry;
 import com.google.api.services.calendar.model.Event;
 import com.google.api.services.calendar.model.Events;
 import com.google.common.collect.Maps;
 import com.sentryfire.config.TechProfile;
 import com.sentryfire.config.TechProfileConfiguration;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class CalendarManager
 {
    Logger log = LoggerFactory.getLogger(getClass());

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FOLDER = "/tmp/credentials"; // Directory to store user credentials.

    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    //    private static final String CLIENT_SECRET_FILE = "client_secret.json";
    private static final String CLIENT_SECRET_FILE = "sentry-scheduler-official.json";
//    private static final String CLIENT_SECRET_FILE = "sentry-scheduler2.json";

    public static String CAL_NAME_PRIMARY = "primary";

    protected static Map<String, String> calendarNameToID = Maps.newHashMap();

    protected static CalendarManager instance;

    protected static Calendar service;

    public static synchronized CalendarManager getInstance()
    {
       if (instance == null)
       {
          instance = new CalendarManager();
          instance.connect();
          instance.buildCalendarNameMap();
       }
       return instance;
    }

    private CalendarManager()
    {
    }

    public void bulkAddEvents(List<Event> events,
                              String calName)
    {
       if (events == null || events.isEmpty())
          return;

       try
       {
          BatchRequest batch = service.batch();
          for (Event e : events)
          {
             try
             {
                service.events().insert(getCalID(calName), e).queue(batch, new EventCallBack());
             }
             catch (Exception ex)
             {
                log.error("Failed to submit event " + e.getId() + ", due to: ", ex);
             }
          }
          batch.execute();
       }
       catch (Exception e)
       {
          log.error("Failed to update calendar ", e);
       }
    }

    public void createCalendar()
    {
//       com.google.api.services.calendar.model.Calendar calendar = new Calendar();
//       calendar.setSummary("sentry");
//       calendar.setTimeZone("America/Los_Angeles");
//
//        //Insert the new calendar
//       Calendar createdCalendar = service.calendars().insert(calendar).execute();

    }

    public void deleteEvent(String eventID,
                            String calName) throws Exception
    {
       service.events().delete(getCalID(calName), eventID).execute();
    }

    public CalendarList listCalendars() throws Exception
    {
       return service.calendarList().list().execute();
    }

    /**
     * not sure why some events end up becoming recurring events?
     */
    public Events listEvents(String calName) throws Exception
    {
//       return service.events().list(getCalID(calName)).execute();
       return service.events().list(getCalID(calName)).setSingleEvents(true).execute();
    }

    public void deleteAllCalendarEvents(String calName) throws Exception
    {
       if (getCalID(calName) == null)
          return;

       Events eventList = service.events().list(getCalID(calName)).execute();
       if (eventList == null || eventList.getItems() == null || eventList.getItems().isEmpty())
          return;

       BatchRequest batch = service.batch();
       for (Event e : eventList.getItems())
       {
          System.out.println("Deleting Event " + e.getId());
          service.events().delete(getCalID(calName), e.getId()).queue(batch, new VoidCallBack());
       }
       batch.execute();
    }

    public void deleteEventList(String calName,
                                List<Event> eventsToDelete) throws Exception
    {
       if (calName == null || eventsToDelete.isEmpty())
          return;
       BatchRequest batch = service.batch();
       for (Event e : eventsToDelete)
       {
          System.out.println("Deleting Event " + e.getId());
          service.events().delete(getCalID(calName), e.getId()).queue(batch, new VoidCallBack());
       }
       batch.execute();
    }

    public void addEvent(Event event,
                         String calName)
    {
       try
       {
          event = service.events().insert(getCalID(calName), event).execute();
          System.out.printf("Event created: %s\n", event.getHtmlLink());
       }
       catch (Exception e)
       {
          log.error("Failed to submit event:", e);
       }
    }

    public Map<String, String> getCalendarNameToID()
    {
       return calendarNameToID;
    }

    ////////////////
    // Private
    ////////////////

    private void connect()
    {
       try
       {
          final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

//          service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getUserOauthCredentials(HTTP_TRANSPORT))
          service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getServiceAccountCredentials(HTTP_TRANSPORT))
             .setApplicationName(APPLICATION_NAME).build();
       }
       catch (Exception e)
       {
          e.printStackTrace();
       }
    }

    private String getCalID(String calName)
    {
       return calendarNameToID.get(calName);
    }

    private void buildCalendarNameMap()
    {
       try
       {
          CalendarList list = listCalendars();

          for (CalendarListEntry entry : list.getItems())
          {
             TechProfile profile = TechProfileConfiguration.getInstance().getDenTechToProfiles().get(entry.getSummary());
             if (profile != null)
                calendarNameToID.put(profile.getName(), entry.getId());
//             if (entry.isPrimary())
//                calendarNameToID.put(CAL_NAME_PRIMARY, entry.getId());
          }
          log.info("Mapped the following calendars to IDs: " + calendarNameToID);
       }
       catch (Exception e)
       {
          log.error("Failed to initialize the calendar list ", e);
       }
    }

    private static Credential getServiceAccountCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException
    {
       GoogleCredential cr = GoogleCredential
          .fromStream(CalendarManager.class.getClassLoader().getResourceAsStream(CLIENT_SECRET_FILE))
          .createScoped(SCOPES);
       GoogleCredential.Builder builder = new GoogleCredential.Builder()
          .setTransport(HTTP_TRANSPORT)
          .setJsonFactory(JSON_FACTORY)
          .setServiceAccountScopes(SCOPES)
          .setServiceAccountId(cr.getServiceAccountId())
          .setServiceAccountPrivateKey(cr.getServiceAccountPrivateKey())
          .setServiceAccountPrivateKeyId(cr.getServiceAccountPrivateKeyId())
          .setTokenServerEncodedUrl(cr.getTokenServerEncodedUrl())
          .setServiceAccountUser("scheduler@com.sentryfire.com");
//          .setClientSecrets("749725681897-jlg6po2hvl71e8lvtchno3h0r6qn0nvj.apps.googleusercontent.com", "y23iPYVUd2VDSxysVnLSxO4q");

       return builder.build();

    }

    public static GoogleCredential createCredentialForServiceAccount(
       HttpTransport transport,
       JsonFactory jsonFactory,
       String serviceAccountId,
       Collection<String> serviceAccountScopes,
       File p12File) throws GeneralSecurityException, IOException
    {
       return new GoogleCredential.Builder().setTransport(transport)
          .setJsonFactory(jsonFactory)
          .setServiceAccountId(serviceAccountId)
          .setServiceAccountScopes(serviceAccountScopes)
          .setServiceAccountPrivateKeyFromP12File(p12File)
          .build();
    }

    private static Credential getUserOauthCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException
    {
       // Load client secrets.
       InputStream in = CalendarManager.class.getClassLoader().getResourceAsStream(CLIENT_SECRET_FILE);
       GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

       // Build flow and trigger user authorization request.
       GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
          HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
          .setDataStoreFactory(new FileDataStoreFactory(new File(CREDENTIALS_FOLDER)))
//          .setAccessType("offline")
          .build();
       return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

 }
