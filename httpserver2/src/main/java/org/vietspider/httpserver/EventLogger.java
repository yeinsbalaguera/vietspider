/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.httpserver;

import java.io.File;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.impl.nio.DefaultNHttpServerConnection;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.EventListener;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 2, 2007  
 */
@SuppressWarnings("unused")
public class EventLogger  implements EventListener {
  
  private volatile boolean registry  = false;
  
  public EventLogger(boolean registry) {
    this.registry = registry;
  }
  

  public void connectionOpen(final NHttpConnection conn) {
    if(!registry || conn == null) return;
    if (conn instanceof DefaultNHttpServerConnection) {
      DefaultNHttpServerConnection connection = (DefaultNHttpServerConnection) conn;
      HttpRequest request = connection.getHttpRequest();
      if(request == null) return;
      Header header  = request.getFirstHeader("registry");
      if(header != null) {
        StringBuilder builder = new StringBuilder("CLIENT_REGISTRY: ");
        header  = connection.getHttpRequest().getFirstHeader("user.name");
        if(header != null) builder.append("user.name: ").append(header.getValue());
        
        header  = connection.getHttpRequest().getFirstHeader("os.name");
        if(header != null) builder.append(", os.name: ").append(header.getValue());
        builder.append(", ip address:").append(connection.getRemoteAddress());
        
        String name = String.valueOf((connection.getRemoteAddress().toString().hashCode()));
        File f = UtilFile.getFile("track/registry/", name);
        try {
          RWData.getInstance().save(f, builder.toString().getBytes());
        } catch (Exception e) {
        }
      }
    } 
  }

  public void connectionTimeout(final NHttpConnection conn) {
  }

  public void connectionClosed(final NHttpConnection conn) {
  }

  public void fatalIOException(final IOException ex, final NHttpConnection conn) {
//    LogService.getInstance().setMessage("SERVER", "I/O error: " + ex.getMessage());
  }

  public void fatalProtocolException(final HttpException ex, final NHttpConnection conn) {
//    LogService.getInstance().setMessage("SERVER", "HTTP error: " + ex.getMessage());
  }

}
