/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io.downloaded;

import org.vietspider.common.io.MD5Hash;
import org.vietspider.common.text.NameConverter;
import org.vietspider.je.codes.Md5UrlDatabases;
import org.vietspider.link.V_URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 11, 2009  
 */
public class TestJEDatabase {
  
  public static String getCodeName(String home) {
    String string = home.toLowerCase().trim() ;
    int idx = string.indexOf("://") ;
    if(idx > 0) {
      string = string.substring(idx + 3, string.length());
    }
    
    idx = string.indexOf('/') ;
    if(idx > 0) string = string.substring(0, idx) ;

    if(string.startsWith("www")) {
      if(string.length() > 3 && string.charAt(3) != '.') {
        idx = string.indexOf('.') ;
        string = "www." + string.substring(idx + 1, string.length()) ;
      }
    } else {
      string = "www." + string ;
    }
   
    StringBuilder builder = new StringBuilder();
    idx = 0; 
    while(idx < string.length()) {
      char c = string.charAt(idx);
      if(Character.isLetterOrDigit(c) || c == '.') {
        builder.append(c);  
      } else {
        builder.append('_');
      }
      idx++;
    }
    return builder.toString();
  }
  
  public static void main(String[] args) throws Throwable {
    System.setProperty("vietspider.data.path", "D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    String group = "ARTICLE";
    
//    String url = "http://www.ictnews.vn/Home/video/Nokia-sieu-ben/2009/07/2CMSV8619486/View.htm";
//    String url = "http://www.ictnews.vn/Home/Gossip/Mang-di-dong-Nhat-Ban-qua-tai-vi-phim-sex/2009/07/2VCMS3719484/View.htm";
    String url = "http://www.tienphong.vn/Tianyon/Index.aspx?ArticleID=165993&ChannelID=9";
    String codeName = getCodeName(url);
    NameConverter converter = new NameConverter();
    Md5UrlDatabases databases =  new Md5UrlDatabases(group, NameConverter.encode(codeName), codeName
        , 6*1023, 5, 1024*1024);
    
    MD5Hash urlId =  MD5Hash.digest(new V_URL(url, null).toNormalize());
    System.out.println(databases.read(urlId, true));
  }
  
}
