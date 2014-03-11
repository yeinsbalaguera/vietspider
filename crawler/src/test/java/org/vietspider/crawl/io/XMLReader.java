/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 26, 2009  
 */
public class XMLReader {
  public static void main(String argv[]) {
    try {
      File file = new File("D:\\Temp\\temp_xml.xml");
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(file);
      doc.getDocumentElement().normalize();
      System.out.println("Root element " + doc.getDocumentElement().getNodeName());
      NodeList nodeLst = doc.getElementsByTagName("property");
      System.out.println("Information of all employees");

      for (int s = 0; s < nodeLst.getLength(); s++) {
        Node fstNode = nodeLst.item(s);
        System.out.println(fstNode.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
