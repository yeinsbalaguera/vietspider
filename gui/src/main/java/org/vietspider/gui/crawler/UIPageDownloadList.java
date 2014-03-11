/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.crawler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.vietspider.client.common.CrawlerClientHandler;
import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.browser.TabBrowser;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.source.SourceSearcher2;
import org.vietspider.gui.source.SourcesViewer;
import org.vietspider.gui.source.SourcesViewer.AddSourceListener;
import org.vietspider.gui.source.SourcesViewer.CrawlSourceEvent;
import org.vietspider.model.Group;
import org.vietspider.net.server.CrawlerStatus;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 22, 2007  
 */
public abstract class UIPageDownloadList {

  protected CrawlerUI crawler;

  protected Table table;

  protected Combo cboFilter;
  protected Spinner spinPage;

  protected DownloadListClient downloadList;

  public UIPageDownloadList(Composite composite, CrawlerUI uiCrawler, ApplicationFactory factory) {
    this.crawler = uiCrawler;

    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 1;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 2;
    composite.setLayout(gridLayout); 

    factory.setComposite(composite);

    cboFilter = factory.createCombo(SWT.BORDER);
    cboFilter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    cboFilter.addKeyListener( new KeyAdapter(){
      public void keyPressed(KeyEvent e){       
        if(e.keyCode == SWT.CR) loadSource(0, cboFilter.getText().trim(), false, -1);
      }
    });
    cboFilter.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt){
        loadSource(0, cboFilter.getText().trim(), false, -1);
      }      
    });

    spinPage = factory.createSpinner(SWT.BORDER);
    spinPage.setCursor(new Cursor(spinPage.getDisplay(), SWT.CURSOR_HAND));
    spinPage.setIncrement(1);
    spinPage.addModifyListener(new ModifyListener() {
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent event) {
        loadSource(spinPage.getSelection(), cboFilter.getText().trim(), true, -1);
      }
    });

    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
//    table  = factory.createList(SWT.BORDER | SWT.MULTI, new String[0]);
//    table.setFont(UIDATA.FONT_8V);
    table = factory.createTable("tableSource",  null, SWT.FULL_SELECTION | SWT.MULTI);  
    table.setLinesVisible(true);
    table.setHeaderVisible(true);
    table.setLayoutData(gridData);
//    table.addMouseListener(new MouseAdapter(){
//      @SuppressWarnings("unused")
//      public void mouseDoubleClick(MouseEvent e) {
////        crawler.showData(getSelectedSource());
//      }
//    });
    //    table.addSelectionListener(new SelectionAdapter(){
    //      @SuppressWarnings("unused")
    //      public void widgetSelected(SelectionEvent evt) {
    //        crawler.showData(getSelectedSource());
    //      }      
    //    });
    //    table.setFont(UIDATA.FONT_10B);
    
    Object menu = null;
//    if(XPWidgetTheme.isPlatform()) {  
//      PopupMenu popupMenu = new PopupMenu(table, XPWidgetTheme.THEME);
//      menu = new CMenu();
//      popupMenu.setMenu((CMenu)menu);
//    } else {
      menu = new Menu(factory.getComposite().getShell(), SWT.POP_UP);
      table.setMenu((Menu)menu);
