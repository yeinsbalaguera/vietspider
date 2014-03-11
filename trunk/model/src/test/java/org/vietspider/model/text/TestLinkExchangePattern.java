/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.text;

import java.util.Properties;

import org.vietspider.link.pattern.LinkExchangePatterns;
import org.vietspider.link.pattern.LinkPatternFactory;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 21, 2008  
 */
public class TestLinkExchangePattern {
  
  public static void main(String[] args) {
    StringBuilder builder = new StringBuilder();
    builder.append("http://blog.360.yahoo.com/blog-*?p=*#comments");
    builder.append("\n");
    builder.append("#comments>");
    
//    builder.append("http://ddhsonline.com/diendan/showthread.php?t=*");
//    builder.append("\n");
//    builder.append("diendan > forum;showthread>hienthi");
//    builder.append("\n");
//    builder.append("http://wwww.vietnamnet.com/t=*");
//    builder.append("\n");
//    builder.append("com>vn;");
    
    String key = "LINK_GENERATOR";
    
    Properties properties = new Properties();
    properties.setProperty(key, builder.toString());
    
    LinkExchangePatterns exchangePatterns =
      LinkPatternFactory.createMultiPatterns(LinkExchangePatterns.class, properties, key);
    
    String link = "http://ddhsonline.com/diendan/showthread.php?t=131";
    String value ;
    //value = exchangePatterns.create(link).get(0);
//    System.err.println(" thay "+ value);
//    
//    link = "http://ddhsonline.com/diendan/showthread.php?t=1dsfds31";
//    value = exchangePatterns.create(link);
//    System.err.println(" thay "+ value);
    
    link = "http://blog.360.yahoo.com/blog-ZmaTjZwweLLUDj7I8qzJ0wXHttZaAh7QTyAC?p=97#comments";
    value = exchangePatterns.create(link).get(0);
    System.err.println(" thay "+ value);
    
//    link = "http://wwww.vietnamnet.com/t=123";
//    value = exchangePatterns.create(link);
//    System.err.println(" thay "+ value);
    
  }
  
}
