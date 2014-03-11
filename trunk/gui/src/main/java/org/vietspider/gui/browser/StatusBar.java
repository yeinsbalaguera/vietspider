/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.browser;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.vietspider.client.ClientPlugin;
import org.vietspider.client.PluginLoader;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.gui.module.ToolWindow;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.LiveSashForm;
import org.vietspider.ui.widget.UIDATA;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 7, 2008  
 */
public class StatusBar extends Composite {

  protected  StatusControlPanel controlPanel;
  protected Label lblStatus;
  private   Label lbl;
  private   Label lbl2; 
  protected ProgressBar progressBar;
  protected Composite progressComposite;

  protected Composite tool;
  protected ClientPlugin [] plugins;

  //  protected ImageCombo combox;
  protected Object morePlugins;
  protected List<ClientPlugin> boxPlugins;

  protected Hyperlink [] items;
  protected List<ClientPlugin> hyperPlugins;

  protected int mode = 0;

  protected Object component;

  protected LiveSashForm liveSashForm;

  protected Workspace workspace; 
  private ToolWindow toolWindow;

  public StatusBar(Workspace workspace, Composite parent, boolean createPlugin) {
    this(workspace, parent, SWT.NONE, createPlugin);
  }

  public StatusBar(Workspace _workspace, Composite parent, int style) {
    this(_workspace, parent, style, true);
  }


