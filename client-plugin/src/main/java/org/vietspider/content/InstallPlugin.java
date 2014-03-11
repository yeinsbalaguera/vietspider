/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import java.io.File;
import java.util.ListResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.ClientPlugin;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.PluginClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.util.Worker;
import org.vietspider.content.joomla.JoomlaSetup;
import org.vietspider.content.joomla.JoomlaSyncArticlesPlugin;
import org.vietspider.content.nukeviet.NukeVietSetup;
import org.vietspider.content.nukeviet.NukeVietSyncArticlesPlugin;
import org.vietspider.content.vbulletin.VBulletinSetup;
import org.vietspider.content.vbulletin.VBulletinSyncArticlesPlugin;
import org.vietspider.content.wordpress.WordPressSetup;
import org.vietspider.content.wordpress.WordPressSyncArticlesPlugin;
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
public class InstallPlugin extends ClientPlugin {

  private String label;
  
  protected String [] pluginNames = {
      "Joomla", /*"Drupal",*/ "WordPress", "NukeViet", "VBulletin"
  };
  
  protected String [] clientPlugins;
  protected String [] serverPlugins;
  
  protected Button buttons[] = new Button[pluginNames.length];
  
  private static ClientRM clientRM;

  public InstallPlugin() {
    ClientRM resources = getResources();
    label = resources.getLabel(InstallPlugin.class.getName() + ".itemInstallPlugin");
    clientPlugins = new String[]{
        JoomlaSyncArticlesPlugin.class.getName(), 
//        DrupalSyncArticlePlugin.class.getName(), 
        WordPressSyncArticlesPlugin.class.getName(), 
        NukeVietSyncArticlesPlugin.class.getName(), 
        VBulletinSyncArticlesPlugin.class.getName()
    };
    
    serverPlugins = new String[]{
        "org.vietspider.server.plugin.joomla.JoomlaSyncArticlePlugin", 
//        "org.vietspider.server.plugin.drupal.DrupalSyncArticlePlugin", 
        "org.vietspider.server.plugin.wordpress.WordPressSyncArticlePlugin", 
        "org.vietspider.server.plugin.nukeviet.NukeVietSyncArticlePlugin", 
        "org.vietspider.server.plugin.vbulletin.VBulletinSyncArticlePlugin"
    };
  }
  
  private void init(Shell parent) {
   final Shell shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
   ApplicationFactory factory = new ApplicationFactory(shell, clientRM, getClass().getName());
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);
    shell.setLayout(new GridLayout(2, false));

    GridData gridData;
    
    Composite center = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    center.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    center.setLayout(rowLayout);
    rowLayout.justify = true;
    
    factory.setComposite(center);
    
    for(int i = 0; i < pluginNames.length; i++) {
      buttons[i] = new Button(center, SWT.CHECK);
      buttons[i].setFont(UIDATA.FONT_8V);
      buttons[i].setText(pluginNames[i]);
      buttons[i].addSelectionListener(new SelectionAdapter(){
        public void widgetSelected(SelectionEvent evt) {
          Button _button = (Button)evt.getSource();
          if(_button.getSelection()) {
            String key = _button.getText();
            if("NukeViet".equals(key)) {
              new NukeVietSetup(_button.getShell());
            } else if("Joomla".equals(key)) {
              new JoomlaSetup(_button.getShell());
//            } else if("Drupal".equals(key)) {
//              new DrupalSetup(_button.getShell());
            } else if("WordPress".equals(key)) {
              new WordPressSetup(_button.getShell());
            } else if("VBulletin".equals(key)) {
              new VBulletinSetup(_button.getShell());
            }
          }
        }   
      });
    }

    Composite bottom = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    bottom.setLayoutData(gridData);
    rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;

    factory.setComposite(bottom);

