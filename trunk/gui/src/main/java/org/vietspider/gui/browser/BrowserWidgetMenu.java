/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMMouseEvent;
import org.vietspider.ui.browser.DocumentHandler;
import org.vietspider.ui.browser.PageMenu;
import org.vietspider.ui.widget.ApplicationFactory;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2009  
 */
public class BrowserWidgetMenu extends PageMenu {

  protected TabBrowser tabBrowser;
  
  private CreateMenuHandler createHandler;

  public BrowserWidgetMenu(TabBrowser tabBrowser, Browser browser, nsIDOMEvent event) {
    this.tabBrowser = tabBrowser;
    this.createHandler = new CreateMenuHandler(tabBrowser);
    this.handler = new DocumentHandler(browser, event);
    this.browser = browser;

    String name  = "org.vietspider.ui.browser.PageMenu";
    ApplicationFactory factory = new ApplicationFactory(browser, "BrowserMenu", name);

    menu = new Menu (browser);
    
    creatCreatorItem(factory);
    factory.createMenuItem(menu, SWT.SEPARATOR);
    
    createLinkItem(factory);
    createNewTabItem(factory);
    factory.createMenuItem(menu, SWT.SEPARATOR);

    createBrowserItem(factory);
    factory.createMenuItem(menu, SWT.SEPARATOR);

    createNodeItem(factory);

    factory.createMenuItem(menu, SWT.SEPARATOR);

    createPageItem(factory);

    nsIDOMMouseEvent mouseEvent = (nsIDOMMouseEvent)event.queryInterface (nsIDOMMouseEvent.NS_IDOMMOUSEEVENT_IID);
    menu.setLocation(mouseEvent.getScreenX (), mouseEvent.getScreenY ());
    menu.setVisible(true);
  }

  public void createNewTabItem(ApplicationFactory factory) {
    MenuItem item = factory.createMenuItem(menu, "newTab");
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String [] links = handler.getSelectedLink();
        for(int l = 0; l < links.length; l++) {
          tabBrowser.createItem().setUrl(links[l]);
        }
      }
    });
  }

  public void creatCreatorItem(ApplicationFactory factory) {
    MenuItem item = factory.createMenuItem(menu, "addHomepage");
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        createHandler.putHomepage(handler.getSelectedLink());
      }
    });
    
    item = factory.createMenuItem(menu, "putTemplateVisitLink");
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        createHandler.putTemplatePage(handler.getSelectedLink(), "templateVisitLink");
      }
    });
    
    item = factory.createMenuItem(menu, "putSamplePage");
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        createHandler.putSamplePage(handler.getSelectedLink());
      }
    });
    
    item = factory.createMenuItem(menu, "putTemplateDataLink");
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        createHandler.putTemplatePage(handler.getSelectedLink(), "templateDataLink");
      }
    });
    
    item = factory.createMenuItem(menu, "testSamplePage");
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        createHandler.testSamplePage(handler.getSelectedLink());
      }
    });
  }
  
  

}
