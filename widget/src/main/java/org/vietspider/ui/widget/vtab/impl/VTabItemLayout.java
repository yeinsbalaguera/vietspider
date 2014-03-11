package org.vietspider.ui.widget.vtab.impl;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.vietspider.ui.widget.vtab.IVTabItem;

public class VTabItemLayout extends Layout  {

  private VTabGroup group;

  public VTabItemLayout(VTabGroup aGroup){
    group = aGroup;
  }

  public void layout(){
    layout(null, true);
  }

  @SuppressWarnings("unused")
  protected Point computeSize(Composite aComposite, int wHint, int hHint, boolean flushCache) {
    return group.getComposite().getSize();
  }

  @SuppressWarnings("unused")
  protected void layout(Composite aComposite, boolean flushCache) { 
    Composite composite = (Composite)(group.getComposite().getContent());
    List<IVTabItem> children = group.getItemList();
    Point barSize = composite.getSize();
    int top = 2;
    int left = 5;    
    for(int i = 0; i < children.size(); i++){
      Control control = children.get(i).getControl();
      int h =  control.getSize().y;
      if(i == (children.size() - 1)) h = barSize.y/children.size() - top;   
      control.setBounds(left, top, barSize.x-10, h);
      top += h+2;
    }
  }  
  
}