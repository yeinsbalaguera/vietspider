/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.export;

import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.SetterMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 2, 2010  
 */
@NodeMap("excel-model")
public class ExcelModel {

  @NodeMap("domain")
  private String domainId;
  @NodeMap("page")
  private int page;
  @NodesMap(value = "headers", item="item")
  private String[] headers;
  
  @GetterMap("domain")
  public String getDomainId() { return domainId; }
  @SetterMap("domain")
  public void setDomainId(String domainId) { this.domainId = domainId; }
  
  @GetterMap("page")
  public int getPage() { return page; }
  @SetterMap("page")
  public void setPage(int page) { this.page = page; }
  
  @GetterMap("headers")
  public String[] getHeaders() { return headers; }
  @SetterMap("headers")
  public void setHeaders(String[] headers) { this.headers = headers; }
  
}
