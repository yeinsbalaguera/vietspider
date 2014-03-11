/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.server.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.vietspider.common.io.GZipIO;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.user.User;
import org.vietspider.users.Organization;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 28, 2007
 */
public abstract class CommonHandler implements HttpRequestHandler  {

  protected String contentType = "text/html";

  public void handle(final HttpRequest request,
      final HttpResponse response,
      final HttpContext context) throws HttpException, IOException {
    Header threadHeader = request.getFirstHeader("thread.executing");
    if(threadHeader == null) {
      execute(request, response, context);
      return;
    } 

    new Thread() {
      public void run() {
        try {
          execute(request, response, context);
        } catch (Throwable e) {
          LogService.getInstance().setThrowable("APPLICATION", e);
        }
      }
    }.start();
  }

  private void execute(final HttpRequest request,
      final HttpResponse response, final HttpContext context) throws IOException {
    Header streamHeader = request.getFirstHeader("stream.data");
    ByteArrayOutputStream arrayOutput = new ByteArrayOutputStream();

    if(streamHeader != null) {
      try {
        execute(request, response, context, null);
        return;
      } catch (Exception e) {
        arrayOutput.write(e.toString().getBytes());
      }
    } else {
      try {
        execute(request, response, context, arrayOutput);
      } catch (Exception e) {
        arrayOutput.write(e.toString().getBytes());
      }
    }
    
    if(response.getEntity() != null) return;

    byte [] bytes = arrayOutput.toByteArray();

    if(bytes.length > 512*1024) bytes = new GZipIO().zip(bytes);
    ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
    byteArrayEntity.setContentType(contentType);
    response.setEntity(byteArrayEntity);
  }

  public abstract void execute(final HttpRequest request, final HttpResponse response,
      final HttpContext context, OutputStream output) throws Exception ;
  
  protected byte[] getRequestData(HttpRequest request) throws Exception {
    BasicHttpEntityEnclosingRequest entityRequest = (BasicHttpEntityEnclosingRequest)request;
    byte[] bytes = RWData.getInstance().loadInputStream(entityRequest.getEntity().getContent()).toByteArray();
    return new GZipIO().unzip(bytes);
  }

  protected java.io.InputStream getRequestBody(HttpRequest request) throws IOException {
    BasicHttpEntityEnclosingRequest entityRequest = (BasicHttpEntityEnclosingRequest)request;
    return entityRequest.getEntity().getContent();
  }

  protected void logAction(HttpRequest request, String action, String message) throws Exception {
    Header userHeader = request.getFirstHeader("username");
    if(userHeader == null) throw new InvalidActionHandler();
    logAction(userHeader.getValue(), action, message);
  }

  protected void logAction(String username, String action, String message) throws Exception {
    StringBuilder builder = new StringBuilder(username);
    action = action.replace(' ', '.');
    builder.append(' ').append(action).append(' ').append(message);
    LogService.getInstance().setMessage("USER", null, builder.toString());
  }

  protected File getFile(HttpRequest request, boolean auCreate) throws Exception {
    Header header = request.getFirstHeader("file");
    if(header == null) return null;
    String fileName = header.getValue();
    int idx = fileName.lastIndexOf('/');
    String folder = fileName.substring(0, idx);
    fileName = fileName.substring(idx+1);
    if(auCreate) return UtilFile.getFile(folder, fileName);
    File file = new File(UtilFile.getFolder(folder), fileName);
    return file.exists() ? file : null;
  }

  @SuppressWarnings("serial")
  protected class InvalidActionHandler extends Exception {

    public String getMessage() { return "Invalid action handler."; }

  }
  
  protected boolean checkAdminRole(HttpRequest request) {
    Header header = request.getFirstHeader("username");
    String username = header != null  ? header.getValue() : null;
    if(username == null) return false;
    Organization org = Organization.getInstance();
    
    User user = org.loadInstance("user", User.class, username);
//    System.out.println(user.getUserName() +  " : " + (user.getPermission()  == User.ROLE_ADMIN));
    if(user.getPermission()  == User.ROLE_ADMIN) return true;
    LogService.getInstance().setMessage(null, " Invalid user " + username);
    return false;
  }
  
  @SuppressWarnings("unchecked")
  public <T> T readAsObject(byte[] _bytes) throws Exception {
    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(_bytes);
    ObjectInputStream objectInputStream = null;
    try {
      objectInputStream = new ObjectInputStream(byteInputStream);
      return (T)objectInputStream.readObject();
    } finally {
      try {
        if(byteInputStream != null) byteInputStream.close();
      } catch (Exception e) {
      }
      try {
        if(objectInputStream != null)  objectInputStream.close();
      } catch (Exception e) {
      }
    } 
  }
  
  public void writeObject(OutputStream output, Object value) {
    byte[] bytes = new byte[0];
    if(value == null) {
      try {
        output.write(bytes);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      return;
    }
    ByteArrayOutputStream bytesObject = new ByteArrayOutputStream();
    ObjectOutputStream objOutput = null;
    try {
      objOutput = new ObjectOutputStream(bytesObject);
      objOutput.writeObject(value);
      objOutput.flush();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    } finally {
      try {
        if(objOutput != null) objOutput.close();
      } catch (Exception e) {
      }
      try {
        if(bytesObject != null) bytesObject.close();
      } catch (Exception e) {
      }
    }

    bytes = bytesObject.toByteArray();
//    if(zip) bytes = new GZipIO().zip(bytes);
    try {
      output.write(bytes);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

}
