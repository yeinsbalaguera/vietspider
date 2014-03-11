/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.util;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 8, 2009  
 */
public class BinaryTreeTest {
  
  public static void main(String[] args) {
    new BinaryTreeTest().run();
  }

  static class Node {
    Node left;

    Node right;

    int value;

    public Node(int value) {
      this.value = value;
    }
  }

  public void run() {
    // build the simple tree from chapter 11.
    Node root = new Node(5);
    System.out.println("Binary Tree Example");
    System.out.println("Building tree with root value " + root.value);
    
    int size  = 1000000;
    for(int i = 0; i < size; i++) {
      int value = 0;
      if(i < size/2) {
        value = (int)(Math.random()*size);
      } else {
        value = -(int)(Math.random()*size);
      }
      insert(root, value);
    }
    while(true) {
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
      }
    }
    
//    System.out.println("Traversing tree in order");
//    printInOrder(root);
//    System.out.println("Traversing tree front-to-back from location 7");
//    printFrontToBack(root, 7);
  }

  public void insert(Node node, int value) {
    if (value < node.value) {
      if (node.left != null) {
        insert(node.left, value);
      } else {
//        System.out.println("  Inserted " + value + " to left of " + node.value);
        node.left = new Node(value);
      }
    } else if (value > node.value) {
      if (node.right != null) {
        insert(node.right, value);
      } else {
//        System.out.println("  Inserted " + value + " to right of "
//            + node.value);
        node.right = new Node(value);
      }
    }
  }

  /*public void printInOrder(Node node) {
    if (node != null) {
      printInOrder(node.left);
      System.out.println("  Traversed " + node.value);
      printInOrder(node.right);
    }
  }

  public void printFrontToBack(Node node, int camera) {
    if (node == null)
      return;
    if (node.value > camera) {
      // print in order
      printFrontToBack(node.left, camera);
      System.out.println("  Traversed " + node.value);
      printFrontToBack(node.right, camera);
    } else if (node.value < camera) {
      // print reverse order
      printFrontToBack(node.right, camera);
      System.out.println("  Traversed " + node.value);
      printFrontToBack(node.left, camera);
    } else {
      // order doesn't matter
      printFrontToBack(node.left, camera);
      printFrontToBack(node.right, camera);
    }
  }*/
}
