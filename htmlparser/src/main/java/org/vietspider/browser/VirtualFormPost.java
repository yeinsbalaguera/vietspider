/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.browser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.vietspider.chars.CharsDecoder;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 24, 2008  
 */
public class VirtualFormPost {

  public final static String TYPE_ATTR = "type";
  public final static String PASSWORD = "password";

  public final static String ID_ATTR = "id";
  public final static String NAME_ATTR = "name";
  public final static String VALUE = "value";
  public final static String ACTION = "action";

  public final static int ERROR = -1;
  public final static int LOGIN = 0;
  public final static int SUCCESSFULL = 1;
  public final static int TIMEOUT = 2;

  protected HashMap<String, ParamValue> maps;
  protected List<IgnoreNameValue> ignores;

  protected WebClient webClient;
  protected HttpMethodHandler httpMethod;

  protected String charset = "utf-8";

  protected String message = null;

  protected ErrorDetector errorDetector;

  protected boolean multipart = false;
  protected String boundary = null;

  protected String logFile ;// = "D:\\java\\test\\vsnews\\data\\track\\temp\\aaa";

  public VirtualFormPost() {
  }

  public VirtualFormPost(WebClient webClient) {
    maps = new HashMap<String, ParamValue>();
    ignores = new ArrayList<IgnoreNameValue>();
    this.webClient = webClient;
    this.httpMethod = new HttpMethodHandler(webClient);
    //    this.httpMethod.setTimeout(15);
  }

  public byte[] get(String referer, String url) throws Exception{
    HttpResponse response = httpMethod.execute(url, referer);
    if(response == null) {
      message = "Not response!";
      return null;
    }

    StatusLine statusLine = response.getStatusLine();
    int statusCode = statusLine.getStatusCode();

    switch (statusCode) {
    case HttpStatus.SC_NOT_FOUND:
    case HttpStatus.SC_NO_CONTENT:
    case HttpStatus.SC_BAD_REQUEST:
    case HttpStatus.SC_REQUEST_TIMEOUT:
    case HttpStatus.SC_NOT_ACCEPTABLE:
    case HttpStatus.SC_SERVICE_UNAVAILABLE:
    case 999:{
      throw new Exception(url + " " + statusLine.getReasonPhrase());
    }
    default:
      break;
    }

    byte [] data = httpMethod.readBody();

    if(webClient.isLog() && logFile != null) {
      StringBuilder builder = new StringBuilder("post to:");
      builder.append(url).append('\n');
      StatusLine line = response.getStatusLine();
      builder.append("Reason Phrase:").append(line.getReasonPhrase()).append('\n');
      builder.append("Status Code:").append(line.getReasonPhrase()).append('\n');
      LogService.getInstance().setMessage("WEB", null,  builder.toString());
      java.io.File file = new File(logFile + ".request_form.html");
      RWData.getInstance().save(file, data);
    }

    return data;
  }

  public int post(String referer,
      String formName, String url) throws Exception {
    return post(referer, formName, url, false);
  }

  public int post(String referer,
      String formName, String url, boolean abort) throws Exception {
    byte[] data = get(referer, url);
    return post(referer, formName, url, data, abort);
  }
  
  public int post(String referer,
      String formName, String postUrl, byte[] data, boolean abort) throws Exception {
    return post(referer, formName, postUrl, data, abort, false);
  }

  public int post(String referer,
      String formName, String postUrl, byte[] data, boolean abort, boolean readReponse) throws Exception {
    if(data == null) {
      message = "Error: Not html data!";
      return ERROR;
    }
    
    if(charset == null) {
      HTMLParserDetector detector = new HTMLParserDetector();
      charset = detector.detectCharset(data);
    }

    char [] chars = CharsDecoder.decode(charset, data, 0, data.length);
    return post(referer, formName, postUrl, chars, abort, readReponse);
  }

  public int post(String referer,
      String formName, String postUrl, char[] chars, boolean abort, boolean readReponse) throws Exception {
//        System.out.println("000000" + webClient.isLog()+  " : "+ logFile);

    List<NodeImpl> tokens  = new HTMLParser2().createTokens(chars);
    if(tokens == null) {
      message = "Error: Can't parse tokens!";
      return ERROR;
    }
    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl node = tokens.get(i);
      if(!node.isNode(Name.INPUT)) continue;
      String value = getAttribute(node, TYPE_ATTR);
      if(value == null) continue;
      if(value.equalsIgnoreCase(PASSWORD)) return LOGIN;
    }
    int i = 0; 
    for(;i < tokens.size(); i++) {
      NodeImpl node = tokens.get(i);

      if(node.isNode(Name.FORM)) {
        if(formName == null) break;        
        Attributes attrs = node.getAttributes(); 
        String name = getAttribute(attrs, NAME_ATTR);
        if(formName.equalsIgnoreCase(name)) break;
      }
    }

    //    System.out.println(webClient.isLog()+  " : "+ logFile);

