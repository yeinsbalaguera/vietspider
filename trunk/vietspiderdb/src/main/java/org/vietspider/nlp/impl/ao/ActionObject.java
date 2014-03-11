/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl.ao;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 24, 2011  
 */
public class ActionObject {
  
  private String data;
  private String[] tags;
  
  public ActionObject() {
    
  }
  
  public ActionObject(String data, String...tags) {
    this.data = data;
    this.tags = tags;
  }
  
  public String getData() { return data; }
  public void setData(String data) { this.data = data; }
  
  public String[] getTags() { return tags; }
  public void setTags(String[] tags) { this.tags = tags; }

  @Override
  public String toString() { return data; }
}
