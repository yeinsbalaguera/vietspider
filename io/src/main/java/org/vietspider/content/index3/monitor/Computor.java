/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index3.monitor;

import java.util.List;
import java.util.TreeSet;

import org.vietspider.bean.Article;
import org.vietspider.bean.Meta;
import org.vietspider.bean.Relation;
import org.vietspider.common.text.WordComparator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 13, 2009  
 */
final class Computor {
  
//  private List<TreeSet<String>> titles = new ArrayList<TreeSet<String>>(); 
//  private List<TreeSet<String>> descs = new ArrayList<TreeSet<String>>(); 
//  private List<Integer> scores = new ArrayList<Integer>();
//  private List<String> sources = new ArrayList<String>();
  
  private WordComparator comparator = new WordComparator();
  
  private List<Relation> saves;
  private List<Article> removes;
  
  Computor(List<Article> _removes, List<Relation> _saves) {
    this.removes = _removes;
    this.saves = _saves;
  }
  
  void compute(List<Element> elements, Element current) {
    Article article = current.article;
    Meta meta = article.getMeta();
    if(meta == null || meta.getTitle() == null ) return;

    TreeSet<String> title = comparator.split(meta.getTitle().toLowerCase());
    TreeSet<String> desc = comparator.split(meta.getDesc().toLowerCase());
    for(int i = 0; i < elements.size(); i++) {
      Element ele = elements.get(i);
      int c1 = comparator.compare(title, ele.title);
      int c2 = comparator.compare(desc, ele.desc);
      boolean c3 = Math.abs(ele.score - current.score) <= 5 
                && ele.source.equalsIgnoreCase(current.source);
//      if((c1 < 3 && c2 < 5) 
//          || (c1 < 3 && c3)
//          || (c2 < 5 && c3)
//          || (c2 < 2 && meta.getDesc().length() >= 230)) {
      if(c1 < 3 && c2 < 5) {
        saveDuplicate(ele.article, article, c1, 0);
        return;
      }
      
//      if(c1 < 3 && c3) {
//        saveDuplicate(ele.article, article, c1, 1);
//        return;
//      }
      
      if(c2 < 3 && c3 && meta.getDesc().length() > 100) {
        saveDuplicate(ele.article, article, c1, 2);
        return;
      }
      
      if(c2 < 2 && meta.getDesc().length() >= 230) {
        saveDuplicate(ele.article, article, c1, 3);
        return;
      }
      
//      System.out.println(meta.getTitle() + " ==== > " + compare);
//      System.out.println(meta.getTitle() + " 2 ==== > " + compare2);
//      System.out.println(meta.getDesc());
//      System.out.println(" == > "+ articles.get(i).getMeta().getDesc());
//      System.out.println("=============== "+meta.getId() +"=====================");
    }
    
    current.title = title;
    current.desc = desc;
    elements.add(current);
  }
  
  @SuppressWarnings("unused")
  private void saveDuplicate(Article mainArticle, Article article, int counter, int type) {
    Meta meta = article.getMeta();
    String id = meta.getId();
    if(id.equals(mainArticle.getMeta().getId())) return;

//    ContentIndex contentIndex = new ContentIndex();
//    contentIndex.setStatus(IIndexEntry.DELETE);
//    contentIndex.setId(id);
//    removeIndexs.add(contentIndex);
    article.setStatus(Article.DELETE);
    removes.add(article);

//    List<MetaRelation> relations = mainArticle.getMetaRelation();
//    for(int i = 0; i < relations.size(); i++) {
//      if(relations.get(i).getId().equalsIgnoreCase(id)) {
//        return;
//      }
//    }

//    System.out.println(mainArticle.getMeta().getTitle());
//    System.out.println(meta.getTitle());
//    System.out.println("http://thuannd:9245/vietspider/DETAIL/"+mainArticle.getMeta().getId());
//    System.out.println("http://thuannd:9245/vietspider/DETAIL/"+ meta.getId() );
//    System.out.println("kieu " + type + " counter "+ counter +" \n\n\n" );
    
    Relation relation = new Relation();
    relation.setMeta(mainArticle.getMeta().getId());
    relation.setRelation(id);
    relation.setPercent(counter == 0 ? 100 : (counter == 1 ? 98 : 95));
    saves.add(relation);

    /*MetaRelation metaRelation = new MetaRelation();
    metaRelation.setId(id);
    metaRelation.setDate(meta.getTime());
    metaRelation.setDes(meta.getDesc());
    metaRelation.setImage(meta.getImage());
    metaRelation.setName(meta.getSource());
    metaRelation.setPercent(relation.getPercent());
    metaRelation.setTitle(meta.getTitle());
    metaRelation.setTime(meta.getTime());
    mainArticle.getMetaRelation().add(metaRelation);*/
  }
  
  static class Element {
    
    TreeSet<String> title;
    TreeSet<String> desc;
    int score;
    String source;
    Article article;
    
    Element(Article article, int score, String source) {
      this.article = article;
      this.score = score;
      this.source = source;
    }
    
    
  }
}
