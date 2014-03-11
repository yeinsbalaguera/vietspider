package org.vietspider.gui.browser;

import org.eclipse.swt.widgets.Composite;


public class Toolbar extends UIToolbar {

  public Toolbar(Composite parent, TabBrowser tab) {
    super(parent, tab);
  }

  public String getText(){
    return cbo.getText();
  }

  public void setText( String text){    
    if(cbo.getText().equalsIgnoreCase(text)) return;
    cbo.setText( text);
  }
  
  public boolean isFocusText() { return cbo.text.isFocusControl(); }

  public void add( String value , int i){
    if( value == null) return;
    this.cbo.add( value, i);
  }

  public int indexOf(String value){
    if( value == null) return -1;
    return this.cbo.indexOf( value);
  }


}
