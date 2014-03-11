/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.vietspider.ui.htmlexplorer.PathBox.ChangePath;
import org.vietspider.ui.htmlexplorer.PathBox.CurrentPath;
import org.vietspider.ui.htmlexplorer.PathBox.PathEvent;
import org.vietspider.ui.widget.PathHightLight;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 12, 2011  
 */
public class PathItemEditor  extends StyledText {
  
  private final static short POPUP_INDEX = 0;
  private final static short POPUP_ATTR = 1;
  
  List<ChangePath> changes = new ArrayList<ChangePath>();
  List<CurrentPath> currents = new ArrayList<CurrentPath>();
  
  PathEvent carretEvent = null;
  
  private Shell attrPopup;
  private Table attrTable;
  private short popupType = POPUP_ATTR;
  
  public PathItemEditor(Composite parent) {
    super(parent, SWT.SINGLE);
    
    addDisposeListener(new DisposeListener() {
      @SuppressWarnings("unused")
      public void widgetDisposed(DisposeEvent evt) {
        if(attrPopup != null 
            && !attrPopup.isDisposed()) attrPopup.dispose();
      }
    });
    
    getShell().addFocusListener(new FocusAdapter() {
      @SuppressWarnings("unused")
      public void focusLost(FocusEvent e) {
        if(attrPopup != null 
            && !attrPopup.isDisposed()) attrPopup.setVisible(false);
      }
    });
    
    
    addListener(SWT.MenuDetect, new Listener(){
      @SuppressWarnings("unused")
      public void handleEvent(Event evt) {
        Menu menu = getParent().getMenu();
        if(menu != null) menu.setVisible(true);
      }
      
    });
    
    addCaretListener(new CaretListener() {
      public void caretMoved(CaretEvent evt) {
//        int index = table.getSelectionIndex();
//        if(index < 0) return;
        
//        if(index == 0) {
//          buildAttrs(evt, index);
////          System.out.println(_newEditor.getText(start, end));
//          return;
//        }
        
        int index = evt.caretOffset;
        PathItemEditor _newEditor = (PathItemEditor)evt.getSource();
        
        if(index == _newEditor.getText().length()) {
          String path = _newEditor.getText();
          if(path.length() > 0 
              && path.charAt(path.length() - 1) == '.') {
            path = path.substring(0, path.length() - 1);
            
            if(getParent() instanceof Table) {
              Table table = (Table) getParent();
              carretEvent = new PathEvent(table.getSelectionIndex(), path);
            } else {
              carretEvent = new PathEvent(-1, path);
            }
            
            for(int i = 0; i < currents.size(); i++) {
              currents.get(i).change(carretEvent);
            }
            return;
          }
        }
        
//        System.out.println(index + " : " + _newEditor.getTextLimit() + " : "+ _newEditor.getText().length());
        
        while(index >= 0 && index < _newEditor.getText().length()) {
          char c = _newEditor.getText(index, index).charAt(0);
          if(c == '.') {
            index--;
            break;
          }
          index++;
        }
        
        String path = null;
        if(index >= _newEditor.getText().length()) {
          path = _newEditor.getText();
        } else {
          path = _newEditor.getText(0, index);
        }
        
        if(getParent() instanceof Table) {
          Table table = (Table) getParent();
          carretEvent = new PathEvent(table.getSelectionIndex(), path);
        } else {
          carretEvent = new PathEvent(-1, path);
        }
        for(int i = 0; i < currents.size(); i++) {
          currents.get(i).change(carretEvent);
        }
      }
    });
    
    addFocusListener(new FocusListener() {
      @SuppressWarnings("unused")
      public void focusLost(FocusEvent arg0) {
        int index = -1;
        String path = null;
        if(getParent() instanceof Table) {
          Table table = (Table) getParent();
          index = table.getSelectionIndex();
          if(index < 0) return;
          path = table.getItem(index).getText(0);
        } else {
          path = PathItemEditor.this.getText();
        }
        PathEvent event = new PathEvent(index, path);
        for(int i = 0; i < changes.size(); i++) {
          changes.get(i).change(event);
        }
      }
      
      @SuppressWarnings("unused")
      public void focusGained(FocusEvent arg0) {
      }
    });
    
    attrPopup = new Shell(getDisplay(), SWT.ON_TOP);
    attrPopup.setLayout(new FillLayout());
    attrTable = new Table(attrPopup, SWT.SINGLE);
    addListener(SWT.KeyDown, new Listener() {
      public void handleEvent(Event event) {
        switch (event.keyCode) {
          case SWT.ARROW_DOWN:
            int index = (attrTable.getSelectionIndex() + 1) % attrTable.getItemCount();
            attrTable.setSelection(index);
            event.doit = false;
            break;
          case SWT.ARROW_UP:
            index = attrTable.getSelectionIndex() - 1;
            if (index < 0) index = attrTable.getItemCount() - 1;
            attrTable.setSelection(index);
            event.doit = false;
            break;
          case SWT.CR:
            if (attrPopup.isVisible() && attrTable.getSelectionIndex() != -1) {
              buildAttrs(attrTable.getSelection()[0].getText());
              attrPopup.setVisible(false);
            }
            break;
          case SWT.ESC:
            attrPopup.setVisible(false);
            break;
        }
      }
    });
    
    attrTable.addListener(SWT.DefaultSelection, new Listener() {
      public void handleEvent(Event event) {
        buildAttrs(attrTable.getSelection()[0].getText());
        attrPopup.setVisible(false);
      }
    });
    attrTable.addListener(SWT.KeyDown, new Listener() {
      public void handleEvent(Event event) {
        if (event.keyCode == SWT.ESC) {
          attrPopup.setVisible(false);
        }
      }
    });

    Listener focusOutListener = new Listener() {
      public void handleEvent(Event event) {
        if (PathItemEditor.this.isDisposed()
            || getDisplay().isDisposed()) {
          attrPopup.dispose();
          return;
        }
        /* async is needed to wait until focus reaches its new Control */
        getDisplay().asyncExec(new Runnable() {
          public void run() {
            if (PathItemEditor.this.isDisposed() 
                || getDisplay().isDisposed()) {
              attrPopup.dispose();
              return;
            }
            Control control = getDisplay().getFocusControl();
            if (control == null || (control != PathItemEditor.this && control != attrTable)) {
              attrPopup.setVisible(false);
            }
          }
        });
      }
    };
    attrTable.addListener(SWT.FocusOut, focusOutListener);
    addListener(SWT.FocusOut, focusOutListener);

    getShell().addListener(SWT.Move, new Listener() {
      public void handleEvent(Event event) {
        if (PathItemEditor.this.isDisposed() 
            || getDisplay().isDisposed()) {
          attrPopup.dispose();
          return;
        }
        if(attrPopup.isDisposed()) return;
        attrPopup.setVisible(false);
      }
    });
    
//    this.horizontalAlignment = SWT.LEFT;
//    grabHorizontal = true;
//    minimumWidth = 50;
    // editing the second column
  }
  
