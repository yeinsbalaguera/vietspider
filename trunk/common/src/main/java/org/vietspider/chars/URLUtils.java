/***************************************************************************
 * Copyright 2004-2006 The VietSpider All rights reserved.  *
 **************************************************************************/
package org.vietspider.chars;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.SWProtocol;

public class URLUtils {

  public final static char [] URICS  = {';', '?', ':', '@', '&', '=', '+', '$', ',', '/'};

  static {
    Arrays.sort(URICS);
  }

  protected URLEncoder encoder; 

  public URLUtils() {
    encoder = new URLEncoder(); 
  }

  public synchronized String createURL(URL url, String link) {
    if(url == null) return link;
    link = createURL(url.getFile(), link);
    return createURL(url.getHost(), url.getPort(), url.getProtocol(), link);
  }

  public synchronized String createURL(String host, int port, String protocol, String link) {
    if(SWProtocol.isHttp(link))  return link;

    String url =protocol+"://"+host;
    if( port >= 0) url += ":"+String.valueOf(port);
    url += link;
    return url;
  }

  @SuppressWarnings("unused")
  public synchronized String createURL(String address, String link)  {
    link = encoder.encode(link);
    address = encoder.encode(address);

    if(SWProtocol.isHttp(link) || link.startsWith("/"))  return link;

    StringBuilder builder = new StringBuilder();
    try {
      build(builder, address, link);
    } catch (Throwable e) {
      LogService.getInstance().setMessage(null, address);
      LogService.getInstance().setMessage(null, link);
      LogService.getInstance().setThrowable(e);
    }

    if(builder.length() > 0 && 
        Arrays.binarySearch(URLUtils.URICS, builder.charAt(0)) < 0) {
      builder.insert(0, '/');
    }

    int idx = -1;
    while((idx = builder.indexOf("/..")) == 0) {
      builder = builder.delete(0, 3);
    }

    //  System.out.println(" ra "+ builder);
    return builder.toString().trim();
  }

  void build(StringBuilder builder, String address, String link) throws Throwable  {
    address = address.trim();
    link = link.trim();
    
    String [] base_components = parseBaseURI(address); 
    String base_path = base_components[0];
    String base_query = base_components[1];
    
//    System.out.println(base_path);
//    System.out.println(base_query);
    if(link.length() < 1) {
      builder.append(base_path);
      builder.append(base_query == null ? "" : base_query);
      return;
    } else if(link.length() == 1 && link.charAt(0) == '.') {
      builder.append(base_path.subSequence(0, base_path.lastIndexOf('/')+1));
      return;
    } else if(link.charAt(0) == '#') {
      builder.append(base_path);
      builder.append(base_query == null ? "" : base_query).append(link);
      return;
    } else if(link.length() >= 2 
        && link.charAt(0) == '.' && link.charAt(1) == '/') {
      link = link.substring(2);
    }

    int q_index = link.indexOf('?');
    if(q_index < 0) {
//      System.out.println(link);
//      System.out.println(base_path);
//      System.out.println(base_query);
      int andIndex = link.indexOf('&');
      int slashIndex = link.indexOf('/');
      if(base_query == null || (slashIndex > -1 
          && (andIndex < 0 || slashIndex < andIndex)) ) {
        builder.append(base_path.subSequence(0, base_path.lastIndexOf('/')+1)).append(link);
      } else {
        char last_base_query = base_query.charAt(base_query.length() - 1);
        if(link.indexOf('=') > -1) {
          builder.append(base_path).append(base_query);
          if(last_base_query != '?' 
            && link.charAt(0) != '&') builder.append('&');
          builder.append(link);
        } else if(last_base_query == '=') {
          builder.append(base_path).append(base_query).append(link);
        } else {
          builder.append(base_path.subSequence(0, base_path.lastIndexOf('/')+1)).append(link);
        }
        
      }
    } else if(q_index == 0) {
      builder.append(base_path).append(link);
    } else if(q_index > 0) {
      builder.append(base_path.subSequence(0, base_path.lastIndexOf('/')+1)).append(link);
    }
  }


