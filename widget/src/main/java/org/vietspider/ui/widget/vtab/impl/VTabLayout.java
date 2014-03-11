package org.vietspider.ui.widget.vtab.impl;

import java.util.List;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.vietspider.ui.widget.vtab.IVTab;
import org.vietspider.ui.widget.vtab.IVTabGroup;

public class VTabLayout extends Layout {
  
  private IVTab bar;
  private int titleHeight = 33;
  
  public VTabLayout(IVTab b){
    bar = b;
  }
  
  protected Point computeSize(Composite aComposite, int wHint, int hHint, boolean flushCache){
    return bar.getSize();
  }
  
  protected void layout(Composite aComposite, boolean flushCache)   {
    if(!bar.isVisible())return;
    List<IVTabGroup> groupList = bar.getGroupsList();
    Point barSize = bar.getSize();
    int top = 0;
    int left = 0;
    
    int bottom = groupList.size() > 1 ? 95 : 50;
    
    for(int i = 0; i < groupList.size(); i++){
      IVTabGroup group = groupList.get(i);          
      ScrolledComposite groupContainer = group.getComposite();
      Control control = group.getPeer();
      if(group.isSelected()){
        control.setBounds(left, top, barSize.x, titleHeight);
        top += titleHeight - 1;
        int controlBottom = layoutBottomUp(groupList, i, left, barSize.x, barSize.y);
        
        groupContainer.setVisible(true);
        groupContainer.setBounds(left, top, barSize.x, controlBottom - top);
        groupContainer.getContent().setBounds(left, 0, barSize.x - 4, group.getItemList().size()*(barSize.y- bottom));//95
        return;
      }
      control.setBounds(0, top, barSize.x, titleHeight);
      top += titleHeight;
      groupContainer.setVisible(false);
    }
    
  }
  
  protected int layoutBottomUp(List<IVTabGroup> aList, int aPosition, int aLeft, int aWidth, int aHeight){
    int top = aHeight - titleHeight + 5;
    int width = aWidth;
    for(int i = aList.size() - 1; i > aPosition; i--){
      IVTabGroup group = aList.get(i);
      top -= titleHeight;
      Control control = group.getPeer();
      control.setBounds(aLeft, top, width, titleHeight);
      ScrolledComposite groupContainer = group.getComposite();
      groupContainer.setVisible(false);
    }    
    return top;
  }
  
}
