/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.parser.rss2;

import java.util.List;

import org.vietspider.parser.xml.XMLNode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 23, 2007  
 */

public interface IMetaItem {  
  
  public void setTitle(String title);  
  public String getTitle() ;
  
  public void setDesc(String desc);  
  public String getDesc();
  
  public void setImage(String image);  
  public String getImage();
  
  public void setTime(String time);  
  public String getTime();
  
  public void addLink(MetaLink link);
  public MetaLink getLink(int index);
  public List<MetaLink> getLinks();
  
  public void setNode(XMLNode node);  
  public XMLNode getNode();
  
  public XMLNode getItem(String name);  
  public String getValueItem(String name);
}

