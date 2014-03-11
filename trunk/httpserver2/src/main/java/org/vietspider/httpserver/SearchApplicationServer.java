/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.httpserver;

import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.server.handler.EmbededRequestHandler;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.search.CachedContentHandler;
import org.vietspider.webui.search.DefaultSearchHandler;
import org.vietspider.webui.search.DefaultThreadHandler;
import org.vietspider.webui.search.FileAdvHandler;
import org.vietspider.webui.search.FileHandler;
import org.vietspider.webui.search.FileSEOPageHandler;
import org.vietspider.webui.search.TopMetasHandler;
import org.vietspider.webui.search.UserPostHandler;
import org.vietspider.webui.search.seo.RSSPageHandler;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 25, 2007
 */
public class SearchApplicationServer extends ApplicationServer {
  
  public SearchApplicationServer() throws Exception {
    SystemProperties systemProperties = SystemProperties.getInstance();

    int port = 9245;
    try {
      String value = systemProperties.getValue("web.port");
      if(value == null || value.trim().isEmpty()) {
        value = systemProperties.getValue("port");
        systemProperties.putValue("web.port", value, true);
      } 
      if(value == null || value.trim().isEmpty()) value = "9245";
      port = Integer.parseInt(value);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      systemProperties.putValue("web.port", "9245", true);
    }
    
    init(port);

    
    HttpRequestHandlerRegistry reqistry = server.getReqistry();
    
    TopMetasHandler topHandler = new TopMetasHandler("site");
    
    DefaultSearchHandler searchHandler = new DefaultSearchHandler(/*topHandler,*/ "site");
    reqistry.register("", searchHandler);
    reqistry.register("/", searchHandler);
    reqistry.register("*", searchHandler);
    
    EmbededRequestHandler embededHandler = new EmbededRequestHandler();
    reqistry.register(embededHandler.getPath() + "/*", embededHandler);
    reqistry.register(embededHandler.getPath() , embededHandler);
    
    createContextHandler(reqistry, new DefaultThreadHandler());
    createContextHandler(reqistry, topHandler);
    
    FileHandler fileHandler = new FileHandler();
    createContextHandler(reqistry, fileHandler);
//    reqistry.register("*", fileHandler);
    
    createContextHandler(reqistry, new FileAdvHandler());
    createContextHandler(reqistry, new FileSEOPageHandler());
    createContextHandler(reqistry, new CachedContentHandler(topHandler));
    createContextHandler(reqistry, new RSSPageHandler());
    
    createContextHandler(reqistry, new UserPostHandler(topHandler));
    
    CMSService cms = CMSService.INSTANCE;;
    cms.setWebPort(port);
    cms.setSiteViewer("site");
    String layout = systemProperties.getValue(Application.CMS_LAYOUT).toLowerCase();
    cms.setPackage("org.vietspider.webui.cms."+layout+".");
  }
  
}
