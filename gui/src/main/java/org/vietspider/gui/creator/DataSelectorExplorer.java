/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.vietspider.browser.FastWebClient;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.model.Region;
import org.vietspider.model.RegionUtils;
import org.vietspider.ui.action.Event;
import org.vietspider.ui.browser.PageMenu;
import org.vietspider.ui.htmlexplorer.HtmlExplorerListener;
import org.vietspider.ui.htmlexplorer.NodeInfoViewer;
import org.vietspider.ui.htmlexplorer.PathBox;
import org.vietspider.ui.htmlexplorer.PathBox.PathEvent;
import org.vietspider.ui.htmlexplorer.TreeHandler;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 30, 2007  
 */
public class DataSelectorExplorer extends HtmlExplorerListener {

//  protected Button butUp;
//  protected Button butDown;
//  protected Button butRemoveAll;
  
  protected  List cboRegionName;
  protected  Combo cboType;
  
  protected Text txtInputName;

//  protected TreeAddButton treeAddButton;

  private java.util.List<NodeInfoViewer> nodeViewers = new ArrayList<NodeInfoViewer>();

  protected java.util.List<Region> dataRegions = new ArrayList<Region>(); 
  
  public DataSelectorExplorer(Composite parent, int type) {
    super(parent, type);
  }

  public DataSelectorExplorer(Composite parent) {
    super(parent);

    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);
    ApplicationFactory factory = new ApplicationFactory(this, "DataExplorer", getClass().getName()); 
    //  if(parent == null) shell.setImage(factory.loadImage("VietSpider2.ico"));

