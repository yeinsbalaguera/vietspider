/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler;

import java.io.IOException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.db.SystemProperties;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 27, 2011  
 */
public class EmbededRequestHandler implements HttpRequestHandler  {

  protected WebClient webClient;
  private String path;
  private String address;
  
  public EmbededRequestHandler() {
    SystemProperties properties = SystemProperties.getInstance();
    address = properties.getValue("embeded.connect.server");
    if(address == null || address.trim().isEmpty()) return;
    this.path = properties.getValue("embeded.connect.path");
    if(path == null || path.trim().isEmpty()) return;
    
    if(!SWProtocol.isHttp(address)) address = "http://" + address;
    webClient = new WebClient();
    try {
      webClient.setURL(null, new URL(address));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public String getPath() { return path; }

  @Override
  public void handle(HttpRequest request, 
      HttpResponse response, HttpContext context) throws HttpException, IOException {
    if(webClient == null) {
      ByteArrayEntity byteArrayEntity = new ByteArrayEntity("no.support".getBytes());
      byteArrayEntity.setContentType("text/plain");
      response.setEntity(byteArrayEntity);
      response.setStatusCode(HttpStatus.SC_NO_CONTENT);
      return;
    }
    
    String uri = request.getRequestLine().getUri();
    int idx = uri.indexOf(path);
    if(idx > -1) uri = uri.substring(idx + path.length());
    
//    System.out.println(" =====  > "+ address + uri);
    
    try {
      HttpPost httpPost = webClient.createPostMethod(address + uri, null, null, null);//new HttpPost(address + uri);
//      webClient.setHeaderValue(httpPost, null);
      httpPost.setHeaders(request.getAllHeaders());
      httpPost.addHeader(new BasicHeader("embeded.path", path));
      
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpPost);
      
//      Header header = httpResponse.getFirstHeader("Location");
//      if(header != null) {
//        String location = header.getValue();
//        if(location != null && !location.trim().isEmpty()) {
//          httpPost = new HttpPost(address + location);
//          webClient.setHeaderValue(httpPost, null);
//          httpPost.setHeaders(request.getAllHeaders());
//          httpResponse = webClient.execute(httpHost, httpPost);
//        }
//      }
//      
      Header [] headers = httpResponse.getAllHeaders();
      for(int i = 0; i < headers.length; i++) {
        if("Location".equals(headers[i].getName())) {
          headers[i] = new BasicHeader("Location", path + headers[i].getValue());
          response.addHeader(headers[i]);
        }
        
      }
      
      EmbededResponseReader reader = new EmbededResponseReader();
      byte [] bytes = reader.readBody(httpResponse);
//      System.out.println(new String(bytes, Application.CHARSET));
//      System.out.println(httpResponse.getEntity().getContentType());
      ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
//      byteArrayEntity.setContentEncoding(httpResponse.getEntity().getContentEncoding());
      byteArrayEntity.setContentType(httpResponse.getEntity().getContentType());
      response.setEntity(byteArrayEntity);
      
      response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
//      response.setStatusLine(httpResponse.getStatusLine());
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
      ByteArrayEntity byteArrayEntity = new ByteArrayEntity("Error!".getBytes());
      byteArrayEntity.setContentType("text/plain");
      response.setEntity(byteArrayEntity);
      response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
    }
   
  }
  
  

}
