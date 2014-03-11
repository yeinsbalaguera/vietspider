/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.File;
import java.io.OutputStream;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 8, 2007
 */
public class ProxyFileHandler extends CMSHandler<String> {
  
  public ProxyFileHandler(String type) {
    super(type); 
    name = "PROXY"; 
  }
  
  public  String handle(final HttpRequest request, final HttpResponse response, 
              final HttpContext context, String...params) throws Exception {
    return write(request, response, context, params[0]);
  }
  
  @SuppressWarnings("unused")
  public String render(OutputStream output, String file, String cookies[], String... params) throws Exception {
    File f = new File(UtilFile.getFolder("system/proxy/"), "proxies.txt");
    if(f.exists() && f.length() > 0) {
      output.write(RWData.getInstance().load(f));
    } else {
      output.write("".getBytes());
    }
    return "text";
  }

}

