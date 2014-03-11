/***************************************************************************
 * Copyright 2004-2006 The VietSpider Studio All rights reserved.  * 
 **************************************************************************/
package org.vietspider.io;

import org.vietspider.common.io.ExceptionWriter;
import org.vietspider.common.io.ExceptionWriter.TraceObject;
import org.vietspider.common.io.LogService;

/**
 * @by thuannd (nhudinhthuan@yahoo.com)
 * Jun 26, 2005
 */
public class LogDataImpl implements LogService.Log {
  
  protected boolean print = true;
  
  protected ExceptionWriter writer;
  
  public LogDataImpl() {
    this("track/logs/");
  }
  
  public LogDataImpl(String folder) {
    print = "true".equals(System.getProperty("vietspider.test"));
    writer = new ExceptionWriter(folder);
    writer.setPrint(print);
    new Thread(writer).start();
  }
  
  public void setMessage(Exception exp, String message) {
    if(exp != null && exp.getMessage() != null) {
      setMessage(" APPLICATION ", exp, exp.getMessage()+ " " + message);  
      return;
    }
    if(message.trim().isEmpty()) {
      new Exception().printStackTrace();
    }
    setMessage("APPLICATION ", exp, message);
  }
  
  public void setMessage(Object obj, Exception exp, String message) {
    String [] elements = new String[3];
    elements[0] = message;
    
    if(exp != null) {
      StackTraceElement[] traceElements = exp.getStackTrace();
      for(StackTraceElement trackElement : traceElements) {
        String track  = trackElement.toString();
//      System.out.println(value);
        if(track.indexOf("vietspider") < 0) continue;
        elements[1] = track;
        break;
      }
      
      elements[2] = exp.getMessage();
      if(elements[2] == null || elements[2].trim().isEmpty()) elements[2] = exp.toString();
    }
//    new Exception().printStackTrace();
    if(print) {
      for(String element : elements) {
        if(element == null) continue;
        System.out.println(element);
      }
    }
    if(obj != null && obj instanceof String) {
      writer.put(new TraceObject((String)obj, elements));
    } else {
      writer.put(new TraceObject(obj != null ? obj.getClass().getSimpleName().toUpperCase() : null, elements));
    }
    if(exp instanceof NullPointerException) setThrowable(exp);
  }
  
  public void setThrowable(Throwable throwable) {
    setThrowable(" APPLICATION ", throwable);
  }
    
  public void setThrowable(Object obj, Throwable throwable) {
    if(throwable == null) return;
    if(print) {
      if(obj != null) System.out.println(obj.toString());
      throwable.printStackTrace();
    }
    if(writer.isOutOfSize()) writer.write();
    writer.put(new TraceObject(obj != null ? obj.toString() : null, throwable, null));
  }
  
  public void setThrowable(Object obj, Throwable throwable, String message) {
    if(throwable == null) return;
    if(print) {
      if(obj != null) System.out.println(obj.toString());
      if(message != null) System.out.println(message);
      throwable.printStackTrace();
    }
    if(writer.isOutOfSize()) writer.write();
    if(obj != null && obj instanceof String) {
      writer.put(new TraceObject((String)obj, throwable, message));
    } else {
      writer.put(new TraceObject(obj != null ? obj.getClass().getSimpleName().toUpperCase() : null, throwable, message));
    }
//    writer.put(new TraceObject(obj != null ? obj.toString() : null, throwable, message));
  }

}
