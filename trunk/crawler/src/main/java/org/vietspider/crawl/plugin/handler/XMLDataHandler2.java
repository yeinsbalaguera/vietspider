/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import org.vietspider.bean.Meta;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.link.Link;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.handler.XMLHandler3;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.NodePath;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.token.TypeToken;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 5, 2010  
 */
public class XMLDataHandler2 extends XMLHandler3 {
  
  protected Source source;
  protected Link link;

  public XMLDataHandler2(Source source) {
    this.source = source;
  }

  public String handle(PluginData pluginData) {
    XMLDocument xmlDocument = buildDocument(pluginData);
    if (xmlDocument == null) return null;
    return xmlDocument.getTextValue();
  }

  public XMLDocument buildDocument(PluginData pluginData) {
    this.resources = new ArrayList();
    this.documentId = pluginData.getMeta().getId();

    HTMLDocument document = (HTMLDocument)pluginData.getLink().getDocument();
    Meta meta = pluginData.getMeta();
    this.link = pluginData.getLink();

    XMLNode xmlRoot = new XMLNode("document", TypeToken.TAG);
    XMLDocument xmlDocument = new XMLDocument(xmlRoot);

    XMLNode xmlContentNode = new XMLNode("content", TypeToken.TAG);
    xmlRoot.addChild(xmlContentNode);

    HTMLNode root = document.getRoot();
    Region[] regions = this.source.getProcessRegion();

    for (int i = 0; i < regions.length; i++) {
      String key = regions[i].getName();
      String content = "";
      try {
        NodePath[] nodePaths = regions[i].getNodePaths();
        content = lookupTextValue2(root, nodePaths, regions[i].getType(), this.link.getAddress());
      } catch (Exception e) {
        LogService.getInstance().setThrowable(this.source, e);
        content = "";
      }
      xmlRoot.addChild(createPropertyNode(key, content));

      if ((regions[i].getType() == 1) || (regions[i].getType() == 0)) {
        if ((meta.getTitle() == null) || (meta.getTitle().isEmpty()))
          meta.setTitle(content.trim());
        else if ((meta.getDesc() == null) || (meta.getDesc().isEmpty())) {
          meta.setDesc(content.trim());
        }
      }
    }
    
    try {
      xmlRoot.addChild(createResources(pluginData.getLink().getAddress()));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(this.source, e);
    }
    createSourceNode(xmlRoot, pluginData.getLink().getAddress());

    xmlRoot.setIsOpen(false);

    if ((meta.getDesc() == null) || (meta.getDesc().isEmpty())) {
      if(meta.getTitle().startsWith("default title")) {
        LogService.getInstance().setMessage(null, "Ignore "+ link.getAddress() +" - Error: Empty data!");
        return null;
      }
      meta.setDesc("empty description");
    }

    return xmlDocument;
  }

  public String createLink(String address, String value) {
    URLUtils urlUtils = new URLUtils();
    if (this.link.getBaseHref() != null) {
      try {
        return urlUtils.createURL(new URI(this.link.getBaseHref()).normalize().toURL(), value);
      } catch (Exception e) {
        return urlUtils.createURL(this.link.getBaseHref(), value);
      }
    }
    try
    {
      address = this.link.getAddress();
      if ((address.startsWith("http://")) || (address.startsWith("shttp://")) || (address.startsWith("https://")))
      {
        value = urlUtils.createURL(new URL(address), value);
        return urlUtils.getCanonical(value);
      }
      address = urlUtils.createURL(address, value);
      Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
      String addressHome = source.getHome()[0];
      value = urlUtils.createURL(new URL(addressHome), address);
      return urlUtils.getCanonical(value);
    } catch (Exception e) {
      value = urlUtils.createURL(this.link.getAddress(), value);
    }return urlUtils.getCanonical(value);
  }
}