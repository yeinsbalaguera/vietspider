/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.OutputStream;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.vietspider.users.Organization;
import org.vietspider.webui.cms.CMSService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 2, 2008  
 */
public class InternalLoginHandler extends CMSHandler <String> {

  public InternalLoginHandler(String type) {
    super(type);
    name = "INTERNALLOGIN"; 
  }

  protected String[] readData(final HttpRequest request) {
    String uri = request.getRequestLine().getUri();
    int index = uri.indexOf("vsl=");
    if(index > -1) {
       uri = uri.substring(index+4);
       return uri.toString().split("&");
    }
    return null;
  }
  
  public String handle(HttpRequest request, 
      HttpResponse response, HttpContext context, String... params) throws Exception {
    login(request, response);
    return write(request, response, context, "", params);
  }  
  
  private void login(HttpRequest request, HttpResponse response)  {
    String username = "";
    String password = "";
    try{
      String [] elements = readData(request);
      if(elements != null && elements.length > 1) {
        username = elements[0];
        password = elements[1];
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
    }
  }

  @SuppressWarnings("unused")
  public String render(OutputStream output, String value, String cookies[], String...params) throws Exception {
    CMSService cms = CMSService.INSTANCE;;
    params = new String []{cms.getHost(), String.valueOf(cms.getWebPort())}; 
    output.write("".getBytes());
    return "text/html";
  }
}
