/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.CommonSearchQuery;
import org.vietspider.index.SearchResponse;
import org.vietspider.index.SearchResultCollection;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 14, 2009  
 */
public class EntireDatabase extends IDatabases {

  private static volatile EntireDatabase INSTANCE;

  public static synchronized EntireDatabase getInstance() {
    if(INSTANCE == null) {
      INSTANCE  = new EntireDatabase();
    }
    return INSTANCE;    
  }

  private volatile boolean execute = true;
  private volatile long sleep = 10*1000l;
  private volatile int maxSync = 1000;
  
  private ArticleDatabase database;

  protected volatile java.util.Queue<Article> tempArticles = new ConcurrentLinkedQueue<Article>();
  protected volatile java.util.Queue<Image> tempImages = new ConcurrentLinkedQueue<Image>();

//  private boolean online = false;

  public EntireDatabase() {
    super(DatabaseService.ENTIRE);
    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close Article Database";}

      public void execute() {
        execute = false;
        try {
          commit();
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);
        }
        //        sync.close();
        closes();
      }
    });
    
    //    sync = new SyncArticleData();

//    SystemProperties properties = SystemProperties.getInstance();
//    online = "true".equalsIgnoreCase(properties.getValue("online.search"));

    int excutorSize = CrawlerConfig.EXCUTOR_SIZE;
    if(excutorSize <= 5) {
      maxSync = 100;
    } else if(excutorSize > 5 && excutorSize <= 10) {
      maxSync = 200;
    } else if(excutorSize > 10 && excutorSize <= 15) {
      maxSync = 500;
    } else if(excutorSize > 15 && excutorSize <= 20) {
      maxSync = 700;
    } else if(excutorSize > 20 && excutorSize <= 25) {
      maxSync = 1000;
    } else if(excutorSize > 25 && excutorSize <= 30) {
      maxSync = 1200;
    } else {
      maxSync = 1500;
    }
    //    nlp = "true".equalsIgnoreCase(properties.getValue("nlp"));

    this.start();
  }

  public void save(Article article) {
    if(article == null) return;
    tempArticles.add(article);
  }

  public void save(Image image) {
    if(image == null) return;
    tempImages.add(image);
  }

  public void run() {
    try {
      database = new ArticleDatabase(getRoot().getAbsolutePath(), false, false) {
        public void init() throws Exception {
          max = IDatabases.getInstance().getMaxSync();

          search  = "true".equalsIgnoreCase(SystemProperties.getInstance().getValue("search.system"));
          if(!search && CrawlerConfig.EXCUTOR_SIZE >= 15) search = true; 

          if(!this.bakup) { 
            File domainFolder = new File(folder, "domain/");
            if(!domainFolder.exists()) domainFolder.mkdirs();
            if(search) {
              domainDb = new CommonDatabase(domainFolder, "domain", 50*1024*1024l, readOnly);
            } else {
              domainDb = new CommonDatabase(domainFolder, "domain", 20*1024*1024l, readOnly);
            }
          }

          File metaFolder = new File(folder, "meta/"); 
          if(!metaFolder.exists()) metaFolder.mkdirs();
          //    if(isEnterprise) {
          if(search) {
            metaDb = new CommonDatabase(metaFolder, "meta", 100*1024*1024l, readOnly);
          } else {
            metaDb = new CommonDatabase(metaFolder, "meta", 50*1024*1024l, readOnly);
          }

          //    } else {
          //      metaDb = new CommonDatabase(metaFolder, "meta", 1*1024*1024l);
          //    }

          File contentFolder = new File(folder, "content/"); 
          if(!contentFolder.exists()) contentFolder.mkdirs();
          //    if(isEnterprise) {
          if(search) {
            contentDb = new CommonDatabase(contentFolder, "content", 100*1024*1024l, readOnly);
          } else {
            contentDb = new CommonDatabase(contentFolder, "content", 50*1024*1024l, readOnly);
          }
          
          if(!bakup) {
            File urlFolder = new File(folder, "url/"); 
            if(!urlFolder.exists()) urlFolder.mkdirs();
            if(search) {
              urlDb = new UrlDatabase(urlFolder, "url", 30*1024*1024l);
            } else {
              urlDb = new UrlDatabase(urlFolder, "url", 20*1024*1024l);
            }
          }

        }
        
        protected synchronized void createImageRawDb() {
          if(imageRawDb != null) return;
          File imageRawFolder = new File(folder, "image_raw/"); 
          if(!imageRawFolder.exists()) imageRawFolder.mkdirs();

          try {
            if(search) {
              imageRawDb = new ImageDatabase(imageRawFolder, "image_raw", 1024*1024l);
            } else {
              imageRawDb = new ImageDatabase(imageRawFolder, "image_raw", 100*1024*1024l);
            }
          } catch (Throwable e) {
            Application.addError(this);
            LogService.getInstance().setThrowable(e);
          }
        }

        protected synchronized  void createImageDb() {
          if(imageDb != null) return;
          File imageFolder = new File(folder, "image/"); 
          if(!imageFolder.exists()) imageFolder.mkdirs();
          try {
            if(search) {
              imageDb = new ImageDatabase(imageFolder, "image", 10*1024*1024l);
            } else {
              imageDb = new ImageDatabase(imageFolder, "image", 5*1024*1024l);
            }
          } catch (Throwable e) {
            Application.addError(this);
            LogService.getInstance().setThrowable(e);
          }
        }
      };
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      System.exit(-1);
    }
    
    if(!database.getIterator().hasNext()) {
      new Thread() {
        public void run (){
          try {
            new MergeDatabase().merge();
          } catch (Throwable e) {
            LogService.getInstance().setThrowable(e);
          }
        }
      }.start();
    }
    
    while(execute) {
      try {
        commit();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }

//      if(!online) closeExpires();

      try {
        Thread.sleep(sleep);
      } catch (Exception e) {
      }
    }
  }

  public synchronized void commit() throws Throwable {
    while(!tempArticles.isEmpty()) {
      Article article = tempArticles.poll();
      String id = article.getId();
      
      long longId = Long.parseLong(id);

      if(article.getStatus() == Article.DELETE) {
        database.delete(longId);
        continue;
      }

      Meta meta = article.getMeta();
      Content content = article.getContent();
      Domain domain = article.getDomain();
      if(meta != null) {
//        if(Application.LICENSE == Install.SEARCH_SYSTEM) {
//          SyncDbService.getService().save(article);
//        }
        database.save(article);
      } else if(content != null){
        database.save(content);
      } else if(article.getRelations() != null){
        database.save(article.getRelations());
      }

//      SyncService.getInstance().sync(SyncArticleData.class, article);
    }

    while(!tempImages.isEmpty()) {
      Image image = tempImages.poll();
      database.save(image);
    }
  }

  public Domain loadDomain(String domainId)  {
    File folder = getRoot();
    File[] files = UtilFile.listFiles(folder, new FileFilter() {
      public boolean accept(File f) {
        if (!f.isDirectory()) return false;
        return UtilFile.validate(f.getName());
      }
    });
    Long id = Long.parseLong(domainId); 
    for(int i = 0; i < files.length; i++) {
      try {
        Domain domain = database.loadDomain(id);
        if(domain != null) return domain;
      } catch (Throwable e) {
        LogService.getInstance().setMessage(null, e.toString());
      }
    }
    return null;
  }
  
  public Article loadArticle(String id) {
    return loadArticle(id, Article.NORMAL);
  }

  public Article loadArticle(String id, short dbtype)  {
    if(database == null) return null;
    Article article = searchTempArticle(id);
    if(article != null && article.getMeta() != null) return article;
    
    try {
      long longId = Long.parseLong(id);
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

  public Image loadImage(String id)  {
    if(database == null) return null;
    Image image = searchTempImage(id);
    if(image != null) return image;
    try {
      return database.loadImage(id);
    } catch (Throwable e) {
      return null;
    }
  }

  private Article searchTempArticle(String id) {
    Iterator<Article> iterator = tempArticles.iterator();
    while(iterator.hasNext()) {
      Article article = iterator.next();
      if(id.equals(article.getId())) return article;
    }
    return null;
  }

  private Image searchTempImage(String id) {
    Iterator<Image> iterator = tempImages.iterator();
    while(iterator.hasNext()) {
      Image image = iterator.next();
      if(id.equals(image.getId())) return image;
    }
    return null;
  }


  void closes() {
    database.close();
  }

  @SuppressWarnings("unused")
  public void deleteExpireDate(File folder, int expire) {
   
  }

  public int getMaxSync() { return maxSync; }
  public void setMaxSync(int maxSync) { this.maxSync = maxSync; }

  public short getType() { return 0; }
  @SuppressWarnings("unused")
  public void setType(short type) {  }

  public File getRoot() {
    return UtilFile.getFolder("content/database3/");
  }
  
  public Meta loadMeta(String id) {
    Article article = loadArticle(id, Article.SIMPLE);
    if(article != null) return article.getMeta();
    return null;
  }

  @SuppressWarnings("unused")
  public IArticleDatabase getDatabase(Date date, boolean create, boolean make) {
    return database;
  }
  
  @SuppressWarnings("unused")
  public IArticleDatabase getDatabase(String id, boolean create, boolean make) {
    return database;
  }

  @SuppressWarnings("unused")
  public IArticleDatabase getDatabase(File folder, boolean create) {
    return database;
  }
  
  public String loadIdByURL(String url) {
    try {
      return  database.searchId(url);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }
  
  public boolean isSearcher() { return false; }
  
  @SuppressWarnings("unused")
  public void search(MetaList metas, CommonSearchQuery query) { return; }
  
  @SuppressWarnings("unused")
  public SearchResponse search(SearchResponse searcher) { return null; }
  
  @SuppressWarnings("unused")
  public String searchForCached(SearchResponse searcher) { return null; }

  @SuppressWarnings("unused")
  public String loadArticleForSearch(SearchResultCollection collection) { return null; }

  @Override
  @SuppressWarnings("unused")
  public Article loadMetaData(String id) { return null;   }
  
}
