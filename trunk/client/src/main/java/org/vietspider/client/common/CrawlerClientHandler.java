/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.vietspider.bean.Domain;
import org.vietspider.bean.Meta;
import org.vietspider.common.Application;
import org.vietspider.common.io.GZipIO;
import org.vietspider.model.Source;
import org.vietspider.net.client.AbstClientConnector.HttpData;
import org.vietspider.net.server.CrawlerStatus;
import org.vietspider.net.server.Metas;
import org.vietspider.net.server.URLPath;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 25, 2007  
 */
public class CrawlerClientHandler {
  
  public List<Meta> getMetas(Domain domain) throws Exception {
    Header header = new BasicHeader("action", "load.meta");
    byte [] bytes = domain.getId().getBytes(Application.CHARSET);
    ClientConnector2 connector = ClientConnector2.currentInstance();
    bytes = connector.post(URLPath.DATA_HANDLER, bytes, header);
    
    bytes = new GZipIO().unzip(bytes);
    
    String xml = new String(bytes, Application.CHARSET);
    if(xml.trim().length() < 1 || xml.trim().equals("-1")) return new ArrayList<Meta>(0);

    XMLDocument document = XMLParser.createDocument(xml, null);
    Metas metas = XML2Object.getInstance().toObject(Metas.class, document);
    return metas.getList();
  }
  
//  public void deleteMetas(StringBuilder builder) throws Exception {
//    Header header = new BasicHeader("action", "delete.meta");
//    byte [] bytes = builder.toString().trim().getBytes(Application.CHARSET);
//    ClientConnector connector = ClientConnector.currentInstance();
//    connector.post(URLPath.DATA_HANDLER, bytes, header);
//  }
  
  public int mornitorCrawler(int status) throws Exception {
    CrawlerStatus instance = getCrawlerStatus(new CrawlerStatus(status));
    return instance == null ? 0 : instance.getStatus();
  }
  
  public String viewExecutor(int index) throws Exception {
    Header header = new BasicHeader("action", "view.executor");
    byte [] bytes = String.valueOf(index).getBytes();
    ClientConnector2 connector = ClientConnector2.currentInstance();
    bytes = connector.post(URLPath.CRAWLER_HANDLER, bytes, header);
    return new String(bytes, Application.CHARSET);
  }
  
  public String abortExecutor(int index) throws Exception {
    Header header = new BasicHeader("action", "abort.executor");
    byte [] bytes = String.valueOf(index).getBytes();
    ClientConnector2 connector = ClientConnector2.currentInstance();
    bytes = connector.post(URLPath.CRAWLER_HANDLER, bytes, header);
    return new String(bytes, Application.CHARSET);
  }
  
  public String viewPool() throws Exception {
    Header header = new BasicHeader("action", "view.crawl.pool");
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = connector.post(URLPath.CRAWLER_HANDLER, new byte[0], header);
    return new String(bytes, Application.CHARSET);
  }
  
  public String clearPoolQueue() throws Exception {
    Header header = new BasicHeader("action", "pool.clear.queue");
    ClientConnector2 connector = ClientConnector2.currentInstance();
    byte [] bytes = connector.post(URLPath.CRAWLER_HANDLER, new byte[0], header);
    return new String(bytes, Application.CHARSET);
  }
  
  public CrawlerStatus getCrawlerStatus(int status) throws Exception {
    return getCrawlerStatus(new CrawlerStatus(status));
  }
  
  public CrawlerStatus getCrawlerStatus(CrawlerStatus status) throws Exception {
    Header header = new BasicHeader("action", "monitor.crawler");
    String xml = Object2XML.getInstance().toXMLDocument(status).getTextValue();
    byte [] bytes = xml.getBytes(Application.CHARSET);
    ClientConnector2 connector = ClientConnector2.currentInstance();
    bytes = connector.post(URLPath.CRAWLER_HANDLER, bytes, header);
    return XML2Object.getInstance().toObject(CrawlerStatus.class, bytes);
  }
  
  public void addSourcesToLoader(Source source) throws Exception {
    StringBuilder builder  = new StringBuilder(source.getName()).append('.');
    builder.append(source.getGroup().toString()).append('.').append(source.getCategory());
    addSourcesToLoader(builder.toString(), false);
  }

  public void addSourcesToLoader(String builder, boolean all) throws Exception {
    byte [] bytes = builder.getBytes(Application.CHARSET);
    Header [] headers = new Header[0];
    if(all) {
      headers = new Header[] {
          new BasicHeader("add.type", "*"),
          new BasicHeader("action", "add.source")
      };
    } else {
      headers = new Header[] {
          new BasicHeader("action", "add.source")
      };
    }
    ClientConnector2 connector = ClientConnector2.currentInstance();
    connector.post(URLPath.CRAWLER_HANDLER, bytes, headers);
  }
  
  /*public void sortListLoader() throws Exception {
    Header header = new BasicHeader("action", "sort.download.list.source");
    ClientConnector connector = ClientConnector.currentInstance();
    connector.post(URLPath.CRAWLER_HANDLER, new byte[0], header);
  }*/
  
  public void removeCrawlSource(String value) throws Exception {
    if(value == null) return ;
    Header header = new BasicHeader("action", "remove.source");
    byte [] bytes = value.getBytes(Application.CHARSET);
    ClientConnector2 connector = ClientConnector2.currentInstance();
    connector.post(URLPath.CRAWLER_HANDLER, bytes, header);
  }
  
  public HttpData loadDownloadList() throws Exception {
    Header [] headers = new Header[] {        
        new BasicHeader("action", "load.file.by.gzip"),
        new BasicHeader("file", "system/load")
    };
    ClientConnector2 connector = ClientConnector2.currentInstance();
    return connector.loadResponse(URLPath.FILE_HANDLER, new byte[0], headers);
  }
}
