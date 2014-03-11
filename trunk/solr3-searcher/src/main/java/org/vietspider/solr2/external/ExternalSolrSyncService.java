/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.external;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.bean.Article;
import org.vietspider.bean.Image;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.renderer.TextRenderer2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 19, 2011  
 */
public class ExternalSolrSyncService extends Thread {

  private static volatile ExternalSolrSyncService INSTANCE;

  public static synchronized ExternalSolrSyncService getInstance() {
    if(INSTANCE != null) return INSTANCE;
    INSTANCE = new ExternalSolrSyncService();
    return INSTANCE;
  }

  protected volatile java.util.Queue<TempDocument> tempQueue = new ConcurrentLinkedQueue<TempDocument>();
  protected ExternalSolrPost post; 

  private boolean execute = true; 
  private HTMLParser2 parser;

  public ExternalSolrSyncService () {
    try {
      String txt = SystemProperties.getInstance().getValue("external.solr.url");
      if(txt == null) {
        txt = "localhost:8080/solr/update";
        SystemProperties.getInstance().putValue("external.solr.url", "localhost:8080/solr/update", false);
      } 

      if(!txt.startsWith("http://")) txt = "http://" + txt;

      //      System.out.println(txt);

      post = new ExternalSolrPost(txt);
      //      System.out.println(post);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      SystemProperties.getInstance().putValue("external.solr.url", "localhost:8080/solr/update", false);
    }

    if(post == null) {
      LogService.getInstance().setMessage(null, "External solr post is null");
      return;
    }

    Application.addShutdown(new Application.IShutdown() {
      public String getMessage() { return "Close Sync Service";}
      public void execute() {
        execute = false;
        storeTemp();
      }
    });
    
    CrawlerConfig.SAVE_IMAGE_TO_FILE = true;

    loadTemp();
    
    parser = new HTMLParser2();

    this.start();
  }

  public void run() {
    while(execute) {
      commit();

      try {
        Thread.sleep(1*1000l);
      } catch (Exception e) {
      }
    }
  }

  void save(Article article) {
    TempDocument document = new TempDocument(article);
    tempQueue.add(document);
  }
  
  void save(Image image) {
    Iterator<TempDocument> iterator = tempQueue.iterator();
    while(iterator.hasNext()) {
      TempDocument tempDoc = iterator.next();
      tempDoc.addImage(image);
    }
  }

  void commit() {
//    int size = tempQueue.size();
//    System.out.println(" luc truoc la  "+ tempQueue.size());
//   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    Iterator<TempDocument> iterator = tempQueue.iterator();
    while(iterator.hasNext()) {
      TempDocument tempDoc = iterator.next();
      if(!tempDoc.isTimeout()) continue;
      iterator.remove();
      try {
        byte [] bytes = post.postData(tempDoc);
        
        String text = new String(bytes, Application.CHARSET);
//        System.out.println(text);
        if(text.toLowerCase().indexOf("error report") > -1) {
//          StringBuilder builder = new StringBuilder();
          HTMLDocument doc = parser.createDocument(text);
          TextRenderer2 renderer2 = new TextRenderer2(doc.getRoot(), TextRenderer2.RENDERER);
          LogService.getInstance().setMessage(null, renderer2.getTextValue().toString());
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    
//    if(tempQueue.size() - size != 0) {
//      System.out.println(" post "+ (tempQueue.size()-size) +  " articles");
//    }
  }

  public void storeTemp() {
    ByteArrayOutputStream bytesOutput =  new ByteArrayOutputStream(10*1024*1024);

    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(bytesOutput);
      out.writeObject(tempQueue);
      out.flush();

      byte [] bytes = bytesOutput.toByteArray();
      if(bytes.length < 10) return;
      
      File file = UtilFile.getFile("content/solr2/external/", "temp.data");
      RWData.getInstance().save(file, bytes);
    } catch (Exception exp) {
      LogService.getInstance().setThrowable(exp);
    } finally {
      try {
        bytesOutput.close();
      } catch (Exception e) {
      }
    }

  }

  @SuppressWarnings("unchecked")
  public void loadTemp()  {
    File folder = UtilFile.getFolder("content/solr2/external/");
    File file = new File(folder, "temp.data");
    if(!file.exists() || file.length() < 10) return;

    try {
      byte [] bytes = RWData.getInstance().load(file);
      file.delete();

      ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
      ObjectInputStream objectInputStream = null;
      objectInputStream = new ObjectInputStream(byteInput);
      tempQueue = (ConcurrentLinkedQueue<TempDocument>)objectInputStream.readObject();
    } catch (StackOverflowError e) {
      LogService.getInstance().setMessage("TPDATABASE - LOAD", new Exception(e), e.toString() );
    } catch (Throwable e) {
      LogService.getInstance().setThrowable(e);
    } 
  }


}
