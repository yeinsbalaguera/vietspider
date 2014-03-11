/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser.login;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.vietspider.browser.HttpSessionUtils;
import org.vietspider.common.io.DataWriter;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 22, 2009  
 */
public class ForumLogin {
  
  public static void main(String[] args) {
    try {
      File file = new File("D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      WebClient webClient = new WebClient();

      String homepage  = "http://360kpop.com/f/";
      webClient.setURL(null, new URL(homepage));

      HttpHost httpHost = webClient.createHttpHost(homepage);
      HttpGet httpGet = webClient.createGetMethod(homepage, "http://360kpop.com/f/");

      HttpResponse response = webClient.execute(httpHost, httpGet);
      HttpEntity entity = response.getEntity();

      System.out.println("Login form get: " + response.getStatusLine());
      if (entity != null) entity.consumeContent();

      System.out.println("Initial set of cookies:");
      DefaultHttpClient httpClient = (DefaultHttpClient) webClient.getHttpClient();
      List<Cookie> cookies = httpClient.getCookieStore().getCookies();
      if (cookies.isEmpty()) {
        System.out.println("None");
      } else {
        for (int i = 0; i < cookies.size(); i++) {
          System.out.println("- " + cookies.get(i).toString());
        }
      }

      HttpMethodHandler handler = new HttpMethodHandler(webClient);
      HttpSessionUtils httpSession = new HttpSessionUtils(handler, "ERROR");

      StringBuilder builder = new StringBuilder(homepage);
      builder.append('\n').append("headvances:123456789");

      httpSession.login(builder.toString(), "utf-8", new URL(homepage), homepage);

      httpGet = webClient.createGetMethod("http://360kpop.com/f/", "http://360kpop.com/f/");
      response = webClient.execute(httpHost, httpGet);
      entity = response.getEntity();

      HttpMethodHandler httpResponseReader = new HttpMethodHandler(webClient);
      byte [] bytes = httpResponseReader.readBody(response);
      org.vietspider.common.io.RWData.getInstance().save(new File("evashops_net.html"), bytes);

      System.out.println("Login form get: " + response.getStatusLine());
      if (entity != null) entity.consumeContent();

      System.out.println("Post logon cookies:");
      cookies = httpClient.getCookieStore().getCookies();
      if (cookies.isEmpty()) {
        System.out.println("None");
      } else {
        for (int i = 0; i < cookies.size(); i++) {
          System.out.println("- " + cookies.get(i).toString());
        }
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    System.exit(0);

  }
}
