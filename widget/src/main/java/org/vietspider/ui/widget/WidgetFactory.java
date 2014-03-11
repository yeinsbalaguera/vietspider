/***************************************************************************
 * Copyright 2001-2005 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.vietspider.ui.XPWidgetTheme;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.services.ImageLoader;
import org.vietspider.ui.widget.action.IHyperlinkListener;
import org.vietspider.ui.widget.vtab.impl.VTab;

/**
 * @by thuannd (nhudinhthuan@yahoo.com)
 * @since Jul 12, 2005
 */
public class WidgetFactory { 
  
  ClientRM resources;
  
  ImageLoader imageLoader = new ImageLoader();
  
  public Button createButton(Composite com, int style){
    if(XPWidgetTheme.isPlatform())  {
      return new Button(com, style);
    }
    return new LButton(com, style);
  }
  
  public Button createButton(Composite com, int style, String text){
    Button widget = createButton(com, style);
    widget.setFont(UIDATA.FONT_9);
    widget.setText(text);
    return widget;
  }
  
//  public CButton createCButton(Composite com, int style, String text){
//    CButton widget = new CButton(com, style, XPWidgetTheme.THEME);
//    widget.setFont(UIDATA.FONT_9);
//    widget.setText(text);
//    return widget;
//  }
//  
//  public CCheckBox createCCheckBox(Composite com, String text){
//    CCheckBox widget = new CCheckBox(com);
//    widget.setFont(UIDATA.FONT_9);
//    widget.setText(text);
//    return widget;
//  }
  
  
  public Button createButton(Composite com, int style, String text, Font font){
    Button widget = createButton(com, style);
    widget.setFont(font);
    widget.setText(text);
    return widget;
  }
  
  public Button createButton(Composite com, int style, String text, SelectionListener listener){
    Button widget = createButton(com, style, text);
    if(listener != null) widget.addSelectionListener(listener);
    return widget;
  }
  
//  public CButton createCButton(Composite com, int style, String text, SelectionListener listener){
//    CButton widget = createCButton(com, style, text);
//    if(listener != null) widget.addSelectionListener(listener);
//    return widget;
//  }
//  
//  public CCheckBox createCCheckBox(Composite com, String text, SelectionListener listener){
//    CCheckBox widget = createCCheckBox(com, text);
//    if(listener != null) widget.addSelectionListener(listener);
//    return widget;
//  }
  
  public Button createButton(Composite com, int style, String text, String tip, SelectionListener listener){
    Button widget = createButton(com, style, text);
    if(listener != null) widget.addSelectionListener(listener);
    widget.setToolTipText(tip);
    return widget;
  }
  
//  public CButton createCButton(Composite com, int style, String text, String tip, SelectionListener listener){
//    CButton widget = createCButton(com, style, text);
//    if(listener != null) widget.addSelectionListener(listener);
//    return widget;
//  }
  
  public Button createButton(Composite com, Image icon, String tip, SelectionListener listener){
    Button widget = createButton(com, SWT.PUSH, "");
    widget.setImage(icon);
    widget.setToolTipText(tip);
    widget.addSelectionListener(listener);
    return widget;
  }
  
  public RButton createIcon(Composite com, Image img,  String tip, int t, MouseListener listener) {    
    RButton button = new RButton(com , img, t);
    button.setBackground(com.getBackground());
    button.addMouseListener(listener);
    button.setToolTipText(tip);
    return button;
    
  }
  
  public ImageHyperlink createIcon(Composite com, Image img, String tip, IHyperlinkListener listener) {    
    ImageHyperlink icon = new ImageHyperlink(com , SWT.CENTER);
    icon.setBackground(com.getBackground());
    icon.setImage(img);
    if(listener != null) icon.addHyperlinkListener(listener);
    icon.setToolTipText(tip);
    return icon;    
  } 
  
  public org.eclipse.ui.forms.widgets.Hyperlink createLink(
      Composite com, String text, org.eclipse.ui.forms.events.IHyperlinkListener listener) {    
    org.eclipse.ui.forms.widgets.Hyperlink link = new org.eclipse.ui.forms.widgets.Hyperlink(com , SWT.LEFT);
    link.setText(text);
//    link.setUnderlined(true);
    link.setFont(UIDATA.FONT_9);
    link.setBackground(com.getBackground());
    if(listener != null) link.addHyperlinkListener(listener);
    return link;    
  } 
  
