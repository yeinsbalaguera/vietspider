/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.net.URL;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.browser.BrowserWidget;
import org.vietspider.gui.creator.CTextPopupMenu.ITextMenu;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.ui.BareBonesBrowserLaunch;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.CCombo2;
import org.vietspider.ui.widget.ImageHyperlink;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 28, 2008  
 */
public class URLWidget implements ITextMenu {
  
  private CCombo2 combo;
  private ISourceInfo iSourceInfo;
  
  private ImageHyperlink add;
  private Image addNormal, addDown;
  
  private volatile Runnable timer;
  
  public URLWidget(ApplicationFactory factory, ISourceInfo fieldGetter_) {
    this.iSourceInfo = fieldGetter_;
  
    combo = new CCombo2(factory.getComposite(), SWT.BORDER);
    add = new ImageHyperlink(combo, SWT.CENTER);
    addNormal = factory.loadImage("add2.png");
    addDown = factory.loadImage("add3.png");
    add.setImage(addNormal);
    combo.setIcon(add);
    add.addHyperlinkListener(new HyperlinkAdapter(){
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
        add.setImage(addNormal);
        add.redraw();
      }

      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
        add.setImage(addNormal);
        add.redraw();
      }
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        add.setImage(addNormal);
        add.redraw();
      }
    });
    add.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        if(combo.text.getText().trim().isEmpty()) return;
        addItem();
      }
      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e) {
        add.setImage(addDown);
        add.redraw();
      }
    });
    
    combo.setFont(UIDATA.FONT_10);
    combo.setBackground(new Color(combo.getDisplay(), 255, 255, 255));
    combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    Object menu =  new CTextPopupMenu(factory, this).getMenu();
    factory.createStyleMenuItem(menu, SWT.SEPARATOR);
    
    factory.createStyleMenuItem(menu, "menuGo", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String address = combo.text.getText();
        if(ApplicationFactory.isMozillaBrowser()) {
          Creator creator = iSourceInfo.<Creator>getField("creator");
          BrowserWidget browserWidget = creator.getWorkspace().getTab().createItem();
          browserWidget.setUrl(address);
          return;
        }
        BareBonesBrowserLaunch.openURL(combo.getShell(), address);
      }
    });
     
    factory.createStyleMenuItem(menu, SWT.SEPARATOR);
    
    factory.createStyleMenuItem(menu, "menuRemoveItem", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        removeItem();
      }
    });
    
    factory.createStyleMenuItem(menu, "menuRemoveAll", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        combo.removeAll();
        combo.text.setText("");
      }
    });
    
