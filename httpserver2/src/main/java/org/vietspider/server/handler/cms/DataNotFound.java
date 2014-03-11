/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler.cms;

import org.apache.http.HttpException;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 4, 2007  
 */
@SuppressWarnings("serial")
public class DataNotFound extends HttpException {

  public String getMessage() { return "Data not found!"; }
  
}
