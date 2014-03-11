/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.parser.rss2;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.Application;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 23, 2007  
 */
public class TestRSSMapping {
  
  private static void testRSS(String name) throws Exception {
    URL url = TestRSSMapping.class.getResource("/resources/"+name);
    File file = new File(url.toURI());
    System.out.println(file.getAbsolutePath());
    
    XMLDocument xmlDocument = XMLParser.createDocument(file, Application.CHARSET, new RefsDecoder());
    
    RSSParser rssParser = new RSSParser();
    
    MetaDocument metaDocument = rssParser.createDocument(xmlDocument);
    IMetaChannel channel  = metaDocument.getChannel();
    List<? extends IMetaItem> items = metaDocument.getItems();
    
    System.out.println("channel generator: "+ channel.getGenerator());
    System.out.println("channel title: "+ channel.getTitle());
    
    System.out.println("item size :"+items.size());
    
    for(IMetaItem item : items) {
      System.out.println();
      System.out.println("item title :"+item.getTitle());
      MetaLink metaLink = item.getLink(0);
      if(metaLink != null) {
        System.out.println("item link :"+metaLink.getHref());
      } else {
        System.out.println("no item link :");
      }
      System.out.println("item description :"+item.getDesc());
    }
  }
  
  public static void main(String[] args) throws Exception {
    testRSS("atom02.xml");
//    testRSS("rss10.xml");
  }
}
