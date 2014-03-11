/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.cms;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.vietspider.model.plugin.Category;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 2, 2010  
 */
public class CommonCategoriesTree {

  protected Tree tree;
  
  public CommonCategoriesTree(Composite parent, GridData gridData) {
    tree = new Tree(parent, SWT.BORDER | SWT.MULTI);
//  treeCategories.setLinesVisible(false);
    if(gridData != null) tree.setLayoutData(gridData);
  }
  
  public Tree getTree() { return tree; }

  public void createCategories(List<Category> categories) {
    if(categories == null) return;
    for(int i = 0; i < categories.size(); i++) {
      TreeItem item = new TreeItem(tree, SWT.NONE);
      Category category = categories.get(i);
      item.setData(category.getCategoryId()+"/"+category.getCategoryName());
      item.setText(category.getCategoryName());
      createItem(item, category.getSubCategories());
    }
  }
  
  private void createItem(TreeItem parent, List<Category> cates) {
    if(cates == null) return;
    for(int i = 0; i < cates.size(); i++) {
      TreeItem item = new TreeItem(parent, SWT.NONE);
      Category category = cates.get(i);
      if(category == null 
          || category.getCategoryId() == null
          || category.getCategoryName() == null) continue;
      item.setData(category.getCategoryId()+"/"+category.getCategoryName());
      item.setText(category.getCategoryName());
      createItem(item, category.getSubCategories());
    }
  }
  
  public String getSelectedId() {
    TreeItem [] items = tree.getSelection();
    if(items == null || items.length < 1) return null;
    
    String category  = (String)items[0].getData();
    int index = category.indexOf('/');
    if(index < 0)  return null;
//    System.out.println(" ====> "+ category+ " : " + category.substring(0, index));
    return category.substring(0, index);
  }
  
  public String[] getSelected() {
    TreeItem [] items = tree.getSelection();
    if(items == null || items.length < 1) return null;
    
    String category  = (String)items[0].getData();
    int index = category.indexOf('/');
    if(index < 0)  return null;
//    System.out.println(" ====> "+ category+ " : " + category.substring(0, index));
    return new String[]{category.substring(0, index), category.substring(index+1)};
  }
  
  public void setSelectById(String id) {
    setSelectById(tree.getItems(), id+"/");
  }
  
  private boolean setSelectById(TreeItem [] items, String id) {
    if(items == null) return false;
    for(int i = 0; i < items.length; i++) {
      String text = (String) items[i].getData();
      if(text.startsWith(id)) {
        tree.setSelection(items[i]);
        return true;
      }
      if(setSelectById(items[i].getItems(), id)) return true;
    }
    return false;
  }
  
 
}
