/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.OrganizationClientHandler;
import org.vietspider.client.common.ZipRatioWorker;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.gui.source.SourcesHandler;
import org.vietspider.model.Source;
import org.vietspider.model.XML2Source;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ProgressExecutor;
import org.vietspider.user.AccessChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 16, 2008  
 */
public class ImportSourceDialog {

  private Shell shell;

  private Button butImport, butCancel;
  private Button butOveride;

  private String lblStop;

  private boolean stop = false;

  private ProgressBar progressBar;
  private SourcesHandler handler;
  
  private String selectedGroup;
  private String selectedCategory;

  public ImportSourceDialog(Shell parent, SourcesHandler handler
                            ,  String selectedGroup, String selectedCategory) {
    shell = new Shell(parent, SWT.TITLE | SWT.APPLICATION_MODAL);
    shell.setLayout(new GridLayout(1, false));
    
    this.selectedGroup = selectedGroup;
    this.selectedCategory = selectedCategory;
    
    this.handler = handler;

    ApplicationFactory factory = new ApplicationFactory(shell, "Creator", getClass().getName());
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);

    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

    butOveride = factory.createButton(SWT.CHECK, factory.getLabel("butOveride"));
    butOveride.setSelection(true);
    butOveride.setLayoutData(gridData);

    Composite fileComposite = new Composite(shell, SWT.NONE);
    RowLayout rowLayout = new RowLayout();
    rowLayout.justify = true;
    fileComposite.setLayout(rowLayout);
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
    fileComposite.setLayoutData(gridData);
    factory.setComposite(fileComposite);

    lblStop = factory.getLabel("stopImport");

    butImport = factory.createButton("butImportFile", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        File file = getFile();
        if(file == null) {
          shell.dispose();
          return;
        }
        butCancel.setText(lblStop);
        butImport.setEnabled(false);
        importSources(file);
      }   
    });   


    butCancel = factory.createButton("butCancelImportFile", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(!butImport.isEnabled()) {
          stop = true;
        } 
        shell.dispose();
      }   
    });   

    factory.setComposite(shell);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    progressBar = factory.createProgress(gridData);   
    progressBar.setVisible(false);

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
//    shell.setImage(parent.getImage());
    shell.setLocation(x, y);
    shell.setSize(400, 140);
//    XPWindowTheme.setWin32Theme(shell);
    shell.open();
  }

  private File getFile() {
    Preferences prefs = Preferences.userNodeForPackage( getClass());     
    String p = prefs.get("openImportSource", "");
    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
    dialog.setFilterExtensions(new String[]{"*.zip"});
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
    if( p != null) prefs.put("openImportSource", p);
    if( p == null || p.trim().isEmpty()) return null;    
    if(p.indexOf('.') < 0) p = p+".zip";
    return new File(p);
  }

  private void importSources(final File file) {
    final boolean isOveride = butOveride.getSelection();
    ZipRatioWorker worker = new ZipRatioWorker() {

      private String message = "";

      @Override
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {}

      @Override
      public void execute() {
        AccessChecker accessChecker = null;
        try {
          accessChecker = new OrganizationClientHandler().loadAccessChecker();
        } catch (Exception e) {
          message = e.toString();
        }
        if(accessChecker == null) return;
        ZipInputStream zipInput = null;
        try {
          FileInputStream fileInput = new FileInputStream(file);    
          zipInput = new ZipInputStream(new BufferedInputStream(fileInput));
          ZipEntry entry;
          
//          XML2Object xml2Bean = XML2Object.getInstance();
          
          while((entry = zipInput.getNextEntry()) != null) {    
            if(stop) break;
            if(entry.isDirectory()) continue;
            
            ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
            int count;
            byte data[] = new byte[2048]; 
            while ((count = zipInput.read(data, 0, 2048)) != -1) {        
              bytesStream.write(data, 0, count);
              increaseRatio(1);
            }
            
            try {
              XML2Source xml2Source = new XML2Source();
              Source source = xml2Source.toSource(bytesStream.toByteArray());
              
              if(source == null) continue;
              
              source.setGroup(selectedGroup);
              source.setCategory(selectedCategory);
//              Source source = xml2Bean.toObject(Source.class, bytesStream.toByteArray());
              String valueKey = source.getFullName();
              if(!accessChecker.isPermitAccess(valueKey, true)) continue;
              if(!isOveride) {
                String group = source.getGroup();
                String category = source.getCategory();
                String name = source.getName();
                SourcesClientHandler client = new SourcesClientHandler(group);
                Source newSource = client.loadSource(category, name);
                if(newSource != null) continue;
              }
              new SourcesClientHandler(source.getGroup()).saveSource(source);
            } catch (Exception e) {
              ClientLog.getInstance().setException(null, e);
            }
          }
          
          zipInput.close();
        } catch (Exception e) {
          message = e.toString();
        } finally {
          try {
            if(zipInput != null) zipInput.close();
          } catch (Exception e) {
          }
        }
      }

      @Override
      public void after() {
        if(shell.isDisposed()) return;
        if(message != null && !message.trim().isEmpty()) {
          MessageBox msg = new MessageBox (shell, SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
        }
        handler.update();
        shell.dispose();
      }
    };
    worker.setRatio(0);
    try {
      ZipFile zipFile = new ZipFile(file, ZipFile.OPEN_READ);
      worker.setTotal(zipFile.size());
    } catch (Exception e) {
      ClientLog.getInstance().setException(shell, e);
      return;
    }
    progressBar.setVisible(true);
    ProgressExecutor loading = new ProgressExecutor(progressBar, worker);
    loading.open();
  }

}
