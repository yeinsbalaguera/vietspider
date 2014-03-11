/***************************************************************************
 * Copyright 2001-2005 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.widget;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIWebBrowser;
import org.vietspider.ui.XPWidgetTheme;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.action.IHyperlinkListener;
import org.vietspider.ui.widget.vtab.impl.VTab;
import org.vietspider.ui.widget.vtab.outlookbar.OutlookBarPane;

/**
 * @by thuannd (nhudinhthuan@yahoo.com)
 * @since Jul 13, 2005
 */
public class ApplicationFactory extends UIFactory {

  private String CLASS_NAME ; 

  public ApplicationFactory(Composite composite, String source, String name) {
    if(composite != null) {
      resources = new ClientRM(composite.getClass(), source);
    } else {
      resources = new ClientRM(null, source);
    }
    CLASS_NAME = name;    
    this.composite = composite;    
  }

  public ApplicationFactory(Composite composite, ClientRM rm, String name) {
    this.resources = rm;
    CLASS_NAME = name;    
    this.composite = composite;    
  }

  public ApplicationFactory(String source, Class<?> cl, int w, int h) {    
    resources = new ClientRM(cl, source);
    CLASS_NAME = cl.getName();
    if(XPWidgetTheme.isPlatform()) {
      composite = createShell(UIDATA.DISPLAY, cl, resources.getLabel(CLASS_NAME+".title"), "VietSpider.png", w, h);
    } else {
      composite = createShell(UIDATA.DISPLAY, cl, resources.getLabel(CLASS_NAME+".title"), "VietSpider2.ico", w, h);  
    }
  }

  public ClientRM getResources() { return resources; }

  public void setClassName( String name){ CLASS_NAME = name; }

  public String getClassName(){ return CLASS_NAME; }

  public Button createButton(String name){
    return super.createButton(composite, SWT.PUSH, resources.getLabel(CLASS_NAME+"."+name));   
  } 

  public Button createButton(String name, SelectionListener listener, Image image){
    Button button = createButton(name, listener);
    button.setImage(image);
    return button;
  }

  public Button createButton(String name, SelectionListener listener){
    if(resources.getLabel(CLASS_NAME+"."+name+"Tip") != null){
      return super.createButton(composite, SWT.PUSH, 
          resources.getLabel(CLASS_NAME+"."+name),resources.getLabel(CLASS_NAME+"."+name+"Tip"), listener);   
    }
    return super.createButton(composite, SWT.PUSH, resources.getLabel(CLASS_NAME+"."+name), listener);   
  }

//  public CButton createCButton(String name, SelectionListener listener){
//    if(resources.getLabel(CLASS_NAME+"."+name+"Tip") != null){
//      return super.createCButton(composite, SWT.PUSH, 
//          resources.getLabel(CLASS_NAME+"."+name),resources.getLabel(CLASS_NAME+"."+name+"Tip"), listener);   
//    }
//    return super.createCButton(composite, SWT.PUSH, resources.getLabel(CLASS_NAME+"."+name), listener);   
//  }

  public Button createButton(String name, int style, SelectionListener listener){
    if( resources.getLabel(CLASS_NAME+"."+name+"Tip") != null){
      return super.createButton(composite, style, 
          resources.getLabel(CLASS_NAME+"."+name),resources.getLabel(CLASS_NAME+"."+name+"Tip"), listener);   
    }
    return super.createButton( this.composite, style, 
        resources.getLabel(CLASS_NAME+"."+name), listener);   
  }

  public RButton createIcon(String name, int t, MouseListener listener){
    Image img = loadJarImage(name);
    return super.createIcon(img, resources.getLabel(CLASS_NAME+"."+name+"Tip"), t, listener);   
  }

  public ImageHyperlink createIcon(String name, IHyperlinkListener listener){
    Image img = loadJarImage(name);
    return super.createIcon(img, resources.getLabel(CLASS_NAME+"."+name+"Tip"), listener);   
  }

  public org.eclipse.ui.forms.widgets.Hyperlink createLink(
                                                           String name, org.eclipse.ui.forms.events.IHyperlinkListener listener) {  
    return super.createLink(resources.getLabel(CLASS_NAME+"."+name), listener); 
  }

