/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import org.eclipse.swt.widgets.Combo;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.SourceInfoControl;
import org.vietspider.model.Source;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 23, 2009  
 */
public class SetSource {
  
  private SourceInfoControl infoControl1;
  private Combo cboGroupType;
  private String category;
  private String name;
  private boolean focus;
  
  public SetSource(SourceInfoControl info, Worker[]plugins,  
      Combo cboGroup, String _category,  String _name, boolean _focus) {
    this.infoControl1 = info;
    this.cboGroupType = cboGroup;
    
    this.category = _category;
    this.name = _name;
    this.focus = _focus;
    
    Worker excutor = new Worker(plugins) {

      private Source source = null;
      private String group = null;
      private String error = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        int idx = cboGroupType.getSelectionIndex();
        if(idx < 0) idx = 0;
        if(idx >= cboGroupType.getItemCount()) return;
        group = cboGroupType.getItem(idx);
      }

      public void execute() {
        try {
          source = new SourcesClientHandler(group).loadSource(category, name);
        }catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(cboGroupType.getShell(), new Exception(error));
          return;
        }
        if(source != null) infoControl1.setSource(source, 0, focus);
      }
    };
    new ThreadExecutor(excutor, cboGroupType).start();
  }

}
