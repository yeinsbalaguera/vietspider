/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import org.vietspider.common.io.LogService.DefaultLog;
import org.vietspider.common.io.LogService.Log;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 15, 2009  
 */
class LogWebsite {

  private volatile static Log LOG;

  private synchronized static Log getInstance() {
    if(LOG == null) LOG = new DefaultLog(); 
    return LOG; 
  }

  private static void createInstance(Class<?> clazz) {
    try {
      LOG = (Log)clazz.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
