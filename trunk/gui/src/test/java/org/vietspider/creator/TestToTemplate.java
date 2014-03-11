/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.creator;

import org.vietspider.gui.browser.TemplateDetector;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2009  
 */
public class TestToTemplate {
  
  public static void main(String[] args) {
//    String link = "http://www.tuoitre.com.vn/Tianyon/Index.aspx?ArticleID=328432&ChannelID=3";
//    String [] elements = TemplateDetector.split(link);
//    for(String ele :  elements) {
//      System.out.println("=== > "+ ele);
//    }
    
    String [] links = new String[] {
        "http://www.tuoitre.com.vn/Tianyon/Index.aspx?ArticleID=328457&ChannelID=16",
        "http://www.tuoitre.com.vn/Tianyon/Index.aspx?ArticleID=328395&ChannelID=10"
    };
    System.out.println(TemplateDetector.toTemplate(links));
  }
}