  public org.eclipse.ui.forms.widgets.Hyperlink createMenuLink(
                                                               String name, HyperlinkAdapter...adapters) {
    return super.createMenuLink(resources.getLabel(CLASS_NAME+"."+name), adapters);   
  } 


  public Label createLabel(String name){   
    return super.createLabel(resources.getLabel(this.CLASS_NAME+"."+name));
  }  

  public Label createLabel(int style, String name){
    return super.createLabel(style, resources.getLabel( this.CLASS_NAME+"."+name));
  }  

  public Group createGroup(String name, Object layoutData, Layout layout){
    if(name.length() > 0) name  = resources.getLabel( this.CLASS_NAME+"."+name);
    return super.createGroup(name, layoutData, layout);
  }

  public Group createGroup(Object layoutData, Layout layout){
    return super.createGroup("", layoutData, layout);
  }

  public Table createTable(String name, SelectionListener listener){
    return createTable(name, listener, -1);
  }

  public Spinner createSpinner(String name, int style){
    Spinner spinner = super.createSpinner(style);
    String value = resources.getLabel(CLASS_NAME+"."+name+"Tip");
    spinner.setToolTipText(value);
    return spinner;
  }

  public Table createTable(String name, SelectionListener listener, int style){
    String value = resources.getLabel(CLASS_NAME+"."+name+"Columns");
    String [] cols = value.split(",");
    value = resources.getLabel(CLASS_NAME+"."+name+"ColumnWidths");
    String [] split = value.split(",");
    int [] w = new int[ split.length];
    for( int i=0; i<w.length; i++){
      w[i] = Integer.parseInt(split[i]);
    }
    if(style < 0) return super.createTable(cols, w, listener);
    return super.createTableWithStyle(cols, w, listener, style);
  }

  public Combo createCombo(SelectionListener listener){
    Combo combo = super.createCombo( SWT.READ_ONLY);
    combo.addSelectionListener( listener);
    return combo;
  }

  public Menu createMenu(Menu menu, String name){
    return super.createMenu( menu, resources.getLabel(CLASS_NAME+"."+name));    
  }

  public MenuItem createMenuItem( Menu menu, String name){
    return super.createMenuItem( menu, resources.getLabel(CLASS_NAME+"."+name));    
  }

  public MenuItem createMenuItem(Menu menu, String name, SelectionListener listener) {
    if(name == null) return super.createMenuItem(menu, null, listener);
    return super.createMenuItem(menu, resources.getLabel(CLASS_NAME+"."+name), listener);   
  }

  public void createStyleMenuItem(Object parent, int style){
    if(parent instanceof Menu) {
      Menu menu = (Menu) parent;
      new MenuItem(menu, style);
      return;
    }

//    if(parent instanceof CMenu) {
//      CMenu cMenu = (CMenu) parent;
//
//      CMenuItem item = new CMenuItem(style);
//      cMenu.addItem(item);
//    }
  }

  public void createStyleMenuItem(Object parent, String name, String img, SelectionListener listener){
    if(parent instanceof Menu) {
      Menu menu = (Menu) parent;
      if(name == null) {
        MenuItem item = super.createMenuItem(menu, null, listener);
        item.setImage(imageLoader.load(composite.getDisplay(), img));
      } else {
        MenuItem item = super.createMenuItem(menu, resources.getLabel(CLASS_NAME+"."+name), listener);
        item.setImage(imageLoader.load(composite.getDisplay(), img));
      }
      return;
    }

//    if(parent instanceof CMenu) {
//      CMenu cMenu = (CMenu) parent;
//      if(name == null) {
//        CMenuItem item = new CMenuItem(SWT.NONE);
//        item.addSelectionListener(listener);
//        item.setImage(imageLoader.load(composite.getDisplay(), img));
//        cMenu.addItem(item);
//      } else {
//        CMenuItem item = new CMenuItem(resources.getLabel(CLASS_NAME+"."+name), SWT.NONE);
//        item.addSelectionListener(listener);
//        item.setImage(imageLoader.load(composite.getDisplay(), img));
//        cMenu.addItem(item);
//      }
//    }
  }

