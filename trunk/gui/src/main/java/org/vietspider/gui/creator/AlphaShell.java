/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 21, 2008  
 */
public abstract class AlphaShell {
  
  protected Shell shell;
  protected String id;
  
  protected boolean auto;
  protected Composite composite;
  
  protected int timeout = 1000;
  
  public AlphaShell(Composite comp, String id) {
    this.id = id;
    this.composite = comp;
    
    Shell parent = composite.getShell();
    
    shell = new Shell(parent, SWT.RESIZE | SWT.MODELESS | SWT.ALPHA | SWT.TITLE);
    shell.setImage(parent.getImage());
    
    shell.setLayout(new GridLayout(1, true));
    shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    
    shell.setVisible(false);
    shell.addShellListener(new ShellAdapter(){
      @SuppressWarnings("unused")
      public void shellDeactivated(ShellEvent e) {
        saveShellProperties();
      }
    });
    
    comp.addDisposeListener(new DisposeListener() {
      @SuppressWarnings("unused")
      public void widgetDisposed(DisposeEvent arg0) {
        shell.dispose();
      }
    });
  }
  
  protected void saveShellProperties() {
    Preferences prefs2 = Preferences.userNodeForPackage(getClass());
    Point point = shell.getLocation();
    Point size = shell.getSize();
    try {
      prefs2.put(id+"_location_x", String.valueOf(point.x));
      prefs2.put(id+"_location_y", String.valueOf(point.y));
      prefs2.put(id+"_size_w", String.valueOf(size.x));
      prefs2.put(id+"_size_h", String.valueOf(size.y));
    } catch (Exception e) {
    }
  }
  
  protected void setShellProperties(int x, int y, int x0, int y0, int w, int h, boolean a) {
    this.auto = a;
    if(!auto) shell.setFocus();
    
    if(auto) {
      shell.setAlpha(200);
    } else {
      shell.setAlpha(-1);
    }
    
    Shell parent = shell.getShell();
    int width = w, height = h;
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    try {
      x = Integer.parseInt(prefs.get(id+"_location_x", ""));
    } catch (Exception e) {
      x = parent.getLocation().x + x + x0;
    }
    
    try {
      width = Integer.parseInt(prefs.get(id+"_size_w", ""));
    } catch (Exception e) {
      width = w;
    }
    
    try {
      y = Integer.parseInt(prefs.get(id+"_location_y", ""));
    } catch (Exception e) {
      y = parent.getLocation().y + y + y0;
    }
    
    try {
      height = Integer.parseInt(prefs.get(id+"_size_h", ""));
    } catch (Exception e) {
      height = h;
    }
    
    shell.setLocation(x, y);
    shell.setSize(width, height);
    shell.setVisible(true);
  }
  
  public void invisible() { invisible(timeout);  }
  
  public void invisible(int timeout_) {
    if(!auto) return;
    Runnable timer = new Runnable () {
      public void run () {
        if(isFocusControl()) return;
        shell.setVisible(false);
      }
    };
    UIDATA.DISPLAY.timerExec(timeout_, timer);
  }
  
  abstract boolean isFocusControl() ;

}
