/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.db.tp;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.io.LogService;
import org.vietspider.content.tp.TpWorkingData;
import org.vietspider.content.tp.TpWorkingData.TpWord;
import org.vietspider.locale.vn.VietWordSplitter;
import org.vietspider.locale.vn.Word;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2010  
 */
public class TpIndexingReader  {

  private EmbeddedSolrServer server;
  private VietWordSplitter wordSplitter;
  private WordStore wordStore;
//  private TpDatabases tpDatabases;

  public TpIndexingReader(EmbeddedSolrServer server/*, TpDatabases tpDatabases*/) {
    this.server = server;
//    this.tpDatabases = tpDatabases;
  }

  public void setWordSplitter(VietWordSplitter wordSplitter) {
    this.wordSplitter = wordSplitter;
  }

  public void setWordStore(WordStore wordStore) {
    this.wordStore = wordStore;
  }

  public SolrQuery createQuery(/*String group, */TpWorkingData data) {
    StringBuilder builder = new StringBuilder();
    TreeSet<String> list = data.getKeys();
    Iterator<String> iterator = list.iterator();
    boolean next = false;
    while(iterator.hasNext()) {
      String key = iterator.next();
      if(next) {
        builder.append(' ');
      } else {
        next = true;
      }
      if(key.indexOf(' ') > -1) {
        builder.append('\"').append(key).append('\"');
      } else {
        builder.append(key);
      }
    }

    String pattern = builder.toString().trim();
    if(pattern.length() < 1) return null;

    if(data.getTitle() != null) {
      List<Word> words = wordSplitter.split(data.getTitle());
      if(words.size() > 0)  builder.append(" title:");
      for(int i = 0; i < words.size(); i++) {
        Word word = words.get(i);
//        System.out.println(word.getValue() +  " : " + wordStore.isIgnoreWord(word));
        if(wordStore.isIgnoreWord(word)) continue;
        if(i > 0) builder.append(' ');
        if(word.getElements().length > 1) {
          builder.append('\"').append(word.getValue()).append('\"');
          if(word.getNoun() > 0) {
            builder.append("^10.0");
          } else {
            builder.append("^5.0");
          }
        } else {
          builder.append(word.getValue());
          if(word.getNoun() > 0) builder.append("^5.0");
        }
      }
//      System.out.println("=====  >"+ builder);
//      System.out.println("query la " + builder);
    }

    if(builder.length() < 1) return null;

    SolrQuery query = new SolrQuery();
    query = query.setQuery(builder.toString());
    query = query.setFields("id", "time", 
        "score", "title", "tag", "source", "key",
        "word", "key_word");
    query = query.setFacetMinCount(1);
    query = query.setHighlight(false);
    query = query.setQueryType("ctpt_handler");

//    System.out.println("===  >"+ query);
    query.setRows(20);
    query = query.setFacetLimit(2);
    return query;
  }

  public void search(TpComputor.Documents documents) {
//    documents.list = new ArrayList<TpWorkingData>();
    documents.list.clear();
//    System.out.println("===============  >"+ data.getTitle());
//    for(; documents.start < documents.max; documents.start += 20) {
    SolrQuery query = documents.query;
    query = query.setStart(documents.start);
    try {
      QueryResponse response = server.query(query);
      if(response == null) return ;
      SolrDocumentList results = response.getResults();

      int numFound = (int)results.getNumFound();
      if(numFound < documents.max) {
        documents.max = numFound;
      }

      Iterator<SolrDocument> iter = results.iterator();
      while (iter.hasNext()) {
        SolrDocument resultDoc = iter.next();
        String metaId = (String)resultDoc.getFieldValue("id");
        if(metaId == null || metaId.equals(documents.id)) continue;

        documents.score = (Float)resultDoc.getFieldValue("score");

        if(documents.start >= 50 && documents.score < 0.01) break;

//        long time = (Long)resultDoc.getFieldValue("time");
//        System.out.println(" score " + resultDoc.getFieldValue("score"));
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(time);

//        String dateFolder = CalendarUtils.getFolderFormat().format(calendar.getTime());

//        TpDatabase tpDatabase = tpDatabases.getDatabase(group, dateFolder, true);
        try {
//          TpDocument tpDocument = tpDatabase.loadDocument(Long.parseLong(metaId));
//          if(tpDocument == null) continue;
          TpWorkingData workingData = new TpWorkingData(metaId);
          documents.list.add(workingData);

          workingData.setScore((Float)resultDoc.getFieldValue("score"));

          String value = (String)resultDoc.getFieldValue("tag");
          if(value != null) workingData.setCategory(value);

          value = (String)resultDoc.getFieldValue("source");
          if(value != null) workingData.setSource(value);

          value = (String)resultDoc.getFieldValue("title");
//            System.out.println(" title "+ value + " : " + resultDoc.getFieldValue("score"));
//          System.out.println(" score " + resultDoc.getFieldValue("score"));
          if(value != null) workingData.setTitle(value);

          value = (String)resultDoc.getFieldValue("word");
          if(value != null) {
            text2List(workingData.getWords(), value);
//            workingData.wordFromText(value);
          }

          value = (String)resultDoc.getFieldValue("key_word");
          if(value != null) {
            text2List(workingData.getKeyWords(), value);
//            workingData.keyFromText(value);
          }

//          Collection<?> keys = resultDoc.getFieldValues("key");
//          Iterator<?> iterator2 = keys.iterator();
//          System.out.println("key: ");
//          while(iterator2.hasNext()) {
//            System.out.print(iterator2.next() + ", ");
//          }
//          System.out.println();
        } catch (Throwable e) {
          LogService.getInstance().setThrowable(this, e, "|"+query.getQuery()+"|");
        }
      }
//        System.out.println( query.getStart() + " to  " + (query.getStart() + counter)+  " of "+ results.getNumFound());
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
//      System.exit(0);
    }
//    System.out.println("====================  > "+ documents.size());
  }

  private void text2List(List<TpWord> list, String text) {
    TextSpliter spliter = new TextSpliter();
    List<String> elements = spliter.toList(text, ';');
    for(int i = 0; i < elements.size(); i++) {
      String value = elements.get(i);
      int idx = value.indexOf(',');
      if(idx < 0) continue;
      int code = -1;
      try {
        code = Integer.parseInt(value.substring(0, idx));
      } catch (Exception e) {
        continue;
      }

      int time = -1;
      try {
        time = Integer.parseInt(value.substring(idx+1));
      } catch (Exception e) {
        continue;
      }
      list.add(new TpWord(code, time));
    }
  }

  public QueryResponse search(SolrQuery query) {
    try {
      return server.query(query);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return null;
  }


}
