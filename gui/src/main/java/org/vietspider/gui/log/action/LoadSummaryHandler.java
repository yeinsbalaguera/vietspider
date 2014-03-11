/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.log.action;

import java.util.Arrays;
import java.util.Comparator;

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
public class LoadSummaryHandler {

  private LogDatabaseViewer viewer;

  public LoadSummaryHandler(LogDatabaseViewer _viewer) {
    this.viewer = _viewer;
    final String dateFile = viewer.getFolder()+"/summary.txt" ;
    Worker excutor = new Worker(new Worker[0]) {

      private String [] data;

      @Override
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      @Override
      public void before() {
        viewer.removeSummary();
      }

      @Override
      public void execute() {
        try {
          data = DataClientHandler.getInstance().loadSummary(dateFile);
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
          data = new String[]{};
        }
      }

      @Override
      public void after() {   
        if(viewer.isDisposed() || data == null) return;

        Arrays.sort(data, new Comparator<String>() {
          public int compare(String fullName1, String fullName2) {
            if(fullName1 == null || fullName2 == null) return 0;
//            fullName1 = extractName(fullName1);
//            fullName2 = extractName(fullName2);
//            if(fullName1 == null || fullName2 == null) return 0;
            return fullName1.compareTo(fullName2);
          }
          
//          private String extractName(String text) {
//            int start = text.indexOf('|');
//            if(start < 0) return null;
//            String value = text.substring(0, start);
//            start = value.lastIndexOf('.');
//            if(start < 0) return null;
//            return value.substring(start+1);
//          }

        });
         viewer.setSummary(data);
      }
    };

    new ThreadExecutor(excutor, viewer).start();
  }
  

}
