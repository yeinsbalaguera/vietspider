/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

import java.io.File;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 22, 2009  
 */
public class TestFirewallWeb {
  public static void main(String[] args) throws Exception {
    WebClient webClient = new WebClient();

    RefererFormHandler refererFormHandler = new RefererFormHandler(webClient);
    String referer = "http://cry9x.net/f@rum/";
    String homepage  = "http://cry9x.net/f@rum/";
    HttpResponse httpResponse = webClient.setURL(homepage, new URL(homepage));
    refererFormHandler.execute(referer, httpResponse);

    HttpHost httpHost = webClient.createHttpHost(homepage);
    HttpGet httpGet = webClient.createGetMethod(homepage, "http://cry9x.net/f@rum/");

    HttpResponse response = webClient.execute(httpHost, httpGet);
    HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);
    byte [] bytes = methodHandler.readBody(response);
    org.vietspider.common.io.RWData.getInstance().save(new File("D:\\Temp\\webclient\\cry.html"), bytes);
    System.exit(0);

  }
}
