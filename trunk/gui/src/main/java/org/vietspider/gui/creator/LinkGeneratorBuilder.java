/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import static org.vietspider.model.SourceProperties.HOMEPAGE_TEMPLATE;
import static org.vietspider.model.SourceProperties.JS_ONCLICK_PATTERN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.vietspider.model.SourceProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 19, 2009  
 */
public class LinkGeneratorBuilder {
  
  public String buildLinkGenerator(Properties properties, String value) {
    HashMap<String, List<String>> map = new HashMap<String, List<String>>();

    if(value == null) value = "";
    if((value = value.trim()).length() > 0) {
      String [] elements = SourceProperties.splitGenerators(value);
      for(int i = 0; i < elements.length; i++) {
        build(map, elements[i].trim());
      }
    }

    value = properties.getProperty(JS_ONCLICK_PATTERN);
    if(value != null && !(value = value.trim()).isEmpty()) {
      String [] elements = value.split("\n");
      getList(map, "org.vietspider.crawl.link.generator.FunctionGenerator", elements, 0);
    }

    value = properties.getProperty(HOMEPAGE_TEMPLATE);
    if(value != null && !(value = value.trim()).isEmpty()) {
      String [] elements = value.split("\n");
      getList(map, "org.vietspider.crawl.link.generator.HomepageExtractor", elements, 0);
//    map.get("type org.vietspider.crawl.link.generator.HomepageExtractor").add(0, source.getFullName());
    }

    Iterator<String> iterator = map.keySet().iterator();
    StringBuilder builder = new StringBuilder();
    while(iterator.hasNext()) {
      String clazz = iterator.next();
      List<String> list = map.get(clazz);
      if(builder.length() > 0) builder.append("#\n");
      builder.append("type ").append(clazz).append('\n');
      for(int i = 0; i < list.size(); i++) {
        builder.append(list.get(i).trim()).append('\n');
      }
    }
    properties.remove(HOMEPAGE_TEMPLATE);
    properties.remove(JS_ONCLICK_PATTERN);
    return  builder.toString();
  }

  private void build(HashMap<String, List<String>> map, String value)  {
    if(value.trim().isEmpty()) return;
    String [] elements = value.split("\n");
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim();
    }
    
    if(elements[0].startsWith("type ")) {
      elements[0] = elements[0].substring(5).trim();
      getList(map, elements[0], elements, 1);
      return;
    }
    String type = toType(elements[0]);
    if(type == null) {
      detect(map, elements);
      return;
    }
    getList(map, type, elements, 1);
  }

  private String toType(String value) {
    if(value.equalsIgnoreCase("index")) {
      return "org.vietspider.crawl.link.generator.URLIndexGenerator";
    } else if(value.equalsIgnoreCase("word")) {
      return "org.vietspider.crawl.link.generator.URLWordGenerator";
    } else if(value.equalsIgnoreCase("homepage")) {
      return  "org.vietspider.crawl.link.generator.HomepageExtractor";
    } else if(value.equalsIgnoreCase("onclick")) {
      return "org.vietspider.crawl.link.generator.FunctionGenerator";
    }
    return null;
  }

  private void detect(HashMap<String, List<String>> map,  String [] elements) {
    if(elements.length > 1) {
      //Type 3
      if(elements[0].indexOf("[index]") > -1
          &&  elements[1].indexOf("->") > -1) {
        getList(map, "org.vietspider.crawl.link.generator.URLIndexGenerator", elements, 0);
        // @TODO need review
      } else if(elements[0].indexOf("[index]") > -1
          &&  elements[1].indexOf("[word]") > -1) {
        getList(map, "org.vietspider.crawl.link.generator.URLWordGenerator", elements, 0);
      }  else {
        //getList(map, ConvertLinkGenerator.class, elements, 0);
      }
    }  else {
      if(elements[0].indexOf("[word]") > -1) {
        getList(map, "org.vietspider.crawl.link.generator.URLWordGenerator", elements, 0);
      } else if(elements[0].indexOf("[vietspider.word]") > -1) {
        getList(map, "org.vietspider.crawl.link.generator.URLWordGenerator", elements, 0);
      } else {
        getList(map, "org.vietspider.crawl.link.generator.HomepageExtractor", elements, 0);
      }
    }
  }

  private List<String> getList(HashMap<String, List<String>> map, 
      String clazz, String [] data, int index) {
    List<String> list = map.get(clazz);
    if(list == null) {
      list = new ArrayList<String>();
      map.put(clazz, list);
    }

    for(int i = index; i < data.length; i++) {
      list.add(data[i]);
    }
    return list;
  }
  
//  public static void main(String[] args) {
//    String value = "http\n#\nindex";
//    System.out.println(value.split("\n[#]\n").length);
//  }
}
