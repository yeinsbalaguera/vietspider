package org.vietspider.gui.wizard;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.DataSelectorExplorer;
import org.vietspider.gui.creator.SourceEditor;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.ui.htmlexplorer.PathBox;
import org.vietspider.ui.htmlexplorer.TreeHandler;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/***************************************************************************
 * Copyright 2001-2011 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Dec 30, 2011  
 */
public class ChannelWizardStep5 extends DataSelectorExplorer implements IChannelWizardStep {
  
  protected Button butNext;
  protected Button butPrevious;
  
  protected ChannelWizard wizard;
  
  public ChannelWizardStep5(ChannelWizard wizard) {
    super(wizard, SWT.NONE);
    
    GridLayout gridLayout = new GridLayout(1, true);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);
    
    this.wizard = wizard;
    handler = new TreeHandler();
    
    ClientRM clientRM = ChannelWizard.getResources();
    ApplicationFactory factory = 
      new ApplicationFactory(wizard, clientRM, getClass().getName());
    
    SashForm mainSash = new SashForm(this, SWT.VERTICAL);
    mainSash.setBackground(getBackground());
    GridData gridData = new GridData(GridData.FILL_BOTH);
    mainSash.setLayoutData(gridData);

    SashForm browserSash = new SashForm(mainSash, SWT.HORIZONTAL);
    browserSash.setBackground(getBackground());
    browserSash.setLayoutData(new GridData(GridData.FILL_BOTH));
    
    browser = ApplicationFactory.createBrowser(browserSash, null);
    gridData = new GridData(GridData.FILL_BOTH);
    browser.setLayoutData(gridData);
    
