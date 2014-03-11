/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

import java.io.File;
import java.net.URL;
import java.util.Hashtable;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.vietspider.chars.URLEncoder;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.util.HTMLAnchorUtil;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.html.util.URLCodeGenerator;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.RequestManager;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 23, 2007  
 */
public class FastWebClient extends WebClient {
  
  protected final static long serialVersionUID = -2l;
  
  protected Hashtable<String, HttpRequestBase> currentGets = new Hashtable<String, HttpRequestBase>();
  protected RefererFormHandler refererFormHandler;
  
  public FastWebClient() {
    refererFormHandler = new RefererFormHandler(this);
  }
  
  public HttpResponse setURL(String referer, final URL url, String blind) throws Exception {
    HttpResponse httpResponse = super.setURL(referer, url, blind);
    refererFormHandler.execute(referer, httpResponse);
    return httpResponse;
  }

  public HttpResponse setURL(String referer, URL url) throws Exception {
    HttpResponse httpResponse = super.setURL(referer, url);
    refererFormHandler.execute(referer, httpResponse);
    return httpResponse;
  }
  
  public void abort(String address) {
    if(address == null || address.trim().length() == 0) return;
    if(!currentGets.containsKey(address)) return;
    HttpRequestBase httpGet = currentGets.remove(address);
    if(httpGet != null) httpGet.abort();
  }

  public byte[] loadContent(String referer, String address) throws Exception {
    URLEncoder urlEncoder = new URLEncoder();
    
    if(host == null) {
//      setURL(referer, new URL(address));
      setURL(referer, new URL(address));
    } else {
      URL url = new URL(address);
      URLCodeGenerator urlCodeUtil = new URLCodeGenerator(); 
      if(!urlCodeUtil.compareHost(url.getHost(), getHost())) {
//        HttpResponse refererResponse = setURL(referer, new URL(address));
        setURL(referer, new URL(address));
      }
    }
    
    //load by post
    /*if(address.indexOf('?') > -1) {
      int idx = address.indexOf('?');
      String query = address.substring(idx+1);
      String url = address.substring(0, idx);
      HttpPost httpPost = RequestManager.getInstance().createPost(this, referer, url);
      StringEntity  entity = new StringEntity(query);
      httpPost.setEntity(entity);
      
      currentGets.put(address, httpPost);
      
      HttpMethodHandler httpMethod = new HttpMethodHandler(this);
      HttpResponse httpResponse = httpMethod.execute(address, referer);
      currentGets.remove(address);
      
      return httpMethod.readBody();
    }*/

    HttpGet httpGet = null;
    try {
      address = urlEncoder.encode(address);
      httpGet = createGetMethod(address, referer);      
      currentGets.put(address, httpGet);

      if(httpGet == null) return null;
      HttpMethodHandler httpMethod = new HttpMethodHandler(this);
      /*HttpResponse httpResponse = */httpMethod.execute(address, referer);
      currentGets.remove(address);
      
//      StatusLine statusLine = httpResponse.getStatusLine();
//      int statusCode = statusLine.getStatusCode();
//      System.out.println(" status code la "+ statusCode);
      
//      Header[] headers = httpResponse.getAllHeaders();
//      for(Header header : headers) {
//        System.out.println(header.getName() + " : "+ header.getValue());
//      }

      return httpMethod.readBody();
    } catch(Exception exp) {
      throw exp;
    }
  }

  public HTMLDocument createDocument(String refer, 
      String address, boolean cache, HTMLParserDetector detector) throws Exception {  
    if( address == null || address.trim().length() < 1) return null; 
    char [] chars = getCacheData().getCachedObject(address);
    if(chars != null) return detector.createDocument(chars);

    File file = new File(address); 
    if(file.exists()) return detector.loadDocument(file);
    
    URL url = new URL(address);
    String ref = url.getRef();
    if(ref != null && (ref = ref.trim()).isEmpty())  ref = null;
    if(ref != null) address = address.substring(0, address.indexOf('#'));
    
    HTMLDocument document = null;
    if(address.startsWith("file")){
      file = new File(url.toURI());
      document = detector.loadDocument(file);
    } else {
      byte[] obj = loadContent(refer, address);   
//      System.out.println(" ===  >"+ obj.le);
      if( obj == null || obj.length < 1) return null;
      document = detector.createDocument(obj);
      chars = document.getTextValue().toCharArray();
      if(cache) cacheResponse(address, chars);
    }
    
    return document == null || ref == null ? 
        document : new HTMLAnchorUtil().searchDocument(document, ref);
  }
  
  public NodePath findNodeByText(HTMLNode node, String start, String end) throws Exception {
    RefsDecoder decoder = new RefsDecoder(); 
    NodePathParser pathParser = new NodePathParser();
    TextHandler textHandler = new TextHandler();
    if(start == null || start.trim().length() == 0) return pathParser.toPath(node);
    start = textHandler.trim(start);
    HTMLNode startNode = textHandler.findByText(node, start, decoder);
    if(end == null || end.trim().length() == 0) {
      return startNode != null ?  pathParser.toPath(startNode) : pathParser.toPath(node);
    }
    end = textHandler.trim(end);
    
    HTMLNode endNode = textHandler.findByText(node, end, decoder);    
    if(endNode == null) { 
      return startNode != null ? pathParser.toPath(startNode) : pathParser.toPath(node);
    }
    if(startNode  == null)  {
      return endNode != null  ?  pathParser.toPath(endNode) : pathParser.toPath(node);
    }
      
    String path1 = pathParser.toPath(startNode).toString();
    String path2 = pathParser.toPath(endNode).toString();
    String path = commonPath(path1, path2);
    return pathParser.toPath(path);
//    HTMLNodeUtil nodeUtil = new HTMLNodeUtil();  
//    String indexPath = nodeUtil.getCommonIndexPath(startNode, endNode);
//    System.out.println(" start node "+ pathParser.toPath(startNode));
//    System.out.println(" end node "+ pathParser.toPath(endNode));
//    System.out.println(" end node "+ pathParser.toPath(node));
//    return pathParser.toPath(nodeUtil.getNodeByIndex(node, indexPath));
  }
  
  private String commonPath(String path1, String path2)  {
    int index = 0;
    int size = Math.min(path1.length(), path2.length());
    while(index < size) {
      char c1 = path1.charAt(index);
      char c2 = path2.charAt(index);
      if(c1 != c2) break;
      index++;
    }
    
    String path = path1.substring(0, index);
    index = path.lastIndexOf('.');
    path = path.substring(0, index);
    return path;
  }
  

}
