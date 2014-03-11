/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.vietspider.serialize.GetterMap;
import org.vietspider.serialize.NodeMap;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 14, 2006
 */
public class Reflection {

  public static Object getValue(Object bean, Field field) throws Exception{
    NodeMap map = field.getAnnotation(NodeMap.class);    
    if(map != null){
      try{
        Method method = getMethodByNodeMap(bean, map);
        return method.invoke(bean);
      }catch(Exception exp){        
      }
    }    
    String name = getMethod('g', field.getName());
    try{
      Method method = bean.getClass().getMethod(name);
      if(method != null) return method.invoke(bean);
    }catch(Exception exp){      
    }
    if(!field.isAccessible()) field.setAccessible(true);
    return field.get(bean);    
  }

  private static Method getMethodByNodeMap(Object bean, NodeMap nodeMap) throws Exception {
    Method [] methods = bean.getClass().getMethods();
    String nameNode = nodeMap.value();
    for(Method ele : methods){
      GetterMap getterMap = ele.getAnnotation(GetterMap.class);      
      if(getterMap == null) continue;
      if(getterMap.value().equals(nameNode)) return ele;
    }
    return null;
  }
  
  private static String getMethod(char c, String name){
    char [] chars = new char[name.length()+3];
    chars[0] = c;
    chars[1] = 'e';
    chars[2] = 't';
    chars[3] = Character.toUpperCase(name.charAt(0));
    for(int i = 1 ; i<name.length(); i++){
      chars[i+3] = name.charAt(i);
    }
    return new String(chars);
  }

}
