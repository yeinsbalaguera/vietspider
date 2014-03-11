/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.content.tp.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import org.vietspider.bean.Relation;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.model.Group;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 26, 2006
 */
public class TopicTracking {
  
  private SummarizeDocument summarize;
  private String metaId;
  private String date;
  private String group = Group.ARTICLE;
  private int minPercentRelation = 0;
  private int dateRangeRelation = 3;
  private List<Relation> relations = new ArrayList<Relation>() ;
  
  private transient TreeSet<SummarizeDocument> docs;
  
  public final static int DELETE = -1, INSERT = 1;
  
  private int type = INSERT;
  
  public TopicTracking(String metaId, String group, int minPercentRelation, int dateRangeRelation) throws Exception {
    this.metaId = metaId;
    this.group = group;
    this.minPercentRelation = minPercentRelation;
    this.dateRangeRelation = dateRangeRelation;
    date = metaId.substring(0, 8);
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
    Date dateInstance = formatDate.parse(date);
    date =  CalendarUtils.getFolderFormat().format(dateInstance);
  }
 
  public SummarizeDocument getSummarize() { return summarize; }
  public void setSummarize(SummarizeDocument summarize) { this.summarize = summarize; }
  
  public String getMetaId() { return metaId; }
  
  public String getDate() { return date; }

  public List<Relation> getRelations() { return relations; }

  public void addRelations(List<Relation> list) {
    for(Relation relation : list) relations.add(0, relation); 
  }

  public int getType() { return type; }

  public void setType(int type) { this.type = type; }

  public String getGroup() { return group; }

  public void setGroup(String group) { this.group = group; }

  public int getMinPercentRelation() { return minPercentRelation; }

  public void setMinPercentRelation(int minPercentRelation) { 
    this.minPercentRelation = minPercentRelation;
  }

  public int getDateRangeRelation() { return dateRangeRelation; }
  public void setDateRangeRelation(int dateRangeRelation) { 
    this.dateRangeRelation = dateRangeRelation; 
  }
  
  public TreeSet<SummarizeDocument> getRelationDocs() {
    if(docs != null) return docs;
    docs = new TreeSet<SummarizeDocument>(new Comparator<SummarizeDocument>() {
      public int compare(SummarizeDocument sd1, SummarizeDocument sd2) {
        return sd1.getId().toString().compareTo(sd2.getId().toString());
      }
    });
    return docs;
  
  }
}
