/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.prefs.Preferences;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.common.text.NameConverter;
import org.vietspider.common.util.Worker;
import org.vietspider.net.server.URLPath;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
class SEOMobileSetup {

  private Shell shell;

  private List lstAdv;

  SEOMobileSetup(Shell parent) {
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    shell.setText("Phone list");
    shell.setLayout(new GridLayout(2, false));

    lstAdv = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
    GridData gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.widthHint = 250;
    lstAdv.setLayoutData(gridData);

    Composite bottomComposite = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    bottomComposite.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottomComposite.setLayout(rowLayout);
    rowLayout.justify = true;

    Button butSave = new Button(bottomComposite, SWT.PUSH);
    butSave.setText("Download");
    butSave.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        save();
      }   
    }); 

    shell.addShellListener(new ShellAdapter() {
      @SuppressWarnings("unused")
      public void shellClosed(ShellEvent e) {
        new ShellSetter(SEOMobileSetup.this.getClass(), shell);
      }
    });

    loadList();

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 250) / 2;
    int y = (displayRect.height - 500)/ 2 - 150;
    shell.setImage(parent.getImage());
    new ShellGetter(SEOMobileSetup.class, shell, 250, 550, x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.setSize(250, 550);
    shell.open();
  }

  private File getFile() {
    Preferences prefs = Preferences.userNodeForPackage( getClass());     
    String p = prefs.get("openMobileFile", "");
    FileDialog dialog = new FileDialog(shell, SWT.SAVE);
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
    if(p != null) prefs.put("openMobileFile", p);
    if(p == null || p.trim().isEmpty()) return null;    
    return new File(p);
  }

  private void save() {
    if(lstAdv.getSelectionCount() < 1) return;
    Worker excutor = new Worker() {

      private String error = null;
      private java.util.List<String> names = new ArrayList<String>();
      private File file;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        Collections.addAll(names, lstAdv.getSelection());
        file  = getFile();
        if(file == null) return;
        if(!file.getAbsolutePath().endsWith(".txt")) {
          file  = new File(file.getAbsolutePath()+".txt");
        }
      }

      public void execute() {
        if(file == null) return;
        for(int i = 0; i < names.size(); i++) {
          try {
            Header [] headers =  new Header[] {
                new BasicHeader("action", "load.file.by.gzip"),
                new BasicHeader("file", "content/seo/mobile/"+names.get(i)+".txt")
            };

            ClientConnector2 connector = ClientConnector2.currentInstance();

            byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);
            String text = new String(bytes, "utf-8");
            text = text.replaceAll("\\(", "");
            text = text.replaceAll("\\)", "");
            RWData.getInstance().save(file, text.getBytes());
          } catch (Exception e) {
            ClientLog.getInstance().setThrowable(null, e);
            error = e.toString();
          }
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
      }
    };
    new ThreadExecutor(excutor, shell).start();
  }

  private void loadList() {
    Worker excutor = new Worker() {

      private String error = null;
      private String [] items  = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers =  new Header[] {
              new BasicHeader("action", "list.folder"),
              new BasicHeader("file", "content/seo/mobile/")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();

          byte [] bytes = connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
          String [] elements = new String(bytes, Application.CHARSET).trim().split("\n");
          java.util.List<String> list = new ArrayList<String>();
          for(int i = 0;  i < elements.length; i++) {
            int idx = elements [i].lastIndexOf('.');

            if(idx < 0) continue;
            String ext = elements[i].substring(idx+1);
            if(!"txt".equals(ext)) continue;
            list.add(NameConverter.decode(elements[i].substring(0, idx)));
          }
          items = list.toArray(new String[list.size()]);
        } catch (Exception e) {
          ClientLog.getInstance().setThrowable(null, e);
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        if(items == null) return;
        lstAdv.setItems(items);
      }
    };
    new ThreadExecutor(excutor, shell).start();
  }


}
