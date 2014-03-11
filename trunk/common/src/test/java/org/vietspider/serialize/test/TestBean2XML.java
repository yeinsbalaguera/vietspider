/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize.test;

import java.util.ArrayList;

import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 26, 2010  
 */
public class TestBean2XML {
  
  public static void main(String[] args) throws Exception {
    Article article = new Article();
    String xml = Object2XML.getInstance().toXMLDocument(article).getTextValue();
    System.out.println("====================================================");
    System.out.println(xml);
    System.out.println("====================================================");
    Article article2 = XML2Object.getInstance().toObject(Article.class, xml);
    System.out.println("meta " + article2.getMeta());
    
    
    Meta meta = new Meta();
    article.setMeta(meta);
    xml = Object2XML.getInstance().toXMLDocument(article).getTextValue();
    System.out.println("====================================================");
    System.out.println(xml);
    
    System.out.println("====================================================");
    article2 = XML2Object.getInstance().toObject(Article.class, xml);
    System.out.println(article2.getMeta().getItem());

    ArrayList<String> list = new ArrayList<String>();
    list.add("thuan");
    meta.setItem(list);
    
    xml = Object2XML.getInstance().toXMLDocument(article).getTextValue();
    System.out.println("====================================================");
    System.out.println(xml);
    
    System.out.println("====================================================");
    article2 = XML2Object.getInstance().toObject(Article.class, xml);
    System.out.println(article2.getMeta().getItem());
    
    meta.putProperty("key", "thuan");
    xml = Object2XML.getInstance().toXMLDocument(article).getTextValue();
    System.out.println("====================================================");
    System.out.println(xml);
    
    System.out.println("====================================================");
    article2 = XML2Object.getInstance().toObject(Article.class, xml);
    System.out.println(article2.getMeta().getPropertyValue("key"));
  }
  
}
