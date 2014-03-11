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
//http://116.193.76.90/site/page_s/index.html
public class FileSEOPageHandler extends CMSHandler<String> {
  
  private FileWriterImpl writerImpl = new FileWriterImpl("system/cms/search/seo/");
  
  public FileSEOPageHandler() {
    super("site"); 
    name = "page_s"; 
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
    return "text/html";
  }

}

