/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.httpserver;

import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.CrawlerConfig;
import org.vietspider.db.SystemProperties;
import org.vietspider.db.database.DatabaseService;
import org.vietspider.server.handler.NotFoundHandler;
import org.vietspider.server.handler.cms.metas.AccountHandler;
import org.vietspider.server.handler.cms.metas.DetailHandler;
import org.vietspider.server.handler.cms.metas.DomainHandler;
import org.vietspider.server.handler.cms.metas.EditContentHandler;
import org.vietspider.server.handler.cms.metas.EventHandler;
import org.vietspider.server.handler.cms.metas.FileHandler;
import org.vietspider.server.handler.cms.metas.ImageHandler;
import org.vietspider.server.handler.cms.metas.IndexSearchHandler;
import org.vietspider.server.handler.cms.metas.InternalLoginHandler;
import org.vietspider.server.handler.cms.metas.PMenuHandler;
import org.vietspider.server.handler.cms.metas.ProxyFileHandler;
import org.vietspider.server.handler.cms.metas.RSSHandler;
import org.vietspider.server.handler.cms.metas.RedirectHandler;
import org.vietspider.server.handler.cms.metas.SolrIndexSearchHandler;
import org.vietspider.server.handler.cms.metas.ThreadHandler;
import org.vietspider.webui.cms.CMSService;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 25, 2007
 */
public class ContentViewerServer extends ApplicationServer {
  
  public ContentViewerServer() throws Exception {
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

    // Set up request handlers
    HttpRequestHandlerRegistry reqistry = server.getReqistry();
    
    createCMSHandler(reqistry, "site");
    
    reqistry.register("", new RedirectHandler("site", "DOMAIN"));
    reqistry.register("/", new RedirectHandler("site", "DOMAIN"));
    reqistry.register("*", new NotFoundHandler());
    
    CMSService cms = CMSService.INSTANCE;;
    cms.setWebPort(port);
    cms.setSiteViewer("site");
    String layout = systemProperties.getValue(Application.CMS_LAYOUT).toLowerCase();
    cms.setPackage("org.vietspider.webui.cms."+layout+".");
    
//    System.out.println("===============================");
  }
  
  
  void createCMSHandler(HttpRequestHandlerRegistry reqistry, String type) {
    createContextHandler(reqistry, new DomainHandler(type));
    createContextHandler(reqistry, new DetailHandler(type));
    createContextHandler(reqistry, new EventHandler(type));
    
    try {
      //start index service 
      String index = SystemProperties.getInstance().getValue(Application.START_INDEX_DATA_SERVICE);
      index = index != null ? index.trim() : null;
      
      if(CrawlerConfig.DATABASE_TYPE == CrawlerConfig.EMBEDED_DB
          && DatabaseService.isMode(DatabaseService.SEARCH)) {
        createContextHandler(reqistry, new SolrIndexSearchHandler(type));  
      } else {
        createContextHandler(reqistry, new IndexSearchHandler(type));  
      }
      
      
//      if("true".equals(index)) {
//        createContextHandler(reqistry, new FilterSearchHandler(type));
//      }
//      } else {
//        createContextHandler(reqistry, new SqlSearchHandler(type));        
//      }
    } catch(Exception exp){
//      createContextHandler(reqistry, new SqlSearchHandler(type));
      LogService.getInstance().setThrowable(exp);
    }
    
    createContextHandler(reqistry, new AccountHandler(type));
    createContextHandler(reqistry, new InternalLoginHandler(type));
    createContextHandler(reqistry, new PMenuHandler(type));
    createContextHandler(reqistry, new ThreadHandler(type));
    createContextHandler(reqistry, new RSSHandler(type));
    createContextHandler(reqistry, new ImageHandler(type));
    createContextHandler(reqistry, new FileHandler(type));
    createContextHandler(reqistry, new ProxyFileHandler(type));
    
    String application  = "vietspider";
    try {
      application =  SystemProperties.getInstance().getValue(Application.APPLICATION_NAME);
    } catch (Exception e) {
      application = "vietspider";
    }
    
    createContextHandler(reqistry, new org.vietspider.server.handler.cms.metas.FileHandler(application));
    createContextHandler(reqistry, new ImageHandler(application));
    
    EditContentHandler editHandler = EditContentHandler.getInstance();
    if(editHandler.isEdit()) createContextHandler(reqistry, editHandler);
  }
  
  
}
