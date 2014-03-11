/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webapp;

import java.io.File;
import java.net.URLEncoder;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.serialize.XML2Object;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 2, 2011  
 */
public class BingWebTranslator extends TranslatorMachine {

  private HttpHost httpHost;

  public BingWebTranslator(TranslateMode config) {
    super(config, "http://api.microsofttranslator.com");

    httpHost = webClient.createHttpHost("http://api.microsofttranslator.com");
  }

  String translate(String text, String from, String to) throws Exception {
    if(text.trim().length() < 1) return "";
    //  System.out.println("================================");
    //  System.out.println(text);
    text = URLEncoder.encode(text, "utf8");

    String uri = "http://api.microsofttranslator.com/v2/ajax.svc/TranslateArray?appId=%22"
        + config.getApplicationId() + "%22&texts=[%22" + text +"%22]";
    if(from != null && from.trim().length() > 0) {
      uri += "&from=%22" +  from + "%22";
    }

    if(to != null && to.trim().length() > 0) {
      uri += "&to=%22" + to + "%22";
    }

    uri += "&oncomplete=_mstc1&onerror=_mste1&loc=en&ctr=VietNam&rgp=1d3a0366";

//        System.out.println("translator: " +uri);

    text = cached.getCachedObject(uri);
    if(text == null || text.trim().length() < 1) {
      try {
        HttpGet httpGet = webClient.createGetMethod(uri, null);
        HttpResponse httpResponse = webClient.execute(httpHost, httpGet);
        byte [] bytes = reader.readBody(httpResponse);
        text = new String(bytes, "utf-8").trim();
        if(text.length() > 0) cached.putCachedObject(uri, text);
      } catch (Throwable e) {
        LogService.getInstance().setThrowable(e);
      }
    }

    if(text == null) return null;

    RefsDecoder decoder = new RefsDecoder();
    text = new String(decoder.decode(text.toCharArray()));

    return processText(text);
  }
  
  private static String processText(String text) {
//    StringBuilder dich = new StringBuilder();
//    StringBuilder phienam = new StringBuilder();
    
//    System.out.println("===================== ");
//    System.out.println(text);
    
    int start = text.indexOf("\"TranslatedText\":\"");
    int end = text.indexOf("\",\"TranslatedTextSentenceLengths\":");
    if(start < 0 || end < 0 || end < start) {
      throw new NullPointerException("Invalid data (start or end data)");
    }
    String newText = text.substring(start + "\"TranslatedText\":\"".length(), end);
    newText = newText.replaceAll("\\\\\"", "\"");
    return newText;   
  }

  
  public static void main(String[] args) {
    try {
      File file  = new File("D:\\java\\test\\vsnews\\data\\");
      System.setProperty("save.link.download", "true");
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
      TranslateMode config = null;
      try {
        file = new File(UtilFile.getFolder("system/plugin/"), "bing.translation.config");
        if(file.exists() && file.length() > 0) {
          byte [] bytes = RWData.getInstance().load(file);
          config = XML2Object.getInstance().toObject(TranslateMode.class, bytes);
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      
      
      System.out.println(config);
      
      BingWebTranslator translator = new BingWebTranslator(config);
      String text = "公開徵求委託辦理「101年度行政院衛生署資訊設備、機房及全國醫療資訊網服務中心維運案」(案號:101A8009)，詳見行政院公共工程委員會政府電子採購網政府採購資訊公告，網址為 ";
      String value = translator.translate(text, null, "vi");
      System.out.println(value);
      
      System.exit(0);
//      System.out.println(file.getCanonicalPath());
      
    } catch (Exception e) {
      e.printStackTrace();
    }      
  }

}
