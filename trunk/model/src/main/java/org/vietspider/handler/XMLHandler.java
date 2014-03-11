/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.handler;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.serialize.Unknown2XML;
import org.vietspider.token.TypeToken;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 1, 2008  
 */
public class XMLHandler extends XMLTextData {
  
  protected XMLNode createTextNode(XMLNode parent, String name, String...values) {
//    boolean merge  = false;
    if(name.indexOf('/') > -1) {
      String [] elements = name.split("/");
      XMLNode root = upRoot(parent);
      XMLNode node = searchChild(root, elements[0]);
//      System.out.println("search duoc "+ elements[0]+"/ "+ node);
      for(int i = 1; i < elements.length - 1; i++) {
        node = searchChild(node, elements[i]);
      }
      parent = node;
//      merge = parent.getTotalChildren() >= values.length;
      name = elements[elements.length-1];
    }
    
    XMLNode _return = null;
    
    int index = name.indexOf("[all]");
    if(index > -1) {
      StringBuilder nodeName = new StringBuilder();
      nodeName.append(name.substring(0, index));
      nodeName.append(name.substring(index+5));
      XMLNode node = new XMLNode(nodeName.toString(), TypeToken.TAG);
      StringBuilder nodeValue = new StringBuilder();
      for(int i = 0; i < values.length; i++) {
        if(nodeValue.length() > 0) nodeValue.append('\t');
        nodeValue.append(values[i]);
      }
      node.addChild(new XMLNode(nodeValue.toString().toCharArray(), null, TypeToken.CONTENT));
      node.setIsOpen(false);
      parent.addChild(node);
      _return =  node;
      return _return;
    }
    
    for(int i = 0; i < values.length; i++) {
      StringBuilder nodeName = new StringBuilder();
      index = name.indexOf("[index]");
      if(index > -1) {
        nodeName.append(name.substring(0, index));
        nodeName.append(i).append(name.substring(index+7));
      } else {
        nodeName.append(name);
      }
      XMLNode node = new XMLNode(nodeName.toString(), TypeToken.TAG);
      node.addChild(new XMLNode(values[i].toCharArray(), null, TypeToken.CONTENT));
      node.setIsOpen(false);
      parent.addChild(node);
      _return =  node;
    }
    
    if(values.length < 1) {
      StringBuilder nodeName = new StringBuilder();
      index = name.indexOf("[index]");
      if(index > -1) {
        nodeName.append(name.substring(0, index));
        nodeName.append(0).append(name.substring(index+7));
      } else {
        nodeName.append(name);
      }
      XMLNode node = new XMLNode(nodeName.toString(), TypeToken.TAG);
      node.addChild(new XMLNode(new char[0], null, TypeToken.CONTENT));
      node.setIsOpen(false);
      parent.addChild(node);
      _return =  node;
    }
    
    return _return;
  }
  
  protected void split(XMLNode parent) {
    List<XMLNode> children = parent.getChildren();
    if(children == null || parent.getName() == null) return;
    for(int i = 0; i < children.size(); i++) {
      split(children.get(i));
    }
    List<XMLNode> nodes = classify(parent);
    if(nodes == null || nodes.size() < 1) return;
    XMLNode ascendant = parent.getParent();
    int index = ascendant.childIndex(parent);
    if(index < 0) return;
    children = ascendant.getChildren();
    children.set(index, nodes.get(0));
    for(int i = 1; i < nodes.size(); i++) {
      children.add(index, nodes.get(i));
      index++;
    }
  }
  
  protected List<XMLNode> classify(XMLNode parent) {
    List<XMLNode> children = parent.getChildren();
    if(children == null) return null;
    List<Category> categories = new ArrayList<Category>();
//    System.out.println("================================================");
    for(int i = 0; i < children.size(); i++) {
      String name = children.get(i).getName();
//      System.out.println(parent.getName() + " : "+ name);
      if(name == null) continue;
      addNodeToCategories(categories, children.get(i));
    }
    
    if(categories.size() < 2) return null;
    
//    System.out.println(parent.getName() +  " : "+ categories.size());
    
    for(int i = 0; i < categories.size(); i++) {
      List<XMLNode> list1 = categories.get(i).nodes;
      if(list1.size() < 2) return null;
      for(int j = i + 1; j < categories.size(); j++) {
        List<XMLNode> list2 = categories.get(j).nodes;
        if(list1.size() != list2.size()) return null;
      }
    }
    
    List<XMLNode> _return = new ArrayList<XMLNode>();
    int index = 0;
    while(true) {
      XMLNode node = new XMLNode(parent.getName(), TypeToken.TAG);
//      System.out.println(index +  " : "+ values.get(0).size());
      for(int i = 0; i < categories.size(); i++) {
        List<XMLNode> list = categories.get(i).nodes;
        if(index >= list.size()) return _return;
        node.addChild(list.get(index));
      }
      node.setIsOpen(false);
      _return.add(node);
      index++;
    }
  }
  
