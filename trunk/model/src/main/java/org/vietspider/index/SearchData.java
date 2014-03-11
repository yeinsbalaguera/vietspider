/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.index;

import org.vietspider.bean.Article;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 21, 2010  
 */
public interface SearchData {
  
  public void addArticle(Article article);
  
  public int getSize();
  
  public int total();
  
}
