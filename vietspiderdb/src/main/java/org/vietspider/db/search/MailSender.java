package org.vietspider.db.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.PropertiesFile;
import org.vietspider.common.io.UtilFile;

import com.sun.mail.smtp.SMTPSendFailedException;

public class MailSender {

  private List<Session> sessions;
  private volatile int sessionIndex = 0;
  private InternetAddress fromAddress;
  private boolean disable = false;
  
  private long resendTime = 5*24*60*60*1000l;

  public MailSender() {
  }

  public void createSession() {
    sessions = new ArrayList<Session>();
    Properties properties = loadConfig();
    if(properties == null) return;
    String host = properties.getProperty("mail.smtp.host");
    if(host == null || host.trim().isEmpty()) return;
    String port = properties.getProperty("mail.smtp.port");
    
    String[] accounts = properties.getProperty("mail.user").split(",");
    String[] passwords = properties.getProperty("mail.password").split(",");
//    System.out.println(account + " ====  >" + password);
    String alias = properties.getProperty("mail.alias");
    boolean isSSLMailServer = "true".equals(properties.getProperty("mail.ssl.server"));
    
    disable = "true".equals(properties.getProperty("disable"));

    Properties mailProperties = new Properties();
    mailProperties.put("mail.smtp.host", host);
    mailProperties.put("mail.smtp.port", port);
    mailProperties.put("mail.smtp.auth", "true");
//    mailProperties.put("mail.debug", "true");
    if(isSSLMailServer) mailProperties.put("mail.smtp.starttls.enable", "true");

    String socketPort = properties.getProperty("mail.smtp.socketFactory.port");
    if(socketPort != null && socketPort.trim().length() > 0) {
      mailProperties .put("mail.smtp.socketFactory.port", socketPort);
    }

    String socketFactoryClass =  properties.getProperty("mail.smtp.socketFactory.class");
    if(socketFactoryClass != null && socketFactoryClass.trim().length() > 0) {
      mailProperties .put("mail.smtp.socketFactory.class", socketFactoryClass);
    }

    String socketFactoryFallback =  properties.getProperty("mail.smtp.socketFactory.fallback");
    if(socketFactoryFallback != null && socketFactoryFallback.trim().length() > 0) {
      mailProperties .put("mail.smtp.socketFactory.fallback", socketFactoryFallback);
    }

    mailProperties .put("mail.smtp.socketFactory.fallback", "false");
    
    //default 2592000000
//   LogService.getInstance().setMessage(null, " default: "+resendTime);
   
   String resendTimeValue = properties.getProperty("resend.time");
   if(resendTimeValue != null) {
     try {
       resendTime = Long.parseLong(resendTimeValue);
     } catch (Exception e) {
       LogService.getInstance().setMessage(null, "put resend.time=" + resendTime);
    }
   }
//    LogService.getInstance().setMessage(null, "User config: " + resendTime);

    for(int i = 0; i < Math.min(accounts.length, passwords.length); i++) {
      createSession(mailProperties, alias, accounts[i], passwords[i]);
    }
   
  }
  
  private void createSession(Properties mailProperties,
      String alias, String account, String password) {
    LmsAuthenticator ax = new LmsAuthenticator(account, password);
    sessions.add(Session.getInstance(mailProperties, ax));
    try {
      fromAddress = new InternetAddress(account, alias);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  public long getResendTime() { return resendTime; }

  public void close() {
    for(Session session : sessions) {
      try {
        Transport trans = session.getTransport("smtp");
        trans.close();
      } catch (Exception e) {
        LogService.getInstance().setThrowable(e);
      }
    }
  }

  public void sendMessage(String subject, String text) throws Exception {
    Properties properties = loadConfig();
    String sendToEmail = properties.getProperty("mail.to.email");
    if(sendToEmail == null || sendToEmail.trim().isEmpty()) return;
    send(subject, text, sendToEmail);
  }

//  @Deprecated
//  public void sendMessage(String toMail, String subject, String text) throws Exception {
//    sendMessage(toMail, subject, text, 1);
//  }

//  @Deprecated()
//  public void sendMessage(String toMail, 
//      String subject, String text, int time) throws Exception {
//    send(subject, text, time, toMail);
//  }
  
  public void send(String subject, String text, String...addresses) throws Exception {
    send(subject, text, 1, addresses);
  }
    
  public void send(String subject, String text, int time, String...addresses) throws Exception {
    if(disable) return;
    Calendar calendar = Calendar.getInstance();
    int hour  = calendar.get(Calendar.HOUR_OF_DAY);
    if((hour >= 7 && hour < 11) 
        || (hour >=14 && hour <= 16)
        || (hour >=19 && hour < 21)) return;
    if(addresses.length < 1) return;
    if(sessions.size() < 1)throw new NoSessionException();
    if(sessionIndex >= sessions.size()) {
      sessionIndex = 0;
    } 
    
    Session session = sessions.get(sessionIndex);
    sessionIndex++;
    
    if(session == null) return ;

    if(Application.LICENSE == Install.PROFESSIONAL 
        || Application.LICENSE == Install.PERSONAL) return;

    Message message = new MimeMessage(session);

    try {
      InternetAddress toAddress = new InternetAddress(addresses[0]);
      
//      System.out.println(" start send mai "+subject + "  : "+ session);

      message.setFrom(fromAddress);
      message.setRecipient(Message.RecipientType.TO, toAddress);
      LogService.getInstance().setMessage(null, " Send mail to "+ toAddress);
      
      for(int i = 1; i < addresses.length; i++) {
        LogService.getInstance().setMessage(null, " Send mail to "+ addresses[i]);
        InternetAddress toAddress2 = new InternetAddress(addresses[i]);
        message.setRecipient(Message.RecipientType.TO, toAddress2);
      }

      message.setSubject(subject);
      message.setContent(text, "text/html; charset=utf-8");

      message.setSentDate(Calendar.getInstance().getTime());
      message.saveChanges();
      Transport.send(message);
    } catch (AuthenticationFailedException e) {
      if(time < 2) {
        session = null;
        createSession();
        send(subject, text, 2, addresses);
      } else {
        sessions.remove(session);
        throw e;
//        LogService.getInstance().setMessage(e, e.toString());
      }
    } catch (SMTPSendFailedException e) {
      if(e.getMessage() == null) {
        LogService.getInstance().setThrowable(e);
      } else  if(e.getMessage().indexOf("quota exceeded") > -1) {
        sessions.remove(session);
        throw e;
      }
    } catch (MessagingException e) {
      LogService.getInstance().setThrowable(e);
      
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
    }
  }

  private static class LmsAuthenticator extends Authenticator {

    private String address = null, password = null;

    public LmsAuthenticator(String addr, String pw) {
      super();
      this.address = addr;
      this.password = pw;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(address, password);
    }
  }

  static Properties loadConfig() {
    Properties properties = new Properties();
    PropertiesFile propertiesLoader = new PropertiesFile(true);
    File fileConfig = new File(UtilFile.getFolder("system"), "mail.config");
    if(!fileConfig.exists() || fileConfig.length() < 1) return null;
    try {
      properties = propertiesLoader.load(fileConfig);
    } catch (Exception e) {
      LogService.getInstance().setThrowable(e);
      return null;
    }
    return properties;
  }
  
  @SuppressWarnings("all")
  public static class NoSessionException extends Exception {
    
  }
  
}