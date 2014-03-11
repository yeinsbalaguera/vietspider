/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.source;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.ExportSourceDialog;
import org.vietspider.gui.creator.ImportSourceDialog;
import org.vietspider.gui.creator.RenameDialog;
import org.vietspider.gui.creator.SendSource;
import org.vietspider.gui.creator.SendSourceWorker;
import org.vietspider.gui.creator.SetDepthDialog;
import org.vietspider.gui.creator.SetPriorityDialog;
import org.vietspider.gui.creator.SetPropertiesDialog;
import org.vietspider.gui.creator.action.LoadGroupCategorySource;
import org.vietspider.gui.creator.action.LoadGroupCategorySource.GroupCategorySource;
import org.vietspider.model.Group;
import org.vietspider.model.Source;
import org.vietspider.net.server.CopySource;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 19, 2007  
 */
public abstract class SourcesHandler extends Composite  implements GroupCategorySource {

  protected String group = Group.ARTICLE;
  protected String template = null;

  //  private Menu menu ;
  private Object menu;

  //  private MenuItem itemSend, itemCopy, itemPaste, itemCut, itemRename;
  //  private MenuItem itemSetPriority, itemSetDepth, itemSetProperties;
  //  private MenuItem itemSaveConfigAsName, itemCreateCategory, itemRestore;
  //  private MenuItem itemExport, itemImport, itemRemote;

  public SourcesHandler(Composite parent) {
    super(parent, SWT.BORDER);
  }

  public abstract String  getSelectedCategory() ;

  abstract String[]  getSelectedCategories() ;

  public abstract String[] getSelectedSources() ;

  abstract String[] getSources(String category) ;

