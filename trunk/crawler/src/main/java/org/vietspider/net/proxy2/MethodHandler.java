/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.proxy2;

import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.RedirectException;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.vietspider.common.io.LogService;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 18, 2007  
 */
class MethodHandler extends HttpMethodHandler {
  
  MethodHandler(WebClient webClient) {
    super(webClient);
  }

  public HttpResponse execute(String address) throws Exception {
    //    System.out.println(address);
    httpHost = webClient.createHttpHost(address);
    return execute(webClient.createGetMethod(address, ""));
  }
  
  private HttpResponse execute(AbortableHttpRequest httpRequest_) throws Exception {
    this.httpRequest = null;
    this.httpResponse = null;
    this.readData = 0l;
    this.httpRequest = httpRequest_;
    if(httpRequest == null) return null;

    try {
      httpResponse = webClient.execute(httpHost, (HttpRequest)httpRequest);
    } catch (RedirectException e) {
      LogService.getInstance().setMessage(e, ((HttpRequest)httpRequest).getRequestLine().getUri());
      return null;
    } catch (IndexOutOfBoundsException e) {
      return null;
    } catch (IllegalStateException e) {
      return null;
    } catch (SocketException e) {
      return null;
    } catch (ConnectionPoolTimeoutException e) {
      return null;
    } catch (InterruptedIOException e) { 
      return null;
    } catch (IllegalArgumentException e) {
//      e.printStackTrace();
    } catch (NoHttpResponseException e) {
    } catch (UnknownHostException e) {
      throw e;
    } 
    if(httpResponse == null) return null;
    return httpResponse;
  }

}
