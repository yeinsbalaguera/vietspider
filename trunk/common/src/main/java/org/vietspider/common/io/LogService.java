/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.io;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 15, 2007  
 */
public class LogService {
  
  private volatile static Log LOG;
  
  public synchronized static Log getInstance() {
    if(LOG == null) LOG = new DefaultLog(); 
    return LOG; 
  }
  
  public static void createInstance(Class<?> clazz) {
    try {
      LOG = (Log)clazz.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static interface Log {
    
    public void setMessage(Object label,  Exception exp, String message) ;
    
    public void setMessage(Exception exp, String message) ;
    
//    @Deprecated()
//    public void setException(Exception exp);
//    
//    @Deprecated()
//    public void setException(Object label, Exception exp);
//    
//    @Deprecated()
//    public void setException(Object label, Exception exp, String message);
    
    public void setThrowable(Throwable throwable);
    
    public void setThrowable(Object label, Throwable throwable);
    
    public void setThrowable(Object label, Throwable throwable, String message);
    
  }
  
  public static class DefaultLog implements Log {
    
    public void setException(Object label, Exception exp) {
      System.err.println(label);
      if(exp != null) exp.printStackTrace();
    }
    
    public void setException(Object label, Exception exp, String message){
      System.err.println(label+ ": " +message);
      if(exp != null) exp.printStackTrace();
    }
    
    public void setMessage(Object label,  Exception exp, String message) {
      System.err.println(label+ ": " +message);
      if(exp != null)  exp.printStackTrace();
    }
    
    public void setMessage(Exception exp, String message) {
      System.err.println(message);
      if(exp != null) exp.printStackTrace();
    }
    
    public void setException(Exception exp) {
      if(exp != null) exp.printStackTrace();
    }
    
    public void setThrowable(Throwable throwable) {
      if(throwable != null)throwable.printStackTrace();
    }
    
    public void setThrowable(Object label, Throwable throwable) {
      System.err.println(label);
      if(throwable != null) throwable.printStackTrace();
    }
    
    public void setThrowable(Object label, Throwable throwable, String message){
      System.err.println(label+ ": " +message);
      if(throwable != null)throwable.printStackTrace();
    }
    
  }
}
