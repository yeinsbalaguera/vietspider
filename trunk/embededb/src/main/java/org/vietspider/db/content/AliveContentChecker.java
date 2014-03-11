/***************************************************************************
 * Copyright 2001-2012 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.content;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.vietspider.bean.Article;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Apr 4, 2012  
 */
public class AliveContentChecker implements Runnable {
  
  public static AliveContentChecker INSTANCE = null;
  
  public synchronized static AliveContentChecker getInstance() {
    if(INSTANCE == null) INSTANCE = new AliveContentChecker();
    return INSTANCE;
  }
  
  private ConcurrentHashMap<String,String> map;
  
  public AliveContentChecker() {
    map = new ConcurrentHashMap<String,String>();
    new Thread(this).start();
  }
  
  public void add(Article article) {
    if(article.getMeta() == null) return;
    add(article.getId(), article.getMeta().getSource());
  }
  
  public void add(String id, String url) {
    map.put(id, url);
  }
  
  public void run() {
    while(true) {
      check();
      try {
        Thread.sleep(5*1000);
      } catch (Exception e) {
      }
    }
  }
  
  private void check() {
    Iterator<Map.Entry<String,String>> iterator = map.entrySet().iterator(); 
    while(iterator.hasNext()) {
      Map.Entry<String,String> entry = iterator.next();
      check(entry.getKey(), entry.getValue());
      try {
        Thread.sleep(2*1000l);
      } catch (Exception e) {
      }
    }
  }
  
  private void check(final String id, final String address) {
    new Thread() {
      public void run() {
        try {
          URL url = new URL(address);
          HttpURLConnection uc = (HttpURLConnection) url.openConnection();
          uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:11.0) Gecko/20100101 Firefox/11.0");
          uc.setRequestMethod("GET");
          uc.connect();
          int code = uc.getResponseCode();

          if(code != HttpURLConnection.HTTP_OK
              && code != HttpURLConnection.HTTP_ACCEPTED) {
            ((ArticleDatabases)ArticleDatabases.getInstance()).delete(id);
          }
        } catch (Exception e) {
          LogService.getInstance().setThrowable(AliveContentChecker.class.getSimpleName(), e);
        }
      }
    }.start();
  }

  public static void main(String[] args) throws Exception  {
    URL url = new URL("http://bdshanoimoi.com.vn/sanitc/forum_posts.asp?TID=8710&SID=zaa45766baface4df1e9485fe841d82b");
    HttpURLConnection uc = (HttpURLConnection) url.openConnection();
    uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:11.0) Gecko/20100101 Firefox/11.0");
    uc.setRequestMethod("GET");
    uc.connect();
    System.out.println(uc.getResponseCode());
    
  }
}
