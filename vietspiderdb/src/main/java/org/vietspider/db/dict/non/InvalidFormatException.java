/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.dict.non;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 15, 2009  
 */
@SuppressWarnings("all")
public class InvalidFormatException extends Exception {
  
  private String value;
  
  public InvalidFormatException(String value) {
    this.value = value;
  }

  public String getMessage() { return "Invalid format "+ value; }

  public String toString() { 
    return super.toString() + ": Invalid format "+ value; 
  }
}
