/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.abix;

import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.PluginClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.content.SyncContentPlugin;
import org.vietspider.content.abix.DrupalCategoriesConfig.DrupalCategory;
import org.vietspider.ui.XPWidgetTheme;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
class FormSelector {
  
  private Shell shell;
  private TreeItem [] categoryItems;
  private Tree treeCategories; 
  private Button [] rankingButtons;
  private Button butCheck;
  private Text txtUsername;
  private Text txtPassword;
  
  private boolean isSaveImage = false;
  
  private String metaId;
  private DrupalCategoriesConfig config;
  
  FormSelector(Shell parent) {
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    ApplicationFactory factory = new ApplicationFactory(shell, "Abix", getClass().getName());
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);
    shell.setLayout(new GridLayout(1, false));
    
    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        new ShellSetter(FormSelector.class, shell);
        Preferences prefs = Preferences.userNodeForPackage(FormSelector.class);
        String username_ = ClientConnector2.currentInstance().getUsername();
        prefs.put(username_ + ".account.username", txtUsername.getText());
        prefs.put(username_ + ".account.password", txtPassword.getText());
        shell.setVisible(false);
        e.doit = false;
      }
    });

    
    final KeyAdapter keyAdapter = new KeyAdapter() {
      public void keyPressed(KeyEvent event) {
        if(event.keyCode == SWT.CR) sync();
      }
    };
    
    org.eclipse.swt.widgets.Group group ;
    factory.setComposite(shell);
    
    GridData gridData = new GridData(GridData.FILL_BOTH);
    GridLayout gridLayout = new GridLayout(1, false);
    group = factory.createGroup("grpChannel", gridData, gridLayout);
    factory.setComposite(group);

    treeCategories = new Tree (group, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    gridData = new GridData(GridData.FILL_BOTH);
    treeCategories.setLayoutData(gridData);
    treeCategories.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        TreeItem [] items = treeCategories.getSelection();
        if(items == null) return;
        for(TreeItem item : items) {
          item.setChecked(!item.getChecked());
        }
      }
    });
    treeCategories.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        treeCategories.setSelection(new TreeItem[0]);
      }
      
    });
    treeCategories.addKeyListener(keyAdapter);
    
    Composite optionComposite = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
