/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.content.db.article;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.bean.Article;
import org.vietspider.bean.ArticleCollection;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.html.HTMLNode;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 5, 2011
 */
public class AlertQueue extends Thread {
  
  private static volatile AlertQueue INSTANCE;
  public synchronized final static AlertQueue createInstance() {
    if(INSTANCE != null) return INSTANCE;
    INSTANCE = new AlertQueue();
    return INSTANCE;
  }
  
  public synchronized final static AlertQueue getInstance() {
    return INSTANCE;
  } 

  private long lastModified = -1;
  private List<KeyWord> words = new ArrayList<KeyWord>();
  private ConcurrentLinkedQueue<Article> queue = new ConcurrentLinkedQueue<Article>();
  
  private AlertQueue() {
    this.start();
  }
  
  public void run() {
    while(true) {
      load();
      try {
        Thread.sleep(5*60*1000l);
      } catch (Exception e) {
      }
    }
  }
  
  public void get(ArticleCollection collection) {
    while(!queue.isEmpty()) {
      collection.get().add(queue.poll());
    }
    collection.getProperties().setProperty("filter", String.valueOf(words.size() > 0));
  }
  
  private void load() {
    File folder = UtilFile.getFolder("system/plugin/filter/");
    File [] files = folder.listFiles();
    if(files  == null) return;
    boolean update = false;
    for(int i = 0; i < files.length; i++) {
      if(files[i].lastModified() > lastModified) {
        update = true;
        break;
      }
    }
    if(!update) return;
    
    TextSpliter spliter = new TextSpliter();
    for(int i = 0; i < files.length; i++) {
      if(files[i].lastModified() > lastModified) {
        lastModified = files[i].lastModified();
      }
      try {
        String text = new String(RWData.getInstance().load(files[i]), Application.CHARSET);
        text = text.trim() ;
        if(text.length() < 1) continue;
        String [] elements = spliter.toArray(text, '\n'); 
        for(int k = 0; k < elements.length; k++) {
          elements[k] = elements[k].trim();
          if(elements[k].length() < 1) continue;
          words.add(new KeyWord(spliter.toArray(elements[k], ',')));
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }
  
  void add(PluginData data) {
    if(queue.size() > 100) return;
    if(words.size() < 1) {
      queue.add(data.getArticle());
      return;
    }
    Article article = data.getArticle();
    StringBuilder builder = new StringBuilder();
    builder.append(article.getMeta().getTitle()).append('\n');
    builder.append(article.getMeta().getDesc());
    List<HTMLNode> nodes = data.getTextNodes();
    
//    System.out.println("==============  > nodes " + nodes);
    if(nodes != null) {
//      System.out.println("==============  > size " + nodes.size());
      for(int i = 0; i < nodes.size(); i++) {
        builder.append('\n').append(nodes.get(i).getValue());
      }
    }
    
    String text = builder.toString().toLowerCase();
    
    for(int i = 0;  i < words.size(); i++) {
      if(words.get(i).valid(text)) {
        queue.add(article);
        return;
      }
    }
  }
  
  private class KeyWord {
    
    private String [] elements;
    
    private KeyWord(String [] elements) {
      this.elements = elements;
//      elements = CharsUtil.split2Array(text, ','); 
      for(int i = 0; i < elements.length; i++) {
        elements[i] = elements[i].toLowerCase().trim();
      }
    }
    
    private boolean valid(String text) {
      for(int i = 0; i < elements.length; i++) {
        if(elements[i].length() < 1) continue;
        if(text.indexOf(elements[i]) > -1) return true;
      }
      return false;
    }
    
//    private boolean valid() {
//      for(int i = 0; i < elements.length; i++) {
//        if(elements[i].length() < 1) continue;
//        if(text.indexOf(elements[i]) > -1) return true;
//      }
//      return false;
//    }
    
  }
  
}