  protected void createMenu(Control control, ApplicationFactory factory) {
//    if(XPWidgetTheme.isPlatform()) {  
//      PopupMenu popupMenu = new PopupMenu(control, XPWidgetTheme.THEME);
//      menu = new CMenu();
//      popupMenu.setMenu((CMenu)menu);
//    } else {
      menu = new Menu(getShell(), SWT.POP_UP);
      control.setMenu((Menu)menu);
//    }

    factory.createStyleMenuItem(menu, "itemCopy", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        copySources(false);
      }
    });

    factory.createStyleMenuItem(menu, "itemCut", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        copySources(true);
      }
    });

    factory.createStyleMenuItem(menu, "itemPaste", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        pasteSources();
      }
    });

    factory.createStyleMenuItem(menu, "itemRename", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String category = getSelectedCategory();
        if(category == null) return;

        String [] sources = getSelectedSources();
        if(sources == null || sources.length < 1)  return;

        CopySource copy = new CopySource();
        copy.setSrcGroup(group);
        copy.setSrcCategory(category);
        copy.setSrcNames(sources);
        new RenameDialog(getShell(), SourcesHandler.this, copy);
      }
    });


    //    if(Application.LICENSE != Install.PERSONAL) {
    factory.createStyleMenuItem(menu, SWT.SEPARATOR);

    //      if(Application.LICENSE  != Install.PERSONAL 
    //          && Application.LICENSE != Install.PROFESSIONAL) {
    factory.createStyleMenuItem( menu, "itemSend", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        sendSources();
      }
    });
    //      }

    /* itemSetPriority = factory.createMenuItem( menu, "itemSetTemplate", new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          setTemplate();
        }
      });*/

    factory.createStyleMenuItem( menu, "itemSetDepth", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        setDepth();
      }
    });

    factory.createStyleMenuItem( menu, "itemSetPriority", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        setPriority();
      }
    });

    factory.createStyleMenuItem( menu, "itemSetProperties", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        setProperties();
      }
    });

    factory.createStyleMenuItem( menu, "itemSaveConfigAsName", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        saveConfigSource(true);
      }
    });
    //    }//end License

    /*factory.createMenuItem( menu, "itemSaveConfigAsCategory", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        saveConfigSource(false);
      }
    });
     */

    factory.createStyleMenuItem(menu, SWT.SEPARATOR);

    factory.createStyleMenuItem(menu, "itemCreateCategory", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new CreateCategoryDialog(SourcesHandler.this);
      }
    });

    factory.createStyleMenuItem(menu, "itemDelete", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeSources();
      }
    });

    factory.createStyleMenuItem(menu, "itemRestore", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        restoreSources();
      }
    });

    //    if(Application.LICENSE != Install.PERSONAL) {
    factory.createStyleMenuItem(menu, SWT.SEPARATOR);

    factory.createStyleMenuItem(menu, "itemExport", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        export();
      }
    });

    factory.createStyleMenuItem(menu, "itemImport", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String _group = getGroup();
        String category = getSelectedCategory();
        new ImportSourceDialog(getShell(), SourcesHandler.this, _group, category);
      }
    });
    //    }

    factory.createStyleMenuItem( menu, "itemRemote", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        RemoteSourceDownloader downloader =
          new RemoteSourceDownloader(getShell(), SourcesHandler.this);
        downloader.setLocalGroup(getGroup());
        downloader.setLocalCategory(getSelectedCategory());
        
        //loadRemote();
        //        RemoteSourcesViewer2 remoteViewer =
        //          new RemoteSourcesViewer2(getShell(), SourcesHandler.this);
        //        remoteViewer.setLocalGroup(getGroup());
        //        remoteViewer.setLocalCategory(getSelectedCategory());
      }
    });

    factory.createStyleMenuItem(menu, SWT.SEPARATOR);

    factory.createStyleMenuItem( menu, "itemUpdate", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        update();
      }
    });
  }

  //  private void loadRemote(){
  //
  //    Shell shell = new Shell(SWT.TITLE);
  //    shell.setLocation(this.getShell().getLocation().x+430, this.getShell().getLocation().y+100); 
  //    shell.setLayout(new FillLayout());
  //    TabFolder tabfolder = new TabFolder(shell,SWT.BOLD);
  //    tabfolder.setFont(UIDATA.FONT_10B);
  //    
  //    RemoteSourcesViewer2 remoteViewer =
  //      new RemoteSourcesViewer2(tabfolder, SourcesHandler.this);
  //    TabItem item = new TabItem(tabfolder,SWT.BOLD);
  //    item.setText("Tester");
  //    item.setControl(remoteViewer);
  //    
  //    shell.setSize(400,420);
  //    shell.setVisible(true);
  //    
  //  }

  public void update() {
    new File(ClientConnector2.getCacheFolder("sources/type"), "group." + group).delete();
    new LoadGroupCategorySource(this, new Worker[0], group, getSelectedCategory(), getSelectedSources());
  }

  protected void removeSources() {
    MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
    ClientRM resource = new ClientRM("Creator");
    msg.setMessage(resource.getLabel(getClass().getName()+".msgAlertDelete"));
    if(msg.open() != SWT.YES) return;  

    File file = new File(ClientConnector2.getCacheFolder("sources/type"), "group." + group);
    file.delete();

    String [] sources = getSelectedSources();
    if(sources == null || sources.length < 1)  {
      String [] categories = getSelectedCategories();
      if(categories == null || categories.length < 1) return;
      try {
        new SourcesClientHandler(group).deleteCategories(categories);
      } catch (Exception e) {
        ClientLog.getInstance().setException(getShell(), e);
      }
      new LoadGroupCategorySource(this, new Worker[0], group, null);
      return ;
    } 

    String category = getSelectedCategory();
    if(category == null) return;
    try {
      new SourcesClientHandler(group).deleteSources(category, sources);
    } catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
    }
    new LoadGroupCategorySource(this, new Worker[0], null, category);
  }

  protected void restoreSources() {
    MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
    ClientRM resource = new ClientRM("Creator");
    msg.setMessage(resource.getLabel("msgAlertRestore"));
    if(msg.open() != SWT.YES) return;    

    String [] sources = getSelectedSources();


    if(sources == null || sources.length < 1)  {
      String [] categories = getSelectedCategories();
      if(categories == null || categories.length < 1) return;
      for(String category : categories) {
        try {
          sources = new SourcesClientHandler(group).loadSources(category);
          for(int i = 0; i < sources.length; i++) {
            int idx = sources[i].indexOf('.');
            if(idx < 0) continue;
            sources[i] = sources[i].substring(idx+1);
          }
          restoreSources(category, sources);
        } catch (Exception e) {
          ClientLog.getInstance().setException(getShell(), e);
        }
      }

      try {
        new SourcesClientHandler(group).deleteCategories(categories);
      } catch (Exception e) {
        ClientLog.getInstance().setException(getShell(), e);
      }

      new LoadGroupCategorySource(this, new Worker[0], group, null);
      return ;
    } 

    String category = getSelectedCategory();
    if(category == null) return;
    try {
      restoreSources(category, sources);
      new SourcesClientHandler(group).deleteSources(category, sources);
    } catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
    }
    new LoadGroupCategorySource(this, new Worker[0], null, category);
  }

  protected void restoreSources(String category, String...names) throws Exception {
    for(String name : names) {
      Source source = null;
      try {
        source = new SourcesClientHandler(group).loadSource(category, name);
      } catch (Exception e) {
        ClientLog.getInstance().setException(getShell(), e);
      }
      if(source == null) continue;
      new SourcesClientHandler(source.getGroup()).saveSource(source);
    }
  }

  protected void copySources(boolean isDelete) {
    String category = getSelectedCategory();
    if(category == null) return;

    String [] sources = getSelectedSources();
    if(sources == null || sources.length < 1)  sources = getSources(category);

    CopySource copy = new CopySource();
    copy.setDelete(isDelete);
    copy.setSrcGroup(group);
    copy.setSrcCategory(category);
    copy.setSrcNames(sources);

    String value = "";
    try {
      value =  Object2XML.getInstance().toXMLDocument(copy).getTextValue();
    }catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
      return;
    }

    Clipboard clipboard = new Clipboard(getShell().getDisplay());
    TextTransfer textTransfer = TextTransfer.getInstance();
    clipboard.setContents(new Object[]{value}, new Transfer[]{textTransfer});
  }

  protected void pasteSources() {
    String [] categories = getSelectedCategories();
    if(categories == null || categories.length < 1) return;

    TextTransfer transfer = TextTransfer.getInstance();
    Clipboard clipboard = new Clipboard(getShell().getDisplay());
    String xml = (String)clipboard.getContents(transfer);
    CopySource copy = null;
    try {
      XMLDocument document = XMLParser.createDocument(xml, null);
      copy = XML2Object.getInstance().toObject(CopySource.class, document);
    }catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
      return;
    }
    if(copy == null) return;

    copy.setDesCategories(categories);
    copy.setDesGroup(group);

    try {
      new SimpleSourceClientHandler().copySources(copy);
      new LoadGroupCategorySource(this, new Worker[0], null, categories[categories.length-1]);
    }catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
    }
  }

  protected void saveConfigSource(boolean asName) {
    String category = getSelectedCategory();
    if(category == null) return;
    String [] sources = getSelectedSources();
    if(sources.length < 1) return;

    try {
      if(asName) {
        new SourcesClientHandler(group).saveConfigAsName(category, sources[0]);
      } else {
        new SourcesClientHandler(group).saveConfigAsCategory(category, sources[0]);
      }
    }catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
    }
  }


  protected void sendSources() {
    String category = getSelectedCategory();
    if(category == null || category.trim().isEmpty()) return;

    String [] sources = getSelectedSources();
    if(sources == null || sources.length < 1)  sources = getSources(category);

    sendSources(category, sources);
  }

  protected void setPriority() {
    String category = getSelectedCategory();
    if(category == null || category.trim().isEmpty()) return;

    String [] sources = getSelectedSources();
    if(sources == null || sources.length < 1)  sources = getSources(category);

    new SetPriorityDialog(getShell(), group, category, sources);
  }

  protected void setProperties() {
    String category = getSelectedCategory();
    if(category == null || category.trim().isEmpty()) return;

    String [] sources = getSelectedSources();
    if(sources == null || sources.length < 1)  sources = getSources(category);

    new SetPropertiesDialog(getShell(), group, category, sources);
  }

  protected void setDepth() {
    String category = getSelectedCategory();
    if(category == null || category.trim().isEmpty()) return;

    String [] sources = getSelectedSources();
    if(sources == null || sources.length < 1)  sources = getSources(category);

    new SetDepthDialog(getShell(), group, category, sources);
  }

  protected void export() {
    String [] categories = getSelectedCategories();
    String [] sources = getSelectedSources();
    new ExportSourceDialog(getShell(), group, categories, sources);
  }

  protected void sendSources(String category, String ... names) {
    Shell window = new Shell(getShell(), SWT.MAX | SWT.APPLICATION_MODAL);
    window.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        e.doit = false;
      }
    });
    window.setLayout(new FillLayout());
    new SendSource(window, new SendSourceWorker(group, category, names));
    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 200)/ 2;
    window.setLocation(x, y);
    window.pack();
