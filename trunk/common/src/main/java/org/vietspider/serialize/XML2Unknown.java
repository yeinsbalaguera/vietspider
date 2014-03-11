/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
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
public class XML2Unknown {

  private static XML2Unknown MAPPER = new XML2Unknown();

  public final static XML2Unknown getInstance() { return MAPPER; } 

  private ReflectUtil reflectUtil;

  private final static String CDATA = "<![CDATA[";
  private final static String END_CDATA = "]]>";

  public XML2Unknown () {
    reflectUtil = new ReflectUtil();
  }

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

  public <T> T toObject(Class<T> clazz, XMLNode root) throws Exception {
    T object = clazz.newInstance();

    NodeMap map = clazz.getAnnotation(NodeMap.class);
    if (map == null) return null;
    String name = map.value();   
    XMLNode node = searchNode(root, name);
    if(node == null) return null;
    toObject(clazz, object, node);
    return object;
  }

  public void mapList(List<String> list, XMLNode node) {
    List<XMLNode> children = node.getChildren();
    for(int i = 0; i < children.size(); i++) {
      XMLNode n = children.get(i);
      String value = null;
      if(n.getChildren() != null
          && n.getChildren().size() > 0) {
        value = n.getChild(0).getTextValue();
      }
      list.add(value);
    }
  }

  public void mapProperties(Map<String, String> map, XMLNode node) {
    List<XMLNode> children = node.getChildren();
    for(int i = 0; i < children.size(); i++) {
      XMLNode n = children.get(i);
      mapProperty(map, n);
    }
  }

  private void mapProperty(Map<String, String> map, XMLNode node) {
    List<XMLNode> children = node.getChildren();
    String key = null;
    String value = null;
    for(int i = 0; i < children.size(); i++) {
      XMLNode n = children.get(i);
      if("key".equals(n.getName())) {
        if(n.getChildren() != null 
            && n.getChildren().size() > 0) {
          key = n.getChild(0).getTextValue();
        }
      } else if("value".equals(n.getName())) {
        if(n.getChildren() != null 
            && n.getChildren().size() > 0) {
          value = n.getChild(0).getTextValue();
        }
      }
    }
    //    System.out.println(key + "  : "+ value);
    if(key == null) return;
    map.put(key, value);
  }

  private XMLNode searchNode(XMLNode node, String name) {
    if(node.isNode(name)) return node;
    List<XMLNode> children = node.getChildren();
    if(children == null) return null;
    for(XMLNode ele :  children) {
      XMLNode value = searchNode(ele, name);
      if(value != null) return value;
    }
    return null;
  }

  public <T> T toBean(Class<T> clazz, XMLNode node) throws Exception {
    T object = clazz.newInstance();
    toObject(clazz, object, node);
    return object;
  }

  public <T> void toObject(Class<T> clazz, T object, XMLNode node) throws Exception {    
    if(clazz != object.getClass()) throw new Exception("Incompatipable type for object and class");
    NodeMap map = clazz.getAnnotation(NodeMap.class);
    if (map == null) return ;
    toXMLValue(clazz, object, node);
  }

  private void toXMLValue(Class<?> clazz, Object bean, XMLNode node)  throws Exception  {
    Attributes attrs = AttributeParser.getInstance().get(node);

    for(Attribute attr : attrs) {
      Field field = getField(attr.getName(), clazz);
      if(field == null) continue;

      NodeMap map = field.getAnnotation(NodeMap.class);
      if(map != null && !map.attribute()) continue;

      Class<?> type = field.getType();
      if(type.isPrimitive() || reflectUtil.isPrimitiveType(type) || type.isEnum()) {
        Object data = toValue(type, attr.getValue());
        putField(bean, field, data);
      }
    }

    List<XMLNode> children = node.getChildren();
    if(children == null) return ;
    for(XMLNode ele :  children) {
      if(node.getChildren() == null || node.getChildren().size() < 1) return;
      Field field = getField(ele.getName(), clazz);
      if(field == null) continue;
      Object data = toValue(bean, field, ele);
      if(data == null) continue;
      putField(bean, field, data);
    }
  }

  private void putField(Object bean, Field field, Object data) throws Exception {
    try {
      Method method = reflectUtil.getSetterMethod(bean.getClass(), field);
      method.setAccessible(true);
      method.invoke(bean, new Object[]{data});
    }catch (Exception e) {
    }
    field.setAccessible(true);
    field.set(bean, data);
  }

