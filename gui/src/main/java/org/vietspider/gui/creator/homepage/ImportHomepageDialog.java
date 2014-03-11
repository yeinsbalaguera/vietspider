/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.homepage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.prefs.Preferences;

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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.HomepagesClientHandler;
import org.vietspider.client.common.ZipRatioWorker;
import org.vietspider.common.io.LogService;
import org.vietspider.common.io.RandomAccess;
import org.vietspider.link.pattern.LinkPatternFactory;
import org.vietspider.link.pattern.LinkPatterns;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ProgressExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 16, 2008  
 */
public class ImportHomepageDialog extends RandomAccess {

  private Shell shell;

  private Button butImport, butCancel;

  private String lblStop;

  private boolean stop = false;
  
  private URLHomepageDialog dialog;

  private ProgressBar progressBar;

  public ImportHomepageDialog(Shell parent, URLHomepageDialog dialog) {
    this.dialog = dialog;
    shell = new Shell(parent, SWT.CLOSE | SWT.APPLICATION_MODAL);
    shell.setLayout(new GridLayout(1, false));
    
    String clazzName  = "org.vietspider.gui.creator.ImportHomepageDialog";
    ApplicationFactory factory = new ApplicationFactory(shell, "Creator", clazzName);
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);
    
    shell.addShellListener(new ShellAdapter() {
      @SuppressWarnings("unused")
      public void shellClosed(ShellEvent arg0) {
        if(!butImport.isEnabled()) {
          stop = true;
        } 
      }
    });

    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    
    Composite bottom = new Composite(shell, SWT.NONE);
    RowLayout rowLayout = new RowLayout();
    rowLayout.justify = true;
    bottom.setLayout(rowLayout);
    gridData = new GridData(GridData.FILL_BOTH);
    bottom.setLayoutData(gridData);
    factory.setComposite(bottom);

    lblStop = factory.getLabel("stopImport");

    butImport = factory.createButton("butImportFile", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        File[] files = getFile();
        if(files == null) {
          shell.dispose();
          return;
        }
        butCancel.setText(lblStop);
        butImport.setEnabled(false);
        for(int i = 0; i < files.length; i++) {
          importHomepages(files[i]);
        }
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
    progressBar = factory.createProgress(gridData);   
    progressBar.setVisible(false);

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
//    shell.setImage(parent.getImage());
    shell.setLocation(x, y);
    shell.setSize(400, 100);
//    XPWindowTheme.setWin32Theme(shell);
    shell.open();
  }

  private File[] getFile() {
    Preferences prefs = Preferences.userNodeForPackage( getClass());     
    String p = prefs.get("openImportSource", "");
    FileDialog fDialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
    fDialog.setFilterExtensions(new String[]{"*.txt", "*.*"});
    if(p != null) {
      File file = new File(p);
      if(file.isDirectory()) {
        fDialog.setFilterPath(p);
      } else {
        try {
          fDialog.setFilterPath(file.getParentFile().getAbsolutePath());
        } catch (Exception e) {
          
        }
      }
    }
    p = fDialog.open();
    if( p != null) prefs.put("openImportSource", p);
    if( p == null || p.trim().isEmpty()) return null;  
    File file = new File(p);
    String [] paths = fDialog.getFileNames();
    File [] files = new File[paths.length];
    for(int i = 0; i < files.length; i++) {
      files[i] = new File(file.getParentFile(), paths[i]);
    }
    return files;
  }

  private void importHomepages(final File file) {
    ZipRatioWorker worker = new ZipRatioWorker() {

      private String message = "";
      
      private String group;
      private String category;
      private String name;
      
      private String [] templates;
      
      @Override
      public void abort() {
        stop = true;
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        if(dialog.getShell().isDisposed()) return;
        group = dialog.getGroup();
        category = dialog.getCategory();
        name = dialog.getName();
        
        templates = dialog.getTemplate().split("\n");
      }

      @Override
      public void execute() {
        HomepagesClientHandler client = new HomepagesClientHandler();
        /*if(file == null) {
          try {
            client.importHomepage(group, category, name);
          } catch (Exception e) {
            if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
              message = e.getMessage();
            } 
            message = e.toString();
          }
          return;
        }*/
        
        RandomAccessFile random = null;
        try {
          random = new RandomAccessFile(file, "r");
          random.seek(0);
          long pointer = 0;
          long length = random.length();
          setTotal(100);
          
          StringBuilder builder = new StringBuilder(group);
          builder.append('.').append(category).append('.').append(name).append('\n');
          
          LinkPatterns patterns = LinkPatternFactory.createPatterns(LinkPatterns.class, templates);
          int counter = 0;
          
          while(pointer < length){
            pointer = random.getFilePointer();
            String line = readLine(random);
            if(line == null || stop) break;
            setRatio((int)((pointer*100)/length));
            
            if(patterns.match(line)) {
              builder.append(line).append('\n');
              counter++;
            }
            
            if(counter < 100) continue;
            
            try {
              client.saveHomepages(builder.toString());
            } catch (Exception e) {
              ClientLog.getInstance().setException(null, e);
            }
            builder = new StringBuilder(group);
            builder.append('.').append(category).append('.').append(name).append('\n');
            counter = 0;
          }
          
          if(builder.length() > 0) {
            try {
              client.saveHomepages(builder.toString());
            }catch (Exception e) {
              ClientLog.getInstance().setException(null, e);
            }
          }
          random.close();
        } catch (IOException e) {
          LogService.getInstance().setThrowable(e);
        } finally {
          try {
            if(random != null) random.close();
          } catch (Exception e2) {
            LogService.getInstance().setThrowable(e2);
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
        shell.dispose();
      }
    };
    worker.setRatio(0);
    progressBar.setVisible(true);
    ProgressExecutor loading = new ProgressExecutor(progressBar, worker);
    loading.open();
  }

}
