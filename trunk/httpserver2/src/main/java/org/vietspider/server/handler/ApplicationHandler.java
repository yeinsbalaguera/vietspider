/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlService;
import org.vietspider.db.SystemProperties;
import org.vietspider.httpserver.ApplicationControlServer;
import org.vietspider.httpserver.ContentViewerServer;
import org.vietspider.httpserver.HttpServer;
import org.vietspider.httpserver.SearchApplicationServer;
import org.vietspider.httpserver.VTemplateViewerServer;
import org.vietspider.pool.ThreadPool;
import org.vietspider.user.AccessChecker;
import org.vietspider.users.AccessCheckerService;
import org.vietspider.users.Organization;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 14, 2007  
 */
public class ApplicationHandler extends CommonHandler {
  
  @SuppressWarnings("unused")
  public void execute(final HttpRequest request, final HttpResponse response,
                      final HttpContext context, OutputStream output) throws Exception  {
//    System.out.println(request.getFirstHeader("user-agent").getValue());
    Header header = request.getFirstHeader("action");
    String action = header.getValue();
    
    if("server.exit".equals(action)) {
      if(!checkAdminRole(request)) return;
      CrawlService crawlService = CrawlService.getInstance();
      ThreadPool<?,?> threadPool = crawlService.getThreadPool() ;
      if(threadPool != null) {
        threadPool.setPause(true);
        threadPool.destroy();
      }
      System.out.println("\n\n");
      
      try {
        Thread.sleep(5*1000);
      } catch (Exception e) {
      }
      
      Date date_ = Calendar.getInstance().getTime();
      SystemProperties systemProperties = SystemProperties.getInstance();
      StringBuilder builder_  = new StringBuilder("Prepare stop ");
      builder_.append(systemProperties.getValue(Application.APPLICATION_NAME));
      builder_.append(" at ").append(date_.toString());
      
      System.out.println(builder_.toString());
      LogService.getInstance().setMessage(null, builder_.toString());
      
      super.logAction(request, action, "exit application server");
      output.write("1".getBytes());
      output.flush();
      
      System.exit(1);
      return ;
    }
    
    if("server.ping".equals(action)) {
      header = request.getFirstHeader("username");
      String username = header != null  ? header.getValue() : null;
      header = request.getFirstHeader("password");
      String password = header != null  ? header.getValue() : null;
      
      Organization organization = Organization.getInstance();
      int login = organization.login(username, password);
      if(login < 0) {
        output.write(String.valueOf(login).getBytes(Application.CHARSET));
        return;
      }
      
      SystemProperties systemProperties = SystemProperties.getInstance();
      output.write(systemProperties.getValue(Application.APPLICATION_NAME).getBytes(Application.CHARSET));
      output.write((","+String.valueOf(login)).getBytes());
      return;
    }
    
    if("restart.web".equals(action)) {
      if(!checkAdminRole(request)) return;
      String port = new String(getRequestData(request), Application.CHARSET).trim();
      if(port == null || port.trim().isEmpty()) {
        output.write("0".getBytes());
        output.flush();
        return;
      }
      int oldPort = SystemProperties.getInstance().getInt("web.port", 80);
      HttpServer.shutdown(oldPort);
      SystemProperties.getInstance().putValue("web.port", port, true);
      
      String appType = SystemProperties.getInstance().getValue("search.system");
      if("true".equals(appType)) {
        SystemProperties.getInstance().remove("search.system");
        SystemProperties.getInstance().putValue("application.type", "search", true);
      }
      
      appType = SystemProperties.getInstance().getValue("application.type");
      
      if("search".equalsIgnoreCase(appType)) {
        new SearchApplicationServer();
      } else if("vtemplate".equalsIgnoreCase(appType)) {
        new VTemplateViewerServer();
      } else {
        new ContentViewerServer();
      }
      HttpServer.listen();
      super.logAction(request, action, 
          "restart web server: old port: " + oldPort + ", new port: "+ port);
      output.write("1".getBytes());
      output.flush();
      return ;
    }
    
    if("restart.app".equals(action)) {
      if(!checkAdminRole(request)) return;
      String port = new String(getRequestData(request), Application.CHARSET).trim();
      if(port == null || port.trim().isEmpty()) {
        output.write("0".getBytes());
        output.flush();
        return;
      }
      int oldPort = SystemProperties.getInstance().getInt("port", 9245);
      HttpServer.shutdown(oldPort);
      SystemProperties.getInstance().putValue("port", port, true);
      new ApplicationControlServer();
      HttpServer.listen();
      super.logAction(request, action,
          "restart application server: old port: " + oldPort + ", new port: "+ port);
      output.write("1".getBytes());
      output.flush();
      return ;
    }
    
    if("load.access.checker".equals(action)) {
      header = request.getFirstHeader("username");
      String username = header != null  ? header.getValue() : null;
      AccessChecker accessChecker = AccessCheckerService.getInstance().getAccessChecker(username);
      ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
      objectOutputStream.writeObject(accessChecker);
      objectOutputStream.close();
      output.write(bytesOutput.toByteArray());
    }
    
    if("web.client.execute".equals(action)) {
      header = request.getFirstHeader("username");
      String username = header != null  ? header.getValue() : null;
      AccessChecker accessChecker = AccessCheckerService.getInstance().getAccessChecker(username);
      ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
      objectOutputStream.writeObject(accessChecker);
      objectOutputStream.close();
      output.write(bytesOutput.toByteArray());
    }
    
  }

}
