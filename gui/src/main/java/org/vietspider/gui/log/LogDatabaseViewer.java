/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.vietspider.chars.TextSpliter;
import org.vietspider.client.ClientPlugin;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.CommonFileFilter;
import org.vietspider.common.io.RWData;
import org.vietspider.common.text.CalendarUtils;
import org.vietspider.common.text.NameConverter;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.log.action.DisableSourceHandler;
import org.vietspider.gui.log.action.ExportSourceLogHandler;
import org.vietspider.gui.log.action.ViewLogHandler;
import org.vietspider.gui.monitor.MonitorStatusBar;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.BareBonesBrowserLaunch;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;
import org.vietspider.ui.widget.images.ToolbarResource;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 20, 2010  
 */
public class LogDatabaseViewer extends LogTextViewer {

  protected Table listSource;
  protected TabFolder mainTab;
  protected Table tblSummary;
  protected Browser browser;
  
  private Color [] colors;

  public LogDatabaseViewer(Composite parent, Workspace workspace, String folder) {
    super(parent, workspace, folder);
    
    colors = new Color[]{
        new Color(getDisplay(), 255, 0, 0),
        new Color(getDisplay(), 215, 167, 0),
        new Color(getDisplay(), 0, 0, 0),
        new Color(getDisplay(), 200, 200, 200)
    };
  }

  protected void initUI() {
    String clazzName  = "org.vietspider.gui.monitor.Monitor";
    ApplicationFactory factory = new ApplicationFactory(this, "Monitor", clazzName);

    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);

    SashForm sashMain = new SashForm(this, SWT.HORIZONTAL);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    sashMain.setLayoutData(gridData);
    sashMain.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

    Menu menu1 = new Menu(getShell(), SWT.POP_UP);
    factory.createMenuItem(menu1,"menuMonitor",new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt){
        loadDate("track/logs/sources/", new CommonFileFilter.Folder());
        //        new LoadSourceDateHandler(LogDatabaseViewer.this);
      }
    });

    listDate = factory.createList(sashMain, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL, new String[]{});  
    listDate.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        String dateFolder = getDateFolder();
        if(dateFolder == null) return ;
        new ExportSourceLogHandler(LogDatabaseViewer.this, dateFolder);
      }
    }); 
    listDate.setMenu(menu1);

    factory.setComposite(sashMain);
    listSource = factory.createTable("tableSource",  null, SWT.FULL_SELECTION | SWT.SINGLE);  
    listSource.setLinesVisible(true);
    listSource.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        selectSourceItem2();
      }

      @SuppressWarnings("unused")
      public void mouseDoubleClick(MouseEvent arg0) {
        spinPage.setSelection(1);
        viewLog(true);
      }
    });

    Menu menu2 = new Menu(getShell(), SWT.POP_UP);
    factory.createMenuItem(menu2, "menuEditSource", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt){
        editSource(null);
      }
    });
    listSource.setMenu(menu2);

    initViewer(factory, sashMain);

    sashMain.setWeights(new int[]{8, 15, 81});

    loadDate("track/logs/sources/", new CommonFileFilter.Folder());
  }
  
  protected void initViewer(ApplicationFactory factory, Composite parent) {
    mainTab = new TabFolder(parent, SWT.TOP);
    mainTab.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
    
    TabItem item;
    
    item = new TabItem(mainTab, SWT.NONE);
    item.setText("Summary");
    
    factory.setComposite(mainTab);
    tblSummary = factory.createTable("tableSummary",  null, SWT.FULL_SELECTION | SWT.SINGLE);  ;
    item.setControl(tblSummary);
    
    tblSummary.addMouseListener(new MouseAdapter() {
      
      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        selectSourceItem();
      }

      @SuppressWarnings("unused")
      public void mouseDoubleClick(MouseEvent arg0) {
        spinPage.setSelection(1);
        viewLog(true);
      }
    });
    
    Composite mainComposite = new Composite(mainTab, SWT.NONE);
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    mainComposite.setLayout(gridLayout);
    
    item = new TabItem(mainTab, SWT.NONE);
    item.setText("Log");
    item.setControl(mainComposite);

    Composite topComposite = new Composite(mainComposite, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END);
    topComposite.setLayoutData(gridData);

    gridLayout = new GridLayout(3, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    topComposite.setLayout(gridLayout);

    factory.setComposite(topComposite);

    cboFilter = factory.createCombo(SWT.BORDER);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    gridData.widthHint = 250;
    cboFilter.setLayoutData(gridData);
    cboFilter.addKeyListener( new KeyAdapter(){
      @Override
      public void keyPressed(KeyEvent e){ 
        if(e.keyCode == SWT.CR) viewLog(false);
      }
    });

    cboFilter.setItems(new String[]{"USER", "SOURCE", "APPLICATION", "SOURCE&ERROR"});

    Composite pageComposite = new Composite(topComposite, SWT.NONE);
    factory.setComposite(pageComposite);

    gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    pageComposite.setLayout(gridLayout);

    factory.createLabel("lblPage");

    spinPage = factory.createSpinner(SWT.BORDER);
    spinPage.setCursor(new Cursor(spinPage.getDisplay(), SWT.CURSOR_HAND));
    spinPage.setIncrement(1);
    gridData = new GridData();
    gridData.widthHint = 30;
    gridData.heightHint = 16;
    spinPage.setLayoutData(gridData);
    spinPage.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        new ViewLogHandler(LogDatabaseViewer.this, spinPage.getSelection());
      }
    });
