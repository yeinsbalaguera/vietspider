/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.export;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.io.LicenseVerifier;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.LButton;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 16, 2008  
 */
public abstract class ExportDataDialog {

  protected Shell shell;
  protected Button butExport;

  protected Table table; 

  protected Button [] butHeaders;

  protected boolean stop = false;
  protected String domain;
  protected String ext;
  protected String title;
  
  private static ClientRM clientRM;

  public ExportDataDialog(Shell parent, String domain_, String ext_, String title_) {
    shell = new Shell(parent, SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    shell.setLayout(new GridLayout(1, false));
    shell.setImage(parent.getImage());

    this.domain = domain_;
    this.ext = ext_;
    this.title = title_;

    ApplicationFactory factory = new ApplicationFactory(shell, getResources(), getClass().getName());
    shell.setText(title);
    factory.setComposite(shell);

    Label lbl  = factory.createLabel(SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    lbl.setLayoutData(gridData);
    lbl.setText("Select fields:");
    
    table = new Table(shell, SWT.BORDER | SWT.MULTI);
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

    Composite buttonComposite = new Composite(shell, SWT.NONE);
    RowLayout rowLayout = new RowLayout();
    rowLayout.justify = true;
    buttonComposite.setLayout(rowLayout);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    buttonComposite.setLayoutData(gridData);
    factory.setComposite(buttonComposite);

    butExport = factory.createButton(SWT.PUSH); 
    butExport.setText("Save");
    butExport.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String domainId = getDomainId();
        if(domainId == null) return;

        File file = getFile();
        if(file == null) return;
        export(domainId, file);
      }   
    }); 

    Button butCancel = factory.createButton(SWT.PUSH);
    butCancel.setText("Close");
    butCancel.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(!butExport.isEnabled()) {
          stop = true;
        } 
        shell.dispose();
      }   
    });   

    File licenseFile = LicenseVerifier.loadLicenseFile();
    boolean license = LicenseVerifier.verify("export", licenseFile);
    if(!license) shell.setText(title + " (Trial)");

    loadHeaders(getDomainId());
  }

  private File getFile() {
    Preferences prefs = Preferences.userNodeForPackage( getClass());     
    String p = prefs.get("openExportData", "");
    FileDialog dialog = new FileDialog(shell, SWT.SAVE);
    dialog.setFilterExtensions(new String[]{"*"+ext});
    if(p != null) {
      File file = new File(p);
      if(file.isDirectory()) {
        dialog.setFilterPath(p);
      } else {
        try {
          dialog.setFilterPath(file.getParentFile().getAbsolutePath());
        } catch (Exception e) {
        }
      }
    }
    p = dialog.open();
    if( p != null) prefs.put("openExportData", p);
    if( p == null || p.trim().isEmpty()) return null;    
    if(p.indexOf('.') < 0) p = p + ext;
    return new File(p);
  }

  private String getDomainId() {
    String elements [] = domain.split("/");
    if(elements.length < 1) return  null;

    elements[0] = elements[0].replace('.', '/');
    StringBuilder builder = new StringBuilder(elements[0]);

    if(elements.length > 1) {
      if(elements[1].indexOf('.') > -1) {
        builder.append('.').append(elements[1]);
      } else {
        builder.append('.').append(Application.GROUPS[0]).append('.').append(elements[1]);
      }
    }
    if(elements.length > 2) builder.append('.').append(elements[2]);
    return builder.toString();
  }

  public void loadHeaders(final String domainId) {
    if(domainId == null) return;
    Worker excutor = new Worker() {

      private String error = null;
      private String data = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          //          PluginClientHandler handler = new PluginClientHandler();
          //          data = handler.send("cvs.export.data.plugin", "load.headers", domainId);
          data = loadHeaderFromServer(domainId);
        } catch (Exception e) {
          error = e.toString();
        }

      }

      public void after() {
        if (error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        
        data = data.trim();
        if(data.indexOf('\n') > -1) {
          File file = getFile();
          if(file == null) return;
          File licenseFile = LicenseVerifier.loadLicenseFile();
          boolean license = LicenseVerifier.verify("export", licenseFile);
          if(!license) {
            Exception exp = new Exception("Trial Version: Maximum is 100 records!");
            ClientLog.getInstance().setMessage(shell, exp);
          }
          exportMore(file, data);
          return;
        }
        
        createCategories(data.split(","));

        Rectangle displayRect = UIDATA.DISPLAY.getBounds();
        int x = (displayRect.width - 350) / 2;
        int y = (displayRect.height - 300)/ 2;
        shell.setLocation(x, y);
        shell.setSize(450, 250);
        shell.open();
      }
    };
    new ThreadExecutor(excutor, shell).start();
  }
  
  public abstract void exportMore(File file, String data);

  public abstract String loadHeaderFromServer(String domainId) throws Exception ;

  protected void createCategories(String [] names) {
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

    butHeaders = new LButton[names.length];
    TableItem[] items = table.getItems();
    int index = 0;
    for (int i = 0; i < row; i++) {
      for(int j = 0; j < 4; j++) {
        TableEditor editor = new TableEditor(table);
        editor.grabHorizontal = true;

        if(index >= names.length) break;
        butHeaders[index] = new LButton(table, SWT.CHECK);
        butHeaders[index].setText(names[index]);
        butHeaders[index].setFont(UIDATA.FONT_8V);
        butHeaders[index].setSelection(true);

        editor.setEditor(butHeaders[index], items[i], j);
        butHeaders[index].pack();
        index++;
      }
    }
  }

  private void export(final String domainId, final File file) {  
    File licenseFile = LicenseVerifier.loadLicenseFile();
    boolean license = LicenseVerifier.verify("export", licenseFile);
    if(!license) {
      Exception exp = new Exception("Trial Version: Maximum is 100 records!");
      ClientLog.getInstance().setMessage(shell, exp);
    }
    
    Worker excutor = new Worker() {

      private List<String> headers = new ArrayList<String>();

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        for(int i = 0; i < butHeaders.length; i++) {
          if(!butHeaders[i].getSelection()) continue;
          headers.add(butHeaders[i].getText());
        }
      }

      public void execute() {
        export(domainId, file, headers.toArray(new String[0]));
      }

      public void after() { shell.dispose(); }
    };
    butExport.setEnabled(false);
    WaitLoading waitLoading =  new WaitLoading(butExport, excutor, SWT.TITLE);
    waitLoading.open();
  }

  public abstract void export(String domainId, File file, String[] dataHeaders);
  
  static synchronized ClientRM getResources() {
    if(clientRM != null) return clientRM;
    
    clientRM = new ClientRM(new ListResourceBundle() {
      protected Object[][] getContents() {
        return new String[][] {
           
        };
      }
    });
    
    return clientRM;
  }

}
