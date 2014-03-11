/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.content;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relations;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.db.database.DatabaseUtils;
import org.vietspider.db.database.MetaList;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Feb 11, 2007
 */
public class DataUtils implements DatabaseUtils {

  @SuppressWarnings("unused")
  public void execute(String... sqls) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void loadEvent(String _date, MetaList data) throws Exception {
    if(_date == null) {
      loadEvent2(data);
      return;
    }
    
    SimpleDateFormat dateFormat  = CalendarUtils.getDateFormat();
    IDatabases databases = IDatabases.getInstance();
    ArticleDatabase articleDatabase = 
      (ArticleDatabase)databases.getDatabase(dateFormat.parse(_date), true, false);
    if(articleDatabase  == null) return;
    CommonDatabase relationDatabase = articleDatabase.getRelationDb();
    List<Relations> relations = new ArrayList<Relations>();
    if(relationDatabase != null) {
      List<String> ids = relationDatabase.loadKey(0, 500);
      for(int i = 0 ; i < ids.size(); i++) {
        try {
          String metaId = ids.get(i);
          Relations rels = articleDatabase.loadRelations(metaId);
          if(rels.getRelations().size() < 1) continue;
          rels.setMetaId(metaId);
          relations.add(rels);
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(e);
        }
      }
    }
    
    Collections.sort(relations, new Comparator<Relations>(){
      public int compare(Relations relation1, Relations relation2){
        return relation2.getRelations().size() - relation1.getRelations().size();
      }
    });
    
    int total = relations.size();
    int totalPage = total / data.getPageSize();
    if (total % data.getPageSize() > 0) totalPage++ ;
    data.setTotalPage(totalPage);
    int currentPage = data.getCurrentPage();
    int from = (currentPage - 1)*data.getPageSize();
//    int to = Math.min(currentPage*data.getPageSize(), total);
    
    List<Article> articles = new ArrayList<Article>();
    for(int i = from; i < relations.size(); i++){
      try {
        Article article =  articleDatabase.loadArticle(relations.get(i).getMetaId());
        if(article.getMetaRelations().size() < 1) continue;
        articles.add(article);
        if(articles.size() >= data.getPageSize()) break;
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    data.setData(articles);
  }
  
  private void loadEvent2(MetaList data) throws Exception {
    Calendar calendar = Calendar.getInstance();
    IDatabases databases = IDatabases.getInstance();
    List<Relations> relations = new ArrayList<Relations>();
    int max = 100;
    int counter = 0;
    while(relations.size() < 100) {
//      System.out.println(" chay vao day "+ relations.size() 
//          + " : "+ calendar.get(Calendar.DATE) +   " : "+ counter
//          );
      if(counter > 2) break;
      ArticleDatabase db = 
        (ArticleDatabase) databases.getDatabase(calendar.getTime(), true, false);
//      System.out.println(db);
      if(db != null) {
        loadRelationData(relations, db, max);
        max -= 50;
        counter++;
      }
      calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-1);
    }
    
//    System.out.println("====  >"+ relations.size());
    
    Collections.sort(relations, new Comparator<Relations>(){
      public int compare(Relations relation1, Relations relation2){
        return relation2.getRelations().size() - relation1.getRelations().size();
      }
    });
    
    List<Article> articles = new ArrayList<Article>();
    for(int i = 0; i < relations.size(); i++){
      try {
        Article article = databases.loadArticle(relations.get(i).getMetaId());
        if(article.getMetaRelations().size() < 1) continue;
        articles.add(article);
        if(articles.size() >= 50) break;
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    data.setData(articles);
  }

  @SuppressWarnings("unused")
  public void loadTopArticles(int top, MetaList list) throws Exception {
    /*ArticleDatabase articleDatabase = IDatabases.getInstance().getDatabase(date);
    CommonDatabase database =   articleDatabase.getRelationDb();
    List<String> ids = database.loadKey(0, 15);
    for(int i = 0; i < ids.size(); i++) {
      try {
        Article article = articleDatabase.loadArticle(ids.get(i));
        if(article != null) list.getData().add(article);
      } catch (Throwable e) {
        LogService.getInstance().setMessage(null, e.toString());
      }
    }*/
    
  }
  
  private void loadRelationData(List<Relations> relations, ArticleDatabase db, int max) {
    if(db  == null) return;
    CommonDatabase relationDatabase = db.getRelationDb();
    List<String> ids = relationDatabase.loadKey(0, max);
    for(int i = 0 ; i < ids.size(); i++) {
      try {
        String metaId = ids.get(i);
        Relations rels = db.loadRelations(metaId);
        if(rels.getRelations().size() < 2) continue;
        rels.setMetaId(metaId);
        relations.add(rels);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  @Override
  public List<Meta> loadTopEvent() throws Exception {
    Calendar calendar = Calendar.getInstance();
    Date date = calendar.getTime(); 
    IDatabases databases = IDatabases.getInstance();
    
    List<Meta> metas = new ArrayList<Meta>();
    IArticleDatabase db = databases.getDatabase(date, true, false);
    if(db == null || !(db instanceof ArticleDatabase)) return metas;
    
    ArticleDatabase articleDatabase = (ArticleDatabase) db;
    
    CommonDatabase relationDatabase = articleDatabase.getRelationDb();
    List<String> ids = relationDatabase.loadKey(0, 15);
    for(int i = 0; i < ids.size(); i++) {
      try {
        Meta meta = articleDatabase.loadMeta(ids.get(i));
        if(meta != null) metas.add(meta);
      } catch (Throwable e) {
        LogService.getInstance().setMessage(null, e.toString());
      }
    }
    return metas;
  }

  @SuppressWarnings("unused")
  public void searchMeta(String pattern, MetaList list) throws Exception {
  }

}
