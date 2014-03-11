/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link;

import java.net.URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 14, 2008  
 */
public class URLMd5Generator {

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
