/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 14, 2009  
 */
public class TestV_URL2 {
  
  public static int count(String value, String pattern) {
    int count = 0;
    int start = 0;
    
    int time = 0;
    while(start < value.length()) {
     int index = value.indexOf(pattern, start);
     time++;
     if(time >= 1000) {
       LogService.getInstance().setMessage("V_URL", null, value + " and "+ pattern);
       break;
     }
     if(index < 0) {
//       System.out.println(" thay co counter la "+ pattern+ " : "+ count);
       return count; 
     }
     start = index + pattern.length();
     count++;
    }
//    System.out.println(" thay co counter la "+ pattern+ " : "+ count);
    return count;
  }
  
  
  public static void main(String[] args) {
    String value = " /index.php/component/content/article/74-tin-tc-khoa-hc/518-ao-to-ch-ca-hang-sa-in-thoi-di-ng/component/content/this.href/component/content/this.href/component/contentthis.href/";
    int time = 0;
    String pattern = "/component/content/";
    int index = 149;
    int newEnd = 167;
    while(count(value, pattern) >= 3 && newEnd < value.length()) {
//      time++;
//      if(time >= 1000) {
//        LogService.getInstance().setMessage("V_URL", null, value + " and "+ pattern+" and  "+index+" and "+newEnd);
//        break;
//      }
      value = value.substring(0, index) + value.substring(Math.min(newEnd, value.length()));
      System.out.println(value.substring(0, index)+ " ======== "+value.substring(Math.min(newEnd, value.length())));
    }
  }
}
