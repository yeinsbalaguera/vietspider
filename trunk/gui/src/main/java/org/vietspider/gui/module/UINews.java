/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.module;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.text.NameConverter;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.browser.BrowserWidget;
import org.vietspider.model.Track;
import org.vietspider.net.server.URLPath;
import org.vietspider.notifier.cache.ColorCache;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.CCombo;
import org.vietspider.ui.widget.ExpandMenu;
import org.vietspider.ui.widget.Hyperlink;
import org.vietspider.ui.widget.ImageHyperlink;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.action.ExpandSelectionEvent;
import org.vietspider.ui.widget.action.ExpandSelectionListener;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 26, 2006
 */
public class UINews {

  protected CCombo cboDate;
  protected ExpandMenu expand;
  
  protected Composite pageComposite;
  protected Text txtPage;
  protected Label lblPage;
  protected int page = 1;
  protected int totalPage = 0;
  protected org.eclipse.ui.forms.widgets.Hyperlink butNextPage;
  protected org.eclipse.ui.forms.widgets.Hyperlink butPrevPage;
  
  protected Text txtSearch;
  
//  protected SourceMenuClient client ;
  
  protected ImageHyperlink buttonReresh;
  
  protected ThreadExecutor serverLoader;
  protected volatile boolean dateLoading = false;
  
  private List<Integer> expandedItems = new ArrayList<Integer>();
  
  private Menu menuFilter;
  
  private BrowserWidget browser;
  
  public UINews(BrowserWidget widget) {
    this.browser = widget;
  }

  public void setExpand(ExpandMenu exp){
    this.expand = exp;
    expand.addSelectionListener(new ExpandSelectionListener(){
      public void select(ExpandSelectionEvent event){
        try {
          selectCategory(event);
        } catch( Exception ex) {
          ClientLog.getInstance().setException(cboDate.getShell(), ex);
        }   
      }
    });
    
    final int time = 5*60*1000;
    Runnable timer = new Runnable () {
      public void run () {
        if(!dateLoading) loadDate();
        if(cboDate.isDisposed()) return;
        cboDate.getDisplay().timerExec(time, this);
      }
    };
    cboDate.getDisplay().timerExec (time, timer);

    loadDate();
  }

  void selectCategory(ExpandSelectionEvent event) throws Exception {
    if(event.getGroup() == null) return;
    String date = cboDate.getItem(cboDate.getSelectionIndex());
    viewPage(date.trim(), event.getGroup(), event.getElement());
  }

  public void selectDate(final int currentPage) {
    if(cboDate.getItemCount() < 1) return;
    ExpandItem [] items = expand.getBar().getItems();
    for(int i = 0; i < items.length; i++) {
      if(expand.getBar().getItem(i).getExpanded()) expandedItems.add(i);
    }
    
    /*if(currentPage > 0) {
      if(client == null) return;
      createUI(client.loadPage(currentPage-1));
      return;
    }*/
    
    Worker excutor = new Worker() {

      private String date;
      private Track track;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        dateLoading = true;
        date = cboDate.getItem(cboDate.getSelectionIndex()).trim();
//        client = new SourceMenuClient("summary", new String[]{date});
      }

      public void execute() {
        try {
          track = DataClientHandler.getInstance().loadTrackId(date, currentPage);
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        if(pageComposite.isDisposed()) {
          dateLoading = false;
          return;
        }
        if(track == null) {
          dateLoading = false;
          return;
        }
        
        try {
          totalPage = Integer.parseInt(track.getProperties().getProperty("total.page"));
        } catch (Exception e) {
          ClientLog.getInstance().setMessage(null, e);
        }
        txtPage.setEditable(totalPage > 1);
//        selectDate(1); 
        createUI(track);
        txtPage.setText(String.valueOf(page) + "/"+ String.valueOf(totalPage));
        butNextPage.setEnabled(page < totalPage);
        butPrevPage.setEnabled(page > 1);
        dateLoading = false;
      }
    };
    new ThreadExecutor(excutor, cboDate).start();
  }
  
