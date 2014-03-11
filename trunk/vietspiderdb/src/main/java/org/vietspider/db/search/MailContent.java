/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.search;

import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 9, 2011  
 */
public class MailContent {
  
  private String mailTitle;
  private String mailContent;
  
  public MailContent() {
    File file  = UtilFile.getFile("system", "mail.content.txt");
    try {
      String text = new String(RWData.getInstance().load(file), Application.CHARSET);
      int index = text.indexOf('\n');
      if(index > 0) {
        mailTitle = text.substring(0, index).trim();
        mailContent = text.substring(index+1).trim();
      }
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }
  
  public String getMailTitle() { return mailTitle; }
  public String getMailContent() { return mailContent; }
  
}
