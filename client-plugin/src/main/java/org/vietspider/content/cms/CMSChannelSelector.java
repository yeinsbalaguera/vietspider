/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.cms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.io.LicenseVerifier;
import org.vietspider.common.util.Worker;
import org.vietspider.net.server.URLPath;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
abstract public class CMSChannelSelector  {

  protected Shell shell;
  protected Button [] butChannels;
  protected Text txtLinkToSource;
  protected Button butDebug;

  protected Table table;

  protected String metaId;
  protected String defaultCategory = null;

  private boolean destroy = false;
  
  private int buttonStyle = SWT.RADIO;

  public CMSChannelSelector(String plugin, Shell parent, String name, String title) {
    init(plugin, parent, new ApplicationFactory(parent, name, getClass().getName()), title, SWT.RADIO);
  }
  
  public CMSChannelSelector(String plugin, Shell parent, ClientRM rm, String title) {
    init(plugin, parent, new ApplicationFactory(parent, rm, getClass().getName()), title, SWT.RADIO);
  }
  
  public CMSChannelSelector(String plugin, Shell parent, ClientRM rm, String title, int butStyle) {
    init(plugin, parent, new ApplicationFactory(parent, rm, getClass().getName()), title, butStyle);
  }
  
  protected void init(String pligin, 
      Shell parent, ApplicationFactory factory, String title, int butStyle) {
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    factory.setComposite(shell);
    shell.setText(title);
    shell.setLayout(new GridLayout(1, false));

    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        shell.setVisible(false);
        e.doit = false;
      }
    });

    final KeyAdapter keyAdapter = new KeyAdapter() {
      public void keyPressed(KeyEvent event) {
        if(event.keyCode == SWT.CR) sync();
      }
    };


    Composite main = new Composite(shell, SWT.NONE);
    main.setLayout(new GridLayout(1, false));
    GridData gridData = new GridData(GridData.FILL_BOTH);
    main.setLayoutData(gridData);
    factory.setComposite(main);

    table = new Table(main, SWT.BORDER | SWT.MULTI);
    table.setLinesVisible(false);
    gridData = new GridData(GridData.FILL_BOTH);
    table.setLayoutData(gridData);

    final int TEXT_MARGIN = 10;
    table.addListener(SWT.MeasureItem, new Listener() {
      public void handleEvent(Event event) {
        TableItem item = (TableItem)event.item;
        String text = item.getText(event.index);
        Point size = event.gc.textExtent(text);
        event.width = size.x + 5*TEXT_MARGIN;
        event.height = Math.max(event.height, size.y + TEXT_MARGIN);
      }
    });
    factory.setComposite(main);
    
    butDebug = factory.createButton(SWT.CHECK);
    butDebug.setText("Log error?");
    butDebug.setFont(UIDATA.FONT_10);

    factory.setComposite(main);
    createOption(pligin, factory);

    factory.setComposite(main);
    createButton(factory, keyAdapter);
    
    buttonStyle = butStyle;

    loadCategries();
  }

  protected void createOption(String plugin, ApplicationFactory factory) {
    Composite optionComposite = new Composite(factory.getComposite(), SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    optionComposite.setLayoutData(gridData);
    optionComposite.setLayout(new GridLayout(2, false));
    factory.setComposite(optionComposite);

    File licenseFile = LicenseVerifier.loadLicenseFile();
    boolean license = LicenseVerifier.verify(plugin, licenseFile);
    
    factory.createLabel("lblLinkToSource");
    txtLinkToSource = factory.createText(SWT.BORDER);
    txtLinkToSource.setEnabled(license);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtLinkToSource.setLayoutData(gridData);
  }

  protected void createButton(ApplicationFactory factory, KeyAdapter keyAdapter) {
    Composite bottom = new Composite(factory.getComposite(), SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;

    factory.setComposite(bottom);

//    if(XPWidgetTheme.isPlatform()) {
//      factory.createCButton("butConfig", new SelectionAdapter(){
//        @SuppressWarnings("unused")
//        public void widgetSelected(SelectionEvent evt) {
//          shell.setVisible(false);
//          destroy = true;
//          invokeSetup();
//        }   
//      });
//    } else {
      factory.createButton("butConfig", new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          shell.setVisible(false);
          destroy = true;
          invokeSetup();
        }   
      });
//    }

//    if(XPWidgetTheme.isPlatform()) {
//      CButton butOk = factory.createCButton("butOk", new SelectionAdapter(){
//        @SuppressWarnings("unused")
//        public void widgetSelected(SelectionEvent evt) {
//          sync();
//        }   
//      }); 
//      butOk.addKeyListener(keyAdapter);
//    } else {
      Button butOk = factory.createButton("butOk", new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          sync();
        }   
      }); 
      butOk.addKeyListener(keyAdapter);
