/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db2.content;

import java.io.File;

import org.vietspider.bean.Article;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.content.ArticleDatabase;
import org.vietspider.db.content.ArticleDatabases;
import org.vietspider.db.database.DatabaseReader.IArticleIterator;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 11, 2011  
 */
public class BabuDatabaseTestReading {


  public static void main(String[] args) throws Exception {
    ArticleBabuDatabases babuDatabases = 
        new ArticleBabuDatabases(new File("D:\\Program\\VietSpiderBuild19\\data\\content\\babudb\\"));
    Article article = babuDatabases.loadArticle("201205020835310000");
    System.out.println(article.getDomain().getName());
//    System.out.println(article.getMeta().getTitle());
//    System.out.println(article.getContent().getContent());
    
  }

}
