/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.vietspider.browser.form.Form;
import org.vietspider.browser.form.FormUtils;
import org.vietspider.browser.form.Param;
import org.vietspider.chars.CharsDecoder;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataWriter;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 21, 2007  
 */
public class TestSearchForm {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loadContent(String address, List<NameValuePair> nvpList) throws Exception {
    HttpPost httpPost = null;
    try {
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      
      httpPost = webClient.createPostMethod(address, "", nvpList, "utf-8");      

      if(httpPost == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      
      HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);
      HttpResponse httpResponse = webClient.execute(httpHost, httpPost);

//      HttpEntity httpEntity = httpResponse.getEntity();
//      return webClient.decodeResponse(webClient.readData(httpEntity.getContent()), httpEntity.getContentEncoding());
      return  methodHandler.readBody(httpResponse);
      
    } catch(Exception exp){
      exp.printStackTrace();
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
    //?target=listcategory&page=5&cat=404&ord=0
    String home = "http://www.vinabook.com/tieng-anh-m13i404/";
    
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    System.out.println(home);
    
    
    webClient.setURL(null, new URL(home));
    
    List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
    nvpList.add(new BasicNameValuePair("target", "listcategory"));
    nvpList.add(new BasicNameValuePair("page", "7"));
    nvpList.add(new BasicNameValuePair("cat", "404"));
    nvpList.add(new BasicNameValuePair("ord", "0"));
    
    
    byte [] bytes = loadContent(home, nvpList);
    char [] chars = CharsDecoder.decode("utf-8", bytes, 0, bytes.length);
    List<NodeImpl> tokens =  new HTMLParser2().createTokens(chars);
    
    FormUtils formUtils = new FormUtils();
    List<Form> forms = formUtils.searchForm(tokens);
    for(Form form : forms ) {
      System.out.println(" ============================================= ");
      System.out.println(form.getName());
      System.out.println(form.getMethod());
      System.out.println(form.getAction());
      List<Param> params = form.getParams();
      for(Param param : params) {
        System.out.println(param.getName() + " : "+ param.getValue());
      }
    }
    System.out.println(forms.size());
    
    org.vietspider.common.io.RWData.getInstance().save(new File("D:\\Temp\\b.html"), bytes);
    System.exit(0);
  }
  
}
