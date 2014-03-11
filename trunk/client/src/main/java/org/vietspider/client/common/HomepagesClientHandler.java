/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.common.Application;
import org.vietspider.net.server.URLPath;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 27, 2008  
 */
public class HomepagesClientHandler {
  
  public String loadHomepages(String group, String category, String name, int page) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.homepage.list"),
        new BasicHeader("page", String.valueOf(page))
    };
    
    byte [] bytes = (group+"."+category+"."+name).getBytes(Application.CHARSET);
    ClientConnector2 connector = ClientConnector2.currentInstance();
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, headers);
    return new String(bytes, Application.CHARSET).trim();
  }
  
  //get total page of homepages
  public int loadTotalHomepages(String group, String category, String name) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "total.page.homepage")
    };
    
    byte [] bytes = (group + "." + category + "." + name).getBytes(Application.CHARSET);
    ClientConnector2 connector = ClientConnector2.currentInstance();
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, headers);
    String value = new String(bytes, Application.CHARSET);
    try {
      return Integer.parseInt(value.trim());
    } catch (Exception e) {
      return 0;
    } 
  }
  
  public void saveHomepages(String value) throws Exception {
    saveHomepages(ClientConnector2.currentInstance(), value);
  }
  
  public void saveHomepages(ClientConnector2 connector, String value) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "save.homepage.list")
    };
    byte [] bytes = value.getBytes(Application.CHARSET);
    connector.post(URLPath.DATA_HANDLER, bytes, headers);
  }
 
}
