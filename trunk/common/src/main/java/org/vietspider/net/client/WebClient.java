/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import static org.vietspider.net.client.HttpClientFactory.ACCEPT_CHARSET_NAME;
import static org.vietspider.net.client.HttpClientFactory.ACCEPT_CHARSET_VALUE;
import static org.vietspider.net.client.HttpClientFactory.ACCEPT_ENCODING_NAME;
import static org.vietspider.net.client.HttpClientFactory.ACCEPT_ENCODING_VALUE;
import static org.vietspider.net.client.HttpClientFactory.ACCEPT_LANGUAGE_NAME;
import static org.vietspider.net.client.HttpClientFactory.ACCEPT_LANGUAGE_VALUE;
import static org.vietspider.net.client.HttpClientFactory.ACCEPT_NAME;
import static org.vietspider.net.client.HttpClientFactory.ACCEPT_VALUE;
import static org.vietspider.net.client.HttpClientFactory.CACHE_CONTROL_NAME;
import static org.vietspider.net.client.HttpClientFactory.CACHE_CONTROL_VALUE;
import static org.vietspider.net.client.HttpClientFactory.CONNECTION_NAME;
import static org.vietspider.net.client.HttpClientFactory.CONNECTION_VALUE;
import static org.vietspider.net.client.HttpClientFactory.CONTENT_TYPE_NAME;
import static org.vietspider.net.client.HttpClientFactory.CONTENT_TYPE_VALUE_FORM;
import static org.vietspider.net.client.HttpClientFactory.CONTENT_TYPE_VALUE_MULTIPART;
import static org.vietspider.net.client.HttpClientFactory.KEEP_ALIVE_NAME;
import static org.vietspider.net.client.HttpClientFactory.KEEP_ALIVE_VALUE;
import static org.vietspider.net.client.HttpClientFactory.REFERER_NAME;
import static org.vietspider.net.client.HttpClientFactory.USER_AGENT_NAME;
import static org.vietspider.net.client.HttpClientFactory.USER_AGENT_VALUE;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.vietspider.cache.InmemoryCache;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 23, 2007  
 */
public class WebClient implements Serializable {
  
  private final static long serialVersionUID = -1l;

  private final static short NORMAL = 1;
  private final static short SLOW = 0;
  private final static short VERY_SLOW = -1;
  private final static short INVISIBLE = -2;

  private volatile boolean log = false;
  private volatile short type_execute = NORMAL; 

  protected volatile String host;
  private volatile String userAgent;

  protected volatile DefaultHttpClient httpClient;
  protected volatile RedirectStrategy redirectHandler; 

  protected volatile InmemoryCache<String, char[]> cacheData;

  private volatile int badRequest = 0;
  //  private volatile Proxies httpProxies;

  public WebClient() {
    httpClient = HttpClientFactory.createDefaultHttpClient(-1, -1);
    cacheData = new InmemoryCache<String, char[]>("page", 10);
    cacheData.setLiveTime(15*60);
    /*if(Application.SERVER_PROPERTIES != null){
      setProxy(Application.SERVER_PROPERTIES);
    } else if(Application.CLIENT_PROPERTIES != null){
      setProxy(Application.CLIENT_PROPERTIES);
    }*/
  }
  
  public WebClient(DefaultHttpClient _httpClient) {
    this.httpClient = _httpClient;
//    httpClient = HttpClientFactory.createDefaultHttpClient(true);
    cacheData = new InmemoryCache<String, char[]>("page", 10);
    cacheData.setLiveTime(15*60);
    /*if(Application.SERVER_PROPERTIES != null){
      setProxy(Application.SERVER_PROPERTIES);
    } else if(Application.CLIENT_PROPERTIES != null){
      setProxy(Application.CLIENT_PROPERTIES);
    }*/
  }

  public void registryProxy(String proxyHost) {
    registryProxy(ProxiesMonitor.createProxy(proxyHost));
  }

  public void registryProxy(String proxyHost, int proxyPort, String username, String password) {
    if(username != null) {
      registryISAProxy(proxyHost, proxyPort, username, password);
      return;
    }
    //    System.out.println("==> proxy "+host+ " : "+ port);
    registryProxy(new HttpHost(proxyHost, proxyPort, "http"));
  }

