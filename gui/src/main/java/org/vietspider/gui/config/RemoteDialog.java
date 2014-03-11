/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.config;

import java.net.URL;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.vietspider.ClientProperties;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.gui.workspace.XPWindowTheme;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.tabfolder.CTabFolder;
import org.vietspider.ui.widget.tabfolder.CTabItem;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 17, 2007  
 */
public abstract class RemoteDialog extends Composite {

  protected Spinner txtPort;
  protected Combo cboHost;
  protected Text txtUsername, txtPassword;

  private Button butProxyEnable;
  private Text txtProxyHost, txtProxyUser, txtProxyPassword;
  private Spinner txtProxyPort;
  protected Control butConnect;
  protected Control butLogout;

  protected Label lblStatus;
  protected Shell mainWindow;

  public RemoteDialog(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout(1, true));
  }

  private void connect() {
    try {
      saveProxy();
    } catch (Exception e) {
      if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
        lblStatus.setText(e.getMessage());
      } else {
        lblStatus.setText(e.toString());
      }
      return ;
    }
    connect(cboHost.getText(), txtPort.getText(), txtUsername.getText(), txtPassword.getText());
  }


  public abstract void close() ;

  public abstract void connect(String host, String port, String username, String password);

  private void saveProxy() throws Exception {
    ClientProperties client  = ClientProperties.getInstance();
    client.putValue("proxy.enable", String.valueOf(butProxyEnable.getSelection()));
    client.putValue("proxy.host", txtProxyHost.getText());
    client.putValue("proxy.port", txtProxyPort.getText());
    client.putValue("proxy.user", txtProxyUser.getText());
    client.putValue("proxy.password", txtProxyPassword.getText());
    client.store();
  }

  protected void saveHistory() {
    String [] items = cboHost.getItems();
    StringBuilder builder = new StringBuilder();
    for(String ele : items) {
      if(builder.indexOf(ele) > -1) continue;
      builder.append(ele).append('\n');
    }
    String url = ClientConnector2.currentInstance().getRemoteURL();
    if(builder.indexOf(url) < 0) builder.append(url);
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    prefs.put("address", builder.toString().trim());
    prefs.put("url", SWProtocol.HTTP + cboHost.getText() + ":" + txtPort.getText() +"/");
  }

  protected void loadHistory() {
    cboHost.removeAll();
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    String prop = prefs.get("address", "");

    String address  = prefs.get("url", "");
    try {
      URL url = new URL(address);
      cboHost.setText(url.getHost());
      txtPort.setSelection(url.getPort());
    } catch (Exception e) {
      lblStatus.setText(e.toString());
    }

    if(prop == null || (prop = prop.trim()).isEmpty()) return ;
    String [] elements = prop.split("\n");
    for(String element : elements) {
      if(!element.trim().isEmpty()) cboHost.add(element);
    }

    ClientProperties client  = ClientProperties.getInstance();
    txtProxyHost.setText(client.getValue("proxy.host", ""));
    try {
      txtProxyPort.setSelection(Integer.parseInt(client.getValue("proxy.port", "8080")));
    } catch (Exception e) {
      txtProxyPort.setSelection(80);
    }
    txtProxyUser.setText(client.getValue("proxy.user", ""));
    txtProxyPassword.setText(client.getValue("proxy.password", ""));
    butProxyEnable.setSelection("true".equals(client.getValue("proxy.enable", "false")));
    setEnableProxy();
  }

  protected void clearItems() {
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    prefs.put("address", "");
  }

  protected void removeItem(String item) {
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    String prop = prefs.get("address", "");

    if(prop == null || (prop = prop.trim()).isEmpty()) return ;
    String [] elements = prop.split("\n");
    StringBuilder builder = new StringBuilder();
    for(String element : elements) {
      if(element.trim().equals(item)) continue;
      if(builder.length() > 0) builder.append('\n');
      builder.append(element);
    }
    prefs.put("address", builder.toString());
  }

  protected void createRemoteAddress(ApplicationFactory factory) {
    Composite tabFolder;
    if(XPWindowTheme.isPlatform()) {
      tabFolder = new CTabFolder(this, SWT.BORDER);
      CTabFolder tab = (CTabFolder)tabFolder;
      XPWindowTheme.setTabBrowserTheme(tab);
    } else {
      tabFolder = new TabFolder(this, SWT.TOP);
    }
    tabFolder.setFont(UIDATA.FONT_10B);
    GridData gridData = new GridData(GridData.FILL_BOTH);     
    tabFolder.setLayoutData(gridData);

    Composite serverComposite = new Composite(tabFolder, SWT.NONE);
    serverComposite.setLayout(new GridLayout(1, true));
    factory.setComposite(serverComposite);

    if(XPWindowTheme.isPlatform()) {
      CTabFolder tab = (CTabFolder)tabFolder;
      CTabItem tabItem = new CTabItem(tab, SWT.NONE);
      tabItem.setText(" Server ");
      tabItem.setControl(serverComposite);
      tab.setSelection(tabItem);
    } else {
      TabFolder tab = (TabFolder)tabFolder;
      TabItem tabItem = new TabItem(tab, SWT.NONE);
      tab.setSelection(tabItem);
      tabItem.setText(" Server ");
      tabItem.setControl(serverComposite);
    }

    Composite hostComposite = new Composite(serverComposite, SWT.NONE);
    hostComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    hostComposite.setLayoutData(gridData);

    Composite composite = new Composite(hostComposite, SWT.NONE);
    composite.setLayout(new GridLayout(1, true));
    factory.setComposite(composite);

    factory.createLabel("lblRemoteHost");   
    cboHost = factory.createCombo(SWT.BORDER);
    gridData = new GridData();
    gridData.widthHint = 170;
    cboHost.setLayoutData(gridData);
    cboHost.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent event) {
        String value  = cboHost.getText();
        int idx = SWProtocol.lastIndexOf(value);
        if(idx > -1) value = value.substring(idx);
        idx = value.indexOf('/');
        if(idx > -1) value = value.substring(0, idx);
        String [] elements = value.split(":");
        if(elements.length < 2)  return;
        cboHost.setText(elements[0]);
        txtPort.setSelection(Integer.parseInt(elements[1]));
      }
    });
    cboHost.setVisibleItemCount(10);
    cboHost.addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent event) {
        if(event.keyCode == SWT.CR) connect();
      }
    });

    Menu menu = new Menu(factory.getComposite().getShell(), SWT.POP_UP);  
    factory.createMenuItem(menu, "menuClearItems", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        cboHost.removeAll();
        clearItems();
      }
    });
    factory.createMenuItem(menu, "menuClearSelectedItem", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int selected = cboHost.getSelectionIndex();
        String item = cboHost.getItem(selected);
        cboHost.remove(selected);
        removeItem(item);
      }
    });
    cboHost.setMenu(menu);

    composite = new Composite(hostComposite, SWT.NONE);
    composite.setLayout(new GridLayout(1, true));
    factory.setComposite(composite);

    factory.createLabel("lblRemotePort");  
    txtPort = factory.createSpinner(SWT.BORDER);
    txtPort.setMaximum(100000);
    gridData = new GridData();
    gridData.widthHint = 50;
    txtPort.setLayoutData(gridData);
    txtPort.addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent event) {
        if(event.keyCode == SWT.CR) connect();
      }
    });

    Composite userComposite = new Composite(serverComposite, SWT.NONE);
    userComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    userComposite.setLayoutData(gridData);

    composite = new Composite(userComposite, SWT.NONE);
    composite.setLayout(new GridLayout(1, true));
    factory.setComposite(composite);

    factory.createLabel("lblUsername");   
    txtUsername = factory.createText();
    gridData = new GridData();
    gridData.widthHint = 120;
    txtUsername.setLayoutData(gridData);
    txtUsername.addFocusListener(new FocusAdapter(){
      @SuppressWarnings("unused")
      public void focusGained(FocusEvent event) {
        txtPassword.selectAll();
      }
    });
    txtUsername.addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent event) {
        if(event.keyCode == SWT.CR) connect();
      }
    });

    composite = new Composite(userComposite, SWT.NONE);
    composite.setLayout(new GridLayout(1, true));
    factory.setComposite(composite);

    factory.createLabel("lblPassword");   
    txtPassword = factory.createText(SWT.BORDER | SWT.PASSWORD);
    gridData = new GridData(SWT.BORDER | SWT.PASSWORD);
    gridData.widthHint = 120;
    txtPassword.setLayoutData(gridData);
    txtPassword.addFocusListener(new FocusAdapter(){
      @SuppressWarnings("unused")
      public void focusGained(FocusEvent event) {
        txtPassword.selectAll();
      }
    });
    txtPassword.addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent event) {
        if(event.keyCode == SWT.CR) connect();
      }
    });

    // *******************proxy components *******************************

    Composite proxyComposite = new Composite(tabFolder, SWT.NONE);
    proxyComposite.setLayout(new GridLayout(1, true));
    factory.setComposite(proxyComposite);
    
    butProxyEnable = factory.createButton("butProxyEnable", SWT.CHECK, null);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    butProxyEnable.setLayoutData(gridData);
    butProxyEnable.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("all")
      public void widgetSelected(SelectionEvent e) {
        setEnableProxy();
      }
    });

    if(XPWindowTheme.isPlatform()) {
      CTabFolder tab = (CTabFolder)tabFolder;
      CTabItem tabItem = new CTabItem(tab, SWT.NONE);
      tabItem.setText("  Proxy  ");
      tabItem.setControl(proxyComposite);
    } else {
      TabFolder tab = (TabFolder)tabFolder;
      TabItem tabItem = new TabItem(tab, SWT.NONE);
      factory.setComposite(tabFolder);
      tabItem.setText("  Proxy  ");
      tabItem.setControl(proxyComposite);
    }

    hostComposite = new Composite(proxyComposite, SWT.NONE);
    hostComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    hostComposite.setLayoutData(gridData);
    
    composite = new Composite(hostComposite, SWT.NONE);
    composite.setLayout(new GridLayout(1, true));
    factory.setComposite(composite);
    
    factory.createLabel("lblProxyHost");    
    txtProxyHost = factory.createText();
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.widthHint = 180;
    txtProxyHost.setLayoutData(gridData);

    composite = new Composite(hostComposite, SWT.NONE);
    composite.setLayout(new GridLayout(1, true));
    factory.setComposite(composite);

    factory.createLabel("lblProxyPort");     
    txtProxyPort = factory.createSpinner(SWT.BORDER);
    txtProxyPort.setMaximum(1000000);
    gridData = new GridData();
    gridData.widthHint = 60;
    txtProxyPort.setLayoutData(gridData);

    userComposite = new Composite(proxyComposite, SWT.NONE);
    userComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    userComposite.setLayoutData(gridData);

    composite = new Composite(userComposite, SWT.NONE);
    composite.setLayout(new GridLayout(1, true));
    factory.setComposite(composite);

    factory.createLabel("lblProxyUsername");    
    txtProxyUser = factory.createText();
    gridData = new GridData();
    gridData.widthHint = 120;
    txtProxyUser.setLayoutData(gridData);
    txtProxyUser.addFocusListener(new FocusAdapter(){
      @SuppressWarnings("unused")
      public void focusGained(FocusEvent event) {
        txtPassword.selectAll();
      }
    });

    composite = new Composite(userComposite, SWT.NONE);
    composite.setLayout(new GridLayout(1, true));
    factory.setComposite(composite);

    factory.createLabel("lblProxyPassword");    
    txtProxyPassword = factory.createText(SWT.BORDER | SWT.PASSWORD);
    gridData = new GridData();
    gridData.widthHint = 120;
    txtProxyPassword.setLayoutData(gridData);
    txtProxyPassword.addFocusListener(new FocusAdapter(){
      @SuppressWarnings("unused")
      public void focusGained(FocusEvent event) {
        txtPassword.selectAll();
      }
    });
  }

  protected void createButton(ApplicationFactory factory, boolean hasLogout) {
    Composite buttonComposite = new Composite(this, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);    
    buttonComposite.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    buttonComposite.setLayout(rowLayout);
    rowLayout.justify = true;

    factory.setComposite(buttonComposite);

    if(hasLogout) {
//      if(XPWindowTheme.isPlatform()) {
//        butLogout = factory.createCButton("butLogout", new SelectionAdapter(){
//          @SuppressWarnings("unused")
//          public void widgetSelected(SelectionEvent evt) {
//            logout();
//          }   
//        }); 
//      } else {
        butLogout = factory.createButton("butLogout", new SelectionAdapter(){
          @SuppressWarnings("unused")
          public void widgetSelected(SelectionEvent evt) {
            logout();
          }   
        }); 
//      }
      if(butLogout != null) butLogout.setEnabled(false);
    }


//    if(XPWindowTheme.isPlatform()) {
//      butConnect = factory.createCButton("butConnect", new SelectionAdapter(){
//        @SuppressWarnings("unused")
//        public void widgetSelected(SelectionEvent evt) {
//          connect();
//          if(butLogout != null) butLogout.setEnabled(true);
//        }   
//      }); 
//    } else {
      butConnect = factory.createButton("butConnect", new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          connect();
          if(butLogout != null) butLogout.setEnabled(true);
        }   
      }); 
