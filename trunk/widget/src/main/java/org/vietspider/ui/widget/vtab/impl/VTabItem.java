package org.vietspider.ui.widget.vtab.impl;

import org.eclipse.swt.widgets.Control;
import org.vietspider.ui.widget.vtab.IVTabGroup;
import org.vietspider.ui.widget.vtab.IVTabItem;

public class VTabItem implements IVTabItem {
  
  private Control control;  
  
  private IVTabGroup group;
  
  private boolean selected  = false;
  
  public Control getControl() { return control; }
  
  public void setControl(Control c){ control = c; }  
  
  public IVTabGroup getGroup() { return group; }
  
  public void setGroup(IVTabGroup aGroup){ group = aGroup; } 
  
  public boolean isSelected(){ return selected; }
  
  public void setSelected(boolean value){ selected = value; }
}
