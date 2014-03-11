/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.workspace;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Color;
import org.vietspider.ui.XPWidgetTheme;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 3, 2009  
 */
public class XPWindowTheme extends XPWidgetTheme {
  
  public static void setTabBrowserTheme(org.vietspider.ui.widget.tabfolder.CTabFolder tabBrowser) {
    if(!isPlatform()) return;
    //XPVerion
    tabBrowser.setBackground(new Color[]{        
        new Color(tabBrowser.getDisplay(), 255, 244, 234),        
//        new Color(getDisplay(), 226, 239, 249),
        new Color(tabBrowser.getDisplay(), 255, 255, 255)
    }, new int[]{90}, false);
   
    tabBrowser.setSelectionBackground(new Color[]{        
        new Color(tabBrowser.getDisplay(), 255, 255, 255),
        new Color(tabBrowser.getDisplay(), 177, 211, 243)
    }, new int[]{70}, true);
    
    tabBrowser.pack();
  }
  
  public static void setTabBrowserTheme(CTabFolder tabFolder) {
    if(!isPlatform()) return;
    //XPVerion
//    tabFolder.setBackground(new Color[]{        
//        new Color(tabFolder.getDisplay(), 255, 244, 234),        
////        new Color(getDisplay(), 226, 239, 249),
//        new Color(tabFolder.getDisplay(), 255, 255, 255)
//    }, new int[]{90}, false);
   
    tabFolder.setSelectionBackground(new Color[]{        
        new Color(tabFolder.getDisplay(), 255, 255, 255),
        new Color(tabFolder.getDisplay(), 177, 211, 243)
    }, new int[]{70}, true);
    
    tabFolder.pack();
  }
}
