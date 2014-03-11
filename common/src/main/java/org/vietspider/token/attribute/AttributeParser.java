/*
 * Copyright 2004-2006 The VietSpider        All rights reserved.
 *
 * Created on January 24, 2006, 7:48 PM
 */

package org.vietspider.token.attribute;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.chars.CharsUtil;
import org.vietspider.chars.TextSpliter;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.token.Node;
/**
 *
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
public final class AttributeParser {
  
  private volatile static AttributeParser parser ;
  
  public final static synchronized AttributeParser getInstance() {
    if(parser != null) return parser;
    parser = new AttributeParser();
    return parser;
  }

//  private static ThreadSoftRef<AttributeParser> parser = new ThreadSoftRef<AttributeParser>(AttributeParser.class);
//  private static ThreadSoftRef<RefsDecoder> refsDecoder = new ThreadSoftRef<RefsDecoder>(RefsDecoder.class);
//
  @Deprecated()
  public static Attributes getAttributes(Node<?> node) { 
    return getInstance().get(node); 
  }

  @Deprecated()
  public static Attribute[] getAttributes(String text) { 
    return getInstance().get(text);
  }
  
  @Deprecated()
  /*
   * use get method
   */
  final static public Attributes parse(Node<?> node) {
    return getInstance().get(node);
  }
  
  @Deprecated()
  /*
   * use get method
   */
  final public static Attribute[] parse(String text) {
    return getInstance().get(text);
  }
  
  @Deprecated()
  final public static void parseStyle(Attributes list, String value) {
    getInstance().getStyle(list, value);
  }
  
  private final static String REF_START = "&#";
  
  final public Attributes get(Node<?> node) {
    Attributes list = new Attributes(node);
    if(!node.isTag()) return list;
    if(node == null || node.getValue() == null) return list;
    String text = trimText(new String(node.getValue()));
    String nodeName  = node.name();
    if(!CharsUtil.startsWith(text, nodeName, 0)) return list;
    
    nodeName = nodeName.toLowerCase();
    
    text = text.substring(nodeName.length());
    text = trimText(text);

    RefsDecoder refsDecoder = new RefsDecoder();
    while(text.length() > 0) {
      Object [] components = getName(text);
      if(components == null) return list;
      String name = (String)components[0];
      text = (String)components[1];
      
      if(components.length > 2) {
        list.add(new Attribute(name, null));
        continue;
      }
      
      String value = "";
      char mark = '\"';
      if(text.length() > 0) {
        components = getValue(text);
        value = (String)components[0];
        text = (String)components[1];
        mark = (Character)components[2];
      }
      
      if(mark != '\"' && mark != '\'') mark = '\"';
      if(value.indexOf(REF_START) > -1) {
        value = new String(refsDecoder.decode(value.toCharArray()));
      }
      list.addElement(new Attribute(name, value, mark));
    }
    parseStyle(list);
    return list;
  }
  
  final private void parseStyle(Attributes list) {
    Attribute attribute = list.get("style");
    if(attribute == null) return;
    String value = attribute.getValue();
    if(value == null) return;
    getStyle(list, value);
  }
  
  final public void getStyle(Attributes list, String value) {
    if(value == null) return;
    TextSpliter spliter = new TextSpliter();
    List<String> elements = spliter.toList(value, ';');
//    String [] elements = value.split(";");
    for(int i = 0; i < elements.size(); i++) {
      String element  = elements.get(i);
      if((element = element.trim()).isEmpty()) continue;
      List<String> data = spliter.toList(value, ':');
      if(data.size() < 1) {
        continue;
      } else if(data.size() < 2) {
        list.add(new Attribute(data.get(0), ""));
      } else{
        list.add(new Attribute(data.get(0), data.get(1)));        
      }
    }
  }
  
  final public Attribute[] get(String text) {
    List<Attribute> list = new ArrayList<Attribute>();
    text = trimText(text);

    RefsDecoder refsDecoder = new RefsDecoder();
    while(text.length() > 0) {
      Object [] components = getName(text);
      if(components == null) return list.toArray(new Attribute[list.size()]);
      String name = (String)components[0];
      text = (String)components[1];
      
      if(components.length > 2) {
        list.add(new Attribute(name, null));
        continue;
      }
      
      String value = "";
      char mark = '\"';
      if(text.length() > 0) {
        components = getValue(text);
        value = (String)components[0];
//        System.out.println(" ========== > "+ value);
        text = (String)components[1];
        mark = (Character)components[2];
      }
      
      if(mark != '\"' && mark != '\'') mark = '\"';
      if(value.indexOf(REF_START) > -1) {
        value = new String(refsDecoder.decode(value.toCharArray()));
      }
      list.add(new Attribute(name, value));
    }
    return list.toArray(new Attribute[list.size()]);
  }  

  private String [] getName(String text) {
    int index = 0;
    int length = text.length();
    
    while(index < length) {
      char c = text.charAt(index);
      if(Character.isWhitespace(c) 
        || Character.isSpaceChar(c)) {
        index++;
        continue;
      }
      break;
    }
    
    boolean single = false;
    while(index < length) {
      char c = text.charAt(index);
      if(c == '=' ){
        single = false;
        break;
      } else if(Character.isWhitespace(c) 
        || Character.isSpaceChar(c)) {
        single = isSingle(text, index);
//        System.out.println(" dung vi cai ni "+ Character.isWhitespace(c));
        if(single) break;
      } 
      index++;
    }
    
    if(index >= length) return null;
    String value = text.substring(0, index);
    text = text.substring(index+1);
//    System.out.println(single+ " : "+ value + " : " + text);
    return single ? new String[]{trimText(value), text, ""} : new String[]{trimText(value), text};
  }
  
  private boolean isSingle(String text, int index) {
    int length = text.length();
    while(index < length) {
      char c = text.charAt(index);
      if(Character.isWhitespace(c) 
        || Character.isSpaceChar(c)) {
        index++;
        continue;
      } 
      return c != '=';
    }
    return true;
  }
  
  /*private int indexOfName(String text) {
    int index = 0;
    int length = text.length();
    while(index < length) {
      char c = text.charAt(index);
      if(c == '=' 
        || Character.isWhitespace(c) 
        || Character.isSpaceChar(c)) return index;
      index++;
    }
    return -1;
  }*/

  private Object [] getValue(String text) {
    boolean hasWhiteSpace = Character.isSpaceChar(text.charAt(0)) 
                                  || Character.isWhitespace(text.charAt(0));
    text = trimText(text);
    
    char c = text.charAt(0);

    //================> start with ["]
    if(c == '"') {
      //end value with ["]
      int end = text.indexOf('"', 1);
      if(end > 0) {
        String value = text.substring(1, end);
        text = end + 1 < text.length() ? text.substring(end+1) : "";
        return new Object[]{trimText(value), trimText(text), c};
      }

      //end value with [' ]
      int idx = 0;
      while(idx < text.length()) {
        if(text.charAt(idx) == '\'' && 
            ( (idx == text.length()-1 )  ||
                (Character.isSpaceChar(text.charAt(idx + 1)) 
                    || Character.isWhitespace(text.charAt(idx+1)))
            )
        ) {
          end = idx;
          break;
        }
        idx++;
      }
      if(end > 0) {
        String value = text.substring(1, end);
        text = end + 2 < text.length() ? text.substring(end+2) : "";
        return new Object[]{trimText(value), trimText(text), c};
      }


      //end value with [ ]
      idx = 0;
      while(idx < text.length()) {
        if(Character.isSpaceChar(text.charAt(idx)) || Character.isWhitespace(text.charAt(idx))) {
          end = idx;
          break;
        }
        idx++;
      }
      if(end < 0) end = text.length() ;
      String value = text.substring(1, end);
      text = end + 1 < text.length() ? text.substring(end+1) : "";
      return new Object[]{trimText(value), trimText(text), c};
    }

    
    
    //================> start with [']
    if(c == '\'') {
      //end value with [']
      int end = text.indexOf('\'', 1);
      if(end < 0) end  = text.indexOf('"', 1);
      
      if(end < 0) {
        //end value with [ ]
        int idx = 0;
        while(idx < text.length()) {
          if(Character.isSpaceChar(text.charAt(idx)) || Character.isWhitespace(text.charAt(idx))) {
            end = idx;
            break;
          }
          idx++;
        }
      }
      if(end < 0) end = text.length() ;
      String value = text.substring(1, end);
      text = end + 1 < text.length() ? text.substring(end+1) : "";
      return new Object[]{trimText(value), trimText(text), c};
    }
    
    
    
   //================> start with [ ]
    //end value with [ ]
    int idx = 0;
    int end  = -1;
    while(idx < text.length()) {
      if(Character.isSpaceChar(text.charAt(idx)) || Character.isWhitespace(text.charAt(idx))) {
        end = idx;
        break;
      }
      idx++;
    }
    if(end < 0) end = text.length() ;

    String value = text.substring(0, end);
    if(hasWhiteSpace && value.indexOf('=') > -1 && end  < text.length()) {
      return new Object[]{"", text, c};    
    }
    text = end + 1 < text.length() ? text.substring(end+1) : "";
    return new Object[]{trimText(value), trimText(text), c};
  }

  private String trimText(String text) {
    int start = 0;
    while(start < text.length() 
        && (Character.isSpaceChar(text.charAt(start))|| Character.isWhitespace(text.charAt(start)))) {
      start++;
    }
    int end = text.length() - 1;
    while(end  >= 0 
        && (Character.isSpaceChar(text.charAt(end))|| Character.isWhitespace(text.charAt(end)))) {
      end--;
    }
    if(end < 0) return "";
    if(start == 0 && end  == text.length() - 1) return text;
    return text.substring(start, end+1);
  }

}
