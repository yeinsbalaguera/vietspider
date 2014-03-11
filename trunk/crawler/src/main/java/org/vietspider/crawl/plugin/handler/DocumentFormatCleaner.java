/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;

import java.io.File;
import java.util.List;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.crawl.link.Link;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.Tag;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.util.NodeHandler;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;
import org.vietspider.serialize.NodesMap;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.SetterMap;
import org.vietspider.serialize.XML2Object;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 12, 2007  
 */
public final class DocumentFormatCleaner {
  
  public final static ArticleCleaner articleCleaner = loadCleaner();
  
  public DocumentFormatCleaner() {
  }
  
  public Object execute(Object[] objs) {
    HTMLDocument document = ((Link)objs[0]).getDocument();
    handle(document.getRoot()); 
    
//    List<HTMLNode> emptyNodes = new LinkedList<HTMLNode>();
//    searchEmptyNode(document.getRoot(), emptyNodes);
//    for(HTMLNode ele : emptyNodes) {
//      System.out.println(ele.getTextValue());
//      nodeHandler.removeNode(ele);
//    }
    
    return objs;
  }
  
  public void handle(HTMLNode node) {
    if(articleCleaner == null) return;
    cleanNode(node);
    /*List<HTMLNode> children = node.getChildrenNode();
    if(children == null) return;
    for(HTMLNode child : children) handle(child);*/
    
    List<HTMLNode> children = node.getChildren();
    if(children  == null) return;
    for(int i = 0; i < children.size(); i++){
      if(children.get(i).isTag()) handle(children.get(i));
    }
  }
  
  private void cleanNode(HTMLNode node) {
    NodeHandler nodeHandler = new NodeHandler();
    //remove node
    Name [] names = articleCleaner.getRemoveNode();
    for(Name name : names) {
      if(node.isNode(name)) {
        nodeHandler.removeNode(node);
        return ;
      }
    }
    
    // remove empty node
    if(cleanEmptyNode(nodeHandler, node)) return ;
    
    // set comment node
    names = articleCleaner.getCommentTag();
    for(Name name : names) {
      if(node.isNode(name)) {
//        System.out.println("\n\n luc truoc "+node.getParent().getTextValue());
        List<HTMLNode> subChildren = node.getChildren();
        for(int i = 0; i < subChildren.size(); i++) {
          cleanNode(subChildren.get(i));
        }
        
        if(node.getParent() != null ) {
          HTMLNode parent  = node.getParent();
          List<HTMLNode> children = parent.getChildren();
          if(children != null) {
            int idx = children.indexOf(node);
            if(idx > 0) {
              for(HTMLNode ele : subChildren) {
                parent.addChild(idx, ele);
//                children.add(idx, ele);
                idx++;
              }
              node.clearChildren();
            }
          }
        }

        node.setValue(("<!--"+new String(node.getValue())+"-->").toCharArray());
        node.setName(Name.COMMENT);
        ((NodeImpl)node).setType(TypeToken.COMMENT);
        
//        System.out.println("\n luc sau "+node.getParent().getTextValue()+"\n");
        return ;
      }
    }
    
    if(node.isNode(Name.COMMENT)) return ;
    
    //remove content
    if(node.isNode(Name.CONTENT)) return;
    
    //remove code  content
    if(node.isNode(Name.CODE_CONTENT)) return;
    
    //remove attribute
    RemoveAttribute [] removeAttributes = articleCleaner.getRemoveAttribute();
    for(RemoveAttribute remove : removeAttributes) {
      if(remove.name != Name.ANY && !node.isNode(remove.name)) continue;
      Attributes attributes = node.getAttributes(); 
      int idx  = attributes.indexOf(remove.attribute);
//        System.out.println(idx + " : "+ attributes.toString());
      if(idx > -1) attributes.remove(idx);
//      System.out.println(idx + " : "+ attributes.toString());
    }
  }
  
  private boolean cleanEmptyNode(NodeHandler nodeHandler, HTMLNode node) {
    if((node.hasChildren() || node.totalOfChildren() != 0)) return false;
    if(node.getConfig().end() == Tag.FORBIDDEN) return false;
    HTMLNode parent = node;
    while(parent.getParent() != null &&
          parent.getParent().totalOfChildren() == 1) {
      parent  = parent.getParent();
    }
    nodeHandler.removeNode(parent); 
    return true;
  }

  @NodeMap("article-cleaner")
  public static class ArticleCleaner {
    
    @NodesMap(value = "remove-attribute")
    private RemoveAttribute [] removeAttribute = new RemoveAttribute[] {new RemoveAttribute(Name.HTML, "class")};
    
    @NodesMap(value = "comment-tag", item = "tag")
    private Name [] commentTag = new Name[] {Name.HTML};
    
    @NodesMap(value = "remove-node", item = "node")
    private Name [] removeNode = new Name[] {Name.HTML};
    
    @NodesMap(value = "remove-text", item = "text")
    private String [] removeText = new String[] {""};
    
    public ArticleCleaner() {
    }

    @GetterMap("remove-attribute")
    public RemoveAttribute [] getRemoveAttribute() { return removeAttribute; }
    @SetterMap("remove-attribute")    
    public void setRemoveAttribute(RemoveAttribute [] removeAttributes) { 
      this.removeAttribute = removeAttributes;
    }

    @GetterMap("comment-tag")
    public Name[] getCommentTag() { return commentTag; }
    @SetterMap("comment-tag")
    public void setCommentTag(Name[] toCommentTags) { this.commentTag = toCommentTags; }

    @GetterMap("remove-node")
    public Name[] getRemoveNode() { return removeNode; }
    @SetterMap("remove-node")
    public void setRemoveNode(Name[] removeNode) { this.removeNode = removeNode; }

    @GetterMap("remove-text")
    public String[] getRemoveText() { return removeText; }
    @SetterMap("remove-text")
    public void setRemoveText(String[] removeTexts) { this.removeText = removeTexts; }
  }
  
  @NodeMap("item")
  public static class RemoveAttribute {
    
    @NodeMap("tag")
    private Name name;
    
    @NodeMap("attribute")
    private String attribute;
    
    public RemoveAttribute() { 
      
    }
    
    public RemoveAttribute(Name name, String value) {
      this.name = name;
      this.attribute = value;
    }

    @GetterMap("tag")      
    public Name getName() { return name; }
    @SetterMap("tag")     
    public void setName(Name name) { this.name = name; }

    public String getAttribute() { return attribute; }

    public void setAttribute(String attribute) { this.attribute = attribute; }
    
  }
  
  
  public static ArticleCleaner loadCleaner() {
    File file = UtilFile.getFile("system", "article-cleaner.xml");
    if(file.exists())  {
      try {
        String xml = new String(RWData.getInstance().load(file), Application.CHARSET);
        XMLDocument document = XMLParser.createDocument(xml, null);
        return XML2Object.getInstance().toObject(ArticleCleaner.class, document);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
      return new ArticleCleaner();
    } 

    ArticleCleaner articleCleaner_ = new ArticleCleaner();
    try {
      String xml = Object2XML.getInstance().toXMLDocument(articleCleaner_).getTextValue();
      RWData.getInstance().save(file, xml.getBytes(Application.CHARSET));
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }

    return articleCleaner_;
  }
  
}
