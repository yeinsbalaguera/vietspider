/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.crawl.plugin.handler.DocumentFormatCleaner.ArticleCleaner;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 11, 2011  
 */
public class TestArticleCleanerXML {
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\test\\xml\\article-cleaner.xml");
    String xml = new String(RWData.getInstance().load(file), Application.CHARSET);
    ArticleCleaner cleaner = XML2Object.getInstance().toObject(ArticleCleaner.class, xml);
    
    System.out.println(cleaner);
    
    System.exit(0);
  }
}
