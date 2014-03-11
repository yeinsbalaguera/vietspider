/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.gui.browser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;
import org.vietspider.gui.config.Config;
import org.vietspider.gui.crawler.Crawler;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.log.LogViewer2;
import org.vietspider.gui.users.Organization;
import org.vietspider.gui.wizard.ChannelWizard;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.gui.workspace.XPWindowTheme;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.services.ImageLoader;
import org.vietspider.ui.widget.tabfolder.CTabFolder;
import org.vietspider.ui.widget.tabfolder.CTabFolder2Adapter;
import org.vietspider.ui.widget.tabfolder.CTabFolderEvent;
import org.vietspider.ui.widget.tabfolder.CTabItem;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 25, 2006
 */
public class TabBrowser extends CTabFolder {

  int itemMouse = -1;  
  private List<Composite> items;
  
//  private StatusBar statusBar;
  private Object tabMenu;
  private  int counter = 0;
  private Workspace workspace;
  private CTabItem selectedItem;
  
//  private Color       _bgFgGradient = ColorCache.getColor(255, 244, 234);
//  private Color       _bgBgGradient = ColorCache.getColor(255, 255, 255);
//  private Color       _borderColor  = ColorCache.getColor(40, 73, 97);
  
  protected Image       _oldImage;

  public TabBrowser(Workspace workspace_) {
    super(workspace_, SWT.BORDER);
    
    setBackgroundMode(SWT.INHERIT_DEFAULT);
    
    this.workspace = workspace_;  
    
    items = new ArrayList<Composite>(5);
    
//    addListener(SWT.Resize, new Listener() {
//      @SuppressWarnings("unused")
//      public void handleEvent(Event e) {
//        paint();
//      }
//    });

    Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
    int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;

    final DragSource source = new DragSource (this, operations);
    source.setTransfer(types);
    DragSourceAdapter dragListener = new DragSourceAdapter () {
      public void dragStart(DragSourceEvent event) {
        int selection = getSelectionIndex();
        if (selection > -1) {
          event.doit = true;
          itemMouse = selection;
        } else {
          event.doit = false;
        }
      };
      public void dragSetData (DragSourceEvent event) {
        event.data = getItem(itemMouse).getText();        
      }
    };
    source.addDragListener(dragListener);

    DropTarget target = new DropTarget(this, operations);
    target.setTransfer(types);
    DropTargetAdapter drogListener = new DropTargetAdapter() {
      public void dragOver(DropTargetEvent event) {
        event.feedback = DND.DROP_MOVE;
      }
      public void drop(DropTargetEvent event) {
        if (event.data == null) {
          event.detail = DND.DROP_NONE;          
          return;
        }     
        
        if (event.item != null)  return;          
        Point pt = getDisplay().map((Control)null, TabBrowser.this, event.x, event.y); 
        int idx = findTab( pt.x);
        if( idx < 0) return ;
        changeItem(itemMouse, idx);  
        setSelection( idx);
      }
    };
    target.addDropListener(drogListener);    
    
    addCTabFolder2Listener( new CTabFolder2Adapter(){
      @SuppressWarnings("unused")
      public void maximize(CTabFolderEvent event){
        if(tabMenu == null) return; 
        if(tabMenu instanceof Menu) {
          ((Menu)tabMenu).setVisible(true);
        }
        
      }   
      public void close(CTabFolderEvent event){
        closeItem(event.item);
        if(getItemCount() < 1 /*
            && ClientConnector2.currentInstance().getPermission() == User.ROLE_ADMIN*/) {
          try {
            createTool(AppBrowser.class, true, SWT.NONE);
          } catch (Exception e) {
            ClientLog.getInstance().setException(getShell(), e);
          }
        }
      }     
    });    
    
    addMouseListener(new MouseAdapter(){
      @SuppressWarnings("unused")
      public void mouseDoubleClick(MouseEvent e){
        try {
          createTool(AppBrowser.class, true, SWT.NONE);
        } catch (Exception exp) {
          ClientLog.getInstance().setException(getShell(), exp);
        } 
//        createItem(BrowserWidget.class);        
//        workspace.getToolbar().setText("");  
      }
    });
    
    addSelectionListener( new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        changeVisibleItem();
      }
    }); 
    
  }

  private int findTab( int x){
    if(getItemCount() < 1) return -1;
    CTabItem item = getItem(0);   
    int idx = -1;   
    for( int i = 0; i<getItemCount(); i++){
      item = getItem(i);
      Rectangle rec = item.getBounds();
      if( (x >= rec.x) && ( x <= rec.x +rec.width) ){       
        idx = i;
        break;
      }
    }
    return idx;
  }

  private void changeItem(int source, int des){  
    try{
      String text = getItem(source).getText();    
      getItem(source).setText(getItem(des).getText());
      getItem(des).setText(text);

      Image icon = getItem(source).getImage();
      getItem(source).setImage(getItem(des).getImage());
      getItem(des).setImage(icon);
      
      getItem(des).setControl(items.get(des));
      
      Composite compSource = items.get(source);
      Composite compDes = items.get(des);
      BrowserWidget browser;       
      getItem(source).setControl(compDes);
      if(compDes instanceof BrowserWidget){
        browser = (BrowserWidget)compDes;
        browser.setItem(getItem(source));
      }  
      items.set(source, items.get(des));
      
      if(compSource instanceof BrowserWidget){
        browser = (BrowserWidget)compSource;
        browser.setItem(getItem(des));
      }       
      getItem(des).setControl(compSource);
      items.set(des, compSource);

    } catch( Exception exp) {
      ClientLog.getInstance().setException(getShell(), exp);
    }
  }  
  
  public void closeAll(){    
    while(getItemCount() > 0){
      closeItem(0);
    }
    
//    if(ClientConnector2.currentInstance().getPermission() == User.ROLE_ADMIN) {
      try {
        createTool(AppBrowser.class, true, SWT.NONE);
      } catch (Exception e) {
        ClientLog.getInstance().setException(getShell(), e);
      }  
//    }
  }
  
  public void closeItem(Widget w){
    CTabItem item = (CTabItem)w;    
    int idx = -1;
    for( int i=0; i < getItemCount(); i++){
      if( item != getItem(i)) continue;
      idx = i;
      break;
    }
    if(idx > -1) closeItem(idx);
    changeVisibleItem();
  }

  public void closeItem(int idx){
    if(idx < items.size()) {
      Composite composite = items.get(idx);
      composite.dispose();
      items.remove(idx);
    }
    if(idx < getItemCount()) getItem(idx).dispose();
    changeVisibleItem();
//    statusBar.createPluginItems();
  }
  
  public void setSelectedItem(Object item) {
    for(int i = 0; i < Math.min(items.size(), getItems().length); i++) {
      if(items.get(i) == item) {
        setSelection(getItems()[i]);
      }
    }
  }
  
  public BrowserWidget createItem() {
    return createItem(BrowserWidget.class); 
  }

  @SuppressWarnings("unchecked")
  private <T extends BrowserWidget> T createItem(Class<T> clazz) { 
    if(selectedItem != null 
        && selectedItem.getControl() instanceof  AppBrowser) {
      closeItem(selectedItem);
    }
//    if(getItemCount() > 0 && 
//        AppBrowser.class.isInstance(getItem(0).getControl())) closeItem(0);
//    workspace.setMaxWindow(null);
    CTabItem item = new CTabItem(this, SWT.CLOSE);
    T browser = null;
    try {
      if(clazz == BrowserWidget.class) {
        browser = (T)new BrowserWidget(this, item, workspace);
      } else {
        Constructor<T> constructor = clazz.getConstructor(TabBrowser.class, CTabItem.class, Workspace.class);
        browser = constructor.newInstance(this, item, workspace);
      } 
      item.setControl(browser);
      items.add(browser); 
      browser.setId(counter);
      counter++;
      setSelection(getItemCount()-1);   
      try{
        ImageLoader imageLoader = new ImageLoader();
        Image image = imageLoader.load(item.getDisplay(), "offline.png");  
        if( image != null)  item.setImage( image);
      } catch (Exception e) {
      } 
    } catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), new Exception(clazz.getName()));
      ClientLog.getInstance().setException(getShell(), e);
    } 
    changeVisibleItem();