//    XPWindowTheme.setWin32Theme(window);
    window.open();
  }

  public String getGroup() { return group; }

  /* public void setIsTemplate(boolean value) {
    if(menu == null) return;
    MenuItem [] items = menu.getItems();
    for(int i = 0; i < items.length; i++) {
      if(i == 3 || i == 4 || i == 9 || i == 11) continue;
      items[i].setEnabled(!value);
    }
  }*/

  /*protected void setTemplate() {
    if(template == null) return;
    final String category = getSelectedCategory();
    if(category == null || category.trim().isEmpty()) return;

    final String [] names = getSelectedSources();
    if(names == null || names.length < 1) return;

    Worker excutor = new Worker() {

      private String message = null;

      public void abort() {
        ClientConnector.currentInstance().abort();
      }

      public void before() {}

      public void execute() {
        try {
          SourcesClientHandler client = new SourcesClientHandler();
          for(String name : names) {
            Source source = client.loadSource(group, category, name);
            if(source == null) continue;            
            client.saveSource(source, template);
          }
        } catch (Exception e) {
          if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
            message = e.getMessage();
          } else {
            message = e.toString();
          }
        }
      }

      public void after() {
        if(message != null && !message.trim().isEmpty()) {
          MessageBox msg = new MessageBox (menu.getShell(), SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
        }
      }
    };
    WaitLoading loading = new WaitLoading(this, excutor);
    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 125) / 2;
    int y = (displayRect.height - 100)/ 2;
    loading.getShell().setLocation(x, y);
    loading.open();
  }*/

  //  protected boolean checkAccess(String [] categories, String value) {
  //    if(categories == null) return true;
  //    for(String category : categories) {
  //      if(category == null || category.trim().isEmpty()) continue;
  //      if(category.length() == 1 && category.charAt(0) == '*') return true;
  //      if(category.endsWith("."+value)) return true;
  //    }
  //    return false;
  //  }

  public void setGroup(String group) { 
    this.group = group;
    
    enabledMenu();

    if(group.equals(Group.DUSTBIN)) {
      disabledMenu(new int[]{0,1,2,3,4,5,6,7,8,9,10,11,14,15,16,17});
    } else {
      disabledMenu(new int[]{13});
    }

    if(Application.LICENSE  == Install.PERSONAL 
        || Application.LICENSE == Install.PROFESSIONAL) {
      disabledMenu(new int[]{5});
    }
    
    if(Application.LICENSE  == Install.PERSONAL) {
      disabledMenu(new int[]{6,7,8,9,15,16});
    }
  }

  private void disabledMenu(int values[]) {
    if(menu instanceof Menu) {
      MenuItem [] items = ((Menu)menu).getItems();
      for(int i = 0; i < items.length; i++) {
        if(isDisabledIndex(i, values)) {
          items[i].setEnabled(false);
        }
      }
    } 
    
//    else if(menu instanceof CMenu) {
//      CMenu cmenu = (CMenu)menu;
//      for(int i = 0; i < cmenu.getItemCount(); i++) {
//        if(isDisabledIndex(i, values)) {
//          cmenu.getItem(i).setEnabled(false);
//        } 
//      }
//    }
  }

  private void enabledMenu() {
    if(menu instanceof Menu) {
      MenuItem [] items = ((Menu)menu).getItems();
      for(int i = 0; i < items.length; i++) {
        items[i].setEnabled(true);
      }
    }
    
//    else if(menu instanceof CMenu) {
//      CMenu cmenu = (CMenu)menu;
//      for(int i = 0; i < cmenu.getItemCount(); i++) {
//        cmenu.getItem(i).setEnabled(true);
//      }
//    }
  }

  private boolean isDisabledIndex(int index, int [] values) {
    for(int i = 0; i < values.length; i++) {
      if(values[i] == index) return true;
    }
    return false;
  }

  public void setTemplate(String template) { this.template = template; }
}