  public void registryISAProxy(String proxyHost, int proxyPort, String username, String password) {
//    System.out.println(hashCode());
    httpClient.getCredentialsProvider().setCredentials(
        new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(username, password));

    registryProxy(new HttpHost(proxyHost, proxyPort, "http")); 

    //    httpClient.getCredentialsProvider().setCredentials(
    //        new AuthScope(proxyHost, proxyPort), 
    //        new NTCredentials(username, password, proxyHost, proxyHost));
  }

  public void registryProxy(HttpHost httpProxy) {
    if(httpProxy == null) {
      httpClient.getParams().removeParameter(ConnRoutePNames.DEFAULT_PROXY);
      return;
    }
//    System.out.println(" vao day roi ");
    httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpProxy);
//    System.out.println("proxy 1 " + httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY));
  }
  
  public HttpResponse setURL(String referer, final URL url, String blind) throws Exception {
    this.setURL(url);
    ProxiesMonitor.getInstance().put(host, blind);
    if(referer == null || referer.trim().isEmpty()) return null;
    return execute(createHttpHost(referer), createGetMethod(referer, ""));
  }
  
  public HttpResponse setURL(String referer, URL url) throws Exception {
    this.setURL(url);
    if(referer == null || referer.trim().isEmpty()) return null;
    return execute(createHttpHost(referer), createGetMethod(referer, ""));
  }
  
  public HttpResponse setURL(String referer, URL url, DefaultHttpClient _httpClient) throws Exception {
    this.setURL(url, _httpClient);
    if(referer == null || referer.trim().isEmpty()) return null;
    return execute(createHttpHost(referer), createGetMethod(referer, ""));
  }

  protected final void setURL(URL url) throws Exception {
    this.setURL(url, HttpClientFactory.createDefaultHttpClient(-1, -1));
  }
  
  protected final void setURL(URL url, DefaultHttpClient _httpClient) throws Exception {
    if(httpClient != null) {
      httpClient.clearRequestInterceptors();
      httpClient.clearResponseInterceptors();
    }
    
//    System.out.println(" chay thu cai nay nhe " + httpClient);
    httpClient = _httpClient;
    if(redirectHandler != null) {
      httpClient.setRedirectStrategy(redirectHandler);
    }
//    }

//    httpClient.getParams().removeParameter(ConnRoutePNames.DEFAULT_PROXY);

    type_execute = NORMAL;
    this.host = url.getHost();
    this.cacheData.clear();
    this.badRequest = 0;
  }

  public void shutdown() {
    cacheData.clear();
    httpClient.clearRequestInterceptors();
    httpClient.clearResponseInterceptors();
    httpClient.getConnectionManager().shutdown();
  }

  public HttpClient getHttpClient() { return httpClient; }

  public void clearCookies() throws Exception {
    httpClient.getCookieStore().clear();
  }

  public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) throws Exception {
    //    if(httpHost.getHostName().indexOf("hochiminh") > -1) {
    //      List<Cookie> cookies = httpClient.getCookieStore().getCookies();
    //      for(int i = 0; i < cookies.size(); i++) {
    //        System.out.println(cookies.get(i).getName() + " : "+ cookies.get(i).getValue());
    //      }

    //      org.apache.http.Header [] headers = httpRequest.getAllHeaders();
    //      StringBuilder builder = new StringBuilder("================Header Value=====================\n");
    //      for(org.apache.http.Header header : headers) {
    //        builder.append(header.getName()).append(':').append(header.getValue()).append('\n');
    //      }
    //      builder.append("==========================================").append('\n');
    //      LogService.getInstance().setMessage("WEB", null, builder.toString());
    //    }
    
//    HttpHost httpHost2 = (HttpHost)httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY);
//    System.out.println("proxy la  " + httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY));
//    System.out.println(httpClient.getCredentialsProvider().getCredentials(
//        new AuthScope(httpHost2.getHostName(), httpHost2.getPort())));

    //    System.out.println("thay "+ httpRequest.getRequestLine().getUri()+ "  : "+slow);
    if(type_execute == SLOW) {
      try {
        Thread.sleep(7*1000l);
      } catch (Exception e) {
      } 
    } else if(type_execute == VERY_SLOW) {
      try {
        Thread.sleep(60*1000l);
      } catch (Exception e) {
      } 
    } else if(type_execute == INVISIBLE) {
      clearCookies();
    }
    
//    Header [] headers = httpRequest.getAllHeaders();
//    for(int i = 0; i < headers.length; i++) {
//      System.out.println(headers[i].getName()+" : "+ headers[i].getValue());
//    }

    return httpClient.execute(httpHost, httpRequest);
    /*{
      HttpResponse httpResponse  = httpClient.execute(httpHost, httpRequest);
      org.apache.http.Header [] headers = httpResponse.getAllHeaders();
      StringBuilder builder = new StringBuilder("================Header Value=====================\n");
      for(org.apache.http.Header header : headers) {
        builder.append(header.getName()).append(':').append(header.getValue()).append('\n');
      }
      builder.append("==========================================").append('\n');
      LogService.getInstance().setMessage("WEB", null, builder.toString());

      return httpResponse;
    }*/
  }

  public void cacheResponse(String address, char[] value) {
    if(cacheData.size() > 10) cacheData.clear();
    cacheData.putCachedObject(address, value);
  }

  public InmemoryCache<String, char[]> getCacheData() { return cacheData; }

  public String getHost() { return host; }

  public void setRedirectHandler(RedirectStrategy redirectHandler) {
    this.redirectHandler = redirectHandler;
  }

  public String getUserAgent() { return userAgent; }
  public void setUserAgent(String agent) { 
    this.userAgent = null;
    //    System.out.println("  chuan bi put user agent "+ userAgent_+ " : " + "slow".equalsIgnoreCase(userAgent_));
    if("slow".equals(agent)) {
      type_execute = SLOW;
      return;
    } 

    if("very slow".equals(agent)) {
      type_execute = VERY_SLOW;
      return;
    } 

    if("invisible".equals(agent)) {
      type_execute = INVISIBLE;
      return;
    }

    type_execute = NORMAL;
    this.userAgent = agent;
  }

  public HttpGet createGetMethod(String link, String referer) throws Exception {    
    HttpGet httpGet = RequestManager.getInstance().createGet(this, referer, link);
//    httpGet.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
//    httpGet.setRequestHeader("Cookie", "special-cookie=value");
//    setHeaderValue(httpGet, referer);
    return httpGet;
  }

  public HttpHost createHttpHost(String address) {
    URL url  = null;
    boolean isSecure = address.toLowerCase().startsWith("https://");
    try {
      url  = new URL(address);
    } catch (Exception e) {
      return isSecure ? new HttpHost(host, 80, "https") : new HttpHost(host, 80);
    }
    int port = url.getPort();
    int dPort = url.getDefaultPort();
    if(port == -1 || port == 80) {
      return isSecure ? new HttpHost(url.getHost(), dPort, "https") : new HttpHost(url.getHost(), dPort);
    } 
    return isSecure ? new HttpHost(url.getHost(), port, "https") :  new HttpHost(url.getHost(), port);
  }

  public HttpPost createFormPostMethod(String link, 
      String referer, List<NameValuePair> nvpList, String charset) throws Exception {   
    HttpPost httpPost = RequestManager.getInstance().createPost(this, referer, link);
//    setHeaderValue(httpPost, referer);
    httpPost.addHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_VALUE_FORM);

    if(log) {
      //for test
      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvpList, charset);
      httpPost.setEntity(entity);
      byte [] bytes = RWData.getInstance().loadInputStream(entity.getContent()).toByteArray();
      StringBuilder builder = new StringBuilder("=====================Post Value=====================\n");
      builder.append(new String(bytes, charset)).append('\n');
      builder.append("==========================================").append('\n');
      LogService.getInstance().setMessage("WEB", null, builder.toString());
      //end test
    } else {
      httpPost.setEntity(new UrlEncodedFormEntity(nvpList, charset));
    }
