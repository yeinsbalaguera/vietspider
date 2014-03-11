/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.vietspider.content.nlp.common.ViDateTimeExtractor;
import org.vietspider.locale.DetachDate;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 9, 2007  
 */
public class TestDateTimeExtractor {
  public static void main(String[] args) {
//  Pattern  pattern = Pattern.compile("\\s\\d{1,2}?:\\d{1,2}?:\\d{1,2}?\\s");
//  Matcher matcher = pattern.matcher("toi dang 34:73:89 o day");

//  Pattern pattern = Pattern.compile("\\s\\d{1,2}?\\s+giờ\\s+\\d{1,2}?\\s");
//  Matcher matcher = pattern.matcher("toi dang  56 giờ 64 o day");

//  Pattern pattern = Pattern.compile("\\s\\d{1,2}?h\\d{1,2}?\\s");
//  Matcher matcher = pattern.matcher("toi dang  56h64 o day");

//  Pattern pattern = Pattern.compile("ngày\\s+\\d{1,2}?\\s+tháng\\s+\\d{1,2}?\\s+năm\\s+\\d{4}");
//  Matcher matcher = pattern.matcher("'cập nhật' ngày 01 tháng 10 năm 1993 das");
//  
//  Pattern pattern = Pattern.compile("\\d{1,2}[/]\\d{1,2}");
//  Matcher matcher = pattern.matcher("'cập nhật' ngày 01/09 tháng 10 năm 1993 das");
//
//  matcher.find();
//  System.out.println(matcher.group());

    ViDateTimeExtractor extractor = new ViDateTimeExtractor();
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy hh:mm a");
    
    String [] values = {
//        "cập nhật vào ngày 01 tháng 10 năm 1993 lúc 3h36 am '",
//        "Ngày cập nhật :31/01/2007 11:46",
//        "SGGP:: Cập nhật ngày 09/10/2007 lúc 10:08\"(GMT+7) ",
//        "Lao Động số 234 Ngày 09/10/2007 Cập nhật: 10:18 PM, 08/10/2007  ",
//        "Tin không khí lạnh tăng cường - Tin phát 4h30 15/10-2007 ",
//        "15/10/2007",
//        "Ngày cập nhật :31/01/2007 11:46",
//        " TIN BÃO KHẨN CẤP (Cơn bão số 5) - Tin phát lúc 5h30 04/10-2007",
//         "05:26' AM - Thứ bảy, 02/06/2007",
//         "Theo Tùng Anh (HHT) (15/10/07)",
//         "Chương trình truyền hình dự kiến ngày 15-10-2007",
//         "Thứ hai, 15/10/2007, 07:58 GMT+7",
//         "Thứ bảy , 13 Tháng mười 2007, 14:03 GMT+7",
//         "Thứ bảy, 16/06/2007 18:00 GMT+7",
//         "02:50' PM - Thứ bảy, 13/10/2007",
//         "Số lượt đọc:  30 -  Cập nhật lần cuối:  11/10/2007 02:39:10 PM",
//         "tin đăng 24/7",
//         "Thứ tư , 12  / 9 / 2007, 7: 55 (GMT+7)",
//         "[08.10.2007 20:31] ",
//         "08/12",
//         "2008/12/01 17:51:09",
         "10:07 GMT - Thứ Sáu, 3 tháng 4, 2009",
         "Cập nhật lúc 14:57'  7/5/2009"
    };
    
    for(String input : values) {
      DetachDate detach = extractor.detect(input.toLowerCase());
      if(detach  == null) {
        System.out.println(input + " =>  no value ");
        continue ;
      }
      Date date  = detach.toDate();
      System.out.println(input + " => " + detach.getScrore()+" => " + dateFormat.format(date));  
    }
    
//    String compute = "cập nhật cách đây 6 giờ";
    String compute = "cập nhật cách đây 2 giờ 30 phút";
//    int idx = value.indexOf(longAgo);
//    System.out.println(idx > -1 && hasTimeWord(value);
//    Date date = extractor.computeDate(compute);
//    if(date == null) return ;
//    System.out.println(dateFormat.format(date));
    
//    String txt = "Hayward";
//    DetachDate detachDate = extractor.extractDateTime(txt);
//    System.out.println(detachDate.getScrore());

  }
}
