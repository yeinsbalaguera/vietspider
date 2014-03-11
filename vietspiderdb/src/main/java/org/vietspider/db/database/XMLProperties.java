/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.db.database;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.token.TypeToken;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 29, 2006
 */
@SuppressWarnings("serial")
public class XMLProperties extends  HashMap<String, String> {
  
  protected XMLDocument document;
  protected XMLNode node_ ;  
  
  private File file_;
  private String name_;
  private boolean readonly_;
  
  public XMLProperties()  {
    
  }

  public XMLProperties(InputStream input, String name, boolean readonly) throws Exception {
    toProperties(XMLParser.createDocument(input, Application.CHARSET, new RefsDecoder()), name, readonly);
  }
  
  public XMLProperties(File file, String name, boolean readonly) throws Exception {
    toProperties(XMLParser.createDocument(file, Application.CHARSET, new RefsDecoder()), name, readonly);
    this.file_ = file;
    this.name_ = name;
    this.readonly_ = readonly;
  }
  
  public void reset() throws Exception {
    XMLDocument doc  = XMLParser.createDocument(file_, Application.CHARSET, new RefsDecoder());
    toProperties(doc, name_, readonly_);
  }
  
  public String get(Object key){
    String value = super.get(key);
    if(value != null && value.trim().length() > 0)  return value;
    try {
      reset();
      value = super.get(key);
    }catch(Exception exp){
      LogService.getInstance().setThrowable(exp);
    }
    return value;
  }
  
  public void toProperties(XMLDocument xmlDocument, String name, boolean readonly) throws Exception {
    XMLNode node = null;
    XMLNode root = xmlDocument.getRoot();
    if (root.getChildren().size() > 0) {
      node = getNode(root, name);
    }
    if(!readonly) {
      this.node_ = node;
      this.document = xmlDocument;
      if(node_ == null){
        node_ = new XMLNode(name.toCharArray(), name, TypeToken.TAG);
        document.getRoot().addChild(node_);        
      }
    }

    if(node == null) return;
    List<XMLNode> children = node.getChildren();
    if(children == null) return;
    for(XMLNode child : children){
      String value = null;
      if(child.getChildren().size() > 0) value = child.getChild(0).getTextValue();
      super.put(child.getNodeValue(), value);
    }    
  }
  
  public byte [] getBytes() throws Exception {
    if(document == null) return null;
    Iterator<String>  iter = keySet().iterator();
    node_.getChildren().clear();
    while(iter.hasNext()){
      String key  = iter.next();
      XMLNode ele = new XMLNode(key.toCharArray(), key, TypeToken.TAG);
      XMLNode child = new XMLNode(get(key).toCharArray(), null, TypeToken.CONTENT);
      ele.addChild(child);
      node_.addChild(ele);
    }
    return document.getTextValue().getBytes(Application.CHARSET);
  }
  
  public OutputStream toOutputStream() throws Exception {
    if(document == null) return null;
    ByteArrayOutputStream output = new ByteArrayOutputStream();    
    output.write(getBytes());
    return output;
  }
  
  public XMLNode getNode(String name) {
    return getNode(document.getRoot(), name);
  }
  
  protected XMLNode getNode(XMLNode root, String name) {
    if(root.getName() == null) return null;
    if(root.getName().equalsIgnoreCase(name)) return root;
    List<XMLNode> children = root.getChildren();
    for(XMLNode child : children){
      XMLNode value = getNode(child, name);
      if(value != null) return value;
    }
    return null;
  }  
}
