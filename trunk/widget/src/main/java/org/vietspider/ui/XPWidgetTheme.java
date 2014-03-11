/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.vietspider.ui.widget.vtab.impl.VTab;
import org.vietspider.ui.widget.vtab.impl.VTabTitle;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 3, 2009  
 */
public class XPWidgetTheme {
  
  public static boolean SKIN  = true;

//  public static Office2007ThemeRender THEME;

  public static VTabTitle createTabTitle (VTab tab) {
    if(!isPlatform()) return new VTabTitle(tab);
    //  VTabTitle groupButton = new VTabTitle(tab, 
    //      new Color(tab.getDisplay(), 255, 255, 120), 
    //      new Color(tab.getDisplay(), 255, 255, 255),
    //      new Color(tab.getDisplay(), 255, 255, 145));

    //    return new VTabTitle(tab, 
    //        new Color(tab.getDisplay(), 177, 211, 243), 
    //        new Color(tab.getDisplay(), 255, 255, 255),
    //        new Color(tab.getDisplay(), 226, 239, 249)); 

    return new VTabTitle(tab, 
        new Color(tab.getDisplay(), 255, 210, 147),
        new Color(tab.getDisplay(), 255, 255, 255),
        new Color(tab.getDisplay(), 177, 211, 243)); 
  }

//  public static void setWin32Theme(Composite composite) {
//    if(!isPlatform()) return;
//    //XPVersion
//    
//    Color color = ColorCache.getInstance().getColor(245, 245, 240);
//    if(composite instanceof Shell) {
//      ShellWrapper wrapper = new ShellWrapper((Shell)composite);
//
//      //      if (Win32.getWin32Version() >= Win32.VERSION(5, 0)) {
//      wrapper.installTheme(ThemeConstants.STYLE_OFFICE2007);
//      //      } else {
//      //        wrapper.installTheme(ThemeConstants.STYLE_VISTA);
//      //      }
//    }
//    setBackgroup(composite, color);//new Color(shell.getDisplay(), 255, 255, 255));
//  }

  public static void setBackgroup(Composite composite, Color color) {
    if(!isPlatform()) return;
    if(isDefaultBackground(composite)) composite.setBackground(color);
    org.eclipse.swt.widgets.Control [] controls = composite.getChildren();
    for(int i = 0; i < controls.length; i++) {
      if(Composite.class.isInstance(controls[i])) {
        setBackgroup((Composite)controls[i], color);
      } else if(Label.class.isInstance(controls[i])) {
        Label label = (Label) controls[i];
        if(isDefaultBackground(label)) label.setBackground(color);
      } else {
        if(isDefaultBackground(controls[i])) controls[i].setBackground(color);
      }
    }

    //    composite.setBackground(color);
    //    Color color1 = Color.win32_new (composite.getDisplay(), OS.GetSysColor (OS.COLOR_BTNFACE));
    //    System.out.println(color1);
    //    System.out.println(composite.getBackground());
  }

  protected static boolean isDefaultBackground(Control composite) {
    return false;
//    Color color1 = Color.win32_new (composite.getDisplay(), org.eclipse.swt.internal.win32.OS.GetSysColor (org.eclipse.swt.internal.win32.OS.COLOR_BTNFACE));
//    Color color2 = composite.getBackground();
//    if(color1.getRed() != color2.getRed()) return false;
//    if(color1.getBlue() != color2.getBlue()) return false;
//    if(color1.getGreen() != color2.getGreen()) return false;
//    return true;
  }

  public static boolean isPlatform() {
    return false;
//    if(!SKIN) return false;
////        return false;
//    if(!"win32".equalsIgnoreCase(SWT.getPlatform())) return false;
//    if(!(Win32.getWin32Version() >= Win32.VERSION(5, 0))) return false;
//    if(THEME == null) THEME = new Office2007ThemeRender();
//    return true;
  }

}
