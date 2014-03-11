package org.vietspider.ui.htmlexplorer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIWebBrowser;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.token.attribute.Attributes;
import org.vietspider.ui.browser.PageMenu;
import org.vietspider.ui.htmlexplorer.PathBox.PathEvent;
import org.vietspider.ui.htmlexplorer.PathBox.RemovePath;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ImageHyperlink;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;
import org.vietspider.ui.widget.images.ToolbarResource;

abstract class HTMLExplorerViewer extends HtmlExplorerListener {

  String charset;
  protected String currentURL;
  HyperLinkUtil hyperlinkUtil;

  protected ImageHyperlink butOk, butCancel; 

  protected java.util.List<NodeInfoViewer> nodeViewers = new ArrayList<NodeInfoViewer>();

  public HTMLExplorerViewer(Composite parent, int type){
    super(parent);
    
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);

    ApplicationFactory factory = new ApplicationFactory(this, "HTMLExplorer", "HTMLExplorer"); 
    suggestTip = factory.getLabel("suggest.tip");
    
    SashForm mainSash = new SashForm(this, SWT.VERTICAL);
    mainSash.setBackground(getBackground());
    GridData gridData= new GridData(GridData.FILL_BOTH);    
    mainSash.setLayoutData(gridData);

    SashForm sash0 = new SashForm(mainSash, SWT.HORIZONTAL);
    sash0.setBackground(getBackground());
    sash0.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));

    Composite browserComposite = new Composite(sash0, SWT.NONE);
    browserComposite.setBackground(getBackground());
    gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    browserComposite.setLayout(gridLayout);
    
    ToolbarResource.createInstance(getDisplay(), "HTMLExplorer", HTMLExplorer.class);
//    toolbar = new HTMLExplorerToolbar(factory, browserComposite, this);
//    gridData = new GridData(GridData.FILL_HORIZONTAL);
//    toolbar.setLayoutData(gridData);
//    toolbar.setBackground(getBackground());

    browser = ApplicationFactory.createBrowser(browserComposite, PageMenu.class);
    if(ApplicationFactory.isMozillaBrowser()) {
      browser.addProgressListener (new ProgressAdapter () {
        @SuppressWarnings("unused")
        public void completed (ProgressEvent event) {
          nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser ();
          if(webBrowser == null) return;
          nsIDOMWindow domWindow = webBrowser.getContentDOMWindow ();
          if(domWindow == null) return;
          nsIDOMEventTarget target = (nsIDOMEventTarget)domWindow.queryInterface (nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
          nsIDOMEventListener listener = new nsIDOMEventListener () {
            public nsISupports queryInterface (String uuid) {
              if (uuid.equals (nsIDOMEventListener.NS_IDOMEVENTLISTENER_IID) ||
                  uuid.equals (nsIDOMEventListener.NS_ISUPPORTS_IID)) {
                return this;
              }
              return null;
            }
            public void handleEvent (nsIDOMEvent devent) {
              nsIDOMElement element = (nsIDOMElement)devent.getTarget().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
              String text =  element.getFirstChild().getNodeValue();
              if(text == null || text.trim().isEmpty() || "null".equalsIgnoreCase(text)) return;
              search(text.trim());
            }
          };
          target.addEventListener ("click", listener, false);
        }
      });
    } 

    gridData = new GridData(GridData.FILL_BOTH);
    browser.setLayoutData(gridData);
    browser.setBackground(getBackground());
    browser.addProgressListener(new ProgressAdapter(){		
      public void changed(ProgressEvent event) {	
        if(event.total == 0) return;				
        int ratio = event.current * 100 / event.total;
        showInformation("Loading " + String.valueOf(ratio) + "%");
        String url = browser.getUrl();
        if(url != null && 
            url.length() > 2 
            && !url.startsWith("about")) {
          currentURL = url;
        }
      }

      @SuppressWarnings("unused")
      public void completed(ProgressEvent event){	
        showInformation(suggestTip);
      }
    });

    browser.addStatusTextListener( new StatusTextListener(){
      @SuppressWarnings("unused")
      public void changed(StatusTextEvent event){
        String url = browser.getUrl();
        if(url == null) return;
        if(url.indexOf('/') < 0) return;
        showInformation("Waiting for "+ browser.getUrl());
//        toolbar.setText(browser.getUrl());   
      }
    });

    factory.setComposite(browserComposite);  
    tree = new Tree(sash0, SWT.MULTI | SWT.BORDER);
    tree.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String path = selectTree();
        if(path != null) box.setSuggestPath(path); 
      }		
    });
    tree.addMouseListener(new MouseAdapter() {
      public void mouseDown(MouseEvent e) {
        if(e.button == 2) addItems();
      }
    });
    tree.setToolTipText(suggestTip);


    Menu treeMenu = new Menu(getShell(), SWT.POP_UP);
    tree.setMenu(treeMenu);

    factory.createStyleMenuItem( treeMenu, "itemAdd", "+.gif", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        try {
          traverseTree(TreeHandler.SELECT, addItems());
        } catch (Exception e) {
          ClientLog.getInstance().setMessage(tree.getShell(), e);
        }
      }   
    });  

    factory.createStyleMenuItem(treeMenu, "itemRemove", "-.gif", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        try {
          removeItem();
        } catch (Exception e) {
          ClientLog.getInstance().setMessage(tree.getShell(), e);
        }
      }   
    });   

    factory.createStyleMenuItem(treeMenu, SWT.SEPARATOR);

    factory.createStyleMenuItem(treeMenu, "itemExpand", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        expand(true);
      }   
    });

    factory.createStyleMenuItem(treeMenu, "itemCollapse", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        expand(false);
      }   
    });

    factory.createStyleMenuItem(treeMenu, SWT.SEPARATOR);

    factory.createStyleMenuItem(treeMenu, "itemExpandDataNode", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        expandDataNode();
      }   
    });

    factory.createStyleMenuItem(treeMenu, "itemCollapseTree", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        TreeItem [] items = tree.getItems();
        if(items == null) return;
        for(TreeItem item : items) {
          expand(item, false);
        }
      }   
    });

    factory.createStyleMenuItem(treeMenu, SWT.SEPARATOR);

    factory.createStyleMenuItem( treeMenu, "itemView", "view.gif", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        viewItem();
      }   
    });   

    sash0.setWeights( new int[]{500, 300});     
    handler = new TreeHandler();  
    box = new PathBox(mainSash, factory);
    box.setLayoutData(gridData);
