/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 3, 2009  
 */
public class VButton {

  private Button button;
//  private CButton cbutton;


  public VButton(Composite parent, int style) {
    if(style == SWT.CHECK || style == SWT.RADIO) {
      button = new Button(parent, style);
      return;
    }
    
//    if(XPWidgetTheme.isPlatform()) {
//      cbutton = new CButton(parent, style, XPWidgetTheme.THEME);
//    } else { 
//      button = new Button(parent, style);
//    }
  }


  public Control getButton() {
//    if(cbutton != null) return cbutton;
    return button;
  }

  public void setImage(Image image) {
//    if(cbutton != null) {
//      cbutton.setImage(image);
//    } else {
      button.setImage(image);
//    }
  }

  public void setFont(Font font) {
//    if(cbutton != null)  return;
    button.setFont(font);
  }

  public void setText(String string) {
//    if(cbutton != null) {
//      cbutton.setText(string);
//    } else {
      button.setText(string);
//    }
  }

  public void setToolTipText(String string) {
//    if(cbutton != null) return;
    button.setToolTipText(string);
  }

  public void addSelectionListener(SelectionListener listener) {
//    if(cbutton != null) {
//      cbutton.addSelectionListener(listener);
//    } else {
      button.addSelectionListener(listener);
//    }
  }

  public void setBounds(int x, int y, int width, int height) {
//    if(cbutton != null) {
//      cbutton.setBounds(new Rectangle(x, y, width, height));
//    } else {
      button.setBounds(x, y, width, height);
//    }
  }

  public void setLayoutData(Object layoutData) {
//    if(cbutton != null) {
//      cbutton.setLayoutData(layoutData);
//    } else {
      button.setLayoutData(layoutData);
//    }
  }

  public void setEnabled(boolean enabled) {
//    if(cbutton != null) {
//      cbutton.setEnabled(enabled);
//    } else {
      button.setEnabled(enabled);
//    }
  }

  public void addFocusListener(FocusListener listener) {
//    if(cbutton != null) {
//      cbutton.addFocusListener(listener);
//    } else {
      button.addFocusListener(listener);
//    }
  }

  public void addKeyListener(KeyListener listener) {
//    if(cbutton != null) {
//      cbutton.addKeyListener(listener);
//    } else {
      button.addKeyListener(listener);
//    }
  }

  public void setVisible(boolean visible) {
//    if(cbutton != null) {
//      cbutton.setVisible(visible);
//    } else {
      button.setVisible(visible);
//    }
  }

  public void setSelection(boolean selected) {
//    if(cbutton != null) {
//      cbutton.setSelection(selected);
//    } else {
      button.setSelection(selected);
//    }
  }

  public boolean getSelection() {
//    if(cbutton != null) {
//      return cbutton.getSelection();
//    } 
      return button.getSelection();
  }

  public boolean isEnabled() {
//    if(cbutton != null) {
//      return cbutton.isEnabled();
//    } 
      return button.isEnabled();
  }

  public Display getDisplay() {
//    if(cbutton != null) {
//      cbutton.getDisplay();
//    } 
    return button.getDisplay();
  }

  public boolean isDisposed() {
//    if(cbutton != null) {
//      return cbutton.isDisposed();
//    } 
    return button.isDisposed();
  }

  public void pack() {
//    if(cbutton != null) {
//      cbutton.pack();
//    } else {
      button.pack();
//    }
  }

  public Shell getShell() {
//    if(cbutton != null) {
//      return cbutton.getShell();
//    } 
    return button.getShell();
  }

  public String getText() {
//    if(cbutton != null) {
//      return cbutton.getText();
//    } 
    return button.getText();
  }

  public boolean setFocus() {
//    if(cbutton != null) {
//      return cbutton.setFocus();
//    } 
    return button.setFocus();
  }
}
