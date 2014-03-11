/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.users;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.OrganizationClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;
import org.vietspider.user.Group;
import org.vietspider.user.User;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 11, 2008  
 */
public class Groups2 extends Composite {

  private List lstGroups;
  private Text txtGroupName;
  private List lstUsers;
  private List lstCategories;
  private MenuItem itemRemoveCategory, itemAddCategory;
  private MenuItem itemRemoveUser, itemAddUser;
  private MenuItem itemDeleteGroup;
  private Button butAddUser, butRemoveUser, butAddCategory, butRemoveCategory;
  
  private Users users;

  public Groups2(ApplicationFactory factory, Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout(4, false));

    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    factory.setComposite(this);
    
    org.eclipse.swt.widgets.Group group = factory.createGroup("groupGroup", gridData, new GridLayout(1, true));
    factory.setComposite(group);

    Composite addGroupComp = new Composite(group, SWT.NONE);
    addGroupComp.setLayout(new GridLayout(3, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    addGroupComp.setLayoutData(gridData);
    factory.setComposite(addGroupComp);

    factory.createLabel("lblGroupName");
    txtGroupName = factory.createText();
    txtGroupName.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.BEGINNING);
    txtGroupName.setLayoutData(gridData);
    txtGroupName.addModifyListener(new ModifyListener() {
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent arg0) {
        lstUsers.removeAll();
        lstCategories.removeAll();
      }
    });
    txtGroupName.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent evt) {
        if(evt.keyCode == SWT.CR) save(-1);
      }
    });

    Button button = factory.createButton("butSaveGroup", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        save(-1);
      }      
    }, factory.loadImage("add.png"));
    gridData = new GridData();//GridData.HORIZONTAL_ALIGN_CENTER
    gridData.heightHint = 24;
//    gridData.horizontalSpan = 2;
    button.setLayoutData(gridData);
    
    lstGroups = factory.createList(group, SWT.BORDER);
    lstGroups.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_BOTH);
    lstGroups.setLayoutData(gridData);
    lstGroups.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent arg0) {
        int idx = lstGroups.getSelectionIndex();
        if(idx < 0) return; 
        load(lstGroups.getItem(idx));
        butAddUser.setEnabled(true);
        butRemoveUser.setEnabled(true);
        butAddCategory.setEnabled(true);
        butRemoveCategory.setEnabled(true);
      }
    });
    
    Menu menu = new Menu(getShell(), SWT.POP_UP);    
    itemDeleteGroup = factory.createMenuItem( menu, "itemDeleteGroup", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
        ClientRM resource = new ClientRM("Organization");
        msg.setMessage(resource.getLabel(Organization.class.getName()+".msgAlertDeleteGroup") );
        if(msg.open() != SWT.YES) return ; 
        delete(lstGroups.getItem(lstGroups.getSelectionIndex()));
        butAddUser.setEnabled(false);
        butRemoveUser.setEnabled(false);
        butAddCategory.setEnabled(false);
        butRemoveCategory.setEnabled(false);
      }
    });
    
    menu.addMenuListener(new MenuAdapter(){
      @SuppressWarnings("unused")
      public void menuShown(MenuEvent e){
        itemDeleteGroup.setEnabled(lstGroups.getSelectionIndex() > -1);
      }
    });
    lstGroups.setMenu(menu);

    factory.setComposite(this);

    group = factory.createGroup("groupUser", gridData, new GridLayout(1, true));
    
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
    lstUsers.setMenu(menu);
    menu.addMenuListener(new MenuAdapter(){
      @SuppressWarnings("unused")
      public void menuShown(MenuEvent e){
        String name = txtGroupName.getText().trim();
        itemAddUser.setEnabled(!name.isEmpty());
        itemRemoveUser.setEnabled(!name.isEmpty());
        itemRemoveUser.setEnabled(lstUsers.getSelectionIndex() > -1);
      }
    });
    
    factory.setComposite(this);
    
