/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.generator;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.crawl.CrawlingSources;
import org.vietspider.model.Source;
import org.vietspider.model.SourceIO;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 3, 2011  
 */
public class URLIndexRange {
  
  private long min = 0;
  private long max = 0;
  private int step = 1;
  
  private boolean increase = true;
  
  protected volatile long index;
  private int indexLength = -1;
  private String key;
  
  public URLIndexRange(String sourceFullName, String key, String template, int order) {
    Source source = CrawlingSources.getInstance().getSource(sourceFullName);
    this.key = key + "." + String.valueOf(order);
    try {
      index = Long.parseLong(source.getProperties().getProperty(this.key));
    } catch (Exception e) {
      
    }
    
    String [] range = template.split("->");
    if(range.length < 2) range = template.split("-");
    if(range.length < 2) range = template.split(">");
    if(range.length < 2) {
      throw new InvalidParameterException("Bad parameter: " + template);
    }
    
    try {
      min = Long.parseLong(range[0]);
    } catch (Exception e) {
      throw new InvalidParameterException("Min isn't a number: " + range[0]);
    }
    
    try {
      max = Long.parseLong(range[1]);
    } catch (Exception e) {
      throw new InvalidParameterException("Max isn't a number: " + range[1]);
    }
    
    if(range.length > 2) {
      try {
        step = Integer.parseInt(range[2]);
      } catch (Exception e) {
      }  
    }
    
    if(max == min)  throw new InvalidParameterException("Max min is equals: " + template);
    
    if(range[0].length() == range[1].length() 
        && (range[0].charAt(0) ==  '0' || range[1].charAt(0) ==  '0')) {
      indexLength = range[0].length();
    }
    
    increase = min < max;
    
    if(increase) {
      if(index < min || index >=  max) {
//        time++;
        index = min;  
      }
    } else {
      if(index > min || index <=  max) {
//        time++;
        index = min;
      }
    }
  }
  
  String[] value(int size, String...templates) {
    List<String> values = new ArrayList<String>();
//    System.out.println(key + " bat dau voi "+ index);
    if(increase) {
      int counter = 0;
      while(index <= max) {
        String valueIndex = String.valueOf(index);
//        System.out.println(valueIndex);
        if(indexLength > -1) {
          while(valueIndex.length() < indexLength) {
            valueIndex = "0" + valueIndex;
          }
        }
        
        for(int i = 0; i < templates.length; i++) {
          int start = templates[i].indexOf("[index]");
//          System.out.println(templates[i] + "  : " + valueIndex + " : "
//              + (start > -1 && start <= templates[i].length() - 7));
//          System.out.println(templates[i] + " : "+ start  + " : " +   templates[i].length() );
          if(start > -1 && start <= templates[i].length() - 7) {
            values.add(templates[i].substring(0, start) +
                valueIndex + templates[i].substring(start + 7));
          } else {
            values.add(templates[i]);
          }
        }
        
        if(size != 1) index += step;
        counter++;
        if(counter > size) break;
      }
//      System.out.println(key +  " con lai "+ values.size() +   " : " + index); 
      return values.toArray(new String[0]);
    } 
    
    while(index >= max) {
      int counter = 0;
      String valueIndex = String.valueOf(index);
      if(indexLength > -1) {
        while(valueIndex.length() < indexLength) {
          valueIndex = "0" + valueIndex;
        }
      }
      
      for(int i = 0; i < templates.length; i++) {
        int start = templates[i].indexOf("[index]");
        if(start > 0 && start <= templates[i].length() - 7) {
          values.add(templates[i].substring(0, start) +
              valueIndex + templates[i].substring(start + 7));
        }
      }
      
      if(size != 1) index -= step;  
      counter++;
      if(counter > size) break;
    }
    return values.toArray(new String[0]);
  }
  
  boolean isOutOfBound() {
    if(increase) return index > max;
    return index < max;
  }
  
  void nextStep() {
    if(increase) {
      index += step;  
    } else {
      index -= step;
    }
  }
  
  void reset() {
    if(increase) {
      if(index < min || index >=  max) {
//        time++;
        index = min;  
      }
    } else {
      if(index > min || index <=  max) {
//        time++;
        index = min;
      }
    }
  }
  
  void save(String fullName) {
    Source source = CrawlingSources.getInstance().getSource(fullName);
    SourceIO.getInstance().saveProperty(source, key, String.valueOf(index));
  }
}
