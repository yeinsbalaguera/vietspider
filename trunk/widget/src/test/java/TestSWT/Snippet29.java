/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package TestSWT;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 12, 2007  
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class Snippet29 {

  public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    Menu bar = new Menu (shell, SWT.BAR);
    shell.setMenuBar (bar);
    MenuItem fileItem = new MenuItem (bar, SWT.CASCADE);
    fileItem.setText ("&File");
    Menu submenu = new Menu (shell, SWT.DROP_DOWN);
    fileItem.setMenu (submenu);
    MenuItem item = new MenuItem (submenu, SWT.PUSH);
    item.addListener (SWT.Selection, new Listener () {
      public void handleEvent (Event e) {
        System.out.println ("Select All");
      }
    });
    item.setText ("Select &All\tCtrl+A");
    item.setAccelerator (SWT.MOD1 + 'A');
    shell.setSize (200, 200);
    shell.open ();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
  }

} 
