package org.vietspider.gui.creator;

import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.browser.ControlComponent;
import org.vietspider.gui.browser.StatusBar;
import org.vietspider.gui.creator.action.LoadGroup;
import org.vietspider.gui.creator.action.LoadGroupCategorySource;
import org.vietspider.gui.creator.action.SetSource;
import org.vietspider.gui.source.ListSources;
import org.vietspider.gui.source.SearchSource;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.model.Group;
import org.vietspider.model.Source;
import org.vietspider.notifier.notifier.NotificationEvent;
import org.vietspider.notifier.notifier.NotificationListener;
import org.vietspider.notifier.notifier.Notifier;
import org.vietspider.notifier.notifier.NotifierDialogs;
import org.vietspider.ui.htmlexplorer.HTMLExplorer;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.LiveSashForm;
import org.vietspider.ui.widget.UIDATA;

public class Creator extends ControlComponent {

  //  protected TreeSources treeSources;
  private ListSources listSources;
  private SearchSource searchSources;

  //  private AdvSourceInfo advSourceInfo;
  private SourceInfoControl infoControl1;

  private Combo cboGroupType;
  private TabFolder tab;

  protected int lastSelectedTabItem = 0;

  protected StatusBar statusBar;

  //  private HTMLExplorer htmlExplorer;
  //  private DataSelectorExplorer dataExplorer;
  //  private TestViewer testViewer;

  private LiveSashForm sashMain;
  private Composite dataForm;

  private Group cachedGroup;

  protected Image notifyIcon;
  protected Image errorIcon;

  public final static short ERROR_SOURCE = 0;
  public final static short ERROR_FIELD = 1;

  public Creator(Composite parent, Workspace _workspace){
    super(parent, _workspace);

    setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

    ApplicationFactory factory = new ApplicationFactory(this, "Creator", getClass().getName());

    notifyIcon = factory.loadImage("warn.png");
    errorIcon = factory.loadImage("error.png");

    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);

    sashMain = new LiveSashForm(this, SWT.VERTICAL);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    sashMain.setLayoutData(gridData);
    sashMain.setBackground(getBackground());

    dataForm = new Composite(sashMain, SWT.NONE);

    sashMain.setMaximizedControl(dataForm);

    gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    dataForm.setLayout(gridLayout);
    SashForm sashForm = new SashForm(dataForm, SWT.HORIZONTAL);
    sashForm.setBackground(getBackground());

    gridData = new GridData(GridData.FILL_BOTH);      
    sashForm.setLayoutData(gridData);

    Composite composite = new Composite(sashForm, SWT.NONE);
    composite.setBackground(getBackground());
    gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 2;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 2;
    composite.setLayout(gridLayout);
    composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    factory.setComposite(composite);

