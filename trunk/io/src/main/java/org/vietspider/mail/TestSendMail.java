/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.mail;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 11, 2008  
 */
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TestSendMail {

  public void sendSSLMessage(String recipients[], String subject, String message) throws MessagingException {
    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.auth", "true");
    props.put("mail.debug", "true");
    props.put("mail.smtp.port", "465");
    props.put("mail.smtp.socketFactory.port", "465");
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.socketFactory.fallback", "false");

    Session session = Session.getDefaultInstance(props,
        new javax.mail.Authenticator() {

      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("admin@nik.vn", "15gh67");
      }
    });
    
    session.setDebug(true);

    Message msg = new MimeMessage(session);
    InternetAddress addressFrom = new InternetAddress("admin@nik.vn");
    msg.setFrom(addressFrom);

    InternetAddress[] addressTo = new InternetAddress[recipients.length];
    for (int i = 0; i < recipients.length; i++) {
      addressTo[i] = new InternetAddress(recipients[i]);
    }
    msg.setRecipients(Message.RecipientType.TO, addressTo);

    msg.setSubject(subject);
    msg.setContent(message, "text/plain");
    Transport.send(msg);
  }

  public static void main(String args[]) throws Exception {
    String emailMsgTxt = "Vn crawler test  chuan bi lam viec";
    String emailSubjectTxt = "test nhe";
    String [] sendTo = {"nhudinhthuan@yahoo.com"};
    new TestSendMail().sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt);
    System.out.println("Sucessfully Sent mail to All Users");
  }
}
