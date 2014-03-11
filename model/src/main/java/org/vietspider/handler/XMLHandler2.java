/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.handler;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.SpecChar;
import org.vietspider.chars.URLUtils;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.Application;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.Tag;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.model.Region;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.Unknown2XML;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 1, 2008  
 */
public class XMLHandler2 {
  
  protected HTMLExtractor extractor  = new HTMLExtractor(); 
  
  private char [] specials = { '&' };
  
  protected List<Resource> resources;
  
  protected String documentId = "xml";
  
  protected CompleteDocHandler completeDoc = new CompleteDocHandler();
  
  protected XMLNode createPropertyNode(XMLNode parent, String name, String...values) {
    boolean merge  = false;
    if(name.indexOf('/') > -1) {
      String [] elements = name.split("/");
      XMLNode root = upRoot(parent);
      XMLNode node = searchChild(root, elements[0]);
      for(int i = 1; i < elements.length - 1; i++) {
        node = searchChild(node, elements[i]);
      }
      parent = node;
      merge  = parent.getTotalChildren() >= values.length;
      name = elements[elements.length-1];
    }
    
    XMLNode _return = null;
    
    for(int i = 0; i < values.length; i++) {
      if(merge) {
        XMLNode propNode = parent.getChild(i);
        createContentItems(propNode, name, values[i]);
        _return =  propNode;
      } else {
        XMLNode propNode = new XMLNode("property", TypeToken.TAG);
        createContentItems(propNode, name, values[i]);
        propNode.setIsOpen(false);
        parent.addChild(propNode);
        _return =  propNode;
      }
    }
    
    return _return;
  }
  
  private void createContentItems(XMLNode propNode, String name, String value) {
    XMLNode xmlNameNode = new XMLNode("name", TypeToken.TAG);
    xmlNameNode.addChild(new XMLNode(name.toCharArray(), null, TypeToken.CONTENT));
    xmlNameNode.setIsOpen(false);
    propNode.addChild(xmlNameNode);

    XMLNode xmlValueNode = new XMLNode("value", TypeToken.TAG);
    if(value != null) {
      xmlValueNode.addChild(new XMLNode(value.toCharArray(), null, TypeToken.CONTENT));
    }
    xmlValueNode.setIsOpen(false);
    propNode.addChild(xmlValueNode);
  }
  
  private XMLNode searchChild(XMLNode parent, String name) {
    List<XMLNode> children = parent.getChildren();
    if(children == null) return null;
    for(int i = 0; i < children.size(); i++) {
      XMLNode node = children.get(i); 
      if(!node.isNode("property")) continue;
      if(!node.getChild(0).isNode("name")) continue;
      if(node.getChild(0).getChildren() == null 
          || node.getChild(0).getChildren().size() < 1) continue;
      if(!node.getChild(0).getChild(0).getTextValue().equalsIgnoreCase(name)) continue;
      if(node.getChildren().size() > 1) {
        return node.getChild(1);
      }
      node.getChildren().clear();
      createContentItems(node, name, null);
      return node.getChild(1);
    }
    
    XMLNode xmlPropertyNode = createPropertyNode(parent, name, new String[]{null});
    return xmlPropertyNode.getChild(1);
  }
  
  protected XMLNode upRoot(XMLNode node) {
    if(node.getParent() == null) return node;
    return upRoot(node.getParent());
  }
  
  protected String[] lookupTextValue2(HTMLNode root, NodePath[] paths, int type, String address) {
    if(paths == null) return new String[]{""};
    
    List<String> values = new ArrayList<String>();

    List<HTMLNode> nodes = extractor.matchNodes(root, paths);
    if(type == Region.TEXT) {
      HTMLText textUtils = new HTMLText();
      for(int i = 0; i < nodes.size(); i++) {
        StringBuilder builder = new StringBuilder();
        textUtils.buildText(builder, nodes.get(i));
        values.add(encode(builder.toString()));
      }
    } else if(type == Region.CDATA) {
      for(int i = 0; i < nodes.size(); i++) {
        StringBuilder builder = new StringBuilder();
        builder.append("<![CDATA[");
        if(nodes.get(i) == null) continue;
        if(address != null) {
          completeDoc.completeURL(address, nodes.get(i));
        }
        buildHTMLNode(new RefsDecoder(), builder, nodes.get(i), 0);
        builder.append("]]>");
        values.add(encode(builder.toString()));
      }
    } else if(type == Region.FILE) { 
      for(int i = 0; i < nodes.size(); i++) {
        searchResources(nodes.get(i));
      }
    } else {
      int counter =  countBlockText(nodes);
      if(counter > 1) {
        for(int i = 0; i < nodes.size(); i++) {
          StringBuilder builder = new StringBuilder();
          builder.append("<![CDATA[");
          if(nodes.get(i) == null) continue;
          if(address != null) {
            completeDoc.completeURL(address, nodes.get(i));
          }
          buildHTMLNode(new RefsDecoder(), builder, nodes.get(i), 0);
          builder.append("]]>");
          values.add(encode(builder.toString()));
        }
      } else {
        HTMLText textUtils = new HTMLText();
        for(int i = 0; i < nodes.size(); i++) {
          StringBuilder builder = new StringBuilder();
          textUtils.buildText(builder, nodes.get(i));
          values.add(encode(builder.toString()));
        }
      }
    }
    return values.toArray(new String[values.size()]);
  }
  
