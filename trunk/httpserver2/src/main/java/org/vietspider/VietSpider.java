/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.SystemProperties;
import org.vietspider.httpserver.ApplicationControlServer;
import org.vietspider.httpserver.ContentViewerServer;
import org.vietspider.httpserver.HttpServer;
import org.vietspider.httpserver.SearchApplicationServer;
import org.vietspider.httpserver.VTemplateViewerServer;
import org.vietspider.io.LogDataImpl;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2007  
 */
public class VietSpider {

  static {
    LogService.createInstance(LogDataImpl.class); 
//    LogWebsite.createInstance(LogWebsiteImpl.class); 
//    LogSource.createInstance(LogSourceImpl.class); 

  }

  private static VietSpider INSTANCE;


  public static VietSpider currentInstance() {
    if(INSTANCE == null) INSTANCE = new VietSpider();
    return INSTANCE;
  }

  public static VietSpider currentInstance(String folderData) {
    UtilFile.FOLDER_DATA = folderData;
    return currentInstance();
  }

  //  private Object service;

  private VietSpider() {
    if(LogService.getInstance() == null) {
      LogService.createInstance(LogDataImpl.class);
    }

    //check test mode
    //    if(Application.TEST_MODE) {
    //      UtilFile.deleteFolder(UtilFile.getFolder("content"), true);
    //      UtilFile.deleteFolder(UtilFile.getFolder("track"), true);
    //      
    //      File file  = UtilFile.getFile("system", "load.temp");
    //      file.delete();
    //      
    //      file  = UtilFile.getFile("system", "last-update");
    //      file.delete();
    //    }
    
    SystemProperties system = SystemProperties.getInstance();

    boolean showTrayIcon = true;
    try {
      showTrayIcon = Boolean.valueOf(System.getProperty("vietspider.server.icon").trim());
    } catch (Exception e) {
      showTrayIcon = true;
    }
    if(showTrayIcon) createTrayIcon();

    ApplicationControlServer appServer = null;

    //start server
    try {
      appServer = new ApplicationControlServer();
      
      String appType = system.getValue("search.system");
      if("true".equals(appType)) {
        SystemProperties.getInstance().remove("search.system");
        SystemProperties.getInstance().putValue("application.type", "search", true);
      }
      
      appType = SystemProperties.getInstance().getValue("application.type");
      
      if("search".equalsIgnoreCase(appType)) {
        new SearchApplicationServer();
      } else if("vtemplate".equalsIgnoreCase(appType)) {
        new VTemplateViewerServer();
      } else {
        new ContentViewerServer();
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    if(appServer == null) System.exit(0);

    Runtime runtime = Runtime.getRuntime();
    java.util.Date date = Calendar.getInstance().getTime();
    StringBuilder builder  = new StringBuilder("[");
    builder.append("VietSpider ");
    builder.append(" started at ").append(date.toString());
    builder.append("] [").append(appServer.getHost()).append(':').append(appServer.getPort());
    builder.append("] [OS ").append(System.getProperty("os.name"));
    builder.append("] [JRE ").append(System.getProperty("java.home"));
    builder.append("] [Memory ").append(runtime.totalMemory()/1000000);
    builder.append("mb, max memory ").append(runtime.maxMemory()/1000000).append("mb]...");
    LogService.getInstance().setMessage(null, builder.toString());

    if(!"true".equals(System.getProperty("vietspider.test"))) System.out.println(builder);

    Application.addShutdown(new Application.IShutdown(){

      public int getPriority() { return 3; }

      public void execute() {
        Date date_ = Calendar.getInstance().getTime();
        StringBuilder builder_  = new StringBuilder();
        builder_.append(SystemProperties.getInstance().getValue(Application.APPLICATION_NAME));
        builder_.append(" stop at ").append(date_.toString());

        System.out.println(builder_.toString());
        LogService.getInstance().setMessage(null, builder_.toString());
      }
    });

    new VietSpiderInitService();
    
//    new InitialAutoIndexing().start();
    
    HttpServer.listen();
  }

  @SuppressWarnings("unused")
  public void setOSService(Object obj) {
    //    this.service = obj;
  }

  private void createTrayIcon() {
    try {
      if(!SystemTray.isSupported())  return ;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return;
    }

    SystemTray tray = SystemTray.getSystemTray();
    URL url = VietSpider.class.getResource("VietSpider2.png");
    Image image = Toolkit.getDefaultToolkit().getImage(url);

    PopupMenu popup = new PopupMenu();
    MenuItem exitItem = new MenuItem("Exit");
    exitItem.addActionListener(new ActionListener() {
      @SuppressWarnings("unused")
      public void actionPerformed(ActionEvent e) {
        Runtime.getRuntime().exit(1);
      }
    });
    popup.add(exitItem);

    TrayIcon trayIcon = new TrayIcon(image, "VietSpider", popup);
    trayIcon.setImageAutoSize(true);
    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      LogService.getInstance().setMessage(e, "TrayIcon could not be added.");
    }
  }

  public static void main(String[] args) {
    System.out.println("Start direct VietSpider...");
    currentInstance();
  }
}
