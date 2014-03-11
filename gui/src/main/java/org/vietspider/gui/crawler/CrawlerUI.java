/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.crawler;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.vietspider.ClientProperties;
import org.vietspider.client.common.CrawlerClientHandler;
import org.vietspider.gui.browser.ControlComponent;
import org.vietspider.gui.browser.StatusBar;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.net.server.CrawlerStatus;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 2, 2006
 */
abstract class CrawlerUI extends ControlComponent {

//  protected Table tableData;
  protected Label [] lbl;
  protected Button butDownload;
  protected Composite compLeftSash, comRightSash;
  protected PageDownloadList sourceList;
  //  protected Section section;
  protected String downloadingTitle;
  //  protected LiveSashForm liveSashForm;

  private ScrolledComposite scrolled;
  private Composite scrolledComposite;
  protected Text txtInformation;

  protected StatusBar statusBar;

//  protected Composite tab;
  
  protected int refreshStatus = 0;

  public CrawlerUI(Composite parent, Workspace workspace) throws Exception {
    super(parent, workspace);

    this.workspace.setCrawler((Crawler)this);

    ApplicationFactory factory = new ApplicationFactory(this, "Crawler", getClass().getName());

    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 1;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 2;
    setLayout(gridLayout);

    GridData gridData;

    //    liveSashForm = new LiveSashForm(this, SWT.VERTICAL);
    //    gridData = new GridData(GridData.FILL_BOTH);
    //    liveSashForm.setLayoutData(gridData);

    Composite mainComposite = new Composite(this, SWT.NONE);
    mainComposite.setLayout(gridLayout);   
    factory.setComposite(mainComposite);
    gridData = new GridData(GridData.FILL_BOTH);
    mainComposite.setLayoutData(gridData);

    SashForm sashMain = new SashForm(mainComposite, SWT.HORIZONTAL);
    sashMain.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    gridData = new GridData(GridData.FILL_BOTH);    
    gridData.grabExcessHorizontalSpace = true;
    sashMain.setLayoutData(gridData);

    Composite composite = new Composite(sashMain, SWT.NONE);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));

    sourceList = new PageDownloadList(composite, this, factory);
    //    sourceList.setShowType(cboShowType.getSelectionIndex());

    int totalThread = 3;
    try {
      CrawlerStatus crawlStatus = new CrawlerClientHandler().getCrawlerStatus(CrawlerStatus.RUNNING);
      totalThread = crawlStatus.getTotalThread();
    } catch (Exception e) {
      totalThread = 1;
    }

//    if(XPWindowTheme.isPlatform()) {
//      tab = new CTabFolder(sashMain, SWT.MULTI);
//      CTabFolder tabFolder = (CTabFolder)tab;
//      XPWindowTheme.setTabBrowserTheme(tabFolder);
//      tab.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
//    } else {
//      tab = new TabFolder(sashMain, SWT.TOP);
//      tab.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
//    }
//    tab.setBackground(new Color(getDisplay(), 255, 255, 255));
//    tab.setFont(UIDATA.FONT_10);
//    gridData = new GridData(GridData.FILL_BOTH);     
//    tab.setLayoutData(gridData);

