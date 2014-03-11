/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import org.eclipse.swt.widgets.Combo;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.model.Group;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 23, 2009  
 */
public class LoadGroup {
  
  private Combo cboGroupType;
  private Creator creator;
  private String name;

  public LoadGroup(Creator _creator, Combo cbo) {
    this.creator = _creator;
    this.cboGroupType = cbo;
    
    if(cbo.getItemCount() < 1) return;
    int idx = cbo.getSelectionIndex();
    if(idx < 0) idx = 0;
    name  = cbo.getItem(idx);
    
    
    Group group  = creator.getSelectedGroup();
    if(group != null && group.getType().equalsIgnoreCase(name)) return;
    
    Worker excutor = new Worker() {
      
      private  Group[] groups = null;
      private String error;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        SimpleSourceClientHandler handler = new SimpleSourceClientHandler();
        try {
          groups = handler.loadGroups().getGroups();
        } catch(Exception exp) {
          error = exp.toString();
        } 
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(cboGroupType.getShell(), new Exception(error));
          return;
        }
        
        if(groups == null || groups.length < 1) return;

        if(cboGroupType.isDisposed()) return;

        for(int i = 0; i < groups.length; i++) {
          if(groups[i].getType().equals(name)){
            creator.setCachedGroup(groups[i]);
          }
        }
      }
    };
    new ThreadExecutor(excutor, cboGroupType).start();
  }
}
