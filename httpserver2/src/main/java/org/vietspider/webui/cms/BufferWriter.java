/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.webui.cms;

import java.io.OutputStream;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 28, 2007
 */
public class BufferWriter {
  
  protected OutputStream output;
  
  protected void append(String text) throws Exception { 
    if(text == null) return;
    try {
      output.write(text.getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setMessage("SERVER", e, null);
    }
  }
  
}
