package com.sentryfire.gui;

public class GuiThread implements Runnable
{

   private EventHandler eh;
   private static MainApp window;
   private static DatePanel datePanel;

   public GuiThread(EventHandler eh)
   {
      window = new MainApp();
      datePanel = new DatePanel();
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
         datePanel.initialize();
         datePanel.setEventHandler(eh);
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

   public DatePanel getDatePanel()
   {
      return datePanel;
   }
}
