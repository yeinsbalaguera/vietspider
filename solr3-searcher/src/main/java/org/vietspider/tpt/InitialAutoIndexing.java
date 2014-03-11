/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.tpt;

import java.io.File;

import org.vietspider.bean.Article;
import org.vietspider.bean.Content;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.content.ArticleDatabase;
import org.vietspider.db.database.DatabaseReader.IArticleIterator;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.nlp.NlpHandler;
import org.vietspider.serialize.Object2XML;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 11, 2011  
 */
public class InitialAutoIndexing extends Thread {
  
  private HTMLParser2 parser = new HTMLParser2();
  
  private boolean execute = true;

  public InitialAutoIndexing() {
    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "auto indexing close";}

      public void execute() {
        execute = false;
      }

      @Override
      public int getPriority() {
        return 0;
      }
      
      
    });
  }

  public void run() {
    while(execute) {
      try {
        File folder = UtilFile.getFolder("content/solr2/tpt_database/");
        File [] files = folder.listFiles();
        if(files == null || files.length < 1) return;

        for(int i = 0; i < files.length; i++) {
          //      if(indexed.contains(files[i].getName())) continue;
          LogService.getInstance().setMessage(null, "Index2: Start tpt index "+ files[i].getName());
          try {
            index(files[i]);
          } catch (Throwable e) {
            LogService.getInstance().setThrowable(e);
          }
          //      save(files[i].getName());
          LogService.getInstance().setMessage(null, "Index2: Finish tpt index "+ files[i].getName());

          UtilFile.deleteFolder(files[i]);

          try {
            Thread.sleep(10*1000l);
          } catch (Exception e) {
          }
        }
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  private void index(File folder) throws Throwable {
    File folder2 = UtilFile.getFolder("content/database/" + folder.getName()+"/");
    ArticleDatabase  database = new ArticleDatabase(folder.getAbsolutePath(), true, true);
    IArticleIterator iterator = database.getIterator();
    
    ArticleDatabase  database2 = null;
    try {
      database2 = new ArticleDatabase(folder2.getAbsolutePath(), true, false);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    long counter = 0;
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
      
      if(database2 != null && article.getNlpRecord() != null) {
        String xml = Object2XML.getInstance().toXMLDocument(article.getNlpRecord()).getTextValue();
        //      System.out.println(xml);
        Long longId = Long.parseLong(article.getId());
        database2.getNlpDb().save(longId, xml.getBytes(Application.CHARSET));
      }
      
      TptService.getInstance().put(article);
      iterator.remove();

      counter++;
      if(counter%500 != 0) {
        if(database2 != null) database2.getNlpDb().sync();
        continue;
      }
      LogService.getInstance().setMessage(null, "index " + counter + " article.");
      
      long sleep = 500;
      if(counter%50000 == 0) sleep = 1*1000;
      try {
        Thread.sleep(sleep);
      } catch (Exception e) {
      }
    }
    
    database.close();
    if(database2 != null) database2.close();

    try {
      Thread.sleep(10*1000l);
    } catch (Exception e) {
    }
  }

}
