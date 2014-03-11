/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 8, 2009  
 */
public class IntBinaryTree {
  
  private volatile Node root;
  private volatile int size = 0;
  
  public IntBinaryTree() {
  }
  
  public void clear() {
    root = null;
    size = 0;
  }
  
  public boolean isEmpty() {return root != null; }
  
  public int size() { return size; }
  
  public boolean insert(int value) {
    if(root != null) {
      return insert(root, value);
    } 
    root = new Node(value);
    return true;
  }
  
  public boolean add(int value) {
    if(root != null) {
      return insert(root, value);
    } 
    root = new Node(value);
    return true;
  }
  
  public boolean contains(int value) {
    return contains(root, value);
  }
  
  /*private boolean contains(Node node, int value) {
    while(true) {
      if (value < node.value) {
        if (node.left != null) {
          node = node.left;
          continue;
        }
        return false;
      } else if (value > node.value) {
        if (node.right != null) {
          node = node.right;
          continue;
        } 
        return false;
      }
      return true;
    }
  }
  
  private boolean insert(Node node, int value) {
    while(true) {
      if (value < node.value) {
        if (node.left != null) {
          
          node = node.left;
          continue;
        } 
        node.left = new Node(value);
        return true;
      } else if (value > node.value) {
        if (node.right != null) {
          System.out.println("chay mai " + node.hashCode());
          node = node.right;
          continue;
        } 
        node.right = new Node(value);
        return true;
      } 
      return false;
    }
  }*/
  
  private boolean contains(Node node, int value) {
    if (value < node.value) {
      if (node.left != null) {
        return contains(node.left, value);
      }
      return false;
    } else if (value > node.value) {
      if (node.right != null) {
        return contains(node.right, value);
      } 
      return false;
    }
    return true;
  }
  
  private boolean insert(Node node, int value) {
    if (value < node.value) {
      if (node.left != null) {
        return insert(node.left, value);
      } 
      node.left = new Node(value);
      return true;
    } else if (value > node.value) {
      if (node.right != null) {
        return insert(node.right, value);
      } 
      node.right = new Node(value);
      return true;
    }
    return false;
  }

  private class Node {
    
    Node left;

    Node right;

    int value;
    
    private Node(int value) {
      this.value = value;
      size++;
    }
    
  }
  
  public int[] array() {
    if(root == null) return new int[0];
    int [] array = new int[size];
    set(array, root, size-1);
    return array;
  }

  private int set(int [] array, Node node, int index) {
    if(node.right != null) {
      index = set(array, node.right, index);
    }

    if(node.left != null) {
      int value = set(array, node.left, index-1);
      array[index] = node.value;
      return value;
    }
    
    array[index] = node.value;
    return index - 1;  
  }
  
}
