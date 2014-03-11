/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.vietspider.browser.form.Form;
import org.vietspider.browser.form.FormUtils;
import org.vietspider.browser.form.Param;
import org.vietspider.common.io.LogService;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 18, 2009  
 */
//https://www.bpn.gov/CCRSearch/Search.aspx
//http://www.bbb.org/us/Find-Business-Reviews/
public class RefererFormHandler {

  protected String action = null; 

  protected List<Param> defaultParams = new ArrayList<Param>();
  
  private WebClient webClient;

  public RefererFormHandler(WebClient client) {
    this.webClient = client;
  }

  public void execute(String referer, HttpResponse httpResponse) {
    if(httpResponse == null || referer == null) return;
//    System.out.println(httpResponse.getFirstHeader("Cookie"));
    HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);
    try {
      byte [] bytes = methodHandler.readBody(httpResponse);
      if(bytes == null || bytes.length < 10) return;
      HTMLParserDetector htmlParser = new HTMLParserDetector();
      List<NodeImpl> tokens  = htmlParser.createTokens(bytes);
      
      FormUtils formUtils = new FormUtils(); 
      List<Form> forms = formUtils.searchForm(tokens);
      if(forms.size() < 1) return;
      FormPostHandler formPostHandler = new FormPostHandler();
//      System.out.println(" thay co forms "+ forms.get(0).getName());
      formPostHandler.post(forms.get(0), forms.get(0).getName(), webClient, referer, 0);
    } catch (SocketException e) {
      return;
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }


}
