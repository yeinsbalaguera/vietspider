/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.gui.browser;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.widget.CCombo;
import org.vietspider.ui.widget.UIDATA;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 25, 2006
 */
abstract class UIToolbarBak {
  
  CCombo cbo; 
  Shell popup; 
  org.eclipse.swt.widgets.List list; 
  List<String> links;
  
  protected Workspace workspace;
  protected Composite parent;
  
  public UIToolbarBak(Composite parent, Workspace workspace) {
    this.workspace = workspace;
    this.parent = parent;
    
    cbo = new CCombo(parent, SWT.BORDER | SWT.DROP_DOWN | SWT.V_SCROLL | SWT.H_SCROLL);   
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
//    gridData.verticalIndent = -31;
//    gridData.heightHint = 20;
    cbo.setForeground(UIDATA.FCOLOR); 
    cbo.setFont(UIDATA.FONT_10);
    cbo.setBackground(UIDATA.ADDRESS_BCOLOR);
    if(UIDATA.isMacOS) {
      gridData.heightHint = 30;
      cbo.setFont(UIDATA.FONT_11);
    }
    cbo.setLayoutData(gridData);
    cbo.setVisibleItemCount(15);
    
    cbo.addKeyListener( new KeyAdapter(){
      public void keyPressed(KeyEvent e){       
        keyListener(e);        
      }
      public void keyReleased(KeyEvent e){
        keyListener(e);        
      }
    });   
    
    cbo.addMouseListener( new MouseAdapter(){
      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e){
        popup.setVisible( false);
      }
    });
    
    cbo.text.addFocusListener( new FocusAdapter(){
      @SuppressWarnings("unused")
      public void focusLost(FocusEvent e){
        if(popup.isFocusControl() || list.isFocusControl()) return;
        popup.setVisible(false);
      }
    });
    
//    try{
//      HistoryData io = new HistoryData();
//      String[] values = io.loadRecentDateByArray(3);
//      cbo.setItems( values);
//    }catch( Exception exp){
//      ClientLog.getInstance().setException(cbo.getShell(), exp);
//    }
    
    cbo.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e){
        if(!cbo.popup.isVisible()) save();
      }      
    });
    
    popup = new Shell (cbo.getShell(), SWT.NO_TRIM | SWT.ON_TOP | SWT.BORDER);
    popup.setBackground( cbo.getBackground());
    list = new org.eclipse.swt.widgets.List (popup, SWT.SINGLE | SWT.V_SCROLL );  
    list.setBackground(cbo.getBackground());
    list.setForeground(cbo.getForeground());
    list.setFont(cbo.getFont());
    list.addKeyListener( new KeyAdapter(){
      public void keyPressed(KeyEvent e){       
        keyListListener(e);        
      }     
    });
    
    list.addMouseListener( new MouseAdapter(){
      @SuppressWarnings("unused")
      public void mouseDown(MouseEvent e){
        if( list.getSelectionCount() > 0) cbo.setText(list.getSelection()[0]);
        save();
      }
    });   
    
    cbo.getShell().addControlListener(new ControlListener(){
      @SuppressWarnings("unused")
      public void controlMoved(ControlEvent e){
        popup.setVisible( false);
      }
      @SuppressWarnings("unused")
      public void controlResized(ControlEvent e){
        popup.setVisible( false);
      }
    });
  }
  
  abstract void keyListener(KeyEvent evt);
  abstract void save();
  abstract void keyListListener(KeyEvent evt);
}