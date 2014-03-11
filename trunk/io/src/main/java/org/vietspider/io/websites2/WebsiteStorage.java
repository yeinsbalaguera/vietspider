/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.io.websites2;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.website.DefaultWebsiteDatabases;
import org.vietspider.db.website.IWebsiteDatabases;
import org.vietspider.db.website.WebsiteDatabases;
import org.vietspider.io.websites2.lang.WebsiteDetectors;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 1, 2009  
 */
public class WebsiteStorage {
  
  private static IWebsiteDatabases INSTANCE;
  
  public volatile static boolean DETECT = false; 

  public synchronized static IWebsiteDatabases getInstance() {
    if(INSTANCE == null) {
      if(Application.LICENSE != Install.SEARCH_SYSTEM) {
        INSTANCE = new DefaultWebsiteDatabases();
        return INSTANCE;    
      }
      
      SystemProperties system = SystemProperties.getInstance();
      
      DETECT = "true".equalsIgnoreCase(system.getValue("detect.website"));
      
      String value = system.getValue("website.master.store");
      if("true".equalsIgnoreCase(value)) {
        INSTANCE  = new WebsiteDatabases();
        new WebsiteDetectors((WebsiteDatabases)INSTANCE);
      } else {
        INSTANCE  = new RWebsiteDatabases();
      }
    }
    return INSTANCE;    
  }
}
