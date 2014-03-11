/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.widget.vtab;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Control;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 6, 2006
 */
public interface IVTabGroup {
  
  public java.util.List<?> getItemList();
  
  public boolean isSelected();
  
  public void setSelected(boolean aSelected);
  
  public ScrolledComposite getComposite();
  
  public void addItem(IVTabItem aItem);  
  
  public String getName();
  
  public Control getPeer();  
  
  public void setVisible(boolean value);

}
