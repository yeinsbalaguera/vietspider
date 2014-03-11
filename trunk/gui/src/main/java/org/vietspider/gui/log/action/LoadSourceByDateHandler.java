/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.log.action;

import java.util.Arrays;
import java.util.Comparator;

import org.vietspider.chars.TextSpliter;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.io.CommonFileFilter;
import org.vietspider.common.text.NameConverter;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.log.LogDatabaseViewer;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 17, 2009  
 */
public class LoadSourceByDateHandler {

  private LogDatabaseViewer viewer;

  public LoadSourceByDateHandler(LogDatabaseViewer _viewer) {
    this.viewer = _viewer;
    //  }
    //
    //  public LoadSourceByDateHandler(Worker...plugins) {
    final String dateFolder = viewer.getFolder() ;
    Worker excutor = new Worker(new Worker[0]) {

      private String [] data;

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
          CommonFileFilter filter = new CommonFileFilter.LogFile();
          data = DataClientHandler.getInstance().loadSourceByDate(dateFolder, filter);
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
          data = new String[]{};
        }
      }

      @Override
      public void after() {   
        if(viewer.isDisposed() || data == null) return;

        for(int i = 0; i < data.length; i++) {
          if(data[i] == null 
              || data[i].trim().length() < 1
              || data[i].trim().equals("null")) continue;
          data[i] = NameConverter.decode(data[i]);
        }

        Arrays.sort(data, new Comparator<String>() {
          public int compare(String fullName1, String fullName2) {
            if(fullName1 == null || fullName2 == null) return 0;
            TextSpliter spliter = new TextSpliter();
            String [] elements = spliter.toArray(fullName1, '.');
            if(elements.length > 2) fullName1 = elements[2];
            
            elements = spliter.toArray(fullName2, '.');
            if(elements.length > 2) fullName2 = elements[2];
            
            return fullName1.compareTo(fullName2);
          }

        });
        for(int i = 0; i < data.length; i++) {
          if(data[i] == null 
              || data[i].trim().length() < 1
              || data[i].trim().equals("null")) continue;
          viewer.addSource(data[i]);
        }
        
        new LoadSummaryHandler(viewer);
      }
      
    };

    new ThreadExecutor(excutor, viewer).start();
  }

}
