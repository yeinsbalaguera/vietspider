package org.vietspider.gui.source;

import java.util.LinkedList;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.vietspider.client.common.ISourceHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.model.Group;
import org.vietspider.ui.action.Listeners;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;

abstract class UISourcesExplorer extends Composite {

  protected List uiSources;
  protected List uiCategories;  
  private java.util.List<Object> actions = new LinkedList<Object>();
  
  protected String group;
  
  protected Combo cboGroupType;
  
  private Button butAddAll;
  private Menu menuCategories;
  private Menu menuSources;
  
  private SelectAllGroup selectAllGroup;
  
  protected ISourceHandler iSourceHandler;

  public UISourcesExplorer(ApplicationFactory factory, 
      Composite parent, int[] sashSplit, ISourceHandler iSourceHandler_) {
    super(parent, SWT.NONE);
    
    this.iSourceHandler = iSourceHandler_;
    
    parent = factory.getComposite();
    String parentName = factory.getClassName();

    factory.setClassName(getClass().getName());

    setLayout(new GridLayout(1, false));    
    
    Composite composite2  = new Composite(this, SWT.NONE);
    composite2.setLayout(new GridLayout(3, false));
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);      
    composite2.setLayoutData(gridData);
    factory.setComposite(composite2);
    
    factory.createLabel("lblGroupType"); 
    cboGroupType = factory.createCombo(SWT.DROP_DOWN | SWT.READ_ONLY);
    cboGroupType.setFont(UIDATA.FONT_9);
    
