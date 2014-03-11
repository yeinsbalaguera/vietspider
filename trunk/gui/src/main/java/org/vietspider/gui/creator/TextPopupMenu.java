/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.ui.widget.ApplicationFactory;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 19, 2008  
 */
public class TextPopupMenu {
  
  private Text text;
  private Object menu;
  private boolean overide = false;
  
  public TextPopupMenu(ApplicationFactory factory, Text txt, boolean overide_) {
    this.text = txt;
    this.overide = overide_;
    
//    if(XPWidgetTheme.isPlatform()) {  
//      PopupMenu popupMenu = new PopupMenu(text, XPWidgetTheme.THEME);
//      menu = new CMenu();
//      popupMenu.setMenu((CMenu)menu);
//      text.setMenu(new Menu(text.getShell(), SWT.POP_UP));
//    } else {
      menu = new Menu(text.getShell(), SWT.POP_UP);
      text.setMenu((Menu)menu);
//    }
   
    
    if(overide) {
      text.addKeyListener(new KeyAdapter() {

        private boolean paste = false;

        public void keyReleased(KeyEvent event) {
          if(event.keyCode == SWT.MOD1) paste = false;
          if(event.keyCode != SWT.CR) return ;
        }

        public void keyPressed(KeyEvent event) {
          if(event.keyCode == SWT.MOD1) {
            paste = true;
            return;
          }
          if(!paste || event.keyCode != 'v') return ;

          final int time = 200;
          Runnable timer = new Runnable () {
            public void run () {
              paste();
            }
          };
          text.getDisplay().timerExec (time, timer);
        }

      });
    }

    factory.createStyleMenuItem(menu, "menuPaste", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        paste();
      }
    });

    factory.createStyleMenuItem(menu, "menuCopy", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(text.getSelectionText() == null 
            || text.getSelectionText().isEmpty()) {
          text.selectAll();
        }
        text.copy();
      }
    });

    factory.createStyleMenuItem(menu, SWT.SEPARATOR);
    
    factory.createStyleMenuItem(menu, "menuClear", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        text.setText("");
      }
    });
    
    factory.createStyleMenuItem(menu, "menuCut", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(text.getSelectionText() == null 
            || text.getSelectionText().isEmpty()) {
          text.selectAll();
        }
        text.cut(); 
      }
    });
    
    
  }
  
  private void paste() {
    if(!overide) {
      text.paste();
      return;
    }
    
    Clipboard clipboard = new Clipboard(text.getDisplay());
    HTMLTransfer transfer = HTMLTransfer.getInstance();
    String value = (String)clipboard.getContents(transfer);
    
    if(value == null || value.trim().isEmpty()) {
      TextTransfer transfer2 = TextTransfer.getInstance();
      value = (String)clipboard.getContents(transfer2);
    }
    
    if(value == null || value.trim().isEmpty()) return;
    
    String [] values = null;
    if(value.indexOf('<') > -1 && value.indexOf('>') > -1) {
      try {
        HTMLDocument document = new HTMLParser2().createDocument(value);
        HyperLinkUtil linkUtil = new HyperLinkUtil();
        List<String> collection = linkUtil.scanSiteLink(document.getRoot());
        values = collection.toArray(new String[collection.size()]);
      } catch (Exception e) {
      }
    } else {
      values = value.trim().split("\n");
    }
    
    if(values == null 
        || values.length < 1 
        || !SWProtocol.isHttp(values[0])) return;
    values[0] = values[0].replaceAll("&amp;", "&");
    text.setText(values[0]); 
    
  }
  
  public Object getMenu() { return menu; }

}
