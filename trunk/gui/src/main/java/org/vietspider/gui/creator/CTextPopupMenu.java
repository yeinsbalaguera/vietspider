/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.vietspider.ui.widget.ApplicationFactory;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 19, 2008  
 */
public class CTextPopupMenu {
  
  private ITextMenu text;
  private Menu menu;
  
  public CTextPopupMenu(ApplicationFactory factory, ITextMenu txt) {
    this.text = txt;
    
//    if(XPWidgetTheme.isPlatform()) {  
//      PopupMenu popupMenu = new PopupMenu(text.getTextComponent(), XPWidgetTheme.THEME);
//      menu = new CMenu();
//      popupMenu.setMenu((CMenu)menu);
//      text.getTextComponent().setMenu(new Menu(text.getTextComponent().getShell(), SWT.POP_UP));
//    } else {
      menu = new Menu(text.getTextComponent().getShell(), SWT.POP_UP);
      text.getTextComponent().setMenu(menu);
//    }
//    menu = new Menu(text.getTextComponent().getShell(), SWT.POP_UP);
    
    factory.createStyleMenuItem(menu, "menuPaste", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        text.paste();
      }
      
    });

    factory.createStyleMenuItem(menu, "menuCopy", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(text.getTextComponent().getSelectionText() == null 
            || text.getTextComponent().getSelectionText().isEmpty()) {
          text.getTextComponent().selectAll();
        }
        text.getTextComponent().copy();
      }
    });

    factory.createStyleMenuItem(menu, "menuCopyAll", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(text.getTextComponent().getSelectionText() == null 
            || text.getTextComponent().getSelectionText().isEmpty()) {
          text.getTextComponent().selectAll();
        }
        text.copyAll();
      }
    });

    factory.createStyleMenuItem(menu, SWT.SEPARATOR);
    
    factory.createStyleMenuItem(menu, "menuClear", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        text.getTextComponent().setText("");
      }
    });
    
    factory.createStyleMenuItem(menu, "menuCut", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        if(text.getTextComponent().getSelectionText() == null 
            || text.getTextComponent().getSelectionText().isEmpty()) {
          text.getTextComponent().selectAll();
        }
        text.getTextComponent().cut(); 
      }
    });
    
  }
  
  public Menu getMenu() { return menu; }
  
  public static interface ITextMenu {
    
    public void paste();
    
    public void copyAll();
    
    public Text getTextComponent();
  }

}
