/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.source;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.common.Application;
import org.vietspider.gui.workspace.XPWindowTheme;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 6, 2009  
 */
public class RemoteSourceDownloader {
  
  private RemoteSourcesViewer2 explorer;
  private RemoteSourceSearcher2 searcher;
  
  public RemoteSourceDownloader(Shell shell, SourcesHandler sourcesHandler) {
    Shell popup = new Shell(shell, SWT.CLOSE);
    popup.setImage(shell.getImage());
    popup.setLocation(shell.getLocation().x+430, shell.getLocation().y+100); 
    popup.setLayout(new FillLayout());
    
    CTabFolder tabFolder = new CTabFolder(popup, SWT.BOLD);
    tabFolder.setCursor(new Cursor(tabFolder.getDisplay(), SWT.CURSOR_HAND));
    tabFolder.setFont(UIDATA.FONT_10B);
    
    explorer = new RemoteSourcesViewer2(tabFolder, sourcesHandler);
    CTabItem item =  new CTabItem(tabFolder,SWT.NONE);
    item.setText("Explorer");
    item.setControl(explorer);
    tabFolder.setSelection(item);
    
    searcher = new RemoteSourceSearcher2(tabFolder, sourcesHandler);
    item = new CTabItem(tabFolder,SWT.NONE);
    item.setText("Search");
    item.setControl(searcher);
    
    if(Application.GROUPS.length < 1) {
      explorer.setLocalGroup("ARTICLE");
      searcher.setLocalGroup("ARTICLE");
    } else {
      explorer.setLocalGroup(Application.GROUPS[0]);
      searcher.setLocalGroup(Application.GROUPS[0]);
    }
    
    XPWindowTheme.setTabBrowserTheme(tabFolder);
    popup.setSize(400,420);
//    XPWindowTheme.setWin32Theme(popup);
    popup.open();
  }
  
  public void setLocalGroup(String group) {
    explorer.setLocalGroup(group);
    searcher.setLocalGroup(group);
  }
  
  public void setLocalCategory(String cate) {
    explorer.setLocalCategory(cate);
    searcher.setLocalCategory(cate);
  }
}
