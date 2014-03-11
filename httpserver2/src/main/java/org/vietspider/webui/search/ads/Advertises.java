/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.webui.search.ads;

import java.util.HashMap;
import java.util.List;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 4, 2010  
 */
public class Advertises extends Thread {
  
  private static Advertises instance;
  
  public synchronized static Advertises getInstance() {
    if(instance == null) instance = new Advertises();
    return instance;
  }
  
  private List<Advertise> datas = null;
  private AdsLoader loader;
  private HashMap<Integer, Integer> visit = new HashMap<Integer, Integer>();
  
  public Advertises() {
    loader = new AdsLoader();
    datas = loader.load();
    this.start();
  }
  
  public void run() {
    while(true) {
      datas = loader.load();
      try {
        Thread.sleep(5*60*1000l);
      } catch (Exception e) {
      }
    }
  }
  
  public Advertise next(int type) {
    Integer index = visit.get(type);
    if(index == null) {
      index = new Integer(0);
      visit.put(type, index);
    }
    
    if(index >= datas.size()) index = 0;
    
    while(index < datas.size()) {
     Advertise advertise = datas.get(index);
     index++;
     visit.put(type, index);
     if(advertise.getType() == type && advertise.isValid()) {
       return advertise;
     }
    }
    
    index = 0;
    visit.put(type, index);
    
    return null;
  }

}
