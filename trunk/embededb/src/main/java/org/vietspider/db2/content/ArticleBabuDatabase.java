/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db2.content;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import static org.vietspider.bean.ContentMapper.*;
import static org.vietspider.db2.content.CommonBabuDatabase.*;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.MetaRelation;
import org.vietspider.bean.Relation;
import org.vietspider.bean.Relations;
import org.vietspider.cache.InmemoryCache;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DatabaseReader.IArticleIterator;
import org.xtreemfs.babudb.api.database.DatabaseInsertGroup;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2009  
 */
public class ArticleBabuDatabase implements org.vietspider.db.content.IArticleDatabase {

  protected CommonBabuDatabase database;

  protected volatile long lastAccess = System.currentTimeMillis();
  protected volatile boolean isClose = false;

  protected volatile int counter = 0;
  protected volatile int max = 100;

  protected boolean search = false;
  protected boolean nlp = false;
  protected String folder;

  protected File imageFolder;

  protected InmemoryCache<String, Article> articleCached;
  
  private ArticleBabuDatabases databases;

  public ArticleBabuDatabase(ArticleBabuDatabases databases, String path) throws Exception {
    this.databases = databases;
    this.folder = path;
//    System.out.println(" babu database load from "+ path);
    articleCached = new InmemoryCache<String, Article>("article.database", 100);
    articleCached.setLiveTime(5*60);
    init();
  }

  protected void init() throws Exception {
    max = databases.getMaxSync();

    nlp  = "true".equalsIgnoreCase(SystemProperties.getInstance().getValue("nlp"));

    File databaseFolder = new File(folder); 
    if(!databaseFolder.exists()) databaseFolder.mkdirs();

    this.imageFolder = new File(databaseFolder, "images/");
    this.imageFolder.mkdirs();

    database = new CommonBabuDatabase(databaseFolder, "articles");
  }

  public String getFolder() { return folder; }

  public void delete(long id) throws Throwable { 
    Article article = loadSimpleArticle(id);
    database.delete(URL, article.getMeta().getSource());
    database.delete(new int[]{META, CONTENT}, id);
  }

  public boolean isDelete(long id) throws Throwable { 
    return !database.contains(META, id); 
  }

  public String searchId(String url) throws Throwable {
    byte [] bytes = database.load(URL, url.getBytes());
    if(bytes == null) return null;
    return new String(bytes);
  }

  public boolean contains(long id) {
    if(isClose) return true;
    return database.contains(META, id);
  }

  public void save(Article article) throws Throwable {
    Meta meta = article.getMeta();
    if(meta.getTime() == null) {
      throw new IllegalArgumentException("Meta contains invalid data!");
    }
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    long id  = Long.parseLong(meta.getId());
    
    DatabaseInsertGroup group =  database.createInsertGroup();
    byte[] keys = database.toKeys(id);
    //support mapping relations, images to meta data
    byte[] data = metaData2Text(article).getBytes(Application.CHARSET);
    data = database.compress(data);
    group.addInsert(META, keys, data);
   
    Content content = article.getContent();
    if(content != null) {
      data = content.getContent().getBytes(Application.CHARSET);
      data = database.compress(data);
      group.addInsert(CONTENT, keys, data);
    }
    
    keys = meta.getSource().getBytes();
    data = meta.getId().getBytes();
    group.addInsert(URL, keys, data);
    
    database.insert(group);
  }

  void sync() throws Throwable {
  }

  public void save(Content content) throws Throwable  {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    long id  = Long.parseLong(content.getMeta());
    database.save(CONTENT, id, content.getContent());
  }

  @SuppressWarnings("unused")
  public void save(Relation relation) throws Throwable  {
    throw new Exception ("Unsupport operation!");
  }

  @SuppressWarnings("unused")
  public void save(Image image) throws Throwable  {
    throw new Exception ("Unsupport operation!");
  }

  public void saveRawImage(Image image) throws Throwable  {
    if(isClose) throw new CloseDatabaseException();
    lastAccess = System.currentTimeMillis();
    if(image.getImage() == null || image.getImage().length < 1) return;
    File file = new File(imageFolder, image.getName());
    RWData.getInstance().save(file, image.getImage());
  }

