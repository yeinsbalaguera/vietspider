/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.cms;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.common.io.LicenseVerifier;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
abstract public class CMSChannelSelector2  {

  protected Shell shell;
  protected Text txtLinkToSource;
  protected Button butDebug;

  protected String metaId;
  protected String defaultCategory = null;

  private boolean destroy = false;
  
  protected CommonCategoriesTree tree;

  public CMSChannelSelector2(String plugin, Shell parent, String name, String title) {
    init(plugin, parent, new ApplicationFactory(parent, name, getClass().getName()), title);
  }
  
  public CMSChannelSelector2(String plugin, Shell parent, ClientRM rm, String title) {
    init(plugin, parent, new ApplicationFactory(parent, rm, getClass().getName()), title);
  }
  
  protected void init(String  plugin, Shell parent, ApplicationFactory factory, String title) {
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    factory.setComposite(shell);
    shell.setText(title);
    shell.setLayout(new GridLayout(1, false));

    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        shell.setVisible(false);
        e.doit = false;
      }
    });

    final KeyAdapter keyAdapter = new KeyAdapter() {
      public void keyPressed(KeyEvent event) {
        if(event.keyCode == SWT.CR) sync();
      }
    };


    Composite main = new Composite(shell, SWT.NONE);
    main.setLayout(new GridLayout(1, false));
    GridData gridData = new GridData(GridData.FILL_BOTH);
    main.setLayoutData(gridData);
    factory.setComposite(main);

    gridData = new GridData(GridData.FILL_BOTH);
    tree = new CommonCategoriesTree(main, gridData);
    Menu menu = new Menu(factory.getComposite().getShell(), SWT.POP_UP);
    tree.getTree().setMenu(menu);
    MenuItem item =  new MenuItem(menu, SWT.NONE);
    item.setText("Reload Categories");    
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        loadCategries();
      }
    });
    
    factory.setComposite(main);
    
    butDebug = factory.createButton(SWT.CHECK);
    butDebug.setText("Log error?");
    butDebug.setFont(UIDATA.FONT_10);

    factory.setComposite(main);
    createOption(plugin, factory);

    factory.setComposite(main);
    createButton(factory, keyAdapter);

    loadCategries();
  }

  protected void createOption(String plugin, ApplicationFactory factory) {
    Composite optionComposite = new Composite(factory.getComposite(), SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    optionComposite.setLayoutData(gridData);
    optionComposite.setLayout(new GridLayout(2, false));
    factory.setComposite(optionComposite);

    File licenseFile = LicenseVerifier.loadLicenseFile();
    boolean license = LicenseVerifier.verify(plugin, licenseFile);
    
    factory.createLabel("lblLinkToSource");
    txtLinkToSource = factory.createText(SWT.BORDER);
    txtLinkToSource.setEnabled(license);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtLinkToSource.setLayoutData(gridData);
  }

  protected void createButton(ApplicationFactory factory, KeyAdapter keyAdapter) {
    Composite bottom = new Composite(factory.getComposite(), SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;

    factory.setComposite(bottom);

//    if(XPWidgetTheme.isPlatform()) {
//      factory.createCButton("butConfig", new SelectionAdapter(){
//        @SuppressWarnings("unused")
//        public void widgetSelected(SelectionEvent evt) {
//          shell.setVisible(false);
//          destroy = true;
//          invokeSetup();
//        }   
//      });
//    } else {
      factory.createButton("butConfig", new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          shell.setVisible(false);
          destroy = true;
          invokeSetup();
        }   
      });
//    }

//    if(XPWidgetTheme.isPlatform()) {
//      CButton butOk = factory.createCButton("butOk", new SelectionAdapter(){
//        @SuppressWarnings("unused")
//        public void widgetSelected(SelectionEvent evt) {
//          sync();
//        }   
//      }); 
//      butOk.addKeyListener(keyAdapter);
//    } else {
      Button butOk = factory.createButton("butOk", new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          sync();
        }   
      }); 
      butOk.addKeyListener(keyAdapter);
//    }

//    if(XPWidgetTheme.isPlatform()) {
//      CButton button = factory.createCButton("butClose", new SelectionAdapter(){
//        @SuppressWarnings("unused")
//        public void widgetSelected(SelectionEvent evt) {
//          new ShellSetter(CMSChannelSelector2.this.getClass(), shell);
//          shell.setVisible(false);
//        }   
//      });
//      button.addKeyListener(keyAdapter);
//    } else {
      Button button = factory.createButton("butClose", new SelectionAdapter(){
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent evt) {
          new ShellSetter(CMSChannelSelector2.this.getClass(), shell);
          shell.setVisible(false);
        }   
      });
      button.addKeyListener(keyAdapter);
//    }


    shell.setImage(shell.getShell().getImage());
    shell.addKeyListener(keyAdapter);
  }

  public boolean isDispose() { return shell == null || shell.isDisposed(); }

  public void show() {
    //    listCategories.setSelection(-1);
    shell.setVisible(true); 
  }

  abstract public void invokeSetup();
  abstract public void sync() ;
  abstract public void loadCategries();

  public void setMetaId(String metaId) {
    butDebug.setSelection(false);
    this.metaId = metaId; 
  }

  public void dispose() { shell.dispose(); }

  public boolean isDestroy() { return destroy; }

}
