/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.parser.rss2;

import java.net.URLDecoder;

import org.vietspider.chars.URLEncoder;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 17, 2008  
 */
public class TestURLEncoder2  {
  private static String unescape(String s) {
    StringBuffer sbuf = new StringBuffer () ;
    int l  = s.length() ;
    int ch = -1 ;
    int b, sumb = 0;
    for (int i = 0, more = -1 ; i < l ; i++) {
      /* Get next byte b from URL segment s */
      switch (ch = s.charAt(i)) {
      case '%':
        ch = s.charAt (++i) ;
        int hb = (Character.isDigit ((char) ch) 
            ? ch - '0'
                : 10+Character.toLowerCase((char) ch) - 'a') & 0xF ;
        ch = s.charAt (++i) ;
        int lb = (Character.isDigit ((char) ch) 
            ? ch - '0'
                : 10+Character.toLowerCase((char) ch) - 'a') & 0xF ;
        b = (hb << 4) | lb ;
        break ;
      case '+':
        b = ' ' ;
        break ;
      default:
        b = ch ;
      }
      /* Decode byte b as UTF-8, sumb collects incomplete chars */
      if ((b & 0xc0) == 0x80) {     // 10xxxxxx (continuation byte)
        sumb = (sumb << 6) | (b & 0x3f) ; // Add 6 bits to sumb
        if (--more == 0) sbuf.append((char) sumb) ; // Add char to sbuf
      } else if ((b & 0x80) == 0x00) {    // 0xxxxxxx (yields 7 bits)
        sbuf.append((char) b) ;     // Store in sbuf
      } else if ((b & 0xe0) == 0xc0) {    // 110xxxxx (yields 5 bits)
        sumb = b & 0x1f;
        more = 1;       // Expect 1 more byte
      } else if ((b & 0xf0) == 0xe0) {    // 1110xxxx (yields 4 bits)
        sumb = b & 0x0f;
        more = 2;       // Expect 2 more bytes
      } else if ((b & 0xf8) == 0xf0) {    // 11110xxx (yields 3 bits)
        sumb = b & 0x07;
        more = 3;       // Expect 3 more bytes
      } else if ((b & 0xfc) == 0xf8) {    // 111110xx (yields 2 bits)
        sumb = b & 0x03;
        more = 4;       // Expect 4 more bytes
      } else /*if ((b & 0xfe) == 0xfc)*/ {  // 1111110x (yields 1 bit)
        sumb = b & 0x01;
        more = 5;       // Expect 5 more bytes
      }
      /* No need to test if the UTF-8 encoding is well-formed */
    }
    return sbuf.toString() ;
  }
  
  private static void test(String value) {
    URLEncoder encoder = new URLEncoder();

    System.out.println();
    String encodedValue  = encoder.encode(value);
    System.out.println("value: "+ value);
    System.out.println("encoded: "+ encodedValue);
    try {
      System.out.println(URLDecoder.decode(encodedValue, "utf-8"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    
//    try {
//      System.out.println("URL: "+new URL(value));
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
  }

  public static void main(String[] args) throws Exception {
//    test(" http://us.mc308.mail.yahoo.com/mc/showFolder?fid=Sen (] t&.rand=11¤54672853");
//
//    test(" automation_detail.php?Conpany=HiendaihoaJSC&CopyRight=hiendaihoa.com&lang=vn&id=5359&Article=Triễn lãm 4 trong 1 năm 2008: Sự kiện mang lại nhiều cơ hội kinh doanh&Designer=NguyenVanNgan&MyEmail=mail@hiendaihoa.com");
//
//    test("http://www.hoinongdan.org.vn/pictures/2008\\2008.8.15.15.24.15.jpg");
//    
//    test("http://www.hoinongdan.org.vn/?aaa=1&amp;bb=2");
//
//    test("http://www.hoinongdan.org.vn/?aaa=1&amp;bb=2");
    
    //http://blog.360.yahoo.com/blog-MGltzco9bqXxlToOPNwsIErIpu5_tQ--?cq=1&tag=ieudzo%2A.nhiu%60
//    test("http://blog.360.yahoo.com/blog-MGltzco9bqXxlToOPNwsIErIpu5_tQ--?cq=1&tag=ieudzo*.nhiu`");
    
//    test(":.bÔngiT'smE.::.♥");
    test("http://my.opera.com/chu%C3%B4nggi%C3%B3bu%E1%BB%93n/info/");
    
//    System.out.println( java.net.URLEncoder.encode(":.bÔngiT'smE.::.♥", "utf-8"));
  }
}
