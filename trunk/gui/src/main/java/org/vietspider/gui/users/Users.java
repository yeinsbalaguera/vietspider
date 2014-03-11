/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
public class Users extends Composite {

  private List lstUsers;
  private Text txtUsername;
  private Text txtFullname;
  private Combo cboPermission;
  private Text txtPassword;
  private Text txtConfirmPassword;
  private Text txtEmail;
  private List lstGroups;
  private MenuItem itemAddGroup, itemRemoveGroup;
  
  private Groups groupUI; 

  public Users(Composite parent, Groups grps, ApplicationFactory factory) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout(2, false));
    
    this.groupUI = grps;
    this.lstUsers = groupUI.getListUsers();

    GridData gridData;

    factory.setComposite(this);

    lstUsers.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent arg0) {
        int idx = lstUsers.getSelectionIndex();
        if(idx < 0) return; 
        load(lstUsers.getItem(idx));
      }
    });
    
    /*
    Menu menu = new Menu(getShell(), SWT.POP_UP);    
    itemDeleteUser = factory.createMenuItem( menu, "itemDeleteUser", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
        ClientRM resource = new ClientRM("Organization");
        msg.setMessage(resource.getLabel(Organization.class.getName()+".msgAlertDeleteUser") );
        if(msg.open() != SWT.YES) return ; 
        delete(lstUsers.getItem(lstUsers.getSelectionIndex()));
      }
    });
    
    menu.addMenuListener(new MenuAdapter(){
      @SuppressWarnings("unused")
      public void menuShown(MenuEvent e){
        itemDeleteUser.setEnabled(lstUsers.getSelectionIndex() > -1);
      }
    });
    lstUsers.setMenu(menu);

    Composite composite = new Composite(sash0, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    factory.setComposite(composite);*/

    factory.createLabel("lblUsername");
    txtUsername = factory.createText();
    txtUsername.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 200;
    txtUsername.setLayoutData(gridData);  

    factory.createLabel("lblPassword");
    txtPassword = factory.createText(SWT.BORDER | SWT.PASSWORD);
    txtPassword.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 200;
    txtPassword.setLayoutData(gridData); 
    
    factory.createLabel("lblConfirmPassword");
    txtConfirmPassword = factory.createText(SWT.BORDER | SWT.PASSWORD);
    txtConfirmPassword.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 200;
    txtConfirmPassword.setLayoutData(gridData); 
    
    factory.createLabel("lblPermission");
    cboPermission = factory.createCombo(SWT.READ_ONLY);
    gridData = new GridData();
    gridData.widthHint = 100;
    cboPermission.setLayoutData(gridData);
    cboPermission.setItems(new String[]{"READ ONLY", "EDIT SOURCE", "ADMINISTRATOR", "MONITOR"});
    cboPermission.select(0);

    factory.createLabel("lblFullname");
    txtFullname = factory.createText();
    txtFullname.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 250;
    txtFullname.setLayoutData(gridData);  

    factory.createLabel("lblEmail");
    txtEmail = factory.createText();
    txtEmail.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 250;
    txtEmail.setLayoutData(gridData);  

    Label label = factory.createLabel("lblGroup");
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    label.setLayoutData(gridData);

    lstGroups = factory.createList(this, SWT.BORDER);
    lstGroups.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.heightHint = 150;
    gridData.widthHint = 200;
    lstGroups.setLayoutData(gridData);

    Menu menu = new Menu(getShell(), SWT.POP_UP);    
    itemAddGroup = factory.createMenuItem( menu, "itemAddGroup", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String userName = txtUsername.getText().trim();

        GroupPopup popup = new GroupPopup(lstGroups.getShell());
        OrganizationClientHandler clientHandler = new OrganizationClientHandler();
        for(String element : popup.getSelected()) {
          boolean add = true;
          for(int i = 0; i < lstGroups.getItemCount(); i++) {
            if(lstGroups.getItem(i).equals(element)) {
              add = false;
              break;
            }
          }
          try {
            Group group = clientHandler.loadGroup(element);
            if(!group.getUsers().contains(userName)) {
              group.getUsers().add(userName);
              clientHandler.save(group);
            }
            if(add) lstGroups.add(element);
          }catch (Exception e) {
            ClientLog.getInstance().setException(txtEmail.getShell(), e);
          }
        }
        if(popup.getSelected().size() > 0) save();
      }
    });
    itemRemoveGroup = factory.createMenuItem( menu, "itemRemoveGroup", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String username = txtUsername.getText().trim();
        int idx = lstGroups.getSelectionIndex();
        
        String groupName = lstGroups.getItem(idx);
        lstGroups.remove(idx);
        OrganizationClientHandler clientHandler = new OrganizationClientHandler();
        try {
          Group group = clientHandler.loadGroup(groupName);
          for(String element : group.getUsers()) {
            if(element.equals(username)) {
              group.getUsers().remove(element);
              clientHandler.save(group);
              break;
            }
          }
        }catch (Exception e) {
          ClientLog.getInstance().setException(txtEmail.getShell(), e);
        }
        save();
      }
    });
    lstGroups.setMenu(menu);
    menu.addMenuListener(new MenuAdapter(){
      @SuppressWarnings("unused")
      public void menuShown(MenuEvent e){
        String username = txtUsername.getText().trim();
        itemAddGroup.setEnabled(!username.isEmpty());
        itemRemoveGroup.setEnabled(!username.isEmpty());
        itemRemoveGroup.setEnabled(lstGroups.getSelectionIndex() > -1);
      }
    });
   
    Composite bottom = new Composite(this, SWT.NONE);
    gridData = new GridData();
    gridData.horizontalSpan = 2;
    gridData.widthHint = 500;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout( rowLayout);
    rowLayout.justify = true;
    factory.setComposite(bottom);
    
    factory.createButton("butDelete", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
       groupUI.deleteUser(txtUsername.getText());
      }      
    }, factory.loadImage("butRemove.png"));  

    factory.createButton("butSave", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        save();
      }      
    }, factory.loadImage("save.png"));    

    factory.createButton("butReset", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        reset();
      }      
    }, factory.loadImage("butReset.png"));    

  }

  private void reset() {
    txtUsername.setText("");
    txtPassword.setText("");
    txtFullname.setText("");
    txtConfirmPassword.setText("");
    cboPermission.select(0);
    txtEmail.setText("");
    lstGroups.removeAll();
  }

  private void load(String username) {
    reset();
    User user = null;
    try {
      user = new OrganizationClientHandler().loadUser(username);
    }catch (Exception e) {
      ClientLog.getInstance().setException(txtEmail.getShell(), e);
    }
    if(user == null) return;
    txtUsername.setText(user.getUserName());
    if(user.getPassword() != null) txtPassword.setText(user.getPassword());
    if(user.getPassword() != null) txtConfirmPassword.setText(user.getPassword());
    if(user.getFullName() != null) txtFullname.setText(user.getFullName());
    if(user.getEmail() != null) txtEmail.setText(user.getEmail());
    cboPermission.select(user.getPermission()-1);
    
    lstGroups.removeAll();
    if(user.getGroups() == null || user.getGroups().size() < 1) return;
    for(String element : user.getGroups()) {
      if(element == null || element.trim().isEmpty()) continue;
      lstGroups.add(element.trim());
    }
  }

  private void save() {
    final User user = new User();
    user.setUserName(txtUsername.getText().trim());
    if(user.getUserName().isEmpty()) {
      MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.OK);
      ClientRM resource = new ClientRM("Organization");
      msg.setMessage(resource.getLabel(Organization.class.getName()+".msgEmptyUsername") );
      msg.open();
      return;
    }
    
    String password = txtPassword.getText();
    String confirmPassword = txtConfirmPassword.getText();
    if(!password.equals(confirmPassword)) {
      MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.OK);
      ClientRM resource = new ClientRM("Organization");
      msg.setMessage(resource.getLabel(Organization.class.getName()+".msgWrongPassword") );
      msg.open();
      return;
    }
    
    user.setEmail(txtEmail.getText());
    user.setFullName(txtFullname.getText());
    user.setPassword(password);
    Set<String> groups = new HashSet<String>();
    Collections.addAll(groups, lstGroups.getItems());
    String selectedGroup  = groupUI.getSelectedGroup();
    if(selectedGroup != null) groups.add(selectedGroup);
    
    ArrayList<String> listGroup = new ArrayList<String>();
    listGroup.addAll(groups);
    user.setGroups(listGroup);
    
    String [] groupUsers = groupUI.getListUsers().getItems();
    boolean add = true;
    for(int k = 0; k < groupUsers.length; k++) {
      if(groupUsers[k].equals(user.getUserName())) {
        add = false;
        break;
      }
    }
    if(add) groupUI.getListUsers().add(user.getUserName());
    
    user.setPermission(cboPermission.getSelectionIndex()+1);

    Worker excutor = new Worker() {

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {        
          new OrganizationClientHandler().save(user);
        }catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
       groupUI.save(false);
      }
    };
    new ThreadExecutor(excutor, txtUsername).start();
  }


}
