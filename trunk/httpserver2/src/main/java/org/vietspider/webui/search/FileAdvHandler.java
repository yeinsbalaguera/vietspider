/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.webui.search;

import java.io.OutputStream;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.server.handler.cms.metas.CMSHandler;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.cms.FileWriterImpl;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 8, 2007
 */
public class FileAdvHandler extends CMSHandler<String> {
  
  private FileWriterImpl writerImpl = new FileWriterImpl("system/cms/search/ads/");
  
  public FileAdvHandler() {
    super("site"); 
    name = "file_adv"; 
  }
  
  public String handle(final HttpRequest request, final HttpResponse response, 
              final HttpContext context, String...params) throws Exception {
    return write(request, response, context, params[0]);
  }
  
//  http://thuannd:9245/search/FILE/Logo197x52.gif
  
  @SuppressWarnings("unused")
  public String render(OutputStream output, String file, String cookies[], String... params) throws Exception {
    CMSService cms = CMSService.INSTANCE;
    writerImpl.write(output, file);
    if(file.endsWith(".png"))  return "image/png";
    if(file.endsWith(".gif"))  return "image/gif";
    if(file.endsWith(".jpg"))  return "image/jpg";
    if(file.endsWith(".css"))  return "text/css";
    if(file.endsWith(".js"))  return "application/x-javascript";
//    if(file.endsWith(".jpg"))  return "image/jpg";
//    if(file.endsWith(".jpg"))  return "image/jpg";
//    if(file.endsWith(".jpg"))  return "image/jpg";
    return "text/html";
  }

}

