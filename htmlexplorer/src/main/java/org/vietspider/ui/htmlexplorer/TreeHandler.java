package org.vietspider.ui.htmlexplorer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.path2.AttrName;
import org.vietspider.html.path2.INode;
import org.vietspider.html.path2.Node;
import org.vietspider.html.path2.NodeExp;
import org.vietspider.html.path2.NodeMatcher;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;

/**
 * @by thuannd (nhudinhthuan@yahoo.com)
 * Jun 29, 2005
 */
public class TreeHandler {
//  
  private TreeFactory treeFactory = new TreeFactory();
  
  private List<TreeItem> selectedItems = new ArrayList<TreeItem>();
  
  public final static int NONE = 0, SELECT = 1, MARK = -1, REMOVE = -2;
  
  public String getConfig(TreeItem item){
    String [] split = item.getText().split("-");    
    String txt = split[0]+"["+split[1]+"]";
    while( item != null){                     
      item = item.getParentItem();
      if( item == null) break;      
      split = item.getText().split("-");    
      txt = split[0]+"["+split[1]+"]"+"."+txt; 
    }  
    return txt;
  }
  
  public void resetTree(Tree tree){
    selectedItems.clear();
    TreeItem [] items = tree.getItems();
    if(items == null) return ;
    for(TreeItem ele : items) resetTreeItem(ele);
  }
  
  private void resetTreeItem(TreeItem item){
    item.setBackground(new Color( item.getDisplay(), 255, 255, 255));
    TreeItem [] items = item.getItems();
    if(items == null) return ;
    for(TreeItem ele : items) resetTreeItem(ele);
  }
  
  public boolean traverseTree(HtmlExplorerListener explorer,
      Tree tree, String path, int style) throws Exception {
    NodePath nodePath = new NodePathParser().toPath(path);
    return traverseTree(explorer, tree, nodePath, path, style);
  }
  
  public boolean traverseTree(HtmlExplorerListener explorer,  
      Tree tree, NodePath nodePath, int style) {
    String path = nodePath.toString();
    return traverseTree(explorer, tree, nodePath, path, style);
  }
  
  public boolean traverseTree(HtmlExplorerListener explorer,
      Tree tree, NodePath nodePath, String path) {
    return traverseTree(explorer, tree, nodePath, path, TreeHandler.SELECT);
  }
  
  public boolean traverseTree(HtmlExplorerListener explorer,
      Tree tree, NodePath nodePath, String path, int style) {
    INode<?>[] nodes = nodePath.getNodes();
    int length = nodes.length;
    if(length > 0 && nodes[length - 1] instanceof AttrName) {
      length--;
    }
    
    if(length < 1) return true;
    
    List<TreeItem> items = selectNode(nodes[0], tree.getItems());
    List<TreeItem> values = new ArrayList<TreeItem>();
    
    if(items.size() < 1) {
      explorer.addErrorPath(path);
      return false;
    }
    
//    new Exception().printStackTrace();
    
    for(int i = 1; i < length; i++) {
      if(items.size() < 1) {
        explorer.addErrorPath(path);
        return false;
      }
      
      values.clear();
      for(TreeItem item : items) {
        values.addAll(selectNode(nodes[i], item.getItems()));
      }
      items.clear();
      items.addAll(values);
    }
    
    if(items.size() < 1) {
      explorer.addErrorPath(path);
      return false;
    }
    
//    System.out.println(path + " : " + (style  == REMOVE));
    
    explorer.showErrorPath(null);
    
    TreeItem item  = items.get(0);
    if(style == SELECT) {
      item.setBackground(new Color(item.getDisplay(), 255, 150, 150));
      Color color = new Color(item.getDisplay(), 255, 255, 111);
      for(int i = 1; i < items.size(); i++) {
        items.get(i).setBackground(color);
      }
      selectedItems.addAll(items);
    } else if(style == MARK){
      item.setBackground(new Color(item.getDisplay(), 180, 255, 255)); 
    } else if(style  == REMOVE) {
      Color color = new Color(item.getDisplay(), 255, 255, 255);
      for(int i = 0; i < items.size(); i++) {
        items.get(i).setBackground(color);
      }
      return true;
    }/* else if(style == NONE) {
      return true;
    }*/
    tree.setSelection(new TreeItem[] {item});
    tree.setFocus();
    return true;
  }
  
