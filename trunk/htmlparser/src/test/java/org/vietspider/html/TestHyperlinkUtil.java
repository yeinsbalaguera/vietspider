/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html;

import java.io.File;
import java.util.List;

import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HyperLinkUtil;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 4, 2008  
 */
public class TestHyperlinkUtil {
  public static void main(String[] args) throws Exception {
    HyperLinkUtil hyperLinkUtil = new HyperLinkUtil();
    File file  = new File("D:\\Temp\\webclient\\a.html");
    HTMLParser2 parser2 = new HTMLParser2();
    HTMLDocument document = parser2.createDocument(file,"utf-8");
    
    HTMLNode node = document.getRoot();
    
//    System.out.println(node.getTextValue());
    
    List<String> list = hyperLinkUtil.scanSiteLink(node);
    System.out.println(list.size());
    for(String ele : list) {
      System.out.println(ele);
    }
  }
}
