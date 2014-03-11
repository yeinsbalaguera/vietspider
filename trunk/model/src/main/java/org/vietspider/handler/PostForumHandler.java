/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.handler;

import java.util.List;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.token.TypeToken;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 5, 2009  
 */
public class PostForumHandler {
  
  private final static char [] brNodeValue = "br".toCharArray();
  private final static char [] hrNodeValue = "hr border=\"1\"".toCharArray();
  
  private CompleteDocHandler complete;
  
  public PostForumHandler(CompleteDocHandler complete) {
    this.complete = complete;
  }
  
  public void build(HTMLNode root, List<HTMLNode> users, List<HTMLNode> contents) {
    if(users.size() < 1 || contents.size() < 1) return;
    
    if(users.size() > contents.size()) {
      int rate = users.size()/contents.size();
      for(int i = 0; i < contents.size(); i++) {
        if(i > 0) {
          HTMLNode hrNode = new NodeImpl(hrNodeValue, Name.HR, TypeToken.SINGLE); 
          root.addChild(hrNode);
//        hrNode.setParent(html);
        }
        
        for(int j = 0; j < rate; j++) {
          HTMLNode userNode = users.get(i+j);
          if(userNode != null) {
            userNode =  complete.completeTable(userNode);
            root.addChild(userNode);
//          userNode.setParent(html);
          }
        }
        
        HTMLNode brNode = new NodeImpl(brNodeValue, Name.BR, TypeToken.SINGLE); 
        root.addChild(brNode);
        
        HTMLNode contentNode = contents.get(i);
        if(contentNode != null) {
          contentNode =  complete.completeTable(contentNode);
          root.addChild(contentNode);
//        contentNode.setParent(html);
        }
      }
      return;
    }
    
    
    int rate = contents.size()/users.size();
    for(int i = 0; i < users.size(); i++) {
      if(i > 0) {
        HTMLNode hrNode = new NodeImpl(hrNodeValue, Name.HR, TypeToken.SINGLE); 
        root.addChild(hrNode);
//      hrNode.setParent(html);
      }
      
      HTMLNode userNode = users.get(i);
      if(userNode != null) {
        userNode =  complete.completeTable(userNode);
        root.addChild(userNode);
//      userNode.setParent(html);
      }
      
      HTMLNode brNode = new NodeImpl(brNodeValue, Name.BR, TypeToken.SINGLE); 
      root.addChild(brNode);

      for(int j = 0; j < rate; j++) {
        HTMLNode contentNode = contents.get(i+j);
        if(contentNode != null) {
          contentNode =  complete.completeTable(contentNode);
          root.addChild(contentNode);
//        contentNode.setParent(html);
        }
      }
    }
  }
  
  
}