    HTMLNode form = null;
    List<HTMLNode> inputs = new ArrayList<HTMLNode>();

    for(; i < tokens.size(); i++) {
      NodeImpl node = tokens.get(i);
      if(node.isNode(Name.FORM)) {
        if(node.isOpen()) {
          form = node;
        } else {
          break;
        }
      } else if(node.isNode(Name.INPUT)
          || node.isNode(Name.TEXTAREA) 
          || node.isNode(Name.SELECT)) {        
        inputs.add(node);
      } 
    }

    if(form == null || inputs.size() < 1) {
      message = "Error: form not found";
      return ERROR;
    }

    String address = getAttribute(form, ACTION);
    if(address == null) {
      message = "Error: post address not found!";
      return ERROR;
    }

//        System.out.println("1111" + webClient.isLog()+  " : "+ logFile);

    List<NameValuePair> params = new ArrayList<NameValuePair>();
    for(i = 0 ; i < inputs.size(); i++) {
      Attributes attrs = inputs.get(i).getAttributes();
      String name = getAttribute(attrs, NAME_ATTR);
      if(name == null) getAttribute(attrs, ID_ATTR);
      if(name == null) continue;
      String type = getAttribute(attrs, TYPE_ATTR);
      if(type == null) type = "text";
      type = type.trim();

      ParamValue paramValues = maps.get(name);
      String [] values = null;
      if(paramValues == null) {
        values = new String[]{getAttribute(attrs, VALUE)};
      } else {
        values = paramValues.getValues();
      }

      for(String value :  values) {
        if(value == null) value = "";
        if(isIgnore(params, name, value)) continue;
        if("radio".equalsIgnoreCase(type) || "checkbox".equalsIgnoreCase(type)) {
          Attribute checked = getAttributeObject(attrs, "checked");
          ParamValue pv  = maps.get(name);
          if(checked == null && pv == null) continue;
        }
        addParam(params, name, value.trim());
      }
      //end for
    }

    URLUtils urlUtils = new URLUtils();
    address = urlUtils.createURL(postUrl, address).trim();
    address = urlUtils.getCanonical(address);

    referer = postUrl.toString();
    HttpHost httpHost = webClient.createHttpHost(address);
    HttpPost httpPost = null;

//        System.out.println(webClient.isLog()+  " : "+ logFile);

    if(multipart) {
      httpPost = webClient.createMultiPartFormPostMethod(address, 
          referer, params, boundary, charset);
    } else {
      httpPost = webClient.createFormPostMethod(address, referer, params, charset);
    }

    //    System.out.println(webClient.isLog()+  " : "+ logFile);
    logPost(httpPost, address, ".post_request.txt");
    //    long start = System.currentTimeMillis();
//        System.out.println(" chuan bi chay vao day request toi server ");
    HttpResponse response2 = httpMethod.execute(httpHost, httpPost);
    //    System.out.println("status message "+response2.getStatusLine().getReasonPhrase());
    //    System.out.println("status message "+response2.getStatusLine().getStatusCode());
    
    
      if(abort) {
        httpMethod.abort();
        return SUCCESSFULL;
      } 

      try {
      byte [] bytes = logResponse(response2, address, ".response.html", readReponse);

      //      if(bytes != null) {
      if(errorDetector != null && errorDetector.isError(bytes)) return ERROR;
      //    long end = System.currentTimeMillis();
      //    System.out.println("mat "+ (end - start));
      if(checkTimeout(bytes)) return TIMEOUT;  
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    } finally {
      try {
        response2.getEntity().getContent().close();
      } catch (Exception e) {
      }
      httpMethod.abort();
    }

