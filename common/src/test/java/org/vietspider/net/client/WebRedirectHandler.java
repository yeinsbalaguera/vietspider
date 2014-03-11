/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 5, 2008  
 */
class WebRedirectHandler extends DefaultRedirectHandler {
  
  
  private final static String REDIRECT_ATTRIBUTE = "redirectCode";
  
  public WebRedirectHandler() {
  }
  
  public boolean isRedirectRequested(
      final HttpResponse response, final HttpContext context) {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    int statusCode = response.getStatusLine().getStatusCode();
    switch (statusCode) {
    case HttpStatus.SC_MOVED_TEMPORARILY:
    case HttpStatus.SC_MOVED_PERMANENTLY:
    case HttpStatus.SC_SEE_OTHER:
    case HttpStatus.SC_TEMPORARY_REDIRECT:
      context.setAttribute(REDIRECT_ATTRIBUTE, statusCode);
      return true;
    default:
      return false;
    } //end of switch
  }
  
  public URI getLocationURI(final HttpResponse response, final HttpContext context) throws ProtocolException {
    URI uri = super.getLocationURI(response, context);
    Object value = context.getAttribute(REDIRECT_ATTRIBUTE);
    System.out.println("redirect "+uri);
    if(value == null)  return uri;
    Integer statusCode = Integer.parseInt(value.toString());
    System.out.println(" code la === > "+ statusCode);
    if(statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
      System.out.println(" xay ra roi nhe ");
      throw new ProtocolException("Crawler.Add."+ uri.toString()); 
    }
    return uri; 
  }
}
