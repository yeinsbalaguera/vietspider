/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.vietspider.serialize.ToObjectCode.FunctionCode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2011  
 */
public class ToObjectCodeMap extends FunctionCode {
  @Override
  public void generate(Field field, 
      List<String> imports, StringBuilder builder, String name, Method method) throws Exception {
    Class<?> keyType = null;
    Class<?> valueType = null;
    
    importClazz(imports, field.getType());
    
    if(!Class.class.isInstance(field.getGenericType())) {
      ParameterizedType paramType = (ParameterizedType)field.getGenericType();
      if(paramType.getActualTypeArguments().length > 0) {
        keyType = (Class<?>)paramType.getActualTypeArguments()[0];
        valueType = (Class<?>)paramType.getActualTypeArguments()[1];
        
        importClazz(imports, keyType);
        importClazz(imports, valueType);
      }
    }
//    try {
//      ParameterizedType paramType = (ParameterizedType)field.getGenericType();
//      if(paramType.getActualTypeArguments().length > 0) {
//        keyType = (Class<?>)paramType.getActualTypeArguments()[0];
//        valueType = (Class<?>)paramType.getActualTypeArguments()[1];
//      }
//    } catch (Exception e) {
////      System.out.println("  ====  > "+ field.getGenericType());
////      throw e;
//      keyType = Object.class;
//      valueType = Object.class;
//    }
   
    
    if(field.getType().isInterface()) {
      builder.append("Map");
      if(keyType != null && valueType != null) {
        builder.append("<").append(keyType.getSimpleName());
        builder.append(",").append(valueType.getSimpleName()).append(">");
      }
      builder.append(" map = null;");
      builder.append('\n');
    } else {
      builder.append(((Class<?>)field.getType()).getSimpleName());
      if(keyType != null && valueType != null) {
        builder.append("<").append(keyType.getSimpleName());
        builder.append(",").append(valueType.getSimpleName()).append(">");
      }
      builder.append(" map = null;");
      builder.append('\n');
    }
    
    try {
      Method getter = ReflectUtil.getGetterMethod(field.getDeclaringClass(), field);
      if(getter != null) {
        builder.append("\t\t\t");
        builder.append("map = object.").append(getter.getName()).append("();");
        builder.append('\n');
      }
    } catch (Exception e) {
    }
    
    builder.append("\t\t\t");
    builder.append("if(map == null) map = new ");
    if(field.getType().isInterface()) {
      builder.append("HashMap");
    } else {
      builder.append(((Class<?>)field.getType()).getSimpleName());
    }
    if(keyType != null && valueType != null) {
      builder.append("<").append(keyType.getSimpleName());
      builder.append(",").append(valueType.getSimpleName()).append(">");
    }
    builder.append("();");
    builder.append('\n');
    
    builder.append("\t\t\t");
    builder.append("XML2Object.getInstance().mapProperties(map, ");
    if(keyType != null && valueType != null) {
      builder.append(keyType.getSimpleName()).append(".class, ");
      builder.append(valueType.getSimpleName()).append(".class,");
    } else {
      builder.append("null, null, ");
    }
    builder.append(" node);");
    builder.append('\n');
    
//    System.out.println(" ====================  > "+ elementType);
//    System.out.println(" da thu cai nau "+ fields[i].getType());
    builder.append("\t\t\t");
    if(method == null) {
      builder.append("object.").append(name).append("=map;");
    } else {
      builder.append("object.").append(method.getName()).append("(map);");
    }
  }
}
