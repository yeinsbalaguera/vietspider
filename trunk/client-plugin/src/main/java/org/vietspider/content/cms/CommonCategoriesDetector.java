/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.cms;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 18, 2010  
 */
public abstract class CommonCategoriesDetector {
  
  protected String loginURL;
  protected String homepage;

  protected String username;
  protected String password;
  
  protected String charset = "utf-8";

  protected  WebClient webClient = new WebClient();

  public CommonCategoriesDetector(String homepage,
      String loginURL, String username, String password, String charset) {
    this.homepage = homepage.trim();
    this.loginURL  = loginURL.trim();
    this.username = username;
    this.password = password;
    this.charset = charset;
  }

  public abstract void detect(String url) throws Exception ;

  public byte[] get(String url) throws Exception{
    HttpMethodHandler httpMethod = new HttpMethodHandler(webClient);
    HttpResponse response = httpMethod.execute(url, loginURL);
    if(response == null) throw new Exception("No response!");

    StatusLine statusLine = response.getStatusLine();
    int statusCode = statusLine.getStatusCode();

    switch (statusCode) {
    case HttpStatus.SC_NOT_FOUND:
    case HttpStatus.SC_NO_CONTENT:
    case HttpStatus.SC_BAD_REQUEST:
    case HttpStatus.SC_REQUEST_TIMEOUT:
    case HttpStatus.SC_NOT_ACCEPTABLE:
    case HttpStatus.SC_SERVICE_UNAVAILABLE:
    case 999:{
      throw new Exception(url + " " + statusLine.getReasonPhrase());
    }
    default:
      break;
    }

    byte [] data = httpMethod.readBody();
    return data;
  }
}