//    }

//    if(XPWindowTheme.isPlatform()) {
//      factory.createCButton("butClose", new SelectionAdapter(){
//        @SuppressWarnings("unused")
//        public void widgetSelected(SelectionEvent evt) {
//          ClientConnector2.currentInstance().loadDefault();
//          close();
//        }   
//      });
//    } else {
      factory.createButton("butClose", new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          ClientConnector2.currentInstance().loadDefault();
          close();
        }   
      });
//    }

    factory.setComposite(this);
    lblStatus = factory.createLabel(SWT.BORDER | SWT.WRAP);
    gridData = new GridData(GridData.FILL_BOTH);    
    lblStatus.setLayoutData(gridData);
  }
  
  protected void setEnableProxy() {
    txtProxyHost.setEnabled(butProxyEnable.getSelection());
    txtProxyPort.setEnabled(butProxyEnable.getSelection());
    txtProxyUser.setEnabled(butProxyEnable.getSelection());
    txtProxyPassword.setEnabled(butProxyEnable.getSelection());
  }

  public void logout() {
    butLogout.setEnabled(false);
    txtUsername.setText("");
    txtPassword.setText("");
    //    cboHost.setText("");
    //    txtPort.setText("");
    if(mainWindow != null && !mainWindow.isVisible()) return;
    if(mainWindow != null) mainWindow.setVisible(false);
  }

  public Shell getMainWindow() { return mainWindow; }

  public void setMainWindow(Shell mainWindow) { this.mainWindow = mainWindow; }

}
