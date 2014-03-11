/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.text.NameConverter;
import org.vietspider.common.util.Worker;
import org.vietspider.model.Group;
import org.vietspider.model.Groups;
import org.vietspider.model.Source;
import org.vietspider.serialize.Object2XML;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ProgressExecutor;
import org.vietspider.ui.widget.waiter.WaitLoading;
import org.vietspider.user.AccessChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 16, 2008  
 */
public class ExportSourceDialog {

  private Shell shell;

  private String  group;
  private String [] categories;
  private String [] names; 

  private Button butExport, butCancel;
  private Button[] radios;

  private String lblStop;

  private boolean stop = false;

  private ProgressBar progressBar;

  public ExportSourceDialog(Shell parent, 
      String group, String [] categories, String [] names) {

    this.group = group;
    this.categories = categories;
    this.names = names;

    shell = new Shell(parent, SWT.TITLE | SWT.APPLICATION_MODAL);
    shell.setLayout(new GridLayout(2, false));

    ApplicationFactory factory = new ApplicationFactory(shell, "Creator", getClass().getName());
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);

    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    radios = new Button[4];

    radios[0] = factory.createButton(SWT.RADIO, factory.getLabel("butSelectedSources"));
    radios[0].setLayoutData(gridData);
    if(Application.LICENSE == Install.SEARCH_SYSTEM) {
      radios[2] = factory.createButton(SWT.RADIO, factory.getLabel("butSelectedGroup"));
      radios[2].setLayoutData(gridData);
    }
    radios[1] = factory.createButton(SWT.RADIO, factory.getLabel("butSelectedCategories"));
    radios[1].setLayoutData(gridData);
    radios[3] = factory.createButton(SWT.RADIO, factory.getLabel("butAllGroup"));
    radios[3].setLayoutData(gridData);

    if(names == null || names.length < 1) {
      radios[0].setEnabled(false);
      if(categories == null || categories.length < 1) {
        if(group == null || group.trim().isEmpty()) {
          radios[2].setEnabled(false);
        } else {
          radios[1].setSelection(true);
        }
        radios[1].setEnabled(false);
      } else {
        radios[1].setSelection(true);
      }
    } else {
      radios[0].setSelection(true);
    }


