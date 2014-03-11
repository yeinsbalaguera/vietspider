/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.index3.monitor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2010  
 */
public class Query {

  private String pattern;
  private long total;
  
  public Query() {
    
  }
  
  public Query(String p, long t) {
    this.pattern = p;
    this.total = t;
  }
  
  public String getPattern() { return pattern; }
  public void setPattern(String pattern) { this.pattern = pattern; }
  
  public long getTotal() { return total; }
  public void setTotal(long total) { this.total = total;  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(pattern).append('/').append(total);
    return builder.toString();
  }
  
  public void fromString(String text) throws Exception {
    int idx = text.lastIndexOf('/');
    if(idx < 0) throw new Exception("Invalid Format");
    pattern = text.substring(0, idx);
    total = Long.parseLong(text.substring(idx+1).trim());
  }
  
}
