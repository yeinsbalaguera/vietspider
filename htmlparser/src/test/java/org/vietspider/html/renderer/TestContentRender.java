/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.renderer;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.Application;
import org.vietspider.common.io.UtilFile;
import org.vietspider.document.util.OtherLinkRemover2;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.util.HTMLParentUtils;
import org.vietspider.html.util.HTMLText;
import org.vietspider.html.util.NodeHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 14, 2008  
 */
public class TestContentRender {
  
  static void searchNodes(NodeIterator iterator, List<HTMLNode> nodes, Name name) {
    while(iterator.hasNext()) {
      HTMLNode n = iterator.next();
      if(n.isNode(name)) nodes.add(n);
    }
  }  
  
  protected static List<HTMLNode> searchNodes(HTMLNode node, Name name) {
    List<HTMLNode> refsNode = new ArrayList<HTMLNode>();
    searchNodes(node.iterator(), refsNode, name);
//    System.out.println("refsnode Size " + refsNode.size());
    
    HTMLText htmlText = new HTMLText();
    HTMLText.EmptyVerify verify = new HTMLText.EmptyVerify();
    
    List<HTMLNode> contents = new ArrayList<HTMLNode>();
    htmlText.searchText(contents, node, verify);
    
//    System.out.println("contents Size " + contentsNode.size());
//    System.out.println("step 6 "+href.getUrl()+ " : "+ contentsNode.size());
    return contents;
  }
  
  static String searchMaxSequence(String text, char c) {
    int index = text.indexOf(c);
    if(index < 0) {
//      System.out.println(text + "\n ++++++ "+ index + " uh null");
      return null;
    }
    
    int counter = 1;
    while(index > -1) {
      counter = searchMaxSequence(text, c, index, counter);
      index = text.indexOf(c, index+counter);
    }
    
    StringBuilder builder = new StringBuilder(counter);
    for(int i = 0; i < counter; i++) {
      builder.append(c); 
    }
    return builder.toString();
  }
  
  private static int searchMaxSequence(String text, char c, int index, int counter) {
    int k = index + 1;
    for(; k < index + counter; k++) {
      if(k >= text.length() || text.charAt(k) != c) return counter;
    }
    
    k = index + counter ;
    while(k  < text.length()) {
      if(text.charAt(k) != c) {
        return k - index;
      }
      k++;
    }
    
    return counter;
  }
  
  private static NodeHandler nodeHandler = new NodeHandler();
  
  private static NodeRenderer filte(NodeRenderer [] nodeRenders) {
    NodeRenderer renderer = null;
    int max = 0;
//    System.out.println("===========================================");
    for(int i = 0; i < nodeRenders.length; i++) {
      int counter = countWord(nodeRenders[i].getNodes());
      int counter1 = countWordA(nodeRenders[i].getNodes());
//      System.out.println("?????????????????????????????????????????????????");
//      System.out.println(renders[i].getTextValue());
//      System.out.println("?????????????????????????????????????????????????");
//      System.out.println(renders[i].getTextValue());
//      System.out.println(" =================> cuoi cung co co " + counter+ " : " + counter1);
      
//      if(counter1 >= counter) {
//        continue;
//      } else {
//        System.out.println("khong co "+counter+ " : "+ counter1);
//      }
      if(renderer == null) {
        max = counter;
        renderer = nodeRenders[i];
      } else if (counter >= max) {
        max = counter;
        renderer = nodeRenders[i];
      }
      
    }
//    System.out.println("--------------------------------------------------------------------");
//    System.out.println(renderer.getTextValue());
//    System.out.println("--------------------------------------------------------------------");
    
    return renderer;
  }
  
  private static int countWord(List<HTMLNode> list) {
    int counter = 0;
    for(HTMLNode node : list) {
      counter += countWord(node);
    }
    return counter;
  }
  
  private static int countWord(HTMLNode node) {
    if(node.isNode(Name.CONTENT)) {
//      System.out.println("chuan bi dem "+ isLinkTag(node)+"\n");
      if(isLinkTag(node)) {
//        System.out.println("ket qua 2 0" );
        return 0;
      }
      
//      System.out.println(node.getTextValue());
      int c = nodeHandler.count(new String(node.getValue()));
//      System.out.println("ket qua 1 "+ c);
      return c;
    }
//    System.out.println("ket qua 3 0" );
    return 0;
  }
  
  private static int countWordA(List<HTMLNode> list) {
    int counter = 0;
    for(HTMLNode node : list) {
      counter += countWordA(node);
    }
    return counter;
  }
  
  private static int countWordA(HTMLNode node) {
    if(node.isNode(Name.CONTENT)) {
      if(isLinkTag(node)) {
        int c = nodeHandler.count(new String(node.getValue()));
        return c;
      }
      return 0;
    }
    return 0;
  }
  