  public void createStyleMenuItem(Object parent, String name, SelectionListener listener) {
    if(parent instanceof Menu) {
      Menu menu = (Menu) parent;
      if(name == null) {
        super.createMenuItem(menu, null, listener);
      } else {
        if(!Character.isUpperCase(name.charAt(0))) {
          name = resources.getLabel(CLASS_NAME+"."+name);
        }
        super.createMenuItem(menu, name, listener);
      }
      return;
    }

//    if(parent instanceof CMenu) {
//      CMenu cMenu = (CMenu) parent;
//      if(name == null) {
//        CMenuItem item = new CMenuItem(SWT.NONE);
//        item.addSelectionListener(listener);
//        cMenu.addItem(item);
//      } else {
//        if(!Character.isUpperCase(name.charAt(0))) {
//          name = resources.getLabel(CLASS_NAME+"."+name);
//        }
//        CMenuItem item = new CMenuItem(name, SWT.NONE);
//        item.addSelectionListener(listener);
//        cMenu.addItem(item);
//      }
//    }
  }

  public static void createStyleMenuItem2(Object parent, String name, SelectionListener listener) {
    if(parent instanceof Menu) {
      Menu menu = (Menu) parent;
      MenuItem item = new MenuItem(menu, SWT.NONE);
      if(name != null) item.setText(name);
      if(listener != null) item.addSelectionListener(listener);
      return;
    }

//    if(parent instanceof PopupMenu) {
//      parent = ((PopupMenu)parent).getMenu();
//    }
//
//    if(parent instanceof CMenu) {
//      CMenu cMenu = (CMenu) parent;
//      if(name == null) {
//        CMenuItem item = new CMenuItem(SWT.NONE);
//        if(listener != null) item.addSelectionListener(listener);
//        cMenu.addItem(item);
//      } else {
//        CMenuItem item = new CMenuItem(name, SWT.NONE);
//        if(listener != null) item.addSelectionListener(listener);
//        cMenu.addItem(item);
//      }
//    }
  }

  public MenuItem createMenuItem(Menu menu, int style, String name, SelectionListener listener){
    if(name == null) return super.createMenuItem(menu, style, null, listener);
    return super.createMenuItem( menu, style, resources.getLabel(CLASS_NAME+"."+name), listener);   
  }

  public MenuItem createMenuItem(Menu menu, String name, ArmListener  listener){
    return super.createMenuItem( menu, SWT.CASCADE, resources.getLabel(CLASS_NAME+"."+name), listener);   
  }

  public MenuItem createMenuItem(Menu menu, String name, String img, SelectionListener listener){
    MenuItem  item = 
        super.createMenuItem( menu, resources.getLabel(CLASS_NAME+"."+name), listener);
    item.setImage(imageLoader.load(composite.getDisplay(), img));
    return item;
  }

  public MenuItem createMenuItem(Menu menu, int style, String name, String img, SelectionListener listener){
    MenuItem  item =
        super.createMenuItem( menu, style, resources.getLabel(CLASS_NAME+"."+name), listener);
    item.setImage(imageLoader.load( this.composite.getDisplay(), img));
    return item;  
  }

  public Menu createToolItem( final ToolBar bar, String img ){
    return super.createToolItem( bar, imageLoader.load( bar.getDisplay(), img));
  }

  public String getLabel(String key){  
    String value = resources.getRawLabel(key);
    if(value != null) return value;
    return resources.getLabel(this.CLASS_NAME+"."+key);
  }

  public String getRawLabel(String key){  
    return resources.getLabel(key);
  }

  public Image loadJarImage(String name){
    return imageLoader.load(composite.getDisplay(), resources.getLabel(CLASS_NAME+"."+name+"Image"));
  }

  public Image loadImage(String name){
    return imageLoader.load(composite.getDisplay(), name);
  }

  public String getTip(String name){
    return resources.getLabel(CLASS_NAME+"."+name+"Tip");
  } 

  public VTab createVTab(Composite com) {
    return createVTab(com, SWT.NONE);
  }

  public Tree createVTabTreeItem(VTab tab, String name) {
    name = getLabel(name);
    Tree tree = createTree(tab.getGroup(name));  
    tab.addItem(name, tree);   
    return tree;
  }

