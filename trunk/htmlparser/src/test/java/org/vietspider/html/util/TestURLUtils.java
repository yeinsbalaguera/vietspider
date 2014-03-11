/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util;

import org.vietspider.chars.URLUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 20, 2007  
 */
public class TestURLUtils {

  private URLUtils creator = new URLUtils();

  void test() {
    String address, link;
    /*address = "http://thtt.chinhphu.vn/aaa/hh";
    link = "../?thuan=zzz";
    System.out.println(creator.createURL(address, link));

    System.out.println("================================================");

    address = "http://thtt.chinhphu.vn/aaa/hh";
    link = "ttt/?thuan=zzz";
    System.out.println(creator.createURL(address, link));

    System.out.println("================================================");

    address = "http://thtt.chinhphu.vn/";
    link = "../?thuan=zzz";
    System.out.println(creator.createURL(address, link));

    System.out.println("================================================");

    address = "http://thtt.chinhphu.vn/?param=12";
    link = "../?param=zzz";
    System.out.println(creator.createURL(address, link));

    System.out.println("================================================");

    address = "http://thtt.chinhphu.vn/?param=12";
    link = "../";
    System.out.println(creator.createURL(address, link));

    System.out.println("================================================");

    address = "http://thtt.chinhphu.vn/?param=12";
    link = ",param2=zzz";
    System.out.println(creator.createURL(address, link));

    System.out.println("================================================");

    address = "http://thtt.chinhphu.vn/?param=12";
    link = "&param2=zzz";
    System.out.println(creator.createURL(address, link));

    System.out.println("================================================");

    address = "http://www.choray.org.vn/";
    link = "yhocTPHCM.asp";
    System.out.println(creator.createURL(address, link));

    System.out.println("================================================");*/

//    address = "http://www.vietduchospital.edu.vn/News_cat.asp?ID=2";
//    link = "news_detail.asp?ID=2&CID=2&IDN=7368";
//    System.out.println(creator.createURL(address, link));
    
//    address = "%3Cbr%3Ehttp://www9.ttvnol.com/uploaded2/littlemouse72/";
//    link = "resize%20of%20dsc_0083.jpg";
    
  
    address = "http://conghung.com/index.php?";
//    link = "conghung=mod:news|act:detail|newsid:3774";
    link = "?xf=sanpham&doftk={bkV6SjlFRE01RWpNeUlUTnhFelhrbG1a}";
    
//    System.out.println(creator.createURL(address, link));
    
    System.out.println(creator.getCanonical("http://www.pc2.evn.com.vn/./aaa/.././item.php?cat_id=143&news_id=1058"));
    System.out.println(creator.getCanonical("www.pc2.evn.com.vn/./aaa/.././item.php?cat_id=143&news_id=1058"));
    System.out.println(creator.getCanonical("/aaa/../.././item.php?cat_id=143&news_id=1058"));
    
    address  = "http://blog.360.yahoo.com/blog-zVFF5KQ2erNqqWXRVbtehw4-?cq=1&amp;l=6&amp;u=10&amp;mx=45&amp;lmt=5";
    System.out.println(creator.getCanonical(address));
    
    address = "http://vnexpress.net/GL/Xa-hoi/2008/../10/../abc/3BA07CD4/";
    System.out.println(creator.getCanonical(address));
    
    address = "http://vnexpress.net/GL/Xa-hoi/2008/../../abc/3BA07CD4?#comment";
    System.out.println(creator.getCanonical(address));
  }
  
  public static void main(String[] args) throws Exception {
    String address;
//    String address = "http://conghung.com/././aaab/.././aaab/./index.php?conghung=mod:news|act:detail|newsid:2188";
    address = "vnexpress.net/GL/Xa-hoi/2008/../../abc/3BA07CD4?#comment";
//    URL url  = new URL(address);
//    System.out.println(URLEncoder.encode("{"));
//    System.out.println(URLEncoder.encode("}"));
//    System.out.println(url.toURI());
    
//    System.out.println(url.toExternalForm());
//    URI uri = new URI(url.getPath());
//    System.out.println(uri);
//    System.out.println(uri.normalize());
    
    TestURLUtils test = new TestURLUtils();
    test.test();
//    test.testHashcode();
  }
}

