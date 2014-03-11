/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2011  
 */
class ToNodeCode {
  
  static void generate(Class<?> clazz, 
      List<String> imports, StringBuilder builder) throws Exception {
    builder.append("\t");
    builder.append("public XMLNode toNode(").append(clazz.getSimpleName()).append(" object) throws Exception {");
    builder.append('\n');

    NodeMap map = clazz.getAnnotation(NodeMap.class);
    builder.append("\t\t").
    append("XMLNode node = new XMLNode(\"").append(map.value()).append("\");");
    builder.append('\n');
    
    builder.append("\t\t").
    append("Attributes attrs  = new Attributes(node);");
    builder.append('\n');
    
    builder.append("\t\t");
    builder.append("Object2XML mapper = Object2XML.getInstance();");
    builder.append('\n');

    Field [] fields = clazz.getDeclaredFields();
    for(Field field : fields) {
      NoMap noMap = field.getAnnotation(NoMap.class);
      if(noMap != null) continue;
      toSingleField(field, builder);
      toCoupleField(field, builder);
      toMapField(field, builder);
    }

    builder.append("\t\t");
    builder.append("return node;");
    builder.append('\n');

    builder.append('\t').append('}').append('\n');
  }

  private static void toSingleField(Field field, StringBuilder builder) throws Exception {
    NodeMap map = field.getAnnotation(NodeMap.class);
    if(map == null) return;

    String name = map.value();  
    Method method = null;
    try {
      method = ReflectUtil.getGetterMethod(field.getDeclaringClass(), field);
    } catch (Exception e) {
    }
    
    boolean isAttribute = map.attribute();
    
    if(isAttribute) {
//      System.out.println(" hihihi "+ field.getType() +  " : "+ field.getType().isPrimitive());
      if(!ReflectUtil.isPrimitive(field.getType())) {
        throw new Exception(field.getName() 
            + " : " + field.getType() + ": attribute must is primitive type!");
      }
      
//      if("type".equals(field.getName())) {
//        throw new Exception("Field \"" + field.getName() + "\" may not is attribute!");
//      }
      
      builder.append("\t\t");
      builder.append("Attribute attr_").append(field.getName()).append(" = new Attribute(\"");
      builder.append(name).append("\", Object2XML.getInstance().toString(");
      if(method == null) {
        builder.append("object.").append(field.getName());
      } else {
        builder.append("object.").append(method.getName()).append("()");
      }
      builder.append("));");
      builder.append('\n');
      
      builder.append("\t\t");
      builder.append("attrs.add(attr_").append(field.getName()).append(");");
      builder.append('\n');

      return;
    }

    builder.append("\t\t");
    if(field.getType().isPrimitive()) {
      builder.append("mapper.addPrimitiveNode(");
    } else {
      builder.append("mapper.addNode(");
    }
    
//    if(isCDATA) builder.append("\"<![CDATA[\" + ");
    if(method == null) {
      builder.append("object.").append(field.getName());
    } else {
      builder.append("object.").append(method.getName()).append("()");
    }
//    if(isCDATA) builder.append(" + \"]]>\"");
    builder.append(", node, ").append(map.cdata()).append(", \"").append(name).append("\");");
    builder.append('\n');
  }


  private static void toCoupleField(Field field, StringBuilder builder) {
    NodesMap map = field.getAnnotation(NodesMap.class);
    if(map == null) return;

    Method method = null;
    try {
      method = ReflectUtil.getGetterMethod(field.getDeclaringClass(), field);
    } catch (Exception e) {
    }

    String name = map.value();    
    //    boolean isAttribute = map.attribute();    
    //    boolean isCDATA = map.cdata();
    String itemName = map.item();
//    if(itemName == null) itemName = "item";
//    System.out.println(" =====  >"+ itemName);
    
    
    Class<?> elementType = null;
    if(!Class.class.isInstance(field.getGenericType())) {
      ParameterizedType paramType = (ParameterizedType)field.getGenericType();
      if(paramType.getActualTypeArguments().length > 0) {
        elementType = (Class<?>)paramType.getActualTypeArguments()[0];
      }
    } else if(field.getType().isArray()) {
      elementType = field.getType().getComponentType();
    }

    builder.append("\t\t");    
    builder.append("mapper.addNode(");
//    if(isCDATA) builder.append("\"<![CDATA[\" + ");
    if(method == null) {
      builder.append("object.").append(field.getName());
    } else {
      builder.append("object.").append(method.getName()).append("()");
    }
//    if(isCDATA) builder.append(" + \"]]>\"");
    builder.append(", node, ").append(map.cdata()).append(", \"").append(name).append("\"");
    builder.append(", \"").append(itemName).append("\"");
    if(elementType != null && elementType != Object.class) {
      builder.append(", \"").append(elementType.getName()).append("\"");
    } else {
      builder.append(", null");
    }
    builder.append(");");
    builder.append('\n');
  }

  private static void toMapField(Field field, StringBuilder builder) {
    PropertiesMap map = field.getAnnotation(PropertiesMap.class);
    if(map == null) return;

    String name = map.value();    
    //    boolean isAttribute = map.attribute();    
    //    boolean isCDATA = map.cdata();
    String itemName = map.item();
    if(itemName == null) itemName = "item";

    Method method = null;
    try {
      method = ReflectUtil.getGetterMethod(field.getDeclaringClass(), field);
    } catch (Exception e) {
    }
    
    Class<?> keyType = null;
    Class<?> valueType = null;
    
    if(!Class.class.isInstance(field.getGenericType())) {
      ParameterizedType paramType = (ParameterizedType)field.getGenericType();
      if(paramType.getActualTypeArguments().length > 0) {
        keyType = (Class<?>)paramType.getActualTypeArguments()[0];
        valueType = (Class<?>)paramType.getActualTypeArguments()[1];
      }
    }

    builder.append("\t\t");
    builder.append("mapper.addNode(");
//    if(isCDATA) builder.append("\"<![CDATA[\" + ");
    if(method == null) {
      builder.append("object.").append(field.getName());
    } else {
      builder.append("object.").append(method.getName()).append("()");
    }
//    if(isCDATA) builder.append(" + \"]]>\"");
    builder.append(", node, ").append(map.cdata()).append(", \"").append(name).append("\"");
    builder.append(", \"").append(itemName).append("\"");
    if(keyType == null) {
      builder.append(", null");
    } else {
      builder.append(", \"").append(keyType.getName()).append("\"");
    }
    if(valueType == null) {
      builder.append(", null");
    } else {
      builder.append(", \"").append(valueType.getName()).append("\"");
    }
    builder.append(");");
    builder.append('\n');
  }

  
}
