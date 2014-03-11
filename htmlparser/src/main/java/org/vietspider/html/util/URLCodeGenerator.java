/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.util;

import java.net.URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 14, 2008  
 */
public class URLCodeGenerator {

  public boolean compareHost(String host1, String host2) {
    int i = host1.length() - 1;
    int j = host2.length() - 1;
    int counter = 0;
    while(i >= 0 && j >= 0) {
      if(Character.toLowerCase(host1.charAt(i)) != Character.toLowerCase(host2.charAt(j))) break;
      if(host1.charAt(i) == '.') counter++;
      i--;
      j--;
      if(i == -1 && (j == -1 || host2.charAt(j) == '.')) counter++;
      else if(j == -1 && (i  == -1 || host1.charAt(i) == '.')) counter++;
      if(counter >= 3) break;
    }
    if(counter >= 3) return true;
//    if(counter < 2) return false;
    if(host1.startsWith("www")) host1 = host1.substring(host1.indexOf('.') + 1);
    if(host2.startsWith("www")) host2 = host2.substring(host2.indexOf('.') + 1);
//    System.out.println(host1+ " : " + counter + " : " + host2 + " : "+ host1.equalsIgnoreCase(host2));
    return host1.equalsIgnoreCase(host2);
  }
  
  public synchronized int hashCode(URL url, String ignoresParam) {
    int hashCode = 0;
    
    // Generate the protocol part.
    String protocol = url.getProtocol();
    if (protocol != null) hashCode += protocol.hashCode();

    // Generate the host part.
    String host = url.getHost();
    if (host != null) hashCode += hashCodeHost(host);
    
    // Generate the path part.
    String path = url.getPath();
    if (path != null && !(path = path.trim()).isEmpty()) hashCode += path.hashCode();
    
    String query = url.getQuery();
    if(query != null && !(query = query.trim()).isEmpty()) {
      String [] elements = query.split("\\&");
      for(String element : elements) {
        if(element.isEmpty() || 
            (ignoresParam != null && element.startsWith(ignoresParam))) continue;
        hashCode += element.hashCode();
      }
    }
      
    // Generate the port part.
    if (url.getPort() == -1) hashCode += 80; else hashCode += url.getPort();

    // Generate the ref part.
    String ref = url.getRef();
    if (ref != null) hashCode += ref.hashCode();

    return hashCode;
  }
  
  private int hashCodeHost(String host) {
    int h = 0;
    int off = 0;
    while(off < 2 && off < host.length()) {
      if(Character.toLowerCase(host.charAt(off)) != 'w') break;
      off++;
    }
    
    if(off > 1) {
      while(off < host.length() && host.charAt(off) != '.') {
        off++;
      }
      if(off < host.length() && host.charAt(off) == '.') off++;
    } else {
      off = 0;
    }
    for (int i = off; i < host.length(); i++) {
      h = 31*h + Character.toLowerCase(host.charAt(off++));
    }
    return h;
  }
}
