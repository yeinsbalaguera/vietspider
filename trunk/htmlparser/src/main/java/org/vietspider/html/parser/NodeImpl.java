/***************************************************************************
 * Copyright 2001-2006 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.html.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.vietspider.chars.CharsDecoder;
import org.vietspider.chars.CharsEncoder;
import org.vietspider.chars.SpecChar;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeChildHandler;
import org.vietspider.html.NodeConfig;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.Tag;
import org.vietspider.token.TypeToken;
import org.vietspider.token.attribute.Attributes;

/**
 * Created by VietSpider
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 14, 2006  
 */

public class NodeImpl extends HTMLNode {

  private static final long serialVersionUID = 1L;
  
  private transient boolean isOpen = false;
  private transient int type = TypeToken.CONTENT ;
  
  protected char [] value ;
  protected byte [] byteValue ;
  
  protected volatile List<HTMLNode> cachedTokens;
  
  protected transient Attributes attributes;
  
  public NodeImpl(char[] value, Name name){ 
    super(name);
    setValue(value);
  }

  public NodeImpl(char[] value, Name name, int type){
    super(name);
    this.type = type;
    children = new NodeList();
    NodeConfig config = HTML.getConfig(name);
    if(config.end() != Tag.FORBIDDEN && type == TypeToken.TAG) isOpen = true;
    setValue(value);
  }

  public boolean isOpen() { return isOpen; }
  public void setIsOpen(boolean open) { isOpen = open; }

  public int getType() { return type; }
  public void setType(int type) { this.type = type; }
  
  public void addChild(HTMLNode node)  {
    if(addInternalChild(node)) clearToken();
  }
  
  boolean addInternalChild(HTMLNode node){
    if(children == null 
        || getConfig().end() == Tag.FORBIDDEN) return false;
    NodeImpl impl = (NodeImpl) node;
    impl.setParentNode(this);
    children.addElement(impl);
    return true;
  }

  public void addChild(int i, HTMLNode node) {
    if(addInternalChild(i, node)) clearToken();
  }
  
  boolean addInternalChild(int i, HTMLNode node) {
    if(children == null || i < 0) return false;
    if(i > children.size()) {
      addInternalChild(node);
      return true;
    }
    NodeImpl impl = (NodeImpl)node;
    clearToken();
    impl.setParentNode(this);
    children.addElement(i, impl);
    return true;
  }
  
  public void setChild(int i, HTMLNode node) {
    if(setInternalChild(i, node)) clearToken();
  }
  
  public void replaceChild(HTMLNode node, List<HTMLNode> list) {
    if(children == null) return;
    for(int i = 0; i < children.size(); i++) {
      if(children.get(i) != node) continue;
      for(int j = list.size() - 1; j > -1; j--) {
        children.addElement(i, list.get(j));
      }
      children.removeElement(node);
      clearToken();
      break;
    }
  }

  boolean setInternalChild(int i, HTMLNode node) {
    if(children == null 
        || i < 0 || i >= children.size()) return false;
    NodeImpl impl = (NodeImpl)node;
    impl.setParentNode(this);
    children.setElement(i, impl);
    return true;
  }
  
  public void removeChild(HTMLNode node) {
    if(removeInternalChild(node)) clearToken();
  }

  boolean removeInternalChild(HTMLNode node) {
    if(children == null) return false;
    int i = children.indexOf(node);
    if(i < 0) return false;
    NodeImpl impl = (NodeImpl)node;
    impl.setParentNode(null);
    children.removeElement(impl);
    return true;
  }

  public void clearChildren() {
    if(children == null || children.size() < 1) return;
    clearToken();
    children.clearElements();
  }

  private void setParentNode(HTMLNode parent) { 
    this.parent = parent;
  }

  public StringBuilder buildValue(StringBuilder builder){
//  if(value.length < 1) return builder;
    if(builder.length() > 0) builder.append(SpecChar.n);
    boolean isTag = name != Name.CONTENT 
    && name != Name.COMMENT
    && name != Name.CODE_CONTENT;
    if(isTag) builder.append('<');
    if(type == TypeToken.CLOSE) builder.append('/');
    builder.append(getValue());
    if(isTag) builder.append('>');
    if(type == TypeToken.CLOSE || getConfig().hidden())  return builder;

    if(children == null ) return builder;
    for(HTMLNode ele : children) {
      ele.buildValue(builder);
    }
    if(getConfig().end() != Tag.FORBIDDEN){
      builder.append(SpecChar.n).append('<').append('/').append(getName()).append('>');
    }
    return builder;
  }

  public void setValue(char [] chars) {
    if(chars.length < 500) {
      this.value = chars;
      return;
    }

    if(name == Name.CONTENT ||
        name == Name.CODE_CONTENT || name == Name.COMMENT ) { 
      try {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream zip = new GZIPOutputStream(outputStream);
        
//        System.out.println(new String(chars));
        Charset charset = Charset.forName(Application.CHARSET);
        CharsEncoder encoder = new CharsEncoder(charset, charset.name());
        
//        zip.write(new String(chars).getBytes(Application.CHARSET));
        zip.write(encoder.encode(chars, 0, chars.length));
        zip.close();
        

        value = null;
        byteValue = outputStream.toByteArray();

//        System.out.println("=== >" + chars.length + " : " + byteValue.length);
//        System.out.println(new String(getValue()));
        return;
      } catch (Exception e) {
//        e.printStackTrace();
      }
    }
    this.value = chars;
  }

