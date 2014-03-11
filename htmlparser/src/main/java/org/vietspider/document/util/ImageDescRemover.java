/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.document.util;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.util.HTMLText;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 28, 2008  
 */
public class ImageDescRemover extends NodeRemover {
  
  public List<HTMLNode> removeDesc(HTMLNode root) {
    List<HTMLNode> values = new ArrayList<HTMLNode>();
    
    HTMLText textUtils = new HTMLText();
    
    List<HTMLNode> images = nodeUtil.search(root, Name.IMG);
    for(int i = 0; i < images.size(); i++) {
      HTMLNode image = images.get(i);
      HTMLNode parent  = searchUpper(image, Name.TABLE);
      if(parent != null) {
        textUtils.searchText(values, handleTable(parent, image));
//        addValues(handleTable(parent, image), values);
        continue;        
      }
      
      parent  = searchUpper(image, Name.DIV, Name.CENTER);
      if(parent != null) {
        if(isValidText(parent, 3)) {
          textUtils.searchText(values, parent);
//          addValues(parent, values);
        }
      }
    }
    
    return values;
  }
  
 /* private void addValues(HTMLNode node, List<HTMLNode> values) {
    if(node == null) return;
    if(node.isNode(Name.CONTENT)) {
      values.add(node);
      return;
    }
    List<HTMLNode> children  = node.getChildren();
    if(children == null) return;
    for(int i = 0; i < children.size(); i++) {
      addValues(children.get(i), values);
    }
  }*/

  public HTMLNode handleTable(HTMLNode tableNode, HTMLNode imgNode) {
    List<HTMLNode> trNodes =  nodeUtil.search(tableNode, Name.TR);
    int trIndex = 0;
    for(; trIndex < trNodes.size()-1; trIndex++) {
      if(isSuper(trNodes.get(trIndex), imgNode)) break;
    }
    if(trIndex >= trNodes.size()-1) return null;

    List<HTMLNode> tdNodes =  nodeUtil.search(trNodes.get(trIndex), Name.TD);
    int tdIndex = 0;
    for(; tdIndex < tdNodes.size()-1; tdIndex++) {
      if(isSuper(tdNodes.get(tdIndex), imgNode)) break;
    }

    if(tdIndex >= trNodes.get(trIndex+1).totalOfChildren()) return null;
    
    HTMLNode nodeValue = trNodes.get(trIndex+1).getChild(tdIndex);
    return isValidText(nodeValue, 3) ? nodeValue : null;
    
//    return isValidText(trNodes.get(trIndex+1).getChildren().get(tdIndex), 3);
  }


}
