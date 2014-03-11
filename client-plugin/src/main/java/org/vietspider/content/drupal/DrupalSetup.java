/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.drupal;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.chars.refs.RefsEncoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.util.Worker;
import org.vietspider.content.drupal.XMLDrupalConfig.Category;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
//date 27/02/2010
//drupal account: admin-admin123
public class DrupalSetup {

  protected Shell shell;

  protected Text txtHomepage;
  protected Text txtPostAddress;
  protected Text txtLogin;
  protected Text txtUploadImage;
  protected Text txtMetaImageWidth;

  protected Text txtUsername;
  protected Text txtPassword;

  protected Button butAlertWhenComplete;
  protected Button butAutoSync;
  
  protected Text txtLinkToSource;

  protected Table tblCategories; 

  protected TabFolder tab;

  protected Button butOk ;

  public DrupalSetup(Shell parent) {
    ClientRM resources = DrupalSyncArticlePlugin.getResources();
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    ApplicationFactory factory = new ApplicationFactory(shell, resources, getClass().getName());
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);
    shell.setLayout(new GridLayout(2, false));

    GridData gridData;

    tab = new TabFolder(shell, SWT.TOP);
    tab.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_BOTH);     
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalSpan = 3;
    tab.setLayoutData(gridData);

    TabItem tabItem;

    Composite commonConfig = new Composite(tab, SWT.NONE);
    tabItem = new TabItem(tab, SWT.NONE);
    tabItem.setText("Common Config");
    tabItem.setControl(commonConfig);

    commonConfig.setLayout(new GridLayout(2, false));
    factory.setComposite(commonConfig);

    factory.createLabel("lblHomepage");  
    txtHomepage = factory.createText();
    txtHomepage.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtHomepage.setLayoutData(gridData);  
    txtHomepage.addFocusListener(new FocusListener() {

      @SuppressWarnings("unused")
      public void focusGained(FocusEvent arg0) {
      }

      @SuppressWarnings("unused")
      public void focusLost(FocusEvent arg0) {
        autoComplete();
      }
      
    });

    factory.createLabel("lblLogin");  
    txtLogin = factory.createText();
    txtLogin.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtLogin.setLayoutData(gridData); 
    
    
    factory.createLabel("lblUploadImage");  
    txtUploadImage = factory.createText();
    txtUploadImage.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtUploadImage.setLayoutData(gridData);
    txtUploadImage.setEnabled(false);

    factory.createLabel("lblPostAddress");  
    txtPostAddress = factory.createText();
    txtPostAddress.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtPostAddress.setLayoutData(gridData);

    GridLayout  gridLayout = new GridLayout(2, false);

    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    
    Composite linkComposite = new Composite(commonConfig, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    linkComposite.setLayoutData(gridData);
    linkComposite.setLayout(new GridLayout(2, false));
    factory.setComposite(linkComposite);
    
    factory.createLabel("lblLinkToSource");   
    txtLinkToSource = factory.createText(SWT.BORDER);
    txtLinkToSource.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtLinkToSource.setLayoutData(gridData);
    
    Composite butComposite = new Composite(commonConfig, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    butComposite.setLayoutData(gridData);
    butComposite.setLayout(new GridLayout(3, false));
    factory.setComposite(butComposite);
    
    butAlertWhenComplete = factory.createButton(SWT.CHECK, factory.getLabel("butAlertWhenComplete"));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    butAlertWhenComplete.setLayoutData(gridData);
    butAlertWhenComplete.setSelection(true);
    
    butAutoSync = factory.createButton(SWT.CHECK, "Auto Synchronized?");
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    butAutoSync.setLayoutData(gridData);
    butAutoSync.setSelection(true);
    
    Composite imageComposite = new Composite(butComposite, SWT.NONE);
    imageComposite.setLayout(new GridLayout(2, false));
    factory.setComposite(imageComposite);
    
    Label lbl = factory.createLabel(SWT.NONE);
    lbl.setText("Teaser Image Width: ");
    txtMetaImageWidth = factory.createText();
    txtMetaImageWidth.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 40;
    txtMetaImageWidth.setLayoutData(gridData);

    Composite userComposite = new Composite(commonConfig, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    userComposite.setLayoutData(gridData);
    userComposite.setLayout(new GridLayout(2, false));
    factory.setComposite(userComposite);

    Composite composite = new Composite(userComposite, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    factory.setComposite(composite);

    factory.createLabel("lblUsername");   
    txtUsername = factory.createText();
    gridData = new GridData();
    gridData.widthHint = 120;
    txtUsername.setLayoutData(gridData);

    composite = new Composite(userComposite, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    factory.setComposite(composite);

    factory.createLabel("lblPassword");   
    txtPassword = factory.createText(SWT.BORDER | SWT.PASSWORD);
    gridData = new GridData(SWT.BORDER | SWT.PASSWORD);
    gridData.widthHint = 120;
    txtPassword.setLayoutData(gridData);

    Composite cateComposite = new Composite(tab, SWT.NONE);
    gridLayout = new GridLayout(1, false);
    cateComposite.setLayout(gridLayout);

    tabItem = new TabItem(tab, SWT.NONE);
    tabItem.setText("Categories Config");
    tabItem.setControl(cateComposite);

    factory.setComposite(cateComposite);
    tblCategories = factory.createTable("tableData",  null, SWT.FULL_SELECTION | SWT.MULTI);
    gridData = new GridData(GridData.FILL_BOTH);
    tblCategories.setLayoutData(gridData);
    Menu menu = new Menu (shell, SWT.POP_UP);
    tblCategories.setMenu (menu);
    MenuItem item = new MenuItem (menu, SWT.PUSH);
    item.setText("Add New Category");
    item.addListener (SWT.Selection, new Listener () {
      @SuppressWarnings("unused")
      public void handleEvent (Event event) {
        createNewItem();
      }
    });

    item = new MenuItem (menu, SWT.PUSH);
    item.setText("Delete Selection");
    item.addListener (SWT.Selection, new Listener () {
      @SuppressWarnings("unused")
      public void handleEvent (Event event) {
        tblCategories.remove(tblCategories.getSelectionIndices());
      }
    });
    
    Composite detectorComposite = new Composite(cateComposite, SWT.NONE);
    detectorComposite.setLayout(new GridLayout(1, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    detectorComposite.setLayoutData(gridData);
    factory.setComposite(detectorComposite);
    
    factory.createButton("butCategoriesDetector", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        detectCategories(false);
      }   
    }); 

    createNewItem();

    Composite bottom = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;

    factory.setComposite(bottom);
    
    factory.createButton("butClear", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        clear() ;
      }   
    }); 

    butOk = factory.createButton("butOk", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(!check()) return;
        if(getCategoriesData().length < 1) {
          detectCategories(true);
        } else {
          saveConfig() ;
        }
        new ShellSetter(DrupalSetup.this.getClass(), shell);
      }   
    }); 
    
    factory.createButton("butClose", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ShellSetter(DrupalSetup.this.getClass(), shell);
        shell.close();
      }   
    });

    loadConfig();

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    shell.setImage(parent.getImage());
    new ShellGetter(DrupalSetup.class, shell, 550, 350, x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
  }

  protected void loadConfig() {
    Worker excutor = new Worker() {

      protected String error = null;
      protected XMLDrupalConfig config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "load.file.by.gzip"),
              new BasicHeader("file", "system/plugin/drupal.config")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);

          config = XML2Object.getInstance().toObject(XMLDrupalConfig.class, bytes);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        if(config == null) return;
        txtHomepage.setText(config.getHomepage());
        txtLogin.setText(config.getLoginAddress());
        txtPostAddress.setText(config.getPostAddress());