  public void save(List<Relation> list) throws Throwable  {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    if(list.size() < 1) return;
    Relation relation = list.get(0); 
    
    long id = Long.valueOf(relation.getMeta());
    Article article = loadSimpleArticle(id);
    if(article == null) return ;
    
    List<Relation> relations = article.getRelations();
    if(relations == null) {
      relations = new ArrayList<Relation>();
      article.setRelations(relations);
    }
    for(int i = 0; i < list.size(); i++) {
      relations.add(list.get(i));
    }

    try {
      database.save(META, id, metaData2Text(article));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  private Relations loadRelations(long id) throws Throwable {
    Article article = loadSimpleArticle(id);
    if(article == null) return null ;

    Relations relations = new Relations(String.valueOf(id));
    relations.setRelations(article.getRelations());

    return relations;
  }

  public Article loadArticle(String metaId) throws Throwable {
    return loadArticle(Long.parseLong(metaId), Article.NORMAL);
  }

  public Article loadArticle(long id, short type) throws Throwable {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();

    String metaId = String.valueOf(id);
//    new Exception().printStackTrace();
//    System.out.println(" load article "+ metaId);
    Article article = articleCached.getCachedObject(metaId);
//        System.out.println("==== > " +metaId + " : " + article);
    if(article != null) {
      if(type != Article.META
          && article.getContent() == null) {
        loadContent(article, id);
      }
      return article;
    }

    article = loadSimpleArticle(id);
    if(article == null) return null;
    
    if(type != Article.META) loadContent(article, id);

    if(type == Article.NORMAL) {
      loadMetaRelation2(article);
    }

    articleCached.putCachedObject(metaId, article);
//    if(!article.getId().equals(metaId)) {
//      System.out.println(" sai roi na "+ metaId + " : "+ article.getId());
//    }

    return article;
  }

  private void loadContent(Article article, long id) throws Throwable {
    byte [] bytes = database.load(CONTENT, id);
    if(bytes == null) return;
    String text = new String(bytes, Application.CHARSET);
    Content content = new Content();
    content.setMeta(String.valueOf(id));
    content.setContent(text);
    try {
      SimpleDateFormat dateTimeFormat = CalendarUtils.getDateTimeFormat();
      SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
      Meta meta = article.getMeta();
      if(meta.getTime() != null) {
        content.setDate(dateFormat.format(dateTimeFormat.parse(meta.getTime())));
      }
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    article.setContent(content);
  }

  public String loadMetaAsRawText(long id) throws Throwable {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();

    byte [] bytes = database.load(META, id);
    if(bytes == null) return null;

    return new String(bytes, Application.CHARSET);
  }

  public String loadRawText(long id) throws Throwable {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();

    StringBuilder builder = new StringBuilder();

    byte [] bytes = database.load(META, id);
    if(bytes == null) return null;

    String metaText = new String(bytes, Application.CHARSET);
    builder.append(metaText);
    builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);

    bytes = database.load(CONTENT, id);
    if(bytes == null) {
      builder.append("content is null");
      builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);
    } else {
      builder.append(new String(bytes, Application.CHARSET));
      builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);
    }

    return builder.toString();
  }

  public Meta loadMeta(String metaId) throws Throwable {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    Article article = loadSimpleArticle(metaId);
    return article.getMeta();
  }

  public Relations loadRelations(String metaId) throws Throwable {
    if(isClose) throw new CloseDatabaseException ();
    return loadRelations(Long.parseLong(metaId));
  }

  public void loadMetaRelation(Article article, long id) throws Throwable {
    //    System.out.println(" chuan bi load "+ id);
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    Relations relations = loadRelations(id);
    List<Relation> list = relations.getRelations();
    article.setRelations(list);
    loadMetaRelation2(article);
  }

