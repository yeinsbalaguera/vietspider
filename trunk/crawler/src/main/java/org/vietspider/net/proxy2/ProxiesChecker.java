/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.proxy2;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 8, 2011  
 */
class ProxiesChecker  extends Thread {
  
  private SingleTest[] testes = new SingleTest[5];
  private Queue<String> queue = new LinkedList<String>();
  private ProxiesSaver saver;
  
  public ProxiesChecker() {
    for(int i = 0; i < testes.length; i++) {
      testes[i] = new SingleTest();
    }
    saver = new ProxiesSaver();
    this.start();
  }
  
  public void run() {
    while(true) {
      if(queue.isEmpty()) {
        List<String> proxies = ProxyStorage.getInstance().get();
        for(int i = 0; i < proxies.size(); i++) {
          queue.add(proxies.get(i));
        }
        
        if(queue.isEmpty()) {
          saver.saveProxies();
        }
      }
      
      for(int i = 0; i < testes.length; i++) {
        process(testes[i]);
      }

      try {
        Thread.sleep(queue.isEmpty() ? 30*1000l : 500l);
      } catch (Exception e) {
      }
    }
  }
  
  private void process(SingleTest test) {
    if(test.isLive() && test.isTimeout()) test.abort();

    if(!test.isLive()) {
      if(test.isSuccess())  {
        String proxy = test.getProxy();
        proxy  = "\n" + proxy + "/" + String.valueOf(test.getTime());
        //      System.out.println("==== duoc 1 cai > "+ proxy);
        File file  = new File(UtilFile.getFolder("system/proxy/"), "proxies.temp.txt");
        try {
          RWData.getInstance().append(file, proxy.getBytes());
        } catch (Exception e) {
          return;
        }
//        System.out.println(" ====  > "+ test.getProxy());
      }
      
      if(!queue.isEmpty()) test.startProxy(queue.poll());
    }
  }
  
  public static void main(String[] args) {
    try {
      File file  = new File("F:\\Bakup\\codes\\vietspider3\\test\\news\\data\\");
      
      System.setProperty("save.link.download", "true");
      System.setProperty("vietspider.data.path", file.getCanonicalPath());
      System.setProperty("vietspider.test", "true");
      
      new ProxiesChecker();
    } catch (Exception e) {
      e.printStackTrace();
    }      
  }
}
