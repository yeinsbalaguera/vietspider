/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.vietspider.browser.VirtualFormPost.ParamValue;
import org.vietspider.chars.CharsDecoder;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 23, 2008  
 */
public class LoginWebsiteHandler {

  private final static String TYPE_ATTR = "type";
  private final static String PASSWORD = "password";
  
  private final static String ID_ATTR = "id";
  private final static String NAME_ATTR = "name";
  private final static String VALUE = "value";
  private final static String ACTION = "action";
  
  private HashMap<String, ParamValue> maps;
  private List<String> removeFields;
//  protected List<IgnoreNameValue> ignores;
  
  private boolean autoDetect = true;

  public LoginWebsiteHandler() {
    maps = new HashMap<String, ParamValue>();
    removeFields = new ArrayList<String>();
//    ignores = new ArrayList<IgnoreNameValue>();
  }

  public boolean login(HttpMethodHandler httpMethod, String referer, 
      String charset, URL loginUrl, String username, String password) throws Exception {
    HttpResponse response = httpMethod.execute(loginUrl.toString(), referer);
    if(response == null) return false;
    
    StatusLine statusLine = response.getStatusLine();
    int statusCode = statusLine.getStatusCode();
//    System.out.println(statusLine.getReasonPhrase());
    
    switch (statusCode) {
      case HttpStatus.SC_NOT_FOUND:
      case HttpStatus.SC_NO_CONTENT:
      case HttpStatus.SC_BAD_REQUEST:
      case HttpStatus.SC_REQUEST_TIMEOUT:
      case HttpStatus.SC_NOT_ACCEPTABLE:
      case HttpStatus.SC_SERVICE_UNAVAILABLE:
      case 999:
        throw new Exception(statusLine.getReasonPhrase());
      default:
        break;
    }
    
//    System.out.println("============== start reading data ===> " + statusLine.getStatusCode());
    
    byte [] data = httpMethod.readBody();
//    System.out.println("login form page test " + new String(data));
    
    HTMLParserDetector htmlParser2 = new HTMLParserDetector();
    if(data == null) return false;
    if(charset == null) {
      charset = htmlParser2.detectCharset(data);
    }
    
    char [] chars = CharsDecoder.decode(charset, data, 0, data.length);
    List<NodeImpl> tokens  = htmlParser2.createTokens(chars);
    if(tokens == null) return false;

    int start = searchPasswordField(tokens);
    LoginUtils loginUtil = new LoginUtils();
    
    if(start == -1) {
     for(int i = 0; i < tokens.size(); i++) {
       NodeImpl impl = tokens.get(i);
       if(!impl.isNode(Name.CONTENT)) continue;
       String text = impl.getTextValue().toLowerCase();
       if(text.indexOf("log out") > -1 || text.indexOf("logout") > -1) return true;
     }
      
      throw new UnknownHostException("Not found login form. Please check login address: "+loginUrl);
    }

    for(; start > -1; start--) {
      NodeImpl node = tokens.get(start);
      if(node.isNode(Name.FORM)) break;
    }

    HTMLNode form = null;
    boolean md5 = false;
    List<HTMLNode> inputs = new ArrayList<HTMLNode>();
    String formValue = null;

    for(int i = start; i < tokens.size(); i++) {
      NodeImpl node = tokens.get(i);
      if(node.isNode(Name.FORM)) {
        if(node.isOpen()) {
          if(!md5) {
            String value = new String(node.getValue());
            md5 = value.toLowerCase().indexOf("md5") > -1;
          }
          form = node;
          formValue = new String(form.getValue()).toLowerCase();
        } else {
          break;
        }
      } else if(node.isNode(Name.INPUT)) {
//        System.out.println("thay co cai ni "+ new String(node.getValue()));
        if(!md5 && formValue != null) {
          md5 = formValue.indexOf("md5") > -1;
        }
        inputs.add(node);
      } else if(node.isNode(Name.SELECT)) {
        i++;
        Attributes attrs = node.getAttributes();
        String name = getAttribute(attrs, NAME_ATTR);
        if(name == null) name = getAttribute(attrs, ID_ATTR);
        if(name == null) continue;
        for(; i < tokens.size(); i++) {
          node = tokens.get(i);
//          System.out.println(new String(node.getValue()));
          if(node.isNode(Name.OPTION)) {
            String nodeValue = new String(node.getValue());
            if(nodeValue.indexOf("selected") < 0) continue;
            attrs = node.getAttributes();
            String value = getAttribute(attrs, VALUE);
            if(value == null) continue;
            StringBuilder builder = new StringBuilder("input ");
            builder.append("name=\"").append(name).append("\" ");
            builder.append("value=\"").append(value).append("\" ");
            builder.append("type=\"option\" ");
            char [] _optionValue = builder.toString().toCharArray();
            inputs.add(new NodeImpl(_optionValue, Name.INPUT, TypeToken.SINGLE));
          } else if(node.isNode(Name.SELECT) && node.getType() == TypeToken.CLOSE) {
            break;
          }
        }
      }
    }
//    System.out.println("\n\n\n\n");
//    System.out.println("thay co cai ni "+ new String(form.getValue()));
    
    if(form == null || inputs.size() < 1) return false;
    String address = getAttribute(form, ACTION);
    if(address ==  null) address = loginUrl.toString();
//    System.out.println(" =====   ? "+ address);
//    if(address == null) return false;
    
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    for(int i = 0 ; i < inputs.size(); i++) {
//      System.out.println(new String(inputs.get(i).getValue()));
      Attributes attrs = inputs.get(i).getAttributes();
      String name = getAttribute(attrs, NAME_ATTR);
      if(name == null) name = getAttribute(attrs, ID_ATTR);
//      System.out.println(" ===========  >"+ name);
      if(name == null) continue;
      String type = getAttribute(attrs, TYPE_ATTR);
      if(type == null) type = "text";
      
      if(autoDetect && type.equalsIgnoreCase("text")) {
        addParam(params, name, username);
        continue;
      } 
      
      if(autoDetect && type.equalsIgnoreCase(PASSWORD)) {
        if(md5) password = hexMd5(password);
//        System.out.println(name + " : "+ password);
//        params.add(new BasicNameValuePair(name, password));
        //@TODO Hashcode
//        System.out.println(formValue+ " : " + formValue.indexOf("vb_login_md5password"));
        if(formValue.indexOf("vb_login_md5password") > -1) {
          addParam(params, name, "");
          addParam(params, "vb_login_md5password", password);
          addParam(params, "vb_login_md5password_utf", password);
//          params.add(new BasicNameValuePair(name, ""));
//          params.add(new BasicNameValuePair("vb_login_md5password", password));
//          params.add(new BasicNameValuePair("vb_login_md5password_utf", password));
        } else {
          addParam(params, name, password);
//          params.add(new BasicNameValuePair(name, password));
        }
        continue;
      } 
      
      
      String value = "";
      ParamValue paramValues = maps.get(name);
      if(paramValues == null) {
        value = getAttribute(attrs, VALUE);
      } else {
        value = paramValues.getValues()[0];
      }
//      System.out.println(name + "  :::  "+ value);
      maps.remove(name);
//      System.out.println("tren form "+name + " : "+ (value == null ? "" : value.trim()));
      if("[disable]".equalsIgnoreCase(value)) continue;
      addParam(params, name, value);
//      params.add(new BasicNameValuePair(name, value == null ? "" : value.trim()));
    }
    
    Iterator<Map.Entry<String, ParamValue>> iterator = maps.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, ParamValue> entry = iterator.next();
//      System.out.println("dat them vao form "+ entry.getKey() + " : "+ entry.getValue());
      String name = entry.getKey();
      
      String value = "";
      ParamValue paramValues = maps.get(name);
      if(paramValues != null && paramValues.getValues().length > 0) {
        value = paramValues.getValues()[0];
      }
      addParam(params, name, value);
//      params.add(new BasicNameValuePair(name, value == null ? "" : value.trim()));
    }
    
//    Iterator<NameValuePair> iterator2 = params.iterator();
//    while (iterator2.hasNext()) {
//      NameValuePair pair = iterator2.next();
//      if(isIgnore(params, pair.getName(), pair.getValue())) iterator2.remove();
//    }
    
//    for(int i = 0; i < params.size(); i++) {
//      System.out.println(params.get(i).getName()+ " : "+ params.get(i).getValue());
//    }
    
