/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package snippet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 27, 2008  
 */
public class Snippet270 {

  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setText("Main Window");
    shell.setLayout(new FillLayout());
    final Browser browser;
    try {
      browser = new Browser(shell, SWT.NONE);
    } catch (SWTError e) {
      System.out.println("Could not instantiate Browser: " + e.getMessage());
      return;
    }
    initialize(display, browser);
    shell.open();
    browser.setUrl("http://www.eclipse.org");
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }

  /* register WindowEvent listeners */
  static void initialize(final Display display, Browser browser) {
    browser.addOpenWindowListener(new OpenWindowListener() {
      public void open(WindowEvent event) {
        if (!event.required) return;  /* only do it if necessary */
        Shell shell = new Shell(display);
        shell.setText("New Window");
        shell.setLayout(new FillLayout());
        Browser browser = new Browser(shell, SWT.NONE);
        initialize(display, browser);
        event.browser = browser;
      }
    });
    browser.addVisibilityWindowListener(new VisibilityWindowListener() {
      public void hide(WindowEvent event) {
        Browser browser = (Browser)event.widget;
        Shell shell = browser.getShell();
        shell.setVisible(false);
      }
      public void show(WindowEvent event) {
        Browser browser = (Browser)event.widget;
        final Shell shell = browser.getShell();
        if (event.location != null) shell.setLocation(event.location);
        if (event.size != null) {
          Point size = event.size;
          shell.setSize(shell.computeSize(size.x, size.y));
        }
        shell.open();
      }
    });
    browser.addCloseWindowListener(new CloseWindowListener() {
      public void close(WindowEvent event) {
        Browser browser = (Browser)event.widget;
        Shell shell = browser.getShell();
        shell.close();
      }
    });
  }
}
