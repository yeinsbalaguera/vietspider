/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.cms.vtemplate;

import java.util.Iterator;
import java.util.List;

import org.vietspider.common.io.LogService;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;
import org.vietspider.token.attribute.Attributes;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 24, 2011  
 */
public class ContentCleaner {
  
  private HTMLParser2 parser = new HTMLParser2();
  
  public void buildContent(StringBuilder builder, String content, String uriFolder) {
    try {
//     if(!"test".equals(uriFolder)) saveForTest(content);
      
      List<NodeImpl> nodes = parser.createTokens(content.toCharArray());
      Iterator<NodeImpl> iterator = nodes.iterator();
      while(iterator.hasNext()) {
        NodeImpl node = iterator.next();
        Name name = node.getName();
        if(name == Name.IFRAME ||
            name == Name.FORM ||
            name == Name.INPUT ||
            name == Name.AREA 
            ) {
          iterator.remove();
        }
      }
      
      for(int i = 0; i < nodes.size(); i++) {
        NodeImpl node = nodes.get(i);
        Name name = node.getName();
        if(name == Name.TABLE 
            || name == Name.B
            || name == Name.DIV 
            || name == Name.P 
            || name == Name.SPAN
            || name == Name.U
            || name == Name.I
            || name == Name.H1
            || name == Name.H2
            || name == Name.H3
            || name == Name.H4
            || name == Name.H5
            || name == Name.H6
            || name == Name.TR
            || name == Name.STRONG) {
          int index = isEmptyNode(nodes, i);
          if(index > i) {
//            System.out.println(i + "  : "+ index + " : " + nodes.size());
            int size = index - i;
            for(int time = 0; time <= size; time++) {
              nodes.remove(i);
            }
//            System.out.println("sau do " + i + "  : "+ index + " : " + nodes.size());
            i--;
            continue;
          }
        }
      }
      
      for(int i = 0; i < nodes.size(); i++) {
        NodeImpl node = nodes.get(i);
        Name name = node.getName();
        int type = node.getType();
        
        if(name == Name.H1
            || name == Name.H2
            || name == Name.H3
            || name == Name.H4
            || name == Name.H5
            || name == Name.H6
            || name == Name.STRONG) {
          if(type != TypeToken.CLOSE && i < nodes.size() - 2) {
            NodeImpl close = searchEndNode(nodes, i);
            if(close != null) {
              //            System.out.println(new String(node.getValue()) + " + "+ isParagraph(nodes, i));
              if(isParagraph(nodes, i)) {
                renameNode(node, Name.DIV);
                name = Name.DIV;

                renameNode(close, Name.DIV);
              } else {
                renameNode(node, Name.B);
                name = Name.B;

                renameNode(close, Name.B);
              }
            }
          } 
        }
        
        if(name == Name.IMG) {
          processImageNode(uriFolder, node);
        } else if(name == Name.FONT) {
          continue;
//          processFontNode(node);
        } else if(name == Name.DIV || name == Name.P || name == Name.SPAN) {
          if(isIgnoreBlock(nodes, i) == i) {
            removeCloseTag(nodes, i);
            continue;
          }
//          System.out.println(" ==== start > " + i + " : "+ new String(node.getValue()));
//          int index = isEmptyNode(nodes, i);
////          if(index > -1) {
////            System.out.println(" ==== end > " + index + " : "+ new String(nodes.get(index).getValue()));
////          }
//          if(index > i) {
//            i = index;
//            continue;
//          }
          
          processBlockNode(node);
          if(i < nodes.size() - 1 && nodes.get(i+1).getName() == name
              && nodes.get(i+1).getType() == TypeToken.CLOSE) {
            node.toComment();
            nodes.get(i+1).toComment();
            i++;
            continue;
          }
//        } else if(name == Name.TABLE 
//            || name == Name.B
//            || name == Name.U
//            || name == Name.I
//            || name == Name.STRONG) {
//          int index = isEmptyNode(nodes, i);
//          if(index > i) {
//            i = index;
//            continue;
//          }
        } else if(name == Name.BR) {
          int index = isIgnoreBR(nodes, i);
          if(index > -1) {
//            System.out.println(i + " : "+ index);
            i = index;
            continue;
          }
        }
        
        if(name == Name.P) {
          renameNode(node, Name.DIV);
          name = Name.DIV;
        }

        boolean isTag = name != Name.CONTENT  && name != Name.COMMENT && name != Name.CODE_CONTENT;
        if(isTag) builder.append('<');
        if(type == TypeToken.CLOSE) builder.append('/');
        builder.append(node.getValue());
        if(isTag) builder.append('>');
        if(type == TypeToken.CLOSE || node.getConfig().hidden()) continue;
      }
    } catch (Exception e) {
      LogService.getInstance().setMessage(e, e.toString());
    }
    //    /vietspider/IMAGE/
  }
  
