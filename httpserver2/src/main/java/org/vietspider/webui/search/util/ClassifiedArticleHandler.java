/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.util;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.MetaRelation;
import org.vietspider.common.io.LogService;
import org.vietspider.content.index3.ArticleHandler2;
import org.vietspider.content.index3.HighlightBuilder;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.index.result.CachedEntry2;
import org.vietspider.index.result.DocEntry;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 25, 2009  
 */
public class ClassifiedArticleHandler implements ArticleHandler2 {

  //http://moom.vn:4529/site/search/3/?query=BlackBerry
  private HighlightBuilder descBuilder;
  private CachedEntry2  pageIO;
  
  public CachedEntry2 getPageIO() { return pageIO; }
  
  public ClassifiedArticleHandler(HighlightBuilder descBuilder) {
    this.descBuilder = descBuilder;
  }

  public void loadArticles(MetaList metas, CachedEntry2 _pageIO) {
    pageIO = _pageIO;
    metas.setTotalPage(pageIO.getTotalPage(metas.getPageSize()));

    List<Article> articles = new ArrayList<Article>(metas.getPageSize());
    int page = metas.getCurrentPage();
    
    while(articles.size() < metas.getPageSize()) {
      List<DocEntry> entries = pageIO.loadPageByAsc(page, metas.getPageSize());
      if(entries.size() < 1) break;
      List<ArticleEntry> articleEntries = null;
      try {
        articleEntries = loadArticles(entries);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        break;
      }
      
      for(int i = 0; i < articleEntries.size(); i++) {
        Article article = articleEntries.get(i).getArticle();
        if(article == null) continue;
        DocEntry entry = articleEntries.get(i).getEntry();
        descBuilder.build(article);
        
        List<MetaRelation> metaRelations = article.getMetaRelations();
        if(metaRelations != null) {
          for(int z = 0; z < Math.min(2, metaRelations.size()); z++) {
            MetaRelation metaRelation = metaRelations.get(z);
            if(metaRelation == null) continue;
            metaRelation.setTitle(descBuilder.buildTitle(metaRelation.getTitle()));
          }
        }
        
        article.setScore(entry.getScore());
//        article.getMeta().setTitle(article.getMeta().getTitle()+ " ( "+entry.getScore()+")");
        
        articles.add(article);

        if(articles.size() >= metas.getPageSize()) break;
      }
      page++;
    }
    
//    SearchMonitor.getInstance().addArticles(articles);
    
    metas.setTotalData(pageIO.getTotalData());
    metas.setData(articles);
  }
  
  public List<ArticleEntry> loadArticles(List<DocEntry> entries) throws Exception {
    List<ArticleEntry> articleEntries = new ArrayList<ArticleEntry>();
    for(int i = 0; i < entries.size(); i++) {
      DocEntry entry = entries.get(i);
      long longId = entry.getMetaId();
      String metaId = String.valueOf(longId);
      Article article = DatabaseService.getLoader().loadArticle(metaId, Article.SEARCH);
      if(article == null) continue;
      ArticleEntry articleEntry = new ArticleEntry(entry, article);
      articleEntries.add(articleEntry);
    }
    return articleEntries;
    
  }

}
