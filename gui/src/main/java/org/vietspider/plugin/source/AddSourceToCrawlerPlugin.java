package org.vietspider.plugin.source;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.ClientPlugin;
import org.vietspider.gui.crawler.CrawlSourceFilter;
import org.vietspider.gui.crawler.DownloadListClient;
import org.vietspider.gui.source.SourcesViewer;
import org.vietspider.ui.services.ClientRM;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 13, 2008  
 */
public class AddSourceToCrawlerPlugin extends ClientPlugin {

  protected String label;

  public AddSourceToCrawlerPlugin() {
    ClientRM resources = new ClientRM("Plugin");
    label = resources.getLabel(getClass().getName()+".label");
  }

  public String getConfirmMessage() { return null; }

  public String getLabel() { return label; }

  @Override
  public boolean isValidType(int type) {
    return type == CONTENT || type == DOMAIN;
  }

  public void invoke(Object... objects) {
    Browser browser = (Browser) objects[1];
    DownloadListClient downloadList = new DownloadListClient();
    Shell shell = new Shell(browser.getShell(),SWT.TITLE);
    shell.setLocation(browser.getShell().getLocation().x+430, browser.getShell().getLocation().y+100); 
    shell.setLayout(new FillLayout());
    
    new SourcesViewer(shell, new CrawlSourceFilter(downloadList));
    
    shell.setSize(400,420);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.setVisible(true);

//    try {
//      Class<?> clazz = loader.loadClass("org.vietspider.gui.source.SourcesViewer");
//      Constructor<?> constructor = clazz.getDeclaredConstructor(Shell.class);
//      constructor.newInstance(browser.getShell());
//    } catch (Exception e) {
//      ClientLog.getInstance().setException(null, e);
//    }
  }
}
