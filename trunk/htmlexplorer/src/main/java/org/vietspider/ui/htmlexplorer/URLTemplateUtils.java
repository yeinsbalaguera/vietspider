/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import org.eclipse.swt.widgets.Text;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 27, 2008  
 */
public class URLTemplateUtils {
  
  public static void handle(Text txtField) {
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
      String part = value.substring(start+1, end);
      if(isNumber(part)) {
        value = value.substring(0, start)+ "$" + value.substring(end, length);
      } else if(isLetter(part)) {
        value = value.substring(0, start)+ "@" + value.substring(end, length);
      } else {
        value = value.substring(0, start)+ "*" + value.substring(end, length);
      }
//      value = value.substring(0, start) + "*" + value.substring(end, length);
    }
    
    if(start < value.length()) {
      value = value.substring(0, start)+"*";
    }
    
    txtField.setText(value);
    txtField.setSelection(start);
  }
  
  public static String handle(String url) {
    int pos = url.indexOf('/', 10);
    if(pos < 0) pos = url.indexOf('?', 10);
    if(pos < 0) return null;
    if(pos >= url.length()) return null;
    int start = pos;
    int end = pos;
    while(true) {
      int length = url.length();
      if(start >= length) break;
      while(start < length) {
        char c = url.charAt(start);
        if(c == '=' || c == '/' || c == '#') {
          start++;
          break;
        }
        start++;
      }
      end  = start;
      while(end < length) {
        char c = url.charAt(end);
        if(c == '&' || c == '/' || c == '?' || c == '#') break;
        end++;
      }
      if(end >= length) break;
      if(start >= end) continue;
      String value = url.substring(start+1, end);
      if(isNumber(value)) {
        url = url.substring(0, start)+ "$" + url.substring(end, length);
      } else if(isLetter(value)) {
        url = url.substring(0, start)+ "@" + url.substring(end, length);
      } else {
        url = url.substring(0, start)+ "*" + url.substring(end, length);
      }
    }
    
    if(start < url.length()) {
      url = url.substring(0, start)+"*";
    }

    return url;
  }
  
  private static boolean isNumber(String value) {
    int idx = 0;
    while(idx < value.length()) {
      char c = value.charAt(idx);
      if(!Character.isDigit(c)) return false;
      idx++;
    }
    return true;
  }
  
  private static boolean isLetter(String value) {
    int idx = 0;
    while(idx < value.length()) {
      char c = value.charAt(idx);
      if(!Character.isLetter(c)) return false;
      idx++;
    }
    return true;
  }
  
  public static class HandleAction {
    
    private char [] chars;
    
    public HandleAction() {
      chars = new char[]{
          '/', '\\', '?', '=', '#', '.', '&', 
          '\'', '"', ',' , '(', ')',
          ';'
      };
    }
     
    public HandleAction(char [] c) {
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
      String part = value.substring(start+1, end);
      if(isNumber(part)) {
        value = value.substring(0, start)+ "$" + value.substring(end, value.length());
      } else if(isLetter(part)) {
        value = value.substring(0, start)+ "@" + value.substring(end, value.length());
      } else {
        value = value.substring(0, start)+ "*" + value.substring(end, value.length());
      }
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
}
