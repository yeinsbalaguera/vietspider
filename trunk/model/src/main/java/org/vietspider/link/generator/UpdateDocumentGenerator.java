/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.CharsDecoder;
import org.vietspider.chars.TextSpliter;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.link.ILink;
import org.vietspider.crawl.link.MockLink;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.link.pattern.LinkPatternFactory;
import org.vietspider.link.pattern.LinkPatterns;
import org.vietspider.model.Source;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.HttpResponseReader;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 30, 2009  
 */
public class UpdateDocumentGenerator implements UpdateDocument {
  
  private String [] templates;
  private String patternValue;
  
  private LinkPatterns pattern;
  private String sourceFullName;
  private String charset = Application.CHARSET;
  private TextSpliter spliter = new TextSpliter();
  
  public UpdateDocumentGenerator(String sourceFullName, String...values) throws Exception {
    if(values.length < 1) throw new Exception ("Incorrect generator");
    this.sourceFullName = sourceFullName;
    
    this.patternValue = values[0];
//    if(values.length == 1) return;
    pattern = LinkPatternFactory.createPatterns(LinkPatterns.class, patternValue);
    
    this.templates = new String[values.length-1];
    for(int i = 1; i < values.length; i++) {
      templates[i-1] = values[i];
    }
  }
  
  public void setCharset(String charset) { this.charset = charset; }

  public void generate(ILink link, WebClient webClient, HTMLDocument document) {
    String address = link.getAddress();
    
    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();
    NodePath nodePath  = null;
    try {
      nodePath  = pathParser.toPath("BODY");
    } catch (Exception e) {
    }
    if(nodePath == null) return;
    HTMLNode body = extractor.lookNode(document.getRoot(), nodePath);
    
    if("referer".equalsIgnoreCase(patternValue)) {
      if(!link.isData()) return;
      HTMLDocument newDoc = loadDocument(webClient, link.getReferer());
      if(newDoc == null) return;
      
      HTMLNode newBody = extractor.lookNode(newDoc.getRoot(), nodePath);
      
      List<HTMLNode> newChildren = newBody.getChildren();
      if(newChildren == null || newChildren.size() < 1) return;
      for(int i = 0; i < newChildren.size(); i++) {
        body.addChild(newChildren.get(i));
      }
      return;
    }
    
//    System.out.println(" vao day roi "+ pattern+ " : "+pattern.match(address));
//    System.out.println("====  >"+address);
    if(!pattern.match(address)) return;
//    System.out.println(" -==== ? "+ address);

    for(String template : templates) {
      String url = createLink(address, template);

      HTMLDocument newDoc = loadDocument(webClient, url);
      if(newDoc == null) return;
      
      HTMLNode newBody = extractor.lookNode(newDoc.getRoot(), nodePath);
      
      List<HTMLNode> newChildren = newBody.getChildren();
      if(newChildren == null || newChildren.size() < 1) return;
      for(int i = 0; i < newChildren.size(); i++) {
        body.addChild(newChildren.get(i));
      }
    }
  }

  private HTMLDocument loadDocument(WebClient webClient, String url) {
    HttpMethodHandler httpGetHandler = new HttpMethodHandler(webClient);
    
    HTMLParserDetector parser = new HTMLParserDetector();
    char [] chars = webClient.getCacheData().getCachedObject(url);
    
    if(chars != null) {
//      System.out.println("thay tu cache " + url);
      try {
        return parser.createDocument(chars);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(sourceFullName, e);
        return null;
      }
    }
    
    byte [] data = null;
//    System.out.println(url);
    try {
      httpGetHandler.execute(url, url);
      data = httpGetHandler.readBody();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(sourceFullName, e);
      return null;
    }
    
    if(data == null || data.length < 1 || data.length > HttpResponseReader.BIG_SIZE) return null;
    Source source = CrawlingSources.getInstance().getSource(sourceFullName);
    if(source  != null) charset = source.getEncoding();
    
    if(charset == null || charset.trim().isEmpty()) {
      charset = parser.detectCharset(data);
    } 
    
    try {
      chars = CharsDecoder.decode(charset, data, 0, data.length);
//      System.out.println(" da load vao day duoc "+ new String(chars));
      chars = new RefsDecoder().decode(chars);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(sourceFullName, e);
      return null;
    }
    if(chars == null) return null;
    
    try {
      return parser.createDocument(chars);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(sourceFullName, e);
      return null;
    }
  }
  
  private String createLink(String address, String template) {
    String [] params = getParams(address);
    String value = new String(template);
//    System.out.println(" thay co params "+ params.length);
    for(int i = 0; i < params.length; i++) {
//      System.out.println(" === > "+ params[i]);
      value = value.replaceAll("\\{"+(i+1)+"\\}", params[i]);
    }
    return value;
  }
  
  private String[] getParams(String address) {
    String [] elements = spliter.toArray(patternValue, '*');//.split("\\*");
    List<String> values  = new ArrayList<String>();
    int index = 0; 
    while(index < elements.length) {
//      System.out.println("check voi address: "+ address);
//      System.out.println(" ===  >"+ elements[index]);
      int start = address.indexOf(elements[index]);
      if(start < 0) break;
      start += elements[index].length();
      int end = address.length();
      if(index < elements.length - 1 
          && elements[index+1].length() > 0) {
//        System.out.println(" data char at "+ address.substring(start+1));
//        System.out.println(" check cai tiep theo la " + elements[index+1]);
        end = address.indexOf(elements[index+1], start+1);
      }
//      System.out.println(start+ " : "+ end);
      if(end < 0) break;
//      System.out.println(" tao duoc param " +address.substring(start, end));
      values.add(address.substring(start, end));
      address = address.substring(end);
      index++;
    }
//    System.out.println(" hihi "+ index + " addres " + address.length());
    
    return values.toArray(new String[values.size()]);
  }
  
  public short getType() { return Generator.UPDATE_DOCUMENT_GENERATOR; }
  
  public static void main(String[] args) throws Exception {
    String pattern = "http://patentscope.wipo.int/*/*/*";
    String template = "http://patentscope.wipo.int/search/en/detail.jsf?docId={3}&recNum=1&maxRec=&office=&prevFilter=&sortOption=&queryString=&tab=PCTDescription";
    UpdateDocumentGenerator generator = new UpdateDocumentGenerator("", pattern, template);
    
    String url = "http://patentscope.wipo.int/search/en/WO2013112032";
    String [] params = generator.getParams(url);
    for(String param : params) {
      System.out.println(param);
    }
    
    System.out.println(" tao duoc link la " + generator.createLink(url, template));
    
    WebClient webClient = new WebClient();
    webClient.setURL(url, new URL(url));
    
    HTMLDocument document = generator.loadDocument(webClient, url);
    if(document == null) {
      System.out.println("document is null ");
      return;
    }
    
    MockLink mockLink = new MockLink(url, null);
    generator.generate(mockLink, webClient, document);
    
    org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    File file = new File("D:\\Temp\\", "update_doc.html"); 
    writer.save(file, document.getTextValue().getBytes("utf-8"));
    Desktop.getDesktop().browse(file.toURI());
    
  }

}
