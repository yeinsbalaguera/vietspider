/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import java.io.Serializable;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.common.Application;
import org.vietspider.net.server.URLPath;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 13, 2z008  
 */
@SuppressWarnings("serial")
public class PluginClientHandler  implements Serializable {
  
  public String send(String plugin, String action, String value) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", action),
        new BasicHeader("plugin.name", plugin)
    };
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = value != null ? value.getBytes(Application.CHARSET) : new byte[0];
    bytes = connector.post(URLPath.DATA_PLUGIN_HANDLER, bytes, headers);
    if(bytes == null || bytes.length < 1) return "";
    return new String(bytes, Application.CHARSET);
  }
  
}
