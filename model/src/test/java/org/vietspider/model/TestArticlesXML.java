/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.io.File;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.ArticleCollection;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 28, 2010  
 */
public class TestArticlesXML {
  public static void main(String[] args) throws Exception {
    String path  = "D:\\java\\vietspider\\startup\\src\\test\\data\\";
    File file  = new File(path);
    
    //  System.out.println(file.getCanonicalPath());
    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    file  = UtilFile.getFile("content", "-323567398.xml");
    byte [] bytes = RWData.getInstance().load(file);
    
    ArticleCollection dataCollection = XML2Object.getInstance().toObject(ArticleCollection.class, bytes);
    List<Article> articles = dataCollection.get();
    for(int i = 0; i < articles.size(); i++) {
      Article article = articles.get(i);
//      System.out.println(article.getId() + " meta " + article.getMeta());
      if(article.getContent() == null) {
//        System.out.println(article.getId() + " content null ");
      } else if(article.getContent().getContent() == null 
          || article.getContent().getContent().isEmpty()) {
        System.out.println(article.getId() + " content value null ");
      }
    }
    
    
//    file  = UtilFile.getFile("content", "1.xml");
//    bytes = RWData.getInstance().load(file);
//    Article article = XML2Object.getInstance().toObject(Article.class, bytes);
//    System.out.println(article.getId() + " : " + article.getContent());
  }
}
