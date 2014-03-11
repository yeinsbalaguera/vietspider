/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.serialize;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2011  
 */
public class Object2XML {
  
  private static Object2XML MAPPER = new Object2XML();
  
  public final static Object2XML getInstance() { return MAPPER; } 
  
  private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
  
  public Object2XML() {
   
  }
  
  public void setDateFormat(SimpleDateFormat dateFormat) { this.dateFormat = dateFormat; }
  public SimpleDateFormat getDateFormat() { return dateFormat; }

  @SuppressWarnings("all")
  public XMLDocument toXMLDocument(Object bean) throws Exception {
    Class<?> clazz = bean.getClass();
    SerializableMapping mapping = SimpleCache.getInstance().getMapping(clazz);
    XMLNode node = mapping.toNode(bean);
    return new XMLDocument(node);
  }
  
  @SuppressWarnings("all")
  public void addPrimitiveNode(char value, XMLNode node, boolean cdata, String...names) throws Exception {
    addNode(String.valueOf(value), node, cdata, names);
  }
  
  public void addPrimitiveNode(int value, XMLNode node, boolean cdata, String...names) throws Exception {
    addNode(String.valueOf(value), node, cdata, names);
  }
  
  public void addPrimitiveNode(long value, XMLNode node, boolean cdata, String...names) throws Exception {
    addNode(String.valueOf(value), node, cdata, names);
  }
  
  public void addPrimitiveNode(short value, XMLNode node, boolean cdata, String...names) throws Exception {
    addNode(String.valueOf(value), node, cdata, names);
  }
  
  public void addPrimitiveNode(double value, XMLNode node, boolean cdata, String...names) throws Exception {
    addNode(String.valueOf(value), node, cdata, names);
  }
  
  public void addPrimitiveNode(float value, XMLNode node, boolean cdata, String...names) throws Exception {
    addNode(String.valueOf(value), node, cdata, names);
  }
  
  public void addPrimitiveNode(boolean value, XMLNode node, boolean cdata, String...names) throws Exception {
    addNode(String.valueOf(value), node, cdata, names);
  }
  
  public void addPrimitiveNode(byte value, XMLNode node, boolean cdata, String...names) throws Exception {
    addNode(String.valueOf(value), node, cdata, names);
  }
  
  public void addNode(Date value, XMLNode node, boolean cdata, String...names) throws Exception {
    addNode(dateFormat.format(value), node, cdata, names);
  }
  
  public void addNode(Calendar value, XMLNode node, boolean cdata, String...names) throws Exception {
    addNode(dateFormat.format(value), node, cdata, names);
  }
  
  @SuppressWarnings("all")
  public XMLNode addNode(Object value, XMLNode node, boolean cdata, String...names) throws Exception {
    if(names.length < 1) {
      names = new String[]{value.getClass().getName()};
    }
    
    if(value == null) return null;
    
    NodeMap nodeMap = value.getClass().getAnnotation(NodeMap.class);
//    System.out.println(value + " : "+ nodeMap);
    if(nodeMap != null) {
      Class<?> clazz = value.getClass();
      String clazzName = clazz.getPackage().getName()+ "." + clazz.getSimpleName() + "_MappingImpl";
//      System.out.println(clazzName);
//      Class<?> mappingClazz = clazz.getClassLoader().loadClass(clazzName);
      
      if(names[0].length() > 0) {
        XMLNode n = new XMLNode(names[0]);
        node.addChild(n);
        SerializableMapping mapping = SimpleCache.getInstance().getMapping(clazz);
        n.addChild(mapping.toNode(value));
        return n;
      } else {
        SerializableMapping mapping = SimpleCache.getInstance().getMapping(clazz);
        node.addChild(mapping.toNode(value));
        return node;
      }
    }
    
    if(value.getClass().isArray() ) {
      if(names.length < 2) {
        names = new String[] {names[0], ""};
      }
      
      XMLNode n = node.addChild(names[0]);
      
      int length = Array.getLength(value);
      for(int i = 0; i < length; i++) {
        Object item = Array.get(value, i);
//        System.out.println(" ===  >" + item);
        if(value == null) continue;
        XMLNode _return = addNode(item, n, cdata, names[1]);
        if(names.length > 2 && names[2] == null) {
          Attributes attributes = new Attributes(_return);
          attributes.add(new Attribute("type", item.getClass().getName()));
        }
      }
      return n;
    }
    
    if(value instanceof List || value instanceof Set) {
      XMLNode n = node.addChild(names[0],
          new String[][]{{"type", value.getClass().getName()}});
      Collection<Object> list = (Collection<Object>) value;
      for(Object ele : list) {
       XMLNode _return = addNode(ele, n, cdata, names[1]);
       if(names[2] == null) {
         Attributes attributes = new Attributes(_return);
         attributes.add(new Attribute("type", ele.getClass().getName()));
       }
      }
      return n;
    }
    
    /*if(value instanceof Set) {
      XMLNode n = node.addChild(names[0],
          new String[][]{{"type", value.getClass().getName()}});
      Set<Object> list = (Set<Object>) value;
      for(Object ele : list) {
       XMLNode _return = addNode(ele, n, names[1]);
       if(names[2] == null) {
         Attributes attributes = new Attributes(_return);
         attributes.add(new Attribute("type", ele.getClass().getName()));
       }
      }
      return n;
    }*/
    
//    System.out.println(value + "  : " + (value instanceof Map));
    
    if(value instanceof Map) {
//      System.out.println(" vao day ");
      XMLNode n = node.addChild(names[0], 
          new String[][]{{"type", value.getClass().getName()}});
      Map map = (Map) value;
      Iterator<Map.Entry> iterator = map.entrySet().iterator();
      while(iterator.hasNext()) {
        Map.Entry entry = iterator.next();
        XMLNode item = n.addChild(names[1]);
       
        XMLNode _return = addNode(entry.getKey(), item, cdata, "key");
        if(names[2] == null) {
          Attributes attributes = new Attributes(_return);
          attributes.add(new Attribute("type", entry.getKey().getClass().getName()));
        }
//        System.out.println("key return: " + _return.getTextValue());
        _return = addNode(entry.getValue(), item, cdata, "value");
        if(names[3] == null) {
          Attributes attributes = new Attributes(_return);
          attributes.add(new Attribute("type", entry.getValue().getClass().getName()));
        }
//        System.out.println("value return: " + _return.getTextValue());
      }
      return n;
    }
    
    if(cdata) {
      StringBuilder builder = new StringBuilder();
      builder.append("<![CDATA[").append(value).append("]]>");
      value = builder.toString();
    }

    return node.addTextChild(names[0], value.toString());
  }
  
  public String toString(short value) { return String.valueOf(value); }
  public String toString(int value) { return String.valueOf(value); }
  public String toString(long value) { return String.valueOf(value); }
  public String toString(float value) { return String.valueOf(value); }
  public String toString(double value) { return String.valueOf(value); }
  public String toString(boolean value) { return String.valueOf(value); }
  public String toString(char value) { return String.valueOf(value); }
  public String toString(byte value) { return String.valueOf(value); }
  public String toString(Date value) { return dateFormat.format(value); }
  public String toString(Calendar value) { return dateFormat.format(value); }
  public String toString(Object value) { return value.toString(); }
  
  
}
