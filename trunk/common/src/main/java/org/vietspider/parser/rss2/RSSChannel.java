/**
 * Copyright 2004-2006 The VietSpider        All rights reserved.
 *
 * Created on January 24, 2007, 8:58 PM
 */
package org.vietspider.parser.rss2;

import org.vietspider.serialize.NodeMap;


/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2006
 */
@NodeMap("channel")
public class RSSChannel extends RSSItem implements IMetaChannel {
  
  @NodeMap("generator")
  private String generator = "";
  
  public RSSChannel(){}
  
  public void setGenerator(String generator){ this.generator = generator; }
  
  public String getGenerator(){ return generator; }

}
