/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.source;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.action.LoadGroupCategorySource;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.user.AccessChecker;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Mar 25, 2007
 */
public class TreeSources extends SourcesHandler {

  private Tree tree;

  private Creator creator;

  private TreeItem lastSelectedItem;

  private boolean clickMouse = false;

  public TreeSources(ApplicationFactory factory, Composite parent) {
    super(parent);

    setLayout(new FillLayout());

    tree = factory.createTree(this, SWT.MULTI);
    tree.setLinesVisible(false);

    createMenu(tree, factory);

    tree.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent arg0) {
        clickMouse = true;
      }
    });

    tree.addTreeListener(new TreeAdapter(){
      @SuppressWarnings("unused")
      public void treeCollapsed(TreeEvent arg0) {
        clickMouse = false;
      }

      @SuppressWarnings("unused")
      public void treeExpanded(TreeEvent arg0) {
        clickMouse = false;
      }

    });

    tree.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent event) {
        if(!clickMouse) return;
        clickMouse = false;
        if(tree.getSelectionCount() < 1) return;
        TreeItem selectedItem = tree.getSelection()[0];
        TreeItem parentItem = selectedItem.getParentItem();
        if(parentItem == null) {
          new LoadGroupCategorySource(TreeSources.this, new Worker[0], null, selectedItem.getText());
          creator.selectData(new Worker[0], null, selectedItem.getText());
          return ;
        }

        String category = parentItem.getText();
        String name = selectedItem.getText();
        creator.setSource(null, category, name, false);
      }      
    });  

  }

  String[] getSelectedCategories() {
    TreeItem [] selectedItems = tree.getSelection();
    if(selectedItems.length < 1) return null;

    List<String> list = new ArrayList<String>();
    for(TreeItem selectedItem  : selectedItems) {
      String category =  null;  
      if(selectedItem.getParentItem() == null) {
        category = selectedItem.getText();;
      } else {
        category  = selectedItem.getParentItem().getText(); 
      }
      if(!list.contains(category)) list.add(category);
    }
    return list.toArray(new String[list.size()]);
  }

  public String getSelectedCategory() {
    TreeItem [] selectedItems = tree.getSelection();
    if(selectedItems.length < 1) return null;

    if(selectedItems[0].getParentItem() == null) {
      return selectedItems[0].getText();
    }

    return selectedItems[0].getParentItem().getText();
  }

  public String[] getSelectedSources() {
    TreeItem [] selectedItems = tree.getSelection();
    if(selectedItems.length < 1) return null;

    List<String> list = new ArrayList<String>();
    for(TreeItem selectedItem  : selectedItems) {
      if(selectedItem.getParentItem() == null) continue;
      String value = selectedItem.getText(); 
      if(!list.contains(value)) list.add(value);
    }
    return list.toArray(new String[list.size()]);
  }

  String[] getSources(String category) {
    TreeItem [] treeItems = tree.getItems();
    TreeItem treeItem = null;
    for(TreeItem ele : treeItems) {
      if(ele.getText().equals(category)) {
        treeItem = ele;
        break;
      }
    }
    if(treeItem == null) return null;

    TreeItem [] items = treeItem.getItems();
    if(items.length < 1) return null;

    List<String> list = new ArrayList<String>();
    for(TreeItem item : items) {
      String value = item.getText(); 
      if(!list.contains(value)) list.add(value);
    }
    return list.toArray(new String[list.size()]);
  }

  public TreeItem lookCategoryItem(String category){
    TreeItem [] items = tree.getItems();
    if(items != null){
      for(TreeItem item : items) {
        if(item.getText().equals(category)) return item;
      }
    }
    TreeItem item = new TreeItem(tree, SWT.NONE);
    item.setFont(UIDATA.FONT_10B);
    item.setText(category);  
    return item;
  }


  public void setCreator(Creator creator) { this.creator = creator; }
  public Creator getCreator() { return creator; }

  public void setCategories(AccessChecker accessChecker, String[] categories) {
    tree.removeAll();
    if(categories == null) return;
    if(accessChecker == null) {
      for(String category : categories) {
        lookCategoryItem(category);
      }
      return;
    }
    
    group = creator.getSelectedGroupName();

    StringBuilder builder = new StringBuilder();
    for(String category : categories) {
      if(!accessChecker.isPermitAccess(group + "." + category, true)) continue;
      lookCategoryItem(category);
      if(builder.length() > 0) builder.append('\n');
      builder.append(category);
    }

    File file = new File(ClientConnector2.getCacheFolder("sources/type"), "group." + group);
    try {
      byte [] bytes = builder.toString().getBytes(Application.CHARSET);
      RWData.getInstance().save(file, bytes);
    } catch (Exception e) {
      file.delete();
    }

  }

  public void setSelectedCategory(String category) {
    if(category == null) return;
    if(lastSelectedItem != null && !lastSelectedItem.isDisposed()) {
      lastSelectedItem.setBackground(new Color(getDisplay(), 255, 255, 255));
      lastSelectedItem.setExpanded(false);
      lastSelectedItem.removeAll();
      lastSelectedItem = null;
    }
    

    TreeItem [] items = tree.getItems();
    if(items == null || items.length < 1) return ;

    TreeItem selectedItem = null;
    for(TreeItem item  : items) {
      if(item.getText().equals(category)) {
        selectedItem = item;
        break;
      }
    }
    if(selectedItem == null) return;
    tree.setSelection(selectedItem);
  }

  public void setSelectedSources(Worker[]plugins, String... sources) {
    TreeItem [] items  = tree.getSelection();
    if(items.length < 1) return;
    TreeItem selectedItem = items[0];
    
    items = selectedItem.getItems();
    if(items == null || items.length < 1) return ;

    for(int i = 0; i < items.length; i++) {
      for(String source : sources) {
        if(!items[i].getText().equals(source)) continue;
        tree.setSelection(new TreeItem[]{selectedItem, items[i]});
        creator.setSource(plugins, getSelectedCategory(), source, false);
        int topIndex = i;
        int visibleCount = tree.getSize().y/48;
        if(topIndex - visibleCount < 0) return;
        topIndex -= visibleCount;
        tree.setTopItem(items[topIndex]);
        break;
      }
    }
  }

  @Override
  public void setSources(String... sources) {
    TreeItem [] items  = tree.getSelection();
    if(items.length < 1) return;
    TreeItem item  = items[0];
    if(lastSelectedItem == item) return;

    if(lastSelectedItem != null && !lastSelectedItem.isDisposed()) {
      lastSelectedItem.setBackground(new Color(getDisplay(), 255, 255, 255));
      lastSelectedItem.setExpanded(false);
      lastSelectedItem.removeAll();
    }
    lastSelectedItem = item;
    lastSelectedItem.setBackground(new Color(getDisplay(), 190, 190, 255));

    if(sources == null || sources.length < 1) return;

    for(String source : sources) {
      item = new TreeItem(lastSelectedItem, SWT.NONE);
      item.setFont(UIDATA.FONT_9);
      int idx = source.indexOf('.');
      if(idx > -1) source = source.substring(idx+1);
      item.setText(source);
    }
    if(!lastSelectedItem.getExpanded()) lastSelectedItem.setExpanded(true);

  }

  @Override
  @SuppressWarnings("unused")
  public void setDisableSources(String... sources) {
  }

}
