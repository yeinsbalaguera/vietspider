/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.config;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.vietspider.bean.DatabaseConfig;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.browser.ControlComponent;
import org.vietspider.gui.browser.StatusBar;
import org.vietspider.gui.config.group.GroupConfig;
import org.vietspider.gui.users.Organization;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.LiveSashForm;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 3, 2007  
 */
public class Config  extends ControlComponent {

  private LiveSashForm sashMain;

  private ApplicationConfig application;
  private CrawlerConfig crawler;
  private PluginConfig plugin;
  private GroupConfig group;
  private CrawlMailConfig mailConfig;
  //  private Organization organization;

  private Composite grpApplication;
  private Composite compCrawler;
  private Composite compGroup;
  private Composite compPlugin;
  private Composite compOrg;
  private Composite compMailConfig;

  private Properties properties;
  private Properties mailProperties;

  private Hyperlink selectedLink;

  private Composite buttonComposite;

  protected StatusBar statusBar;

  public Config(Composite parent, Workspace workspace) {
    super(parent, workspace);

    setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

    ApplicationFactory factory = new ApplicationFactory(this, "Config", getClass().getName());

    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 5;
    gridLayout.horizontalSpacing = 10;
    gridLayout.verticalSpacing = 10;
    gridLayout.marginWidth = 5;
    setLayout(gridLayout);

    factory.setComposite(this);
    int style = GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_VERTICAL;
    GridData gridData = new GridData(GridData.BEGINNING | style);

    gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 20;
    gridLayout.horizontalSpacing = 10;
    gridLayout.verticalSpacing = 15;
    gridLayout.marginWidth = 5;

    Group groupMenu = factory.createGroup("grpMenu", gridData, gridLayout);
    //    groupMenu.setBackground(new Color(getDisplay(), 255, 255, 255));
    HyperlinkAdapter selectedAdapter = new HyperlinkAdapter() {

      public void linkExited(HyperlinkEvent e) {
        Hyperlink hyperlink = (Hyperlink)e.widget;
        if(selectedLink != hyperlink) {
          hyperlink.setUnderlined(false);  
        }
      }

      public void linkActivated(HyperlinkEvent e) {
        if(selectedLink != null) {
          selectedLink.setUnderlined(false);   
        }
        selectedLink = (Hyperlink)e.widget;
        selectedLink.setUnderlined(true);
      }
    };

    factory.setComposite(groupMenu);
    factory.createMenuLink("itemSystem", new HyperlinkAdapter(){
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        sashMain.setMaximizedControl(grpApplication);
        buttonComposite.setVisible(true);
      }
    }, selectedAdapter);

