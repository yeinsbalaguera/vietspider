/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import sun.util.ResourceBundleEnumeration;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 3, 2008  
 */
public class PropertyResourceBundle extends ResourceBundle {

  @SuppressWarnings("unchecked")
  public PropertyResourceBundle (File file) throws IOException {
    Properties properties = new Properties();
    byte [] bytes = null;
    try {
      bytes = RWData.getInstance().load(file);
    } catch (Exception e) {
    }
    if(bytes == null) return;
    String value  = new String(bytes, "utf-8");

    String [] elements = value.split("\n");
    char[] convtBuf = new char[1024];
    for(String element : elements) {
      element = element.trim();
      if(element.isEmpty()) continue;
      String [] data = element.split("=");
      if(data.length < 2) {
        properties.put(data[0], "");
        continue;
      }
      data[1] = convert(data[1], convtBuf);
      properties.put(data[0], data[1]);
    }
    lookup = new HashMap(properties);
  }

  // Implements java.util.ResourceBundle.handleGetObject; inherits javadoc specification.
  public Object handleGetObject(String key) {
    if (key == null) {
      throw new NullPointerException();
    }
    return lookup.get(key);
  }

  /**
   * Returns an <code>Enumeration</code> of the keys contained in
   * this <code>ResourceBundle</code> and its parent bundles.
   *
   * @return an <code>Enumeration</code> of the keys contained in
   *         this <code>ResourceBundle</code> and its parent bundles.
   * @see #keySet()
   */
  public Enumeration<String> getKeys() {
    ResourceBundle parent_ = this.parent;
    return new ResourceBundleEnumeration(
        lookup.keySet(), (parent_ != null) ? parent_.getKeys() : null);
  }

  private String convert (String text, char[] convtBuf) {
    if(text.indexOf("\\u") < -1) return text;
    
    char [] in = text.toCharArray();
    int off = 0;
    int len = in.length;
    
    if (convtBuf.length < len) {
      int newLen = len * 2;
      if (newLen < 0) {
        newLen = Integer.MAX_VALUE;
      } 
      convtBuf = new char[newLen];
    }
    char aChar;
    char[] out = convtBuf; 
    int outLen = 0;
    int end = off + len;

    while (off < end) {
      aChar = in[off++];
      if (aChar == '\\') {
        aChar = in[off++];   
        if(aChar == 'u') {
          // Read the xxxx
          int value=0;
          for (int i=0; i<4; i++) {
            aChar = in[off++];  
            switch (aChar) {
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
              value = (value << 4) + aChar - '0';
              break;
            case 'a': case 'b': case 'c':
            case 'd': case 'e': case 'f':
              value = (value << 4) + 10 + aChar - 'a';
              break;
            case 'A': case 'B': case 'C':
            case 'D': case 'E': case 'F':
              value = (value << 4) + 10 + aChar - 'A';
              break;
            default:
              throw new IllegalArgumentException(
                  "Malformed \\uxxxx encoding.");
            }
          }
          out[outLen++] = (char)value;
        } else {
          if (aChar == 't') aChar = '\t'; 
          else if (aChar == 'r') aChar = '\r';
          else if (aChar == 'n') aChar = '\n';
          else if (aChar == 'f') aChar = '\f'; 
          out[outLen++] = aChar;
        }
      } else {
        out[outLen++] = aChar;
      }
    }
    return new String (out, 0, outLen);
  }

  protected Set<String> handleKeySet() { return lookup.keySet(); }

  // ==================privates====================

  private Map<String,Object> lookup;

}
