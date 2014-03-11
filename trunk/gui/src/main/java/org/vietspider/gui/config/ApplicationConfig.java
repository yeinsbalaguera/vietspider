/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.config;

import static org.vietspider.common.Application.APPLICATION_NAME;
import static org.vietspider.common.Application.CLEAN_DATABASE;
import static org.vietspider.common.Application.EXPIRE_DATE;
import static org.vietspider.common.Application.PORT;
import static org.vietspider.common.Application.SCHEDULE_CLEAN_DATA;
import static org.vietspider.common.Application.WEB_PORT;

import java.net.URL;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
public class ApplicationConfig  extends Composite {

//  private DatabaseSetting databaseSetting;

  private Spinner spinExpireDate;
  private Button butDeleteDatabase;
  private Spinner spinScheduleCleanData;
  private Text /*txtHost,*/ txtAppPort, txtWebPort, txtApplication/*, txtBackupFolder*/;
  
  private Properties properties;

  public ApplicationConfig(Composite parent, ApplicationFactory factory) {
    super(parent, SWT.NONE);

    setLayout(new GridLayout(1, false));
    factory.setComposite(this);

    GridData gridData;

    Group grpApplication = factory.createGroup("grpSystem", new GridData(), new GridLayout(1, false));
    
    Composite addressComposite = new Composite(grpApplication, SWT.NONE);
    addressComposite.setLayout(new GridLayout(1, false));
    factory.setComposite(addressComposite);
    
    Composite hostComposite = new Composite(addressComposite, SWT.NONE);
    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    hostComposite.setLayout(gridLayout);
    factory.setComposite(hostComposite);

    factory.createLabel("lblHost");   
    Text txtHost = factory.createText();
    gridData = new GridData();
    gridData.widthHint = 120;
    txtHost.setLayoutData(gridData);
    try {
      URL url = new URL(ClientConnector2.currentInstance().getRemoteURL());
      txtHost.setText(url.getHost());
    } catch (Exception e) {
    }
    txtHost.setEditable(false);
    
    Composite portComposite = new Composite(grpApplication, SWT.NONE);
    portComposite.setLayout(new GridLayout(2, false));
    factory.setComposite(portComposite);

    Composite appPortComposite = new Composite(portComposite, SWT.NONE);
    appPortComposite.setLayout(new GridLayout(3, false));
    factory.setComposite(appPortComposite);

    factory.createLabel("lblAppPort");     
    txtAppPort = factory.createText();
    gridData = new GridData();
    gridData.widthHint = 50;
    txtAppPort.setLayoutData(gridData);
    
    factory.createButton("butReStart", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        restartServer("restart.app", txtAppPort.getText());
      }   
    }); 
    
    Composite webPortComposite = new Composite(portComposite, SWT.NONE);
    webPortComposite.setLayout(new GridLayout(3, false));
    factory.setComposite(webPortComposite);

    factory.createLabel("lblWebPort");     
    txtWebPort = factory.createText();
    gridData = new GridData();
    gridData.widthHint = 50;
    txtWebPort.setLayoutData(gridData);
    
    factory.createButton("butReStart", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        restartServer("restart.web", txtWebPort.getText());
      }   
    }); 

    Composite applicationComposite = new Composite(grpApplication, SWT.NONE);
    applicationComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    applicationComposite.setLayoutData(gridData);
    factory.setComposite(applicationComposite);

    factory.createLabel("lblApplication");
    txtApplication = factory.createText();
    gridData = new GridData();
    gridData.widthHint = 100;
    txtApplication.setLayoutData(gridData);

//  Composite backupComposite = new Composite(group, SWT.NONE);
//  backupComposite.setLayout(new GridLayout(2, false));
//  gridData = new GridData(GridData.FILL_HORIZONTAL);
//  gridData.horizontalSpan = 2;
//  backupComposite.setLayoutData(gridData);
//  factory.setComposite(backupComposite);

