/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.seo;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import org.vietspider.bean.Article;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 20, 2010  
 */
public class LastAccessData {

  private static LastAccessData INSTANCE;

  public synchronized static LastAccessData getInstance() {
    if(INSTANCE != null) return INSTANCE;
    INSTANCE = new LastAccessData();
    return INSTANCE;
  }


  private static int MAX_ARTICLE = 250;
  public static int MAX_QUERY = 50;

//  private ConcurrentSkipListSet<Query> querySet;
  protected ArrayBlockingQueue<Long> articleQueue;


  public LastAccessData() {
    articleQueue = new ArrayBlockingQueue<Long>(MAX_ARTICLE + 50);
    /*querySet = new ConcurrentSkipListSet<Query>(new Comparator<Query>() {
      public int compare(Query o1, Query o2) {
        long t = o2.getTotal() - o1.getTotal();
        if(t > 0) return 1;
        if(t < 0) return -1;
        return 0;
      }
    });*/

    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close Query Database";}

      public void execute() {
        try {
          saveFile();
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    });

    loadInit();
  }

//  public ConcurrentSkipListSet<Query> getQuerySet() {
//    return querySet;
//  }

//  public void add(String pattern, int total) {
////    System.out.println(" ====  >"+ pattern + " : "+ total);
//    add(new Query(pattern, total));
//  }

//  public void add(Query newQuery) {
//    if(newQuery.getTotal() < 11) return;
//    while(querySet.size() > MAX_QUERY) {
//      Query query = querySet.last();
//      querySet.remove(query);
//    }
//    addToCollection(newQuery);
//  }

//  private void addToCollection(Query newQuery) {
//    Iterator<Query> iterator = querySet.iterator();
//    while(iterator.hasNext()) {
//      Query  query = iterator.next();
//      if(newQuery.getPattern().equals(query.getPattern())) {
//        iterator.remove();
//        break;
//      }
//    }
//    querySet.add(newQuery);
//  }


  private void loadInit() {
    File folder = UtilFile.getFolder("/track/search/queries/");

    //load query
   /* File file = new File(folder, "last.queries.txt");
    if(file.exists() && file.length() > 0) {
      try {
        byte [] bytes = RWData.getInstance().load(file);
        String text = new String(bytes, Application.CHARSET);
        String [] elements = text.split("\n");
        for(int i = 0; i < elements.length; i++) {
          try {
            Query query = new Query();
            query.fromString(elements[i]);
            addToCollection(query);
          } catch (Exception e) {
            LogService.getInstance().setThrowable(e);
          }
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        file.delete();
      }
    }*/
    
    //load article
    File file = new File(folder, "last.articles.txt");
    if(file.exists() && file.length() > 0) {
      try {
        byte [] bytes = RWData.getInstance().load(file);
        String text = new String(bytes, Application.CHARSET);
        String [] elements = text.split("\n");
        for(int i = 0; i < elements.length; i++) {
          try {
            addArticle(Long.parseLong(elements[i].trim()));
          } catch (Exception e) {
            LogService.getInstance().setThrowable(e);
          }
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        file.delete();
      }
    }
  }

  public void saveFile() {
    File folder = UtilFile.getFolder("/track/search/queries/");
    StringBuilder builder = new StringBuilder();

    // save query
   /* File file = new File(folder, "last.queries.txt");
    Iterator<Query> iterator = querySet.iterator();
    while(iterator.hasNext()) {
      Query  query = iterator.next();
      if(builder.length() > 0) builder.append('\n');
      builder.append(query.toString());
    }

    try {
      org.vietspider.common.io.RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
*/
    //save article
    File file = new File(folder, "last.articles.txt");
//    builder.setLength(0);
    Iterator<Long> artIterator = articleQueue.iterator();
    while(artIterator.hasNext()) {
      Long value = artIterator.next();
      if(builder.length() > 0) builder.append('\n');
      builder.append(value.toString());
    }

    try {
      RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    }catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public void addArticle(long id) {
    int size = MAX_ARTICLE - 10;
    while(articleQueue.size() > size) {
      articleQueue.poll();
    }
    articleQueue.add(id);
  }

  public ArrayBlockingQueue<Long> getArticleQueue() {
    return articleQueue;
  }
  
  public void loadArticles(MetaList metas) throws Exception {
    for(Long id : articleQueue) {
      String metaId = String.valueOf(id);
      Article article = DatabaseService.getLoader().loadArticle(metaId);
      if(article != null) metas.getData().add(article);
    }
  }

}
