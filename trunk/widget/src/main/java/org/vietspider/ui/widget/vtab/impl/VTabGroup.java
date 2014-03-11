package org.vietspider.ui.widget.vtab.impl;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vietspider.ui.XPWidgetTheme;
import org.vietspider.ui.widget.vtab.IVTabGroup;
import org.vietspider.ui.widget.vtab.IVTabItem;

public class VTabGroup implements IVTabGroup {  
  
  private String name;
  
  private boolean selected;
  
  private java.util.List<IVTabItem> itemList;
  
  private Control peer;
  
  private ScrolledComposite composite;
  
  public VTabGroup(VTab tab, String n) {
    selected = false;
    itemList = new ArrayList<IVTabItem>();
    name = n;
    
    VTabTitle groupButton = XPWidgetTheme.createTabTitle(tab); 

    groupButton.setText(name);
    peer = groupButton;
    
    composite = new ScrolledComposite(tab, SWT.NONE);   
    composite.setBackground(tab.getBackground());
    setComposite(composite);
  }
  
  public String getName(){
    return name;
  }
  
  public void addItem(IVTabItem aItem){
    itemList.add(aItem);
  } 
  
  public Control getPeer() {
    return peer;
  }
  
  public void setPeer(Control aPeer){
    peer = aPeer;    
  }
  
  public ScrolledComposite getComposite() {
    return composite;
  }
  
  public void setComposite(ScrolledComposite aComposite){
    Composite itemContainer = new Composite(aComposite, SWT.NONE);  
    itemContainer.setBackground(aComposite.getBackground());
    aComposite.setContent(itemContainer);
    itemContainer.setLayout(new VTabItemLayout(this));
  }  
  
  public java.util.List<IVTabItem> getItemList() {
    return itemList;
  }
  
  public boolean isSelected(){
    return selected;
  }
  
  public void setSelected(boolean aSelected) {
    selected = aSelected;
  }
  
  public void setVisible(boolean value){
    peer.setVisible(value);
    composite.setVisible(value);
  }
  
}
