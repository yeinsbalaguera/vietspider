/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.vietspider.bean.NLPData;
import org.vietspider.common.Application;
import org.vietspider.nlp.query.QueryAnalyzer;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 13, 2011  
 */
public class QuerySingleTest {
  
  public static void main(String[] args) throws Exception {
    File file = new File("D:\\java\\test\\data\\");

    //    UtilFile.FOLDER_DATA = file.getCanonicalPath();
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    Application.PRINT = false;

    String text = "căn hộ đống đa";
//    System.out.println(text);
    QueryAnalyzer analyzer = QueryAnalyzer.getProcessor();
    Map<Short, Collection<?>> map = analyzer.process(text);
    System.out.println("file name: " + file.getName());
   
//    print(map, NLPData.NORMAL_TEXT);
    print(map, NLPData.ACTION_OBJECT);
//    print(map, NLPData.ADDRESS);
//    print(map, NLPData.CITY);
//    print(map, NLPData.PRICE);
//    print(map, NLPData.AREA);
  }
  
  private static void print(Map<Short, Collection<?>> map, short type) {
    System.out.println(" type = "+ type);
    Collection<?> values = map.get(type);
    System.out.println("result " + values);
    if(values == null) return ;
    System.out.println(" total item "+ values.size());
    Iterator<?> iterator = values.iterator();
    while(iterator.hasNext()) {
      System.out.println(iterator.next().toString());
    }
  }
}
