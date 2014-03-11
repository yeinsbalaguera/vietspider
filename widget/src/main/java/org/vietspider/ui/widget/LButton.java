/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 18, 2010  
 */
public class LButton extends Button {
  
  private boolean enable = true;
  private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
  
//  private Color foreground = null;
//  private Color disableColor = null;

  public LButton(Composite parent, int style) {
    super (parent, style);
    
    super.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent e) {
        if(!enable) {
          MessageBox messageBox = new MessageBox (getShell(), SWT.OK | SWT.ICON_WARNING);
          messageBox.setText ("Warning!");
          messageBox.setMessage ("Disable function!");
          messageBox.open ();
          return;
        }
        
        for(int i = 0; i < selectionListeners.size(); i++) {
          selectionListeners.get(i).widgetSelected(e);
        }
      }
      
      public void widgetDefaultSelected(SelectionEvent e) {
        if(!enable) return;
        for(int i = 0; i < selectionListeners.size(); i++) {
          selectionListeners.get(i).widgetDefaultSelected(e);
        }
      }
    });
    
//    foreground = getForeground();
//    disableColor = new Color(getDisplay(), 229, 229, 229);
  }
  
  public void setEnabled(boolean value) {
    this.enable = value;
//    if(enable) {
//      setForeground(foreground);
//    } else {
//      setBackground(disableColor);
//    }
//    if("win32".equalsIgnoreCase(SWT.getPlatform())) {
//      super.setEnabled(value);
//      return;
//    }
  }

//  @Override
//  public void setForeground(Color color) {
//    foreground = color;
//    if(enable) super.setForeground(foreground);
//  }

  @Override
  public void addSelectionListener(SelectionListener listener) {
    checkWidget ();
    if (listener == null) return;
    selectionListeners.add(listener);
  }

  @Override
  public void removeSelectionListener(SelectionListener listener) {
    selectionListeners.remove(listener);
  }
  
  @Override
  protected void checkSubclass () {
   
  }
  

  
  
}
