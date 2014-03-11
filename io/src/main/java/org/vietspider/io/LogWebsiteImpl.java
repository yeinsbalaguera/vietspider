/***************************************************************************
 * Copyright 2004-2006 The VietSpider Studio All rights reserved.  * 
 **************************************************************************/
package org.vietspider.io;

import org.vietspider.common.io.ExceptionWriter;

/**
 * @by thuannd (nhudinhthuan@yahoo.com)
 * Jun 26, 2005
 */
public final class LogWebsiteImpl extends LogDataImpl {
  
  public LogWebsiteImpl() {
    print = "true".equals(System.getProperty("vietspider.test"));
    writer = new ExceptionWriter("track/logs/website/");
    writer.setPrint(print);
    new Thread(writer).start();
  }

}