//    long len = httpPost.getEntity().getContentLength();
//    if(len > 0) {
//      httpPost.addHeader(new BasicHeader("Content-Length", String.valueOf(len)));
//    }

    return httpPost;
  }
  
  public HttpPost createMultiPartFormPostMethod(String link, 
      String referer, List<NameValuePair> nvpList, String boundary, String charset) throws Exception {
    HttpPost httpPost = RequestManager.getInstance().createPost(this, referer, link);
//    setHeaderValue(httpPost, referer);
    if(boundary == null) {
      httpPost.addHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_VALUE_MULTIPART);
    } else {
      httpPost.addHeader(CONTENT_TYPE_NAME,
          CONTENT_TYPE_VALUE_MULTIPART + "; boundary=" + boundary);
    }
   

    Charset _charset = Charset.forName(charset);
    MultipartEntity entity = null;
    if(boundary != null) {
      entity = new MultipartEntity(
          HttpMultipartMode.BROWSER_COMPATIBLE, boundary, _charset);
    } else {
      entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    }
    
    for(int i = 0; i < nvpList.size(); i++) {
      NameValuePair nv = nvpList.get(i);
      String value = nv.getValue();
//      System.out.println(" gia tri value ="+ value);
      if(value.indexOf("system" + File.separator + "plugin") > -1 
          && value.indexOf("_temp"+ File.separator) > -1) {
        String type = null;
        if(value.startsWith("type")) {
          int start = value.indexOf(':');
          int end = value.indexOf(' ', start+1);
          type = value.substring(start, end);
          value = value.substring(end+1).trim();
//          System.out.println(" da thay type "+ type);
//          System.out.println(" path "+ value);
        }
        final String mimeType = type;
        File file = new File(value);
        FileBody fileBody = type == null ? new FileBody(file) : new FileBody(file) {
          public String getMimeType() {
            return mimeType;
          }
        };
        entity.addPart(nv.getName(), fileBody);
      } else {
        entity.addPart(nv.getName(), new StringBody(value, _charset));
      }
    }
    httpPost.setEntity(entity);