    cboGroupType.setVisibleItemCount(20);
    cboGroupType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    cboGroupType.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {  
        selectGroupType();
      }      
    });
    
    butAddAll = factory.createButton("butAddAll", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(selectAllGroup == null)  return;
        selectAllGroup.selectAllGroup(cboGroupType.getItems());
      }   
    }, factory.loadImage("butAddAll.png"));    

    
    SashForm sash = new SashForm(this, SWT.HORIZONTAL);
    sash.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    gridData = new GridData(GridData.FILL_BOTH);      
    sash.setLayoutData(gridData);
    factory.setComposite(sash);

    factory.setComposite(factory.createGroup("groupCategory", null, new FillLayout()));   
    uiCategories = factory.createList(SWT.MULTI | 
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, new String[]{}, new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(uiCategories.getSelectionCount() > 1) return ;
        loadSources(0);
//        selectCategory();
        Preferences prefs = Preferences.userNodeForPackage(UISourcesExplorer.class);
        try {
          prefs.put("selectedDomain", String.valueOf(uiCategories.getSelectionIndex()));
        } catch (Exception e) {
        }
      }      
    });
    uiCategories.setFont(UIDATA.FONT_10B);

    factory.setComposite(sash);

    factory.setComposite(factory.createGroup("groupAgent", null, new FillLayout()));
    uiSources = factory.createList(SWT.MULTI | 
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, new String[]{}, new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        selectSource();
      }
    });

    menuSources = new Menu(getShell(), SWT.POP_UP);	   
    factory.createMenuItem(menuSources, "itemDeleteAgent", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeSource();
      }
    });
    uiSources.setMenu(menuSources);

    menuCategories = new Menu(getShell(), SWT.POP_UP); 
    factory.createMenuItem(menuCategories, "itemDeleteCategory", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeCategory();
      }
    }
    );
    uiCategories.setMenu(menuCategories);

    sash.setWeights(sashSplit);

    factory.setComposite(parent);
    factory.setClassName(parentName);
  }
  
  public String getSelectedSource(){
    if(uiSources.getSelection() != null && uiSources.getSelection().length > 0)
      return uiSources.getSelection()[0];
    return  "";		 
  }
  
  public String[] getSelectedSources(){
    return uiSources.getSelection();
  }
  
  public String getCategory() {
    if(uiCategories.getSelectionIndex() > -1) 
      return uiCategories.getItem(uiCategories.getSelectionIndex());
    return "";
  }
  
  public String [] getCategories() { return uiCategories.getSelection(); }
  
  public void addAction(Object action){ 
    actions.add(action); 
  }
  
  @Listeners()
  public java.util.List<Object> getActions() { 
    return actions; 
  }
  
  public void removeAction(Object action){
    actions.remove(action);
  }

  void selectSource()  {
  } 
  
  public void loadGroups(final String defaultGroup) {
    Worker excutor = new Worker() {
      
      private Group [] groups; 
      private Exception exp;

      public void abort() {
        iSourceHandler.abort();
      }

      public void before() {
        cboGroupType.removeAll();
      }

      public void execute() {
        try {
          groups = iSourceHandler.loadGroups().getGroups();
        }catch (Exception e) {
          exp = e;
        }
      }

      public void after() {
        if(exp != null) {
          ClientLog.getInstance().setMessage(uiSources.getShell(), exp);
          return;
        }
        if(groups == null) return;
        for(Group groupEle : groups) {
          if(groupEle.getType().equals(Group.DUSTBIN)) continue;
          cboGroupType.add(groupEle.getType());
        }
        
        if(defaultGroup != null) {
          for(int i = 0; i < cboGroupType.getItemCount(); i++) {
            if(cboGroupType.getItem(i).equals(defaultGroup)) {
              cboGroupType.select(i);
              selectGroupType();
              return;
            }
          }
        } else {
          Preferences prefs = Preferences.userNodeForPackage(SourcesExplorer.class);
          int selectedGroup = 0;
          try {
            selectedGroup = Integer.parseInt(prefs.get("selectedGroup", "0"));
          } catch (Exception e) {
            selectedGroup = 0;
          }
          
          try {
            if(selectedGroup > -1 && selectedGroup < cboGroupType.getItemCount()) {
              cboGroupType.select(selectedGroup);
            } else if(cboGroupType.getItemCount() > 0) {
              cboGroupType.select(0);
            }
            int idx = cboGroupType.getSelectionIndex();
            if(idx >= 0) group = cboGroupType.getItem(idx);
          } catch (Exception e) {
            ClientLog.getInstance().setMessage(cboGroupType.getShell(), e);
          }
          loadCategory(0);
        }
      }
    };
    WaitLoading loading = new WaitLoading(cboGroupType, excutor);
    loading.open();
  }
  
  abstract void loadSources(int time);
  abstract void removeSource();
  abstract void removeCategory();

  public List getListSource(){ return uiSources; }
  public List getListDomain(){ return uiCategories; }

  public String getGroup() { return group; }
  
  abstract void loadCategory(int time) ;
  
  public void selectGroupType() {
    int idx = cboGroupType.getSelectionIndex();
    if(idx < 0) return ;
    group = cboGroupType.getItem(idx);
    loadCategory(0);   
    
    Preferences prefs = Preferences.userNodeForPackage(SourcesExplorer.class);
    try {
      prefs.put("selectedGroup", String.valueOf(idx));
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static interface SelectAllGroup {
    
    public void selectAllGroup(String [] groups) ;
    
  }

  public void setSelectAllGroup(SelectAllGroup selectAllGroup) { 
    this.selectAllGroup = selectAllGroup;
  }
  
  public void enableAddAll() { butAddAll.setVisible(true); }
  
  public void disableAddAll() { butAddAll.setVisible(false); }
  
  public void disableMenu() {
    menuCategories.dispose();
    menuSources.dispose();
  }
  
  public void disableGroupType() {
    cboGroupType.setEnabled(false);
  }

  public void selectGroup(String value) {
    loadGroups(value);
  }
  
  public void removeSelectedSources() {
    String[] values  = getSelectedSources();
    for(int i = 0;  i < values.length; i++) {
      uiSources.remove(values[i]);
    }
  }
  
  public void removeSelectedCategories() {
    String[] values  = getCategories();
    for(int i = 0;  i < values.length; i++) {
      uiCategories.remove(values[i]);
    }
  }
  
}
