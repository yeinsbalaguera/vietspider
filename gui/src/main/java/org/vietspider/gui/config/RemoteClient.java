/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.gui.config;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.ClientProperties;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Linux
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 16, 2007  
 */
public class RemoteClient extends RemoteDialog {
  
  protected String [] localeNames = {"English", "Vietnamese"};
  protected String [] localeValues = {"en", "vn"};
  
  protected Object butAutoConnect;
  protected Combo cboLanguage;

  private boolean connected = false;
  
  private boolean isFirst = true;
  
  public RemoteClient(Composite parent) {
    super(parent);
    
    ApplicationFactory factory = new ApplicationFactory(this, "Connector", getClass().getName());
    if(Shell.class.isInstance(parent)) {
      ((Shell)parent).setText(factory.getLabel("title"));
    }
    
    createRemoteAddress(factory);
    createRemoteConfig(factory);
    createButton(factory, true);
    
    loadHistory();
    
    ClientProperties clientProperties = ClientProperties.getInstance();
    
    String address  = clientProperties.getValue("remote.server");
    if(address == null || address.trim().isEmpty()) {
      try {
        InetAddress inetAddress = InetAddress.getLocalHost();
        address = SWProtocol.HTTP+inetAddress.getHostName()+":9245/";
      } catch (Exception e) {
        address = "http://127.0.0.1:9245/";
      }
    }
    
    String username  = "vietspider";
    String password  = "vietspider";
    try {
      Preferences prefs = Preferences.userNodeForPackage(RemoteClient.class);
      username  = prefs.get("remote.username", "");
      password  = prefs.get("remote.password", "");
    } catch (Exception e) {
      e.printStackTrace();
      username  = "vietspider";
      password  = "vietspider";
    }
    try {
      if(address != null && address.length() > 0) {
        URL url = new URL(address);
        cboHost.setText(url.getHost());
        txtPort.setSelection(url.getPort());
      }
      if(cboHost.getText().trim().isEmpty()) {
        cboHost.setText("localhost");
      }
      
      if(txtPort.getText().trim().isEmpty()) {
        txtPort.setSelection(9245);
      }
      if(username == null || username.trim().isEmpty()) username = "vietspider";
      txtUsername.setText(username);
      if(password == null || password.trim().isEmpty()) password = "vietspider";
      if(isAutoConnected()) txtPassword.setText(password);
    } catch (Exception e) {
      lblStatus.setText(e.toString());
    }
    
    if(cboHost.getText().trim().length() > 0) butConnect.setFocus(); else cboHost.setFocus();
    
    if(!isAutoConnected() || cboHost.getText().trim().length() < 1)  return;
    
    Runnable timer = new Runnable () {
      public void run () {
//        ClientProperties clientProperties_ = new ClientProperties();
        String host = cboHost.getText().trim();
        String port = txtPort.getText().trim();
        String username_ = txtUsername.getText();
        String password_ = txtPassword.getText();
        connect(host, port, username_, password_);    
      }
    };
    getDisplay().timerExec (1000, timer);
  }
  
  private boolean isAutoConnected() {
    if(butAutoConnect instanceof Button) {
     return ((Button)butAutoConnect).getSelection();
    } 
    
//    else if(butAutoConnect instanceof CCheckBox) {
//      return ((CCheckBox)butAutoConnect).getSelection(); 
//    }
    return false;
  }
  
  private void setAutoConnected(boolean value) {
    if(butAutoConnect instanceof Button) {
     ((Button)butAutoConnect).setSelection(value);
    } 
    
//    else if(butAutoConnect instanceof CCheckBox) {
//      ((CCheckBox)butAutoConnect).setSelection(value); 
//    }
  }
  
  public boolean isConnected() { return connected; }

  public void setConnected(boolean connected) {
    isFirst = false;
    this.connected = connected; 
  }
  
