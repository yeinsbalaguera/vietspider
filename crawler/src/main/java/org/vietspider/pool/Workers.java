/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.common.io.LogService;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 22, 2009  
 */
public class Workers extends Thread {
  
  private final static Workers instance = new Workers();
  
  final static  Workers getInstance() { return instance; }
  
  private List<Data>  works = new ArrayList<Data>();
  
  public Workers() {
    start();
  }
  
  public void addWork(Worker<?,?> work) {
    for(int i = 0; i < works.size(); i++) {
      if(works.get(i).getWorker() == work) {
        works.get(i).setValue(work.getValue());
        return;
      }
    }
    works.add(new Data(work));
  }
  
  public void run() {
    while(true) {
      for(int i = 0; i < works.size(); i++) {
        Worker<?, ?> work = works.get(i).getWorker();
        if(work.getValue() == null) {
          works.get(i).setValue(null);
          continue;
        }
        if(works.get(i).getValue() != work.getValue()) {
          works.get(i).setValue(work.getValue());
          continue;
        }
        String message = "Abort worker " + work.getId() + "/" + work.getValue();
        LogService.getInstance().setMessage("WORKER", null, message);
//        System.out.println(" chuan bi abort work "+ work);
        work.abort();
      }
      
      try {
        Thread.sleep(5*60*1000);
      } catch (Exception e) {
      } 
    }
  }
  
  private static class Data {
    
    private Worker<?,?> worker;
    
    private Object value;
    
    public Data(Worker<?,?> worker) {
      this.worker = worker;
    }

    private Worker<?,?> getWorker() { return worker; }

    private Object getValue() { return value; }

    private void setValue(Object value) { this.value = value;  }
    
  }

}
