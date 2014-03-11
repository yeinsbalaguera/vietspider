/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.plugin.nukeviet;

import org.vietspider.model.plugin.SyncContent;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.SetterMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 26, 2009  
 */
@NodeMap("nukeviet-sync-data")
public class NukeVietSyncData extends SyncContent {

  @NodeMap("article-id")
  private String articleId = null;
  @NodeMap("category-id")
  private String categoryId = null;
  @NodeMap("link-to-source")
  private String linkToSource = null;
//  @NodeMap("upload-image")
//  private boolean uploadImage = false;
  @NodeMap("published")
  private boolean published = false;
  
  @NodeMap("debug")
  private boolean debug = false;
  public boolean isDebug() { return debug; }
  public void setDebug(boolean debug) { this.debug = debug; }
  
  public NukeVietSyncData() {
    super("nukeviet.sync.article.plugin");
  }
  
  public String getArticleId() { return articleId; }
  public void setArticleId(String articleId) { this.articleId = articleId; }
  
  public String getCategoryId() { return categoryId; }
  public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
  
  public String getLinkToSource() { return linkToSource; }
  public void setLinkToSource(String linkToSource) { this.linkToSource = linkToSource; }
  
//  @GetterMap("upload-image")
//  public boolean isUploadImage() { return uploadImage; }
//  @SetterMap("upload-image")
//  public void setUploadImage(boolean value) { this.uploadImage = value; }
  
  @GetterMap("published")
  public boolean isPublished() { return published; }
  @SetterMap("published")
  public void setPublished(boolean isPublished) { this.published = isPublished; }
  
}
