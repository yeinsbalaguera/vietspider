/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.vietspider.bean.NLPData;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.nlp.NlpProcessor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 16, 2011  
 */
public class PriceUnitTest2 extends TestCase {

  private NlpProcessor filder = NlpProcessor.getProcessor();
  private File folder = new File("D:\\Temp\\price_testcase\\");
  
  @Override
  protected void setUp() throws Exception {
    File file  = new File("D:\\java\\test\\vscrawler\\data");

    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
  }

  @SuppressWarnings("unchecked")
  public List<String> extract(String name) throws Exception {
    File file = new File(folder, name);
    String text = new String(RWData.getInstance().load(file), "utf-8");
    Map<Short, Collection<?>> map = filder.process(name, text);
    return (List<String>)map.get(NLPData.PRICE);
  }

  @Test
  public void test() throws Exception {
    List<String> values = extract("b4.txt");
    assertEquals("94.828 triệu/m2", values.get(1).toString());

    values = extract("b6.txt");
    assertEquals("3 tỷ/tổng", values.get(0).toString());

    values = extract("b7.txt");
    assertEquals("67.568 triệu/m2", values.get(1).toString());

    values = extract("b8.txt");
    assertEquals("93 triệu/m2", values.get(1).toString());

    values = extract("b9.txt");
    assertEquals("91.667 triệu/m2", values.get(1).toString());

    values = extract("b10.txt");
    assertEquals("106.5 triệu/m2", values.get(1).toString());

    values = extract("b11.txt");
    assertEquals("70.833 triệu/m2", values.get(1).toString());

    values = extract("b12.txt");
    assertEquals("65 triệu/m2", values.get(0).toString());

    values = extract("b13.txt");
    assertEquals("94.118 triệu/m2", values.get(1).toString());

    values = extract("b15.txt");
    assertEquals("91.667 triệu/m2", values.get(1).toString());

    values = extract("b16.txt");
    assertEquals("87.5 triệu/m2", values.get(1).toString());

    values = extract("b17.txt");
    assertEquals("100 triệu/m2", values.get(1).toString());

    values = extract("b18.txt");
    assertEquals("43.537 triệu/m2", values.get(1).toString());

    values = extract("b19.txt");
    assertEquals("70 triệu/m2", values.get(1).toString());

    values = extract("b21.txt");
    assertEquals("155 triệu/m2", values.get(1).toString());

    values = extract("b22.txt");
    assertEquals("91.429 triệu/m2", values.get(1).toString());

    values = extract("b23.txt");
    assertEquals("80 triệu/m2", values.get(1).toString());

    values = extract("b24.txt");
    assertEquals("192 triệu/m2", values.get(2).toString());

    values = extract("b25.txt");
    assertEquals("144.615 triệu/m2", values.get(1).toString());

    values = extract("b26.txt");
    assertEquals("228.956 triệu/m2", values.get(1).toString());

    values = extract("b27.txt");
    assertEquals("180 triệu/m2", values.get(1).toString());

    values = extract("b28.txt");
    assertEquals("196.667 triệu/m2", values.get(1).toString());

    values = extract("b29.txt");
    assertEquals("132.353 triệu/m2", values.get(1).toString());

    values = extract("b30.txt");
    assertEquals("171.875 triệu/m2", values.get(1).toString());

    values = extract("b31.txt");
    assertEquals("86.047 triệu/m2", values.get(1).toString());

    values = extract("b32.txt");
    assertEquals("131.579 triệu/m2", values.get(1).toString());

    values = extract("b33.txt");
    assertEquals("272.727 triệu/m2", values.get(1).toString());

    values = extract("b34.txt");
    assertEquals("290.909 triệu/m2", values.get(1).toString());

    values = extract("b35.txt");
    assertEquals("17.5 triệu/m2", values.get(1).toString());

    values = extract("b36.txt");
    assertEquals("275 triệu/m2", values.get(1).toString());

    values = extract("b37.txt");
    assertEquals("107.563 triệu/m2", values.get(1).toString());

    values = extract("b38.txt");
    assertEquals("110.924 triệu/m2", values.get(1).toString());

    values = extract("b40.txt");
    assertEquals("125 triệu/m2", values.get(2).toString());

    values = extract("b42.txt");
    assertEquals("66.667 triệu/m2", values.get(1).toString());

    values = extract("b46.txt");
    assertEquals("250 triệu/tổng", values.get(0).toString());

    values = extract("b47.txt");
    assertEquals("79.452 triệu/m2", values.get(2).toString());

    values = extract("b48.txt");
    assertEquals("2.8 tỷ/tổng", values.get(1).toString());

    values = extract("b49.txt");
    assertEquals("67.523 triệu/m2", values.get(1).toString());

    values = extract("b51.txt");
    assertEquals("80 triệu/m2", values.get(2).toString());

    values = extract("b55.txt");
    assertEquals("201.22 triệu/m2", values.get(1).toString());

//    values = extract("");
//    assertEquals("", values.get(1).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());
//
//    values = extract("");
//    assertEquals("", values.get(0).toString());


    if(values == null) return; 
    for(String ele : values) {
      System.out.println(ele);
    }

  }


}
