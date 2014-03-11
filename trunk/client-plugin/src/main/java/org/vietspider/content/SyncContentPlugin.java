package org.vietspider.content;

import org.eclipse.swt.widgets.Control;
import org.vietspider.bean.sync.SyncDatabaseConfig;
import org.vietspider.client.ClientPlugin;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.PluginClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.content.sync.SyncDatabaseSetting;
import org.vietspider.serialize.Object2XML;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.waiter.ThreadExecutor;
import org.vietspider.ui.widget.waiter.WaitLoading;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 13, 2008  
 */
public class SyncContentPlugin extends ClientPlugin {

  protected String label;
  protected SyncDatabaseSetting syncDatabaseSetting;

  public SyncContentPlugin() {
    ClientRM resources = new ClientRM("Plugin");
    Class<?>  clazz = SyncContentPlugin.class;
    label = resources.getLabel(clazz.getName()+".itemSyncContent");
  }
  
  @Override
  public boolean isValidType(int type) { return type == CONTENT; }
  
  public String getConfirmMessage() { return null; }

  public String getLabel() { return label; }

  public void invoke(Object...objects) {
    if(!enable || values == null || values.length < 1) return;
    
    final String metaId = values[0];
    final Control link = (Control) objects[0];

    Worker excutor = new Worker() {

      private String error = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          PluginClientHandler handler = new PluginClientHandler();
          handler.send("sync.content.to.database", "save", metaId);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(link.getShell(), new Exception(error));
          return;
        } 
        ClientRM resources = new ClientRM("Plugin");
        String key = SyncContentPlugin.class.getName() + ".sync.content.successfull";
        String message = resources.getLabel(key);
        ClientLog.getInstance().setMessage(link.getShell(), new Exception(message));
      }
    };
    new ThreadExecutor(excutor, link).start();
  }

  public void syncEnable(Object... objects) {
    final Control link = (Control) objects[0];
    Worker excutor = new Worker() {

      private String error = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          PluginClientHandler handler = new PluginClientHandler();
          String value = handler.send("sync.content.to.database", "check", (String)null);
          enable = "true".equals(value);
        }catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(link.getShell(), new Exception(error));
          return;
        }
        /*if(!enable) */link.setEnabled(enable);
      }
    };
    new ThreadExecutor(excutor, link).start();
  }
  
  public boolean isSetup() { return true; }
  
  public void invokeSetup(Object...objects) {
    final Control link = (Control) objects[0];
    if(syncDatabaseSetting == null) {
      syncDatabaseSetting = new SyncDatabaseSetting(link.getShell());
    } else {
      syncDatabaseSetting.setVisible(true);
    }
  }
  
  public void resetSetup() { 
    syncDatabaseSetting = null;
  }
  
  public void saveSetup(Object...objects) { 
    final Control link = (Control) objects[0];
    if(link == null || link.isDisposed()) return;
    Worker excutor = new Worker() {

      private SyncDatabaseConfig syncDatabaseConfig;
      
      private StringBuilder error = new StringBuilder();

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        if(syncDatabaseSetting == null) return;
        syncDatabaseConfig = syncDatabaseSetting.getConfig();
      }

      public void execute() {
        if(syncDatabaseConfig == null) return;
        try {
          PluginClientHandler handler = new PluginClientHandler();
          String xml = Object2XML.getInstance().toXMLDocument(syncDatabaseConfig).getTextValue();
          handler.send("sync.content.to.database", "setup", xml);
        } catch (Exception e) {
          error.append('\n').append(e.toString());
        }
      }

      public void after() {
        if(error.length() > 0) {
          Exception exp = new Exception(error.toString());
          ClientLog.getInstance().setMessage(link.getShell(), exp);
        }
      }
    };
    WaitLoading loading = new WaitLoading(link, excutor);
    loading.open();
  }
  

}
