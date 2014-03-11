/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.vbulletin;

import java.io.File;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.chars.refs.RefsEncoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.io.LicenseVerifier;
import org.vietspider.common.io.LogService;
import org.vietspider.common.util.Worker;
import org.vietspider.model.plugin.Category;
import org.vietspider.model.plugin.vbulletin.XMLVBulletinConfig;
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
 * login vbulletin admin:admin123  
 */
public class VBulletinSetup {

  private Shell shell;

  private Text txtHomepage;
  private Text txtPostAddress;
  private Text txtCharset;
  private Text txtLogin;
//  private Text txtUploadImage;
//  private Text txtImagePath;
  private Text txtTextStyle;
//  private Text txtMetaImageWidth;

  private Text txtUsername;
  private Text txtPassword;

//  private Button butPublished;
//  private Button butFrontpage;
  private Button butAlertWhenComplete;
  private Button butAutoSync;
  
  private Text txtLinkToSource;

//  private Text txtCateURL;
//  private Table tblCategories;
  private Tree tblCategories; 

  protected TabFolder tab;

  private Button butOk ;

  public VBulletinSetup(Shell parent) {
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    
    ApplicationFactory factory = new ApplicationFactory(shell,
        VBulletinSyncArticlesPlugin.getResources(), getClass().getName());
    
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

    factory.createLabel("lblCharset");  
    txtCharset = factory.createText();
    txtCharset.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 50;
    txtCharset.setLayoutData(gridData);
    txtCharset.setText("utf-8");

    factory.createLabel("lblPostAddress");  
    txtPostAddress = factory.createText();
    txtPostAddress.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtPostAddress.setLayoutData(gridData); 

//    factory.createLabel("lblUploadImage");  
//    txtUploadImage = factory.createText();
//    txtUploadImage.setFont(UIDATA.FONT_10);
//    gridData = new GridData(GridData.FILL_HORIZONTAL);   
//    txtUploadImage.setLayoutData(gridData); 

//    factory.createLabel("lblImagePath");  
//    txtImagePath = factory.createText();
//    txtImagePath.setFont(UIDATA.FONT_10);
//    gridData = new GridData(GridData.FILL_HORIZONTAL);   
//    txtImagePath.setLayoutData(gridData); 

    Composite optionComposite = new Composite(tab, SWT.NONE);
    GridLayout gridLayout = new GridLayout(2, false);
    optionComposite.setLayout(gridLayout);

    tabItem = new TabItem(tab, SWT.NONE);
    tabItem.setText("Option Config");
    tabItem.setControl(optionComposite);

    factory.setComposite(optionComposite);

    gridLayout = new GridLayout(2, false);

//    Group groupPublish = factory.createGroup("", new GridData(), gridLayout);
//    factory.setComposite(groupPublish);
//
//    butPublished = factory.createButton(SWT.CHECK, factory.getLabel("butPublished"));
//    butPublished.setSelection(true);
//
//    butFrontpage = factory.createButton(SWT.CHECK, factory.getLabel("butFrontpage"));
//    butFrontpage.setSelection(true);

    factory.setComposite(optionComposite);
    Group groupAutoSync = factory.createGroup("", new GridData(), gridLayout);
    factory.setComposite(groupAutoSync);

    butAutoSync = factory.createButton(SWT.CHECK, factory.getLabel("butAutoSync"));
    butAutoSync.setSelection(true);
    File licenseFile = LicenseVerifier.loadLicenseFile();
    butAutoSync.setEnabled(LicenseVerifier.verify("vbulletin", licenseFile));
  
    factory.setComposite(optionComposite);

    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    
    Group groupComplete = factory.createGroup("", gridData, gridLayout);
    factory.setComposite(groupComplete);
    
//    factory.createLabel("lblMetaImageWidth"); 
//    txtMetaImageWidth = factory.createText();
//    txtMetaImageWidth.setFont(UIDATA.FONT_10);
//    gridData = new GridData();
//    gridData.widthHint = 40;
//    txtMetaImageWidth.setLayoutData(gridData);
    
    factory.createLabel("lblTextStyle");  
    txtTextStyle = factory.createText();
    txtTextStyle.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtTextStyle.setLayoutData(gridData);
    
    factory.createLabel("lblLinkToSource");   
    txtLinkToSource = factory.createText(SWT.BORDER);
    txtLinkToSource.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtLinkToSource.setLayoutData(gridData);
    
    butAlertWhenComplete = factory.createButton(SWT.CHECK, factory.getLabel("butAlertWhenComplete"));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    butAlertWhenComplete.setLayoutData(gridData);
    butAlertWhenComplete.setSelection(true);

    Composite userComposite = new Composite(optionComposite, SWT.NONE);
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

    Composite detectorComposite = new Composite(cateComposite, SWT.NONE);
    detectorComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    detectorComposite.setLayoutData(gridData);
    factory.setComposite(detectorComposite);

    Label lbl = factory.createLabel("");   
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    lbl.setLayoutData(gridData);
//    txtCateURL = factory.createText(SWT.BORDER);
//    txtCateURL.setLayoutData(gridData);
    factory.createButton("butCategoriesDetector", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        detectCategories(false);
      }   
    }); 

    factory.setComposite(cateComposite);
    
    tblCategories = factory.createTree(cateComposite,  SWT.BORDER);
    gridData = new GridData(GridData.FILL_BOTH);
    tblCategories.setLayoutData(gridData);