  public char[] getValue() { 
    if(value != null) return value;
    try {
      ByteArrayInputStream byteInput = new ByteArrayInputStream(byteValue);
      GZIPInputStream gzip = new GZIPInputStream(byteInput);

      ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
      int read = -1;
      byte [] bytes = new byte[16*1024];
      while((read = gzip.read(bytes)) != -1) {
        byteOutput.write(bytes, 0, read);
      }

      bytes = byteOutput.toByteArray();
      return CharsDecoder.decode(Application.CHARSET, bytes, 0, bytes.length);
//      ByteBuffer byteBuffer = ByteBuffer.wrap(byteOutput.toByteArray());
//      Charset charset = Charset.forName(Application.CHARSET);
//      CharBuffer charBuffer = charset.decode(byteBuffer);
//      return charBuffer.array();
    } catch (Exception e) {
      return value;
    }
  }

  public boolean isTag() {
    return type != TypeToken.CONTENT 
            && type != TypeToken.COMMENT  
            && type != TypeToken.CODE_CONTENT; 
  }

  public void clone(HTMLNode nodeParent) {
    if(type == TypeToken.CONTENT
        || type == TypeToken.COMMENT 
        || type == TypeToken.CODE_CONTENT) {
      ((NodeImpl)nodeParent).addChild(new NodeImpl(getValue(), name));
      return;
    }

    NodeImpl newImpl = new NodeImpl(getValue(), name, type);
    nodeParent.addChild(newImpl);
    if(children == null) return;
    for(int i = 0; i < children.size(); i++) {
      children.get(i).clone(newImpl);
    }
  }

  Iterator<HTMLNode> childIterator() { 
    return children.iteratorElement(); 
  }
  
  public NodeIterator iterator()  { 
    try {
      return new NodeIteratorImpl(tokens(0));
    } catch (Exception e) {
      Exception exception = new Exception("Stack over flow error.");
      LogService.getInstance().setThrowable(exception);
      throw new StackOverflowError();
//      return new NodeIteratorImpl(cachedTokens);
    }
  }
  
  public NodeIterator iterator(List<HTMLNode> ignores) {
    return new NodeIteratorImpl(tokens(ignores));
  }
  
  protected List<HTMLNode> tokens(int stack) throws Exception  {
    if(cachedTokens != null) return cachedTokens;
    cachedTokens = new ArrayList<HTMLNode>();
    cachedTokens.add(this);
    if(children == null) return cachedTokens;
    
    if(stack >= 5000) {
      Exception exception = new Exception("Stack over flow error ("+String.valueOf(stack)+")");
      throw exception;
    }
      
    
    for(int i = 0; i < children.size(); i++) {
      NodeImpl node = (NodeImpl)children.get(i);
      if(node == this) continue;
      if(node == null || node.tokens(stack+1) == null) continue;
      if(cachedTokens == null) break;
      cachedTokens.addAll(node.tokens(stack+1));
    }
    return cachedTokens;
  }
  
  protected List<HTMLNode> tokens(List<HTMLNode> ignores) {
    ArrayList<HTMLNode> newTokens = new ArrayList<HTMLNode>();
    if(ignores.contains(this)) return newTokens; 
    newTokens.add(this);
    if(children == null) return newTokens;
    for(int i = 0; i < children.size(); i++) {
      NodeImpl node = (NodeImpl)children.get(i); 
      newTokens.addAll(node.tokens(ignores));
    }
    // close application
//    NodeImpl nodeImpl = new NodeImpl(name.toString().toCharArray(), name, TypeToken.CLOSE);
//    newTokens.add(nodeImpl);
    return newTokens;
  }
  
  public void traverse(NodeChildHandler handler) {
    for(int i = 0; i < children.size(); i++) {
      handler.handle(i, children.get(i));
    }
  }
  
  private void clearToken() {
    cachedTokens = null;
    if(parent == null) return;
    ((NodeImpl)parent).clearToken();
  }
  
//  private void removeToken(HTMLNode node) {
//    if(cachedTokens != null) {
//      cachedTokens.remove(node);
//    }
//    if(parent == null) return;
//    ((NodeImpl)parent).removeToken(node);
//  }
  
  public static class NodeIteratorImpl implements NodeIterator {

    protected Iterator<HTMLNode> iterator;

    public NodeIteratorImpl(List<HTMLNode> tokens) {
      this.iterator = tokens.iterator();
    }

    public boolean hasNext() {
      return iterator.hasNext();
    }

    public HTMLNode next() {
      return iterator.next();
    }
  }
  
  public void toComment() {
    name = Name.COMMENT;
    type = TypeToken.COMMENT;
    StringBuilder builder = new StringBuilder("<!--");
    builder.append(value).append("-->");
    value = builder.toString().toCharArray();
    attributes = null;
  }
  
  public String name() { return name.name(); }

  /*public static void next(NodeImpl node) {
//  System.out.println(node.getName() + "  : " + node.hashCode() 
//  + " | " + node.nextNode.getName() + " : " + node.nextNode.hashCode());
    if(node.getChildren() == null)  return;
    for(int i = 0; i < node.getChildren().size(); i++) {
      next((NodeImpl)node.getChildren().get(i));
    }
//  System.out.println("==========================================");
  }*/

}
