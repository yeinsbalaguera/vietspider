/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db2.content;

import java.io.File;
import java.util.Calendar;

import org.vietspider.bean.Article;
import org.vietspider.common.Application;
import org.vietspider.db.content.ArticleDatabases;
import org.vietspider.db.content.IArticleDatabase;
import org.vietspider.db.database.DatabaseReader.IArticleIterator;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 11, 2011  
 */
public class Bak2DatabaseTest {

  public static void main(String[] args) throws Throwable {
//    File file = new File("D:\\VietSpider Build 18\\data\\");
//    File file  = new File("D:\\java\\test\\vsnews\\data\\");
    File file  = new File("D:\\Program\\vssearch\\data\\");
    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    Application.PRINT = false;
    
    ArticleDatabases articleDatabases = new ArticleDatabases(true);
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DATE, 8);
    calendar.set(Calendar.MONTH, 3);
    IArticleDatabase db = articleDatabases.getDatabase(calendar.getTime(), true, false);
    IArticleIterator iterator = db.getIterator();
    while(iterator.hasNext()) {
      Article article = iterator.next(Article.SIMPLE);
      System.out.println(article.getId());
    }
    
  }

}