    factory.createMenuLink("itemCrawlerConfig", new HyperlinkAdapter(){
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        sashMain.setMaximizedControl(compCrawler);
        buttonComposite.setVisible(true);
      }
    }, selectedAdapter);

    factory.createMenuLink("itemGroup", new HyperlinkAdapter(){
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        sashMain.setMaximizedControl(compGroup);
        buttonComposite.setVisible(true);
      }
    }, selectedAdapter);

    if(Application.LICENSE != Install.PERSONAL) { 
      factory.createMenuLink("itemOrg", new HyperlinkAdapter(){
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e) {
          sashMain.setMaximizedControl(compOrg);
          buttonComposite.setVisible(false);
        }
      }, selectedAdapter);
    }

    factory.createMenuLink("itemPlugin", new HyperlinkAdapter(){
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        sashMain.setMaximizedControl(compPlugin);
        buttonComposite.setVisible(true);
      }
    }, selectedAdapter);

    factory.createMenuLink("itemMailConfig", new HyperlinkAdapter(){
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        sashMain.setMaximizedControl(compMailConfig);
        buttonComposite.setVisible(true);
      }
    }, selectedAdapter);

    sashMain = new LiveSashForm(this, SWT.VERTICAL);
    gridData = new GridData(GridData.FILL_BOTH);
    sashMain.setLayoutData(gridData);

    factory.setComposite(sashMain);
    grpApplication = new Composite(sashMain, SWT.NONE);
    grpApplication.setBackground(getBackground());
    grpApplication.setLayout(new GridLayout(1, false));
    application = new ApplicationConfig(grpApplication, factory);
    sashMain.setMaximizedControl(grpApplication);


    factory.setComposite(sashMain);
    compPlugin = new Composite(sashMain, SWT.NONE);
    compPlugin.setLayout(new GridLayout(1, false));
    compPlugin.setBackground(getBackground());
    factory.setComposite(compPlugin);
    GridData griData2 = new GridData();
    Group grpPlugin = factory.createGroup("grpPlugin", griData2, new GridLayout(1, false));
    plugin = new PluginConfig(grpPlugin, factory);

    factory.setComposite(sashMain);
    compCrawler = new Composite(sashMain, SWT.NONE);
    compCrawler.setLayout(new GridLayout(1, false));
    compCrawler.setBackground(getBackground());
    crawler = new CrawlerConfig(compCrawler, factory);

    factory.setComposite(sashMain);
    compGroup = new Composite(sashMain, SWT.NONE);
    compGroup.setLayout(new GridLayout(1, false));
    compGroup.setBackground(getBackground());
    group = new GroupConfig(compGroup, factory);

    if(Application.LICENSE != Install.PERSONAL) { 
      factory.setComposite(sashMain);
      compOrg = new Composite(sashMain, SWT.NONE);
      compOrg.setLayout(new GridLayout(1, false));
      compOrg.setBackground(getBackground());
      /*organization = */new Organization(compOrg, workspace);
    }

    factory.setComposite(sashMain);
    compMailConfig = new Composite(sashMain, SWT.NONE);
    compMailConfig.setLayout(new GridLayout(1, false));
    compMailConfig.setBackground(getBackground());
    factory.setComposite(compMailConfig);
    griData2 = new GridData();
    Group grpMailConfig = factory.createGroup("grpMailConfig", griData2, new GridLayout(1, false));
    mailConfig = new CrawlMailConfig(grpMailConfig, factory);

    Composite bottom1 = new Composite(this, SWT.NONE);

    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    bottom1.setLayoutData(gridData);

    gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 5;
    gridLayout.horizontalSpacing = 10;
    gridLayout.verticalSpacing = 10;
    gridLayout.marginWidth = 5;
    bottom1.setLayout(gridLayout);

    Label label = new Label(bottom1, SWT.HORIZONTAL);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    label.setLayoutData(gridData);

    buttonComposite = new Composite(bottom1, SWT.NONE);
    gridData = new GridData(GridData.END);
    buttonComposite.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    rowLayout.spacing = 20;
    buttonComposite.setLayout(rowLayout);
    rowLayout.justify = false;

    factory.setComposite(buttonComposite);

    factory.createButton("butSaveConfig", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        try {
          group.saveGroupConfig();
        }catch (Exception e) {
          ClientLog.getInstance().setException(getShell(), e);
        }

        try {
          setData2Properties();
          saveData2Server();
          plugin.saveSetup();
        } catch (Exception e) {
          ClientLog.getInstance().setException(getShell(), e);
        }
      }   
    }, factory.loadImage("save.png")); 

    factory.createButton("butResetConfig", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        try {
          group.loadGroupConfig();
        }catch (Exception e) {
          ClientLog.getInstance().setException(getShell(), e);
        }
        loadSystemConfig();
      }   
    }, factory.loadImage("butReset.png"));

    loadSystemConfig();

    //    plugin = new PluginConfig(sashMain, factory);



    /*TabFolder tab = new TabFolder(this, SWT.BOTTOM | SWT.RIGHT);
    tab.setFont(UIDATA.FONT_10);

    Composite serverComposite = new Composite(tab, SWT.NONE);
    TabItem item = new TabItem(tab, SWT.NONE);
    tab.setSelection(item);
    item.setText(factory.getLabel("tab.server.config"));
    item.setControl(serverComposite);

    serverComposite.setLayout(new FillLayout());
    SystemConfig systemConfig = new SystemConfig(serverComposite, factory);

    // group
    item = new TabItem(tab, SWT.NONE);
    item.setText(factory.getLabel("tab.group.config"));
    Composite groupComposite = new Composite(tab, SWT.NONE);
    item.setControl(groupComposite);

    groupComposite.setLayout(new FillLayout());
    GroupConfig groupConfig = new GroupConfig(groupComposite, factory);*/

    statusBar = new StatusBar(workspace, this, true);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    statusBar.setLayoutData(gridData);  
    statusBar.setComponent(this);
  }

  private void loadSystemConfig() {
    Worker excutor = new Worker() {

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        DataClientHandler dataClientHandler = DataClientHandler.getInstance();
        try {
          properties = dataClientHandler.getSystemProperties();
          mailProperties = dataClientHandler.loadMailConfig(); 
        } catch (Exception e) {
          ClientLog.getInstance().setException(getShell(), e);
          properties = new Properties();
        }
      }

      public void after() {
        setValue2UI();
        plugin.resetSetup();
      }
    };
    WaitLoading loading = new WaitLoading(this, excutor);
    loading.open();

//    application.getDatabaseSetting().load();
  }

  private void setValue2UI() {
    application.setValue2UI(properties);
    crawler.setValue2UI(properties);
    mailConfig.setValue2UI(mailProperties);
  }

  private void setData2Properties() throws Exception {
    application.setData2Properties();
    crawler.setData2Properties();
    mailConfig.setData2Properties();
  }

  private void saveData2Server() {
    Worker excutor = new Worker() {

      private DatabaseConfig databaseConfig;

      private StringBuilder error = new StringBuilder();

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
//        DatabaseSetting databaseSetting = application.getDatabaseSetting();
//        if(databaseSetting != null) databaseConfig = databaseSetting.getConfig();
      }

      public void execute() {
        DataClientHandler clientHandler = DataClientHandler.getInstance();
        try {
          clientHandler.saveSystemProperties(properties);
          clientHandler.saveMailConfig(mailProperties);
        } catch (Exception e) {
          error.append(e.toString());
        }

        if(databaseConfig != null) {
          try {
            clientHandler.saveDBInfo(databaseConfig, "database.connection.setting");
          } catch (Exception e) {
            error.append('\n').append(e.toString());
          }
        }
      }

      public void after() {
        if(error.length() > 0) {
          ClientLog.getInstance().setMessage(getShell(), new Exception(error.toString()));
        }
      }
    };
    WaitLoading loading = new WaitLoading(this, excutor);
    loading.open();
  }

  @Override
  public String getNameIcon() { return "small.settingsfolder.png"; }


}
