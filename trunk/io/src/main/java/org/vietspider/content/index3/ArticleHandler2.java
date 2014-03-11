/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index3;

import org.vietspider.db.database.MetaList;
import org.vietspider.index.result.CachedEntry2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 6, 2009  
 */
public interface ArticleHandler2 {
  
  public void loadArticles(MetaList metas, CachedEntry2 io);
  
}