  /*private void buildAttrs(CaretEvent evt, int index) {
    if(carretEvent == null) return;
    int start = -1;
    int end = -1;
    PathBoxEditor _newEditor = (PathBoxEditor)evt.getSource();
    
    index = evt.caretOffset;
    while(index >= 0 && index < _newEditor.getText().length()) {
      char c = _newEditor.getText(index, index).charAt(0);
      if(c == '}') {
        end = index-1;
        break;
      }
      index++;
    }
    
    index = evt.caretOffset;
    while(index >= 0 && index < _newEditor.getText().length()) {
      char c = _newEditor.getText(index, index).charAt(0);
      if(c == '{') {
        start = index+1;
        break;
      }
      index--;
    }
    
    if(start < 0 || end < 0 || start > end) return;
    String full = table.getItem(carretEvent.getIndex()).getText(0);
    
    if(full.charAt(full.length() - 1) == '.') {
      String attr = _newEditor.getText(start, end);
      int idx = attr.indexOf('=');
      if(idx > 0) attr = attr.substring(0, idx);
      full +=  attr;
      table.getItem(carretEvent.getIndex()).setText(1, full);
      return ;
    }
    
    String attr = "[" + _newEditor.getText(start, end) + "]" ;
    index = full.indexOf(attr);
    if(index > -1) {
      full = full.substring(0, index) + full.substring(index + attr.length());
      table.getItem(carretEvent.getIndex()).setText(0, full);
      return;
    }
    String path = carretEvent.getPath();
    
//    System.out.println(carretEvent.getIndex());
//    System.out.println(full);
//    System.out.println(path);
    
    String newValue = path + attr + full.substring(path.length());
    table.getItem(carretEvent.getIndex()).setText(0, newValue);
  }*/
  
