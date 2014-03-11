/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.berkeleydb;

import java.io.File;

import org.vietspider.common.io.MD5Hash;
import org.vietspider.je.codes.Md5UrlDatabase;
import org.vietspider.link.V_URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 10, 2009  
 */
public class TestMd5Database {
  
  public static void main(String[] args) throws Throwable {
    File folder = new File("D:\\Temp\\");
    Md5UrlDatabase database = new Md5UrlDatabase(folder, "abc", "123", -1, true);
    
    String url = "file:///F:/Downloads/je-3.3.75/je-3.3.75/docs/java/index.html";
    V_URL v_url = new V_URL(url);
    MD5Hash mdHash = MD5Hash.digest(v_url.toNormalize());
    
    database.save(mdHash, 100);
    
    System.out.println(database.search(mdHash));
    
    url = "http://thuannd:9244/site/site/site/site/site/site/site/DOMAIN/1/10.05.2009";
    v_url = new V_URL(url);
    System.out.println(v_url.toNormalize());
    mdHash = MD5Hash.digest(v_url.toNormalize());
    
    database.save(mdHash, 30);
    
    url = "http://thuannd:9244/site/site/site/site/DOMAIN/1/10.05.2009";
    v_url = new V_URL(url);
    System.out.println(v_url.toNormalize());
    mdHash = MD5Hash.digest(v_url.toNormalize());
    
    System.out.println(database.search(mdHash));
    
    url = "http://www20.24h.com.vn/news/detail/55/228541";
    v_url = new V_URL(url);
    System.out.println(v_url.toNormalize());
    mdHash = MD5Hash.digest(v_url.toNormalize());
    
    database.save(mdHash, 30);
    
    System.out.println("cai cuoi cung ta co " + database.search(mdHash));
  }
}