  String [] parseBaseURI(String address) {
    String base_path = null;
    String base_query = null;

    if(SWProtocol.isHttp(address)) {  // site is : http://abc.com/x/y?z=0
      int p_index = address.indexOf('/', 7);
      int q_index = address.indexOf('?', 7);
      if(p_index > -1 && q_index > -1) {
        base_path = address.substring(p_index, q_index);
        base_query = address.substring(q_index, address.length());
      } else if(p_index > -1 && q_index < 0) {
        base_path = address.substring(p_index, address.length());
      } else if(p_index  < 0 && q_index > -1) {
        base_path = "";
        base_query = address.substring(q_index, address.length());
      } else {
        base_path = "";
        base_query = null;
      }
    } else {
      int p_index = address.indexOf('/');
      int dot_index = address.indexOf('.');

      if(dot_index > -1 && dot_index < p_index)  { // site is : abc.com/x/y?z=0
        p_index = address.indexOf('/', dot_index);
        int q_index = address.indexOf('?', dot_index);
        if(p_index > -1 && q_index > -1) {
          base_path = address.substring(p_index, q_index);
          base_query = address.substring(q_index, address.length());
        } else if(p_index > -1 && q_index < 0) {
          base_path = address.substring(p_index, address.length());
          base_query = null;
        } else if(p_index  < 0 && q_index > -1) {
          base_path = "";
          base_query = address.substring(q_index, address.length());
        } else {
          base_path = "";
          base_query = null;
        }
      } else {
        int q_index = address.indexOf('?', dot_index);
        if(q_index > -1) {
          base_path = address.substring(0, q_index);
          base_query = address.substring(q_index, address.length());
        } else {
          base_path = address;
          base_query = null;
        }
      }
    }

    return new String[]{base_path, base_query};
  }

  public static Map<String, String> getParams(String s) throws Exception {
    if(s == null) return null;
    Map<String, String> ps = new HashMap<String, String>();
    String[] paramStrs = s.split("\\&");
    for(String psStr: paramStrs){
      psStr= URLDecoder.decode(psStr, Application.CHARSET);
      int index = psStr.indexOf('='); 
      if(index < 0) continue;
      String key = psStr.substring(0, index);
      String value = psStr.substring(index+1);
      //    System.out.print("Key=" +key);
      //    System.out.println(" || Value=" +value);
      //    System.out.println("-------------------------------");
      ps.put(key, value);
    }
    return ps;
  }
  
  public String getCanonical(String address) {
    return getCanonical(address, true);
  }

  public String getCanonical(String address, boolean encode) {
    if (address == null) return null;
    int index = SWProtocol.lastIndexOf(address);
    if(index > 0) return normalize(address, encode);

    index = address.indexOf('/');
    if(index < 0) return address;
    int dotIndex = address.indexOf('.');
    if(dotIndex > 0 &&  dotIndex < index) {
      return normalize("http://"+address, encode);
    }

    try {
      URI uri = new URI(address);
      return uri.normalize().toString();
    } catch (Exception e) {
      return address;
    }

  }

  private String normalize(String address, boolean encode)  {
    try {
      URL url  = new URL(address);
      String path = url.getPath();
      URI uri = null;
      if(encode) {
        uri = new URI(encoder.encode(normalizePath(path)));
      } else {
        try {
          uri = new URI(normalizePath(path));
        } catch (Exception e) {
          uri = new URI(encoder.encode(normalizePath(path)));
        }
      }
      String newPath = uri.normalize().toString();
      int index = address.indexOf(path);
      return address.substring(0, index) + newPath + address.substring(index+path.length());
      //      return address.replaceAll(path, newPath);
    } catch (MalformedURLException e) {
      LogService.getInstance().setMessage("APPLICATION", e, address);
      return address;
    } catch (Exception e) {
      LogService.getInstance().setThrowable("APPLICATION", e, address);
      return address;
    }
  }

  private String normalizePath(String path) {
    int index = 0;
    StringBuilder builder = new StringBuilder();
    while(index < path.length()) {
      char c = path.charAt(index);
      if(c == '/') {
        if(index > 0){
          if(path.charAt(index-1) != '/') builder.append(c);
        } else {
          builder.append(c);
        }
      } else {
        builder.append(c);
      }
      index++;
    }
    return builder.toString();
  }

  public URLEncoder getEncoder() { return encoder; }

  public static void main(String[] args) {
    URLUtils utils = new URLUtils();
    
//    String address = "http://hiephoioto.com/sales/detail/d58f0f/../***-BAN-TRA-GOP-XE-HYUNDAI-25C---XE-KHACH-45C---49C-$$$/";
//    System.out.println(address);
//    System.out.println(utils.getCanonical(address));
    
    String address = "http://rongbay.com/ajax_request.html?search_type=sphinx&request_type=request_search&searchword=mua&catid_search=83&nc=0&tt=0&total=2524&page=3";
    String link  = "raovat-5801556/Ve-may-bay-gia-re-trong-nuoc-va-Quoc-T.html&searchword=mua";
    System.out.println(utils.createURL(address, link));
    
  }
}

