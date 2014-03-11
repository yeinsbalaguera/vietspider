/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.httpserver;

import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.server.handler.NotFoundHandler;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.cms.vtemplate.FileHandler;
import org.vietspider.webui.cms.vtemplate.RSSHomepageHandler;
import org.vietspider.webui.cms.vtemplate.VDetailHandler;
import org.vietspider.webui.cms.vtemplate.VDomainHandler;
import org.vietspider.webui.cms.vtemplate.VEventHandler;
import org.vietspider.webui.cms.vtemplate.VImageHandler;
import org.vietspider.webui.cms.vtemplate.VThreadHandler;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 25, 2007
 */
public class VTemplateViewerServer extends ApplicationServer {
  
  public VTemplateViewerServer() throws Exception {
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
    
    String type =  "tonghoptin";
    
    createCMSHandler(reqistry, type);
    
    VEventHandler eventHandler = new VEventHandler(type);
    
    reqistry.register("", eventHandler);
    reqistry.register("/", eventHandler);
    reqistry.register("/"+ type, eventHandler);
    reqistry.register("/"+ type +"/", eventHandler);
    reqistry.register("*", new NotFoundHandler());
    
    RSSHomepageHandler rssHandler = new RSSHomepageHandler(type);
    reqistry.register("/site/rss", rssHandler);
    reqistry.register("/site/rss/*", rssHandler);
    
    CMSService cms = CMSService.INSTANCE;;
    cms.setWebPort(port);
    cms.setSiteViewer(type);
    String layout = systemProperties.getValue(Application.CMS_LAYOUT).toLowerCase();
    cms.setPackage("org.vietspider.webui.cms."+layout+".");
  }
  
  
  void createCMSHandler(HttpRequestHandlerRegistry reqistry, String type) {
    VDomainHandler domainHandler = new VDomainHandler(type);
    reqistry.register(domainHandler.getPath()+"/*", domainHandler);
    
    VThreadHandler threadHandler = new VThreadHandler(type);
    reqistry.register(threadHandler.getPath()+"/*", threadHandler);
    
    VDetailHandler detailHandler = new VDetailHandler(type);
    reqistry.register(detailHandler.getPath()+"/*", detailHandler);
    
    VImageHandler imageHandler = new VImageHandler(type);
//    System.out.println(" ====  > chay thu cai nay "+ imageHandler.getPath());
    reqistry.register(imageHandler.getPath()+"/*", imageHandler);
    
    imageHandler = new VImageHandler("vietspider");
    reqistry.register(imageHandler.getPath()+"/*", imageHandler);
    
    FileHandler fileHandler = new FileHandler(type, "file");
    reqistry.register(fileHandler.getPath()+"/*", fileHandler);
//    System.out.println(fileHandler.getPath()+"/*");
//    reqistry.register("/*/*/file/*", fileHandler);
//    reqistry.register("*/file/*", fileHandler);
    
//    fileHandler = new FileHandler("FILE");
//    System.out.println(fileHandler.getPath());
//    reqistry.register("/site/FILE/*", fileHandler);
  }
  
}
