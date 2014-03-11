/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.model;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.vietspider.bean.website.Website;
import org.vietspider.bean.website.Websites;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.io.websites2.WebsiteStorage;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 28, 2008  
 */
class SourceIndexer2 extends SourceIndexerHandler implements Runnable {

  private Hashtable<String, Integer> queue;

  void setQueue(Hashtable<String, Integer> q) { 
    this.queue = q;
    new Thread(this).start();
  }

  boolean isWriting(){ return queue != null; }

  public synchronized void run() {
    index();
    queue = null;
    notifyAll();
  }

  private void index() {
    IndexWriter writer = createIndexModifier(true);
    if(writer == null || queue == null) return ;

    Iterator<String> iterator = queue.keySet().iterator();

    while(iterator.hasNext()) {
      String value = iterator.next();
      Integer status = queue.get(value);
      Source source = null;
      try {
        File file = new File(UtilFile.getFolder("sources/sources/"), value);
        source = loadSource(file);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      if(source == null) continue;

      if(status == SourceIndexerService.DELETE) {
        value = String.valueOf(value.hashCode());
        try {
          writer.deleteDocuments(new Term(SourceIndexerService.ID, value));
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
        String addressWebsite = Websites.toAddressWebsite(source.getHome()[0]);
        Website website = createWebsite(addressWebsite, "vn", Website.UNAVAILABLE);
        website.setSource(source.getFullName());
        website.setCharset(source.getEncoding());
        WebsiteStorage.getInstance().save(website);
        continue;
      } 

      String id  = String.valueOf(source.getFullName().hashCode());

      try {
        writer.deleteDocuments(new Term(SourceIndexerService.ID, id));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        org.apache.lucene.document.Document document = toDocument(id, source);
        if(document != null) {
          writer.addDocument(document);

          String address = Websites.toAddressWebsite(source.getHome()[0]);
          Website website = createWebsite(address, "vn", Website.CONFIGURATION);
          website.setSource(source.getFullName());
          website.setCharset(source.getEncoding());
          WebsiteStorage.getInstance().save(website);
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }

    queue.clear();

    try {
      writer.commit();
    } catch (Exception exp) {
      LogService.getInstance().setThrowable(exp);
    } finally {
      try {
        writer.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

}
