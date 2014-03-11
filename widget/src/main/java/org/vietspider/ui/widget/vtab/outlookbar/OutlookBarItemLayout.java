/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.widget.vtab.outlookbar;
/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 18, 2000
 */
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.vietspider.ui.widget.ImageHyperlink;

public class OutlookBarItemLayout extends Layout  {
  
  private OutlookBarPane pane;
  
  private int sizeIcon = 60;
  
  public OutlookBarItemLayout(OutlookBarPane p){    
    pane = p;
  }
  
  protected Point computeSize(Composite aComposite, int wHint, int hHint, boolean flushCache) {
     return pane.getControl().getSize();
  }
  
  protected void layout(Composite aComposite, boolean flushCache) {
    List<OutlookBarItem> itemList = pane.getItemList();
    if(pane == null) return;
    Point size = pane.getControl().getSize();
    int top = 4;
    int width = size.x - 4;
    int left = (width - sizeIcon) / 2;
    
    for(int i = 0; i < itemList.size(); i++) {
      OutlookBarItem item = itemList.get(i);
      ImageHyperlink image = item.getImage();
      Rectangle rect  = image.getImage().getBounds();
      image.setBounds(left, top, rect.width,  rect.height);
      top +=  rect.height + 4;
      Label itemLabel = item.getLabel();
      itemLabel.setBounds(5, top, width-20, 16);
      top = top + 40;      
    }    
  }  
}