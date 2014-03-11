/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.httpserver;

import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.vietspider.common.Application;
import org.vietspider.db.SystemProperties;
import org.vietspider.net.server.URLPath;
import org.vietspider.server.handler.ApplicationFileHandler;
import org.vietspider.server.handler.ApplicationHandler;
import org.vietspider.server.handler.CrawlerHandler;
import org.vietspider.server.handler.DataContentHandler;
import org.vietspider.server.handler.DataHandler;
import org.vietspider.server.handler.DataPluginHandler;
import org.vietspider.server.handler.RemoteDataHandler;
import org.vietspider.server.handler.SearchContentHandler;
import org.vietspider.server.handler.cms.metas.DetailHandler;
import org.vietspider.server.handler.cms.metas.DomainHandler;
import org.vietspider.server.handler.cms.metas.EditContentHandler;
import org.vietspider.server.handler.cms.metas.EventHandler;
import org.vietspider.server.handler.cms.metas.FilterContentHandler;
import org.vietspider.server.handler.cms.metas.ImageHandler;
import org.vietspider.server.handler.cms.metas.IndexSearchHandler;
import org.vietspider.server.handler.cms.metas.ThreadHandler;
import org.vietspider.server.handler.cms.metas.TranslateHandler;
import org.vietspider.webui.cms.CMSService;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 25, 2007
 */
public class ApplicationControlServer extends ApplicationServer {

  public ApplicationControlServer() throws Exception {
    SystemProperties systemProperties = SystemProperties.getInstance();

    int port = 9245;
    try {
      String value = systemProperties.getValue("port");
      if(value == null || value.trim().isEmpty()) {
        systemProperties.putValue("port", "9245", true);
      } else { 
        port = Integer.parseInt(value);
      }
    } catch (Exception e) {
      systemProperties.putValue("port", "9245", true);
    }
    
    init(port);

    // Set up request handlers
    HttpRequestHandlerRegistry reqistry = server.getReqistry();

    reqistry.register(URLPath.FILE_HANDLER, new ApplicationFileHandler());
    reqistry.register(URLPath.APPLICATION_HANDLER, new ApplicationHandler());
    reqistry.register(URLPath.DATA_HANDLER, new DataHandler());
    reqistry.register(URLPath.REMOTE_DATA_HANDLER, new RemoteDataHandler());
    reqistry.register(URLPath.DATA_CONTENT_HANDLER, new DataContentHandler());
    reqistry.register(URLPath.DATA_PLUGIN_HANDLER, new DataPluginHandler());
    reqistry.register(URLPath.CRAWLER_HANDLER, new CrawlerHandler());
    
    reqistry.register(URLPath.SEARCH_HANDLER, new SearchContentHandler());

    CMSService cms = CMSService.INSTANCE;;
    cms.setHost(getHost());
    cms.setAppPort(getPort());
    
    String application  = "vietspider";
    try {
      application = systemProperties.getValue(Application.APPLICATION_NAME);
    } catch (Exception e) {
      application = "vietspider";
    }
    cms.setAppViewer(application);
    cms.setViewer(application);
    
    createCMSHandler(reqistry, application);
  }


  private void createCMSHandler(HttpRequestHandlerRegistry reqistry, String type) {
    createContextHandler(reqistry, new DomainHandler(type));
    createContextHandler(reqistry, new DetailHandler(type));
    createContextHandler(reqistry, new EventHandler(type));

    createContextHandler(reqistry, new IndexSearchHandler(type));

    createContextHandler(reqistry, new ThreadHandler(type));
    createContextHandler(reqistry, new FilterContentHandler(type));
    createContextHandler(reqistry, new ImageHandler(type));
    createContextHandler(reqistry, new TranslateHandler(type));
    
    AppFileHandler fileHandler = new AppFileHandler();
    createContextHandler(reqistry, fileHandler);
//    reqistry.register("/site/FILE/*", fileHandler);
    
//    createContextHandler(reqistry, new org.vietspider.server.handler.cms.metas.FileHandler(type));
    

    EditContentHandler editHandler = EditContentHandler.getInstance();
    if(editHandler.isEdit()) createContextHandler(reqistry, editHandler);
  }

}
