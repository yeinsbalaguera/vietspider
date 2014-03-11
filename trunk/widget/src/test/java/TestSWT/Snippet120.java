/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package TestSWT;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 18, 2007  
 */
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class Snippet120 {

public static void main (String [] args) {
  Display display = new Display ();
  Shell shell = new Shell (display);
  shell.setSize (200, 200);
  Monitor primary = display.getPrimaryMonitor ();
  Rectangle bounds = primary.getBounds ();
  Rectangle rect = shell.getBounds ();
  int x = bounds.x + (bounds.width - rect.width) / 2;
  int y = bounds.y + (bounds.height - rect.height) / 2;
  shell.setLocation (x, y);
  shell.open ();
  while (!shell.isDisposed ()) {
    if (!display.readAndDispatch ()) display.sleep ();
  }
  display.dispose ();
}
}