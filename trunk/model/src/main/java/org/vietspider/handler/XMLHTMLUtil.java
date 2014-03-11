/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.vietspider.chars.SpecChar;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.common.io.LogService;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.Tag;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.util.HTMLText;
import org.vietspider.model.Region;
import org.vietspider.token.TypeToken;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 30, 2009  
 */
public class XMLHTMLUtil {

  protected HTMLExtractor extractor  = new HTMLExtractor(); 
  protected CompleteDocHandler completeDoc = new CompleteDocHandler();

  private char [] specials = { '&' };

  protected XMLTextData xmlText;

  public XMLHTMLUtil(XMLTextData xmlText) {
    this.xmlText = xmlText;
  }

  public HTMLExtractor getExtractor() { return extractor; }

  public Object lookupTextValue2(HTMLNode root, Region region, String address) {
    NodePath[] nodePaths = null;
    try {
      nodePaths = region.getNodePaths();
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    if(nodePaths == null) return new String[]{""};

    int type = region.getType();
    
    String [] paths = region.getPaths();
    List<HTMLNode> nodes = new ArrayList<HTMLNode>();//.matchNodes(root, paths);
    for(int i = 0; i < nodePaths.length; i++) {
      if(nodePaths[i] == null) {
        if(paths[i].equalsIgnoreCase("$link")) {
          if(address == null) address = "";
          String value = "<![CDATA[" + address + "]]>";
          nodes.add(new NodeImpl(value.toCharArray(), Name.CONTENT));
        } else if(paths[i].equalsIgnoreCase("$time")) {
          DateFormat dateFormat = new SimpleDateFormat();
          String value = dateFormat.format(Calendar.getInstance().getTime());
          nodes.add(new NodeImpl(value.toCharArray(), Name.CONTENT));
        } else {
          nodes.add(new NodeImpl(paths[i].toCharArray(), Name.CONTENT));
        }
        
        continue;
      }
      
      List<HTMLNode> list = extractor.matchNodes(root, nodePaths[i]);
//      System.out.println(region.getName() + " list "+ list + " : ");
//      System.out.println(nodePaths[i]);
      if(list == null || list.size() < 1) continue;
      nodes.addAll(list);
    }
   
    if(type == Region.TEXT) {
      HTMLText textUtils = new HTMLText();
      List<String> values = new ArrayList<String>();
      for(int i = 0; i < nodes.size(); i++) {
        StringBuilder builder = new StringBuilder();
        textUtils.buildText(builder, nodes.get(i));
        values.add(encode(builder.toString()));
      }
      
      return values.toArray(new String[values.size()]);
    } 


    if(type == Region.CDATA) {
      List<String> values = new ArrayList<String>();
      for(int i = 0; i < nodes.size(); i++) {
        StringBuilder builder = new StringBuilder();
        builder.append("<![CDATA[");
        if(nodes.get(i) == null) continue;
        if(address != null) {
          completeDoc.completeURL(address, nodes.get(i));
        }
        buildHTMLNode(new RefsDecoder(), builder, nodes.get(i), 0);
        builder.append("]]>");
        values.add(encode(builder.toString()));
      }
      return values.toArray(new String[values.size()]);
    } 

    if(type == Region.FILE) { 
      ArrayList<XMLResource> resources = new ArrayList<XMLResource>();
      for(int i = 0; i < nodes.size(); i++) {
        xmlText.searchResources(resources, nodes.get(i));
      }
      return resources.toArray(new XMLResource[resources.size()]);
    } 

    List<String> values = new ArrayList<String>();
    int counter =  countBlockText(nodes);
    if(counter > 1) {
      for(int i = 0; i < nodes.size(); i++) {
        StringBuilder builder = new StringBuilder();
        builder.append("<![CDATA[");
        if(nodes.get(i) == null) continue;
        if(address != null) {
          completeDoc.completeURL(address, nodes.get(i));
        }
        buildHTMLNode(new RefsDecoder(), builder, nodes.get(i), 0);
        builder.append("]]>");
        values.add(encode(builder.toString()));
      }
    } else {
      HTMLText textUtils = new HTMLText();
      for(int i = 0; i < nodes.size(); i++) {
        StringBuilder builder = new StringBuilder();
        textUtils.buildText(builder, nodes.get(i));
        values.add(encode(builder.toString()));
      }
    }
    return values.toArray(new String[values.size()]);
  }

  public void buildHTMLNode(RefsDecoder decoder, 
      StringBuilder builder, HTMLNode node, int tab){
    //  if(value.length < 1) return builder;
    if(builder.length() > 0) builder.append(SpecChar.n);
    Name name  = node.getName();
    NodeImpl nodeImpl = (NodeImpl)node; 
    int type  = nodeImpl.getType();
    boolean isTag = node.isTag();

    for(int i = 0; i < tab; i++) {
      builder.append(SpecChar.s);  
    }

    if(isTag || node.getName() == Name.UNKNOWN) builder.append('<');
    if(type == TypeToken.CLOSE) builder.append('/');
    builder.append(decoder.decode(node.getValue()));
    if(isTag || node.getName() == Name.UNKNOWN) builder.append('>');
    if(type == TypeToken.CLOSE || nodeImpl.getConfig().hidden())  return;

    List<HTMLNode> children = node.getChildren();
    if(children == null ) return;
    for(HTMLNode child : children) {
      buildHTMLNode(decoder, builder, child, tab+2);
    }

    if(nodeImpl.getConfig().end() != Tag.FORBIDDEN){
      builder.append(SpecChar.n);
      for(int i = 0; i < tab; i++) {
        builder.append(SpecChar.t);  
      }
      builder.append('<').append('/').append(name).append('>');
    }
  }

  protected String encode(String value) {
    StringBuilder builder = new StringBuilder();

    int index = 0;
    while(index < value.length()){
      char c = value.charAt(index) ;
      boolean enc = false;
      for(int i = 0; i < specials.length; i++) {
        if(specials[i] == c) {
          enc = true;
          break;
        }
      }
      builder.append(enc ? encode(c) : c);  
      index++;
    }
    return builder.toString();
  }

  private String encode(char c) {
    StringBuffer buffer = new StringBuffer();
    buffer.append ("&#").append(String.valueOf((int)c)).append (';');
    return buffer.toString();
  }

  protected int countBlockText(List<HTMLNode> list) {
    int counter = 0;
    for(int i = 0; i < list.size(); i++) {
      counter += countBlockText2(list.get(i));
    }
    return counter;
  }

  protected int countBlockText2(HTMLNode root) {
    int counter = 0;
    List<HTMLNode> children = root.getChildren();
    if(children == null) return counter;
    for(int i = 0; i < children.size(); i++) {
      HTMLNode node = children.get(i);
      if(node.isNode(Name.TD) 
          || node.isNode(Name.DIV)
          || node.isNode(Name.P)) {
        if(!hasContainer(node)) {
          if(hasText(node)) counter++;
          continue;
        }
      }
      counter += countBlockText2(node);
    }

    return counter;
  }

  private boolean hasContainer(HTMLNode root) {
    NodeIterator iterator = root.iterator();
    iterator.next();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(node.isNode(Name.TD) 
          || node.isNode(Name.DIV)
          || node.isNode(Name.P)) return true;
    }
    return false;
  }

  private boolean hasText(HTMLNode root) {
    NodeIterator iterator = root.iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(node.isNode(Name.CONTENT)) return true;
    }
    return false;
  }
}
