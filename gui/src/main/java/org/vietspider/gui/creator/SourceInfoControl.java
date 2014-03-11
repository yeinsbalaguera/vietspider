/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.vietspider.model.Group;
import org.vietspider.model.Source;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.LiveSashForm;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 20, 2008  
 */
public class SourceInfoControl extends Composite {
  
  private LiveSashForm sash;
  private SourceEditor sourceEditor;

  private ScrolledComposite scrolledAdvanced;
//  private ScrolledComposite scrolledSimple;
  
//  private Button button;

  public SourceInfoControl(Composite parent, Creator creator, ApplicationFactory factory) {
    super(parent, SWT.NONE);
    
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);
    
    sash = new LiveSashForm(this, SWT.VERTICAL);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    sash.setLayoutData(gridData);
    
    scrolledAdvanced = new ScrolledComposite(sash, SWT.BORDER | SWT.V_SCROLL);
    scrolledAdvanced.setExpandHorizontal(true);
    sourceEditor = new SourceEditor(scrolledAdvanced, factory);
    sourceEditor.setSize(700, 400);
    sourceEditor.setCreator(creator);
    scrolledAdvanced.setContent(sourceEditor);
    sourceEditor.loadExpandedProperties();
    
    sash.setMaximizedControl(scrolledAdvanced );    
  }
  
  public void setGroup(Group group) {
    sourceEditor.setGroup(group);
  }
  
  public void setSource(Source source, int version, boolean focus) {
    sourceEditor.setSource(source, version);
    if(!focus) return;
    sourceEditor.setFocus();
  }
  
  public void save() { sourceEditor.saveSource(); }
  
  public void reset() { sourceEditor.reset(); }
  
  public void test() { sourceEditor.test(); }
  
  public SourceEditor getSourceEditor() { return sourceEditor; }
  
  public void setEditMode(boolean value) {
    if(!value) sourceEditor.setSize(700, 370);
    sourceEditor.setEditMode(value);
  }
}