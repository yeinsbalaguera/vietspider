/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.article;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.vietspider.article.index.ArticleIndexStorage;
import org.vietspider.bean.Article;
import org.vietspider.bean.Relation;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.content.tp.TpWorkingData;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.LinkLogIO;
import org.vietspider.crawl.crepo.SessionStore;
import org.vietspider.crawl.crepo.SessionStores;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.db.StorableTempQueue;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.idm2.EIDFolder2;
import org.vietspider.db.link.track.LinkLog;
import org.vietspider.jdbc.external.ExternalDatabase;
import org.vietspider.model.Source;
import org.vietspider.model.TopArticleService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 29, 2011  
 */
public class ArticlePluginSaver extends StorableTempQueue<PluginData> implements Runnable {

  private volatile static ArticlePluginSaver INSTANCE;

  public final synchronized static ArticlePluginSaver getInstance() {
    if(INSTANCE == null) INSTANCE = new ArticlePluginSaver();
    return INSTANCE;
  }
  
  private TopicTrackingService ttservice;

//  protected TpDatabases tpDatabases;
//  private SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMdd");
  
  
  public ArticlePluginSaver() {
    super(UtilFile.getFolder("content/temp/articles/"), 1000);

    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close Article Saver";}

      public void execute() {
        storeTemp();
        ttservice.shutdown();
      }
    });

