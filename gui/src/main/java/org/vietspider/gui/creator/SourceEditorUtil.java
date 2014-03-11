/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.vietspider.ui.widget.ApplicationFactory;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 20, 2008  
 */
public class SourceEditorUtil {
  
  private Composite ui;
  
  public SourceEditorUtil(Composite composite) {
    this.ui = composite;
  }
  
  public  Combo createCombo(ApplicationFactory factory) {
    Combo cbo = factory.createCombo(SWT.READ_ONLY, new String[]{});
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);  
    cbo.setLayoutData(gridData);
    return cbo;
  }

  public Composite createComposite(ApplicationFactory factory, Composite parent, int columns) {
    GridLayout gridLayout = new GridLayout(columns, false);
    gridLayout.marginHeight = 5;
    gridLayout.horizontalSpacing = 5;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(gridLayout);
    factory.setComposite(composite);
    composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    return composite;
  }
  
  public Composite createComposite2(ApplicationFactory factory, Composite parent, int columns) {
    GridLayout gridLayout = new GridLayout(columns, false);
    gridLayout.marginHeight = 5;
    gridLayout.horizontalSpacing = 5;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(gridLayout);
    factory.setComposite(composite);
    return composite;
  }
  
  void createComposite(ApplicationFactory factory, int columns, GridData gridData) {
    GridLayout gridLayout = new GridLayout(columns, false);
    gridLayout.marginHeight = 5;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 5;
    gridLayout.marginWidth = 2;
    
    Composite composite  = new Composite(ui, SWT.NONE);
    composite.setLayout(gridLayout);
    factory.setComposite(composite);
    if(gridData != null) composite.setLayoutData(gridData);
  }
  
  public void paste(Combo combo) {
    Clipboard clipboard = new Clipboard(ui.getDisplay());
    TextTransfer transfer = TextTransfer.getInstance();
    String text = (String)clipboard.getContents(transfer);
    if(text == null || text.trim().length() < 1) return;
    int index = text.indexOf("VietSpiderSourceCopy\n");
    if(index != 0) return;
    text = text.substring(index + "VietSpiderSourceCopy\n".length());
    String [] lines = text.split("\n");
    for(String line : lines) {
      line = line.trim();
      if(line.length() < 1) continue;
      combo.add(line);
    }
    if(combo.getItemCount() > 0) combo.select(0);
  }
  
  public static void setDropTarget (final Text text) {
    int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
    DropTarget target = new DropTarget(text, operations);
    target.setTransfer(new Transfer[] {URLTransfer.getInstance()});
    target.addDropListener (new DropTargetAdapter() {
      
      public void dragEnter(DropTargetEvent e) {
        if (e.detail == DND.DROP_NONE)
          e.detail = DND.DROP_LINK;
      }
      public void dragOperationChanged(DropTargetEvent e) {
        if (e.detail == DND.DROP_NONE) e.detail = DND.DROP_LINK;
      }
      
      public void drop(DropTargetEvent event) {
        if (event.data == null) {
          event.detail = DND.DROP_NONE;
          return;
        }
        text.setText(((String) event.data));
      }
    });
  }
  
  public static void setDropTarget (final List list) {
    int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
    DropTarget target = new DropTarget(list, operations);
    target.setTransfer(new Transfer[] {URLTransfer.getInstance()});
    target.addDropListener (new DropTargetAdapter() {
      
      public void dragEnter(DropTargetEvent e) {
        if (e.detail == DND.DROP_NONE)
          e.detail = DND.DROP_LINK;
      }
      public void dragOperationChanged(DropTargetEvent e) {
        if (e.detail == DND.DROP_NONE) e.detail = DND.DROP_LINK;
      }
      
      public void drop(DropTargetEvent event) {
        if (event.data == null) {
          event.detail = DND.DROP_NONE;
          return;
        }
        list.add(((String) event.data));
      }
    });
  }
  
  public void copy(Combo combo) {
    StringBuilder builder = new StringBuilder("VietSpiderSourceCopy");
    for(int i = 0; i < combo.getItemCount(); i++) {
      builder.append("\n").append(combo.getItem(i));
    }
    Clipboard clipboard = new Clipboard(ui.getDisplay());
    TextTransfer textTransfer = TextTransfer.getInstance();
    clipboard.setContents(new Object[]{builder.toString()}, new Transfer[]{textTransfer});
  }
  
  public Group createGroup(ApplicationFactory factory, String label, int columns) {
    GridLayout gridLayout = new GridLayout(columns, false);
    gridLayout.marginHeight = 2;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 2;
    
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);     
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalSpan = 3;    
    Group uiGroup = factory.createGroup(label, gridData, gridLayout);

    factory.setComposite(uiGroup);
    return uiGroup;
  }
  
  public Menu initMenu(ApplicationFactory factory, final Combo cbo) {
    Menu menu = new Menu(ui.getShell(), SWT.POP_UP);
    factory.createMenuItem(menu, "menuRemoveSelected", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int idx = cbo.getSelectionIndex();
        if(idx < 0) return;
        cbo.remove(idx);
      }
    });
    
    factory.createMenuItem(menu, "menuRemove", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        cbo.removeAll();
      }
    });
    
    new MenuItem(menu, SWT.SEPARATOR);
    
    factory.createMenuItem(menu, "menuCopy", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        copy(cbo);        
      }
    });
    
    factory.createMenuItem(menu, "menuPaste", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        paste(cbo);
      }
    });
    cbo.setMenu(menu);
    
    return menu;
  }
}
