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
import org.apache.http.client.methods.HttpGet;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataWriter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 21, 2007  
 */
public class TestRapVN {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loginWeb(String login) throws Exception {
    try{
      String [] elements = login.split("\n");
      List<String[]> name_values = new ArrayList<String[]>();
      for(int i = 1; i < elements.length; i++) {
        int idx = elements[i].indexOf('=');
        if(idx < 0) continue;
        String [] values = new String[2];
        values[0] = elements[i].substring(0, idx);
        values[1] = elements[i].substring(idx+1);
        name_values.add(values);
      }
//      HttpResponse httpResponse = webClient.login(elements[0], name_values);
//
//      HttpResponseReader httpResponseReader = new HttpResponseReader();
//      return httpResponseReader.readBody(httpResponse);
      
      return null;
      
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

      HttpResponseReader httpResponseReader = new HttpResponseReader();
      return httpResponseReader.readBody(httpResponse);
    } catch(Exception exp){
      exp.printStackTrace();
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
    String home = "http://www.updatesofts.com/forums/showthread.php?t=108538";
    
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    System.out.println(home);
    
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    webClient.setURL(null, new URL(home));
    
    String login = "http://www.updatesofts.com/forums/login.php?do=login\n"+
                   "vb_login_username=thuannd123\n"+
                   "vb_login_password=\n"+
                   "s=257bf44c9dde4d72d059654b2c9585bb\n"+
                   "do=login\n"+
                   "vb_login_md5password=e5a61ab8beb8a48eb57bfed68888ba39\n"+
                   "vb_login_md5password_utf=e5a61ab8beb8a48eb57bfed68888ba39";
    
    
    byte [] bytes = loginWeb(login);
    writer.save(new File("F:\\Temp2\\webclient\\a.html"), bytes);
    
    bytes = loginWeb(login);
    writer.save(new File("F:\\Temp2\\webclient\\a1.html"), bytes);
    
    bytes = loadContent(home);
    writer.save(new File("F:\\Temp2\\webclient\\b.html"), bytes);
    
    
  }
  
}