  private void createUI(Track menuInfo) {
    expand.removeAll(); 
    List<Track> categories = menuInfo.getTrackIds(Track.CATEGORY);
    ClientRM resources = new ClientRM("VietSpider");
    boolean isSingle =  Application.isSingleData();
    for(Track category : categories) {
      List<String> sources = new ArrayList<String>();
      for(Track sourceInfo : category.getChildren()) {
        if(sourceInfo == null) continue; 
        sources.add(sourceInfo.getName());
      }
      
      Composite composite = expand.createGroup(sources);
      
      Hyperlink lbl = new Hyperlink(composite, SWT.LEFT);
      lbl.setFont(UIDATA.FONT_8V); 
      lbl.setBackground(UIDATA.BCOLOR);  
      lbl.setForeground(UIDATA.FCOLOR);
//      System.out.println("===> "+ category.getParent().getName() + "." + category.getName());
      lbl.setTip(category.getParent().getName() + "." + category.getName());
      lbl.setText(resources.getLabel("viewAll"));
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);          
      lbl.setLayoutData(gridData);
      lbl.addHyperlinkListener(new HyperlinkAdapter(){
        public void linkActivated(HyperlinkEvent e){
          String txtGroup = e.getTip().trim();
          ExpandSelectionEvent exv = new ExpandSelectionEvent(-1, -1, txtGroup, null);
          expand.select(exv);
        }
      });   
      String categoryValue = null;
      if(isSingle) {
        categoryValue = category.getName();
//        int idx = categoryValue.indexOf('.'); 
//        if(idx > -1) categoryValue = categoryValue.substring(idx+1);
      } else {
        categoryValue = category.getParent().getName() + "." +category.getName();
      }
      expand.createExpandItem(categoryValue, composite);
    }
//    load(resources);
    expand.getBar().layout();
    