//    factory.setComposite(tab);
//    tableData = factory.createTable("tableData",  null, SWT.FULL_SELECTION | SWT.MULTI);  
//    tableData.setLinesVisible(true);
//    tableData.setHeaderVisible(true); 
//    tableData.addMouseListener(new MouseAdapter(){
//      @SuppressWarnings("unused")
//      public void mouseDoubleClick(MouseEvent e){
//        viewContent();
//      }      
//    });

    if(totalThread >= 10) {
      TabFolder tab = new TabFolder(sashMain, SWT.TOP);
      tab.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
      
      TabItem item = new TabItem(tab, SWT.NONE);
      item.setText("   " + factory.getLabel("tab.information.data")+"   ");
      scrolled = new ScrolledComposite(tab, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
      item.setControl(scrolled);
      
      txtInformation = new Text(tab, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
      txtInformation.setEditable(false);
      txtInformation.setLayoutData(gridData);
      txtInformation.setFont(UIDATA.FONT_10);
      txtInformation.setMenu(null);
      item = new TabItem(tab, SWT.NONE);
      item.setText("   " + factory.getLabel("tab.status.data")+"   ");
      item.setControl(txtInformation);
      
      tab.setSelection(0);
    } else {
      txtInformation = new Text(sashMain, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
      txtInformation.setEditable(false);
      txtInformation.setLayoutData(gridData);
      txtInformation.setFont(UIDATA.FONT_10);
      txtInformation.setMenu(null);
    }
    
    Menu inforMenu = new Menu(getShell(), SWT.POP_UP);
    txtInformation.setMenu(inforMenu);
    
    factory.createStyleMenuItem(inforMenu, "pool.clear.queue", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        Properties properties_ = ClientProperties.getInstance().getProperties();
        ClientRM resources_ = new ClientRM("Crawler");
        int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
        MessageBox msg = new MessageBox (txtInformation.getShell(), style);
        msg.setMessage(resources_.getLabel("pool.clear.queue.confirm"));
        if(msg.open() != SWT.YES) return ;
        new ClearPoolQueue((Crawler)CrawlerUI.this);
      }
    });
    
//    if(XPWindowTheme.isPlatform()) {
//      CTabFolder tabFolder = (CTabFolder)tab;
//      tabFolder.addSelectionListener(new SelectionAdapter() {
//        @SuppressWarnings("unused")
//        public void widgetSelected(SelectionEvent e) {
//          loadCrawlerData();
//        }
//      });
//
//      CTabItem item = new CTabItem(tabFolder, SWT.NONE);
//      if(totalThread >= 10) {
//        item.setText("   " + factory.getLabel("tab.information.data")+"   ");
//        item.setControl(scrolled);
//        tabFolder.setSelection(0);
//
//        item = new CTabItem(tabFolder, SWT.NONE);
//      }
//      item.setText("   " + factory.getLabel("tab.table.data")+"   ");
//      item.setControl(tableData);
//      
//      item = new CTabItem(tabFolder, SWT.NONE);
//      item.setText("   " + factory.getLabel("tab.status.data")+"   ");
//      item.setControl(txtInformation);
//      
//      if(totalThread < 10) tabFolder.setSelection(1);
//    } else {
//      TabFolder tabFolder = (TabFolder)tab;
//      tabFolder.addSelectionListener(new SelectionAdapter() {
//        @SuppressWarnings("unused")
//        public void widgetSelected(SelectionEvent e) {
//          loadCrawlerData();
//        }
//      });
//
//      TabItem item = new TabItem(tabFolder, SWT.NONE);
//      if(totalThread >= 10) {
//        item.setText("   " + factory.getLabel("tab.information.data")+"   ");
//        item.setControl(scrolled);
//        tabFolder.setSelection(0);
//
//        item = new TabItem(tabFolder, SWT.NONE);
//      }
//      
//      item.setText("   " + factory.getLabel("tab.table.data")+"   ");
//      item.setControl(tableData);
//      
//      item = new TabItem(tabFolder, SWT.NONE);
//      item.setText("   " + factory.getLabel("tab.status.data")+"   ");
//      item.setControl(txtInformation);
//      if(totalThread < 10) tabFolder.setSelection(1);
//    }
//
//    Menu menu = new Menu(factory.getComposite().getShell(), SWT.POP_UP);  
//    factory.createMenuItem(menu, "menuDeleteMeta", new SelectionAdapter(){
//      @SuppressWarnings("unused")
//      public void widgetSelected(SelectionEvent evt) {
//        deleteMeta();
//      }
//    });
//    tableData.setMenu(menu);

    sashMain.setWeights(new int[]{290, 550});

    Composite buttonComposite = new Composite(mainComposite, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);    
    buttonComposite.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    buttonComposite.setLayout(rowLayout);
    rowLayout.justify = true;

    factory.setComposite(buttonComposite);

    butDownload = factory.createButton("butDownload", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        startOrStop();
      }   
    }, factory.loadImage("butDownload.png")); 
