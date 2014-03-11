/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 30, 2008  
 */
public class TestProxies2 {
  
  private static WebClient webClient = new WebClient();
  
  private static HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);
  
//  static {
//    methodHandler.setTimeout(30);
//  }
  
  private static byte[] bytes = null;
  
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
    final String home = "http://patentscope.wipo.int/";
    final String address = "http://patentscope.wipo.int/search/en/WO2013003029";
    
    final long WAIT = 30*1000;
//    String home  = "http://vietnamnet.vn/";
//    String address = "http://vietnamnet.vn/thegioi/2008/08/799026/";
    
    System.out.println(home);
    
    webClient.setUserAgent("Mozilla/5.0 (compatible; Yahoo! VN Slurp; http://help.yahoo.com/help/us/ysearch/slurp)");
//    webClient.registryProxy("216.194.70.3", 8118, null, null);
    
    File file  = new File("F:\\Bakup\\codes\\vietspider3\\test\\temp\\webclient\\proxies.txt") ;
    String textValue  = new String(RWData.getInstance().load(file), "utf-8");
    String [] proxies = textValue.split("\n");
    File newFile  = new File("F:\\Bakup\\codes\\vietspider3\\test\\temp\\webclient\\good.proxies.txt") ;
   
    final File folder = new File("F:\\Bakup\\codes\\vietspider3\\test\\temp\\webclient\\proxy\\");
    deleteFolder(folder);

    final List<String> aliveList = new ArrayList<String>();

    for(int i = 0; i < proxies.length; i++) {
      try {
        proxies[i] = proxies[i].trim();
        webClient.setURL(null, new URL(home));
        System.out.println("=== >"+ proxies[i]+" ==> "+i+"/"+proxies.length);
//      System.out.println("=== >"+Thread.currentThread().getId()+ " : "+ array[i]);
        String [] elements = proxies[i].split(":");
        String proxyHost = elements[0].trim();
        int proxyPort = Integer.parseInt(elements[1].trim()); 

        long start = System.currentTimeMillis();
        webClient.registryProxy(proxyHost, proxyPort, null, null);
        bytes = null;
       Thread thread =  new Thread() {
          public void run() {
            try {
              bytes = loadContent(address);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        };
        thread.start();
        
        while(true) {
          long end = System.currentTimeMillis();
          if((end - start) >= WAIT) {
            System.out.println(" timeout ");
            thread.stop();
            thread.interrupted();
            break;
          }
          try {
            Thread.sleep(2*1000l);
          } catch (Exception e) {
          }
        }
        
        if(bytes == null) continue;
        String fileName = proxies[i].replace('.', '_');
        fileName = fileName.replace(':', '_');
        RWData.getInstance().save(new File(folder, fileName+".html"), bytes);
        
        System.out.println(bytes.length+ " : "+ (bytes.length > 15000));
        if(bytes.length > 30000) {
          String value = proxyHost + ":" + proxyPort;
          if(aliveList.contains(value)) continue;
          aliveList.add(value);
          RWData.getInstance().append(newFile, (value+"\n").getBytes());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }//het for
    
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
