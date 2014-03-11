/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.parser.HTMLParser;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;
import org.vietspider.token.attribute.Attributes;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 4, 2006
 */
public class TestHTMLParser {
  
  public static void testAttribute(HTMLNode n) {
//    System.out.println(n.getValue());
    Attributes attrs = AttributeParser.getInstance().get(n);
    System.out.println(" =-===  > "+ n.totalOfChildren());
//    for(Attribute attr : attrs){
//      System.out.println(attr.getName() +"  : "+attr.getValue());
//    }
//    attrs.set(new Attribute("background", "red"));
//    System.out.println(n.getValue());
////    attrs.set(new Attribute("background", "blue"));
////    System.out.println(n.getValue());
////    
//    List<Attribute> list  = new ArrayList<Attribute>();
//    attrs.add(new Attribute("border", "2"));
//    attrs.add(new Attribute("background", "yellow"));
//    attrs.add(new Attribute("id", "body_yahoo"));
////    attrs.set(list);
//    System.out.println(n.getValue());
//    
//    for(Attribute attr : attrs){
//      System.out.println(attr.getName() +" : "+attr.getValue());
//    }
    
    attrs.remove("background");
    
    System.out.println("cuoi cung ta co " + new String(n.getValue()));
  }


  public static void main(String[] args) throws Exception {
//    NodeImpl node = new NodeImpl("body id = yahoo class = \"css\'   background=\"color border=1 ".toCharArray(), Name.BODY);
    NodeImpl node = new NodeImpl("body background=\"color\"".toCharArray(), Name.BODY);
    node.setType(TypeToken.TAG);
    testAttribute(node);
//    print("",node);  
    
  }

}
