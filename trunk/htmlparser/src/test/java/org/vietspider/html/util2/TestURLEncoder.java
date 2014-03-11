/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util2;

import java.net.URI;

import org.vietspider.chars.URLEncoder;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 14, 2008  
 */
public class TestURLEncoder {
  
  public static void main(String[] args) throws Exception {
//    String address = "http://conghung.com/index.php?conghung=mod:news|act:category|catID:13";
//    String address = "http://privatesend.asp?method=Topic&mname=Bo`_Con";
//    String address = "http://www9.ttvnol.com/forum/f_474/Nhung-ai-ho-Ha-vao-day-giao-luu-di-biet-dau-chung-ta-lai-co-ho^-^/899621/trang-14.ttvn";
//    String address = " http://www9.ttvnol.com/forum/f_474/(¯`°•-Ca-nha-co-ai-ho-Luc-(Loc)-ho-Tu-ko-thia-Vo-day-diem-danh-cai-nhi-•°´¯)/967690/trang-9.ttvn";
    
    
//    String address = "http://www9.ttvnol.com/forum/thanhhoa/(^-^)Noi-Dang-ky-Dat-title-Cho-cac-thanh-vien-cua-Box-Danh-sach-thanh-vien-moi-nhat-trang-90-Welcome-♫♫♫/121501/trang-43.ttvn";
    
    String address = "http://www9.ttvnol.com/forum/f_474/(¯`°•-♥-♠♪-Gia-dinh-Le-Luu-cung-chuc-tan-xuan-Cung-hi-phat-tai-♪♠♥-•°´¯)/980849/trang-63.ttvn";
    
    URLEncoder encoder = new URLEncoder();
    String value  = encoder.encode(address);
    System.out.println(value);
    URI uri = new URI(value);
  }
  
}
