/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.vietspider.common.Application;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2011  
 */
public class XML2Object {
  
  private static XML2Object MAPPER = new XML2Object();
  public final static XML2Object getInstance() { return MAPPER; } 
  
  private final static String CDATA = "<![CDATA[";
  private final static String END_CDATA = "]]>";
  
  private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
  
  public XML2Object() {
  }
  
  public void setDateFormat(SimpleDateFormat dateFormat) { this.dateFormat = dateFormat; }
  public SimpleDateFormat getDateFormat() { return dateFormat; }
  
  public <T> T toObject(Class<T> clazz, byte [] bytes) throws Exception {
    return toObject(clazz, new String(bytes, Application.CHARSET).trim());
  }

  public <T> T toObject(Class<T> clazz, String value) throws Exception {
    XMLDocument document = XMLParser.createDocument(value, null);
    return toObject(clazz, document);
  }

  public <T> T toObject(Class<T> clazz, XMLDocument document) throws Exception {
    return toObject(clazz, document.getRoot());
  }

  @SuppressWarnings("all")
  public <T> T toObject(Class<T> clazz, XMLNode root) throws Exception {
    if(ReflectUtil.isPrimitive(clazz)) {
      return toValue(clazz, root.getTextValue());
    }
    
    SerializableMapping mapping = SimpleCache.getInstance().getMapping(clazz);
    T object = (T)mapping.create();
    
    NodeMap nodeMap = clazz.getAnnotation(NodeMap.class);
    if(nodeMap != null) {
//      System.out.println(clazzName);
      
//      System.out.println(clazz + " : "+ nodeMap +  " : " + object);
    
//    RefsDecoder decoder = new RefsDecoder();
      List<XMLNode> children = root.getChildren();
      if(children == null || 
          children.size() < 1) return object;
      
      if(children.size() == 1 
          && nodeMap.value().equals(children.get(0).getName())) {
        root = children.get(0);
      }
      
//      System.out.println(clazz + " : "+ nodeMap.value() +  " : "+ root.getName());
      
      children = root.getChildren();
      for(int i = 0; i < children.size(); i++) {
        XMLNode n = children.get(i);
        if(!n.isTag()) continue;
        String name = n.getName();
        String value = null;
        if(n.getChildren() != null
            && n.getChildren().size() > 0) {
          value = n.getChild(0).getTextValue();
        }
        
        mapping.toField(object, n, name, value);
      }
      
      Attributes attributes = AttributeParser.getInstance().get(root);
      for(int i = 0; i < attributes.size(); i++) {
        Attribute attribute = attributes.get(i);
        String name = attribute.getName();
        String value = attribute.getValue();
        if("type".equals(name)) continue;
        mapping.toField(object, null, attribute.getName(), attribute.getValue());
      }
      
      return object;
    }
    
    throw new Exception ("Not found mapping class!");
//    return XML2Unknown.getInstance().toObject(clazz, bytes);
  }
  
  @SuppressWarnings("all")
  public void mapCollection(Collection collection, Class<?> elementType, XMLNode node) throws Exception {
    List<XMLNode> children = node.getChildren();
    for(int i = 0; i < children.size(); i++) {
      XMLNode n = children.get(i);
      if(n.getChildren() == null
          || n.getChildren().size() < 1) continue;
      mapElementCollection(collection, elementType, n);
    }
  }
  
  @SuppressWarnings("all")
  private void mapElementCollection(Collection list, Class<?> elementType, XMLNode n) throws Exception {
    if(elementType == null) {
      Attributes attributes = AttributeParser.getInstance().get(n);
      Attribute attribute = attributes.get("type");
      if(attribute != null && attribute.getValue().trim().length() > 0) {
        elementType = Class.forName(attribute.getValue().trim());
      }
    }
//    System.out.println("\n");
//    System.out.println(n.getNodeValue());
//    System.out.println(elementType);
    
    if(elementType == null) elementType = String.class;

    if(ReflectUtil.isPrimitive(elementType)) {
      list.add(toObject(elementType, n.getChild(0)));
      return;
    }  
    
    list.add(toObject(elementType, n)); 
  }
  
  @SuppressWarnings("all")
  public void mapProperties(Map map,
      Class<?> keyClazz, Class<?> valueClazz, XMLNode node)  throws Exception {
    List<XMLNode> children = node.getChildren();
    for(int i = 0; i < children.size(); i++) {
      XMLNode n = children.get(i);
      mapProperty(map, keyClazz, valueClazz, n);
    }
  }
  
