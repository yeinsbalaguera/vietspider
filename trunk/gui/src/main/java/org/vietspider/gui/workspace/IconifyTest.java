/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.workspace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 3, 2011  
 */
public class IconifyTest {
  /**
   * @param args
   */
  public static void main(String[] args) {
      try {
          final Display d = new Display();
          final Shell shell = new Shell(d);
          shell.setText("Parent Shell");
          shell.setSize(100, 100);

          Listener l = new Listener() {
              public void handleEvent(Event event) {
                  System.out.println(event);
              }
          };

          shell.addListener(SWT.Iconify, l);
          shell.addListener(SWT.Deiconify, l);

          shell.open();
          while (!shell.isDisposed()) {
              if (!d.readAndDispatch())
                  d.sleep();
          }

          d.dispose();

      } catch (Exception e) {
          e.printStackTrace();
      }
  }
}