  public void connect(final String host, final String port, final String username, final String password) {
    Worker excutor = new Worker() {
      
      private String message = ""; 
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          ClientRM resources = new ClientRM("Connector");
          
          ClientConnector2 connecter = ClientConnector2.currentInstance(host, port);
          int ping  = connecter.ping(username, password);
          connected = ping > -1;
          if(ping < -2) {
            message =  resources.getLabel("cannotConnect");
            return;
          } else if (ping == -2) {
            message = resources.getLabel("incorrectUser");
            return;
          } else if (ping == -1) {
            message =  resources.getLabel("incorrectPassword");
            return;
          } else  if(ping == 0) {
            message =  resources.getLabel("incorrectMode");
            return;
          }
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
          if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
            message = e.getMessage();
          } else {
            message = e.toString();
          }
        }
      }

      public void after() {
        if(message.length() > 0) {
          lblStatus.setText(message);
          return; 
        }
        
        lblStatus.setText(message);
        saveHistory();
        ClientProperties clientProperties = ClientProperties.getInstance();
        try {
          String currentURL = SWProtocol.HTTP + host + ":" + port +"/";
          String localAddress = null;
          try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            localAddress = SWProtocol.HTTP + inetAddress.getHostName()+":9245/";
          } catch (Exception e) {
          localAddress = "http://127.0.0.1:9245/";
          }
          
          if(currentURL.startsWith(localAddress)) {
            clientProperties.putValue("remote.server", "");
          } else {
            clientProperties.putValue("remote.server", currentURL);
          }
          
          Preferences prefs = Preferences.userNodeForPackage(RemoteClient.class);
          prefs.put("remote.username", username);
          prefs.put("remote.password", password);
          prefs.sync();
          
          int idxLanguage = cboLanguage.getSelectionIndex();
          clientProperties.putValue("locale", localeValues[idxLanguage]);
          
          clientProperties.store();
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }
    };
    WaitLoading loading = new WaitLoading(getShell(), excutor);
    loading.open();
  }
  
  public void show() {
    loadHistory();
    setConnected(false);
    if(butLogout != null) butLogout.setEnabled(true);
    getShell().setVisible(true);
  }
  
  public void close() {
    if(isFirst || (!mainWindow.isDisposed() && !mainWindow.isVisible())) {
      System.exit(0); 
    } else {
      getShell().setVisible(false);
    }
  }
  
  public void savePref() {
    ClientProperties properties = ClientProperties.getInstance();
    String value = String.valueOf(isAutoConnected());
    properties.putValue("auto.connect", value);
    properties.store();
  }
  
  protected void loadPref() {
    ClientProperties properties = ClientProperties.getInstance();
    String value = properties.getValue("auto.connect");
    setAutoConnected(value != null && "true".equalsIgnoreCase(value));
    String locale = properties.getValue("locale");
    if(locale != null)  {
      locale = locale.trim();
      for(int i = 0; i < localeValues.length; i++) {
        if(localeValues[i].equalsIgnoreCase(locale)) {
          cboLanguage.select(i);
          break;
        }
      }
    }
  }
  
  protected void createRemoteConfig(ApplicationFactory factory) {
    Composite languageComposite = new Composite(this, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);    
    languageComposite.setLayoutData(gridData);
    languageComposite.setLayout(new GridLayout(2, false));
    
    factory.setComposite(languageComposite);
    
    factory.createLabel("lblLanguage");    
    cboLanguage = factory.createCombo(SWT.NONE | SWT.READ_ONLY , new String[]{});
    try {
      File file = new File(UtilFile.getFolder("client"), "luaguage.config");
      if(file.exists()) {
        byte [] bytes = RWData.getInstance().load(file);
        String value = new String(bytes, Application.CHARSET);
        String [] elements = value.trim().split("\n");
        localeNames = new String[elements.length];
        localeValues = new String[elements.length];
        for(int i = 0; i < elements.length; i++) {
          String [] values = elements[i].split("\\.");
          localeValues[i] = values[0];
          localeNames[i] = values[1];
        }
      } else {
        cboLanguage.setItems(localeNames);
      }
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
      cboLanguage.setItems(localeNames);
    }
    
    factory.setComposite(this);
    
//    if(XPWindowTheme.isPlatform()) {
//      butAutoConnect = factory.createCCheckBox(this, "butAutoConnect", new SelectionAdapter(){
//        @SuppressWarnings("unused")
//        public void widgetSelected(SelectionEvent evt) {
//          savePref();
//        }   
//      });
//      gridData = new GridData(GridData.FILL_BOTH);    
//      ((CCheckBox)butAutoConnect).setLayoutData(gridData);
//    } else {
      butAutoConnect = factory.createButton("butAutoConnect", SWT.CHECK,  new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          savePref();
        }   
      });
      gridData = new GridData(GridData.FILL_BOTH);    
      ((Button)butAutoConnect).setLayoutData(gridData);
//    }
   
    loadPref();
  }
  
 
}
