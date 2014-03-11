/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.ContentMapper;
import org.vietspider.common.io.LogService;
import org.vietspider.db.content.ArticleDatabase;
import org.vietspider.db.content.ArticleDatabases;
import org.vietspider.db.database.MetaList;
import org.vietspider.model.TopArticle;
import org.vietspider.model.TopArticleService;
import org.vietspider.model.TopArticles;
import org.vietspider.webui.cms.RequestUtils;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 26, 2006
 */
public class VEventHandler extends VTemplateHandler {

  private VMetaListRenderer renderer;
  private long lastUpdate = -1;
  private byte[] pcCached;
  private byte[] mobileCached;
  
  public VEventHandler(String type) {
    super(type); 
    name = "top"; 

    renderer = new VMetaListRenderer();
  }

  public void render(HttpRequestData hrd) throws Exception {
    if(RequestUtils.isInvalidBot(hrd.getAgent())) {
      invalid(hrd);
      return;
    }
    
    if(RequestUtils.isBot(hrd.getAgent())) {
      LogService.getInstance().setMessage("WEB_CLIENT", null,
          "User Agent: " + hrd.getAgent() + ", is mobile? " + String.valueOf(hrd.isMobile()));
    } else {
      log.setMessage("USER_SEARCH", null,hrd.getUri());
      log.setMessage("USER_SEARCH", null, "User Agent: " + hrd.getAgent());
    }
    hrd.setPageType(HttpRequestData.EVENT);

    if(hrd.isMobile()) {
      if(mobileCached != null && System.currentTimeMillis() - lastUpdate < 30*60*1000l) {
        //      System.out.println(" van dung cached cu ");
        hrd.write(mobileCached);
        return;
      }
    } else {
      if(pcCached != null && System.currentTimeMillis() - lastUpdate < 30*60*1000l) {
        //      System.out.println(" van dung cached cu ");
        hrd.write(pcCached);
        return;
      }
    }
    //    System.out.println("time out roi nhe, cap nhat moi ");

    lastUpdate = System.currentTimeMillis();

    MetaList metas = new MetaList(name);
    metas.setTitle("Nik tin tức - Tin nổi bật"); 

    List<Article> others = new ArrayList<Article>();
    List<Article> articles = metas.getData();

    List<TopArticles> collection = TopArticleService.getInstance().getCollection();
    for(int i = 0; i < collection.size(); i++) {
      TopArticles topArticles = collection.get(i);
      List<TopArticle> tlist = topArticles.getArticles();

      for(int  j = 0; j < Math.min(tlist.size(), 2); j++) {
        Article article = loadArticle(tlist.get(j));
        if(article == null) continue; 
        article.getMeta().putProperty("event.tag", topArticles.getId());
        articles.add(article);
      }

      for(int  j = 0; j < Math.min(tlist.size(), 3); j++) {
        Article article = loadArticle(tlist.get(j));
        if(article == null) continue; 
        others.add(article);
      }
    }
    
    if(others.size() > 0) {
      int random = (int)(Math.random()*others.size());
      //    System.out.println("===== > random " +  random);
      articles.add(0, others.get(random));
      others.remove(random);
    }
    

    hrd.getProperties().put("event.others", others);

    renderer.write(hrd, metas);
    if(hrd.isMobile()) {
      mobileCached = hrd.getOutput().toByteArray();
    } else {
      pcCached = hrd.getOutput().toByteArray();
    }
  }

  private Article loadArticle(TopArticle topArticle) {
    ArticleDatabase db = (ArticleDatabase) 
    ArticleDatabases.getInstance().getDatabase(topArticle.getId(), true, false);
    if(db == null) return null;
    try {
      Article article = new Article();
      String rawMeta = db.loadMetaAsRawText(Long.parseLong(topArticle.getId()));
      if(rawMeta == null) return null;
      if(rawMeta.indexOf(ContentMapper.SEPARATOR) < 0) return null;
      ContentMapper.text2MetaData(article, rawMeta);
      if(article.getMeta() == null) return null;
      
//      article.setMeta(meta) ;
//      
//      Domain domain = db.loadDomain(Long.parseLong(meta.getDomain()));
//      article.setDomain(domain);
      
      db.loadMetaRelation2(article);

      return article;
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }

}


/*
DatabaseService.getUtil().loadEvent(null, metas);

String [] cates = CommonRenderer.getInstance().menu.getConfigCategories();
List<Article> list = metas.getData(); 
if(cates != null && list.size() > 0) {
  int max = 2;

  List<Article> articles = new ArrayList<Article>();
  int random = (int)Math.random()*(Math.min(20, list.size()));
  articles.add(list.get(random));
  list.remove(random);

  for(int i = 1; i < cates.length; i++) {
    String label = cates[i];
    int idx = label.indexOf('.');
    if(idx > 0) label = label.substring(idx+1);
    int counter = 0;
    Iterator<Article> iterator = list.iterator();
    while(iterator.hasNext()) {
      Article article = iterator.next();
      String temp = article.getDomain().getCategory();
//      System.out.println(temp + " : "+ cates[i]);
      if(!temp.equals(label)) continue;
      article.getMeta().putProperty("event.tag", cates[i]);
      articles.add(article);
      counter++;
      iterator.remove();
      if(counter >= max) break;
    }
  }

  List<Article> others = new ArrayList<Article>();
  Set<String> titles = new HashSet<String>();
  for(int i = 0; i < list.size(); i++) {
    Article article = list.get(i);
    String dup = article.getMeta().getPropertyValue("duplicated");
    if("true".equals(dup)) continue;
    String title = article.getMeta().getTitle().toLowerCase();
    if(titles.contains(title)) continue;
    titles.add(title);
    others.add(article);
    if(others.size() >= 10) break;
  }

  hrd.getProperties().put("event.others", others);

  list.clear();
  list.addAll(articles);
} else {
  while(list.size() > 10) {
    list.remove(list.size() - 1);
  }
}*/