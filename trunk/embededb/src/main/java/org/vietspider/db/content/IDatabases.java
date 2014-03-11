/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;

import java.io.File;
import java.util.Date;

import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 23, 2009  
 */
public abstract class IDatabases extends Thread  {
  
  private static volatile IDatabases INSTANCE;
  
  public volatile static long SLEEP = 30*1000l;

  public static synchronized IDatabases getInstance() {
    if(INSTANCE == null) createInstance();
    return INSTANCE;    
  }
  
   private synchronized static void createInstance() {
     if(INSTANCE != null) return;
     try {
       INSTANCE = (IDatabases)loadInstance("databases.type", ArticleDatabases.class);
     } catch (Throwable e) {
       e.printStackTrace();
       LogService.getInstance().setThrowable(e);
       Application.addError(e);
     }
//     SystemProperties properties = SystemProperties.getInstance();
//     String type = properties.getValue("databases.type");
//     if(type == null || type.trim().isEmpty()) {
//       LogService.getInstance().setThrowable(new NullPointerException("no database type"));
//       type = "org.vietspider.db.content.ArticleDatabases";
//       properties.putValue("databases.type", type);
//     }
//     
//     try {
//       Class<?> clazz = Class.forName(type); 
//       INSTANCE = (IDatabases)clazz.newInstance();
//     } catch (Throwable e) {
//       e.printStackTrace();
//       LogService.getInstance().setThrowable(e);
//       Application.addError(e);
//    }
   }
   
   protected IDatabases() {
   }
   
   public IDatabases(int mode) {
     DatabaseService.setMode(mode);
   }
   
   private static Object loadInstance(String label, Class<?> clazz) throws Exception {
     SystemProperties properties = SystemProperties.getInstance();
     String clazzName = properties.getValue(label);
//     System.out.println(clazzName);
     if(clazzName == null || clazzName.trim().isEmpty()) {
       clazzName  = clazz.getName();
       properties.putValue(label, clazzName, true);
     }
     
//     System.out.println("==== > "+properties.getValue("search.system"));
     
     if("true".equalsIgnoreCase(properties.getValue("search.system"))) {
       clazzName = "org.vietspider.db.solr.database.SolrDatabaseWrapper";
       properties.putValue(label, clazzName, false);
     }
     
     if("true2".equalsIgnoreCase(properties.getValue("search.system"))) {
       clazzName = "org.vietspider.solr2.SolrIndexStorage";
       properties.putValue(label, clazzName, false);
     }
     
     try {
       return Class.forName(clazzName).newInstance();
     } catch (ClassNotFoundException e) {
       LogService.getInstance().setThrowable(e);
       properties.putValue(label, clazz.getName(), true);
       return clazz.newInstance();
     } 
   }

  
   public abstract void save(Article article);
   public abstract void save(Image image);
   
//   public abstract  void commit() throws Throwable;

   public abstract Domain loadDomain(String domainId);
   public abstract Article loadArticle(String id);
   public abstract Article loadArticle(String id, short dbtype);
   public abstract Image loadImage(String id);
   public abstract String loadIdByURL(String url);
  
//   public abstract IArticleDatabase getDatabase(String id, boolean create);
   public abstract Meta loadMeta(String id);
   public abstract IArticleDatabase getDatabase(Date date, boolean create, boolean make);
   public abstract IArticleDatabase getDatabase(String id, boolean create, boolean make);
//   public abstract IArticleDatabase getDatabase(File folder, boolean create) ;
  
//   public abstract void closeExpires() ;
  
   public abstract void deleteExpireDate(File folder, int expire) ;
  
   public abstract int getMaxSync();
//   public abstract void setMaxSync(int maxSync);
  
   public abstract File getRoot();
   
   public abstract void search(MetaList metas, CommonSearchQuery query);
   
   public abstract Article loadMetaData(String id) ;
   
   public abstract SearchResponse search(SearchResponse searcher);
   
   public abstract String searchForCached(SearchResponse searcher);
   
   public abstract String loadArticleForSearch(SearchResultCollection collection) ;
   
}
