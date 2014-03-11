/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.users;

import org.eclipse.swt.widgets.Composite;
import org.vietspider.gui.workspace.Workspace;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 11, 2008  
 */
public class Organization extends UIOrganization {
  
  public Organization(Composite parent, Workspace workspace) {
    super(parent, workspace);
  }
  
  public String getNameIcon() { return "small.userfolder.png"; }
  
  
}
