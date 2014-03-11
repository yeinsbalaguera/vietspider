/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.vietspider.serialize.ToObjectCode.FunctionCode;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2011  
 */
public class ToObjectCodeSimple extends FunctionCode {
  @Override
  public void generate(Field field, 
      List<String> imports, StringBuilder builder, String name, Method method) {
    Class<?> clazz = field.getType();
    if(clazz.isEnum()) {
      importClazz(imports, clazz);
    }
    if(method == null) {
      builder.append("object.").append(name).append("=");
      builder.append("XML2Object.getInstance().toValue(");
      builder.append(clazz.getSimpleName()).append(".class, value)");
      builder.append(";");
    } else {
      builder.append("object.").append(method.getName()).append("(");
      builder.append("XML2Object.getInstance().toValue(");
      builder.append(clazz.getSimpleName()).append(".class, value)");
      builder.append(");");
    }
  }
}
