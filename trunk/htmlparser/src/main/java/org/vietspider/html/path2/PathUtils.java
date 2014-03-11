/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html.path2;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.Name;
import org.vietspider.token.attribute.Attribute;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 5, 2009  
 */
public class PathUtils {
  
  public static String normalize(List<String> list) {
    if(list.size() < 1) return null;
    if(list.size() < 2) return list.get(0);
    
    NodePathParser parser = new NodePathParser();
    NodePath path1 = null;
    try {
      path1 = parser.toPath(list.get(0));
    } catch (Exception e) {
      return list.get(0);
    }
    
    NodePath path2 = null;
    try {
      path2 = parser.toPath(list.get(list.size()-1));
    } catch (Exception e) {
      return list.get(0);
    }
    
    INode<?>[] inodes_1 = path1.getNodes();
    INode<?>[] inodes_2 = path2.getNodes();
    int size =  Math.min(inodes_1.length, inodes_2.length);
    List<INode<?>> inodes = new ArrayList<INode<?>>(size);
    for(int i = 0; i < size; i++) {
//      System.out.println(inodes_1[i].getcl + " : "+ inodes_2[i]);
      if(inodes_1[i].equals(inodes_2[i])) {
        inodes.add(inodes_1[i]);
      } else {
        inodes.add(new NodeExp((Name)inodes_1[i].getName(), "*", new Attribute[0]));
      }
    }
    
    NodePath path = new NodePath(inodes.toArray(new INode[inodes.size()]));
    return path.toString();
  }
  
  public static void main(String[] args) {
    List<String> list = new ArrayList<String>();
    list.add("DIV[1].DIV[5].TABLE[0].TBODY[0].TR[3].TD[1].DIV[0][class=postbody].A[0]");
    list.add("DIV[1].DIV[7].TABLE[0].TBODY[0].TR[9].TD[1].DIV[0][class=postbody]");
    
    System.out.println(normalize(list));
  }
  
}

