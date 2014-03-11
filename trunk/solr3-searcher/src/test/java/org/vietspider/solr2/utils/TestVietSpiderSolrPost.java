/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.utils;

import java.io.File;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.common.io.DataWriter;
import org.vietspider.solr2.external.ExternalSolrPost;
import org.vietspider.solr2.external.TempDocument;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 19, 2011  
 */
public class TestVietSpiderSolrPost {
  
    public static void main(String[] args) throws Exception {
      File file  = new File("D:\\java\\test\\vsnews\\data\\");

      System.setProperty("save.link.download", "true");
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
      Article article = new Article();
      
      Meta meta = new Meta();
      meta.setId("1123");
      meta.setTitle("title 1");
      meta.setDesc("this is description");
      meta.setSource("http://test.com.vn");
      meta.setTime("12/06/2011");
      meta.setSourceTime("13/5/2009");
      article.setMeta(meta);
      
      Content content = new Content(meta.getId(), meta.getTime(), "content asdhaskdasdk content");
      article.setContent(content);
      
      Domain domain = new Domain("24/05/2011", "XML", "chuyen muc", "test_com");
      article.setDomain(domain);
      
      TempDocument document = new TempDocument(article);
      
      ExternalSolrPost post = new ExternalSolrPost("http://localhost:8080/");
      byte [] bytes = post.postData(document);
      file = new File("D:\\java\\test\\test.solr.html");
      org.vietspider.common.io.RWData.getInstance().save(file, bytes);
      
      System.exit(0);
    }
}
