/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;

import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.nlp.text.LineSplitter;
import org.vietspider.nlp.text.TextElement;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2011  
 */
public class TestSplit {
  
  public static void main(String[] args) throws Exception {
    LineSplitter splitter = new LineSplitter();
    String folder_path = "D:\\Temp\\address_testcase\\";
    File file = new File(folder_path + "34.txt");
    String text = new String(RWData.getInstance().load(file), "utf-8");
    TextElement element = splitter.split(text);
    while(element != null) {
      System.out.println(element.getValue());
      System.out.println("================================================");
      element = element.next();
    }
    
  }
}
