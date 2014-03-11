/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.config;

import static org.vietspider.common.Application.AUTO_START_CRAWLER;
import static org.vietspider.common.Application.DETECT_WEBSITE;
import static org.vietspider.common.Application.DOWNLOAD_LIMIT_DATE;
import static org.vietspider.common.Application.HTTP_CLIENT_TIMEOUT;
import static org.vietspider.common.Application.LOG_WEBSITE_ERROR;
import static org.vietspider.common.Application.QUEUE_SLEEP;
import static org.vietspider.common.Application.SAVE_IMAGE_TO_FILE;
import static org.vietspider.common.Application.START_INDEX_DATA_SERVICE;
import static org.vietspider.common.Application.START_MINING_INDEX_SERVICE;
import static org.vietspider.common.Application.TOTAL_EXECUTOR;
import static org.vietspider.common.Application.TOTAL_WORKER_OF_EXECUTOR;
import static org.vietspider.common.Application.WEIGHT_PRIORITY_EXECUTOR;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
public class CrawlerConfig extends Composite {

  private Button butAutoStart;
  private Button butSaveImage2Folder;
  private Button butStartMiningIndex;
  private Button butStartIndexData;
  private Button butDetectWebsite;
  private Button butLogWebsite;

  private Spinner /*spinPoolTime,*/ spinLimitDate;
  private Spinner spinQueueSleep;

  private Spinner spinTotalExecutor;
  private Spinner spinSizeOfExecutor;
  private Spinner spinWeightPriorityExecutor;
  private Spinner spinHttpClientTimeout;
  
  private Properties properties;
  
  private CrawlProxyConfig proxyConfig;

  public CrawlerConfig(Composite parent, ApplicationFactory factory) {
    super(parent, SWT.NONE);

    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.horizontalSpacing = 20;
    gridLayout.verticalSpacing = 7;
    setLayout(gridLayout);
    

    GridData gridData;

//  **************************************************************************************************

//  factory.createLabel("lblPoolTime"); 
//  spinPoolTime = factory.createSpinner("lblPoolTime", SWT.BORDER);
//  spinPoolTime.setMinimum(0);
//  spinPoolTime.setMaximum(1440);
    
    factory.setComposite(this);
    
    gridLayout = new GridLayout(2, false);
    gridLayout.horizontalSpacing = 5;
    gridLayout.verticalSpacing = 7;
    Group grpCrawler = factory.createGroup("grpCrawlerConfig", new GridData(GridData.FILL_BOTH), gridLayout);
    factory.setComposite(grpCrawler);

    factory.createLabel("lblHttpClientTimeout"); 
    spinHttpClientTimeout = factory.createSpinner("lblHttpClientTimeout", SWT.BORDER);
    spinHttpClientTimeout.setMinimum(10);
    spinHttpClientTimeout.setMaximum(600);

    factory.createLabel("lblWeightPriority"); 
    spinWeightPriorityExecutor = factory.createSpinner("lblWeightPriority", SWT.BORDER);
    spinWeightPriorityExecutor.setMinimum(1);
    spinWeightPriorityExecutor.setMaximum(60);
    
    factory.createLabel("lblQueueSleep"); 
    spinQueueSleep = factory.createSpinner("lblQueueSleep", SWT.BORDER);
    spinQueueSleep.setMinimum(0);

    factory.createLabel("lblLimitDate"); 
    spinLimitDate = factory.createSpinner("lblLimitDate", SWT.BORDER);
    spinLimitDate.setMinimum(0);
    spinLimitDate.setMaximum(10);

    factory.createLabel("lblTotalExecutor"); 
    spinTotalExecutor = factory.createSpinner("lblTotalExecutor", SWT.BORDER);
    spinTotalExecutor.setMinimum(1);
    if(Application.LICENSE == Install.PERSONAL) { 
      spinTotalExecutor.setMaximum(5);
    } if(Application.LICENSE == Install.PROFESSIONAL) { 
      spinTotalExecutor.setMaximum(10);
    } if(Application.LICENSE == Install.ENTERPRISE) {
      spinTotalExecutor.setMaximum(30);
    } else if(Application.LICENSE == Install.SEARCH_SYSTEM) {
      spinTotalExecutor.setMaximum(1000);
    }

    factory.createLabel("lblSizeOfExecutor"); 
    spinSizeOfExecutor = factory.createSpinner("lblSizeOfExecutor", SWT.BORDER);
    spinSizeOfExecutor.setMinimum(1);
    if(Application.LICENSE == Install.PERSONAL) { 
      spinSizeOfExecutor.setMaximum(2);
    } else if(Application.LICENSE == Install.PROFESSIONAL) { 
      spinSizeOfExecutor.setMaximum(4);
    } else if(Application.LICENSE == Install.ENTERPRISE) {
      spinSizeOfExecutor.setMaximum(5);
    } else if(Application.LICENSE == Install.SEARCH_SYSTEM) {
      spinSizeOfExecutor.setMaximum(20);
    }

    butAutoStart = factory.createButton("butAutoStart", SWT.CHECK, null);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    butAutoStart.setLayoutData(gridData);
    
    butSaveImage2Folder = factory.createButton("butSaveImage2Folder", SWT.CHECK, null);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    butSaveImage2Folder.setLayoutData(gridData);
    

    butStartMiningIndex = factory.createButton("butStartMiningIndex", SWT.CHECK, null);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    butStartMiningIndex.setLayoutData(gridData);

    butStartIndexData = factory.createButton("butStartIndexData", SWT.CHECK, null);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    butStartIndexData.setLayoutData(gridData);
//    if(Application.LICENSE == Install.PERSONAL) butStartIndexData.setEnabled(false); 
    
    butLogWebsite = factory.createButton("butLogWebsite", SWT.CHECK, null);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    butLogWebsite.setLayoutData(gridData);

    if(Application.LICENSE == Install.SEARCH_SYSTEM) {
      butDetectWebsite = factory.createButton("butDetectWebsite", SWT.CHECK, null);
      gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.horizontalSpan = 2;
      butDetectWebsite.setLayoutData(gridData);
    }
    
    Composite comp1 = new Composite(this, SWT.NONE);
    comp1.setLayoutData(new GridData(GridData.FILL_VERTICAL));
    gridLayout = new GridLayout(1, false);
    comp1.setLayout(gridLayout);
    
    int style = GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL;
    factory.setComposite(comp1);
    
    gridData = new GridData(GridData.BEGINNING | style);
    Group groupProxy = factory.createGroup("grpProxy", gridData, new GridLayout(1, false));
    proxyConfig = new CrawlProxyConfig(groupProxy, factory);
    
    
//    factory.setComposite(comp1);
//    gridData = new GridData(GridData.BEGINNING | style);
//    Group groupMail = factory.createGroup("grpMail", gridData, new GridLayout(1, false));
//    mailConfig = new CrawlMailConfig(groupMail, factory);
  }
  