  @SuppressWarnings("unchecked")
  private Object toValue(Object bean, Field field, XMLNode node) throws Exception {
    try {
      Class<?> type = field.getType();

      field.setAccessible(true);
      Object current= field.get(bean);
      if(current != null) type = current.getClass();

      if(Collection.class.isAssignableFrom(type)) {
        Type eleParamType = Object.class;
        ParameterizedType paramType = (ParameterizedType)field.getGenericType();
        if(paramType.getActualTypeArguments().length > 0) {
          eleParamType = paramType.getActualTypeArguments()[0];
        }
        Object [] array =  (Object [])toArrayValues((Class<?>)eleParamType, field, node);
        Collection collection = null;

        if(current != null && current instanceof Collection) {
          collection = (Collection) current;
        } else if(type.isInterface())  {
          Attributes attributes = AttributeParser.getInstance().get(node); 
          Attribute attr = attributes.get("type");
          if(attr != null) {
            String typeName = attr.getValue();
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(typeName);
            collection = (Collection) clazz.newInstance();
          } else {
            collection = new ArrayList<Object>();
          }
        } else {
          collection = (Collection) type.newInstance();
        }
        Collections.addAll(collection, array);
        return collection;
      } else if(Map.class.isAssignableFrom(type)) {
        return toMapValues(bean, field, node);
      } 

      if(type.isArray()) return toArrayValues(type, field, node);
      NodeMap valueMap = type.getAnnotation(NodeMap.class);
      return toValue(type, valueMap == null ? null : valueMap.value(), node);
    } catch (Exception e) {
      Exception newException  = new Exception("Field: "+ field.getName()+ ": "+ e.getMessage());
      newException.setStackTrace(e.getStackTrace());
      throw newException;
    }
  }

  private Object toArrayValues(Class<?> type, Field field, XMLNode node) throws Exception {
    List<XMLNode> children = node.getChildren();
    if(children == null) return new Object[]{};
    Class<?> elementType = type;
    if(type.isArray()) elementType = type.getComponentType();
    Object array = Array.newInstance(elementType, children.size());

    NodesMap nodesMap  = field.getAnnotation(NodesMap.class);
    String mapItemName = nodesMap == null ? null : nodesMap.item();
    if(mapItemName != null && mapItemName.trim().length() < 1) mapItemName = null;
    for(int i = 0; i < children.size(); i++) {
      if(children.get(i).getChildren() == null || children.get(i).getChildren().size() < 1) {
        Array.set(array, i, null);
        continue;
      }
      XMLNode elementNode  = children.get(i);
      if(elementType.isPrimitive() || reflectUtil.isPrimitiveType(elementType)) {
        Array.set(array, i, toValue(elementType, new String(elementNode.getChild(0).getValue())));
      } else {
        Array.set(array, i, toValue(elementType, mapItemName, elementNode)); 
      }
    }
    return array;
  }

  @SuppressWarnings("unchecked")
  private Object toMapValues(Object bean, Field field, XMLNode node) throws Exception {
    List<XMLNode> children = node.getChildren();
    if(children == null) return new Object[]{};

    field.setAccessible(true);
    Object current= field.get(bean);
    Class<?> type = field.getType();

    Map<Object, Object> map = null;

    if(current != null && current instanceof Map<?,?>) {
      map = (Map<Object, Object>) current;
    } else if(type.isInterface())  {
      Attributes attributes = AttributeParser.getInstance().get(node);
      Attribute attr = attributes.get("type");
      if(attr != null) {
        String typeName = attr.getValue();
        Class<?> clazz = Class.forName(typeName); 
        //Thread.currentThread().getContextClassLoader().loadClass(typeName);
        map =  (Map<Object, Object>) clazz.newInstance();
      } else {
        map = new Hashtable<Object, Object>();
      }
    } else {
      map =  (Map<Object, Object>) type.newInstance();
    }

    Class<?> keyParamType = String.class;
    Class<?> valueParamType = String.class;
    try {
      ParameterizedType paramType = (ParameterizedType) field.getGenericType();
      if(paramType.getActualTypeArguments().length > 0) {
        keyParamType = (Class<?>)paramType.getActualTypeArguments()[0];
      }
      if(paramType.getActualTypeArguments().length > 1) {
        valueParamType = (Class<?>)paramType.getActualTypeArguments()[1];
      }
    }catch (Exception e) {
    }

    NodesMap nodesMap  = keyParamType.getAnnotation(NodesMap.class);
    String mapKeyItemName = nodesMap == null ? null : nodesMap.item();
    if(mapKeyItemName != null && mapKeyItemName.trim().length() < 1) mapKeyItemName = null;

    nodesMap  = valueParamType.getAnnotation(NodesMap.class);
    String mapValueItemName = nodesMap == null ? null : nodesMap.item();
    if(mapValueItemName != null && mapValueItemName.trim().length() < 1) mapValueItemName = null;


    for(int i = 0; i < children.size(); i++) {
      XMLNode elementNode  = children.get(i);
      if(elementNode.getChildren().size() < 1)  continue;

      XMLNode keyNode = elementNode.getChildren().get(0);
      Object keyObject = null;

      if(keyParamType.isPrimitive() || reflectUtil.isPrimitiveType(keyParamType)) {
        String nodeValue = (keyNode.getChildren() == null 
            || keyNode.getChildren().size() < 1) ? 
                "" : keyNode.getChild(0).getNodeValue();
        keyObject = toValue(keyParamType, nodeValue);
      } else {
        keyObject = toValue(keyParamType, mapKeyItemName, keyNode);
      }
      if(keyObject == null) continue;


      XMLNode valueNode = elementNode.getChildren().get(1);
      Object valueObject = null;

      if(valueParamType.isPrimitive() || reflectUtil.isPrimitiveType(valueParamType)) {
        String nodeValue = (valueNode.getChildren() == null 
            || valueNode.getChildren().size() < 1) ? 
                "" : valueNode.getChild(0).getNodeValue();
        valueObject = toValue(valueParamType, nodeValue);
      } else {
        valueObject = toValue(valueParamType, mapValueItemName, valueNode);
      }

      try {
        map.put(keyObject, valueObject);
      }catch (Exception e) {
      }
    }

    return map;
  }

