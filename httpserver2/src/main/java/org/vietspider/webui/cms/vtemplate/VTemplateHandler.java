/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.io.LogDataImpl;
import org.vietspider.server.handler.cms.DataNotFound;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 26, 2007
 */
public abstract class VTemplateHandler implements HttpRequestHandler  {

  protected String name;

  protected String type;
  
  protected LogDataImpl log;

  public VTemplateHandler(String type) {
    this.type = type;
    log = new LogDataImpl("track/logs/bot/");
  }

  public String getPath() { return "/"+type + "/"+name; }

  @SuppressWarnings("unused")
  public void handle(final HttpRequest request,
      final HttpResponse response,
      final HttpContext context) throws HttpException, IOException {
    HttpRequestData hrd = HttpUtils.createHRD(request, type);
    try {
      String [] params = new String[0]; 

      String path  = request.getRequestLine().getUri();
      if(name != null && !name.trim().isEmpty()) {
        int idx = path.indexOf(name);
        if(idx > 0) {
          String component = path.substring(idx+name.length());
          component = component.trim();
          if(component.charAt(0) == '/') component = component.substring(1);
          try {
            component = URLDecoder.decode(component, Application.CHARSET);
          } catch (Exception e) {
            LogService.getInstance().setMessage(e, e.toString());
            component = "";
          }
          params = component.split("/");
        }
      }
      
      hrd.setParams(params);
      render(hrd);
    } catch (DataNotFound exp) {
      hrd.write(exp);
    } catch (Throwable exp) {
      ByteArrayOutputStream arrayOutput = new ByteArrayOutputStream();
      StackTraceElement[] traces = exp.getStackTrace();
      try {
        arrayOutput.write(exp.toString().getBytes());
        arrayOutput.write("\n".getBytes());
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }     

      for(StackTraceElement ele : traces){
        try {
          arrayOutput.write(ele.toString().getBytes());
          arrayOutput.write("\n".getBytes());
        }catch (Exception e) {
          LogService.getInstance().setThrowable(e);
        }
      }
      
      hrd.setOutput(arrayOutput);
    }
    response.setEntity(hrd.createEntity());
  }

  public abstract void render(HttpRequestData hrd) throws Exception ;

  public void redirect(HttpRequestData hrd) throws Exception {
    StringBuilder builder = new StringBuilder();
    builder.append("<html>");
    builder.append("<head>");
    builder.append("<meta http-equiv=\"Refresh\" content=\"2;URL=/\">");
    builder.append("</head>");
    builder.append("<body> Invalid access or not found data or data was deleted</body>");
    builder.append("</html>");
    
    hrd.write(builder.toString());
  }
  
  public void invalid(HttpRequestData hrd) throws Exception {
    StringBuilder builder = new StringBuilder();
    builder.append("<html><head><title>Error...</title>");
    builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>");
    builder.append("</head><body>");
    builder.append("<h1>Bạn hiện đang truy cập quá nhanh.");
    builder.append(" Hãy đợi 5 giây sau rồi nhấn Refesh hoặc phím F5 để thử lại.</h1>");
    builder.append("</body></html>");
    hrd.write(builder.toString());
  }

}
