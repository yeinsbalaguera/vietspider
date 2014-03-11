/***************************************************************************
 * Copyright 2004-2006 The VietSpider Studio All rights reserved.  * 
 **************************************************************************/
package org.vietspider.ui.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.common.io.ExceptionWriter;
import org.vietspider.common.io.ExceptionWriter.TraceObject;
import org.vietspider.common.io.UtilFile;

/**
 * @by thuannd (nhudinhthuan@yahoo.com)
 * Jun 26, 2005
 */
public final class ClientLog {
  
  private boolean print = true;
  
  private ExceptionWriter writer;
  
  private static ClientLog LOG;
  
  public static ClientLog getInstance() {
    if(LOG == null) LOG = new ClientLog();
    return LOG;
  }
  
  public ClientLog() {
    print = "true".equals(System.getProperty("vietspider.test"));
    writer = new ExceptionWriter("client/logs");
    new Thread(writer).start();
    new Thread(new CleanData()).start();
  }
  
  public void setException(Object obj, Exception exp) {
    if(exp == null) return;
    if(print) exp.printStackTrace();
    writer.put(new TraceObject(null, exp, null)); 
    if(obj == null || exp instanceof NullPointerException) return;
    Shell shell = (Shell) obj;
    if(shell.isDisposed()) return;
    MessageBox messageBox = new MessageBox(shell, SWT.OK);    
    if(exp.getMessage() != null) {
      messageBox.setMessage(exp.getMessage());
    } else {
      messageBox.setMessage(exp.toString());
    }
    messageBox.open();
  }
  
  
  public void setThrowable(Object obj, Throwable throwable) {
    if(throwable == null) return;
    if(print) throwable.printStackTrace();
    if(writer.isOutOfSize()) writer.write();
    writer.put(new TraceObject(obj != null ? obj.toString() : null, throwable, null));
    if(obj == null || throwable instanceof NullPointerException) return;
    Shell shell = (Shell) obj;
    MessageBox messageBox = new MessageBox(shell, SWT.OK);    
    if(throwable.getMessage() != null) {
      messageBox.setMessage(throwable.getMessage());
    } else {
      messageBox.setMessage(throwable.toString());
    }
    messageBox.open();
  }

  public void setMessage(Object obj, Exception exp) {
    if(exp == null) return;
    if(exp instanceof NullPointerException) {
      exp.printStackTrace();
    }
    
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
    if(obj == null) return;
    Shell shell = (Shell) obj;
    MessageBox messageBox = new MessageBox(shell, SWT.OK);
    if(elements[1] != null) messageBox.setMessage(elements[1]);
    messageBox.open();
  }
  //new ClientProperties().getProperties()
  public void showMessage(Shell shell, Class<?> owner, String key) {
    MessageBox message = new MessageBox(shell, SWT.YES );    
    try {
      if(owner != null) {
        ClientRM resources = new ClientRM(owner.getSimpleName());
        message.setMessage(resources.getLabel(key));
      } else {
        message.setMessage(key);
      }
      message.open();
    } catch (Exception e) {
      setMessage(shell, e);
    }
  }
  
  public static class CleanData implements Runnable {

    public void run() {
      while(true) {
        try {
          delele(UtilFile.getFolder("client/logs"), 10);
          Thread.sleep(12000);
        }catch (Exception exp) {
          exp.printStackTrace();
        }
      }
    }

    public void delele(File folder, int expire){
      File [] files = UtilFile.listFiles(folder);
      try{
        delete(files, expire); 
      }catch(Exception exp){
        exp.printStackTrace();
      }
    }

    private void delete(File [] files, int expire) throws Exception {
      if(files == null) return;
      List<File> deletes = new ArrayList<File>();
      for(int i = 0; i < files.length; i++) {
        if(files[i].isFile() && files[i].length() < 2){
          files[i].delete();
          continue;
        }
        if(files[i].isDirectory()){
          File [] listDir = files[i].listFiles();
          if(listDir == null || listDir.length < 1){
            files[i].delete();
            continue;
          }
        }
        deletes.add(files[i]);
      }

      for(int i = expire; i < deletes.size(); i++) {
        if(files[i].isFile()){
          files[i].delete();
          continue;
        } 
        UtilFile.deleteFolder(files[i]);
      }
    }
  }

}
