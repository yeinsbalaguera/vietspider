/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.database;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 14, 2009  
 */
public class WebsiteSaveSiteService {
  
  private static volatile WebsiteSaveSite instance;
  
  public static void setInstance(WebsiteSaveSite instance) {
    WebsiteSaveSiteService.instance = instance;
  }
  
  public static void save(String address, String html) {
    if(instance == null) return;
    if(html == null || html.length() < 10) return;
    try {
      instance.save(address, html);
    } catch (Exception e) {
      LogService.getInstance().setThrowable("SITE", e, address);
    }
  }

  public static interface WebsiteSaveSite {
    
    public void save(String address, String html) throws Exception ;
    
  }

}
