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
@NodeMap("feed")
public class FeedItem extends RSSItem implements IMetaChannel {

  @NodeMap("id")
  private String id;
  
  @NodeMap("generator")
  private String generator = "";
  
  @NodeMap("author")
  private String author;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getAuthor() { return author; }
  public void setAuthor(String author) { this.author = author; }
  
  public void setGenerator(String generator){ this.generator = generator; }
  public String getGenerator(){ return generator; }
}