//    }

//    if(XPWidgetTheme.isPlatform()) {
//      CButton button = factory.createCButton("butClose", new SelectionAdapter(){
//        @SuppressWarnings("unused")
//        public void widgetSelected(SelectionEvent evt) {
//          new ShellSetter(CMSChannelSelector.this.getClass(), shell);
//          shell.setVisible(false);
//        }   
//      });
//      button.addKeyListener(keyAdapter);
//    } else {
      Button button = factory.createButton("butClose", new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          new ShellSetter(CMSChannelSelector.this.getClass(), shell);
          shell.setVisible(false);
        }   
      });
      button.addKeyListener(keyAdapter);
//    }


    shell.setImage(shell.getShell().getImage());
    shell.addKeyListener(keyAdapter);
  }

  public boolean isDispose() { return shell == null || shell.isDisposed(); }

  public void show() {
    //    listCategories.setSelection(-1);
    shell.setVisible(true); 
  }

  abstract public void invokeSetup();
  abstract public void sync() ;
  abstract public void loadCategries();
  
  protected int getSelectedIndex() {
    for(int i = 0; i < butChannels.length; i++) {
      if(butChannels[i].getSelection()) return i;
    }
    return -1;
  }

  protected Integer[] getSelectedIndexes() {
    List<Integer> list = new ArrayList<Integer>();
    for(int i = 0; i < butChannels.length; i++) {
      if(butChannels[i].getSelection()) list.add(i);
    }
    return list.toArray(new Integer[0]);
  }

  public void setMetaId(String metaId) {
    butDebug.setSelection(false);
    this.metaId = metaId; 
  }

  public void dispose() { shell.dispose(); }

  public boolean isDestroy() { return destroy; }

  protected void createCategories(String plugin, String [] names) {
    int length = 0;
    for (int i = 0; i < names.length; i++) {
      if(length < names[i].length()) {
        length = names[i].length();
      }
    }
    length = length *5;
    if(length < 120) length = 120;

    for (int i = 0; i < 4; i++) {
      TableColumn column = new TableColumn(table, SWT.NONE);
      column.setWidth(length);
    }

    int row = names.length/4 + 1;
    for (int i = 0; i < row; i++) {
      new TableItem(table, SWT.NONE);
    }

    butChannels = new Button[names.length];
    TableItem[] items = table.getItems();
    int index = 0;
    for (int i = 0; i < row; i++) {
      for(int j = 0; j < 4; j++) {
        TableEditor editor = new TableEditor(table);
        editor.grabHorizontal = true;

        if(index >= names.length) break;
        butChannels[index] = new Button(table, buttonStyle);
        butChannels[index].setText(names[index]);
        butChannels[index].setFont(UIDATA.FONT_8V);

        editor.setEditor(butChannels[index], items[i], j);
        butChannels[index].pack();
        index++;
      }
    }

    File licenseFile = LicenseVerifier.loadLicenseFile();
    boolean license = LicenseVerifier.verify(plugin, licenseFile);
    if(!license) {
      for(int i = 10; i < butChannels.length; i++) {
        butChannels[i].setEnabled(false);
      }
    }
  }
  
  protected void loadDefaultCategory(final String pluginName) {
    Worker excutor = new Worker() {

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        defaultCategory = null;
        for(int i = 0; i < butChannels.length; i++) {
          butChannels[i].setSelection(false);
        }
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "load.category"),
              new BasicHeader("plugin.name", pluginName)
          };
          ClientConnector2 connector = ClientConnector2.currentInstance();
          
          byte [] bytes = connector.post(URLPath.DATA_PLUGIN_HANDLER, metaId.getBytes(), headers);
          defaultCategory = new String(bytes, Application.CHARSET);
        } catch (Exception e) {
        }
      }

      public void after() {
        if(defaultCategory == null || defaultCategory.trim().isEmpty()) return;
        int idx = defaultCategory.indexOf('.');
        if(idx > 0) defaultCategory = defaultCategory.substring(idx+1);
        
        for(int i = 0; i < butChannels.length; i++) {
          if(butChannels[i].getText().equalsIgnoreCase(defaultCategory)) {
            butChannels[i].setSelection(true);
            return;
          }
        }
        
        String lowCate = defaultCategory.toLowerCase();
        
        for(int i = 0; i < butChannels.length; i++) {
          String low = butChannels[i].getText().toLowerCase(); 
          if(low.startsWith(lowCate) || lowCate.startsWith(low)) {
            butChannels[i].setSelection(true);
            return;
          }
        }
      }
    };
    new ThreadExecutor(excutor, table).start();
  }

}
