/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.parser.rss2;

import org.vietspider.serialize.NodeMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 23, 2007  
 */
@NodeMap("link")
public class MetaLink {
  
  public final static String DATA_HREF = "alternate";
  
  @NodeMap(value = "rel", attribute=true)
  private String rel = DATA_HREF;
  
  @NodeMap(value = "type", attribute=true)
  private String type = "text/html";
  
  @NodeMap(value = "href", attribute=true)
  private String href = "";
  
  public MetaLink() {
  }
  
  public MetaLink(String href) {
    this.href = href;
  }

  public String getHref() { return href; }
  public String getRealHref() {
    if(!rel.equals(DATA_HREF)) return null;
    return href; 
  }
  public void setHref(String value) { href = value; }
  
  public String getType() { return type; }
  public void setType(String value) { type = value; }

  public String getRel() { return rel; }
  public void setRel(String value) { rel = value; }
  
  

}
