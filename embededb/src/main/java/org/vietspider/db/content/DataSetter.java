/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.content;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.database.DatabaseWriter;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 8, 2007
 */
public class DataSetter implements DatabaseWriter {
  
//  private ArticleDatabases backup;

  public DataSetter() {
    IDatabases.getInstance();
//    SystemProperties properties = SystemProperties.getInstance();
//    if("true".equals(properties.getValue("database.backup"))) {
//      backup = new ArticleDatabases(true);
//    }
  }

  public void save(Image image) throws Exception {
    IDatabases.getInstance().save(image);
  }

  public void save(List<Relation> relations) throws Exception {
    if(relations == null || relations.size() < 1) return;
    Article article = new Article();
    article.setRelations(relations);
    IDatabases.getInstance().save(article);
  }

  public void save(Article article) throws Exception {
    if(article.getStatus() == Article.DELETE) {
      IDatabases.getInstance().save(article);
      return;
    }
    
    if(article.getMeta() == null 
        && article.getContent() != null) {
      IDatabases.getInstance().save(article);
      return ;
    }
    
//    Meta meta = article.getMeta();
//    Content content = article.getContent();
//    Domain domain = article.getDomain();
    IDatabases.getInstance().save(article);
//    if(BackupDatabase.getInstance().isBackup()) {
    ArticleDatabases backup = BackupDatabase.getInstance().getDatabase();
    if(backup != null) backup.save(article);
//    }
    
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
      IDatabases.getInstance().save(article);
      return;
    }
//    System.out.println(" ++++ > "+ article.getId()+ " : "+ IDatabases.getInstance().getClass());
    IDatabases.getInstance().save(article);
    ArticleDatabases backup = BackupDatabase.getInstance().getDatabase();
    if(backup != null) backup.save(article);
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
    IDatabases.getInstance().save(article);
  }

  public void set(Content content) throws Exception {
    Article article = new Article();
    article.setContent(content);
    IDatabases.getInstance().save(article);
  }

  @Override
  public void save(String metaId, String title, String desc, String content) throws Exception {
     Meta meta = IDatabases.getInstance().loadMeta(metaId);
     if(meta == null) return;
     meta.setTitle(title);
     meta.setDesc(desc);
     Article article = new Article();
     article.setMeta(meta);
     if(content != null) {
       Calendar calendar = Calendar.getInstance();
       String date = CalendarUtils.getDateFormat().format(calendar.getTime());
       Content _content = new Content(metaId, date, content);
       article.setContent(_content);
     }
     IDatabases.getInstance().save(article);
  }
  
  
  
}
