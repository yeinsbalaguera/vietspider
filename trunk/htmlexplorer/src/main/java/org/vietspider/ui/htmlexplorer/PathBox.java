/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.PathHightLight;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 12, 2011  
 */
public class PathBox extends Composite {

  public static class PathEvent {
    private int index;
    private String path;

    public PathEvent(int index, String value) {
      this.index = index;
      this.path = value;
    }

    public int getIndex() { return index; }
    public String getPath() { return path; }
  }

  public static interface RemovePath {
    public void remove(PathEvent event) ;
  }

  public static interface ChangePath {
    public void change(PathEvent event) ;
  }

  public static interface CurrentPath {
    public void change(PathEvent event) ;
  }

  private Table table;
  private TableEditor editor;
  private TableColumn pathColunm;

  private PathItemEditor input;
  private ScrolledComposite scrolled;

  //  private ApplicationFactory factory;

  private List<ChangePath> listChanges = new ArrayList<ChangePath>();
  private List<CurrentPath> listCurrents = new ArrayList<CurrentPath>();
  private List<RemovePath> listRemoves = new ArrayList<RemovePath>();

  private PathEvent carretEvent = null;
  private Menu menu;

  public PathBox(Composite parent, ApplicationFactory factory) {
    super (parent, SWT.BORDER);

    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);

    scrolled = new ScrolledComposite(this, SWT.H_SCROLL);

