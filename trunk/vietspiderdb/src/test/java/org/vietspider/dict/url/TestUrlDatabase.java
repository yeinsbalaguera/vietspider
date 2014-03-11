/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.dict.url;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.vietspider.common.io.RandomAccess;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.url.UrlDatabase;
import org.vietspider.paging.PageIOs;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jul 14, 2009
 */
public final  class TestUrlDatabase  extends RandomAccess {
  
  private static File dbFile  = new File("D:\\Temp\\url\\BLOG.Blog Vi-u1EC7t nam.360Plus\\");
  private static String dbName = "360YahooPlus";

  public static void searchPage(int page) throws Throwable {
    UrlDatabase mdDatabase = new UrlDatabase(dbFile, 1024*1024);
    
    System.out.println(mdDatabase.getTotalPage(10));
    
    List<String> list =  mdDatabase.loadUrlByPage(page, 10);
    
    for(int i = 0;  i < list.size(); i++) {
      System.out.println(list.get(i));
    }
    
    mdDatabase.close();
  }

  public void save() throws Exception{
    UtilFile.deleteFolder(dbFile, false);
    UrlDatabase urlDatabase = new UrlDatabase(dbFile, 1024*1024);
    RandomAccessFile random = null;

    try {
      File textFile = new File("F:\\Projects\\bak\\" + dbName + ".txt");
      random = new RandomAccessFile(textFile, "r");
      random.seek(0);
      while(true) {
        String url = readLine(random);
        if(url == null) break;
        urlDatabase.save(url);
      }
    } catch (Throwable e) {
      e.printStackTrace();
    } finally {
      try {
        if(random != null) random.close();
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
    
    PageIOs.getInstance().commit();
    System.out.println(" create database successfull!");
    
//    String url = "http://twitter.com/NextGenGOP";
//    MD5Hash md5Hash = MD5Hash.digest(new V_URL(url, null).toNormalize());
//    String savedUrl = urlDatabase.search(md5Hash);
//    System.out.println("url:       " + url);
//    System.out.println("saved url: " + savedUrl);
    
    urlDatabase.close();
  }

  public static void main(String[] args) throws Throwable{
    TestUrlDatabase test = new TestUrlDatabase();
    test.save();
    
//    searchPage(20715);
    
//    String  url = "http://twitter.com/NextGenGOP";
//    MD5Hash md5Hash = MD5Hash.digest(new V_URL(url, null).toNormalize());
//    System.out.println(" thay co "+ md5Hash);
    
    System.exit(0);
  }
}

