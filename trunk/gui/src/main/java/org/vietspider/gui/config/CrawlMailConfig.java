/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.config;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
public class CrawlMailConfig extends Composite {
	
  private final static String MAIL_HOST = "mail.smtp.host"; 
  private final static String MAIL_PORT = "mail.smtp.port";
  private final static String MAIL_FALLBACK = "mail.smtp.socketFactory.fallback";
  private final static String SOCKET_CLASS = "mail.smtp.socketFactory.class";
  private final static String SEND_HOUR = "mail.send.hour";
  private final static String SOCKET_PORT = "mail.smtp.socketFactory.port";
  private final static String MAIL_SSL_SERVER = "mail.ssl.server";
  private final static String MAIL_ALIAS = "mail.alias";
  private final static String RESEND_TIME = "resend.time";
  private final static String MAIL_USER = "mail.user";
  private final static String MAIL_PASSWORD = "mail.password";
  private final static String MAIL_SEND_TO_EMAIL = "mail.to.email";

  private Text txtHost;
  private Text txtPort;
  private Text txtSocketPort;
  private Button butFallBack;
  private Text txtSocketClass;
  private Button butSSLServer;
  private Text txtMailAlias;
  private Text txtResendTime;
  private Text txtUser;
  private Spinner spinSendHour;
  private Text txtPassword;
  private Text txtSendToEmail;
  
  private Properties properties;