  public int indexOfError(Tree tree, String path) throws Exception {
    NodePathParser pathParser = new NodePathParser();
    NodePath nodePath = pathParser.toPath(path);
    
    INode<?>[] nodes = nodePath.getNodes();
    int length = nodes.length;
    String attrName = null;
    
//    System.out.println(nodes[length - 1] +  " : " + ((nodes[length - 1] instanceof AttrName)));
    if(nodes[length - 1] instanceof AttrName) {
      length--;
      attrName = (String)nodes[nodes.length - 1].getName();
    }
    if(length < 1) return -1;
    
    List<TreeItem> items = null;
    
    StringBuilder builder = new StringBuilder();
    
    for(int i = 0; i < length; i++) {
      if(i == 0) {
        items = selectNode(nodes[0], tree.getItems());
      } else {
        List<TreeItem> values = new ArrayList<TreeItem>();
        for(TreeItem item : items) {
          values.addAll(selectNode(nodes[i], item.getItems()));
        }
        items.clear();
        items.addAll(values);
      }
      
      if(items.size() < 1) break;
      
//      System.out.println(" checking " + nodes[i].getName() + " : "+ items.get(0).getText());
      
      if(builder.length() > 0) builder.append('.');
      builder.append(nodes[i].toString());
    }
    
    if(attrName != null) return builder.length() + attrName.length() + 1;
    
    return builder.length();
  }
  
  private List<TreeItem> selectNode(INode<?> inode, TreeItem [] items){
    String name  = inode.getName()+"-";
    List<TreeItem>  list = new ArrayList<TreeItem>();
    NodeMatcher expMatcher = new NodeMatcher();
    for(TreeItem item : items){
      if(item.getText().startsWith(name)){
        String indexValue = item.getText().substring(name.length());
        int index = Integer.parseInt(indexValue);
        
        if(inode instanceof NodeExp) {
          NodeExp nodeExp = (NodeExp)inode;
          if(expMatcher.match(nodeExp.getPattern(), index)) {
            Attribute [] attrs = nodeExp.getAttributes(); 
            if(attrs == null || attrs.length < 1) {
              list.add(item);
            } else {
              String data = (String)item.getData();
              int idx = data.indexOf(' ');
              if(idx > 0) data = data.substring(idx+1);
              if(contains(AttributeParser.getInstance().get(data), attrs)) list.add(item); 
            }
          }
          continue;
        }

        Node node = (Node)inode;
        if(node.getIndex() != index) continue;
        Attribute [] attrs = inode.getAttributes(); 
        if(attrs == null || attrs.length < 1) {
          list.add(item);
        } else {
          String data = (String)item.getData();
          int idx = data.indexOf(' ');
          if(idx > 0) data = data.substring(idx+1);
          if(contains(AttributeParser.getInstance().get(data), attrs)) list.add(item); 
        }

      }
    }
    return list;
  }
  
  private boolean contains(Attribute [] n_attributes, Attribute [] template_attrs) {
    for(Attribute attr : template_attrs) {
      if(!contains(n_attributes, attr))  return false;
//      Attribute attr1 = null;
//      for(Attribute attr2 : n_attributes) {
//        if(attr2.getName().equalsIgnoreCase(attr.getName())) {
//          attr1 = attr2;
//          break;
//        }
//      }
//      if(attr1 == null || 
//          !attr.getValue().equalsIgnoreCase(attr1.getValue())) return false;
    }
    return true;
  }
  
  private boolean contains(Attribute [] attributes, Attribute attribute) {
    for(Attribute attr : attributes) {
      String name1 = attr.getName();
      String name2 = attribute.getName();
//      System.out.println(name1 + " : "+ name2);
      String value1 = attr.getValue();
      String value2 = attribute.getValue();
      
//      System.out.println(value1 + " : "+ value2);
//      System.out.println("=======================================");
      if(name1.equalsIgnoreCase(name2) && value1.equalsIgnoreCase(value2)) return true;
    }
    return false;
  }
  
  public void createTreeItem(Tree tree, HTMLDocument doc){
    if(doc == null) return ;
    selectedItems.clear();
    HTMLNode node = doc.getRoot();
    createItem(tree, node);
  }
  
  private void createItem(Object parent, HTMLNode node){
    List<HTMLNode> children = node.getChildren();
    if(children == null) return;
    for(int i=0; i< children.size(); i++){
      HTMLNode child  = children.get( i);      
      TreeItem item;
      if(parent instanceof Tree) {
        item = treeFactory.get((Tree)parent, child.getName(), getIndex(children, child) , getStyle(child));
        item.setData(new String(child.getValue()));
      } else {
        item = treeFactory.get((TreeItem)parent, child.getName(), getIndex(children, child) , getStyle(child));
        item.setData(new String(child.getValue()));
      }
      createItem(item, child);
    }   
  }
  
  private int getIndex(List<HTMLNode> children, HTMLNode element){
    int count = 0;
    for(int i = 0; i < children.size(); i++){
      if(children.get(i).isNode(element.getName())){
        if(children.get(i) == element) return count;
        count++;
      }
    }
    return count;
  }
  
  private int getStyle(HTMLNode element){
    Name name = element.getConfig().name();
    if(name == Name.CONTENT) return -2;
    if(name == Name.COMMENT || name == Name.CODE_CONTENT) return -1;
    return sizeOfContent(element);
  }
  
  private int sizeOfContent(HTMLNode node){
    int s = node.getValue().length;
    List<HTMLNode> children = node.getChildrenNode();
    for(HTMLNode child : children) {
      s += sizeOfContent(child);
    }
    return s;
  }

  public List<TreeItem> getSelectedItems() { return selectedItems; }
  
}