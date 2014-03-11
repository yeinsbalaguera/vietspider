/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.log;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.vietspider.gui.browser.ControlComponent;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 15, 2010  
 */
public class LogViewer2 extends ControlComponent {

  public LogViewer2(Composite parent, Workspace workspace) {
    super(parent, workspace);

    String clazzName  = "org.vietspider.gui.monitor.Monitor";
    ApplicationFactory factory = new ApplicationFactory(this, "Monitor", clazzName);

    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);

    SashForm sashMain = new SashForm(this, SWT.HORIZONTAL);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    sashMain.setLayoutData(gridData);
    sashMain.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

    Composite leftComposite = new Composite(sashMain, SWT.NONE);
    gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 5;
    gridLayout.marginWidth = 0;
    leftComposite.setLayout(gridLayout);


    TabFolder tab = new TabFolder(leftComposite, SWT.TOP);
    tab.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));

    tab.setBackground(new Color(getDisplay(), 255, 255, 255));
    tab.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_BOTH);     
    tab.setLayoutData(gridData);
    
    SimpleLogTextViewer appLog = new SimpleLogTextViewer(tab, workspace, "track/logs/");
    TabItem item = new TabItem(tab, SWT.NONE);
    item.setText(factory.getLabel("tab.common"));
    item.setControl(appLog);
    tab.setSelection(item);
    
//    LogTextViewer netLog = new LogTextViewer(tab, workspace, "track/logs/website/");
//    item = new TabItem(tab, SWT.NONE);
//    item.setText(factory.getLabel("tab.website"));
//    item.setControl(netLog);
//    tab.setSelection(item);
    
    LogDatabaseViewer dbLog = new LogDatabaseViewer(tab, workspace, "track/logs/sources/");
    item = new TabItem(tab, SWT.NONE);
    item.setText(factory.getLabel("tab.source"));
    item.setControl(dbLog);
    tab.setSelection(item);
    
    tab.setSelection(0);
  }
  
  @Override
  public String getNameIcon() { return "small.log.png"; }

}
