/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vietspider.gui.workspace.Workspace;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 11, 2009  
 */
public abstract class ControlComponent extends Composite {
  
  protected Workspace workspace;

  public ControlComponent(Composite parent, Workspace workspace) {
    this(parent, workspace, SWT.NONE);
  }

  public ControlComponent(Composite parent, Workspace workspace, int style) {
    super(parent, style);
    this.workspace = workspace;
  }
  
  public Workspace getWorkspace() { return workspace;}
  
  public abstract String getNameIcon();
  
}