    cboGroupType = factory.createCombo(SWT.DROP_DOWN | SWT.READ_ONLY);
    cboGroupType.setFont(UIDATA.FONT_9B);
    cboGroupType.setVisibleItemCount(10);
    cboGroupType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    cboGroupType.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        selectData(new Worker[0], null, null);
      }      
    });

    composite.setBackground(new Color(getDisplay(), 255, 255, 255));

    //    if(XPWindowTheme.isPlatform()) {
    //      tab = new CTabFolder(composite, SWT.MULTI);
    //      CTabFolder tabFolder = (CTabFolder)tab;
    //      XPWindowTheme.setTabBrowserTheme(tabFolder);
    //      tab.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
    //    } else {
    tab = new TabFolder(composite, SWT.TOP);
    tab.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
    //    }

    tab.setBackground(new Color(getDisplay(), 255, 255, 255));
    tab.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_BOTH);     
    tab.setLayoutData(gridData);

    /* treeSources = new TreeSources(factory, tab);
    factory.setComposite(tab);
    if(XPWindowTheme.isPlatform()) {
      CTabFolder tabFolder = (CTabFolder)tab;
      CTabItem item = new CTabItem(tabFolder, SWT.NONE);
      item.setText(factory.getLabel("tab.explorer"));
      item.setControl(treeSources);
    } else {
      TabFolder tabFolder = (TabFolder)tab;
      TabItem item = new TabItem(tabFolder, SWT.NONE);
      item.setText(factory.getLabel("tab.explorer"));
      item.setControl(treeSources);
    }
    treeSources.setCreator(this);*/

    listSources = new ListSources(factory, tab);
    factory.setComposite(tab);
    //    if(XPWindowTheme.isPlatform()) {
    //      CTabFolder tabFolder = (CTabFolder)tab;
    //      CTabItem item = new CTabItem(tabFolder, SWT.NONE);
    //      item.setText(factory.getLabel("tab.list"));
    //      item.setControl(listSources);
    //    } else {
    TabItem item = new TabItem(tab, SWT.NONE);
    item.setText(factory.getLabel("tab.list"));
    item.setControl(listSources);
    //    }
    listSources.setCreator(this);

    searchSources = new SearchSource(factory, tab);
    factory.setComposite(tab);
    //    if(XPWindowTheme.isPlatform()) {
    //      CTabFolder tabFolder = (CTabFolder)tab;
    //      CTabItem item = new CTabItem(tabFolder, SWT.NONE);
    //      item.setText(factory.getLabel("tab.search"));
    //      item.setControl(searchSources);
    //    } else {
    //      TabFolder tabFolder = (TabFolder)tab;
    item = new TabItem(tab, SWT.NONE);
    item.setText(factory.getLabel("tab.search"));
    item.setControl(searchSources);
    //    }
    searchSources.setCreator(this);

    infoControl1 = new SourceInfoControl(sashForm, this, factory);

    sashForm.setWeights(new int[]{150, 520});

    SelectionAdapter tabSelector = new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        selectData(new Worker[0],getSelectedGroupName(), 
            getSelectedCategory(), getSelectedSources());

        Preferences prefs_ = Preferences.userNodeForPackage(Creator.class);
        //        if(tab instanceof TabFolder) {
        //          TabFolder tabFolder = (TabFolder)tab;
        prefs_.put("selectedTab", String.valueOf(tab.getSelectionIndex()));
        lastSelectedTabItem = tab.getSelectionIndex(); 
        //        } else if(tab instanceof CTabFolder) {
        //          CTabFolder tabFolder = (CTabFolder)tab;
        //          prefs_.put("selectedTab", String.valueOf(tabFolder.getSelectionIndex()));
        //          lastSelectedTabItem = tabFolder.getSelectionIndex(); 
        //        }
      }
    };

    //    if(tab instanceof TabFolder) {
    tab.addSelectionListener(tabSelector);    
    //    } else if(tab instanceof CTabFolder) {
    //      ((CTabFolder)tab).addSelectionListener(tabSelector);
    //    }


    try {
      Preferences prefs = Preferences.userNodeForPackage(getClass());
      lastSelectedTabItem = Integer.parseInt(prefs.get("selectedTab", ""));
    } catch (Exception e) {
      lastSelectedTabItem = 0;
    }
    //    if(tab instanceof TabFolder) {
    tab.setSelection(lastSelectedTabItem);
    //    } else if(tab instanceof CTabFolder) {
    //      ((CTabFolder)tab).setSelection(lastSelectedTabItem);
    //    }

    final Listener listener =  new Listener() {
      public void handleEvent(Event e) {
        if(workspace.getTab().getSelection().getControl() != Creator.this) return;
        if ((e.stateMask & SWT.CTRL) != 0) {
          char key = (char)e.keyCode;
          if(key == 's' || key == 'S') {
            infoControl1.save();
          } else if(key == 'r' || key == 'R') {
            infoControl1.reset(); 
          } else if(key == 't' || key == 'T') {
            infoControl1.test(); 
          }
        }
      }
    };

    addDisposeListener(new DisposeListener() {
      @SuppressWarnings("unused")
      public void widgetDisposed(DisposeEvent evt) {
        getDisplay().removeFilter(SWT.KeyDown, listener);
      }
    });

    getDisplay().addFilter(SWT.KeyDown, listener);

    statusBar = new StatusBar(workspace, dataForm, true);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    statusBar.setLayoutData(gridData);  
    statusBar.setComponent(this);

    //    Runnable timer = new Runnable () {
    //      public void run () {
    //        selectData(null, null);
    //      }
    //    };
    //    getDisplay().timerExec (500, timer);

    //    new LoadGroups(this, cboGroupType);
  }	

  public void setSource(Source source) { 
    infoControl1.setSource(source, 0, true);  
  }

  public void selectCategory(String category) {
    listSources.setSelectedCategory(category);
  }

  public void setSource(Worker[]plugins, String category, String name, boolean focus) {
    new SetSource(infoControl1, plugins, cboGroupType, category, name, focus);
  }

  public String getSelectedCategory() {
    if(lastSelectedTabItem == 0) {
      //      return treeSources.getSelectedCategory();
      //    } else if(lastSelectedTabItem == 1) {
      return listSources.getSelectedCategory();
    }
    return searchSources.getSelectedCategory();
  }

  public String[] getSelectedSources() {
    if(lastSelectedTabItem == 0) {
      //      return treeSources.getSelectedSources();
      //    } else if(lastSelectedTabItem == 1) {
      return listSources.getSelectedSources();
    }
    return searchSources.getSelectedSources();
  }

  public String getSelectedGroupName() {
    int idx = cboGroupType.getSelectionIndex();
    if(idx < 0) idx = 0;
    if(idx >= cboGroupType.getItemCount()) return "ARTICLE";
    String group = cboGroupType.getItem(idx);

    if(cachedGroup == null 
        || !cachedGroup.getType().equals(group)) {
      new LoadGroup(this, cboGroupType);
    }

    return group;
  }

  public void selectGroup(String group) {
    if(group == null) return;
    for(int i = 0; i < cboGroupType.getItemCount(); i++) {
      if(group.equalsIgnoreCase(cboGroupType.getItem(i))) {
        cboGroupType.select(i);
        return;
      }
    }
  }

  public void selectData(Worker[] plugins, String group, String category, String...sources) {
    //    if(tab instanceof TabFolder) {
    //      TabFolder tabFolder = (TabFolder)tab;
    if(tab.getSelectionIndex() == 0) {
      //        new LoadGroupCategorySource(treeSources, plugins, group, category, sources);
      //      } else if(tabFolder.getSelectionIndex() == 1) {
      //        System.out.println(" chon "+ group + " / "+ category+ " / "+ sources);
      new LoadGroupCategorySource(listSources, plugins, group, category, sources);
    } else {
      new LoadGroupCategorySource(searchSources, plugins, group, category, sources);
    }
    //    } else if(tab instanceof CTabFolder) {
    //      CTabFolder tabFolder = (CTabFolder)tab;
    //      if(tabFolder.getSelectionIndex() == 0) {
    ////        new LoadGroupCategorySource(treeSources, plugins, group, category, sources);
    ////      } else if(tabFolder.getSelectionIndex() == 1) {
    //        //        System.out.println(" chon "+ group + " / "+ category+ " / "+ sources);
    //        new LoadGroupCategorySource(listSources, plugins, group, category, sources);
    //      } else {
    //        new LoadGroupCategorySource(searchSources, plugins, group, category, sources);
    //      }
    //    }
  }

  public void showMessage(String message, Image icon, short _type) {
    //    new Exception().printStackTrace();
    String title  = "Message!";
    if(icon == errorIcon) title = "Error!";
    NotificationListener listener = null;

    if(_type  == ERROR_SOURCE) {
      listener = new NotificationListener() {
        public void clicked(NotificationEvent event) {
          if(event.getComponent() == NotificationEvent.CLOSE) return;
          String _message =  event.getMessage();
          int start = _message.indexOf('\"');
          int end = _message.indexOf('\"', start+1);
          if(start < 0 || end <= start) return;
          String value = _message.substring(start+1, end);
          //          System.out.println(" vallue na "+ value);
          TextSpliter spliter = new TextSpliter();
          List<String> elements  = spliter.toList(value, '.');
          //          System.out.println(" ====  >"+ elements.size());
          if(elements.size() == 2) {
            selectData(new Worker[0], getSelectedGroupName(), elements.get(0), elements.get(1));
          }  else if (elements.size() == 3) {
            selectData(new Worker[0], elements.get(0), elements.get(1), elements.get(2));
          }
          //            System.out.println(element);
        }
      };
    }   

    Notifier notifier = new Notifier(Display.getCurrent().getActiveShell(), title);
    notifier.setTitle(title);
    notifier.setIcon(icon);
    notifier.setListener(listener);
    notifier.setMessage(message);
    notifier.setDisplayTime(18000);

    NotifierDialogs.notify(notifier);

    //        18000, title, message, title, icon, listener)
    //    NotifierDialogs.notify(Display.getCurrent().getActiveShell(),
    //        18000, title, message, title, icon, listener);
  }

  //  public StatusBar getStatusBar() { return statusBar; }

  //  public HTMLExplorer getHtmlExplorer() {
  //    if(htmlExplorer == null) {
  //      htmlExplorer = new HTMLExplorer(sashMain);
  //    }
  //    htmlExplorer.reset();
  //    return htmlExplorer; 
  //  }
  //
  //  public DataSelectorExplorer getDataExplorer() { 
  //    if(dataExplorer == null) {
  //      dataExplorer = new DataSelectorExplorer(sashMain);
  //    }
  //    dataExplorer.reset();
  //    return dataExplorer; 
  //  }

  //  public TestViewer getTestViewer() { 
  //    if(testViewer == null) testViewer = new TestViewer(this, sashMain);
  //    return testViewer; 
  //  }

  //  public void showHtmlExplorer() { sashMain.setMaximizedControl(htmlExplorer); }
  public HTMLExplorer showHtmlExplorer() { 
    HTMLExplorer htmlExplorer = new HTMLExplorer(sashMain);
    sashMain.setMaximizedControl(htmlExplorer); 
    htmlExplorer.setWebClient(infoControl1.getSourceEditor().getWebClient());
    return htmlExplorer;
  }

  public void showMainForm() {
    Control [] controls = sashMain.getChildren();
    for(int i = 0; i < controls.length; i++) {
      if(dataForm == controls[i]) continue;
      controls[i].dispose();
    }
    sashMain.setMaximizedControl(dataForm); 
  }
  public DataSelectorExplorer showDataExplorer() {
    DataSelectorExplorer dataExplorer = new DataSelectorExplorer(sashMain);
    dataExplorer.setWebClient(infoControl1.getSourceEditor().getWebClient());
    sashMain.setMaximizedControl(dataExplorer); 
    return dataExplorer;
  }

  public TestViewer showTestViewer() {
    TestViewer testViewer = new TestViewer(this, sashMain);
    sashMain.setMaximizedControl(testViewer); 
    return testViewer; 
  }

  public SourceInfoControl getInfoControl() { return infoControl1; }

  public Group getSelectedGroup() { return cachedGroup; }
  public void setCachedGroup(Group cachedGroup) {
    this.cachedGroup = cachedGroup;
  }

  public Combo getUIGroupType() { return cboGroupType; }
  public Image getNotifyIcon() { return notifyIcon; }
  public Image getErrorIcon() { return errorIcon;  }

  public String getNameIcon() { return "small.webstore.png"; }

  public ListSources getListSources() { return listSources; }

  public void setEditMode(boolean value) {
    listSources.setEnabled(value);
    cboGroupType.setEnabled(value);
    if(!value) tab.setSelection(0);
    tab.setEnabled(value);
    infoControl1.setEditMode(value);
    searchSources.setEnabled(value);
  }
}
