/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 18, 2008  
 */
public class TestGeneratElexiconLink {

  public static void main(String[] args) throws Exception {
    int [] indexs = {
        8,10,12,11,2,7,4
    };
    String link = "http://www.metvuong.com/result_advance_search.php?action=action&search_id=&listing_type=Rent&category=Residential&listing_City=";
    for(int i = 0; i <indexs.length; i++) {
      String value  = link + String.valueOf(indexs[i]) ;
      for(int k = 1; k < 11; k++) {
        value += "&development=0&min_price=&min_price_currency=VND&max_price=&max_price_currency=VND&min_meter=&max_meter=&bed=&bath=&grade=&curpage=";
        value += String.valueOf(k);
      }
      System.out.println(value);
    }
  }

}
