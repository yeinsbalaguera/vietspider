/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.crawler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.vietspider.client.common.CrawlerClientHandler;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.net.server.CrawlerStatus;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.services.ImageLoader;
import org.vietspider.ui.widget.InformationViewer;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 2, 2006
 */
public class Crawler extends CrawlerUI {

//  private List<String> data;
  private LoadStatus loadStatus;

  public Crawler(Composite parent, Workspace workspace)  throws Exception {
    super(parent, workspace);
  }

  void startOrStop() {
    if(workspace != null) new StartStop();
  }

//  void showData(String [] values){
//    tableData.removeAll();  
//    if(values == null || values.length < 3) return;
//    //    Calendar calendar = Calendar.getInstance();
//    //    String date = CalendarUtils.getDateFormat().format(calendar.getTime());
//    String date = DataOfDay.getDateValue();
//    showData(date, values[0], values[1], values[2]);    
//  }

//  private void showData(String date, String group, String category, String source) {
//    setSelectedItem(getTabItemCount() == 3 ? 1 : 0);
//    new ShowData(date, group, category, source);
//  }   
//
//  void viewContent() {
//    int idx = tableData.getSelectionIndex();
//    if(data == null || data.size() < 1 || idx < 0 || idx >= data.size()) return;
//    if(workspace == null) return;    
//    StringBuilder builder = new StringBuilder();
//    ClientConnector2 connector = ClientConnector2.currentInstance(); 
//    builder.append(connector.getRemoteURL()).append('/');
//    builder.append(connector.getApplication()).append("/DETAIL/").append(data.get(idx));
//    workspace.getTab().setUrl(builder.toString());    
//  }

  public void loadStatus() {
    if(loadStatus == null) {
      loadStatus = new LoadStatus();
      return;
    }
    loadStatus.newSession();
  }

//  void deleteMeta() {
//    int [] idx = tableData.getSelectionIndices();
//    if(data == null || data.size() < 1 || idx == null || idx.length < 1) return;
//    MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
//
//    ClientRM resource = new ClientRM("Crawler");
//
//    String mes;
//    if(idx.length == 1) {
//      mes = resource.getLabel(getClass().getName()+".msgAlertDeleteMeta")+" \""+tableData.getItem(idx[0]).getText(1) +"\"?";
//    } else {
//      mes = resource.getLabel(getClass().getName()+".msgAlertDeleteMetas") ;
//    }
//    msg.setMessage(mes);
//    if(msg.open() != SWT.YES) return ; 
//    StringBuilder builder = new StringBuilder();
//    for(int i : idx) {
//      builder.append(data.get(i)).append('\n');
//    }
//    new DeleteData(builder);
//  }

  private boolean running = false;

  private void showButton(boolean value) {
    if(butDownload.isDisposed()) return;
//    if(!butDownload.isEnabled())  butDownload.setEnabled(true);
    if(running == value) return;
    this.running = value;
    ImageLoader imgLoad = new ImageLoader();
    ClientRM resource = new ClientRM("Crawler");
    if(running) {
      butDownload.setImage(imgLoad.load(butDownload.getDisplay(), "butStop.png"));
      butDownload.setText(resource.getLabel(getClass().getName()+".butStop"));
    } else {
      butDownload.setImage(imgLoad.load(butDownload.getDisplay(), "butDownload.png"));
      butDownload.setText(resource.getLabel(getClass().getName()+ ".butDownload"));
    }
    butDownload.pack();
  }

  void viewExecutor(int index) {
    new ViewExecutor(index);
  }

  private class ViewExecutor extends BackgroupLoader {

    int idx;
    String value;
    private InformationViewer viewer; 

    private ViewExecutor(int i) {
      super(Crawler.this, butDownload);
      idx = i;
    }

    public void finish() {
      if(butDownload == null || butDownload.isDisposed()) return;
      ClientRM resources = new ClientRM("Crawler");
      String [] buttons = {
          resources.getLabel("abort.executor"),
          resources.getLabel("pool.update")
      };
      SelectionAdapter [] adapters = {
          new SelectionAdapter(){
            @SuppressWarnings("unused")
            public void widgetSelected(SelectionEvent evt) {
              ClientRM resources_ = new ClientRM("Crawler");
              int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
              MessageBox msg = new MessageBox (butDownload.getShell(), style);
              msg.setMessage(resources_.getLabel("abort.executor.confirm"));
              if(msg.open() != SWT.YES) return ;
              new AbortExecutor(idx);
            }
          },
          new SelectionAdapter(){
            @SuppressWarnings("unused")
            public void widgetSelected(SelectionEvent evt) {
              new ThreadExecutor(ViewExecutor.this, butDownload).start();
            }
          }     
      };
      if(viewer == null) {
        viewer = new InformationViewer(butDownload.getShell(), value, buttons,  adapters);
      } else {
        viewer.setValue(value);
      }
    }