  private static boolean isLinkTag(HTMLNode node) {
    return isLinkTag(node, 0);
  }

  private static boolean isLinkTag(HTMLNode node, int level) {
    if(node == null || level >= 5) return false;
    if(node.isNode(Name.A)) return true;
    return isLinkTag(node.getParent(), level+1);
  }
  
  private static void proceess(File file) throws Exception  {
    HTMLDocument document = HTMLParser.createDocument(file,"utf-8");
    RefsDecoder decoder = new RefsDecoder();
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(!node.isNode(Name.CONTENT)) continue;
      char [] chars = node.getValue();
      chars = decoder.decode(chars);
      
      chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
      chars =  java.text.Normalizer.normalize(new String(chars), Normalizer.Form.NFC).toCharArray();
//      chars = normalizer.normalize(chars);
      node.setValue(chars);              
    }  
    
    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();

    NodePath nodePath  = pathParser.toPath("BODY");
    HTMLNode  body = extractor.lookNode(document.getRoot(), nodePath);
    
    List<HTMLNode> contents = searchNodes(body, Name.A);
    OtherLinkRemover2 linkRemover = new OtherLinkRemover2();
    List<HTMLNode> nodes = linkRemover.removeLinks(body, null);
    for(int i = 0; i < nodes.size(); i++) {
      contents.remove(nodes.get(i));
    }
    ContentRenderer renderer = ContentRendererFactory.createContentRenderer(body, null);
    
    int length = renderer.getTextValue().length();
    List<HTMLNode> renderNodes = renderer.getNodePositions(0, length);
    NodeRenderer nodeRenderer = new NodeRenderer(renderer, renderNodes, 0, length);
    
//    ContentRenderer renderer = new ContentRenderer(body, null, true);
    
    NodeRenderer [] nodeRenderers = {nodeRenderer};
    while(true) {
      nodeRenderer = filte(nodeRenderers);
      
      if(nodeRenderer == null) break;
      
      String pattern = searchMaxSequence(nodeRenderer.getText(), '\\');
      System.out.println("=== > " + file.getName()+ " pattern "+ pattern + " : "+ nodeRenderers.length);
      if(pattern == null || pattern.length() < 30 ) {
        break;
      }
      nodeRenderers = nodeRenderer.split(pattern);
//      for(int h = 0; h < renderers.length; h++) {
//        System.out.println("?????????????????????????????????????????????????");
//        System.out.println(renderers[h].getTextValue());
//        System.out.println("?????????????????????????????????????????????????");
//      }
    }
    nodeRenderer = filte(nodeRenderers);
    nodeRenderers = new NodeRenderer[] {nodeRenderer};
    if(renderer == null) {
      System.err.println("null roi ");
    }
    
    int counter = countWord(nodeRenderer.getNodes());
    System.out.println("=== > "+ file.getName()+ " counter "+ counter);
    
//    for(int i = 0; i < nodeRenderers.length; i++) {
      file  = new File("F:\\Temp2\\web\\output\\"+file.getName()+".html");
      HTMLParentUtils htmlUtil = new HTMLParentUtils();
      int start  = nodeRenderer.getStart();
      int end = nodeRenderer.getEnd();
      HTMLNode parent  = htmlUtil.getUpParent(renderer.getNodePositions(start, end));
//      file  = new File("F:\\Temp2\\web\\output\\"+String.valueOf(i)+".txt");
      byte [] bytes = parent.getTextValue().getBytes(Application.CHARSET);
      org.vietspider.common.io.RWData.getInstance().save(file, bytes);
//    }
  }
  
  public static void main(String[] args) throws Exception {
    UtilFile.deleteFolder(new File("F:\\Temp2\\web\\output\\"), false);
    
//    File file = new File("F:\\Temp2\\web\\vietnamnet.htm");
//    proceess(file);
    
    File folder  = new File("F:\\Temp2\\web\\");
    File [] files = folder.listFiles();
    for(int i = 0; i < files.length; i++) {
      if(files[i].getName().endsWith(".htm") 
          || files[i].getName().endsWith(".html")) {
        proceess(files[i]);
      }
    }
  }
  
  private static boolean contains(String data, String pattern) {
    int index = data.indexOf(pattern);
    if(index < 0) return false;
//    System.out.println(pattern);
//    if(index > 0) System.out.println("=====> "+data.charAt(index-1)+ " : "+Character.isLetterOrDigit(data.charAt(index-1)));
    if(index > 0 
        && Character.isLetterOrDigit(data.charAt(index-1))) return false;
//    if(index > 0) System.out.println(data.charAt(index-1));
    index = index+pattern.length();
//    if(index < data.length()-1)System.out.println("===1==> "+data.charAt(index+1));
    if(index < data.length()-1 
        && Character.isLetterOrDigit(data.charAt(index+1))) return false;
    return true;  
  }
}
