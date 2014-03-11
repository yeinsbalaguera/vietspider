/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.PathHightLight;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 6, 2009  
 */
public class StyledList  extends Composite {

  protected List<TableEditor> editors;
  protected Table table;  

  protected  StyledText selectedText; 

  protected Font font;

//  private Color normalColor;
//  private Color selectedColor;
  private Color disableColor;

  private String tempText;
  
  private int width = 100;

  private Menu menu;
  
  private List<SelectionListener> listeners = new ArrayList<SelectionListener>(3);

  public StyledList(ApplicationFactory factory, Composite parent, int style) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());
    table  = new Table(this, style);
    //    table.setLinesVisible(true);

    table.setFont(getFont());
    table.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        select(table.getSelectionIndex());
      }
      
    });
//    normalColor = new Color(getDisplay(), 255, 255, 255);
//    selectedColor = new Color(getDisplay(), 255, 255, 179); 
    disableColor = new Color(getDisplay(), 100, 100, 100);

    editors = new ArrayList<TableEditor>();

    if(factory != null) {
      menu = new Menu(getShell(), SWT.POP_UP);
      table.setMenu(menu);

      factory.createStyleMenuItem(menu, "menuClearSelected", new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          clearSelection();
        }
      });

      factory.createStyleMenuItem(menu, SWT.SEPARATOR);

      factory.createStyleMenuItem(menu, "menuRemoveSelected", "-.gif", new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          if(selectedText == null) return;
          remove(selectedText.getText());
        }
      });

      factory.createStyleMenuItem(menu, "menuRemove", new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          removeAll();
        }
      });
    }
  }
  
  public ScrollBar getHorizontalBar() {
    return table.getHorizontalBar();
  }

  public String[] getItems() {
    List<String> list = new ArrayList<String>(table.getItemCount());
    for(int i = 0; i < editors.size(); i++) {
      if(editors.get(i) == null 
          || editors.get(i).getEditor() == null 
          || editors.get(i).getEditor().isDisposed()) continue;
      StyledText styledText = (StyledText)editors.get(i).getEditor();
      list.add(styledText.getText());
    }
    return list.toArray(new String[list.size()]);
  }

  public void setItems(String [] items) {
    removeAll();
    width =  widthElement(items);
    for(int i = 0; i < items.length; i++) {
      TableEditor editor = createEditor(items[i]);
      editors.add(editor);
    }
  }

  public int getSelectionIndex() {
    for(int i = 0; i < editors.size(); i++) {
      if(editors.get(i).getEditor() == selectedText) return i;
    }
    return -1;
  }

  public void setItem(int index, String value) {
    StyledText styledText = (StyledText)editors.get(index).getEditor();
    table.getItem(index).setText(0, value);
    styledText.setText(value);
    
    width = widthElement(value, width);
    for(int i = 0; i < editors.size(); i++) {
      if(editors.get(i).minimumWidth == width) continue;
      editors.get(i).minimumWidth = width;
    }
    PathHightLight.setAutoStyle(styledText);
  }

  public void select(StyledText text) {
    if(selectedText != null) {
//      selectedText.setBackground(normalColor);
//      PathHightLight.unsetAutoStyle(selectedText, disableColor);
    }
    selectedText = text;
    if(selectedText == null) return;
//    selectedText.setBackground(selectedColor);
    setViewSelected();
    
    Event e = new Event();
    e.widget = this;
    SelectionEvent event  = new SelectionEvent(e);
    event.item = table;
    event.text = selectedText.getText();
    for(int i = 0; i < listeners.size(); i++) {
      listeners.get(i).widgetSelected(event);
    }
  }
  
  private void setViewSelected() {
    int type = selectedText != null ? 1 : 0;
    for(int i = 0; i < editors.size(); i++) {
      StyledText textEditor = (StyledText)editors.get(i).getEditor();
      if(type == 1 && textEditor != selectedText) {
        textEditor.setFont(UIDATA.FONT_8V);
        table.getItem(i).setFont(UIDATA.FONT_8V);
        table.getItem(i).setForeground(disableColor);
        textEditor.setVisible(false);
//        PathHightLight.unsetAutoStyle(textEditor, disableColor);
        continue;
      }
//      if(type == 0) {
//        PathHightLight.unsetAutoStyle(textEditor, table.getForeground());
//      }
      textEditor.setVisible(true);
      textEditor.setFont(getFont());
    }
  }

  public void select(int index) {
    select((StyledText)editors.get(index).getEditor());
  }
  

  public void remove(String element) {
    List<String> list = new ArrayList<String>(table.getItemCount());
    for(int i = 0; i < editors.size(); i++) {
      TableEditor editor = editors.get(i);
      if(editor == null 
          || editor.getEditor() == null 
          || editor.getEditor().isDisposed()) continue;
      StyledText styledText = (StyledText)editor.getEditor();
      String text = styledText.getText();
      if(text.equals(element)) continue;
      list.add(text);
    }
    setItems(list.toArray(new String[list.size()]));
  }

  public String getItem(int index) {
    StyledText styledText = (StyledText)editors.get(index).getEditor();
    return styledText.getText();
  }

  public void removeAll() {
    selectedText = null;
    if(editors != null) {
      for(int i = 0; i < editors.size(); i++) {
        TableEditor editor = editors.get(i);
        if(editor == null 
            || editor.getEditor() == null 
            || editor.getEditor().isDisposed()) continue;
        editor.getEditor().dispose();
        editor.dispose();
      }
    }
    editors.clear();
    table.removeAll();
  }

  public void remove(int index) {
    List<String> list = new ArrayList<String>(table.getItemCount());
    for(int i = 0; i < editors.size(); i++) {
      if(i == index) continue;
      TableEditor editor = editors.get(i);
      if(editor == null 
          || editor.getEditor() == null 
          || editor.getEditor().isDisposed()) continue;
      StyledText styledText = (StyledText)editor.getEditor();
      String text = styledText.getText();
      list.add(text);
    }
    setItems(list.toArray(new String[list.size()]));
  }

  public void add(String text) {
    width = widthElement(text, width);
    TableEditor editor = createEditor(text);
    editors.add(editor);
  }

  private TableEditor createEditor(String text) {
    for(int i = 0; i < editors.size(); i++) {
      if(editors.get(i).minimumWidth == width) continue;
      editors.get(i).minimumWidth = width;
    }
    
    TableItem item  = new TableItem(table, SWT.NONE);
    item.setText(text);
    TableEditor editor = new TableEditor(table);
    StyledText styledText = new StyledText(table, SWT.SINGLE);
    styledText.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent arg0) {
        final StyledText textEditor =  ((StyledText)arg0.getSource()); 
        tempText = textEditor.getText();
        Runnable timer = new Runnable () {
          public void run () {
            if(textEditor.isDisposed() 
                || !textEditor.isFocusControl()) return;
            String newText = textEditor.getText();
            if(tempText.equals(newText)) {
              tempText = newText;
              PathHightLight.setAutoStyle(textEditor);
              return;
            }
            getDisplay().timerExec(1000, this);
          }
        };
        getDisplay().timerExec(1000, timer);
      }
    });
    styledText.addMouseListener(new MouseAdapter() {
      public void mouseDown(MouseEvent e) {
        if(e.button == 3) {
          menu.setVisible(true);
        }
      }

      public void mouseUp(MouseEvent e) {
        select((StyledText)e.getSource());
      }
      
//      public void mouseDoubleClick(MouseEvent e) {
//        StyledText textEditor = (StyledText)e.getSource(); 
//        PathHightLight.handleDoubleClick(textEditor);
//      }
    });
