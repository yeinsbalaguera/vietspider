/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.ContentMapper;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Image;
import org.vietspider.bean.Meta;
import org.vietspider.bean.MetaRelation;
import org.vietspider.bean.NLPRecord;
import org.vietspider.bean.Relation;
import org.vietspider.bean.Relations;
import org.vietspider.cache.InmemoryCache;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DatabaseReader.IArticleIterator;
import org.vietspider.serialize.XML2Object;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2009  
 */
public class ArticleDatabase implements IArticleDatabase {
  
  protected boolean bakup = false;

  protected CommonDatabase metaDb;
  protected CommonDatabase contentDb;
  protected CommonDatabase relationDb;
  protected CommonDatabase nlpDb;

  protected CommonDatabase deleteDb; 
  
  protected UrlDatabase urlDb;
  protected CommonDatabase domainDb;
  protected ImageDatabase imageRawDb;
  protected ImageDatabase imageDb;

//  protected NLPMapper nlpMapper;

  protected volatile long lastAccess = System.currentTimeMillis();
  protected volatile boolean isClose = false;

  protected volatile int counter = 0;
  protected volatile int max = 100;

  protected boolean search = false;
  protected boolean readOnly = false;
  protected boolean nlp = false;
  protected String folder;
  
  protected InmemoryCache<String, Article> articleCached;

  public ArticleDatabase(String path, boolean bakup, boolean readOnly) throws Exception {
    this.folder = path;
    this.bakup = bakup;
    this.readOnly = readOnly;
    articleCached = new InmemoryCache<String, Article>("article.database", 100);
    articleCached.setLiveTime(5*60);
    init();
  }
  
  protected void init() throws Exception {
    max = IDatabases.getInstance().getMaxSync();

    search  = "true".equalsIgnoreCase(SystemProperties.getInstance().getValue("search.system"));
    if(!search && CrawlerConfig.EXCUTOR_SIZE >= 15) search = true; 
    
    nlp  = "true".equalsIgnoreCase(SystemProperties.getInstance().getValue("nlp"));

    File metaFolder = new File(folder, "meta/"); 
    if(!metaFolder.exists()) metaFolder.mkdirs();
    //    if(isEnterprise) {
    if(search) {
      metaDb = new CommonDatabase(metaFolder, "meta", 80*1024*1024l, false);
    } else {
      metaDb = new CommonDatabase(metaFolder, "meta", 10*1024*1024l, false);
    }

    //    } else {
    //      metaDb = new CommonDatabase(metaFolder, "meta", 1*1024*1024l);
    //    }

    File contentFolder = new File(folder, "content/"); 
    if(!contentFolder.exists()) contentFolder.mkdirs();
    //    if(isEnterprise) {
    if(search) {
      contentDb = new CommonDatabase(contentFolder, "content", 80*1024*1024l, readOnly);
    } else {
      contentDb = new CommonDatabase(contentFolder, "content", 10*1024*1024l, readOnly);
    }
    
    if(!bakup) {
      File urlFolder = new File(folder, "url/"); 
      if(!urlFolder.exists()) urlFolder.mkdirs();
      if(search) {
        urlDb = new UrlDatabase(urlFolder, "url", 10*1024*1024l);
      } else {
        urlDb = new UrlDatabase(urlFolder, "url", 1*1024*1024l);
      }
    }

  }
  
  public String getFolder() { return folder; }

  public void delete(long id) throws Throwable {
    if(bakup) return;
    if(deleteDb == null) createDeleteDb();
    deleteDb.save(id, new byte[0]);
  }

  public boolean isDelete(long id) throws Throwable {
    if(bakup) return false;
    if(deleteDb == null) createDeleteDb();
    return deleteDb.contains(id);
  }

  public String searchId(String url) throws Throwable {
    if(bakup) return null;
    return urlDb.load(url);
  }

  public boolean contains(long id) {
    if(isClose) return true;
    return metaDb.contains(id);
  }

