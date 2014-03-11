/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.js;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 8, 2008  
 */
public class JsFunction {
  
  private String name;
  
  private String value;
  
  public JsFunction(){}
  
  public JsFunction(String name) {
    this.name = name;
  }
  
  public JsFunction(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getValue() { return value; }
  public void setValue(String value) { this.value = value; }
  
}