  @SuppressWarnings("all")
  private void mapProperty(Map map, 
      Class<?> keyType, Class<?> valueType, XMLNode node) throws Exception {
    List<XMLNode> children = node.getChildren();
//    System.out.println(node.getTextValue());
    Object key = null;
    Object value = null;
    for(int i = 0; i < children.size(); i++) {
      XMLNode n = children.get(i);
      if("key".equals(n.getName())) {
        if(keyType == null) {
          Attributes attributes = AttributeParser.getInstance().get(n);
          Attribute attribute = attributes.get("type");
          if(attribute != null && attribute.getValue().trim().length() > 0) {
            keyType = Class.forName(attribute.getValue().trim());
          }
        }
        
        if(keyType == null) keyType = String.class;
        
        if(n.getChildren() != null 
            && n.getChildren().size() > 0) {
          if(ReflectUtil.isPrimitive(keyType)) {
            key = toObject(keyType, n.getChild(0));
          } else {
            key = toObject(keyType, n);
          }
//          key = toObject(keyType, n.getChild(0)); 
        }
      } else if("value".equals(n.getName())) {
        if(valueType == null) {
          Attributes attributes = AttributeParser.getInstance().get(n);
          Attribute attribute = attributes.get("type");
          if(attribute != null && attribute.getValue().trim().length() > 0) {
            valueType = Class.forName(attribute.getValue().trim());
          }
        }
        
        if(valueType == null) valueType = String.class;
        
        
        if(n.getChildren() != null 
            && n.getChildren().size() > 0) {
//          System.out.println(n.getChild(0).getTextValue());
          if(ReflectUtil.isPrimitive(valueType)) {
            value = toObject(valueType, n.getChild(0));
          } else {
            value =  toObject(valueType, n);
          }
//          value = toObject(valueType, n.getChild(0)); 
        }
      }
    }
    if(key == null || value == null) return;
    map.put(key, value);
  }
  
  /*public <T> Object toArray(Class<T> type, XMLNode node) throws Exception {
    List<XMLNode> children = node.getChildren();
    if(children == null) return Array.newInstance(type,  0);
    
    
//    Class<?> elementType = type;
//    if(type.isArray()) elementType = type.getComponentType();
    Object array = Array.newInstance(type, children.size());
    
//    NodesMap nodesMap  = field.getAnnotation(NodesMap.class);
//    String mapItemName = nodesMap == null ? null : nodesMap.item();
//    if(mapItemName != null && mapItemName.trim().length() < 1) mapItemName = null;
    for(int i = 0; i < children.size(); i++) {
      if(children.get(i).getChildren() == null || children.get(i).getChildren().size() < 1) {
        Array.set(array, i, null);
        continue;
      }
      XMLNode elementNode  = children.get(i);
      if(type.isPrimitive() || ReflectUtil.isPrimitiveType(type)) {
        Array.set(array, i, toValue(type, new String(elementNode.getChild(0).getValue())));
      } else {
//        Array.set(array, i, toValue(elementType, mapItemName, elementNode));
        Array.set(array, i, toObject(type, elementNode));
      }
    }
    return array;
  }*/
  
  @SuppressWarnings("all")
  public <T> T toValue(Class<T> type, String value) throws Exception {
    if(value == null) return null;
    value = value.trim();
    if(value.startsWith(CDATA) && value.endsWith(END_CDATA)) { 
      value = value.substring(CDATA.length(), value.length()-3);
    }

    if(type == String.class || value == null) return (T)value;
    if(type == StringBuffer.class) return (T)new StringBuffer(value);
    if(type == StringBuilder.class) return (T)new StringBuilder(value);
    value = value.trim();
    if(type == char.class || type  == Character.class) {
      if(value.length() < 1) return (T)new Character(' ');
      return (T)new Character(value.trim().charAt(0));
    }
    
    if(type == Date.class || type == Calendar.class) return (T) dateFormat.parse(value);
    if(type == byte.class || type == Byte.class) return (T)new Byte(value);
    if(type == boolean.class || type == Boolean.class) return (T)new Boolean(value);
    if(type == short.class || type == Short.class) return (T)new Short(value);
    if(type == int.class || type == Integer.class) return (T)new Integer(value);
    if(type == long.class || type == Long.class) return (T)new Long(value);
    if(type == float.class || type == Float.class) return (T)new Float(value);
    if(type == double.class || type == Double.class) return (T)new Double(value);

    if(type.isEnum()) {
      return (T)Enum.valueOf((Class<? extends Enum>)type, value.toUpperCase());
    }

    return (T)value;
  }
}