  public CrawlMailConfig(Composite parent, ApplicationFactory factory) {
    super(parent, SWT.NONE);
    
    setLayout(new GridLayout(2, false));
    factory.setComposite(this);

    GridData gridData;
    
    boolean enable = Application.LICENSE == Install.ENTERPRISE 
                    || Application.LICENSE == Install.SEARCH_SYSTEM;

//  ************************************************************************************************
    
    Composite composite = new Composite(this, SWT.NONE);
    composite.setLayout(new GridLayout(3, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    composite.setLayoutData(gridData);
    factory.setComposite(composite);
    
    Composite subcomposite = new Composite(composite, SWT.NONE);
    GridLayout gridLayout = new GridLayout(2, false);
    subcomposite.setLayout(gridLayout);
    factory.setComposite(subcomposite);

    factory.createLabel("txtMailHost");   
    txtHost = factory.createText();
    txtHost.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 150;
    txtHost.setLayoutData(gridData);

    subcomposite = new Composite(composite, SWT.NONE);
    subcomposite.setLayout(new GridLayout(2, false));
    factory.setComposite(subcomposite);

    factory.createLabel("lblMailPort");  
    txtPort = factory.createText();
    txtPort.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 50;
    txtPort.setLayoutData(gridData);
    
    subcomposite = new Composite(composite, SWT.NONE);
    subcomposite.setLayout(new GridLayout(2, false));
    factory.setComposite(subcomposite);

    factory.createLabel("lblSocketPort");  
    txtSocketPort = factory.createText();
    txtSocketPort.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 50;
    txtSocketPort.setLayoutData(gridData);
    
    factory.setComposite(this);

    factory.createLabel("lblSocketFactoryClass");  
    txtSocketClass = factory.createText();
    txtSocketClass.setFont(UIDATA.FONT_10);
    txtSocketClass.setText("javax.net.ssl.SSLSocketFactory");
    gridData = new GridData();
    gridData.widthHint = 400;
    txtSocketClass.setLayoutData(gridData);
    
    Composite optionComposite = new Composite(this, SWT.NONE);
    gridLayout = new GridLayout(3, false);
    optionComposite.setLayout(gridLayout);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    optionComposite.setLayoutData(gridData);
    factory.setComposite(optionComposite);
    
    butFallBack = factory.createButton(SWT.CHECK, factory.getLabel("butMailFallback"));
    butFallBack.setSelection(false);
    
    butSSLServer = factory.createButton(SWT.CHECK, factory.getLabel("butMailSSLServer"));
    butSSLServer.setSelection(false);
    
    Composite subComposite = new Composite(optionComposite, SWT.NONE);
    subComposite.setLayout(new GridLayout(2, false));
    factory.setComposite(subComposite);
    
    factory.createLabel("lblSleepTime");  
    spinSendHour = factory.createSpinner(SWT.BORDER);
    spinSendHour.setMinimum(0);
    spinSendHour.setMaximum(23);
    spinSendHour.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 50;
    spinSendHour.setLayoutData(gridData);
    
    factory.setComposite(this);
    
    composite = new Composite(this, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    composite.setLayoutData(gridData);
    factory.setComposite(composite);
    
    subcomposite = new Composite(composite, SWT.NONE);
    gridLayout = new GridLayout(2, false);
    subcomposite.setLayout(gridLayout);
    factory.setComposite(subcomposite);
    
    factory.createLabel("lblMailAlias");  
    txtMailAlias = factory.createText();
    txtMailAlias.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 120;
    txtMailAlias .setLayoutData(gridData);
    
    
    subcomposite = new Composite(composite, SWT.NONE);
    gridLayout = new GridLayout(2, false);
    subcomposite.setLayout(gridLayout);
    factory.setComposite(subcomposite);

    factory.createLabel("lblResendTime");  
    txtResendTime = factory.createText();
    txtResendTime.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 120;
    txtResendTime.setLayoutData(gridData);

    Composite usernameComposite = new Composite(this, SWT.NONE);
    usernameComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    usernameComposite.setLayoutData(gridData);
    factory.setComposite(usernameComposite);
    
    factory.createLabel("txtMailUser");
    txtUser = factory.createText();
    txtUser.setEditable(enable);
    gridData = new GridData();
    gridData.widthHint = 200;
    txtUser.setLayoutData(gridData);

    Composite passwordComposite = new Composite(this, SWT.NONE);
    passwordComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    passwordComposite.setLayoutData(gridData);
    factory.setComposite(passwordComposite);

    factory.createLabel("txtMailPass");
    txtPassword = factory.createText(); 
    txtPassword.setEditable(enable);
    txtPassword.setEchoChar('x');
    gridData = new GridData();
    gridData.widthHint = 200;
    txtPassword.setLayoutData(gridData);
    
    Composite sendToEmailComposite = new Composite(this, SWT.NONE);
    sendToEmailComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    sendToEmailComposite.setLayoutData(gridData);
    factory.setComposite(sendToEmailComposite);
    
    factory.createLabel("txtMailSendToEmail");
    txtSendToEmail = factory.createText();   
    txtSendToEmail.setEditable(enable);
    gridData = new GridData();
    gridData.widthHint = 180;
    txtSendToEmail.setLayoutData(gridData);
  }
  
  void setValue2UI(Properties properties_) {
    this.properties = properties_;
    
    load(txtHost, MAIL_HOST);
    load(txtPort, MAIL_PORT);
    load(txtSocketPort, SOCKET_PORT);
    load(butFallBack, MAIL_FALLBACK);
    load(txtSocketClass, SOCKET_CLASS);
    load(spinSendHour, SEND_HOUR);
    load(butSSLServer, MAIL_SSL_SERVER);
    load(txtUser, MAIL_USER);
    load(txtPassword, MAIL_PASSWORD);
    load(txtSendToEmail, MAIL_SEND_TO_EMAIL);
    load(txtMailAlias, MAIL_ALIAS);
    load(txtResendTime, RESEND_TIME);
  }
  
  void setData2Properties() throws Exception {
    properties.setProperty(MAIL_HOST, txtHost.getText());
    properties.setProperty(MAIL_PORT, txtPort.getText());
    properties.setProperty(SOCKET_PORT, txtSocketPort.getText());
    properties.setProperty(MAIL_FALLBACK, String.valueOf(butFallBack.getSelection()));
    properties.setProperty(SOCKET_CLASS, txtSocketClass.getText());
    properties.setProperty(SEND_HOUR, String.valueOf(spinSendHour.getSelection()));
    properties.setProperty(MAIL_SSL_SERVER, String.valueOf(butSSLServer.getSelection()));
    properties.setProperty(MAIL_USER, txtUser.getText());
    properties.setProperty(MAIL_PASSWORD, txtPassword.getText());
    properties.setProperty(MAIL_SEND_TO_EMAIL, txtSendToEmail.getText());
    properties.setProperty(MAIL_ALIAS, txtMailAlias.getText());
    properties.setProperty(RESEND_TIME, txtResendTime.getText());
  }
  
  private void load(Text widget, String name){
    try {
      String value =  properties.getProperty(name);
      if(value == null) return;
      widget.setText(value);
    } catch (NullPointerException e) {
    } catch (Exception exp) {
      ClientLog.getInstance().setException(getShell(), exp);
    }
  }
  
  private void load(Button widget, String name){
    try {
      String value =  properties.getProperty(name);
      if(value == null) return;
      widget.setSelection(Boolean.valueOf(value));
    } catch (NullPointerException e) {
    } catch (Exception exp) {
      ClientLog.getInstance().setException(getShell(), exp);
    }
  }
  
  private void load(Spinner widget, String name){
    try {
      String value =  properties.getProperty(name);
      if(value == null) return;
      widget.setSelection(Integer.parseInt(value));
    } catch (NullPointerException e) {
    } catch (Exception exp) {
      ClientLog.getInstance().setException(getShell(), exp);
    }
  }

}
