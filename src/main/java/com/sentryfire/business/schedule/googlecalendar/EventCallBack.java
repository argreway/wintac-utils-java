 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      EventCallBack.java
  * Created:   7/16/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.business.schedule.googlecalendar;

 import java.io.IOException;

 import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
 import com.google.api.client.googleapis.json.GoogleJsonError;
 import com.google.api.client.http.HttpHeaders;
 import com.google.api.services.calendar.model.Event;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class EventCallBack extends JsonBatchCallback<Event>
 {
    Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onFailure(GoogleJsonError e,
                          HttpHeaders responseHeaders) throws IOException
    {

       log.info("failed " + e);
    }

    @Override
    public void onSuccess(Event event,
                          HttpHeaders responseHeaders) throws IOException
    {
       log.info("Added " + event.getId());

    }
 }

