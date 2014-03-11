/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.model;

import java.util.Iterator;
import java.util.Properties;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 2, 2009  
 */
public class SourceComparator {
  public static boolean equals(Source source1, Source source2) {
    if(!source1.getGroup().equals(source2.getGroup())) return false;
    if(!source1.getName().equals(source2.getName())) return false;
    if(!source1.getCategory().equals(source2.getCategory())) return false;
    
    if(!source1.getEncoding().equals(source2.getEncoding())) return false;
    
    if(source1.getExtractType() != source2.getExtractType()) return false;
    
    if(source1.getDepth() != source2.getDepth()) return false;
    if(source1.getPriority() != source2.getPriority()) return false;
    
    
    if(!compareArrays(source1.getHome(), source2.getHome())) return false;
    
    
    if(!compareRegion(source1.getUpdateRegion(), source2.getUpdateRegion())) return false;
    if(!compareRegions(source1.getExtractRegion(), source2.getExtractRegion())) return false;
    
    if(!compareRegions(source1.getProcessRegion(), source2.getProcessRegion())) return false;
    
    if(!compareProperties(source1.getProperties(), source2.getProperties())) return false;
    
      
    return true;  
  }
  
  private static boolean compareArrays(String[] values1, String [] values2) {
    if(values1 == null) {
      if(values2 == null) return true;
      return false;
    }
    
    if(values2 == null) {
      if(values2 != null) return false;
      return true;
    }
    
    if(values1.length != values2.length)  return false;
    for(int i = 0; i < values1.length; i++) {
      if(!values1[i].equals(values2[i])) {
        return false;
      }
    }
    
    return true;
  }
  
  private static boolean compareProperties(Properties properties1, Properties properties2) {
    if(properties1.size() != properties2.size()) return false;
    
    Iterator<Object> iterator = properties1.keySet().iterator();
    while(iterator.hasNext()) {
      String key = (String)iterator.next();
      String value1  = properties1.getProperty(key);
      String value2  = properties1.getProperty(key);
      if(!value1.equals(value2)) return false;
    }
  
    return true;
  }
  
  private static boolean compareRegions(Region [] regions1, Region [] regions2) {
    if(regions1.length != regions2.length) return false;

    for(int i = 0; i < regions1.length; i++) {
      if(!compareRegion(regions1[i], regions2[i])) {
        return false;
      }
    }

    return true;
  }

  private static boolean compareRegion(Region region1, Region region2) {
    if(!region1.getName().equals(region2.getName())) return false;
    if(region1.getType() != region2.getType()) return false;
    
    if(!compareArrays(region1.getPaths(), region2.getPaths())) {
      return false;
    }
  
    return true;
  }
}


