/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db;

import java.util.Calendar;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.index.ITextIndex;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 13, 2009  
 */
public class ContentIndex extends ITextIndex   {
  
  private final static long serialVersionUID = -1143827297548263235l;
  
  final public static String FIELD_SEQUENCE   = "sequence" ;
  final public static String FIELD_TITLE_WORD   = "title_word" ;
  final public static String FIELD_DESC_WORD   = "desc_word" ;
  final public static String FIELD_CONTENT_WORD   = "content_word" ;
  final public static String FIELD_REGION   = "region" ;
  final public static String FIELD_POST_DATE   = "post_date" ;
  final public static String FIELD_PRICE   = "price" ;
  final public static String FIELD_DOMAIN   = "domain" ;
  
  private String desc;
  private String content;
  private String region;
  private long price ;
  private int postDate = -1;
  
  public ContentIndex() {
    date = CalendarUtils.getDateFormat().format(Calendar.getInstance().getTime());
    expireDate = CrawlerConfig.EXPIRE_DATE;
    folder = UtilFile.getFolder("content/cindexed");
  }

  public String getDescription(){ return desc; }
  public void setDescription(String desc){ this.desc = desc; }
  
  public String getContent(){return content;}
  public void setContent(String content){ this.content = content; }
  
  public String getRegion() { return region; }
  public void setRegion(String region) { this.region = region;  }

  public long getPrice() { return price; }
  public void setPrice(long price) { this.price = price; }

  public int getPostDate() {
    if(postDate > 0) return postDate;
    postDate = Integer.parseInt(id.substring(0, 8));
    return postDate; 
  }
  
//  public void setPostDate(String postDate) { this.postDate = postDate; }
  
  @Override
  public void fromDocument(Document document) {
    Field field = document.getField(FIELD_ID);
    id = field.stringValue();
//    field = document.getField(FIELD_DATE);
//    date = field.stringValue();
  }
  
  @Override
  public Document toDocument() {
    
    Document doc = new Document();

    doc.add(new Field(FIELD_ID, id, Field.Store.YES, Field.Index.NOT_ANALYZED));
   
    if(title != null) {
      Field field = new Field(FIELD_TITLE_WORD, title, Field.Store.NO, Field.Index.ANALYZED);
      field.setBoost(3.0f);
//      field.setOmitTermFreqAndPositions(true) ;
      doc.add(field);  
//      addField(doc, analyzer, FIELD_TITLE_WORD, title/*, 3.0f*/);
    }
    if(desc != null) {
      Field field = new Field(FIELD_DESC_WORD, desc, Field.Store.NO, Field.Index.ANALYZED);
      field.setBoost(2.0f);
      doc.add(field); 
//      addField(doc, analyzer, FIELD_DESC_WORD, desc/*, 2.5f*/);
    }
    if(content != null) {
      Field field = new Field(FIELD_CONTENT_WORD, content, Field.Store.NO, Field.Index.ANALYZED);
      doc.add(field); 
//      addField(doc, analyzer, FIELD_CONTENT_WORD, content/*, 2.5f*/);
    }
    
    NumericField numbeField = new NumericField(FIELD_POST_DATE);
    numbeField.setIntValue(getPostDate());
//    Field field = new Field(FIELD_POST_DATE, getPostDate(), Field.Store.NO, Field.Index.NOT_ANALYZED);
    doc.add(numbeField);  
    
    if(region != null) {
//      System.out.println(" danh index "+ region);
      Field field = new Field(FIELD_REGION, region, Field.Store.NO, Field.Index.NOT_ANALYZED);
      doc.add(field);  
    }
    
    if(price > 0) {
      Field field = new Field(FIELD_PRICE, String.valueOf(price), Field.Store.NO, Field.Index.NOT_ANALYZED);
      doc.add(field);  
    }
    
    return doc;
  }
  
 /* private void addField(Document doc, 
      ViIndexAnalyzer2 analyzer, String fieldName, String text) {
    List<PhraseData> sequences = analyzer.analyzeSequences(text);
    for(int i = 0; i < sequences.size(); i++) {
      PhraseData sequence = sequences.get(i);
      String value = sequence.getValue();
      if(!hasCharacter(value)) continue;
      
      String lower = value.toLowerCase();
      
      Field field = new Field(FIELD_SEQUENCE, lower, Field.Store.NO, Field.Index.ANALYZED);
//      field.setBoost(10.0f);
      doc.add(field); 
    }
    
    List<PhraseData> phrases = analyzer.analyzePhrases(sequences);
    for(int i = 0; i < phrases.size(); i++) {
      PhraseData phrase = phrases.get(i);
      String value = phrase.getValue();
      if(!hasCharacter(value)) continue;

      if(phrase.isNoun()) {
        Field field = new Field(fieldName, value, Field.Store.NO, Field.Index.NOT_ANALYZED);
        field.setBoost(1.5f);
        field.setOmitTermFreqAndPositions(true) ;
        doc.add(field);  
        continue;
      }

      Field field = new Field(fieldName, value, Field.Store.NO, Field.Index.NOT_ANALYZED);
      field.setOmitTermFreqAndPositions(true) ;
//      field.setBoost(boost); 
      doc.add(field);  
    }
  }*/
  
  /*private boolean hasCharacter(String value) {
    int  index = 0;
    while(index < value.length()) {
      if(Character.isLetterOrDigit(value.charAt(index)) ) return true;
      index++;
    }
    return false;
  }*/
  
  public int getExpireDate() { return CrawlerConfig.EXPIRE_DATE; }
  
  public ContentIndex clone() {
    ContentIndex entry = new ContentIndex();

    entry.setId(id);
    entry.setTitle(title);
    entry.setDescription(desc);
    entry.setContent(content);
    entry.setDate(date);
    entry.setRegion(region);
    entry.setPrice(price);
    entry.setExpireDate(expireDate);
    
    return entry;
  }
  
//  @Override
//  public Analyzer getAnalyzer() { return new SemicolonAnalyzer(); }

}