//    statusBar.createPluginItems();
    return browser;
  }  
  
  public BrowserWidget getSelected(){
    if(items.size() < 1) createItem(BrowserWidget.class);
    int idx = 0;
    if(getSelectionIndex() >= 0 ) idx = getSelectionIndex();
    idx = getSelectionIndex();
    while(idx >= items.size()) idx--;
    if(idx < 0) return null;
    Composite item = items.get(idx);
    if(item instanceof BrowserWidget) return (BrowserWidget)item;
    return null;
  }

  public void setTabMenu(Object tabMenu) { this.tabMenu = tabMenu; }
  
  public void setUrl(String url){
    if(url.trim().length() == 0) return;   
    BrowserWidget browser = getSelected();
    if(browser == null) browser = createItem(BrowserWidget.class);   
    browser.setUrl(url);
  }
  
  final public ControlComponent createTool(
      Class<? extends ControlComponent> clazz, boolean checkExists, int style) throws Exception {
    if(getSelection() != null 
        && clazz != AppBrowser.class
        && (getSelection().getControl() instanceof  AppBrowser)) {
      closeItem(getSelection());
      selectedItem = null;
    }
    
//    workspace.setMaxWindow(this);
    if(checkExists){
      for(int i=0; i<items.size(); i++){
        if(clazz.isInstance(items.get(i))) {
          setSelection(i);
          return (ControlComponent)items.get(i);
        }
      }
    }
    
    ControlComponent object = null;
    try {
      if(clazz == Creator.class) {
        object = new Creator(this, workspace);
      } else if(clazz == ChannelWizard.class) {
        object = new ChannelWizard(this, workspace);
      } else if(clazz == BrowserWidget.class) {
        object = new BrowserWidget(this, workspace);
      } else if(clazz == Crawler.class) {
        object = new Crawler(this, workspace);
      } else if(clazz == Organization.class) {
        object = new Organization(this, workspace);
      } else if(clazz == LogViewer2.class) {
        object = new LogViewer2(this, workspace);
//      } else if(clazz == WebsiteStore.class) {
//        object = new WebsiteStore(this, workspace);
      } else if(clazz == AppBrowser.class) {
        object = new AppBrowser(this, workspace);
      } else if(clazz == Config.class) {
        object = new Config(this, workspace);
//      } else if(clazz == SourceMonitor.class) {
//        object = new SourceMonitor(this, workspace);
//      } else if(clazz == BrowserExplorer.class) {
//        object = new BrowserExplorer(this, workspace);
      } else {
        Exception exp = new Exception(clazz.getName()+ ":Unknown component!");
        ClientLog.getInstance().setException(getShell(), exp);
        return null;
      }
      
      XPWindowTheme.setBackgroup(object, getShell().getBackground());
    } catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
      return null;
    }
    
    CTabItem item = new CTabItem(this, style);
