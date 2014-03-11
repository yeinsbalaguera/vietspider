/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.bds;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.util.Worker;
import org.vietspider.content.cms.sync.SyncManager2;
import org.vietspider.model.plugin.bds.BdsSyncData;
import org.vietspider.model.plugin.bds.XMLBdsConfig;
import org.vietspider.model.plugin.bds.XMLBdsConfig.Category;
import org.vietspider.model.plugin.bds.XMLBdsConfig.Region;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.XML2Object;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
class SinglePostSelector  {

  protected String metaId;
  private Category [] categories;
  private Region [] regions;
  protected String defaultCategory = null;
  
  private Combo cboCategories;
  private Combo cboRegions;
  
  protected Button butDebug;

  private boolean isShowMessage = true;
  private boolean destroy = false;

  protected Shell shell;
  
//  protected Label lblTitle;

  public SinglePostSelector(Shell parent) {
    ClientRM rm = BdsSyncArticlesPlugin.getResources();
    ApplicationFactory factory = new ApplicationFactory(parent, rm, getClass().getName());
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    factory.setComposite(shell);
    shell.setText("Gửi tin rao");
    shell.setLayout(new GridLayout(1, false));
    
    factory.setComposite(shell);
    
//    lblTitle = factory.createLabel(SWT.NONE);
    
    Group group = new Group(shell, SWT.SHADOW_ETCHED_IN);
    group.setText("Nhu cầu đăng");
    group.setLayout(new GridLayout(1, false));
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    group.setLayoutData(gridData);
    factory.setComposite(group);
    cboCategories = factory.createCombo(SWT.BORDER);
    cboCategories.setLayoutData(gridData);
    
    group = new Group(shell, SWT.SHADOW_ETCHED_IN);
    group.setText("Nơi đăng");
    factory.setComposite(group);
    group.setLayout(new GridLayout(1, false));
    group.setLayoutData(gridData);
    cboRegions = factory.createCombo(SWT.BORDER);
    cboRegions.setLayoutData(gridData);

    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        shell.setVisible(false);
        e.doit = false;
      }
    });

    final KeyAdapter keyAdapter = new KeyAdapter() {
      public void keyPressed(KeyEvent event) {
        if(event.keyCode == SWT.CR) sync();
      }
    };
    
    factory.setComposite(shell);
    createButton(factory, keyAdapter);
    
    loadCategriesRegions();
  }

  public void invokeSetup() {
    new BdsSetup(shell.getShell());
  }

  public void sync() {
    new ShellSetter(SinglePostSelector.this.getClass(), shell);
    shell.setVisible(false); 
    BdsSyncData syncData = new BdsSyncData();
    syncData.setArticleId(metaId);
    
    int index = cboCategories.getSelectionIndex();
    if(index < 0) index = 0;
    syncData.setCategoryId(categories[index].getCategoryId());
    
    index = cboRegions.getSelectionIndex();
    if(index < 0) index = 0;
    syncData.setRegionId(regions[index].getRegionId());
    
    syncData.setDebug(butDebug.getSelection());
    syncData.setShowMessage(isShowMessage);
    
    SyncManager2.getInstance(shell).sync(syncData);
  }

  public void setMetaId(String metaId) {
    this.metaId = metaId; 
//    this.lblTitle.setText(title);
//    if(butChannels != null) loadDefaultCategory("bds.sync.article.plugin");
  }

  private void loadCategriesRegions() {
    Worker excutor = new Worker() {

      private String error = null;
      private XMLBdsConfig config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "load.file.by.gzip"),
              new BasicHeader("file", "system/plugin/bds.config")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);

          config = XML2Object.getInstance().toObject(XMLBdsConfig.class, bytes);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        if(config == null) return;

        setCategoriesData(config.getCategories());
        setRegionData(config.getRegions());

        isShowMessage = config.isAlertMessage();

        Rectangle displayRect = UIDATA.DISPLAY.getBounds();
        int x = (displayRect.width - 250) / 2;
        int y = (displayRect.height - 180)/ 2;
//        shell.setSize(300, 200);
//        shell.setLocation(x, y);
        new ShellGetter(SinglePostSelector.class, shell, 300, 200, x, y);
        shell.open();
      }
    };
    new ThreadExecutor(excutor, shell).start();
  }

  public boolean isDispose() { return shell == null || shell.isDisposed(); }

  public void show() {
    //    listCategories.setSelection(-1);
    shell.setVisible(true); 
  }

  public void dispose() { shell.dispose(); }

  public boolean isDestroy() { return destroy; }

  protected void createButton(ApplicationFactory factory, KeyAdapter keyAdapter) {
    Composite bottom = new Composite(factory.getComposite(), SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;

    factory.setComposite(bottom);
    
    butDebug = factory.createButton(SWT.CHECK);
    butDebug.setText("Dò lỗi?");
    butDebug.setFont(UIDATA.FONT_10);

    Button butConfig = factory.createButton("butConfig", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        shell.setVisible(false);
        destroy = true;
        invokeSetup();
      }   
    });
    butConfig.setText("Đóng");

    Button butOk = factory.createButton("butOk", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        sync();
      }   
    }); 
    butOk.setText("Đăng tin");
    butOk.addKeyListener(keyAdapter);

    Button button = factory.createButton("butClose", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ShellSetter(SinglePostSelector.this.getClass(), shell);
        shell.setVisible(false);
      }   
    });
    button.setText("Đóng");
    button.addKeyListener(keyAdapter);
    
    shell.setImage(shell.getShell().getImage());
    shell.addKeyListener(keyAdapter);
  }

  private void setCategoriesData(Category[] categories) {
    this.categories = categories;
    cboCategories.removeAll();
    if(categories == null) return;
    for(int i = 0; i < categories.length; i++) {
      cboCategories.add(categories[i].getCategoryName());
    }
    if(cboCategories.getItemCount() > 0) cboCategories.select(0);
  }
  
  private void setRegionData(Region[] regions) {
    this.regions = regions;
    cboRegions.removeAll();
    if(regions == null) return;
    for(int i = 0; i < regions.length; i++) {
     cboRegions.add(regions[i].getRegionName());
    }
    if(cboRegions.getItemCount() > 0) cboRegions.select(0);
  }
  
  public static void main(String[] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    new SinglePostSelector(shell);
    
    shell.pack ();
    shell.open ();
    while (!shell.isDisposed ()) {
      if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
  }

}
