/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.ContentMapper;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.bean.Relations;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
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
public class ArticleDatabases extends IDatabases {
  
  public final static short  BERKEYLEY = 0;
  public final static short  ACORN = 1;

  protected volatile static long TIMEOUT = 30*60*1000L;

  private volatile boolean execute = true;
  private volatile int maxSync = 1000;
  
  private boolean readOnly = false;
  
  protected File root;
  
//  private ArticleStorage tempStorage;
  //  private volatile boolean enterprise = false;

  protected Map<String, IArticleDatabase> holder = new ConcurrentHashMap<String, IArticleDatabase>();
  protected volatile java.util.Queue<Article> tempArticles = new ConcurrentLinkedQueue<Article>();
//  protected volatile java.util.Queue<Image> tempImages = new ConcurrentLinkedQueue<Image>();

  private SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMdd");
  
  protected DeleteDatabase deleteArticleDb;
  
  protected int maxDatatabase = 60; 
  //  private File root = UtilFile.getFolder("content/database/");
  //  private SyncArticleData sync;

  //  private boolean nlp = false;
  
  private boolean online = false;
  
  protected boolean bakup = false;

  protected short type  = BERKEYLEY; 
  
  public ArticleDatabases(File folder, boolean readOnly) {
    this.readOnly = readOnly;
    this.root = folder;
    this.bakup = true;
    init();
  }
  
  public ArticleDatabases(boolean bakup) {
    this.bakup = bakup;
    init();
  }
  
  public ArticleDatabases() {
    super(DatabaseService.DEFAULT);
    init();
  }

  private void init() {
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
        
        deleteArticleDb.saveFile();
      }
    });
    
    if(isSearchSystem()) {
      TIMEOUT = 45*60*1000L;//TIMEOUT = 2*24*60*60*1000L;
    } else if(bakup) {
      TIMEOUT = 45*60*1000L;
    }

    