    public void load() throws Exception {
      value =  new CrawlerClientHandler().viewExecutor(idx);
    }
  }

  private class AbortExecutor extends BackgroupLoader {

    int idx;

    private AbortExecutor(int i) {
      super(Crawler.this, butDownload);
      idx = i;
    }

    public void finish() {
    }

    public void load() throws Exception {
      new CrawlerClientHandler().abortExecutor(idx);
    }
  }

  private class LoadStatus extends BackgroupLoader {

    private CrawlerStatus crawlStatus = null;

    private boolean finished = false;

    private LoadStatus() {
      super(Crawler.this, Crawler.this);
    }

    public void newSession () {
      if(!finished) return;
      finished = false;
      new ThreadExecutor(this, widget).start();
    }

    public void finish() {
      finished = true;
      //      try {
      if(crawlStatus == null) return;
      showButton(crawlStatus.getStatus() == CrawlerStatus.RUNNING);

      String [] status = crawlStatus.getThreadStatus();
      if(status == null) return;

      for(int i = 0; i < Math.min(status.length, lbl.length); i++) {
        if(lbl[i] == null || lbl[i].isDisposed()) continue;
        if(status[i] == null) status[i] = "";
        lbl[i].setText(status[i]);
      }  

      sourceList.showCrawling(crawlStatus);  
      //      } catch (Throwable e) {
      //        ClientLog.getInstance().setThrowable(getShell(), e);
      //      }
    }

    public void load() throws Exception {
      crawlStatus = new CrawlerClientHandler().getCrawlerStatus(CrawlerStatus.RUNNING);
    }
  }

//  private class ShowData extends BackgroupLoader {
//
//    private List<Meta> list = null;
//    private Domain domain = null ;
//
//    private ShowData(String date, String group, String category, String source) {
//      super(Crawler.this, butDownload);
//      domain = new Domain(date, group, category, source);
//    }
//
//    public void finish() {
//      if(tableData.isDisposed()) return;
//      tableData.removeAll();
//      if(list == null) list = new ArrayList<Meta>(0);
//      TableItem items [] = new TableItem[list.size()];
//      Crawler.this.data = new ArrayList<String>(list.size());
//
//      for( int i=0; i < list.size(); i++){      
//        data.add(list.get(i).getId());
//        items[i] = new TableItem(tableData, SWT.NONE);
//        items[i].setText(0, String.valueOf(list.size() - i));
//        String time =  list.get(i).getTime();
//        if(time == null) continue;
//        items[i].setText(1, list.get(i).getTitle());
//        if(time.indexOf(' ') > -1) time = time.substring(time.indexOf(' '));
//        items[i].setText(2, time);
//      }  
//    }
//
//    public void load() throws Exception {
//      list = new CrawlerClientHandler().getMetas(domain);
//    }
//  }

  private class StartStop extends BackgroupLoader {

    private StartStop() {
      super(Crawler.this, butDownload);
    }

    public void before() {
      //butDownload.setEnabled(false);
    }

    public void finish() {}

    public void load() throws Exception {
      new CrawlerClientHandler().mornitorCrawler(CrawlerStatus.START_OR_STOP);
    }
  }

//  private class DeleteData extends BackgroupLoader {
//
//    private StringBuilder builder;
//
//    private DeleteData(StringBuilder builder) {
//      super(Crawler.this, butDownload);
//      this.builder = builder;
//    }
//
//    public void finish() {
//      showData(sourceList.getSelectedSource());
//    }
//
//    public void load() throws Exception {
//      try {
//        PluginClientHandler handler = new PluginClientHandler();
//        handler.send("delete.data.plugin", "delete.article", builder.toString());
//      } catch (Exception e) {
//        error = e.toString();
//      }
//      //      new CrawlerClientHandler().deleteMetas(builder);
//    }
//  }

  @Override
  public String getNameIcon() { return "small.crawler.png"; }

  
}
