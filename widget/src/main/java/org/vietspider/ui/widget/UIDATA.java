/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.ui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * May 24, 2006
 */
public class UIDATA {
  
  public static Display DISPLAY = Display.getDefault();
  
  public static String FONT_NAME = "Tahoma";
  public static String FONT_NAME_2 = "Verdana";
  public static int FONT_SIZE = 8;
  
  public static volatile boolean isMacOS = false;
  
  static {
    String platform = SWT.getPlatform();
    if(platform.equalsIgnoreCase("carbon")) {
      isMacOS = true;
      FONT_SIZE = 10;
      FONT_NAME = "Arial";
    }
//    System.out.println(SWT.O);
  }
  
  public static Font FONT_9 = new Font(DISPLAY, FONT_NAME, FONT_SIZE+1, SWT.NORMAL);
  public static Font FONT_9B = new Font(DISPLAY, FONT_NAME, FONT_SIZE+1, SWT.BOLD);
  public static Font FONT_9I = new Font(DISPLAY, FONT_NAME, FONT_SIZE+1, SWT.ITALIC);
  public static Font FONT_10 = new Font(DISPLAY, FONT_NAME, FONT_SIZE+2, SWT.NORMAL);
  
  public static Font FONT_13 = new Font(DISPLAY, FONT_NAME, FONT_SIZE+5, SWT.NORMAL);
  public static Font FONT_10B = new Font(DISPLAY, FONT_NAME, FONT_SIZE+2, SWT.BOLD);
  public static Font FONT_8T = new Font(DISPLAY, FONT_NAME, FONT_SIZE, SWT.NORMAL);
  public static Font FONT_8TB = new Font(DISPLAY, FONT_NAME, FONT_SIZE, SWT.BOLD);
  public static Font FONT_11B = new Font(DISPLAY, FONT_NAME, FONT_SIZE+3, SWT.BOLD);
  public static Font FONT_11 = new Font(DISPLAY, FONT_NAME, FONT_SIZE+3, SWT.NORMAL);
  
  public static Font FONT_12B = new Font(DISPLAY, FONT_NAME_2, FONT_SIZE+3, SWT.BOLD);
  
  public static Font FONT_8V = new Font(DISPLAY, FONT_NAME_2, FONT_SIZE, SWT.NORMAL);
  public static Font FONT_8VB = new Font(DISPLAY, FONT_NAME_2, FONT_SIZE, SWT.BOLD);
  
  public static Font FONT_9VB = new Font(DISPLAY, FONT_NAME_2, FONT_SIZE+1, SWT.BOLD);
  public static Font FONT_9VI = new Font(DISPLAY, FONT_NAME_2, FONT_SIZE+1, SWT.ITALIC);
  
//  public static Font FONT_10 = new Font(DISPLAY, SFONT, 13, SWT.NORMAL);
  
  public static Color FCOLOR = new Color(DISPLAY, 0, 64, 64);
  public static Color PROGRESS_COLOR =  new Color(DISPLAY, 192, 192, 192);
  public static Color ADDRESS_BCOLOR = new Color(DISPLAY, 250, 250, 250);
  public static Color BCOLOR = DISPLAY.getSystemColor(SWT.COLOR_WHITE);
  
}