//    item.setShowClose(false);
    
    try { 
      ClientRM resources = new ClientRM("VietSpider");
      item.setText(resources.getLabel(clazz.getSimpleName()));
      item.setControl(object);
      items.add(object);
      setSelection(getItemCount()-1);
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }
    
    try {
      Method method = clazz.getDeclaredMethod("setTabItem", new Class[]{CTabItem.class});
      method.invoke(item.getControl(), new Object[]{item});
    } catch (Exception e) {
    }
    
    try {
      ImageLoader imageLoader = new ImageLoader();
      Image image = imageLoader.load(item.getDisplay(), object.getNameIcon());  
      if(image != null)  item.setImage(image);
    } catch (Exception e) {
    } 
    changeVisibleItem();
    return object;
  }

  public int getCounter() { return counter; }

  public Workspace getWorkspace() { return workspace; }
  
  public void changeVisibleItem() {
    if(selectedItem != null && !selectedItem.isDisposed()) {
      invisibleItem(selectedItem);
    } 
    
    selectedItem = getSelection();
    if(selectedItem != null && !selectedItem.isDisposed()) {
      visibleItem(selectedItem);
    } 
  }
  
  public void invisibleItem(CTabItem item) {
    Class<?> clazz = item.getControl().getClass();
    try {
      Method method = clazz.getDeclaredMethod("invisibleItem", new Class[0]);
      method.invoke(item.getControl(), new Object[0]);
    } catch (Exception e) {
    }
  }
  
  public void visibleItem(CTabItem item) {
    if(item == null || item.getControl() == null) return;
    Class<?> clazz = item.getControl().getClass();
    try {
      Method method = clazz.getDeclaredMethod("visibleItem", new Class[0]);
      method.invoke(item.getControl(), new Object[0]);
    } catch (Exception e) {
    }
  }
  
  @SuppressWarnings("unchecked")
  public <T> List<T> getItems(Class<T> clazz) {
    List<T> values = new ArrayList<T>();
    for(int i = 0; i < items.size(); i++) {
      if(clazz.isInstance(items.get(i))) {
        values.add((T)items.get(i));
      }
    }
    return values;
  }
  
  
}