  public org.eclipse.ui.forms.widgets.Hyperlink createMenuLink(Composite parent, String text, HyperlinkAdapter...adapters) {
    Hyperlink link = new Hyperlink(parent, SWT.LEFT);
    link.setText(text);
    link.setFont(UIDATA.FONT_9VB);
    link.setBackground(parent.getBackground());
    link.addHyperlinkListener(new HyperlinkAdapter(){
      public void linkEntered(HyperlinkEvent e) {
        Hyperlink hyperlink = (Hyperlink)e.widget;
        hyperlink.setUnderlined(true);
      }

      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
        Hyperlink hyperlink = (Hyperlink)e.widget;
      }
      
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
      }
    });
    for(HyperlinkAdapter adapter : adapters) {
      if(adapter != null) link.addHyperlinkListener(adapter);
    }
    
    return link;
  }
  
  public Label createLabel(Composite com, int style){
    return new Label(com, style);
  }
  
  public Label createLabel(Composite com, int style, String text){
    Label widget = this.createLabel(com, style);
    widget.setFont(UIDATA.FONT_9);
    widget.setText(text);
    return widget;
  }
  
  public Label createLabel(Composite com, int style, String text, Font font){
    Label widget = createLabel(com, style);
    widget.setFont(font);
    widget.setText(text);
    return widget;
  }
  
  public Label createLabel(Composite com, int style, String text, Object layout){
    Label widget = createLabel(com, style, text);
    widget.setLayoutData(layout);
    return widget;
  }
  
  
  public Text createText(Composite com, int style){
    return new Text(com, style);
  }
  
  public Text createText(Composite com, int style, String txt){
    Text widget = createText(com, style);
    widget.setFont(UIDATA.FONT_9);
    widget.setText(txt);
    return widget;
  }
  
  public Text createText(Composite com, int style, Font font){
    Text widget = createText(com, style);
    widget.setFont(font);    
    return widget;
  }
  
  public Text createText(Composite com, int style, String txt, Object layout){
    Text widget = createText(com, style, txt);
    widget.setLayoutData(layout);
    return widget;
  }  
  
  public Text createText(Composite com, int style, Object layout){
    Text widget = createText(com, style, "");
    widget.setLayoutData(layout);
    return widget;
  }  
  
  public Group createGroup(Composite com, int style){
    return new Group(com, style);
  }
  
  public Group createGroup(Composite com, int style, String txt){
    Group widget = createGroup(com, style);
    widget.setFont(UIDATA.FONT_10B);  
    widget.setText(txt);
    return widget;
  }
  
  public Group createGroup(Composite com, int style, String txt, Object layoutData){
    Group widget = createGroup(com, style, txt);
    widget.setLayoutData(layoutData);
    return widget;
  }  
  
  public Group createGroup(Composite com, int style, String txt, Object layoutData, Layout layout){
    Group widget = createGroup(com, style, txt);
    widget.setLayoutData(layoutData);
    widget.setLayout(layout);
    return widget;
  }  
  
  
  public Combo createCombo(Composite com, int style){
    return new Combo(com, style);
  }
  
  public Combo createCombo(Composite com, int style, String[] txt){
    Combo widget = createCombo(com, style);
    widget.setFont(UIDATA.FONT_10);
    widget.setItems(txt);
    return widget;
  }
  
  public Combo createCombo(Composite com, int style, String[] text, SelectionListener listener){
    Combo widget = createCombo(com, style, text);
    widget.addSelectionListener(listener);
    return widget;
  }
  
  
  public List createList(Composite com, int style){
    return new List(com, style);
  }  
  
  public List createList(Composite com, int style, String[] txt){
    List widget = createList(com, style);
    widget.setFont(UIDATA.FONT_9);
    widget.setItems(txt);
    return widget;
  }
  
  public ExpandMenu createExpandMenu(Composite com, int style){ return new ExpandMenu(com, style); }
  
  public List createList(Composite com, int style, String[] text, SelectionListener listener){
    List widget = createList(com, style, text);
    widget.addSelectionListener(listener);
    return widget;
  }
  
  public Table createTable(Composite com, 
      int style, String [] cols, int [] w, SelectionListener listener){
    Table table = new Table(com, style);
    table.setFont(UIDATA.FONT_10);
    if(listener != null) table.addSelectionListener(listener);
    table.setLinesVisible(true);
    table.setHeaderVisible(true);
    
    for(int i = 0; i < cols.length; i++){
      TableColumn order = new TableColumn(table, SWT.LEFT);
      order.setText(cols[i]);
      order.setWidth(w[i]);
    }   
    return table;
  }
  
  public Spinner createSpinner(Composite composite, int style) {
    Spinner spinner = new Spinner(composite, style);
    spinner.setFont(UIDATA.FONT_10);
    return spinner;
  }
  
  public Menu createMenu(Menu menu, String text){
    MenuItem item = new MenuItem(menu, SWT.CASCADE);
    item.setText(text);     
    Menu mSub = new Menu(menu.getShell(), SWT.DROP_DOWN);
    item.setMenu(mSub);
    return mSub;
  }
  
  public MenuItem createMenuItem(Menu menu, int style){
    return new MenuItem(menu, style);
  }
  
  public MenuItem createMenuItem(Menu menu, int style, String text){
    MenuItem item = createMenuItem(menu, style);
    if(text != null) item.setText(text);
    return item;
  }
  
  public MenuItem createMenuItem(Menu menu, int style, String text, SelectionListener listener){
    MenuItem item = createMenuItem(menu, style, text);    
    if(listener != null) item.addSelectionListener(listener);
    return item;
  }
  
  public MenuItem createMenuItem(Menu menu, int style, String text, ArmListener listener){
    MenuItem item = createMenuItem(menu, style, text);    
    item.addArmListener (listener);
    return item;
  }
  
  public Menu createToolItem(final ToolBar bar, Image image ){
    final ToolItem tool = new ToolItem(bar, SWT.DROP_DOWN);     
    if(image != null) tool.setImage(image);    
    final Menu menu = new Menu(bar.getShell(), SWT.POP_UP);
    tool.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        if (event.detail == SWT.ARROW) {
          Rectangle rect = tool.getBounds();
          Point pt = new Point(rect.x, rect.y + rect.height);
          pt = bar.toDisplay(pt);
          menu.setLocation(pt.x, pt.y);
          menu.setVisible(true);
        }
      }
    });    
    return menu;
  }
    
  public Shell createShell(Display display, final Class<?> cl, String title, String icon , int w, int h){   
    final Shell shell = new Shell(display, SWT.RESIZE | SWT.TITLE | SWT.CLOSE | SWT.MAX | SWT.MIN);    
    new ShellGetter(cl, shell, w, h);
    shell.setText(title);   
    Image image = imageLoader.load(display, icon);  
    if(image != null)  shell.setImage(image);
    return shell;
  }  
  
  
  public ProgressBar createProgress(Composite composite, GridData gridData){
    ProgressBar progressBar =  new ProgressBar(composite, SWT.HORIZONTAL);   
    progressBar.setBackground(UIDATA.BCOLOR);
    progressBar.setForeground(UIDATA.PROGRESS_COLOR);
    progressBar.setLayoutData(gridData);
    return progressBar;
  }
  
  public Tree createTree(Composite com){
    Tree tree = new Tree(com, SWT.SINGLE);   
    tree.setForeground(UIDATA.FCOLOR);   
    tree.setFont(UIDATA.FONT_13);
    return tree;
  }
  
  public Tree createTree(Composite com, int style){
    Tree tree = new Tree(com, style);   
    tree.setForeground(UIDATA.FCOLOR);   
    tree.setFont(UIDATA.FONT_13);
    return tree;
  }  
  
  
  public VTab createVTab(Composite com, int style){
    return new VTab(com, style);
  }

  public ImageLoader getImageLoader() { return imageLoader; }
}
