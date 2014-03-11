/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 9, 2007
 */
@NodeMap("article")
public class Article implements Bean<Article>, Serializable {
  
  public final static String RAW_TEXT_ELEMENT_SEPARATOR = "---------------vietspider element separator-----------------------";
  public final static String RAW_TEXT_ARTICLE_SEPARATOR = "---------------vietspider article separator-----------------------";
  
  private final static long serialVersionUID = -6063235l;
  
  public final static short NORMAL  = 0;
  public final static short SIMPLE  = 1;
  public final static short SEARCH  = 2;
  public final static short META  = 3;
  public final static short EXPORT  = 4;
  public final static short USER  = 5;
  public final static short META_DOMAIN  = 6;
  
  public final static int DELETE = -1;
  public final static int WAIT = 0;
  public final static int READ = 1;
  public final static int SYNCHRONIZED = 2;
  public static final int EDITED = 3;
//  public static final int INDEX = 4;
//  public static final int NLP_OWNER = 5;
  
  public final static int SAVE_NEW = -1;
  public final static int SAVE_RELATION = 0;
  public final static int SAVE_DELETE = 1;
  public final static int SAVE_META = 2;
  public static final int SAVE_CONTENT = 3;
  
  @NodeMap("id")
  private String id;

  @NodeMap("meta")
  private Meta meta;
  @NodeMap("content")
  private Content content;
  @NodeMap("domain")
  private Domain domain;
  
  @NodesMap(value = "meta-relations", item = "item")
  private List<MetaRelation> metaRelations = new ArrayList<MetaRelation>();
  @NodesMap(value = "relations", item = "item")
  private List<Relation> relations;
  
  
  @NodesMap(value = "images", item = "item")
  private List<Image> images = new ArrayList<Image>();
  
  @NodeMap("nld-record")
  private NLPRecord nlpRecord;
  
  @NodeMap("status")
  private int status = WAIT;
  @NodeMap("save-type")
  private int saveType = SAVE_NEW;
  
  @NodeMap("score")
  private float score;
//  private int score = 0;
  
  private Map<String, Object> temp = new HashMap<String, Object>();
 
  
  public Article() {}
  
  public Article(Domain domain, Meta meta, Content content) {
    this.domain = domain;
    this.meta = meta;
    this.content = content;
  }
  
  public String getId() {
    if(id == null) id = getArticleId();
    return id; 
  }
  public void setId(String id) { this.id = id;  }
  
  public Content getContent() { return content; }
  public void setContent(Content content) { this.content = content; }

  public Meta getMeta() { return meta; }
  public void setMeta(Meta meta) { this.meta = meta; }

  public List<MetaRelation> getMetaRelations() { return metaRelations; }
  public void setMetaRelations(List<MetaRelation> relations) { this.metaRelations = relations; }
  
  public Domain getDomain() { return domain; }
  public void setDomain(Domain domain) { this.domain = domain; }
  
  public List<Relation> getRelations() { return relations; }
  public void setRelations(List<Relation> relations) { this.relations = relations; }
  
  public List<Image> getImages() { return images; }
  public void setImages(List<Image> images) { this.images = images; }
  
  public void addImage(Image image) { 
    if(images == null) images = new ArrayList<Image>();
    images.add(image);
  }

  public int getStatus() { return status; }
  public void setStatus(int status) { this.status = status; }
  
  public NLPRecord getNlpRecord() { return nlpRecord; }
  public void setNlpRecord(NLPRecord nlpRecord) { this.nlpRecord = nlpRecord; }
  
//  public int getScore() { return score; }
//  public void setScore(int score) { this.score = score; }
  
 /* public String getPrice() { return price; }
  public void setPrice(String price) { this.price = price;  }

  public int getRate() { return rate;  }
  public void setRate(int rate) { this.rate = rate; }

  public List<Comment> getComments() { return comments; }
  public void setComments(List<Comment> comments) { this.comments = comments; }
  
  public String getRegion() {return region;  }
  public void setRegion(String region) { this.region = region;  }*/
  
  public int getSaveType() { return saveType; }
  public void setSaveType(int saveType) { this.saveType = saveType; }
  
  public float getScore() { return score; }
  public void setScore(float score) { this.score = score; }

  private String getArticleId() {
    if(meta != null) return meta.getId();
    
    if(content != null) return content.getMeta();
    
    if(relations == null || relations.size() < 1) return null;
    return relations.get(0).getMeta();
  }
  
  public void setField(RSField field, Object value) throws Exception {
    switch (field) {
    case META:
      meta = (Meta) value;    
      return;
    case CONTENT:
      content = (Content) value;
      return;  
    case DOMAIN:
      domain = (Domain) value;
      return;
    default:
      return;
    }
  }
  
  public RSField getField() { return null; }
  
  public void putProperty(String key, Object value) {
    //    if("text".equals(key.trim())) new Exception().printStackTrace();
    if(temp == null) temp = new HashMap<String, Object>();
    temp.put(key, value);
  }
  public Iterator<String> iteratorPropertyKey() { 
    if(temp == null) temp = new HashMap<String, Object>();
    return temp.keySet().iterator();
  }
  @SuppressWarnings("unchecked")
  public <T> T getPropertyValue(String key) {
    if(temp == null) return null;
    return (T)temp.get(key);
  }
  public void removeProperty(String key) {
    if(temp == null) return;
    temp.remove(key);
  }
  public boolean hasProperty() {
    if(temp == null) return false;
    return !temp.isEmpty();
  }

}
