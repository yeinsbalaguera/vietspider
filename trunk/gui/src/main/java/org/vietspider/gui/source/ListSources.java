/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.source;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.RWData;
import org.vietspider.common.text.VietComparator;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.action.LoadGroupCategorySource;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.user.AccessChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 16, 2008  
 */
public class ListSources extends SourcesHandler {

  private Combo cboCategories;
//  private List listSources;
//  private Tree listSources;
  private Table listSources;

  private Creator creator;

  public ListSources(ApplicationFactory factory, Composite parent) {
    super(parent);
    setLayout(new GridLayout(1, false));

    cboCategories= new Combo(this, SWT.BORDER | SWT.READ_ONLY);
    cboCategories.setFont(UIDATA.FONT_9);
    cboCategories.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        int idx = cboCategories.getSelectionIndex();
        if(idx < 0) return;
        String category = cboCategories.getItem(idx);
        new LoadGroupCategorySource(ListSources.this, new Worker[0], null, category);
        creator.selectData(new Worker[0], null, category);

        Preferences prefs_ = Preferences.userNodeForPackage(Creator.class);
        prefs_.put(group+".selected.category", String.valueOf(idx));
      }
    });
    cboCategories.setVisibleItemCount(20);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    cboCategories.setLayoutData(gridData);
    KeyAdapter keyAdapter = new KeyAdapter(){
      public void keyPressed(KeyEvent e) {
        if(e.keyCode == SWT.DEL) removeSources();
      }
    };
    cboCategories.addKeyListener(keyAdapter);

//    listSources = factory.createTree(this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
    listSources = new Table(this, SWT.BORDER | SWT.MULTI | SWT.VIRTUAL);
    listSources.setLinesVisible(false);
//    listSources.set
      //factory.createList(this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
    listSources.setFont(UIDATA.FONT_9);
    //    if(Application.LICENSE == Install.PERSONAL) listSources.setEnabled(false);
    gridData = new GridData(GridData.FILL_BOTH);
    listSources.setLayoutData(gridData);

    createMenu(listSources, factory);

    listSources.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        if(listSources.getSelectionCount() < 1) return;
        String category = cboCategories.getText().trim();
//        TreeItem item = listSources.getSelection()[0];
        TableItem item = listSources.getSelection()[0];
        String name = item.getText();
//        String name = listSources.getSelection()[0];
        creator.setSource(new Worker[0], category, name, false);
      }
    });
    listSources.addKeyListener(keyAdapter);
  }

  @Override
  String[] getSelectedCategories() {
    int idx = cboCategories.getSelectionIndex();
    return idx < 0 ? null: new String[]{cboCategories.getItem(idx)};
  }

  public String getSelectedCategory() {
    if(cboCategories.getItemCount() < 1) return null;
    int idx = cboCategories.getSelectionIndex();
    if(idx < 0) idx = 0; 
    return cboCategories.getItem(idx);
  }

  public String[] getSelectedSources() {
//    TreeItem [] items = listSources.getSelection();
    TableItem [] items = listSources.getSelection();
    String [] names = new String[items.length];
    for(int i = 0; i < names.length; i++) {
      names[i] = items[i].getText();
    }
    return names;
//    return listSources.getSelection(); 
  }

  @SuppressWarnings("unused")
  String[] getSources(String category) {
//    TreeItem [] items = listSources.getItems();
    TableItem [] items = listSources.getItems();
    String [] names = new String[items.length];
    for(int i = 0; i < names.length; i++) {
      names[i] = items[i].getText();
    }
    return names;
//    return listSources.getItems(); 
  }

  public void setCreator(Creator creator) { this.creator = creator; }
  public Creator getCreator() { return creator; }

  public void setCategories(AccessChecker accessChecker, String[] categories) {
    cboCategories.removeAll();
    if(categories == null) return;
    if(accessChecker == null) {
      for(String dataCategory : categories) {
        cboCategories.add(dataCategory);
      }
      cboCategories.select(0);
      return;
    }
    group = creator.getSelectedGroupName();

    StringBuilder builder = new StringBuilder();
    for(String dataCategory : categories) {
      if(!accessChecker.isPermitAccess(group + "." + dataCategory, true)) continue;
      cboCategories.add(dataCategory);
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
    cboCategories.select(0);
  }

  public void setSelectedCategory(String category) {
    for(int i = 0; i < cboCategories.getItemCount(); i++) {
      if(cboCategories.getItem(i).equals(category)) {
        cboCategories.select(i);
        return;
      }
    }  
  }

  public void setSelectedSources(Worker[]plugins,  String... sources) {
    if(sources == null || sources.length < 1) return;
//    TreeItem [] items = listSources.getItems();
    TableItem [] items = listSources.getItems();
//    TreeItem topItem = null;
//    TableItem topItem = null;
    int topIndex = -1;
    List<TableItem> selected = new ArrayList<TableItem>();
    for(int i = 0; i < sources.length; i++) {
      for(int j = 0; j < items.length; j++) {
        if(items[j].getText().equals(sources[i])) {
          selected.add(items[j]);
//          if(topItem == null) topItem = items[j];
          if(topIndex < 0) topIndex = j;
        }
      }
    }
    listSources.setSelection(selected.toArray(new TableItem[0]));
    creator.setSource(plugins, getSelectedCategory(), sources[0], true);
    listSources.setTopIndex(topIndex);
//    if(topItem != null) listSources.setTopItem(topItem);
//  int topIndex = listSources.getSelectionIndex();
    
    //scroll to selected
//    listSources.setSelection(sources);
//    creator.setSource(plugins, getSelectedCategory(), sources[0], true);
//    int topIndex = listSources.getSelectionIndex();
//    int visibleCount = listSources.getSize().y/28;
//    if(topIndex - visibleCount < 0) return;
//    topIndex -= visibleCount;
//    listSources.setTopIndex(topIndex);

  }

  public void setSources(String... sources) {
    for(int i = 0; i < sources.length; i++) {
      int idx = sources[i].indexOf('.');
      if(idx > -1) sources[i] = sources[i].substring(idx+1);
    }
    java.util.Arrays.sort(sources, new VietComparator());
    listSources.removeAll();
    for(int i = 0; i < sources.length; i++) {
//      TreeItem item = new TreeItem(listSources, SWT.NONE);
      TableItem item = new TableItem(listSources, SWT.NONE);
      item.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
      item.setText(sources[i]);
    }
//    listSources.setItems(sources);
    listSources.setToolTipText(sources.length+" items");
  }
  
  @Override
  public void setDisableSources(String... sources) {
    if(Application.LICENSE == Install.PERSONAL) return;
    TableItem [] items = listSources.getItems();
    for(int i = 0; i < sources.length; i++) {
      for(int j = 0; j < items.length; j++) {
//        System.out.println(" bebebe  "+ sources[i]);
        if(items[j].getText().equals(sources[i])) {
          items[j].setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
        }
      }
    }
  }
 
  public void setEnable(boolean value) {
    listSources.setEnabled(value);
    cboCategories.setEnabled(value);
  }
  
}
