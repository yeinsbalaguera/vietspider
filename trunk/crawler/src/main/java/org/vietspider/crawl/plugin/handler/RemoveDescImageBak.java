/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.plugin.handler;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 2, 2007  
 */

final class RemoveDescImageBak {

 /* private List<HTMLNode> imageNodes;
  private List<HTMLNode> contentNodes;
  private NodeHandler nodeHandler;
  
  private Pattern pattern;

  RemoveDescImageBak(NodeHandler nodeHandler) {
    imageNodes = new CopyOnWriteArrayList<HTMLNode>();
    this.nodeHandler = nodeHandler;
    pattern = Pattern.compile("\\bảnh|\\bminh họa", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
  }

  public List<HTMLNode> getImageNodes() { return imageNodes; }

  public void removeDescImageNode(HTMLNode root, List<HTMLNode> contents) {
    imageNodes.clear();
    contentNodes = contents;
    removeDescImageNode(root, root);
  }

  public HTMLNode removeDescImageNode(HTMLNode root, HTMLNode node) {
    if(node.isNode(Name.IMG)) return node;
    List<HTMLNode> children  = node.getChildren();
    if(children == null) return null;
    HTMLNode imgNode = null;
    for(int i = 0; i < children.size(); i++) {
      imgNode = removeDescImageNode(root, children.get(i));
      if(imgNode == null || checkAttributeImage(imgNode)) continue;
      analysesImage(imgNode);
      imageNodes.add(imgNode);
      imgNode = null;
    }
    return imgNode;
  }

  private boolean checkAttributeImage(HTMLNode node) {
    Attributes attributes = node.getAttributes(); 
    Attribute attribute = attributes.get("width");
    if(attribute != null) {
      try {
        int value  = Integer.parseInt(attribute.getValue());
        if(value < 40) return true;
      }catch (NumberFormatException e) {
      }
    }
    attribute = attributes.get("height");
    if(attribute == null) return  false;
    try {
      int value  = Integer.parseInt(attribute.getValue());
      if(value < 40) return true;
    }catch (NumberFormatException e) {
    }
    return false;
  }

  protected boolean isContentFormat(HTMLNode node) {
    if(nodeHandler.isStyleNode(node)) return true;
    switch(node.getName()) {
    case BR: 
    case CONTENT:
    case COMMENT: 
    case IMG: 
      return true;
    case P: 
      return node.getChildren() == null || node.getChildren().size() < 1;
    }
    return false;
  }

  protected boolean isHeaderNode(HTMLNode node) {
    switch(node.getName()) {
    case H1:
    case H2:
    case H3:
    case H4:
    case H5:
      return true;
    }
    return false;
  }

  private HTMLNode searchContentNode(HTMLNode node, List<Integer> indexImage) {
    if(node.isNode(Name.CONTENT))  {
      if(nodeHandler.isEmpty(node.getValue())) return null;
      List<Integer> indexContent = getIndexPath(node);
//      System.out.println(node.getTextValue());
////      print(indexImage);
////      print(indexContent);
//      System.out.println(" ======================== "+compare(indexImage, indexContent));
      if(compare(indexImage, indexContent)) return node;
      return null;
    }
    if(node.isNode(Name.SCRIPT) 
        || node.isNode(Name.STYLE) || node.isNode(Name.UNKNOWN)) return null;
    
    List<HTMLNode> children  = node.getChildren();
    if(children == null) return null;
    for(HTMLNode ele : children) {
      HTMLNode content = searchContentNode(ele, indexImage);
      if(content != null) return ele;
    }
    return null;
  }
  
  private boolean compare(List<Integer> images, List<Integer> contents){
    for(int i = 0; i < Math.min(images.size(), contents.size()); i++) {
      if(images.get(i) == contents.get(i)) continue;
      if(images.get(i) < contents.get(i)) return true;
      if(images.get(i) > contents.get(i)) return false;
    }
    return false;
  }

  private boolean analysesImage(HTMLNode image) {
    List<Integer> indexImage = getIndexPath(image);
    HTMLNode tr = getAncestor(image, Name.TR);
    HTMLNode tbody = null;
//  int start = searchIndexContainNode(tr, image);
    if(tr != null && (tbody = getAncestor(tr, Name.TBODY)) != null) {
      if(analysesTr(tr, tbody, indexImage)) return true;
      if(analysesNextTr(tr, tbody, indexImage)) return true;
    }
    HTMLNode parentNode = image.getParent();
//  start = searchIndexContainNode(parentNode, image);
    if(parentNode == null) return false;
    HTMLNode contentNode = searchContentNode(parentNode, indexImage);
    if(contentNode == null)  return false;
    return checkContentNode(parentNode, nodeHandler.upParentContentNode(contentNode)); 
  }

  private boolean analysesNextTr(HTMLNode tr, HTMLNode tbody, List<Integer> indexImage) {
    List<HTMLNode> children = tbody.getChildren();
    int i = 0;
    for(; i < children.size(); i++) {
      if(children.get(i) == tr) break; 
    }
    if(i >= children.size() - 1) return false;
    return analysesTr(children.get(i+1), tbody, indexImage);
  }

  private boolean analysesTr(HTMLNode tr, HTMLNode tbody, List<Integer> indexImage) {
    HTMLNode contentNode = searchContentNode(tr, indexImage);
    if(contentNode == null) return  false;
    if(checkContentNode(tr, nodeHandler.upParentContentNode(contentNode))) return true;
    List<HTMLNode> children = tbody.getChildren();
    if(children.size() > 2) return false;
    
    List<HTMLNode> newContents = new ArrayList<HTMLNode>();
    nodeHandler.searchTextNode(tbody, newContents);
    return nodeHandler.isShortContents(newContents, 50);
  }

  private boolean checkContentNode(HTMLNode root, HTMLNode contentNode) {
    StringBuilder builder = nodeHandler.buildContent(new StringBuilder(), contentNode);
    if(nodeHandler.count(builder) >= 50) return false;
    Matcher matcher = pattern.matcher(builder.toString());
    boolean isDesc = matcher.find() || 
                     checkAttribute(root, contentNode) || !isHeaderNode(contentNode);
    if(isDesc) nodeHandler.removeContent(contentNode, contentNodes);
    return isDesc;
  }

  private boolean checkAttribute(HTMLNode root, HTMLNode node) {
    if(node == root || node == null) return false;
    Attributes attributes = node.getAttributes(); 
    Attribute attribute = attributes.get("class");
    if(attribute != null) {
      String value = attribute.getValue().toLowerCase();
      if(value.indexOf("image") > -1 ||  value.indexOf("img") > -1 || 
          value.indexOf("anh") > -1 || value.indexOf("thumbnail") > -1) return true;
    } 
    return checkAttribute(root, node.getParent());
  }

  private HTMLNode getAncestor(HTMLNode node, Name name ){
    if(node == null) return null;
    if(node.isNode(name)) return node;
    return getAncestor(node.getParent(), name);
  }

  protected List<Integer> getIndexPath(HTMLNode element){
    HTMLNode parent = element.getParent();
    HTMLNode child = element;
    List<Integer> list = new ArrayList<Integer>();
    while(parent != null){
      list.add(parent.getChildren().indexOf(child));
      child = parent;
      parent = parent.getParent();
    }
    Collections.reverse(list);
    return list;
  }*/
  
//  private void print(List<Integer> list) {
//    for (Integer integer : list) {
//      System.out.print(integer + ", ");
//    }
//    System.out.println();
//  }

}