    SashForm sash0 = new SashForm(this, SWT.HORIZONTAL);
    sash0.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    sash0.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));

    browser = ApplicationFactory.createBrowser(sash0, PageMenu.class);
    //  ############################################################# Button UI ######################

    tree = new Tree(sash0, SWT.MULTI | SWT.BORDER);
    tree.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        selectTree();       
      }   
    });
    tree.addMouseListener(new MouseAdapter() {
      public void mouseDown(MouseEvent e) {
        if(e.button == 2) addItems();
      }
    });

    Menu treeMenu = new Menu(getShell(), SWT.POP_UP);
    tree.setMenu(treeMenu);

    factory.createStyleMenuItem( treeMenu, "itemAdd", "+.gif", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        addItems();
      }   
    });  

    factory.createStyleMenuItem(treeMenu, "itemRemove", "-.gif", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeItem();
      }   
    });

    factory.createStyleMenuItem(treeMenu, SWT.SEPARATOR);

    factory.createStyleMenuItem(treeMenu, "itemExpand", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        expand(true);
      }   
    });

    factory.createStyleMenuItem(treeMenu, "itemCollapse", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        expand(false);
      }   
    });

    factory.createStyleMenuItem(treeMenu, SWT.SEPARATOR);

    factory.createStyleMenuItem(treeMenu, "itemExpandDataNode", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        expandDataNode();
      }   
    });

    factory.createStyleMenuItem(treeMenu, "itemCollapseTree", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        TreeItem [] items = tree.getItems();
        if(items == null) return;
        for(TreeItem item : items) {
          expand(item, false);
        }
      }   
    });

    factory.createStyleMenuItem(treeMenu, SWT.SEPARATOR);

    factory.createStyleMenuItem( treeMenu, "itemView", "view.gif", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        viewItem();
      }   
    });   

    sash0.setWeights( new int[]{500, 300});

    //  ############################################################# Button UI ######################    

    Composite bottom = new Composite(this, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.heightHint = 200;
    bottom.setLayoutData(gridData);

    gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 1;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 1;
    bottom.setLayout(gridLayout);

    Composite nameComposite = new Composite(bottom, SWT.NONE);
    gridData = new GridData(GridData.FILL_VERTICAL);
    nameComposite.setLayoutData(gridData);
    gridLayout = new GridLayout(2, false);
    nameComposite.setLayout(gridLayout);
    factory.setComposite(nameComposite);

    txtInputName = factory.createText(SWT.BORDER);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtInputName.setLayoutData(gridData);
    txtInputName.setFont(UIDATA.FONT_10B);
    txtInputName.addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent event) {
        if(event.keyCode == SWT.CR) addRegion();
      }
    });

    gridData = new GridData();
    gridData.heightHint = 26;
    factory.createIcon("butAddRegionName", new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent evt) {
        addRegion();
      }

    }).setLayoutData(gridData);

    cboRegionName = factory.createList(SWT.MULTI | SWT.BORDER  | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY, new String[0]);
    gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.horizontalSpan = 2;
    gridData.widthHint = 150;
    cboRegionName.setLayoutData(gridData);
    cboRegionName.setFont(UIDATA.FONT_10B);
    cboRegionName.addFocusListener(new FocusAdapter() {
      @SuppressWarnings("unused")
      public void focusGained(FocusEvent arg0) {
        int selectedRegion = cboRegionName.getSelectionIndex();
        if(selectedRegion < 0) return;
        dataRegions.get(selectedRegion).setPaths(box.getItems());
      }

    });
    cboRegionName.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int idx = cboRegionName.getSelectionIndex();
        setSelectedPaths(idx);
      }      
    });

    Menu nameMenu = new Menu(getShell(), SWT.POP_UP);
    cboRegionName.setMenu(nameMenu);
    
    factory.createStyleMenuItem(nameMenu, "menuRenameRegion", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new RenameRegionDialog(getShell(), DataSelectorExplorer.this);
      }
    });
    
    factory.createStyleMenuItem(nameMenu, "menuUpRegion", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int index = cboRegionName.getSelectionIndex();
        changePosition(index, index-1);
      }
    });
    
    factory.createStyleMenuItem(nameMenu, "menuDownRegion", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int index = cboRegionName.getSelectionIndex();
        changePosition(index, index+1);
      }
    });

    factory.createStyleMenuItem(nameMenu, "menuRemoveRegion", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeRegion();
      }
    });
    
    cboType = factory.createCombo(SWT.BORDER);
    cboType.setItems(new String[]{"DEFAULT", "TEXT", "CDATA", "FILE"});
    gridData = new GridData();
    gridData.horizontalSpan = 2;
    gridData.widthHint = 150;
    cboType.setLayoutData(gridData);
    cboType.setFont(UIDATA.FONT_10B);
    cboType.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int index = cboRegionName.getSelectionIndex();
        if(index < 0) return;
        Region region = dataRegions.get(index);
        region.setType(cboType.getSelectionIndex());
      }
    });
    cboType.select(0);

    Composite dataComposite = new Composite(bottom, SWT.NONE);
    gridData = new GridData(GridData.FILL_BOTH);
    dataComposite.setLayoutData(gridData);

    gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 2;
    dataComposite.setLayout(gridLayout);

    factory.setComposite(dataComposite);

    box = new PathBox(dataComposite, factory);
    box.setLayoutData(gridData);
    box.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        lblStatus.setText("");
        String path  = box.getSelectedPath();
        if(path == null) return;
        try {
          traverseTree(TreeHandler.SELECT, new String[]{path});
        } catch (Exception exp) {
          ClientLog.getInstance().setMessage(tree.getShell(), exp);
        }
        if(isErrorPath(path)) showErrorPath(path);
      }
    });
    box.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
