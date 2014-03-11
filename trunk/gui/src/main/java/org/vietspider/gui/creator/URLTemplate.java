/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.gui.creator.CTextPopupMenu.ITextMenu;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.ui.htmlexplorer.URLTemplateUtils;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.CCombo2;
import org.vietspider.ui.widget.ImageHyperlink;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 28, 2008  
 */
public class URLTemplate implements ITextMenu {
  
  private CCombo2 combo;
  
  private ImageHyperlink add;
  private Image addNormal, addDown;
  
  private  Object menu;
  
  private final static String JAVASCRIPT = "javascript"; 
  
  public void copyAll() {
    Clipboard clipboard = new Clipboard(combo.getDisplay());
    TextTransfer transfer = TextTransfer.getInstance();
    clipboard.setContents( new Object[] { getValue() }, new Transfer[]{ transfer });
  }

  public Text getTextComponent() { return combo.text; }

  public void paste() {
    Clipboard clipboard = new Clipboard(combo.getDisplay());
    
    HTMLTransfer transfer = HTMLTransfer.getInstance();
    String value = (String)clipboard.getContents(transfer);
    
    if(value != null && !value.trim().isEmpty()) {
      String [] values = null;
      if(value.indexOf('<') > -1 && value.indexOf('>') > -1) {
        try {
          HTMLDocument document = new HTMLParser2().createDocument(value);
          HyperLinkUtil linkUtil = new HyperLinkUtil();
          List<String> collection = linkUtil.scanSiteLink(document.getRoot());
          values = collection.toArray(new String[collection.size()]);
        } catch (Exception e) {
        }

        if(values == null || values.length < 1  
            || (!SWProtocol.isHttp(values[0]) && !values[0].startsWith(JAVASCRIPT))) return;
        values[0] = values[0].replaceAll("&amp;", "&");
        combo.text.setText(values[0]); 
      }
      return;
    }
    
    TextTransfer transfer2 = TextTransfer.getInstance();
    String text = (String)clipboard.getContents(transfer2);
    if(text != null && !text.trim().isEmpty()) addItems(text.split("\n"));
  }

  public URLTemplate(ApplicationFactory factory) {
    combo = new CCombo2(factory.getComposite(),SWT.BORDER);
    
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
    
    menu = new CTextPopupMenu(factory, this).getMenu();
    
    factory.createStyleMenuItem(menu, SWT.SEPARATOR);
    
    factory.createStyleMenuItem(menu, "menuToTemplate", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        URLTemplateUtils.handle(combo.text);
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
        Runnable timer = new Runnable () {
          public void run () {
            paste();
          }
        };
        combo.getDisplay().timerExec (time, timer);
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

      @SuppressWarnings("unused")
      public void mouseDoubleClick(MouseEvent e) {
        new URLTemplateUtils.HandleAction().handle(combo.text);
      }
    });
    
    SourceEditorUtil.setDropTarget(combo.text);
  }
  
  public Object getMenu() {
    return menu;
  }

  public void putText(String text) {
    combo.text.setText(text);
  }
  
  public int getItemCount() {
    return combo.getItemCount();
  }
  
  public String[] getItems() {
    addItem(combo.text.getText());
    return combo.getItems();
  }
  
  public void setItems(String [] items) {
    combo.removeAll();
    combo.setItems(items);
    if(items.length > 0) combo.select(0);
  }
  
  public void addItems(String [] items) {
    for(String item : items) {
      if(item == null) continue;
//      if(item.indexOf('*') < 0) continue;
      if(item.indexOf('*') < 0 
          && item.indexOf('@') < 0
          && item.indexOf('$') < 0) continue;
      if(!SWProtocol.isHttp(item) && !item.startsWith(JAVASCRIPT)) continue;
      addItem(item);
    }
  }
  
  private void addItem(String value) {
    String [] items = combo.getItems();
    for(String item : items) {
      if(value.equalsIgnoreCase(item)) return; 
    }
    combo.add(value);
    combo.setVisibleItemCount(combo.getItemCount());
  }
  
  public void setValue(Properties properties, String name) {
    combo.removeAll();
    Object value = properties.get(name);
    if(value == null) {
      combo.setItems(new String[0]);
      return;
    } 
    String [] items = value.toString().split("\n");
    combo.setItems(items);
    if(items.length > 0) combo.select(0);
  }
  
  public String getValue() {
    addItem();
    String [] items = combo.getItems();
    StringBuilder builder = new StringBuilder();
    for(String ele : items) {
      if(builder.length() > 0) builder.append('\n');
      builder.append(ele);
    }
    return builder.toString();
  }
  
  public void addItem() {
    String value = combo.text.getText();
    if(value.indexOf('*') < 0 
        && value.indexOf('@') < 0
        && value.indexOf('$') < 0) return;
    if(!SWProtocol.isHttp(value) && !value.startsWith(JAVASCRIPT)) return;
    String [] items = combo.getItems();
    for(String item : items) {
      if(value.equalsIgnoreCase(item)) return; 
    }
    combo.add(value);
    combo.setVisibleItemCount(combo.getItemCount());
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
  
  public void clearAll() {
    combo.removeAll();
    combo.text.setText("");
  }

}