//    httpPost.removeHeaders("Content-Length");
//    httpPost.addHeader("Content-Length", String.valueOf(entity.getContentLength()));
    
    return httpPost;
  }

  public HttpPost createPostMethod(String link, 
      String referer, List<NameValuePair> nvpList, String charset) throws Exception {    
    HttpPost httpPost = RequestManager.getInstance().createPost(this, referer, link);
//    setHeaderValue(httpPost, referer);

    if(nvpList == null) return httpPost;
    
    httpPost.setEntity(new UrlEncodedFormEntity(nvpList, charset));

    return httpPost;
  }

  public void setHeaderValue(HttpUriRequest httpRequest, String referer) {
    String requestHost  = httpRequest.getURI().getHost();
    if(requestHost == null) {
      try {
        requestHost  = new URI(httpRequest.getRequestLine().getUri()).getHost();
      } catch (Exception e) {
      }
    }
    if(requestHost == null) requestHost = host;
    httpRequest.addHeader(HttpClientFactory.HOST_NAME, requestHost);
    if(userAgent != null) {
      httpRequest.addHeader(USER_AGENT_NAME, userAgent);
    } else {
      httpRequest.addHeader(USER_AGENT_NAME, USER_AGENT_VALUE);
    }
    httpRequest.addHeader(CONNECTION_NAME, CONNECTION_VALUE);
    httpRequest.addHeader(ACCEPT_ENCODING_NAME, ACCEPT_ENCODING_VALUE);
    httpRequest.addHeader(ACCEPT_CHARSET_NAME, ACCEPT_CHARSET_VALUE);
    httpRequest.addHeader(KEEP_ALIVE_NAME, KEEP_ALIVE_VALUE);
    httpRequest.addHeader(ACCEPT_NAME, ACCEPT_VALUE);
    httpRequest.addHeader(ACCEPT_LANGUAGE_NAME, ACCEPT_LANGUAGE_VALUE);
    httpRequest.addHeader(CACHE_CONTROL_NAME, CACHE_CONTROL_VALUE);
    if(referer != null) httpRequest.addHeader(REFERER_NAME, referer);
  }

  public boolean isLog() { return log; }

  public void setLog(boolean log) { this.log = log; }

  public RedirectStrategy getRedirectHandler() { return redirectHandler; }

  public synchronized void increaseBadRequest() { badRequest++; }
  public boolean isBadClient() {
    //    System.out.println(" bad proxy "+ hashCode()+ " / "+ badRequest + "/5");
    if(badRequest < 5) return false;
    addBadProxy();
    return true;
  }

  public void resetBadRequestCounter() {  badRequest = 0; }
  
  public void addBadProxy() {
    Proxies proxies = ProxiesMonitor.getInstance().getProxies(host);
    if(proxies == null) return;
    HttpHost httpProxy = (HttpHost) httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY);
    if(httpProxy == null) return;
    proxies.addBadProxy(httpProxy.getHostName() + ":" +String.valueOf(httpProxy.getPort()));
  }

  public Proxies getProxies() {
    return ProxiesMonitor.getInstance().getProxies(host);
  }

}
