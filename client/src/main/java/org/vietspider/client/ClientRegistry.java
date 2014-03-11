/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client;

import java.net.URL;
import java.util.prefs.Preferences;

import org.vietspider.client.common.ClientConnector2;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 11, 2008  
 */
public class ClientRegistry implements Runnable {
  
  private ClientConnector2 connector = new ClientConnector2();
  
  public byte[] loadContent(HttpMethodHandler methodHandler, String address) throws Exception {
    try{
      methodHandler.execute(address, "");
      return  methodHandler.readBody();
    } catch(Exception exp){
      return null;
    }
  }
  
  public ClientRegistry() {
    new Thread(this).start();
  }
  
  public void run() {
    Preferences prefs = Preferences.userNodeForPackage(ClientRegistry.class);
    try {
//      String value  = prefs.get("vietspider.registry", "");
//      System.out.println(" da gui thanh cong "+ value);
//      if(value.equals("true")) return;
    } catch (Exception e) {
    }
    
    WebClient webClient = new WebClient();
    HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);
    
//    methodHandler.setTimeout(60);
    String home = "http://nhudinhthuan.googlepages.com/";
    try {
      webClient.setURL(null, new URL(home));

      String address = "http://nhudinhthuan.googlepages.com/site.txt";
      byte[] obj = loadContent(methodHandler, address);

      connector.setURL(new String(obj));
      
      connector.get("/");
//      System.out.println(" da gui thanh cong ");
    } catch (Exception e) {
      return;
    }
    
    try {
      prefs.put("vietspider.registry", "true");
    } catch (Exception e) {
    }
  }
  
}
