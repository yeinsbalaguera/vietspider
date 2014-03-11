package org.vietspider.mail;


/*
 To use this program, change values for the following three constants,

 SMTP_HOST_NAME -- Has your SMTP Host Name
 SMTP_AUTH_USER -- Has your SMTP Authentication UserName
 SMTP_AUTH_PWD  -- Has your SMTP Authentication Password

 Next change values for fields

 emailMsgTxt  -- Message Text for the Email
 emailSubjectTxt  -- Subject for email
 emailFromAddress -- Email Address whose name will appears as "from" address

 Next change value for "emailList".
 This String array has List of all Email Addresses to Email Email needs to be sent to.
 Next to run the program, execute it as follows,
 SendMailUsingAuthentication authProg = new SendMailUsingAuthentication();

 */

public class SendMailUsingAuthentication {

	/*//private static final String SMTP_HOST_NAME = "smtp.mail.yahoo.com";
	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_AUTH_USER = "lebienthuy";
	private static final String SMTP_AUTH_PWD = "hhh";
	private static final int SMTP_PORT = 465;

	private static final String emailMsgTxt = "Online Order Confirmation Message. <br/>Also <br/> include the Tracking Number.";
	private static final String emailSubjectTxt = "Order Confirmation Subject";
	private static final String emailFromAddress = "lebienthuy@gmail.com";

	// Add List of Email address to who email needs to be sent to
	private static final String[] emailList = { "lethuytc2000@yahoo.com" };

	public static void main(String args[]) throws Exception {
		System.out.println(" start");
		new MailService().sendMessage(emailSubjectTxt, emailMsgTxt);
		System.out.println("finish");
//		SendMailUsingAuthentication smtpMailSender = new SendMailUsingAuthentication();
//		smtpMailSender.postMail(emailList, emailSubjectTxt, emailMsgTxt, emailFromAddress);
	}

	public void postMail(String recipients[], String subject, String message, String from) {
		try {
			boolean debug = false;

			//Set the host smtp address
			//  Set the host smtp address

			Properties props = new Properties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.port", SMTP_PORT);
			props.put("mail.smtp.starttls.enable", "true");

			props.put("mail.smtp.host", SMTP_HOST_NAME);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.socketFactory.port", SMTP_PORT);
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");

			SecurityManager security = System.getSecurityManager();

			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(props, auth);
			session.setDebug(debug);

			// create a message

			Message msg = new MimeMessage(session);

			// set the from and to address

			InternetAddress addressFrom = new InternetAddress(from);
			msg.setFrom(addressFrom);

			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);

			// Setting the Subject and Content Type

			msg.setText(message);
			msg.setSubject(subject);
			msg.setContent(message, "text/plain");

			Transport.send(msg);

		} catch (MessagingException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	*//**
	 * SimpleAuthenticator is used to do simple authentication
	 * when the SMTP server requires it.
	 *//*
	private class SMTPAuthenticator extends javax.mail.Authenticator {

		public PasswordAuthentication getPasswordAuthentication() {
			try {
				String username = SMTP_AUTH_USER;
				String password = SMTP_AUTH_PWD;
				System.out.println("username  " + username + "     Password " + password);
				return new PasswordAuthentication(username, password);
			} catch (Exception e) {

			}
			return null;
		}
	}*/

}
