package com.sentryfire.gui;

public class GuiThread implements Runnable
{

   private EventHandler eh;
   private static MainApp window;

   public GuiThread(EventHandler eh)
   {
      window = new MainApp();
      this.eh = eh;
   }

   @Override
   public void run()
   {
      try
      {
         eh.setMainApp(window);
         window.setEventHandler(eh);
         window.initialize();
         window.frame.setVisible(true);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public MainApp getWindow()
   {
      return window;
   }
}