  public void loadMetaRelation2(Article article) throws Throwable {
    List<MetaRelation> values = new ArrayList<MetaRelation>();
    List<Relation> list = article.getRelations();
    if(list == null) return;
    for(int i = 0; i < Math.min(list.size(), 8); i++) {
      //      long relId = Long.parseLong(list.get(i).getRelation());
      Relation relation = list.get(i);
      if(relation == null) continue;
      String txtRelId = relation.getRelation();

      Article article2 = databases.loadMetaData(txtRelId);
      if(article2 == null) continue;
      Meta meta = article2.getMeta();
      
      if(meta.getSource() == null) continue;

      MetaRelation metaRel = new MetaRelation();
      metaRel.setId(relation.getRelation());
      metaRel.setTitle(meta.getTitle());
      metaRel.setDes(meta.getDesc());
      metaRel.setImage(meta.getImage());
      metaRel.setTime(meta.getTime());
      metaRel.setSource(meta.getSource());
      try {
        SimpleDateFormat dateTimeFormat = CalendarUtils.getDateTimeFormat();
        SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
        metaRel.setDate(dateFormat.format(dateTimeFormat.parse(meta.getTime())));
      } catch (Exception e) {
      }

      Domain domain = article2.getDomain();
      if(domain != null) {
        metaRel.setName(domain.getName());
      } else {
        URL url = new URL(metaRel.getSource());
        metaRel.setName(url.getHost());
      }

      metaRel.setPercent(list.get(i).getPercent());
      values.add(metaRel);
    }
    article.setMetaRelations(values);
    //    return values;.
  }

  public List<MetaRelation> loadShortMetaRelation(long id) throws Throwable {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    List<MetaRelation> values = new ArrayList<MetaRelation>();
    Relations relations = loadRelations(id);
    List<Relation> list = relations.getRelations();

    for(int i = 0; i < Math.min(list.size(), 6); i++) {
      String txtRelId = list.get(i).getRelation();
      
      Article article = databases.loadMetaData(txtRelId);
      if(article == null) continue;

      Meta meta = article.getMeta();

      MetaRelation metaRel = new MetaRelation();
      metaRel.setId(list.get(i).getRelation());
      metaRel.setTitle(meta.getTitle());
      metaRel.setDes(meta.getDesc());
      metaRel.setImage(meta.getImage());
      metaRel.setTime(meta.getTime());
      metaRel.setSource(meta.getSource());
      try {
        SimpleDateFormat dateTimeFormat = CalendarUtils.getDateTimeFormat();
        SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
        metaRel.setDate(dateFormat.format(dateTimeFormat.parse(meta.getTime())));
      } catch (Exception e) {
      }
      
      try {
        Domain domain = article.getDomain(); 
        if(domain != null) {
          metaRel.setName(domain.getName());
        } else {
          URL url = new URL(metaRel.getSource());
          metaRel.setName(url.getHost());
        }
      } catch (Exception e) {
      }
      metaRel.setPercent(list.get(i).getPercent());
      values.add(metaRel);
    }

    for(int i = 6; i < list.size(); i++) {
      values.add(null);
    }

    return values;
  }

  public Image loadImage(String id) throws Throwable  {
    int idx = id.indexOf('.');
    if(idx < 0) return null;
    String metaId = id.substring(0, idx);
    
    Article article = loadSimpleArticle(metaId);
    if(article == null) return null;
    
    List<Image> images = article.getImages();
    if(images == null) return null;
    for(int i = 0; i < images.size(); i++) {
      if(!id.equals(images.get(i).getId())) continue;
      Image image = images.get(i);
      File file = new File(imageFolder, image.getName());
      if(file.exists()) {
        image.setImage(RWData.getInstance().load(file));
      }
      return image;
    }
    return null;
  }

  @SuppressWarnings("unused")
  public Domain loadDomain(long id) throws Throwable  {
    throw new Exception ("Unsupport operation!");
  }
  
  private Article loadSimpleArticle(String metaId) throws Throwable{
    return loadSimpleArticle(Long.valueOf(metaId));
  }
  
  private Article loadSimpleArticle(long metaId) throws Throwable{
    byte [] bytes = database.load(META, metaId);
    if(bytes == null) return null;
    String metaData = new String(bytes, Application.CHARSET);
//    System.out.println(metaId + " : "+ metaData);
    if(metaData.indexOf(SEPARATOR) < 0) return null;
    Article article = new Article();
    text2MetaData(article, metaData);
    return article;
  }

  public long getLastAccess() { return lastAccess; }

  public void refreshLastAccess() { lastAccess = System.currentTimeMillis(); }

  public boolean isClose() { return isClose; }

  public void close() {
    isClose = true;
    database.close();
  }


  @SuppressWarnings("serial")
  public static class CloseDatabaseException extends Exception {

  }

  public IArticleIterator getIterator() { return null; }

}