    for(int i = 0; i < expandedItems.size(); i++) {
      int idx = expandedItems.get(i);
      if(idx >= expand.getBar().getItemCount()) continue;
      expand.getBar().getItem(idx).setExpanded(true); 
    }
    expandedItems.clear();
  }
  
  public void loadDate() {  
    if(cboDate.isDisposed() || !buttonReresh.isEnabled()) return;
    buttonReresh.setEnabled(false);
    
    Worker excutor = new Worker() {

      private String [] data;
      private int idx;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        dateLoading = true;
        if(cboDate.isDisposed()) return;
        idx = cboDate.getSelectionIndex();
      }

      public void execute() {
        try {
          data = DataClientHandler.getInstance().getDate();
        }catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
          data = new String[]{};
        }
      }

      public void after() {
        if(cboDate.isDisposed() || data.length < 1) {
          dateLoading = false;
          return;
        }
        buttonReresh.setEnabled(true);
        if(cboDate.getItemCount() > 0) cboDate.removeAll();
        for(String ele : data) {
//          System.out.println(" chay thu nhe "+ ele);
          if(ele == null) continue;
          cboDate.add(" "+ele);
        }
        if(idx < 0 || idx >= data.length) idx = 0;   
        cboDate.select(idx);
        selectDate(0);
        dateLoading = false;
      }
    };
    
    if(serverLoader != null && serverLoader.isAlive()) return;
    serverLoader  = new ThreadExecutor(excutor, cboDate);
    serverLoader.start();
  }
  
  public void viewPage(){
    try{
      if(cboDate.isDisposed()) return;
      if(cboDate.getItemCount() < 1 || cboDate.getSelectionIndex() < 0 ) return;
      String date = cboDate.getItem(cboDate.getSelectionIndex());
      if(date == null) return;
      date = date.trim(); 
      viewPage(date, null, null);
    } catch( Exception exp){
      ClientLog.getInstance().setException(cboDate.getShell(), exp);
    }   
  }
  

  private void viewPage(String date, String category, String name){
    if(date == null) return;
    date = date.replace('/', '.');
    try{
      if(date == null) return;
      StringBuilder builder = new StringBuilder();
      ClientConnector2 connecter = ClientConnector2.currentInstance();
      builder.append(connecter.getRemoteURL()).append('/');
      builder.append(connecter.getApplication()).append('/').append("DOMAIN");
      builder.append("/1/").append(date);
      if(category != null) {
        builder.append('/');
        if(Application.isSingleData()  && category.indexOf('.') < 0) {
          String group = Application.GROUPS[0];
          builder.append(URLEncoder.encode(group, Application.CHARSET)).append('.');  
        }
        builder.append(URLEncoder.encode(category, Application.CHARSET));
      }
      if(name != null) {
        builder.append('/').append(URLEncoder.encode(name, Application.CHARSET));
      }
      browser.setUrl(builder.toString());
    }catch (Exception err) {
      ClientLog.getInstance().setException(cboDate.getShell(), err);
    }
  }

  public void setDataPanel(Composite parent) {
    ApplicationFactory factory  = new ApplicationFactory(parent, "VietSpider", getClass().getName());
    parent.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
    GridLayout gridLayout = new GridLayout();
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    gridLayout.marginRight = 0;
    gridLayout.marginLeft = 4;
    gridLayout.numColumns = 2 ;    
    parent.setLayout(gridLayout); 
    parent.setSize(100, 30);
    
    factory.setComposite(parent);
    
    /*factory.createIcon("hot.news", new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        goHotContent();
      }
    });*/
    
    cboDate = new CCombo(parent, SWT.READ_ONLY | SWT.BORDER);
    cboDate.setCursor(new Cursor(cboDate.getDisplay(), SWT.CURSOR_HAND));
    cboDate.setBackground(ColorCache.getWhite());
    cboDate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    cboDate.setFont(UIDATA.FONT_8V);
    cboDate.addListener(SWT.Selection, new Listener() {
      @SuppressWarnings("unused")
      public void handleEvent(Event e) {
        int idx = cboDate.getSelectionIndex();
        selectDate(idx);
//        loadDate();
      }          
    });     
    cboDate.setVisibleItemCount(15);
    
    factory.setComposite(cboDate);
    
    buttonReresh = factory.createIcon("refresh", new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        if(!dateLoading) loadDate();
      }
    });
    cboDate.setIcon(buttonReresh);
    
    /*butMore = factory.createIcon("more", new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        butMore.getMenu().setVisible(true);
      }
    });*/
    