//        txtUploadImage.setText(config.getImageUploadAddress());
        txtMetaImageWidth.setText(config.getMetaImageWidth());

        txtUsername.setText(config.getUsername());
        txtPassword.setText(config.getPassword());

        butAlertWhenComplete.setSelection(config.isAlertMessage());
        butAutoSync.setSelection(config.isAutoSync());
        
        RefsDecoder decoder = new RefsDecoder();
        String text = config.getLinkToSource();
        if(text != null) {
          text = new String(decoder.decode(text.toCharArray()));
          txtLinkToSource.setText(text);
        }

        setCategoriesData(config.getCategories());
      }
    };
    new ThreadExecutor(excutor, txtHomepage).start();
  }

  protected void saveConfig() {
    butOk.setEnabled(false);
    Worker excutor = new Worker() {

      protected String error = null;
      protected XMLDrupalConfig config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        config = new XMLDrupalConfig();
        config.setHomepage(txtHomepage.getText());
        config.setLoginAddress(txtLogin.getText());
        config.setPostAddress(txtPostAddress.getText());
//        config.setImageUploadAddress(txtUploadImage.getText());
        config.setMetaImageWidth(txtMetaImageWidth.getText());

        config.setUsername(txtUsername.getText());
        config.setPassword(txtPassword.getText());

        config.setAlertMessage(butAlertWhenComplete.getSelection());
        config.setAutoSync(butAutoSync.getSelection());
        
        RefsEncoder encoder = new RefsEncoder();
        String text = txtLinkToSource.getText();
        text = new String(encoder.encode(text.toCharArray()));
        config.setLinkToSource(text);

        Category[] categories = getCategoriesData();
        config.setCategories(categories);
      }

      public void execute() {
        if(config == null) return;
        try {
          Object2XML bean2XML = Object2XML.getInstance();
          String xml = bean2XML.toXMLDocument(config).getTextValue();

         
          
          Header [] headers =  new Header[] {
              new BasicHeader("action", "save"),
              new BasicHeader("file", "system/plugin/drupal.config")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = xml.getBytes(Application.CHARSET);
          connector.post(URLPath.FILE_HANDLER, bytes, headers);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        shell.close();
      }
    };
    new ThreadExecutor(excutor, txtHomepage).start();
  }

  protected void createNewItem() {
    TableItem tableItem = new TableItem(tblCategories, SWT.NONE);
    tableItem.setText("");
    tableItem.setText("");
    tableItem.setText("");
  }
  
  protected void detectCategories(final boolean save) {
    if(!check()) return;
    
    Worker excutor = new Worker() {

      private String error = null;
      private String cateURL = null;
      
      private CategoriesDetector detector;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        String homepage = txtHomepage.getText();
        String login = txtLogin.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        detector = new CategoriesDetector(homepage, login, username, password, "utf-8");
        cateURL = txtPostAddress.getText();
      }

      public void execute() {
        try {
          System.out.println(" ===========  >"+ cateURL);
          detector.detect(cateURL);
        } catch (Exception e) {
          e.printStackTrace();
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        List<Category> list = detector.getCategories();
        Category [] categories = list.toArray(new Category[list.size()]);
        if(categories.length < 1) {
          Exception exception = new Exception("Warning: No categories from server. Try again.");
          ClientLog.getInstance().setMessage(shell, exception);
          return;
        }
        setCategoriesData(categories);
        if(save) saveConfig();
      }
    };
    
    WaitLoading loading = new WaitLoading(txtHomepage, excutor);
    loading.open();
  }
  
  protected void setCategoriesData(Category [] categories) {
    tblCategories.removeAll();
    if(categories == null) return;
    for(int i = 0; i < categories.length; i++) {
      TableItem item = new TableItem(tblCategories, SWT.NONE);
      item.setText(0, categories[i].getCategoryId());
      item.setText(1, categories[i].getCategoryName());
    }
  }
  
  protected Category[] getCategoriesData() {
    List<Category> list = new ArrayList<Category>();
    TableItem [] items = tblCategories.getItems();
    for(int i = 0; i < items.length; i++) {
      String categoryId = items[i].getText(0);
      if(categoryId == null || categoryId.trim().isEmpty()) continue;
      String categoryName = items[i].getText(1);
      if(categoryName == null || categoryName.trim().isEmpty()) continue;
      Category category = new Category();
      category.setCategoryId(categoryId);
      category.setCategoryName(categoryName);
      list.add(category);
    }
    
    return list.toArray(new Category[list.size()]);
  }
  
  protected void autoComplete() {
    String url =  txtHomepage.getText();
    if((url = url.trim()).isEmpty()) return;
    
    txtLogin.setText(url);
      
    if(txtPostAddress.getText().trim().isEmpty()) {
      if(url.endsWith("/")) {
        txtPostAddress.setText(url +"node/add/");
      } else {
        txtPostAddress.setText(url +"/node/add/");
      }
    }
    
    if(txtUsername.getText().trim().isEmpty()) {
      txtUsername.setText("admin"); 
    }
    
  }
  
  protected void clear() {
    txtHomepage.setText("");
    txtLogin.setText("");
    txtPostAddress.setText("");
    txtMetaImageWidth.setText("-1");
    
    txtUsername.setText("");
    txtPassword.setText("");
    
    txtLinkToSource.setText("<p align=\"right\">Theo<a href=\"$source.link\">$source.name</a></p>");
    
    butAlertWhenComplete.setSelection(true);
    
    tblCategories.removeAll();
  }
  
  public boolean check(){
    ClientRM resources = new ClientRM("DrupalSyncArticle");
    Text [] texts = new Text[] {
        txtHomepage, txtLogin, txtPostAddress, txtUsername, txtPassword
    };
    String [] labels = new String[]{
        "Homepage", "Login", "PostAddress", "Username", "Password"
    };
    for(int i = 0; i < labels.length; i++) {
      if(!check(resources, texts[i], labels[i])) return false; 
    }
    
    return true;
  }   
  
  protected boolean check(ClientRM resources, Text text, String label) {
    String value = text.getText().trim();
    if(value.length() == 0){
      String message = resources.getLabel(getClass().getName()+".msgErrorEmpty" + label);
      ClientLog.getInstance().setMessage(shell, new Exception(message));
      return false;
    }
    return true;
  }


}
