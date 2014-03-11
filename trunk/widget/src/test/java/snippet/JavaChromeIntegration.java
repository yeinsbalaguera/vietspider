/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package snippet;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 18, 2011
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class JavaChromeIntegration {

  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    shell.setMinimumSize(600, 400);

    OleFrame frame = new OleFrame(shell, SWT.NONE);
    OleClientSite site = null;
    try {
      // the VersionIndependentProgID for Chrome Google Frame is ChromeTab.ChromeFrame
      site = new OleClientSite(frame, SWT.NONE, "ChromeTab.ChromeFrame");
      //site = new OleClientSite(frame,       SWT.NONE, "Word.Document");
    } catch (org.eclipse.swt.SWTException e) {
      // probably not installed
      if (e.code == 1004) {
        // ClassID not found in the registry
        System.out.println("Chrome Google Frame is not installed. Available here: http://code.google.com/chrome/chromeframe/");
      } else {
        System.out.println("Could not load Chrome Google Frame. Exception: " + e);
      }
      return;
    }
    frame.pack();

    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }
}

