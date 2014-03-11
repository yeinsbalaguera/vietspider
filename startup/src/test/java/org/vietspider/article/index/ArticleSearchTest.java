/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.article.index;

import java.io.File;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.common.Application;
import org.vietspider.db.database.MetaList;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 16, 2011  
 */
public class ArticleSearchTest {
  
  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\java\\test\\vsnews\\data\\");
    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    Application.PRINT = false;

    try {
      ArticleSearchQuery query = new ArticleSearchQuery("檔時");
      MetaList list = new MetaList();
      list.setCurrentPage(1);
      ArticleIndexStorage.getInstance().search(list, query);
      
      List<Article> articles = list.getData();
      for(Article article : articles) {
        System.out.println(article.getMeta().getPropertyValue("hl.title"));
        System.out.println(article.getMeta().getPropertyValue("hl.desc"));
        System.out.println("==================================");
      }

//      System.out.println(list.getData().size());
    } finally {
      try {
        Thread.sleep(3*1000);
      } catch (Exception e) {
      }
      System.exit(0);
    }
  }
}
