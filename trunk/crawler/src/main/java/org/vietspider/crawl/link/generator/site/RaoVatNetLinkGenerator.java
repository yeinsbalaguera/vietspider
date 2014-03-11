/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.generator.site;

import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.link.generator.Generator;
import org.vietspider.model.Source;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 3, 2009  
 */
public class RaoVatNetLinkGenerator {
  
  protected Source source;
  @SuppressWarnings("unused")
  public RaoVatNetLinkGenerator(Source source, String...values) throws Exception {
    this.source = source;
  }
  
  public void generate(HTMLDocument doc, List<String> list) {
    NodeIterator iterator = doc.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.DIV)) continue;
      Attributes attributes = node.getAttributes(); 
      Attribute attribute = attributes.get("id");
      if(attribute == null) continue;
      String value = attribute.getValue();
      if(value == null) continue;
      value = value.trim();
      int idx = value.indexOf("url");
      if(idx < 0) continue;
      value = value.substring(idx+3);
      
      List<HTMLNode> children = node.getChildren();
      if(children == null || children.size() < 1) continue;
      String link = children.get(0).getTextValue().trim();
//      int idx = link.indexOf(',');
//      if(idx < 0) continue;
//      link = link.substring(idx+1);
      list.add("http://raovat.net/xem/" + value +"-"+ link);
    }
  }
  
  public short getType() { return Generator.DOCUMENT_GENERATOR; }

  public Source getSource() { return source; }
  
}
