/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db2.content;

import java.io.File;
import java.io.FileFilter;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.ContentMapper;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.content.IArticleDatabase;
import org.vietspider.db.content.IDatabases;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 14, 2009  
 */
public class ArticleBabuDatabases extends org.vietspider.db.content.ArticleDatabases {

  public ArticleBabuDatabases(File folder) {
    super(folder, false);
  }

  public ArticleBabuDatabases(boolean bakup) {
    super(bakup);
  }

  public ArticleBabuDatabases() {
    super();
  }
  
  public void save(Article article) {
    if(article == null) return;
//    System.out.println(" ========= da chay vao day ");
    tempArticles.add(article);
//    try {
//      commit();
//    } catch (Throwable e) {
//      e.printStackTrace();
//    }
  }
  
  public String loadTempIdByURL(String url) {
    Iterator<Article> iterator = tempArticles.iterator();
    while(iterator.hasNext()) {
      Article article = iterator.next();
      Meta meta = article.getMeta();
      if(url.equals(meta.getSource())) return meta.getId();
    }
    return null;
  }
  
  public String loadIdByURL(String url) {
    File _root = IDatabases.getInstance().getRoot();

    File [] files = UtilFile.listFiles(_root);
    Date date = Calendar.getInstance().getTime();
    for(int i = 0; i < Math.min(files.length, 15); i++) {
      if(files[i].isFile()) continue;
      File configFile = new File(files[i], "config.db");
      if(!configFile.exists()) continue;
      //    System.out.println("search url " + files[i].getAbsolutePath());
      ArticleBabuDatabase database = (ArticleBabuDatabase)getDatabase(date, files[i], true);
      if(database == null) return null;
      try {
        String id = database.searchId(url);
        if(id != null) return id;
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    return null;
  }

  public synchronized void commit() throws Throwable {
    while(!tempArticles.isEmpty()) {
      Article article = tempArticles.poll();
      String id = article.getId();
      IArticleDatabase database = getDatabase(id, true, true);
//      System.out.println(" =========  >"+ database);
      if(database == null) continue;
      

      long longId = Long.parseLong(id);

      if(article.getStatus() == Article.DELETE) {
        database.delete(longId);
        continue;
      }

      Meta meta = article.getMeta();
      Content content = article.getContent();
      if(meta != null) {
        //        if(Application.LICENSE == Install.SEARCH_SYSTEM) {
        //          SyncDbService.getService().save(article);
        //        }
        database.save(article);
      } else if(content != null){
        database.save(content);
      } else if(article.getRelations() != null){
        //        System.out.println(" update relation " + article.getRelations().get(0).getMeta());
        database.save(article.getRelations());
      }

      List<Image> images = article.getImages();
//      System.out.println("images  "+ images);
      if(images == null) continue;
      ArticleBabuDatabase bdatabase = (ArticleBabuDatabase) database;
      for(int i = 0; i < images.size(); i++) {
//        System.out.println(" size of "+ images.get(i).getImage());
        bdatabase.saveRawImage(images.get(i));
      }
    }
    
    // delete old instance databases
    deleteExpire();
  }
  
  private void deleteExpire() {
    File [] files = UtilFile.listFiles(getRoot(), new FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory();
      }
    }, new Comparator<File>() {
      public int compare(File a, File b) {
        String name1 = a.getName();
        String name2 = b.getName();
        if(name1.indexOf("week") > -1
            && name2.indexOf("week") > -1) {
//          System.out.println(" xay ra "+ name1 + " : "+ name2);
          try {
            int idx1 = name1.indexOf('_');
            int year1 = Integer.parseInt(name1.substring(idx1+1));
            int idx2 = name2.indexOf('_');
            int year2 = Integer.parseInt(name2.substring(idx2+1));
            if(year1 < year2) return -1;
            if(year1 > year2) return 1;
            
            int week1 = Integer.parseInt(name1.substring(4, idx1));
            int week2 = Integer.parseInt(name2.substring(4, idx2));
//            System.out.println(week1+ " : "+ week2);
            return week2 - week1;
          } catch (Exception e) {
            LogService.getInstance().setThrowable(e);
          }
        } else  if(name1.indexOf("week") > -1
            && name2.indexOf("week") < 0) {
          return -1;
        } else   if(name1.indexOf("week") < 0
            && name2.indexOf("week") > -1) {
          return 1;
        }
        if(a.lastModified() < b.lastModified()) return -1;
        if(a.lastModified() == b.lastModified()) return 0;
        return 1;
      }
    });
    if(files == null || bakup) return;
//    System.out.println(" thay co "+ maxDatatabase);
    for(int i = maxDatatabase; i < files.length; i++) {
      UtilFile.deleteFolder(files[i]);
      LogService.getInstance().setMessage(null, 
          "BABU Database delete instance: " + files[i].getName());
    }
  }

