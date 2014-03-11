/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.users;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.vietspider.gui.browser.ControlComponent;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.widget.ApplicationFactory;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 2, 2006
 */
abstract class UIOrganization extends ControlComponent {
  
//  protected StatusBar statusBar;
  private Users users;
  
  private Groups groups;

  public UIOrganization(Composite parent, Workspace workspace) {
    super(parent, workspace);
    
    setLayout(new GridLayout(2, false));
    ApplicationFactory factory = new ApplicationFactory(this, "Organization", getClass().getName());
    
    groups = new Groups(factory, this);
    GridData gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.widthHint = 250;
    groups.setLayoutData(gridData);
    
    users = new Users(this, groups, factory);
    gridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
    users.setLayoutData(gridData);
    
//    statusBar = new StatusBar(workspace, this);
//    gridData = new GridData(GridData.FILL_HORIZONTAL);
//    gridData.horizontalSpan = 2;
//    statusBar.setLayoutData(gridData);  
//    statusBar.setComponent(this);
  }
  
}