  private void addNodeToCategories(List<Category> categories, XMLNode node) {
    for(int k = 0; k < categories.size(); k++) {
      if(categories.get(k).name.equals(node.getName())) {
        categories.get(k).nodes.add(node);
        return;
      }
    }
    Category category = new Category(node.getName());
    category.nodes.add(node);
    categories.add(category);
  }
  
  private XMLNode searchChild(XMLNode parent, String name) {
    List<XMLNode> children = parent.getChildren();
    if(children == null) return null;
    for(int i = 0; i < children.size(); i++) {
      XMLNode node = children.get(i); 
      if(node.isNode(name)) return node;
      /*node.getChildren().clear();
      System.out.println(" ta thay co cai ni "+ node.getName());
//      createContentItems(node, name, null);
      XMLNode newNode = new XMLNode(name, TypeToken.TAG);
      newNode.setIsOpen(false);
      node.addChild(newNode);*/
//      return node;
    }
    
    XMLNode newNode = createTextNode(parent, name, new String[]{""});
    return newNode;
  }
  
  protected XMLNode upRoot(XMLNode node) {
    if(node.getParent() == null) return node;
    return upRoot(node.getParent());
  }
  
  public void createSourceNode(XMLNode xmlRoot, String address) {
    XMLNode xmlSourceNode = new XMLNode("src", TypeToken.TAG);
    char [] chars = ("<![CDATA[" + address+ "]]>").toCharArray();
    xmlSourceNode.addChild(new XMLNode(chars, null, TypeToken.CONTENT));
    xmlSourceNode.setIsOpen(false);
    xmlRoot.addChild(xmlSourceNode);
  }
  
//  public List<XMLResource> getResources() { return resources; }

  public void createResources(String name, 
      XMLNode parent, XMLResource[] resources, String address) throws Exception {
    for(int i = 0; i < resources.length; i++) {
      StringBuilder nodeName = new StringBuilder();
      int index = name.indexOf("[index]");
      if(index > -1) {
        nodeName.append(name.substring(0, index));
        nodeName.append(i).append(name.substring(index+7));
      } else {
        nodeName.append(name);
      }
      XMLNode node = new XMLNode(nodeName.toString(), TypeToken.TAG);

      XMLResource resource = resources[i];
      String link  = resource.getLink();
      if(link.toLowerCase().startsWith("mailto")) {
      } else {
        link = createLink(address, resource.getLink());
      }
      resource.setLink(link);
      Unknown2XML.getInstance().toXML(resource, node);
      node.setIsOpen(false);
      parent.addChild(node);
    }
    //    xmlSourceNode.addChild(new XMLNode(chars, null, TypeToken.CONTENT));
  }
  
  public String buildTemplate(String template, XMLDocument document) {
    if(template == null) return document.getTextValue();
    List<XMLNode> nodes = document.getRoot().getChildren();
    int index = template.indexOf("$text");
    if(index < 0) return document.getTextValue();
    StringBuilder builder = new StringBuilder();
    builder.append(template.subSequence(0, index));
//    RefsEncoder encoder = new RefsEncoder();
    for(int i = 0; i < nodes.size(); i++) {
       builder.append('\n');
      nodes.get(i).buildValue(builder, null);
    }
    builder.append(template.subSequence(index+5, template.length()));
    return builder.toString();
  }
  
  private static class Category {
    
    private String name;
    private List<XMLNode> nodes = new ArrayList<XMLNode>();
    
    public Category(String name) {
      this.name = name;
    }
  }
}
