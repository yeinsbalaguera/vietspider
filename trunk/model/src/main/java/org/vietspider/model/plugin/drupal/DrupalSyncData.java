/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.plugin.drupal;

import org.vietspider.model.plugin.SyncContent;
import org.vietspider.serialize.NodeMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 26, 2009  
 */
@NodeMap("drupal-sync-data")
public class DrupalSyncData extends SyncContent {

  @NodeMap("article-id")
  private String articleId = null;
  @NodeMap("category-id")
  private String categoryId = null;
  @NodeMap("link-to-source")
  private String linkToSource = null;
  
  @NodeMap("debug")
  private boolean debug = false;
  public boolean isDebug() { return debug; }
  public void setDebug(boolean debug) { this.debug = debug; }
  
  public DrupalSyncData() {
    super("drupal.sync.article.plugin");
  }
  
  public String getArticleId() { return articleId; }
  public void setArticleId(String articleId) { this.articleId = articleId; }
  
  public String getCategoryId() { return categoryId; }
  public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
  
  public String getLinkToSource() { return linkToSource; }
  public void setLinkToSource(String linkToSource) { this.linkToSource = linkToSource; }
  
}
