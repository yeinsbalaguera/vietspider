/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.external;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Image;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 20, 2011  
 */
public class TempDocument implements Serializable {

  private long start  = System.currentTimeMillis();
  
  private Article article;
  private List<Image> images = new ArrayList();
  
  
  public TempDocument(Article article) {
    this.article = article;
  }
  
  public void addImage(Image image) {
    if(image.getMeta().equals(article.getId())) {
      images.add(image);
    }
  }
  
  public Article getArticle() {
    return article;
  }

  public List<Image> getImages() {
    return images;
  }

  public boolean isTimeout() {
    return System.currentTimeMillis() - start >= 5*1000l;
  }
  
}
