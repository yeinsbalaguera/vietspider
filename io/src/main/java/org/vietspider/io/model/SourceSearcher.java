/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.NameConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 5, 2007  
 */
public class SourceSearcher {

  public List<String> searchNameByGroup(String group) throws Exception {
    List<Document> documents = search(SourceIndexerService.GROUP, group);
    List<String> list =  new ArrayList<String>();
    for(Document document : documents) {
      Field field = document.getField(SourceIndexerService.NAME);
      String value = field.stringValue();
      if(!list.contains(value)) list.add(value);
    }
    return list;
  }
  
  public List<String> searchByURL(String url) throws Exception {
    List<String> list =  new ArrayList<String>();
    List<Document> documents = search(SourceIndexerService.URL, url);
    
    for(Document document : documents) {
      StringBuilder builder = new StringBuilder();
      Field field  = document.getField(SourceIndexerService.GROUP);
      builder.append(field.stringValue()).append('.');
      field = document.getField(SourceIndexerService.CATEGORY);
      builder.append(field.stringValue()).append('.');
      field = document.getField(SourceIndexerService.NAME);
      builder.append(field.stringValue());
      String value = builder.toString();
      if(!list.contains(value)) list.add(value);
    }
    
    return list;
  }
  
  public List<String> searchByHost(String group, String host) throws Exception {
    List<String> list =  new ArrayList<String>();
    String elements [] = host.split("\\.");
    if(elements.length < 0) return list;
    String match = elements[0];
    for(String element : elements) {
      if(match.length() < element.length()) match = element;
    }

    List<Document> documents = search(SourceIndexerService.HOST, match);
    
    NameConverter converter = new NameConverter();
    for(Document document : documents) {
      Field field  = document.getField(SourceIndexerService.GROUP);
      if(!field.stringValue().equals(group))continue;
      
      Field [] fields = document.getFields(SourceIndexerService.HOST);
      boolean add = true;
      for(int i = 0; i < elements.length; i++) {
        boolean has = false;
        for(Field f : fields) {
          if(!f.stringValue().equals(elements[i])) continue;
          has = true;
          break;
        }
        
        if(has) continue;
        add = false;
        break;
      }
      if(!add) continue;
      
      StringBuilder builder = new StringBuilder();
      field = document.getField(SourceIndexerService.CATEGORY);
      builder.append(field.stringValue()).append('.');
      
      String encCate = NameConverter.encode(field.stringValue());
      File folder = UtilFile.getFolder("sources/sources/" + group + "/");
      folder = new File(folder, encCate+"/");
      
      field = document.getField(SourceIndexerService.NAME);
      builder.append(field.stringValue());

      File file = new File(folder, encCate + "." + NameConverter.encode(field.stringValue()));
      if(!file.exists() || file.length() < 1) continue;
      
      String value = builder.toString();
      if(!list.contains(value)) list.add(value);
    }
    
    return list;
  }
  
  public List<String> searchByCategory(String group, String category) throws Exception {
    List<String> list =  new ArrayList<String>();

    List<Document> documents = search(SourceIndexerService.CATEGORY, category);
    
    for(Document document : documents) {
      Field field  = document.getField(SourceIndexerService.GROUP);
      if(!field.stringValue().equals(group))continue;
      
      field = document.getField(SourceIndexerService.NAME);
      String value = group+"."+category+"."+field.stringValue();
      if(!list.contains(value)) list.add(value);
    }
    
    return list;
  }

  public List<Document> search(String fieldName, String fieldValue) throws Exception {
    File file  = UtilFile.getFolder("sources/index/");
//    RAMDirectory ramDir = null;
    IndexReader reader = null;
    IndexSearcher indexSearcher = null;

    List<Document> list =  new ArrayList<Document>();
    try {
      Directory directory = FSDirectory.open(file);
      reader = IndexReader.open(directory, true);
      indexSearcher = new IndexSearcher(reader);
      
//      ramDir = new RAMDirectory(file);
//      reader = IndexReader.open(ramDir);
//      indexSearcher = new IndexSearcher(reader);
      
      TermQuery query = new TermQuery(new Term(fieldName, fieldValue));
      
      
      TopDocs topDocs = indexSearcher.search(query, null, 100);
      ScoreDoc [] docs = topDocs.scoreDocs;
      
//      HitDocCollector collector = new HitDocCollector(500) ;
//      indexSearcher.search(query, null, collector) ;
//      HitDoc[] hitDocs = collector.getHitDoc(); 
      
      for (int i = 0; i < docs.length; i++) {
        int docId = docs[i].doc;
        list.add(indexSearcher.doc(docId));
      }

//      Hits hits  = indexSearcher.search(query);    
//      Iterator<?> iter = hits.iterator();         
//      while(iter.hasNext()){
//        list.add(((Hit)iter.next()).getDocument());
//
//      }
    } finally {
      try {
        if(indexSearcher != null) indexSearcher.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        if(reader != null) reader.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

    /*  try {
        if(ramDir != null) ramDir.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }*/
    }

    return list;
  }

}
