/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 9, 2011  
 */
class MemoryDuplicateDetector extends Thread {
  
  protected volatile java.util.Queue<CollectionModel> queue = new ConcurrentLinkedQueue<CollectionModel>();
  private SolrIndexingWriter writer;
  
  MemoryDuplicateDetector(SolrIndexingWriter writer) {
    this.writer = writer;
    this.start();
  }
  
  void compute(HashMap<String, Float> scores, List<Article> articles) { 
    queue.add(new CollectionModel(scores, articles)); 
  }
  
  public void run() {
    while(true) {
      while(!queue.isEmpty()) {
        CollectionModel collection  = queue.poll();
        List<Model> list = collection.toModels();
        for(int i = 0; i < list.size(); i++) {
          Model model = list.get(i);
          for(int j = i + 1; j  < list.size(); j++) {
            Model model2 = list.get(j);
            compare(model, model2);
          }
        }
      }
      
      try {
        Thread.sleep(15*1000l);
      } catch (Exception e) {
      }
    }
  }
  
  private void compare(Model model1, Model model2) {
//    System.out.println(model1.score + " : "+ model2.score + " : "+ (model1.score == model2.score));
    if(model1.score != model2.score) return;
    Article article1 = model1.article;
    Article article2 = model2.article;
    
    Meta meta1 = article1.getMeta();
    Meta meta2 = article2.getMeta();
    
    String hlTitle1 = meta1.getPropertyValue("hl.title");
    if(hlTitle1 == null)  hlTitle1 = meta1.getTitle();
    
    String hlTitle2 = meta2.getPropertyValue("hl.title");
    if(hlTitle2 == null) hlTitle2 = meta2.getTitle();
    
//    System.out.println(hlTitle1+ " : " +hlTitle2 + " : " + hlTitle1.equals(hlTitle2));
    
    if(!hlTitle1.equals(hlTitle2)) return;
    
    String hlDesc1 = meta1.getPropertyValue("hl.desc");
    if(hlDesc1 == null) return;
    
    String hlDesc2 = meta2.getPropertyValue("hl.desc");
    if(hlDesc2 == null) return;
    
//    System.out.println(hlDesc1+ " : " +hlDesc2 + " : " + hlDesc1.equals(hlDesc2));
    if(!hlDesc1.equals(hlDesc2)) return;
    
//    RelService.getInstance().save(meta1.getId(), meta2.getId());
    writer.delete(meta2.getId());
  }
  

  
  static class CollectionModel {
    
    HashMap<String, Float> scores;
    List<Article> articles;
    
    CollectionModel(HashMap<String, Float> scores, List<Article> articles) {
      this.scores = scores;
      this.articles = articles;
    }
    
    List<Model> toModels() {
      List<Model> list = new ArrayList<Model>();
      
      for(int i = 0; i < articles.size(); i++) {
        String id = articles.get(i).getId();
        float score = scores.get(id);
        if(score < 1) continue;
        list.add(new Model(articles.get(i), score));
      }
      
      return list;
    }
  }
  
  private static class Model {
    
    private Article article;
    private float score;
    
    Model(Article article, float score) {
      this.article = article;
      this.score = score;
    }
  }
}
