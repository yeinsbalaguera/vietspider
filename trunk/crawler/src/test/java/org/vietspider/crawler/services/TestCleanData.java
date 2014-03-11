/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.crawler.services;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.CrawlService;
import org.vietspider.crawl.plugin.handler.DocumentFormatCleaner;
import org.vietspider.crawl.plugin.handler.DocumentFormatCleaner.ArticleCleaner;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jan 29, 2007
 */
public class TestCleanData {
  
  private static ArticleCleaner loadCleaner() {
    File file = UtilFile.getFile("system", "article-cleaner.xml");
    ArticleCleaner articleCleaner = null;
    if(file.exists())  {
      try {
        String xml = new String(RWData.getInstance().load(file), Application.CHARSET);
        XMLDocument document = XMLParser.createDocument(xml, null);
//        System.out.println(xml);
        articleCleaner = XML2Object.getInstance().toObject(ArticleCleaner.class, document);
      }catch (Exception e) {
        e.printStackTrace();
      }
      if(articleCleaner == null) articleCleaner = new ArticleCleaner();
      return articleCleaner;
    } 

    articleCleaner = new ArticleCleaner();
    try {
      String xml = Object2XML.getInstance().toXMLDocument(articleCleaner).getTextValue();
      org.vietspider.common.io.RWData.getInstance().save(file, xml.getBytes(Application.CHARSET));
    }catch (Exception e) {
      e.printStackTrace();
//      LogService.getInstance().setThrowable(e);
    }

    return articleCleaner;
  }
  

  public static void main(String[] args) throws Exception {
    File file = new File(CrawlService.class.getResource("").toURI());
    String path = file.getAbsolutePath()+File.separator+".."+File.separator+"..";
    path += File.separator+".."+File.separator+".."+File.separator+".."+File.separator+"..";
    path += File.separator + "startup"+File.separator+"src"+File.separator+"test"+File.separator+"data";
    file  = new File(path);
    
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.out.println(file.getCanonicalPath());
    
    DocumentFormatCleaner cleanData = new DocumentFormatCleaner();
    
    HTMLDocument document = new HTMLParser2().createDocument(new File("F:\\Temp2\\web\\output\\a.htm"), "utf-8");
    cleanData.handle(document.getRoot());
    
    HTMLNode node = document.getRoot();
    file = new File("F:\\Temp2\\web\\output\\b.htm");    
    RWData.getInstance().save(file, node.getTextValue().getBytes("utf-8"));
  }

}
