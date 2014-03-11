/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.mail;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 3, 2010  
 */
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class mailer
{
  public static void main (String args[]) throws Exception {
//    String smtpHost = "smtp.mail.yahoo.com";
    String from= "niksearch1@yahoo.com";
    String to= "nhudinhthuan@gmail.com";
    String smtpHost = "smtp.mail.yahoo.com";
    String popHost = "pop.mail.yahoo.com";
//    String from = args[0]; // with @yahoo.com
//    String to = args[1];
//    String username = args[2];
//    String password = args[3];

    Properties props = System.getProperties();
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.auth", "true");

    Session session = Session.getDefaultInstance(props, null);
    MimeMessage message = new MimeMessage(session);

    message.setFrom(new InternetAddress(from));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    message.setSubject("hi nhe tu yahoo");
    message.setText("Test Message");
    
    Transport transport = session.getTransport("smtp");
    transport.connect(smtpHost, "niksearch1", "nik12345");
    message.saveChanges();
    transport.sendMessage(message, message.getAllRecipients());
    transport.close();


  }
}