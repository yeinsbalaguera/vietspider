/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.export;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.prefs.Preferences;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
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
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.PluginClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.common.util.Worker;
import org.vietspider.net.client.AbstClientConnector.HttpData;
import org.vietspider.net.server.URLPath;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 16, 2008  
 */
public class ExportAll2XMLDataDialog {

  private Shell shell;
  private Button butExport;

  private boolean stop = false;
  private String value;
  
  public ExportAll2XMLDataDialog(Shell parent, String value_) {
    shell = new Shell(parent, SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    shell.setLayout(new GridLayout(1, false));
    
    ClientRM clientRM = ExportAll2XMLDataPlugin.getResources();
    
    this.value = value_;

    ApplicationFactory factory = new ApplicationFactory(shell, clientRM, getClass().getName());
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);

    Composite dataComposite = new Composite(shell, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    dataComposite.setLayoutData(gridData);
    dataComposite.setLayout(new GridLayout(4, false));
    factory.setComposite(dataComposite);
    
 
    Composite bottomComposite = new Composite(shell, SWT.NONE);
    bottomComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    factory.setComposite(bottomComposite);
    
    Composite buttonComposite = new Composite(shell, SWT.NONE);
    RowLayout rowLayout = new RowLayout();
    rowLayout.justify = true;
    buttonComposite.setLayout(rowLayout);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    buttonComposite.setLayoutData(gridData);
    factory.setComposite(buttonComposite);

    butExport = factory.createButton("butExportFile", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String elements [] = value.split("/");
        if(elements.length < 1) return;
        
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
        
        File file = getFile();
        if(file == null) return;
        export(builder.toString(), file);
      }   
    });   


    factory.createButton("butCancelExportFile", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(!butExport.isEnabled()) {
          stop = true;
        } 
        shell.dispose();
      }   
    });   

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    shell.setImage(parent.getImage());
    shell.setLocation(x, y);
    shell.setSize(450, 110);
    shell.open();
  }
  
  private void export(final String domainId, final File file) {  
    Worker excutor = new Worker() {
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {}

      public void execute() {
        String plugin = "export.data.to.xml.plugin";
        if(stop) return;
        ClientConnector2 connector = ClientConnector2.currentInstance();
        HttpData httpData = null;
        try {
          PluginClientHandler handler = new PluginClientHandler();
          String pageValue = handler.send(plugin, "compute.export", domainId);
          int totalPage  = -1;
          try {
            totalPage = Integer.parseInt(pageValue);
          } catch (Exception e) {
            return;
          }
          if(totalPage < 1) return;
          int page = 1;
          
          String simpleName = file.getName();
          int idx = simpleName.indexOf(".xml");
          simpleName = simpleName.substring(0, idx);
          File writableFile = file;
          
          while(page < totalPage) {
            Header [] headers = new Header[] {
                new BasicHeader("action", "export"),
                new BasicHeader("plugin.name", plugin),
                new BasicHeader("page", String.valueOf(page))
            };
            byte [] bytes = domainId.getBytes(Application.CHARSET);
            httpData = connector.loadResponse(URLPath.DATA_PLUGIN_HANDLER, bytes, headers);
            InputStream inputStream = httpData.getStream();
            InputStream gzip = null;
            try {
              gzip = new GZIPInputStream(inputStream);
            } catch (Exception e) {
              gzip = inputStream;
            }
            
            String prefix = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n\n";
            ByteArrayInputStream prefixBytesInput = new ByteArrayInputStream(prefix.getBytes());
            RWData.getInstance().save(writableFile, prefixBytesInput, true);
            try {
              RWData.getInstance().save(writableFile, gzip, true);
              inputStream.close();
            } finally {
              connector.release(httpData);
              inputStream.close();
            }
            page += 10;
            writableFile = new File(file.getParentFile(), simpleName+"_"+ String.valueOf(page)+".xml");
          }
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        } finally {
          connector.release(httpData);
        }
      }

      public void after() { shell.dispose(); }
    };
    butExport.setEnabled(false);
    WaitLoading waitLoading =  new WaitLoading(butExport, excutor, SWT.TITLE);
    waitLoading.open();
  }
  
  private File getFile() {
    Preferences prefs = Preferences.userNodeForPackage( getClass());     
    String p = prefs.get("openExportData", "");
    FileDialog dialog = new FileDialog(shell, SWT.SAVE);
    dialog.setFilterExtensions(new String[]{"*.xml"});
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
    if(p.indexOf('.') < 0) p = p+".xml";
    return new File(p);
  }

}
