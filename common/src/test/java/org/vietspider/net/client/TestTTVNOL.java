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
public class TestTTVNOL {
  
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
//      httpGet.addHeader(new BasicHeader("Cookie", "ASP.NET_SessionId=udyg5l3pqyq4rgrhv43xfu45; path=/; HttpOnly"));

      if(httpGet == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);
      int statusCode = httpResponse.getStatusLine().getStatusCode();
      System.out.println(statusCode);
//      Header [] headers = httpResponse.getAllHeaders();
//      System.out.println("================= client la ====================================");
//      for(Header header : headers) {
//        System.out.println(header.getName()+ " : "+header.getValue());
//      }

      HttpResponseReader httpResponseReader = new HttpResponseReader();
      return httpResponseReader.readBody(httpResponse);
      
    } catch(Exception exp){
      exp.printStackTrace();
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
    String home = "http://www10.ttvnol.com/forum/vanhoc/447760.ttvn";
    
    webClient.setRedirectHandler(new WebRedirectHandler2());
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    System.out.println(home);
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    webClient.setURL(home, new URL(home));
//    
//    String login = "http://vnpassport.com/signin.aspx?id=http%3a%2f%2fwww9.ttvnol.com%2fweb%2fLoginProcess.aspx&r=1&p=th78%2brwMryPDw6eMPMi6pNzBenQhEEAIX3LyUJikvu1hhNH1kEv2gqKoii2NJlC4T8q26%2fJLURi86IgZ9sMNfyHkl%2bHX6Wuzl4mjAVJh7nSRkbANgcm9RDwa7alturmlyaVCbqXzmzibvMEzBoQFke85rL49pLehd9ewGX1fc78%3d&returnUrl=http://www8.ttvnol.com/forum/default.aspx\n"+
//                   "__EVENTTARGET= \n"+
//                   "__EVENTARGUMENT= \n"+
//                   "__VIEWSTATE=/wEPDwULLTE4NDcxNTkzMjYPZBYCAgEPZBYEAgUPDxYCHgRUZXh0BSrEkMSDbmcgbmjhuq1wIHbDoG8gd2Vic2l0ZSB3d3c5LnR0dm5vbC5jb21kZAIPDw9kFgIeCW9ua2V5ZG93bgUZZm5UcmFwS0QoYnRuU2lnbmluLGV2ZW50KWQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFgEFC2Noa1JlbWVtYmVya0Z9KbL2dAQil9JcJ9bc5sPfkYk=\n"+
//                   "txtUserName=headvances\n"+
//                   "h1=hex_md5(head123)\n"+
//                   "txtPass=head123\n"+
//                   "btnSignin=Đăng nhập\n"+
//                   "__EVENTVALIDATION=/wEWBgL4prDIDwKl1bKzCQLI7+btDALKw6LdBQLR55GJDgLD94v5Dflh17vijX6KEIDxWMRzuhcL97WJ";
    
    
//    byte [] bytes = loginWeb(login);
//    writer.save(new File("F:\\Temp2\\webclient\\a.html"), bytes);
//    
//    bytes = loginWeb(login);
//    writer.save(new File("F:\\Temp2\\webclient\\a1.html"), bytes);
    
    byte [] bytes = loadContent(home);
    
    System.out.println(" co so byte la "+ bytes.length);
    writer.save(new File("F:\\Temp2\\webclient\\b.html"), bytes);
    
    
  }
  
}
