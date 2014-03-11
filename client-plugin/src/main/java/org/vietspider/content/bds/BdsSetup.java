/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.bds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.io.LogService;
import org.vietspider.common.util.Worker;
import org.vietspider.model.plugin.bds.XMLBdsConfig;
import org.vietspider.model.plugin.bds.XMLBdsConfig.Category;
import org.vietspider.model.plugin.bds.XMLBdsConfig.Region;
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
public class BdsSetup {

  private Shell shell;

  private Text txtHomepage;
  private Text txtCharset;

  private Button butAlertWhenComplete;
  private Button butAutoSync;
  private Button butUploadImage;
  
  private Text txtLogin;
  private Text txtUsername;
  private Text txtPassword;

//  private Text txtCateURL;
  private Table tblCategories;
  private Table tblRegions; 

  protected TabFolder tab;

  private Button butOk ;

  public BdsSetup(final Shell parent) {
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    ClientRM resources = BdsSyncArticlesPlugin.getResources();
    ApplicationFactory factory = new ApplicationFactory(shell, resources, getClass().getName());
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);
    shell.setLayout(new GridLayout(2, false));

    GridData gridData;
    
    factory.createLabel("lblHomepage");  
    txtHomepage = factory.createText();
    txtHomepage.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtHomepage.setLayoutData(gridData);  
    txtHomepage.setText("http://goodhome.vn/dang-tin-nhanh");

    factory.createLabel("lblCharset");  
    txtCharset = factory.createText();
    txtCharset.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 50;
    txtCharset.setLayoutData(gridData);
    txtCharset.setText("utf-8");
    
    factory.createLabel("lblLogin");  
    txtLogin = factory.createText();
    txtLogin.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtLogin.setLayoutData(gridData);
    txtLogin.setText("http://goodhome.vn/dang-nhap.html");
    
    Composite userComposite = new Composite(shell, SWT.NONE);
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
//    txtUsername.setText("cuong");

