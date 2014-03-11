/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.server.handler.cms;

import java.io.OutputStream;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 28, 2007
 */
public interface FileWriter {
  
  public void write(OutputStream output, String name) throws Exception ;
  
}