//    combo.text.setMenu(menu);
    
    combo.text.addKeyListener(new KeyAdapter() {
      
      private boolean paste = false;
      
      public void keyReleased(KeyEvent event) {
        if(event.keyCode == SWT.MOD1) paste = false;
        if(event.keyCode != SWT.CR) return ;
        if(combo.text.getText().trim().isEmpty()) return;
        addItem();
      }
      
      public void keyPressed(KeyEvent event) {
        if(event.keyCode == SWT.MOD1) {
          paste = true;
          return;
        }
        if(!paste || event.keyCode != 'v') return ;
        final int time = 200;
        Runnable _timer = new Runnable () {
          public void run () {
            paste();
          }
        };
        combo.getDisplay().timerExec (time, _timer);
      }
      
    });
    
    combo.text.setDoubleClickEnabled(false);
    combo.text.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseDown(MouseEvent e) {
        if(e.count == 3) {
          combo.text.setText("");
          combo.text.paste();
          return;
        }
        
        if(e.button == 2) {
          if(combo.text.getText().trim().isEmpty()) return;
          addItem();
        }
      }
    });
    
    combo.text.addModifyListener(new ModifyListener(){
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent evt) {
        startChecking();
      }
    });
    
    SourceEditorUtil.setDropTarget(combo.text);
  }
  
  private void startChecking() {
    if(timer != null || iSourceInfo == null) return;
    final String url = combo.text.getText().trim();
    if(url.isEmpty()) return;
    timer = new Runnable () {
      public void run () {
        timer = null;
        if(combo == null || combo.text == null) return;
        if(combo.isDisposed() || combo.text.isDisposed()) return;
        if(!iSourceInfo.<Boolean>getField("isNewSource")) return;
        
        String newURL = combo.text.getText().trim();
        try {
          new URL(newURL).getHost();
        } catch (Exception e) {
          startChecking();
        }
        
        if(url.equalsIgnoreCase(newURL)) {
          checkURL();
          return;
        } 
        
        startChecking();
      }
    };
    combo.getDisplay().timerExec (1000, timer);
  }
  
  public void paste() {
    Clipboard clipboard = new Clipboard(combo.getDisplay());
    HTMLTransfer transfer = HTMLTransfer.getInstance();
    String text = (String)clipboard.getContents(transfer);
    
    if(text == null || text.trim().isEmpty()) {
      TextTransfer transfer2 = TextTransfer.getInstance();
      text = (String)clipboard.getContents(transfer2);
    }
    
    if(text == null || text.trim().isEmpty()) return;
    
    if(text.indexOf('<') > -1 && text.indexOf('>') > -1) {
      try {
        HTMLDocument document = new HTMLParser2().createDocument(text);
        HyperLinkUtil linkUtil = new HyperLinkUtil();
        List<String> collection = linkUtil.scanSiteLink(document.getRoot());
        for(int i = 0; i < collection.size(); i++) {
          collection.set(i, collection.get(i).replaceAll("&amp;", "&")); 
        }        
        addItems(collection.toArray(new String[collection.size()]));
      } catch (Exception e) {
      }
      return;
    }
    addItems(text.split("\n"));
  }
  
  
  public void copyAll() {
    String [] items = getItems();
    StringBuilder  builder = new StringBuilder();
    for(String item : items) {
      builder.append(item).append('\n');
    }
    Clipboard clipboard = new Clipboard(combo.getDisplay());
    TextTransfer transfer = TextTransfer.getInstance();
    clipboard.setContents( 
        new Object[] { builder.toString().trim() }, 
        new Transfer[]{ transfer }
    );
  }

  public Text getTextComponent() { return combo.text; }
  
  public void setItem(String item) {
    combo.setText(item);
    if(combo.text.isFocusControl()) return;
  }
  
  public void setItems(String [] items) {
    combo.removeAll();
    try {
      combo.setItems(items);
    } catch (Exception e) {
      Creator creator = iSourceInfo.getField("creator");
      creator.showMessage(e.toString(), creator.getErrorIcon(), Creator.ERROR_FIELD);
    }
    if(items.length > 0) combo.select(0);
    combo.setVisibleItemCount(Math.min(combo.getItemCount(), 20));
  }
  
  public void addItems(String [] items) {
    for(String item : items) {
      if(item == null) continue;
      if(!SWProtocol.isHttp(item)) continue;
      addItem(item);
    }
    if(combo.getItemCount() > 0) combo.select(0);
  }
  
  private void addItem() {
    String value = combo.text.getText().trim();
    if(!SWProtocol.isHttp(value)) return;
    if(value.isEmpty()) return;
    addItem(value);
  }
  
  private void addItem(String value) {
    String [] items = combo.getItems();
    for(String item : items) {
      if(value.equalsIgnoreCase(item)) return; 
    }
    combo.add(value);
    combo.setVisibleItemCount(Math.min(combo.getItemCount(), 20));
  }
  
  private void removeItem() {
    String text = combo.text.getText();
    for(int i = 0; i < combo.getItemCount(); i++) {
      if(combo.getItem(i).equals(text)) {
        combo.remove(i);
        combo.text.setText("");
        break;
      }
    }
  }
  
  private void checkURL() {
    Worker excutor = new Worker() {
      
      private String [] urls ;
      private String url;
      private int type = 0;
      private String [] elements ;
      private String group;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        Creator creator = iSourceInfo.getField("creator");
        group = creator.getSelectedGroupName();
        String [] items = combo.getItems(); 
        urls = new String[items.length+1];
        for(int i = 0; i < items.length; i++) {
          urls[i] = items[i];
        }
        urls[urls.length-1] = getText();
      }

      public void execute() {
        SourcesClientHandler  handler = null;
        try {
          handler = new SourcesClientHandler(group);
        } catch (Exception e) {
          type = -1;
          url = e.toString();
        }
        if(handler == null) return;
        
        for(String u : urls ){
          if(u == null || u.trim().isEmpty()) continue;
          try {
            elements = handler.searchSourceByURL(u);
            if(elements != null 
                && elements.length > 0 
                && !elements[0].trim().isEmpty()) {
              type = 1;
              url = u;
              break;
            }
            
            elements = handler.searchSourceByHost(new URL(u).getHost());
            if(elements != null 
                && elements.length > 0 
                && !elements[0].trim().isEmpty()) {
              type  = 2;
              url = u;
              break;
            }
            
          } catch (Exception e) {
            type = -1;
            url = e.toString();
          }
        }
      }

      public void after() {
        Creator creator = iSourceInfo.<Creator>getField("creator"); 
//        StatusBar statusBar = creator.getStatusBar();
        
        if(type == -1) {
          creator.showMessage(url, creator.getErrorIcon(), Creator.ERROR_FIELD);
          return;
        }

        if(type == 0) {
          combo.text.setForeground(new Color(combo.text.getDisplay(), 0, 0, 0));
//          statusBar.showMessage();
          return;
        }
      
        String appendLabel = new ClientRM("Creator").getLabel("existURL");
        
        Color color = null;
        String status  = "";
        if(type == 1) {
          color = new Color(combo.text.getDisplay(), 255, 0, 0);
          status = url + " " + appendLabel + " \""+ elements[0].trim() + "\"";
        } else if(type == 2) {
          color = new Color(combo.text.getDisplay(), 255, 50, 50);
          try {
            status = new URL(url).getHost()+ " " + appendLabel + " \""+ elements[0].trim() + "\"";
          } catch (Exception e) {
            status =  url + " " + appendLabel + " \""+ elements[0].trim() + "\"";
          }
        }
        
        combo.text.setForeground(color);
        creator.showMessage(status, creator.getErrorIcon(), Creator.ERROR_SOURCE);
      }
    };
    new ThreadExecutor(excutor, combo.text).start();
  }
  
  public void addModifyListener(ModifyListener listener) { 
    combo.text.addModifyListener(listener);
  }
  
  public String getText() { return combo.text.getText(); }
  
  public String [] getItems() { 
    addItem();
    return combo.getItems(); 
  }
  
  public void select(int index) {
    if(index >= combo.getItemCount()) return;
    combo.select(index); 
  }
  
  public void clearItems() {
    if(combo == null || combo.isDisposed()) return;
    combo.text.setForeground(new Color(combo.text.getDisplay(), 0, 0, 0));    
    combo.removeAll(); 
  }

}
