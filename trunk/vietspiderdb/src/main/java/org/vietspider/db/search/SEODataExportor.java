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
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.CalendarUtils;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 7, 2010  
 */
class SEODataExportor {
  
  private int exportDate;
  private File mobileFile;
  private File mailFile;
  private Queue<SEOData> queue;
  
  public SEODataExportor() {
    queue = new ConcurrentLinkedQueue<SEOData>();
  }
  
  void export(Calendar calendar) {
    StringBuilder mobileBuilder = new StringBuilder();
    StringBuilder mailBuilder = new StringBuilder();
    while(!queue.isEmpty()) {
      SEOData data = queue.poll();
      if(data.getType() == SEOData.PHONE) {
        if(mobileBuilder.length() > 0) mobileBuilder.append('\n');
        mobileBuilder.append(data.getData());
      } else  if(data.getType() == SEOData.EMAIL) {
        if(mailBuilder.length() > 0) mailBuilder.append('\n');
        mailBuilder.append(data.getData());
      }
    }
    
    if(mobileBuilder.length() > 0 || mailBuilder.length() > 0) createExportFile(calendar);
    // save mobile
    if(mobileBuilder.length() > 0) {
      if(mobileFile.exists() && mobileFile.length() > 0) mobileBuilder.insert(0, '\n');
      try {
        RWData.getInstance().append(mobileFile, mobileBuilder.toString().getBytes(Application.CHARSET));
      } catch (Exception e) {
        LogService.getInstance().setThrowable("SEO_PROCESS", e);
      }
    }
    
//    System.out.println("mail builder " + mailBuilder);

    // save mail
    if(mailBuilder.length() > 0) {
      if(mailFile.exists() && mailFile.length() > 0) mailBuilder.insert(0, '\n');
      try {
        RWData.getInstance().append(mailFile, mailBuilder.toString().getBytes(Application.CHARSET));
      } catch (Exception e) {
        LogService.getInstance().setThrowable("SEO_PROCESS", e);
      }
    }
  }
  
  void add(SEOData data) {
//    System.out.println(" add data to queue for exporting: "+data.getData());
    queue.add(data);
  }
  
  private void createExportFile(Calendar calendar) {
    int d = calendar.get(Calendar.DATE);
    if(d == exportDate && mobileFile != null && mailFile != null) return;
    this.exportDate = d;
    String name  = CalendarUtils.getFolderFormat().format(calendar.getTime());
    mobileFile = UtilFile.getFile("content/seo/mobile/", name+".txt");
    mailFile = UtilFile.getFile("content/seo/mail/", name+".txt");
  }

}
