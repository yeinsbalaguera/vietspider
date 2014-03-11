/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HyperLinkUtil;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 30, 2008  
 */
public class TestExport360YahooAccount {
  
  private static  void parseText(String text)  throws Exception {
    HTMLDocument document = new HTMLParser2().createDocument(text);
    HyperLinkUtil linkUtil = new HyperLinkUtil();
    List<String> collection = linkUtil.scanSiteLink(document.getRoot());
    for(int i = 0; i < collection.size(); i++) {
      String link = collection.get(i).replaceAll("&amp;", "&");
      if(!link.startsWith("/blogger_")) continue;
      if(!link.endsWith("/home")) continue;
      String [] elements = link.split("/");
      System.out.println("http://360.yahoo.com/profile-"+elements[2]);
    }
  }
  
  public static void main(String[] args) {
    File [] files = new File("D:\\Temp\\Xemblog\\").listFiles();
    for(int i = 0; i < files.length; i++) {
      try {
        byte [] bytes = RWData.getInstance().load(files[i]);
        parseText(new String(bytes, Application.CHARSET));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
}
