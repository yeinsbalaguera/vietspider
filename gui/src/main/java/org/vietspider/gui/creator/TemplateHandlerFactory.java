/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import org.eclipse.swt.widgets.Text;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 27, 2008  
 */
public class TemplateHandlerFactory {
  
  public void handle(Text txtField) {
    int pos = txtField.getCaretPosition();
    String value = txtField.getText();
    if(pos >= value.length()) return;
    int start = pos;
    int end = pos;
    while(true) {
      int length = value.length();
      if(start >= length) break;
      while(start < length) {
        char c = value.charAt(start);
        if(c == '=' || c == '/' || c == '#') {
          start++;
          break;
        }
        start++;
      }
      end  = start;
      while(end < length) {
        char c = value.charAt(end);
        if(c == '&' || c == '/' || c == '?' || c == '#') break;
        end++;
      }
      if(end >= length) break;
      if(start >= end) continue;
      value = value.substring(0, start) + "*" + value.substring(end, length);
    }
    
    if(start < value.length()) {
      value = value.substring(0, start)+"*";
    }
    
    txtField.setText(value);
    txtField.setSelection(start);
  }
}
