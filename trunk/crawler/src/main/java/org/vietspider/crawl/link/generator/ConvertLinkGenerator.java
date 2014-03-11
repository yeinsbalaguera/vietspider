/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.generator;

import org.vietspider.link.generator.Generator;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 18, 2009  
 */
//https://www.bpn.gov/CCRSearch/Search.aspx
//http://www.bbb.org/us/Find-Business-Reviews/#middle-result
public class ConvertLinkGenerator {
  
  private String sourceFullName;
  
  @SuppressWarnings("unused")
  public ConvertLinkGenerator(String sourceFullName, String...values) {
    this.sourceFullName = sourceFullName;
  }
  
  public short getType() { return Generator.CREATE_LINK_GENERATOR; }
  
}
