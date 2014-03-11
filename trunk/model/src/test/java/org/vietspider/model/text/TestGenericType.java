/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model.text;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 8, 2007  
 */
public class TestGenericType {
  
  public HashMap<String, Integer> hashMap  = new HashMap<String, Integer>(); 
  
  public static void main(String[] args) throws Exception {
    Field field = TestGenericType.class.getDeclaredField("hashMap");
    Type eleParamType = Object.class;
    ParameterizedType paramType = (ParameterizedType)field.getGenericType();
    if(paramType.getActualTypeArguments().length > 0) {
      eleParamType = paramType.getActualTypeArguments()[0];
      System.out.println(eleParamType);
    }  
  }
}
