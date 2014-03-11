/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.io.File;
import java.util.Properties;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.util.Worker;
import org.vietspider.model.Source;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 21, 2007  
 */
public class SetPropertiesDialog {
  
  private Shell shell;
  private Combo cboProperties;
  private Text txtProperties;
  
  private String group;
  private String category;
  private String [] names; 
  
  public SetPropertiesDialog(Shell parent, String group, String category, String...names) {
    this.group = group;
    this.category = category;
    this.names = names;
    
    shell = new Shell(parent, SWT.CLOSE | SWT.APPLICATION_MODAL);
    shell.setLayout(new GridLayout(3, false));
    
    ApplicationFactory factory = new ApplicationFactory(shell, "Creator", getClass().getName());
    
    cboProperties = factory.createCombo(SWT.NONE | SWT.READ_ONLY , new String[]{});
    cboProperties.setVisibleItemCount(15);
    cboProperties.setItems(loadNames());
    cboProperties.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        int idx = cboProperties.getSelectionIndex();
        Preferences prefs_ = Preferences.userNodeForPackage(Creator.class);
        prefs_.put("set.properties.selected.property", String.valueOf(idx));
      }
    });
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    cboProperties.setLayoutData(gridData);
    
    try {
      Preferences prefs_ = Preferences.userNodeForPackage(Creator.class);
      String value = prefs_.get("set.properties.selected.property", "");
      cboProperties.select(Integer.parseInt(value));
    } catch (Exception e) {
      if(cboProperties.getItemCount() > 0) cboProperties.select(0);
    }
    
    factory.createButton("butOk", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) { 
        shell.setVisible(false);
        setProperty();
      }      
    });
    
    factory.createButton("butAdd", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) { 
        shell.setVisible(false);
        addProperty();
      }      
    });
    
    txtProperties = factory.createText(SWT.MULTI | SWT.BORDER);
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 3;
    txtProperties.setLayoutData(gridData);
    
    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 200)/ 2;
    shell.setImage(parent.getImage());
    shell.setLocation(x, y);
    shell.setSize(300, 250);
//    XPWindowTheme.setWin32Theme(shell);
    shell.open();
  }
  
  public void setProperty() {
    Worker excutor = new Worker() {
      
      private String message = "";
      
      private String nameProperty;
      private String valueProperty;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        int idx = cboProperties.getSelectionIndex();
        nameProperty = cboProperties.getItem(idx);
        valueProperty = txtProperties.getText();
      }

      public void execute() {
        try {
          SourcesClientHandler client = new SourcesClientHandler(group);
          for(String name : names) {
            Source source = client.loadSource(category, name);
            if(source == null) continue;
            Properties properties = source.getProperties();
            properties.setProperty(nameProperty, valueProperty);
            source.setProperties(properties);
            client.saveSource(source);
          }
        } catch (Exception e) {
          if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
            message = e.getMessage();
          } else {
            message = e.toString();
          }
        }
      }

      public void after() {
        if(message != null && !message.trim().isEmpty()) {
          MessageBox msg = new MessageBox (shell, SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
        }
        shell.dispose();
      }
    };
    WaitLoading loading = new WaitLoading(shell, excutor);
    loading.open();
  }
  
  public void addProperty() {
    Worker excutor = new Worker() {
      
      private String message = "";
      private String nameProperty;
      private String valueProperty;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        int idx = cboProperties.getSelectionIndex();
        nameProperty = cboProperties.getItem(idx);
        valueProperty = txtProperties.getText();
      }

      public void execute() {
        try {
          SourcesClientHandler client = new SourcesClientHandler(group);
          String separator = getSeparator(nameProperty);
          for(String name : names) {
            Source source = client.loadSource(category, name);
            if(source == null) continue;
            Properties properties = source.getProperties();
            String oldValue = properties.getProperty(nameProperty);
            if(oldValue != null) {
              properties.setProperty(nameProperty, valueProperty + separator + oldValue);
            } else {
              properties.setProperty(nameProperty, valueProperty);  
            }
            
            source.setProperties(properties);
            client.saveSource(source);
          }
        } catch (Exception e) {
          if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
            message = e.getMessage();
          } else {
            message = e.toString();
          }
        }
      }

      public void after() {
        if(message != null && !message.trim().isEmpty()) {
          MessageBox msg = new MessageBox (shell, SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
        }
        shell.dispose();
      }
    };
    WaitLoading loading = new WaitLoading(shell, excutor);
    loading.open();
  }
  
  private String getSeparator(String propertyName) {
    if(propertyName.equals("ContentFilter")) return ",";
    return "\n";
  }
  
  private String[] loadNames() {
    File file = UtilFile.getFile("sources/type", "property_name");
    try {
      String value = new String(RWData.getInstance().load(file), Application.CHARSET);
      return value.split("\n");
    }catch (Exception e) {
      ClientLog.getInstance().setException(shell, e);
      return new String[]{};
    }
  }
  
}
