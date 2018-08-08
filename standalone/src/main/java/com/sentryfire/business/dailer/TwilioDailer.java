package com.sentryfire.business.dailer;

import java.net.URI;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Play;
import com.twilio.twiml.voice.Say;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import static spark.Spark.post;


public class TwilioDailer
{
   static Logger log = LoggerFactory.getLogger(TwilioDailer.class);

   public static final String ACCOUNT_SID = "ACc07836be7f940dc51d404cd9f7a9c933";
   public static final String AUTH_TOKEN = "699286571f9d1d859cb0aecf097e4f4c";

   public TwilioDailer()
   {
      Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
      setupServer();
   }

   public static void setupServer()
   {
//      get("/voice.xml", (request, response) -> {
      post("/voice.xml", (request, response) -> {
         return handleArRequest(request, response);
      });


      post("/", (request, response) -> {
         return handleArRequest(request, response);
      });

   }

   private static String handleArRequest(Request request,
                                         Response response)
   {
      response.type("application/xml");

      log.info("Params " + request.body());

      String[] amt = request.queryParamsValues("amt");
      String[] name = request.queryParamsValues("name");
      Say say = new Say.Builder(
         "Hey " + name[0] + ".  This is Sentry Fire And Safety.  You owe us " + amt[0] + " dollars!" +
         "Pay up now or else we will send Lou."
      ).voice(Say.Voice.MAN)
         .language(Say.Language.EN_GB).build();
      Play play = new Play.Builder().url("http://demo.twilio.com/docs/classic.mp3").build();

      VoiceResponse twiml = new VoiceResponse.Builder()
         .say(say)
         .play(play)
         .build();

      return twiml.toXml();
   }

   public void sendCall()
   {
      sendActual();
   }

   public void sendActual()
   {
      try
      {
         String to = "3039219224";
         String from = "3039219224";
//         String from = "+7 187172297";
         Call call = Call.creator(
            new PhoneNumber(to),
            new PhoneNumber(from),
            new URI("http://884250aa.ngrok.io/voice.xml?amt=1234.0&name=Cathy")).create();
      }
      catch (Exception e)
      {
         log.error("Failed to send call.", e);

      }
   }
}


