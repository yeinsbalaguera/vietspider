/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index;

import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.serialize.NodeMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 20, 2010  
 */
@NodeMap("search-result")
public class SearchResult {

  @NodeMap("score")
  private float score;

  @NodeMap("id")
  private String id;
  
  @NodeMap(value = "title", cdata = true)
  private String title;
  
  @NodeMap(value = "desc", cdata = true)
  private String desc;
  
  @NodeMap("article")
  private Article article;
  
  public SearchResult() {
  }
  
  public SearchResult(String id, float score) {
    this.id = id;
    this.score = score;
  }

  public float getScore() { return score; }
  public void setScore(float score) { this.score = score; }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDesc() { return desc; }
  public void setDesc(String desc) { this.desc = desc; }

  public Article getArticle() { return article; }
  public void setArticle(Article article) {
    if(article == null) return;
    if(!article.getId().equals(id)) return;
    article.setScore(score);
    Meta meta = article.getMeta();
    if(title != null && !title.trim().isEmpty()) meta.setTitle(title);
    if(desc != null && !desc.trim().isEmpty()) meta.setDesc(desc);
    this.article = article; 
  }

}
