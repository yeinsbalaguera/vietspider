/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean;

import org.vietspider.serialize.NodeMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 26, 2007  
 */
@NodeMap("product-content")
public class ProductContent {
  
  @NodeMap("name")
  private String name;
  @NodeMap("content")
  private String content;
  @NodeMap("price")
  private String price;
  
  @NodeMap("image")
  private String image;
  
  public ProductContent() {
    
  }

  public String getName() { return name; }

  public void setName(String name) { this.name = name; }

  public String getContent() { return content; }

  public void setContent(String content) { this.content = content; }

  public String getPrice() { return price; }

  public void setPrice(String price) { this.price = price; }

  public String getImage() { return image; }

  public void setImage(String image) { this.image = image; }
}
