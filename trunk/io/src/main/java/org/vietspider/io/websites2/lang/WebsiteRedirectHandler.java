/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.io.websites2.lang;

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
public final class WebsiteRedirectHandler extends DefaultRedirectStrategy {
  
  private final static String REDIRECT_ATTRIBUTE = "redirectCode";
  
  private URI uri;
  
  public WebsiteRedirectHandler(/*CrawlExecutor executor*/) {
  }
  
  public URI getURI() { return uri; }
  
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
  
  public URI getLocationURI(final HttpRequest request, 
      final HttpResponse response, final HttpContext context) throws ProtocolException {
    uri = super.getLocationURI(request, response, context);
    Object value = context.getAttribute(REDIRECT_ATTRIBUTE);
    if(value == null)  return uri;
    return uri; 
  }
  
}
