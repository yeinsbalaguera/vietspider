/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler;

import java.net.InetAddress;
import java.net.URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 17, 2007  
 */
public class TestURLHashCode {

  private static int toHashCode(URL u) {
    int hashCode = 0;
    try {
      System.out.println("inet address "+ InetAddress.getByName(u.getHost()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    // Generate the protocol part.
    String protocol = u.getProtocol();
    if (protocol != null) hashCode += protocol.hashCode();

    // Generate the host part.
    String host = u.getHost();
    if (host != null) hashCode += host.toLowerCase().hashCode();
    
    // Generate the path part.
    String path = u.getPath();
    if (path != null && !(path = path.trim()).isEmpty()) hashCode += path.hashCode();
    
    String query = u.getQuery();
    if(query != null && !(query = query.trim()).isEmpty()) {
      String [] elements = query.split("\\&");
      for(String ele : elements) {
        if(!(ele  = ele.trim()).isEmpty()) hashCode += ele.hashCode();
      }
    }
      
    // Generate the port part.
    if (u.getPort() == -1) hashCode += 80; else hashCode += u.getPort();

    // Generate the ref part.
    String ref = u.getRef();
    if (ref != null) hashCode += ref.hashCode();

    return hashCode;
  }
  
  private  static void split(String query) {
    String [] elements = query.split("\\&");
    for(String ele : elements) {
      System.out.println(ele);
    }
  }
  
  private static int toHashCode(char [] values) {
    int h = 0;
    int off = 0;
    for (int i = 0; i < values.length; i++) {
      System.out.println((int)values[off]);
      h = 31*h + values[off++];
    }
    return h;
  }

  public static void main(String[] args) throws Exception {
    String value = "hello";
//  System.out.println(value.hashCode());
//  System.out.println(toHashCode(value.toCharArray()));

//    String a1 = "http://www.vnmedia.vn/newsdetail.asp?NewsId=105943";
//    String a2 = "http://www.vnmedia.vn/newsdetail.asp?NewsId=105943&";
    String a1 = "http://www2.thanhnien.com.vn/news/default.aspx?";
    String a2 = "http://www.thanhnien.com.vn/news/default.aspx?";

    System.out.println(toHashCode(new URL(a1)));
    System.out.println(toHashCode(new URL(a2)));
//    System.out.println(new URL(a1).hashCode());

//    split("id=1250043&scid=739861");
  }
}
