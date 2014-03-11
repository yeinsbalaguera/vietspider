/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 6, 2009  
 */
public class PathHightLight {

  private static Color nameColor;
  private static Color indexColor;
//  private static Color commonColor;

  /*static public synchronized void unsetAutoStyle(StyledText styledText, Color color) {
    int index = 0;
    String text = styledText.getText(); 

    StyleRange styleRange = new StyleRange();
    styleRange.start = index;
    styleRange.fontStyle = SWT.NORMAL;
    styleRange.foreground = color;
    styleRange.length = text.length();
    styledText.setStyleRange(styleRange);
  }*/

  static public synchronized void setAutoStyle(StyledText styledText) {
    int index = 0;
    String text = styledText.getText(); 
    int max = text.length(); //styledText. getTextLimit();
    Display display = styledText.getDisplay();
    
    int caretPos = styledText.getCaretOffset();

    if(nameColor == null) {
      nameColor = new Color(display, 0, 0, 225);
      indexColor = display.getSystemColor(SWT.COLOR_RED);
//      commonColor = new Color(display, 100, 100, 100);
    }
    
    StyleRange styleRange = new StyleRange();
    styleRange.start = index;
    styleRange.fontStyle = SWT.NORMAL;
    styleRange.foreground = nameColor;
    while(index < max) {
      char c = text.charAt(index);
      if(c == '[') {
//        setCommonStyle(styledText, index);
        
        if(styleRange.start >=  index-1) return;

        String value = styledText.getText(styleRange.start, index-1);
        String upper = value.toUpperCase();
        if(!upper.equals(value)) {
          styledText.replaceTextRange(styleRange.start, value.length(), upper);
        }
        styleRange.length = upper.length();
        styledText.setStyleRange(styleRange);

        styleRange = new StyleRange();
        styleRange.start = index + 1;
        styleRange.fontStyle = SWT.NORMAL;
        styleRange.foreground = indexColor;
      } else if (c == ']') {
//        setCommonStyle(styledText, index);

        styleRange.length = index - styleRange.start;
        styledText.setStyleRange(styleRange);
      } else  if (c == '.') {
//        setCommonStyle(styledText, index);

        styleRange = new StyleRange();
        styleRange.start = index + 1;
        styleRange.fontStyle = SWT.NORMAL;
        styleRange.foreground = nameColor;
      }
      index++;
    }
    
    styledText.setCaretOffset(caretPos);
  }

  public static void handleDoubleClick(StyledText textEditor) {
    int pos = textEditor.getCaretOffset();
    String value  = textEditor.getText();
    if(pos < 0 || pos >= value.length())  {
      //      if(popup != null) popup.dispose(); 
      return;
    } 

    int start = -1;
    int index = pos;
    while(index > -1) {
      char c = value.charAt(index);
      if(c == '[') {
        start = index;
        break;
      } else if(c == '.') {
        break;
      }
      index--;
    }
    if(start < 0) return;

    int end = -1;
    index = pos;
    while(index < value.length()) {
      char c = value.charAt(index);
      if(c == ']') {
        end = index;
        break;
      } else if(c == '.') {
        break;
      }
      index++;
    }
    if(end < 0) return;

    value = value.substring(0, start+1) + "*" + value.substring(end);
    textEditor.setText(value);
    textEditor.setSelection(end, end);
  }

//  private static void setCommonStyle(StyledText styledText, int index) {
//    StyleRange commonStyleRange = new StyleRange();
//    commonStyleRange.start = index;
//    commonStyleRange.length = 1;
//    commonStyleRange.fontStyle = SWT.NORMAL;
//    commonStyleRange.foreground = commonColor;
//    styledText.setStyleRange(commonStyleRange);
//  }
}
