package org.vietspider.ui.htmlexplorer;

import org.eclipse.swt.widgets.Composite;
import org.vietspider.ui.widget.ApplicationFactory;

public class HTMLExplorerToolbar extends HTMLExplorerToolbarViewer {

  public HTMLExplorerToolbar(ApplicationFactory factory, 
      Composite parent, HTMLExplorerViewer viewer) {
    super(factory, parent, viewer);
  }

  public String getText(){
    return cboAddress.getText();
  }

  public void setText( String text){    
    cboAddress.setText(text);
  }

  public void add(String value , int i){
    if( value == null) return;
    this.cboAddress.add( value, i);
  }

  public int indexOf(String value){
    if( value == null) return -1;
    return this.cboAddress.indexOf( value);
  }
  
}
