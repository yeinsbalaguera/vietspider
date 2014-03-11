/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.serialize;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 9, 2007
 */
public class ReflectUtil {
  
  public static Method getSetterMethod(Class<?> clazz, Field field) throws Exception {
    String mapName = getMapName(field);
    Method method = null;
    if(mapName != null) method = getSetterMethodByMap(clazz, mapName);
    if(method != null) return method;
    
    method = getMethodByName(clazz, getSetterOrGetter('s', field.getName()));
    if(method != null) return method;
    
    method = getMethodByName(clazz, mapName);
    return method;
  }
  
  public static Method getGetterMethod(Class<?> clazz, Field field) throws Exception {
    String mapName = getMapName(field);
    Method method = null;
    if(mapName != null) method = getGetterMethodByMap(clazz, mapName);    
//    System.out.println(mapName +" : "+ method);
    if(method != null) return method;
    
    method = getMethodByName(clazz, getSetterOrGetter('g', field.getName()));
    if(method != null) return method;
    
    method = getMethodByName(clazz, getIsMethod(field.getName()));
    if(method != null) return method;
    
    method = getMethodByName(clazz, mapName);
    return method;
  }
  
  private static String getMapName(Field field) {
    NodeMap nodeMap = field.getAnnotation(NodeMap.class);
    if(nodeMap != null) return nodeMap.value();
    
    NodesMap nodesMap = field.getAnnotation(NodesMap.class);
    if(nodesMap != null) return nodesMap.value();
    
    PropertiesMap propretiesMap = field.getAnnotation(PropertiesMap.class);
    return propretiesMap == null ? null : propretiesMap.value();
  }
  
  private static Method getSetterMethodByMap(Class<?> clazz, String mapName) {
    if(clazz == null) return  null;
    Method [] methods = clazz.getDeclaredMethods();
    for(Method ele : methods){      
      SetterMap getterMap = ele.getAnnotation(SetterMap.class);      
      if(getterMap == null) continue;
//      System.out.println(mapName + " : "+ele +  " : " + getterMap.value().equals(mapName));
      if(getterMap.value().equals(mapName)) return ele;
    }
    Method method = getSetterMethodByMap(clazz.getSuperclass(), mapName);
    if(method != null) return method;
    return null;
  }
  
  private static Method getGetterMethodByMap(Class<?> clazz, String mapName) {
    if(clazz == null) return  null;
    Method [] methods = clazz.getDeclaredMethods();
    for(Method ele : methods){      
      GetterMap getterMap = ele.getAnnotation(GetterMap.class); 
      if(getterMap == null) continue;
      if(getterMap.value().equals(mapName)) return ele;
    }
    Method method = getGetterMethodByMap(clazz.getSuperclass(), mapName);
    if(method != null) return method;
    return null;
  }
  
  private static Method getMethodByName(Class<?> clazz, String name) {
    Method method = getPublicMethodByName(clazz, name);
    if(method != null) return method;
    method = getProtectedMethodByName(clazz, name);
    if(method != null) return method;
    return null;
  }
  
  private static Method getPublicMethodByName(Class<?> clazz, String name) {
    Method [] methods = clazz.getMethods();
    for(Method ele : methods){
      if(name.equals(ele.getName())) return ele;
    }
    return null;
  }
  
  private static Method getProtectedMethodByName(Class<?> clazz, String name) {
    if(clazz == null) return null;
    Method [] methods = clazz.getDeclaredMethods();
    for(Method ele : methods){
      if(ele.isAccessible()) continue;
      if(name.equals(ele.getName())) return ele;
    }
    Method method = getProtectedMethodByName(clazz.getSuperclass(), name);
    if(method != null) return method;
    return null;
  }
  
  private static String getSetterOrGetter(char c, String name){
    char [] chars = new char[name.length()+3];
    chars[0] = c;
    chars[1] = 'e';
    chars[2] = 't';
    chars[3] = Character.toUpperCase(name.charAt(0));
    for(int i = 1 ; i < name.length(); i++){
      chars[i+3] = name.charAt(i);
    }
//    System.out.println(" chuan bi time "+ new String(chars));
    return new String(chars);
  }
  
  private static String getIsMethod(String name) {
    char [] chars = new char[name.length()+2];
    chars[0] = 'i';
    chars[1] = 's';
    chars[2] = Character.toUpperCase(name.charAt(0));
    for(int i = 1 ; i<name.length(); i++){
      chars[i+2] = name.charAt(i);
    }
    return new String(chars);
  }
  
  public static boolean isPrimitiveType(Class<?> type) {
    return type == Integer.class 
              || type == Long.class
              || type == Boolean.class
              || type == Double.class
              || type == Float.class
              || type == Short.class
              || type == Byte.class
              || type == Short.class
              || type == Character.class
              || type == String.class
              || type == StringBuffer.class
              || type == StringBuilder.class; 
  }
  
  public static boolean isPrimitive(Class<?> type) {
    return  type == String.class
        || type == StringBuilder.class || type == StringBuffer.class
        || type == char.class || type  == Character.class
        
        || type == Date.class || type == Calendar.class
        
        
        || type == short.class || type == Short.class
        || type == int.class || type == Integer.class
        || type == long.class || type == Long.class
        || type == float.class || type == Float.class
        || type == double.class || type == Double.class
        
        || type == byte.class || type == Byte.class
        || type == boolean.class || type == Boolean.class
        
        || type.isEnum()
        ;
  }
  
}