    input = new PathItemEditor(scrolled);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    scrolled.setLayoutData(gridData);
    input.setFont(UIDATA.FONT_9);
    input.addFocusListener(new FocusAdapter() {
      @SuppressWarnings("unused")
      public void focusLost(FocusEvent arg0) {
        PathHightLight.setAutoStyle(input); 
      }
    });
    input.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        if(e.keyCode == SWT.CR) {
          String path = input.getText().trim();
          if(path.length() < 1) return;
          addPath(path, false);
        }
      }
    });

    scrolled.setContent(input);
    scrolled.setExpandHorizontal(true);
    scrolled.setExpandVertical(true);
    scrolled.addControlListener(new ControlAdapter() {
      @SuppressWarnings("unused")
      public void controlResized(ControlEvent e) {
        Rectangle r = scrolled.getClientArea();
        scrolled.setMinSize(input.computeSize(r.width, SWT.DEFAULT));
      }
    });

    Button butAdd = new Button(this, SWT.PUSH);
    butAdd.setText(factory.getLabel("butAdd"));
    gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    butAdd.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        String path = input.getText().trim();
        if(path.length() < 1) return;
        addPath(path, false);
      }

    });
    butAdd.setLayoutData(gridData);
    butAdd.setFont(UIDATA.FONT_9);


    table = new Table(this, SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
    table.setLayoutData(gridData);
    pathColunm = new TableColumn(table, SWT.NONE);
    table.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));

    editor = new TableEditor(table);
    editor.horizontalAlignment = SWT.LEFT;
    editor.grabHorizontal = true;
    editor.minimumWidth = 260;

    table.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        TableItem item = (TableItem) e.item;
        if (item == null ) return;

        Control oldEditor = editor.getEditor();
        if (oldEditor != null) oldEditor.dispose();

        PathItemEditor newEditor = new PathItemEditor(table);
        newEditor.changes = listChanges;
        newEditor.currents = listCurrents;
        newEditor.carretEvent = carretEvent;
        newEditor.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_HAND));
        newEditor.setBackground(item.getBackground());
        newEditor.setFont(item.getFont());
        newEditor.edit(item, editor);
        PathHightLight.setAutoStyle(newEditor);
      }
    });

    table.addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        int selection  = table.getHorizontalBar().getSelection();
        scrolled.getHorizontalBar().setSelection(selection);
      }
    });

    menu = new Menu(getShell(), SWT.POP_UP);

    MenuItem menuItem1 = new MenuItem(menu, SWT.PUSH);
    menuItem1.setText(factory.getLabel("menuRemoveSelected"));
    menuItem1.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        TableItem [] items = table.getSelection();
        if(items == null) return;
        for(int i = 0; i < items.length; i++) {
          removePath(items[i]);
        }
      }
    });

    menuItem1 = new MenuItem(menu, SWT.PUSH);
    menuItem1.setText(factory.getLabel("menuUp"));
    menuItem1.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        up();
      }
    });

    menuItem1 = new MenuItem(menu, SWT.PUSH);
    menuItem1.setText(factory.getLabel("menuDown"));
    menuItem1.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")public void widgetSelected(SelectionEvent evt) {
        down();
      }
    });

    menuItem1 = new MenuItem(menu, SWT.PUSH);
    menuItem1.setText(factory.getLabel("menuRemove"));
    menuItem1.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused") public void widgetSelected(SelectionEvent evt) {
        MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
        ClientRM clientRM = new ClientRM("HTMLExplorer");
        msg.setMessage(clientRM.getLabel("remove.all.message"));
        if(msg.open() != SWT.YES) return ;
        removeAll();
      }
    });

    table.setMenu(menu);
  }

  public void setCarretEvent(PathEvent carretEvent) { this.carretEvent = carretEvent; }

  public void addItemChangePath(ChangePath changePath) { listChanges.add(changePath); }

  public void addItemRemovePath(RemovePath removePath) { listRemoves.add(removePath); }

  public void addItemCurrentPath(CurrentPath currentPath) { listCurrents.add(currentPath); }

  public void addSuggestChangePath(ChangePath changePath) { input.changes.add(changePath); }

  public void addSuggestCurrentPath(CurrentPath currentPath) { input.currents.add(currentPath); }

  public void addSelectionListener(SelectionListener listener) { 
    table.addSelectionListener(listener);
  }

  public void setItems(String[] paths) {
    table.removeAll();
    cleanEditor();
    for (int i = 0; i < paths.length; i++) {
      addPath(paths[i], false);
    }
    pathColunm.pack();
  }

  public void addItem() {
    String _path = input.getText().trim();
    if(_path.length() < 1
        || _path.indexOf('{') > -1
        || _path.indexOf('}') > -1) return;
    addPath(_path, false);
    input.setText("");

    int index = table.getItemCount() - 1;
    if(index < 1) return;
    String path = table.getItem(index).getText(0);
    PathEvent event = new PathEvent(index, path);
    for(int i = 0; i < listChanges.size(); i++) {
      listChanges.get(i).change(event);
    }
  }

  public void setSuggestPath(String path) {
    input.setText(path);
    PathHightLight.setAutoStyle(input);
  }

  //  public void setPath(String path) {
  //    input.setText(path);
  //    pathColunm.pack();
  //  }

  public void removeAll() {
    input.setText("");
    TableItem[] items = table.getItems();
    if(items != null) {
      for(int i = 0; i < items.length; i++) {
        PathEvent event = new PathEvent(i, items[i].getText(0));
        for(int j = 0; j < listRemoves.size(); j++) {
          listRemoves.get(j).remove(event);
        }
        items[i].dispose();
      }
      cleanEditor();
    }
    pathColunm.pack();
  }

  public int getSelectedIndex() {
    return table.getSelectionIndex();
  }

  public String getSelectedPath() {
    int select = table.getSelectionIndex();
    if(select < 0) return null;
    return table.getItem(select).getText(0);
  }

  public void packList() {
    pathColunm.pack();
  }

  public void addPath(String path, boolean error) {
    TableItem item = new TableItem(table, SWT.NONE);
    item.setFont(UIDATA.FONT_9);
    item.setText(0, path);
    //    table.pack();
    pathColunm.pack();
  }

  public void showErrorPath(List<String> errors) {
    TableItem [] items = table.getItems();
    for(int i = 0; i < items.length; i++) {
      if(isErrorPath(items[i].getText(0), errors)) {
        items[i].setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
      } else {
        items[i].setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
      }
    }
  }

  private boolean isErrorPath(String path, List<String> errors) {
    for(int i = 0; i < errors.size(); i++) {
      if(path.equals(errors.get(i))) return true;
    }
    return false;
  }

  public void removePath(int index) {
    if(index < 0) return;
    TableItem item = table.getItem(index);
    PathEvent event = new PathEvent(index, item.getText(0));
    for(int i = 0; i < listRemoves.size(); i++) {
      listRemoves.get(i).remove(event);
    }

    item.dispose();
    cleanEditor();

    Control oldEditor = editor.getEditor();
    if (oldEditor != null) oldEditor.dispose();
    table.setSelection(-1);

    pathColunm.pack();
    table.redraw();
  }

  public void removePath(TableItem item) {
    PathEvent event = new PathEvent(-1, item.getText(0));
    for(int i = 0; i < listRemoves.size(); i++) {
      listRemoves.get(i).remove(event);
    }
    item.dispose();
    cleanEditor();

    Control oldEditor = editor.getEditor();
    if (oldEditor != null) oldEditor.dispose();
    table.setSelection(-1);

    //    table.pack();
    pathColunm.pack();
  }

  public void removePath(String path) {
    TableItem [] items = table.getItems();
    for(int i = 0; i < items.length; i++) {
      if(items[i].getText(0).equals(path)) {
        PathEvent event = new PathEvent(i, items[i].getText(0));
        for(int j = 0; j < listRemoves.size(); j++) {
          listRemoves.get(j).remove(event);
        }
        items[i].dispose();
      }
    }
    cleanEditor();

    Control oldEditor = editor.getEditor();
    if (oldEditor != null) oldEditor.dispose();
    table.setSelection(-1);

    pathColunm.pack();
  }

  public int getItemCount() { return table.getItemCount(); }

  private void cleanEditor() {
    if(editor.getEditor() != null 
        && !editor.getEditor().isDisposed()) {
      editor.getEditor().setVisible(false);
    }
  }

  public String[] getItems() {
    TableItem [] items = table.getItems();
    String [] values = new String[items.length];
    for(int i = 0; i < items.length; i++) {
      values[i] = items[i].getText(0);
    }
    return values;
  }

  public void up() {
    int index = table.getSelectionIndex();
    if(index < 1) return;
    String path1 = table.getItem(index).getText(0);
    String path2 = table.getItem(index-1).getText(0);
    table.getItem(index-1).setText(0, path1);
    table.getItem(index).setText(0, path2);
    Control oldEditor = editor.getEditor();
    if (oldEditor != null) oldEditor.dispose();
    table.select(index-1);
  }

  public void down() {
    int index = table.getSelectionIndex();
    if(index >= table.getItemCount()-1) return;
    String path1 = table.getItem(index).getText(0);
    String path2 = table.getItem(index+1).getText(0);
    table.getItem(index+1).setText(0, path1);
    table.getItem(index).setText(0, path2);
    Control oldEditor = editor.getEditor();
    if (oldEditor != null) oldEditor.dispose();
    table.select(index+1);
  }

  public void highlightError(String path, int from) {
    if(from >= path.length()) return;

    PathItemEditor pathEditor = (PathItemEditor)editor.getEditor();
    if (pathEditor == null) return;

    if(!path.equals(pathEditor.getText())) return;

    //    System.out.println(from + " : "+ (path.length() - from));

    StyleRange [] styleRanges = pathEditor.getStyleRanges(from, path.length() - from);

    //    System.out.println(styleRanges.length);

    Color color = new Color(getDisplay(), 255, 200, 200);
    if(styleRanges == null || styleRanges.length < 1) {
      StyleRange styleRange = new StyleRange();
      styleRange.start = from;
      styleRange.length = path.length() - from;
      styleRange.background = color;
      pathEditor.setStyleRange(styleRange);
    } else {
      for(int i = 0; i < styleRanges.length; i++) {
        StyleRange styleRange = styleRanges[i];
        styleRange.background = color;
        pathEditor.setStyleRange(styleRange);
      }
    }

    pathEditor.redraw();
  }

  public void showAttrItemPopup(String [] attrs) {
    if(editor == null 
        || editor.getEditor() == null
        || editor.getEditor().isDisposed()) return;
    PathItemEditor newEditor = (PathItemEditor)editor.getEditor();
    newEditor.showAttrSuggestion(attrs);
  }
  
  public void reset() {
    table.removeAll();
    input.setText("");
  }
  
  public PathItemEditor getSuggestWidget() { return input; }

  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());

    ApplicationFactory factory = new ApplicationFactory(shell,
        "HTMLExplorer", "org.vietspider.ui.htmlexplorer.HTMLExplorer");

    PathBox box = new PathBox(shell, factory);

    shell.setSize(300, 300);
    shell.open();

    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }

}
