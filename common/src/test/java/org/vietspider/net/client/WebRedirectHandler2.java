/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.net.URI;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 5, 2008  
 */
class WebRedirectHandler2 extends DefaultRedirectStrategy {
  
  
  public boolean isRedirectRequested(
      final HttpResponse response, final HttpContext context) {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    int statusCode = response.getStatusLine().getStatusCode();
    switch (statusCode) {
    case HttpStatus.SC_MOVED_TEMPORARILY:
//      System.out.println(" move temp roi "+ HttpStatus.SC_MOVED_TEMPORARILY);
//    case HttpStatus.SC_MOVED_PERMANENTLY:
//      System.out.println(" thay cao kha "+ HttpStatus.SC_MOVED_PERMANENTLY);
    case HttpStatus.SC_SEE_OTHER:
    case HttpStatus.SC_TEMPORARY_REDIRECT:
      return true;
    default:
      return false;
    } //end of switch
  }
  
  public URI getLocationURI(
      final HttpRequest request,
      final HttpResponse response, 
      final HttpContext context) throws ProtocolException {
    URI uri = super.getLocationURI(request, response, context);
//    Header [] headers = response.getAllHeaders();
//    System.out.println("=======================================");
//    for(Header header : headers) {
//      System.out.println(header.getName() + " : "+ header.getValue());
//    }
//    System.out.println("==========================================");
//    System.out.println(uri.toString());
    return uri;
  }
}