//    tblCategories = factory.createTable("tableData",  null, SWT.FULL_SELECTION | SWT.MULTI);
//    gridData = new GridData(GridData.FILL_BOTH);
//    tblCategories.setLayoutData(gridData);
//    Menu menu = new Menu (shell, SWT.POP_UP);
//    tblCategories.setMenu (menu);
//    MenuItem item = new MenuItem (menu, SWT.PUSH);
//    item.setText("Add New Category");
//    item.addListener (SWT.Selection, new Listener () {
//      @SuppressWarnings("unused")
//      public void handleEvent (Event event) {
//        createNewItem();
//      }
//    });
//    item = new MenuItem (menu, SWT.PUSH);
//    item.setText("Delete Selection");
//    item.addListener (SWT.Selection, new Listener () {
//      @SuppressWarnings("unused")
//      public void handleEvent (Event event) {
//        tblCategories.remove(tblCategories.getSelectionIndices());
//      }
//    });
//    createNewItem();
//    tblCategories.addListener (SWT.MouseDown, new TableEditorListener(tblCategories));

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
        if(getCategoriesData().size() < 1) {
          detectCategories(true);
        } else {
          saveConfig() ;
        }
        new ShellSetter(VBulletinSetup.this.getClass(), shell);
      }   
    }); 
    
    factory.createButton("butClose", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ShellSetter(VBulletinSetup.this.getClass(), shell);
        shell.close();
      }   
    });

    loadConfig();

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    shell.setImage(parent.getImage());
    new ShellGetter(VBulletinSetup.class, shell, 550, 350, x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
  }

  private void loadConfig() {
    Worker excutor = new Worker() {

      private String error = null;
      private XMLVBulletinConfig config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "load.file.by.gzip"),
              new BasicHeader("file", "system/plugin/vbulletin.config")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);

          config = XML2Object.getInstance().toObject(XMLVBulletinConfig.class, bytes);
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
        txtCharset.setText(config.getCharset());
        txtPostAddress.setText(config.getPostAddress());
//        txtUploadImage.setText(config.getImageUploadAddress());
//        txtImagePath.setText(config.getImagePath());
        if(config.getTextStyle() != null) {
          txtTextStyle.setText(config.getTextStyle());
        }
//        txtMetaImageWidth.setText(config.getMetaImageWidth());

        txtUsername.setText(config.getUsername());
        txtPassword.setText(config.getPassword());

