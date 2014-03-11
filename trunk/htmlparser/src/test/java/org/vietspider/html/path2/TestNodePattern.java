/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

import org.vietspider.html.Name;
import org.vietspider.token.attribute.Attribute;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 11, 2007  
 */
public class TestNodePattern {
  
  public static void main(String[] args) {
    NodeExp nodeExp = new NodeExp(Name.TR, "*", null);
    
//    System.out.println(nodeExp.equals(new Node(Name.TR, 1, null)));
    
    Attribute [] attrs =  new Attribute[]{new Attribute("style", "a")};
    nodeExp = new NodeExp(Name.TR, "*", attrs );
    attrs =  new Attribute[]{new Attribute("style", "a")};
    System.out.println(nodeExp.equals(new Node(Name.TR, 2,  attrs)));
    
//    nodeExp = new NodeExp(Name.TR, "i>1&(i-(i:2))=2", null);
//    System.out.println(nodeExp.equals(new Node(Name.TR, 1, null)));
//    System.out.println(nodeExp.equals(new Node(Name.TR, 2, null)));
//    System.out.println(nodeExp.equals(new Node(Name.TR, 4, null)));
//    System.out.println(nodeExp.equals(new Node(Name.TR, 5, null)));
//    System.out.println(nodeExp.equals(new Node(Name.TR, 6, null)));
  }
}
