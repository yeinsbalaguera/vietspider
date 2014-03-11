/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html;

import org.vietspider.chars.URLEncoder;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 24, 2007  
 */
public class TestURLCreator {

  public static void main(String[] args) throws Exception {
//    URI uri = new URI("http://www.giadinh.net.vn/aaa/bbb/?direct=4ae2cf3bb0f9c39b5e5cd468ffc8d19c&column=112&lang=Vn");
//    uri = uri.resolve("../../?direct=455c6d31e7e5e49f8dea243641ca29f2&column=150&nID=3500&lang=Vn");
//    System.out.println(uri.getPath());
//    URI uri  = new URI("http://www.htv.com.vn//data//news//2007/9/104389/heroin200.jpg");
//    uri =  uri.normalize();
//    URL url = uri.toURL();
//    System.out.println(url.getPath());
//    
//    
//    String address = "http://thuan//data/news/2007/9/104401/?direct=4ae2cf3bb0f9c39b5e5cd468ffc8d19c&column=112&lang=Vn";
//    uri = new URI(address);
//    uri =  uri.normalize();
////    address = address.replaceAll("//", "/");
////    String link = "yhocTPHCM.asp";
//    System.out.println(uri.getPath());
//    System.out.println("query " +new URI(address).getQuery());
    
    
    URLEncoder creator  = new URLEncoder();
    System.out.println(creator.encode("http://www.itpromotion.gov.vn/index.php?id=540&tx_ttnews[tt_news]=120&tx_ttnews[backPid]=540&cHash=9be9134240"));;
  
    
  }
}
