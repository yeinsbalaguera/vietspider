/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.gui.config.group;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.vietspider.model.Group;
import org.vietspider.model.Region;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 3, 2007  
 */
public class GroupConfig extends Composite implements ChangeGroup{
  
  private GroupsConfig groupsConfig;
  private Combo cboDownloadType;
  
  private Text txtRegion;
  
  private List listRegionName;
  private List listRegionType;
  
  private Button butDownloadImage;
  private Button butForceDownloadImage;
  private Button butSetImageToMeta;
//  private Button butAutoDetectImage;
  private Button butCheckTitle;
  
  private Spinner spiMinPercentRelation;
  private Spinner spiDateRangeRelation;
  private Spinner spiStartDownloadTime;
  private Spinner spiEndDownloadTime;
  
  private Spinner spiMinPriority;
  private Spinner spiMaxPriority;
  
  public GroupConfig(Composite parent, ApplicationFactory factory) {
    super(parent, SWT.NONE);
    
    setLayout(new GridLayout(1, false));
    
    GridData gridData;
    
    gridData = new GridData();
    GridLayout gridLayout = new GridLayout(1, false);
    org.eclipse.swt.widgets.Group group ;
    factory.setComposite(this);
    group = factory.createGroup("grpGroup", gridData, gridLayout);
    factory.setComposite(group);
    
    groupsConfig = new GroupsConfig(group, this, factory);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    groupsConfig.setLayoutData(gridData);
    
    Composite regionComposite = new Composite(group, SWT.NONE);
    regionComposite.setLayout(new GridLayout(3, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    regionComposite.setLayoutData(gridData);
    factory.setComposite(regionComposite);
    
    Composite regionCompositeInput = new Composite(regionComposite, SWT.NONE);
    regionCompositeInput.setLayout(new GridLayout(3, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan  = 3;
    regionCompositeInput.setLayoutData(gridData);
    factory.setComposite(regionCompositeInput);
    
    factory.createLabel("lblRegion");
    txtRegion = factory.createText(SWT.BORDER);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtRegion.setLayoutData(gridData);
    txtRegion.addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent event) {
        if(event.keyCode != SWT.CR) return;
        createRegion();
      }
    });
    
    gridData = new GridData();
    gridData.heightHint = 26;
    factory.createIcon("butAddRegionName", new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent evt) {
        createRegion();
      }

    }).setLayoutData(gridData);
    
