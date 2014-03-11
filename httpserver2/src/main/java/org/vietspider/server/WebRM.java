/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server;

import org.vietspider.common.io.ResourceManager2;
import org.vietspider.db.SystemProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 16, 2008  
 */
public class WebRM extends ResourceManager2 {
  
  public WebRM() {
      super(WebRM.class, SystemProperties.getInstance().getProperties(), "WebUI");
  }

}
