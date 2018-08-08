 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      AppAppender.java
  * Created:   6/29/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire;

 import java.io.IOException;
 import java.io.OutputStream;
 import java.util.List;

 import com.google.common.collect.Lists;
 import com.sentryfire.gui.GUIManager;
 import org.apache.log4j.Layout;
 import org.apache.log4j.WriterAppender;

 public class AppAppender extends WriterAppender
 {
    static List<LoggableApp> registeredApps = Lists.newArrayList();

    public static void registerAppLog(LoggableApp app)
    {
       registeredApps.add(app);
    }

    /**
     * Constructs an unconfigured appender.
     */
    public AppAppender()
    {
    }

    public AppAppender(Layout layout)
    {
       setLayout(layout);
       activateOptions();
    }

    /**
     * Prepares the appender for use.
     */
    public void activateOptions()
    {
       setWriter(createWriter(new AppStream()));
       super.activateOptions();
    }

    /**
     * {@inheritDoc}
     */
    protected
    final void closeWriter()
    {
       super.closeWriter();
    }

    /**
     * An implementation of OutputStream that redirects to the
     * current System.err.
     */
    private static class AppStream extends OutputStream
    {
       public AppStream()
       {
       }

       public void close()
       {
       }

       public void flush()
       {
//          System.err.flush();
       }

       public void write(final byte[] b) throws IOException
       {
          GUIManager.logMessage(new String(b));
       }

       public void write(final byte[] b,
                         final int off,
                         final int len)
          throws IOException
       {
          String full = new String(b);
          String message = full.substring(off, off + len);

          if (GUIManager.isGUI())
          {
             GUIManager.logMessage(message);
          }

          for (LoggableApp app : registeredApps)
          {
             app.writeMessage(message);
          }
       }

       public void write(final int b) throws IOException
       {
          if (GUIManager.isGUI())
          {
             System.err.write(b);
             GUIManager.logMessage("" + b);
          }
          for (LoggableApp app : registeredApps)
          {
             app.writeMessage("" + b);
          }
       }
    }

    public interface LoggableApp
    {
       public void writeMessage(String message);
    }
 }
