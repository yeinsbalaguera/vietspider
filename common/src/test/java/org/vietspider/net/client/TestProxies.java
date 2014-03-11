/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.File;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataWriter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 30, 2008  
 */
public class TestProxies {

  private static WebClient webClient = new WebClient();

  private static HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);

//  static {
//    methodHandler.setTimeout(30);
//  }

  public static byte[] loadContent(String address) throws Exception {
    try{
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);

      HttpResponse httpResponse = methodHandler.execute(address, "");

      HttpResponseReader httpResponseReader = new HttpResponseReader();
      return httpResponseReader.readBody(httpResponse);
    } catch(Exception exp){
      return null;
    }
  }

  public static void main(String[] args) throws Exception {
//    final String home = "http://www.talawas.org/";
//    final String address = "http://www.talawas.org/talaDB/showFile.php?res=9681&rb=0401";

        String home  = "http://vietnamnet.vn/";
    String address = "http://vietnamnet.vn/thegioi/2008/08/799026/";

    System.out.println(home);

    webClient.setUserAgent("Mozilla/5.0 (compatible; Yahoo! VN Slurp; http://help.yahoo.com/help/us/ysearch/slurp)");
    //    webClient.registryProxy("216.194.70.3", 8118, null, null);

    final org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();

    try {
      webClient.setURL(null, new URL(home));

      webClient.registryISAProxy("proxy", 8080, "vpbtc\\khach", "khach");
      byte[] bytes = loadContent(address);
      System.out.println(" bytes is "+ bytes);
      
      File file  = new File("D:\\Temp\\a.html");
      writer.save(file, bytes);

    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println(" da kiem tra xong toan bo\n\n");
    System.exit(0);
  }

  synchronized public static void deleteFolder(File file){
    File[] list = file.listFiles();
    for(File ele : list){
      ele.delete();
    }
  }
}
