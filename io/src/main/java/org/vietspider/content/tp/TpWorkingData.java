/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.tp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.vietspider.bean.Relation;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 7, 2009  
 */
public class TpWorkingData implements Serializable {
  
  public final static char[] SPECIALS = {
    '+', '-', '&', '|', '|', '!', '(', ')', '{', '}', '[', ']', 
    '^', '"', '~', '*', '?', ':', '\\' };

  private final static long serialVersionUID = -34382767548263235l;

  //  private ContentIndex contentIndex;
  //  private TpDocument tpDocument;

  private TreeSet<String> keys = new TreeSet<String>();

  private List<TpWord> tpWords = new ArrayList<TpWord>();
  private List<TpWord> tpKeyWords = new ArrayList<TpWord>();
  private int totalWord = -1;
  private int totalKeyWord = -1;

  //  private int minRate = 15;
  //  private int range = 3;
  //  private String group;

  private transient String title;
  private transient String source;
  private transient String category;

  private List<Relation> relations ;
  private List<Relation> duplicates;

  private String id;
  
  private transient float score;

  public TpWorkingData() {
    //    this.tpDocument = new TpDocument();
  }
  
  public TpWorkingData(String id) {
    this.id = id;
  }

  //  public TpWorkingData(TpDocument tpDocument) {
  //    this.tpDocument = tpDocument;
  //  }

  public void addKey(String value) {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      for(int k = 0; k < SPECIALS.length; k++) {
        if(c != SPECIALS[k]) continue;
        c = ' ';
        break;
      }
      builder.append(c);
    }
    value = builder.toString();
    
    for(int i = 0; i < tpKeyWords.size(); i++) {
      if(value.equalsIgnoreCase(tpKeyWords.get(i).value)) {
        tpKeyWords.get(i).time++;
        return;
      }
    }
    tpKeyWords.add(new TpWord(value));
    keys.add(value);
  }

  public void addWord(String value) {
    for(int i = 0; i < tpWords.size(); i++) {
      if(value.equalsIgnoreCase(tpWords.get(i).value)) {
        tpWords.get(i).time++;
        return;
      }
    }
    tpWords.add(new TpWord(value));
  }
  
  public String word2Text() { return list2Text(tpWords); }
  public String key2Text() { return list2Text(tpKeyWords); }
  
  public List<TpWord> getKeyWords() { return tpKeyWords; }
  public List<TpWord> getWords() { return tpWords; }
  
  public int getTotalWord() {
    if(totalWord > -1) return totalWord;
    for(int i = 0; i < tpWords.size(); i++) {
      totalWord += tpWords.get(i).time;
    }
    return totalWord;
  }
  
  public int getTotalKeyWord() {
    if(totalKeyWord > -1) return totalKeyWord;
    totalKeyWord = 0;
    for(int i = 0; i < tpKeyWords.size(); i++) {
//      System.out.println(tpKeyWords.get(i).value + " : "+ tpKeyWords.get(i).time);
      totalKeyWord += tpKeyWords.get(i).time;
    }
//    System.out.println("============================ "+ totalKeyWord);
    return totalKeyWord;
  }
  
  private String list2Text(List<TpWord> list) {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < list.size(); i++) {
      if(builder.length() > 0) builder.append(';');
      builder.append(list.get(i).code).append(',').append(list.get(i).time);
    }
    return builder.toString();
  }
  
//  public void wordFromText(String text) { text2List(tpWords, text); }
//  public void keyFromText(String text) { text2List(tpKeyWords, text); }
//  
//  private void text2List(List<TpWord> list, String text) {
//    TextSpliter spliter = new TextSpliter();
//    List<String> elements = spliter.toList(text, ';');
//    for(int i = 0; i < elements.size(); i++) {
//      String value = elements.get(i);
//      int idx = value.indexOf(',');
//      if(idx < 0) continue;
//      int code = -1;
//      try {
//        code = Integer.parseInt(value.substring(0, idx));
//      } catch (Exception e) {
//        continue;
//      }
//      
//      int time = -1;
//      try {
//        time = Integer.parseInt(value.substring(idx+1));
//      } catch (Exception e) {
//        continue;
//      }
//      list.add(new TpWord(code, time));
//    }
//  }

  //  public TpWorkingData(TpDocument2 tpDocument2, ContentIndex contentIndex) {
  //    this.tpDocument2 = tpDocument2;
  //    this.contentIndex = contentIndex;
  //  }

  //  public void setContentIndex(ContentIndex contentIndex) { 
  //    this.contentIndex = contentIndex; 
  //  }

  //  public ContentIndex getContentIndex() { return contentIndex; }

  //  public TpDocument getTpDocument() { return tpDocument; }

  public TreeSet<String> getKeys() { return keys; }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  //  public void addKey(String key) {
  ////    System.out.println("========== > add key "+ key);
  //    tpDocument.getNouns().add(key);
  //    keys.add(key);
  //  }
  public void setKeys(TreeSet<String> _keys) { this.keys = _keys; }

  public List<Relation> getRelations() {
    if(relations != null) return relations;
    relations = new ArrayList<Relation>();
    return relations;
  }
  public void setRelations(List<Relation> relations) { this.relations = relations; }

  public List<Relation> getDuplicates() {
    if(duplicates == null) {
      duplicates = new ArrayList<Relation>();
    }
    return duplicates; 
  }

  public void addDuplicate(String relationId, int percent) {
    if(id.equals(relationId)) return;
    if(duplicates == null) {
      duplicates = new ArrayList<Relation>();
    }

    for(Relation ele : duplicates){
      if( ele.getRelation().equals(relationId)) return;
    }

    Relation duplicateRelation = new Relation();
    duplicateRelation.setMeta(id);
    duplicateRelation.setRelation(relationId);
    duplicateRelation.setPercent(percent);
    duplicates.add(duplicateRelation);
  }

  public boolean containsDuplicate(String relationId) {
    if(duplicates == null) {
      duplicates = new ArrayList<Relation>();
    }

    for(Relation ele : duplicates){
      if( ele.getRelation().equals(relationId)) return true;
    }

    return false;
  }

  //  public int getMinRate() { return minRate; }
  //  public void setMinRate(int minRate) { this.minRate = minRate; }

  //  public int getRange() { return range; }
  //  public void setRange(int range) { this.range = range; }
  //  
  //  public String getGroup() { return group; }
  //  public void setGroup(String group) { this.group = group; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getSource() { return source; }
  public void setSource(String source) { this.source = source; }

  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  
  public float getScore() { return score; }
  public void setScore(float score) { this.score = score; }

  public static class TpWord  implements Serializable { 

    private static final long serialVersionUID = 1L;
    
    private String value;
    private int code;
    private int time = 1;

    public TpWord(String value) {
      this.value = value;
      this.code = value.toLowerCase().hashCode();
    }

    public TpWord(int code, int time) {
      this.code = code;
      this.time = time;
    }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }

  }

}
