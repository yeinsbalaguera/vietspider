/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.plugin.nukeviet;

import java.io.File;

import org.vietspider.bean.Article;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.database.DatabaseReader;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.model.plugin.nukeviet.NukeVietSyncData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 8, 2011  
 */
public class TestNukeVietSynchronized {
  
  private static void format(NukeVietSyncData data) throws Exception {
    NukeVietContentFormatter2 formatter2 = new NukeVietContentFormatter2();
    DatabaseReader getter = DatabaseService.getLoader();
    Article article = getter.loadArticle(data.getArticleId());
    String html = article.getContent().getContent();
    
    StringBuilder builder = new StringBuilder();
    builder.append("<meta  http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />");
    builder.append(html);
    
    File file = UtilFile.getFile("track/temp/", "raw."+ String.valueOf(data.getArticleId()) + ".html");
    new DataWriter().save(file, builder.toString().getBytes(Application.CHARSET));
    
    builder.setLength(0);
    builder.append("<meta  http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />");
    builder.append(formatter2.build(html));
    
    file = UtilFile.getFile("track/temp/", "format."+ String.valueOf(data.getArticleId()) + ".html");
    new DataWriter().save(file, builder.toString().getBytes(Application.CHARSET));
  }

  public static void main(String[] args) throws Throwable {
    try {
//      File file  = new File("D:\\java\\test\\vsnews\\data\\");
      File file = new File("D:\\VietSpider Build 17\\data\\");

      System.setProperty("save.link.download", "true");
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");

      NukeVietSyncData data = new NukeVietSyncData();
      data.setDebug(true);
      data.setArticleId("201104112231330003");
      data.setCategoryId("9");
      data.setLinkToSource("<a href=\"$source.link\">$source.name</a>");

      NukeVietSyncArticlePlugin plugin = new NukeVietSyncArticlePlugin();
      plugin.sendArticle(data, false, false);
      
//      format(data); 
      
    } catch (Throwable e) {
      e.printStackTrace();
    } finally {
//      try {
//        Thread.sleep(15*1000l);
//      } catch (Exception e) {
//        // TODO: handle exception
//      }
      System.exit(0);
    }
  }

}
