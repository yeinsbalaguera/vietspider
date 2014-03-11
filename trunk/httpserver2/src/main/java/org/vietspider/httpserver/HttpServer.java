/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.httpserver;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.nio.DefaultServerIOEventDispatch;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.nio.protocol.BufferingHttpServiceHandler;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 22, 2010  
 */
public class HttpServer {

  private final static Map<Integer, HttpServer> servers = new ConcurrentHashMap<Integer, HttpServer>();

  public static synchronized HttpServer getInstance(boolean clientRegistry, int port){
    HttpServer server = servers.get(port);
    if(server != null) return server;
    server = new HttpServer(clientRegistry, port);
    servers.put(port, server);
    return server;
  }

  public synchronized static void restart(int port) {
    Iterator<Integer> iterator = servers.keySet().iterator();
    while(iterator.hasNext()) {
      HttpServer _server = servers.get(iterator.next());
      if(_server.getPort() != port) continue;
      boolean clientRegistry = _server.isClientRegistry();
      shutdown(port);

      final HttpServer server = new HttpServer(clientRegistry, port);
      servers.put(port, server);
      new Thread(new Runnable() {
        public void run() {
          try {
            server._listen();
          } catch (Throwable e) {
            LogService.getInstance().setThrowable(e);
          }
        }
      }).start();
    }
  }

  public synchronized static void listen() {
    Iterator<Integer> iterator = servers.keySet().iterator();
    while(iterator.hasNext()) {
      final HttpServer _server = servers.get(iterator.next());
      if(_server.isListening()) continue;
      new Thread(new Runnable() {
        public void run() {
          int time = 0;
          while(time < 3) {
            try {
              _server._listen();
              time = 5;
            } catch (Throwable e) {
              LogService.getInstance().setThrowable(e);
              time++;
            }
          }
        }
      }).start();
    }
  }

  public synchronized static void shutdown(int port) {
    HttpServer server = servers.get(port);
    if(server == null) return;
    //    System.out.println(server);
    server._shutdown();
    //    System.out.println(server.isListening());
  }

  protected ListeningIOReactor ioReactor;
  protected IOEventDispatch ioEventDispatch;

  protected HttpParams params;
  protected BufferingHttpServiceHandler handler;
  protected HttpRequestHandlerRegistry reqistry;

  private int port;
  private boolean clientRegistry = false;

  private volatile boolean listening = false; 

  private HttpServer(boolean clientRegistry, int port) {
    this.port = port;
    this.clientRegistry = clientRegistry;

    Application.addShutdown(new Application.IShutdown() {
      public int getPriority() { return 2; }
      public void execute() {
        if(ioReactor == null) return;
        try {
          ioReactor.shutdown();         
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    params = new BasicHttpParams();
    params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 300000)
    .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
    .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
    .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
    .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "VietSpider-Server/1.1");

    BasicHttpProcessor httpproc = new BasicHttpProcessor();
    httpproc.addInterceptor(new ResponseDate());
    httpproc.addInterceptor(new ResponseServer());
    httpproc.addInterceptor(new ResponseContent());
    httpproc.addInterceptor(new ResponseConnControl());

    handler = new BufferingHttpServiceHandler(
        httpproc, new DefaultHttpResponseFactory(), new DefaultConnectionReuseStrategy(), params);

    handler.setEventListener(new EventLogger(clientRegistry));

    reqistry = new HttpRequestHandlerRegistry();
  }

  public boolean isClientRegistry() { return clientRegistry; }

  private void _listen() throws Exception {
    handler.setHandlerResolver(reqistry);

    ioEventDispatch = new DefaultServerIOEventDispatch(handler, params);
    ioReactor = new DefaultListeningIOReactor(2, params);

    //  ((DefaultServerIOEventDispatch)ioReactor).(new IOReactorExceptionHandler() {
    //
    //          public boolean handle(IOException ex) {
    //              if (ex instanceof BindException) {
    //                  return true;
    //              }
    //              return false;
    //          }
    //
    //          public boolean handle(RuntimeException ex) {
    ////              if (ex instanceof UnsupportedOperationException) {
    ////                  // Unsupported operations considered OK to ignore
    ////                  return true;
    ////              }
    //              return false;
    //          }
    //          
    //      });

    listening = true;

    try {
      ioReactor.listen(new InetSocketAddress("0.0.0.0", port));
    } catch (Throwable e) {
      listening = false;
      LogService.getInstance().setThrowable(e);
      e.printStackTrace();
    }

    try {
      ioReactor.execute(ioEventDispatch);
    } catch (InterruptedIOException ex) {
      listening = false;
    } catch (IOException e) {
      listening = false;
      LogService.getInstance().setThrowable(e);      
    } catch(Throwable e2) {
      listening = false;
      LogService.getInstance().setThrowable(e2);
    }
  }

  public void _shutdown() {
    try {
      ioReactor.shutdown(); 
      listening = false;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public BufferingHttpServiceHandler getHandler() { return handler; }

  public int getPort() { return port; }

  public HttpRequestHandlerRegistry getReqistry() { return reqistry; }

  public boolean isListening() { return listening; }

}
