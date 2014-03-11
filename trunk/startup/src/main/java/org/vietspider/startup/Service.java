/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.startup;

import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jul 11, 2007
 */
public class Service implements WrapperListener {
  
  private ActionRunner runner;
  
  public Service() {
  }
  
  @SuppressWarnings("unused")
  public void controlEvent(int event) {
  }
  
  
  @SuppressWarnings("unused")
  public Integer start(String[] args) {
    Thread actionThread;
    runner = new ActionRunner();
    actionThread = new Thread(runner);
    actionThread.start();
    return null;
  }
  
  public void finish() {
    System.exit(0);
    WrapperManager.stop(1);
  }
  
  public int stop(int exitCode) { 
    if (runner != null) runner.end();
    return exitCode;
  }
  
  private class ActionRunner implements Runnable {
    
    private boolean mAlive;
    private Object vietspider;

    public ActionRunner() { 
      mAlive = true;
    }

    public void run() {
      try {
        ClassLoaderCreator creator = new ClassLoaderCreator();
        URLClassLoader loader = creator.createLoader(true);
        Class<?> clazz  = loader.loadClass("org.vietspider.VietSpider");
        Method method = clazz.getDeclaredMethod("currentInstance", new Class[0]);
        System.setProperty("vietspider.server.icon", "true");
        vietspider = method.invoke(null, new Object[0]);
        
        method = clazz.getDeclaredMethod("setOSService", new Class[]{Object.class});
        method.invoke(vietspider, Service.this);
      }catch (Exception e) {
        e.printStackTrace();
        WrapperManager.stop(0);
        return;
      }
      
      while (mAlive) {
        try {
          Thread.sleep(5000);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      
    }

    public void end( ) { 
      /*if(vietspider != null) {
        try {
          Method method = vietspider.getClass().getDeclaredMethod("finish", new Class[]{});
          method.invoke(vietspider, new Object[]{});
        }catch (Exception exp) {
          exp.printStackTrace();
        }
      }*/
      mAlive = false;
    }
  }
  
  public static void main(String[] args) {
    WrapperManager.start(new Service(), args );
  }
  

}
