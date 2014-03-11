/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import org.eclipse.swt.widgets.Text;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 19, 2008  
 */
public class TemplateHandlerAction {
 
  private char [] chars;
  
  public TemplateHandlerAction() {
    chars = new char[]{
        '/', '\\', '?', '=', '#', '.', '&', 
        '\'', '"', ',' , '(', ')',
        ';'
    };
  }
   
  public TemplateHandlerAction(char [] c) {
    chars = c;
  }

  public void handle(Text txtField) {
    int pos = txtField.getCaretPosition();
    String value = txtField.getText();
    if(pos >= value.length()) return;
    int start = pos;
    while(start > 0) {
      if(isSeparator(value.charAt(start))) {
        start++;
        break;
      }
      start--;
    }
    int end = pos;
    while(end < value.length()) {
      if(isSeparator(value.charAt(end))) {
        break;
      }
      end++;
    }
    if(start >= end) return;
    value = value.substring(0, start)+ "*" + value.substring(end, value.length());
    txtField.setText(value);
    txtField.setSelection(start);
  }
  
  private boolean isSeparator(char c) {
    for(char ele : chars) {
      if(c == ele) return true;
    }
    return false;
  }
}
