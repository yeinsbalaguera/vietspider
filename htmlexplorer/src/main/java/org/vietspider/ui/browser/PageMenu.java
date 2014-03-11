package org.vietspider.ui.browser;

import java.awt.Desktop;
import java.io.File;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMMouseEvent;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.BrowserMenu;

/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 24, 2009  
 */

public class PageMenu extends BrowserMenu {

  protected Browser browser;

  protected DocumentHandler handler;
  
  public PageMenu() {
  }

  public Menu createMenu(Browser _browser, nsIDOMEvent event) {
    this.handler = new DocumentHandler(_browser, event);
    this.browser = _browser;

    ApplicationFactory factory = new ApplicationFactory(browser, "BrowserMenu", getClass().getName());

    menu = new Menu (browser);
    
    createLinkItem(factory);
    factory.createMenuItem(menu, SWT.SEPARATOR);

    createBrowserItem(factory);
    factory.createMenuItem(menu, SWT.SEPARATOR);

    createNodeItem(factory);
    
    factory.createMenuItem(menu, SWT.SEPARATOR);
    
    createPageItem(factory);

    nsIDOMMouseEvent mouseEvent = (nsIDOMMouseEvent)event.queryInterface (nsIDOMMouseEvent.NS_IDOMMOUSEEVENT_IID);
    menu.setLocation(mouseEvent.getScreenX (), mouseEvent.getScreenY ());
    menu.setVisible(true);
    
    return menu;
  }

  public void createNodeItem(ApplicationFactory factory) {
    MenuItem item = factory.createMenuItem(menu, "copyText");
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        putClipBroard(handler.getSelectedText());
      }
    });

    item = factory.createMenuItem(menu, "viewHTML");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        File file = handler.getHTMLFile(evt.hashCode());
        openFile(file, false);
      }
    });

    item = factory.createMenuItem(menu, "viewHTMLWith");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        File file = handler.getHTMLFile(evt.hashCode());
        openFile(file, true);
      }
    });

    item = factory.createMenuItem(menu, "copyHTML");
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        putClipBroard(handler.getSelectedHTML());
      }
    });
  }
  
  public void createPageItem(ApplicationFactory factory) {
    MenuItem item = factory.createMenuItem(menu, "viewPageSource");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        File file = handler.getDocumentFile(evt.hashCode());
        openFile(file, false);
      }
    });
  }

  public void createBrowserItem(ApplicationFactory factory) {
    MenuItem item = factory.createMenuItem(menu, "back");
    item.setEnabled(browser.isBackEnabled());
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        browser.back();
      }
    });

    item = factory.createMenuItem(menu, "forward");
    item.setEnabled(browser.isForwardEnabled());
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        browser.forward();
      }
    });

    item = factory.createMenuItem(menu, "refresh");
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        browser.refresh();
      }
    });

    item = factory.createMenuItem(menu, "stop");
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        browser.stop();
      }
    });
  }

  private void openFile(final File file, boolean newEditor) {
    Preferences prefs = Preferences.userNodeForPackage(PageMenu.class);
    String path = prefs.get("html.editor", "");
    if(newEditor) {
      path = openEditor() ;
    } else if(path == null 
        || path.trim().isEmpty()
        || !(new File(path)).exists()
    ) {
      if("win32".equalsIgnoreCase(SWT.getPlatform())) {
        String root = System.getProperty("user.home");
        int idx = root.indexOf(':');
        if(idx > 0) root = root.substring(0, idx+1);
        path = root + "\\Program Files\\EmEditor\\emeditor.exe";
        if(!(new File(path)).exists()) path = openEditor() ;
      } else {
        path = openEditor() ;
      }
    }
    if(path != null) prefs.put("html.editor", path);
    openFile(path, file.getAbsolutePath());
    
    
    new Thread() {
      public void run() {
        try {
          Thread.sleep(10*1000);
          file.delete();
        } catch (Exception e) {
        }
      }
    }.start();
  }

  private void openFile(final String path, final String htmlFile)  {
    new Thread() {
      public void run() {
        if(path != null) {
          try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(new String[]{path, htmlFile});
            return;
          } catch (Exception e) {
            ClientLog.getInstance().setMessage(null, e);
          }
        }
        
        try {
          Desktop.getDesktop().edit(new File(htmlFile));
        } catch (Exception e) {
          ClientLog.getInstance().setMessage(null, e);
        }
      }
    }.start();
  }

  private String openEditor() {
    String root = System.getProperty("user.home");
    int idx = root.indexOf(':');
    if(idx > 0) root = root.substring(0, idx+1);

    FileDialog dialog = new FileDialog (browser.getShell(), SWT.OPEN);

    dialog.setFilterNames (new String [] {"HTML Editor", "All Files (*)"});
    dialog.setFilterExtensions(new String [] {"*.exe", "*"});

    if("win32".equalsIgnoreCase(SWT.getPlatform())) {
      dialog.setFilterPath(root+"\\Program Files");
    } else {
      dialog.setFilterPath (root);
    }
    return dialog.open();
  }
  
  public void createLinkItem(ApplicationFactory factory) {
    MenuItem item = factory.createMenuItem(menu, "copyLink");
    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        putClipBroard(handler.getSelectedLinkAsText());
      }
    });
  }

}