  private void buildAttrs(String attr) {
    Table table = null;
    if(getParent() instanceof  Table) {
      table = (Table) getParent();
    }
    
    String full = null;
    if(table != null) { 
      full = table.getItem(carretEvent.getIndex()).getText(0);
    } else {
      full = getText();
    }
    
    if(popupType == POPUP_INDEX) {
      int start = getCaretOffset();
      while(start > -1) {
        char c = full.charAt(start);
        if(c == '[') break;
        start--;
      }
      int end = getCaretOffset();
      while(end < full.length()) {
        char c = full.charAt(end);
        if(c == ']') break;
        end++;
      }
      
      if(start > 0 && start < end-1) {
        String newValue = full.substring(0, start+1) + attr + full.substring(end);
        if(table != null) {
          table.getItem(carretEvent.getIndex()).setText(0, newValue);
        }
        setText(newValue);
        PathHightLight.setAutoStyle(this);
        setCaretOffset(end);
      }
      return;
    }
    
   /* if(full.charAt(full.length() - 1) == '.') {
      int idx = attr.indexOf('=');
      if(idx > 0) attr = attr.substring(0, idx);
      full +=  attr;
      if(table != null) {
        table.getItem(carretEvent.getIndex()).setText(0, full);
      }
      setText(full);
      setCaretOffset(full.length());
      PathHightLight.setAutoStyle(this);
      return ;
    }*/
    
    attr = "[" + attr + "]" ;
    int index = full.indexOf(attr);
    if(index > -1) {
      full = full.substring(0, index) + full.substring(index + attr.length());
      if(table != null) {
        table.getItem(carretEvent.getIndex()).setText(0, full);
      }
      setText(full);
      setCaretOffset(index);
      PathHightLight.setAutoStyle(this);
    } else {
      String path = carretEvent.getPath();

      String newValue = path + attr + full.substring(path.length());
      if(table != null) {
        table.getItem(carretEvent.getIndex()).setText(0, newValue);
      }
      setText(newValue);
      setCaretOffset(path.length());
    }
    
    PathHightLight.setAutoStyle(this);
    if(table != null) table.getColumn(0).pack();
  }
  
  public void addChangePath(ChangePath changePath) { changes.add(changePath); }
  
  public void addCurrentPath(CurrentPath currentPath) { currents.add(currentPath); }
  
  public void edit(TableItem item, final TableEditor editor) {
    setText(item.getText(0));
    addModifyListener(new ModifyListener() {
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent me) {
        StyledText text = (StyledText) editor.getEditor();
        editor.getItem().setText(0, text.getText());
      }
    });
//    selectAll();
    setFocus();
    editor.setEditor(this, item, 0);
    editor.getEditor().setVisible(true);
  }
  
  public void showAttrSuggestion(String [] attrs) {
    int numberic = getNumberic(); 
    if(numberic > -1) {
      popupType = POPUP_INDEX;
      attrs = new String[] {
          "*", 
          "i>" + String.valueOf(numberic),
          "i<=" + String.valueOf(numberic),
          "i%2=1",
          "i%2=0",
      };
    } else {
      popupType = POPUP_ATTR;
    }
    
    if(attrs == null || attrs.length < 1) return; 
    
    attrTable.removeAll();
    for (int i = 0; i < attrs.length; i++) {
      TableItem item = new TableItem(attrTable, SWT.NONE);
      item.setText(attrs[i]);
    }
    attrTable.setFont(getFont());
    
    if(attrs.length < 1) {
      attrPopup.setVisible(false);
    } else {
      Rectangle textBounds = getDisplay().map(getParent(), null, getBounds());
      GC gc = new GC(this);
      FontMetrics fm = gc.getFontMetrics();
      int charWidth = fm.getAverageCharWidth();
      int x = textBounds.x + getCaretOffset()*charWidth;
      int y = textBounds.y + textBounds.height;

      attrPopup.setBounds(x, y, 120, 70);
      attrPopup.pack();
      attrPopup.setSize(Math.max(120, attrPopup.getSize().x), 70);
      attrPopup.setVisible(true);
    }
    
   /* addListener(SWT.Modify, new Listener() {
      public void handleEvent(Event event) {
        String string = getText();
        if (string.length() == 0) {
          attrPopup.setVisible(false);
        } else {
          TableItem[] items = attrTable.getItems();
          for (int i = 0; i < items.length; i++) {
            items[i].setText(string + '-' + i);
          }
          Rectangle textBounds = getDisplay().map(getShell(), null, getBounds());
          attrPopup.setBounds(textBounds.x, textBounds.y + textBounds.height, textBounds.width, 150);
          attrPopup.setVisible(true);
        }
      }
    });*/
  }
  
  private int getNumberic() {
    try {
      int caret = getCaretOffset();
      String text = getText(caret, caret);
//      System.out.println("=== > " + text);     
      boolean numberic = false;
      if(text.length() > 0 
          && Character.isDigit(text.charAt(0))) numberic = true;
      
      if(!numberic) {
        text = getText(caret-1, caret-1);
//        System.out.println("sadasd "+ text);
        if(text.length() > 0 
            && Character.isDigit(text.charAt(0))) numberic = true;
      }
      
      if(!numberic) return -1;
      
      int start = caret;
      text = getText();
      while(start > -1) {
        char c = text.charAt(start);
        if(c == '[') break;
        start--;
      }
      int end = caret;
      while(end < text.length()) {
        char c = text.charAt(end);
        if(c == ']') break;
        end++;
      }
      if(start > 0 && start < end-1) {
        return Integer.parseInt(text.substring(start+1, end));
      }
    } catch (Exception e) {
    }
    return -1;
  }
  

}
