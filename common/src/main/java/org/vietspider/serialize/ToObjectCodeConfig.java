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
public class ToObjectCodeConfig extends FunctionCode {
  
  @Override
  public void generate(Field field, 
      List<String> imports, StringBuilder _builder, String name, Method method) {
    importClazz(imports, field.getType());
//    System.out.println(" chay thu vao day roi nhe "+ name);
//    System.out.println(" chay thu cai ni "+ field.getType());
    if(method == null) {
      _builder.append("object.").append(name).append("=");
      _builder.append("XML2Object.getInstance().toObject(");
      _builder.append(field.getType().getSimpleName()).append(".class, node)");
      _builder.append(";");
    } else {
      _builder.append("object.").append(method.getName()).append("(");
      _builder.append("XML2Object.getInstance().toObject(");
      _builder.append(field.getType().getSimpleName()).append(".class, node)");
      _builder.append(");");
    }
  }
}
