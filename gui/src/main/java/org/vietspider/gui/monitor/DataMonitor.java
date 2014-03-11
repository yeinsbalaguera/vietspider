/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.monitor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Spinner;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.io.CommonFileFilter;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.browser.ControlComponent;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ImageHyperlink;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 25, 2008  
 */
public class DataMonitor extends ControlComponent {
  
  protected Spinner spinPage;
  
  protected ImageHyperlink butGo;
  protected ImageHyperlink butShowDetail;
  
  protected List listDate;
  
  public DataMonitor(Composite parent,  Workspace _workspace) {
    super(parent, _workspace);
  }
  
  public void loadDate(String logFolder, CommonFileFilter filter) {
    loadDate(logFolder, filter, 0);
  }
  
  private void loadDate(final String logFolder,
      final CommonFileFilter filter, final int time) { 
    if(listDate == null) return;
    Worker excutor = new Worker() {

      private String [] data;
      private Exception exp;

      @Override
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      @Override
      public void before() {
        listDate.removeAll();
      }

      @Override
      public void execute() {
        DataClientHandler handler = DataClientHandler.getInstance();
        try {
          if(logFolder != null) {
            data = handler.getDateLogs(logFolder, filter);
          } else {
            data = handler.getDate();
          }
        } catch (Exception e) {
          exp = e;
          ClientLog.getInstance().setMessage(null, e);
          data = new String[]{};
        }
      }

      @Override
      public void after() { 
//        System.out.println(logFolder+ " : "+ data.length + " : "+ filter);
        if(listDate.isDisposed()) return;
        if(exp != null && time < 3) {
          loadDate(logFolder, filter, time+1);
        }
        listDate.setItems(data);   
      }
    };
    
    new ThreadExecutor(excutor, listDate).start();
  }
  
  public String getSelectedDate() {
    int idx = listDate.getSelectionIndex();
    return idx < 0 ? null : listDate.getItem(idx);
  }

  public Spinner getSpinPage() {
    return spinPage;
  }

  public ImageHyperlink getButGo() {
    return butGo;
  }

  public ImageHyperlink getButShowDetail() {
    return butShowDetail;
  }

  public List getListDate() {
    return listDate;
  }

  @Override
  public String getNameIcon() { return "small.monitor.png"; }
  
}