    URLUtils urlUtils = new URLUtils();
    address = urlUtils.createURL(loginUrl, address).trim();
    address = urlUtils.getCanonical(address);
    
    referer = loginUrl.toString();
    WebClient webClient = httpMethod.getWebClient();
    HttpHost httpHost = webClient.createHttpHost(address);
    
//    webClient.setLog(true);
//    address = "/wordpress/wp-login.php";
//    referer = null;
    
//    System.out.println("post to " + address);
    
    HttpPost httpPost = webClient.createFormPostMethod(address, referer, params, charset);
//    System.out.println("pepe111 " + httpPost.getRequestLine().getUri());
//    DefaultHttpClient dclient = (DefaultHttpClient) webClient.getHttpClient();
//    dclient.getCookieStore().clear();
//    System.out.println(webClient.hashCode());
//    System.out.println("proxy " + dclient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY));
//    dclient.getCookieStore().addCookie(new BasicClientCookie(
//        "wordpress_logged_in_bbfa5b726c6b7a9cf3cda9370be3ee91",
//        "admin%7C1307034088%7C1442d9b76873b2bb23256d4752348457"));
    
    
//    webClient.registryProxy("127.0.0.1:8081");
    
//    List<Cookie> cookies = dclient.getCookieStore().getCookies();
//    for(int i = 0; i < cookies.size(); i++) {
//      System.out.println(cookies.get(i).getName() + " : "+ cookies.get(i).getValue());
//    }
    
//    httpPost.addHeader(new BasicHeader("Cookie", "wp-settings-time-1=1305999853; wp-settings-1=m4%3Do%26editor%3Dtinymce; wordpress_test_cookie=WP+Cookie+check; bblastvisit=1305910144; bblastactivity=0"));
//    webClient.setLog(false);
    