//    users = new Users(this, factory);
    users.setLayoutData(gridData);

    factory.setComposite(this);
    
    group = factory.createGroup("groupCategories", gridData, new GridLayout(1, true));
    
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
        String name = txtGroupName.getText().trim();
        itemAddCategory.setEnabled(!name.isEmpty());
        itemRemoveCategory.setEnabled(!name.isEmpty());
        itemRemoveCategory.setEnabled(lstCategories.getSelectionIndex() > -1);
      }
    });

    load(-1);
  }

  private void save(final int selected) {
    final Group group = new Group();
    group.setGroupName(txtGroupName.getText().trim());
    if(group.getGroupName().isEmpty()) return;
    
    String [] users_ = lstUsers.getItems();
    for(String user : users_){
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
        load(selected) ;
      }
    };
    new ThreadExecutor(excutor, lstGroups).start();
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
        if(elements != null) lstGroups.setItems(elements);
        if(lstGroups.getItemCount() > selected && selected > -1) lstGroups.select(selected);
        boolean enabled = lstGroups.getSelectionIndex() > -1;
        butAddUser.setEnabled(enabled);
        butRemoveUser.setEnabled(enabled);
        butAddCategory.setEnabled(enabled);
        butRemoveCategory.setEnabled(enabled);
      }
    };
    new ThreadExecutor(excutor, lstGroups).start();
  }
  
  private void load(String groupname) {
    Group group = null;
    try {
      group = new OrganizationClientHandler().loadGroup(groupname);
    }catch (Exception e) {
      ClientLog.getInstance().setException(txtGroupName.getShell(), e);
    }
    if(group == null) return;
    txtGroupName.setText(group.getGroupName());
    
    lstUsers.removeAll();
    if(group.getUsers() != null) {
      for(String element : group.getUsers()) {
        if(element == null || element.trim().isEmpty()) continue;
        lstUsers.add(element.trim());
      }
    }
    
    lstCategories.removeAll();
    if(group.getWorkingCategories() != null) {
      for(String element : group.getWorkingCategories()) {
        if(element == null || element.trim().isEmpty()) continue;
        lstCategories.add(element.trim());
      }
    }
  }
  
  private void delete(final String groupname) {
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
              if(element.equals(groupname)) {
                user.getGroups().remove(element);
                clientHandler.save(user);
                break;
              }
            }
          }
          clientHandler.deleteGroup(groupname);
        }catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() { load(-1); }
    };
    new ThreadExecutor(excutor, txtGroupName).start();
  }
  
  private void addUser() {
    int idxGroup = lstGroups.getSelectionIndex();
    String groupname = txtGroupName.getText();
    UserPopup popup = new UserPopup(lstGroups.getShell());
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
        if(!user.getGroups().contains(groupname)) {
          user.getGroups().add(groupname);
          clientHandler.save(user);
        }
      } catch (Exception e) {
        ClientLog.getInstance().setException(txtGroupName.getShell(), e);
      }
      if(add) lstUsers.add(element);
    }
    if(popup.getSelected().size() > 0) save(idxGroup);
  }
  
  private void removeUser() {
    int idxGroup = lstGroups.getSelectionIndex();
    int idx = lstUsers.getSelectionIndex();
    if(idx < 0 || idx >= lstUsers.getItemCount()) return;
    String groupname = txtGroupName.getText();
    
    String username = lstUsers.getItem(idx);
    OrganizationClientHandler clientHandler = new OrganizationClientHandler();
    try {
      User user = clientHandler.loadUser(username);
      if(user == null) return;
      for(String element : user.getGroups()) {
        if(element == null) continue;
        if(element.equals(groupname)) {
          user.getGroups().remove(element);
          clientHandler.save(user);
          break;
        }
      }
    } catch (Exception e) {
      ClientLog.getInstance().setException(txtGroupName.getShell(), e);
    }
    lstUsers.remove(idx);
    save(idxGroup);
  }
  
  private void addCategory() {
    int idxGroup = lstGroups.getSelectionIndex();
    CategoryPopup popup = new CategoryPopup(lstGroups.getShell());
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
    if(popup.getSelected().size() > 0) save(idxGroup);
  }
  
  private void removeCategory() {
    int idxGroup = lstGroups.getSelectionIndex();
    String [] values = lstCategories.getSelection();
    for(String value : values) {
      if(value == null) continue;
      lstCategories.remove(value);
    }
    save(idxGroup);
  }
}

