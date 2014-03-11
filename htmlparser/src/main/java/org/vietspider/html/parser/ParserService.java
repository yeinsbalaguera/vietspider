/***************************************************************************
 * Copyright 2001-2006 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.html.parser;

import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.Name;
import org.vietspider.token.TokenParser;
import org.vietspider.token.TypeToken;

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 14, 2006  
 */
public final class ParserService {
  
  /*
  private static ThreadSoftRef<DOMParser DOM_PARSER = new ThreadSoftRef<DOMParser>(DOMParser.class);
  
  private static ThreadSoftRef<NodeCreator> NODE_CREATOR = new ThreadSoftRef<NodeCreator>(NodeCreator.class);  
  
  private  static ThreadSoftRef<NodeCloser> NODE_CLOSER = new ThreadSoftRef<NodeCloser>(NodeCloser.class);
  
  private static ThreadSoftRef<NodeSetter> NODE_SETTER = new ThreadSoftRef<NodeSetter>(NodeSetter.class);
  
  private static ThreadSoftRef<TokenParser> TOKEN_PARSER = new ThreadSoftRef<TokenParser>(TokenParser.class);
  
  static private NodeImpl ROOT;
  
  static DOMParser getDOMParser () { return DOM_PARSER.getRef(); }
  
  static NodeCreator getNodeCreator () { return NODE_CREATOR.getRef(); }
  
  static NodeCloser getNodeCloser () { return NODE_CLOSER.getRef(); }
  
  static NodeSetter getNodeSetter () { return NODE_SETTER.getRef(); }
  
  public static TokenParser getTokenParser () { return TOKEN_PARSER.getRef(); }*/
  
  private NodeImpl root;
  
  private DOMParser domParser;
  private TokenParser tokenParser;
  
  private NodeCreator creator;
  private NodeCloser closer;
  private NodeSetter setter;
  
  public ParserService() {
    domParser = new DOMParser(this);
    tokenParser = new TokenParser();
    
    creator = new NodeCreator(this);
    closer = new NodeCloser(this);
    setter = new NodeSetter(this);
  }

  final void parse(CharsToken tokens, HTMLDocument document){    
    root = new NodeImpl(new char[]{'h', 't', 'm', 'l'}, Name.HTML, TypeToken.TAG);       
    document.setRoot(root);
    List<NodeImpl> opens = creator.getOpens();
    opens.clear();
    opens.add(root);
    domParser.parse(document, tokens);  
  }

  final NodeImpl createHeader(){
    NodeImpl node = new NodeImpl(new char[]{'h', 'e', 'a', 'd'}, Name.HEAD, TypeToken.TAG);
    root.addInternalChild(0, node);
//    ROOT.getChildren().add(0, node);
//    node.setParent(ROOT);
    return node;
  }

  final NodeImpl createBody(){
    NodeImpl node = new NodeImpl(new char[]{'b', 'o', 'd', 'y'}, Name.BODY, TypeToken.TAG);
    root.addInternalChild(node);
//    ROOT.getChildren().add(node);
//    node.setParent(ROOT);
    return node;
  }

  final NodeImpl getRootNode() { return root; }

  final void setRootNode(NodeImpl root) { this.root = root; }

  final DOMParser getDomParser() { return domParser;  }
  final TokenParser getTokenParser() { return tokenParser; } 

  final NodeCreator getNodeCreator() { return creator; }
  final NodeCloser getNodeCloser() { return closer; }
  final NodeSetter getNodeSetter() { return setter; }

  
}
