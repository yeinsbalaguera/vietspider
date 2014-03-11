/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;

import java.io.File;
import java.net.URLEncoder;

import org.vietspider.common.io.RWData;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 18, 2008  
 */
public class TestGenerate360YahooLink {
  
  public static void main(String[] args) throws Exception {
//    String link  = "http://danhba.vdc.com.vn/tracuu/doanhnghiep/default.asp?MaNganhNghe=-1&MaThanhPho=-1&Page=";
    
//    http://www.thuvienkhoahoc.com/tusach/index.php?title=%C4%90%E1%BA%B7c+bi%E1%BB%87t%3AAllpages&from=C&namespace=0
//    String link  = "http://www.thuvienkhoahoc.com/tusach/index.php?title=%C4%90%E1%BA%B7c+bi%E1%BB%87t%3AAllpages&from=";
//    String link2 =  "&namespace=0";
    
//    String link = "http://blogsearch.google.com/blogsearch?hl=en&num=10&c2coff=1&lr=lang_vi&safe=active&ie=UTF-8&q=blogurl%3Aspaces.live.com+";
//    String link2 = "&btnG=Search+Blogs";
    char [] chars = "0123456789QWERTYUIOPSDFGHJKLZXCVBNMĐÊƠÔƯÂ".toCharArray();
    
    String  [] elements = {        };
    
    File file  = new File(TestGenerate360YahooLink.class.getResource("single.word.dict.cfg").toURI());
    String data = new String(RWData.getInstance().load(file), "utf-8");
    elements = data.split(";");
    
//    String start = "http://blogsearch.google.com/blogsearch?hl=en&ie=UTF-8&q=site%3Ablogspot.com+";
//    String end = "&btnG=Search+Blogs";
    
    String start = "http://profile.live.com/Results.aspx?q=";
    String end = "&submit=&Search.FirstName=&Search.LastName=&search.interests=&search.gender=&search.ageFrom=&search.ageTo=&search.location=&search.occupation=&tp=2";
    
//    String link  = "http://www.google.com.vn/search?hl=en&client=firefox-a&rls=org.mozilla%3Aen-US%3Aofficial&hs=HVO&q=site%3Ablogspot.com+";
    
    for(int i = 0; i < elements.length; i++) {
      String word = URLEncoder.encode(elements[i], "utf-8");
      String value  = start + word + end;
      System.out.println(value);
//      for(int j = 1; j < 1000; j += 100) {
//        System.out.println(value + String.valueOf(j));
//      }
      
//      System.out.println(link +String.value[i])+link2);
    }
  }
  
}