//    tpDatabases = new TpDatabases();

    ttservice = new TopicTrackingService();

    new Thread(this).start();
  }

  public void add(PluginData data)  {
    ttservice.add(data);
    queue.add(data);
  }

  public void run() {
    while(true) {
      
      ttservice.init();
      
      try {
        commit();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }

      //delete expire data
      ttservice.deleteExpire();

      try {
        Thread.sleep(15*1000L);
      } catch (Exception e) {
      }
    }
  }

  protected void commit() {
    Queue<PluginData> working = new LinkedList<PluginData>();
    try {
      load(working);
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    
    ttservice.write(working);
    //    System.out.println("chuan bi commit " + queue.size() + " : "+ working.size());

    while(!working.isEmpty()) {
      PluginData data = working.poll();
      //      System.out.println(" xu ly "+ data.getArticle().getId());
      ttservice.compute(data);

      try {
        save2Database(data);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(data.getLink().getSourceFullName(), e);
        continue;
      }
      
      if(Application.LICENSE != Install.PERSONAL) {
        ArticleIndexStorage.getInstance().save(data.getArticle());
      }
      
      ExternalDatabase.getInstance().add(data.getArticle());

//      ContentIndex contentIndex = PluginData2TpDocument.toIndexContent(data);
//      if(contentIndex != null) ContentIndexers.getInstance().index(contentIndex);

      //    DbIndexerService dbIndex = DbIndexerService.getInstance();
      //    if(dbIndex != null) dbIndex.getDbIndexEntry().save(pluginData);

//      MonitorDataSaver logSaver = SourceLogHandler.getInstance().getSaver();
//      Source cloneSource = data.cloneSource();
//      cloneSource.setCategory(data.getArticle().getDomain().getCategory());
      //    System.out.println(" ==== source cate ===>  "+ link.getSource().getCategory());
      //    System.out.println(" ==== clone cate ===>  "+ cloneSource.getCategory());
//      logSaver.updateTotalLinkAndData(cloneSource, data.getArticle().getMeta().getCalendar(), 0, 1);
      if(AlertQueue.getInstance() != null) AlertQueue.getInstance().add(data);
    }

  }

  private void load(java.util.Collection<PluginData> working) throws Throwable {
    File [] files = listTempFiles();

    if(files == null || files.length < 1) {
      //      if(queue.size() >= 1000) {
      int idx = 0;
      Iterator<PluginData> iterator = queue.iterator();
      while(iterator.hasNext()) {
        PluginData data  = iterator.next();
        working.add(data);
        queue.remove(data);
        idx++;
        if(idx >= sizeOfWorking) break;
      }
      //      }
      return;
    }

    load(files, working);
  }

  private boolean save2Database(PluginData data) throws Exception {
    Article article = data.getArticle();

    TpWorkingData tpData = data.getTpData();
    boolean duplicated = tpData != null && tpData.getDuplicates().size() > 0;

    if(duplicated) {
      article.getMeta().putProperty("duplicated", "true");
      if(isDuplicatedSource(data)) return true;
    }

    //    System.out.println(" chuan bi save " + article.getId());
    //save to databse
    try {
      DatabaseService.getSaver().save(article);
//      article.setImages(data.getImages());
//      LinkedList<Image> images = data.getImages();
//      while(!images.isEmpty()) {
//        Image image = images.poll();
//        try {
//          DatabaseService.getSaver().save(image);
//        }catch (Exception e) {
//          LogService.getInstance().setThrowable(e);
//        }
//      }

      try {
        if(tpData != null) DatabaseService.getSaver().save(tpData.getDuplicates());
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }

      try {
        if(tpData != null && tpData.getRelations().size() > 0) {
          DatabaseService.getSaver().save(tpData.getRelations());
          article.setRelations(tpData.getRelations());
          TopArticleService.getInstance().add(article);
        }
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      
      

    } catch (SQLException e) {
      Link link = data.getLink();
      LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_PLUGIN);
      String address =  link.getAddress();
      if(address.length() > 150) address = address.substring(0, 150) + "...";
      LogService.getInstance().setMessage(data.getLink().getSourceFullName(), e, address);

      try {
        DatabaseService.getDelete().deleteArticle(article.getMeta().getId());
      } catch (Exception e2) {
        LinkLogIO.saveLinkLog(link, e2, LinkLog.PHASE_PLUGIN);
        LogService.getInstance().setThrowable(data.getLink().getSourceFullName(), e2);
      }

      return false;
    } catch (Exception e) {
      Link link = data.getLink();
      LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_PLUGIN);
      String address =  link.getAddress();
      if(address.length() > 150) address = address.substring(0, 150) + "...";
      Exception e1 = new Exception(address + " " + e.toString());
      e1.setStackTrace(e.getStackTrace());
      LogService.getInstance().setMessage(data.getLink().getSourceFullName(), e1, address);
      try {
        DatabaseService.getDelete().deleteArticle(article.getMeta().getId());
      } catch (Exception e2) {
        LinkLogIO.saveLinkLog(link, e2, LinkLog.PHASE_PLUGIN);
      }
      return false;
    }

    //save id tracker
    /*if(tpData != null) {
      System.out.println(article.getId() + " dup : " + tpData.getDuplicates().size()  
          + " : " + tpData.getRelations().size());
      System.out.println(" ===================  > duplicated ");
      List<Relation> relations = tpData.getDuplicates();
      for(Relation rel : relations) {
        System.out.println(rel.getMeta() + " : "+ rel.getRelation() + " : " + rel.getPercent());
      }
      System.out.println(" ===================  > relation ");
      relations = tpData.getRelations();
      for(Relation rel : relations) {
        System.out.println(rel.getMeta() + " : "+ rel.getRelation() + " : " + rel.getPercent());
      }
    }*/
    
    if(!duplicated) {
      try {
        EIDFolder2.write(article.getDomain(), article.getMeta().getId(), Article.WAIT);
      } catch (Exception e) {
        Link link = data.getLink();
        LinkLogIO.saveLinkLog(link, e, LinkLog.PHASE_PLUGIN);
        LogService.getInstance().setThrowable(data.getLink().getSourceFullName(), e);
      }
    }
    //    else {
    //      System.out.println(" ===================  > duplicated ");
    //      System.out.println(" duplicated save " + article.getId() 
    //           + " : "+ article.getDomain().getCategory() +  " : "+ article.getDomain().getName());
    //      List<Relation> relations = tpData.getDuplicates();
    //      for(Relation rel : relations) {
    //        System.out.println(rel.getMeta() + " : "+ rel.getRelation() + " : " + rel.getPercent());
    //      }
    //    }

    try {
      LinkLogIO.saveLinkLog(data.getLink(), "done", LinkLog.PHASE_PLUGIN, article.getId());
      Source source = CrawlingSources.getInstance().getSource(data.getLink().getSourceFullName());
      SessionStore store = SessionStores.getStore(source.getCodeName());
      if(store != null) store.doneLink();
    } catch (Exception e) {
      LogService.getInstance().setMessage(data.getLink().getSourceFullName(), e, data.getLink().getAddress());
    }

    return true;
  }

  private boolean isDuplicatedSource(PluginData pluginData) {
    List<Relation> relations = pluginData.getTpData().getDuplicates();
    String host = null;
    try {
      host = new URL(pluginData.getArticle().getMeta().getSource()).getHost();
    } catch (Exception e) {
    }
    if(host == null) return false;
    for(Relation rel : relations) {
      Article dupArticle = null;
      try {
        dupArticle = DatabaseService.getLoader().loadArticle(rel.getRelation());
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      if(dupArticle == null) continue;
      String dupHost = null;
      try {
        dupHost = new URL(dupArticle.getMeta().getSource()).getHost();
      } catch (Exception e) {
      }
      if(host.equals(dupHost)) return true;
    }
    return false;
  }

}
