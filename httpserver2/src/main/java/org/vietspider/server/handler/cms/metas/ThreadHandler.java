/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.sql.SQLException;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.bean.MetaRelation;
import org.vietspider.common.io.LogService;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 26, 2007
 */
public class ThreadHandler extends MetaListHandler {
  
  public ThreadHandler(String type) {
    super(type); 
    name = "THREAD";
  }
  
  public  String handle(final HttpRequest request, final HttpResponse response, 
      final HttpContext context, String...params) throws Exception {
    
    if(params.length < 2) {
      redirect(request, response);
      return "text/html";
    }
    
    Article article = null;
    try {
      article = loadDatabase(params[1]);      
    } catch (Exception e) {
      throw e;
    }

    if(article == null) {
      redirect(request, response);
      return "text/html";
    }
    
    MetaList metas = new MetaList(name);
    
    try {
      metas.setCurrentPage(Integer.parseInt(params[0]));
    }catch (NumberFormatException e) {
      metas.setCurrentPage(1);
    }
    
    metas.setAction("THREAD");
    metas.setTitle(article.getMeta().getTitle());
    metas.setUrl(params[1]);

    String title  = article.getMeta().getTitle();
    article.getMeta().setTitle(title+" (*)");
    metas.getData().add(article);
    List<MetaRelation> list = article.getMetaRelations();
    
    int totalPage = list.size() / metas.getPageSize() ;
    if (list.size() % metas.getPageSize() > 0) totalPage++ ;
    metas.setTotalPage(totalPage);
    
    int page = metas.getCurrentPage();
    if(page  < 1 || page > totalPage) page  = 1;
    int start = (page - 1) * metas.getPageSize();
    int end  = Math.min(start + metas.getPageSize(), list.size());
    for(int i = start; i < end; i++) {
      MetaRelation relation = list.get(i);
      Article article2 = new Article();
      
      Meta meta = new Meta();
      meta.setId(relation.getId());
      meta.setTitle(relation.getTitle());
      meta.setTime(relation.getTime());
      meta.setDesc(relation.getDes());
      meta.setImage(relation.getImage());
      article2.setMeta(meta);
      
      Domain domain = new Domain();
      domain.setCategory("");
      domain.setName(relation.getName());
      article2.setDomain(domain);
      
      metas.getData().add(article2);
    }
    article.getMetaRelations().clear();
    return write(request, response, context, metas, params);
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
