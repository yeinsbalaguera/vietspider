/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.generator;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.link.generator.Generator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 11, 2008  
 */
public final class URLIndexGenerator extends URLGenerator {
  
  private List<URLIndexRange> indexes = new ArrayList<URLIndexRange>();
  
  public URLIndexGenerator(String sourceFullName, String... values) throws Exception {
    super(sourceFullName, values);
    List<String> newTemplates = new ArrayList<String>();
    
    for(int i = 0;  i < templates.length; i++) {
      if(templates[i].indexOf("[index]") > -1) {
        newTemplates.add(templates[i]);
      } else if(templates[i].indexOf('-') > -1 
          || templates[i].indexOf('>') > -1 ) {
        indexes.add(new URLIndexRange(sourceFullName, key, templates[i], i));
      }
    }
    templates = newTemplates.toArray(new String[newTemplates.size()]);
  }
  
 
  public void generate(List<String> list) {
//    System.out.println(" vao day generaate "+ list.size() + " : "+ index);
    list.clear();
    
    for(int i = 0; i < templates.length; i++) {
      String [] values = new String[]{ templates[i] };
      for(int j = 0; j < indexes.size(); j++) {
        if(j == indexes.size() -1) {
          values = indexes.get(j).value(50, values);
        } else {
          values = indexes.get(j).value(1, values);
        }
        
        if(j > 0 && indexes.get(j).isOutOfBound()) {
          indexes.get(j-1).nextStep();
          indexes.get(j).reset();
        }
      }
//      System.out.println("ra day " + values.length);

      for(int k = 0; k < values.length; k++) {
//        System.out.println(" ====  > thay co "+ values[k]);
        if(values[k].indexOf("[index]") < 0) {
          list.add(values[k]);
        }
      }
    }
      
//    System.out.println(" sau do sinh ra generaate "+ list.size() + " : "+ index);
//    System.out.println(" va luc ra generaate "+ list.size());
    for(int i = 0; i < indexes.size(); i++) {
      indexes.get(i).save(sourceFullName);
    }
  }
  
  public short getType() { return Generator.HOMEPAGE_GENERATOR; }
  
  public static void main(String[] args) throws Exception {
    URLIndexGenerator generator = new URLIndexGenerator("", 
    		"type org.vietspider.crawl.link.generator.URLIndexGenerator",
        "http://www6.vnmedia.vn/newsdetail.asp?newsid=[index]&catid=[index]",
    		"1->10",
    		"2->5"
//    		"1->10"
    		);
    
    List<String> list = new ArrayList<String>();
    for(int i = 0; i < 10; i++) {
      generator.generate(list);
    }
    
    for(int i = 0; i < list.size(); i++) {
      System.out.println(list.get(i));
    }    
//    System.out.println("\n\n\n");
//    
//    generator.generate(list);
//    for(int i = 0; i < list.size(); i++) {
//      System.out.println(list.get(i));
//    }
//    
//    generator.generate(list);
//    for(int i = 0; i < list.size(); i++) {
//      System.out.println(list.get(i));
//    }
    System.exit(0);
  }
  
}
