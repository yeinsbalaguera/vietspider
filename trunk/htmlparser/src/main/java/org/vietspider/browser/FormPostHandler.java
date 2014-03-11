/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.vietspider.browser.form.Form;
import org.vietspider.browser.form.FormUtils;
import org.vietspider.browser.form.Param;
import org.vietspider.chars.URLUtils;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 22, 2009  
 */
public class FormPostHandler {
  
  public void post(Form form, String name, WebClient webClient, String referer, int time) throws Exception {
    if(time  > 5) return;
    URLUtils urlUtils = new URLUtils();
    String action = form.getAction();
    if(action == null) return;
    String address = urlUtils.createURL(referer, action).trim();
    address = urlUtils.getCanonical(address);
    
    if(form == null) return;
    
    if("get".equalsIgnoreCase(form.getMethod())) {
      StringBuilder builder = new StringBuilder(address).append('?');
      List<Param> params = form.getParams();
      for(int i = 0; i < params.size(); i++) {
        if(i > 1) builder.append('&');
        builder.append(params.get(i).getName()).append('=');
        builder.append(URLEncoder.encode(params.get(i).getValue(), "utf-8"));
      }
//      System.out.println("referer "+ referer);
//      System.out.println("address " + address);
//      System.out.println(builder);
      HttpHost httpHost = webClient.createHttpHost(referer);
      HttpGet httpGet = webClient.createGetMethod(builder.toString(), referer);
      
//      Header [] headers = httpGet.getAllHeaders();
//      for(Header header : headers) {
//        System.out.println(header.getName() + " : " + header.getValue());
//      }
      
      HttpResponse response = webClient.execute(httpHost, httpGet);
      
//       headers = response.getAllHeaders();
//      for(Header header : headers) {
//        System.out.println(header.getName() + " : " + header.getValue());
//      }
      
      //only for test
      HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);
      byte [] bytes = methodHandler.readBody(response);
      if(bytes == null || bytes.length < 10) return;
      HTMLParserDetector htmlParser = new HTMLParserDetector();
      List<NodeImpl> tokens  = htmlParser.createTokens(bytes);
      
      FormUtils formUtils = new FormUtils(); 
      List<Form> forms = formUtils.searchForm(tokens);
      if(forms == null || forms.size() < 1 || forms.get(0) == null
         || name == null || !name.equalsIgnoreCase(forms.get(0).getName())) return;
//    System.out.println(" thay co forms "+ forms.get(0).getName()+ " / "+ time);
      post(forms.get(0), name, webClient, referer, time+1);
//      byte [] bytes = methodHandler.readBody(response);
//      org.vietspider.common.io.RWData.getInstance().save(new File("D:\\Temp\\webclient\\cry2.html"), bytes);
      return;
    }
    
    List<Param> params = form.getParams();
    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
    for(int i = 0; i < params.size(); i++) {
      addParam(pairs, params.get(i).getName(), params.get(i).getValue());
    }
    
    post(address, webClient, referer, pairs);
  }
  
  public void post(String address, 
      WebClient webClient, String referer, List<NameValuePair> pairs) throws Exception {
    
    HttpHost httpHost = webClient.createHttpHost(address);
//      webClient.setLog(true);
    HttpPost httpPost = webClient.createFormPostMethod(address, referer, pairs, "utf-8");
//      webClient.setLog(false);
    HttpMethodHandler httpMethod = new HttpMethodHandler(webClient);
    HttpResponse httpResponse = httpMethod.execute(httpHost, httpPost);
    if(httpResponse == null) return;

    StatusLine statusLine = httpResponse.getStatusLine();
    int statusCode = statusLine.getStatusCode();
    //  System.out.println(statusCode);
    if(statusCode == HttpStatus.SC_MOVED_PERMANENTLY 
        || statusCode == HttpStatus.SC_SEE_OTHER 
        || statusCode == HttpStatus.SC_MOVED_TEMPORARILY
        || statusCode == HttpStatus.SC_TEMPORARY_REDIRECT) {
      Header header = httpResponse.getFirstHeader("Location");
      if(header != null 
          && header.getValue() != null 
          && !header.getValue().trim().isEmpty()) {
        //      System.out.println(header.getValue());
        try {
          httpResponse = httpMethod.execute(header.getValue(), null);
        } catch (Throwable e) {
        }
        //      System.out.println(" da xong roi ");
      }
    }
  }

  public void addParam(List<NameValuePair> pairs, String name, String value){
    NameValuePair pair  = null;
    int i = 0;
    for(; i < pairs.size(); i++) {
      if(pairs.get(i).getName().equalsIgnoreCase(name)) {
        pair = pairs.get(i); 
        break;
      }
    }

    if(value == null || value.trim().isEmpty()) {
      if(pair != null) return;
      pairs.add(new BasicNameValuePair(name, ""));
      return;
    }

    if(pair == null) {
      pairs.add(new BasicNameValuePair(name, value.trim()));
      return;
    }

    String o_value  = pair.getValue();
    if(o_value.trim().isEmpty()) {
      pairs.set(i, new BasicNameValuePair(name, value));
      return;
    }
    pairs.add(new BasicNameValuePair(name, value.trim()));
  }

}
