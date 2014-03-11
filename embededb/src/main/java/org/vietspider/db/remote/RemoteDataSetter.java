/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.remote;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.content.BackupDatabase;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.DatabaseWriter;
import org.vietspider.db.sync.SyncService;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
public class RemoteDataSetter implements DatabaseWriter {

//  private ArticleDatabases backup;

  public RemoteDataSetter() {
    DatabaseService.setMode(DatabaseService.REMOTE);
    try {
      SyncService.getInstance().createHandler(SyncArticleData.class);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      System.exit(0);
    }
    SystemProperties.getInstance().putValue("database.backup", "true", false);
    
//    if("true".equals(SystemProperties.getInstance().getValue("database.backup"))) {
//      backup = new ArticleDatabases(true);
//    }
  }

  public void save(Image image) throws Exception {
    SyncService.getInstance().sync(SyncImageData.class, image);
  }

  public void save(List<Relation> relations) throws Exception {
    if(relations == null || relations.size() < 1) return;
    Article article = new Article();
    article.setRelations(relations);
    article.setSaveType(Article.SAVE_RELATION);
    SyncService.getInstance().sync(SyncArticleData.class, article);
  }
  
  public void save(Article article) throws Exception {
    if(article.getStatus() == Article.DELETE) {
      SyncService.getInstance().sync(SyncArticleData.class, article);
      return;
    }
//    Meta meta = article.getMeta();
//    Content content = article.getContent();
//    Domain domain = article.getDomain();
//    NLPRecord nlpRecord = null;
//    if(nlp) {
//      nlpRecord = NLPExtractor.getInstance().extract(meta, domain, content);
//      article.setNlpRecord(nlpRecord);
//    }
    
    SyncService.getInstance().sync(SyncArticleData.class, article);
    BackupDatabase.getInstance().getDatabase().save(article);
    
    List<Image> images = article.getImages();
    for(int i = 0; i < images.size(); i++) {
      try {
        save(images.get(i));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  public void saveAs(Article article) throws Exception {
    if(article.getStatus() == Article.DELETE) {
      SyncService.getInstance().sync(SyncArticleData.class, article);
      return;
    }
    SyncService.getInstance().sync(SyncArticleData.class, article);
    BackupDatabase.getInstance().getDatabase().save(article);
  }

  @Deprecated()
  @SuppressWarnings("unused")
  public void save(Meta meta, Domain domain, Content content) throws Exception {
    throw new UnsupportedOperationException();
    /* Article article = new Article();
    article.setMeta(meta);
    article.setContent(content);
    article.setDomain(domain);
    NLPRecord nlpRecord = null;
    if( IDatabases.getInstance().isNLP()) {
      nlpRecord = NLPExtractor.getInstance().extract(meta, domain, content);
      article.setNlpRecord(nlpRecord);
    }
    IDatabases.getInstance().save(article);*/
  }

  public void save(Relation relation) throws Exception {
    Article article = new Article();
    List<Relation> relations = new ArrayList<Relation>();
    relations.add(relation);
    article.setRelations(relations);
    article.setSaveType(Article.SAVE_RELATION);
    SyncService.getInstance().sync(SyncArticleData.class, article);
  }

  public void set(Content content) throws Exception {
    Article article = new Article();
    article.setContent(content);
    article.setSaveType(Article.SAVE_CONTENT);
    SyncService.getInstance().sync(SyncArticleData.class, article);
  }
  
  @SuppressWarnings("unused")
  public void save(String metaId, String title, String desc, String content) throws Exception {
    
  }
  
}