    return SUCCESSFULL;
  }

  public void abort() { httpMethod.abort(); }

  private boolean isIgnore( List<NameValuePair> params, String name, String value) {
    for(int i = 0; i < ignores.size(); i++) {
      if(ignores.get(i).equals(name, value)) return true;
    }
    for(int i = 0; i < params.size(); i++) {
      NameValuePair np = params.get(i);
      if(np.getName().equalsIgnoreCase(name) 
          && np.getValue().equalsIgnoreCase(value)) return true;
    }
    return false;
  }

  private void addParam(List<NameValuePair> params, String name, String value) {
    BasicNameValuePair newPair = new BasicNameValuePair(name, value.trim());
    //    for(int i = 0; i < params.size(); i++) {
    //      BasicNameValuePair pair = (BasicNameValuePair)params.get(i);
    //      if(pair.getName().equalsIgnoreCase(name)) {
    //        params.set(i, newPair);
    //        return;
    //      }
    //    }
    params.add(newPair);
  }

  public String getAttribute(HTMLNode node, String name) {
    Attributes attrs = node.getAttributes(); 
    int idx = attrs.indexOf(name);
    if(idx < 0) return null; 
    return attrs.get(idx).getValue();
  }

  public String getAttribute(Attributes attrs, String name) {
    int idx = attrs.indexOf(name);
    if(idx < 0) idx = attrs.indexOf("id");
    if(idx < 0) return null; 
    return attrs.get(idx).getValue();
  }

  public Attribute getAttributeObject(Attributes attrs, String name) {
    int idx = attrs.indexOf(name);
    if(idx < 0) return null; 
    return attrs.get(idx);
  }

  public HashMap<String, ParamValue> getData() { return maps; }

  public void resetData() { maps.clear();   }
  public void resetIgnore() { ignores.clear();   }

  public void putData(String key, String...values) { maps.put(key, new ParamValue(values)); }

  public void ignoreData(String key, String value) { 
    ignores.add(new IgnoreNameValue(key, value)); 
  }

  public String getCharset() { return charset; }
  public void setCharset(String charset) { this.charset = charset; }

  public String getMessage() {
    StringBuilder builder = new StringBuilder();
    if(errorDetector != null) builder.append(errorDetector.getMessage()).append(' ');
    if(message != null) builder.append(message);
    return builder.toString(); 
  }

  public void log(String address, byte [] bytes) throws Exception {
    LogService.getInstance().setMessage("WEB", null, "post to:" +address);
    java.io.File file = UtilFile.getFile("track/temp/", Utils.toFileName(address));
    RWData.getInstance().save(file, bytes);
  }

  public ErrorDetector getErrorDetector() { return errorDetector; }
  public void setErrorDetector(ErrorDetector errorDetector) {
    this.errorDetector = errorDetector;
  }

  public boolean isMultipart() { return multipart; }
  public void setMultipart(boolean multipart) { this.multipart = multipart; }

  public String getLogFile() { return logFile; }
  public void setLogFile(String logFile) { this.logFile = logFile; }

  public String getBoundary() { return boundary; }
  public void setBoundary(String boundary) { this.boundary = boundary; }

  //  ".post_request.txt"
  public void logPost(HttpPost httpPost, String address, String fileName) {
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
      
      System.out.println("===> " +new String(byteArray.toByteArray(), Application.CHARSET));
      builder.append(new String(byteArray.toByteArray(), Application.CHARSET));

      java.io.File file = new File(logFile + fileName);
//      System.out.println(" chuan bi save " + file.getAbsolutePath());
      RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  //"." + name + ".response.html"
  public byte[] logResponse(HttpResponse response, 
      String address, String fileName, boolean readResponse) {
    return logResponse(httpMethod, response, address, fileName, readResponse);
  }
  
  
  public byte[] logResponse(HttpMethodHandler method,
      HttpResponse response, String address, String fileName) {
    return logResponse(method, response, address, fileName, false);
  }

  public byte[] logResponse(HttpMethodHandler method,
      HttpResponse response, String address, String fileName, boolean readResponse) {
    if(logFile == null || !webClient.isLog()) {
      try {
        if(readResponse) method.readBody();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
//      System.out.println(logFile);
//      System.out.println(webClient.isLog());
      return null;
    }
    StringBuilder builder = new StringBuilder("post to:");
    builder.append(address).append('\n');
    if(response != null) {
      StatusLine line = response.getStatusLine();
      builder.append("Reason Phrase:").append(line.getReasonPhrase()).append('\n');
      builder.append("Status Code:").append(line.getStatusCode()).append('\n');
    } else {
      builder.append("HTTP Response is null!");
    }
    LogService.getInstance().setMessage("WEB", null,  builder.toString());
    java.io.File _file = new File(logFile + fileName);
    byte[] bytes = null;
    try {
      bytes = method.readBody();
      RWData.getInstance().save(_file, bytes);
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
//      LogService.getInstance().setThrowable(e);
    }
    return bytes;
  }

  private boolean checkTimeout(byte [] data) throws Exception {
    if(data == null) return true;
    HTMLParserDetector parser = new HTMLParserDetector();
    if(charset == null) charset = parser.detectCharset(data);

    char [] chars = CharsDecoder.decode(charset, data, 0, data.length);
    List<NodeImpl> tokens  = parser.createTokens(chars);
    if(tokens == null) return true;

    for(int i = 0; i < tokens.size(); i++) {
      NodeImpl node = tokens.get(i);
      if(!node.isNode(Name.INPUT)) continue;
      String value = getAttribute(node, TYPE_ATTR);
      if(value == null) continue;
      if(value.equalsIgnoreCase(PASSWORD)) return true;
    }
    return false;
  }

  public static class IgnoreNameValue {

    private String name;
    private String value;

    public IgnoreNameValue(String name, String value){
      this.name = name;
      this.value = value;
    }

    public boolean equals(String n, String v) {
      return name.equalsIgnoreCase(n) && 
      ((value.length() == 1 && value.charAt(0) == '*') || value.equalsIgnoreCase(v));
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

  }

  public static class ParamValue {

    private String [] values;

    public ParamValue() {  }

    public ParamValue(String...values) { 
      this.values = values; 
    }

    public String[] getValues() { return values; }

    public void setValues(String...values) { this.values = values; }
  }



}
