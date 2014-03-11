/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.server.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 17, 2007  
 */
public class NotFoundHandler implements HttpRequestHandler  {

  @SuppressWarnings("unused")
  public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
    ByteArrayOutputStream arrayOutput = new ByteArrayOutputStream();
    arrayOutput.write("Resource not found!".getBytes());
    ByteArrayEntity byteArrayEntity = new ByteArrayEntity(arrayOutput.toByteArray());
    response.setStatusCode(HttpStatus.SC_NOT_FOUND);
    byteArrayEntity.setContentType("text/html");
    response.setEntity(byteArrayEntity);
  }

}
