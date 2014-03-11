/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.io.File;
import java.util.List;

import org.vietspider.bean.Article;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;



/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 23, 2011  
 */
public class TopArticleService extends Thread {

  private volatile static TopArticleService instance;

  public final synchronized static TopArticleService getInstance() {
    if(instance == null) instance = new TopArticleService();
    return instance;
  }

  private TopArticleCollection collection;
  private boolean write = false;

  public TopArticleService() {
    File file = UtilFile.getFile("content/summary/", "top-article.xml");
    if(file.exists() && file.length() > 0) {
      try {
        String xml = new String(RWData.getInstance().load(file), Application.CHARSET);
        collection = XML2Object.getInstance().toObject(TopArticleCollection.class, xml);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    if(collection == null) collection =  new TopArticleCollection();

    Application.addShutdown(new Application.IShutdown() {
      public String getMessage() { return "Save top articles";}
      public void execute() {
        save();
      }
    });
    this.start();
  }

  public void run() {
    while(true) {
      save();
      try {
        Thread.sleep(5*60*1000l);
      } catch (Exception e) {
      }
    }
  }

  public void add(Article article) {
    collection.add(article);
    write = true;
  }
  
  public List<TopArticles> getCollection() {
    return collection.getList();
  }

  private void save() {
    if(!write) return;
    File file = UtilFile.getFile("content/summary/", "top-article.xml");
    try {
      String xml = Object2XML.getInstance().toXMLDocument(collection).getTextValue();
      RWData.getInstance().save(file, xml.getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    write = false;
  }

}
