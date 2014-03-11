/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search;

import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.MetaRelation;
import org.vietspider.common.io.LogService;
import org.vietspider.content.index3.HighlightBuilder;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 5, 2009  
 */

class ArticleThreadLoader {
  
  private HighlightBuilder descBuilder;
  
  ArticleThreadLoader(String query) {
    this.descBuilder = new HighlightBuilder(query);
  }
  
  public void load(MetaList metas, List<MetaRelation> relations) {
    int totalPage = relations.size() / metas.getPageSize() ;
    if (relations.size() % metas.getPageSize() > 0) totalPage++ ;
    metas.setTotalPage(totalPage);
    
    int page = metas.getCurrentPage();
    if(page  < 1 || page > totalPage) page  = 1;
    int start = (page - 1) * metas.getPageSize();
    int end  = Math.min(start + metas.getPageSize(), relations.size());
    for(int i = start; i < end; i++) {
      MetaRelation relation = relations.get(i);
      Article article2 = loadDatabase(relation.getId(), Article.SEARCH);
      if(article2 == null) continue;
      descBuilder.build(article2);
      
      metas.getData().add(article2);
    }
  }
  
  Article loadDatabase(String metaId, short type) {
    Article article = null;
    try {
      article = DatabaseService.getLoader().loadArticle(metaId, type);
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
      return null;
    }
    if(article == null) return null;

    if(article.getMeta() == null) return  null;

    return article;
  }
}
