/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.config;

import static org.vietspider.common.Application.PROXY_ENABLE;
import static org.vietspider.common.Application.PROXY_HOST;
import static org.vietspider.common.Application.PROXY_PASSWORD;
import static org.vietspider.common.Application.PROXY_PORT;
import static org.vietspider.common.Application.PROXY_USER;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
public class CrawlProxyConfig extends Composite {

  private Text txtProxyHost;
  private Text txtProxyPort;
  private Text txtProxyUser;
  private Text txtProxyPass;
  
  private Button butEnable;
  
  private Properties properties;

  public CrawlProxyConfig(Composite parent, ApplicationFactory factory) {
    super(parent, SWT.NONE);

    setLayout(new GridLayout(2, false));
    factory.setComposite(this);

    GridData gridData;

//  **************************************************************************************************

    Composite proxyHostComposite = new Composite(this, SWT.NONE);
    proxyHostComposite.setLayout(new GridLayout(2, false));
    factory.setComposite(proxyHostComposite);
    
    butEnable = factory.createButton("butProxyEnable", SWT.CHECK, null);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    butEnable.setLayoutData(gridData);
    butEnable.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        txtProxyHost.setEnabled(butEnable.getSelection());
        txtProxyPort.setEnabled(butEnable.getSelection());
        txtProxyUser.setEnabled(butEnable.getSelection());
        txtProxyPass.setEnabled(butEnable.getSelection());
      }
      
    });

    factory.createLabel("lblProxyHost");    
    txtProxyHost = factory.createText();
    gridData = new GridData();
    gridData.widthHint = 100;
    txtProxyHost.setLayoutData(gridData);
    

    Composite proxyPortComposite = new Composite(this, SWT.NONE);
    proxyPortComposite.setLayout(new GridLayout(2, false));
    factory.setComposite(proxyPortComposite);

    factory.createLabel("lblProxyPort");     
    txtProxyPort = factory.createText();
    gridData = new GridData();
    gridData.widthHint = 50;
    txtProxyPort.setLayoutData(gridData);

//    Composite proxyUserComposite = new Composite(this, SWT.NONE);
//    proxyUserComposite.setLayout(new GridLayout(2, false));
//    gridData = new GridData(GridData.FILL_HORIZONTAL);
//    gridData.horizontalSpan = 2;
//    proxyUserComposite.setLayoutData(gridData);
//    factory.setComposite(proxyUserComposite);

    Composite proxyUsernameComposite = new Composite(this, SWT.NONE);
    proxyUsernameComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    proxyUsernameComposite.setLayoutData(gridData);
    factory.setComposite(proxyUsernameComposite);

    factory.createLabel("lblProxyUser");
    txtProxyUser = factory.createText();
    gridData = new GridData();
    gridData.widthHint = 120;
    txtProxyUser.setLayoutData(gridData);

    Composite proxyPasswordComposite = new Composite(this, SWT.NONE);
    proxyPasswordComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    proxyPasswordComposite.setLayoutData(gridData);
    factory.setComposite(proxyPasswordComposite);

    factory.createLabel("lblProxyPass");
    txtProxyPass = factory.createText();   
    txtProxyPass.setEchoChar('x');
    gridData = new GridData();
    gridData.widthHint = 120;
    txtProxyPass.setLayoutData(gridData);
  }
  
  void setValue2UI(Properties properties) {
    this.properties = properties;
    
    load(txtProxyHost, PROXY_HOST);
    load(txtProxyPort, PROXY_PORT);
    load(txtProxyUser, PROXY_USER);
    load(txtProxyPass, PROXY_PASSWORD);
    
    String value =  properties.getProperty(PROXY_ENABLE);
    butEnable.setSelection("true".equals(value));
    
    txtProxyHost.setEnabled(butEnable.getSelection());
    txtProxyPort.setEnabled(butEnable.getSelection());
    txtProxyUser.setEnabled(butEnable.getSelection());
    txtProxyPass.setEnabled(butEnable.getSelection());
  }
  
  void setData2Properties() throws Exception {
    properties.setProperty(PROXY_ENABLE, String.valueOf(butEnable.getSelection()));
    properties.setProperty(PROXY_HOST, txtProxyHost.getText());
    properties.setProperty(PROXY_USER, txtProxyUser.getText());
    properties.setProperty(PROXY_PASSWORD, txtProxyPass.getText());
    try {
      properties.setProperty(PROXY_PORT, String.valueOf(Integer.parseInt(txtProxyPort.getText().trim())));
    } catch (Exception e) {
      properties.setProperty(PROXY_PORT, "80");
    }

  }
  
  private void load(Text widget, String name){
    try{
      String value =  properties.getProperty(name);
      if(value == null) return;
      widget.setText(value.trim());
    } catch (NullPointerException e) {
    } catch (Exception exp) {
      ClientLog.getInstance().setException(getShell(), exp);
    }
  }

}
