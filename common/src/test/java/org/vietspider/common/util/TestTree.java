package org.vietspider.common.util;


/**
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 19, 2006  
 */
public class TestTree {
  
  private static void printNode(Tree node, String tab){
    System.out.println(tab+node.getValue());
    TreeIterator iter = node.iteratorChildren();
    while(iter.hasNext()){
      Tree n = iter.next();  
      printNode(n, tab+"  ");
    }
  }
  
  public static void main(String[] args) {
    Tree<String> Tree = new Tree<String>("yahoo");
    Tree<String> child = new Tree<String>("google", Tree);
    child = new Tree<String>("hihi", child);
    child = new Tree<String>("java");
    Tree.addChild(child);
    new Tree<String>("cuon chay", child);
    child = new Tree<String>("cai nua ne", child);
    new Tree<String>("haha", child);    
    
    printNode(Tree, "");
    
//    System.out.println(Tree.getChild(1).getValue());
    
    child = new Tree<String>("insertNode");
    Tree.insertChild(1, child);
   /* printNode(Tree, "");
    System.out.println("================================");
    Tree.remove(1);
    printNode(Tree, "");
    System.out.println(Tree.get(1).getValue());*/
    
    Tree [] children = Tree.getChildren();
    for(Tree ele : children){
      System.out.println(ele.getValue());
    }
  }
}
