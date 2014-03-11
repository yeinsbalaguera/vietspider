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
public class ToObjectCodeList extends FunctionCode {
  @Override
  public void generate(Field field, 
      List<String> imports, StringBuilder builder, String name, Method method) {
    Class<?> elementType = null;
    
    if(!Class.class.isInstance(field.getGenericType())) {
      ParameterizedType paramType = (ParameterizedType)field.getGenericType();
      if(paramType.getActualTypeArguments().length > 0) {
        elementType = (Class<?>)paramType.getActualTypeArguments()[0];
        importClazz(imports, elementType);
      }
    }
    
    
    if(field.getType().isInterface()) {
      builder.append("List");
      if(elementType != null) {
        builder.append("<").append(elementType.getSimpleName()).append(">");
      }
      builder.append(" list = null;");
      builder.append('\n');
    } else {
      builder.append(((Class<?>)field.getType()).getSimpleName());
      if(elementType != null) {
        builder.append("<").append(elementType.getSimpleName()).append(">");
      }
      builder.append(" list = null;");
      builder.append('\n');
    }
    
    try {
      Method getter = ReflectUtil.getGetterMethod(field.getDeclaringClass(), field);
      if(getter != null) {
        builder.append("\t\t\t");
        builder.append("list = object.").append(getter.getName()).append("();");
        builder.append('\n');
      }
    } catch (Exception e) {
    }
    
    builder.append("\t\t\t");
    builder.append("if(list == null) list = new ");
    if(field.getType().isInterface()) {
      builder.append("ArrayList");
    } else {
      builder.append(((Class<?>)field.getType()).getSimpleName());
    }
    if(elementType != null) {
      builder.append("<").append(elementType.getSimpleName()).append(">");
    }
    builder.append("();");
    builder.append('\n');
    
    builder.append("\t\t\t");
    builder.append("XML2Object.getInstance().mapCollection(list, ");
    if(elementType != null) {
      builder.append(elementType.getSimpleName()).append(".class");
    } else {
      builder.append("null");
    }
    builder.append(", node);");
    builder.append('\n');
    
//    System.out.println(" ====================  > "+ elementType);
//    System.out.println(" da thu cai nau "+ fields[i].getType());
    builder.append("\t\t\t");
    if(method == null) {
      builder.append("object.").append(name).append("=list;");
    } else {
      builder.append("object.").append(method.getName()).append("(list);");
    }
  }
}
