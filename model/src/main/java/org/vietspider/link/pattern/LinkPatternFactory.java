/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.LogService;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.link.pattern.model.IPattern;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 21, 2008  
 */
public class LinkPatternFactory <T extends IPattern, E extends AbsPatterns<T>> {
  
  public static <E extends AbsPatterns<?>> E createPatterns(Class<E> cClazz, Properties properties, String key) {
    if(!properties.containsKey(key)) return null;
    String value = properties.getProperty(key);
    if(value == null || (value = value.trim()).length() < 1) return null;
    String [] elements = value.split("\n");
    if(elements == null || elements.length < 1) return null;
    return createPatterns(cClazz, elements);
  }
  
  @SuppressWarnings("all")
  public static <E extends AbsPatterns<?>> E createPatterns(Class<E> cClazz, String...elements) {
    URLEncoder encoder = new URLEncoder();
    if(elements == null || elements.length < 1) return null;

    Class<?> pClazz = null;
    try {
      java.lang.reflect.Type type  = cClazz.getGenericSuperclass();
      pClazz = (Class<?>)((ParameterizedType)type).getActualTypeArguments()[0];
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    if(pClazz == null) return null;
    
    Object [] objects = (Object[])Array.newInstance(pClazz, elements.length); 
    for(int i = 0; i < elements.length; i++) {
      if(elements[i] == null || (elements[i] = elements[i].trim()).isEmpty()) continue;
      try {
        objects[i] = pClazz.newInstance();
        String patternURL = elements[i];//encoder.encode(elements[i]);
        IPattern absPattern = ((IPattern)objects[i]);
        absPattern.setValue(patternURL);
//        absPattern.setEndWith(patternURL.charAt(patternURL.length() - 1) == '*');
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        objects[i] = null;
      }
    }
    
    try {
      Constructor<E> constructor = cClazz.getDeclaredConstructor(objects.getClass());
      return constructor.newInstance(new Object[]{objects});
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  
  @SuppressWarnings("all")
  public static <E extends AbsPatterns<?>> E createMultiPatterns (
                Class<E> cClazz, Properties properties, String key) {
    if(!properties.containsKey(key)) return null;

    String value = properties.getProperty(key);
    if(value == null || (value = value.trim()).length() < 1) return null;
    String [] elements = value.split("\n");
    
    return createMultiPatterns(cClazz, elements);
  }
  
    @SuppressWarnings("all")
  public static <E extends AbsPatterns<?>> E createMultiPatterns(Class<E> cClazz, String [] elements) {   
    List<String> listPattern = new ArrayList<String>();
    List<String> listTemplate = new ArrayList<String>();
    int size = elements.length;
    if(size%2 != 0) size--;
    
    URLDecoder decoder = new URLDecoder();
    for(int i  = 0; i < size; i += 2) {
      listPattern.add(elements[i]);
      listTemplate.add(elements[i+1]);
      
      try {
        listPattern.add(decoder.decode(elements[i], "utf-8"));
        listTemplate.add(elements[i+1]);
      } catch (Exception e) {
      }
    }
    
    String [] patterns = listPattern.toArray(new String[listPattern.size()]);
    String [] templates = listTemplate.toArray(new String[listTemplate.size()]);
    
    Class<? extends IPattern> pClazz = null;
    try {
      java.lang.reflect.Type type  = cClazz.getGenericSuperclass();
      pClazz = (Class<? extends IPattern>)((ParameterizedType)type).getActualTypeArguments()[0];
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    IPattern [] itemplates = createTemplates(pClazz, patterns, templates);
    
    try {
      Constructor<E> constructor = cClazz.getDeclaredConstructor(itemplates.getClass());
      return constructor.newInstance(new Object[]{itemplates});
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
  }
  
  @SuppressWarnings("all")
  public static <T extends IPattern> T[] createTemplates(
      Class<T> tClazz, String [] patterns, String [] templates) {
    
    URLEncoder encoder = new URLEncoder();
    if(patterns == null || patterns.length < 1) return null;
    
    T [] objects = (T[])Array.newInstance(tClazz, patterns.length); 
    for(int i = 0; i < patterns.length; i++) {
      if(i >= templates.length) continue;
      if(patterns[i] == null || (patterns[i] = patterns[i].trim()).isEmpty()) continue;
      String lcasePattern = patterns[i].toLowerCase();
      if(SWProtocol.isHttp(lcasePattern)) {
         patterns[i] = encoder.encode(patterns[i]);
      } 
      
      try {
        objects[i] = tClazz.newInstance();
        ((IPattern)objects[i]).setValue(patterns[i], templates[i]);
      } catch (Exception e) {
        objects[i] = null;
      }
    }
    return objects;
  }
  
  
 
}
