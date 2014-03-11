/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 28, 2006
 */
public class UIMenu {
  
  private final TabBrowser tab;
//  private MenuItem  itemDelete;
//  private String metaId;
  
  public UIMenu(ApplicationFactory factory, TabBrowser tab_) {
    this.tab = tab_;
    
    Object menu = null;
//    if(XPWidgetTheme.isPlatform()) {  
//      PopupMenu popupMenu = new PopupMenu(tab_, XPWidgetTheme.THEME);
//      menu = new CMenu();
//      popupMenu.setMenu((CMenu)menu);
//      tab.setTabMenu(popupMenu);    
//    } else {
      menu = new Menu(factory.getComposite().getShell(), SWT.POP_UP);
      tab.setTabMenu(menu);    
//    }
    
    factory.createStyleMenuItem(menu, "menuNew", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        try {
          tab.createTool(AppBrowser.class, true, SWT.NONE);
        } catch (Exception exp) {
          ClientLog.getInstance().setException(tab.getShell(), exp);
        } 
      }      
    });
    
    factory.createStyleMenuItem(menu, "menuCloseAll", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        tab.closeAll();
      }
    });
    
    /*new MenuItem(menu, SWT.SEPARATOR);
    
    factory.createMenuItem(menu, "menuHelp", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        try {
          DataReader reader =  RWData.getInstance();
          String url = UINews.class.getResource("").toString()+"help.htm";
          byte [] bytes = reader.loadInputStream(new URL(url).openStream()).toByteArray();
          String text  = new String(bytes, Application.CHARSET);
          tab.createItem().setText(text);
        } catch (Exception exp) {
          ClientLog.getInstance().setException(tab.getShell(), exp);
        }
      }      
    });
    
    factory.createMenuItem(menu, "menuAbout", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        DataReader reader =  RWData.getInstance();
        try {
          String url = UINews.class.getResource("").toString()+"about.htm";
          byte [] bytes = reader.loadInputStream(new URL(url).openStream()).toByteArray();
          String text  = new String(bytes, Application.CHARSET);
          tab.createItem().setText(text);
        } catch (Exception exp) {
          ClientLog.getInstance().setException(tab, exp);
        }
      }      
    });*/
    
  }  
  
  
 
}
