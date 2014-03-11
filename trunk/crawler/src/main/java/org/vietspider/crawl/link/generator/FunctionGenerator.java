/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.generator;

import static org.vietspider.link.pattern.LinkPatternFactory.createMultiPatterns;

import java.util.List;

import org.vietspider.link.generator.Generator;
import org.vietspider.link.pattern.JSOnclickPatterns;
import org.vietspider.model.Source;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 18, 2009  
 */
public class FunctionGenerator {
  
  protected volatile JSOnclickPatterns jsOnclickPatterns;
  
  protected String sourceFullName;
  
  public FunctionGenerator() {
  }
  
  public FunctionGenerator(String sourceFullName, String...values) {
    this.sourceFullName = sourceFullName;
    if(values != null && values.length > 1) { 
      jsOnclickPatterns = createMultiPatterns(JSOnclickPatterns.class, values);
    } else {
      jsOnclickPatterns = null;
    }
  }
  
  public void generate(List<String> list) {
//    System.out.println(" da vao day roi "+ list.size());
    if(jsOnclickPatterns == null) {
      list.clear();
      return;
    }
    
    for(int i = 0; i < list.size(); i++) {
      String onclick = list.get(i);
      String url = jsOnclickPatterns.create(onclick);
//      if(url != null) {
//        System.out.println(" +++ ++ "+ onclick);
//        System.out.println(" ----------- "+ url);
//      }
      list.set(i, url);
    }
  }
  
  public short getType() { return Generator.FUNCTION_GENERATOR; }
}