//    spinPage.addModifyListener(new ModifyListener() {
//      @SuppressWarnings("unused")
//      public void modifyText(ModifyEvent event) {
//        //new ViewLogHandler(LogDatabaseViewer.this, spinPage.getSelection());
//      }
//    });

    factory.setComposite(topComposite);

    final ToolbarResource resources = ToolbarResource.getInstance();
    butGo = resources.createIcon(topComposite, 
        resources.getImageGo(), resources.getTextGo(), new HyperlinkAdapter(){ 
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        butGo.setImage(resources.getImageGo());
      }
      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
        butGo.setImage(resources.getImageGo());
      }
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
        butGo.setImage(resources.getImageGo());
      }
    }); 
    butGo.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        viewLog(false);
      }

      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        butGo.setImage(resources.getImageGo1());
        butGo.redraw();
      }
    });

    FormToolkit toolkit = new FormToolkit(getDisplay());
    
    scrolled = new ScrolledComposite(mainComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    rtext = toolkit.createFormText(scrolled, false);
    rtext.setFont(UIDATA.FONT_11);

    rtext.addHyperlinkListener(new org.eclipse.ui.forms.events.HyperlinkAdapter() {
      public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
        editSource(e.getHref().toString());
      }
    });

    scrolled.setLayoutData(new GridData(GridData.FILL_BOTH));
    scrolled.setContent(rtext);
    scrolled.setExpandHorizontal(true);
    scrolled.setExpandVertical(true);

    scrolled.addControlListener(new ControlAdapter() {
      @SuppressWarnings("unused")
      public void controlResized(ControlEvent e) {
        Rectangle r = scrolled.getClientArea();
        scrolled.setMinSize(rtext.computeSize(r.width, SWT.DEFAULT));
      }
    });
    
    browser = ApplicationFactory.createBrowser(mainTab, null);
    item = new TabItem(mainTab, SWT.NONE);
    item.setText("Quick View");
    item.setControl(browser);
    
    statusBar = new MonitorStatusBar(workspace, this, ClientPlugin.LOG);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    statusBar.setLayoutData(gridData); 
  }
  
  private void selectSourceItem() {
    if(tblSummary.getSelectionCount() < 1) return;
    String fullName = tblSummary.getSelection()[0].getText(0);
    String [] elements = fullName.split("\\.");
    TableItem [] items = listSource.getItems();
    for(int i = 0; i < items.length; i++) {
      if(items[i].getText(0).equals(elements[0]) 
          && items[i].getText(1).equals(elements[1]) 
          && items[i].getText(2).equals(elements[2]) ) {
        listSource.setSelection(i);
        return;
      }
    }
  }
  
  private void selectSourceItem2() {
    if(listSource.getSelectionCount() < 1) return;
    TableItem item = listSource.getSelection()[0];
    StringBuilder builder = new StringBuilder(item.getText(0));
    builder.append('.').append(item.getText(1));
    builder.append('.').append(item.getText(2));
    
    String fullName = builder.toString();
    
    TableItem [] items = tblSummary.getItems();
    for(int i = 0; i < items.length; i++) {
      if(items[i].getText(0).equals(fullName) ) {
        tblSummary.setSelection(i);
        return;
      }
    }
  }

  public void removeAllSources()  { listSource.removeAll(); }
  public void removeSummary()  { tblSummary.removeAll(); }

  public String getFolder() {
    String dateFolder = getDateFolder();
    if(dateFolder == null) return null;
    return folder +  dateFolder ;
  }

  private String getDateFolder() {
    if(listDate.getSelectionCount() < 1) return null;
    SimpleDateFormat dateFormat = CalendarUtils.getDateFormat();
    try {
      Date dateInstance = dateFormat.parse(listDate.getSelection()[0]);
      return CalendarUtils.getFolderFormat().format(dateInstance)+ "/";
    } catch (Exception e) {
      ClientLog.getInstance().setThrowable(null, e);
    }
    return null;
  }

  public void addSource(String fullName) {
    String[] elements = fullName.split("\\.");
    if(elements.length < 3) return;

    TableItem item = new TableItem(listSource, SWT.NONE);      
    item.setFont(UIDATA.FONT_8V);
    item.setText(0, elements[2]);
    item.setText(1, elements[1]);
    item.setText(2, elements[0]);
    if(elements.length < 4)  return;
    item.setText(3, elements[3]);
  }
  
  public void setSummary(String[] lines) {
    TextSpliter spliter = new TextSpliter();
    for(String line : lines) {
      String[] elements = spliter.toArray(line, '|');
      if(elements.length < 10) return;

      TableItem item = new TableItem(tblSummary, SWT.NONE);      
      item.setFont(UIDATA.FONT_8V);
      for(int i = 0; i < elements.length-1; i++) {
        item.setText(i, elements[i]);
      }
      item.setText(elements.length-1, convert(Long.parseLong(elements[elements.length-1])));
      
      item.setForeground(0, getForeground(elements[1]));
      item.setForeground(2, getForeground(elements[1]));
    }
    mainTab.setSelection(0);
  }
  
  private Color getForeground(String svalue) {
    double score = 100;
    try {
      score = Double.parseDouble(svalue.trim());
    } catch (Exception e) {
      return colors[2];
    }
    if(score == 0) return colors[3];
    if(score < 0) return colors[0];
    if(score > 0 && score < 10) return colors[1];
    return colors[2];
  }
  
  private String convert(long downloaded) {
    if(downloaded < 1024) {
      return String.valueOf(downloaded) +" Bytes";
    } else if(downloaded >= 1024 && downloaded < 1024*1024l) {
      String value = String.valueOf((double)downloaded/1024); 
      int idx = value.indexOf('.');
      value = value.substring(0, Math.min(idx+3, value.length()));
      return  value +" KB";
    } else if(downloaded >= 1024*1024 && downloaded < 1024*1024*1024l) {
      String value = String.valueOf((double)downloaded/(1024*1024l)); 
      int idx = value.indexOf('.');
      value = value.substring(0, Math.min(idx+3, value.length()));
      return value + " MB";
    } else {
      String value = String.valueOf((double)downloaded/(1024*1024*1024l)); 
      int idx = value.indexOf('.');
      value = value.substring(0, Math.min(idx+3, value.length()));
      return value+" GB";
    }
  }

  public String getFileName() {
    if(listSource.getSelectionCount() < 1) return null;
    TableItem [] items = listSource.getSelection();
    String name  = NameConverter.encode(items[0].getText(0));
    String cate  = NameConverter.encode(items[0].getText(1));
    String group  = NameConverter.encode(items[0].getText(2));
    String index = items[0].getText(3);
    if(index.trim().length() > 0) {
      return group + "." + cate + "." + name + "." + index.trim();
    }
    return group + "." + cate + "." + name;
  }
  
  public String getFullName() {
    if(listSource.getSelectionCount() < 1) return null;
    TableItem [] items = listSource.getSelection();
    String name  = items[0].getText(0);
    String cate  = items[0].getText(1);
    String group  = items[0].getText(2);
    return group + "." + cate + "." + name;
  }

  public void setFile(File file) {
    String home = null;
    StringBuilder buf = new StringBuilder();

    buf.append("<form>");
    buf.append("<p>");
    try {
      String fullName = getFullName();
      buildSourceAction(buf, fullName);
      buf.append("<br/><br/>");

      byte [] bytes = RWData.getInstance().load(file);
      String text = new String(bytes, Application.CHARSET);
      String [] elements = text.split("\n");
      for(String line : elements) {
        buf.append("\n");
        if(line.toLowerCase().startsWith("http")) {
          int index = line.indexOf("<br/>");
          line = line.substring(0, index);
          buf.append("<a href=\"#").append(line).append("\">");
          buf.append(line).append("</a>     ");
          
          buf.append("<a href=\"^").append(line).append("\">Edit</a>     ");

          buf.append("<a href=\"").append(line).append("\">Test</a>     ");

          if(home == null) {
            int idx = line.indexOf('/', 8);
            if(idx > 0) home = line.substring(0, idx);
          }
          if(home != null) {
            buf.append("<a href=\"#").append(home).append("\">Website</a>\t"); 
          }

          buf.append("<br/>");
        } else {
          buf.append(line);
        }
      }
      buildSourceAction(buf, fullName);
      buf.append("<br/>");
    } catch (Exception e) {
      buf.append(e.toString());
    }

    buf.append("</p>");
    buf.append("</form>");
    try {
      rtext.setText(buf.toString(), true, false);
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }
    //    scrolled.getParent().pack();
    Rectangle r = scrolled.getClientArea();
    scrolled.setMinSize(rtext.computeSize(r.width, SWT.DEFAULT));
    mainTab.setSelection(1);
    if(home != null) browser.setUrl(home);
  }
  
  private void buildSourceAction(StringBuilder buf, String fullName) {
    buf.append("<a href=\"!").append(fullName).append("\">");
    buf.append("Disable Channel").append("</a>\t|\t");
    buf.append("<a href=\"@").append(fullName).append("\">");
    buf.append("Edit ").append(fullName).append("</a> ");
  }

  protected void editSource(String link) {
    if(Application.LICENSE == Install.PERSONAL) {
      ClientLog.getInstance().showMessage(getShell(), null, 
          "The feature not available in free version!");
      return;
    }
    try {
      if(link != null && link.charAt(0) == '#') {
        link = link.substring(1);
        BareBonesBrowserLaunch.openURL(getShell(), link);
        return;
      } else  if(link != null && link.charAt(0) == '@') {
        link = link.substring(1);
        String [] elements = link.split("\\.");
        final Creator creator = (Creator)workspace.getTab().createTool(Creator.class, true, SWT.CLOSE);
        creator.selectData(new Worker[] {
            new ProcessLinkWorker(creator, null, 0)
        }, elements[0], elements[1], elements[2]);
        return;
      } else  if(link != null && link.charAt(0) == '^') {
        processLink(link.substring(1), 2);
        return;
      } else if(link != null && link.charAt(0) == '!') {
        link = link.substring(1);
        String [] elements = link.split("\\.");
        new DisableSourceHandler(getShell(), elements[0], elements[1], elements[2]);
        return;
      }
      
      if(listSource.getSelectionCount() < 1) return;

      processLink(link, 1);
    } catch (Exception e) {
      ClientLog.getInstance().setMessage(null, e);
    }
  }
  
  private void processLink(String link, int type) throws Exception {
    final Creator creator = 
      (Creator)workspace.getTab().createTool(Creator.class, true, SWT.CLOSE);
    
    TableItem item = listSource.getSelection()[0];
    String group  = item.getText(2);
    String category  = item.getText(1);
    String name  = item.getText(0);
    
    if(group.equals(creator.getSelectedGroupName())
        && category.equals(creator.getSelectedCategory())
        && creator.getSelectedSources().length > 0
        && name.equals(creator.getSelectedSources()[0])) {
      WaitLoading loading = new WaitLoading(this, 
          new ProcessLinkWorker(creator, link, type));
      loading.open();
      return;
    }
//    System.out.println(creator.getSelectedGroupName() + " : "+ group);
//    System.out.println(creator.getSelectedCategory() + " : "+ category);
//    System.out.println(creator.getSelectedSources()+ " : "+ name);
    
    creator.selectData(new Worker[] {
        new ProcessLinkWorker(creator, link, type)
    }, group, category, name);
  }


}
