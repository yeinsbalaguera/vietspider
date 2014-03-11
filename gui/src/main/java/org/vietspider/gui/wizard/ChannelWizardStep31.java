package org.vietspider.gui.wizard;
import java.io.File;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.OrganizationClientHandler;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.source.SourcesExplorer;
import org.vietspider.model.Source;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;
import org.vietspider.user.AccessChecker;

/***************************************************************************
 * Copyright 2001-2011 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Dec 30, 2011  
 */
public class ChannelWizardStep31 extends ChannelWizardComposite {
  
  private Browser browser;
  
  private Combo groupCombo;
  private List categoryList;
  
  private AccessChecker accessChecker;
  
  public ChannelWizardStep31(ChannelWizard wizard) {
    super(wizard, null);
    
    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);
    
    browser = ApplicationFactory.createBrowser(this, null);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.verticalSpan = 2;
    browser.setLayoutData(gridData);
    
    groupCombo = new Combo(this, SWT.BORDER);
    groupCombo.setFont(UIDATA.FONT_9B);
    groupCombo.setVisibleItemCount(10);
    groupCombo.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(groupCombo.getItemCount() < 1) return;
        if(groupCombo.getSelectionIndex() < 0) return;
        LoadCategoryWorker worker = new LoadCategoryWorker();
        WaitLoading waitLoading = new WaitLoading(ChannelWizardStep31.this, worker);
        waitLoading.open();
      }
    });
    gridData = new GridData();
    gridData.widthHint = 180;
    groupCombo.setLayoutData(gridData);
    
    Menu menu = new Menu(getShell(), SWT.POP_UP);
    groupCombo.setMenu(menu);

    MenuItem menuItem1 = new MenuItem(menu, SWT.PUSH);
    menuItem1.setText(ChannelWizard.getLabel("reload"));
    menuItem1.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        reload();
      }
    });
    
   
    categoryList = new List(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.widthHint = 180;
    categoryList.setLayoutData(gridData);
    categoryList.setFont(UIDATA.FONT_9);
    categoryList.setMenu(menu);
    
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    createButton(gridData);
  }
  
  @Override
  void openHelp() {
    String youtube = "http://www.youtube.com/watch?v=kYoYcaJkLdA";
    String video = "http://vietspider.org/video/step4.avi";
    wizard.openHelp(youtube, video);
  }
  
  public void show() {
    wizard.setTitle(" 4/10");
    
    String html = wizard.getTemp("extract.doc");
    if(html != null) browser.setText(html);
    
    if(groupCombo.getItemCount() > 0
        && categoryList.getItemCount() > 0) return;
    reload();
  }
  
  private void reload() {
    LoadGroup worker = new LoadGroup(new LoadGroup.Handler(){
      public void showError(String error) {
         error(error);
      }

      public void handle(AccessChecker _accessChecker, String[] groups, File file) {
        if(groupCombo.isDisposed()) return;
        ChannelWizardStep31.this.accessChecker = _accessChecker;
        //    System.err.println(" ====  >"+ selectedGroup + " / "+ selectedCategory + " / "+ selectedSource);
        //    new Exception().printStackTrace();
        if(groups == null || groups.length < 1 || groupCombo.isDisposed()) return;
        groupCombo.removeAll();
        if(accessChecker != null) {
          StringBuilder builder = new StringBuilder();
          for(String _group : groups) {
            if(!accessChecker.isPermitGroup(_group)) continue;
            groupCombo.add(_group);
            if(builder.length() > 0) builder.append('\n');
            builder.append(_group);
          }
          try {
            byte [] bytes = builder.toString().getBytes(Application.CHARSET);
            RWData.getInstance().save(file, bytes);
          } catch (Exception e) {
            file.delete();
          }
        } else {
          for(String _group : groups) {
            groupCombo.add(_group);
          } 
        }

        int selectedIndex = 0;
        try {
          Preferences prefs = Preferences.userNodeForPackage(SourcesExplorer.class);
          selectedIndex = Integer.parseInt(prefs.get("selectedGroup", ""));
        } catch (Exception e) {
          selectedIndex = 0;
        }

        if(selectedIndex < 0) selectedIndex = 0;
        if(selectedIndex >= groupCombo.getItemCount()) {
          if(groupCombo.getItemCount() > 0) groupCombo.select(0);
          return;
        }
        groupCombo.select(selectedIndex);
      }
      
    }, new LoadCategoryWorker());
    WaitLoading waitLoading = new WaitLoading(this, worker);
    waitLoading.open();
  }
  
  @Override
  public boolean validateNext() {
    if(categoryList.getSelectionIndex() < 0) {
      error(ChannelWizard.getLabel("step0.error.no.select.category"));
      return false;
    }
    return true;
  }

  @Override
  public void next() {
    Source source = wizard.getSource();
    source.setGroup(getSelectedGroupName());
    source.setCategory(categoryList.getSelection()[0]);
    if(source.getGroup().equals("XML")) {
      showNext(wizard);  
    } else {
      showNext(wizard, 3);
    }
  }
  
  private class LoadCategoryWorker extends Worker {

    private String error = null;
    private String [] categories = null;
    private String selectedGroup;
    private File file;

    private LoadCategoryWorker() {
    }

    public void abort() {
      ClientConnector2.currentInstance().abort();
    }

    public void before() {
      categoryList.removeAll();
      selectedGroup = getSelectedGroupName();
      file = new File(ClientConnector2.getCacheFolder("sources/type"), "group." + selectedGroup);
    }

    public void execute() {
      try {
        if(file.exists()) {
          byte [] bytes = RWData.getInstance().load(file);
          categories = new String(bytes, Application.CHARSET).trim().split("\n");
        } else {
          categories = new SourcesClientHandler(selectedGroup).loadCategories();
          if(accessChecker == null) {
            accessChecker = new OrganizationClientHandler().loadAccessChecker();
          }
        }
      } catch(Exception exp) {
        error = exp.toString();
      } 
    }


    public void after() {
      if(error != null && !error.isEmpty()) {
        error(error);
        return;
      }

      setCategories(accessChecker, categories);
    }
  }
  
  private String getSelectedGroupName() {
    int idx = groupCombo.getSelectionIndex();
    if(idx < 0) idx = 0;
    if(idx >= groupCombo.getItemCount()) return "ARTICLE";
    return groupCombo.getItem(idx);
  }
  
  private void setCategories(AccessChecker accessChecker, String[] categories) {
    categoryList.removeAll();
    if(categories == null) return;
    if(accessChecker == null) {
      for(String dataCategory : categories) {
        categoryList.add(dataCategory);
      }
      categoryList.select(0);
      return;
    }
    String group = getSelectedGroupName();

    StringBuilder builder = new StringBuilder();
    for(String dataCategory : categories) {
      if(!accessChecker.isPermitAccess(group + "." + dataCategory, true)) continue;
      categoryList.add(dataCategory);
      if(builder.length() > 0) builder.append('\n');
      builder.append(dataCategory);
    }  

    File file = new File(ClientConnector2.getCacheFolder("sources/type"), "group." + group);
    try {
      byte [] bytes = builder.toString().getBytes(Application.CHARSET);
      RWData.getInstance().save(file, bytes);
    } catch (Exception e) {
      file.delete();
    }
    categoryList.select(0);
  }
  
  public void reset() {
    browser.setUrl("about:blank");
  }
}
