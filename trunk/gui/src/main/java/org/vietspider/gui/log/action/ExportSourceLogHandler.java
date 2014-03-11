/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.log.action;

import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.log.LogDatabaseViewer;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 17, 2009  
 */
public class ExportSourceLogHandler {
  
  private LogDatabaseViewer viewer;
  private String date;
  
  public ExportSourceLogHandler(LogDatabaseViewer _viewer, String _date) {
    this.viewer = _viewer;
    this.date = _date;
    
    Worker excutor = new Worker(new Worker[0]) {

      @Override
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      @Override
      public void before() {
        viewer.removeAllSources();
      }

      @Override
      public void execute() {
        try {
          DataClientHandler.getInstance().exportSourceLogFromDate(date);
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      @Override
      public void after() {   
        new LoadSourceByDateHandler(viewer);
      }
    };

    new ThreadExecutor(excutor, viewer).start();
  }
}