    logPost(webClient, httpPost, address, "login_post_request.txt");
    
//    System.out.println(" chay vao day " );
    
    HttpResponse httpResponse = httpMethod.execute(httpHost, httpPost);
//    System.out.println(" ============= > "+ httpResponse);
    if(httpResponse == null) return false;
    
//    webClient.setLog(true);
    
    statusLine = httpResponse.getStatusLine();
//    statusCode = statusLine.getStatusCode();
//    System.out.println("post response " + statusLine.getStatusCode());
//    if(statusCode == HttpStatus.SC_MOVED_PERMANENTLY 
//        || statusCode == HttpStatus.SC_SEE_OTHER 
//        || statusCode == HttpStatus.SC_MOVED_TEMPORARILY
//        || statusCode == HttpStatus.SC_TEMPORARY_REDIRECT) {
      Header header = httpResponse.getFirstHeader("Location");
//      System.out.println(" header ====  > "+ header.getValue());
      if(header != null 
          && header.getValue() != null 
          && !header.getValue().trim().isEmpty()) {
        
//        String location = header.getValue();
//        int idx = location.indexOf('/', 10);
//        if(idx > 0) location = location.substring(idx);
//        System.out.println(location + " : "+ address);
        httpMethod.abort();
//        EntityUtils.consume(response.getEntity());
//        System.out.println("bebe " + header.getValue() + " : "+ address);
        try {
//          DefaultHttpClient httpClient = (DefaultHttpClient)webClient.getHttpClient();
//          httpClient.clearRequestInterceptors();
//          httpClient.clearResponseInterceptors();
//          httpClient.getConnectionManager().closeExpiredConnections();
          httpResponse = httpMethod.execute(header.getValue(), null);
        } catch (Throwable e) {
          LogService.getInstance().setThrowable("WEB", e);
        }
        address = header.getValue();
//        System.out.println(" da xong roi ");
      }
    
//    webClient.setLog(false);
    
//    System.out.println("status code "+ statusCode);
//    Header [] headers = httpResponse.getAllHeaders();
//    for(Header header1 : headers) {
//      System.out.println(header1.getName() + " : " + header1.getValue());
//    }

    byte [] bytes = new byte[0];
//    org.vietspider.common.io.DataWriter writer = org.vietspider.common.io.RWData.getInstance();
//    java.io.File file = UtilFile.getFile("track/logs/", "login_" + Utils.toFileName(address));

    loginUtil.setPrevLogin(tokens);
    boolean error  = false;
//    System.out.println("======================================");
    try {
      bytes = httpMethod.readBody(httpResponse);
//      System.out.println(new String(bytes));
    } catch (SocketException e) {
      LogService.getInstance().setThrowable("WEB", e);
      bytes = e.toString().getBytes();
      error = true;
    } catch (Exception e) {
      LogService.getInstance().setThrowable("WEB", e);
      bytes = e.toString().getBytes();
      error = true;
    }
    
    if(webClient.isLog()) {
      java.io.File file = UtilFile.getFile("track/temp/", "login_" + Utils.toFileName(address));
      LogService.getInstance().setMessage("WEB", null, "Login to: " +address);
      RWData.getInstance().save(file, bytes);
    }
    
    if(bytes == null || bytes.length < 1) {
      error = true;
      throw new Exception("No response from server.");
    }
    
//    System.out.println(" thay co "+ new String(bytes));
    
    if(!error) {
      chars = CharsDecoder.decode(charset, bytes, 0, bytes.length);
      tokens  = htmlParser2.createTokens(chars);
      if(tokens == null) return false;
      loginUtil.setAfterLogin(tokens);
//      System.out.println(" vao day roi " + loginUtil.getError()); 
//      if(loginUtil.getError() != null && loginUtil.getError().length() < 1) return true;
      if(searchPasswordField(tokens) > 0) throw new Exception(loginUtil.getError()); 
    }

