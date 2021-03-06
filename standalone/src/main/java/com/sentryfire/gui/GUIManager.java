 /*=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  * Author:    Tony Greway
  * File:      GUIManager.java
  * Created:   6/25/18
  *
  * Description:
  *
  *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

 package com.sentryfire.gui;

 public class GUIManager
 {
    private static EventHandler eventHandler;

    private static boolean isGUI = false;
    private static GuiThread guiThread;

    public static void launchGui()
    {
       eventHandler = new AppEventHandler();
       guiThread = MainApp.startGui(eventHandler);
       isGUI = true;
    }

    public static void logMessage(String message)
    {
       guiThread.getWindow().logMessage(message);
    }

    public static void logError(String message)
    {
       guiThread.getWindow().logMessage("ERROR: " + message);
    }

    public static void showDateWindow(boolean show)
    {
       guiThread.getDatePanel().frame.setVisible(show);
    }

    public static DatePanel getDatePanel()
    {
       return guiThread.getDatePanel();
    }

    public static boolean isGUI()
    {
       return isGUI;
    }
 }