    composite = new Composite(userComposite, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    factory.setComposite(composite);

    factory.createLabel("lblPassword");   
    txtPassword = factory.createText(SWT.BORDER | SWT.PASSWORD);
    gridData = new GridData(SWT.BORDER | SWT.PASSWORD);
    gridData.widthHint = 120;
    txtPassword.setLayoutData(gridData);
//    txtPassword.setText("cuong@76");
    Composite optionComposite = new Composite(shell, SWT.NONE);
    GridLayout gridLayout = new GridLayout(4, false);
    optionComposite.setLayout(gridLayout);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    optionComposite.setLayoutData(gridData);
    
    factory.setComposite(optionComposite);
    
    butAutoSync = factory.createButton(SWT.CHECK, "Tự động đăng?");
    butAutoSync.setSelection(true);
    butUploadImage = factory.createButton(SWT.CHECK, "Có ảnh?");
    butAlertWhenComplete = factory.createButton(SWT.CHECK, "Báo khi đăng xong?");
    
    Button button = factory.createButton("butClear", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        detectCategories(false);
      }   
    }); 
    button.setText("Tải danh sách Nhu Cầu Đăng và Nơi Đăng");
    
    factory.setComposite(shell);
    
    tab = new TabFolder(shell, SWT.TOP);
    tab.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_BOTH);     
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalSpan = 3;
    tab.setLayoutData(gridData);
    
    factory.setComposite(tab);

    TabItem tabItem;
    
    tblCategories = factory.createTable("tableData",  null, SWT.FULL_SELECTION | SWT.MULTI);
    gridData = new GridData(GridData.FILL_BOTH);
    tblCategories.setLayoutData(gridData);
    tabItem = new TabItem(tab, SWT.NONE);
    tabItem.setText("Nhu cầu đăng");
    tabItem.setControl(tblCategories);
    
    tblRegions = factory.createTable("tableData",  null, SWT.FULL_SELECTION | SWT.MULTI);
    gridData = new GridData(GridData.FILL_BOTH);
    tblRegions.setLayoutData(gridData);
    tabItem = new TabItem(tab, SWT.NONE);
    tabItem.setText("Nơi đăng");
    tabItem.setControl(tblRegions);
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
        new ShellSetter(BdsSetup.this.getClass(), shell);
      }   
    }); 
    
    factory.createButton("butClose", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ShellSetter(BdsSetup.this.getClass(), shell);
        shell.close();
      }   
    });

    loadConfig();

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    shell.setImage(parent.getImage());
    new ShellGetter(BdsSetup.class, shell, 550, 350, x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
   /*shell.addDisposeListener(new DisposeListener() {
      @Override
      public void widgetDisposed(DisposeEvent arg0) {
        System.exit(0);
      }
    });*/
  }

  private void loadConfig() {
    Worker excutor = new Worker() {

      private String error = null;
      private XMLBdsConfig config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "load.file.by.gzip"),
              new BasicHeader("file", "system/plugin/bds.config")
          };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);

          config = XML2Object.getInstance().toObject(XMLBdsConfig.class, bytes);
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
        txtCharset.setText(config.getCharset());
        butUploadImage.setSelection(config.isUploadImage());
        
        txtLogin.setText(config.getLoginAddress());
        txtUsername.setText(config.getUsername());
        txtPassword.setText(config.getPassword());

        butAlertWhenComplete.setSelection(config.isAlertMessage());
        butAutoSync.setSelection(config.isAuto());
        
        List<Category> categories = new ArrayList<Category>();
        Collections.addAll(categories, config.getCategories());
        setCategoriesData(categories);
        
        List<Region> regions = new ArrayList<Region>();
        Collections.addAll(regions, config.getRegions());
        setRegionData(regions);
      }
    };
    new ThreadExecutor(excutor, txtHomepage).start();
  }

  private void saveConfig() {
    butOk.setEnabled(false);
    Worker excutor = new Worker() {

      private String error = null;
      private XMLBdsConfig config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        config = new XMLBdsConfig();
        config.setHomepage(txtHomepage.getText());
        config.setCharset(txtCharset.getText());
        
        config.setLoginAddress(txtLogin.getText());
        config.setUsername(txtUsername.getText());
        config.setPassword(txtPassword.getText());

        config.setUploadImage(butUploadImage.getSelection());
        config.setAlertMessage(butAlertWhenComplete.getSelection());
        config.setAuto(butAutoSync.getSelection());
        
        Category[] categories = getCategoriesData();
        config.setCategories(categories);
        
        config.setRegions(getRegionsData());
      }

      public void execute() {
        if(config == null) return;
        try {
          Object2XML bean2XML = Object2XML.getInstance();
          String xml = bean2XML.toXMLDocument(config).getTextValue();

          Header [] headers =  new Header[] {
              new BasicHeader("action", "save"),
              new BasicHeader("file", "system/plugin/bds.config")
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
      private String cateURL = null;
      
      private CategoriesDetector detector;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        cateURL = txtHomepage.getText();
        String charset = txtCharset.getText();
        String login = txtLogin.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        detector = new CategoriesDetector(cateURL, 
            login, username, password, charset);
      }

      public void execute() {
        try {
          detector.detect(cateURL);
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
        if(detector.getCategories().size() < 1) {
          Exception exception = new Exception("Warning: Không tìm thấy Nhu Cầu Đăng. Xin hãy thử lại!");
          ClientLog.getInstance().setMessage(shell, exception);
        } else {
          setCategoriesData(detector.getCategories());
        }
        
        if(detector.getRegions().size() < 1) {
          Exception exception = new Exception("Warning: Không tìm thấy Nơi Đăng. Xin hãy thử lại!");
          ClientLog.getInstance().setMessage(shell, exception);
        } else {
          setRegionData(detector.getRegions());
        }
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
      TableItem item = new TableItem(tblCategories, SWT.NONE);
      item.setText(0, categories.get(i).getCategoryId());
      item.setText(1, categories.get(i).getCategoryName());
    }
  }
  
  private void setRegionData(List<Region> regions) {
    tblRegions.removeAll();
    if(regions == null) return;
    for(int i = 0; i < regions.size(); i++) {
      TableItem item = new TableItem(tblRegions, SWT.NONE);
      item.setText(0, regions.get(i).getRegionId());
      item.setText(1, regions.get(i).getRegionName());
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
  
  private Region[] getRegionsData() {
    List<Region> list = new ArrayList<Region>();
    TableItem [] items = tblRegions.getItems();
    for(int i = 0; i < items.length; i++) {
      String id = items[i].getText(0);
      if(id == null || id.trim().isEmpty()) continue;
      String name = items[i].getText(1);
      if(name == null || name.trim().isEmpty()) continue;
      Region region = new Region();
      region.setRegionId(id);
      region.setRegionName(name);
      list.add(region);
    }
    
    return list.toArray(new Region[list.size()]);
  }
  
  private void clear() {
    txtHomepage.setText("");
    txtCharset.setText("utf-8");
    butUploadImage.setSelection(true);
    txtLogin.setText("");
    txtUsername.setText("");
    txtPassword.setText("");
    butAutoSync.setSelection(false);
    butAlertWhenComplete.setSelection(true);
    tblCategories.removeAll();
    tblRegions.removeAll();
  }
  
  public boolean check(){
    Text [] texts = new Text[] {
        txtHomepage, txtLogin,  /*txtUploadImage, txtImagePath,*/
        txtCharset, txtUsername, txtPassword
        
    };
    String [] labels = new String[]{
        "Hãy nhập Trang Đăng Tin", "Hãy nhập Trang Đăng Nhập",  /*"UploadImage", "ImagePath" ,*/ 
        "Charset", "Hãy nhập username", "Hãy nhập Password"
        
    };
    for(int i = 0; i < labels.length; i++) {
      if(!check(texts[i], labels[i])) return false; 
    }
    
    return true;
  }   
  
  private boolean check(Text text, String message) {
    String value = text.getText().trim();
    if(value.length() == 0){
      ClientLog.getInstance().setMessage(shell, new Exception(message));
      return false;
    }
    return true;
  }
  
  public static void main(String[] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    new BdsSetup(shell);
    
    shell.pack ();
    shell.open ();
    while (!shell.isDisposed ()) {
      if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
  }


}
