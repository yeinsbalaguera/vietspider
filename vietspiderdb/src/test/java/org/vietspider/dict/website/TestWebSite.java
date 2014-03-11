/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.dict.website;

import org.vietspider.bean.website.Website;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 6, 2009  
 */
public class TestWebSite {
  public static void main(String[] args) {
//     String address = "http://www11.nh";
//     String address = "http:// http://www.totalvideogames.com/PlayStation-3/news/PS3-Manufacturing-Costs-Reduced-By-70-14439.html";
     String address = "http://www.vacollective.org>Vietnamese Artists Collective";
     System.out.println(Website.toHost(address));
     
  }
}