//  Button button = factory.createButton("butBackupFolder", new SelectionAdapter(){
//  @SuppressWarnings("unused")
//  public void widgetSelected(SelectionEvent evt) {
//  Preferences prefs = Preferences.userNodeForPackage(getClass());     
//  String path = prefs.get("openBackupFolder", "");
//  DirectoryDialog dialog = new DirectoryDialog(txtBackupFolder.getShell(), SWT.OPEN);
//  if( path != null) dialog.setFilterPath( path);
//  path = dialog.open();
//  if(path != null) prefs.put("openFileInTab", path);
//  if(path == null || path.trim().equals("")) return;
//  txtBackupFolder.setText(path);
//  }   
//  });   
//  if(Application.LICENSE == Install.PERSONAL)  button.setEnabled(false);
//  txtBackupFolder = factory.createText();
//  gridData = new GridData(GridData.FILL_HORIZONTAL);
//  txtBackupFolder.setLayoutData(gridData);
//  if(Application.LICENSE == Install.PERSONAL)  txtBackupFolder.setEnabled(false);

    Composite dataComposite = new Composite(grpApplication, SWT.NONE);
    dataComposite.setLayout(new GridLayout(4, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    dataComposite.setLayoutData(gridData);
    factory.setComposite(dataComposite);

    factory.createLabel("lblExpireDate"); 
    spinExpireDate = factory.createSpinner("lblExpireDate", SWT.BORDER);
    spinExpireDate.setMinimum(2);
    spinExpireDate.setMaximum(10000);

    factory.createLabel("lblScheduleCleanData"); 
    spinScheduleCleanData = factory.createSpinner("lblScheduleCleanData", SWT.BORDER);
    spinScheduleCleanData.setMinimum(-1);
    spinScheduleCleanData.setMaximum(23);
    
    
    butDeleteDatabase = factory.createButton("butDeleteDatabase", SWT.CHECK, null);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 4;
    butDeleteDatabase.setLayoutData(gridData);
    
//    int style = GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL;
//    factory.setComposite(this);
//    gridData = new GridData(GridData.BEGINNING | style);
//    Group groupProxy = factory.createGroup("grpDatabaseSetting", gridData, new GridLayout(1, false));
//    databaseSetting = new DatabaseSetting(groupProxy, factory);
  }
  
  void setValue2UI(Properties properties) {
    this.properties = properties;
    if(properties == null) return;
    
//    databaseSetting.setDatabaseType(properties);
    load(spinExpireDate, EXPIRE_DATE, 0);
    load(spinScheduleCleanData, SCHEDULE_CLEAN_DATA, 0);
    
    butDeleteDatabase.setSelection("true".equals(properties.get(CLEAN_DATABASE)));

//  load(txtHost, HOST);
    load(txtAppPort, PORT);
    load(txtWebPort, WEB_PORT);
    load(txtApplication, APPLICATION_NAME);
//  load(txtBackupFolder, BACKUP_FOLDER);
  }
  
  void setData2Properties() throws Exception {
    properties.setProperty(EXPIRE_DATE, String.valueOf(spinExpireDate.getSelection()));
    properties.setProperty(CLEAN_DATABASE, String.valueOf(butDeleteDatabase.getSelection()));
    properties.setProperty(SCHEDULE_CLEAN_DATA, String.valueOf(spinScheduleCleanData.getSelection()));

//  ************************************************************************************************
//  properties.setProperty(HOST, txtHost.getText());
    properties.setProperty(APPLICATION_NAME, txtApplication.getText());
//  properties.setProperty(BACKUP_FOLDER, txtBackupFolder.getText());
    if(txtAppPort.getText().trim().length() > 0) {
      properties.setProperty(PORT, String.valueOf(Integer.parseInt(txtAppPort.getText())));
    }
    
    if(txtWebPort.getText().trim().length() > 0) {
      properties.setProperty(WEB_PORT, String.valueOf(Integer.parseInt(txtWebPort.getText())));
    }
    
//    int type = databaseSetting.type();
//    if(type == 0) {
//      properties.put("dataCleaner", "org.vietspider.data.jdbc2.DataCleaner");
//      properties.put("dataGetter", "org.vietspider.data.jdbc2.DataGetter");
//      properties.put("dataSaver", "org.vietspider.data.jdbc2.DataSetter");
//      properties.put("dataUtils", "org.vietspider.data.jdbc2.DataUtils");
//      properties.put("databases.type", "");
//    } else if(type == 1) {
//      properties.put("dataCleaner", "org.vietspider.db.remote.RemoteDataCleaner");
//      properties.put("dataGetter", "org.vietspider.db.remote.RemoteDataGetter");
//      properties.put("dataSaver", "org.vietspider.db.remote.RemoteDataSetter");
//      properties.put("dataUtils", "org.vietspider.db.remote.RemoteDataUtils");
//      properties.put("databases.type", "org.vietspider.db.content.ArticleDatabases");
//    } else if(type == 2) {
//      properties.put("dataCleaner", "org.vietspider.vietspider.content.DataCleaner");
//      properties.put("dataGetter", "org.vietspider.vietspider.content.DataGetter");
//      properties.put("dataSaver", "org.vietspider.vietspider.content.DataSetter");
//      properties.put("dataUtils", "org.vietspider.vietspider.content.DataUtils");
//      properties.put("databases.type", "org.vietspider.solr2.SolrIndexStorage");
//    } else {
//      properties.put("dataCleaner", "org.vietspider.vietspider.content.DataCleaner");
//      properties.put("dataGetter", "org.vietspider.vietspider.content.DataGetter");
//      properties.put("dataSaver", "org.vietspider.vietspider.content.DataSetter");
//      properties.put("dataUtils", "org.vietspider.vietspider.content.DataUtils");
//      properties.put("databases.type", "org.vietspider.db.content.ArticleDatabases");
//    }
    
  }
  
  private void load(Spinner widget, String name, int devide){
    try {
      String value =  properties.getProperty(name);
      if(value == null || (value = value.trim()).length() < 1) return;
      int intValue = Integer.parseInt(value);
      if(devide > 0) intValue = intValue/devide;  
      widget.setSelection(intValue);
    }catch (Exception exp) {
      ClientLog.getInstance().setException(getShell(), exp);
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

//  public DatabaseSetting getDatabaseSetting() { return databaseSetting; }

  private void restartServer(final String type, final String port) {
    Worker excutor = new Worker() {

      private String message = "";

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        if(port == null || port.trim().isEmpty()) {
          message = "Error: Port is empty!";
          return;
        }
        try {
          DataClientHandler.getInstance().restartServer(type, port);
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
          MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
        }
      }
    };
    WaitLoading loading = new WaitLoading(txtApplication, excutor);
    loading.open();
  }

}
