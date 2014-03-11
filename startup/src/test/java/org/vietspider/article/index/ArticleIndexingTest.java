/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.article.index;

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
public class ArticleIndexingTest extends Thread {

  public ArticleIndexingTest() {
    this.start();
  }

  public void run() {
    try {
      index();
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }

    try {
      Thread.sleep(15*1000l);
    } catch (Exception e) {
    }
    System.exit(0);
  }
  
  private void index () throws Throwable {
    File folder = UtilFile.getFolder("content/database");
    File [] files = folder.listFiles();
//    for(int i = 0; i < Math.min(files.length, 2); i++) {
    for(int i = 0; i < files.length; i++) {
      if(files[i].isDirectory()) index(files[i]);
    }
  }


  private void index(File folder) throws Throwable {
    LogService.getInstance().setMessage(null, "Article index " + folder.getAbsolutePath());
    ArticleDatabases databases = (ArticleDatabases)ArticleDatabases.getInstance();
    if(databases == null) return;
    ArticleDatabase database = (ArticleDatabase)databases.getDatabase(null, folder, true);
    //    System.out.println(folder.getAbsolutePath() + " : " + database);
    if(database == null) return;
    
    IArticleIterator iterator = database.getIterator();
    int counter = 0;
    while(iterator.hasNext()) {
      Article article = iterator.next(Article.NORMAL);
      
      ArticleIndexStorage indexer = ArticleIndexStorage.getInstance();
      if(indexer == null) return;
      indexer.save(article);
      
      counter++;
      
      if(counter%100 == 0) {
        try {
          Thread.sleep(10*1000l);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      
      if(counter%100 == 0) {
        LogService.getInstance().setMessage(null, "Article index " + counter + " articles.");
      }
    }
    
    database.close();
    
    try {
      Thread.sleep(30*1000);
    } catch (Exception e) {
    }
    


    LogService.getInstance().setMessage(null, "Article index " + counter + " articles.");
    //    databases.close(database);
  }
  
  public static void main(String[] args) throws Exception {
//    File file = new File("D:\\VietSpider Build 18\\data\\");
//    File file  = new File("D:\\java\\test\\vsnews\\data\\");
    File file  = new File("D:\\Releases\\Cuc ATVSTP\\VietSpider3_19_Enterprise\\data");
    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    Application.PRINT = false;
    
    new ArticleIndexingTest();
  }

}
