/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.gui.wizard;

import java.io.File;

import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.OrganizationClientHandler;
import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.util.Worker;
import org.vietspider.user.AccessChecker;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jan 2, 2012
 */
public class LoadGroup extends Worker {

  private AccessChecker accessChecker;
  private String error = null;
  private String [] groups = null;
  private File file; 
  private Handler handler;

  public LoadGroup(Handler handler, Worker...plugins) {
    super(plugins);
    this.handler = handler;
  }

  public void abort() {
    ClientConnector2.currentInstance().abort();
  }

  public void before() {
    file = new File(ClientConnector2.getCacheFolder("sources/type"), "groups");
  }

  public void execute() {
    try {
      if(file.exists()) {
        byte [] bytes = RWData.getInstance().load(file);
        groups = new String(bytes, Application.CHARSET).trim().split("\n");
      } else {
        groups = new SimpleSourceClientHandler().loadGroups().getNames();
        accessChecker = new OrganizationClientHandler().loadAccessChecker();
      }
    } catch(Exception exp) {
      error = exp.toString();
    } 
  }


  public void after() {
    if(error != null && !error.isEmpty()) {
      handler.showError(error);
      return;
    }
    handler.handle(accessChecker, groups, file);
  }

  /*private void setGroups(AccessChecker accessChecker, File file) {
    //    System.err.println(" ====  >"+ selectedGroup + " / "+ selectedCategory + " / "+ selectedSource);
    //    new Exception().printStackTrace();
    if(groups == null || groups.length < 1 || groupCombo.isDisposed()) return;
    groupCombo.removeAll();
    if(accessChecker != null) {
      StringBuilder builder = new StringBuilder();
      for(String _group : groups) {
        if(!accessChecker.isPermitGroup(_group)) continue;
        groupCombo.add(_group);
        if(builder.length() > 0) builder.append('\n');
        builder.append(_group);
      }
      try {
        byte [] bytes = builder.toString().getBytes(Application.CHARSET);
        org.vietspider.common.io.RWData.getInstance().save(file, bytes);
      } catch (Exception e) {
        file.delete();
      }
    } else {
      for(String _group : groups) {
        groupCombo.add(_group);
      } 
    }

    int selectedIndex = 0;
    try {
      Preferences prefs = Preferences.userNodeForPackage(SourcesExplorer.class);
      selectedIndex = Integer.parseInt(prefs.get("selectedGroup", ""));
    } catch (Exception e) {
      selectedIndex = 0;
    }

    if(selectedIndex < 0) selectedIndex = 0;
    if(selectedIndex >= groupCombo.getItemCount()) {
      if(groupCombo.getItemCount() > 0) groupCombo.select(0);
      return;
    }
    groupCombo.select(selectedIndex);
  }*/
  
  static interface Handler {
    
    public void showError(String error);
    public void handle(AccessChecker accessChecker, String [] groups, File file);
    
  }
}
