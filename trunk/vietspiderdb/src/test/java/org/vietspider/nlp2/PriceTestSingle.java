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
import org.vietspider.nlp.impl.PriceFilter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class PriceTestSingle {

  public static String load(File file) throws Exception {
    return new String(RWData.getInstance().load(file), "utf-8");
  }

  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\java\\test\\vscrawler\\data");
    
    PriceFilter.TEST = true;

    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    String folder_path = "D:\\Temp\\price_testcase\\";
//    String folder_path = "D:\\Temp\\no_mail\\";
    file = new File(folder_path + "b55.txt");
    if(!file.exists()) {
      System.err.println("file not found!");
      return;
    }
    String text = load(file);
    NlpProcessor processor = NlpProcessor.getProcessor();
    Map<Short, Collection<?>> map = processor.process(file.getName(), text);
    //  List<String> values = map.get(INlpFilter.PHONE);
    Collection<?> values = map.get(NLPData.PRICE);
    System.out.println(file.getName());
    System.out.println("result " + values);
    if(values != null) {
      System.out.println(" total item "+ values.size());
      Iterator<?> iterator = values.iterator();
      while(iterator.hasNext()) {
        System.out.println(iterator.next().toString());
      }
    }
    
//    double value = 488.99999999999994; 
//    value = Math.rint(value*1000)/1000;
//    System.out.println(value);
//    System.out.println((10000d/1000000d));
    
//    System.out.println('–'  + " : " + '-' + " : " + ('–' == '-'));
//    System.out.println((int)'–'  + " : " + (int)'-' + " : " + ('–' == '-'));
//    35 – 42 triệu / m2
  }

}
