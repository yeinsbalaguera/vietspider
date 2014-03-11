/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.CCombo;
import org.vietspider.ui.widget.CoolButton;
import org.vietspider.ui.widget.CoolButtonSelectionEvent;
import org.vietspider.ui.widget.CoolButtonSelectionListener;
import org.vietspider.ui.widget.ImageHyperlink;
import org.vietspider.ui.widget.LiveSashForm;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;
import org.vietspider.ui.widget.images.ToolBoxImageRegistry;
import org.vietspider.ui.widget.images.ToolbarResource;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 25, 2006
 */
abstract class HTMLExplorerToolbarViewer extends Composite {
  
  private LiveSashForm liveSash;
  protected CCombo cboAddress; 
  protected ProgressBar progressBar;

  protected ImageHyperlink butStop, butRefresh; 
  private ImageHyperlink butBack, butForward;
  private ImageHyperlink butGo;
  
  protected HTMLExplorerViewer viewer;
  
  private boolean showAll = false;
  
  public HTMLExplorerToolbarViewer(ApplicationFactory factory, 
      Composite parent, HTMLExplorerViewer viewer_) {
    super(parent, SWT.NONE);
    
    this.viewer  = viewer_;
    
    GridLayout gridLayout = new GridLayout(8, false);
    gridLayout.marginHeight = 2;
    gridLayout.horizontalSpacing = 5;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;    
    setLayout(gridLayout);
    
    GridData gridData = new GridData();
    final ToolbarResource resources = ToolbarResource.getInstance();
    butBack = resources.createIcon(this,
        resources.getImageBack(), resources.getTextBack(), new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        butBack.setImage(resources.getImageBack());
      }
      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
        butBack.setImage(resources.getImageBack());
      }
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
        butBack.setImage(resources.getImageBack());
      }
    });
    
    butBack.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        viewer.getBrowser().back();
      }

      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        butBack.setImage(resources.getImageBack1());
        butBack.redraw();
      }
    });

    gridData = new GridData(); 
    butForward = resources.createIcon(this, 
        resources.getImageForward(), resources.getTextForward(), new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        butForward.setImage(resources.getImageForward());
      }
      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
        butForward.setImage(resources.getImageForward());
      }
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
        butForward.setImage(resources.getImageForward());
      }
    });
    butForward.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        viewer.getBrowser().forward();
      }

      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        butForward.setImage(resources.getImageForward1());
        butForward.redraw();
      }
    });
    butForward.setLayoutData(gridData);
    
    liveSash = new LiveSashForm(this, SWT.HORIZONTAL);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    if(UIDATA.isMacOS) {
      gridData.heightHint = 30;
    } else {
      gridData.heightHint = 20;
    }
    liveSash.setLayoutData(gridData);
    factory.setComposite(liveSash);
    
    cboAddress = new CCombo(liveSash, SWT.BORDER | SWT.DROP_DOWN | SWT.V_SCROLL | SWT.H_SCROLL);   
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    cboAddress.setForeground(UIDATA.FCOLOR); 
    cboAddress.setFont(UIDATA.FONT_10);
    cboAddress.setBackground(UIDATA.ADDRESS_BCOLOR);
    if(UIDATA.isMacOS) {
      gridData.heightHint = 30;
      cboAddress.setFont(UIDATA.FONT_11);
    }
    cboAddress.setLayoutData(gridData);
    cboAddress.setVisibleItemCount(15);
    
    cboAddress.addKeyListener( new KeyAdapter(){
      public void keyReleased(KeyEvent e){
        if(e.keyCode != 13) return;
        viewer.goAddress(cboAddress.getText());
      }
    });   
    
    cboAddress.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e){
        viewer.goAddress(cboAddress.getText());
      }      
    }); 
    
    progressBar = new ProgressBar(liveSash, SWT.NONE);
    
    liveSash.setMaximizedControl(cboAddress);
    
    butRefresh = resources.createIcon(this, 
        resources.getImageRefesh(), resources.getTextRefesh(), new HyperlinkAdapter(){    
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        butRefresh.setImage(resources.getImageRefesh());
      }
      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
        butRefresh.setImage(resources.getImageRefesh());
      }
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
        butRefresh.setImage(resources.getImageRefesh());
      }
    });
    butRefresh.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        viewer.getBrowser().setUrl(cboAddress.getText());
      }

      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        butRefresh.setImage(resources.getImageRefesh1());
        butRefresh.redraw();
      }
    });
    gridData = new GridData(); 
    butRefresh.setLayoutData(gridData);  

    butStop = resources.createIcon(this, 
        resources.getImageStop(), resources.getTextStop(), new HyperlinkAdapter(){ 
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        butStop.setImage(resources.getImageStop());
      }
      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
        butStop.setImage(resources.getImageStop());
      }
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
        butStop.setImage(resources.getImageStop());
      }
    }); 
    butStop.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        viewer.getBrowser().stop();
      }

      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        butStop.setImage(resources.getImageStop1());
        butStop.redraw();
      }
    });
    gridData = new GridData(); 
    butStop.setLayoutData(gridData);
    
    Label lblSeparator = new Label(this, SWT.NONE);
    lblSeparator.setText("   ");
    

    final CoolButton toggleButton = new CoolButton(this,
        ToolBoxImageRegistry.getImage(ToolBoxImageRegistry.IMG_BUTTON_CHECKBOX_NORMAL),
        ToolBoxImageRegistry.getImage(ToolBoxImageRegistry.IMG_BUTTON_CHECKBOX_HOVER),
        ToolBoxImageRegistry.getImage(ToolBoxImageRegistry.IMG_BUTTON_CHECKBOX_PRESSED),
        ToolBoxImageRegistry.getImage(ToolBoxImageRegistry.IMG_BUTTON_CHECKBOX_NORMAL_TOGGLED),
        ToolBoxImageRegistry.getImage(ToolBoxImageRegistry.IMG_BUTTON_CHECKBOX_HOVER_TOGGLED),
        ToolBoxImageRegistry.getImage(ToolBoxImageRegistry.IMG_BUTTON_CHECKBOX_PRESSED_TOGGLED));
    
    toggleButton.setHotRegionMask(ToolBoxImageRegistry.getImage(ToolBoxImageRegistry.IMG_BUTTON_CHECKBOX_HOT_SPOT));
    toggleButton.setHotToggledRegionMask(ToolBoxImageRegistry.getImage(ToolBoxImageRegistry.IMG_BUTTON_HOT_SPOT_TOGGLED));
    gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
    toggleButton.setLayoutData(gridData);
    toggleButton.setToolTipText(factory.getLabel("butComplete"));
    toggleButton.addSelectionListener(new CoolButtonSelectionListener(){
      @SuppressWarnings("unused")
      public void selectionOnPress(CoolButtonSelectionEvent e) {
        showAll = !showAll;
        Preferences prefs = Preferences.userNodeForPackage(HTMLExplorerViewer.class);
        prefs.put("showAll", String.valueOf(showAll));
      }
      @SuppressWarnings("unused")
      public void selectionOnRelease(CoolButtonSelectionEvent e) {
      }
    });  
    
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    String prefValue = prefs.get( "showAll", "");
    if(prefValue != null && prefValue.trim().length() > 0){
      showAll = Boolean.valueOf(prefValue).booleanValue();
    } else {
      showAll = true;
    }
    toggleButton.setSelection(showAll);
    
    butGo = resources.createIcon(this, 
        resources.getImageGo(), resources.getTextGo(), new HyperlinkAdapter(){ 
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        butGo.setImage(resources.getImageGo());
      }
      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
        butGo.setImage(resources.getImageGo());
      }
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
        butGo.setImage(resources.getImageGo());
      }
    }); 
    butGo.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        viewer.selectAddress();
      }

      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        butGo.setImage(resources.getImageGo1());
        butGo.redraw();
      }
    });
    gridData = new GridData(); 
    butGo.setLayoutData(gridData);
    
  }
  
  public void addAddressToList(String value) {
    value = value.trim();
    String [] items = cboAddress.getItems();
    for(String item : items) {
      if(value.equalsIgnoreCase(item)) return;
    }
    cboAddress.add(value);
  }

  public boolean isShowAll() { return showAll; }

  ProgressBar getProgressBar() {
    return progressBar;
  }
  
  void showProgress() {
    liveSash.setMaximizedControl(progressBar);
  }
  
  void showInputAddress() {
    liveSash.setMaximizedControl(cboAddress);
  }
  
  public void setEnable(boolean value) {
    cboAddress.setEditable(value);
    butRefresh.setEnabled(value);
    butGo.setEnabled(value);
  }
  
}