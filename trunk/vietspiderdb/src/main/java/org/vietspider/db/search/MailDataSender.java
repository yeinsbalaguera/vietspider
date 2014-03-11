/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.search;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.mail.internet.InternetAddress;

import org.vietspider.common.io.LogService;
import org.vietspider.db.search.MailSender.NoSessionException;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2011  
 */
class MailDataSender extends Thread {

  protected volatile Queue<String> waitData = new ConcurrentLinkedQueue<String>();
  private MailSender sender;
  private MailContent content;

  public MailDataSender() {
    sender = new MailSender();
    content = new MailContent();
    sender.createSession();
    start();
  }

  void add(SEOData data) {
    if(data.getType() != SEOData.EMAIL) return;
    waitData.add(data.getData()); 
  }

  public void run() {
    while(true) {
      try {
        if(waitData.isEmpty()) {
          Thread.sleep(5*1000l);
          continue;
        }
        while(!waitData.isEmpty()) {
          send(waitData.poll(), 0);
        }

        Thread.sleep(500l);
      } catch (Exception e) {
        LogService.getInstance().setThrowable("SEO_PROCESS", e);
      }
    }
  }

  private void send(String address, int time) {
//    System.out.println(address + "  : " + time);
    if(time >= 3) return;
    String title = content.getMailTitle();
    String text = content.getMailContent();
    if(title == null || title.trim().isEmpty()) return;
    if(text == null || text.trim().isEmpty()) return;

    try {
      InternetAddress.parse(address);  
    } catch (Exception e) {
      return;
    }

//    System.out.println(address+  " : "+ title);

    try {
      sender.send(title, text, address);
    } catch (NoSessionException e) {
      LogService.getInstance().setMessage(e, "no session");
      sender.createSession();
      send(address, time+1);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
}
