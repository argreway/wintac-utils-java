 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      VoidCallBack.java
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
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class VoidCallBack extends JsonBatchCallback<Void>
 {
    Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onFailure(GoogleJsonError e,
                          HttpHeaders responseHeaders) throws IOException
    {

       log.error("Delete failed.", e);
    }

    @Override
    public void onSuccess(Void aVoid,
                          HttpHeaders responseHeaders) throws IOException
    {
       log.info("Deleted");
    }
 }
