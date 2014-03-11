/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import org.vietspider.common.Application;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2009  
 */
public abstract class Pausable {
  
  protected boolean pause = false;
  protected ConcurrentSkipListSet<Object> pauseObjects = new ConcurrentSkipListSet<Object>();
  
  protected List<Timer> timers = new ArrayList<Timer>();
  
  void addTimer(Timer timer) { timers.add(timer); }
  
  public void start() { 
    for(int i = 0; i < timers.size(); i++) {
      Thread thread = new Thread(timers.get(i));
      thread.start();
    }
  }
  
//  protected  volatile boolean live = true;
//  protected volatile int sleep  = 1*1000; 
  
 /* public void run () {
    while(live) {
      try {
        if(!isPause()) execute();
      } catch (Throwable e) {
        LogService.getInstance().setThrowable("CRAWLER POOL",  e, e.getMessage());
      }
      
      try {
        Thread.sleep(sleep);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }O
  }*/
  
//  abstract public void execute() throws Throwable ;
  
  public boolean isPause() {
    return pause || pauseObjects.size() > 0 || Application.hasError();
  }
  
  public void setPause(boolean value) {
    if(Application.hasError()) return;
    pause = value; 
  }
  
  public void removePauseByService(Object object) {
    pauseObjects.remove(object);
  }
  
  public void addPauseByService(Object object) {
    pauseObjects.add(object);
  }

}
