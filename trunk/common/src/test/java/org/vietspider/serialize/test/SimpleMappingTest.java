/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize.test;

import java.util.ArrayList;

import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 6, 2011  
 */
public class SimpleMappingTest {
  
  public static void main(String[] args) throws Exception {
    Article article = new Article();
    article.setTitle("this is title");
    article.setContent("this is content");
    
    Meta meta = new Meta();
    ArrayList<String> list = new ArrayList<String>();
    list.add("thuan");
    meta.setItem(list);
    
    meta.putProperty("key1", "this is a");
    meta.putProperty("key2", "this is b");
    
    article.setMeta(meta);
    
    String xml = Object2XML.getInstance().toXMLDocument(article).getTextValue();
    System.out.println("====================================================");
    System.out.println(xml);
    System.out.println("====================================================");
    Article article2 = XML2Object.getInstance().toObject(Article.class, xml);
    
    System.out.println("title: " + article2.getTitle());
    System.out.println("content: " + article2.getContent());
    
    Meta meta2 = article2.getMeta();
    System.out.println("meta: " + meta2.getItem().size());
    
    System.out.println("meta: " + meta2.getPropertyValue("key2"));
  }
}
