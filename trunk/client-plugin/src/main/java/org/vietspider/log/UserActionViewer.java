/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.log;

import java.io.File;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.ui.browser.PageMenu;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 2, 2008  
 */
public class UserActionViewer {
  
  private Browser browser;
  private Shell shell;
  
  public UserActionViewer(Composite parent, File file) {
    shell = new Shell(parent.getShell(), SWT.RESIZE | SWT.TITLE | SWT.CLOSE | SWT.MAX | SWT.MIN);
    shell.setImage(parent.getShell().getImage());
    
    ApplicationFactory factory = new ApplicationFactory(shell,  "LogPlugin", getClass().getName());
    factory.setComposite(shell);
    
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    shell.setLayout(gridLayout);
    
    try {
      browser = ApplicationFactory.createBrowser(shell, PageMenu.class);
    }  catch (Throwable e) {
      ClientLog.getInstance().setException(shell, new Exception(e.toString()));
      browser  = new Browser(shell, SWT.NONE);
    }
    
    GridData gridData = new GridData(GridData.FILL_BOTH);
    browser.setLayoutData(gridData);
    try {
      browser.setUrl(file.toURI().toURL().toString());
    } catch (Exception e) {
      ClientLog.getInstance().setException(shell, e);
    }
    browser.addTitleListener( new TitleListener(){
      public void changed(TitleEvent event){
        shell.setText(event.title);
      }
    });
    
    Composite bottom = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.VERTICAL_ALIGN_CENTER | GridData.HORIZONTAL_ALIGN_CENTER);
    bottom.setLayoutData(gridData);

    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.spacing = 20;
    rowLayout.justify = true;
    
    bottom.setLayout(rowLayout);
    factory.setComposite(bottom);
    
    factory.createButton("butExportToFile", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        File outFile = getFile();
        if(outFile == null) return;
        try {
          byte [] bytes = browser.getText().getBytes(Application.CHARSET);
          RWData.getInstance().save(outFile, bytes);
        } catch (Exception e) {
          ClientLog.getInstance().setException(shell, e);
        }
      }   
    }); 
    
    factory.createButton("butBack", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        browser.back();
      }   
    }); 
    
    shell.addShellListener(new ShellAdapter(){       
      @SuppressWarnings("unused")
      public void shellClosed(ShellEvent e){
        new ShellSetter(UserActionViewer.this.getClass(), shell);  
      }     
    });
    
    new ShellGetter(getClass(), shell, 700, 550);
    shell.open();
  }
  
  private File getFile() {
    Preferences prefs = Preferences.userNodeForPackage( getClass());     
    String path = prefs.get("openExportLog", "");
    FileDialog dialog = new FileDialog(shell, SWT.SAVE);
    dialog.setFilterExtensions(new String[]{"*.html", "*.htm"});
    if(path != null) {
      File file_ = new File(path);
      if(file_.isDirectory()) {
        dialog.setFilterPath(path);
      } else {
        try {
          dialog.setFilterPath(file_.getParentFile().getAbsolutePath());
        } catch (Exception e) {
        }
      }
    }
    path = dialog.open();
    if( path != null) prefs.put("openExportLog", path);
    if( path == null || path.trim().isEmpty()) return null;    
    if(path.indexOf('.') < 0) path = path+".html";
    return new File(path);
  }
  

}