  public StatusBar(Workspace _workspace, Composite parent, int style, boolean createPlugin) {
    super(parent, style);

    this.workspace = _workspace;

    boxPlugins = new ArrayList<ClientPlugin>();
    hyperPlugins = new ArrayList<ClientPlugin>(3);

    GridLayout gridLayout = new GridLayout(3, false);
    gridLayout.marginHeight = 1;
    gridLayout.horizontalSpacing = 1;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 2;
    setLayout(gridLayout); 

    //    controlPanel = new StatusControlPanel(workspace, this);
    //    GridData gridData = new GridData();
    //    gridData.widthHint= 150;
    //    gridData.heightHint = 25;
    //    controlPanel.setLayoutData(gridData);

    ApplicationFactory factory = new ApplicationFactory(this, "VietSpider", getClass().getName());
    Button startTool = new Button(this, SWT.PUSH);
    startTool.setImage(factory.loadImage("status.folder.png"));
    startTool.setText(factory.getRawLabel("status.start"));
    startTool.setFont(UIDATA.FONT_8TB);
    startTool.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
    startTool.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        if(toolWindow != null && !toolWindow.isDisposed()) {
          toolWindow.dispose();
          toolWindow = null;
        } else {
          toolWindow = new ToolWindow(workspace);
        }
      }
    });
    GridData gridData = new GridData();
    gridData.widthHint= 150;
    gridData.heightHint = 25;
    startTool.setLayoutData(gridData);

    lblStatus = new Label(this, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    lblStatus.setLayoutData(gridData);  
    lblStatus.setFont(UIDATA.FONT_10);

    liveSashForm = new LiveSashForm(this, SWT.VERTICAL);
    liveSashForm.setBackground(getBackground());
    gridData = new GridData(); 
    gridData.heightHint = 25;
    gridData.widthHint = 400;
    //    gridLayout.numColumns = 2;
    liveSashForm.setLayoutData(gridData);

    progressComposite = new Composite(liveSashForm, SWT.NONE);
    gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 1;
    gridLayout.horizontalSpacing = 1;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 2;
    progressComposite.setLayout(gridLayout); 

    lbl = new Label(progressComposite, SWT.NONE);
    lbl.setFont(UIDATA.FONT_9);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    lbl.setLayoutData(gridData);

    progressBar = new ProgressBar(progressComposite, SWT.HORIZONTAL);   
    progressBar.setBackground(UIDATA.BCOLOR);
    progressBar.setForeground(UIDATA.PROGRESS_COLOR);
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END); 
    gridData.heightHint = 18;
    gridData.widthHint = 200;
    //    gridLayout.numColumns = 2;
    progressBar.setLayoutData(gridData);

    tool = new Composite(liveSashForm, SWT.NONE);
    gridLayout = new GridLayout(4, false);
    gridLayout.marginHeight = 1;
    gridLayout.horizontalSpacing = 15;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 2;
    tool.setLayout(gridLayout); 

    lbl2 = new Label(tool, SWT.NONE);
    lbl2.setFont(UIDATA.FONT_9);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    lbl2.setLayoutData(gridData);

    PluginLoader pluginLoader = new PluginLoader();
    plugins = pluginLoader.createPlugins();
    String error = pluginLoader.getError();
    if(error.length() > 0) {
      ClientLog.getInstance().setException(getShell(), new Exception(error));
    }

    liveSashForm.setVisible(true);

    setBackground(getParent().getBackground());

    if(createPlugin) createPluginItems();
  }

  public void setPlugins(ClientPlugin[] plugins) {
    this.plugins = plugins;
  }

  protected void invoke(Hyperlink hyperLink) {
    for(int i = 0; i < items.length; i++) {
      if(items[i] != hyperLink) continue;
      ClientPlugin plugin  = hyperPlugins.get(i);
      if(plugin.getConfirmMessage() != null) {
        MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
        msg.setMessage(plugin.getConfirmMessage());
        if(msg.open() != SWT.YES) return ;
      }

      if(component == null) {
        plugin.invoke(items[i], null);
      } else {
        plugin.invoke(items[i], component);
      }
      break;
    }
  }

  public void setMessage(String text) {
    lblStatus.setText(text);
    if(mode == 0) {
      lblStatus.setForeground(new Color(getDisplay(), 0, 0, 0));
      return;
    }
    mode = 0;
    lblStatus.setForeground(new Color(getDisplay(), 255, 0, 0));
  }

  public void setProgressValue(int value) {
    progressBar.setSelection(value);
    liveSashForm.setMaximizedControl(progressComposite);
    if(value == 0 || value >= 100) {
      createPluginItems();
    } else {
      liveSashForm.setVisible(true);
    }
  }

  public void createPluginItems() {
    String metaId = getMetaId();
    String domain = null;
    disposeTools();
    if(metaId != null) {
      createPluginItems(ClientPlugin.CONTENT, metaId);
    } else if((domain = getDomain()) != null) {
      createPluginItems(ClientPlugin.DOMAIN, domain);
    }  else {
      createPluginItems(ClientPlugin.APPLICATION, domain);
    }

    liveSashForm.setMaximizedControl(tool);
    liveSashForm.setVisible(true);
  }

  protected void disposeTools() {
    Control[] widgets = tool.getChildren();
    if(widgets == null) return;
    for(int i = 1; i < widgets.length; i++) {
      if(widgets[i].isDisposed()) continue;
      widgets[i].dispose();
    }
  }

  public void setProgressValue(String text, int value) {
    if(mode != 0) showMessage();
    lblStatus.setText(text);
    setProgressValue(value);
  }

  public void showMessage() {
    mode = 0;
    lblStatus.setForeground(new Color(getDisplay(), 0, 0, 0));
  }

  public void setMessage(String text, Color color) {
    if(color.getRed() != 0) mode = 1;
    lblStatus.setText(text);
    lblStatus.setForeground(color);
  }

  public void setError(String error) {
    mode = 1;
    lblStatus.setForeground(new Color(getDisplay(), 255, 0, 0));
    lblStatus.setText(error);
  }

  public void showProgressBar() {
    liveSashForm.setMaximizedControl(progressComposite);
    liveSashForm.setVisible(true);
  }

  public ProgressBar getProgressBar() { return progressBar; }

  protected String getMetaId() {
    if(component == null
        || !(component instanceof Browser) ) return null;
    Browser browser = (Browser)component;
    if(browser == null) return null;
    String url = browser.getUrl();
    String app = ClientConnector2.currentInstance().getApplication();
    String path  = "/"+app+"/DETAIL/";
    int idx = url.indexOf(path);
    if(idx < 0) return null;
    String id = url.substring(idx+path.length());
    int sIndex = id.indexOf('/');
    if(sIndex > -1) id = id.substring(0, sIndex);
    return id;
  }

  protected String getDomain() {
    if(component == null 
        || !(component instanceof Browser) ) return null;
    Browser browser = (Browser)component;
    if(browser == null) return null;
    String url = browser.getUrl();
    String app = ClientConnector2.currentInstance().getApplication();
    String path  = "/"+app+"/DOMAIN/";
    int idx = url.indexOf(path);
    if(idx < 0) return null;
    String value = url.substring(idx+path.length());
    idx = value.indexOf('/');
    if(idx < 0) return null;
    value = value.substring(idx+1);
    try {
      return URLDecoder.decode(value, "utf-8");
    } catch (Exception e) {
      return null;
    }
  }


  protected void createPluginItems(int type, String ...values) {
    List<ClientPlugin> shows = new ArrayList<ClientPlugin>();  
    for(int i = 0; i < plugins.length; i++) {
      if(!plugins[i].isValidType(type)) continue;
      plugins[i].setValues(values);
      if(plugins[i].isInvisible()) {
        plugins[i].invoke(component);
        continue;
      }
      shows.add(plugins[i]);
    }

    hyperPlugins.clear();
    items = new Hyperlink[Math.min(2, shows.size())];
    for(int i = 0; i < items.length; i++) {
      ClientPlugin plugin = shows.remove(0);
      hyperPlugins.add(plugin);
      items[i] = new Hyperlink(tool, SWT.LEFT);
      items[i].setCursor(new Cursor(items[i].getDisplay(), SWT.CURSOR_HAND));
      items[i].setText(plugin.getLabel());
      //      link.setUnderlined(true);
      items[i].setFont(UIDATA.FONT_8VB);
      items[i].setBackground(tool.getBackground());
      //      System.out.println(items[i].getText() + " : "+ items[i].isvi);
      //      if(XPWidgetTheme.isPlatform()) {  
      //        items[i].setForeground(new Color(getDisplay(), 0, 50, 100));
      //      }
      items[i].addHyperlinkListener(new HyperlinkAdapter(){

        public void linkEntered(HyperlinkEvent e) {
          Hyperlink hyperlink = (Hyperlink)e.widget;
          hyperlink.setUnderlined(true);
        }

        public void linkExited(HyperlinkEvent e) {
          Hyperlink hyperlink = (Hyperlink)e.widget;
          hyperlink.setUnderlined(false);
        }

        public void linkActivated(HyperlinkEvent e) {
          invoke((Hyperlink)e.widget);
        }
      });
      plugin.syncEnable(items[i]);
    }

    boxPlugins.clear();
    boxPlugins.addAll(shows);

    if(boxPlugins.size() < 1) return;

    if(morePlugins != null) {
      if(morePlugins instanceof Menu) {
        ((Menu)morePlugins).dispose();
      }
      //      else if(morePlugins instanceof PopupMenu) {
      //        ((PopupMenu)morePlugins).dispose();
      //      }
    }

    Label lblMore = new Label(tool, SWT.NONE);
    lblMore.setFont(UIDATA.FONT_8VB);
    GridData gridData = new GridData();
    gridData.widthHint = 120;
    lblMore.setLayoutData(gridData);
    lblMore.setBackground(tool.getBackground());

    //    if(XPWidgetTheme.isPlatform()) {  
    //      morePlugins = new PopupMenu(lblMore, XPWidgetTheme.THEME);
    //      CMenu cmenu = new CMenu();
    //      ((PopupMenu)morePlugins).setMenu(cmenu);
    //    } else {
    morePlugins = new Menu(tool);
    //    }

    for(int i = 0; i < boxPlugins.size(); i++) {
      ApplicationFactory.createStyleMenuItem2(
          morePlugins, boxPlugins.get(i).getLabel(), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
              int index = -1;
              if(morePlugins instanceof Menu) { 
                MenuItem menuItem = (MenuItem)e.widget;
                index = ((Menu)morePlugins).indexOf(menuItem);
              } 

              //          else if(morePlugins instanceof PopupMenu) {
              //            PopupMenu popup = (PopupMenu)morePlugins;
              //            CMenuItem menuItem = (CMenuItem)e.data;
              //            index = popup.getMenu().indexOf(menuItem);
              //          }

              if(index < 0 || index > boxPlugins.size()) return;
              ClientPlugin plugin = boxPlugins.get(index);
              if(plugin.getConfirmMessage() != null) {
                MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
                msg.setMessage(plugin.getConfirmMessage());
                if(msg.open() != SWT.YES) return ;
              }

              if(component == null) {
                plugin.invoke(tool, null);
              } else {
                plugin.invoke(tool, component);
              }
            }
          });
    }

    lblMore.setText("More plugins...");
    lblMore.setBackground(getBackground());
    lblMore.setCursor(new Cursor(lblMore.getDisplay(), SWT.CURSOR_HAND));
    lblMore.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        if(morePlugins instanceof Menu) { 
          ((Menu)morePlugins).setVisible(true);
        }

        //        else if(morePlugins instanceof PopupMenu) {
        //          ((PopupMenu)morePlugins).open();
        //        }
      }
    });
    /* combox.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int index = combox.getSelectionIndex();
        if(index < 0 || index > boxPlugins.size()) return;
        ClientPlugin plugin = boxPlugins.get(index);
        if(plugin.getConfirmMessage() != null) {
          MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
          msg.setMessage(plugin.getConfirmMessage());
          if(msg.open() != SWT.YES) return ;
        }

        if(component == null) {
          plugin.innvoke(combox, null);
        } else {
          plugin.innvoke(combox, component);
        }
      }
    });*/

  }

  public void setBackground(Color color) {
    super.setBackground(color);
    lblStatus.setBackground(getBackground());
    progressComposite.setBackground(getBackground());
    lbl.setBackground(getBackground());
    tool.setBackground(getBackground());
    lbl2.setBackground(tool.getBackground());
    if(items != null) {
      for(int i = 0; i < items.length; i++) {
        items[i].setBackground(tool.getBackground());
      }
    }
  }

  public Object getComponent() { return component; }
  public void setComponent(Object comp) { this.component = comp; }

  public void viewPlugins() {
    if(items == null) return;
    for(int i = 0; i < items.length; i++) {
      items[i].setVisible(true);
    }
  }

}