  private NodeImpl searchEndNode(List<NodeImpl> nodes, int index) {
    Name name = nodes.get(index).getName();
    for(int i = index+1; i < nodes.size(); i++) {
      NodeImpl node = nodes.get(i); 
      if(node.getName() == name 
          && node.getType() == TypeToken.CLOSE) {
        return node;
      }
    }
    return null;
  }
  
  private void removeCloseTag(List<NodeImpl> nodes, int index) {
    Name name = nodes.get(index).getName();
    int counter = 0;
    for(int i = index+1; i < nodes.size(); i++) {
      NodeImpl node = nodes.get(i);
      if(!node.isNode(name)) continue;
        if(node.getType() == TypeToken.CLOSE) {
          if(counter > 0) {
            counter--; 
            continue;
          } 
          nodes.remove(i);
          return;
        } 
        counter++;
    }
  }
  
  private void renameNode(NodeImpl node, Name newName) {
    String value = new String(node.getValue());
    int index = value.indexOf(' ');
    if(index < 0) index = value.length();
    value = newName.toString() + value.substring(index);
    node.setValue(value.toCharArray());
    node.setName(newName);
  }
  
  private int isIgnoreBlock(List<NodeImpl> nodes, int i) {
    if(i >= nodes.size() - 1) return -1;
    if((nodes.get(i).getType() != nodes.get(i+1).getType())) return -1;
    
    Name name = nodes.get(i).getName();
    if(name != Name.DIV && name != Name.P) return -1;
    
    name = nodes.get(i+1).getName();
    if(name == Name.DIV || name == Name.P) return i;

    return -1;
  }
  
  private int isIgnoreBR(List<NodeImpl> nodes, int index) {
    if(index < nodes.size() - 1) {
      int i = index+1;
      NodeImpl node = nodes.get(i);
      while(i < nodes.size()-1
          && 
          node.isNode(Name.BR)){
//        if(node.isNode(Name.CONTENT)
//          ||  node.isNode(Name.IMG)) break;
        i++;
        node = nodes.get(i);
      }
//      System.out.println(i + " : "+ index);
      if(node.isNode(Name.DIV) ||
          node.isNode(Name.P)
           || node.isNode(Name.TD)
        ) return i-1;
    } 
    
    
    if(index > 0) {
      int i = index-1;
      NodeImpl node = nodes.get(i);
      while(i > 0 && 
          node.isNode(Name.BR)){
//      while(i > 0){
//        if(node.isNode(Name.CONTENT)
//            ||  node.isNode(Name.IMG)) break;
        i--;
        node = nodes.get(i);
      }
//      System.out.println("keke " + i + " : "+ index);
      if(node.isNode(Name.DIV) ||
          node.isNode(Name.P)
          || node.isNode(Name.TD)
      ) return index;
//      nodes.remove(i);
//      i--;
    }    
    
    return -1;
  }
  
  private int isEmptyNode(List<NodeImpl> nodes, int start) {
    NodeImpl node = nodes.get(start);
    if(node.getType() == TypeToken.CLOSE) return -1;
    Name name = node.getName();
    for(int i = start+1; i < nodes.size(); i++) {
      NodeImpl n = nodes.get(i);
      if(n.isNode(Name.CONTENT) || n.isNode(Name.IMG)) return -1;
//      System.out.println(" compare " + i + " : "+ new String(n.getValue()) + " : "+ n.isNode(name) 
//          + " : "+ n.getName() +  " : "+ name);
      if(n.isNode(name)) {
        int type = n.getType();
//        System.out.println(" test " + i + " : "+ new String(n.getValue()) + " : "+ (type == TypeToken.CLOSE));
        if(type == TypeToken.CLOSE) return i;
        int index = isEmptyNode(nodes, i+1);
        if(index < i) return -1;
//        if(index < i+1) {
//          System.out.println(" error " + i + " : "+ new String(n.getValue()));
//          return -1;
//        }
        i = index;
      }
    }
    return -1;
  }
  
