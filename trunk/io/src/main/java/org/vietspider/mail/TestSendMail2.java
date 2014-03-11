/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.mail;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 11, 2008  
 */
import java.io.File;

import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.db.search.MailSender;

public class TestSendMail2 {

  public static void main(String args[]) throws Exception {
    System.setProperty("vietspider.data.path", "D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    MailSender sender = new MailSender();
    String mailTitle  =  "aaa";
    String mailContent  =  "aaa";
    File file  = UtilFile.getFile("system", "mail.content.txt");
    try {
      byte [] bytes = RWData.getInstance().load(file);
      String text = new String(bytes, Application.CHARSET);
      int idx = text.trim().indexOf('\n');
      mailTitle = text.substring(0, idx);
      mailContent = text.substring(idx+1);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
    System.out.println(mailTitle);
    String toMail = "dung261@gmail.com";
//    String toMail = "nhudinhthuan@yahoo.com";
//    sender.sendMessage(toMail, mailTitle, mailContent);
//    System.out.println("Sucessfully Sent mail to All Users");
  }
}