//    String [] paths = {
//        "BODY[0].TABLE[0].TBODY[0].TR[0].TD[0].TABLE[5].TBODY[0].TR[0].TD[0].TABLE[0].TBODY[0].TR[0].TD[2].TABLE[2].TBODY[0].TR[0].TD[0].TABLE[1].TBODY[0].TR[0].TD[2]",
//        "BODY[0].TABLE[0].TBODY[0].TR[0].TD[0].TABLE[5].TBODY[0].TR[0].TD[0].TABLE[0].TBODY[0].TR[0].TD[2].TABLE[2].TBODY[0].TR[0].TD[0].TABLE[2].TBODY[0].TR[1]",
//        "BODY[0].DIV[0].DIV[4].DIV[0].DIV[0]",
//        "BODY[0].DIV[0].DIV[0].DIV[2].DIV[0].DIV[0].DIV[0].TABLE[0].TBODY[0].TR[1].TD[0].TABLE[0].TBODY[0].TR[1].TD[0].TABLE[0].TBODY[0].TR[0].TD[1].CONTENT[2]]"
//    };
//    box.setItems(paths);
    box.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e){
        lblStatus.setText("");
        String path  = box.getSelectedPath();
        if(path == null) return;
        try {
          traverseTree(TreeHandler.SELECT, new String[]{path});
        } catch (Exception exp) {
          ClientLog.getInstance().setMessage(tree.getShell(), exp);
        }
        if(isErrorPath(path)) showErrorPath(path);
      }
    });
    box.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
