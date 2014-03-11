/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.browser;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 3, 2011  
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class WebKitBrowser {

  public static void main(String [] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    shell.setText("WebKit");
    final Browser browser;
    try {
      browser = new Browser(shell, SWT.WEBKIT);
    } catch (SWTError e) {
      System.out.println("Could not instantiate Browser: " + e.getMessage());
      display.dispose();
      return;
    }
    shell.open();
    browser.setUrl("http://webkit.org");
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
  }
}
