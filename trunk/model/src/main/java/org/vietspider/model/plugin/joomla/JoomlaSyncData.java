/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.plugin.joomla;

import org.vietspider.model.plugin.SyncContent;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.SetterMap;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 26, 2009  
 */
@NodeMap("joomla-config")
public class JoomlaSyncData extends SyncContent {

  @NodeMap("article-id")
  private String articleId = null;
  @NodeMap("section-id")
  private String sectionId = null;
  @NodeMap("category-id")
  private String categoryId = null;
  @NodeMap("meta-image-width")
  private int metaImageWidth = -1;
  
  @NodeMap("link-to-source")
  private String linkToSource = null;
  @NodeMap("frontpage")
  private boolean featured = true;
  @NodeMap("published")
  private boolean published = false;
  
  @NodeMap("debug")
  private boolean debug = false;
  public boolean isDebug() { return debug; }
  public void setDebug(boolean debug) { this.debug = debug; }
  
  public JoomlaSyncData() {
    super("joomla.sync.article.plugin");
  }
  
  public String getArticleId() { return articleId; }
  public void setArticleId(String articleId) { this.articleId = articleId; }
  
  public String getSectionId() { return sectionId; }
  public void setSectionId(String sectionId) { this.sectionId = sectionId; }
  
  public String getCategoryId() { return categoryId; }
  public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
  
  public String getLinkToSource() { return linkToSource; }
  public void setLinkToSource(String linkToSource) { this.linkToSource = linkToSource; }
  
  public int getMetaImageWidth() { return metaImageWidth;  }
  public void setMetaImageWidth(int metaImageWidth) { this.metaImageWidth = metaImageWidth; }
  
  @GetterMap("featured")
  public boolean isFeatured() { return featured; }
  @SetterMap("featured")
  public void setFeatured(boolean isFeatured) { this.featured = isFeatured; }
  
  @GetterMap("published")
  public boolean isPublished() { return published; }
  @SetterMap("published")
  public void setPublished(boolean isPublished) { this.published = isPublished; }
  
}
