/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.widget.vtab.outlookbar;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 19, 2000
 */

public class OutlookBarPane extends Composite {    
  
  private java.util.List<OutlookBarItem> itemList; 
    
  private final Composite composite;
  
  private ScrolledComposite scrolledComposite;
    
  public OutlookBarPane(Composite parent){
    super(parent, SWT.NONE);
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 2;
    setLayout(gridLayout);
    
    itemList = new ArrayList<OutlookBarItem>();
    
    scrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL);
    GridData gridData = new GridData(GridData.FILL_BOTH 
        | GridData.HORIZONTAL_ALIGN_CENTER  | GridData.VERTICAL_ALIGN_BEGINNING) ;
    scrolledComposite.setLayoutData(gridData);
    scrolledComposite.setExpandHorizontal(true);
    
    composite = new Composite(scrolledComposite, SWT.NONE);      
    composite.setBackground(new Color(composite.getDisplay(), 255, 255, 255));
    composite.setLayout(new OutlookBarItemLayout(this));
    composite.setSize(parent.getSize().x, 200);    
    scrolledComposite.setContent(composite);      
    scrolledComposite.setBackground(new Color(composite.getDisplay(), 255, 255, 255));
  }  

  public void addItem(OutlookBarItem aItem){
    itemList.add(aItem);
  }  
  
  public void setSize() {
    composite.setSize(getSize().x, itemList.size()*204);
  }
  
  public java.util.List<OutlookBarItem> getItemList() {
    return itemList;
  }
  
  public Composite getControl() { return composite; }

  public ScrolledComposite getScrolledComposite() { return scrolledComposite; }
}
