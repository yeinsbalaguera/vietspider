/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.gui.config.group;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.model.Group;
import org.vietspider.model.Groups;
import org.vietspider.model.Region;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 3, 2007  
 */
public class GroupsConfig extends Composite {
  
  private Combo cboGroup;
  
  private Groups groups;
  
  private ChangeGroup changeGroup;
  
  public GroupsConfig(Composite parent, ChangeGroup cg, ApplicationFactory factory) {
    super(parent, SWT.NONE);
    
    this.changeGroup = cg;
    
    setLayout(new GridLayout(3, false));
    factory.setComposite(this);
    
    factory.createLabel("lblGroup");
    cboGroup = factory.createCombo(SWT.BORDER);
    cboGroup.addFocusListener(new FocusAdapter(){
      @SuppressWarnings("unused")
      public void focusGained(FocusEvent arg0) {
        changeGroup.setDataToGroup(getSelectedGroup());
      }
    });
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
    factory.createMenuItem(menu, "menuRemove", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeGroup();
      }
    });
    cboGroup.setMenu(menu);
    
    factory.createIcon("butAddRegionName", new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent evt) {
        addGroup();
      }
    });
  }
  
  private void addGroup() {
    String text =  cboGroup.getText();
    if(text == null || (text = text.trim()).isEmpty()) return;
    createGroup(text.toUpperCase());
  }
  
  void createGroup(String text) {  
    for(Group group : groups.getGroups()) {
      if(group.getType().equalsIgnoreCase(text)) return;
    }
    
    Group [] newGroups = new Group[groups.getGroups().length+1];
    System.arraycopy(groups.getGroups(), 0, newGroups, 0, newGroups.length-1);
    
    Group  newGroup = new Group();
    newGroup.setType(text);
    newGroups[newGroups.length-1] = newGroup;
    
    groups.setGroups(newGroups);
    cboGroup.add(text);
    cboGroup.select(cboGroup.getItemCount() - 1);
    selectGroup();
  }
  
  private void removeGroup() {
    if(cboGroup.getItemCount() < 2) return;
    
    int idx = cboGroup.getSelectionIndex();
    String type = cboGroup.getItem(idx);

    MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
    ClientRM resource = new ClientRM("Config");
    String mes = resource.getLabel(getClass().getName()+".msgAlertDeleteGroup")+" \""+type +"\"?";
    msg.setMessage(mes);
    if(msg.open() != SWT.YES) return ; 

    Group group = groups.getGroup(type);
    if(group == null) return ;
    Group [] newGroups = new Group[groups.getGroups().length-1];
    idx = 0;
    for(int i = 0; i < groups.getGroups().length; i++) {
      if(groups.getGroups()[i] == group) continue;
      newGroups[idx] = groups.getGroups()[i];
      idx++;
    }
    
    groups.setGroups(newGroups);
    cboGroup.remove(type);
    idx = idx - 1;
    if(idx < 0) idx = 0;
    cboGroup.select(0);
    selectGroup();
  }
  
  private void selectGroup() {
    int idx = cboGroup.getSelectionIndex();
    if(idx < 0) return;
    String type = cboGroup.getItem(idx);
    if(type == null) return;
    Group group = groups.getGroup(type);
    if(group == null) return ;
    changeGroup.setGroup(group);
  }
  
  public Group getSelectedGroup() {
    int idx = cboGroup.getSelectionIndex();
    if(idx < 0) idx = 0;
    String type = cboGroup.getItem(idx);
    return groups.getGroup(type);
  }
  
  void loadGroupConfig() {
    Worker excutor = new Worker() {

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        cboGroup.removeAll();
      }

      public void execute() {
        try {
          groups = new SimpleSourceClientHandler().loadGroups();
        }catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        if(groups == null) return;
        for(Group group : groups.getGroups()) {
          if(group.getType().equals(Group.DUSTBIN)) continue;
          cboGroup.add(group.getType());
        }
        if(cboGroup.getItemCount() > 0) cboGroup.select(0);
        selectGroup();
      }
    };
    WaitLoading loading = new WaitLoading(cboGroup, excutor);
    loading.open();
  }
  
  public Region getRegion(String name) {
    Group group = getSelectedGroup();
    List<Region> regions = group.getProcessRegions();
    for(int i = 0; i < regions.size(); i++) {
      if(regions.get(i).getName().equals(name)) return  regions.get(i);
    }
    
    Region region = new Region(name);
    region.setType(Region.DEFAULT);
    getSelectedGroup().getProcessRegions().add(region);
    return region;
  }
  
  public Region setRegion(String name, int type) {
    Group group = getSelectedGroup();
    List<Region> regions = group.getProcessRegions();
    for(int i = 0; i < regions.size(); i++) {
      Region region = regions.get(i);
      if(region.getName().equals(name)) {
        region.setType(type);
        return region;
      }
    }
    
    Region region = new Region(name);
    region.setType(type);
    getSelectedGroup().getProcessRegions().add(region);
    return region;
  }
  
  public void removeRegion(String name) {
    Group group = getSelectedGroup();
    List<Region> regions = group.getProcessRegions();
    for(int i = 0; i < regions.size(); i++) {
      Region region = regions.get(i);
      if(region.getName().equals(name)) {
        regions.remove(i);
        return;
      }
    }
  }
  
  public void removeAllRegion() {
    getSelectedGroup().getProcessRegions().clear();
  }
  
  public void saveGroupConfig() {
    Worker excutor = new Worker() {

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        createGroup("DUSTBIN");
        changeGroup.setDataToGroup(getSelectedGroup());
      }

      public void execute() {
        try {
          new SimpleSourceClientHandler().saveGroups(groups);
        }catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
      }
    };
    WaitLoading loading = new WaitLoading(cboGroup, excutor);
    loading.open();
  }
}