//    }

    factory.createStyleMenuItem(menu, "menuCrawlItem", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        handleItem(CrawlerStatus.GO_TO_ITEM);
      }
    });
    

    factory.createStyleMenuItem(menu, "menuEditSource", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        editSource();
      }   
    });

    factory.createStyleMenuItem(menu, "menuCrawlItemWithRedown", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        MessageBox msg = new MessageBox (table.getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
        ClientRM resource = new ClientRM("Crawler");
        msg.setMessage(resource.getLabel("crawler.confirm.redownload"));
        if(msg.open() != SWT.YES) return ;  
        handleItem(CrawlerStatus.GO_TO_ITEM_WITH_REDOWNLOAD);
      }
    });

    factory.createStyleMenuItem(menu, SWT.SEPARATOR);

    
    factory.createStyleMenuItem(menu, "menuAdd", "+.gif", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        addSources();
      }   
    });
    

    factory.createStyleMenuItem(menu, "menuRemove", "-.gif", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeSources(); 
      }   
    });

    factory.createStyleMenuItem(menu, "menuClear", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        MessageBox msg = new MessageBox (table.getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
        ClientRM resource = new ClientRM("Crawler");
        msg.setMessage(resource.getLabel("org.vietspider.gui.crawler.Crawler.msgAlertClearSources"));
        if(msg.open() != SWT.YES) return ;  
        new ClearDownloadList();
      }   
    });

    factory.createStyleMenuItem(menu, SWT.SEPARATOR);

    factory.createStyleMenuItem(menu, "menuStopItem", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        MessageBox msg = new MessageBox (table.getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
        ClientRM resource = new ClientRM("Crawler");
        msg.setMessage(resource.getLabel("org.vietspider.gui.crawler.Crawler.msgAlertStopItem"));
        if(msg.open() != SWT.YES) return ;
        handleItem(CrawlerStatus.STOP_ITEM);
      }
    });

    factory.createStyleMenuItem(menu, SWT.SEPARATOR);

    factory.createStyleMenuItem(menu, "menuUpdate", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        loadSource(0, cboFilter.getText().trim(), true, -1);
      }   
    });

    /*factory.createStyleMenuItem(menu, "menuViewPool", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ViewPool();
      }   
    });*/

    new LoadGroups();
  }


  protected void addSources() {
    ApplicationFactory factory = new ApplicationFactory(cboFilter, "Crawler", getClass().getName());

    final Shell shell = new Shell(table.getShell(),SWT.TITLE);
    shell.setLocation(table.getShell().getLocation().x+430, table.getShell().getLocation().y+100); 
    shell.setLayout(new FillLayout());

    AddSourceListener addSourceListener = new AddSourceListener(){
      public void add(CrawlSourceEvent addEvent){
        String value = addEvent.getValue();
        if("*".equals(value)) {
          Runnable timer2 = new Runnable () {
            public void run () {
              loadSource(-1, cboFilter.getText().trim(), false, 500);
              if(shell.isDisposed()) return;
            }
          };
          shell.getDisplay().timerExec (5*1000, timer2);
          return;
        }
        try {
          downloadList.append(value);
          loadSource(-1, cboFilter.getText().trim(), false, 500);
        } catch (Exception e) {
          loadSource(-1, cboFilter.getText().trim(), true, 5*1000);
        }
      }
    };
    
    AddSourceListener addSourceListener2 = new AddSourceListener(){
      public void add(CrawlSourceEvent addEvent){
        String value = addEvent.getValue();
        try {
          downloadList.append(value);
          loadSource(-1, cboFilter.getText().trim(), false, 500);
        } catch (Exception e) {
          loadSource(-1, cboFilter.getText().trim(), true, 5*1000);
        }
      }
    };
    
    /*if(XPWindowTheme.isPlatform()) {
      CTabFolder tabFolder = new CTabFolder(shell, SWT.MULTI);
      XPWindowTheme.setTabBrowserTheme(tabFolder);
      tabFolder.setCursor(new Cursor(shell.getDisplay(), SWT.CURSOR_HAND));
      tabFolder.setFont(UIDATA.FONT_10B);
      
      SourcesViewer explorer = new SourcesViewer(tabFolder);
      CTabItem item = new CTabItem(tabFolder,SWT.BOLD);
      item.setText("  " +factory.getLabel("tabfolder.add") + "  ");
      item.setControl(explorer);
      explorer.addAddListener(addSourceListener);
      tabFolder.setSelection(item);
      
      SourceSearcher2 searcher = new SourceSearcher2(tabFolder);
      CTabItem item2 = new CTabItem(tabFolder,SWT.BOLD);
      item2.setText("  " +factory.getLabel("tabfolder.search") + "  ");
      item2.setControl(searcher);
      searcher.addAddListener(addSourceListener2);
      
    } else {*/
    TabFolder tabFolder = new TabFolder(shell, SWT.BOLD);
    tabFolder.setCursor(new Cursor(shell.getDisplay(), SWT.CURSOR_HAND));
    tabFolder.setFont(UIDATA.FONT_10B);
    //factory.setComposite(tabfolder);
    SourcesViewer explorer = new SourcesViewer(tabFolder);
    TabItem item = new TabItem(tabFolder,SWT.BOLD);
    item.setText("  " + factory.getLabel("tabfolder.add") + "  ");
    item.setControl(explorer);
    explorer.addAddListener(addSourceListener);
    tabFolder.setSelection(item);

    SourceSearcher2 searcher = new SourceSearcher2(tabFolder);
    TabItem item2 = new TabItem(tabFolder,SWT.BOLD);
    item2.setText("  " + factory.getLabel("tabfolder.search") + "  ");
    item2.setControl(searcher);
    searcher.addAddListener(addSourceListener2);
//    }
   
    
    shell.setSize(400,420);
//    XPWindowTheme.setWin32Theme(shell);
    shell.setVisible(true);
  }


  abstract void loadSource(int page, String pattern, boolean update, long sleep);

  abstract void handleItem(int style);

  abstract void removeSources(); 

  abstract String [] getSelectedSource(); 

  abstract void showCrawling(CrawlerStatus crawlStatus); 

  private void editSource() {
    if(crawler.getWorkspace() == null) return;
    String [] values =  getSelectedSource();
    if(values == null) return ;
    try{
      TabBrowser tab = crawler.getWorkspace().getTab();
      Creator creator = (Creator)tab.createTool(Creator.class, true, SWT.CLOSE);
      creator.selectData(new Worker[0], values[0], values[1], values[2]);
    } catch (Exception e) {
      ClientLog.getInstance().setException(crawler.getWorkspace().getShell(), e);
    }
  }

  private class LoadGroups extends BackgroupLoader {

    private  Group [] groups = null;

    private LoadGroups() {
      super((Crawler)crawler, table);
    }

    public void finish() {
      if(groups == null || cboFilter.isDisposed()) return;
      for(Group group : groups) {
        if(group.getType().equals(Group.DUSTBIN)) continue;
        cboFilter.add("."+group.getType()+".");
      }
    }

    public void load() throws Exception {
      groups = new SimpleSourceClientHandler().loadGroups().getGroups();
    }
  }

  private class ClearDownloadList extends BackgroupLoader {

    private ClearDownloadList() {
      super((Crawler)crawler, table);
    }

    public void finish() {
      table.removeAll();
      loadSource(0, cboFilter.getText().trim(), true, 2000);
    }

    public void load() throws Exception {
      new CrawlerClientHandler().removeCrawlSource("*");
    }
  }

 /* private class ViewPool extends BackgroupLoader {

    String value;
    private InformationViewer viewer; 

    private ViewPool() {
      super((Crawler)crawler, table);
    }

    public void finish() {
      if(table == null || table.isDisposed()) return;
      ClientRM resources = new ClientRM("Crawler");
      String [] buttons = {
          resources.getLabel("pool.clear.queue"),
          resources.getLabel("pool.update")
      };

      SelectionAdapter [] adapters = {
          new SelectionAdapter() {
            @SuppressWarnings("unused")
            public void widgetSelected(SelectionEvent evt) {
              Properties properties_ = ClientProperties.getInstance().getProperties();
              ClientRM resources_ = new ClientRM("Crawler");
              int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
              MessageBox msg = new MessageBox (table.getShell(), style);
              msg.setMessage(resources_.getLabel("pool.clear.queue.confirm"));
              if(msg.open() != SWT.YES) return ;
              new ClearPoolQueue();
            }
          },
          new SelectionAdapter(){
            @SuppressWarnings("unused")
            public void widgetSelected(SelectionEvent evt) {
              new ThreadExecutor(ViewPool.this, table).start();
            }
          } 
      };
      if(viewer == null) {
        viewer = new InformationViewer(table.getShell(), value, buttons,  adapters);
      } else {
        viewer.setValue(value);
      }
    }

    public void load() throws Exception {
      value =  new CrawlerClientHandler().viewPool();
    }
  }*/

 /* private class ClearPoolQueue extends BackgroupLoader {

    private ClearPoolQueue() {
      super((Crawler)crawler, table);
    }

    public void finish() {
    }

    public void load() throws Exception {
      new CrawlerClientHandler().clearPoolQueue();
    }
  }*/

}
