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
public class ToObjectCodeArray extends FunctionCode {
  @Override
  public void generate(Field field,
      List<String> imports, StringBuilder builder, String name, Method method) {
    Class<?> clazz = field.getType().getComponentType();
    
    importClazz(imports, clazz);
    
    builder.append("List<XMLNode> list = node.getChildren();");
    builder.append('\n');
    
    builder.append("\t\t\t");
    builder.append("if(list == null) return;");
    builder.append('\n');
    
    builder.append("\t\t\t");
    builder.append(clazz.getSimpleName()).append("[] values = ");
    builder.append("new ").append(clazz.getSimpleName()).append("[node.getChildren().size()];");
    builder.append('\n');
    
    builder.append("\t\t\t");
    builder.append("for(int i = 0; i < list.size(); i++) {");
    builder.append('\n');
    
    builder.append("\t\t\t\t");
    builder.append("XMLNode n = list.get(i);");
    builder.append('\n');
    
    builder.append("\t\t\t\t");
    builder.append("if(n.getChildren() == null || n.getChildren().size() < 1) {");
    builder.append('\n');
    
    builder.append("\t\t\t\t\t");
    builder.append("continue;");
    builder.append('\n');
    
    builder.append("\t\t\t\t");
    builder.append("}");
    builder.append('\n');
    
    builder.append("\t\t\t\t");
    
//    System.out.println(clazz +  " : "+ clazz.isEnum() +   " : " + ReflectUtil.isPrimitive(clazz));
    
    if(clazz.isPrimitive() || ReflectUtil.isPrimitive(clazz)) {
      builder.append("values[i] = XML2Object.getInstance().toValue(");
      builder.append(clazz.getSimpleName());
      builder.append(".class, n.getChild(0).getTextValue());");
//      Array.set(array, i, toValue(type, new String(elementNode.getChild(0).getValue())));
    } else if(clazz == Object.class) {
      builder.append("Class<?> elementType = String.class;");
      builder.append('\n');
      
      builder.append("\t\t\t\t");
      builder.append("Attributes attributes = AttibuteParser.getInstance().get(n);");
      builder.append('\n');
      
      builder.append("\t\t\t\t");
      builder.append("Attribute attribute = attributes.get(\"type\");");
      builder.append('\n');
      
      builder.append("\t\t\t\t");
      builder.append("if(attribute != null && attribute.getValue().trim().length() > 0) {");
      builder.append('\n');
      
      builder.append("\t\t\t\t");
      builder.append("  elementType = Class.forName(attribute.getValue().trim());");
      builder.append('\n');
      
      builder.append("\t\t\t\t");
      builder.append("}");
      builder.append('\n');
      
      builder.append("\t\t\t\t");
      builder.append("values[i] = XML2Object.getInstance().toObject(elementType, n.getChild(0));");
//      System.out.println("\n");
//      System.out.println(n.getNodeValue());
//      System.out.println(elementType);
      
      
    } else {
      builder.append("values[i] = XML2Object.getInstance().toObject(");
      builder.append(clazz.getSimpleName());
      builder.append(".class, n);");
//      Array.set(array, i, toValue(elementType, mapItemName, elementNode));
    }
    
    builder.append('\n');
    
    builder.append("\t\t\t");
    builder.append("}");
    builder.append('\n');
    
    builder.append("\t\t\t");
    
//    object.setPrices((double[])XML2Object.getInstance().toArray(double.class, node));
    
//    System.out.println(" =====  >  type "+ clazz.getSimpleName());
    if(method == null) {
      builder.append("object.").append(name).append("=");
      builder.append("values");
//      builder.append("(").append(clazz.getSimpleName()).append("[])");
//      builder.append("XML2Object.getInstance().toArray(");
//      builder.append(clazz.getSimpleName()).append(".class, node)");
      builder.append(";");
    } else {
      builder.append("object.").append(method.getName()).append("(");
      builder.append("values");
//      builder.append("(").append(clazz.getSimpleName()).append("[])");
//      builder.append("XML2Object.getInstance().toArray(");
//      builder.append(clazz.getSimpleName()).append(".class, node)");
      builder.append(");");
    }
  }
}
