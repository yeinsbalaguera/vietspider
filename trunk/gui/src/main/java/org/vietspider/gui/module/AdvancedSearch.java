/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.module;

import java.net.URLEncoder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
public class AdvancedSearch {

  private Shell shell;
  
  private Text txtSearch;
  private DateTime dtStart;
  private DateTime dtEnd;
  private Combo cboSource;
  
  private Browser browser;

  public AdvancedSearch(Browser browser) {
    this.browser = browser;
    shell = new Shell(browser.getShell(), SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    ClientRM resources = new ClientRM(getClass(), "AdvancedSearch");
    shell.setText(resources.getLabel("title"));
    
    ApplicationFactory factory = new ApplicationFactory(shell, resources, getClass().getName());
    shell.setLayout(new GridLayout(2, false));
    factory.setComposite(shell);
    
    Composite compStartDate = new Composite(shell, SWT.NONE);
    compStartDate.setLayout(new GridLayout(1, false));
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    compStartDate.setLayoutData(gridData);
    factory.setComposite(compStartDate);
    
    factory.createLabel("lblStart");
    dtStart = new DateTime(compStartDate, SWT.CALENDAR);
    gridData = new GridData();
//    gridData.widthHint = 350;
    gridData.horizontalSpan = 2;
    dtStart.setLayoutData(gridData);
    
    Composite compEndDate = new Composite(shell, SWT.NONE);
    compEndDate.setLayout(new GridLayout(1, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    compEndDate.setLayoutData(gridData);
    factory.setComposite(compEndDate);
    
    factory.createLabel("lblEnd");
    dtEnd = new DateTime(compEndDate, SWT.CALENDAR);
    gridData = new GridData();
//    gridData.widthHint = 350;
    dtEnd.setLayoutData(gridData);
    
    dtStart.setDate(dtEnd.getYear(), dtEnd.getMonth()-1, dtEnd.getDay());
    
    
    Composite compSource = new Composite(shell, SWT.NONE);
    compSource.setLayout(new GridLayout(3, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    compSource.setLayoutData(gridData);
    factory.setComposite(compSource);
    
    factory.createLabel("lblSource");
    cboSource = factory.createCombo(SWT.BORDER | SWT.READ_ONLY);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    cboSource.setLayoutData(gridData);
    factory.createButton("butGoSource", SWT.PUSH,  new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        goSource();
        shell.close();
      }   
    }); 
    
    
    Composite compSearch = new Composite(shell, SWT.NONE);
    GridLayout layout = new GridLayout(2, false);
    layout.horizontalSpacing = 0;
    layout.verticalSpacing = 0;
    layout.marginRight = 0;
    compSearch.setLayout(layout);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    compSearch.setLayoutData(gridData);
    factory.setComposite(compSearch);
    
    // search component
    txtSearch = factory.createText(SWT.SEARCH | SWT.ICON_CANCEL);
    txtSearch.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    /*if ((txtSearch.getStyle() & SWT.ICON_CANCEL) == 0) {
      Image image = shell.getDisplay().getSystemImage(SWT.ICON_ERROR);
      ToolBar toolBar = new ToolBar (compSearch, SWT.FLAT);
      ToolItem item = new ToolItem (toolBar, SWT.PUSH);
      item.setImage (image);
      item.addSelectionListener(new SelectionAdapter() {
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent e) {
          txtSearch.setText("");
          cboSource.select(-1);
        }
      });
    } else {
      gridData.horizontalSpan = 2;
    }*/
    txtSearch.setLayoutData(gridData);
    txtSearch.addSelectionListener(new SelectionAdapter() {
      public void widgetDefaultSelected(SelectionEvent e) {
        if (e.detail == SWT.CANCEL) {
          txtSearch.setText("");
          cboSource.select(-1);
        } else {
          search();
          new ShellSetter(AdvancedSearch.this.getClass(), shell);
          shell.close();
        }
      }
    });
    
    Composite bottom = new Composite(shell, SWT.NONE);
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
        txtSearch.setText("");
        cboSource.select(-1);
      }   
    }); 

    factory.createButton("butSearch", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        search();
        new ShellSetter(AdvancedSearch.this.getClass(), shell);
        shell.close();
      }   
    }); 
    
    shell.addShellListener(new ShellAdapter() {
      @SuppressWarnings("unused")
      public void shellClosed(ShellEvent e) {
        new ShellSetter(AdvancedSearch.this.getClass(), shell);
      }
    });
    
    loadSources();

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 550) / 2;
    int y = (displayRect.height - 350)/ 2 - 150;
    shell.setImage(browser.getShell().getImage());
//    shell.setSize(550, 350);
//    shell.setLocation(x, y);
    new ShellGetter(AdvancedSearch.class, shell, 350, 350, x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
  }
  
  private void loadSources() {
    Worker excutor = new Worker() {

      private String[] sources;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          sources = DataClientHandler.getInstance().loadAllSources("ARTICLE");
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        if(sources == null) return;
        cboSource.setItems(sources);
      }
    };
    new ThreadExecutor(excutor, cboSource).start();
  }
  
 /* public static void main(String[] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    
    AdvancedSearch searcher = new AdvancedSearch(shell);
    
    searcher.shell.addShellListener(new ShellAdapter(){       
      public void shellClosed(ShellEvent e){
        System.exit(0);
      }     
    });
    
    shell.open ();
    while (!shell.isDisposed ()) {
      if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
  }*/
  
  private void search() {
    String pattern = txtSearch.getText();
    pattern = pattern.trim();
    if(pattern.length() < 1) return;
    ClientConnector2 connector = ClientConnector2.currentInstance();
    StringBuilder builder = new StringBuilder();
    builder.append(connector.getRemoteURL()).append('/');
    try {
      pattern = URLEncoder.encode(pattern, "utf-8");
    } catch (Exception e) {
      ClientLog.getInstance().setThrowable(browser.getShell(), e);
      return;
    }
    builder.append(connector.getApplication()).append("/SEARCH/1/?text=").append(pattern);
    
    int idx = cboSource.getSelectionIndex();
    if(idx > -1) {
      String source = cboSource.getItem(idx);
      try {
        source = URLEncoder.encode(source, "utf-8");
      } catch (Exception e) {
        ClientLog.getInstance().setThrowable(browser.getShell(), e);
        return;
      }
      builder.append('&').append("source=").append(source);
    }
    
    builder.append('&').append("dtstart=").append(dtStart.getDay());
    builder.append('.').append(dtStart.getMonth()+1).append('.').append(dtStart.getYear());
    
    builder.append('&').append("dtend=").append(dtEnd.getDay());
    builder.append('.').append(dtEnd.getMonth()+1).append('.').append(dtEnd.getYear());
    
//    System.out.println("search url " + builder);
    
    browser.setUrl(builder.toString());
  }
  
  private void goSource() {
    int idx = cboSource.getSelectionIndex();
    if(idx < 0)  return;
    ClientConnector2 connector = ClientConnector2.currentInstance();
    StringBuilder builder = new StringBuilder();
    builder.append(connector.getRemoteURL()).append('/');
    builder.append(connector.getApplication()).append("/SEARCH/1/?");
    String source = cboSource.getItem(idx);
    try {
      source = URLEncoder.encode(source, "utf-8");
    } catch (Exception e) {
      ClientLog.getInstance().setThrowable(browser.getShell(), e);
      return;
    }
    builder.append("source=").append(source);
    browser.setUrl(builder.toString());
  }
  

}