    listRegionName = factory.createList(regionComposite, SWT.BORDER | SWT.V_SCROLL);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    gridData.heightHint = 70;
    listRegionName.setLayoutData(gridData);
    listRegionName.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int index  = listRegionName.getSelectionIndex();
        if(index < 0) return;
        String name = listRegionName.getItem(index);
        Region region = groupsConfig.getRegion(name);
        listRegionType.select(region.getType());
      }
    });
    
    Menu menu = new Menu(getShell(), SWT.POP_UP);
    factory.createMenuItem(menu, "menuRemoveRegion", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String [] names = listRegionName.getSelection();
        for(String name : names) {
          listRegionName.remove(name);
          groupsConfig.removeRegion(name);
        }
      }
    });
    
    factory.createMenuItem(menu, "menuRemoveAllRegion", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        listRegionName.removeAll();
        groupsConfig.removeAllRegion();
      }
    });
    listRegionName.setMenu(menu);
    
    listRegionType = factory.createList(regionComposite, SWT.BORDER | SWT.V_SCROLL);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.heightHint = 70;
    listRegionType.setLayoutData(gridData);
    listRegionType.setItems(new String[]{"DEFAULT", "TEXT", "CDATA", "FILE"});
    listRegionType.select(0);
    listRegionType.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int index  = listRegionName.getSelectionIndex();
        if(index < 0) return;
        String name = listRegionName.getItem(index);
        int type  = listRegionType.getSelectionIndex();
        groupsConfig.setRegion(name, type);
      }
    });
    
    
    Composite percentComposite = new Composite(group, SWT.NONE);
    percentComposite.setLayout(new GridLayout(3, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    percentComposite.setLayoutData(gridData);
    factory.setComposite(percentComposite);
    
    factory.createLabel("lblMinPercentRelation"); 
    spiMinPercentRelation = factory.createSpinner("lblMinPercentRelation", SWT.BORDER);
    spiMinPercentRelation.setMinimum(0);
    spiMinPercentRelation.setIncrement(5);
    spiMinPercentRelation.setMaximum(95);
    spiMinPercentRelation.addModifyListener(new ModifyListener() {
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent event) {
        int min  = spiMinPercentRelation.getSelection();
        if(min < 1) {
          spiDateRangeRelation.setEnabled(false);
        } else {
          spiDateRangeRelation.setEnabled(true);
        }
      }
    });
    factory.createLabel("percentRelation"); 
    
    Composite dateRangeComposite = new Composite(group, SWT.NONE);
    dateRangeComposite.setLayout(new GridLayout(3, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    dateRangeComposite.setLayoutData(gridData);
    factory.setComposite(dateRangeComposite);
    
    factory.createLabel("lblDateRangeRelation"); 
    spiDateRangeRelation = factory.createSpinner("lblDateRangeRelation", SWT.BORDER);
    spiDateRangeRelation.setMinimum(1);
    spiDateRangeRelation.setIncrement(1);
    spiDateRangeRelation.setMaximum(15);
    spiDateRangeRelation.setSelection(3);
    factory.createLabel("calendar_d"); 
    
    Composite timeComposite = new Composite(group, SWT.NONE);
    timeComposite.setLayout(new GridLayout(6, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    timeComposite.setLayoutData(gridData);
    factory.setComposite(timeComposite);
    
    cboDownloadType = factory.createCombo(SWT.BORDER |SWT.READ_ONLY);
    String value = factory.getLabel("downloadType");
    cboDownloadType.setItems(value.split(","));
    cboDownloadType.select(0);
    
    factory.createLabel("lblStartDownloadTime"); 
    
    spiStartDownloadTime = factory.createSpinner("lblStartDownloadTime", SWT.BORDER);
    spiStartDownloadTime.setMinimum(0);
    spiStartDownloadTime.setIncrement(1);
    spiStartDownloadTime.setMaximum(22);
    spiStartDownloadTime.setSelection(0);
    factory.createLabel("calendar_to");
    
    spiEndDownloadTime = factory.createSpinner("lblEndDownloadTime", SWT.BORDER);
    spiEndDownloadTime.setMinimum(1);
    spiEndDownloadTime.setIncrement(1);
    spiEndDownloadTime.setMaximum(23);
    spiEndDownloadTime.setSelection(23);
    factory.createLabel("calendar_h");
    
    Composite priorityComposite = new Composite(group, SWT.NONE);
    priorityComposite.setLayout(new GridLayout(4, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    priorityComposite.setLayoutData(gridData);
    factory.setComposite(priorityComposite);
    
    factory.createLabel("lblMinPriority"); 
    spiMinPriority = factory.createSpinner("lblMinPriority", SWT.BORDER);
    spiMinPriority.setMinimum(-10);
    spiMinPriority.setMaximum(100);
    
    factory.createLabel("lblMaxPriority"); 
    spiMaxPriority = factory.createSpinner("lblMaxPriority", SWT.BORDER);
    spiMaxPriority.setMinimum(0);
    spiMaxPriority.setMaximum(1000);
    
    Composite imageComposite = new Composite(group, SWT.NONE);
    imageComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    imageComposite.setLayoutData(gridData);
    factory.setComposite(imageComposite);
    
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    
    butCheckTitle = factory.createButton("butCheckTitle", SWT.CHECK, null);  
    butCheckTitle.setLayoutData(gridData);
    
    butDownloadImage = factory.createButton("butDownloadImage", SWT.CHECK, null);  
    butDownloadImage.setLayoutData(gridData);
    butDownloadImage.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent event) {
        if (butDownloadImage.getSelection()) {
          butForceDownloadImage.setEnabled(true);
        } else {
          butForceDownloadImage.setEnabled(false);
        }
      }
    });
    butForceDownloadImage = factory.createButton("butForceDownloadImage", SWT.CHECK, null);
    butForceDownloadImage.setLayoutData(gridData);
    butForceDownloadImage.setEnabled(butDownloadImage.getSelection());
    
    butSetImageToMeta = factory.createButton("butSetImageToMeta", SWT.CHECK, null);  
    butSetImageToMeta.setLayoutData(gridData);
    
   /* butSetImageToMeta.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent event) {
        if (butSetImageToMeta.getSelection()) {
          butAutoDetectImage.setEnabled(true);
        } else {
          butAutoDetectImage.setEnabled(false);
        }
      }
    });
    butAutoDetectImage = factory.createButton("butAutDetectImage", SWT.CHECK, null);  
    butAutoDetectImage.setLayoutData(gridData);*/
    
    try {
      loadGroupConfig();
    } catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
    }
    
  }
  
  public void loadGroupConfig() { groupsConfig.loadGroupConfig(); }
  
  public void saveGroupConfig() { groupsConfig.saveGroupConfig(); }
  
  public void setGroup(Group group) {
    reset();
    if(group == null) return ;
    for(Region region : group.getProcessRegions()) {
      listRegionName.add(region.getName());
    }
    if(listRegionName.getSelectionCount() > 0) listRegionName.select(0);
    
    butCheckTitle.setSelection(group.isCheckTitle());
    butDownloadImage.setSelection(group.isDownloadImage());
    butForceDownloadImage.setSelection(group.isForceDownloadImage());
    butForceDownloadImage.setEnabled(butDownloadImage.getSelection());
    butSetImageToMeta.setSelection(group.isSetImageToMeta());
//    butAutoDetectImage.setSelection(group.isAutoDetectImage());
    spiMinPercentRelation.setSelection(group.getMinPercentRelation());
    spiDateRangeRelation.setSelection(group.getDateRangeRelation());
    spiStartDownloadTime.setSelection(group.getStartTime());
    spiEndDownloadTime.setSelection(group.getEndTime());
    spiMinPriority.setSelection(group.getMinPriority());
    spiMaxPriority.setSelection(group.getMaxPriority());
    cboDownloadType.select(group.isDownloadInRangeTime() ? 0 : 1);
  }
  
  public void setDataToGroup(Group group) {
    if(group == null) return ;
    group.setCheckTitle(butCheckTitle.getSelection());
    group.setDownloadImage(butDownloadImage.getSelection());
    group.setForceDownloadImage(butForceDownloadImage.getSelection());
    group.setSetImageToMeta(butSetImageToMeta.getSelection());
//    group.setAutoDetectImage(butAutoDetectImage.getSelection());
    group.setMinPercentRelation(spiMinPercentRelation.getSelection());
    group.setDateRangeRelation(spiDateRangeRelation.getSelection());
    group.setMinPriority(spiMinPriority.getSelection());
    group.setMaxPriority(spiMaxPriority.getSelection());
    int start = spiStartDownloadTime.getSelection();
    int end = spiEndDownloadTime.getSelection();
    if(start == end) {
      end += 1; 
    } else if(start > end) {
      int tmp = start;
      start = end;
      end = tmp;
    }
    group.setStartTime(start);
    group.setEndTime(end);
    group.setDownloadInRangeTime(cboDownloadType.getSelectionIndex() == 0);
  }
  
  void reset() {
    txtRegion.setText("");
    listRegionName.removeAll();
    butCheckTitle.setSelection(true);
    butDownloadImage.setSelection(false);
    butForceDownloadImage.setSelection(false);
    butForceDownloadImage.setEnabled(butDownloadImage.getSelection());
    butSetImageToMeta.setSelection(true);
//    butAutoDetectImage.setSelection(true);
    spiMinPercentRelation.setSelection(0);
    spiDateRangeRelation.setSelection(3);
    spiStartDownloadTime.setSelection(0);
    spiEndDownloadTime.setSelection(23);
    spiMinPriority.setSelection(-1);
    spiMaxPriority.setSelection(720);
    cboDownloadType.select(0);
  }
  
  private void createRegion() {
    String name = txtRegion.getText();
    if(name == null || (name = name.trim()).isEmpty()) return;
    for(String ele : listRegionName.getItems()) {
      if(ele.equalsIgnoreCase(name)) return ;
    }
    listRegionName.add(name);
    groupsConfig.setRegion(name, Region.DEFAULT);
  }
}
