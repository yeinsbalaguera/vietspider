/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import java.io.File;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.browser.FastWebClient;
import org.vietspider.chars.URLEncoder;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.util.HTMLAnchorUtil;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.html.util.URLCodeGenerator;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.offices.DocumentConverter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 23, 2007  
 */
public class FastWebClient2 extends FastWebClient {
  
  public byte[] loadContent(String referer, String address) throws Exception {
    return loadContent(referer, address, null);
  }
  
  public byte[] loadContent(String referer, String address, String charset) throws Exception {
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
      HttpResponse httpResponse = httpMethod.execute(address, referer);
      currentGets.remove(address);
      
//      StatusLine statusLine = httpResponse.getStatusLine();
//      int statusCode = statusLine.getStatusCode();
//      System.out.println(" status code la "+ statusCode);
      
//      Header[] headers = httpResponse.getAllHeaders();
//      for(Header header : headers) {
//        System.out.println(header.getName() + " : "+ header.getValue());
//      }
      
      String contentType = "text/html";

      Header header = httpResponse.getFirstHeader("Content-Type");
      if(header != null) contentType = header.getValue();
      if(contentType == null 
          || contentType.trim().isEmpty()) contentType = "text/html";

      String extension = null;
      
//      System.out.println(" =============== > "+ contentType);

      if(contentType.indexOf("html") < 0
          && contentType.indexOf("text") < 0 
          && contentType.indexOf("xml") < 0 ) {
        if(DocumentConverter.getInstance() == null) {
          return httpMethod.readBody();
        }
        
        extension = DocumentConverter.getInstance().getExtension(address, contentType);
//        System.out.println(" thay co "+ extension);
        if(extension == null) {
//          if(source.isDebug()) {
//            LogSource.getInstance().setMessage(source, null, link.getAddress()+": {invalid.extension}");
//          }
          return httpMethod.readBody();
        }
        
        byte [] bytes = httpMethod.readBody();
        bytes = DocumentConverter.getInstance().convert(bytes, extension, charset);
        return bytes;
      }

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
      byte[] obj = loadContent(refer, address, detector.getCharset());   
//      System.out.println(" ===  >"+ obj.le);
      if( obj == null || obj.length < 1) return null;
      document = detector.createDocument(obj);
      chars = document.getTextValue().toCharArray();
      if(cache) cacheResponse(address, chars);
    }
    
    return document == null || ref == null ? 
        document : new HTMLAnchorUtil().searchDocument(document, ref);
  }

}
