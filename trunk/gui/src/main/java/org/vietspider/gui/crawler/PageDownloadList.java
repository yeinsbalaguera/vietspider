/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.crawler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableItem;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.CrawlerClientHandler;
import org.vietspider.common.util.RatioWorker;
import org.vietspider.gui.browser.StatusBar;
import org.vietspider.net.server.CrawlerStatus;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ProgressExecutor;

/** 
 * Linux
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 22, 2007  
 */
public class PageDownloadList extends UIPageDownloadList {

  private Color color1 = new Color(table.getDisplay(), 255, 255, 255);
  private Color color2 = new Color(table.getDisplay(), 188, 185, 247);
  
  public PageDownloadList(Composite parent, CrawlerUI uiCrawler, ApplicationFactory factory) {
    super(parent, uiCrawler, factory);
    downloadList = new DownloadListClient();
  }

  void loadSource(int page) { 
    loadSource(page, cboFilter.getText().trim(), page < 0, -1);
  }

  void loadSource(int page, String pattern, boolean update, long sleep) {
    try {
      if(sleep > 0) Thread.sleep(sleep);
    } catch (Exception e) {
    }
    table.removeAll();
    if(page > 0) {
      createUI(page, downloadList);
      return ;
    }

    if(page == -1) {
      page = downloadList.totalPage();
    } else if(page == -2) {
      page = spinPage.getSelection();
    } else page = 1;
    
    LoadSource worker = new LoadSource(pattern, page, update);
    StatusBar statusBar = crawler.getStatusBar();
    statusBar.showProgressBar();
    ProgressExecutor loading = new ProgressExecutor(statusBar.getProgressBar(), worker);
    loading.open();
  }
  
  private void createUI(int page, DownloadListClient downloadListClient) {
    if(downloadListClient.totalPage() < 1) return ;

    if(page > downloadListClient.totalPage()) {
      spinPage.setSelection(1);
      return;
    }
    
    java.util.List<String> sources = downloadListClient.loadPage(page-1);
    
//    String [] items = new String[sources.size()];
    TableItem items [] = new TableItem[sources.size()];
    int index = (page-1)*100; 
//    int size = (page*100)/10;
    for( int i=0; i < sources.size(); i++){
      /*StringBuilder builder = new StringBuilder();
      builder.append(String.valueOf(index+i+1));
      for(int j = 0; j < size; j++) {
        builder.append(' ');
      }
      builder.append('-').append(' ').append(sources.get(i));
      items[i] = builder.toString();*/
      
      items[i] = new TableItem(table, SWT.NONE);      
      items[i].setFont(UIDATA.FONT_8V);
      items[i].setText(0, String.valueOf(index+i+1));
      items[i].setText(1, sources.get(i));
    }
        
  }

  void handleItem(int style) {
    String [] values = getSelectedSource();
    if(values != null && values.length > 2) new HandleItem(values, style);
  }

  void removeSources() {
    MessageBox msg = new MessageBox (table.getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
    ClientRM resource = new ClientRM("Crawler");
    msg.setMessage(resource.getLabel("org.vietspider.gui.crawler.Crawler.msgAlertRemoveSources"));
    if(msg.open() != SWT.YES) return ;  
    
    final TableItem [] selectedItems = table.getSelection();
    if(selectedItems != null && selectedItems.length > 0) new RemoveSources(selectedItems);
  }

  protected String [] getSelectedSource() {
    TableItem [] selected = table.getSelection();
    if(selected == null || selected.length < 1) return null;
    String [] elements  = selected[0].getText(1).split("\\.");
    if(elements.length < 3) return null;
    return new String[] {elements[1], elements[2], elements[0]};
  }

  void showCrawling(CrawlerStatus status) {
    if(table.isDisposed()) return;
    if(status == null) return;
    String [] elements = new String[status.getSources().size()];
    for(int i = 0; i < elements.length; i++) {
      elements [i] = status.getSources().get(i);
    }

    TableItem [] items = table.getItems();
    for(TableItem item : items) {
      if(exists(elements, item.getText(1))) {
        item.setBackground(color2);
      } else {
        item.setBackground(color1);
      }
    }
  }
  
/*  String[] getSelectedText() {
    String [] elements = table.getSelection();
    String [] values = new String[elements.length];
    for(int i = 0; i < elements.length; i++) {
      int idx = elements[i].indexOf('-');
      if(idx < 0) continue;
      values[i] = elements[i].substring(idx).trim();
    }
    
    return values;
  }*/

  private boolean exists(String [] elements, String value) {
    for(String ele : elements) {
      if(ele.equals(value)) return true;
    }
    return false;
  }
  
  private class HandleItem extends BackgroupLoader {
    
    private CrawlerStatus crawlStatus;
    
    private HandleItem(String [] values, int style) {
      super((Crawler)crawler, table);
      
      crawlStatus = new CrawlerStatus(style);
      crawlStatus.addSource(values[2]+"."+values[0]+"."+values[1]);
    }
    
    public void finish() {
    }

    public void load() throws Exception {
      new CrawlerClientHandler().getCrawlerStatus(crawlStatus);
    }
  }

  private class LoadSource extends RatioWorker {

    private String pattern;
    protected  String error;
    private int selectedPage;
    private boolean update = true;
    
    private LoadSource(String pattern, int page, boolean update) {
      this.pattern = pattern;
      this.selectedPage = page;
      this.update = update;
    }
    
    public void abort() {
      ClientConnector2.currentInstance().abort();
    }
    
    public void before() {
    }
    
    public void execute() {
      int time = 0;
      while(time < 3) {
        try {
          downloadList.update(pattern, update);
          error = null;
          break;
        } catch (Exception e) {
          //        e.printStackTrace();
          error = e.getMessage();
          if(error == null || error.trim().isEmpty()) error = e.toString();
        }
        time++;
      }
    }
    
    public void after() {    	
      if(spinPage.isDisposed()) return;
      
      if(error != null) {
        crawler.getStatusBar().setError(error);       
      } else {
        crawler.getStatusBar().setMessage("");
      }
      
      ClientRM resource = new ClientRM("Crawler");
      int total = downloadList.totalPage();
      if(total > 1) {
        spinPage.setEnabled(true);
        spinPage.setMinimum(1);
        spinPage.setSelection(selectedPage);
        spinPage.setMaximum(total);
        spinPage.setToolTipText(String.valueOf(total) +" "+ resource.getLabel("page"));
      } else {
        spinPage.setEnabled(false);
        spinPage.setSelection(1);
      }
      
      if(spinPage.getSelection()  == 1) {
    	  String os_name = System.getProperty("os.name").toLowerCase();
    	  if(os_name.indexOf("linux") > -1) createUI(1, downloadList);
      }
    }

  }

  private class RemoveSources extends PrivateLoader {

    private StringBuilder builder = new StringBuilder();
    
    private RemoveSources(TableItem [] selectedItems) {
      super(cboFilter, crawler.getStatusBar());
      for(TableItem item : selectedItems) {
        if(item.getText(1).trim().isEmpty()) continue;
        if(builder.length() > 0) builder.append('\n');
        builder.append(item.getText(1));
      }
    }
    
    public void finish() {
      loadSource(-2, cboFilter.getText().trim(), true, 2000);
    }

    public void load() throws Exception {
      if(builder.length() < 1) return;
      new CrawlerClientHandler().removeCrawlSource(builder.toString().trim());
    }
  }

  public DownloadListClient getDownloadList() { return downloadList; }

}
