/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.config;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.vietspider.bean.DatabaseConfig;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.waiter.WaitLoading;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Mar 24, 2007
 */
public class SystemConfig extends Composite {
  
  private ApplicationConfig application;
  private CrawlerConfig crawler;
  private CrawlProxyConfig proxy;
  private PluginConfig plugin;
  
  private Properties properties;

  public SystemConfig(Composite parent, ApplicationFactory factory) {
    super(parent, SWT.NONE);

    setLayout(new GridLayout(3, false));
    factory.setComposite(this);

    GridData gridData;
    Group group ;
    
    Composite leftComposite = new Composite(this, SWT.NONE);
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 10;
    gridLayout.marginWidth = 0;
    leftComposite.setLayout(gridLayout);
    
    int style = GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL;
    
    factory.setComposite(leftComposite);
    gridData = new GridData(GridData.BEGINNING | style);
    group = factory.createGroup("grpSystem", gridData, new GridLayout(1, false));
    application = new ApplicationConfig(group, factory);
    
    factory.setComposite(leftComposite);
    gridData = new GridData(GridData.BEGINNING | style);
    group = factory.createGroup("grpProxy", gridData, new GridLayout(1, false));
    proxy = new CrawlProxyConfig(group, factory);
    
    factory.setComposite(this);
    gridData = new GridData(GridData.BEGINNING | style);
    group = factory.createGroup("grpCrawlConfig", gridData, new GridLayout(1, false));
    crawler = new CrawlerConfig(group, factory);
    
    factory.setComposite(this);
    gridData = new GridData(GridData.BEGINNING | style);
    group = factory.createGroup("grpPlugin", gridData, new GridLayout(1, false));
    plugin = new PluginConfig(group, factory);
    
    Composite bottom = new Composite(this, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    bottom.setLayoutData(gridData);
    
    RowLayout rowLayout = new RowLayout();
    rowLayout.justify = true;
    rowLayout.marginTop = 30;
    bottom.setLayout(rowLayout);

    factory.setComposite(bottom);
    factory.createButton("butSaveConfig", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        try{
          setData2Properties();
          saveData2Server();
          plugin.saveSetup();
        }catch (Exception e) {
          ClientLog.getInstance().setException(getShell(), e);
        }
      }   
    }, factory.loadImage("save.png")); 

    factory.createButton("butResetConfig", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        loadSystemConfig();
      }   
    }, factory.loadImage("butReset.png"));

    loadSystemConfig();
  }
  
  private void loadSystemConfig() {
    Worker excutor = new Worker() {

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          properties = DataClientHandler.getInstance().getSystemProperties();
        } catch (Exception e) {
          ClientLog.getInstance().setException(getShell(), e);
          properties = new Properties();
        }
      }

      public void after() {
        setValue2UI();
        plugin.resetSetup();
      }
    };
    WaitLoading loading = new WaitLoading(this, excutor);
    loading.open();
  }

  private void setValue2UI() {
    application.setValue2UI(properties);
    proxy.setValue2UI(properties);
    crawler.setValue2UI(properties);
  }
  
  private void setData2Properties() throws Exception {
    application.setData2Properties();
    proxy.setData2Properties();
    crawler.setData2Properties();
  }

  private void saveData2Server() {
    Worker excutor = new Worker() {

      private DatabaseConfig databaseConfig;
      
      private StringBuilder error = new StringBuilder();

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
//        DatabaseSetting databaseSetting = application.getDatabaseSetting();
//        if(databaseSetting != null) databaseConfig = databaseSetting.getConfig();
      }

      public void execute() {
        DataClientHandler clientHandler = DataClientHandler.getInstance();
        try {
          clientHandler.saveSystemProperties(properties);
        } catch (Exception e) {
          error.append(e.toString());
        }
        if(databaseConfig != null) {
          try {
            clientHandler.saveDBInfo(databaseConfig, "database.connection.setting");
          } catch (Exception e) {
            error.append('\n').append(e.toString());
          }
        }
      }

      public void after() {
        if(error.length() > 0) {
          ClientLog.getInstance().setMessage(getShell(), new Exception(error.toString()));
        }
      }
    };
    WaitLoading loading = new WaitLoading(this, excutor);
    loading.open();
  }

}

