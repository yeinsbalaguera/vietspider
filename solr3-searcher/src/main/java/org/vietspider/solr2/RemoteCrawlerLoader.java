/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.io.File;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicHeader;
import org.vietspider.bean.Article;
import org.vietspider.bean.ContentMapper;
import org.vietspider.common.Application;
import org.vietspider.common.io.GZipIO;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.content.ArticleDatabases;
import org.vietspider.net.client.DataClientService;
import org.vietspider.net.server.URLPath;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 4, 2011  
 */
public class RemoteCrawlerLoader {
  
  private ArticleDatabases databases;
  private String crawlerUsername;
  protected DataClientService crawler;
  protected  ContentMapper mapper = new ContentMapper();
//  protected DataSimpleCache<Article> cache;
  
  public RemoteCrawlerLoader() {
    String dbFolder = SystemProperties.getInstance().getValue("remote.database.folder");
//    System.out.println(" ======  >"+ dbFolder);
//    System.out.println(new File(dbFolder).getAbsolutePath());
    if(dbFolder != null && !dbFolder.trim().isEmpty()) {
      databases = new ArticleDatabases(new File(dbFolder), true);
    } 
    
    String remoteCrawler = SystemProperties.getInstance().getValue("remote.crawler.address");
    crawlerUsername = SystemProperties.getInstance().getValue("remote.crawler.username");
    if(remoteCrawler != null && !remoteCrawler.trim().isEmpty()) {
      crawler = new DataClientService(remoteCrawler);
    }
    
//    cache = new DataSimpleCache<Article>(24*60*60l);
  }
  
  public Article loadArticle(String metaId) {
    Article article = null;
    if(databases != null) article = databases.loadArticle(metaId);
    if(article != null) return article;
    
    return loadArticleFromCrawler(metaId, 0);
  }
  
  private Article loadArticleFromCrawler(String metaId, int time) {
    if(crawler == null || metaId == null) return null;
    List<Header> lheaders = new ArrayList<Header>();
    lheaders.add(new BasicHeader("action", "load.article.as.raw.text"));
    lheaders.add(new BasicHeader("username", crawlerUsername));
    Header [] headers = lheaders.toArray(new Header[0]);
    try {
      byte [] bytes = crawler.post(URLPath.REMOTE_DATA_HANDLER, metaId.getBytes(), headers);
      bytes = new GZipIO().unzip(bytes);
//      System.out.println(" vao day roi "+ bytes.length);
      return ContentMapper.toArticle(new String(bytes, Application.CHARSET));
//      return crawler.readFromXML(Article.class, 
//          URLPath.REMOTE_DATA_HANDLER, metaId.getBytes(), headers);
//      return crawler.readAsObject(URLPath.REMOTE_DATA_HANDLER, metaId.getBytes(), headers);
    } catch (HttpHostConnectException e) {
      LogService.getInstance().setMessage(e, e.toString());
    } catch (Exception e) {
      if(time > 2) {
        LogService.getInstance().setMessage(e, "Can not load article from "+ crawler.getUrl());
        return null;
      } 
      return loadArticleFromCrawler(metaId, time + 1);
    }
    return null;
  }
  
  public List<Article> loadArticles(List<String> ids) {
    List<Article> articles = new ArrayList<Article>();
    if(ids.size() < 1) return articles;
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < ids.size(); i++) {
      if(databases != null) {
        Article article = loadArticle(ids.get(i));
        if(article != null) {
          articles.add(article);
          continue;
        }
      }
      if(builder.length() > 0) builder.append('\n');
      builder.append(ids.get(i));
    }
    List<Article> list = loadArticles2(builder.toString(), 0);
      //loadArticles(builder.toString(), 0, false);
    if(list == null) return articles;
    for(int i = 0; i < list.size(); i++) {
      Article article = list.get(i);
      if(article == null) continue;
//      cache.put(article.getId(), article);
      articles.add(article);
    }
    return articles;
  }
  
 /* private List<Article> loadArticles(String metaIds, int time, boolean xml) { 
    if(crawler == null) return null;
    List<Header> lheaders = new ArrayList<Header>();
    lheaders.add(new BasicHeader("action", "load.articles"));
    lheaders.add(new BasicHeader("username", crawlerUsername));
    if(xml) {
      lheaders.add(new BasicHeader("write.as.xml", "true"));
    }
    Header [] headers = lheaders.toArray(new Header[0]);
    try {
      ArticleCollection collection = null;
      if(xml) {
        byte [] bytes = crawler.post(URLPath.REMOTE_DATA_HANDLER, metaIds.getBytes(), headers);
        bytes = new GZipIO().unzip(bytes);
        //        System.out.println(" ====  >"+ bytes.length);
//                System.out.println(new String(bytes));
        if(bytes != null && bytes.length > 10) {
          collection =  XML2Object.getInstance().toObject(ArticleCollection.class, bytes);
        }
      } else {
//      long start = System.currentTimeMillis();
//        collection = crawler.readAsObject(URLPath.REMOTE_DATA_HANDLER, metaIds.getBytes(), headers);
        collection = crawler.readFromXML(ArticleCollection.class, 
            URLPath.REMOTE_DATA_HANDLER, metaIds.getBytes(), headers);
//      long end = System.currentTimeMillis();
//      System.out.println(" back tro lai " + (end - start));
      }
      if(collection != null) return collection.get();
    } catch (HttpHostConnectException e) {
      LogService.getInstance().setMessage(e, e.toString());
    } catch (StreamCorruptedException e) {
      return loadArticles(metaIds, time+1, true);
    } catch (Exception e) {
      if(time > 2) {
        LogService.getInstance().setMessage(e,
            "Can not load articles from "+ crawler.getUrl());
        return null;
      } 
      return loadArticles(metaIds, time + 1, false);
    }
    return null;
  }*/
  
  private List<Article> loadArticles2(String metaIds, int time) { 
    if(crawler == null) return null;
    List<Header> lheaders = new ArrayList<Header>();
    lheaders.add(new BasicHeader("action", "load.articles.as.raw.text"));
    lheaders.add(new BasicHeader("username", crawlerUsername));
    Header [] headers = lheaders.toArray(new Header[0]);
    try {
      byte [] bytes = crawler.post(URLPath.REMOTE_DATA_HANDLER, metaIds.getBytes(), headers);
      bytes = new GZipIO().unzip(bytes);
      String text = new String(bytes, Application.CHARSET);
      String [] elements = text.split(Article.RAW_TEXT_ARTICLE_SEPARATOR);
      List<Article> articles = new ArrayList<Article>();
      for(int i = 0; i < elements.length; i++) {
        Article article = ContentMapper.toArticle(elements[i]);
        if(article != null) articles.add(article);
      }
      return articles;
    } catch (HttpHostConnectException e) {
      LogService.getInstance().setMessage(e, e.toString());
    } catch (StreamCorruptedException e) {
      return loadArticles2(metaIds, time+1);
    } catch (Exception e) {
      if(time > 2) {
        LogService.getInstance().setMessage(e, "Can not load articles from "+ crawler.getUrl());
        return null;
      } 
      return loadArticles2(metaIds, time + 1);
    }
    return null;
  }
  
  
}
