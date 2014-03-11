/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.generator;

import static org.vietspider.model.SourceProperties.HOMEPAGE_TEMPLATE;
import static org.vietspider.model.SourceProperties.JS_ONCLICK_PATTERN;
import static org.vietspider.model.SourceProperties.LINK_GENERATOR;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.db.SystemProperties;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;
import org.vietspider.model.SourceProperties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 18, 2009  
 */
public class LinkGeneratorBuilder {
  
  public static void buildLinkGenerator(Source source) {
    Properties properties = source.getProperties();
    HashMap<Class<?>, List<String>> map = new HashMap<Class<?>, List<String>>();
    
    int save = 0;
    String value = properties.getProperty(LINK_GENERATOR);
    if(value == null) value = "";
    if((value = value.trim()).length() > 0) {
      String [] elements = SourceProperties.splitGenerators(value);
      for(int i = 0; i < elements.length; i++) {
        try {
          save += build(map, elements[i].trim());
        } catch (Exception e) {
          LogService.getInstance().setThrowable(source, e);
        }
      }
    }
    
    value = properties.getProperty(JS_ONCLICK_PATTERN);
    if(value != null && !(value = value.trim()).isEmpty()) {
      String [] elements = value.trim().split("\n");
      toList(map, FunctionGenerator.class, elements, 0);
      save++;
    }
    
    value = properties.getProperty(HOMEPAGE_TEMPLATE);
    if(value != null && !(value = value.trim()).isEmpty()) {
      String [] elements = value.split("\n");
      toList(map, HomepageExtractor.class, elements, 0);
      save++;
    }
    
    if(save > 0) {
      Iterator<Class<?>> iterator = map.keySet().iterator();
      StringBuilder builder = new StringBuilder();
      while(iterator.hasNext()) {
        Class<?> clazz = iterator.next();
        List<String> list = map.get(clazz);
        if(builder.length() > 0) builder.append("#\n");
        builder.append("type ").append(clazz.getName()).append('\n');
        for(int i = 0; i < list.size(); i++) {
          builder.append(list.get(i)).append('\n');
        }
      }
      Source tempSource = SourceIO.getInstance().saveProperty(source, LINK_GENERATOR, builder.toString());
      tempSource.getProperties().remove(HOMEPAGE_TEMPLATE);
      tempSource.getProperties().remove(JS_ONCLICK_PATTERN);
//      source.getProperties().setProperty(LINK_GENERATOR, builder.toString());
//      source.setSave(true);
    }
    
    if(map.get(FunctionGenerator.class) == null) {
      toList(map, FunctionGenerator.class, new String[0], 1);
    }
    
    Iterator<Class<?>> iterator = map.keySet().iterator();
    while(iterator.hasNext()) {
      Class<?> clazz = iterator.next();
      List<String> list = map.get(clazz);
      try {
        Class<?>[] classes = new Class[]{String.class, String[].class};
        Constructor<?> constructor = clazz.getDeclaredConstructor(classes);
        String [] values = list.toArray(new String[list.size()]);
        source.getLinkGenerators().add(constructor.newInstance(
            new Object[]{source.getFullName(), values}));
      } catch (Exception e) {
        LogService.getInstance().setThrowable(source, e, clazz.getName());
      }
    }
    
    if(Application.LICENSE == Install.SEARCH_SYSTEM) {
      SystemProperties system = SystemProperties.getInstance();
      if("true".equals(system.getValue("detect.website"))) {
        source.getLinkGenerators().add(new WebsiteDetector(source.getFullName()));
      }
    }
  }
  
  private static int build(HashMap<Class<?>, List<String>> map, String value) throws Exception {
    if(value.trim().isEmpty()) return 0;
    String [] elements = value.split("\n");
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim();
    }
    
    if(elements[0].startsWith("type ")) {
      elements[0] = elements[0].substring(5).trim();
      Class<?> clazz = Class.forName(elements[0]);
      toList(map, clazz, elements, 1);
      return 0;
    }
    return detect(map, elements);
  }
  
  private  static int detect(HashMap<Class<?>, List<String>> map,  String [] elements) throws Exception {
    if(elements.length > 1) {
      //Type 3
      if(elements[0].indexOf("[index]") > -1
          &&  elements[1].indexOf("->") > -1) {
        toList(map, URLIndexGenerator.class, elements, 0);
        // @TODO need review
      } else if(elements[0].indexOf("[index]") > -1
          &&  elements[1].indexOf("[word]") > -1) {
        toList(map, URLWordGenerator.class, elements, 0);
      }  else {
        //getList(map, ConvertLinkGenerator.class, elements, 0);
      }
    }  else {
      if(elements[0].indexOf("[vietspider.word]") > -1) {
        toList(map, URLWordGenerator.class, elements, 0);
      }  else if(elements[0].indexOf("[word]") > -1) {
        toList(map, URLWordGenerator.class, elements, 0);
      } else {
        toList(map, HomepageExtractor.class, elements, 0);
      }
    }
    return 1;
  }
  
  private static List<String> toList(HashMap<Class<?>, List<String>> map, 
      Class<?> clazz, String [] data, int index) {
    List<String> list = map.get(clazz);
    if(list == null) {
      list = new ArrayList<String>();
      map.put(clazz, list);
    }
    for(int i = index; i < data.length; i++) {
      list.add(data[i].trim());
    }
    return list;
  }
  
}