    factory.createButton("butOk", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String[] c_plugins = new String[clientPlugins.length];
        String[] s_plugins = new String[serverPlugins.length];
        for(int i = 0; i < buttons.length; i++) {
          if(buttons[i].getSelection()) {
            c_plugins[i] = clientPlugins[i];
            s_plugins[i] = serverPlugins[i];
          } else {
            c_plugins[i] = "#" + clientPlugins[i];
            s_plugins[i] = "#" + serverPlugins[i];
          }
        }
        try {
          saveClient(c_plugins);
        } catch (Exception e) {
          ClientLog.getInstance().setException(shell, e);
        }
        
        try {
          saveServer(shell, s_plugins);
        } catch (Exception e) {
          ClientLog.getInstance().setException(shell, e);
        }
        
        MessageBox messageBox = new MessageBox(shell, SWT.OK);
        messageBox.setMessage("Cần khởi động lại VietSpider!");
        messageBox.open();
        
        new ShellSetter(InstallPlugin.this.getClass(), shell);
        shell.close();
      }   
    }); 
    
    factory.createButton("butClose", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ShellSetter(InstallPlugin.this.getClass(), shell);
        shell.close();
      }   
    });
    shell.addDisposeListener(new DisposeListener() {
      @SuppressWarnings("unused")
      public void widgetDisposed(DisposeEvent arg0) {
        new ShellSetter(InstallPlugin.this.getClass(), shell);
      }
    });
    
    try {
      File file = UtilFile.getFile("client", "plugin.config");
      String text = new String(RWData.getInstance().load(file), Application.CHARSET);
      String[] elements = text.split("\n");
      for(int i = 0; i < elements.length; i++) {
        elements[i] = elements[i].trim();
        if(elements[i].length() < 1) continue;
        for(int j = 0; j < clientPlugins.length; j++) {
          if(elements[i].endsWith(clientPlugins[j])
              || clientPlugins[j].endsWith(elements[i])) {
            if(elements[i].charAt(0) == '#') {
              buttons[j].setSelection(false);
            } else {
              buttons[j].setSelection(true);
            }
            break;
          }
        }
      }
    } catch (Exception e) {
      ClientLog.getInstance().setException(null, e);
    }


    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    shell.setImage(parent.getImage());
    new ShellGetter(InstallPlugin.class, shell, 450, 100, x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
  }
  
  public String getConfirmMessage() { return null; }

  public String getLabel() { return label; }

  @SuppressWarnings("unused")
  public boolean isValidType(int type_) {
    return true;
  }
  
  @Override
  public void invoke(Object... objects) {
    Composite browser = (Composite) objects[1];
    init(browser.getShell());
  }
  
  private void saveClient(String[] plugins) throws Exception {
    File file = UtilFile.getFile("client", "plugin.config");
    String text = new String(RWData.getInstance().load(file), Application.CHARSET);
    String[] elements = text.split("\n");
    for(int i = 0; i < elements.length; i++) {
      elements[i] = elements[i].trim();
    }
    
    for(int j = 0; j < plugins.length; j++) {
      boolean exist = false;
      for(int i = 0; i < elements.length; i++) {
        if(elements[i].length() < 1) continue;
//        System.out.println(elements[i] + "  : "+ plugins[j]);
        if(elements[i].endsWith(plugins[j])
            || plugins[j].endsWith(elements[i])) {
          elements[i] = plugins[j];
          exist = true;
        }
      }
      if(exist) plugins[j] = null; 
    }
    
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < plugins.length; i++) {
      if(plugins[i] == null) continue;
      if(plugins[i].charAt(0) == '#') continue;
      if(builder.length() > 0) builder.append('\n');
      builder.append(plugins[i]);
    }
    
    for(int i = 0; i < elements.length; i++) {
      if(elements[i].length() < 1) continue;
      if(builder.length() > 0) builder.append('\n');
      builder.append(elements[i]);
    }
    
    RWData.getInstance().save(file, builder.toString().getBytes(Application.CHARSET));
  }
  
  private void saveServer(Shell shell, String[] plugins) throws Exception {
    final StringBuilder builder = new StringBuilder();
    for(int i = 0; i < plugins.length; i++) {
      if(builder.length() > 0) builder.append('\n');
      builder.append(plugins[i]);
    }
    
    Worker excutor = new Worker() {
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {}

      public void execute() {
        try {
          PluginClientHandler handler = new PluginClientHandler();
          handler.send("install.sync.plugin", "save", builder.toString());
        } catch (Exception e) {
        }
      }

      public void after() {}
    };
    new ThreadExecutor(excutor, shell).start();
  }

  static synchronized ClientRM getResources() {
    if(clientRM != null) return clientRM;

    final String _package = "org.vietspider.content.";


    clientRM = new ClientRM(new ListResourceBundle() {
      protected Object[][] getContents() {

        String setupModule = _package + "InstallPlugin.";
        return new String[][] {
            
            {setupModule + "itemInstallPlugin", "Cài đặt plugin đồng bộ"},
            
            {setupModule + "title", "Cài đặt plugin đồng bộ"},

           
            {setupModule + "butOk","Lưu"},
            {setupModule + "butOkTip",""},
            {setupModule + "butClose","Đóng"},
            {setupModule + "butCloseTip",""}
            
        };
      }
    });

    return clientRM;
  }


}
