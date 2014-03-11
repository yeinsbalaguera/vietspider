/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.webui.cms.render;

import java.io.OutputStream;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 28, 2007
 */
public interface PMenuRenderer {
  
  public void write(OutputStream output, String viewer, String [] cookies, String [] params) throws Exception;
  
}