  private Object toValue(Class<?> type, String mapName, XMLNode node) throws Exception {
    if(node.getChildren().size() == 0) return null;
    if(type.isPrimitive() || reflectUtil.isPrimitiveType(type) || type.isEnum()) {
      String value = new String(node.getChild(0).getValue());
      return toValue(type, value);
    }

    if(Modifier.isAbstract(type.getModifiers())) {
      Attributes attributes = AttributeParser.getInstance().get(node.getChild(0)); 
      Attribute attr = attributes.get("type");
      if(attr != null) {
        String typeName = attr.getValue();
        type = Class.forName(typeName);
        Object newBean = type.newInstance();
        toXMLValue(type, newBean, node.getChild(0));
        return newBean;  
      }
    }

    //    NodeMap valueMap = type.getAnnotation(NodeMap.class);
    Object newBean = type.newInstance();

    toXMLValue(type, newBean, mapName == null ? node : node.getChild(0));
    return newBean;  
  }

  @SuppressWarnings("unchecked")
  private Object toValue(Class<?> type, String value) {
    value = value.trim();
    if(value.startsWith(CDATA) && value.endsWith(END_CDATA)) { 
      value = value.substring(CDATA.length(), value.length()-3);
    }

    if(type == String.class || value == null) return value;
    if(type == StringBuffer.class) return new StringBuffer(value);
    if(type == StringBuilder.class) return new StringBuilder(value);
    value = value.trim();
    if(type == char.class || type  == Character.class) {
      if(value.length() < 1) return new Character(' ');
      return new Character(value.trim().charAt(0));
    }

    if(type == byte.class || type == Byte.class) return new Byte(value);
    if(type == boolean.class || type == Boolean.class) return new Boolean(value);
    if(type == short.class || type == Short.class) return new Short(value);
    if(type == int.class || type == Integer.class) return new Integer(value);
    if(type == long.class || type == Long.class) return new Long(value);
    if(type == float.class || type == Float.class) return new Float(value);
    if(type == double.class || type == Double.class) return new Double(value);

    if(type.isEnum()) {
      return Enum.valueOf((Class<? extends Enum>)type, value.toUpperCase());
    }

    return value;
  }

  private Field getField(String name, Class<?> clazz) throws Exception {
    Field [] fields = clazz.getDeclaredFields();
    for(Field field : fields) {
      NodeMap map = field.getAnnotation(NodeMap.class);
      if(map != null && map.value().equals(name)) return field;
      NodesMap maps = field.getAnnotation(NodesMap.class);
      if(maps != null && maps.value().equals(name)) return field;
      PropertiesMap proMaps = field.getAnnotation(PropertiesMap.class);
      if(proMaps != null && proMaps.value().equals(name)) return field;
    }
    if(clazz.getSuperclass() == null) return null;    
    return getField(name, clazz.getSuperclass());
  }

}
