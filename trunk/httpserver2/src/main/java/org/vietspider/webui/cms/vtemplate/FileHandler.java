/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.io.OutputStream;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.server.handler.cms.metas.CMSHandler;
import org.vietspider.webui.cms.FileWriterImpl;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 8, 2007
 */
public class FileHandler extends CMSHandler<String> {
  
  private FileWriterImpl writerImpl = new FileWriterImpl("system/cms/vtemplate/");
  
  public FileHandler(String type, String _name) {
    super(type); 
    name = _name;
  }
  
  public String handle(final HttpRequest request, final HttpResponse response, 
              final HttpContext context, String...params) throws Exception {
    return write(request, response, context, params[0]);
  }
  
  @SuppressWarnings("unused")
  public String render(OutputStream output, String file, String cookies[], String... params) throws Exception {
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

