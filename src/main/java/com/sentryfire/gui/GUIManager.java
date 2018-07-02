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

    private static boolean isConsole = false;
    private static GuiThread guiThread;

    public static void launchGui()
    {
       isConsole = true;
       eventHandler = new AppEventHandler();
       guiThread = MainApp.startGui(eventHandler);
    }

    public static void logMessage(String message)
    {
       guiThread.getWindow().logMessage(message);
    }

    public static void logError(String message)
    {
       guiThread.getWindow().logMessage("ERROR: " + message);
    }

    public static boolean isConsole()
    {
       return isConsole;
    }
 }
