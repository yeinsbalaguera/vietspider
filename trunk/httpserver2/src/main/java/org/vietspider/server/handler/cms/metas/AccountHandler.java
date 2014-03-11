/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.server.WebRM;
import org.vietspider.users.Organization;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.cms.render.RedirectRenderer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 2, 2008  
 */
public class AccountHandler extends CMSHandler <String> {
  
  private String message = "";

  public AccountHandler(String type) {
    super(type);
    name = "LOGIN"; 
  }

  public String handle(HttpRequest request, 
      HttpResponse response, HttpContext context, String... params) throws Exception {
    Header [] headers = request.getHeaders("Cookie");
    if(headers != null && headers.length > 0) {
      logout(response, headers);  
    } else {
      login(request, response);
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
  
  private void logout(HttpResponse response, Header [] headers) {
    String value = headers[0].getValue();
    if(value == null) return;
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 2);
    
    StringBuilder builder = new StringBuilder();
    builder.append(value).append("; path=\" / \"; ");
    builder.append("expires=").append(calendar.getTime().toString()).append(',');
    builder.append(" cookie4=2; path=/");
//    System.out.println("logout " + builder);
    response.addHeader(new BasicHeader("Set-Cookie", builder.toString()));
  }
  
  
  private void login(HttpRequest request, HttpResponse response)  {
    String username = "";
    String password = "";
    try{
      String [] elements = readData(request);
      if(elements != null && elements.length > 1) {
        username = elements[0].split("=")[1];
        password = elements[1].split("=")[1];
      }
    } catch (Exception e) {
      return;
    }
    
    Organization organization = Organization.getInstance();
    int login = organization.login(username, password);
    if(login > 0) {
      StringBuilder builder = new StringBuilder();
      builder.append("userlogin=").append(username).append(".").append(login);
      builder.append("; path=\" / \",");
      builder.append(" cookie4=2; path=/");
      response.addHeader(new BasicHeader("Set-Cookie", builder.toString()));
      message = null;
    } else if (login == -2) {
      message = new WebRM().getLabel("incorrectUser");
    } else if (login == -1) {
      message =  new WebRM().getLabel("incorrectPassword");
    } else  if(login == 0) {
      message =  new WebRM().getLabel("incorrectMode");
    }
  }

  @SuppressWarnings("unused")
  public String render(OutputStream output, String value, String cookies[], String...params) throws Exception {
    CMSService cms = CMSService.INSTANCE;;
    params = new String []{cms.getHost(), String.valueOf(cms.getWebPort())}; 
    RedirectRenderer render = new RedirectRenderer();
    render.write(output, type, "5", message, new String[]{"/site/FILE/Login.html", "/"});
    return "text/html";
  }
}
