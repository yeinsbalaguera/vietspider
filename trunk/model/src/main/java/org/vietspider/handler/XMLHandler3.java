/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.handler;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.URLUtils;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.Tag;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.Unknown2XML;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 5, 2010  
 */
public class XMLHandler3 {
  
  protected HTMLExtractor extractor;
  private char[] specials;
  protected List<Resource> resources;
  protected String documentId;
  protected CompleteDocHandler completeDoc;

  public XMLHandler3() {
    this.extractor = new HTMLExtractor();

    this.specials = new char[] { '&' };

    this.documentId = "xml";

    this.completeDoc = new CompleteDocHandler();
  }
  protected XMLNode createPropertyNode(String name, String value) {
    XMLNode xmlPropertyNode = new XMLNode("property", TypeToken.TAG);

    XMLNode xmlNameNode = new XMLNode("name", TypeToken.TAG);
    xmlNameNode.addChild(new XMLNode(name.toCharArray(), null, TypeToken.CONTENT));
    xmlNameNode.setIsOpen(false);
    xmlPropertyNode.addChild(xmlNameNode);

    XMLNode xmlValueNode = new XMLNode("value", TypeToken.TAG);
    xmlValueNode.addChild(new XMLNode(value.toCharArray(), null, TypeToken.CONTENT));
    xmlValueNode.setIsOpen(false);
    xmlPropertyNode.addChild(xmlValueNode);

    xmlPropertyNode.setIsOpen(false);
    return xmlPropertyNode;
  }

  protected String lookupTextValue2(HTMLNode root, NodePath[] paths, int type, String address) {
    if (paths == null) return "";
    StringBuilder builder = new StringBuilder();
    
    RefsDecoder decoder = new RefsDecoder();

    List<HTMLNode> nodes = this.extractor.matchNodes(root, paths);
    if (type == 1) {
      HTMLText textUtils = new HTMLText();
      boolean separator = nodes.size() > 1;
      for (int i = 0; i < nodes.size(); i++) {
        if(separator) builder.append("<item>\n");
        textUtils.buildText(builder, nodes.get(i), decoder);
        if(separator) builder.append("</item>\n");
      }
    } else if (type == 2) {
      builder.append("<![CDATA[");
      for (int i = 0; i < nodes.size(); i++) {
        if (i > 0) builder.append('\n');
        if (nodes.get(i) != null) {
          if (address != null) {
            this.completeDoc.completeURL(address, nodes.get(i));
          }
          buildHTMLNode(decoder, builder, nodes.get(i), 0);
        }
      }
      builder.append("]]>");
    } else if (type == 3) {
      for (int i = 0; i < nodes.size(); i++) {
        searchResources(nodes.get(i));
      }
    } else {
      int counter = countBlockText(nodes);
      if (counter > 1) {
        builder.append("<![CDATA[");
        for (int i = 0; i < nodes.size(); i++) {
          if (i > 0) builder.append('\n');
          if (nodes.get(i) != null) {
            if (address != null) {
              this.completeDoc.completeURL(address, nodes.get(i));
            }
            buildHTMLNode(decoder, builder, nodes.get(i), 0);
          }
        }
        builder.append("]]>");
      } else {
        boolean separator = nodes.size() > 1;
        HTMLText textUtils = new HTMLText();
        for (int i = 0; i < nodes.size(); i++) {
          if(separator) builder.append("<item>\n");
          textUtils.buildText(builder, nodes.get(i), decoder);
          if(separator) builder.append("</item>\n");
        }
//        System.out.println("=========================================================");
//        System.out.println(builder);
      }
    }
    return encode(builder.toString());
  }

  protected String encode(String value) {
    StringBuilder builder = new StringBuilder();

    int index = 0;
    while (index < value.length()) {
      char c = value.charAt(index);
      boolean enc = false;
      for (int i = 0; i < this.specials.length; i++) {
        if (this.specials[i] == c) {
          enc = true;
          break;
        }
      }
      builder.append(enc ? encode(c) : Character.valueOf(c));
      index++;
    }
    return builder.toString();
  }

