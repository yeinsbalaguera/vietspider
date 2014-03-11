/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.pattern.model;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.link.regex.URIElement;
import org.vietspider.link.regex.URIParser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 22, 2008  
 */
public class URIExchangePattern  implements IPattern {
  
  private Words [] templates;
  private URIElement regex;

  public URIExchangePattern() {    
  }
  
  public boolean match(String value) {
    return regex.match(value);
  }

  public void setValue(String...values) {
    if(values.length < 2) return;
    regex = URIParser.parse(values[0]);
    String template = values[1].trim();
    if(template.isEmpty()) template = null;

    String templateElements [] = template.split(";");
    List<Words> list = new ArrayList<Words>();
    for(String element : templateElements) {
      String [] words = element.trim().split(">");
      if(words.length < 1 ) continue;
      if(words.length == 1) {
        list.add(new Words(words[0].trim(), ""));  
        continue;
      } 
      list.add(new Words(words[0].trim(), words[1].trim()));
    }
    templates = list.toArray(new Words[list.size()]);
  }

  public String create(String value) {
    if(templates == null || !regex.match(value)) return null;

    String url  = value;
    for(int i = 0; i < templates.length; i++) {
      url = url.replaceFirst(templates[i].from, templates[i].to); 
    }
//  System.out.println(" thanh ----->" + url);
    return url;
  }

  public static class Words {

    private String from;

    private String to;

    private Words(String from, String to) {
      this.from = from;
      this.to = to;
    }

  }
}
