/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.parser.rss2;

import org.vietspider.chars.refs.RefsEncoder;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 2, 2008  
 */
public class TestHTMLRef2 {
  public static void main(String[] args) throws Exception {
    
    RefsEncoder encoder = new RefsEncoder();
    String value =  "Tin rao của bạn đã được gửi";
      
    System.out.println(new String(encoder.encode(value.toCharArray())));
    
    value =  "Chúng tôi đã thêm tin rao bất động sản của bạn vào <a href=\"http://nik.vn\"><b>nik.vn</b></a> và tìm cách gửi tới người có nhu cầu bằng hệ thống phân tích thông minh.<br><br><a href=\"http://nik.vn\"><b>nik.vn</b></a> là trang web tìm kiếm nhà đất lớn cho phép tìm kiếm hàng trăm nghìn tin rao bất động sản từ khắp các websites ở Việt Nam.<br><br>Hy vọng <a href=\"http://nik.vn\"><b>nik.vn</b></a> tìm được đúng địa chỉ cho nội dung rao của bạn.<br><br>Thân ái!";
    System.out.println(new String(encoder.encode(value.toCharArray())));
    
    value =  "người cần";
    System.out.println(new String(encoder.encode(value.toCharArray())));
//    
//    value =  "Nguồn";
//    System.out.println(new String(encoder.encode(value.toCharArray())));
//    
//    System.out.println((long)30*24*60*60*1000);
////    
//    value =  "Mật khẩu";
//    System.out.println(new String(encoder.encode(value.toCharArray())));
//
//    
//    value =  "Đăng nhập";
//    System.out.println(new String(encoder.encode(value.toCharArray())));
//    
//    value =  "Đăng xuất";
//    System.out.println(new String(encoder.encode(value.toCharArray())));

  }
}
