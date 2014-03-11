/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.workspace;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.ClientProperties;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.UtilFile;
import org.vietspider.gui.config.RemoteClient;
import org.vietspider.ui.XPWidgetTheme;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ImageLoader;
import org.vietspider.ui.widget.UIDATA;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 7, 2006
 */
public class VietSpiderClient {

  private static VSWindow window; 

  private static Shell shell = null;

  private static RemoteClient remoteClient;
    private static VietSpiderTrayIcon vsTrayIcon; 

  public VietSpiderClient() {
    ClientProperties clientProperties = ClientProperties.getInstance();
    Application.CLIENT_PROPERTIES = clientProperties.getProperties();
//    String skin = clientProperties.getProperties().getProperty("skin");
//    if("false".equalsIgnoreCase(skin)) {
    XPWidgetTheme.SKIN = false;
    XPWindowTheme.SKIN = false;
//    }
    
//    if(XPWindowTheme.isPlatform()) Extension.isInitial();
    shell = new Shell(UIDATA.DISPLAY, SWT.CLOSE | SWT.APPLICATION_MODAL);
    shell.setText("Login");
    ImageLoader imageLoader = new ImageLoader();
    Image image = null;
    if(XPWidgetTheme.isPlatform()) {
      image = imageLoader.load(UIDATA.DISPLAY, "VietSpider.png");
    } else {
      image = imageLoader.load(UIDATA.DISPLAY, "VietSpider2.ico");
    }

    shell.setLayout(new FillLayout());
    shell.setImage(image);
    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        e.doit = false;
      }
    });
    
    connect(false);

    searchXULRunner();

    showWindow();

    while(!shell.isDisposed()) {
      Display display = shell.getDisplay();
      try {
        if(!display.readAndDispatch()) display.sleep();
      } catch (Throwable e) {
        ClientLog.getInstance().setThrowable(null, e);
        ClientLog.getInstance().setException(shell, new Exception(e.toString()));
      }
      if(window == null) continue;
    }

    if(vsTrayIcon != null) vsTrayIcon.dispose();

    if("true".equals(System.getProperty("vietspider.shutdown.server"))) {
      try {
        DataClientHandler.getInstance().exitApplication();
      }catch (Exception e) {
        ClientLog.getInstance().setException(null, e);
      }
    }
    File folder = UtilFile.getFolder("client");
    File [] files = folder.listFiles(new FileFilter(){
      public boolean accept(File f) {
        return f.getName().startsWith("http___");
      }
    });
    if(files != null) {
      for(File file : files) {
        try {
          UtilFile.deleteFolder(file);
        } catch (Exception e) {
        }
      }
    }
    Runtime.getRuntime().exit(0);
  }

  public static void connect(boolean logout) {
    if(remoteClient == null) {
      remoteClient = new RemoteClient(shell);
      Rectangle displayRect = UIDATA.DISPLAY.getBounds();
      int x = (displayRect.width - 350) / 2;
      int y = (displayRect.height - 200)/ 2;
      shell.setLocation(x, y);
      shell.pack();
//      XPWindowTheme.setWin32Theme(shell);
      shell.open();
    } else {
      remoteClient.show();
      remoteClient.setFocus();
      if(logout) remoteClient.logout();
    }

    while (!shell.isDisposed()) {
      if(remoteClient.isConnected()) break;
      if(!remoteClient.getShell().isVisible()) return;
      Display display = shell.getDisplay();
      if(display.isDisposed()) break;
      try {
        if(!display.readAndDispatch()) display.sleep();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if(!shell.isDisposed()) shell.setVisible(false);
    if(window == null) return ;

    if(!window.getShell().isDisposed()) {
      window.closeShell();  
      window.getShell().dispose();
      window = null;
    }

    showWindow();
  }

  public static void showWindow(){
    if(window != null && !window.getShell().isDisposed()) {
      window.getShell().setMinimized(false);
      window.getShell().setVisible(true);
      return;
    }
  

    UtilFile.deleteFolder(ClientConnector2.getCacheFolder("sources/type"), false);
    StringBuilder title = new StringBuilder();
    try {
      URL url = new URL(ClientConnector2.currentInstance().getRemoteURL());
      title.append(url.getHost()).append(':').append(url.getPort()).append(" - ");
    } catch (Exception e) {
      title.append(ClientConnector2.currentInstance().getRemoteURL()).append(" - ");
    }
    window = new VSWindow(title.toString());
    if(remoteClient != null) remoteClient.setMainWindow(window.getShell());
    window.getWorkspace().getTab().closeAll();
//    window.getShell().addShellListener(new ShellAdapter() {
//      @SuppressWarnings("unused")
//      public void shellIconified(ShellEvent e) {
//        window.getShell().setVisible(false);
//      }
//    });
    
    if(vsTrayIcon != null) vsTrayIcon.dispose();
    vsTrayIcon = new VietSpiderTrayIcon(window.getWorkspace());
  }

  public final static void dispose() { shell.dispose(); }

  static void searchXULRunner() {
    try {
      ClientProperties clientProperties = ClientProperties.getInstance();
      String xulPath = clientProperties.getValue("xulRunnerPath");
//      System.out.println(xulPath);
      if(xulPath != null && xulPath.startsWith("file://")) {
        URL url = new URL(xulPath);
        xulPath = new File(url.toURI()).getAbsolutePath();
      }
      if(xulPath != null && (xulPath = xulPath.trim()).length() > 0) {  
        File file  = new File(xulPath);
//        System.out.println(file.getAbsolutePath()  + " : " + file.exists());
        if(file.exists()) {
//          File xpFile = new File(file, "javaxpcom.jar");
//          if(xpFile.exists()) {
            System.setProperty("org.eclipse.swt.browser.XULRunnerPath", xulPath);
            return;
//          }
        }
      } 

      String os_name = System.getProperty("os.name").toLowerCase();
      if(os_name.indexOf("windows") > -1 
          || os_name.indexOf("win") > -1) return;

      File folder = new File(UtilFile.FOLDER_DATA);
      File xulFile  = searchXULRunner(folder.getParentFile());
     // System.out.println("hehe " + xulFile);
      if(xulFile != null) {
        xulPath = xulFile.getAbsolutePath();
        System.setProperty("org.eclipse.swt.browser.XULRunnerPath", xulPath);
        clientProperties.putValue("xulRunnerPath", xulPath);
      } 
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }

  }

  static File searchXULRunner(File file) {
    File [] files = file.listFiles();
    for(File f : files) {
      if(f.isFile()) continue;
      if(f.getName().indexOf("xulrunner") > -1) {
        File xpFile = new File(f, "javaxpcom.jar");
        if(xpFile.exists()) return f;
      } 
      File value = searchXULRunner(f);
      if(value != null) return value;
    }
    return null;
  }

  public static void main(String[] args) {    
    try {
      new VietSpiderClient();
    }catch(Exception exp){
      ClientLog.getInstance().setException(null, exp);
    }
    System.exit(1);  
  }

}
