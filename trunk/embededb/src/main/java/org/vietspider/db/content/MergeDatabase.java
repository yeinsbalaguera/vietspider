/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;

import java.io.File;

import org.vietspider.bean.Article;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.database.DatabaseReader.IArticleIterator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 23, 2009  
 */
public class MergeDatabase {
  
  public void merge() throws Throwable {
    if(!(IDatabases.getInstance() instanceof EntireDatabase)) return;
    File file = UtilFile.getFolder("content/database/");
    File [] files = file.listFiles();
    ArticleDatabases old = new ArticleDatabases();
    for(int i = 0; i < files.length; i++) {
      LogService.getInstance().setMessage(null, "merge data from "+ files[i].getAbsolutePath());
      IArticleDatabase db  = old.getDatabase(null, files[i], true);
      IArticleIterator iterator = db.getIterator();
      while(iterator.hasNext()) {
        Article article  = iterator.next(Article.EXPORT);
        IDatabases.getInstance().save(article);
      }
    }
    old.closes();
    LogService.getInstance().setMessage(null, "Finished merging data!");
  }
  
}
