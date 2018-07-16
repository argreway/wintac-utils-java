 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
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
 import java.util.Collections;
 import java.util.List;

 import com.google.api.client.auth.oauth2.Credential;
 import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
 import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
 import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
 import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
 import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
 import com.google.api.client.googleapis.batch.BatchRequest;
 import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
 import com.google.api.client.http.javanet.NetHttpTransport;
 import com.google.api.client.json.JsonFactory;
 import com.google.api.client.json.jackson2.JacksonFactory;
 import com.google.api.client.util.store.FileDataStoreFactory;
 import com.google.api.services.calendar.Calendar;
 import com.google.api.services.calendar.CalendarScopes;
 import com.google.api.services.calendar.model.Event;
 import com.google.api.services.calendar.model.Events;
 import com.sentryfire.business.schedule.SchedulerBuilder;
 import com.sentryfire.model.WO;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class CalendarManager
 {
    Logger log = LoggerFactory.getLogger(getClass());

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FOLDER = "/tmp/credentials"; // Directory to store user credentials.

    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CLIENT_SECRET_FILE = "client_secret.json";

    private String CAL_NAME = "primary";


    protected Calendar service;
    protected SchedulerBuilder schedulerBuilder = new SchedulerBuilder();

    public CalendarManager()
    {
       log.info("Connecting to google calendar.");
       connect();
    }

    public void bulkUpdateWorkOrders(List<WO> woList)
    {
       try
       {

          List<Event> events = schedulerBuilder.buildSchedule(woList);

          BatchRequest batch = service.batch();
          for (Event e : events)
          {
             try
             {
                service.events().insert(CAL_NAME, e).queue(batch, new EventCallBack());
             }
             catch (Exception ex)
             {
                log.error("Failed to submit event " + e.getId() + ", due to: ", e);
             }
          }

//          batch.execute();
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

    public void deleteEvent(String eventID) throws Exception
    {
       service.events().delete(CAL_NAME, eventID).execute();
    }

    public void deleteAllEvents() throws Exception
    {
       Events eventList = service.events().list(CAL_NAME).execute();

       BatchRequest batch = service.batch();
       for (Event e : eventList.getItems())
       {
          System.out.println("Deleting Event " + e.getId());
          service.events().delete(CAL_NAME, e.getId()).queue(batch, new VoidCallBack());
       }
       batch.execute();
    }

    public void addEvent(Event event)
    {
       try
       {
          event = service.events().insert(CAL_NAME, event).execute();
          System.out.printf("Event created: %s\n", event.getHtmlLink());
       }
       catch (Exception e)
       {
          log.error("Failed to submit event:", e);
       }
    }

    ////////////////
    // Private
    ////////////////

    private void connect()
    {
       try
       {
          final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

          service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getUserOauthCredentials(HTTP_TRANSPORT))
             .setApplicationName(APPLICATION_NAME).build();
       }
       catch (Exception e)
       {
          e.printStackTrace();
       }
    }

    private static Credential getServiceAccountCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException
    {
       //          GoogleCredential cr = GoogleCredential
       //             .fromStream(getClass().getClassLoader().getResourceAsStream(CLIENT_SECRET_FILE))
       //             .createScoped(SCOPES);
       //          GoogleCredential.Builder builder = new GoogleCredential.Builder()
       //             .setTransport(HTTP_TRANSPORT)
       //             .setJsonFactory(JSON_FACTORY)
       //             .setServiceAccountScopes(SCOPES)
       //             .setServiceAccountId(cr.getServiceAccountId())
       //             .setServiceAccountPrivateKey(cr.getServiceAccountPrivateKey())
       //             .setServiceAccountPrivateKeyId(cr.getServiceAccountPrivateKeyId())
       //             .setTokenServerEncodedUrl(cr.getTokenServerEncodedUrl())
       //             .setServiceAccountUser("749725681897-jlg6po2hvl71e8lvtchno3h0r6qn0nvj.apps.googleusercontent.com");

       //          GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream("MyProject-1234.json"))
       GoogleCredential credential = GoogleCredential.fromStream(CalendarManager.class.getClassLoader().getResourceAsStream(CLIENT_SECRET_FILE))
          .createScoped(SCOPES);
       return credential;

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
