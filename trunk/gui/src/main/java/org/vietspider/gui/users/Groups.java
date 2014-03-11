/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.users;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.OrganizationClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;
import org.vietspider.ui.widget.waiter.ThreadExecutor;
import org.vietspider.user.Group;
import org.vietspider.user.User;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 11, 2008  
 */
public class Groups extends Composite {

  private Combo cboGroup;
  
  private List lstUsers;
  private List lstCategories;
  
  private MenuItem itemRemoveCategory, itemAddCategory;
  private MenuItem itemRemoveUser, itemAddUser;
  private Button butAddUser, butRemoveUser, butAddCategory, butRemoveCategory;
  
  public Groups(ApplicationFactory factory, Composite parent) {
    super(parent, SWT.NONE);
    
    setLayout(new GridLayout(3, false));
    factory.setComposite(this);
    
    factory.createLabel("groupGroup");
    cboGroup = factory.createCombo(SWT.BORDER);
    cboGroup.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent arg0) {
        selectGroup();
      }
    });
    
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    cboGroup.setLayoutData(gridData);
    cboGroup.setVisibleItemCount(20);
    
    Menu menu = new Menu(getShell(), SWT.POP_UP);
    factory.createMenuItem(menu, "itemDeleteGroup", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        delete(cboGroup.getText());
      }
    });
    cboGroup.setMenu(menu);
    
    factory.createIcon("butSaveGroup", new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent evt) {
        save(true);
      }
    });
    
    org.eclipse.swt.widgets.Group group;
    
    factory.setComposite(this);
    group = factory.createGroup("groupUser", gridData, new GridLayout(1, true));
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 3;
    group.setLayoutData(gridData);
    
    lstUsers = factory.createList(group, SWT.BORDER);
    gridData = new GridData(GridData.FILL_BOTH);
    lstUsers.setLayoutData(gridData);
    
    menu = new Menu(getShell(), SWT.POP_UP);    
    itemAddUser = factory.createMenuItem( menu, "itemAddUser", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        addUser();
      }
    });
    
    itemRemoveUser = factory.createMenuItem( menu, "itemRemoveUser", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeUser();
      }
    });
    
    factory.createMenuItem(menu, SWT.SEPARATOR);
    
    factory.createMenuItem(menu, "itemDeleteUser", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(lstUsers.getSelectionIndex() < 0) return;
        MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
        ClientRM resource = new ClientRM("Organization");
        msg.setMessage(resource.getLabel(Organization.class.getName()+".msgAlertDeleteUser") );
        if(msg.open() != SWT.YES) return ; 
        deleteUser(lstUsers.getItem(lstUsers.getSelectionIndex()));
      }
    });
    
    
    lstUsers.setMenu(menu);
    menu.addMenuListener(new MenuAdapter(){
      @SuppressWarnings("unused")
      public void menuShown(MenuEvent e){
        int index = cboGroup.getSelectionIndex();
        itemAddUser.setEnabled(index > -1);
        itemRemoveUser.setEnabled(index > -1);
      }
    });
    
    Composite addUserComp = new Composite(group, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    addUserComp.setLayoutData(gridData);
    factory.setComposite(addUserComp);
    
    RowLayout rowLayout = new RowLayout();
    rowLayout.justify = true;
    addUserComp.setLayout( rowLayout);

    butAddUser = factory.createButton("butAddUser", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        addUser();
      }      
    }, factory.loadImage("add.png"));
    butAddUser.setLayoutData(new RowData(100, 24));
    butAddUser.setEnabled(false);
    
    butRemoveUser = factory.createButton("butRemoveUser", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeUser();
      }      
    }, factory.loadImage("remove.png"));
    butRemoveUser.setLayoutData(new RowData(100, 24));
    butRemoveUser.setEnabled(false);
    
    factory.setComposite(this);
    group = factory.createGroup("groupCategories", gridData, new GridLayout(1, true));
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 3;
    group.setLayoutData(gridData);
    
    factory.setComposite(group);
    lstCategories = factory.createList(group, SWT.BORDER | SWT.MULTI);
    gridData = new GridData(GridData.FILL_BOTH);
    lstCategories.setLayoutData(gridData);
    
    menu = new Menu(getShell(), SWT.POP_UP);    
    itemAddCategory = factory.createMenuItem( menu, "itemAddCategory", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        addCategory();
      }
    });
    itemRemoveCategory = factory.createMenuItem( menu, "itemRemoveCategory", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeCategory();
      }
    });
    lstCategories.setMenu(menu);
    menu.addMenuListener(new MenuAdapter(){
      @SuppressWarnings("unused")
      public void menuShown(MenuEvent e){
        int index = cboGroup.getSelectionIndex();
        itemAddCategory.setEnabled(index > -1);
        itemRemoveCategory.setEnabled(index > -1);
      }
    });
    
    Composite addCategoriesComp = new Composite(group, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    addCategoriesComp.setLayoutData(gridData);
    factory.setComposite(addCategoriesComp);
    
    rowLayout = new RowLayout();
    rowLayout.justify = true;
    addCategoriesComp.setLayout( rowLayout);

    butAddCategory = factory.createButton("butAddCategory", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        addCategory();
      }      
    }, factory.loadImage("add.png"));
    butAddCategory.setLayoutData(new RowData(100, 24));
    butAddCategory.setEnabled(false);
    
    butRemoveCategory = factory.createButton("butRemoveCategory", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeCategory();
      }      
    }, factory.loadImage("remove.png"));
    butRemoveCategory.setLayoutData(new RowData(100, 24));
    butRemoveCategory.setEnabled(false);
    
    load(-1);
  }
  
  private void load(final int selected) {
    Worker excutor = new Worker() {

      private String [] elements ;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }
      
      public void before() {}

      public void execute() {
        try {
          elements = new OrganizationClientHandler().listGroups();
        }catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        if(elements != null) cboGroup.setItems(elements);
        if(cboGroup.getItemCount() > selected && selected > -1) {
          cboGroup.select(selected);
        } else if(cboGroup.getItemCount() > 0) {
          cboGroup.select(0);
        }
        
        if(selected < 0 && cboGroup.getSelectionIndex() > -1)  {
          load(cboGroup.getItem(cboGroup.getSelectionIndex()));
          return;
        }
        
        boolean enabled = cboGroup.getSelectionIndex() > -1;
        butAddUser.setEnabled(enabled);
        butRemoveUser.setEnabled(enabled);
        butAddCategory.setEnabled(enabled);
        butRemoveCategory.setEnabled(enabled);
      }
    };
    new ThreadExecutor(excutor, cboGroup).start();
  }
  
  private void delete(final String groupName) {
    Worker excutor = new Worker() {

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }
      
      public void before() {}

      public void execute() {
        OrganizationClientHandler clientHandler = new OrganizationClientHandler();
        try {
          String [] userNames = clientHandler.listUsers();
          for(String userName : userNames) {
            User user = clientHandler.loadUser(userName);
            if(user == null) continue;
            for(String element : user.getGroups()) {
              if(element == null) continue;
              if(element.equals(groupName)) {
                user.getGroups().remove(element);
                clientHandler.save(user);
                break;
              }
            }
          }
          clientHandler.deleteGroup(groupName);
        }catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() { load(-1); }
    };
    new ThreadExecutor(excutor, cboGroup).start();
  }
  
  void save(boolean isNew) {
    if(isNew) {
      lstUsers.removeAll();
      lstCategories.removeAll();
    }
    final Group group = new Group();
    group.setGroupName(cboGroup.getText());
    if(group.getGroupName().isEmpty()) return;
    
    String [] users = lstUsers.getItems();
    for(String user : users){
      group.getUsers().add(user);
    }
    
    String [] categories = lstCategories.getItems();
    for(String category : categories){
      group.getWorkingCategories().add(category);
    }

    Worker excutor = new Worker() {

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          new OrganizationClientHandler().save(group);
        }catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        load(cboGroup.getSelectionIndex()) ;
      }
    };
    new ThreadExecutor(excutor, cboGroup).start();
  }
  
  void selectGroup() {
    int idx = cboGroup.getSelectionIndex();
    if(idx < 0) return; 
    load(cboGroup.getItem(idx));
    butAddUser.setEnabled(true);
    butRemoveUser.setEnabled(true);
    butAddCategory.setEnabled(true);
    butRemoveCategory.setEnabled(true);
  }
  
  String getSelectedGroup() {
    int idx = cboGroup.getSelectionIndex();
    if(idx < 0) return null; 
    return cboGroup.getItem(idx);
  }
  
  private void load(String groupName) {
    lstCategories.removeAll();
    lstUsers.removeAll();
    
    Group group = null;
    try {
      group = new OrganizationClientHandler().loadGroup(groupName);
    }catch (Exception e) {
      ClientLog.getInstance().setException(cboGroup.getShell(), e);
    }
    if(group == null) return;
    
    
    if(group.getUsers() != null) {
      for(String element : group.getUsers()) {
        if(element == null || element.trim().isEmpty()) continue;
        lstUsers.add(element.trim());
      }
    }
    
    
    if(group.getWorkingCategories() != null) {
      for(String element : group.getWorkingCategories()) {
        if(element == null || element.trim().isEmpty()) continue;
        lstCategories.add(element.trim());
      }
    }
  }
  
  private void addUser() {
    String groupName = getSelectedGroup();
    if(groupName == null) return;
    
    UserPopup popup = new UserPopup(cboGroup.getShell());
    OrganizationClientHandler clientHandler = new OrganizationClientHandler();
    for(String element : popup.getSelected()) {
      boolean add = true;
      for(int i = 0; i < lstUsers.getItemCount(); i++) {
        if(lstUsers.getItem(i).equals(element)) {
          add = false;
          break;
        }
      }
      
      try {
        User user = clientHandler.loadUser(element);
        if(user == null) return;
        if(!user.getGroups().contains(groupName)) {
          user.getGroups().add(groupName);
          clientHandler.save(user);
        }
      } catch (Exception e) {
        ClientLog.getInstance().setException(cboGroup.getShell(), e);
      }
      if(add) lstUsers.add(element);
    }
    if(popup.getSelected().size() > 0) save(false);
  }
  
  private void removeUser() {
    int idxGroup = cboGroup.getSelectionIndex();
    if(idxGroup < 0) return;
    String groupName = cboGroup.getItem(idxGroup);
    int idx = lstUsers.getSelectionIndex();
    if(idx < 0 || idx >= lstUsers.getItemCount()) return;
    
    String username = lstUsers.getItem(idx);
    OrganizationClientHandler clientHandler = new OrganizationClientHandler();
    try {
      User user = clientHandler.loadUser(username);
      if(user == null) return;
      for(String element : user.getGroups()) {
        if(element == null) continue;
        if(element.equals(groupName)) {
          user.getGroups().remove(element);
          clientHandler.save(user);
          break;
        }
      }
    } catch (Exception e) {
      ClientLog.getInstance().setException(cboGroup.getShell(), e);
    }
    lstUsers.remove(idx);
    save(false);
  }
  
  private void addCategory() {
    int idxGroup = cboGroup.getSelectionIndex();
    if(idxGroup < 0) return;
    
    CategoryPopup popup = new CategoryPopup(cboGroup.getShell());
    for(String element : popup.getSelected()) {
      boolean add = true;
      for(int i = 0; i < lstCategories.getItemCount(); i++) {
        if(lstCategories.getItem(i).equals(element)) {
          add = false;
          break;
        }
      }
      if(add) lstCategories.add(element);
    }
    if(popup.getSelected().size() > 0) save(false);
  }
  
  private void removeCategory() {
    int idxGroup = cboGroup.getSelectionIndex();
    if(idxGroup < 0) return;
    
    String [] values = lstCategories.getSelection();
    for(String value : values) {
      if(value == null) continue;
      lstCategories.remove(value);
    }
    save(false);
  }
  
  void deleteUser(final String userName) {
    if(userName.trim().isEmpty()) return;
    Worker excutor = new Worker() {

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }
      
      public void before() {}

      public void execute() {
        OrganizationClientHandler clientHandler = new OrganizationClientHandler();
        try {
          String [] groupNames = clientHandler.listGroups();
          for(String groupName : groupNames) {
            Group group = clientHandler.loadGroup(groupName);
            if(group == null) continue;
            for(String element : group.getUsers()) {
              if(element.equals(userName)) {
                group.getUsers().remove(element);
                clientHandler.save(group);
                break;
              }
            }
          }
          clientHandler.deleteUser(userName);
        }catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() { 
        selectGroup();
      }
    };
    new ThreadExecutor(excutor, cboGroup).start();
  }

  public List getListUsers() {return lstUsers; }
}

