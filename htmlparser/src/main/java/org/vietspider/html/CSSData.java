/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 14, 2009  
 */
public class CSSData {
  
  private HashMap<String, String> map = new HashMap<String, String>();
  
  public void CSSDocument() {
  }
  
  public void CSSDocument(String value) {
    addValue(value);
  }
  
  public void addValue(String value) {
//    System.out.println(value);
    String [] elements = value.split("[}]");
    for(String element : elements) {
      String [] data = element.split("[{]");
      if(data.length < 2) continue;
      map.put(data[0], data[1]);
    }
  }
  
  public String[] getValue(String name) {
    List<String> list  = new ArrayList<String>();
    Iterator<String> iterator = map.keySet().iterator();
    String keyName  = "."+name;
    while(iterator.hasNext()) {
      String key = iterator.next();
      if(key.indexOf(keyName) > -1) list.add(map.get(key));
    }
    return list.toArray(new String[list.size()]);
  }

}
