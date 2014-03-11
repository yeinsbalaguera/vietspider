package org.vietspider.ui.widget.vtab;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface IVTab {  
  
  public Point getSize();  
  
  public java.util.List<IVTabGroup> getGroupsList();
  
  public Composite getGroup(String aGroup);
  
  public IVTabItem addItem(String aGroup, Control control);  
  
  public boolean isVisible();
  
}
