/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 28, 2011  
 */
public class HttpUtils {

  private static final String ACCEPT_ENCODING = "Accept-Encoding";
  private static final String GZIP_CODEC = "gzip";
  final static  String [] MOBILE_USER_AGENTS = {
    "android",
    "mobile safari",
    "apple-iphone",
    "iphone",
    "opera mobi",
    "opera mini",
    "blackberry",
    "tablet",
    "windows phone",
    "kindle",
    "fennec",
    "midp",
    
    "danger hiptop", "avantgo",
    "docomo",
    "up.browser",
    "vodafone", "semc-browser",
    "j-phone",
    "ddipocket",
    "pdxgw",
    "astel",
    "palmos", "eudoraweb",
    "windows ce",
    "minimo",
    "plucker",
    "netfront",
    "wm5 pie",
    "xiino",
    
    "htc_hd2",
    "htc_touch_pro",
    
    "teleca",
    "lge",
    "portalmmm",
    "nintendo wii",
    "smartphone",
    "symbian os",
    "symbianos",
    "symbos",
    "nokia",
    "maemo browser",
    "palm",
    "cricket",
    
    "vodafone",
    "o3",
    "bell mobility canada",
    "rogers",
    "verizon us",
    "sprint",
    "cingular us",
    "t-mobile",
    "ppc"
    
  };

  public static boolean isZipResponse(HttpRequest request) {
    Header encHeader = request.getFirstHeader(ACCEPT_ENCODING);
    if (encHeader == null) return false;
    HeaderElement[] codecs = encHeader.getElements();

    for (int i = 0; i < codecs.length; i++) {
      if(codecs[i].getName().equalsIgnoreCase(GZIP_CODEC)) return true;
    }
    return false;
  }

  public static byte [] gzipCompress(byte [] bytes) {
    ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
    try {
      GZIPOutputStream gzip = new GZIPOutputStream(byteArrayStream);
      gzip.write(bytes, 0, bytes.length);
      gzip.close();
    } catch (Exception e) {
      LogService.getInstance().setThrowable("SERVER", e);
    }
    return byteArrayStream.toByteArray();
  }
  
  public static String[] getCookie(HttpRequest request) {
    Header header = request.getFirstHeader("Cookie");
//    System.out.println(" trong khi get first cookie "+ header);
    if(header == null) return null;
    String value  = header.getValue();
//    System.out.println("trong cms hanler "+ value);
    if(value == null || (value = value.trim()).isEmpty()) return null;
    return value.split("#");
  }
  
  public static String getUserAgent(HttpRequest request) {
    Header header = request.getFirstHeader("User-Agent");
//    System.out.println(" trong khi get first cookie "+ header);
    if(header == null) return "No User-Agent";
    String value  = header.getValue();
//    System.out.println("trong cms hanler "+ value);
    if(value == null || (value = value.trim()).isEmpty()) return "No User-Agent";
    return value;
  }
  
  public static HttpRequestData createHRD(HttpRequest request, String type)  {
    HttpRequestData hrd = new HttpRequestData(HttpUtils.getUserAgent(request));
    hrd.setUri(request.getRequestLine().getUri());
    hrd.setCookies(HttpUtils.getCookie(request));
    hrd.setZipResponse(isZipResponse(request));
    
    String uriFolder = "/" + type;
    Header header = request.getFirstHeader("embeded.path");
//    System.out.println("= ====  > embeded path "+ header);
    if(header != null && header.getValue() != null) {
//      System.out.println("========== > "+ header.getValue());
      uriFolder = header.getValue() + "/" + type; 
    }
    hrd.setUriFolder(uriFolder);
    
    header = request.getFirstHeader("Referer");
    hrd.setReferer(header == null ? null : header.getValue());
    
    return hrd;
  }
  
//  public static HttpEntity createEntity(String text, String contentType) throws Exception {
//    byte [] bytes  = text.getBytes(Application.CHARSET);
//    ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
//    byteArrayEntity.setContentType(contentType+"; charset="+Application.CHARSET);
//    return byteArrayEntity;
//  }
  
  public static boolean isDateFormat(String value) {
    if(value.length() != 10) return false;
    for(int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if(Character.isDigit(c) || c == '.') continue;
      return false;
    }
    return true;
  }
  
}
