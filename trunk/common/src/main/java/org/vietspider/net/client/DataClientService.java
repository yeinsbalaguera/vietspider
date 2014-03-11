/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.vietspider.common.Application;
import org.vietspider.common.io.GZipIO;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 25, 2009  
 */
public class DataClientService {

  protected WebClient webClient;
  protected String url;

  protected HttpResponseReader reader;
  protected String localAddress = "127.0.0.1";

  private ConcurrentHashMap<HttpRequest, Long> httpRequests = new ConcurrentHashMap<HttpRequest, Long>();

  protected DataClientService() {
    loadLocalAddress();
  }

  public DataClientService(String url_) {
    this.url = url_;
    if(!SWProtocol.isHttp(url)) url = "http://" + url;
    webClient = new WebClient();
    try {
      webClient.setURL(null, new URL(url));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    loadLocalAddress();
  }

  public WebClient getWebClient() { return webClient; }

  private void loadLocalAddress() {
    try {
      localAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public String getUrl() { return url; }

  public final void abort() {
    Iterator<HttpRequest> iterator = httpRequests.keySet().iterator();
    while(iterator.hasNext()) {
      HttpRequest request = iterator.next();
      if(request instanceof HttpGet) {
        ((HttpGet)request).abort();
      } else if(request instanceof HttpPost) {
        ((HttpPost)request).abort();
      }
    }
  }

  //  private HttpResponse loadPostResponse(String path, byte[] bytes, Header...headers) throws Exception {
  ////  System.out.println("   remoteURL+path=" + remoteURL+path);
  //    
  //  }

  public void postResponse(String path, byte[] bytes, Header...headers) throws Exception {
    //  System.out.println("   remoteURL+path=" + remoteURL+path);
    HttpPost httpPost = RequestManager.getInstance().createPost(webClient, null, url+path);
    manage(httpPost);
    boolean hasUserAgent = false;
    for(Header header : headers) {
      if(header.getName().equals(HttpClientFactory.USER_AGENT_NAME)) {
        hasUserAgent = true;
      }
      httpPost.addHeader(header);
    }
    if(!hasUserAgent) {
      httpPost.addHeader(new BasicHeader(
          HttpClientFactory.USER_AGENT_NAME, HttpClientFactory.USER_AGENT_VALUE));
    }
    httpPost.addHeader(new BasicHeader("Content-Type", "text/xml; charset=utf-8"));
    //    httpPost.addHeader(new BasicHeader("local-address", localAddress));

    int length = bytes.length;
    bytes = new GZipIO().zip(bytes);
    if(length > bytes.length) httpPost.addHeader(new BasicHeader("zip", "true"));
    if(bytes != null) httpPost.setEntity(new ByteArrayEntity(bytes));
    //  httpPost.setHeader("Content-Length", String.valueOf(bytes.length));

    HttpHost httpHost = webClient.createHttpHost(url);
    try {
      HttpResponse httpResponse = webClient.execute(httpHost, httpPost);
      release(httpPost, httpResponse);
    } catch (Exception e) {
      httpPost.abort();
      throw e;
    }
  }

  /*@SuppressWarnings("unchecked")
  public <T> T readAsObject(String path, byte [] bytes, Header...headers) throws Exception {
    byte [] _bytes = post(path, bytes, headers);
//    bytes = new GZipIO().unzip(bytes);
    if(bytes == null) return null;
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
  }*/
  
  public <T> T readFromXML(Class<T> clazz, String path, byte [] bytes, Header...headers) throws Exception {
    byte [] _bytes = post(path, bytes, headers);
    if(_bytes == null || _bytes.length < 1) return null;
    return XML2Object.getInstance().toObject(clazz, _bytes);
  }
  
  public byte[] postAsXML(String path, Object value, Header...headers) throws Exception {
    XMLDocument doc = Object2XML.getInstance().toXMLDocument(value);
    byte [] bytes = doc.getTextValue().getBytes(Application.CHARSET);
    return post(path, bytes, headers);
  }

  public byte[] postAsObject(String path, Object value, Header...headers) throws Exception {
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

    return post(path, bytesObject.toByteArray(), headers);
  }

  public byte[] post(String path, byte [] bytes, Header...headers) throws Exception {
    HttpPost httpPost = RequestManager.getInstance().createPost(webClient, null, url+path);
    manage(httpPost);
    boolean hasUserAgent = false;
    for(Header header : headers) {
      if(header.getName().equals(HttpClientFactory.USER_AGENT_NAME)) {
        hasUserAgent = true;
      }
      httpPost.addHeader(header);
    }
    if(!hasUserAgent) {
      httpPost.addHeader(new BasicHeader(
          HttpClientFactory.USER_AGENT_NAME, HttpClientFactory.USER_AGENT_VALUE));
    }
    httpPost.addHeader(new BasicHeader("Content-Type", "text/xml; charset=utf-8"));
    //    httpPost.addHeader(new BasicHeader("local-address", localAddress));

    int length = bytes.length;
    bytes = new GZipIO().zip(bytes);
    if(length > bytes.length) httpPost.addHeader(new BasicHeader("zip", "true"));
    if(bytes != null) httpPost.setEntity(new ByteArrayEntity(bytes));

    //  httpPost.setHeader("Content-Length", String.valueOf(bytes.length));

    HttpHost httpHost = webClient.createHttpHost(url);
    HttpResponse httpResponse = null;
    try {
      httpResponse = webClient.execute(httpHost, httpPost);
      if(httpResponse == null) return new byte[0];
      if(reader != null) reader.abort();
      reader = new HttpResponseReader();
      //    HttpResponse httpResponse = loadPostResponse(path, bytes, headers);
      return reader.readBody(httpResponse);
    } finally {
      release(httpPost, httpResponse);
    }
  }

  public void release(HttpPost httpPost, HttpResponse httpResponse)  {
    RequestManager.getInstance().close(httpPost);

    if(httpPost != null) httpRequests.remove(httpPost);
    if(httpResponse == null) {
      if(httpPost != null) httpPost.abort();
      return;
    }

    HttpEntity httpEntity = httpResponse.getEntity();
    if(httpEntity == null) {
      if(httpPost != null)  httpPost.abort();
      return;
    }

    if(httpEntity instanceof BasicManagedEntity) {
      BasicManagedEntity entity = (BasicManagedEntity) httpEntity;
      try {
        entity.releaseConnection();
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
    } else {
      try {
        if(httpEntity.getContent() != null) httpEntity.getContent().close();
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
    }

  }

  public void release(HttpGet httpGet, HttpResponse httpResponse) {
    RequestManager.getInstance().close(httpGet);

    if(httpGet != null) httpRequests.remove(httpGet);
    if(httpResponse == null) {
      if(httpGet != null) httpGet.abort();
      return;
    }

    HttpEntity httpEntity = httpResponse.getEntity();
    if(httpEntity == null) {
      if(httpGet != null) httpGet.abort();
      return;
    }

    if(httpEntity instanceof BasicManagedEntity) {
      BasicManagedEntity entity = (BasicManagedEntity) httpEntity;
      try {
        entity.releaseConnection();
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
    } else {
      try {
        if(httpEntity.getContent() != null) httpEntity.getContent().close();
      } catch (Exception e) {
        LogService.getInstance().setMessage(e, e.toString());
      }
    }
  }

  public void manage(HttpRequest httpRequest) {
    RequestManager.getInstance().closeExpires(httpRequests, 3*60*1000);
    httpRequests.put(httpRequest, System.currentTimeMillis());
  }

  public void closeTimeout() {
    RequestManager.getInstance().closeExpires(httpRequests, 3*60*1000);
  }


}
