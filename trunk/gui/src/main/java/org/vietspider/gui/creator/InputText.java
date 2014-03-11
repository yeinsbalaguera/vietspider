/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 28, 2008  
 */
public class InputText extends AlphaShell {
  
  protected Text txtField;
  
  protected java.util.List<String> items = new ArrayList<String>();
  
  protected Text txtDialog;
  
  protected Button butDelete, butOk;
  
//  protected ViewerTextInput viewerText;
  
  public InputText(SourceEditorUI sourceInfo_, String id) {
    super(sourceInfo_, id);
    
    txtDialog = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    txtDialog.setLayoutData(gridData);
    txtDialog.addFocusListener(new FocusAdapter() {
      @SuppressWarnings("unused")
      public void focusGained(FocusEvent event) {
        if(auto) shell.setAlpha(-1);
      }

      @SuppressWarnings("unused")
      public void focusLost(FocusEvent evt) {
        if(auto) shell.setAlpha(200); 
        setItems(txtDialog.getText().trim().split("\n"));
      }
    });
    
    ClientRM resources = new ClientRM("Creator");
    
    Composite bottom = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    gridData.widthHint = 250;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;
    
    butDelete  = new Button(bottom, SWT.BORDER);
    butDelete.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        txtDialog.setText("");
      }      
    });
    butDelete.setText(resources.getLabel("clear")); 
    
    butOk  = new Button(bottom, SWT.BORDER);
    butOk.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        saveShellProperties();
        if(!auto) shell.setVisible(false);
      }      
    });
    butOk.setText(resources.getLabel("ok"));
  }
  
  protected void showDialog(int x, int y, int x0, int y0, int w, int h, boolean a) {
    super.setShellProperties(x, y, x0, y0, w, h, a);
    butOk.setEnabled(!auto);
    setWidgetValue();
  }
  
  protected Menu createCommonMenu(ApplicationFactory factory) {
    Menu menu = new Menu(txtField.getShell(), SWT.POP_UP);
    factory.createMenuItem(menu, "menuRemove", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeAll();
      }
    });
    
    
    factory.createMenuItem(menu, SWT.SEPARATOR);
    
    factory.createMenuItem(menu, "menuCut", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        txtField.copy();
        txtField.cut(); 
      }
    });
    
    factory.createMenuItem(menu, "menuCopy", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        txtField.copy();
      }
    });
    
    MenuItem menuItem = factory.createMenuItem(menu, "menuPaste");
    menuItem.addListener(SWT.Selection, new Listener() {
      @SuppressWarnings("unused")
      public void handleEvent (Event e) {
        Clipboard clipboard = new Clipboard(txtField.getDisplay());
        TextTransfer transfer = TextTransfer.getInstance();
        String text = (String)clipboard.getContents(transfer);
        if(text != null && !text.trim().isEmpty()) addItems(text.split("\n"));
        txtField.paste();
      }
    });
    menuItem.setAccelerator(SWT.MOD1 + 'v');
    
    factory.createMenuItem(menu, "menuClear", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        txtField.setText("");
      }
    });
    
    return menu;
  }
  
  public void removeAll() {
    items.clear();
    txtField.setText("");
    txtField.setForeground(new Color(txtField.getDisplay(), 0, 0, 0));
  }
  
  public void addItems(String [] elements) {
    for(String element : elements) {
      if(element.trim().isEmpty() || items.contains(element))  continue;
      items.add(element.trim());
    }
    setWidgetValue();
  }
  
  public String [] getItems() {
    String value = txtField.getText().trim();
    boolean add = true;
    for(String ele : items) {
      if(ele.trim().equals(value)) {
        add = false;
      }
    }
    if(add) items.add(value);
    return items.toArray(new String[items.size()]);
  }
  
  public String getTxtDialog() {
    if(txtField.getText().trim().isEmpty() && items.size() > 0) return items.get(0);
    return txtField.getText();
  }
  
  public void setItems(String [] elements) {
    items.clear();
    for(String element : elements) {
      if(element == null) continue;
      element = element.trim();
      if(element.isEmpty() || items.contains(element))  continue;
      items.add(element);
    }
    if(items.size() > 0) {
      txtField.setText(items.get(0).trim());
    } else {
      txtField.setText("");
    }
    setWidgetValue();
  }
  
  private void setWidgetValue() {
//    viewerText  = new ViewerTextInput(txtField, items.toArray(new String[items.size()]));
//    new AutocompleteTextInput(txtField, items.toArray(new String[items.size()]));
    if(txtDialog == null || txtDialog.isDisposed()) return;
    StringBuilder builder  = new StringBuilder();
    for(String element : items) {
      if(builder.length() > 0) builder.append('\n');
      builder.append(element);
    }
    txtDialog.setText(builder.toString());
  }
  
  protected boolean isFocusControl() {
    return txtDialog.isFocusControl() 
        || txtField.isFocusControl()
        || shell.isFocusControl() 
        || butDelete.isFocusControl()
        || butOk.isFocusControl();
  }
  
}
