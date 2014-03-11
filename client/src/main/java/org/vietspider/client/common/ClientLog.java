/***************************************************************************
 * Copyright 2004-2006 The VietSpider Studio All rights reserved.  * 
 **************************************************************************/
package org.vietspider.client.common;

import org.vietspider.common.io.ExceptionWriter;
import org.vietspider.common.io.ExceptionWriter.TraceObject;

/**
 * @by thuannd (nhudinhthuan@yahoo.com)
 * Jun 26, 2005
 */
final class ClientLog {
  
  private boolean print = true;
  
  private ExceptionWriter writer;
  
  private volatile static ClientLog LOG;
  
  static synchronized ClientLog getInstance() {
    if(LOG == null) LOG = new ClientLog();
    return LOG;
  }
  
  ClientLog() {
    print = "true".equals(System.getProperty("vietspider.test"));
    writer = new ExceptionWriter("client/logs");
    new Thread(writer).start();
  }
  
  void setException(Exception exp) {
    if(exp == null) return;
    if(print) exp.printStackTrace();
    writer.put(new TraceObject(null, exp, null)); 
    if(exp instanceof NullPointerException) return;
  }

  void setMessage(Exception exp) {
    if(exp == null) return;
    String [] elements = new String[2];
    
    StackTraceElement[] traceElements = exp.getStackTrace();
    for(StackTraceElement trackElement : traceElements) {
      String track  = trackElement.toString();
//      System.out.println(value);
      if(track.indexOf("vietspider") > -1) {
        elements[0] = track;
        break;
      }
    }
    
    elements[1] = exp.getMessage();
    if(elements[1] == null 
        || elements[1].trim().isEmpty()) elements[1] = exp.toString();
//    new Exception().printStackTrace();
    if(print) {
      for(String element : elements) {
        if(element == null) continue;
        System.out.println(element);
      }
    }
    writer.put(new TraceObject((String)null, elements));
    if(exp instanceof NullPointerException) setException(exp);
  }
  
}
