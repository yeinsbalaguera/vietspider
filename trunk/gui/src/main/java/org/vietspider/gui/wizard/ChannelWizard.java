package org.vietspider.gui.wizard;
import java.net.URL;
import java.util.HashMap;
import java.util.ListResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.CrawlerClientHandler;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.browser.ControlComponent;
import org.vietspider.gui.crawler.Crawler;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.SourceEditor;
import org.vietspider.gui.module.ToolWindow;
import org.vietspider.gui.web.FastWebClient2;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.model.Source;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.services.ImageLoader;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.tabfolder.CTabItem;
import org.vietspider.ui.widget.waiter.WaitLoading;

/***************************************************************************
 * Copyright 2001-2011 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Dec 30, 2011  
 */
//http://www.bbc.co.uk/news/world-middle-east-16396345
public class ChannelWizard extends ControlComponent {

  private Source tempSource;
  private StackLayout layout;

  private CTabItem tabItem;

  private HashMap<String, Object> temp;

  private ToolWindow toolWindow;

  private Image toolBarImage;

  public ChannelWizard(Composite _parent, Workspace _workspace){
    super(_parent, _workspace);
    this.tempSource = new Source();
    
    temp = new HashMap<String, Object>();

    layout = new StackLayout();
    setLayout(layout);

    try {
      ImageLoader imageLoader = new ImageLoader();
      toolBarImage = imageLoader.load(getDisplay(), "status.folder.png");
    } catch (Exception e) {
    }

    try {
      layout.topControl = new ChannelWizardStep1(this);
      new ChannelWizardStep2(this);
      new ChannelWizardStep3(this);
      new ChannelWizardStep31(this);
      new ChannelWizardStep4(this);
      new ChannelWizardStep5(this);
      new ChannelWizardStep6(this);
      new ChannelWizardStep7(this);
      new ChannelWizardStep8(this);
      new ChannelWizardStep9(this);
    } catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
    }
    
//    new ChannelWizardStep9(this);
  }

  public void showToolBar() {
    if(toolWindow != null && !toolWindow.isDisposed()) {
      toolWindow.dispose();
      toolWindow = null;
    } else {
      toolWindow = new ToolWindow(workspace);
    }
  }

  public Source getSource() { return tempSource; }

  //  public Composite getParent() { return this; }

  public StackLayout getLayout() { return layout; }
  
  void openHelp(String youtube, String url) {
    new WizardHelp(getShell(), youtube, url);
  }

  static synchronized String getLabel(String key) {
    return getResources().getLabel(key);
  }

  void loadDataHTML(String link) throws Exception {
    Creator creator = getTemp("creator");
    SourceEditor editor = creator.getInfoControl().getSourceEditor();
    FastWebClient2 webClient2 = (FastWebClient2)editor.getWebClient();
    byte [] dataHtml = null;
    int idx = link.indexOf('/', 10);
    if(idx > 0) {
      String home = link.substring(0, idx);
      webClient2.setURL(home, new URL(home));
    } else {
      idx = link.indexOf('?', 10);
      if(idx > 0) {
        String home = link.substring(0, idx);
        webClient2.setURL(home, new URL(home));
      } else {
        webClient2.setURL(link, new URL(link));
      }
    }
    dataHtml = webClient2.loadContent(link, link, null);
    putTemp("page.data.content", dataHtml);
    //    dataHtml = webClient.loadContent(link, homepage);
  }

  public void putTemp(String key, Object value) {
    temp.put(key, value);
  }

  @SuppressWarnings("unchecked")
  public <T> T getTemp(String key) {
    return (T)temp.get(key);
  }

  /*private void setProxy(URI uri) {
    try {
      System.setProperty("java.net.useSystemProxies", "true");
      ProxySelector selector = ProxySelector.getDefault();
      List<Proxy> list = selector.select(uri);
      if (list.size() < 1) return;
      String host = list.get(0).address().toString();
      int idx = host.indexOf(":");
      int port = 80;
      if(idx > 0) {
        try {
          port = Integer.parseInt(host.substring(idx+1).trim());
          host = host.substring(0, idx);
        } catch (Exception e) {
        }
      }
      webClient.registryProxy(host, port, null, null);
    } catch (IllegalArgumentException e) {

    }
  }*/

  private static ClientRM clientRM;

  static synchronized ClientRM getResources() {
    if (clientRM != null) return clientRM;

    try {
      clientRM = new ClientRM(ChannelWizard.class, "ChannelWizard");
    } catch (Exception e) {
      clientRM = new ClientRM(new ListResourceBundle() {
        protected Object[][] getContents() {
          return new String[][] { 
              {"next", "Next"},
              {"previous", "Previous"},

              {"status.start", "Tools"},

              {"step0.error.no.select.category", "Please select category!"},

              {"step1.address", "Address: "},
              {"step1.error.no.url", "Please input the data page address and press Enter!"},
              {"step1.no.data", "Cann't load data from the current url!"},

              {"step2.add.more", "Add More"},
              {"step2.encoding", "Encoding"},

              {"step3.remove", "Remove Path"},
              {"step3.add.node", "Add Node"},
              {"step3.remove.node", "Remove Node"},
              {"step3.expand.node", "Expand Node"},
              {"step3.collapse.node", "Collapse Node"},
              {"step3.expand.data.node", "Expand Data Node"},
              {"step3.view.item", "View Tree"},
              {"step3.remove.all", "Remove All Paths"},
              {"step3.up", "Up"},
              {"step3.down", "Down"},
              {"step3.no.path", "Please select add one node on the html tree!"},

              {"step4.add.name.tip", "Define XML Tag. Please add tag name!"},
              {"org.vietspider.gui.wizard.ChannelWizardStep4.menuRenameRegion", "Rename"},
              {"org.vietspider.gui.wizard.ChannelWizardStep4.menuUpRegion", "Up"},
              {"org.vietspider.gui.wizard.ChannelWizardStep4.menuDownRegion", "Down"},
              {"org.vietspider.gui.wizard.ChannelWizardStep4.menuRemoveRegion", "Remove"},
              {"org.vietspider.gui.wizard.RenameTagDialog.name", "New Name:"},
              {"org.vietspider.gui.wizard.RenameTagDialog.cancel", "Cancel"},

              {"step4.no.tag.name", "No Tag! Please input tag name and add to list!"},

              {"org.vietspider.gui.wizard.ChannelWizardStep5.butAdd", "Add"},
              {"org.vietspider.gui.wizard.ChannelWizardStep5.butRemove", "Remove"},
              {"step5.menu.add", "Add"},
              {"step5.menu.remove", "Remove"},
              {"step5.menu.expand", "Expand"},
              {"step5.menu.collapse", "Collapse"},
              {"step5.menu.expand.data", "Expand Data Node"},
              {"step5.menu.collapse.tree", "Collapse Tree"},
              {"step5.menu.view.item", "View Node"},

              {"step6.data.link", "Data Link:"},
              {"step6.data.link.template", "Data Link Template:"},
              {"step6.add.template.tip", "Add the current template"},

              {"org.vietspider.gui.wizard.ChannelWizardStep6.menuPaste", "Paste"},
              {"org.vietspider.gui.wizard.ChannelWizardStep6.menuCopy", "Copy"},
              {"org.vietspider.gui.wizard.ChannelWizardStep6.menuCopyAll", "Copy All"},
              {"org.vietspider.gui.wizard.ChannelWizardStep6.menuClear", "Clear"},
              {"org.vietspider.gui.wizard.ChannelWizardStep6.menuCut", "Cut"},
              {"org.vietspider.gui.wizard.ChannelWizardStep6.menuToTemplate", "To Template"},
              {"org.vietspider.gui.wizard.ChannelWizardStep6.menuRemoveItem", "Remove Item"},
              {"org.vietspider.gui.wizard.ChannelWizardStep6.menuRemoveAll", "Remove All"},
              {"org.vietspider.gui.wizard.ChannelWizardStep6.menuUseDataLink", "Use The Current Link"},

              {"step6.error.pattern", "Sample Page mot match Link Pattern!"},

              {"step7.start.page", "Start Page: "},
              {"step7.start.page.tip", "Add Start Page"},

              {"step8.channel.name", "Channel Name: "},
              {"step8.error.no.name", "Please input the channel name!"},

              {"done", "Done"},
              {"done.crawl", "Done and Crawl"},

              {"ChannelWizardStep3.butAdd", "Add"},
              {"ChannelWizardStep5.butAdd", "Add" }
          };
        }
      });
    }
    return clientRM;
  }

  @Override
  public String getNameIcon() { return "small.createsource.png";  }

  public void save(final Source saveSource, final boolean crawl) {
    Worker worker = new Worker() {

      private boolean overide = true;
      private String error;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        String name = saveSource.getName();
        try {
          SourcesClientHandler client = new SourcesClientHandler(saveSource.getGroup());
          Source newSource = client.loadSource(saveSource.getCategory(), name);
          overide = newSource == null;
        } catch (Exception e) {
          error = e.toString();
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        if(error != null && error.length() > 0) {
          ClientLog.getInstance().setMessage(getShell(), new Exception(error));
          return;
        }
        if(!overide) {
          MessageBox msg = new MessageBox(getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
          ClientRM resource = new ClientRM("Creator");
          msg.setMessage(resource.getLabel("msgAlertOverideSource"));
          if(msg.open() != SWT.YES) return;    
        } 
        saveSource(saveSource, crawl); 
      }
    };
    WaitLoading waitLoading = new WaitLoading(this, worker);
    waitLoading.open();
  }

  private void saveSource(final Source saveSource, final boolean crawl) {
    Worker [] plugins  = new Worker[0];
    if(crawl) {
      plugins = new Worker[1];
      plugins[0] = new Worker() {

        private String sourceValue;
        private String error;

        public void abort() {
          ClientConnector2.currentInstance().abort();
        }

        public void before() {
          StringBuilder builder = new StringBuilder();
          builder.append(saveSource.getName()).append(".");
          builder.append(saveSource.getGroup()).append(".");
          builder.append(saveSource.getCategory());
          sourceValue = builder.toString().trim();
        }

        public void execute() {
          try {
            CrawlerClientHandler client = new CrawlerClientHandler();
            client.addSourcesToLoader(sourceValue, false);
          } catch (Exception exp) {
            error = exp.toString();
          }
        }

        public void after() {
          if(error != null && error.length() > 0) {
            ClientLog.getInstance().setException(getShell(), new Exception(error));
            return;
          }
          try {
            workspace.getTab().createTool(Crawler.class, true,  SWT.CLOSE);
          } catch (Exception exp) {
            ClientLog.getInstance().setException(getShell(), exp);
          }
          getWorkspace().getTab().closeItem(tabItem);
        }
      };
    }

    Worker worker = new Worker(plugins) {

      String error = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          saveSource.setLastModified(System.currentTimeMillis());
          new SourcesClientHandler(saveSource.getGroup()).saveSource(saveSource);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(getShell(), new Exception(error));
          return;
        }
        if(!crawl) reset();
//        {
//          try {
//            workspace.getTab().createTool(AppBrowser.class, true, SWT.NONE);
//          } catch (Exception e) {
//            ClientLog.getInstance().setException(getShell(), e);
//          } 
//          getWorkspace().getTab().closeItem(tabItem);
//        } 
      }
    };
    WaitLoading waitLoading = new WaitLoading(this, worker);
    waitLoading.open();
  }

  protected void createToolBarButton(Composite composite) {
    Button startTool = new Button(composite, SWT.PUSH);
    startTool.setImage(toolBarImage);
    //    startTool.setImage(clientRM.loadImage("status.folder.png"));
    startTool.setText(clientRM.getLabel("status.start"));
    startTool.setFont(UIDATA.FONT_8TB);
    startTool.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
    startTool.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        showToolBar();
      }
    });
    GridData gridData = new GridData();
    gridData.widthHint= 150;
    gridData.heightHint = 25;
    startTool.setLayoutData(gridData);
  }

  public void setTabItem(CTabItem item) { this.tabItem = item; }

  public void setTitle(String step) {
    String text = tabItem.getText();
    int idx = text.indexOf('-');
    if(idx > 0) text = text.substring(0, idx);
    tabItem.setText(text + "-" + step);
  }
  
  public void reset() {
    Control[] children = getChildren();
    for (int i = 0; i < children.length; i++) {
      Control child = children[i];
      ((IChannelWizardStep)child).reset();
    }
    if(children.length > 0) {
      layout.topControl = children[0]; 
      ((IChannelWizardStep)children[0]).show();
      layout();
    }
  }

  public CTabItem getTabItem() { return tabItem; }

  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);

    shell.setLayout(new FillLayout());

    ChannelWizard wizard = new ChannelWizard(shell, null);
    //    GridLayout layout = new GridLayout(1, true);
    //    shell.setLayout(layout);

    shell.setSize(600, 450);
    shell.setLocation(250, 150);

    shell.open();
    
    wizard.openHelp("http://www.youtube.com/watch?v=YZ3dLBkpGx8&feature=related", "http://vietspider.org/video/step2.avi");
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
    System.exit(0);
  }


}