//    butDownload.setEnabled(false);

    factory.createButton("butAdd", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        sourceList.addSources();
      }   
    }, factory.loadImage("butAdd.png"));

    factory.createButton("butRemove", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        sourceList.removeSources();
      }   
    }, factory.loadImage("butRemove.png"));

    factory.setComposite(this);  

    org.eclipse.swt.widgets.Group group = null;

    if(totalThread < 10) {
      group = new org.eclipse.swt.widgets.Group(this, SWT.NO);
      group.setFont(UIDATA.FONT_10B);
      group.setText(factory.getLabel("groupStatus"));
      gridData = new GridData(GridData.FILL_HORIZONTAL);
      group.setLayoutData(gridData);

      group.setLayout(new FillLayout());
    }

    if(scrolled == null) {
      scrolled = new ScrolledComposite(group, SWT.V_SCROLL);
    }

    scrolledComposite = new Composite(scrolled, SWT.NONE);
    gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    gridLayout.marginHeight = 1;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 2;
    scrolledComposite.setLayout(gridLayout);
    factory.setComposite(scrolledComposite);

    lbl = new Label[totalThread];
    gridData = new GridData(GridData.FILL_HORIZONTAL);    
    Color bgColor  = new Color(getDisplay(), 255, 255, 255);
    for( int i=0 ; i < lbl.length; i++){
      lbl[i] = factory.createLabel(SWT.BORDER);      
      lbl[i].setText("");
      lbl[i].setLayoutData(gridData);
      lbl[i].addMouseListener(new MouseAdapter() {
        public void mouseDoubleClick(MouseEvent evt) {
          for(int j = 0; j < lbl.length; j++) {
            if(evt.widget != lbl[j]) continue;
            viewExecutor(j);
            break;
          }
        }
      });
      lbl[i].setBackground(bgColor);
    }
    if(scrolled != null) {
      //    composite2.setSize(800, totalThread*40);
      scrolled.setContent(scrolledComposite);
      scrolled.setExpandHorizontal(true);
      scrolled.setExpandVertical(true);

      scrolled.addControlListener(new ControlAdapter() {
        @SuppressWarnings("unused")
        public void controlResized(ControlEvent e) {
          Rectangle r = scrolled.getClientArea();
          scrolled.setMinSize(scrolledComposite.computeSize(r.width, SWT.DEFAULT));
        }
      });
    }

    //    if(section != null) {
    //      liveSashForm.setWeights(new int[]{95, 5});
    //    } else {
    //      liveSashForm.setWeights(new int[]{87, 13});
    //    }
    //    
    //    if(section != null) section.setExpanded(false);

    Runnable timer = new Runnable () {
      public void run () {
        if(CrawlerUI.this.isDisposed()) return;
        loadCrawlerData();
        getDisplay().timerExec(3000, this);
      }
    };
    getDisplay().timerExec(10, timer);

    statusBar = new StatusBar(workspace, this, true);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    statusBar.setLayoutData(gridData);  
    statusBar.setComponent(this);

    sourceList.loadSource(0);
  }

//  abstract void deleteMeta();
//  abstract void viewContent();
  abstract void startOrStop();
//  abstract void showData(String [] values);
  abstract void loadStatus();
  abstract void viewExecutor(int index);

  //  public void dispose() {
  //    super.dispose();
  //    if(section != null) section.dispose();
  //  }

  public StatusBar getStatusBar() { return statusBar; }


//  protected int getSelectedTabItem() {
//    if(XPWindowTheme.isPlatform()) {
//      CTabFolder tabFolder = (CTabFolder)tab;
//      return tabFolder.getSelectionIndex();
//    }
//    TabFolder tabFolder = (TabFolder)tab;
//    return tabFolder.getSelectionIndex();
//  }
  
//  protected int getTabItemCount() {
//    if(XPWindowTheme.isPlatform()) {
//      CTabFolder tabFolder = (CTabFolder)tab;
//      return tabFolder.getItemCount();
//    }
//    TabFolder tabFolder = (TabFolder)tab;
//    return tabFolder.getItemCount();
//  }

//  protected void setSelectedItem(int idx) {
//    if(tab == null) return;
//    if(XPWindowTheme.isPlatform()) {
//      final CTabFolder tabFolder = (CTabFolder)tab;
//      tabFolder.setSelection(idx);
//      return ;
//    }
//    TabFolder tabFolder = (TabFolder)tab;
//    tabFolder.setSelection(idx);
//  }
//  
  private void loadCrawlerData() {
//    int selected = getSelectedTabItem();
//    if(getTabItemCount() == 3) {
//      if(selected == 0) {
//        loadStatus();
//      } else if(selected == 2) {
//        if(refreshStatus == 0) {
//          new ViewPoolInformation((Crawler)CrawlerUI.this, txtInformation);
//          refreshStatus++;
//        } else {
//          refreshStatus++;
//        }
//        if(refreshStatus == 20) refreshStatus = 0;
//      }
//      return;
//    }
    
//    if(selected == 1) {
      if(refreshStatus == 0) {
        new ViewPoolInformation((Crawler)CrawlerUI.this, txtInformation);
        refreshStatus++;
      } else {
        refreshStatus++;
      }
      if(refreshStatus == 20) refreshStatus = 0;
//    }
    loadStatus();
  }
}