//        butDown.setVisible(true);
//        butUp.setVisible(true);
        highlightErrorPath(box.getSelectedPath());
        //        butRemoveAll.setVisible(true);
      }
    });
    box.addItemChangePath(new PathBox.ChangePath() {
      @Override
      public void change(PathEvent event) {
        traverseByPath(event.getPath());
      }
    });
    box.addItemCurrentPath(new PathBox.CurrentPath() {
      @Override
      public void change(PathBox.PathEvent event) {
        if(document == null) return;
        String path = event.getPath();
        String [] attrs = getAttrs(path);
        box.showAttrItemPopup(attrs);
      }
    });
    box.addSuggestCurrentPath(new PathBox.CurrentPath() {
      @Override
      public void change(PathBox.PathEvent event) {
        if(document == null) return;
        String path = event.getPath();
        String [] attrs = getAttrs(path);
        box.getSuggestWidget().showAttrSuggestion(attrs);
      }
    });

    Composite buttonComponent = new Composite(bottom, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    buttonComponent.setLayoutData(gridData);

    gridLayout = new GridLayout(3, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 15;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 10;
    buttonComponent.setLayout(gridLayout);
    
    Composite removeComposite = new Composite(buttonComponent, SWT.NONE);
    removeComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    removeComposite.setLayoutData(gridData);
    
    factory.setComposite(removeComposite);
    
    /*butRemoveAll = factory.createButton(SWT.PUSH);
    butRemoveAll.setText(factory.getResources().getLabel("menuRemoveAll"));
    butRemoveAll.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
        ClientRM clientRM = new ClientRM("HTMLExplorer");
        msg.setMessage(clientRM.getLabel("remove.all.message"));
        if(msg.open() != SWT.YES) return ;
        box.removeAll();
      }      
    });
    //    butRemoveAll.setVisible(false);
    butRemoveAll.setFont(UIDATA.FONT_9);

    butUp = factory.createButton(SWT.PUSH);
    butUp.setText(factory.getResources().getLabel("menuUp"));
    butUp.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        box.up();
      }      
    });
    butUp.setVisible(false);
    butUp.setFont(UIDATA.FONT_9);

    butDown = factory.createButton(SWT.PUSH);
    butDown.setText(factory.getResources().getLabel("menuDown"));
    butDown.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        box.down();
      }      
    });
    butDown.setVisible(false);
    butDown.setFont(UIDATA.FONT_9);*/

    lblStatus = factory.createLabel(SWT.NONE);
    gridData = new GridData();
//    gridData.widthHint = 305;
    lblStatus.setLayoutData(gridData);
    lblStatus.setFont(UIDATA.FONT_10);
    lblStatus.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
    
    ClientRM resource = new ClientRM("HTMLExplorer");
    butRemovePath = factory.createButton(SWT.PUSH);
    butRemovePath.setText(resource.getLabel("remove.path.yes"));
    butRemovePath.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(errorPath != null) {
          box.removePath(errorPath);
          clearInformation();
        }
        showErrorPath(null);
      }      
    });
    butRemovePath.setVisible(false);
    butRemovePath.setFont(UIDATA.FONT_9);
    
    factory.setComposite(buttonComponent);
    
    Button button = factory.createButton("butOk", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {       
        clickOk();          
      }   
    });
    button.setFont(UIDATA.FONT_9VB);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    button.setLayoutData(gridData);

    button = factory.createButton("butCancel", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {       
        clickCancel();          
      }   
    }); 
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    button.setLayoutData(gridData);

    handler = new TreeHandler();    