  public ExpandMenu createExpandMenu(VTab tab, String name) {
    name = getLabel(name);
    ExpandMenu menu = createExpandMenu(tab.getGroup(name), SWT.NONE);  
    tab.addItem(name, menu);   
    return menu;
  }

  public ExpandMenu createExpandMenu(Composite parent, String name) {
    name = getLabel(name);
    ExpandMenu menu = createExpandMenu(parent, SWT.NONE);  
    return menu;
  }

  public OutlookBarPane createOutlookBarItem(VTab tab, String name) {
    name = getLabel(name);    
    Composite parent  = tab.getGroup(name);
    parent.setLayout(new FillLayout());
    OutlookBarPane comp = new OutlookBarPane(parent);
    tab.addItem(name, comp);
    return comp;
  }

  public OutlookBarPane createOutlookBarItem(Composite parent) {
    OutlookBarPane comp = new OutlookBarPane(parent);
    return comp;
  }

  public Combo createVTabComboItem(VTab tab, String name) {
    name = getLabel(name);
    Composite comp = new Composite(tab.getGroup(name), SWT.NONE);
    comp.setBackground(tab.getBackground());
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;    
    composite.setLayout(gridLayout); 

    Combo cbo = new Combo(comp, SWT.READ_ONLY);
    cbo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Spinner spinPage = new Spinner(comp, SWT.BORDER);
    spinPage.setIncrement(1);
    spinPage.addModifyListener(new ModifyListener() {
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent event) {
      }
    });

    tab.addItem(name, comp);   
    return cbo;
  }  

  public Composite createVTabCompositeItem(VTab tab, String name) {
    name = getLabel(name);
    Composite comp = new Composite(tab.getGroup(name), SWT.NONE);
    comp.setBackground(tab.getBackground());
    tab.addItem(name, comp);  
    return comp;
  }

  private static boolean mozilla  = false;

  public static boolean isMozillaBrowser() { return mozilla; }

  public static Browser createBrowser(Composite parent, final Class<?> clazz) {
    try {
      String xulPath = System.getProperty("org.eclipse.swt.browser.XULRunnerPath");
      if(xulPath != null && xulPath.trim().length() > 0) {
        File file = new File(xulPath);
        System.setProperty("org.eclipse.swt.browser.XULRunnerPath", file.getAbsolutePath());
        mozilla = true;
        final Browser browser = new Browser(parent,  SWT.MOZILLA | SWT.BORDER);
        if(clazz == null) return browser;
        browser.addProgressListener (new ProgressAdapter () {
          @SuppressWarnings("unused")
          public void completed (ProgressEvent event) {
            nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser ();
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
                Menu menu = browser.getMenu();
                if(menu != null) menu.dispose();
                try {
                  BrowserMenu browserMenu = (BrowserMenu)clazz.newInstance();
                  browser.setMenu(browserMenu.createMenu(browser, event2));
                } catch (Exception e) {
                  ClientLog.getInstance().setMessage(browser.getShell(), e);
                }
              }
            };
            target.addEventListener ("contextmenu", listener, false);
          }
        });
        return browser;
      }

    } catch (Throwable e) {
      if(Shell.class.isInstance(parent)) {
        ClientLog.getInstance().setMessage(parent, new Exception(e.toString()));
      } else {
        ClientLog.getInstance().setMessage(parent.getShell(), new Exception(e.toString()));
      }
    }
    
    String os_name = System.getProperty("os.name").toLowerCase();
    String model = System.getProperty("sun.arch.data.model");
    
    if(os_name.indexOf("windows") > -1 && model.trim().equals("64")) {
      return new Browser(parent, SWT.NONE);
    }
    
    Browser widget = null;
//    ClientLog.getInstance().setMessage(parent.getShell(), new Exception("hehehe " + os_name+ "."+ model));
    try {
      widget = new Browser(parent, SWT.NONE);
    } catch (Error e) {
      widget = new Browser(parent, SWT.NONE);
      ClientLog.getInstance().setMessage(null, new Exception(e.toString()));
    }
//    return new Browser(parent, SWT.WEBKIT);
    return widget;
  }

}