    return !error;
  }
  
  private void logPost(WebClient webClient, HttpPost httpPost, String address, String fileName) {
    java.io.File logFile = UtilFile.getFolder("track/temp/");
    if(logFile == null || !webClient.isLog()) return; 
    StringBuilder builder = new StringBuilder();
    builder.append("POST ").append(address);
    Header [] headers = httpPost.getAllHeaders();
    for(Header header : headers) {
      builder.append('\n');
      builder.append(header.getName()).append(": ").append(header.getValue());
    }

    try {
      ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
      httpPost.getEntity().writeTo(byteArray);
      builder.append('\n');
      builder.append(new String(byteArray.toByteArray(), Application.CHARSET));

      java.io.File file = new File(logFile, fileName);
//      System.out.println(" chuan bi save " + file.getAbsolutePath());
      RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  private String getAttribute(HTMLNode node, String name) {
    Attributes attrs = node.getAttributes(); 
    int idx = attrs.indexOf(name);
    if(idx < 0) return null; 
    return attrs.get(idx).getValue();
  }

  private String getAttribute(Attributes attrs, String name) {
    int idx = attrs.indexOf(name);
    if(idx < 0) return null; 
    return attrs.get(idx).getValue();
  }
  
  private String hexMd5(String password) throws Exception {
//  System.out.println("===== > "+ password);
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    md5.update(password.getBytes());
    BigInteger hash = new BigInteger(1, md5.digest());
    return pad(hash.toString(16), 32, '0');
  }
  
  private static String pad(String s, int length, char pad) {
    StringBuffer buffer = new StringBuffer(s);
    while (buffer.length() < length) {
      buffer.insert(0, pad);
    }
    return buffer.toString();
  }
  
  private int searchPasswordField(List<NodeImpl> tokens) {
    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl node = tokens.get(i);
      if(!node.isNode(Name.INPUT)) continue;
      String value = getAttribute(node, TYPE_ATTR);
      if(value == null) continue;
      if(value.equalsIgnoreCase(PASSWORD)) return i;
    }
    return -1;
  }
  
  public void resetData() { maps.clear();   }
  public void putData(String key, String...values) { maps.put(key, new ParamValue(values)); }
  public void putData(String line) { 
    int index = line.indexOf('=');
    if(index < 1) return;
    String _name = line.substring(0, index);
    String _value = line.substring(index+1, line.length());
//    System.out.println(" thay co "+ _name + " : " + _value);
    maps.put(_name.trim(), new ParamValue(_value.trim())); 
  }
  
  private void addParam(List<NameValuePair> params, String name, String value){
    if(isRemoveField(name)) return;
    NameValuePair pair  = null;
    int i = 0;
    for(; i < params.size(); i++) {
      if(params.get(i).getName().equalsIgnoreCase(name)) {
        pair = params.get(i); 
        break;
      }
    }
    
    if(value == null || value.trim().isEmpty()) {
      if(pair != null) return;
      params.add(new BasicNameValuePair(name, ""));
      return;
    }
    
    if(pair == null) {
      params.add(new BasicNameValuePair(name, value.trim()));
      return;
    }
    
    String o_value  = pair.getValue();
    if(o_value.trim().isEmpty()) {
      params.set(i, new BasicNameValuePair(name, value));
      return;
    }
    params.add(new BasicNameValuePair(name, value.trim()));
  }
  
  public boolean isAutoDetect() { return autoDetect;  }
  public void setAutoDetect(boolean autoDetect) { this.autoDetect = autoDetect;  }

  public List<String> getRemoveFields() { return removeFields; }
  
  private boolean isRemoveField(String name) {
    for(int i = 0; i < removeFields.size(); i++) {
      if(removeFields.get(i).equalsIgnoreCase(name)) return true;
    }
    return false;
  }
  
//  private boolean isIgnore(List<NameValuePair> params, String name, String value) {
//    for(int i = 0; i < ignores.size(); i++) {
//      if(ignores.get(i).equals(name, value)) return true;
//    }
//    for(int i = 0; i < params.size(); i++) {
//      NameValuePair np = params.get(i);
//      if(np.getName().equalsIgnoreCase(name) 
//          && np.getValue().equalsIgnoreCase(value)) return true;
//    }
//    return false;
//  }
  
//  public void resetIgnore() { ignores.clear();   }
//
//  public void ignoreData(String key, String value) { 
//    ignores.add(new IgnoreNameValue(key, value)); 
//  }

}
