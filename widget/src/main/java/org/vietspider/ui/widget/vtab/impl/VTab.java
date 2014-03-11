package org.vietspider.ui.widget.vtab.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.vietspider.ui.widget.vtab.IVTab;
import org.vietspider.ui.widget.vtab.IVTabGroup;
import org.vietspider.ui.widget.vtab.IVTabItem;

/**
 * @author kite
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class VTab extends Composite implements IVTab {

  private IVTabGroup selectedGroup;

  private java.util.List<IVTabGroup> groupsList;
  private List<VTabHideListener> listeners;
  
  private List<ImageHyperlink> icons;

  private boolean visible = true;

  public VTab(Composite com, int i) {
    super(com, i);    
    setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));    
    selectedGroup = null;
    groupsList = new ArrayList<IVTabGroup>();
    setLayout(new VTabLayout(this));    
    listeners = new ArrayList<VTabHideListener>();

    addMouseListener(new MouseAdapter(){
      public void mouseDown(MouseEvent event) {
        if(checkShow(event)) layout();       
      } 
    });     

    addPaintListener(new PaintListener(){
      public void paintControl(PaintEvent event) {
        onPaint(event);
      }
    });
    
    icons = new ArrayList<ImageHyperlink>();
  }
  
  public ImageHyperlink createIcon(Image image, String tip, HyperlinkAdapter listener)  {
    ImageHyperlink icon = new ImageHyperlink(this , SWT.CENTER);
    icon.setBackground(getBackground());
    if(image != null) icon.setImage(image);
    if(tip != null) icon.setToolTipText(tip);
    icons.add(icon);
    if(listener != null) icon.addHyperlinkListener(listener);
    return icon;  
  }

  public VTabGroup createGroup(String name) { 
    VTabGroup group = new VTabGroup(this, name); 
    groupsList.add(group);
    group.getPeer().addMouseListener(new MouseAdapter(){
      public void mouseUp(MouseEvent event) {
        Control vControl = (Control)event.widget;
        IVTabGroup grp = getGroupByControl(vControl);
        if(grp == null || grp.isSelected()) return;       
        selectedGroup.setSelected(false);
        grp.setSelected(true);
        selectedGroup = grp;         
        layout();           
      } 
    });
    if(selectedGroup != null) return group;
    selectedGroup = group;
    group.setSelected(true);    
    return group;
  }

  private boolean checkShow(MouseEvent event){
    Rectangle rect = ((VTab)event.getSource()).getClientArea();
    int y = rect.height - 15;
    if(event.y <  y - 10   ||  event.y > y + 10) return false;
    VTabHideEvent hevent = new VTabHideEvent();
    if(visible){      
      int x = rect.width - 25; 
      if(event.x < x || event.x > x+10) return false;
      hide(hevent);
    }else{
      if(event.x < 1 || event.x > 15 ) return false;
      show(hevent);      
    }    
    return true;
  }
  
  public IVTabGroup getGroupByName(String name){
    IVTabGroup group = null;
    for(IVTabGroup ele : groupsList){
      if(ele.getName().trim().equalsIgnoreCase(name)) group = ele;
    }
    if(group == null) group = createGroup(name);
    return group;
  }

  public Composite getGroup(String name){    
    return (Composite)(getGroupByName(name).getComposite().getContent());  
  }

  public IVTabItem addItem(String aGroup, Control control){
    IVTabGroup group = getGroupByName(aGroup);
    IVTabItem aItem = new VTabItem();
    aItem.setGroup(group);
    aItem.setControl(control);     
    group.addItem(aItem);
    return aItem;
  }

  public IVTabGroup getGroupByControl(Control aControl){    
    for(IVTabGroup ele : groupsList) {
      if(ele.getPeer() == aControl) return ele;
    }
    return null;    
  }

  public java.util.List<IVTabGroup> getGroupsList(){
    return groupsList;
  }

  public void setSelectGroup(int idx){
    for( int i=0; i < this.groupsList.size(); i++){
      if( i != idx) continue;
      IVTabGroup group = groupsList.get(i);
      selectedGroup.setSelected(false);
      group.setSelected(true);
      selectedGroup = group;
      layout();
      return;
    }
  }

  public int getSelectGroup(){
    for( int i=0; i< this.groupsList.size(); i++){
      if( groupsList.get(i).isSelected()) return i;
    }
    return 0;
  }  

  public void layout(){
    super.layout();
    if(!visible) return;
    Control [] control = selectedGroup.getComposite().getChildren();
    if(control.length < 1) return;
    Composite comp = (Composite)control[0]; 
    comp.layout();    
    showIcons();
  }  
  

  private void showIcons() {
    Rectangle rect = getClientArea();
    int x = rect.width/2 - icons.size()*15; 
    int y = rect.height - 15;
    int width = 25;
    int height = 25;
    
    for(ImageHyperlink icon : icons) {
      if(icon.getImage() == null) {
        width = 5;
      } else {
        width = 25;
      }
      icon.setBounds(x, y-10, width, height);
      icon.layout();
      x += width + 5;
    }
  }

  private void invisibleIcons() {
    for(ImageHyperlink icon : icons) {
      icon.setBounds(0, 0, 0, 0);
      icon.layout();
    }
  }

  void onPaint(PaintEvent event) {
    Rectangle rect = getClientArea();
    if (rect.width == 0 || rect.height == 0) return;
    
    if(!visible) {
      invisibleIcons();
    } else {
      showIcons();
    }
    
    GC gc = event.gc;
    gc.setBackground(new Color(getDisplay(), 255, 255, 255));  
    int y = rect.height - 15;
    int x;
    if(!visible) x = 2; else x = rect.width-25;
    gc.fillRectangle(x-1, y-5, 15, 15);
    gc.setForeground(new Color(getDisplay(), 170, 170, 170));   
    gc.drawRectangle(x-1, y-5, 15, 15);

    if(!visible){
      x = 4;  
      gc.drawLine(x,y,     x+2,y+2);
      gc.drawLine(x+2,y+2, x,y+4);
      gc.drawLine(x+1,y,   x+3,y+2);
      gc.drawLine(x+3,y+2, x+1,y+4);
      gc.drawLine(x+4,y,   x+6,y+2);
      gc.drawLine(x+6,y+2, x+5,y+4);
      gc.drawLine(x+5,y,   x+7,y+2);
      gc.drawLine(x+7,y+2, x+4,y+4);
      return ;
    }

    x = rect.width - 23;
    y = rect.height - 14;

    gc.drawLine(x, y+1,     x+3,y-2); 
    gc.drawLine(x+1, y+1,    x+3,y-1);

    gc.drawLine( x, y+1,   x+3,y+4);
    gc.drawLine( x, y+1,   x+3,y+3);

    gc.drawLine( x+4, y+1,   x+7,y-2);
    gc.drawLine( x+5, y+1,     x+7,y-1);

    gc.drawLine( x+4, y+1,   x+7,y+4);
    gc.drawLine( x+4, y+1,   x+7,y+3);  
    
  }

//event
  public void fireHideEvent(VTabHideEvent e) {
    synchronized(listeners)  {
      for(int i = 0; i < listeners.size(); i++)  listeners.get(i).hide(e);   
    }
  }

  public void fireShowEvent(VTabHideEvent e) {
    synchronized(listeners)  {
      for(int i = 0; i < listeners.size(); i++) listeners.get(i).show(e);   
    }
  }
  
  public void addHideListener(VTabHideListener listener) {      
    synchronized(listeners) {
      listeners.remove(listener);
      listeners.add(listener);
    }
  }
  
  public void removeHideListener(VTabHideListener listener) {
    synchronized(listeners) {
      listeners.remove(listener);
    }
  }
  
  public void hide(VTabHideEvent ev) { 
    fireHideEvent(ev);    
    setVisible(false);
  } 

  public void show(VTabHideEvent ev) {   
    fireShowEvent(ev);    
    setVisible(true);
  } 

  public boolean isVisible() { return visible; }

  public void setVisible(boolean value){
    visible = value;
    for(IVTabGroup ele : groupsList){
      ele.setVisible(value);
    }
  }

}