  private void processImageNode(String uriFolder, NodeImpl node) {
    Attributes attributes = AttributeParser.getInstance().get(node);
    if(!processSourceImage(attributes, uriFolder)) return;
    Attribute attribute = attributes.get("style");
    if(attribute == null) {
      attributes.add(new Attribute("style", "padding:2px 7px 0px 0px;"));
    } else {
      String value = attribute.getValue() ;
      int idx = value.toLowerCase().indexOf("padding");
      if(idx > 0) {
        int end = value.indexOf(';', idx+1);
        if(end < 0) end = value.length();
        value  = value.substring(0, idx) + value.substring(end);
      }
      value += " " + "padding:2px 7px 0px 0px;"; 
      attribute.setValue(value);
      attributes.set(attribute);
    }
  }
  
  private boolean processSourceImage(Attributes attributes, String uriFolder) {
    Attribute attribute = attributes.get("src");
    if(attribute == null) return false;
    String value = attribute.getValue();
    if(!value.startsWith("/vietspider/IMAGE/")) return true;
    value = value.substring("/vietspider/IMAGE/".length());
    attribute.setValue(uriFolder + "/IMAGE/" + value);
    attributes.set(attribute);
    return true;
  }

//  private void processFontNode(NodeImpl node) {
//    Attributes attributes = AttributeParser.getInstance().get(node);
//    attributes.remove("face");
//    attributes.remove("font-size");
//  }

  private void processBlockNode(NodeImpl node) {
    //    System.out.println(" thay co 111 "+ new String(node.getValue()));
    Attributes attributes = AttributeParser.getInstance().get(node);
    attributes.remove("MARGIN");
    attributes.remove("class");
    attributes.remove("style");
    attributes.remove("font-face");
    attributes.remove("font-size");
    if(node.getType() != TypeToken.CLOSE) {
      attributes.add(new Attribute("class", "detail-content"));
    }
    //    System.out.println(" thay co "+ new String(node.getValue()));
  }
  
  private boolean isParagraph(List<NodeImpl> nodes, int index) {
    NodeImpl node = nodes.get(index);
    StringBuilder builder = new StringBuilder();
    
    for(int i = 0; i < nodes.size(); i++) {
      NodeImpl n = nodes.get(i);
      if(n.getName() == node.getName() && n.getType() == TypeToken.CLOSE) break;
      if(n.isNode(Name.CONTENT)) {
        if(builder.length() > 0) builder.append(' ');
        builder.append(n.getValue());
      }
    }
    
    
    String value = builder.toString().trim();
//    System.out.println(value);
//    System.out.println(value.length());
    
    return value.length() > 100 
      || (value.length() > 50 && value.charAt(value.length()-1) == '.');
  }
  
 /* private  static void saveForTest(String content) throws Exception {
    int index = 1;
    File file = getFile(index);
    while(file.exists()) {
      index++;
      file = getFile(index);
    }
    org.vietspider.common.io.RWData.getInstance().save(file, content.getBytes("utf8"));
  }
  
  public static File getFile(int index) {
    File file = new File("D:\\java\\vietspider\\httpserver2\\target\\test-classes\\org\\vietspider\\webui\\cms\\vtemplate"
        , "content.cleaner.test"+String.valueOf(index)+".html");
    
    return file;
  }*/

  //  String createImage(String img) throws Exception {
  //    String pattern = "$image";
  //    int idx = html.indexOf(pattern);
  //    int start = 0;
  //    if(idx > -1){
  //      append(html.substring(start, idx));append(img);
  //      start = idx + pattern.length();
  //    }
  //    if(start < html.length()) append(html.substring(start));
  //  }
}
