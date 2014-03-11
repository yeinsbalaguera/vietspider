/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import java.io.File;
import java.util.prefs.Preferences;

import org.eclipse.swt.widgets.Combo;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.OrganizationClientHandler;
import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.source.SourcesExplorer;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;
import org.vietspider.user.AccessChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 23, 2009  
 */
public class LoadGroups {
  
  private Combo cboGroupType;
  private Creator creator;

  public LoadGroups(Creator _creator, Combo cbo) {
    this.creator = _creator;
    this.cboGroupType = cbo;
    
    Worker excutor = new Worker() {
      private String error = null;
      private String [] groups = null;
      private AccessChecker accessChecker;
      private File file; 

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
          ClientLog.getInstance().setMessage(cboGroupType.getShell(), new Exception(error));
          return;
        }

        if(cboGroupType.isDisposed()) return;

        setGroups(groups, accessChecker, file);
        
        creator.selectData(new Worker[0], null, null);

      /*  for(int i = 0; i < cboGroupType.getItemCount(); i++) {
          if(cboGroupType.getItem(i).equals(selectedGroup)) {
            cboGroupType.select(i);
            selectGroupType();
            break;
          }
        }*/

      }
    };
    new ThreadExecutor(excutor, cboGroupType).start();
  }
  
  private synchronized void setGroups(String [] groups, AccessChecker accessChecker, File file) {
//    System.err.println(" ====  >"+ selectedGroup + " / "+ selectedCategory + " / "+ selectedSource);
//    new Exception().printStackTrace();
    if(groups == null || groups.length < 1 || cboGroupType.isDisposed()) return;
    cboGroupType.removeAll();
    if(accessChecker != null) {
      StringBuilder builder = new StringBuilder();
      for(String group : groups) {
        if(!accessChecker.isPermitGroup(group)) continue;
        cboGroupType.add(group);
        if(builder.length() > 0) builder.append('\n');
        builder.append(group);
      }
      try {
        byte [] bytes = builder.toString().getBytes(Application.CHARSET);
        RWData.getInstance().save(file, bytes);
      } catch (Exception e) {
        file.delete();
      }
    } else {
      for(String group : groups) {
        cboGroupType.add(group);
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
    if(selectedIndex >= cboGroupType.getItemCount()) {
      if(cboGroupType.getItemCount() > 0) cboGroupType.select(0);
      return;
    }
    cboGroupType.select(selectedIndex);
  }
}
