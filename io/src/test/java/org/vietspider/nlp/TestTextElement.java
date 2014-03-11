/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp;

import java.io.File;

import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextSplitter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class TestTextElement {
  
  private static File folder = new File("D:\\Temp\\quan\\"); 
  
//  private static String load(String name) throws Exception {
//    URL url = TestTextElement.class.getResource(name);
//    File contentFile = new File(url.toURI());
//    return load(contentFile);
//  }
  
  public static String load(File file) throws Exception {
    return new String(RWData.getInstance().load(file), "utf-8");
  }
  
  public static void main(String[] args) throws Exception  {
     TextSplitter splitter = new TextSplitter();
     File file  = new File(folder, "201007142332560000.txt");
     String text = load(file);
     TextElement root = splitter.split(text);
     while(root != null) {
       System.out.println("========================");
       System.out.println(root.getValue());
       root = root.next();
     }
  }
}
