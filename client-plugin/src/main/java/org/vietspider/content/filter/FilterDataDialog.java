/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.filter;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.text.NameConverter;
import org.vietspider.common.util.Worker;
import org.vietspider.net.server.URLPath;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 16, 2008  
 */
public class FilterDataDialog {

  protected Shell shell;
  private org.eclipse.swt.widgets.List cboRegionName;
  private Text txtInputName;
  private Text txtKeyWord;
  
  public FilterDataDialog(Shell parent) {
    shell = new Shell(parent, SWT.TITLE | SWT.RESIZE | SWT.CLOSE);
    shell.setLayout(new GridLayout(1, false));
    shell.setImage(parent.getImage());
    shell.setText("Quản lý bộ lọc");
    
    ClientRM clientRM = FilterDataPlugin.getResources();
    ApplicationFactory factory = new ApplicationFactory(shell, clientRM, getClass().getName());
    factory.setComposite(shell);
    
    shell.setLayout(new GridLayout(2, false));
    
    Composite nameComposite = new Composite(shell, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    nameComposite.setLayoutData(gridData);
    GridLayout gridLayout = new GridLayout(2, false);
    nameComposite.setLayout(gridLayout);
    factory.setComposite(nameComposite);

    txtInputName = factory.createText(SWT.BORDER);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtInputName.setLayoutData(gridData);
    txtInputName.setFont(UIDATA.FONT_10B);
    txtInputName.addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent event) {
        if(event.keyCode == SWT.CR) {
          saveFilter(txtInputName.getText(), "");
          txtInputName.setText("");
        }
      }
    });

    gridData = new GridData();
    gridData.heightHint = 26;
    factory.createIcon("butAddRegionName", new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent evt) {
        saveFilter(txtInputName.getText(), "");
        txtInputName.setText("");
      }
    }).setLayoutData(gridData);
    txtInputName.setToolTipText("Nhập tên bộ lọc và nhấn Enter để tạo bộ lọc mới");
    
    factory.setComposite(shell);
    
    cboRegionName = factory.createList(SWT.MULTI | SWT.BORDER  | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY, new String[0]);
    gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.widthHint = 150;
    cboRegionName.setLayoutData(gridData);
    cboRegionName.setFont(UIDATA.FONT_10B);
    cboRegionName.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        loadKeyWord();
      }      
    });

    Menu nameMenu = new Menu(shell, SWT.POP_UP);
    cboRegionName.setMenu(nameMenu);
    
    factory.createStyleMenuItem(nameMenu, "menuRenameFilter", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int idx = cboRegionName.getSelectionIndex();
        if(idx < 0 || idx >= cboRegionName.getItemCount()) {
          ClientLog.getInstance().setMessage(shell, new Exception("Hãy chọn một bộ lọc"));
          return;
        }
        new RenameFilterDialog(shell, FilterDataDialog.this, cboRegionName.getItem(idx));
      }
    });
    
    factory.createStyleMenuItem(nameMenu, "menuRemoveFilter", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        deleteFilter(cboRegionName.getSelection());
      }
    });
    
    Composite dataComposite = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_BOTH);
    dataComposite.setLayoutData(gridData);

    gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 2;
    dataComposite.setLayout(gridLayout);

    factory.setComposite(dataComposite);
    
    txtKeyWord = factory.createText(SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    gridData = new GridData(GridData.FILL_BOTH);
    txtKeyWord.setLayoutData(gridData);
    
    Composite bottom = new Composite(dataComposite, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;

    factory.setComposite(bottom);
    
    factory.createButton("butClear", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        txtKeyWord.setText("");
      }   
    }); 
    txtKeyWord.setToolTipText("Nhập các từ khóa cho bộ lọc, sử dụng dấu phẩy để phân cách");
    
    factory.createButton("butSave", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int idx = cboRegionName.getSelectionIndex();
        if(idx < 0 || idx >= cboRegionName.getItemCount()) {
          ClientLog.getInstance().setMessage(shell, new Exception("Hãy chọn một bộ lọc"));
          return;
        }
        saveFilter(cboRegionName.getItem(idx), txtKeyWord.getText().trim());
      }   
    }); 
    cboRegionName.setToolTipText("Danh sách các bộ lọc nội dung");
    
    loadFilter(null);
    
    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    shell.setLocation(x, y);
    shell.setSize(450, 350);
    shell.open();
  }
  
  void loadFilter(final String selected) {
    Worker excutor = new Worker() {

      private String error = null;
      private String [] names;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
       
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "list.folder"),
              new BasicHeader("file", "system/plugin/filter/")//"track/logs/"
          };
          
          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
          names = new String(bytes, Application.CHARSET).trim().split("\n");
          for(int i = 0; i < names.length; i++) {
            names[i] = NameConverter.decode(names[i]);
          }
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        if(names == null) return;
        cboRegionName.setItems(names);
        if(selected != null) selectFilter(selected);
        // end method
      }
    };
    new ThreadExecutor(excutor, cboRegionName).start();
  }
  
  void selectFilter(String name) {
    for(int i = 0 ; i < cboRegionName.getItemCount(); i++) {
      if(cboRegionName.getItem(i).equals(name)) {
        cboRegionName.setSelection(i);
        break;
      }
    }
  }
  
  void saveFilter(final String name, final String text) {
    Worker excutor = new Worker() {

      private String error = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() { 
      }

      public void execute() {
        if(name.length() < 1) return;
        try {
          Header [] headers =  new Header[] {
              new BasicHeader("action", "save.file"),
              new BasicHeader("file", "system/plugin/filter/" + NameConverter.encode(name))
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = text.getBytes(Application.CHARSET);
          connector.post(URLPath.FILE_HANDLER, bytes, headers);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        loadFilter(name);
      }
    };
    new ThreadExecutor(excutor, cboRegionName).start();
  }
  
  void deleteFilter(final String[] names) {
    Worker excutor = new Worker() {

      private String error = null;
 
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() { 
      }

      public void execute() {
        if(names == null || names.length < 1) return;
        try {
          for(int i = 0; i < names.length; i++) {
            Header [] headers =  new Header[] {
                new BasicHeader("action", "delete"),
                new BasicHeader("file", "system/plugin/filter/" + NameConverter.encode(names[i]))
            };

            ClientConnector2 connector = ClientConnector2.currentInstance();
            connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
          }
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        loadFilter(null);
      }
    };
    new ThreadExecutor(excutor, cboRegionName).start();
  }
  
  private void loadKeyWord() {
    Worker excutor = new Worker() {

      private String error = null;
      private String name;
      private String data;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        String [] selected = cboRegionName.getSelection();
        if(selected == null || selected.length < 1) return;
        name = selected[0];
      }

      public void execute() {
        if(name == null || name.length() < 1) return;
        try {
          
            Header [] headers =  new Header[] {
                new BasicHeader("action", "load.file"),
                new BasicHeader("file", "system/plugin/filter/" + NameConverter.encode(name))
            };

            ClientConnector2 connector = ClientConnector2.currentInstance();
            byte [] bytes = connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
            data = new String(bytes, Application.CHARSET);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        if(data == null) return;
        txtKeyWord.setText(data);
      }
    };
    new ThreadExecutor(excutor, cboRegionName).start();
  }
  
  String getKeyWord() { return txtKeyWord.getText(); }

}