//    gridData.horizontalSpan = 2;
    optionComposite.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    optionComposite.setLayout(rowLayout);
    rowLayout.justify = true;
    factory.setComposite(optionComposite);
    
 // ++++++++++++++++++++++++++++++ account+++++++++++++++++++++++++++++++++++++++
    rowLayout = new RowLayout();
    rowLayout.justify = true;
    factory.setComposite(optionComposite);
    group = factory.createGroup("grpAccount", null, rowLayout);
    factory.setComposite(group);
    
    Composite composite = new Composite(group, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    factory.setComposite(composite);
    
    factory.createLabel("lblUsername");   
    txtUsername = factory.createText();
    gridData = new GridData();
    gridData.widthHint = 100;
    txtUsername.setLayoutData(gridData);
    
    String username  = "";
    String password = "";
    try {
      Preferences prefs = Preferences.userNodeForPackage(FormSelector.class);
      String vusername = ClientConnector2.currentInstance().getUsername();
      username  = prefs.get(vusername + ".account.username", "");
      password = prefs.get(vusername + ".account.password", "");
    } catch (Exception e) {
      e.printStackTrace();
      username  = "";
    }
    txtUsername.setText(username);
    txtUsername.addKeyListener(keyAdapter);
    
    composite = new Composite(group, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    factory.setComposite(composite);
    
    factory.createLabel("lblPassword");   
    txtPassword = factory.createText(SWT.BORDER | SWT.PASSWORD);
    gridData = new GridData(SWT.BORDER | SWT.PASSWORD);
    gridData.widthHint = 100;
    txtPassword.setLayoutData(gridData);
    txtPassword.setText(password);
    txtPassword.addKeyListener(keyAdapter);
    
    composite = new Composite(group, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));
    factory.setComposite(composite);
    butCheck = factory.createButton(SWT.CHECK, "Relogin");
    butCheck.setSelection(false);
    
    //+++++++++++++++++++++++++++++++++++ ranking +++++++++++++++++++++++++++++++++++
    
    rowLayout = new RowLayout();
    optionComposite.setLayout(rowLayout);
    rowLayout.justify = true;
    factory.setComposite(optionComposite);
    group = factory.createGroup("grpRanking", null, rowLayout);
    factory.setComposite(group);
    rankingButtons = new Button[5];
    for(int i = 0; i < rankingButtons.length; i++) {
      rankingButtons[i] = new Button(group, SWT.RADIO);
      if(i == 1) rankingButtons[i].setSelection(true);
      rankingButtons[i].addKeyListener(keyAdapter);
    }
    
    //+++++++++++++++++++++++++++++++++++++++button++++++++++++++++++++++++++++++++++++++++
    Composite bottom = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    bottom.setLayoutData(gridData);
    rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;
    
    factory.setComposite(bottom);
    
    Button butOk = factory.createButton("butOk", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        sync();
      }   
    }); 
    butOk.addKeyListener(keyAdapter);
    
    Button button = factory.createButton("butClose", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ShellSetter(FormSelector.class, shell);
        Preferences prefs = Preferences.userNodeForPackage(FormSelector.class);
        String username_ = ClientConnector2.currentInstance().getUsername();
        prefs.put(username_ + ".account.username", txtUsername.getText());
        prefs.put(username_ + ".account.password", txtPassword.getText());
        shell.setVisible(false);
      }   
    });
    button.addKeyListener(keyAdapter);
    
    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    shell.setImage(parent.getImage());
    shell.addKeyListener(keyAdapter);
    new ShellGetter(FormSelector.class, shell, 550, 300, x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
  }
  
  public void setData(DrupalCategoriesConfig config_) {
    this.config = config_;
    DrupalCategory [] categories = config.getCategories(); 
    categoryItems = new TreeItem[categories.length];
    TreeItem parentItem = null;
    for(int i = 0; i < categoryItems.length; i++) {
      String name = categories[i].getName().trim();
      if(name.startsWith("-")) {
        if(parentItem == null) continue;
        categoryItems[i] = new TreeItem(parentItem, SWT.NONE);
        name  = name.substring(1, name.length());
        categoryItems[i].setText(name);
        categoryItems[i].setFont(UIDATA.FONT_9);
      } else {
        categoryItems[i] = new TreeItem(treeCategories, SWT.NONE);
        categoryItems[i].setText(name);
        categoryItems[i].setFont(UIDATA.FONT_10B);
        parentItem = categoryItems[i];        
      }
    }
    
    for(int i = 0; i < categoryItems.length; i++) {
      categoryItems[i].setExpanded(true);
    }
  }
  
  public boolean isDispose() { return shell == null || shell.isDisposed(); }
  
  public void show() {
    for(int i = 0; i < categoryItems.length; i++) {
      categoryItems[i].setChecked(false);
    }
    shell.setVisible(true); 
  }

  public boolean isSaveImage() { return isSaveImage; }

  public void setMetaId(String metaId) { this.metaId = metaId; }
  
  private void synchronizedData(final String value) {
    Preferences prefs = Preferences.userNodeForPackage(FormSelector.class);
    String username_ = ClientConnector2.currentInstance().getUsername();
    prefs.put(username_ + ".account.username", txtUsername.getText());
    prefs.put(username_ + ".account.password", txtPassword.getText());
    shell.setVisible(false);
    
    Worker excutor = new Worker() {

      private String error = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          PluginClientHandler handler = new PluginClientHandler();
          error = handler.send("abix.sync.article.to.drupal.plugin", "save.article", value);
          if(error.toLowerCase().indexOf("successfull") > -1) {
            try {
              String user = ClientConnector2.currentInstance().getUserHeader().getValue();
              handler.send("action.user.log.plugin", "save", metaId + "."+ user+ ".sync");
            } catch (Exception e) {
            }
          }
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
        } else { 
          ClientRM resources = new ClientRM("Abix");
          String key = SyncContentPlugin.class.getName() + ".sync.content.message";
          String message = resources.getLabel(key);
          ClientLog.getInstance().setMessage(shell, new Exception(message));
        }
      }
    };
    new ThreadExecutor(excutor, shell).start();
  }
  
  private void sync() {
    if(!check()) return;
    StringBuilder builder = new StringBuilder();
    builder.append(txtUsername.getText()).append('\n');
    builder.append(txtPassword.getText()).append('\n');
    builder.append(metaId).append('\n');
    
    StringBuilder builderCategories = new StringBuilder();
    DrupalCategory [] categories = config.getCategories();
    for(int i = 0; i < categoryItems.length; i++) {
      if(!categoryItems[i].getChecked()) continue;
      if(builderCategories.length() > 0) builderCategories.append(',');
      builderCategories.append(categories[i].getId());
    }
    builder.append(builderCategories).append('\n');
    
    for(int i = 0; i < rankingButtons.length; i++) {
      if(rankingButtons[i].getSelection()) {
        builder.append(String.valueOf(i+1)).append('\n');
        break;
      }
    }
    builder.append(String.valueOf(butCheck.getSelection())).append('\n');
    
    synchronizedData(builder.toString());
  }
  
  private boolean check() {
    ClientRM resources = new ClientRM("Abix");
    boolean has = false;
    for(TreeItem item  : categoryItems) {
      if(item.getChecked()) {
        has = true;
        break;
      }
    }
    
    if(!has){
      String message = resources.getLabel(getClass().getName()+".msgNoSelectCategory");
      ClientLog.getInstance().setMessage(treeCategories.getShell(), new Exception(message));
      return false;
    }
    
    if(txtUsername.getText().trim().isEmpty()) {
      String message = resources.getLabel(getClass().getName()+".msgNoUsername");
      ClientLog.getInstance().setMessage(treeCategories.getShell(), new Exception(message));
      return false;
    }
    
    if(txtPassword.getText().trim().isEmpty()) {
      String message = resources.getLabel(getClass().getName()+".msgNoPassword");
      ClientLog.getInstance().setMessage(treeCategories.getShell(), new Exception(message));
      return false;
    }
    
    return true;
  }
  

}
