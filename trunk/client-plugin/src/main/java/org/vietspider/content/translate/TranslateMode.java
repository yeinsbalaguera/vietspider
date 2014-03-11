/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.translate;

import org.vietspider.serialize.NodeMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 19, 2011  
 */
@NodeMap("translate-mode")
public class TranslateMode {
  
  final static short SINGLE = 0;
  final static short COUPLE = 1;
  final static short PARAGRAPH = 2;

  @NodeMap("mode")
  private short mode = SINGLE;
  @NodeMap("application-id")
  private String applicationId = "A06CAD095F5CD226B6437F62669DBC9F42966F99";
  @NodeMap("from")
  private String from = null;
  @NodeMap("vi")
  private String to = "vi";
  
  public TranslateMode() {
    
  }

  public short getMode() { return mode; }
  public void setMode(short mode) { this.mode = mode; }

  public String getApplicationId() { return applicationId; }
  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getFrom() { return from; }
  public void setFrom(String from) { this.from = from; }

  public String getTo() { return to; }
  public void setTo(String to) { this.to = to; }
  
  
}
