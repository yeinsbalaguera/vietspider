/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.search;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 3, 2010  
 */
class YahooMailSender {
  
  void send(String title, String content, String [] to) throws Exception {
    Properties props = System.getProperties();
    props.put("mail.smtp.host", "smtp.mail.yahoo.com");
    props.put("mail.smtp.auth", "true");

    Session session = Session.getDefaultInstance(props, null);
    MimeMessage message = new MimeMessage(session);

    message.setFrom(new InternetAddress("niksearch1@yahoo.com"));
    InternetAddress [] toAddresses = new InternetAddress[to.length];
    for(int i = 0; i < to.length; i++) {
      toAddresses[i] = new InternetAddress(to[i]); 
    }
    message.addRecipients(Message.RecipientType.TO, toAddresses);
    message.setSubject(title);
    message.setText(content);
    
    Transport transport = session.getTransport("smtp");
    transport.connect("smtp.mail.yahoo.com", "niksearch1", "nik12345");
    message.saveChanges();
    transport.sendMessage(message, message.getAllRecipients());
    transport.close();
  }
}
