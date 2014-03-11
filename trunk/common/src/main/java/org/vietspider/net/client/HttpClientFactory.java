/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpVersion;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.vietspider.net.apache.DefaultRequestDirector;
import org.vietspider.net.apache.DefaultResponseParser;
import org.vietspider.net.apache.SocketInputBuffer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 1, 2008  
 */
public class HttpClientFactory {

  public final static String USER_AGENT_NAME = "User-Agent";
  public final static String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 5.1; rv:25.0) Gecko/20100101 Firefox/25.0";

  public final static String CONNECTION_NAME = "Connection";
  public final static String CONNECTION_VALUE = "keep-alive";

  public final static String ACCEPT_ENCODING_NAME = "Accept-Encoding";
  public final static String ACCEPT_ENCODING_VALUE = "gzip, deflate";

  public final static String ACCEPT_CHARSET_NAME = "Accept-Charset"; 
  public final static String ACCEPT_CHARSET_VALUE = "ISO-8859-1,utf-8;q=0.7,*;q=0.7"; 
  // public final static String ACCEPT_CHARSET_VALUE = "UTF-8,*";

  public final static String KEEP_ALIVE_NAME = "Keep-Alive"; 
  public final static String KEEP_ALIVE_VALUE = "115";

  public final static String ACCEPT_LANGUAGE_NAME = "Accept-Language";
  public final static String ACCEPT_LANGUAGE_VALUE = "en-us,en;q=0.5";

  public final static String ACCEPT_NAME = "Accept";
  //  public final static String ACCEPT_VALUE = "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,image/*,*/*;q=0.5";
  public final static String ACCEPT_VALUE = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";

  public final static String CACHE_CONTROL_NAME = "Cache-Control";
  public final static String CACHE_CONTROL_VALUE ="max-age=0";

  public final static String CONTENT_TYPE_NAME ="Content-Type";
  public final static String CONTENT_TYPE_VALUE_FORM ="application/x-www-form-urlencoded";
  public final static String CONTENT_TYPE_VALUE_MULTIPART ="multipart/form-data";

  public static int TIMEOUT = 1;

  public final static String REFERER_NAME = "Referer";
  public final static String LOGIN_PROPERTY  = "Login";

  public final static String HOST_NAME  = "Host";

  //  private final static String HEX_MD5 = "hex_md5"; 

  /*public static AnonymousHttpClient createHttpClient(Proxies proxies) {
    SchemeRegistry schemeRegistry = createRegitry();
    HttpParams params = createHttpParams();

    ClientConnectionManager connectionManager =
      new ThreadSafeClientConnManager(params, schemeRegistry);

    AnonymousHttpClient client = new AnonymousHttpClient(connectionManager, params);
    client.setProxies(proxies);
    client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(3, false));

    return client;
  }*/
  
//  public static DefaultHttpClient createDefault() {
//    return createDefaultHttpClient(-1, -1);
//  }

  public static DefaultHttpClient createDefaultHttpClient(int socketTime, int connTimeout) {
    SchemeRegistry schemeRegistry = createRegitry();
    HttpParams params = createHttpParams(socketTime, connTimeout);
    // prepare parameters

    ThreadSafeClientConnManager connectionManager = 
      new ThreadSafeClientConnManager(schemeRegistry) {
      protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        return new DefaultClientConnectionOperator(schreg){
          public OperatedClientConnection createConnection() {
            return new DefaultClientConnection() {
              @Override
              protected HttpMessageParser createResponseParser(
                  final SessionInputBuffer buffer,
                  final HttpResponseFactory responseFactory, 
                  final HttpParams httpParams) {
                // override in derived class to specify a line parser
                return new DefaultResponseParser (buffer, null, responseFactory, httpParams);
              }

              @Override
              protected SessionInputBuffer createSessionInputBuffer(
                  final Socket socket,final int buffersize, final HttpParams _params) throws IOException {
                SessionInputBuffer inbuffer =  new SocketInputBuffer(socket, buffersize, _params);
                return inbuffer;
              }
            };
          }
        };
      }
    };
    
    connectionManager.setMaxTotal(5000);
    
    DefaultHttpClient client = new DefaultHttpClient(connectionManager, params) {

      private final Log log = LogFactory.getLog(getClass());

      @Override()
      protected RequestDirector createClientRequestDirector(
          final HttpRequestExecutor requestExec,
          final ClientConnectionManager conman,
          final ConnectionReuseStrategy reustrat,
          final ConnectionKeepAliveStrategy kastrat,
          final HttpRoutePlanner rouplan,
          final HttpProcessor httpProcessor,
          final HttpRequestRetryHandler retryHandler,
          final RedirectStrategy redirectStrategy,
          final AuthenticationHandler targetAuthHandler,
          final AuthenticationHandler proxyAuthHandler,
          final UserTokenHandler stateHandler,
          final HttpParams _params) {
        return new DefaultRequestDirector(
            log,
            requestExec,
            conman,
            reustrat,
            kastrat,
            rouplan,
            httpProcessor,
            retryHandler,
            redirectStrategy,
            targetAuthHandler,
            proxyAuthHandler,
            stateHandler,
            _params);
      }



      //      @Override
      //      protected HttpRequestExecutor createRequestExecutor() {
      //        return new HttpRequestExecutor2();
      //      }

    };
    client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(3, false));
    return client;
  }

  private final static SchemeRegistry createRegitry() {
    SchemeRegistry schemeRegistry = new SchemeRegistry();
    // Register the "http" and "https" protocol schemes, they are
    // required by the default operator to look up socket factories.
    SchemeSocketFactory sf = PlainSocketFactory.getSocketFactory();
    schemeRegistry.register(new Scheme("http", 80, sf));
    //    sf = SSLSocketFactory.getSocketFactory();

    SSLSocketFactory ssf = SSLSocketFactory.getSocketFactory();
    schemeRegistry.register(new Scheme("https", 443, ssf));

    return schemeRegistry;
  }

  private final static HttpParams createHttpParams(int socketTime, int connTimeout) {
    HttpParams params = new BasicHttpParams();
    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    HttpProtocolParams.setContentCharset(params, "UTF-8");
    HttpProtocolParams.setUseExpectContinue(params, true);

    if(socketTime > 0) {
      params.setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTime);
    }
    if(connTimeout > 0) {
      params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connTimeout);
    }
   
//    params.setParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true);
    params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
    params.setBooleanParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
    params.setIntParameter(ClientPNames.MAX_REDIRECTS, 100);
    //    params.setLongParameter(ConnManagerPNames.TIMEOUT, TIMEOUT);
//    ConnManagerParams.setMaxTotalConnections(params, 5000);
//    ConnManagerParams.setMaxConnectionsPerRoute(params,  new ConnPerRoute() {
//      public int getMaxForRoute(HttpRoute _route) { return 10000000; }
//    });
    //    params.setLongParameter(ClientPNames.conCONNECTION_MANAGER_TIMEOUT, TIMEOUT);
    //    HttpConnectionManagerParams.setMaxTotalConnections(params, 3000);
    return params;
  }

  public final static String hexMd5(String password) throws Exception {
    //  System.out.println("===== > "+ password);
    MessageDigest algorithm = MessageDigest.getInstance("MD5");
    algorithm.reset();
    algorithm.update(password.getBytes());
    byte messageDigest[] = algorithm.digest();

    StringBuffer hexString = new StringBuffer();
    for (int i=0;i < messageDigest.length; i++) {
      hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
    }
    return hexString.toString();
  }


}
