/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.wordpress;

import java.io.File;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.io.LicenseVerifier;
import org.vietspider.common.util.Worker;
import org.vietspider.content.cms.sync.SyncManager2;
import org.vietspider.model.plugin.wordpress.WordPressSyncData;
import org.vietspider.model.plugin.wordpress.XMLWordPressConfig;
import org.vietspider.model.plugin.wordpress.XMLWordPressConfig.Category;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.XML2Object;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/**
 * Author : Nhu Dinh Thuan nhudinhthuan@yahoo.com Aug 15, 2008
 */
class MultiPostSelector {

  private static final String UNUSED = "unused";

  private Shell shell;

  private SingleArticleSelector[] selectors;

  private Category[] categories;

  private WordPressSyncArticlesPlugin plugin;

  MultiPostSelector(Shell parent) {
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    ClientRM clientRM = WordPressSyncArticlesPlugin.getResources();
    ApplicationFactory factory = new ApplicationFactory(shell, clientRM, getClass().getName());
    shell.setText("Synchronized data");
    factory.setComposite(shell);
    shell.setLayout(new GridLayout(1, false));

    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        new ShellSetter(MultiPostSelector.class, shell);
        shell.setVisible(false);
        e.doit = false;
      }
    });

    factory.setComposite(shell);

    GridData gridData = new GridData(GridData.FILL_BOTH);
    GridLayout gridLayout = new GridLayout(1, false);

    ScrolledComposite scrolled = new ScrolledComposite(shell, SWT.V_SCROLL | SWT.H_SCROLL);
    scrolled.setLayoutData(gridData);
    Composite scrolledComposite = new Composite(scrolled, SWT.NONE);

    gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    gridLayout.marginHeight = 1;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 2;
    scrolledComposite.setLayout(gridLayout);

    selectors = new SingleArticleSelector[10];
    for (int i = 0; i < selectors.length; i++) {
      selectors[i] = new SingleArticleSelector(scrolledComposite, i+1);
      selectors[i].setLayoutData(new GridData());
    }

    scrolled.setContent(scrolledComposite);
    scrolled.setExpandHorizontal(true);
    scrolled.setExpandVertical(true);

    scrolled.setMinSize(scrolledComposite.computeSize(500, 850));

    Composite bottom = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    // gridData.horizontalSpan = 2;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;
    factory.setComposite(bottom);

//    if(XPWidgetTheme.isPlatform()) {
//      factory.createCButton("butNextPage", new SelectionAdapter() {
//        @SuppressWarnings(UNUSED)
//        public void widgetSelected(SelectionEvent evt) {
//          if(plugin != null) plugin.nextPage();
//        }
//      });
//    } else {
      factory.createButton("butNextPage", new SelectionAdapter() {
        @SuppressWarnings(UNUSED)
        public void widgetSelected(SelectionEvent evt) {
          if(plugin != null) plugin.nextPage();
        }
      });
//    }

    SelectionAdapter syncListener = new SelectionAdapter() {
      @SuppressWarnings(UNUSED)
      public void widgetSelected(SelectionEvent evt) {
        File licenseFile = LicenseVerifier.loadLicenseFile();
//        System.out.println(" == > "+ licenseFile.getAbsolutePath());
        boolean license = LicenseVerifier.verify("wordpress", licenseFile);
        int max = license ? 100 : 5;
        int counter = 0;

        for(int i = 0; i < selectors.length; i++) {
          WordPressSyncData syncData = selectors[i].getSyncData();
          if(syncData == null) continue;
          SyncManager2.getInstance(shell).sync(syncData);
          counter++;
          if(counter >= max) break;
        }

        boolean visisble = false;
        if(license) {
          if(counter >= max) {
            Exception exp = new Exception("Free license: Maximum 5 articles!");
            ClientLog.getInstance().setMessage(shell, exp);
          }
        } else {
          for(int i = 0; i < selectors.length; i++) {
            if(selectors[i].enableSelection()) {
              visisble = true;
              break;
            }
          }
        }

        if(visisble) return;
        if(plugin != null) plugin.nextPage();
//        new ShellSetter(MultiPostSelector.class, shell);
//        shell.setVisible(false);
      }
    };

//    if(XPWidgetTheme.isPlatform()) {
//      factory.createCButton("butOk", syncListener);
//    } else {
      factory.createButton("butOk", syncListener);
//    }

//    if(XPWidgetTheme.isPlatform()) {
//      factory.createCButton("butClose", new SelectionAdapter() {
//        @SuppressWarnings(UNUSED)
//        public void widgetSelected(SelectionEvent evt) {
//          new ShellSetter(MultiPostSelector.class, shell);
//          shell.setVisible(false);
//        }
//      });
//    } else {
      factory.createButton("butClose", new SelectionAdapter() {
        @SuppressWarnings(UNUSED)
        public void widgetSelected(SelectionEvent evt) {
          new ShellSetter(MultiPostSelector.class, shell);
          shell.setVisible(false);
        }
      });
//    }


    loadCategries();

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300) / 2;
    shell.setImage(parent.getImage());
    new ShellGetter(MultiPostSelector.class, shell, 550, 350, x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
  }

  public void setData(String[] ids, String[] titles, String [] categories) {
    int i = 0;
    RefsDecoder decoder = new RefsDecoder();
    for (; i < Math.min(ids.length, selectors.length); i++) {
      titles[i] = new String(decoder.decode(titles[i].toCharArray()));
      selectors[i].setArticle(ids[i], titles[i], categories[i]);
    }
    for(;i < selectors.length; i++) {
      selectors[i].setArticle("", "", "");	    
    }
    shell.setVisible(true);
  }

  public void loadCategries() {
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
          Header[] headers = new Header[] {
              new BasicHeader("action", "load.file.by.gzip"),
              new BasicHeader("file", "system/plugin/wordpress.config") };

          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte[] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);
          
//          System.out.println(new String(bytes, Application.CHARSET));

          config = XML2Object.getInstance().toObject(XMLWordPressConfig.class, bytes);
        } catch (Exception e) {
          error = e.toString();
        }

      }

      public void after() {
        if (error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        if (config == null) return;

        categories = config.getCategories();
        for (int i = 0; i < selectors.length; i++) {
          selectors[i].setShowMessage(config.isAlertMessage());
          selectors[i].setCategories(categories);
        }
      }
    };
    new ThreadExecutor(excutor, shell).start();
  }

  public void invokeSetup() {
    new WordPressSetup(shell.getShell());
  }

  public void setPlugin(WordPressSyncArticlesPlugin plugin) {
    this.plugin = plugin;
  }

}
