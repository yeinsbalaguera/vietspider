/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.vietspider.ui.widget.action.ExpandSelectionEvent;
import org.vietspider.ui.widget.action.ExpandSelectionListener;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 16, 2003
 */
public class ExpandMenu extends Composite {

  private ExpandBar bar;

  protected List<ExpandSelectionListener> listeners;

  public ExpandMenu(Composite comp, int style){
    super(comp, style);
    setLayout(new FillLayout());
    bar = new ExpandBar(this, SWT.V_SCROLL);
    bar.setBackground(new org.eclipse.swt.graphics.Color(getDisplay(), 255, 255, 255));
    bar.setSpacing(7);        
    bar.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
    bar.setFont(UIDATA.FONT_8VB);
    listeners = new ArrayList<ExpandSelectionListener>();
  }

  @SuppressWarnings("hiding")
  public Composite createGroup(List<String> data){
    Composite composite = new Composite (bar, SWT.NONE);
    composite.setBackground(UIDATA.BCOLOR);  
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 5;
    gridLayout.horizontalSpacing = 8;
    gridLayout.verticalSpacing = 10;
    gridLayout.marginWidth = 5;
    composite.setLayout(gridLayout);  
    Hyperlink lbl;    
    HyperlinkAdapter listener = new HyperlinkAdapter(){
      public void linkActivated(HyperlinkEvent e){
        String txtElement = e.getLabel().trim();          
        Hyperlink  h = (Hyperlink)e.getSource();
        Composite comp = h.getParent();          
        for(int i = 0; i < bar.getItemCount(); i++){
          if(bar.getItem(i).getControl() != comp) continue;
          String txtGroup = bar.getItem(i).getText().trim();           
          ExpandSelectionEvent exv = new ExpandSelectionEvent(i, h.getIndex(), txtGroup, txtElement);
          select(exv);            
          break;
        }          
      }
    };
    
    int index = 0;
    
    for(String ele : data){
      lbl = new Hyperlink(composite, SWT.LEFT);
      lbl.setIndex(index);
      lbl.addHyperlinkListener(listener);
      lbl.setFont(UIDATA.FONT_8V); 
      lbl.setBackground(UIDATA.BCOLOR);  
      lbl.setForeground(UIDATA.FCOLOR);
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
      lbl.setLayoutData(gridData);
      lbl.setText(ele.trim());
      
      index++;
    }   
    return composite;
  }
  
  public void createExpandItem(String title, Composite composite) {
    ExpandItem item = new ExpandItem(bar, SWT.NONE, bar.getItemCount());   
//  if(imgGroup != null) item.setImage(imgGroup);
    item.setText(title);
    item.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
    item.setControl(composite);
  }
  
  public void removeAll(){    
    while(bar.getItemCount() > 0){
      Composite composite = (Composite)bar.getItem(0).getControl();      
      if(composite != null ) composite.dispose();
      bar.getItem(0).dispose();
    }   
    bar.layout();
  }
  
//  public String getTab() { return tab; }

//event
  public void fireSelectionEvent(ExpandSelectionEvent e) {
    synchronized(listeners)  {
      for(int i = 0; i < listeners.size(); i++){
        try{
          ExpandSelectionListener listener = listeners.get(i);         
          listener.select(e);       
          break;
        }catch(Exception exp){                  
        }       
      }
    }
  }
  public void addSelectionListener(ExpandSelectionListener listener) {      
    synchronized(listeners) {
      listeners.remove(listener);
      listeners.add(listener);
    }
  }
  
  public void removeSelectionListener(ExpandSelectionListener listener) {
    synchronized(listeners) {
      listeners.remove(listener);
    }
  }
  
  public void select(ExpandSelectionEvent ev) {    
    fireSelectionEvent(ev);    
  }

  public ExpandBar getBar() {return bar; }

//  public Image getImageDelete() { return imageDelete; }

}

