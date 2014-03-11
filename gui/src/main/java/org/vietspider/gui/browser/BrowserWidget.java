/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIWebBrowser;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.gui.module.UINews;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.LiveSashForm;
import org.vietspider.ui.widget.tabfolder.CTabItem;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 26, 2006
 */
public class BrowserWidget extends ControlComponent {

  protected Browser widget; 

  protected TabBrowser tab; 
  protected String title;
  protected CTabItem item;

  protected int id;

  private String appName;

  protected Toolbar toolbar;
  protected StatusBar statusBar;
  
  protected LiveSashForm liveSashForm;
  
  protected  UINews uiNews;

  public BrowserWidget(Composite parent, Workspace workspace) {
    super(parent, workspace, SWT.TRANSPARENCY_ALPHA);
  }

  public BrowserWidget(TabBrowser parent, CTabItem item_, Workspace workspace) {
    super(parent, workspace, SWT.TRANSPARENCY_ALPHA);
    init(parent, item_);
  }
  
  protected void init(TabBrowser parent, CTabItem item_)  {
    this.tab = parent;
    this.item = item_;
    
    setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    
    this.appName = ClientConnector2.currentInstance().getApplication();

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0; 
    gridLayout.numColumns = 1;  
    setLayout(gridLayout);
    
    toolbar = new Toolbar(this, tab);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL); 
    toolbar.setLayoutData(gridData); 
    
//    Label lblSeparator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
//    gridData = new GridData(GridData.FILL_HORIZONTAL);
//    lblSeparator.setLayoutData(gridData);
    
    liveSashForm = new LiveSashForm(this, SWT.HORIZONTAL);
    gridData = new GridData(GridData.FILL_BOTH);
    liveSashForm.setLayoutData(gridData);
    liveSashForm.setBackground(getBackground());
    
    
    Composite menu = new Composite(liveSashForm, SWT.BORDER);
    menu.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
    
    uiNews = new UINews(this);
    uiNews.setDataPanel(menu);
//    gridData = new GridData(); 
//    gridData.widthHint = 150;
//    menu.setLayoutData(gridData); 
    
    createBrowser(liveSashForm);
    
    liveSashForm.setWeights(new int[]{20, 80});
    
//    lblSeparator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
//    gridData = new GridData(GridData.FILL_HORIZONTAL);
//    lblSeparator.setLayoutData(gridData);
        
//    try {
//      int height = Integer.parseInt(ClientProperties.getInstance().getValue("dict.height").trim());
//      if(height > 0) createDict(height);
//    } catch (Exception e) {
//    }
    
