/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.wordpress;

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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
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
import org.vietspider.common.io.LicenseVerifier;
import org.vietspider.common.util.Worker;
import org.vietspider.content.cms.sync.TableEditorListener;
import org.vietspider.model.plugin.wordpress.XMLWordPressConfig;
import org.vietspider.model.plugin.wordpress.XMLWordPressConfig.Category;
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
public class WordPressSetup {

  private Shell shell;

  private Text txtHomepage;
  private Text txtPostAddress;
  private Text txtCharset;
  private Text txtLogin;
  private Text txtUploadImage;
  private Text txtPostImageURL;
  private Text txtImageStartURL;
//  private Text txtTextStyle;
//  private Text txtMetaImageWidth;

  private Text txtUsername;
  private Text txtPassword;

  private Button butPublished;
  private Button butUploadImage;
  private Button butAlertWhenComplete;
  private Button butAutoSync;
  
//  private Combo cboImagePosition;
  
  private Text txtLinkToSource;

//  private Text txtCateURL;
  private Table tblCategories; 

  protected TabFolder tab;

  private Button butOk ;

  public WordPressSetup(Shell parent) {
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    ClientRM clientRM = WordPressSyncArticlesPlugin.getResources();
    ApplicationFactory factory = new ApplicationFactory(shell, clientRM, getClass().getName());
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
    gridData = new GridData();
    gridData.widthHint = 250;
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

    factory.createLabel("lblPostAddress");  
    txtPostAddress = factory.createText();
    txtPostAddress.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtPostAddress.setLayoutData(gridData); 
    
    factory.createLabel("lblUploadImage"); 
    butUploadImage = factory.createButton(SWT.CHECK, factory.getLabel("butUploadImage"));
    butUploadImage.setSelection(true);
    butUploadImage.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        txtUploadImage.setEditable(butUploadImage.getSelection());
      }
    });
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    butUploadImage.setLayoutData(gridData); 

    factory.createLabel("lblUploadImageURL");  
    txtUploadImage = factory.createText();
    txtUploadImage.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtUploadImage.setLayoutData(gridData); 
    
    factory.createLabel("lblPostImageURL");  
    txtPostImageURL = factory.createText();
    txtPostImageURL.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtPostImageURL.setLayoutData(gridData); 
    
    factory.createLabel("lblImageStartURL");  
    txtImageStartURL = factory.createText();
    txtImageStartURL.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtImageStartURL.setLayoutData(gridData); 
//    cboImagePosition = factory.createCombo(SWT.BORDER | SWT.READ_ONLY);
//    cboImagePosition.setFont(UIDATA.FONT_10);
//    gridData = new GridData();
//    gridData.widthHint = 250;
//    cboImagePosition.setLayoutData(gridData); 
//    cboImagePosition.setItems(new String[]{
//        "Không hiển thị",
//        "Hiển thị theo cấu hình module",
//        "Hiển thị dưới phần mở đầu"
//    });

    Composite optionComposite = new Composite(tab, SWT.NONE);
    GridLayout gridLayout = new GridLayout(2, false);
    optionComposite.setLayout(gridLayout);

    tabItem = new TabItem(tab, SWT.NONE);
    tabItem.setText("Option Config");
    tabItem.setControl(optionComposite);

    factory.setComposite(optionComposite);

    gridLayout = new GridLayout(2, false);

    Group groupPublish = factory.createGroup("", new GridData(), gridLayout);
    factory.setComposite(groupPublish);

    butPublished = factory.createButton(SWT.CHECK, factory.getLabel("butPublished"));
    butPublished.setSelection(true);

    factory.setComposite(optionComposite);
    Group groupAutoSync = factory.createGroup("", new GridData(), gridLayout);
    factory.setComposite(groupAutoSync);

    butAutoSync = factory.createButton(SWT.CHECK, factory.getLabel("butAutoSync"));
    butAutoSync.setSelection(true);
    File licenseFile = LicenseVerifier.loadLicenseFile();
    butAutoSync.setEnabled(LicenseVerifier.verify("wordpress", licenseFile));
  
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
//    
//    factory.createLabel("lblTextStyle");  
//    txtTextStyle = factory.createText();
//    txtTextStyle.setFont(UIDATA.FONT_10);
//    gridData = new GridData(GridData.FILL_HORIZONTAL);   
//    txtTextStyle.setLayoutData(gridData);
    
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
    detectorComposite.setLayout(new GridLayout(3, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    detectorComposite.setLayoutData(gridData);
    factory.setComposite(detectorComposite);

//    factory.createLabel("lblCateURL");   
//    txtCateURL = factory.createText(SWT.BORDER);
//    gridData = new GridData(GridData.FILL_HORIZONTAL);
//    txtCateURL.setLayoutData(gridData);
    factory.createButton("butCategoriesDetector", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        detectCategories(false);
      }   
    }); 
