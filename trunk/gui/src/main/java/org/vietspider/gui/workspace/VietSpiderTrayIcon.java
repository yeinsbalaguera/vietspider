/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.workspace;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.vietspider.ClientProperties;
import org.vietspider.bean.Article;
import org.vietspider.bean.ArticleCollection;
import org.vietspider.bean.Meta;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataContentHandler;
import org.vietspider.gui.browser.BrowserWidget;
import org.vietspider.notifier.notifier.NotificationEvent;
import org.vietspider.notifier.notifier.NotificationListener;
import org.vietspider.notifier.notifier.Notifier;
import org.vietspider.notifier.notifier.NotifierDialog;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 2, 2009  
 */
public class VietSpiderTrayIcon {

  private Tray tray;
  private TrayItem item;

  private Shell shell;
//  private Runnable timer; 
  private Runnable timer2;
  private int displayTime = 5*1000;
  private int max = 50;

  private NotificationListener listener;

  private List<String> showed = new Vector<String>();
  private Queue<Article> articles = new LinkedList<Article>();

  private Workspace workspace;

  private NotifierDialog notifierDialog;
  private boolean alertEveryTime = false;

  public VietSpiderTrayIcon(Workspace _workspace) {
    this.shell = _workspace.getShell();
    this.workspace = _workspace;
    ApplicationFactory factory = new ApplicationFactory(shell, "TrayIcons", getClass().getName());

    Image image = shell.getImage();

    Display display = shell.getDisplay();
    tray = display.getSystemTray();
    if(tray == null) return;
    item = new TrayItem(tray, SWT.NONE);

    StringBuilder title = new StringBuilder(factory.getLabel("title"));
    title.append(" Client").append(" - ");
    try {
      URL url = new URL(ClientConnector2.currentInstance().getRemoteURL());
      title.append(url.getHost()).append(':').append(url.getPort());
    } catch (Exception e) {
      title.append(ClientConnector2.currentInstance().getRemoteURL());
    }

    item.setText(title.toString());
    item.setToolTipText(title.toString());
    item.setImage(image);

    final Menu menu = new Menu(shell, SWT.POP_UP);

    factory.createMenuItem(menu, "itemLogin", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        VietSpiderClient.connect(false);
      }
    });

    factory.createMenuItem(menu, SWT.SEPARATOR);

    factory.createMenuItem(menu, "itemExit", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        VietSpiderClient.dispose();
      }
    });

    item.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        VietSpiderClient.showWindow();
      }
    });


    item.addListener(SWT.MenuDetect, new Listener () {
      @SuppressWarnings("unused")
      public void handleEvent (Event event) {
        menu.setVisible(true);
      }
    });

    listener = new NotificationListener() {
      public void clicked(NotificationEvent event) {
        if(event.getComponent() == NotificationEvent.CLOSE) return;

        StringBuilder builder = new StringBuilder();
        ClientConnector2 connecter = ClientConnector2.currentInstance();
        builder.append(connecter.getRemoteURL()).append('/');
        builder.append(connecter.getApplication()).append('/').append("DETAIL/");
        builder.append(event.getId());

        BrowserWidget browser = workspace.getTab().getSelected();
        if(browser == null) browser = workspace.getTab().createItem();
        browser.setUrl(builder.toString());

        shell.setMinimized(false);
        shell.setVisible(true);
      }
    };

    String tip = ClientProperties.getInstance().getValue("alert.new.articles");
    if("false".equals(tip)) return;

//    timer = new Runnable () {
//      public void run () {
//        try {
//          handle();
//          if(shell.isDisposed()) return;
//          shell.getDisplay().timerExec (10*1000, timer);
//        } catch (Throwable e) {
//          ClientLog.getInstance().setMessage(null, new Exception(e.getMessage()));
//        }
//      }
//    };
//    display.timerExec (10*1000, timer);
    
    new ThreadLoader().start();

    timer2 = new Runnable () {
      public void run () {
        try {
          show();
        } catch (Throwable e) {
          ClientLog.getInstance().setMessage(null, new Exception(e.getMessage()));
        }
        if(shell.isDisposed()) return;
        shell.getDisplay().timerExec (displayTime, timer2);
      }
    };
    display.timerExec (5*1000, timer2);
  }

  public void dispose() {
    if(item == null || item.isDisposed()) return;
    if(tray == null || tray.isDisposed()) return;
    item.isDisposed();
    tray.dispose();
  }

  private void show() {
    if(articles.isEmpty()) return;
    Article article = articles.poll();
    String id = article.getId();
    if(showed.contains(id)) return;
    showed.add(id);

//  System.out.println(" add ===============  >  show every time "+ alertEveryTime
//      + " : " + );
    if(!alertEveryTime && shell.isVisible()) return;

    //    System.out.println(" will show "+ id);

    Meta meta = article.getMeta();
    //    System.out.println("show " + meta.getTitle());

    Notifier notifier = new Notifier(shell, id);
    notifier.setTitle(meta.getTitle());
    notifier.setListener(listener);
    notifier.setMessage(meta.getDesc());
    notifier.setDisplayTime(displayTime);

    if(notifierDialog != null) {
      notifierDialog.getShell().dispose();
    }

    notifierDialog = new NotifierDialog(notifier);
    Color textColor = new Color(Display.getDefault(), 0, 0, 0);
    notifierDialog.setTitleFgColor(textColor);
    notifierDialog.setTitleFont(UIDATA.FONT_9B);
    notifierDialog.setTextFont(UIDATA.FONT_8T);
    notifierDialog.setTextFgColor(textColor);
    notifierDialog.init(notifier);
    notifierDialog.setWidth(400);
    notifierDialog.createGUI(notifier);

    Rectangle clientArea = notifier.getParent().getMonitor().getBounds();

    int startX = clientArea.x + clientArea.width - (notifierDialog.getWidth() + 2);//352;
    int startY = clientArea.y + clientArea.height - 150;

    Shell _shell = notifierDialog.getShell();
    _shell.setLocation(startX, startY);
    _shell.setAlpha(200);
    _shell.setVisible(true);

    notifierDialog.fadeIn();

    //    _shell.forceActive();
  }

  private class ThreadLoader extends Thread {

    public void run() {
      while(true) {
        try {
          load();
        } catch (Exception e) {
          ClientLog.getInstance().setThrowable(null, e);
        }
        try {
          Thread.sleep(10*1000l);
        } catch (Exception e) {
        }
      }
    }

    private void load()  throws Exception {
      while(showed.size() > max) {
        //      System.out.println(" remove " + showed.get(0));
        showed.remove(0);
      }

      if(articles.size() > max) return;

      DataContentHandler dataContentHandler = new DataContentHandler();
      ArticleCollection collection = dataContentHandler.loadNewContents();
      if(collection == null) return;
      alertEveryTime = "true".equals(collection.getProperties().getProperty("filter"));
//      System.out.println(" =============   > " + collection.getProperties().getProperty("filter"));
      for(int i = 0; i < collection.get().size(); i++) {
        Article article = collection.get().get(i);
        if(showed.contains(article.getId())) {
          //          System.out.println(" showed " + article.getId());
          continue;
        }
        boolean add = true;
        Iterator<Article> iterator = articles.iterator();
        while(iterator.hasNext()) {
          Article temp = iterator.next();
          if(temp.getId().equals(article.getId())) {
            add = false;
            break;
          }
        }
//                System.out.println(" add ===============  >  "+ article.getId()
//                    + " : " + shell.isFocusControl());
        if(add) articles.add(article);
      }
    }

  }

}
