/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Domain;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.db.database.MetaList;
import org.vietspider.db.idm2.EntryReader;
import org.vietspider.db.idm2.IEntryDomain;
import org.vietspider.db.idm2.MergeEntryDomains;
import org.vietspider.db.idm2.SimpleEntryDomain;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 26, 2006
 */
public class VDetailHandler extends VTemplateHandler {

  private VArticleRendererImpl renderer;

  public VDetailHandler(String type) {
    super(type);
    name = "detail"; 
    renderer = new VArticleRendererImpl();
  }

  public void render(HttpRequestData hrd) throws Exception {
    String [] params = hrd.getParams();
    if(params.length < 1) throw new Exception("Incorrect parameters");

    //    if(logAgent) LogService.getInstance().setMessage(null, userAgent);

    Article article = null;
    try {
      article = loadDatabase(params[0]); 
      article.putProperty("others", loadOthers(article.getDomain()));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(this, e);
      redirect(hrd);
      return;
    }

//    if(article == null) {
//      redirect(hrd);
//      return;
//    }

    renderer.write(hrd, article);
  }

  private Article loadDatabase(String metaId) {
    if(metaId == null || metaId.trim().isEmpty()) return null;
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

  private List<Article> loadOthers(Domain domain) {
    if(domain == null)  return new ArrayList<Article>();
    try {
      MetaList metas = new MetaList(name);
      metas.setPageSize(20);

      String date = domain.getDate();
      String category = domain.getGroup() + "." + domain.getCategory();
      
      EntryReader entryReader = new EntryReader();
      IEntryDomain entryDomain = new SimpleEntryDomain(date, category, null);
      for(int i = 0; i < 5; i++) {
        //working with entry
        int page = (int)(Math.random()*5);
        if(page < 1) page = 1;
        metas.setCurrentPage(page);
       
//        System.out.println(date + " : "+ category );
        entryReader.read(entryDomain, metas, -1, Article.META);
//        System.out.println("pepe " + metas.getCurrentPage()+ " : "+ metas.getData().size());
        if(metas.getData().size() > 0) return metas.getData();
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(this, e);
    }
    return new ArrayList<Article>();
  }
  
  public static void main(String[] args) {
    System.out.println(Math.random()*5);
    int page = (int)(Math.random()*5);
    if(page < 1) page = 1;
    System.out.println(page + 1);
  }

}