  public void save(Article article) throws Throwable {
    Meta meta = article.getMeta();
    if(meta.getTime() == null) throw new IllegalArgumentException("Meta contains invalid data!");
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    long id  = Long.parseLong(meta.getId());

//    if(domain != null && domainDb != null) {
//      long domainId = Long.parseLong(domain.getId());
//      if(!domainDb.contains(domainId)) {
//        domainDb.save(domainId, ContentMapper.domain2Text(domain).getBytes(Application.CHARSET));
//      }
//    }

//    System.out.println(" ------ > "+ nlp + " : "+ nlpRecord);
    
//    if(nlp && nlpRecord != null) {
//      if(nlpDb == null) createNlpDb();
//      //      String nlpProduct = nlpMapper.buildProduct(nlpRecord);
//      //      if(nlpProduct.length() > 0) meta.putProperty("nlp.product", nlpProduct);
//
////      String nlpPhone = nlpMapper.buildPhone(nlpRecord);
////      if(nlpPhone.length() > 0) meta.putProperty("nlp.phone", nlpPhone);
//
////      String nlpAddress = nlpRecord.getOneData(NLPData.ADDRESS);
////      if(nlpAddress != null) {
////        nlpAddress = nlpMapper.toPresentation(nlpAddress);
////        meta.putProperty("nlp.address", nlpAddress);
////      }
////
////      String nlpArea =  nlpRecord.getOneData(NLPData.AREA_SHORT);
////      if(nlpArea != null && nlpArea.length() > 0) meta.putProperty("nlp.area", nlpArea);
////
////      List<String> nlpPrices =  nlpRecord.getData(NLPData.PRICE);
////      String price = nlpMapper.buildCommon(nlpPrices);
////      if(price != null) meta.putProperty("nlp.price", price);
//    }

//    if(meta != null) {
//    System.out.println(" thay co images "+ article.getImages().size());
    metaDb.save(id, ContentMapper.metaData2Text(article).getBytes(Application.CHARSET));
//    }

    Content content = article.getContent();
    if(content != null) {
      contentDb.save(id, content.getContent().getBytes(Application.CHARSET));
    }

//    System.out.println(" =---  > "+ search + " : "+ nlp + " : "+ nlpRecord);
//    if(nlp && nlpRecord != null) {
//      if(nlpDb == null) createNlpDb();
//      String xml = Object2XML.getInstance().toXMLDocument(nlpRecord).getTextValue();
////      System.out.println(xml);
//      nlpDb.save(id, xml.getBytes(Application.CHARSET));
//    }

    if(urlDb != null) urlDb.save(meta.getSource(), meta.getId());

    counter++;
    if(counter < max) return;
    sync();
    counter = 0;
  }

  void sync() throws Throwable {
    //    System.out.println("chuan bi sync "+ Runtime.getRuntime().freeMemory());
    metaDb.sync();
    contentDb.sync();
    if(relationDb != null) relationDb.sync();
    if(nlpDb != null) nlpDb.sync();
    
    if(domainDb != null)  domainDb.sync();
    if(urlDb != null) urlDb.sync();
    if(deleteDb != null) deleteDb.sync();
    if(imageDb != null) imageDb.sync();
    if(imageRawDb != null)imageRawDb.sync();
    //    System.out.println("ket thuc sync "+ Runtime.getRuntime().freeMemory());
  }

  public void save(Content content) throws Throwable  {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    long id  = Long.parseLong(content.getMeta());
    contentDb.save(id, content.getContent().getBytes(Application.CHARSET));
  }

  public void save(Relation relation) throws Throwable  {
    throw new Exception ("Unsupport operation!");
//    if(isClose) throw new CloseDatabaseException ();
//    lastAccess = System.currentTimeMillis();
//    long id = Long.parseLong(relation.getMeta());
//    Relations relations = loadRelations(id);
//    relations.getRelations().add(relation);
//    if(relationDb == null) createRelationDb(); 
//    relationDb.save(id, ContentMapper.relations2Text(relations).getBytes(Application.CHARSET));
  }

  public void save(Image image) throws Throwable  {
    throw new Exception ("Unsupport operation!");
   /* if(bakup) return;
    if(isClose) throw new CloseDatabaseException();
    lastAccess = System.currentTimeMillis();
    if(imageDb == null) createImageDb();
    imageDb.save(image.getId(), ContentMapper.image2Text(image).getBytes(Application.CHARSET));
    if(image.getImage() == null || image.getImage().length < 1) return;
    if(imageRawDb == null) createImageRawDb();
    imageRawDb.save(image.getId(), image.getImage());*/
  }
  