  private String encode(char c) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("&#").append(String.valueOf(c)).append(';');
    return buffer.toString();
  }

  public void buildHTMLNode(RefsDecoder decoder, StringBuilder builder, HTMLNode node, int tab)
  {
    if (builder.length() > 0) builder.append('\n');
    Name name = node.getName();
    NodeImpl nodeImpl = (NodeImpl)node;
    int type = nodeImpl.getType();
    boolean isTag = node.isTag();

    for (int i = 0; i < tab; i++) {
      builder.append(' ');
    }

    if ((isTag) || (node.getName() == Name.UNKNOWN)) builder.append('<');
    if (type == TypeToken.CLOSE) builder.append('/');
    builder.append(decoder.decode(node.getValue()));
    if ((isTag) || (node.getName() == Name.UNKNOWN)) builder.append('>');
    if ((type == TypeToken.CLOSE) || (nodeImpl.getConfig().hidden())) return;

    List<HTMLNode> children = node.getChildren();
    if (children == null) return;
    for (HTMLNode child : children) {
      buildHTMLNode(decoder, builder, child, tab + 2);
    }

    if (nodeImpl.getConfig().end() != Tag.FORBIDDEN) {
      builder.append('\n');
      for (int i = 0; i < tab; i++) {
        builder.append('\t');
      }
      builder.append('<').append('/').append(name).append('>');
    }
  }

  protected int countBlockText(List<HTMLNode> list) {
    int counter = 0;
    for (int i = 0; i < list.size(); i++) {
      counter += countBlockText2(list.get(i));
    }
    return counter;
  }

  protected int countBlockText2(HTMLNode root) {
    int counter = 0;
    List<HTMLNode> children = root.getChildren();
    if (children == null) return counter;
    for (int i = 0; i < children.size(); i++) {
      HTMLNode node = children.get(i);
      if ((node.isNode(Name.TD)) || (node.isNode(Name.DIV)) || (node.isNode(Name.P)))
      {
        if (!hasContainer(node)) {
          if (!hasText(node)) continue; counter++; continue;
        }
      }

      counter += countBlockText2(node);
    }

    return counter;
  }

  private boolean hasContainer(HTMLNode root) {
    NodeIterator iterator = root.iterator();
    iterator.next();
    while (iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if ((node.isNode(Name.TD)) || (node.isNode(Name.DIV)) || (node.isNode(Name.P)))
      {
        return true;
      }
    }
    return false;
  }

  private boolean hasText(HTMLNode root) {
    NodeIterator iterator = root.iterator();
    while (iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if (node.isNode(Name.CONTENT)) return true;
    }
    return false;
  }

  public void createSourceNode(XMLNode xmlRoot, String address) {
    XMLNode xmlSourceNode = new XMLNode("src", TypeToken.TAG);
    char[] chars = new StringBuilder().append("<![CDATA[").append(address).append("]]>").toString().toCharArray();
    xmlSourceNode.addChild(new XMLNode(chars, null, TypeToken.CONTENT));
    xmlSourceNode.setIsOpen(false);
    xmlRoot.addChild(xmlSourceNode);
  }

  public XMLNode createResources(String address) throws Exception {
    XMLNode xmlResourcesNode = new XMLNode("resources", TypeToken.TAG);
    if (this.resources != null) {
      for (int i = 0; i < this.resources.size(); i++) {
        XMLNode node = new XMLNode("resource", TypeToken.TAG);
        Resource resource = this.resources.get(i);
        resource.setLink(createLink(address, resource.getLink()));
        Unknown2XML.getInstance().toXML(resource, node);
        node.setIsOpen(false);
        xmlResourcesNode.addChild(node);
      }
    }

    xmlResourcesNode.setIsOpen(false);
    return xmlResourcesNode;
  }

  protected void searchResources(HTMLNode root) {
    NodeIterator iterator = root.iterator();
    while (iterator.hasNext()) {
      HTMLNode n = iterator.next();

      if (n.isNode(Name.A)) {
        Attributes attributes = n.getAttributes();
        Attribute attr;
        if ((attr = attributes.get("href")) != null) {
          String address = attr.getValue();
          if ((address == null) || (address.length() < 1))
            continue;
          String id = buildResourceId();

          String name = getName(n, address);
          if (name == null) name = id;

          if (this.resources == null) this.resources = new ArrayList<Resource>();
          this.resources.add(new Resource(id, name, address));
        }
      } else if (n.isNode(Name.IMG)) {
        Attributes attributes = n.getAttributes();
        Attribute attr;
        if ((attr = attributes.get("src")) != null) {
          String address = attr.getValue();
          if ((address == null) || (address.length() < 1))
            continue;
          String id = buildResourceId();

          String name = getName(null, address);
          if (name == null) name = id;

          if (this.resources == null) this.resources = new ArrayList<Resource>();
          this.resources.add(new Resource(id, name, address));
        }
      }
    }
  }

  protected String buildResourceId() {
    if (this.resources == null) return "no_id";
    StringBuilder builder = new StringBuilder(this.documentId).append('.').append(this.resources.size() + 1);
    return builder.toString();
  }

  protected String getName(HTMLNode node, String address) {
    String name = null;
    if (node != null) {
      HTMLText textUtils = new HTMLText();
      StringBuilder builder = new StringBuilder();
      textUtils.buildText(builder, node);
      name = builder.toString();

      NodeHandler nodeHandler = new NodeHandler();
      try {
        if (nodeHandler.count(name) < 10) return rename(name); 
      }
      catch (Exception e)
      {
      }
    }
    if (address.indexOf(47) > -1)
      name = address.substring(address.lastIndexOf(47) + 1);
    else {
      name = address;
    }

    if (name.indexOf(63) > -1) name = name.substring(0, name.indexOf(63));
    try
    {
      name = rename(name);
    } catch (Exception e) {
      return null;
    }
    return name;
  }

  protected String rename(String name) throws Exception {
    char[] chars = URLDecoder.decode(name, "UTF-8").toCharArray();
    char[] specs = { '-', '\\', '?', '|', '"', '=', '<', '>' };
    int i = 0;
    while (i < chars.length) {
      for (char c : specs)
        if (chars[i] == c) {
          chars[i] = '.';
          break;
        }
      i++;
    }
    return new String(chars);
  }

  public String createLink(String refer, String link)
  {
    URLUtils urlUtils = new URLUtils();
    if ((refer.startsWith("http://")) || (refer.startsWith("shttp://")) || (refer.startsWith("https://")))
    {
      try
      {
        return urlUtils.createURL(new URL(refer), link);
      } catch (Exception e) {
      }
    }
    refer = urlUtils.createURL(refer, link);
    return refer;
  }
  public List<Resource> getResources() {
    return this.resources;
  }

  @NodeMap("resource")
  public static class Resource
  {

    @NodeMap("id")
    private String id;

    @NodeMap(value="link", cdata=true)
    private String link;

    @NodeMap("name")
    private String name;

    public Resource()
    {
    }

    public Resource(String id, String name, String link)
    {
      this.id = id;
      this.link = link;
      this.name = name;
    }
    public String getId() { return this.id; } 
    public void setId(String id) { this.id = id; } 
    public String getLink() { return this.link; } 
    public void setLink(String link) { this.link = link; } 
    public String getName() { return this.name; } 
    public void setName(String name) { this.name = name;
    }
  }
}
