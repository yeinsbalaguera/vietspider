/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.sync;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 5, 2008  
 */
public class SyncScriptInput extends Composite  {
  
  private Combo cboScripts;
  private Text txtInput;
  private List<String> scripts;
  private int selected  = -1;
  
  public SyncScriptInput(Composite parent, ApplicationFactory factory) {
    super(parent, SWT.NONE);
    
    setLayout(new GridLayout(2, false));
    factory.setComposite(this);
    
    scripts = new ArrayList<String>();
    
    txtInput = factory.createText(SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    txtInput.setFont(UIDATA.FONT_10);
    txtInput.setLayoutData(gridData);
    
    
    Composite rightComposite = new Composite(this, SWT.NONE);
    gridData = new GridData(GridData.FILL_VERTICAL);
    rightComposite.setLayoutData(gridData);
    rightComposite.setLayout(new GridLayout(1, false));
    factory.setComposite(rightComposite);
    
    Composite dbsComposite = new Composite(rightComposite, SWT.NONE);
    dbsComposite.setLayout(new GridLayout(1, false));
    factory.setComposite(dbsComposite);
    factory.createLabel("lblScripts");
    cboScripts = factory.createCombo(SWT.BORDER | SWT.READ_ONLY);
    cboScripts.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        selected = cboScripts.getSelectionIndex();
        if(selected < 0) return;
        txtInput.setText(scripts.get(selected));
      }
    });
    
    Composite bottom1 = new Composite(rightComposite, SWT.NONE);
    gridData = new GridData(GridData.FILL_VERTICAL);
    bottom1.setLayoutData(gridData);
    
    Composite bottom = new Composite(rightComposite, SWT.NONE);
    gridData = new GridData(GridData.VERTICAL_ALIGN_END);
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    rowLayout.justify = false;
    rowLayout.type = SWT.VERTICAL;
    bottom.setLayout(rowLayout);
    factory.setComposite(bottom);
    
    factory.createButton("butAddScript", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String value = txtInput.getText();
        value  = value.trim();
        for(String ele : scripts) {
          if(ele.equalsIgnoreCase(value)) return;
        }
        if(selected > -1) {
          if(value.isEmpty()) {
            scripts.remove(selected);
            cboScripts.remove(selected);
          } else {
            scripts.set(selected, value);
          }
        } else {
          if(value.isEmpty()) return;
          scripts.add(value);
          cboScripts.add("Script " + String.valueOf(scripts.size()));
          cboScripts.select(scripts.size() - 1);
          selected = scripts.size() - 1;
        }
      }   
    });
    
    factory.createButton("butResetScriptInput", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        txtInput.setText("");
        cboScripts.clearSelection();
        selected = -1;
      }   
    });
  }

  public List<String> getScripts() { return scripts; }
  
  public void setScripts(List<String> list) {
    this.scripts = list;
    if (scripts.size() < 1) return;
    for(int i = 0; i < scripts.size(); i++) {
      cboScripts.add("Script " + String.valueOf(i + 1));
    }
    cboScripts.select(scripts.size() - 1);
    selected = scripts.size() - 1;
    txtInput.setText(scripts.get(scripts.size() - 1));
  }

}
