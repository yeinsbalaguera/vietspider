/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.net.post;

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
public final class WebRedirectHandler extends DefaultRedirectHandler {
  
  private final static String REDIRECT_ATTRIBUTE = "redirectCode";
  
  public final static String START_NEW_LINK = "crawler.add.element.";
  
//  private volatile Md5Codes codes;
  
//  private CrawlExecutor executor;
  
  public WebRedirectHandler(/*CrawlExecutor executor*/) {
  }
  
  @SuppressWarnings("unused")
  public boolean isRedirectRequested(final HttpResponse response, final HttpContext context) {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    int statusCode = response.getStatusLine().getStatusCode();
    switch (statusCode) {
    case HttpStatus.SC_MOVED_TEMPORARILY:
    case HttpStatus.SC_MOVED_PERMANENTLY:
    case HttpStatus.SC_SEE_OTHER:
    case HttpStatus.SC_TEMPORARY_REDIRECT:
//      context.setAttribute(REDIRECT_ATTRIBUTE, statusCode);
      return true;
    default:
      return false;
    } //end of switch
  }
  
  public URI getLocationURI(final HttpResponse response, final HttpContext context) throws ProtocolException {
    URI uri = super.getLocationURI(response, context);
//    System.out.println(response.getFirstHeader("referer"));
    Object value = context.getAttribute(REDIRECT_ATTRIBUTE);
    if(value == null)  return uri;
    
//    Integer statusCode = Integer.parseInt(value.toString());
//    if(statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
//      throw new ProtocolException(START_NEW_LINK + " " + uri.toString()); 
//    }
    return uri; 
  }

  @SuppressWarnings("serial")
  public static class CrawlRedirectException extends Exception {
  }
  
}
