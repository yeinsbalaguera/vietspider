/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.mail;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 11, 2008  
 */
public class TestRecipientMail {
  
  public static void main(String[] args)  throws Exception  {
    
    Properties properties = new Properties();
    String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    
    properties.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
    properties.setProperty("mail.pop3.socketFactory.fallback", "false");
    properties.setProperty("mail.pop3.port",  "995");
    properties.setProperty("mail.pop3.socketFactory.port", "995");
    
    Session session = Session.getInstance(properties);
    Store store = session.getStore("pop3");
    
    String strHost = "pop.gmail.com";
    String strUser = "niksearch@gmail.com";
    String strPass = "thuan12345";
    
    store.connect(strHost, strUser, strPass);
    
//    Folder folder = store.getDefaultFolder();
    Folder folder = store.getFolder("INBOX");
    folder.open(Folder.READ_ONLY);
    
        
    Message [] messages = folder.getMessages();
    System.out.println(messages.length);
    for(Message message : messages) {
      System.out.println("===============================================================");
      System.out.println(message.getSubject());
      Multipart multipart = (Multipart)message.getContent();
      System.out.println(multipart.getBodyPart(0).getContent());
    }
  }
  
}
