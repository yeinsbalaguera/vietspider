/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.homepage;

import java.io.File;
import java.io.FileOutputStream;
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
import org.vietspider.client.common.HomepagesClientHandler;
import org.vietspider.client.common.ZipRatioWorker;
import org.vietspider.common.io.LogService;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ProgressExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 16, 2008  
 */
public class ExportHomepageDialog {

  private Shell shell;

  private String  group;
  private String  category;
  private String  name; 

  private Button butExport, butCancel;

  private String lblStop;

  private boolean stop = false;

  private ProgressBar progressBar;

  public ExportHomepageDialog(Shell parent, 
      String group, String category, String name) {

    this.group = group;
    this.category = category;
    this.name = name;

    shell = new Shell(parent, SWT.CLOSE | SWT.APPLICATION_MODAL);
    shell.setLayout(new GridLayout(2, false));
    shell.addShellListener(new ShellAdapter() {
      @SuppressWarnings("unused")
      public void shellClosed(ShellEvent arg0) {
        if(!butExport.isEnabled()) {
          stop = true;
        }         
      }
    });

    String clazzName  = "org.vietspider.gui.creator.ExportHomepageDialog";
    ApplicationFactory factory = new ApplicationFactory(shell, "Creator", clazzName);
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);

    Composite fileComposite = new Composite(shell, SWT.NONE);
    RowLayout rowLayout = new RowLayout();
    rowLayout.justify = true;
    fileComposite.setLayout(rowLayout);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
    fileComposite.setLayoutData(gridData);
    factory.setComposite(fileComposite);

    lblStop = factory.getLabel("stopExport");
    
    butExport = factory.createButton("butExportFile", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        File file = getFile();
        if(file == null) {
          shell.dispose();
          return;
        }
        butCancel.setText(lblStop);
        butExport.setEnabled(false);
        export(file);
      }   
    });   

//    butExport = factory.createButton("butExport", new SelectionAdapter(){
//      @SuppressWarnings("unused")
//      public void widgetSelected(SelectionEvent evt) {
//        butCancel.setText(lblStop);
//        butExport.setEnabled(false);
//        export(null);
//      }   
//    });   
    

    butCancel = factory.createButton("butCancelExportFile", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(!butExport.isEnabled()) {
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
    shell.setImage(parent.getImage());
    shell.setLocation(x, y);
    shell.setSize(400, 100);
//    XPWindowTheme.setWin32Theme(shell);
    shell.open();

  }

  private File getFile() {
    Preferences prefs = Preferences.userNodeForPackage( getClass());     
    String p = prefs.get("openExportSource", "");
    FileDialog dialog = new FileDialog(shell, SWT.SAVE);
    dialog.setFilterExtensions(new String[]{"*.txt"});
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
    if( p != null) prefs.put("openExportSource", p);
    if( p == null || p.trim().isEmpty()) return null;    
    if(p.indexOf('.') < 0) p = p+".txt";
    return new File(p);
  }

  private void export(final File file) {
    ZipRatioWorker worker = new ZipRatioWorker() {

      private String message = "";

      @Override
      public void abort() {
        stop = true;
      }

      public void before() {
        if(file != null && file.exists()) file.delete();
      }

      @Override
      public void execute() {
        HomepagesClientHandler client = new HomepagesClientHandler();
        FileOutputStream output = null;
        try {
          if(!file.exists()) file.createNewFile();
          output = new FileOutputStream(file, true);
          
          int totalPage = client.loadTotalHomepages(group, category, name);
          this.setTotal(totalPage);
          
          for(int page = 1; page <= totalPage; page++) {
            String homepage = client.loadHomepages(group, category, name, page);
            if(page < totalPage) homepage += "\n";
            output.write(homepage.getBytes("utf-8"));
            increaseRatio(1);
            if(stop) break;
          }
          
         /* HttpResponse response = client.exportToFile(group, category, name);
          
          Header header = response.getFirstHeader("total.page.homepage");
          if(header == null) return; 
          this.setTotal(Integer.parseInt(header.getValue().trim()));
          
          
          InputStream inputStream = response.getEntity().getContent();
          while( (read = inputStream.read(buff)) != -1) {
            if(stop) break;
            String value  = new String(buff, 0, read, "utf-8");
            if(value.endsWith("\nend")) {
              value = value.substring(0, value.lastIndexOf("\nend"));
              output.write(value.getBytes("utf-8"));
              break;
            } 
            output.write(value.getBytes("utf-8"));            
            if(read > -1) increaseRatio(read);
          }*/
        } catch (Exception e) {
          if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
            message = e.getMessage();
          } 
          message = e.toString();
        } finally {
          try {
            if(output != null) output.close();
          } catch (Exception e) {
            LogService.getInstance().setThrowable(e);
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