    createStatusBar();
  }
  
  /*protected void createDict(int height) {
    if(height < 100) height = 100;
    final WordComponent component = new WordComponent(this);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL); 
    gridData.heightHint  = height;
    widget.addProgressListener(new ProgressAdapter() {
      
      @SuppressWarnings("unused")
      public void completed(ProgressEvent arg0) {
        component.setHtml(widget.getText());
      }
      
    });
    component.setLayoutData(gridData);
  }*/
  
  protected void createBrowser(Composite parent)  {
    widget = ApplicationFactory.createBrowser(parent, null);
    if(ApplicationFactory.isMozillaBrowser()) {
      widget.addProgressListener (new ProgressAdapter () {
        @SuppressWarnings("unused")
        public void completed (ProgressEvent event) {
          nsIWebBrowser webBrowser = (nsIWebBrowser)widget.getWebBrowser ();
          if(webBrowser == null) return;
          nsIDOMWindow domWindow = webBrowser.getContentDOMWindow ();
          nsIDOMEventTarget target = (nsIDOMEventTarget)domWindow.queryInterface (nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
          nsIDOMEventListener listener = new nsIDOMEventListener () {
            public nsISupports queryInterface (String uuid) {
              if (uuid.equals (nsIDOMEventListener.NS_IDOMEVENTLISTENER_IID) ||
                  uuid.equals (nsIDOMEventListener.NS_ISUPPORTS_IID)) {
                return this;
              }
              return null;
            }

            public void handleEvent (nsIDOMEvent event2) {
              Menu menu = widget.getMenu();
              if(menu != null) menu.dispose();
              BrowserWidgetMenu browserMenu = new BrowserWidgetMenu(tab, widget, event2);
              widget.setMenu(browserMenu.getMenu());
            }
          };
          target.addEventListener ("contextmenu", listener, false);
        }
      });
    }

    if(parent == this) {
      GridData gridData = new GridData( GridData.FILL_BOTH); 
      //    gridData.horizontalSpan = 3;
      widget.setLayoutData(gridData);
    }

    addWidgetListener();
    item.setText("   ...    ");
  }
  
  protected void createStatusBar() {
    statusBar = new StatusBar(workspace, this, true);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
//    gridData.horizontalSpan = 2;
    statusBar.setLayoutData(gridData);  
    //    tab.setStatusBar(statusBar);
    statusBar.setComponent(widget);
  }

  protected void addWidgetListener() {
    widget.addProgressListener(new ProgressAdapter(){   
      public void changed(ProgressEvent event){  
        processChanged(event);
      }
      public void completed(ProgressEvent event){      
        processCompleted(event);
      }
    });      

    widget.addTitleListener( new TitleListener(){
      public void changed(TitleEvent event){
        if(item.isDisposed()) return;
        title = event.title;
        item.setText(title);
        widget.setToolTipText(title);
//        DbIndexers.getInstance().index(new PageIndex(widget.getUrl(), title));
      }
    });

    widget.addStatusTextListener( new StatusTextListener(){
      public void changed(StatusTextEvent event){        
        changedStatus(event);   
      }
    });

    widget.addCloseWindowListener( new CloseWindowListener(){
      @SuppressWarnings("unused")
      public void close(WindowEvent event){
        tab.closeItem(item);
      }
    });

    widget.addOpenWindowListener( new OpenWindowListener(){
      public void open(WindowEvent event){      
        event.browser = tab.createItem().widget;
      }       
    });  

    //    widget.addMouseWheelListener(new MouseWheelListener() {
    //      
    //      @Override
    //      public void mouseScrolled(MouseEvent e) {
    //        StringBuilder builder = new StringBuilder();
    //        builder.append("alert(document.body.clientHeight);");
    //        builder.append("alert(document.all ? document.body.scrollTop : window.scrollTop);");
    //? window.pageYOffset : ");
    //        builder.append("document.documentElement ? document.documentElement.scrollTop : ");
    //        builder.append("document.body ? document.body.scrollTop : 0
    //        widget.execute(builder.toString());
    //        System.out.println(" dang chay roi " + e.toString());
    //      }
    //    });
  }

  @SuppressWarnings("unused")
  private void processCompleted(ProgressEvent event){
    if(item.isDisposed()) return;
    if(title != null) item.setText(title);
    String url = widget.getUrl();
//    System.out.println("complete");
    if(!url.startsWith("file") && url.indexOf(appName) < 0) {    
      widget.getDisplay().asyncExec(new WebIconLoader(this));
    }
//    widget.getDisplay().asyncExec(new WebIconLoader(this));
    //    System.out.println(" === >" + htmlText);
    //    if(htmlText.indexOf("vietspider_browser:back") > -1) {
    //      widget.back();
    //      widget.refresh();
    //    }
//    DbIndexers.getInstance().index(new PageIndex(widget.getUrl(), title));

    if(tab.getSelection () != item) return; 
    if(!toolbar.isFocusText()) toolbar.setText(widget.getUrl());
    if(statusBar != null) statusBar.setProgressValue(0);
  }

  private void processChanged(ProgressEvent event){
    
    if(event.total == 0) return;        
    int ratio = event.current * 100 / event.total;
    //    busy = event.current != event.total; 
    if(ratio > 30 && ratio < 70) {
      String url = widget.getUrl();
      url = url.toLowerCase();
      if(url.startsWith("file")) {
        liveSashForm.setMaximizedControl(widget);
      } else if(url.startsWith("http://") && url.indexOf(appName) > 0) {     
        liveSashForm.setMaximizedControl(null);
        liveSashForm.setWeights(new int[]{20, 80});
      } else {
//        item.setImage(imageLoader.load(getDisplay(), "web.png"));
        liveSashForm.setMaximizedControl(widget);
      }
      
      widget.getDisplay().asyncExec(new WebIconLoader(this));
    }
    if(tab.getSelection() != item) return;
    if(widget.getUrl() != null &&
        !widget.getUrl().trim().isEmpty()) {
      if(!toolbar.isFocusText()) toolbar.setText(widget.getUrl());
    }
    if(statusBar != null) statusBar.setProgressValue(ratio);
  } 

  void changedStatus(StatusTextEvent event) { 
    if(tab.getSelection() != item) return;
    if(statusBar != null) statusBar.setMessage(event.text);
  }  

  public void setItem(CTabItem item) { this.item = item; }

  public void setId(int i){ this.id = i; }

  public int getId(){ return id; }

  public void backPage() { widget.back(); }

  public void forwardPage(){ widget.forward(); } 

  public void stopPage(){
    widget.stop();
    statusBar.setProgressValue(0);
  }

  public void setUrl(String url){
    if(url.equals(widget.getUrl())){
      widget.refresh();
      return;   
    }
    widget.setUrl(url); 
    widget.setFocus();
  }  
  public String getUrl(){ return widget.getUrl(); }

  public void setText(String text){ widget.setText(text); }

  public Browser getWidget() { return widget; }

  public StatusBar getStatusBar() { return statusBar; }

//  public void invisibleItem() { toolbar.getLoader().dropped(); }
  
  public void viewPage() {
    Runnable timer = new Runnable () {
      public void run () {
        uiNews.viewPage(); 
      }
    };
    getDisplay().timerExec (1000, timer);
  }

  @Override
  public String getNameIcon() { return "window.png"; }
  
  public CTabItem getItem() { return item; }

}

