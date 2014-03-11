/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.browser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 25, 2009  
 */
public interface OpenLinkListener {
  
  public void open(OpenLinkEvent event); 
  
  public static class OpenLinkEvent {
    
    private String link;

    public OpenLinkEvent(String link) {
      this.link = link;
    }
    
    public String getLink() { return link; }
    
  }

}
