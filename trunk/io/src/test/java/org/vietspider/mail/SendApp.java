package org.vietspider.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendApp {
	public static void send(String smtpHost, int smtpPort, String from, String to, String subject,
	    String content) throws AddressException, MessagingException {
		// Create a mail session
		java.util.Properties props = new java.util.Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", "" + smtpPort);
		Session session = Session.getDefaultInstance(props, null);
		// Construct the message
		Message msg = new MimeMessage(session);
		msg.setFrom(null);
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		msg.setSubject(subject);
		msg.setText(content);

		System.out.println(" 221dd11222222");
		// Send the message
		Transport.send(msg);
	}

	public static void main(String[] args) throws Exception {
		// Send a test message
		send("mail.newtechcs.com", 465, "vs@newtechcs.com",

		"lethuytc2000@yahoo.com", "hi ", "How are you WithRegards Vinay");
	}
}