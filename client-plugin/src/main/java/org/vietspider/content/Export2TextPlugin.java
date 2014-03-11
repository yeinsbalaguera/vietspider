/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import java.io.File;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.PluginClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.DataWriter;
import org.vietspider.common.io.RWData;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 14, 2008  
 */
public class Export2TextPlugin  extends AdminDataPlugin {

  private String label;

  public Export2TextPlugin() {
    label = "Save as text...";
  }

  @Override
  public boolean isValidType(int type) { 
    return type == CONTENT || type == DOMAIN; 
  }

  public String getLabel() { return label; }

  public void invoke(Object...objects) {
    if(!enable || values == null || values.length < 1) return;

    final Control link = (Control) objects[0];
    
    final File file = getFile(values[0], link.getShell());
    
    Worker excutor = new Worker() {
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {}

      public void execute() {
        String plugin = "export.to.text.data.plugin";
        try {
          PluginClientHandler handler = new PluginClientHandler();
          String pageValue = handler.send(plugin, "nlp.renderer",  values[0]);
          if(file == null) return;
          RWData.getInstance().save(file, pageValue.getBytes(Application.CHARSET));
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        } 
      }

      public void after() {  }
    };
    WaitLoading waitLoading =  new WaitLoading(link, excutor, SWT.TITLE);
    waitLoading.open();
  }
  
  private File getFile(String id, Shell shell) {
    if(id.indexOf('.') > -1) return null;
    Preferences prefs = Preferences.userNodeForPackage( getClass());     
    String p = prefs.get("openExportData", "");
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
    if( p != null) prefs.put("openExportData", p);
    if( p == null || p.trim().isEmpty()) return null;    
    if(p.indexOf('.') < 0) p = p+".txt";
    return new File(p);
  }
}