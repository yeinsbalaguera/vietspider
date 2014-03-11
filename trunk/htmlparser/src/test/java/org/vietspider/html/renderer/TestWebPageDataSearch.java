/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.io.File;
import java.net.URL;
import java.text.Normalizer;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.URLEncoder;
import org.vietspider.chars.URLUtils;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.html.CSSData;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.renderer.extractor.WebPageDataSearcher;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 14, 2009  
 */
public class TestWebPageDataSearch {

  private  static HTMLExtractor extractor  = new HTMLExtractor();
  private  static NodePathParser pathParser = new NodePathParser();

  private static WebClient webClient = new WebClient();

  public static byte[] loadContent(String address) throws Exception {
//  URL url = new URL(address);
    HttpGet httpGet = null;
    try{
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      httpGet = webClient.createGetMethod(address, "http://www.xe24h.net/?HOME/VN/");

      if(httpGet == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);

      HttpMethodHandler httpResponseReader = new HttpMethodHandler(webClient);
      byte [] bytes = null; 

      bytes = httpResponseReader.readBody(httpResponse);
      return bytes;
    } catch(Exception exp){
      exp.printStackTrace();
//    LogService.getInstance().setThrowable(e);
      return null;
    }
  }

  private static void loadCSS(String address, HTMLDocument document) throws Exception {
    CSSData cssData = new CSSData();
    document.putResource("CSS.DATA", cssData);

    NodePath nodePath  = pathParser.toPath("HEAD");
    HTMLNode head = extractor.lookNode(document.getRoot(), nodePath);

    URLUtils urlUtils = new URLUtils();
    NodeIterator iterator = head.iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.LINK)) continue;
      Attributes attributes = node.getAttributes(); 
      Attribute attribute = attributes.get("type");
      if(attribute == null) continue;
      if(!"text/css".equalsIgnoreCase(attribute.getValue())) continue;

      attribute = attributes.get("href");
      if(attribute == null) continue;
      String link = attribute.getValue();
      if(link == null) continue;

      link = urlUtils.createURL(new URL(address), link); 

      System.out.println(link);
      byte [] bytes = loadContent(link);

      String css = new String(bytes, "utf-8");
      cssData.addValue(css);
    }
  }

  public static void main(String[] args) throws Exception {
    String address = "http://vnexpress.net/GL/Xa-hoi/2009/02/3BA0B4AB/";
    webClient.setURL(address, new URL(address));
//  String address  = "http://vnmedia.vn/newsdetail.asp?NewsId=154558&CatId=58";
    java.net.URL url = new java.net.URL(address);
    HTMLDocument document = HTMLParser.createDocument(loadContent(address),"utf-8");

    RefsDecoder decoder = new RefsDecoder();
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.CONTENT)) continue;
      char [] chars = node.getValue();
      chars = decoder.decode(chars);

      chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
      chars =  java.text.Normalizer.normalize(new String(chars), Normalizer.Form.NFC).toCharArray();
      node.setValue(chars);              
    }  

    loadCSS(address, document);

    NodePath nodePath  = pathParser.toPath("BODY");
    HTMLNode body = extractor.lookNode(document.getRoot(), nodePath);

    WebPageDataSearcher dataSearcher = new WebPageDataSearcher(document);
    HTMLNode node = dataSearcher.search(body);

    File file  = new File("F:\\Temp2\\web\\output\\extract.htm");
    byte [] bytes = new byte[0];
    if(node != null) bytes = node.getTextValue().getBytes(Application.CHARSET);
    RWData.getInstance().save(file, bytes);

  }
}
