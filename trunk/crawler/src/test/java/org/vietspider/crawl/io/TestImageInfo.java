/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.chars.URLEncoder;
import org.vietspider.io.ImageInfo;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 16, 2008  
 */
public class TestImageInfo {

  private static WebClient webClient = new WebClient();

  public static byte[] loadBytes(String address) throws Exception {
//  URL url = new URL(address);


    HttpGet httpGet = null;
    try{
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      httpGet = webClient.createGetMethod(address, "");

      if(httpGet == null) return null;
//    System.out.println(" an cut ");
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);

      int statusCode = httpResponse.getStatusLine().getStatusCode();
      System.out.println(" status code is "+ statusCode);

      Header [] headers = httpResponse.getAllHeaders();
      for(Header header : headers) {
        System.out.println(header.getName() + " : " + header.getValue());
      }

      System.out.println(" \n\n chuan bi read "+ address);

      byte [] bytes = null; 

      long start = System.currentTimeMillis();

//    HttpEntity httpEntity = httpResponse.getEntity();
//    bytes = webClient.decodeResponse(webClient.readData(httpEntity.getContent()), httpEntity.getContentEncoding());

      HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);
      bytes = methodHandler.readBody(httpResponse);

      long end = System.currentTimeMillis();
      System.out.println(" doc het "+ (end - start)+ " s");

      System.out.println(" bytes size "+ bytes.length);

      return bytes;

    } catch(Exception exp){
      exp.printStackTrace();
//    LogService.getInstance().setThrowable(e);
      return null;
    }
  }

  public static InputStream loadContent(String address) throws Exception {
//  URL url = new URL(address);

    HttpGet httpGet = null;
    try{
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      httpGet = webClient.createGetMethod(address, "");

      if(httpGet == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);

      Header [] headers = httpResponse.getAllHeaders();
      for(Header header : headers) {
        System.out.println(header.getName() + " : " + header.getValue());
      }
      
      HttpEntity entity = httpResponse.getEntity(); 
      int encoding = getContentEncoding(entity.getContentEncoding());
      
      InputStream inputStream = entity.getContent();
      if(encoding < 0) return inputStream;
      if(encoding == 0) new InflaterInputStream(inputStream, new Inflater(true));
      return new GZIPInputStream(inputStream);
    } catch(Exception exp){
      exp.printStackTrace();
//    LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  
  private static int getContentEncoding(Header header) {
    if(header == null) return -1;
    String encoding = header.getValue();
    if(encoding == null || (encoding = encoding.trim()).isEmpty()) return -1;

    if (encoding.equals ("gzip")) return 1;
    // DEFLATE
    if (encoding.equals ("deflate")) return 0;
    return -1;
  }

  public static void main(String[] args) throws Exception {
    ImageInfo imageInfo2 = new ImageInfo();

    String home = "http://www.tinhoctaichinh.vn/";
    webClient.setURL(null, new URL(home));

    String address = "http://www.tinhoctaichinh.vn/avatar.aspx?ID=6913&at=0&ts=177&lm=633476783370900000";
    address = "http://opi.yahoo.com/online?u=longdn267&m=g&t=2";
    InputStream inputStream = loadContent(address);

    imageInfo2.setInput(inputStream);
    imageInfo2.check();

    System.out.println(imageInfo2.getWidth());
    System.out.println(imageInfo2.getHeight());

    inputStream.close();
    System.exit(0);
  }

}
