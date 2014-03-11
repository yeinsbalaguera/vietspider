/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.vietspider.bean.NLPData;
import org.vietspider.common.io.RWData;
import org.vietspider.nlp.NlpProcessor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class ActionObjectTestSingle {

  public static String load(File file) throws Exception {
    return new String(RWData.getInstance().load(file), "utf-8");
  }

  public static void main(String[] args) throws Exception {
    String folder_path = "D:\\Temp\\ao_testcase\\";
    File file = new File(folder_path + "a40.txt");
    if(!file.exists()) {
      System.err.println("file not found!");
      return;
    }

    String text = load(file);
    NlpProcessor processor = NlpProcessor.getProcessor();
    Map<Short, Collection<?>> map = processor.process(file.getName(), text);
    //  List<String> values = map.get(INlpFilter.PHONE);
    Collection<?> values = map.get(NLPData.ACTION_OBJECT);
    System.out.println(file.getName());
    System.out.println("result " + values);
    if(values != null) {
      System.out.println(" total item "+ values.size());
      Iterator<?> iterator = values.iterator();
      while(iterator.hasNext()) {
        System.out.println(NLPData.action_object(iterator.next().toString()));
      }
    }
  }

}
