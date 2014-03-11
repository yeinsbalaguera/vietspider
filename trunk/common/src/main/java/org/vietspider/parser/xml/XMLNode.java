/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.parser.xml;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.SpecChar;
import org.vietspider.chars.XMLDataEncoder;
import org.vietspider.token.Node;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;
import org.vietspider.token.attribute.Attributes;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2006
 */
public class XMLNode implements Node<String> {

  protected XMLNode parent;   
  protected char [] value ;
  protected String name;  

  protected List<XMLNode> children;
  
  private transient boolean isOpen = false;
  private transient int type = TypeToken.CONTENT;
  
  public XMLNode(char[] name){
    this(name, new String(name), TypeToken.TAG);
  }
  
  public XMLNode(String name, int type){
    this(name.toCharArray(), name, type);
  }
  
  public XMLNode(String name){
    this(name.toCharArray(), name, TypeToken.TAG);
  }
  
  
  public XMLNode(char [] value, String name){
    this(value, name, TypeToken.TAG);
  } 
  
  public XMLNode(char [] value, String name, int type){  
    this.value = value;
    this.name = name;
    this.type = type;
    children = new ArrayList<XMLNode>(); 
    isOpen = type == TypeToken.TAG;
  }  

  public void setParent(XMLNode p){   this.parent = p; }

  public XMLNode getParent(){ return this.parent; }  

  public void addChild(XMLNode ele){
    children.add(ele);
    ele.setParent(this);
  }
  public List<XMLNode> getChildren() { return children; }
  
  public XMLNode addChild(String _name){
    XMLNode node = new XMLNode(_name);
    children.add(node);
    node.setParent(this);
    return node;
  }
  
  public XMLNode addChild(String _name, String[][]attrs){
    XMLNode node = new XMLNode(_name);
    Attributes attributes = AttributeParser.getInstance().get(node);
    for(int i = 0; i < attrs.length; i++) {
      attributes.add(new Attribute(attrs[i][0], attrs[i][1]));
    }
    children.add(node);
    node.setParent(this);
    return node;
  }
  
  public XMLNode addTextChild(String _name, String text){
    XMLNode node = new XMLNode(_name);
    XMLNode textNode = new XMLNode(text.toCharArray(), null, TypeToken.CONTENT);
    node.addChild(textNode);
    children.add(node);
    node.setParent(this);
    return node;
  }
  
  public int getTotalChildren(){ return children.size(); }

  public char[] getValue() { return value; }

  public void setValue(char[] value) { this.value = value; }

  public String getName() { return name; }

  public void setName(String name) { this.name = name; }
  
  public boolean isNode(String n){
    if(name == null) return false;
    return name.equalsIgnoreCase(n);
  }

  boolean isOpen() { return isOpen; }

  public void setIsOpen(boolean open) { isOpen = open; }
  
  public int getType() { return type; }
  
  public String getTextValue(){
    return getTextValue(null);
  }
  
  public String getTextValue(XMLDataEncoder encoder){
    StringBuilder builder = new StringBuilder();
    buildValue(builder, 0, encoder);
    return builder.toString();
  }
  
  public XMLNode getChild(int i){
    if(children == null) return null;
    if(i < 0 || i >= children.size()) return null;
    return children.get(i); 
  }
  
  public int childIndex(XMLNode child){
    if(children == null) return -1;
    for(int i = 0; i < children.size(); i++) {
      if(child == children.get(i)) return i;
    }
    return -1;
  }
  
  public String getNodeValue(){   return new String(value);  }
  
  public void buildValue(StringBuilder builder, XMLDataEncoder encoder){
    buildValue(builder, 0, encoder);
  }

  private void buildValue(StringBuilder builder, int tab, XMLDataEncoder encoder){    
    if(tab > 0) builder.append(SpecChar.n);
    for(int  i = 0 ; i < tab ; i++) builder.append(SpecChar.s);
    if(type != TypeToken.CONTENT && type != TypeToken.COMMENT) builder.append('<');
    if(encoder != null && (type == TypeToken.CONTENT || type == TypeToken.COMMENT)) {
      value = encoder.encode(value);
    }
    builder.append(value);
    
    if(type != TypeToken.CONTENT && type != TypeToken.COMMENT) builder.append('>');
    
    if(children == null ) return ;
    if(children.size() == 1  
        && children.get(0).getType() != TypeToken.TAG 
        && children.get(0).value.length < 100) tab = -3;
    for(XMLNode ele : children){
      ele.buildValue(builder, tab+2, encoder);
    }
    if(type == TypeToken.TAG){
      if(tab > -1) builder.append(SpecChar.n);
      for(int  i = 0 ; i < tab ; i++) builder.append(SpecChar.s);
      builder.append('<').append('/').append(getName()).append('>');
    }
    return ;
  }
  
  public boolean isTag() {
    return type != TypeToken.CONTENT && type != TypeToken.COMMENT; 
  }
  
  public String name() { return name; }

}
