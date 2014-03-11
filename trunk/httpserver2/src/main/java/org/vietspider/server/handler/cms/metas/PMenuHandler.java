/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.server.WebRM;
import org.vietspider.user.Filter;
import org.vietspider.users.UserFilterService;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.cms.render.PMenuRenderer;
import org.vietspider.webui.cms.render.RedirectRenderer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 18, 2008  
 */
public class PMenuHandler extends CMSHandler <String> {
  
  private String message = "";
  private int typeRender = 0;
  private String [] categories;

  public PMenuHandler(String type) {
    super(type);
    name = "PMENU"; 
  }
  
  public String handle(HttpRequest request, 
      HttpResponse response, HttpContext context, String... params) throws Exception {
    message = null;
    typeRender = 0;
    categories = new String[0];
    
    String username = CMSService.INSTANCE.getUsername(getCookie(request));
    if(username == null) {
      WebRM resources = new  WebRM();
      message = resources.getLabel("incorrectLogin");
      write(request, response, context, "", params);
      return "text/html";
    }
    
    String category = null;
    String group = null;
    String uri = request.getRequestLine().getUri();
    int query = uri.indexOf('?');
    if(query > -1) {
      int indexGroup = uri.indexOf("group", query);
      if(indexGroup > -1) {
        int indexAnd = uri.indexOf('&', indexGroup);
        if(indexAnd > -1) {
          group = uri.substring(indexGroup+6, indexAnd);
          int indexCategory = uri.indexOf("category", indexAnd);
          if(indexCategory > 0) category = uri.substring(indexCategory+9);
        } else {
          group = uri.substring(indexGroup+6);
        }
      }
    }
    
    if(category != null && group != null) {
      categories = new String[]{group, category};
    } else if(group != null) {
      categories = new String[]{group};
    } else {
      categories = new String[0];
    }
    
    String method = request.getRequestLine().getMethod();
    if(method.trim().toLowerCase().equals("get")) {
      typeRender = 1;
    } else if(method.trim().toLowerCase().equals("post")) {
      String filterName = null;
      
      String [] values = readData(request);
      StringBuilder builder = new StringBuilder();
      for(String value : values) {
        int index = value.indexOf("pmenu"); 
        if(index != 0) {
          index = value.indexOf("filterName");
          if(index == 0) filterName = URLDecoder.decode(value.substring(11), "utf-8");
          continue;
        }
        String menu = value.substring(6);
        builder.append('\n');
        menu = URLDecoder.decode(menu, "utf-8");
        builder.append(menu);
      }
      
      if(filterName == null) {
        typeRender = 3;
        write(request, response, context, "", params);
        return "text/html";
      }
      
      Filter filter = new Filter(filterName, builder.toString(), Filter.DOMAIN);
      UserFilterService.getInstance().saveFilter(username, filter);
      typeRender = 2;
    }
    
    return write(request, response, context, "", params);
  }
  
  protected String[] readData(final HttpRequest request) throws IOException {
    BasicHttpEntityEnclosingRequest entityRequest = (BasicHttpEntityEnclosingRequest)request;
    InputStream inputStream = entityRequest.getEntity().getContent();
    try {
      ByteArrayOutputStream input = RWData.getInstance().loadInputStream(inputStream);
      String bodyValue = new String(input.toByteArray(), Application.CHARSET).trim();
      return bodyValue.split("&");
    } catch (Exception e) {
      return null;
    }
  }
  
  @SuppressWarnings("unused")
  public String render(OutputStream output, String value, String cookies[], String...params) throws Exception {
    CMSService cms = CMSService.INSTANCE;;
    params = new String []{cms.getHost(), String.valueOf(cms.getWebPort())}; 
    if(typeRender < 1) {
      RedirectRenderer render = new RedirectRenderer();
      render.write(output, type, "5", message, "/site/FILE/Login.html", "/");
      return "text/html";
    } 
    
    if(typeRender == 1) {
      PMenuRenderer render = cms.createRender(PMenuRenderer.class);
      try {
        render.write(output, type, cookies,  categories);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return "text/html";
    } 
    
    if(typeRender == 2) {
      RedirectRenderer render = new RedirectRenderer();
      render.write(output, type, "5", message, new String[]{"/", "/"});
      return "text/html";
    } 
    
    RedirectRenderer render = new RedirectRenderer();
    render.write(output, type, message, "/", "/");
    return "text/html";
  }
}
