/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.sync;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.bean.sync.SyncDatabaseConfig;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.util.Worker;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.XML2Object;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.InformationViewer;
import org.vietspider.ui.widget.LiveSashForm;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 5, 2008  
 */
public class SyncDatabaseSetting {

  private Shell shell;

  private DatabaseInfo dataInfo;

  private Button butOk;
  private Button butAuto;

  private SyncDatabaseConfig config;
  private InformationViewer viewer;
  private SyncScriptInput syncScriptInput;

  private int mode = 0;

  private LiveSashForm sash;

  public SyncDatabaseSetting(Shell parent) {
    shell = new Shell(parent, SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    ApplicationFactory factory = new ApplicationFactory(shell, "Plugin", getClass().getName());
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);
    shell.setLayout(new GridLayout(1, false));

    sash = new LiveSashForm(shell, SWT.VERTICAL);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    sash.setLayoutData(gridData);

    dataInfo = new DatabaseInfo(sash, factory);

    butAuto = factory.createButton("butAuto", SWT.CHECK, null);
//  dataInfo.setLayoutData(gridData);
    syncScriptInput = new SyncScriptInput(sash, factory);

    Composite bottom = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    rowLayout.justify = true;
    bottom.setLayout(rowLayout);

    factory.setComposite(bottom);
    butOk = factory.createButton("butOk", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(config == null) {
          config = new SyncDatabaseConfig();
          mode = 0;
        } 
        if(mode == 0) {
          dataInfo.getData(config);
          config.setAuto(butAuto.getSelection());
          connect();
        } else {
          config.setScripts(syncScriptInput.getScripts());
          setVisible(false);
        }
      }   
    });
    butOk.setEnabled(Application.LICENSE != Install.PERSONAL);

    factory.createButton("butCancel", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        config = null;
        shell.setVisible(false);
      }   
    });

    sash.setMaximizedControl(dataInfo);

    load();

    shell.setSize(500, 250);
    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    shell.setImage(parent.getImage());
    shell.setLocation(x, y);
    shell.open();
  }

  public void setVisible(boolean value) {
    shell.setVisible(value);
  }

  private void connect() {
    Worker excutor = new Worker() {

      private String value;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          value = DataClientHandler.getInstance().checkDatabaseConnection(config);
        } catch (Exception e) {
          value = e.toString();
        }
      }

      public void after() {
        String [] buttons = new String[] {butOk.getText()};
        SelectionAdapter [] listeners = new SelectionAdapter[]{new SelectionAdapter() {
          @SuppressWarnings("unused")
          public void widgetSelected(SelectionEvent evt) {
            viewer.close();
            if(value.indexOf("Successfully connected") > -1) {
              if(shell.getSize().x < 600) shell.setSize(600, 350);
              sash.setMaximizedControl(syncScriptInput);
              mode = 1;
            } 
          } 
        }};
        viewer = new InformationViewer(shell, value, buttons, listeners);

      }
    };
    WaitLoading waitLoading =  new WaitLoading(shell, excutor, SWT.TITLE);
    waitLoading.open();
  }

  private void load() {
    Worker excutor = new Worker() {

      private String value;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "load.file.by.gzip"),
              new BasicHeader("file", "system/sync_database.xml")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();

          byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);
          value =  new String(bytes, Application.CHARSET);
        } catch (Exception e) {
          value = e.toString();
        }
      }

      public void after() {
        try {
          SyncDatabaseConfig dc = XML2Object.getInstance().toObject(SyncDatabaseConfig.class, value);
          dataInfo.setData(dc);
          butAuto.setSelection(dc.isAuto());
          syncScriptInput.setScripts(dc.getScripts());
        } catch (Exception e) {
          String [] buttons = new String[] {butOk.getText()};
          SelectionAdapter [] listeners = new SelectionAdapter[]{new SelectionAdapter() {
            @SuppressWarnings("unused")
            public void widgetSelected(SelectionEvent evt) {
              viewer.close();           
            } 
          }};
          viewer = new InformationViewer(shell, value, buttons, listeners);
        }
      }
    };
    WaitLoading waitLoading =  new WaitLoading(shell, excutor, SWT.TITLE);
    waitLoading.open();
  }

  public SyncDatabaseConfig getConfig() { return config; }

}