//    createMoreMenu(factory);
    factory.setComposite(parent);
    
    factory.createIcon("view", new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        viewPage();
      }
    });
    Composite panel = new Composite(parent, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    panel.setLayoutData(gridData);
    
    panel.setBackground(new Color(panel.getDisplay(), 255, 255, 255));
    panel.setSize(100, 25);
    
    RowLayout rowLayout = new RowLayout();
    panel.setLayout(rowLayout);
    rowLayout.justify = true;
    rowLayout.fill = true;
    rowLayout.marginLeft = 0;
    rowLayout.marginTop = 5;
    rowLayout.marginRight = 0;
    rowLayout.marginBottom = 0;
    panel.setLayout(rowLayout);
    
    this.pageComposite = panel;
    
    factory.setComposite(panel);
    
    butPrevPage = factory.createLink("pagePrev", new IHyperlinkListener(){
      @SuppressWarnings("unused")
      public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
        selectPage(-1); 
      }

      public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
        org.eclipse.ui.forms.widgets.Hyperlink hyperlink = 
          (org.eclipse.ui.forms.widgets.Hyperlink)e.widget;
        hyperlink.setUnderlined(true);
      }

      public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
        org.eclipse.ui.forms.widgets.Hyperlink hyperlink = 
          (org.eclipse.ui.forms.widgets.Hyperlink)e.widget;
        hyperlink.setUnderlined(false);
      }
    });
    butPrevPage.setFont(UIDATA.FONT_8V);
    
    txtPage = factory.createText(SWT.CENTER | SWT.BORDER);
    txtPage.setBackground(new Color(txtPage.getDisplay(), 255, 255, 255));
    txtPage.setFont(UIDATA.FONT_8V);
    txtPage.setLayoutData(new RowData(70, 8));
    txtPage.addFocusListener(new FocusListener(){

      @SuppressWarnings("unused")
      public void focusGained(FocusEvent evt) {
//        txtPage.setText("");
      }

      @SuppressWarnings("unused")
      public void focusLost(FocusEvent evt) {
        if(txtPage.getText().trim().isEmpty()) {
          txtPage.setText(String.valueOf(page)+"/"+String.valueOf(totalPage));
        }
      }
      
    });
    txtPage.addKeyListener(new KeyAdapter(){

      @Override
      public void keyReleased(KeyEvent e) {
        if(e.keyCode == SWT.CR) selectPage(0); 
      }

    });
    
    factory.setComposite(panel);
    
    butNextPage = factory.createLink("pageNext", new IHyperlinkListener(){
      @SuppressWarnings("unused")
      public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
        selectPage(1);
      }

      public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
        org.eclipse.ui.forms.widgets.Hyperlink hyperlink = 
          (org.eclipse.ui.forms.widgets.Hyperlink)e.widget;
        hyperlink.setUnderlined(true);
      }

      public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
        org.eclipse.ui.forms.widgets.Hyperlink hyperlink = 
          (org.eclipse.ui.forms.widgets.Hyperlink)e.widget;
        hyperlink.setUnderlined(false);
      }
    });
    butNextPage.setFont(UIDATA.FONT_8V);
    
    setExpand(factory.createExpandMenu(parent, "sectionNews"));
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
    expand.setLayoutData(gridData);
    
    
    if(Application.LICENSE == Install.PERSONAL
        || !Application.hasGroup("ARTICLE")) return;
    
    Label lblFilter = new Label(parent, SWT.NONE);
    lblFilter.setFont(UIDATA.FONT_8VB);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    lblFilter.setLayoutData(gridData);
    menuFilter = new Menu(lblFilter);

    lblFilter.setText(factory.getResources().getLabel("filter"));
    lblFilter.setBackground(parent.getBackground());
    lblFilter.setCursor(new Cursor(lblFilter.getDisplay(), SWT.CURSOR_HAND));
    lblFilter.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        menuFilter.setVisible(true);
      }
    });
    
    Composite searchPanel = new Composite(parent, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    searchPanel.setLayout(new GridLayout(3, false));
    searchPanel.setLayoutData(gridData);
    
    factory.setComposite(searchPanel);
    
    Button butAdvancedSearch = factory.createButton(SWT.PUSH);
    butAdvancedSearch.setImage(factory.loadImage("add2.png"));
    butAdvancedSearch.setToolTipText(factory.getResources().getLabel("advanced.search"));
    gridData = new GridData();
    butAdvancedSearch.setLayoutData(gridData);
    butAdvancedSearch.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent arg0) {
        new AdvancedSearch(browser.getWidget());
      }
      
    });
    
    txtSearch = factory.createText(SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtSearch.setLayoutData(gridData);
    txtSearch.setMessage("Search");
    txtSearch.setFont(UIDATA.FONT_10);
    txtSearch.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if(e.keyCode  == SWT.CR) search();
      }
    });
    
    factory.createIcon("view", new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        search();
      }
    });
    
    loadFilter(0);
  }
  
  private void selectPage(int type) {
    try {
      String value = txtPage.getText().trim();
      int idx = value.indexOf('/'); 
      if(idx > 0) value = value.substring(0, idx);
      page = Integer.parseInt(value.trim());
    } catch (Exception exp) {
      page = 1;
    }
    page  = page + type;
    page = page < 0 ? 1 : page > totalPage ? totalPage : page; 
    selectDate(page);
    txtPage.setText(String.valueOf(page)+"/"+ String.valueOf(totalPage));
    butNextPage.setEnabled(page < totalPage);
    butPrevPage.setEnabled(page > 1);
  }
  
  private void search() {
    String pattern = txtSearch.getText();
    pattern = pattern.trim();
    if(pattern.length() < 1) return;
    ClientConnector2 connector = ClientConnector2.currentInstance();
    StringBuilder builder = new StringBuilder();
    builder.append(connector.getRemoteURL()).append('/');
    try {
      pattern = URLEncoder.encode(pattern, "utf-8");
    } catch (Exception e) {
      ClientLog.getInstance().setThrowable(browser.getShell(), e);
      return;
    }
    builder.append(connector.getApplication()).append("/SEARCH/1/?text=").append(pattern);
    
    browser.setUrl(builder.toString());
  }
  
 /* private void goHotContent()  {
    String url = browser.getUrl();
    int indexDomain = url.indexOf("DOMAIN");
    Pattern pattern = null;
    if(indexDomain > -1) {
      pattern = Pattern.compile("\\b\\d{1,2}\\s*[.]\\s*\\d{1,2}\\s*[.]\\s*\\d{4}\\b");
    } else {
      pattern = Pattern.compile("\\d{4}\\d{2}\\d{2}");
    }
    String date  = null;
    try {
      Matcher matcher = pattern.matcher(url);
      if(matcher.find()) {
        date = url.substring(matcher.start(), matcher.end());
      }
      if(indexDomain < 0) {
        Date dateInstance = new SimpleDateFormat("yyyyMMdd").parse(date); 
        date = CalendarUtils.getParamFormat().format(dateInstance);
      }
    } catch (Exception e) {
    }
    
    if(date == null){
      Calendar cal = Calendar.getInstance();    
      date = CalendarUtils.getParamFormat().format(cal.getTime());
    } else {
      date = date.replace('/', '.');
    }
    
    ClientConnector2 connector = ClientConnector2.currentInstance();
    StringBuilder builder = new StringBuilder();
    builder.append(connector.getRemoteURL()).append('/');
    builder.append(connector.getApplication()).append('/').append("EVENT");
    builder.append("/1/").append(date);    
    browser.setUrl(builder.toString());
  }*/
  
  void loadFilter(final int time) {
    Worker excutor = new Worker() {

      private String error = null;
      private String [] names;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
       
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "list.folder"),
              new BasicHeader("file", "system/plugin/filter/")//"track/logs/"
          };
          
          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = connector.post(URLPath.FILE_HANDLER, new byte[0], headers);
          names = new String(bytes, Application.CHARSET).trim().split("\n");
          for(int i = 0; i < names.length; i++) {
            names[i] = NameConverter.decode(names[i]);
          }
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          if(time < 3) {
            loadFilter(time+1);
            return;
          }
          ClientLog.getInstance().setMessage(cboDate.getShell(), new Exception(error));
          return;
        }
        if(names == null) return;
        
        for(int i = 0; i < names.length; i++) {
          ApplicationFactory.createStyleMenuItem2(
              menuFilter, names[i], new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                  MenuItem item = (MenuItem)e.getSource();
                  String filter = item.getText();
                  if(filter.length() < 1) return;
                  ClientConnector2 connector = ClientConnector2.currentInstance();
                  StringBuilder builder = new StringBuilder();
                  builder.append(connector.getRemoteURL()).append('/');
                  try {
                    filter = URLEncoder.encode(filter, "utf-8");
                  } catch (Exception exp) {
                    ClientLog.getInstance().setThrowable(browser.getShell(), exp);
                    return;
                  }
                  builder.append(connector.getApplication()).append("/FILTER/1/?text=").append(filter);
                  browser.setUrl(builder.toString());
                }
          });
        }
        // end method
      }
    };
    new ThreadExecutor(excutor, cboDate).start();
  }
  
}
