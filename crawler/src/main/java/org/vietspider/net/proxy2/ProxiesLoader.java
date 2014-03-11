/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.proxy2;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.conn.HttpHostConnectException;
import org.vietspider.chars.TextSpliter;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.SystemProperties;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 8, 2011  
 */
public class ProxiesLoader extends Thread {
  
  private static ProxiesLoader LOADER;
  
  public final synchronized static void init() {
    if(LOADER != null) return;
    LOADER = new ProxiesLoader();
  }
  
  
  protected WebClient webClient = new WebClient();
  protected HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);

  private Queue<String> queue = new LinkedList<String>();
  private int date = -1;
  private Set<Integer> processed = new HashSet<Integer>();
  private List<Integer> hours = new ArrayList<Integer>();
//  private Set<Integer> schedule = new HashSet<Integer>();

  ProxiesLoader() {
    Calendar calendar = Calendar.getInstance();
    date = calendar.get(Calendar.DAY_OF_MONTH);
    
    try {
      String value = SystemProperties.getInstance().getValue("proxy.load");
      List<String> list = new TextSpliter().toList(value, ',');
      for(int i = 0; i < list.size(); i++) {
        hours.add(Integer.parseInt(list.get(i)));
      }
    } catch (Exception e) {
    }
//    schedule.add(13);
//    schedule.add(14);
    
    webClient.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0");
    new ProxiesChecker();
    
    
    this.start();
  }

  public void run() {
    while(true) {
      Calendar calendar = Calendar.getInstance();
      int d = calendar.get(Calendar.DAY_OF_MONTH);
      //new date
      if(date != d) {
        processed.clear();
        date = d;
      }

      int h = calendar.get(Calendar.HOUR_OF_DAY);
      if( (isHour(h)) && !processed.contains(h)) {
        processed.add(h);
        
        ProxyStorage.getInstance().clean();

        File file = UtilFile.getFile("system/proxy/", "proxy.source.txt");
        if(file.exists() && file.length() > 0) {
          try {
            String text = new String(RWData.getInstance().load(file), "utf-8");
            if(!text.trim().isEmpty()) {
              Collections.addAll(queue, text.split("\n"));
            }
          } catch (Exception e) {
            LogService.getInstance().setThrowable(e);
          }
        }
      }

      if(!queue.isEmpty()) {
        process(queue.poll());
      }

      try {
        Thread.sleep(5*1000l);
      } catch (Exception e) {
      }
    }
  }

  
  private boolean isHour(int h) {
    for(int i = 0; i < hours.size(); i++) {
      if(hours.get(i) == h) return true;
    }
    return false;
  }

  private void process(String address) {
    try {
      address = address.trim();
      URL url =  new URL(address);

      webClient.setURL(null, url);
      byte[] bytes = loadContent(address);
      if(bytes == null || bytes.length < 10) return;

      String value = toText(bytes);
      Pattern pattern = Pattern.compile("\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[:]\\p{Digit}+");

      Matcher matcher = pattern.matcher(value);
      while(matcher.find()) {
        ProxyStorage.getInstance().write(value.substring(matcher.start(), matcher.end()));
      }

      Pattern patternHost = Pattern.compile("\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}");
      Pattern patternPort = Pattern.compile("\\p{Digit}+");
      Matcher matcherHost = patternHost.matcher(value);
      Matcher matcherPort = patternPort.matcher(value);
      int start = 0;
      while(matcherHost.find(start)) {
        int startHost = matcherHost.start();
        int endHost = matcherHost.end();

        String host = value.substring(startHost, endHost);
        if(matcherPort.find(endHost)) {
          int startPort = matcherPort.start();
          int endPort = matcherPort.end();
          start = endPort;
          String port = value.substring(startPort, endPort);
          //            System.out.println(" thay co "+ host+ ":" + port);
          ProxyStorage.getInstance().write(host + ":" + port);
        } else {          
          start = endHost;
        }
      }

    } catch (HttpHostConnectException e) {
      LogService.getInstance().setMessage("PROXY", e, e.toString() + " "+ address);
    } catch (Exception e1) {
      LogService.getInstance().setMessage("PROXY", e1, e1.toString() + " "+ address);
    }
  }

  protected byte[] loadContent(String address) throws Exception {
    URLEncoder urlEncoder = new URLEncoder();
    address = urlEncoder.encode(address);

    HttpResponse httpResponse = methodHandler.execute(address, "");
    if(httpResponse == null) return null;

    return methodHandler.readBody();
  }
  
  private String toText(byte [] bytes) throws Exception {
    HTMLDocument document  = new HTMLParser2().createDocument(bytes, "utf-8");
    NodeIterator iterator = document.getRoot().iterator();
    StringBuilder builder = new StringBuilder();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(node.isNode(Name.CONTENT)) {
        if(builder.length() > 0) builder.append('\n');
        builder.append(node.getTextValue());
      }
    }
    return builder.toString();
  }
  
  public static void main(String[] args) {
    try {
      File file  = new File("F:\\Bakup\\codes\\vietspider3\\test\\news\\data\\");
      
      System.setProperty("save.link.download", "true");
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
      ProxiesLoader.init();
    } catch (Exception e) {
      e.printStackTrace();
    }      
  }


}
