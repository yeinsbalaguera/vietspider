/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Tree;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.renderer.ContentRegionSearcher2;
import org.vietspider.html.util.HTMLText;
import org.vietspider.ui.services.ClientLog;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 30, 2008  
 */
public class AutoSelectDataNode2 {

  private TreeHandler handler;
  private Tree tree;
  private HtmlExplorerListener explorer;

  public AutoSelectDataNode2(HtmlExplorerListener explorer,
      HTMLDocument doc, String url, TreeHandler handler, Tree tree) {
    this.explorer = explorer;
    this.handler = handler;
    this.tree = tree;
    if(Application.LICENSE == Install.PERSONAL) return; 

    try {
      autoSelect(doc, url);
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }
  }

  public void searchNodes(NodeIterator iterator, List<HTMLNode> nodes, Name name) {
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(name)) nodes.add(n);
    }
  }  

  protected List<HTMLNode> searchNodes(HTMLNode node, Name name) {
    List<HTMLNode> refsNode = new ArrayList<HTMLNode>();
    searchNodes(node.iterator(), refsNode, name);
    //    System.out.println("refsnode Size " + refsNode.size());

    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();

    List<HTMLNode> contents = new ArrayList<HTMLNode>();
    htmlText.searchText(contents, node, verify);
    return contents;
  }


  public void autoSelect(HTMLDocument document, String url) throws Exception {
    ContentRegionSearcher2 searcher = new ContentRegionSearcher2();
    HTMLNode nodes = searcher.extractContent(document, url, false);

    NodePathParser pathParser = new NodePathParser();
    //    for(int i = 0; i < nodes.size(); i++) {
    NodePath path = pathParser.toPath(nodes);   
    if(path == null) return; 
    handler.traverseTree(explorer, tree, path, TreeHandler.MARK);
    //    }
  }

}
