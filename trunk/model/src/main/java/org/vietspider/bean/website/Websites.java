/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.bean.website;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.text.SWProtocol;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 4, 2008  
 */
@NodeMap("websites")
public class Websites {
  
  private final static String slash = "/";
  private final static String question = "?";
  
  public static String toAddressWebsite(String address) {
    int from  = SWProtocol.LENGTH + 1;
    int index = address.indexOf(slash, from);
    if(index < 1) index = address.indexOf(question, from);
    if(index < 1) return address; 
    return address.substring(0, index);
  }
  
  @NodesMap(value="list", item="item")
  private List<Website> list;
  @NodeMap("page")
  private int page = 1;
  @NodeMap("page-size")
  private int pageSize = 25;
  @NodeMap("totalPage")
  private int totalPage = 0;
  @NodeMap("date")
  private String date = "";
  @NodeMap("ip")
  private String ip = "";
  @NodeMap("status")
  private int status = -2;
  @NodeMap("language")
  private String language = null;
  
  public Websites() {
    list = new ArrayList<Website>();
  }

  public List<Website> getList() { return list; }
  public void setList(List<Website> list) { this.list = list; }

  public int getPage() { return page; }
  public void setPage(int page) { this.page = page; }

  public int getTotalPage() { return totalPage; }
  public void setTotalPage(int totalPage) { this.totalPage = totalPage; }

  public String getDate() { return date; }
  public void setDate(String date) { this.date = date; }

  public int getStatus() { return status; }
  public void setStatus(int v) { this.status = v; }

  public String getLanguage() { return language; }
  public void setLanguage(String language) { this.language = language; }

  public String getIp() { return ip; }
  public void setIp(String ip) { this.ip = ip; }

  public int getPageSize() { return pageSize; }
  public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