//    System.out.println(" thay co get root roi "+ getRoot());
    deleteArticleDb = new DeleteDatabase(getRoot(), "delete.txt");
    //    sync = new SyncArticleData();

    SystemProperties properties = SystemProperties.getInstance();
    
    online = "true".equalsIgnoreCase(properties.getValue("online.search"));
    try {
      String value = properties.getValue("databases.max.instance");
      if(value != null) {
        maxDatatabase = Integer.parseInt(value); 
      } else {
        properties.putValue("databases.max.instance", String.valueOf(maxDatatabase), false);
      }
    } catch (Exception e) {
      maxDatatabase = 60;
      properties.putValue("databases.max.instance", String.valueOf(maxDatatabase), false);
    }

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

  @SuppressWarnings("unused")
  public void save(Image image) {
//    if(image == null) return;
//    tempImages.add(image);
  }

  public void run() {
   /* new Thread() {
      public void run() {
        try {
          //          DatabaseConverter.execute2();
          //          new RegionConverter().extract();
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);

        }

      }
    }.start();*/
    while(execute) {
      
//      if(tempStorage != null 
//          && tempStorage.isMerge()) {
//        tempStorage.merge();
//      }
      
      //detete data
      deleteArticleDb.monitor();
      
      try {
        commit();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }

      if(!online) closeExpires();

      try {
        Thread.sleep(SLEEP);
      } catch (Exception e) {
      }
    }
  }

  public synchronized void commit() throws Throwable {
    while(!tempArticles.isEmpty()) {
      Article article = tempArticles.poll();
      String id = article.getId();
      IArticleDatabase database = getDatabase(id, true, true);
      if(database == null) continue;

      long longId = Long.parseLong(id);
      
      if(article.getStatus() == Article.DELETE) {
        database.delete(longId);
        continue;
      }

      Meta meta = article.getMeta();
      Content content = article.getContent();
//      Domain domain = article.getDomain();
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
      
      if(database instanceof  ArticleDatabase) {
        ArticleDatabase articleDatabase = (ArticleDatabase) database;
        List<Image> images = article.getImages();
        for(int i = 0; i < images.size(); i++) {
          articleDatabase.saveRawImage(images.get(i));
        }
      }
        

//      SyncService.getInstance().sync(SyncArticleData.class, article);
      //      sync.sync(article);
    }
    

//    while(!tempImages.isEmpty()) {
//      Image image = tempImages.poll();
//      IArticleDatabase database = getDatabase(image.getMeta(), true, true);
//      if(database == null) continue;
//      database.save(image);
//    }
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
    Date date = Calendar.getInstance().getTime();
    for(int i = 0; i < files.length; i++) {
      IArticleDatabase database = getDatabase(date, files[i], true);
      if(database == null) continue;
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
    
    if(!(database instanceof ArticleDatabase)) return "";
    
    try {
      return ((ArticleDatabase)database).loadRawText(longId);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      return "";
    }
  }
  
  public List<Relation> loadRelations(String id)  {
    Article article = searchTempArticle(id);
    if(article != null && article.getMeta() != null) {
      return article.getRelations();
    }
    
    IArticleDatabase database = getDatabase(id, true, false);
    if(database == null) return null;
    try {
      Relations relations = database.loadRelations(id);
      if(relations == null) return null;
      return relations.getRelations();
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  
  public void delete(String id)  {
    Article article = loadArticle(id);
    if(article != null) {
      deleteArticleDb.add(article.getDomain(), id);
    } else {
      deleteArticleDb.add(null, id);
    }
   /* removeTempArticle(id);
    
    IArticleDatabase database = getDatabase(id, false, false);
    if(database == null) return;
    try {
      database.delete(Long.parseLong(id));
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }*/
  }

  
  public Meta loadMeta(String id) {
    Article article = searchTempArticle(id);
    if(article != null && article.getMeta() != null) return article.getMeta();
    
    String rawMeta = loadMetaAsRawText(id);
    if(rawMeta == null || rawMeta.length() < 1) return null;
    
    if(rawMeta.indexOf(ContentMapper.SEPARATOR) > -1) {
      article = new Article();
      ContentMapper.text2MetaData(article, rawMeta);
      return article.getMeta();
    }
//    System.out.println(id + " : "+ rawMeta.length());
    return ContentMapper.text2Meta(rawMeta);
//    Article article = loadArticle(id, Article.SIMPLE);
//    if(article != null) return article.getMeta();
//    return null;
  }
  
  public Article loadMetaData(String id) {
    Article article = searchTempArticle(id);
    if(article != null && article.getMeta() != null) return article;
    
    String rawMeta = loadMetaAsRawText(id);
    if(rawMeta == null || rawMeta.length() < 1) return null;
    
    article = new Article();
    if(rawMeta.indexOf(ContentMapper.SEPARATOR) > -1) {
      ContentMapper.text2MetaData(article, rawMeta);
    } else {
      Meta meta = ContentMapper.text2Meta(rawMeta);
      article.setMeta(meta);
      
      if(!bakup) {
        Domain domain = ArticleDatabases.getInstance().loadDomain(meta.getDomain()); 
        article.setDomain(domain);
      }
    }
    return article;
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
    
    if(!(database instanceof ArticleDatabase)) return "";
    
    try {
      return  ((ArticleDatabase)database).loadMetaAsRawText(longId);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
      return "";
    }
  }

  public Image loadImage(String id)  {
//    System.out.println(" === +++   >"+ id);
    if(id == null || id.length() < 10) return null;
    Image image = searchTempImage(id);
    if(image != null) return image;
    
    String dateValue  = id.substring(0, 8);
    Date date = null;
    try {
      date = idFormat.parse(dateValue);
    } catch (Throwable e) {
      return null;
    }
    
    IArticleDatabase database = getDatabase(date, true, false);
//    System.out.println("image data " + database);
    if(database == null) return null;
    try {
      return database.loadImage(id);
    } catch (Throwable e) {
      return null;
    }
  }

  public Article searchTempArticle(String id) {
    Iterator<Article> iterator = tempArticles.iterator();
    while(iterator.hasNext()) {
      Article article = iterator.next();
      if(id.equals(article.getId())) return article;
    }
    return null;
  }
  
  public void removeTempArticle(String id) {
    Iterator<Article> iterator = tempArticles.iterator();
    while(iterator.hasNext()) {
      Article article = iterator.next();
      if(id.equals(article.getId())) {
        iterator.remove();
        return ;
      }
    }
  }

  private Image searchTempImage(String id) {
    Iterator<Article> iterator = tempArticles.iterator();
    while(iterator.hasNext()) {
      Article article = iterator.next();
      List<Image> list = article.getImages();
      for(int i = 0; i < list.size(); i++) {
        if(id.equals(list.get(i).getId())) return list.get(i);
      }
    }
    return null;
  }

  public synchronized IArticleDatabase getDatabase(String id, boolean create, boolean make) {
    if(id == null || id.length() < 10) return null;
    id  = id.substring(0, 8);
//    String dateFolder = null;
    Date date = null;
    try {
      date = idFormat.parse(id);
//      dateFolder = CalendarUtils.getFolderFormat().format(date);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable("INDEXER", e);
      return null;
    }
    return getDatabase(date, create, make);
    
//    System.out.println(" bebe "+ id + " : "+ dateFolder);

   /* if(dateFolder == null) return null;
    File dbFolder = new File(getRoot(),  dateFolder+"/");
//    System.out.println(" bebe "+ id + " : "+ dbFolder.exists() + " : "+ dbFolder.getAbsolutePath());
    if(!make) {
      if(!dbFolder.exists()) return null;
      return getDatabase(date, dbFolder, create);
    }
    if(!dbFolder.exists()) dbFolder.mkdirs();
    return getDatabase(date, dbFolder, create);*/
  }

  public synchronized IArticleDatabase getDatabase(Date date, boolean create, boolean make) {
    String dateFolder = null;
    try {
      dateFolder = CalendarUtils.getFolderFormat().format(date);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable("INDEXER", e);
      return null;
    }

    if(dateFolder == null) return null;
    File dbFolder = new File(getRoot(),  dateFolder+"/");
    if(dbFolder.exists()) return getDatabase(date, dbFolder, create);
    
    if(Application.LICENSE != Install.PERSONAL) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      int week = calendar.get(Calendar.WEEK_OF_YEAR);
      int year = calendar.get(Calendar.YEAR);
      dbFolder = new File(getRoot(),  "week" + String.valueOf(week) + "_" + String.valueOf(year) +"/");
      if(dbFolder.exists()) return getDatabase(date, dbFolder, create);
    }
    
    if(make) dbFolder.mkdirs();
    if(!dbFolder.exists()) return null;
    return getDatabase(date, dbFolder, create);
  }

  public synchronized IArticleDatabase getDatabase(Date date, File folder, boolean create) {
    Calendar current = Calendar.getInstance();
    current.set(Calendar.DATE, current.get(Calendar.DATE) - maxDatatabase);
    
//    int dbDate = date.getDate();
//    int currentDate = current.get(Calendar.DATE);
//    System.out.println(" chjay thu ta co "+ dbDate + " : "+ currentDate);
    
    if(date != null) {
      long expire = current.getTimeInMillis() - date.getTime();
      if(expire >= 6*30*24*60*60*1000l) return null;
//      if(date != null 
//          && date.getTime() < current.getTimeInMillis()) return null;
    }
    
    
    String path = folder.getAbsolutePath();
//    if(path.indexOf("bak") < 0) new Exception().printStackTrace();
    IArticleDatabase tracker = holder.get(path);
    if(tracker != null && !tracker.isClose()) return tracker;
    if(!create) return null;
    try {
      if(readOnly) {
        if(CalendarUtils.isCurrent(date)) return null;
        tracker = new ArticleDatabase(folder.getAbsolutePath(), bakup, true) {
          protected synchronized void createRelationDb() {
            if(relationDb != null) return;
            File relationFolder = new File(folder, "relation/"); 
            if(!relationFolder.exists()) relationFolder.mkdirs();
            try {
              if(search) {
                relationDb = new CommonDatabase(relationFolder, "relation", 5*1024*1024l, true);
              } else {
                relationDb = new CommonDatabase(relationFolder, "relation", 1*1024*1024l, true);
              }
            } catch (Throwable e) {
              Application.addError(this);
              LogService.getInstance().setThrowable(e);
            }
          }
        };
      } else {
        boolean localReadOnly = false;
        if(bakup && !CalendarUtils.isCurrent(date)) localReadOnly = true;
        tracker = new ArticleDatabase(folder.getAbsolutePath(), bakup, localReadOnly);  
      }
//      if(type == BERKEYLEY) {
      
//      } else if(type == ACORN) {
//        tracker = new ArticleDatabase2(folder.getAbsolutePath());
//      }
//    } catch (RecoveryException e) {
//      LogService.getInstance().setThrowable(path, e.getCause());
//      LogService.getInstance().setThrowable(path, e);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(path, e);
    }
    if(tracker != null) holder.put(path, tracker);
    return tracker;
  }

  public void closeExpires()  {
    Iterator<Map.Entry<String, IArticleDatabase>> iterator = holder.entrySet().iterator();
    while(iterator.hasNext()) {
      IArticleDatabase tracker = iterator.next().getValue();
      long total  = System.currentTimeMillis() - tracker.getLastAccess();
      if(total >= TIMEOUT) {
        total = total/(60*1000l);
//        if(tracker instanceof ArticleDatabase) {
//          LogService.getInstance().setMessage(null,
//              String.valueOf(tracker.hashCode()) + "  is timeout - " + total + " minutes");
//        } else {
//          ArticleDatabase db = (ArticleDatabase)tracker;
//          
//          LogService.getInstance().setMessage(null, "Database " +
//              db.getFolder() + "  is timeout - " + total + " minutes");
//        }
        LogService.getInstance().setMessage(null, "Database " +
          tracker.getFolder() + "  is timeout - " + total + " minutes");
        tracker.close();
        iterator.remove();
      } 
    }
  }
  
  public void close(IArticleDatabase database)  {
    Iterator<Map.Entry<String, IArticleDatabase>> iterator = holder.entrySet().iterator();
    while(iterator.hasNext()) {
      IArticleDatabase tracker = iterator.next().getValue();
      if(tracker != database) continue;
      tracker.close();
      iterator.remove();
    }
  }

  void closes() {
    Iterator<Map.Entry<String, IArticleDatabase>> iterator = holder.entrySet().iterator();
    while(iterator.hasNext()) {
      IArticleDatabase tracker = iterator.next().getValue();
      tracker.close();
      iterator.remove();
    }
  }

  public void deleteExpireDate(File folder, int expire) {
    if(bakup) return;
    File[] files = UtilFile.listFiles(folder, new FileFilter() {
      public boolean accept(File f) {
        if (!f.isDirectory()) return false;
        return UtilFile.validate(f.getName());
      }
    });

    for (int i = expire; i < files.length; i++) {
      IArticleDatabase database = holder.get(files[i].getAbsolutePath());
      if(database != null) database.close();
      UtilFile.deleteFolder(files[i]);
    }

    if(folder.listFiles() == null 
        || folder.listFiles().length < 1) folder.delete();
  }

  public int getMaxSync() { return maxSync; }
  public void setMaxSync(int maxSync) { this.maxSync = maxSync; }

  public short getType() { return type;  }
  public void setType(short type) { this.type = type; }

  public File getRoot() {
    if(root != null) return root;
    if(bakup) {
      if(type == BERKEYLEY) {
        return UtilFile.getFolder("content/bak/database/");
      } else if(type == ACORN) {
        return UtilFile.getFolder("content/bak/database2/");
      }
      return UtilFile.getFolder("content/bak/database/");
    }
    
    if(type == BERKEYLEY) {
      return UtilFile.getFolder("content/database/");
    } else if(type == ACORN) {
      return UtilFile.getFolder("content/database2/");
    }
    return UtilFile.getFolder("content/database/");
  }
  
  private boolean isSearchSystem() {
    File folder = UtilFile.getFolder("content/temp/indexed");
    File [] fs = folder.listFiles();
    for(int i = 0; i < fs.length; i++) {
      if(fs[i].length() > 1024*1024*1024) return true;
    }
    return false;
  }
  
  public String loadIdByURL(String url) {
    File _root = IDatabases.getInstance().getRoot();

    File [] files = UtilFile.listFiles(_root);
    Date date = Calendar.getInstance().getTime();
    for(int i = 0; i < Math.min(files.length, 3); i++) {
      //    System.out.println("search url " + files[i].getAbsolutePath());
      ArticleDatabase database = (ArticleDatabase)getDatabase(date, files[i], true);
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
  
  public boolean isSearcher() { return false; }
  
  @SuppressWarnings("unused")
  public void search(MetaList metas, CommonSearchQuery query) { return; }
  
  @SuppressWarnings("unused")
  public SearchResponse search(SearchResponse searcher) { return null; }
  
  @SuppressWarnings("unused")
  public String searchForCached(SearchResponse searcher) { return null; }
  
  @SuppressWarnings("unused")
  public String loadArticleForSearch(SearchResultCollection collection) { return null; }
  
}
