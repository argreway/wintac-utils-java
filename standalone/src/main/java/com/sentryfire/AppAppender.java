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

 import com.sentryfire.gui.GUIManager;
 import org.apache.log4j.Layout;
 import org.apache.log4j.WriterAppender;

 public class AppAppender extends WriterAppender
 {
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
          if (GUIManager.isGUI())
          {
             String full = new String(b);
             GUIManager.logMessage(full.substring(off, off + len));
          }
       }

       public void write(final int b) throws IOException
       {
          if (GUIManager.isGUI())
          {
             System.err.write(b);
             GUIManager.logMessage("" + b);
          }
       }
    }
 }
