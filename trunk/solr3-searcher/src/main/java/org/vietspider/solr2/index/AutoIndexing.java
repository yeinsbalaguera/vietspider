/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.index;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Meta;
import org.vietspider.bean.NLPData;
import org.vietspider.bean.NLPRecord;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.content.ArticleDatabase;
import org.vietspider.db.content.IDatabases;
import org.vietspider.db.database.DatabaseReader.IArticleIterator;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.nlp.NlpHandler;
import org.vietspider.solr2.SolrIndexStorage;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 11, 2011  
 */
public class AutoIndexing extends Thread {

  //  private Set<String> indexed = new HashSet<String>();

  private HTMLParser2 parser = new HTMLParser2();
  private SolrIndexStorage storage;

  public AutoIndexing(SolrIndexStorage storage) {
    this.storage = storage;
    //    File file  = UtilFile.getFile("content/solr2/", "indexed.txt");
    //    DataReader reader = RWData.getInstance();
    //    try {
    //      String text = new String(reader.load(file), Application.CHARSET);
    //      String [] elements = text.split("\n");
    //      for(String ele : elements ) {
    //        if((ele = ele.trim()).isEmpty()) continue;
    //        indexed.add(ele);
    //      }
    //    } catch (Exception e) {
    //      LogService.getInstance().setThrowable(e);
    //    }
  }

  //  private void save(String name) {
  //    indexed.add(name);
  //    
  //    File file  = UtilFile.getFile("content/solr2/", "indexed.txt");
  //    StringBuilder builder = new StringBuilder();
  //    Iterator<String> iterator = indexed.iterator();
  //    while(iterator.hasNext()) {
  //      if(builder.length() > 0) builder.append('\n');
  //      builder.append(iterator.next());
  //    }
  //    
  //    try {
  //      org.vietspider.common.io.RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
  //    } catch (Exception e) {
  //      LogService.getInstance().setThrowable(e);
  //    }
  //  }

  public void run() {
    while(true) {
      try {
        File folder = UtilFile.getFolder("content/solr2/database2/");
        File [] files = folder.listFiles();
        if(files == null || files.length < 1) return;

        for(int i = 0; i < files.length; i++) {
          //      if(indexed.contains(files[i].getName())) continue;
          LogService.getInstance().setMessage(null, "Index2: Start index "+ files[i].getName());
          try {
            index(files[i]);
          } catch (Throwable e) {
            LogService.getInstance().setThrowable(e);
          }
          //      save(files[i].getName());
          LogService.getInstance().setMessage(null, "Index2: Finish index "+ files[i].getName());

          UtilFile.deleteFolder(files[i]);

          try {
            Thread.sleep(1*1000l);
          } catch (Exception e) {
          }
        }
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  private void index(File folder) throws Throwable {
    ArticleDatabase  database = new ArticleDatabase(folder.getAbsolutePath(), true, true);
    IArticleIterator iterator = database.getIterator();
    long counter = 0;
    StringBuilder nlpBuilder = new StringBuilder();
    
    while(iterator.hasNext()) {
      Article article = iterator.next(Article.NORMAL);
      
      Meta meta = article.getMeta();
      Content content  = article.getContent();
      try {
        HTMLDocument doc = parser.createDocument(content.getContent());
        article.setNlpRecord(NlpHandler.process(meta, doc.getRoot()));
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
      
      storage.save(article);
      save(nlpBuilder, article.getNlpRecord());

      iterator.remove();

      counter++;
      
      if(counter%50 == 0) {
        RWData.getInstance().append(getNlpFile(), nlpBuilder.toString().getBytes(Application.CHARSET));
        nlpBuilder.setLength(0);
      }
      
      if(counter%500 != 0) continue;
      LogService.getInstance().setMessage(null, "index " + counter + " article.");
      Calendar calendar = Calendar.getInstance();
      int hour = calendar.get(Calendar.HOUR);
//      System.out.println(hour);
      if(hour >= 8 && hour <= 21) {
        try {
          Thread.sleep(IDatabases.SLEEP);
        } catch (Exception e) {
        }
      } else {
        try {
          Thread.sleep(3*1000l);
        } catch (Exception e) {
        }
      }
    }
    
    if(nlpBuilder.length() > 0) {
      RWData.getInstance().append(getNlpFile(), nlpBuilder.toString().getBytes(Application.CHARSET));
    }
    
    database.close();
    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR);
//    System.out.println("hehe "+hour);
    if(hour >= 8 && hour <= 21) {
      try {
        Thread.sleep(IDatabases.SLEEP);
      } catch (Exception e) {
      }
    }
  }
  
  private void save(StringBuilder builder, NLPRecord record) {
    if(record == null) return;
    builder.append("id: ").append(record.getMetaId()).append('\n');
    List<String> list = record.getData(NLPData.ACTION_OBJECT);
    if(list.size() > 0) {
      builder.append("action_object: ");
      for(int i = 0; i < list.size(); i++) {
        builder.append(list.get(i)).append(';');
      }
      builder.append('\n');
    }
    
    list = record.getData(NLPData.AREA);
    if(list.size() > 0) {
      builder.append("area: ");
      for(int i = 0; i < list.size(); i++) {
        builder.append(list.get(i)).append(';');
      }
      builder.append('\n');
    }
    
    list = record.getData(NLPData.ADDRESS);
    if(list.size() > 0) {
      builder.append("address: ");
      for(int i = 0; i < list.size(); i++) {
        builder.append(list.get(i)).append(';');
      }
      builder.append('\n');
    }
    
    list = record.getData(NLPData.PRICE_TOTAL);
    if(list.size() > 0) {
      builder.append("price total: ");
      for(int i = 0; i < list.size(); i++) {
        builder.append(list.get(i)).append(';');
      }
      builder.append('\n');
    }
    
    list = record.getData(NLPData.PRICE_UNIT_M2);
    if(list.size() > 0) {
      builder.append("price unit m2: ");
      for(int i = 0; i < list.size(); i++) {
        builder.append(list.get(i)).append(';');
      }
      builder.append('\n');
    }
    
    list = record.getData(NLPData.PRICE_MONTH);
    if(list.size() > 0) {
      builder.append("price month: ");
      for(int i = 0; i < list.size(); i++) {
        builder.append(list.get(i)).append(';');
      }
      builder.append('\n');
    }
    builder.append('\n');
  }
  
  private File getNlpFile() {
    int index = 1;
    while(true) {
      File nlpFile = UtilFile.getFile("content/solr2/", "nlp." + String.valueOf(index) + ".txt");
      if(nlpFile.exists() && nlpFile.length() >= 10*1024*1024l) {
        index++;
        continue;
      }
      return nlpFile;
    }
  }

}
