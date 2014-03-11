/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2011  
 */
class ToObjectCode {
  
  static void generate(Class<?> clazz, 
      List<String> imports, StringBuilder codeBuilder) throws Exception {
    codeBuilder.append("\t");
    codeBuilder.append("public void toField(");
    codeBuilder.append(clazz.getSimpleName()).append(" object,");
    codeBuilder.append(" XMLNode node, String name, String value");
    codeBuilder.append(") throws Exception {");
    codeBuilder.append('\n');

    Field [] fields = clazz.getDeclaredFields();
    for(int i = 0; i < fields.length; i++) {
      Class<?> type = fields[i].getType();
      
      NodeMap nodeMap = type.getAnnotation(NodeMap.class);
      if(nodeMap != null) {
        fromField(fields[i], imports, codeBuilder, new ToObjectCodeConfig());  
        continue;
      }
      
      if(type.isArray()) {
        fromCollectionField(fields[i], imports, codeBuilder, new ToObjectCodeArray());  
        continue;
      }
      
      if(Set.class.isAssignableFrom(type)) {
        fromCollectionField(fields[i], imports, codeBuilder, new ToObjectCodeSet());  
        continue;
      }
      
      if(List.class.isAssignableFrom(type)) {
        fromCollectionField(fields[i], imports, codeBuilder, new ToObjectCodeList());  
        continue;
      }
      
      if(Map.class.isAssignableFrom(type)) {
//        System.out.println(" checking " + fields[i].getName() 
//            +  " : " + type +  " : " + Map.class.isAssignableFrom(type));
        fromMapField(fields[i], imports, codeBuilder, new ToObjectCodeMap());
        continue;
      }
      
      if(ReflectUtil.isPrimitive(type)) {
        fromField(fields[i], imports, codeBuilder, new ToObjectCodeSimple());  
        continue;
      }
    }

    codeBuilder.append('\n');;

    codeBuilder.append('\t').append('}').append('\n');
  }
  
  private static void fromField(Field field,
      List<String> imports, StringBuilder builder, FunctionCode function) throws Exception {
    NodeMap map = field.getAnnotation(NodeMap.class);
    if(map == null) return;

    fromField(field, imports, builder, function, map.value());
  }
  
  private static void fromCollectionField(Field field, 
      List<String> imports, StringBuilder builder, FunctionCode function) throws Exception {
    NodesMap map = field.getAnnotation(NodesMap.class);
    if(map == null) return;

    fromField(field, imports, builder, function, map.value());
  }
  
  private static void fromMapField(Field field, List<String> imports,  
      StringBuilder builder, FunctionCode function) throws Exception {
    PropertiesMap map = field.getAnnotation(PropertiesMap.class);
    if(map == null) {
      NodesMap nodesMap = field.getAnnotation(NodesMap.class);
      if(nodesMap != null) throw new Exception("Use PropertiesMap for field " + field.getName());
      return;
    }

    fromField(field, imports, builder, function, map.value());
  }
  
  private static void fromField(Field field, 
      List<String> imports, StringBuilder builder,
      FunctionCode function, String name) throws Exception {
    Method method = null;
    try {
      method = ReflectUtil.getSetterMethod(field.getDeclaringClass(), field);
    } catch (Exception e) {
    }

    builder.append("\t\t");
    builder.append("if(name.equals(\"").append(name).append("\")) {");
    builder.append('\n');
    
    builder.append("\t\t\t");
    try {
      function.generate(field, imports, builder, field.getName(), method);
    } catch (Exception e) {
      Exception exception = new Exception(field.getName()+": "+ e.getMessage());
      exception.setStackTrace(e.getStackTrace());
      throw exception;
    }
    builder.append('\n');
    
    builder.append("\t\t\t");
    builder.append("return;");
    builder.append('\n');
    
    builder.append("\t\t");
    builder.append("}");
    builder.append('\n');
  }
  
  static abstract class FunctionCode {
    
    public abstract void generate(Field field, List<String> imports, 
        StringBuilder codeBuilder, String name, Method method) throws Exception;
    
    public void importClazz(List<String> imports, Class<?> clazz) {
      if(!imports.contains("import java.util.*;")) {
        imports.add("import java.util.*;");
      }
      
      if(clazz.isMemberClass()) {
        imports.add("import " + clazz.getEnclosingClass().getName() + "." + clazz.getSimpleName() + ";");
      } else if(clazz.isEnum()) {
        imports.add("import " +  clazz.getName() + ";");
      } else if(!ReflectUtil.isPrimitive(clazz)) {
        if("java.lang".equals(clazz.getPackage().getName())) return;
        imports.add("import " +  clazz.getName() + ";");
      }
    }
    
  }
}
