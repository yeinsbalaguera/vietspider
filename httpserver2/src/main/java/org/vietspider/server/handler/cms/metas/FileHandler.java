/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.OutputStream;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.server.handler.cms.FileWriter;
import org.vietspider.webui.cms.CMSService;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 8, 2007
 */
public class FileHandler extends CMSHandler<String> {
  
  public FileHandler(String type) {
    super(type); 
    name = "FILE"; 
  }
  
  public  String handle(final HttpRequest request, final HttpResponse response, 
              final HttpContext context, String...params) throws Exception {
    return write(request, response, context, params[0]);
  }
  
  @SuppressWarnings("unused")
  public String render(OutputStream output, String file, String cookies[], String... params) throws Exception {
    CMSService cms = CMSService.INSTANCE;;
    FileWriter render = cms.createRender(FileWriter.class);
    render.write(output, file);
    return "text/html";
  }

}