    Composite fileComposite = new Composite(shell, SWT.NONE);
    RowLayout rowLayout = new RowLayout();
    rowLayout.justify = true;
    fileComposite.setLayout(rowLayout);
    gridData = new GridData(GridData.FILL_BOTH);
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
//    shell.setImage(parent.getImage());
    shell.setLocation(x, y);
    shell.setSize(400, 140);
//    XPWindowTheme.setWin32Theme(shell);
    shell.open();

  }

  private File getFile() {
    Preferences prefs = Preferences.userNodeForPackage( getClass());     
    String p = prefs.get("openExportSource", "");
    FileDialog dialog = new FileDialog(shell, SWT.SAVE);
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
    if( p != null) prefs.put("openExportSource", p);
    if( p == null || p.trim().isEmpty()) return null;    
    if(p.indexOf('.') < 0) p = p+".zip";
    return new File(p);
  }

  private void export(final File file, final List<ESource> list) {
    ZipRatioWorker worker = new ZipRatioWorker() {

      private String message = "";

      @Override
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {}

      @Override
      public void execute() {
        try {
          if(file.exists()) file.createNewFile();
        } catch (Exception e) {
          message = e.toString();
        }
        ZipOutputStream zipOutput = null;
        try {
          FileOutputStream outputStream = new FileOutputStream(file);
          zipOutput = new ZipOutputStream(outputStream);
          Object2XML bean2XML = Object2XML.getInstance();
          for(int i = 0; i < list.size(); i++) {
            if(stop) break;
            increaseRatio(1);
            ESource eSource = list.get(i);
            String name = eSource.n;
            int idx = name.indexOf('.');
            if(idx > -1) name = name.substring(idx+1);
            SourcesClientHandler client = new SourcesClientHandler(eSource.g);
            Source source = client.loadSource(eSource.c, name);
            if(source == null) continue;

            StringBuilder builder = new StringBuilder(eSource.g);
            builder.append('.').append(NameConverter.encode(eSource.c));
            builder.append('.').append(NameConverter.encode(name));
            ZipEntry entry = new ZipEntry(builder.toString());

            zipOutput.putNextEntry(entry);
            String xml = bean2XML.toXMLDocument(source).getTextValue();
            byte [] bytes = xml.getBytes(Application.CHARSET);
            zipOutput.write(bytes, 0, bytes.length);
            zipOutput.closeEntry();    
          }
          zipOutput.close();
        } catch (Exception e) {
          message = e.toString();
        } finally {
          try {
            if(zipOutput != null) zipOutput.close();
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
        shell.dispose();
      }
    };
    worker.setRatio(0);
    worker.setTotal(list.size());
    progressBar.setVisible(true);
    ProgressExecutor loading = new ProgressExecutor(progressBar, worker);
    loading.open();
  }

  private void export(File file) {
    int style = 0;
    for(int i = 0; i< radios.length; i++) {
      if(radios[i] == null || !radios[i].getSelection()) continue;
      style  = i;
      break;
    }
    List<ESource> list = new ArrayList<ESource>();
    if(style == 0) {
      for(int i = 0; i < names.length; i++) {
        list.add(new ESource(group, categories[0], names[i])); 
      }
      if(list.size() > 0) export(file, list);
      return;
    } 

    if(style == 1) {
      exportSources(file, list);
      return;
    }

    if(style == 2) {
      exportCategories(file, list);
      return;
    } 
    exportGroups(file, list);
  }

  private void exportSources (final File file, final List<ESource> list) {
    Worker excutor = new Worker() {

      private String message = "";

      public void abort() {
        ClientConnector2.currentInstance().abort();
        shell.dispose();
        return;
      }

      public void before() { }

      public void execute() {
        try {
          SourcesClientHandler client = new SourcesClientHandler(group);
          for(String cate : categories) {
            String [] sources = client.loadSources(cate);
            for(int i = 0; i < sources.length; i++) {
              list.add(new ESource(group, cate, sources[i])); 
            }
          }
        } catch (Exception e) {
          if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
            message = e.getMessage();
          } else {
            message = e.toString();
          }
        }
      }

      public void after() {
        if(shell.isDisposed()) return;
        if(message != null && !message.trim().isEmpty()) {
          MessageBox msg = new MessageBox (shell, SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
        }
        if(list.size() > 0) {
          export(file, list);
        } else {
          shell.dispose();
        }
      }
    };
    WaitLoading loading = new WaitLoading(shell, excutor);
    loading.open();
  }

  private void exportCategories(final File file, final List<ESource> list) {
    Worker excutor = new Worker() {

      private String message = "";

      public void abort() {
        ClientConnector2.currentInstance().abort();
        shell.dispose();
        return;
      }

      public void before() { }

      public void execute() {
        try {
          AccessChecker accessChecker = null;
          try {
            accessChecker = new OrganizationClientHandler().loadAccessChecker();
          } catch (Exception e) {
            message = e.toString();
          }
          
          if(accessChecker == null) return;
          
          SourcesClientHandler client = new SourcesClientHandler(group);
          categories = client.loadCategories();
          for(String cate : categories) {
            String valueKey = group + "." + cate;
            if(!accessChecker.isPermitAccess(valueKey, true)) continue;
            
            String [] sources = client.loadSources(cate);
            for(int i = 0; i < sources.length; i++) {
              list.add(new ESource(group, cate, sources[i])); 
            }
          }
        } catch (Exception e) {
          if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
            message = e.getMessage();
          } else {
            message = e.toString();
          }
        }
      }

      public void after() {
        if(shell.isDisposed()) return;
        if(message != null && !message.trim().isEmpty()) {
          MessageBox msg = new MessageBox (shell, SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
        }
        if(list.size() > 0) {
          export(file, list);
        } else {
          shell.dispose();
        }
      }
    };
    WaitLoading loading = new WaitLoading(shell, excutor);
    loading.open();
  }

  private void exportGroups(final File file, final List<ESource> list) {
    Worker excutor = new Worker() {
      private String message = "";
      public void abort() {
        ClientConnector2.currentInstance().abort();
        shell.dispose();
        return;
      }

      public void before() { }

      public void execute() {
        try {
          Groups groups = new SimpleSourceClientHandler().loadGroups();
          AccessChecker accessChecker = null;
          try {
            accessChecker = new OrganizationClientHandler().loadAccessChecker();
          } catch (Exception e) {
            message = e.toString();
          }
          
          if(accessChecker == null) return;
          
          for(Group group_ : groups.getGroups()) {
            String groupType = group_.getType(); 
            if(groupType.equals("DUSTBIN")) continue;
            if(!accessChecker.isPermitGroup(groupType)) continue;
            SourcesClientHandler client = new SourcesClientHandler(groupType);
            categories = client.loadCategories(); 
            for(String cate : categories) {
              String valueKey = groupType + "." + cate;
              if(!accessChecker.isPermitAccess(valueKey, true)) continue;
              String [] sources = client.loadSources(cate);
              for(int i = 0; i < sources.length; i++) {
                list.add(new ESource(groupType, cate, sources[i])); 
              }
            }  
          }
        } catch (Exception e) {
          if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
            message = e.getMessage();
          } else {
            message = e.toString();
          }
        }
      }

      public void after() {
        if(shell.isDisposed()) return;
        if(message != null && !message.trim().isEmpty()) {
          MessageBox msg = new MessageBox (shell, SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
        }
        if(list.size() > 0) {
          export(file, list);
        } else {
          shell.dispose();
        }
      }
    };
    WaitLoading loading = new WaitLoading(shell, excutor);
    loading.open();
  }

  private class ESource {

    private String g;
    private String c;
    private String n;

    private ESource(String g, String c, String n) {
      this.g = g;
      this.c = c;
      this.n = n;
    }
  }
}