//        butDown.setVisible(true);
//        butUp.setVisible(true);
        highlightErrorPath(box.getSelectedPath());
      }
    });
    box.addItemChangePath(new PathBox.ChangePath() {
      @Override
      public void change(PathEvent event) {
        traverseByPath(event.getPath());
//        NodePathParser pathParser = new NodePathParser();
//        NodePath nodePath = null;
//        try {
//          nodePath = pathParser.toPath(path);
//        } catch (Exception exp) {
//          return;
//        }
//        handler.traverseTree(HTMLExplorerViewer.this, tree, nodePath, path);
      }
    });
    box.addItemRemovePath(new RemovePath() {
      public void remove(PathEvent event) {
        try {
          traverseTree(TreeHandler.REMOVE, new String[]{event.getPath()});
        } catch (Exception e) {
          ClientLog.getInstance().setMessage(getShell(), e);
        }
      }
    });
    box.addItemCurrentPath(new PathBox.CurrentPath() {
      @Override
      public void change(PathBox.PathEvent event) {
        if(document == null) return;
        String path = event.getPath();
        String [] attrs = getAttrs(path);
        box.showAttrItemPopup(attrs);
      }
    });
    box.addSuggestCurrentPath(new PathBox.CurrentPath() {
      @Override
      public void change(PathBox.PathEvent event) {
        if(document == null) return;
        String path = event.getPath();
        String [] attrs = getAttrs(path);
        box.getSuggestWidget().showAttrSuggestion(attrs);
      }
    });

    mainSash.setWeights(new int[]{80, 20});
    
    if(type == HTMLExplorer.CONTENT) createButtonComponent(factory);
  } 
  
  protected void createButtonComponent(ApplicationFactory factory) {
    Composite buttonComposite = new Composite(this, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    buttonComposite.setLayoutData(gridData);
    factory.setComposite(buttonComposite);

    GridLayout gridLayout = new GridLayout(3, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 15;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 10;
    buttonComposite.setLayout(gridLayout);

    Composite removeComposite = new Composite(buttonComposite, SWT.NONE);
    buttonComposite.setBackground(getBackground());
    removeComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    removeComposite.setLayoutData(gridData);

    factory.setComposite(removeComposite);

   /* butRemoveAll = factory.createButton(SWT.PUSH);
    butRemoveAll.setText(factory.getResources().getLabel("menuRemoveAll"));
    butRemoveAll.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
        ClientRM clientRM = new ClientRM("HTMLExplorer");
        msg.setMessage(clientRM.getLabel("remove.all.message"));
        if(msg.open() != SWT.YES) return ;
        box.removeAll();
      }      
    });
    //    butRemoveAll.setVisible(false);
    butRemoveAll.setFont(UIDATA.FONT_9);

    butUp = factory.createButton(SWT.PUSH);
    butUp.setText(factory.getResources().getLabel("menuUp"));
    butUp.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        box.up();
      }      
    });
    butUp.setVisible(false);
    butUp.setFont(UIDATA.FONT_9);

    butDown = factory.createButton(SWT.PUSH);
    butDown.setText(factory.getResources().getLabel("menuDown"));
    butDown.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        box.down();
      }      
    });
    butDown.setVisible(false);
    butDown.setFont(UIDATA.FONT_9);*/

    lblStatus = factory.createLabel(SWT.NONE);
    lblStatus.setBackground(getBackground());
    gridData = new GridData();
    gridData.minimumWidth = 180;
    lblStatus.setLayoutData(gridData);
    lblStatus.setFont(UIDATA.FONT_10B);
    lblStatus.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));

    butRemovePath = factory.createButton(SWT.PUSH);
    butRemovePath.setText(factory.getResources().getLabel("remove.path.yes"));
    butRemovePath.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(errorPath != null) {
          box.removePath(errorPath);
          clearInformation();
        }
        showErrorPath(null);
      }      
    });
    butRemovePath.setVisible(false);
    butRemovePath.setFont(UIDATA.FONT_9);
    
//    Label lblSuggest = factory.createLabel(SWT.NONE);
//    lblSuggest.setBackground(getBackground());
//    gridData = new GridData(GridData.FILL_HORIZONTAL);
//    //    gridData.widthHint = 305;
//    lblSuggest.setLayoutData(gridData);
//    lblSuggest.setFont(UIDATA.FONT_10B);
////    lblSuggest.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
//    lblSuggest.setText(factory.getLabel("suggest.tip"));

    factory.setComposite(buttonComposite);

    String butTip = factory.getLabel("butOk");
    final ToolbarResource resources = ToolbarResource.getInstance();
    butOk = resources.createIcon(factory.getComposite(), 
        resources.getImageGo(), butTip, new HyperlinkAdapter(){ 
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        butOk.setImage(resources.getImageGo());
      }
      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
        butOk.setImage(resources.getImageGo());
      }
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
        butOk.setImage(resources.getImageGo());
      }
    }); 
    butOk.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        invisibleComponent();
        clickOk();    
      }

      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        butOk.setImage(resources.getImageGo1());
        butOk.redraw();
      }
    });

    butTip = factory.getLabel("butCancel");
    butCancel = resources.createIcon(factory.getComposite(), 
        resources.getImageCancel(), butTip, new HyperlinkAdapter(){ 
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        butCancel.setImage(resources.getImageCancel());
      }
      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
        butCancel.setImage(resources.getImageCancel());
      }
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
        butCancel.setImage(resources.getImageCancel());
      }
    }); 
    butCancel.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        invisibleComponent();
        clickCancel();    
      }

      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        butCancel.setImage(resources.getImageCancel1());
        butCancel.redraw();
      }
    });

    //    factory.setComposite(bottom);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);

  /*  String url = "";
    try {
      Preferences prefs = Preferences.userNodeForPackage(HTMLExplorer.class);
      url  = prefs.get("url.address", "");
    } catch (Exception e) {
      url = "";
    }*/
