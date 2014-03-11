/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.bean.Meta;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.CrawlingSources;
import org.vietspider.crawl.plugin.PluginData;
import org.vietspider.handler.XMLResource;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.path2.NodePath;
import org.vietspider.js.JsUtils;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Unknown2XML;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 27, 2008  
 */
public class DocumentHandler extends XMLDataHandler {

  public DocumentHandler(Source source) {
    super(source);
  }

  public XMLDocument buildDocument(PluginData pluginData) {
    documentId = pluginData.getMeta().getId();

    HTMLDocument document = pluginData.getLink().getDocument();
    Meta meta = pluginData.getMeta();
    this.link = pluginData.getLink();

    XMLNode xmlRoot = new XMLNode("document", TypeToken.TAG);
    XMLDocument xmlDocument = new XMLDocument(xmlRoot);

    XMLNode xmlContentNode = new XMLNode("content", TypeToken.TAG);
    xmlRoot.addChild(xmlContentNode);

    HTMLNode root = document.getRoot();
    Source source = CrawlingSources.getInstance().getSource(link.getSourceFullName());
    Region [] regions = source.getProcessRegion();

    for(int i = 0; i < regions.length; i++) {
      String key = regions[i].getName().toLowerCase();
      
      if(key.endsWith("đính kèm")) {
        searchFileNode(root, regions[i]);
        continue;
      }
      
      String [] contents = createNode(root, regions[i], xmlRoot);
//      if(key.endsWith("đính kèm")) {
//        content = createFileNode(root, regions[i], xmlRoot);
//      } else {
//        content = createNode(root, regions[i], xmlRoot);
//      }
      
      if(key.endsWith("số ký hiệu")) {
        meta.setTitle(contents[0].trim());
      } else if(key.toLowerCase().endsWith("trích yếu")) {
        meta.setDesc(contents[0].trim());
      }
    }

    createSourceNode(xmlRoot, pluginData.getLink().getAddress());
    
    xmlRoot.setIsOpen(false);

    if(meta.getTitle()  == null || meta.getTitle().isEmpty())  return null;
    
    return xmlDocument;
  }


  protected XMLNode createPropertyNode(XMLNode parent, String name, String value) {
    String lower = name.toLowerCase();
    if(lower.endsWith("ngôn ngữ")) {
      lower = value.toLowerCase().trim();
      if(lower.indexOf("việt") > -1
          || "vietnamese".equals(lower)) {
        value = "vi"; 
      } else  if(lower.indexOf("trung") > -1
          || "chinese".equals(lower)) {
        value = "zh";
      } else  if(lower.indexOf("anh") > -1
          || "english".equals(lower)) {
        value = "en";
      } else {
        value = "vi"; 
      }
      return super.createTextNode(parent, "Ngôn ngữ", value);
    }  else if(lower.endsWith("phân loại")) {
      return super.createTextNode(parent, name, buildCategories(value));
    }
    return super.createTextNode(parent, name, value);
  }
  
  private String buildCategories(String value) {
    String [] elements = value.split("»");
    StringBuilder builder = new StringBuilder();
    for(String element : elements) {
      if(element.trim().isEmpty()) continue;
      if(builder.length() > 0) builder.append('/');
      builder.append(element.trim());
    }
    return builder.toString();
  }

  protected String[] createNode(HTMLNode root, Region region, XMLNode xmlRoot) {
//    String [] contents = new String[]{""};
    try {
      int type  = region.getType();
      if(type == Region.FILE) type = Region.TEXT;
      Object value = htmlUtil.lookupTextValue2(root, region, link.getAddress());
      if(value instanceof String[]) {
        String [] contents = (String []) value; 
        createTextNode(xmlRoot, region.getName(), contents);
        return contents;
      } else if (value instanceof XMLResource[]){
        createResources((XMLResource[]) value, link.getAddress());
        return new String[]{""};
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(link.getSourceFullName(), e);
    }
    
    String [] contents = new String[]{""};
    createTextNode(xmlRoot, region.getName(), contents);
    return contents;
  }
  
  protected void searchFileNode(HTMLNode root, Region region) {
    try {
      NodePath [] nodePaths = region.getNodePaths();
      if(nodePaths == null) return;
      List<HTMLNode> nodes = htmlUtil.getExtractor().matchNodes(root, nodePaths);
      ArrayList<XMLResource> resources = new ArrayList<XMLResource>();
      for(int k = 0; k < nodes.size(); k++) {
        searchResources(resources, nodes.get(k));
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(link.getSourceFullName(), e);
    }
    
//    XMLNode xmlPropertyNode = new XMLNode("property", TypeToken.TAG);
//
//    XMLNode xmlNameNode = new XMLNode("name", TypeToken.TAG);
//    xmlNameNode.addChild(new XMLNode(region.getName().toCharArray(), null, TypeToken.CONTENT));
//    xmlNameNode.setIsOpen(false);
//    xmlPropertyNode.addChild(xmlNameNode);
//
//    XMLNode xmlValueNode = new XMLNode("value", TypeToken.TAG);
//    try {
//      xmlValueNode.addChild(createResources(link.getAddress()));//pluginData.getLink().getAddress()
//    } catch (Exception e) {
//      LogService.getInstance().setThrowable(source, e);
//    }
//    xmlValueNode.setIsOpen(false);
//    xmlPropertyNode.addChild(xmlValueNode);
//
//    xmlPropertyNode.setIsOpen(false);
//    xmlRoot.addChild(xmlPropertyNode) ;
//    
//    return "";
  }

  protected void searchResources(ArrayList<XMLResource> resources, HTMLNode root) {
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();

      if(!n.isNode(Name.A)) continue;
      Attributes attributes = n.getAttributes(); 
      Attribute attr ;
      if((attr = attributes.get("href")) == null) continue;
      String address  = attr.getValue();
      if(address == null || address.length() < 1) continue;
      
      if(address.toLowerCase().startsWith("javascript")) {
        String [] params = JsUtils.getParams(address);
        for(String param : params) {
          String id  = buildResourceId();
          String name  = getName(n, param);
          if(name == null) name = id;
          resources.add(new XMLResource(id, name, param));
        }
      } else {
        String id  = buildResourceId();
        String name  = getName(n, address);
        if(name == null) name = id;
        resources.add(new XMLResource(id, name, address));
      }
    }
  }
  
  public XMLNode createResources(XMLResource[] resources, String address) throws Exception {
    XMLNode xmlResourcesNode = new XMLNode("resources", TypeToken.TAG);
    if(resources != null) {
      for(int i = 0; i < resources.length; i++) {
        XMLNode node = new XMLNode("resource", TypeToken.TAG);
        XMLResource resource = resources[i];
        resource.setLink(createLink(address, resource.getLink()));
        Unknown2XML.getInstance().toXML(resource, node);
        node.setIsOpen(false);
        xmlResourcesNode.addChild(node);
      }
    }
//    xmlSourceNode.addChild(new XMLNode(chars, null, TypeToken.CONTENT));
    xmlResourcesNode.setIsOpen(false);
    return xmlResourcesNode;
  }
}


