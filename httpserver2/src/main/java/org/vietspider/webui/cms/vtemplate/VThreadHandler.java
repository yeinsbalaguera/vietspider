/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.sql.SQLException;

import org.vietspider.bean.Article;
import org.vietspider.common.io.LogService;
import org.vietspider.db.database.DatabaseService;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 26, 2007
 */
public class VThreadHandler extends VTemplateHandler {
  
  private VMetaListRenderer renderer;
  
  public VThreadHandler(String type) {
    super(type); 
    name = "thread"; 
    
    renderer = new VMetaListRenderer();
  }
  
  public void render(HttpRequestData hrd) throws Exception {
    redirect(hrd);
    /*String [] params = hrd.getParams();
    if(params.length < 2) {
      redirect(hrd);
      return;
    }
    
    Article article = null;
    try {
      article = loadDatabase(params[1]);      
    } catch (Exception e) {
      throw e;
    }

    if(article == null) {
      redirect(hrd);
      return;
    }
    
    MetaList metas = new MetaList(name);
    
    try {
      metas.setCurrentPage(Integer.parseInt(params[0]));
    }catch (NumberFormatException e) {
      metas.setCurrentPage(1);
    }
    
    metas.setAction("thread");
    metas.setTitle(article.getMeta().getTitle());
    metas.setUrl(params[1]);

    String title = article.getMeta().getTitle();
    article.getMeta().setTitle(title);
    metas.getData().add(article);
    List<Relation> list = article.getRelations();
    
    int totalPage = list.size() / metas.getPageSize() ;
    if (list.size() % metas.getPageSize() > 0) totalPage++ ;
    metas.setTotalPage(totalPage);
    
//    System.out.println(totalPage + " : "+ list.size());
    
    int page = metas.getCurrentPage();
    if(page  < 1 || page > totalPage) page  = 1;
    int start = (page - 1) * metas.getPageSize();
    int end  = Math.min(start + metas.getPageSize(), list.size());
    for(int i = start; i < end; i++) {
      Relation relation = list.get(i);
      Article article2 = DatabaseService.getLoader().loadArticle(relation.getRelation(), Article.META);
      
//      Meta meta = new Meta();
//      meta.setId(relation.getId());
//      meta.setTitle(relation.getTitle());
//      meta.setTime(relation.getTime());
//      meta.setDesc(relation.getDes());
//      meta.setImage(relation.getImage());
//      article2.setMeta(meta);
//      
//      Domain domain = new Domain();
//      domain.setCategory("");
//      domain.setName(relation.getName());
//      article2.setDomain(domain);
      
      metas.getData().add(article2);
    }
    article.getMetaRelations().clear();
    
    hrd.setPageType(HttpRequestData.THREAD);
    
    renderer.write(hrd, metas);*/
  }
  
  private Article loadDatabase(String metaId) {
    Article article = null;
    try {
      article = DatabaseService.getLoader().loadArticle(metaId);
    } catch (SQLException e) {
      return null;
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
      return null;
    }
    if(article == null) return null;

    if(article.getMeta() == null) return  null;

    return article;
  }

}
