/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.users;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.gui.source.SourcesExplorer;
import org.vietspider.model.Group;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2008  
 */
public class CategoryPopup {
  
  private List uiCategories;
  private Combo cboGroupType;
  
  private ArrayList<String> listValue = new ArrayList<String>();
  private String group;
  
  private  final Shell shell ;
  
  public CategoryPopup(Shell parent) {
    shell = new Shell(parent, SWT.CLOSE | SWT.APPLICATION_MODAL);
    int x = parent.getLocation().x + 420;
    int y = parent.getLocation().y + 90;
    shell.setLocation(x, y);
    shell.setImage(parent.getImage());
    shell.setLayout(new GridLayout(1, true));
    shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    
    ApplicationFactory factory = new ApplicationFactory(shell, "Organization", getClass().getName());
    
    cboGroupType = factory.createCombo(SWT.DROP_DOWN | SWT.READ_ONLY);
    cboGroupType.setFont(UIDATA.FONT_9);
    try {
      Group [] groups = new SimpleSourceClientHandler().loadGroups().getGroups();
      for(Group groupEle : groups) {
        if(groupEle.getType().equals(Group.DUSTBIN)) continue;
        cboGroupType.add(groupEle.getType());
      }
    } catch (Exception e) {
      ClientLog.getInstance().setException(cboGroupType.getShell(), e);
    }
    cboGroupType.select(0);
    cboGroupType.setVisibleItemCount(20);
    cboGroupType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    cboGroupType.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {  
        selectGroup();
      }      
    });
    
    uiCategories = factory.createList(shell, SWT.BORDER | SWT.MULTI);
    uiCategories.setFont(UIDATA.FONT_10);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    uiCategories.setLayoutData(gridData); 
    
    Composite bottom = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
    gridData.widthHint = 250;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;
    factory.setComposite(bottom);
    
    factory.createButton("butAdd", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        add();
      }      
    }, factory.loadImage("butAdd.png"));
    
    factory.createButton("butAddAll", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        addAll();
      }      
    }, factory.loadImage("butAdd.png"));
    
    selectGroup();
    
    shell.setSize(300, 400);
//    XPWindowTheme.setWin32Theme(shell);
    shell.open();
    
    while(!shell.isDisposed()) {
      Display display = shell.getDisplay();
      if(!display.readAndDispatch()) display.sleep(); 
    }
  }
  
  private void add() {
    for(String element : uiCategories.getSelection()) {
      listValue.add(group+"."+element);
    }
    shell.dispose();
  }

  private void addAll() {
    listValue.add("*");
    shell.dispose();
  }
  
  public ArrayList<String> getSelected() { return listValue; }
  
  public String getGroup() { return group; }
  
  private void selectGroup() {
    int idx = cboGroupType.getSelectionIndex();
    if(idx < 0) return ;
    group = cboGroupType.getItem(idx);

    try {
      String [] categories = new SourcesClientHandler(group).loadCategories();
      if(categories == null) categories = new String[0];
      uiCategories.setItems(categories);
    }catch(Exception exp){
      ClientLog.getInstance().setException(shell, exp);
    }  

    Preferences prefs = Preferences.userNodeForPackage(SourcesExplorer.class);
    try {
      prefs.put("selectedGroup", String.valueOf(idx));
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
