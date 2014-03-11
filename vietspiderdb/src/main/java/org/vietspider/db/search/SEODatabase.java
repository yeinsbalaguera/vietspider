/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.search;

import java.io.File;
import java.util.Calendar;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.HashStringDatabase;
import org.vietspider.serialize.XML2Object;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 10, 2010  
 */
public class SEODatabase extends HashStringDatabase implements Runnable {

  private volatile static SEODatabase instance;

  public static synchronized SEODatabase getInstance() {
    if(instance != null) return instance;
    File folder = UtilFile.getFolder("content/seo/db/");
    try {
      instance = new SEODatabase(folder);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    return instance; 
  }
  
  protected Queue<SEOData> waitData = new ConcurrentLinkedQueue<SEOData>();
  private SEODataExportor exportor;
  private MailDataSender sender;

  private SEODatabase(File folder) throws Exception {
    super(folder, "seo", 1*1024*1024l);
    exportor = new SEODataExportor();
    sender = new MailDataSender();
    Application.addShutdown(new Application.IShutdown() {

      public String getMessage() { return "Close SEO Database";}

      public int getPriority() { return 2; }

      public void execute() {
        close();
      }
    });
    
    new Thread(this).start();
  }
  
  public void run() {
    while(true) {
      try {
        while(!waitData.isEmpty()) {
          save1(waitData.poll());
        }
        
        Calendar calendar = Calendar.getInstance();
        exportor.export(calendar);
        
        Thread.sleep(1*1000l);
      } catch (Exception e) {
        LogService.getInstance().setThrowable("SEO_PROCESS", e);
      }
    }
  }

  public void save(SEOData data) {
    waitData.add(data);
  }
  
  private void save1(SEOData data) {
//    System.out.println("==== >"+ data + " : "+ waitData.size());
    byte [] key = data.getKey();
    if(data.getType() == SEOData.DELETE) {
      map.remove(key);
      return;
    }

    if(map.containsKey(key)) {
      SEOData oldData = null;
      try {
        String xml = map.get(key);
        oldData = XML2Object.getInstance().toObject(SEOData.class, xml);
        if(!oldData.needUpdate()) return;
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    
    exportor.add(data);
    sender.add(data);
    
    try {
      data.updateTime();
      map.put(key, data.toXML());      
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    
    counter++;
    if(counter < 50) return;
    db.sync();
    counter = 0;
  }

 /* public void active(IProcess iprocess) {
    Iterator<byte[]> iterator = map.keySet().iterator();
    iprocess.startHandle();

    //for test
    SEOData test = new SEOData();
    test.setData("nhudinhthuan@yahoo.com");
    test.setType(SEOData.EMAIL);
    try {
      iprocess.handle(test);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return;
    }

    boolean isContinue = true;
    while(iterator.hasNext() && isContinue) {
      byte[] key = iterator.next();
      SEOData data = null;
      try {
        String xml = map.get(key);
        data = XML2Object.getInstance().toObject(SEOData.class, xml);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        iterator.remove();
      }

      if(data == null) continue;

      if(iprocess.isExpireData(data)) {
        iterator.remove();
        continue;
      }

      try {
        iprocess.handle(data);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
        isContinue = false;
      }

      try {
        String xml  = data.toXML();
        map.put(key, xml);
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
    iprocess.finishHandle();
  }*/
  
  /*private static class Data {
    
    private IProcess process;
    private SEOData value;
    
    private Data(IProcess process, SEOData value) {
      this.process = process;
      this.value = value;
    }
  }*/

/*  public interface IProcess {

//    public void startHandle();

    public void handle(SEOData data) throws Exception ;

//    public void finishHandle();

    public boolean isExpireData(SEOData data);

    public void export(SEOData data);

  }*/

}
