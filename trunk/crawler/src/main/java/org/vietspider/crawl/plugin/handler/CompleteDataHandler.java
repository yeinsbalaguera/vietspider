/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.net.URL;

import org.vietspider.bean.Meta;
import org.vietspider.chars.URLUtils;
import org.vietspider.common.io.LogService;
import org.vietspider.crawl.link.Link;
import org.vietspider.handler.CompleteDocHandler;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.NodeChildHandler;
import org.vietspider.html.util.HyperLinkUtil;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 12, 2007  
 */
public class CompleteDataHandler extends CompleteDocHandler {

  public CompleteDataHandler(HyperLinkUtil hyperLinkUtil, URLUtils urlCreator) {
    super(hyperLinkUtil, urlCreator);
//  hyperLinkUtil = new HyperLinkUtil();
  }

  public void completeURL(String addressHome, Link link, Meta meta) {
    URL home = null;
    try{
      home = new URL(addressHome);//link.getSource().getHome()[0]);  
    }catch(Exception exp){
      LogService.getInstance().setThrowable(link, exp);
    }
    if(home == null) return;

    String address = urlUtils.createURL(home, link.buildURL());
    meta.setSource(address);

    HTMLNode node = link.<HTMLDocument>getDocument().getRoot();

    try {
      hyperLinkUtil.createFullNormalLink(node, new URL(address));
    }catch(Exception exp){
      LogService.getInstance().setThrowable(link, exp);
    }
    completeTable(link.<HTMLDocument>getDocument());
  }

  private void completeTable(HTMLDocument document){
    final HTMLNode root = document.getRoot();

    root.traverse(new NodeChildHandler() {
      public void handle(int index, HTMLNode node) {
        root.setChild(index, completeTable(node));
      }
    });

    /*List<HTMLNode> children = root.getChildren();
    for(int i = 0; i <  children.size(); i++){
      HTMLNode node = children.get(i);
      root.setChild(i, completeTable(node));
//      children.set(i, completeTable(node));
    }    */
  }

}
