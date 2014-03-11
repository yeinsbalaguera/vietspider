/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.vietspider.model.Group;
import org.vietspider.model.Region;
import org.vietspider.ui.widget.ImageHyperlink;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 2, 2006
 */
abstract class SourceEditorExtractUI extends Composite implements ISourceInfo {
  
  protected Image imgNewBlock;
  protected Image imgEditBlock; 
  
  protected String tipHasData;
  protected String tipVisitRegion;
  protected String tipExtractRegion;
  protected String tipRemoveRegion;
  protected String tipDataRegion;
  
  protected ImageHyperlink iconVisitRegion;
  protected ImageHyperlink iconExtractRegion;
  protected ImageHyperlink iconRemoveRegion;
  protected ImageHyperlink iconDataRegion;  
  
  private List<String> visitRegions = new ArrayList<String>();
  private List<String> extractRegions = new ArrayList<String>();
  private List<String> removeRegions = new ArrayList<String>();
  
  private List<Region> dataRegions = new ArrayList<Region>(); 
  
  public SourceEditorExtractUI(Composite parent) {
    super(parent, SWT.NONE);
  } 
  
  public void setVisitRegions(String [] paths) {
    setRegions(paths, visitRegions, iconVisitRegion, tipVisitRegion);
  }

  public String[] getVisitRegions() { 
    return visitRegions.toArray(new String[visitRegions.size()]); 
  }
  
  public void setExtractRegions(String [] paths) {
    setRegions(paths, extractRegions, iconExtractRegion, tipExtractRegion);
  }
  public String[] getExtractRegions() { 
    return extractRegions.toArray(new String[extractRegions.size()]); 
  }
  
  public void setRemoveRegions(String [] paths) {
    setRegions(paths, removeRegions, iconRemoveRegion, tipRemoveRegion);
  }
  public String[] getRemoveRegions() { 
    return removeRegions.toArray(new String[removeRegions.size()]); 
  }
  
  private void setRegions(String [] paths, List<String> regions, ImageHyperlink icon, String tip) {
    regions.clear();
    if(paths == null || paths.length < 1) {
      icon.setImage(imgNewBlock);
      icon.setToolTipText(tip);
      icon.redraw();
      return;
    }

    Collections.addAll(regions, paths);
    icon.setImage(imgEditBlock);
    icon.setToolTipText(tip + " (" + tipHasData +")");
    icon.redraw();
  }
  
  public void setDataRegions(List<Region> regions) {
    this.dataRegions = regions;
    
    boolean hasData = false;
    
    if(dataRegions != null && dataRegions.size() > 0) {
      for(int i = 0; i < dataRegions.size(); i++) {
        if(dataRegions.get(i).getPaths() != null
            && dataRegions.get(i).getPaths().length > 0)
        {
          hasData = true;
          break;
        }
      }
    }
    
    if(!hasData) {
      iconDataRegion.setImage(imgNewBlock);
      iconDataRegion.setToolTipText(tipDataRegion);
      iconDataRegion.redraw();
      return;
    }

    iconDataRegion.setImage(imgEditBlock);
    iconDataRegion.setToolTipText(tipDataRegion + " (" + tipHasData +")");
    iconDataRegion.redraw();
  }
  
  public List<Region> getDataRegions() {
    if(dataRegions == null || dataRegions.size() < 1) {
      createDefaultDataRegions();
    }
    return dataRegions;  
  }
  
  protected List<Region> createDefaultDataRegions() {
    dataRegions = new ArrayList<Region>();
    Group group = getGroupInstance(); 
    List<Region> regions = group.getProcessRegions();
    for(int i = 0; i < regions.size(); i++) {
      if(regions.get(i) == null) continue;
      dataRegions.add(regions.get(i).clone());
    }
    return dataRegions;
  }
  
  abstract Group getGroupInstance() ;
  

}