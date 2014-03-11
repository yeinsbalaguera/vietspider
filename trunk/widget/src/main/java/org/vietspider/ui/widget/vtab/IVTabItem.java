/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.widget.vtab;

import org.eclipse.swt.widgets.Control;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 6, 2006
 */
public interface IVTabItem {
  
  public IVTabGroup getGroup() ;
  
  public void setGroup(IVTabGroup aGroup);
  
  public void setControl(Control control);
  
  public Control getControl();
  
  public boolean isSelected();
  
  public void setSelected(boolean aSelected);
  
}
