/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataWriter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 21, 2007  
 */
public class TestUpdateSofts1 {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loginWeb(String url, List<NameValuePair> params) throws Exception {
    try{
      HttpMethodHandler httpMethod = new HttpMethodHandler(webClient);
      HttpResponse httpResponse = httpMethod.execute(url, null, params, "utf-8");

      HttpResponseReader httpResponseReader = new HttpResponseReader();
      return httpResponseReader.readBody(httpResponse);
      
    } catch(Exception exp){
      exp.printStackTrace();
      return null;
    }
  }
  
  public static byte[] loadContent(String address) throws Exception {
    HttpGet httpGet = null;
    try {
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      httpGet = webClient.createGetMethod(address, "");      

      if(httpGet == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);

//      HttpEntity httpEntity = httpResponse.getEntity();
//      return webClient.decodeResponse(webClient.readData(httpEntity.getContent()), httpEntity.getContentEncoding());
      HttpResponseReader httpResponseReader = new HttpResponseReader();
      return httpResponseReader.readBody(httpResponse);
      
    } catch(Exception exp){
      exp.printStackTrace();
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
    String home = "http://www.cyvee.com/Modules/Opportunity/ViewOpportunity.aspx?tp=1&oid=985";
    
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    System.out.println(home);
    
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    webClient.setURL(null, new URL(home));
    
    String loginURL = "http://www.cyvee.com/Login.aspx";
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("__EVENTTARGET", ""));
    params.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
    params.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwUKMTk5ODU3NDAzMw8WAh4LUmVmZXJyZXJVcmwFCy9JbmRleC5hc3B4FgJmD2QWBAICD2QWAmYPFgIeBGhyZWYFPWh0dHA6Ly93d3cuY3l2ZWUuY29tL0FwcF9UaGVtZXMvRGVmYXVsdC92aS1WTi9pbWFnZXMvbG9nby5pY29kAgQPZBYEAgEPZBYCAgEPZBYIAgMPFgIeB1Zpc2libGVoZAIFDxYCHwJoZAIHDxYCHwJoZAILDw8WAh8CaGRkAgIPZBYCAgEPZBYEAgEPDxYCHwAFCy9JbmRleC5hc3B4ZBYCZg9kFgICDg8PFgIeC05hdmlnYXRlVXJsBShodHRwOi8vd3d3LmN5dmVlLmNvbS9Gb3JnZXRQYXNzd29yZC5hc3B4ZGQCBQ8PFgIfAwUiaHR0cDovL3d3dy5jeXZlZS5jb20vUmVnaXN0ZXIuYXNweGRkGAEFHl9fQ29udHJvbHNSZXF1aXJlUG9zdEJhY2tLZXlfXxYBBVBjdGwwMCRjb250ZW50V3JhcHBlclBsYWNlSG9sZGVyJGNvbnRlbnRQbGFjZUhvbGRlciR1Y01haW5Mb2dpbkNvbnRyb2wkY2JSZW1lbWJlciaf/5eCh+/z6eKI5cfge9mqNb+j"));
    params.add(new BasicNameValuePair("ctl00$contentWrapperPlaceHolder$contentPlaceHolder$ucMainLoginControl$txtEmail", "nhudinhthuan@yahoo.com"));
    params.add(new BasicNameValuePair("ctl00$contentWrapperPlaceHolder$contentPlaceHolder$ucMainLoginControl$txtPassword", "head123"));
    params.add(new BasicNameValuePair("ctl00$contentWrapperPlaceHolder$contentPlaceHolder$ucMainLoginControl$btnLogin", "Đăng nhập"));
    params.add(new BasicNameValuePair("__EVENTVALIDATION", "/wEWBgKkr9OfBwLWoreOAwLOwv3oDgK/qJSOBgLY8sD/BAKx7q7bC2In3br4BD51pGsPOoD26XkR3EqF"));
    
    try {
      byte [] bytes = loginWeb(loginURL, params);
      writer.save(new File("F:\\Temp2\\webclient\\a.html"), bytes);
      System.out.println(" da login xong");
    } catch (Exception e) {
      // TODO: handle exception
    }
    
    byte [] bytes = loadContent(home);
    writer.save(new File("F:\\Temp2\\webclient\\b.html"), bytes);
    
  }
  
}
