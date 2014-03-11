/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

import java.util.Comparator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 7, 2007  
 */
public class BinaryTree<T> {

  private Node root;
  
  private Comparator<T> comparator;

  public BinaryTree(Comparator<T> comparator) {
    this.comparator = comparator;
  }

  public void insert(T value) {
    insert(value, root, null, false);
  }

  private void insert(T value, Node node, Node parent, boolean right) {
    if (node == null) {
      if (parent == null) {
        root = node = new Node(value, parent);
      } else if (right) {
        parent.right = node = new Node(value, parent);
      } else {
        parent.left = node = new Node(value, parent);
      }
      return;
    } 

    int compare = comparator.compare(value, node.value);
    if (compare == 0) {
      node.value = value;
    } else if( compare > 0) { 
      insert(value, node.right, node, true);
    } else {
      insert(value, node.left, node, false);
    }
  }

  public boolean contains(T value) { return contains(value, root); }
  
  private boolean contains(T value, Node node) {
    if (node == null) return false;
    int compare = comparator.compare(value, node.value);
    if (compare == 0) return true;
    if (compare > 0 ) return contains(value, node.right);
    return contains(value, node.left);
  }
  
  public void delete(T info) { delete(info, root); }
  
  private void delete(T value, Node node) {
    if (node == null) return ;
    int compare = comparator.compare(value, node.value);
    if(compare == 0) {
      deleteNode(node);
    } else if (compare > 0) {
      delete(value, node.right);
    } else {
      delete(value, node.left);
    }
  }
  
  private void deleteNode(Node node) {
    Node eNode, tempNode;
    
    if (node.left == null && node.right == null) {
      if (node.parent == null) {
        root = null;
      } else if (node.parent.right == node) {
        node.parent.right = null;
      } else if (node.parent.left == node) {
        node.parent.left = null;
      }
      return ;
    }
    
    if (node.left != null) {
      tempNode = node.left;
      for (eNode = node.left; eNode != null; eNode = eNode.right) {
        tempNode = eNode;
      }
      node.value = tempNode.value;

      if (node.left.right != null) {
        tempNode.parent.right = tempNode.left;
      } else {
        tempNode.parent.left = tempNode.left;
      }

      if (tempNode.left != null) tempNode.left.parent = tempNode.parent;
      
      return;
    }

    if (node.right == null) return;

    tempNode = node.right;

    node.value = tempNode.value;

    node.right = tempNode.right;
    if (node.right != null) node.right.parent = node;

    node.left = tempNode.left;
    if (node.left != null) node.left.parent = node;
  }
  
  class Node {

    T value;

    Node parent;

    Node left;

    Node right;

    Node(T value, Node parent) {
      this.value = value;
      this.parent = parent;
    }

  }

}
