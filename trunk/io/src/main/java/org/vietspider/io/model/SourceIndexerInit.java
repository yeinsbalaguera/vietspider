/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.vietspider.bean.website.Website;
import org.vietspider.bean.website.Websites;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.io.websites2.WebsiteStorage;
import org.vietspider.model.Source;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 28, 2008  
 */
public class SourceIndexerInit extends SourceIndexerHandler implements Runnable {
  
  private int counter = 1;
  
  public SourceIndexerInit() {
    new Thread(this).start();
  }
  
  public synchronized void run() {
    IndexWriter writer = createIndexModifier(true);
    LogService.getInstance().setMessage("INDEXER", null, "Start index source store...");
    if(writer != null) {
      index(writer, UtilFile.getFolder("sources/sources/"));
    }
    LogService.getInstance().setMessage("INDEXER", null, "Finish index source store...");
    counter = -1;
    try {
      writer.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    notifyAll();
  }
  
  boolean isIndexing() { return counter > -1; }
  
  private void index(IndexWriter writer, File folder) {
    File [] files = folder.listFiles();
    if(files == null) return;
    
    List<File> directories = new ArrayList<File>();
    
    for(File file : files) {
      if(file.isDirectory()) {
        if(file.getName().equalsIgnoreCase("DUSTBIN")) continue;
        directories.add(file);
        continue;
      } 
      
      String name = file.getName();
      if(file.length() < 1 
          || name.indexOf('.') < 1 
          || name.indexOf(".v.") > 0) continue;
      
      Source source = loadSource(file);
      if(source == null) continue;

      String id = String.valueOf(source.getFullName().hashCode());
      
      try {
        Document document = toDocument(id, source);
        if(document == null) continue;
        writer.addDocument(document);
        
        String address = Websites.toAddressWebsite(source.getHome()[0]);
        Website website = createWebsite(address, "vn", Website.CONFIGURATION);
        website.setSource(source.getFullName());
        website.setCharset(source.getEncoding());
        WebsiteStorage.getInstance().save(website);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      counter++;
      
      if(counter%100 != 0)  continue;
      
      try {
        writer.commit();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        Thread.sleep(500);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

    }
    
    for(File directory : directories) {
      index(writer, directory);
      try {
        writer.commit();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }

      try {
        Thread.sleep(1000);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
   
  }
  
}
