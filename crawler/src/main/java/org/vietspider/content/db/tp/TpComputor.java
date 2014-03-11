/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.vietspider.bean.Relation;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.content.tp.TpWorkingData;
import org.vietspider.content.tp.vn.comparator.TpDocumentMatcher;
import org.vietspider.db.SystemProperties;
import org.vietspider.model.Group;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 31, 2009  
 */
public class TpComputor {
  
  private final static int MIN_RATE = 10;

  private boolean removeDuplicated = true;
//  private int maxDate = 3;
//  private TpIndexingSearcher [] searchers;
  private TpIndexingReader indexingReader;
  private Group group;

  public TpComputor(TpIndexingReader indexingReader, Group group/*, int range*/) {
    this.indexingReader = indexingReader;
    SystemProperties system = SystemProperties.getInstance();
    try {
      String value = system.getValue("tp.remove.duplicated");
      if(value == null || value.trim().isEmpty()) {
        system.putValue("tp.remove.duplicated", "true", false);
      }
      removeDuplicated = "true".equals(value);
    } catch (Exception e) {
      system.putValue("tp.remove.duplicated", "true", false);
    }
    
    this.group = group;
    
//    Groups groups = GroupIO.getInstance().loadGroups(); 
//    group = groups.getGroup(groupName);
  }

  public void compute(TpWorkingData current) throws Throwable {
    SolrQuery query = indexingReader.createQuery(current);
    if(query == null) return;
    
    Documents documents = new Documents(current.getId(), query);
    
//    System.out.println("========================  > "+ current.getTitle());
    while(documents.start < documents.max) {
      indexingReader.search(documents);
      if(documents.list.size() < 1) break;
      if(documents.start >= 50 && documents.score < 0.01) break;
//      System.out.println(" lay duoc " + documents.list.size());
//      System.out.println(documents.start + " to  " + (query.getStart() + documents.list.size())+  " of "+ documents.max);
      Iterator<TpWorkingData> iterator = documents.list.iterator();
      TpDocumentMatcher matcher = new TpDocumentMatcher();
      int minRate = Math.max(MIN_RATE, group.getMinPercentRelation());
      while(iterator.hasNext()) {
        TpWorkingData another = iterator.next();

//      TpDocument tpDocument = current.getTpDocument();
        double m = matcher.compare(current, another);
//      System.out.println("prepare "+current.getTpDocument().getId() 
//          + " : " + another.getTpDocument().getId() + " : "+ m);
        if(m < minRate) continue;   
        if(m > 100) m = 100;

        Relation relation = new Relation();

        String metaId = current.getId();
        String relationId = another.getId();
        if(metaId.equalsIgnoreCase(relationId)) continue;
        // check duplicate data
        if(removeDuplicated && (m >= 85
            || (m > 50 &&
                compareTitle(current.getTitle(), another.getTitle())))) {
          current.addDuplicate(relationId, (int)m);
        }

        relation.setMeta(metaId);
        relation.setRelation(relationId);

        List<Relation> relations = current.getRelations();
        if(check(relations, relation.getRelation())) continue;

        relation.setPercent((int)m);
        relations.add(relation);
      }
      documents.start += 20;
    }
    
    List<Relation> relations = current.getRelations();
    
    Collections.sort(relations, new Comparator<Relation>() {
      public int compare(Relation r1, Relation r2) {
        return r2.getPercent() - r1.getPercent();
      }
    });
    
    if(relations.size() > 10) {
      Iterator<Relation> iterator2 = relations.iterator();
      while(iterator2.hasNext()) {
        Relation relation = iterator2.next();
        if(relation.getPercent() < 25) {
//          System.out.println(" ===  > "+ relation.getMeta() + " : "+ relation.getPercent());
          iterator2.remove();
        }
      }
    }
    
//    System.out.println("==================");
//    for(int i = 0; i < current.getRelations().size(); i++) {
//      System.out.println(current.getRelations().get(i).getMeta() 
//          + " : "+ current.getRelations().get(i).getPercent());
//    }
    
//    System.out.println(" thay co "+ current.getRelations().size());
  }
  
  private boolean check(List<Relation> relations, String id){  
    for(Relation ele : relations){
      if(ele.getRelation().equals(id)) return true;
    }
    return false;
  }
  
  private boolean compareTitle(String title1, String title2) {
    if(title1 == null || title2 == null) return false;
    TextSpliter spliter = new TextSpliter();
    List<String> list1 = spliter.toList(title1, ' ');
    List<String> list2 = spliter.toList(title2, ' ');
    
    if(list1.size() - list2.size() > 3) return false;
    if(list1.size() - list2.size() < -3) return false;
    
    int diff = 0;
    for(int i = 0; i < list1.size(); i++) {
      boolean dup = false;
      for(int j = 0; j < list2.size(); j++) {
        if(list1.get(i).equalsIgnoreCase(list2.get(j))) {
          dup = true;
          break;
        }
      }
      if(!dup) diff++;
      if(diff >= 3) return false;
    }
    if(diff < 1) return true;
    return diff < 3 && list1.size() >= 7 && list2.size() >= 7;
  }
  
  static public class Documents {
    List<TpWorkingData> list = new ArrayList<TpWorkingData>();
    int start = 0;
    int max = 500 ;
    SolrQuery query;
    String id;
    float score = 1;
    
    public Documents(String id, SolrQuery query) {
      this.id = id;
      this.query = query;
      if(Application.LICENSE == Install.PERSONAL) {
        max = 100;
      } else if(Application.LICENSE == Install.PROFESSIONAL) {
        max = 200;
      } else if(Application.LICENSE == Install.ENTERPRISE) {
        max = 300;
      } else {
        max = 500;
      }
    }
    
  }

}
