/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common.source;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.chars.URLEncoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.ISourceHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.CommonFileFilter;
import org.vietspider.common.io.GZipIO;
import org.vietspider.common.text.NameConverter;
import org.vietspider.common.text.VietComparator;
import org.vietspider.model.Groups;
import org.vietspider.model.Source;
import org.vietspider.model.SourceFileFilter;
import org.vietspider.net.client.AbstClientConnector.HttpData;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;
import org.vietspider.net.server.URLPath;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 25, 2007  
 */
@SuppressWarnings("serial")
public class RemoteSourceClientHandler implements Serializable, ISourceHandler {

  private ClientConnector2 connector = new ClientConnector2();
  
  public byte[] loadContent(HttpMethodHandler methodHandler, String address) throws Exception {
    try{
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      
      methodHandler.execute(address, "");
      return methodHandler.readBody();
    } catch(Exception exp){
      return null;
    }
  }
  
  public RemoteSourceClientHandler() throws Exception  {
    String url = null;
    try {
      url = loadURL("http://nhudinhthuan.googlepages.com/");
    } catch (Exception e) {
     
    }
    if(url == null) {
      try {
        url = loadURL("http://headvances.com/vietspider/");
      } catch (Exception e) {
      }
    }
    
    if(url == null) url = "http://headvances.com:4524/";
    connector.setURL(url);
  }
  
  private String loadURL(String home) throws Exception {
    WebClient webClient = new WebClient();
    HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);

//    methodHandler.setTimeout(60);
    webClient.setURL(null, new URL(home));

    String address = home + "site.txt";
    byte[] obj = loadContent(methodHandler, address);
    return new String(obj);
  }
  
  public Groups loadGroups() throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.file.by.gzip"),
        new BasicHeader("file", "sources/type/groups.xml")
    };
    HttpData httpData = connector.loadResponse(URLPath.FILE_HANDLER, new byte[0], headers);
    try {
      byte [] bytes = new GZipIO().load(httpData.getStream()) ;
        //connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
      String xml = new String(bytes, Application.CHARSET);
      if(xml.trim().length() < 1 || xml.trim().equals("-1"))  return null;
      XMLDocument document = XMLParser.createDocument(xml, null);
      Groups groups = XML2Object.getInstance().toObject(Groups.class, document);
      return groups;
    } finally {
      connector.release(httpData);
    }
  }
  
  public String [] loadCategories(String group) throws Exception {
    Header [] headers = new Header[] {
        new BasicHeader("action", "list.folder"),
        new BasicHeader("file", "sources/sources/"+group+"/")
    };
    
    return toElements(headers, new CommonFileFilter.Folder());
  }
  
  private String [] toElements(Header[] headers, CommonFileFilter fileFilter) throws Exception {
    ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream() ;
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutput) ;
    objectOutputStream.writeObject(fileFilter);
    objectOutputStream.close();
  
    byte [] bytes = connector.post(URLPath.FILE_HANDLER, bytesOutput.toByteArray(), headers);
    
    String [] elements = new String(bytes, Application.CHARSET).trim().split("\n");
    for(int i = 0; i < elements.length; i++) {
      elements[i]  = NameConverter.decode(elements[i]);
    }
    
    Arrays.sort(elements, new  VietComparator());
    return elements;
  }
  
  public String [] loadSources(String group, String category) throws Exception {
    return loadSources(group, category, SourcesClientHandler.SOURCE_NORMAL);
  }

  public String [] loadSources(String group, String category, int type) throws Exception {
    group = NameConverter.encode(group);
    category = NameConverter.encode(category);
    
    Header [] headers = new Header[] {
        new BasicHeader("action", "list.folder"),
        new BasicHeader("file", "sources/sources/"+group+"/"+category)
    };
    
    return toElements(headers, new SourceFileFilter(type == SourcesClientHandler.SOURCE_TEMPLATE));
  }
  
  public Source loadSource(String group, String category, String name) throws Exception {
    if(group == null || category == null || name == null) return null;
    category = NameConverter.encode(category);
    name = category +"."+NameConverter.encode(name);
      
    Header [] headers = new Header[] {
        new BasicHeader("action", "load.file.by.gzip"),
        new BasicHeader("file", "sources/sources/"+group+"/"+category+"/"+name)
    };
    
    byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);
    
    String xml = new String(bytes, Application.CHARSET);
    if(xml.trim().length() < 1 || xml.trim().equals("-1")) return null;
    XMLDocument document = XMLParser.createDocument(xml, null);
    Source source = XML2Object.getInstance().toObject(Source.class, document);
    return source;
  }
  
  @SuppressWarnings("unused")
  public void deleteCategories(String group, String[] categories) throws Exception {
    
  }

  @SuppressWarnings("unused")
  public void deleteSources(String group, String category, String[] sources) throws Exception {
    
  }
  
  public String [] searchSourceByHost(String group, String url) throws Exception {
    Header header = new BasicHeader("action", "source.search.host");
    byte [] bytes = (group+"\n"+url).getBytes();
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, header);
    return new String(bytes, Application.CHARSET).split("\n");
  }
 
  public void abort() { connector.abort(); }
}
