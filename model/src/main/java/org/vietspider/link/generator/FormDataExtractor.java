/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;

import java.util.List;

import org.vietspider.crawl.link.ILink;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.net.client.WebClient;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 15, 2009  
 */
public class FormDataExtractor implements UpdateDocument {
  
  private String sourceFullName;
  
  @SuppressWarnings("unused")
  public FormDataExtractor(String sourceFullName, String...values) throws Exception {
    this.sourceFullName = sourceFullName;
  }
  
  @SuppressWarnings("unused")
  public void generate(ILink ilink, WebClient webClient, HTMLDocument document) {
    update(document.getRoot());
  }
  
  private void update(HTMLNode node) {
    List<HTMLNode> children  = node.getChildren();
    if(children == null) return;
    for(int i = 0; i < children.size(); i++) {
      HTMLNode child = children.get(i);
      if(child.isNode(Name.INPUT)) {
        Attributes attributes = child.getAttributes();
        Attribute attribute = attributes.get("value");
        NodeImpl p = new NodeImpl("P".toCharArray(), Name.P, TypeToken.TAG);
        if(attribute != null && attribute.getValue() != null) {
          String value = attribute.getValue();
          if(value.trim().length() > 0) {
            HTMLNode content = new NodeImpl(value.toCharArray(), Name.CONTENT, TypeToken.CONTENT);
            p.addChild(content);           
          }
        } 
        p.setIsOpen(false);
        node.setChild(i, p);
      } if(child.isNode(Name.TEXTAREA)) {
        NodeImpl p = new NodeImpl("P".toCharArray(), Name.P, TypeToken.TAG);
        if(child.getChildren() != null && child.getChildren().size() > 0) {
          char [] chars = child.getChild(0).getTextValue().toCharArray();
          HTMLNode content = new NodeImpl(chars, Name.CONTENT, TypeToken.CONTENT);
          p.addChild(content);    
        }
        p.setIsOpen(false);
        node.setChild(i, p);
      } else {
        update(child);
      }
    }
  }
  
  public short getType() { return Generator.UPDATE_DOCUMENT_GENERATOR; }
  
}
