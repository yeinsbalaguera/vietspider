/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.explorer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.vietspider.chars.CharsDecoder;
import org.vietspider.chars.URLEncoder;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.link.generator.FunctionFormGenerator;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 27, 2009  
 */
public class SiteExplorer {

  private LinkExplorer homepage;

  private LinkExplorer data;
  private LinkExplorer visit;

  private int depth = 3;
  private String charset = "utf-8";
  private boolean decode = false;

  private List<Exception> exceptions = new ArrayList<Exception>(); 

  private WebClient webClient;
  
  private int total = 0;

  private List<FunctionFormGenerator> generators = new ArrayList<FunctionFormGenerator>();
  
  public SiteExplorer(String homepage) {
    this.homepage = new LinkExplorer(homepage, 0);
    this.homepage.setIsLink(true);
    UtilFile.deleteFolder(UtilFile.getFolder("track/temp/"), false);
  }

  public SiteExplorer(String homepage, int depth) {
    this(homepage);
    this.depth = depth;
  }

  public SiteExplorer(String referer, String homepage, int depth) {
    this(homepage);
    this.depth = depth;
    this.homepage.setReferer(referer);
  }

  public void explore() {
    if(webClient == null) return;
    exceptions.clear();
    total = 0;
    explore(homepage);
  }

  private void explore(LinkExplorer link) {
    total++;
    if(total > 15) return;
    try {
      download(link);
    } catch (Exception e) {
      exceptions.add(e);
    }
    
    if(link.getTokens() == null || link.getLevel() >= depth) return;

    if(link.isData()) return;
    
    try {
      FormLinkExtractor linkExtractor = new FormLinkExtractor(new URL(link.getAddress()), link);
      linkExtractor.setGenerators(generators);
      List<LinkExplorer> explorers = linkExtractor.extract();
      
      /*for(LinkExplorer explorer : explorers) {
        java.io.File file;

        java.io.File folder = org.vietspider.common.io.UtilFile.getFolder("track/temp/"+ 
            String.valueOf(link.hashCode()) +"/");
        org.vietspider.common.io.DataWriter writer = org.vietspider.common.io.RWData.getInstance();
        
        org.vietspider.serialize.Object2XML bean2XML = org.vietspider.serialize.Object2XML.getInstance();
        org.vietspider.parser.xml.XMLDocument document = bean2XML.toXMLDocument(explorer);
        file = new java.io.File(folder, "_"+String.valueOf(explorer.hashCode())+ ".txt");
        writer.save(file, document.getTextValue().getBytes("utf-8"));
      }*/
      
      for(LinkExplorer explorer : explorers) {
        if(!explorer.isData()) continue;
        explore(explorer);
        data = explorer;
        if(data != null /*&& visit != null*/) break;
//        System.out.println(data);
        
      }
      
      for(LinkExplorer explorer : explorers) {
        if(data != null && visit != null) return;
        if(explorer.isData()) continue;
        explore(explorer);
        if(explorer.isLink() && explorer.getLevel() > 0) {
          visit = explorer;
        }
//        System.out.println(data);
      }
    } catch (Exception e) {
      exceptions.add(e);
    }
  }

  private void download(LinkExplorer link) throws Exception  {
    URLEncoder urlEncoder = new URLEncoder();
    String address = link.getAddress();
    address = urlEncoder.encode(address);

    HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);
    HttpResponse response = null;
    if(link.getParams() == null 
        || link.getParams().size() < 1) {
      response = methodHandler.execute(address, link.getReferer());
    } else {
      response = methodHandler.execute(address, link.getReferer(), link.getRequestParams(), charset);
    }
    
    if(response == null) return;

    byte [] bytes = methodHandler.readBody();
    if(charset == null || charset.trim().isEmpty()) {
      charset = new HTMLParserDetector().detectCharset(bytes);
    }
    
    try {
      saveData(link, bytes);
    } catch (Exception e) {
      exceptions.add(e);
    }
    
    char [] chars = CharsDecoder.decode(charset, bytes, 0, bytes.length);

    if(decode) chars = new RefsDecoder().decode(chars);

    link.setTokens(new HTMLParser2().createTokens(chars));
  }

  public void setCharset(String charset) { this.charset = charset; }

  public void setDecode(boolean decode) { this.decode = decode; }

  public void setWebClient(WebClient webClient) throws Exception {
    this.webClient = webClient;
    this.webClient.setURL(homepage.getReferer(), new URL(homepage.getAddress()));
  }

  public void addFunctionFormGenerator(FunctionFormGenerator generator) {
    generators.add(generator);
  }

  public List<Exception> getExceptions() {
    return exceptions;
  }
  
  public LinkExplorer getData() { return data; }
  
  public LinkExplorer getVisit() { return visit; }
  
  public LinkExplorer getHomepage() { return homepage; }
  
  private void saveData(LinkExplorer link, byte [] bytes) throws Exception {
    //begin log
    java.io.File file;
    File folder = UtilFile.getFolder("track/temp/");
    file = new File(folder, String.valueOf(link.hashCode())+ ".html");
    RWData.getInstance().save(file, bytes);
    
    org.vietspider.serialize.Object2XML bean2XML = org.vietspider.serialize.Object2XML.getInstance();
    org.vietspider.parser.xml.XMLDocument document = bean2XML.toXMLDocument(link);
    file = new File(folder, String.valueOf(link.hashCode())+ ".txt");
    RWData.getInstance().save(file, document.getTextValue().getBytes("utf-8"));
    //end log
  }

  

}