//    toolbar.setText(url);

    //    treeAddButton = new TreeAddButton(this);
    //    viewFunctions();
  }  

  abstract String selectTree();
  abstract String openFile();
  abstract public void goAddress(String url) ;
  abstract public void selectAddress(final String ...paths);
  //  abstract String editItem();
  abstract void removeItem() throws Exception ;
  abstract void viewItem();
  abstract public void traverseTree(int style, String[] paths) throws Exception; 

  public void clickOk(){
    Iterator<NodeInfoViewer> iterator = nodeViewers.iterator();
    while(iterator.hasNext()) {
      NodeInfoViewer viewer = iterator.next();
      if(viewer != null) viewer.close();
      viewer.close();
    }

    if(box.getItemCount() < 1) { 
      box.addItem();
    }

    HTMLExplorerEvent event = null;
    event = new HTMLExplorerEvent(box.getItems(), currentURL,  document, charset);
    try{
      event.fire(this);
    }catch(Exception exp){
      ClientLog.getInstance().setException(getShell(), exp);
    }
  }

  public void clickCancel(){
    Iterator<NodeInfoViewer> iterator = nodeViewers.iterator();
    while(iterator.hasNext()) {
      NodeInfoViewer viewer = iterator.next();
      if(viewer != null) viewer.close();
      viewer.close();
    }

    HTMLExplorerEvent event = new HTMLExplorerEvent();
    try{
      event.fire(this);
    }catch(Exception exp){
      ClientLog.getInstance().setException(getShell(), exp);
    }
  }

  public void setUrl(String url){
//    if(url == null || url.trim().length() < 1) return ;
//    browser.setUrl(url);
    goAddress(url);
  }

  public void setCharset(String ch){
    if(ch == null || ch.trim().length() == 0) return;
    charset = ch;    
  }

  public void documentBrowserCompleted(String content, String address){
    try{
      document = new HTMLParser2().createDocument(content);
      removeIFrameSource(document.getRoot());
      browser.setUrl(address);
//      toolbar.setText(address);
      tree.removeAll();
      handler.createTreeItem(tree, document); 
    } catch( Exception exp){
      ClientLog.getInstance().setException(getShell(), exp);
    }
  }

  protected void removeIFrameSource(HTMLNode node) {
    if(node.isNode(Name.IFRAME)) {
      Attributes attributes = node.getAttributes(); 
      attributes.remove("src");
    }
    List<HTMLNode> children = node.getChildren();
    if(children == null || children.size() < 1) return;
    for(int i = 0; i < children.size(); i++) {
      removeIFrameSource(children.get(i));
    }
  }

//  public void setEnableBrowser(boolean value) {
//    toolbar.setEnable(value);
//  }

  public void setDocument(String address, HTMLDocument doc){
    currentURL = address;
    browser.setUrl(address);
    setDocument(doc); 
  }

  public void setDocument(HTMLDocument doc){
    this.document = doc;

    if(hyperlinkUtil == null) hyperlinkUtil = new HyperLinkUtil();
    removeIFrameSource(document.getRoot());
    tree.removeAll();
    handler.createTreeItem(tree, doc);
    new AutoSelectDataNode2(this, doc, currentURL, handler, tree);
  }

  public Tree getTree() { return tree; }

  private void expand(boolean expand) {
    TreeItem [] items = tree.getSelection();
    for(TreeItem item : items) {
      expand(item, expand);
    }
  }

  private void expandDataNode() {
    java.util.List<TreeItem> items = handler.getSelectedItems();
    for(TreeItem item : items) {
      expandDataNode(item, false);
    }
  }

  private void expandDataNode(TreeItem item, boolean expand) {
    item.setExpanded(expand);
    TreeItem parent = item.getParentItem();
    if(parent == null || parent.isDisposed()) return;
    expandDataNode(parent, true);
  }


  private void expand(TreeItem item, boolean expand) {
    item.setExpanded(expand);
    TreeItem [] children = item.getItems();
    if(children == null) return;
    for(TreeItem child : children) {
      expand(child, expand);
    }
  }

  public HTMLDocument getDocument() { return document; }

//  public void setType(int type) { 
//    this.type = type;
//    if(type == HTMLExplorer.NONE) {
//      toolbar.setVisible(false);
//    } else {
//      toolbar.setVisible(true);
//    }
//  }

  public Browser getBrowser() { return browser; }

  protected void invisibleComponent() {
    //    treeAddButton.setVisible(false);
  }

  @Override
  public void addErrorPath(String path) { 
    errorPaths.add(path);
    Runnable timer = new Runnable () {
      public void run () {
        if(box.isDisposed()) return;
        box.showErrorPath(errorPaths);
      }
    };
    getDisplay().timerExec (1000, timer);
  }

  private void highlightErrorPath(String path) {
    if(path == null || path.trim().isEmpty()) return;
    try {
      int index = handler.indexOfError(tree, path);
      if(index > 0) box.highlightError(path, index);
    } catch (Exception e) {
      ClientLog.getInstance().setMessage(null, e);
    }
  }

}
