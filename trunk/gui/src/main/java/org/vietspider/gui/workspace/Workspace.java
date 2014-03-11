/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.workspace;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.vietspider.gui.browser.TabBrowser;
import org.vietspider.gui.browser.UIMenu;
import org.vietspider.gui.crawler.Crawler;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.images.ToolbarResource;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 26, 2006
 */
public class Workspace extends Composite {

  public TabBrowser tab;
  
  private Crawler uiCrawler;
  
  public Workspace(ApplicationFactory factory, Composite parent, int style) {
    super(parent, style); 

    factory.setComposite(this);
    factory.setClassName(getClass().getName());

    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 2;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 2;
    setLayout(gridLayout); 
    
    ToolbarResource.createInstance(getDisplay(), "VietSpider", Workspace.class);
    
    tab = new TabBrowser(this);  
    GridData gridData = new GridData(GridData.FILL_BOTH);
    tab.setLayoutData(gridData);
    new UIMenu(factory, tab);
  } 
  
//  public void setMaxWindow(Control control) {
//    liveSashForm.setMaximizedControl(control);
//  }
  
  public TabBrowser getTab(){ return tab; }
  
  public void setCrawler(Crawler uiCrawler) { this.uiCrawler = uiCrawler; }
  public Crawler getUICrawler(){ return uiCrawler; }

//  public StatusBar getStatusBar() { return statusBar; }
  
}
