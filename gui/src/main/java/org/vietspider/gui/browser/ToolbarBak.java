package org.vietspider.gui.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.gui.workspace.Workspace;

public class ToolbarBak extends UIToolbarBak {

  public ToolbarBak(Composite parent, Workspace workspace) {
    super(parent, workspace);
  }

  public String getText(){
    return cbo.getText();
  }

  public void setText( String text){    
    cbo.setText( text);
  }

  public void add( String value , int i){
    if( value == null) return;
    this.cbo.add( value, i);
  }

  public int indexOf(String value){
    if( value == null) return -1;
    return this.cbo.indexOf( value);
  }

  void keyListListener(KeyEvent e){    
    if(e.keyCode == 13){   
      if(list.getItemCount() == 1) cbo.setText(list.getItem( 0));      
      if( list.getSelectionCount() > 0) cbo.setText(list.getSelection()[0]);
      save();
      return;
    }   
    if( e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_UP) return;    
    cbo.text.setFocus(); 
    cbo.text.setSelection( cbo.text.getText().length(), cbo.text.getText().length());    
  }

  protected void keyListener(KeyEvent e){    
    if( e.keyCode == 13){      
      save();
      return;
    }   
    if( e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_UP){
      if( !popup.isVisible()) return;
      list.setFocus();
      if( list.getSelectionCount() > 1)
        this.cbo.setText( list.getSelection()[0]);
      return ;  
    }
    if(e.keyCode == 262144 || e.stateMask == 262144){
      if(e.keyCode == 97) cbo.text.setSelection(0, cbo.text.getText().length());      
      if(e.keyCode == 32) cbo.setText("");      
      return; 
    }   
    setSelect(cbo.getText());    
  } 

  private void setSelect(String txt){	  
    if(links == null || links.size() < 1){
//      HistoryData io = new HistoryData();
//      try{
//        links = io.loadRecentDate(10);
//      }catch( Exception exp){
//        ClientLog.getInstance().setException(cbo.getShell(), exp);
//      }  
    } 
    if(list.getItemCount() < 1) return;
    for( int i=0; i< links.size(); i++){    
      if(indexOf(txt, links.get(i)) && list.getItemCount() < 1) continue;     
      if(list.getItem(0).equals(links.get(i))) {
        customPopupShow(Math.min( 15, list.getItemCount()));   
        return;
      }          
    }
    list.removeAll();
    for( int i=0; i< links.size(); i++){                   
      if(indexOf(txt, links.get(i))) list.add( links.get(i));        
    }
    customPopupShow(Math.min( 15, list.getItemCount()));
  }

  protected void selectPopup(){
    cbo.setText(list.getSelection()[0]);
    save();
  }

  private boolean indexOf( String text, String pattern){
    if( text.indexOf("www")>-1 && text.indexOf(".") > -1)
      text = text.substring( text.indexOf(".")+1, text.length());
    else if(SWProtocol.lastIndexOf(text)> -1)  
      text = text.substring(SWProtocol.lastIndexOf(text), text.length());

    if(pattern.indexOf("www")>-1 && pattern.indexOf(".") > -1)
      pattern = pattern.substring( pattern.indexOf(".")+1, pattern.length());
    else if(SWProtocol.lastIndexOf(pattern) > -1)  
      pattern = pattern.substring(SWProtocol.lastIndexOf(pattern), pattern.length());
    return pattern.startsWith( text);
  } 

  public void customPopupShow( int visibleItemCount){
    Point size = cbo.getSize ();
    int itemCount = list.getItemCount ();
    itemCount = (itemCount == 0) ? visibleItemCount : Math.min(visibleItemCount, itemCount);
    int itemHeight = list.getItemHeight () * itemCount;
    Point listSize = list.computeSize (SWT.DEFAULT, itemHeight, false);
    list.setBounds (1, 1, Math.min( size.x - 2, listSize.x), listSize.y);

    int index = list.getSelectionIndex ();
    if (index != -1) list.setTopIndex (index);
    Display display = cbo.getDisplay ();
    Rectangle listRect = list.getBounds ();
    Rectangle parentRect = display.map ( cbo.getParent(), null, cbo.getBounds ());
    Point comboSize = cbo.getSize ();
    Rectangle displayRect = cbo.getMonitor ().getClientArea ();
    int width = Math.max (comboSize.x, listRect.width + 2);
    int height = listRect.height + 2;
    int x = parentRect.x;
    int y = parentRect.y + comboSize.y;
    if (y + height > displayRect.y + displayRect.height) y = parentRect.y - height;
    popup.setBounds (x, y, width, height);    
    popup.setVisible (true);
  }

  protected void save(){ 
    workspace.getTab().setUrl(getText());
    popup.setVisible(false);
    if(links == null) return;
    links.clear();
    links = null;
  }

}