    tree = new Tree(browserSash, SWT.BORDER);
    tree.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused") public void widgetSelected(SelectionEvent evt) {
        selectTree();
      }
    });
    tree.addMouseListener(new MouseAdapter() {
      public void mouseDown(MouseEvent e) {
        if (e.button == 2) addItems();
      }
    });
    
    Menu treeMenu = new Menu(getShell(), SWT.POP_UP);
    tree.setMenu(treeMenu);

    MenuItem menuItem1 = new MenuItem(treeMenu, SWT.PUSH);
    menuItem1.setText(ChannelWizard.getLabel("step5.menu.add"));
    menuItem1.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        addItems();
      }
    });
    
    menuItem1 = new MenuItem(treeMenu, SWT.PUSH);
    menuItem1.setText(ChannelWizard.getLabel("step5.menu.remove"));
    menuItem1.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeItem();
      }
    });
    
    menuItem1 = new MenuItem(treeMenu, SWT.PUSH);
    menuItem1.setText(ChannelWizard.getLabel("step5.menu.expand"));
    menuItem1.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")public void widgetSelected(SelectionEvent evt) {
        expand(true);
      }
    });
    

    menuItem1 = new MenuItem(treeMenu, SWT.PUSH);
    menuItem1.setText(ChannelWizard.getLabel("step5.menu.collapse"));
    menuItem1.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused") public void widgetSelected(SelectionEvent evt) {
        expand(false);
      }
    });
    
    menuItem1 = new MenuItem(treeMenu, SWT.PUSH);
    menuItem1.setText(ChannelWizard.getLabel("step5.menu.expand.data"));//"Expand Data Node");
    menuItem1.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")public void widgetSelected(SelectionEvent evt) {
        expandDataNode();
      }
    });

    menuItem1 = new MenuItem(treeMenu, SWT.PUSH);
    menuItem1.setText(ChannelWizard.getLabel("step5.menu.collapse.tree"));//"Collapse Tree");
    menuItem1.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")public void widgetSelected(SelectionEvent evt) {
        TreeItem [] items = tree.getItems();
        if(items == null) return;
        for(TreeItem item : items) {
          expand(item, false);
        }
      }
    });
    
    menuItem1.setText(ChannelWizard.getLabel("step5.menu.view.item"));//"View Item");
    menuItem1.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")public void widgetSelected(SelectionEvent evt) {
        viewItem();
      }
    });
  
    browserSash.setWeights(new int[] { 500, 300 });
    
    Composite bottom = new Composite(mainSash, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.heightHint = 300;
    bottom.setLayoutData(gridData);

    gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 1;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 1;
    bottom.setLayout(gridLayout);

    cboRegionName = new List(bottom, SWT.BORDER | SWT.V_SCROLL);
    cboRegionName.setFont(UIDATA.FONT_9);
    gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.widthHint = 150;
    cboRegionName.setLayoutData(gridData);
    cboRegionName.setFont(UIDATA.FONT_10B);
    cboRegionName.addFocusListener(new FocusAdapter() {
      @SuppressWarnings("unused")public void focusGained(FocusEvent arg0) {
        int selectedRegion = cboRegionName.getSelectionIndex();
        if (selectedRegion < 0) return;
        dataRegions.get(selectedRegion).setPaths(box.getItems());
      }
    });
    cboRegionName.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")public void widgetSelected(SelectionEvent evt) {
        int idx = cboRegionName.getSelectionIndex();
        if(idx < 0) return;
        setSelectedPaths(idx);
      }
    });
  
    box = new PathBox(bottom, factory);
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.verticalSpan = 2;
    box.setLayoutData(gridData);
    box.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")public void widgetSelected(SelectionEvent e) {
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
      @SuppressWarnings("unused")public void widgetSelected(SelectionEvent e) {
//        butDown.setVisible(true);
//        butUp.setVisible(true);
        highlightErrorPath(box.getSelectedPath());
      }
    });
    box.addItemChangePath(new PathBox.ChangePath() {
      public void change(PathBox.PathEvent event) {
        traverseByPath(event.getPath());
      }
    });
    box.addItemCurrentPath(new PathBox.CurrentPath() {
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
    
    cboType = new Combo(bottom, SWT.BORDER);
    cboType.setFont(UIDATA.FONT_9);
    cboType.setItems(new String[] {"DEFAULT", "TEXT", "CDATA", "FILE" });
    gridData = new GridData();
    gridData.widthHint = 150;
    cboType.setLayoutData(gridData);
    cboType.setFont(UIDATA.FONT_10B);
    cboType.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")public void widgetSelected(SelectionEvent evt) {
        int index = cboRegionName.getSelectionIndex();
        if(index < 0) return;
        Region region = dataRegions.get(index);
        region.setType(cboType.getSelectionIndex());
      }
    });
    cboType.select(0);
    
    mainSash.setWeights(new int[] {80, 20 });
    
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    createButton(gridData);
  }

  @Override
  public void show() {
    wizard.setTitle(" 6/10");
    if(this.webClient == null) {
      Creator creator = wizard.getTemp("creator");
      SourceEditor editor = creator.getInfoControl().getSourceEditor();
      this.webClient = editor.getWebClient();
    }
    String [] names = wizard.getTemp("tag.name");
    if(isNew(names)) {
      dataRegions = new ArrayList<Region>();
      for(int i = 0; i < names.length; i++) {
        dataRegions.add(new Region(names[i]));
      }
      cboRegionName.setItems(names);
    }
    String html = wizard.getTemp("extract.doc");
    if(html != null) browser.setText(html);
    createTree();
    if(tree == null) return;
    TreeItem[] items = tree.getItems();
    if(items != null && items.length > 0) {
      tree.select(items[0]);
      selectTree();
    }
  }
  
  private boolean isNew(String [] names) {
    if(dataRegions == null || dataRegions.size() < 1 
        || names.length != dataRegions.size()) return true;
    for(int i = 0; i <  names.length; i++) {
      if(dataRegions.get(i) == null) return true;
      if(!names[i].equals(dataRegions.get(i).getName())) return true;
    }
    return false;
  }
  
  private void createTree() {
    NodePathParser pathParser = new NodePathParser();
    HTMLExtractor extractor = new HTMLExtractor();
    
    HTMLDocument extractDoc = wizard.getTemp("document");
    
    try {
      String[] paths = wizard.getTemp("extract.path");
      NodePath [] nodePaths  = pathParser.toNodePath(paths);
      document = extractor.extract(extractDoc, nodePaths);
      
      tree.removeAll();
      handler.createTreeItem(tree, document); 
    } catch (Exception e) {
      error(e.toString());
      return;
    }
  }
  
  protected void createButton(GridData gridData) {
    Composite composite = new Composite(this, SWT.NONE);
    composite.setLayoutData(gridData);
    
    GridLayout gridLayout = new GridLayout(6, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 15;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    composite.setLayout(gridLayout);
    
    wizard.createToolBarButton(composite);
    
   /* butRemoveAll = new Button(composite, SWT.PUSH);
    butRemoveAll.setFont(UIDATA.FONT_9);
    butRemoveAll.setText(ChannelWizard.getLabel("step3.remove.all"));
    butRemoveAll.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
        ClientRM clientRM = new ClientRM("HTMLExplorer");
        msg.setMessage(clientRM.getLabel("remove.all.message"));
        if(msg.open() != SWT.YES) return ;
        box.removeAll();
      }
    });
    butRemoveAll.setFont(UIDATA.FONT_9);
    gridData = new GridData();
    butRemoveAll.setLayoutData(gridData);

    butUp = new Button(composite, SWT.PUSH);
    butUp.setFont(UIDATA.FONT_9);
    butUp.setText(ChannelWizard.getLabel("step3.up"));
    butUp.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        box.up();
      }
    });
    butUp.setVisible(false);
    butUp.setFont(UIDATA.FONT_9);
    gridData = new GridData();
    butUp.setLayoutData(gridData);

    butDown = new Button(composite, SWT.PUSH);
    butDown.setFont(UIDATA.FONT_9);
    butDown.setText(ChannelWizard.getLabel("step3.down"));
    butDown.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        box.down();
      }
    });
    butDown.setVisible(false);
    butDown.setFont(UIDATA.FONT_9);
    gridData = new GridData();
    butDown.setLayoutData(gridData);*/
    
    lblStatus = new Label(composite, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    lblStatus.setLayoutData(gridData);
    lblStatus.setFont(UIDATA.FONT_9);
    
    butRemovePath = new Button(composite, SWT.PUSH);
    butRemovePath.setFont(UIDATA.FONT_9);
    butRemovePath.setText(ChannelWizard.getLabel("step3.remove"));
    butRemovePath.addSelectionListener(new SelectionAdapter() {
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
    gridData = new GridData();
    butRemovePath.setLayoutData(gridData);
    
    Button butHelp = new Button(composite, SWT.PUSH);
    butHelp.setFont(UIDATA.FONT_9);
    butHelp.setText("Help");//ChannelWizard.getLabel("previous"));
    butHelp.addSelectionListener( new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        openHelp();
      }
    });
    gridData = new GridData();
    butHelp.setLayoutData(gridData);
    
    butPrevious = new Button(composite, SWT.PUSH);
    butPrevious.setFont(UIDATA.FONT_9);
    butPrevious.setText("Previous");
    butPrevious.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        ChannelWizardComposite.showPrevious(wizard);
      }
    });
    gridData = new GridData();
    butPrevious.setLayoutData(gridData);
    
    butNext = new Button(composite, SWT.PUSH);
    butNext.setFont(UIDATA.FONT_9);
    butNext.setText("Next");
    butNext.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        int selectedIndex = cboRegionName.getSelectionIndex();
        if(selectedIndex < 0) selectedIndex = 0;
        
        if(dataRegions.size() > 0 && selectedIndex < dataRegions.size()) {
          Region region = dataRegions.get(selectedIndex);
          region.setPaths(box.getItems());
          region.setType(cboType.getSelectionIndex());
        }
        
        Source source = wizard.getSource();
        source.setProcessRegion(dataRegions.toArray(new Region[dataRegions.size()]));
        ChannelWizardComposite.showNext(wizard);
      }
    });
    gridData = new GridData();
    butNext.setLayoutData(gridData);
  }
  
  void openHelp() {
    String youtube = "http://www.youtube.com/watch?v=yPhKuhUMloE";
    String video = "http://vietspider.org/video/step6.avi";
    wizard.openHelp(youtube, video);
  }
  
  public void reset() {
    browser.setUrl("about:blank");
    tree.removeAll();
    box.reset();
    dataRegions = null;
    cboRegionName.removeAll();
  }
  
  public void error(String error) {
    lblStatus.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
    lblStatus.setText(error);
  }


}
