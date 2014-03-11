/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.vietspider.bean.Meta;
import org.vietspider.chars.URLUtils;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.handler.XMLHandler;
import org.vietspider.handler.XMLResource;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.token.TypeToken;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 1, 2008  
 */
public class XMLDataHandler extends XMLHandler {

  protected Link link;
  
  protected String template;
  
  private List<XMLResource> resources = new ArrayList<XMLResource>(); 

  public XMLDataHandler(Source source) {
    Properties properties = source.getProperties();
    template = properties.getProperty("DocumentTemplate");
    if(template == null || template.trim().isEmpty()) {
      template = null;
    } else {
      RefsDecoder decoder = new RefsDecoder();
      template = new String(decoder.decode(template.toCharArray()));
    }

  }

  public String handle(PluginData pluginData) {
    resources.clear();
    XMLDocument xmlDocument = buildDocument(pluginData);
    if(xmlDocument == null) return null;
    String xml = buildTemplate(template, xmlDocument);
    return xml;
  }

  public XMLDocument buildDocument(PluginData pluginData) {
    documentId = pluginData.getMeta().getId();

    HTMLDocument document = pluginData.getLink().getDocument();
    Meta meta = pluginData.getMeta();
    this.link = pluginData.getLink();
    
    XMLNode xmlRoot = new XMLNode("document", TypeToken.TAG);
    XMLDocument xmlDocument = new XMLDocument(xmlRoot);

    HTMLNode root = document.getRoot();
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    Region [] regions = source.getProcessRegion();

    for(int i = 0; i < regions.length; i++) {
      String key = regions[i].getName();
//      String[] contents = new String[]{""};
      try {
        Object value = htmlUtil.lookupTextValue2(root, regions[i], link.getAddress());
        if(value instanceof  String[]) {
          String [] contents = (String[]) value;
          createTextNode(xmlRoot, key, contents);
          if(regions[i].getType() == Region.TEXT || regions[i].getType() == Region.DEFAULT) {
            if(meta.getTitle()  == null || meta.getTitle().isEmpty()) {
              if(contents.length > 0) meta.setTitle(contents[0].trim());
            } else if(meta.getDesc() == null || meta.getDesc().isEmpty()) {
              if(contents.length > 0) meta.setDesc(contents[0].trim());
            }
          }
        } else if(value instanceof XMLResource[])  {
          XMLResource[] _resources = (XMLResource[])value;
          createResources(key, xmlRoot, _resources, link.getAddress());
          Collections.addAll(resources, _resources);
        }
      } catch (Exception e) {
        LogService.getInstance().setThrowable(link.getSourceFullName(), e);
      }
    }
      

//    try {
//      xmlRoot.addChild(createResources(pluginData.getLink().getAddress()));
//    } catch (Exception e) {
//      LogService.getInstance().setThrowable(source, e);
//    }
    createSourceNode(xmlRoot, pluginData.getLink().getAddress());

    xmlRoot.setIsOpen(false);
    
    split(xmlRoot);

//    if(meta.getTitle()  == null || meta.getTitle().isEmpty()) {
//      meta.setTitle("no title");
//    }

    if(meta.getDesc() == null || meta.getDesc().isEmpty()) {
      meta.setDesc("empty description");
    }

    return xmlDocument;
//  return new String[]{title, desc, xmlDocument.getTextValue()};
  }

  public String createLink(String address, String value) {
    URLUtils urlUtils = new URLUtils();
    if(link.getBaseHref() != null ) {
      try {
        return urlUtils.createURL(new URI(link.getBaseHref()).normalize().toURL(), value);
      }catch (Exception e) {
        return urlUtils.createURL(link.getBaseHref(), value);
      }
    } 

    try {
      address = link.getAddress();
      if(address.startsWith("http://") 
          || address.startsWith("shttp://") 
          || address.startsWith("https://")) {
        value  = urlUtils.createURL(new URL(address), value);
        return urlUtils.getCanonical(value);
      } 
      address = urlUtils.createURL(address, value);
      
      Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
      String addressHome = source.getHome()[0];
      value = urlUtils.createURL(new URL(addressHome), address);//href.getSource().getHome()[0]
      return urlUtils.getCanonical(value);
    } catch (Exception e) {
      value =  urlUtils.createURL(link.getAddress(), value);
      return urlUtils.getCanonical(value);
    }
  }
  
  public List<XMLResource> getResources() { return resources; }

}
