/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl;

import java.io.File;
import java.util.Properties;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.PropertiesFile;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.plugin.handler.DocumentFormatCleaner.ArticleCleaner;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 2, 2008  
 */
public final class ResourcesLoader {
  
  public ArticleCleaner loadCleaner() {
    File file = UtilFile.getFile("system", "article-cleaner.xml");
    if(file.exists())  {
      try {
        String xml = new String(RWData.getInstance().load(file), Application.CHARSET);
        XMLDocument document = XMLParser.createDocument(xml, null);
        return XML2Object.getInstance().toObject(ArticleCleaner.class, document);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      return new ArticleCleaner();
    } 

    ArticleCleaner articleCleaner = new ArticleCleaner();
    try {
      String xml = Object2XML.getInstance().toXMLDocument(articleCleaner).getTextValue();
      RWData.getInstance().save(file, xml.getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    return articleCleaner;
  }
  
  public Properties loadCrawlError() {
    File file = UtilFile.getFile("system", "crawl.error.properties");
    if(!file.exists()) return new Properties();
    
    PropertiesFile propertiesFile = new PropertiesFile(true);
    try {
      return propertiesFile.load(UtilFile.getFile("system", "crawl.error.properties"));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return new Properties();
  }
  
//  public HostPatterns loadIgnoreSite() {
//    File file = UtilFile.getFile("system", "ignore.site");
//    try {
//      byte [] bytes = RWData.getInstance().load(file);
//      String value  = new String(bytes, Application.CHARSET);
//      return LinkPatternFactory.createPatterns(HostPatterns.class, value.split("\n"));
//    } catch (Exception e) {
//      LogService.getInstance().setThrowable(e);
//    }
//    return null;
//  }
//  
  
}
