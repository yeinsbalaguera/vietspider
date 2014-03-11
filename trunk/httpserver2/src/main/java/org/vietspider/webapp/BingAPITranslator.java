/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webapp;

import java.net.URLEncoder;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.chars.refs.RefsDecoder;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 2, 2011  
 */
class BingAPITranslator extends TranslatorMachine {
  
  private final static int BING_MAX_SIZE = 100;

  private final String START_TAG = "<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/\">";
  private final String END_TAG = "</string>";
  
  private HttpHost httpHost;
  
  public BingAPITranslator(TranslateMode config) {
    super(config, "http://api.microsofttranslator.com/");
    
    httpHost = webClient.createHttpHost("http://api.microsofttranslator.com/");
  }
  
  String translate(String text, String from, String to) throws Exception {
    if(text.length() > BING_MAX_SIZE) {
      int index = BING_MAX_SIZE;
      int start = 0;
      StringBuilder builder = new StringBuilder();
//      System.out.println("================================");
//      System.out.println(text);
      while(index < text.length()) {
        char c = text.charAt(index); 
        while(Character.isLetterOrDigit(c)) {
          index++;
          if(index >= text.length()) break;
          c = text.charAt(index); 
        }
        String value = text.substring(start, index);
//        System.out.println("---------------------");
//        System.out.println(value);
        builder.append(' ').append(bingTranslatorByAPI(value, from, to));
        start = index;
        index += BING_MAX_SIZE;
      }
      if(start < text.length()) {
        String value = text.substring(start, text.length());
//        System.out.println("-------- cuoi cung -------------");
//        System.out.println(value);
        builder.append(' ').append(bingTranslatorByAPI(value, from, to));
      }
      
      return builder.toString();
    }
    return bingTranslatorByAPI(text, from, to);
  }
  
  private String bingTranslatorByAPI(String text, String from, String to) throws Exception {
    if(text.trim().length() < 1) return "";
//    System.out.println("================================");
//    System.out.println(text);
    text = URLEncoder.encode(text, "utf8");

    String uri = "http://api.microsofttranslator.com/v2/Http.svc/Translate?appId=" 
      + config.getApplicationId() +"&text=" + text;
    if(from != null && from.trim().length() > 0) {
      uri += "&from=" +  from;
    }

    if(to != null && to.trim().length() > 0) {
      uri += "&to=" + to;
    }

    //    System.out.println("translator: " +uri);

    text = cached.getCachedObject(uri);
    if(text == null || text.trim().length() < 1) {
      HttpGet httpGet = webClient.createGetMethod(uri, null);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);
      byte [] bytes = reader.readBody(httpResponse);
      text = new String(bytes, "utf-8").trim();
      if(text.length() > 0) cached.putCachedObject(uri, text);
    }
    
    int idx = text.indexOf(START_TAG); 
    if(idx < 0) throw new NullPointerException("Invalid data (no start tag from Bing)");
    text = text.substring(idx + START_TAG.length());

    idx = text.indexOf(END_TAG); 
    if(idx < 0) throw new NullPointerException("Invalid data (no end tag from Bing)"); 
    text = text.substring(0, idx);

    RefsDecoder decoder = new RefsDecoder();
    text = new String(decoder.decode(text.toCharArray()));
    
//    System.out.println("------------------------");
//    System.out.println(text);
    
    return text;
  }
}