//    styledText.addKeyListener(new KeyAdapter() {
//      @Override
//      public void keyPressed(KeyEvent event) {
//        StyledText textEditor = (StyledText)event.getSource(); 
//        if(event.character == '[') {
//          textEditor.insert("]");
//        }
//      }
//    });
    
    styledText.setText(text);
    styledText.setFont(getFont());
    editor.horizontalAlignment = SWT.LEFT;
    editor.minimumWidth = width; //widthElement(text) ;
    editor.setEditor(styledText, item, 0);
    PathHightLight.setAutoStyle(styledText);
    styledText.setVisible(false);
    styledText.setEditable(false);
    styledText.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
    return editor;
  }

  public int getItemCount() { return table.getItemCount(); }


  public void setFont(Font font) {
    this.font = font;
  }
  
  public void clearSelection() {
    selectedText = null;
    for(int i = 0; i < editors.size(); i++) {
      StyledText textEditor = (StyledText)editors.get(i).getEditor();
      table.getItem(i).setFont(table.getFont());
      table.getItem(i).setForeground(table.getForeground());
      textEditor.setVisible(false);
    }
  }

  public Font getFont() {
    if(this.font != null) return font;
    return UIDATA.FONT_10B;
  }
  
  protected int widthElement(String [] elements) {
    int max = 100;
    for(int i = 0; i < elements.length; i++) {
      max = widthElement(elements[i], max);
    }
    return max;
  }

  protected int widthElement(String text, int defaultW) {
    GC gc = new GC(table);
    gc.setFont(font);
    FontMetrics fontMetrics = gc.getFontMetrics();
    gc.dispose();
    int w = fontMetrics.getAverageCharWidth();
    w = (w + 1)*text.length();
    if(w < defaultW) w = defaultW;
    Rectangle rect = table.getClientArea();
    if(rect.width > w) w = rect.width;
    return w;
  }
  
  @Override()
  public void dispose() {
    removeAll();
    super.dispose();
  }
  
  public void addSelectionListener(SelectionListener listener) {
    listeners.add(listener);
  }

}