//        butPublished.setSelection(config.isPublished());
//        butFrontpage.setSelection(config.isFrontpage());
        butAlertWhenComplete.setSelection(config.isAlertMessage());
        butAutoSync.setSelection(config.isAuto());
        
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

  private void saveConfig() {
    butOk.setEnabled(false);
    Worker excutor = new Worker() {

      private String error = null;
      private XMLVBulletinConfig config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        config = new XMLVBulletinConfig();
        config.setHomepage(txtHomepage.getText());
        config.setLoginAddress(txtLogin.getText());
        config.setCharset(txtCharset.getText());
        config.setPostAddress(txtPostAddress.getText());
//        config.setImageUploadAddress(txtUploadImage.getText());
//        config.setImagePath(txtImagePath.getText());
        config.setTextStyle(txtTextStyle.getText());
        
//        config.setMetaImageWidth(txtMetaImageWidth.getText());

        config.setUsername(txtUsername.getText());
        config.setPassword(txtPassword.getText());

//        config.setPublished(butPublished.getSelection());
//        config.setFrontpage(butFrontpage.getSelection());
        config.setAlertMessage(butAlertWhenComplete.getSelection());
        config.setAuto(butAutoSync.getSelection());
        
        RefsEncoder encoder = new RefsEncoder();
        String text = txtLinkToSource.getText();
        text = new String(encoder.encode(text.toCharArray()));
        config.setLinkToSource(text);

        List<Category> categories = getCategoriesData();
        config.setCategories(categories);
      }

      public void execute() {
        if(config == null) return;
        try {
          Object2XML bean2XML = Object2XML.getInstance();
          String xml = bean2XML.toXMLDocument(config).getTextValue();


          Header [] headers =  new Header[] {
              new BasicHeader("action", "save"),
              new BasicHeader("file", "system/plugin/vbulletin.config")
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

  private void detectCategories(final boolean save) {
    if(!check()) return;
    
    Worker excutor = new Worker() {

      private String error = null;
//      private String cateURL = null;
      
      private CategoriesDetector detector;
      private String homepage;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        homepage = txtHomepage.getText();
        String login = txtLogin.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String charset = txtCharset.getText();
        detector = new CategoriesDetector(homepage, login, username, password, charset);
      }

      public void execute() {
        try {
          detector.detect(homepage);
        } catch (Exception e) {
          LogService.getInstance().setThrowable(e);
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        List<Category> categories = detector.getCategories();
        if(categories.size() < 1) {
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
  
  private void setCategoriesData(List<Category> categories) {
    tblCategories.removeAll();
    if(categories == null) return;
    for(int i = 0; i < categories.size(); i++) {
      TreeItem item = new TreeItem(tblCategories, SWT.NONE);
      Category category = categories.get(i);
      item.setText(category.getCategoryId()+"/"+category.getCategoryName());
      createItem(item, category.getSubCategories());
    }
  }
  
  private void createItem(TreeItem parent, List<Category> categories) {
    if(categories == null) return;
    for(int i = 0; i < categories.size(); i++) {
      TreeItem item = new TreeItem(parent, SWT.NONE);
      Category category = categories.get(i);
      item.setText(category.getCategoryId()+"/"+category.getCategoryName());
      createItem(item, category.getSubCategories());
    }
  }
  
  private List<Category> getCategoriesData() {
    return items2Categories(tblCategories.getItems());
  }
  
  private List<Category> items2Categories(TreeItem[] items) {
    List<Category> list = new ArrayList<Category>();
    for(int i = 0; i < items.length; i++) {
      list.add(item2Category(items[i]));
    }
    return list;
  }
  
  private Category item2Category(TreeItem item) {
    String text = item.getText();
    int index = text.indexOf('/');
    String id = text.substring(0, index);
    String name  = text.substring(index+1);
    Category category = new Category();
    category.setCategoryId(id);
    category.setCategoryName(name);
    TreeItem [] _items  = item.getItems();
    if(_items != null && _items.length > 0) {
      category.setSubCategories(items2Categories(_items));
    }
    return category;
  }
  
  private void autoComplete() {
    String url =  txtHomepage.getText();
    if((url = url.trim()).isEmpty()) return;
    
    int idx = url.lastIndexOf("/index.php");
    if(idx > 0) url = url.substring(0, idx);
    if(url.charAt(url.length()-1) == '/') {
      url = url.substring(0, url.length()-1);
    }
    
    if(txtLogin.getText().trim().isEmpty()) {
      txtLogin.setText(url+"/"); 
    }
    
    if(txtPostAddress.getText().trim().isEmpty()) {
      txtPostAddress.setText(url +"/newthread.php?do=newthread&f="); 
    }
    
//    if(txtUploadImage.getText().trim().isEmpty()) {
//      txtUploadImage.setText(url +"/administrator/index.php?option=com_media&folder=stories"); 
//    }
    
//    if(txtImagePath.getText().trim().isEmpty()) {
//      txtImagePath.setText("images/"); 
//    }
    
    if(txtUsername.getText().trim().isEmpty()) {
      txtUsername.setText("admin"); 
    }
//    if(txtCateURL.getText().trim().isEmpty()) {
//      txtCateURL.setText(url +"/administrator/index.php?option=com_categories&section=com_content"); 
//    }
    
  }
  
  private void clear() {
    txtHomepage.setText("");
    txtLogin.setText("");
    txtCharset.setText("utf-8");
    txtPostAddress.setText("");
//    txtUploadImage.setText("");
//    txtImagePath.setText("images/");
    txtTextStyle.setText("align=\"justify\"");
    
    txtUsername.setText("");
    txtPassword.setText("");
    
//    txtCateURL.setText("");
    
    txtLinkToSource.setText("<p align=\"right\">Theo<a href=\"$source.link\">$source.name</a></p>");
    
    butAutoSync.setSelection(false);
//    butPublished.setSelection(true);
//    butFrontpage.setSelection(true);
    butAlertWhenComplete.setSelection(true);
    
    tblCategories.removeAll();
  }
  
  public boolean check(){
    ClientRM resources = VBulletinSyncArticlesPlugin.getResources();
    Text [] texts = new Text[] {
        txtHomepage, txtLogin, txtPostAddress,/* txtUploadImage, */
        txtCharset, txtUsername, txtPassword
        
    };
    String [] labels = new String[]{
        "Homepage", "Login", "PostAddress", /*"UploadImage", */
        "Charset", "Username", "Password"
        
    };
    for(int i = 0; i < labels.length; i++) {
      if(!check(resources, texts[i], labels[i])) return false; 
    }
    
    return true;
  }   
  
  private boolean check(ClientRM resources, Text text, String label) {
    String value = text.getText().trim();
    if(value.length() == 0){
      String message = resources.getLabel(getClass().getName()+".msgErrorEmpty" + label);
      ClientLog.getInstance().setMessage(shell, new Exception(message));
      return false;
    }
    return true;
  }


}
