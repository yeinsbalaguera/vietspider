/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.image;

import java.io.InputStream;

import org.vietspider.bean.Image;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 11, 2007  
 */
public class ImageData {
  
  private int counter;
  
  private String link;
  
  private Image image;
  private InputStream inputStream;
  
//  private int width;
//  private int height;
  
  public ImageData() {
    image  = new Image();
  }
  
  public ImageData(String metaId, int counter) {
    this.counter = counter;
    this.image = new Image(metaId, counter);
  }
  
  public int getCounter() { return counter; }

  public String getLink() { return link; }

  public void setLink(String link) { this.link = link; }

  public Image getImage() { return image; }

  public InputStream getInputStream() { return inputStream; }

  public void setInputStream(InputStream inputStream) { this.inputStream = inputStream;  }
  
//  public int getWidth() { return width; }
//  public void setWidth(int width) { this.width = width;  }
//
//  public int getHeight() { return height; }
//  public void setHeight(int height) { this.height = height; }

}
