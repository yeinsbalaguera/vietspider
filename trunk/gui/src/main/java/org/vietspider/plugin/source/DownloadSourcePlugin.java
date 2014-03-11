package org.vietspider.plugin.source;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.ClientPlugin;
import org.vietspider.gui.browser.AppBrowser;
import org.vietspider.gui.source.RemoteSourceDownloader;
import org.vietspider.ui.services.ClientRM;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 13, 2008  
 */
public class DownloadSourcePlugin extends ClientPlugin {
  
  protected String label;

  public DownloadSourcePlugin() {
    ClientRM resources = new ClientRM("Plugin");
    label = resources.getLabel(getClass().getName()+".label");
  }
 
  
  public String getConfirmMessage() { return null; }

  public String getLabel() { return label; }

  @Override
  public boolean isValidType(int type) {
    return type == CONTENT || type == DOMAIN || type == APPLICATION;
  }

  public void invoke(Object... objects) {
    Shell shell = null;
    if(objects[1] instanceof Browser) {
      Browser browser = (Browser) objects[1];
      shell = browser.getShell();
    } else  if(objects[1] instanceof AppBrowser) {
      AppBrowser browser = (AppBrowser) objects[1];
      shell = browser.getShell();
    }
    if(shell == null) return;
    
    new RemoteSourceDownloader(shell, null); 
   
    /*Shell popup = new Shell(shell, SWT.CLOSE);
    popup.setImage(shell.getImage());
    popup.setLocation(shell.getLocation().x+430, shell.getLocation().y+100); 
    popup.setLayout(new FillLayout());
    
    CTabFolder tabFolder = new CTabFolder(popup, SWT.BOLD);
    tabFolder.setFont(UIDATA.FONT_10B);
    RemoteSourcesViewer2 viewer = new RemoteSourcesViewer2(tabFolder);
    CTabItem item =  new CTabItem(tabFolder,SWT.NONE);
    item.setText("Explorer");
    item.setControl(viewer);
    tabFolder.setSelection(item);
    
    RemoteSourceSearcher2 searcher = new RemoteSourceSearcher2(tabFolder, null);
    item = new CTabItem(tabFolder,SWT.NONE);
    item.setText("Search");
    item.setControl(searcher);
    
    if(Application.GROUPS.length < 1) {
      viewer.setLocalGroup("ARTICLE");
      searcher.setLocalGroup("ARTICLE");
    } else {
      viewer.setLocalGroup(Application.GROUPS[0]);
      searcher.setLocalGroup(Application.GROUPS[0]);
    }
    
    XPWindowTheme.setTabBrowserTheme(tabFolder);
    popup.setSize(400,420);
    XPWindowTheme.setWin32Theme(popup);
    popup.open();*/
    
//    try {
//      ClassLoader loader = new VsClassLoader().create();      
//      Class<?> clazz = loader.loadClass("org.vietspider.gui.source.RemoteSourcesViewer");
//      Constructor<?> constructor = clazz.getDeclaredConstructor(Shell.class);
//      Object obj = constructor.newInstance(browser.getShell());
//      Method method = clazz.getDeclaredMethod("setLocalGroup", String.class);
//      if(Application.GROUPS.length < 1) {
//        method.invoke(obj, "ARTICLE");
//      } else {
//        method.invoke(obj, Application.GROUPS[0]);
//      }
//    } catch (Exception e) {
//      ClientLog.getInstance().setException(null, e);
//    }
  }
}
