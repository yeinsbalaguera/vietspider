/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.util.HTMLParentUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2009  
 */
public class ContentRegionSearcher2 {

  private HTMLParentUtils parentUtil = new HTMLParentUtils();
//private SentenceCounter sentenceCounter = new SentenceCounter();


  public NodeRenderer filte(NodeRenderer [] nodeRenderers) {
    List<NodeRenderer> list = new ArrayList<NodeRenderer>();  
    for(int i = 0; i < nodeRenderers.length; i++) {
//    if(nodeRenderers[i].getOverage() < 1/*
//    || nodeRenderers[i].getTotalPattern() < 10*/) continue;
      list.add(nodeRenderers[i]);
    }

    NodeRenderer renderer = null;
//  int index = -1;
    for(int i = 0;  i < list.size(); i++) {
      if(renderer != null 
          && renderer.getScore() >= list.get(i).getScore()) continue;
      renderer = list.get(i);
//    index = i;
    }
//  System.out.println(" chon cai thu " + index + " : "+ renderer.getScore()+ " : ");
    return renderer;
  }

  public HTMLNode extractContent(HTMLDocument document, String url, boolean clean) throws Exception  {
    HTMLNode body = ContentRendererFactory.searchBody(document);
//    PageExtractor pageExtractor = new PageExtractor();
//    pageExtractor.filter(body, NodeChecker.createDefaultCheckers());
    ContentRenderer renderer = ContentRendererFactory.createContentRenderer(body, url);

    NodeRenderer nodeRenderer = extractContent(renderer);
    if(nodeRenderer == null) return null;

    List<HTMLNode> nodes = nodeRenderer.getContents();
//  for(int i = 0; i< nodes.size(); i++) {
//    System.out.println(nodes.get(i).getName() + " : ");
//  }
    HTMLNode  value = parentUtil.getUpParent(nodes);

//  ScoreCalculator.printNode(nodeRenderer);
//  System.out.println("thay co cai na " + nodeRenderer.getScore() );

//  java.io.File file  = new java.io.File("F:\\Temp2\\web\\output\\a.txt");
//  byte [] bytes = nodeRenderer.getText().getBytes(org.vietspider.common.Application.CHARSET);
//  org.vietspider.common.io.RWData.getInstance().save(file, bytes);

    if(nodes.size() < 3) return value;

    if(clean) {
      LinkRemover linkRemover = new LinkRemover(renderer.getLinkChecker());
      linkRemover.remove(value, nodes.get(0), nodes.get(nodes.size()-1));
    }
    return value;
  }

  public NodeRenderer extractContent(ContentRenderer renderer) throws Exception  {
    int length = renderer.getTextValue().length();
    List<HTMLNode> renderNodes = renderer.getNodePositions(0, length);
    NodeRenderer nodeRenderer = new NodeRenderer(renderer, renderNodes, 0, length);

    NodeRenderer [] nodeRenderers = {nodeRenderer};
    int size = 3;
    while(true) {
      NodeRenderer filteNodeRenderer = filte(nodeRenderers);

      if(filteNodeRenderer == null) {
//      System.out.println(" break by filterNodeRenderer is null"); 
        break;
      }

      String pattern = filteNodeRenderer.getMaxPatternValue();
      if(pattern == null || pattern.length() < 4) break;

      if(filteNodeRenderer.comparePattern() <  1) {
        nodeRenderer = filteNodeRenderer;
        break;
      } else if(filteNodeRenderer.comparePattern() < size) {
        if(filteNodeRenderer.getTotalPattern() < size*10) {
//          //System.out.println(countLink(filteNodeRenderer.getParent())+ " : " + ((size-1)*10));
//          List<HTMLNode> validNodes = renderer.getLinkChecker().getValidNodes();
//          if(countLink(validNodes, filteNodeRenderer.getParent()) >= size*10) {
////            System.out.println(" break  filter renderer" );
//            nodeRenderer = filteNodeRenderer;
//            size--;
//          } else {
//            System.out.println(" break by compare pattern "); 
            nodeRenderer = filteNodeRenderer;
//            break;
//          }
        }
      } else if(filteNodeRenderer.getTotalPattern() < 50 ) {
//      System.out.println(" break by total of pattern");
        nodeRenderer = filteNodeRenderer;
        break;
      }

      nodeRenderer = filteNodeRenderer;

      nodeRenderers = nodeRenderer.split(pattern);
    }
//  ScoreCalculator.printNode(nodeRenderer);
    return nodeRenderer;
  }

  private int countLink(List<HTMLNode> validNodes, HTMLNode node) {
    if(node == null) return 0;
    NodeIterator iterator = node.iterator();
    int counter = 0;
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(validNodes.contains(n)) continue;
      if(n.isNode(Name.A)) counter++;
    }
//  System.out.println(" thay co cai na "+ counter);
    return counter;
  }

}

