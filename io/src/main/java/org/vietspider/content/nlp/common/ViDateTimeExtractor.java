/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.nlp.common;

import java.util.Date;
import java.util.List;

import org.vietspider.bean.Meta;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.locale.DateTimeDetector;
import org.vietspider.locale.DetachDate;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 9, 2007  
 */
public final class ViDateTimeExtractor extends DateTimeDetector {
  
  public ViDateTimeExtractor() { 
    super();
  }
  
  public void removeDateTimeNode(List<HTMLNode> list, Meta meta) {
    HTMLNode lastNode = null;
    DetachDate lastDetachDate = null;
    int lastEnd = -1;
    StringBuilder lastBuilder = null;
    Date date = null;
    
    HTMLText htmlText = new HTMLText();
    for(int i = 0; i < list.size(); i++) {
      if(i >= 10 && i < list.size() - 3) {
        i = list.size() - 4;
        continue;
      }
      
      HTMLNode node = upToParent(list.get(i));
      StringBuilder builder = new StringBuilder();
      htmlText.buildText(builder, node);
//      nodeHandler.buildContent(builder, node);
            
      String value = null;
      int end = -1;
      if(builder.length() > 0 && builder.charAt(0) == '(') {
        end = builder.indexOf(")", 1);
        if(end > -1) value  = builder.substring(1, end);
      } 
      if(value == null) value = builder.toString();
      if(value.length() >= 100) continue;
      value = value.toLowerCase();
      
      int idx = value.indexOf(longAgo);
      if(idx > -1 && hasTimeWord(value)) {
        date = computeDate(value.substring(idx));
        lastEnd = end;
        lastBuilder = builder;
        lastNode = node;
        break;
      }
      
      DetachDate detachDate = detect(value);
      if(detachDate == null || 
          (lastDetachDate != null && lastDetachDate.getScrore() >= detachDate.getScrore())) continue;
      lastDetachDate = detachDate;
      lastNode = node;
      lastEnd = end;
      lastBuilder = builder;     
    }
    if(lastNode == null) return;
    
    if(date == null && lastDetachDate != null) date = lastDetachDate.toDate();
    if(date != null) {
      meta.setSourceTime(CalendarUtils.getDateTimeFormat().format(date));
    }
    
    NodeHandler nodeHandler = new NodeHandler();
    if(lastEnd > -1) {
      String value = lastBuilder.substring(lastEnd+1);
      lastNode.setValue(value.toCharArray());
    } else {
      nodeHandler.removeNode(lastNode);
    }
//    System.out.println(lastNode.getTextValue());
    nodeHandler.removeContent(lastNode.iterator(), list);
  }
  
  private HTMLNode upToParent(HTMLNode node) {
    HTMLNode parent = node.getParent();
    if(parent == null) return node;
    List<HTMLNode> children = parent.getChildren();
    int i = 0;
    for(; i < children.size(); i++ ){
      if(children.get(i) == node) break;
    }
    if(i < children.size()-1 && children.get(i+1).getName() == Name.SUP) return parent;
    return node;
  }  
  
}
