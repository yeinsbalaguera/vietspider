/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.link.generator;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vietspider.common.text.SWProtocol;
import org.vietspider.io.websites2.WebsiteStorage;
import org.vietspider.link.generator.Generator;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 16, 2009  
 */
@SuppressWarnings("unused")
public class WebsiteDetector implements Generator {
  
  private String sourceFullName;
  
  public WebsiteDetector(String sourceFullName, String...values) {
    this.sourceFullName = sourceFullName;
  }
  
  public short getType() { return Generator.SCRAN_LINK_GENERATOR; }
  
  public void generate(List<String> list) {
    if(!WebsiteStorage.DETECT) return;
    
    HashMap<String,String> map  =  new HashMap<String, String>();
    for(int i = 0; i < list.size(); i++) {
      String value = list.get(i);
      if(value == null) continue;
      
      if(SWProtocol.lastIndexOfWWW(value, 0) == 3) {
        value = "http://"+value;;
      }
      if(SWProtocol.isHttp(value)) {
        try {
          String host = new URL(value).getHost();
          if(map.containsKey(host)) continue;
          map.put(host, value);
        } catch (Exception e) {
        }
      }
    }
    
    Iterator<String> iterator = map.keySet().iterator();
    List<String> values = new ArrayList<String>();
    while(iterator.hasNext()) {
      values.add(map.get(iterator.next()));
    }
    WebsiteStorage.getInstance().save(values);
    //end method
  }
  
}