  public void saveRawImage(Image image) throws Throwable  {
    if(bakup) return;
    if(isClose) throw new CloseDatabaseException();
    lastAccess = System.currentTimeMillis();
    if(image.getImage() == null || image.getImage().length < 1) return;
    if(imageRawDb == null) createImageRawDb();
    imageRawDb.save(image.getId(), image.getImage());
  }

  public void save(List<Relation> list) throws Throwable  {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    if(list.size() < 1) return;
    Relation relation = list.get(0); 
    long id  = Long.parseLong(relation.getMeta());
    Article article = new Article();
    String metaData = new String(metaDb.load(id), Application.CHARSET);
    if(metaData.indexOf(ContentMapper.SEPARATOR) < 0) return;
    ContentMapper.text2MetaData(article, metaData);
    
//    Relations relations = loadRelations(id);
//    relations.setMetaId(String.valueOf(id));
    //    relation.setMeta(String.valueOf(id));

    List<Relation> relations = article.getRelations();
    if(relations == null) {
      relations = new ArrayList<Relation>();
      article.setRelations(relations);
    }
    for(int i = 0; i < list.size(); i++) {
      relations.add(list.get(i));
    }
    
    try {
      metaDb.save(id, ContentMapper.metaData2Text(article).getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
//    if(relationDb == null) createRelationDb(); 
////    System.out.println("============> "+mapper.relations2Text(relations));
//    relationDb.save(id, ContentMapper.relations2Text(relations).getBytes(Application.CHARSET));
  }

  private Relations loadRelations(long id) throws Throwable {
    if(relationDb == null) createRelationDb(); 
    byte [] bytes = relationDb.load(id);
    if(bytes == null) {
      return new Relations(String.valueOf(id));
    }
    Relations relations = null;
    if(bytes != null && bytes.length > 0) {
      String text = new String(bytes, Application.CHARSET);
      relations = ContentMapper.text2Relations(text);
    }

    if(relations == null) {
      relations = new Relations(String.valueOf(id));
    }
    return relations;
  }

  public Article loadArticle(String metaId) throws Throwable {
    return loadArticle(Long.parseLong(metaId), Article.NORMAL);
  }

  public Article loadArticle(long id, short type) throws Throwable {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    
    String metaId = String.valueOf(id);
    Article article = articleCached.getCachedObject(metaId);
//    System.out.println("==== > " +metaId + " : " + article);
    if(article != null) {
      if(type != Article.META
          && article.getContent() == null) {
        loadContent(article, id);
      }
      return article;
    }

    article = new Article();
    article.setImages(null);
    
    byte [] bytes = metaDb.load(id);
    if(bytes == null) return null;
    String text = new String(bytes, Application.CHARSET);
    if(text.indexOf(ContentMapper.SEPARATOR) > -1) {
      ContentMapper.text2MetaData(article, text);
      if(type != Article.META) loadContent(article, id);
      
      if(type == Article.NORMAL) {
        loadMetaRelation2(article);
      }
      
      articleCached.putCachedObject(metaId, article);
      
      return article;
    }
    
    // old code

    Meta meta = ContentMapper.text2Meta(text);
    //    if(meta.getTime() == null) System.out.println(text);
    article.setMeta(meta);
    
    if(type != Article.META) loadContent(article, id);

    createDomainDB();
    if(domainDb != null) {
      try {
        String domainID = meta.getDomain();
        if(meta.getDomain() != null) {
          bytes = domainDb.load(Long.parseLong(domainID.trim()));
          if(bytes == null) return null;
          text = new String(bytes, Application.CHARSET);
          Domain domain = ContentMapper.text2Domain(text);
          article.setDomain(domain);
        }
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
    } else {
      try {
        URL url = new URL(meta.getSource());
        Domain domain = new Domain();
        domain.setName(url.getHost());
        article.setDomain(domain);
      } catch (Exception e) {
      }
    }
    
    if(type == Article.META_DOMAIN) {
      articleCached.putCachedObject(metaId, article);
      return article;
    }

    if(type == Article.NORMAL) {
      loadMetaRelation(article, id);
    } else if(type  == Article.SEARCH) {
      Relations relations = loadRelations(id);
      if(relations != null) {
        article.setRelations(relations.getRelations());
      }
      //      article.setMetaRelation(loadShortMetaRelation(id));
    } else {
      Relations relations = loadRelations(id);
      article.setRelations(relations.getRelations());
    }

    //    if(type == Article.EXPORT) {
    if(nlp) {
      if(nlpDb == null) createNlpDb();
      bytes = nlpDb.load(id);
      if(bytes != null) {
        text = new String(bytes, Application.CHARSET);
        //      System.out.println(text);
        article.setNlpRecord(XML2Object.getInstance().toObject(NLPRecord.class, text));
      }
    }
    articleCached.putCachedObject(metaId, article);
    return article;
  }
  
  private void loadContent(Article article, long id) throws Throwable {
    byte [] bytes = contentDb.load(id);
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
    
    byte [] bytes = metaDb.load(id);
    if(bytes == null) return null;
    
    return new String(bytes, Application.CHARSET);
  }
  
  public String loadRawText(long id) throws Throwable {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    
    StringBuilder builder = new StringBuilder();

    byte [] bytes = metaDb.load(id);
    if(bytes == null) return null;
    
    String metaText = new String(bytes, Application.CHARSET);
    builder.append(metaText);
    builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);

    bytes = contentDb.load(id);
    if(bytes == null) {
      builder.append("content is null");
      builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);
    } else {
      builder.append(new String(bytes, Application.CHARSET));
      builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);
    }
    
    if(metaText.indexOf(ContentMapper.SEPARATOR) > -1) {
      return builder.toString();
    }
    
    createDomainDB();
    if(domainDb != null) {
      try {
        String domainId = getDomainId(metaText);
        bytes = domainDb.load(Long.parseLong(domainId));
        builder.append(new String(bytes, Application.CHARSET));
        builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);
      } catch (Exception e) {
        builder.append("domain is null");
        builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);
      }
    } else {
      builder.append("domain is null");
      builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);
    }
    
    if(relationDb == null) createRelationDb(); 
    bytes = relationDb.load(id);
    if(bytes == null) {
      builder.append("relations is null");
      builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);
    } else {
      builder.append(new String(bytes, Application.CHARSET));
      builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);
    }

    //    if(type == Article.EXPORT) {
    if(nlp) {
      if(nlpDb == null) createNlpDb();
      bytes = nlpDb.load(id);
      if(bytes != null) {
        builder.append(new String(bytes, Application.CHARSET));
        builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);
      } else {
        builder.append("nlp is null");
        builder.append(Article.RAW_TEXT_ELEMENT_SEPARATOR);
      }
    }

    return builder.toString();
  }
  
  

  /*private void computeNLP(long id, Meta meta) throws Throwable {
    if(nlpDb == null) createNlpDb();
    if(nlpDb == null) return;
    byte [] bytes = nlpDb.load(id);
    if(bytes == null || bytes.length < 1) return;
    String text = new String(bytes, Application.CHARSET);
    NLPRecord nlpRecord = nlpMapper.text2Record(text);

    Map<String, Object> properties = meta.getProperties();
    String nlpProduct = nlpMapper.buildProduct(nlpRecord);
    if(nlpProduct.length() > 0)  properties.put("nlp.product", nlpProduct);

    String nlpPhone = nlpMapper.buildPhone(nlpRecord);
    if(nlpPhone.length() > 0)  properties.put("nlp.phone", nlpPhone);

    String nlpAddress = nlpMapper.buildAddress(nlpRecord);
    if(nlpAddress.length() > 0)  properties.put("nlp.address", nlpAddress);

    if(properties.size() > 0) {
      metaDb.save(id, mapper.meta2Text(meta).getBytes(Application.CHARSET));
    }
  }
   */
  public Meta loadMeta(String metaId) throws Throwable {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    long id = Long.parseLong(metaId);
    byte [] bytes = metaDb.load(id);
    if(bytes == null) return null;
    String text = new String(bytes, Application.CHARSET);
    return ContentMapper.text2Meta(text);
  }
  
 /* public Article loadMetaData(String metaId) throws Throwable {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    long id = Long.parseLong(metaId);
    byte [] bytes = metaDb.load(id);
    if(bytes == null) return null;
    String text = new String(bytes, Application.CHARSET);
    Article article = new Article();
    if(text.indexOf(ContentMapper.SEPARATOR) > -1) {
      ContentMapper.text2MetaData(article, text);
    } else {
      Meta meta = ContentMapper.text2Meta(text);
      article.setMeta(meta);
    }
    return article;
  }*/

  public Relations loadRelations(String metaId) throws Throwable {
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    long id = Long.parseLong(metaId);
    if(relationDb == null) createRelationDb(); 
    byte [] bytes = relationDb.load(id);
    if(bytes == null) return null;
    String text = new String(bytes, Application.CHARSET);
    return ContentMapper.text2Relations(text);
  }

  public void loadMetaRelation(Article article, long id) throws Throwable {
//    System.out.println(" chuan bi load "+ id);
    if(isClose) throw new CloseDatabaseException ();
    lastAccess = System.currentTimeMillis();
    Relations relations = loadRelations(id);
    List<Relation> list = relations.getRelations();
    article.setRelations(list);
    loadMetaRelation2(article);
//    IDatabases databases = IDatabases.getInstance();
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

      //      ArticleDatabase database = (ArticleDatabase)databases.getDatabase(txtRelId, true);
      //      byte [] bytes = database.getMetaDb().load(relId);
      //      if(bytes == null) continue;
      //      String text = new String(bytes, Application.CHARSET);
      //      Meta meta = mapper.text2Meta(text);
      Article article2 = ArticleDatabases.getInstance().loadMetaData(txtRelId);
      if(article2 == null) continue;
      Meta meta = article2.getMeta();//ArticleDatabases.getInstance().loadMeta(txtRelId);
//      if(meta == null) continue;

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
//      try {
//        System.out.println("hihih " + bakup);
        //        bytes = database.getDomainDb().load(Long.parseLong(meta.getDomain()));
        //        text = new String(bytes, Application.CHARSET);
        if(!bakup) {
          Domain domain = article2.getDomain();
          //ArticleDatabases.getInstance().loadDomain(meta.getDomain()); 
          if(domain != null) {
            metaRel.setName(domain.getName());
          } else {
//            domain = new Domain();
//            domain.setId(meta.getDomain());
            URL url = new URL(metaRel.getSource());
            metaRel.setName(url.getHost());
          }
        }
//      } catch (Exception e) {
//                e.printStackTrace();
//      }
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

    IDatabases databases = IDatabases.getInstance();
    for(int i = 0; i < Math.min(list.size(), 6); i++) {
      //      long relId = Long.parseLong(list.get(i).getRelation());
      String txtRelId = list.get(i).getRelation();

      //      ArticleDatabase database = (ArticleDatabase)databases.getDatabase(txtRelId, true);
      //      byte [] bytes = database.getMetaDb().load(relId);
      //      if(bytes == null) continue;
      //      String text = new String(bytes, Application.CHARSET);
      Article article = databases.loadMetaData(txtRelId);
      if(article == null) continue;
      
      Meta meta = article.getMeta();//databases.loadMeta(txtRelId);//mapper.text2Meta(text);

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
        //        bytes = database.getDomainDb().load(Long.parseLong(meta.getDomain()));
        //        text = new String(bytes, Application.CHARSET);
        Domain domain = article.getDomain(); 
          //ArticleDatabases.getInstance().loadDomain(meta.getDomain());//mapper.text2Domain(text);        
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
    if(bakup) return null;
    if(isClose) throw new CloseDatabaseException();
    lastAccess = System.currentTimeMillis();
    
//    System.out.println("hehe " + id);
    int idx = id.indexOf('.');
    if(idx < 0) return null;
    String metaId = id.substring(0, idx);
//    System.out.println("meta id "+ metaId);
    Article article = loadArticle(metaId);
    if(article != null
        && article.getImages() != null) {
      List<Image> images = article.getImages();
      for(int i = 0; i < images.size(); i++) {
        Image image = images.get(i);
//        System.out.println(" ===  > id "+  id +  " : " + images.get(i).getId()
//            + " : "+ image.getImage());
        if(!id.equals(image.getId())) continue;
        if((image.getImage() != null 
            && image.getImage().length > 0)) return image;

        if(imageRawDb == null) createImageRawDb();
        byte [] bytes = imageRawDb.load(id);
//          System.out.println(" ----  > " + id + " : " + bytes.length);
        if(bytes !=  null) image.setImage(bytes);
        return image;
      }
      return null;
    }
    
    if(imageDb == null) createImageDb();
    byte [] bytes = imageDb.load(id);
//    System.out.println("=============== > bytes" + bytes);
    if(bytes == null) return null;
    String text = new String(bytes, Application.CHARSET);
    Image image = ContentMapper.text2Image(text);

    if(imageRawDb == null) createImageRawDb();
    bytes = imageRawDb.load(id);
    if(bytes !=  null) image.setImage(bytes);

    return image;
  }

  public Domain loadDomain(long id) throws Throwable  {
//    System.out.println(bakup);
    if(bakup) return null;
    if(isClose) throw new CloseDatabaseException();
    lastAccess = System.currentTimeMillis();
    createDomainDB();
    if(domainDb == null) return null;
    byte [] bytes = domainDb.load(id);
//    System.out.println("thay co "+ id + " : " + bytes);
    if(bytes == null) return null;
    String text = new String(bytes, Application.CHARSET);
    Domain domain = ContentMapper.text2Domain(text);
    return domain;
  }

  public long getLastAccess() { return lastAccess; }

  public void refreshLastAccess() { lastAccess = System.currentTimeMillis(); }

  public boolean isClose() { return isClose; }

  public void close() {
    isClose = true;
    metaDb.close();
    contentDb.close();
    if(nlpDb != null) nlpDb.close();
    if(relationDb != null) relationDb.close();
    
    if(deleteDb != null) deleteDb.close();
    if(domainDb != null) domainDb.close();
    if(urlDb != null) urlDb.close();
    if(imageDb != null) imageDb.close();
    if(imageRawDb != null) imageRawDb.close();
    
  }

  public CommonDatabase getDomainDb() {
    lastAccess = System.currentTimeMillis();
    try {
      createDomainDB();
      return domainDb;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }

  public CommonDatabase getMetaDb() {
    lastAccess = System.currentTimeMillis();
    return metaDb; 
  }

  /*  public CommonDatabase getContentDb() {
    lastAccess = System.currentTimeMillis();
    return contentDb; 
  }
   */
  public CommonDatabase getRelationDb() {
    lastAccess = System.currentTimeMillis();
    if(relationDb == null) createRelationDb(); 
    return relationDb; 
  }

  /*public ImageDatabase getImageDb() {
    lastAccess = System.currentTimeMillis();
    return imageDb; 
  }*/

  /*public ImageDatabase getImageRawDb() {
    lastAccess = System.currentTimeMillis();
    if(imageRawDb == null) createImageRawDb();
    return imageRawDb; 
  }*/

  public CommonDatabase getNlpDb() {
    lastAccess = System.currentTimeMillis();
    if(nlpDb == null) createNlpDb();
    return nlpDb;
  }

  @SuppressWarnings("serial")
  public static class CloseDatabaseException extends Exception {

  }

  public IArticleIterator getIterator() { return new ArticleIterator(); }

  public class ArticleIterator implements IArticleIterator {

    private Iterator<Long> iterator = null;
    private  long id;

    private ArticleIterator () {
      iterator = metaDb.getMap().keySet().iterator();
    }

    public boolean hasNext() { return iterator.hasNext(); }

    public Article next(short type) throws Throwable {
      id = iterator.next();
      return loadArticle(id, type);
    }

    public void remove() {
      //      try {
      //        System.out.println("=== > delete "+ id);
      //        delete(id);
      //      } catch (Throwable e) {
      //        LogService.getInstance().setThrowable(e);
      //      }
      iterator.remove(); 
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
        imageRawDb = new ImageDatabase(imageRawFolder, "image_raw", 5*1024*1024l);
      }
    } catch (Throwable e) {
      Application.addError(this);
      LogService.getInstance().setThrowable(e);
    }
  }

  protected synchronized  void createImageDb() {
    if(imageDb != null) return;
    File imageFolder = new File(folder, "image/"); 
    if(!imageFolder.exists()) return;//imageFolder.mkdirs();
    try {
      if(search) {
        imageDb = new ImageDatabase(imageFolder, "image", 10*1024*1024l);
      } else {
        imageDb = new ImageDatabase(imageFolder, "image", 2*1024*1024l);
      }
    } catch (Throwable e) {
      Application.addError(this);
      LogService.getInstance().setThrowable(e);
    }
  }

  protected synchronized void createRelationDb() {
    if(relationDb != null) return;
    File relationFolder = new File(folder, "relation/"); 
    if(!relationFolder.exists()) return;//relationFolder.mkdirs();
    try {
      if(search) {
        relationDb = new CommonDatabase(relationFolder, "relation", 5*1024*1024l, false);
      } else {
        relationDb = new CommonDatabase(relationFolder, "relation", 1*1024*1024l, false);
      }
    } catch (Throwable e) {
      Application.addError(this);
      LogService.getInstance().setThrowable(e);
    }
  }

  private synchronized final void createDeleteDb() {
    if(deleteDb != null) return;
    File deleteFolder = new File(folder, "delete/"); 
    if(!deleteFolder.exists()) deleteFolder.mkdirs();
    try {
      if(search) {
        deleteDb = new CommonDatabase(deleteFolder, "delete", 2*1024*1024l, readOnly);
      } else {
        deleteDb = new CommonDatabase(deleteFolder, "delete", 1024*1024l, readOnly);
      }
    } catch (Throwable e) {
      Application.addError(this);
      LogService.getInstance().setThrowable(e);
    }
  }

  public synchronized final CommonDatabase createNlpDb() {
    if(nlpDb != null) return nlpDb;
//    nlpMapper = new NLPMapper();
    File nlpFolder = new File(folder, "nlp/"); 
    if(!nlpFolder.exists())  return null;// nlpFolder.mkdirs();
    try {
      if(search) {
        nlpDb = new CommonDatabase(nlpFolder, "nlp", 50*1024*1024, readOnly);
      } else {
        nlpDb = new CommonDatabase(nlpFolder, "nlp", 2*1024*1024, readOnly);
      }
    } catch (Throwable e) {
      Application.addError(this);
      LogService.getInstance().setThrowable(e);
    }
    return nlpDb;
  }
  
  private static String getDomainId(String metaText) {
    int idx = metaText.indexOf(']');
    int time = 0;
    while(time < 5) {
//      System.out.println("============  >"+ metaText.substring(idx) );
      idx = metaText.indexOf(']', idx+1);
      time++;
    }
    
    metaText = metaText.substring(idx+1);
//    System.out.println(metaText);
    idx = metaText.indexOf(']');
    return metaText.substring(1, idx);
  }
  
  private synchronized final void createDomainDB() throws Exception {
//    new Exception ().printStackTrace();
    if(domainDb != null) return;
    if(!this.bakup) { 
      File domainFolder = new File(folder, "domain/");
      if(!domainFolder.exists()) return;
      if(search) {
        domainDb = new CommonDatabase(domainFolder, "domain", 10*1024*1024l, readOnly);
      } else {
        domainDb = new CommonDatabase(domainFolder, "domain", 1*1024*1024l, readOnly);
      }
    }
  }
  
//  public static void main(String[] args) {
//    Calendar calendar = Calendar.getInstance();
//    System.out.println(calendar.get(Calendar.WEEK_OF_YEAR));
//    String text = "[123123][Nha Trang - Bán nhà - khu d?t vàng - m?t ti?n du?ng Tr?n Phú][1/ Di?n tích &#58;  4 x 21.5m = 86m2][~][20/07/2011 21:32:28][06/04/2011 09:01:28][-212132424][http&#58;//5giay.vn/showthread.php?t=2831102][region][khánh hòa][owner][true][temp.url.code][ec543618b101be9a5f8b8b4e3c588442][action_object][1&#44;1]";
//    System.out.println("hehehe "+ getDomainId(text));
//  }


}
