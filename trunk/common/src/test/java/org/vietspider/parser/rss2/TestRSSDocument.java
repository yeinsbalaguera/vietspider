/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.parser.rss2;

import org.vietspider.serialize.Object2XML;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 23, 2007  
 */
public class TestRSSDocument {
  
  public static void main(String[] args) throws Exception {
    MetaDocument rssDocument = new MetaDocument();
    
    RSSItem rssItem = new RSSItem();
    rssItem.setTitle("title for item 1");
    rssItem.setDesc(" descrption adabsjdhasd ");
    MetaLink link = new MetaLink();
    link.setHref("http://www.vietnamnet.vn");
    rssItem.addLink(link);
    
    RSSChannel rssChannel = new RSSChannel();
    rssChannel.setGenerator("thuannd");
    rssChannel.setTitle("rss channel for first rss document");
    rssDocument.addItem(rssItem);
    
    rssDocument.setChannel(rssChannel);
    
    String xml = Object2XML.getInstance().toXMLDocument(rssDocument).getTextValue();
    System.out.println(xml);
  }
  
}
