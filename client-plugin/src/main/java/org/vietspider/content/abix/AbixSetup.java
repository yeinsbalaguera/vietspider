/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.abix;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.chars.URLEncoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.PluginClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.util.Worker;
import org.vietspider.content.abix.DrupalCategoriesConfig.DrupalCategory;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;
import org.vietspider.net.server.URLPath;
import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.parser.xml.XMLNode;
import org.vietspider.parser.xml.XMLParser;
import org.vietspider.serialize.Object2XML;
import org.vietspider.ui.services.ClientLog;
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
class AbixSetup {
  
  private Shell shell;
  
  private Text txtHomepage;
  private Text txtPostAddress;
  private Text txtCharset;
  private Text txtCategoriesConfig;
  
  private Button butOk ;
  
  AbixSetup(Shell parent) {
    shell = new Shell(parent, SWT.RESIZE | SWT.CLOSE | SWT.APPLICATION_MODAL);
    ApplicationFactory factory = new ApplicationFactory(shell, "Abix", getClass().getName());
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);
    shell.setLayout(new GridLayout(2, false));
    
    GridData gridData;
    
    factory.createLabel("lblHomepage");  
    txtHomepage = factory.createText();
    txtHomepage.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtHomepage.setLayoutData(gridData);  
    
    factory.createLabel("lblPostAddress");  
    txtPostAddress = factory.createText();
    txtPostAddress.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtPostAddress.setLayoutData(gridData); 
    
    factory.createLabel("lblCharset");  
    txtCharset = factory.createText();
    txtCharset.setFont(UIDATA.FONT_10);
    gridData = new GridData();
    gridData.widthHint = 50;
    txtCharset.setLayoutData(gridData);
    
    factory.createLabel("lblCategoriesConfig");  
    txtCategoriesConfig = factory.createText();
    txtCategoriesConfig.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtCategoriesConfig.setLayoutData(gridData);
    try {
      Preferences prefs = Preferences.userNodeForPackage(AbixSetup.class);
      String url = prefs.get("category.url", "");
      if(url != null && !url.trim().isEmpty()) {
        txtCategoriesConfig.setText(url);
      } else {
        txtCategoriesConfig.setText("http://www.eyesonvietnam.com/categories.xml");
      }
      
    } catch (Exception e) {
      txtCategoriesConfig.setText("http://www.eyesonvietnam.com/categories.xml");
    }
    
    Composite bottom = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;
    
    factory.setComposite(bottom);
    
    butOk = factory.createButton("butOk", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        saveConfig() ;
      }   
    }); 
    
   factory.createButton("butClose", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ShellSetter(AbixSetup.class, shell);
        shell.close();
      }   
    });
    
    loadConfig();
    
    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    shell.setImage(parent.getImage());
    new ShellGetter(AbixSetup.class, shell, 550, 150, x, y);
    shell.open();
  }
  
  private void loadConfig() {
    Worker excutor = new Worker() {

      private String error = null;
      private String value = "";

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          PluginClientHandler handler = new PluginClientHandler();
          value = handler.send("abix.sync.article.to.drupal.plugin", "load.config", value);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        if(value == null || value.trim().isEmpty()) return;
        String [] elements = value.split("\n");
        if(elements.length > 0) txtHomepage.setText(elements[0]);
        if(elements.length > 1) txtPostAddress.setText(elements[1]);
        if(elements.length > 2) txtCharset.setText(elements[2]);
      }
    };
    new ThreadExecutor(excutor, txtHomepage).start();
  }
  
  private void saveConfig() {
    butOk.setEnabled(false);
    Worker excutor = new Worker() {

      private String error = null;
      private StringBuilder builder = new StringBuilder();

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        builder.append(txtHomepage.getText()).append('\n');
        builder.append(txtPostAddress.getText()).append('\n');
        builder.append(txtCharset.getText());
      }

      public void execute() {
        try {
          PluginClientHandler handler = new PluginClientHandler();
          error = handler.send("abix.sync.article.to.drupal.plugin", "save.config", builder.toString());
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        loadCategoriesConfig();
      }
    };
    new ThreadExecutor(excutor, txtHomepage).start();
  }
  
  private void loadCategoriesConfig() {
    final String url  = txtCategoriesConfig.getText();
    if(url == null || url.trim().isEmpty()) return;
    
    try {
      Preferences prefs = Preferences.userNodeForPackage(AbixSetup.class);
      prefs.put("category.url", url);
    } catch (Exception e) {
    }
    
    Worker excutor = new Worker() {

      private String error = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          String xml = loadContent(url);
          XMLDocument document = XMLParser.createDocument(xml, null);
          
          XMLNode xmlNode = document.getRoot().getChild(0).getChild(0);
          
          List<XMLNode> children  = xmlNode.getChildren();
          List<DrupalCategory> categories = new ArrayList<DrupalCategory>();
          
          for(int i = 0; i < children.size(); i++) {
            List<XMLNode> element = children.get(i).getChild(0).getChildren();
            if(element.size() < 2) continue;
            try {
              DrupalCategory category = new DrupalCategory();
              category.setId(element.get(0).getChild(0).getTextValue());
              category.setName(element.get(1).getChild(0).getTextValue());
              categories.add(category);
            } catch (Exception e) {
              ClientLog.getInstance().setException(null, e);
            }
          }
          
          if(categories.size() < 1) return;
          
          DrupalCategoriesConfig categoriesConfig = new DrupalCategoriesConfig();
          categoriesConfig.setCategories(categories.toArray(new DrupalCategory[categories.size()]));
          
          xml = Object2XML.getInstance().toXMLDocument(categoriesConfig).getTextValue();
          
          Header [] headers = new Header[] {
              new BasicHeader("action", "save"),
              new BasicHeader("file", "system/plugin/abix/categories.xml")
          };
          byte [] bytes = xml.getBytes(Application.CHARSET);
//          String path  = "D:\\java\\headvances\\core\\trunk\\vietspider\\startup\\src\\test\\data\\client\\logs\\";
//          org.vietspider.common.io.RWData.getInstance().save(new File(path, "a.txt"), bytes);
          ClientConnector2 connector = ClientConnector2.currentInstance();
          connector.post(URLPath.FILE_HANDLER, bytes, headers);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        butOk.setEnabled(true);
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        new ShellSetter(AbixSetup.class, shell);
        shell.close();
      }
    };
    new ThreadExecutor(excutor, txtHomepage).start();
  }
  
  public String loadContent(String address) throws Exception {
    try{
      WebClient webClient = new WebClient();
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      webClient.setURL(address, new URL(address));
      
      HttpMethodHandler methodHandler = new HttpMethodHandler(webClient);
    
      methodHandler.execute(address, "");
      byte [] bytes = methodHandler.readBody();
      
      return new String(bytes, Application.CHARSET);
    } catch(Exception exp){
      exp.printStackTrace();
      return null;
    } finally {
      
    }
  }
  
}
