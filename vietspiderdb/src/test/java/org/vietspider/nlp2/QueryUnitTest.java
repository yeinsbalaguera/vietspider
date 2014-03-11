/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp2;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.vietspider.bean.NLPData;
import org.vietspider.nlp.impl.Price;
import org.vietspider.nlp.impl.Unit;
import org.vietspider.nlp.impl.ao.ActionObject;
import org.vietspider.nlp.query.QueryAnalyzer;
import org.vietspider.nlp.query.ValueRange;
import org.vietspider.nlp.query.ValueRange.DoubleValueRange;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 20, 2011  
 */
@SuppressWarnings("unchecked")
public class QueryUnitTest extends TestCase {
  
  protected void setUp() throws Exception {
    File file  = new File("D:\\java\\test\\vscrawler\\data");

    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
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
  
  private static void printAO(Map<Short, Collection<?>> map, short type) {
    System.out.println(" type = "+ type);
    Collection<?> values = map.get(type);
    System.out.println("result " + values);
    if(values == null) return ;
    System.out.println(" total item "+ values.size());
    Iterator<?> iterator = values.iterator();
    while(iterator.hasNext()) {
      System.out.println(NLPData.action_object(iterator.next().toString()));
    }
  }
  
  private Unit area(Map<Short, Collection<?>> map, int index) {
    List<Unit> list = (List<Unit>)map.get(NLPData.AREA);
    if(list == null || list.size() == 0) return null;
    return list.get(index);
  }
  
  private String owner(Map<Short, Collection<?>> map) {
    List<String> list = (List<String>)map.get(NLPData.OWNER);
    if(list == null || list.size() == 0) return null;
    return list.get(0);
  }
  
  private Price price(Map<Short, Collection<?>> map, int index) {
    List<Price> list = (List<Price>)map.get(NLPData.PRICE);
    if(list == null || list.size() == 0) return null;
    return list.get(index);
  }
  
  private String address(Map<Short, Collection<?>> map, int index) {
    List<String> list = (List<String>)map.get(NLPData.ADDRESS);
    if(list == null || list.size() == 0) return null;
    return list.get(index);
  }
  
  private String city(Map<Short, Collection<?>> map, int index) {
    List<String> list = (List<String>)map.get(NLPData.CITY);
    if(list == null || list.size() == 0) return null;
    return list.get(index);
  }
  
  private String ao(Map<Short, Collection<?>> map, int index) {
    List<ActionObject> list = (List<ActionObject>)map.get(NLPData.ACTION_OBJECT);
    if(list == null || list.size() == 0) return null;
    return NLPData.action_object(list.get(index).getData());
  }
  
  private String text(Map<Short, Collection<?>> map, int index) {
    List<String> list = (List<String>)map.get(NLPData.NORMAL_TEXT);
    if(list == null || list.size() == 0) return null;
    return list.get(index);
  }
  
  @Test
  public void test() throws Exception {
    QueryAnalyzer analyzer = QueryAnalyzer.getProcessor();
    
    Map<Short, Collection<?>> map = analyzer.process("nhà hn");
    assertEquals("hà nội", city(map, 0));
    
    map = analyzer.process("bán nhà tại hn");
    assertEquals("hà nội", city(map, 0));
    assertEquals("Bán nhà", ao(map, 0));
    
    map = analyzer.process("bán nhà tại hn cuoc song moi giá");
    assertEquals("hà nội", city(map, 0));
    assertEquals("Bán nhà", ao(map, 0));
    assertEquals("tại", text(map, 0));
    assertEquals("cuoc song moi giá", text(map, 1));
    
    map = analyzer.process("thuê nhà gia re cần thơ");
    assertEquals("cần thơ", city(map, 0));
    assertEquals("Cần thuê nhà", ao(map, 0));
    assertEquals("gia re", text(map, 0));
    
    map = analyzer.process("cho thuê nhà gia re cần thơ");
    assertEquals("Cho thuê nhà", ao(map, 0));
    
    map = analyzer.process("phong cho thue gia re");
    assertEquals("Cho thuê phòng", ao(map, 0));
    assertEquals("gia re", text(map, 0));
    
    map = analyzer.process("bán chung cu cao cap");
    assertEquals("Bán căn hộ", ao(map, 0));
    assertEquals("cao cap", text(map, 0));
    
    map = analyzer.process("nhà giá rẻ");
    assertEquals("nhà giá rẻ", text(map, 0));
    
    map = analyzer.process("nha Trung văn");
    assertEquals("trung văn", address(map, 0));
    assertEquals("nha", text(map, 0));
    
    map = analyzer.process("HCM Cần thuê căn hộ q.tân phú");
    assertEquals("Cần thuê căn hộ", ao(map, 0));
    assertEquals("thành phố hồ chí minh", city(map, 0));
    assertEquals("quận tân phú", address(map, 0));
    
    map = analyzer.process("  ban chung cu q.tân phú HCM tầm 500tr");
    assertEquals("Bán căn hộ", ao(map, 0));
    assertEquals("thành phố hồ chí minh", city(map, 0));
    assertEquals("quận tân phú", address(map, 0));
    assertEquals(500*1000*1000d, price(map, 0).getValue());
    assertEquals(Price.UNIT_TOTAL, price(map, 0).getUnit());
    
    map = analyzer.process("  cho thue chung cu  2000$");
    assertEquals("Cho thuê căn hộ", ao(map, 0));
    assertEquals("42 triệu/tháng", price(map, 0).toString());
    assertEquals(Price.UNIT_MONTH, price(map, 0).getUnit());
    
    map = analyzer.process("  phong  cho thue hn tu 1tr toi 3 trieu");
    assertEquals("hà nội", city(map, 0));
    assertEquals("Cho thuê phòng", ao(map, 0));
    assertEquals("1 triệu/tháng", price(map, 0).toString());
    assertEquals(Price.UNIT_MONTH, price(map, 0).getUnit());
    assertEquals("3 triệu/tháng", price(map, 1).toString());
    
    map = analyzer.process(" hn   cho thuê  phòng tu 1tr/m toi 3 trieu/m2");
    assertEquals("hà nội", city(map, 0));
    assertEquals("Cho thuê phòng", ao(map, 0));
    assertEquals("1 triệu/m2", price(map, 0).toString());
    assertEquals(Price.UNIT_M2, price(map, 0).getUnit());
    assertEquals("3 triệu/m2", price(map, 1).toString());
    
    map = analyzer.process(" hn phong cho thue tu 1tr/m toi 3 trieu/m2");
    assertEquals("hà nội", city(map, 0));
    assertEquals(null, ao(map, 0));
    assertEquals("1 triệu/m2", price(map, 0).toString());
    assertEquals(Price.UNIT_M2, price(map, 0).getUnit());
    assertEquals("3 triệu/m2", price(map, 1).toString());
    assertEquals("phong cho thue tu", text(map, 0));
    assertEquals("toi", text(map, 1));
    
    map = analyzer.process(" phong cho thue dien tich 22m2");
    assertEquals(22f, area(map, 0).getValue());
    assertEquals("Cho thuê phòng", ao(map, 0));
    assertEquals("dien tich", text(map, 0));
    
    map = analyzer.process(" phong cho thue 22m2");
    assertEquals(22f, area(map, 0).getValue());
    assertEquals("Cho thuê phòng", ao(map, 0));
    
    map = analyzer.process(" phong cho thue 22m2 den 25m2");
    assertEquals(22f, area(map, 0).getValue());
    assertEquals(25f, area(map, 0).getNext().getValue());
    assertEquals("Cho thuê phòng", ao(map, 0));
    
    map = analyzer.process("ban nha 100m2 gia 2ty");
    assertEquals(100f, area(map, 0).getValue());
    assertEquals(2000*1000*1000d, price(map, 0).getValue());
    assertEquals(Price.UNIT_TOTAL, price(map, 0).getUnit());
    assertEquals("Bán nhà", ao(map, 0));
    
    map = analyzer.process("ban nha chinh chu 100m2 gia 2ty");
    assertEquals(100f, area(map, 0).getValue());
    assertEquals(2000*1000*1000d, price(map, 0).getValue());
    assertEquals(Price.UNIT_TOTAL, price(map, 0).getUnit());
    assertEquals("Bán nhà", ao(map, 0));
    assertEquals("true", owner(map));
    assertEquals("gia", text(map, 0));
    
    map = analyzer.process("căn hộ 30m2 - 40m2");
    assertEquals(30f, area(map, 0).getValue());
    assertEquals(40f, area(map, 0).getNext().getValue());
    assertEquals(null, text(map, 0));
    
    map = analyzer.process("nhà 500 triệu");
    int hashCode1 = map.hashCode();
//    System.out.println(" === > "+ map.hashCode());
    assertEquals(500*1000*1000d, price(map, 0).getValue());
    assertEquals("nhà", text(map, 0));
    
    map = analyzer.process("nhà 500 triệu");
    int hashCode2 = map.hashCode();
//    System.out.println(" === > "+ map.hashCode());
    assertEquals(hashCode1, hashCode2);
    DoubleValueRange range = analyzer.getRange(map, ValueRange.PRICE_TOTAL_RANGE);
    assertEquals(1, range.size());
    
    printAO(map, NLPData.ACTION_OBJECT);
    print(map, NLPData.PRICE);
//    print(map, NLPData.CITY);
//    print(map, NLPData.AREA);
//    print(map, NLPData.ADDRESS);
    print(map, NLPData.NORMAL_TEXT);
  }
}