  void setValue2UI(Properties properties) {
    this.properties = properties;
    
    load(butAutoStart, AUTO_START_CRAWLER);
    load(butSaveImage2Folder, SAVE_IMAGE_TO_FILE);
    load(butStartMiningIndex, START_MINING_INDEX_SERVICE);
    load(butStartIndexData, START_INDEX_DATA_SERVICE);
    load(butLogWebsite, LOG_WEBSITE_ERROR);
    if(butDetectWebsite != null) load(butDetectWebsite, DETECT_WEBSITE);
    
//  ************************************************************************************************
//  try{
//  String value =  properties.getProperty(POOL_TIMER);
//  if(value != null && value.trim().length() > 0) {
//  spinPoolTime.setSelection(Integer.parseInt(value)/60);
//  }
//  }catch (Exception exp) {
//  ClientLog.getInstance().setException(getShell(), exp);
//  }
//  ************************************************************************************************

    load(spinQueueSleep, QUEUE_SLEEP, 0);
    load(spinTotalExecutor, TOTAL_EXECUTOR, 0);
    load(spinSizeOfExecutor, TOTAL_WORKER_OF_EXECUTOR, 0);
    load(spinHttpClientTimeout, HTTP_CLIENT_TIMEOUT, 1000);
    load(spinWeightPriorityExecutor, WEIGHT_PRIORITY_EXECUTOR, 0);
    load(spinLimitDate, DOWNLOAD_LIMIT_DATE, 0);
    
    proxyConfig.setValue2UI(properties);
  }
  
  void setData2Properties() throws Exception {
    properties.setProperty(AUTO_START_CRAWLER, String.valueOf(butAutoStart.getSelection()));
    properties.setProperty(SAVE_IMAGE_TO_FILE, String.valueOf(butSaveImage2Folder.getSelection()));
    properties.setProperty(START_MINING_INDEX_SERVICE, String.valueOf(butStartMiningIndex.getSelection()));
    if(butDetectWebsite != null) {
      properties.setProperty(DETECT_WEBSITE, String.valueOf(butDetectWebsite.getSelection()));
    }
    properties.setProperty(START_INDEX_DATA_SERVICE, String.valueOf(butStartIndexData.getSelection()));

    properties.setProperty(QUEUE_SLEEP, String.valueOf(spinQueueSleep.getSelection()));
    properties.setProperty(TOTAL_EXECUTOR, String.valueOf(spinTotalExecutor.getSelection()));
    properties.setProperty(TOTAL_WORKER_OF_EXECUTOR, String.valueOf(spinSizeOfExecutor.getSelection()));
    properties.setProperty(HTTP_CLIENT_TIMEOUT, String.valueOf(spinHttpClientTimeout.getSelection()*1000));
    properties.setProperty(WEIGHT_PRIORITY_EXECUTOR, String.valueOf(spinWeightPriorityExecutor.getSelection()));
    
    properties.setProperty(LOG_WEBSITE_ERROR, String.valueOf(butLogWebsite.getSelection()));

    properties.setProperty(DOWNLOAD_LIMIT_DATE, String.valueOf(spinLimitDate.getSelection()));
    
    proxyConfig.setData2Properties();
  }
  
  private void load(Button widget, String name){
    try{
      String value =  properties.getProperty(name);
      if(value == null || (value = value.trim()).length() < 1) return;
      widget.setSelection(new Boolean(value).booleanValue());
    }catch (Exception exp) {
      ClientLog.getInstance().setException(getShell(), exp);
    }
  }

  private void load(Spinner widget, String name, int devide){
    try {
      String value =  properties.getProperty(name);
      if(value == null || (value = value.trim()).length() < 1) return;
      int intValue = Integer.parseInt(value.trim());
      if(devide > 0) intValue = intValue/devide;  
      widget.setSelection(intValue);
    }catch (Exception exp) {
      ClientLog.getInstance().setException(getShell(), exp);
    }
  }

}