//    treeAddButton = new TreeAddButton(this);
  }
  

  protected void selectTree(){
    TreeItem[] items = tree.getSelection();
    if( items == null || items.length  < 1) return;
    TreeItem item = items[0];
    String txt = handler.getConfig(item);
    box.setSuggestPath(txt);   
    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();
    try {
      if(txt.indexOf('[') < 0 || txt.indexOf(']') < 0) return;
      NodePath nodePath  = pathParser.toPath(txt);
      HTMLNode node = extractor.lookNode(document.getRoot(), nodePath);

      searchRenderer.viewDocument(null, node, null);

//      treeAddButton.computeShowArea(item);
    } catch(Exception exp) {
      ClientLog.getInstance().setException(tree.getShell(), exp);
    }    
  }  
  
  public String[] addItems() {
    if(cboRegionName.getSelectionIndex() == -1) {
      if(cboRegionName.getItemCount() > 0) cboRegionName.select(0);
    }
    TreeItem[] items = tree.getSelection();
    if(items == null || items.length  < 1) {
      box.addItem();
    } else {
      for(TreeItem item : items) {
        box.addPath(handler.getConfig(item), false);    
      }
    }
    return null;
  }

  protected void removeItem(){
    TreeItem[] items = tree.getSelection();
    if( items == null || items.length  < 1) return;
    for(TreeItem item : items) {
      String path = handler.getConfig(item);    
      String [] _items = box.getItems();
      for(int i = 0; i < _items.length; i++){
        if(_items[i].equals(path)){
          box.removePath(_items[i]);
          break;
        }
      }   
    }
  }

  protected void viewItem(){
    TreeItem[] items = tree.getSelection();
    if( items == null || items.length  < 1) return;
    int x = getShell().getLocation().x+120, y = getShell().getLocation().y +130;
    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();
    for(TreeItem item : items) {
      String pathIndex = handler.getConfig(item); 
      if(pathIndex.indexOf('[') < 0 || pathIndex.indexOf(']') < 0) continue;
      try {
        NodeInfoViewer viewer = new NodeInfoViewer(getShell(), x, y);
        NodePath nodePath  = pathParser.toPath(pathIndex);
        HTMLNode node = extractor.lookNode(document.getRoot(), nodePath);
        viewer.setNode(node);   
        x += 10;
        y += 10;
        nodeViewers.add(viewer);
      }catch(Exception exp){
        ClientLog.getInstance().setMessage(tree.getShell(), exp);
      }
    }
  }

  public java.util.List<String> traverseTree(int style, String[] paths) { 
    java.util.List<String> removePaths = new ArrayList<String>();
    NodePathParser pathParser = new NodePathParser();
    for(String path : paths) {
      if(path.indexOf('[') < 0 || path.indexOf(']') < 0) continue;
      try {
        NodePath nodePath = pathParser.toPath(path);
        handler.traverseTree(this, tree, nodePath, path, style);
      } catch (Exception e) {
        ClientLog.getInstance().setMessage(tree.getShell(), e);
        return new ArrayList<String>();
      }
    }
    return removePaths;
  }

  public void setPath(String path, int style) {  
    if(path == null) return ;
    try {
      handler.traverseTree(this, tree, path, style);
      box.addPath(path, false);   
    } catch (Exception e) {
      ClientLog.getInstance().setMessage(tree.getShell(), e);
    }
  }

  public void setDocument(HTMLDocument doc){
    this.document = doc;
    tree.removeAll();
    handler.createTreeItem(tree, doc); 
  }

  public void clickOk(){
    Iterator<NodeInfoViewer> iterator = nodeViewers.iterator();
    while(iterator.hasNext()) {
      NodeInfoViewer viewer = iterator.next();
      if(viewer != null) viewer.close();
      viewer.close();
    }

    int selectedIndex = cboRegionName.getSelectionIndex();
    if(selectedIndex < 0) selectedIndex = 0;
    
    if(dataRegions.size() > 0 && selectedIndex < dataRegions.size()) {
      Region region = dataRegions.get(selectedIndex);
      region.setPaths(box.getItems());
      region.setType(cboType.getSelectionIndex());
    }

    try {
      new Event().fire(this);
    } catch(Exception exp){
      ClientLog.getInstance().setException(tree.getShell(), exp);
    }

  }

  public void clickCancel(){
    Iterator<NodeInfoViewer> iterator = nodeViewers.iterator();
    while(iterator.hasNext()) {
      NodeInfoViewer viewer = iterator.next();
      if(viewer != null) viewer.close();
      viewer.close();
    }

    Event event = new Event();
    event.setEventType(Event.EVENT_CANCEL);
    try{
      event.fire(this);
    }catch(Exception exp){
      ClientLog.getInstance().setException(getShell(), exp);
    }
  }

  protected void expand(boolean expand) {
    TreeItem [] items = tree.getSelection();
    for(TreeItem item : items) {
      expand(item, expand);
    }
  }

  protected void expandDataNode() {
    java.util.List<TreeItem> items = handler.getSelectedItems();
    for(TreeItem item : items) {
      expandDataNode(item, false);
    }
  }

  private void expandDataNode(TreeItem item, boolean expand) {
    item.setExpanded(expand);
    TreeItem parent = item.getParentItem();
    if(parent == null || parent.isDisposed()) return;
    expandDataNode(parent, true);
  }


  protected void expand(TreeItem item, boolean expand) {
    item.setExpanded(expand);
    TreeItem [] children = item.getItems();
    if(children == null) return;
    for(TreeItem child : children) {
      expand(child, expand);
    }
  }

  private void resetUI() {
    cboRegionName.setItems(RegionUtils.getNames(dataRegions));
    if(cboRegionName.getItemCount() < 1) return;
    //    cboRegionName.select(0);
  }

  public void reset() {
    browser.setText("<html></html>");
    box.removeAll();
    box.setSuggestPath("");
    document = null;
    cboRegionName.removeAll();
    tree.removeAll();
    dataRegions = new ArrayList<Region>();
    resetUI();
  }

  private void addRegion() {
    String name = txtInputName.getText();
    if(name == null || (name = name.trim()).isEmpty()) return;
    name = name.replace(' ', '_');
    Region region = new Region(name);
    dataRegions.add(region);
    txtInputName.setText("");
    resetUI();
  }
  
  void renameRegion(String newName) {
    int index = cboRegionName.getSelectionIndex();
    if(index < 0) return;
    cboRegionName.setItem(index, newName);
    dataRegions.get(index).setName(newName);
  }

  private void removeRegion() {
    String[] names = cboRegionName.getSelection();
    Iterator<Region> iterator = dataRegions.iterator();
    while(iterator.hasNext()) {
      Region region = iterator.next();
      for(int i = 0; i < names.length; i++) {
        if(region.getName().equals(names[i])) {
          iterator.remove();
          break;
        }
      }
    }
    resetUI();
  }

  public void setWebClient(FastWebClient webClient) { this.webClient = webClient; }

  public java.util.List<Region> getDataRegions() { return dataRegions; }

  public void setDataRegions(java.util.List<Region> dataRegions) { 
    this.dataRegions = dataRegions;
    resetUI();
  }

  public boolean hasSelectedItem() { 
    return box != null && box.getSelectedIndex() > -1; 
  }
  
  String getSelectedRegion() {
    int index = cboRegionName.getSelectionIndex();
    if(index < 0) return "";
    return cboRegionName.getItem(index);
  }
  
  void changePosition(int oldIndex, int newIndex) {
    if(oldIndex < 0 || oldIndex >= cboRegionName.getItemCount())  return;
    if(newIndex < 0 || newIndex >= cboRegionName.getItemCount())  return;
    
    String temp = cboRegionName.getItem(oldIndex);
    cboRegionName.setItem(oldIndex, cboRegionName.getItem(newIndex));
    cboRegionName.setItem(newIndex, temp);
    
    Region region = dataRegions.get(oldIndex);
    dataRegions.set(oldIndex, dataRegions.get(newIndex));
    dataRegions.set(newIndex, region);
    
    setSelectedPaths(oldIndex);
  }
  
  protected void setSelectedPaths(int index) {
    box.removeAll();
    if(index < 0 || index >= dataRegions.size()) return;

    Region region = dataRegions.get(index);
    if(region.getPaths() == null) return;

    String [] elements = region.getPaths();
//    System.out.println(" ======  >"+ elements.length);
//    box.setItems(elements);
    handler.resetTree(tree);
    for(String ele : elements) {
      if(ele.indexOf('[') < 0 || ele.indexOf(']') < 0) continue;
      setPath(ele, TreeHandler.MARK);
    }

    cboType.select(region.getType());
  }
  
  protected void highlightErrorPath(String path) {
    if(path == null || path.trim().isEmpty()) return;
    try {
      int index = handler.indexOfError(tree, path);
      if(index > 0) box.highlightError(path, index);
    } catch (Exception e) {
      ClientLog.getInstance().setMessage(null, e);
    }
  }

}