//    txtCateURL.setText("http://localhost/joomla/administrator/index.php?option=com_categories&section=com_content");

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

    createNewItem();
    tblCategories.addListener (SWT.MouseDown, new TableEditorListener(tblCategories));

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
        new ShellSetter(WordPressSetup.this.getClass(), shell);
      }   
    }); 
    
    factory.createButton("butClose", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ShellSetter(WordPressSetup.this.getClass(), shell);
        shell.close();
      }   
    });

    loadConfig();

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    shell.setImage(parent.getImage());
    new ShellGetter(WordPressSetup.class, shell, 550, 350, x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
  }

  private void loadConfig() {
    Worker excutor = new Worker() {

      private String error = null;
      private XMLWordPressConfig config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "load.file.by.gzip"),
              new BasicHeader("file", "system/plugin/wordpress.config")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);

          config = XML2Object.getInstance().toObject(XMLWordPressConfig.class, bytes);
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
        butUploadImage.setSelection(config.isUploadImage());
        txtUploadImage.setText(config.getImageUploadAddress());
        txtPostImageURL.setText(config.getPostImageURL());
        txtImageStartURL.setText(config.getImageStartURL());
        
//        try {
//          cboImagePosition.select(Integer.parseInt(config.getImagePosition().trim()));
//        } catch (Exception e) {
//        }
//        txtTextStyle.setText(config.getTextStyle());
//        txtMetaImageWidth.setText(config.getMetaImageWidth());

        txtUsername.setText(config.getUsername());
        txtPassword.setText(config.getPassword());

        butPublished.setSelection(config.isPublished());
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

  private void saveConfig() {
    butOk.setEnabled(false);
    Worker excutor = new Worker() {

      private String error = null;
      private XMLWordPressConfig config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        config = new XMLWordPressConfig();
        config.setHomepage(txtHomepage.getText());
        config.setLoginAddress(txtLogin.getText());
        config.setCharset(txtCharset.getText());
        config.setPostAddress(txtPostAddress.getText());
        config.setImageUploadAddress(txtUploadImage.getText());
        config.setUploadImage(butUploadImage.getSelection());
        config.setPostImageURL(txtPostImageURL.getText());
        config.setImageStartURL(txtImageStartURL.getText());
        
//        config.setImagePosition(String.valueOf(cboImagePosition.getSelectionIndex()));
        
//        config.setMetaImageWidth(txtMetaImageWidth.getText());

        config.setUsername(txtUsername.getText());
        config.setPassword(txtPassword.getText());

        config.setPublished(butPublished.getSelection());
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
              new BasicHeader("file", "system/plugin/wordpress.config")
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

  private void createNewItem() {
    TableItem tableItem = new TableItem(tblCategories, SWT.NONE);
    tableItem.setText("");
    tableItem.setText("");
  }
  
  private void detectCategories(final boolean save) {
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
        homepage = homepage.trim();
        if(homepage.length() > 0 
            && homepage.charAt(homepage.length()-1) == '/') {
          homepage = homepage.substring(0, homepage.length()-1);
          txtHomepage.setText(homepage);
        }
        String login = txtLogin.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String charset = txtCharset.getText();
        this.cateURL = txtPostAddress.getText();
//        if(cateURL.trim().isEmpty()) {
//          cateURL =  txtPostAddress.getText();
//          txtCateURL.setText(cateURL);
//        }
        detector = new CategoriesDetector(homepage, login, username, password, charset);
      }

      public void execute() {
        try {
          detector.detect(cateURL);
        } catch (Exception e) {
          e.printStackTrace();
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
//          return;
        }
        List<Category> list = detector.getCategories();
        Category [] categories = list.toArray(new Category[list.size()]);
        if(categories.length < 1) {
          Exception exception = new Exception("Warning: No categories from server. Try again.");
          ClientLog.getInstance().setMessage(shell, exception);
//          return;
        }
        setCategoriesData(categories);
        if(save) saveConfig();
      }
    };
    
    WaitLoading loading = new WaitLoading(txtHomepage, excutor);
    loading.open();
  }
  
  private void setCategoriesData(Category [] categories) {
    tblCategories.removeAll();
    if(categories == null) return;
    for(int i = 0; i < categories.length; i++) {
      TableItem item = new TableItem(tblCategories, SWT.NONE);
      item.setText(0, categories[i].getCategoryId());
      item.setText(1, categories[i].getCategoryName());
    }
  }
  
  private Category[] getCategoriesData() {
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
  
  private void autoComplete() {
    String url =  txtHomepage.getText();
    if((url = url.trim()).isEmpty()) return;
    
    int idx = url.lastIndexOf("/index.php");
    if(idx > 0) url = url.substring(0, idx);
    if(url.charAt(url.length()-1) == '/') {
      url = url.substring(0, url.length()-1);
      txtHomepage.setText(url);
    }
    
    idx = url.indexOf("/wp-admin");
    if(idx < 0) {
      txtHomepage.setText(url + "/wp-admin");
    }
    
    idx = url.indexOf("/wp-admin");
    if(idx < 0) {
      idx = url.indexOf("/wp-admin/");
      if(idx > 0) {
        url = url.substring(0, url.length()-1);
        txtHomepage.setText(url);
      }
    }
    if(idx > 0) url = url.substring(0, idx);
    
    
    if(txtLogin.getText().trim().isEmpty()) {
      txtLogin.setText(url +"/wp-login.php"); 
    }
    if(txtPostAddress.getText().trim().isEmpty()) {
      txtPostAddress.setText(url +"/wp-admin/post-new.php"); 
    }
    if(txtUploadImage.getText().trim().isEmpty()) {
      txtUploadImage.setText(url +"/wp-admin/media-upload.php?type=image&tab=type&post_id="); 
    }
    
    if(txtPostImageURL.getText().trim().isEmpty()) {
      txtPostImageURL.setText("/wordpress/wp-admin/async-upload.php"); 
    }
    
    if(txtImageStartURL.getText().trim().isEmpty()) {
      txtImageStartURL.setText(url + "/wp-content/uploads/"); 
    }
    
    if(txtUsername.getText().trim().isEmpty()) {
      txtUsername.setText("admin"); 
    }
  }
  
  private void clear() {
    txtHomepage.setText("");
    txtLogin.setText("");
    txtCharset.setText("utf-8");
    txtPostAddress.setText("");
    txtUploadImage.setText("");
//    txtTextStyle.setText("align=\"justify\"");
    
    txtUsername.setText("");
    txtPassword.setText("");
    txtPostImageURL.setText("");
    txtImageStartURL.setText("");
    
    
//    txtCateURL.setText("");
    
    txtLinkToSource.setText("<p align=\"right\">Theo<a href=\"$source.link\">$source.name</a></p>");
    
    butAutoSync.setSelection(false);
    butPublished.setSelection(true);
    butUploadImage.setSelection(true);
    butAlertWhenComplete.setSelection(true);
    
    tblCategories.removeAll();
  }
  
  public boolean check(){
    ClientRM resources = WordPressSyncArticlesPlugin.getResources();
    Text [] texts = new Text[] {
        txtHomepage, txtLogin, txtPostAddress, /*txtUploadImage,*/ /*txtImagePath,*/
        txtCharset, txtUsername, txtPassword
        
    };
    String [] labels = new String[]{
        "Homepage", "Login", "PostAddress", /*"UploadImage",*/ /*"ImagePath" ,*/ 
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
