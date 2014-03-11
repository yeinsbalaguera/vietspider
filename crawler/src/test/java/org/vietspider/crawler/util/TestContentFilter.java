/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.DataWriter;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.link.ContentFilters;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 3, 2008  
 */
public class TestContentFilter {
  
  protected static List<HTMLNode> searchTextNodes(HTMLNode root) {
    NodeHandler nodeHandler = new NodeHandler();
    List<HTMLNode> refsNode =  new ArrayList<HTMLNode>();
    nodeHandler.searchNodes(root.iterator(), refsNode, Name.A);
    if(refsNode.size() > 20) return null;
    
    List<HTMLNode> contentsNode = new ArrayList<HTMLNode>();
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
    htmlText.searchText(contentsNode, root, verify);
//    nodeHandler.searchTextNode(root, contentsNode);
//    System.out.println("step 6 "+href.getUrl()+ " : "+ contentsNode.size());
    return contentsNode.size() < 2 ? null : contentsNode ;
  }
  
  public static void main(String[] args) throws Exception {
    String [] elements = {"kinh doanh", "lạm phát", "giá lương thực", "điều chỉnh"};
    ContentFilters contentFilters = new ContentFilters(null, elements);
    File file  = new File("F:\\Temp\\text_content_filter.html");
    HTMLNode node = new HTMLParser2().createDocument(file,"utf-8").getRoot();
    
    List<HTMLNode> contentNodes = searchTextNodes(node);
    contentFilters.mark(contentNodes);
    
    File file2  = new File("F:\\Temp\\text_content_filter2.html");
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    writer.save(file2, node.getTextValue().getBytes("utf-8"));
  }
  
  
}
