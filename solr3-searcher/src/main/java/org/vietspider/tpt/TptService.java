/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tpt;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.bean.Article;
import org.vietspider.bean.Relation;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.content.BackupDatabase;
import org.vietspider.db.remote.SyncArticleData;
import org.vietspider.db.sync.SyncService;
import org.vietspider.solr2.SolrIndexStorage;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2011  
 */
public class TptService extends Thread {

  private static TptService service;

  public synchronized final static TptService getInstance() {
    if(service == null) service = new TptService();
    return service;
  }

  TptSolrDataIO solr;
  private DuplicateComputor nlpComputor;

  protected volatile Queue<Article> tempArticles;

  private boolean execute = true;

  public TptService()  {
    super();
    tempArticles = new ConcurrentLinkedQueue<Article>();
    try {
      solr = new TptSolrDataIO();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      System.exit(0);
    }

    nlpComputor = new DuplicateComputor(this);

    Application.addShutdown(new Application.IShutdown() {
      public String getMessage() { return "Close Tpt Service";}
      public void execute() {
        execute = false; 
        solr.close();
      }
    });
    
//    new AutoPriceIndexing();

//    new InitialAutoIndexing().start();
    this.start();
  }

  public void run() {
    while(execute) {

      compute();

      try {
        //      System.out.println(" truoc ========  >"+ tempIndexs.size());
        if(solr.writer.isCommit()) solr.writer.commit(true, true);
        //      System.out.println(" sau ========  >"+ tempIndexs.size());
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        solr.writer.optimize();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        Thread.sleep(2*1000l);
      } catch (Exception e) {
      }

    }
  }

  private void compute() {
    try {
      //      System.out.println(" dang chay roi "+ tempArticles.size());
      while(!tempArticles.isEmpty()) {
        Article article = tempArticles.poll();
        

//        IDVerifiedStorages.save("tpt", article);
        String duplicate = nlpComputor.search(article);
        
        if(article.getStatus() == Article.DELETE) continue;
        
//        if("true".equals(article.getMeta().getPropertyValue("owner"))) {
//          LogService.getInstance().setMessage(null, "================ > owner: "+ article.getId());
//        }

        if(duplicate == null) {
          //call to search index article
          
          //main function
//          IDVerifiedStorages.save("sync", article);
          
//          PriceIndexDatabases.getInstance().save(article);
          
          //remove test
          if("81".equals(SystemProperties.getInstance().getValue("web.port"))) {
            SolrIndexStorage.getInstance().save(article);
          } else {
            SyncService.getInstance().sync(SyncArticleData.class, article);
          }

          try {
            TptIndex index = solr.createSolrIndex(article);
            solr.writer.save(index);
          } catch (Exception e) {
            LogService.getInstance().setThrowable(e);
          }
          continue;
        }
        
//        IDVerifiedStorages.save("duplicate", article);
        
        if(BackupDatabase.getInstance() == null
            || BackupDatabase.getInstance().getDatabase() == null) continue;

        //        System.out.println(" ==== >" + duplicate  + " : "+ article.getId());
        
        Relation relation = new Relation();
        relation.setMeta(duplicate);
        relation.setRelation(article.getId());
        relation.setPercent(100);

        List<Relation> relations = new ArrayList<Relation>();
        relations.add(relation);

        Article article2 = new Article();
        article2.setId(duplicate);
        article2.setRelations(relations);

        BackupDatabase.getInstance().getDatabase().save(article2);
      }

    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public void put(Article article) {
    if(solr == null 
        || article.getNlpRecord() == null) return;
    if(article.getContent() == null) return;
    tempArticles.add(article);
  }


}
