/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.vietspider.bean.NLPData;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.nlp.NlpProcessor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class TestSingleFile {
  
  public static String load(File file) throws Exception {
    return new String(RWData.getInstance().load(file), "utf-8");
  }

  //  private static void print(short type, TextElement root) {
  //    TextElement element = root;
  //    System.out.println("=========================================");
  //    while(element != null) {
  //      if(element.getScore(type) > 0) { 
  //        System.out.println(element.getValue());
  //      }
  //      element = element.getNext();
  //    }
  //  }

  public static void main(String[] args) throws Exception {
    //    TextSplitter splitter = new TextSplitter();
    NlpProcessor processor = NlpProcessor.getProcessor();
    String folder_path = "D:\\Temp\\address_testcase\\";

    //data_1.txt
    //201101090933310067.txt
//    File file = new File("D:\\Temp\\price_testcase\\201012292344140050.txt");
    File file = new File(folder_path + "201101031434480029.txt");
    if(!file.exists()) {
      System.err.println("file not found!");
      return;
    }
    String text = load(file);
    Map<Short, Collection<?>> map = processor.process(file.getName(), text);
//    List<String> values = map.get(INlpFilter.PHONE);
    Collection<?> values = map.get(NLPData.ADDRESS);
    System.out.println(file.getName());
    System.out.println("result " + values);
    if(values != null) {
      System.out.println(" total item "+ values.size());
      Iterator<?> iterator = values.iterator();
      while(iterator.hasNext()) {
        System.out.println(iterator.next());
      }
    }
  }

}