  protected String encode(String value) {
    StringBuilder builder = new StringBuilder();
    
    int index = 0;
    while(index < value.length()){
      char c = value.charAt(index) ;
      boolean enc = false;
      for(int i = 0; i < specials.length; i++) {
        if(specials[i] == c) {
          enc = true;
          break;
        }
      }
      builder.append(enc ? encode(c) : c);  
      index++;
    }
    return builder.toString();
  }
  
  private String encode(char c) {
    StringBuffer buffer = new StringBuffer();
    buffer.append ("&#").append(String.valueOf((int)c)).append (';');
    return buffer.toString();
  }
  
  public void buildHTMLNode(RefsDecoder decoder, 
      StringBuilder builder, HTMLNode node, int tab){
//  if(value.length < 1) return builder;
    if(builder.length() > 0) builder.append(SpecChar.n);
    Name name  = node.getName();
    NodeImpl nodeImpl = (NodeImpl)node; 
    int type  = nodeImpl.getType();
    boolean isTag = node.isTag();
    
    for(int i = 0; i < tab; i++) {
      builder.append(SpecChar.s);  
    }
    
    if(isTag || node.getName() == Name.UNKNOWN) builder.append('<');
    if(type == TypeToken.CLOSE) builder.append('/');
    builder.append(decoder.decode(node.getValue()));
    if(isTag || node.getName() == Name.UNKNOWN) builder.append('>');
    if(type == TypeToken.CLOSE || nodeImpl.getConfig().hidden())  return;

    List<HTMLNode> children = node.getChildren();
    if(children == null ) return;
    for(HTMLNode child : children) {
      buildHTMLNode(decoder, builder, child, tab+2);
    }
    
    if(nodeImpl.getConfig().end() != Tag.FORBIDDEN){
      builder.append(SpecChar.n);
      for(int i = 0; i < tab; i++) {
        builder.append(SpecChar.t);  
      }
      builder.append('<').append('/').append(name).append('>');
    }
  }
  
  protected int countBlockText(List<HTMLNode> list) {
    int counter = 0;
    for(int i = 0; i < list.size(); i++) {
      counter += countBlockText2(list.get(i));
    }
    return counter;
  }
  
/*  private int countBlockText(HTMLNode root) {
    int counter = 0;
    
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      switch (node.getName()) {
      case CONTENT:
        String text = node.getTextValue();
        if(text != null && !text.trim().isEmpty()) counter++;
        break;
      case IMG:
        break;
      case FONT:
      case B:
      case STRONG:
      case I:
      case A:
      case STRIKE:
      case SUP:
      case SUB:
      case SCRIPT:
      case STYLE:
        NodeImpl nodeImpl = (NodeImpl) node;
        if(nodeImpl.getType() == TypeToken.TAG && iterator.hasNext()) iterator.next();
        break;
      default:
      break;
      }
    }
    
    return counter;
  }*/
  
  protected int countBlockText2(HTMLNode root) {
    int counter = 0;
    List<HTMLNode> children = root.getChildren();
    if(children == null) return counter;
    for(int i = 0; i < children.size(); i++) {
      HTMLNode node = children.get(i);
      if(node.isNode(Name.TD) 
          || node.isNode(Name.DIV)
          || node.isNode(Name.P)) {
        if(!hasContainer(node)) {
          if(hasText(node)) counter++;
          continue;
        }
      }
      counter += countBlockText2(node);
    }
    
    return counter;
  }
    
