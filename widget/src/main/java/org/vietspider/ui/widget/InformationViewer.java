/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.widget;

import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 22, 2008  
 */
public class InformationViewer {
  
  private Shell shell;
  private Text text;

  public InformationViewer(Shell parent, String value, 
      String [] buttons, SelectionAdapter [] selections) {
    shell = new Shell(parent, SWT.RESIZE | SWT.CLOSE);
    shell.setImage(parent.getImage());
    loadShellProperties();
    
    shell.setLayout(new GridLayout(1, true));
    
    text = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    text.setEditable(false);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    text.setLayoutData(gridData);
    text.setFont(UIDATA.FONT_10);
    text.setText(value);
    text.addFocusListener(new FocusAdapter() {
      @SuppressWarnings("unused")
      public void focusLost(FocusEvent evt) {
        Runnable timer = new Runnable () {
          public void run () {
            if(text.isDisposed()) return;
            if(text.isFocusControl() || shell.isFocusControl()) return;
            saveShellProperties() ;
            shell.dispose();
          }
        };
        UIDATA.DISPLAY.timerExec(5*60*1000, timer);
      }
    });
    
    shell.addShellListener(new ShellAdapter(){
      @SuppressWarnings("unused")
      public void shellClosed(ShellEvent e) {
        close();
      }
    });
    
    if(buttons != null && buttons.length > 0) {
      Composite bottom = new Composite(shell, SWT.NONE);
      gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
      bottom.setLayoutData(gridData);
      RowLayout rowLayout = new RowLayout();
      bottom.setLayout(rowLayout);
      rowLayout.justify = true;

      for(int i = 0; i < buttons.length; i++) {
        Button button  = new Button(bottom, SWT.BORDER);
        if(i < selections.length 
            && selections[i] != null)  {
          button.addSelectionListener(selections[i]);
        }
        button.setText(buttons[i]); 
      }
    }
    
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
  }
  
  public void close() {
    saveShellProperties() ;
    shell.dispose();
  }
  
  protected void saveShellProperties() {
    Preferences prefs2 = Preferences.userNodeForPackage(getClass());
    if(shell == null || shell.isDisposed()) return;
    Point point = shell.getLocation();
    Point size = shell.getSize();
    String id = "executorViewer";
    try {
      prefs2.put(id+"_location_x", String.valueOf(point.x));
      prefs2.put(id+"_location_y", String.valueOf(point.y));
      prefs2.put(id+"_size_w", String.valueOf(size.x));
      prefs2.put(id+"_size_h", String.valueOf(size.y));
    } catch (Exception e) {
    }
  }
  
  protected void loadShellProperties() {
    Shell parent = shell.getShell();
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    String id = "executorViewer";
    
    int x = 0;
    int y = 0;
    
    try {
      x = Integer.parseInt(prefs.get(id+"_location_x", ""));
    } catch (Exception e) {
      x = (parent.getLocation().x + parent.getSize().x)/2 - 150;
    }
    
    int width = 400;
    try {
      width = Integer.parseInt(prefs.get(id+"_size_w", ""));
    } catch (Exception e) {
      width = 400;
    }
    
    try {
      y = Integer.parseInt(prefs.get(id+"_location_y", ""));
    } catch (Exception e) {
      y = (parent.getLocation().y + parent.getSize().y)/2 - 100;
    }
    
    int height = 300;
    try {
      height = Integer.parseInt(prefs.get(id+"_size_h", ""));
    } catch (Exception e) {
      height = 300;
    }
    
    shell.setLocation(x, y);
    shell.setSize(width, height);
  }
  
  public void setValue(String value) {
    if(text.isDisposed()) return;
    text.setText(value); 
  }
}