  public String loadRawText(String id)  {
    if(deleteArticleDb.isDelete(id)) return "";

    IArticleDatabase database = getDatabase(id, true, false);
    if(database == null) return "";

    long longId = -1;
    try {
      longId = Long.parseLong(id);
    } catch (Exception e) {
      return null;
    }

    if(longId == -1) return "";

    if(!(database instanceof ArticleBabuDatabase)) return "";

    try {
      return ((ArticleBabuDatabase)database).loadRawText(longId);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      return "";
    }
  }

  public String loadMetaAsRawText(String id) {
    if(deleteArticleDb.isDelete(id)) return "";
    IArticleDatabase database = getDatabase(id, true, false);
    if(database == null) return "";

    long longId = -1;
    try {
      longId = Long.parseLong(id);
    } catch (Exception e) {
      return null;
    }

    if(longId == -1) return "";

    if(!(database instanceof ArticleBabuDatabase)) return "";

    try {
      return ((ArticleBabuDatabase)database).loadMetaAsRawText(longId);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      return "";
    }
  }
  
  public synchronized IArticleDatabase getDatabase(Date date, File folder, boolean create) {
    Calendar current = Calendar.getInstance();
    current.set(Calendar.DATE, current.get(Calendar.DATE) - maxDatatabase);
    
//    System.out.println("=== > " + date);

//    if(date != null 
//        && date.getTime() < current.getTimeInMillis()) return null;

    String path = folder.getAbsolutePath();
    
    IArticleDatabase tracker = holder.get(path);
    if(tracker != null && !tracker.isClose()) return tracker;
    
    try {
      tracker = new ArticleBabuDatabase(ArticleBabuDatabases.this, folder.getAbsolutePath());  
    } catch (Exception e) {
      LogService.getInstance().setThrowable(path, e);
    }
    
//    System.out.println("=== > tracker " + tracker);
    if(tracker != null) holder.put(path, tracker);
    return tracker;
  }
  
  public File getRoot() {
    if(root != null) return root;
    if(bakup) {
      return UtilFile.getFolder("content/bak/babudb/");
    }
    return UtilFile.getFolder("content/babudb/");
  }
  
  public Article loadMetaData(String id) {
    Article article = searchTempArticle(id);
    if(article != null && article.getMeta() != null) return article;
    
    String rawMeta = loadMetaAsRawText(id);
//    System.out.println(" ta co cai ni "+ rawMeta);
    if(rawMeta == null || rawMeta.length() < 1) return null;
    
    article = new Article();
    ContentMapper.text2MetaData(article, rawMeta);
    return article;
  }
  
  public Article loadArticle(String id, short dbtype)  {
//    System.out.println(" vao thu trong nay "+ id);
    if(deleteArticleDb.isDelete(id)) return null;
    
    Article article = searchTempArticle(id);
    if(article != null && article.getMeta() != null) return article;
    
//    article = tempStorage.loadArticle(id, dbtype);
//    if(article != null) return article;
//      System.out.println(" thay co " + article);
//      return article;
//    }
    
    
    IArticleDatabase database = getDatabase(id, true, false);
    if(database == null) return null;
    
    long longId = -1;
    try {
      longId = Long.parseLong(id);
    } catch (Exception e) {
      return null;
    }
    
    if(longId == -1) return null;
    
    try {
      if(dbtype != Article.NORMAL && database.isDelete(longId)) {
        return null;
      }

      Article newArticle = database.loadArticle(longId, dbtype);
      if(article != null && article.getContent() != null) {
        newArticle.setContent(article.getContent());
      }
      return newArticle;
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  
  public static void main(String[] args) {
    File root = new File("D:\\Temp\\babudb\\");
    File [] files = UtilFile.listFiles(root, new FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory();
      }
    }, new Comparator<File>() {
      public int compare(File a, File b) {
        String name1 = a.getName();
        String name2 = b.getName();
        if(name1.indexOf("week") > -1
            && name2.indexOf("week") > -1) {
//          System.out.println(" xay ra "+ name1 + " : "+ name2);
          try {
            int idx1 = name1.indexOf('_');
            int year1 = Integer.parseInt(name1.substring(idx1+1));
            int idx2 = name2.indexOf('_');
            int year2 = Integer.parseInt(name2.substring(idx2+1));
            if(year1 < year2) return -1;
            if(year1 > year2) return 1;
            
            int week1 = Integer.parseInt(name1.substring(4, idx1));
            int week2 = Integer.parseInt(name2.substring(4, idx2));
//            System.out.println(week1+ " : "+ week2);
            return week2 - week1;
          } catch (Exception e) {
            LogService.getInstance().setThrowable(e);
          }
        } else  if(name1.indexOf("week") > -1
            && name2.indexOf("week") < 0) {
          return -1;
        } else   if(name1.indexOf("week") < 0
            && name2.indexOf("week") > -1) {
          return 1;
        }
        if(a.lastModified() < b.lastModified()) return -1;
        if(a.lastModified() == b.lastModified()) return 0;
        return 1;
      }
    });
    
    for(int i = 0; i < files.length; i++) {
      System.out.println(files[i].getName());
    }
  }

 
}