  private boolean hasContainer(HTMLNode root) {
    NodeIterator iterator = root.iterator();
    iterator.next();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(node.isNode(Name.TD) 
          || node.isNode(Name.DIV)
          || node.isNode(Name.P)) return true;
    }
    return false;
  }
  
  private boolean hasText(HTMLNode root) {
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(node.isNode(Name.CONTENT)) return true;
    }
    return false;
  }
  
  public void createSourceNode(XMLNode xmlRoot, String address) {
    XMLNode xmlSourceNode = new XMLNode("src", TypeToken.TAG);
    char [] chars = ("<![CDATA[" + address+ "]]>").toCharArray();
    xmlSourceNode.addChild(new XMLNode(chars, null, TypeToken.CONTENT));
    xmlSourceNode.setIsOpen(false);
    xmlRoot.addChild(xmlSourceNode);
  }
  
  public XMLNode createResources(String address) throws Exception {
    XMLNode xmlResourcesNode = new XMLNode("resources", TypeToken.TAG);
    if(resources != null) {
      for(int i = 0; i < resources.size(); i++) {
        XMLNode node = new XMLNode("resource", TypeToken.TAG);
        Resource resource = resources.get(i);
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
  
  protected void searchResources(HTMLNode root) {
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      
      if(n.isNode(Name.A)) {
        Attributes attributes = n.getAttributes(); 
        Attribute attr ;
        if((attr = attributes.get("href")) == null) continue;
        String address  = attr.getValue();
        if(address == null || address.length() < 1) continue;
        
        String id  = buildResourceId();
        
        String name  = getName(n, address);
        if(name == null) name = id;
        
        if(resources == null) resources = new ArrayList<Resource>();
        resources.add(new Resource(id, name, address));
      } else if(n.isNode(Name.IMG )) {
        Attributes attributes = n.getAttributes(); 
        Attribute attr ;
        if((attr = attributes.get("src")) == null) continue;
        String address  = attr.getValue();
        if(address == null || address.length() < 1) continue;
        
        String id  = buildResourceId();
        
        String name  = getName(null, address);
        if(name == null) name = id;
        
        if(resources == null) resources = new ArrayList<Resource>();
        resources.add(new Resource(id, name, address));
      }
    }
  }
  
  protected String buildResourceId() {
    if(resources == null) return "no_id";
    StringBuilder builder  = new StringBuilder(documentId).append('.').append(resources.size()+1);
    return builder.toString();
  }
  
  protected String getName(HTMLNode node, String address) {
    String name = null;
    if(node != null) {
      HTMLText textUtils = new HTMLText();
      StringBuilder builder = new StringBuilder();
      textUtils.buildText(builder, node);
      name  =  builder.toString();

      NodeHandler nodeHandler = new NodeHandler();
      try {
        if(nodeHandler.count(name) < 10) return rename(name);
      } catch (Exception e) {
      }
    }
      
    if(address.indexOf('/') > -1) {
      name = address.substring(address.lastIndexOf('/')+1);
    } else {
      name = address;
    }
    
    if(name.indexOf('?') > -1) name = name.substring(0, name.indexOf('?'));
    
    try{
      name = rename(name);
    } catch (Exception e) {
      return null;
    }
    return name;
  }
  
  protected String rename(String name) throws Exception {
    char [] chars = URLDecoder.decode(name, Application.CHARSET).toCharArray();
    char [] specs = {'-', '\\', '?', '|', '"', '=', '<', '>'};
    int i = 0;
    while(i < chars.length){
      for(char c : specs){
        if(chars[i] != c) continue;
        chars[i] = '.';
        break;
      }
      i++;
    }
    return new String(chars);
  }
  
  @NodeMap("resource")
  public static class Resource {
    
    @NodeMap("id")
    private String id;
    
    @NodeMap(value = "link", cdata = true)
    private String link;
    
    @NodeMap("name")
    private String name;
    
    public Resource() {
    }
    
    public Resource(String id, String name, String link) {
      this.id = id;
      this.link = link;
      this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

  }
  
  public String createLink(String refer, String link) {
    URLUtils urlUtils = new URLUtils();
    if(refer.startsWith("http://")
        || refer.startsWith("shttp://") 
        || refer.startsWith("https://")) {
      try {
        return urlUtils.createURL(new URL(refer), link);
      } catch (Exception e) {
      }
    } 
    refer = urlUtils.createURL(refer, link);
    return refer;
  }

  public List<Resource> getResources() { return resources; }

}
