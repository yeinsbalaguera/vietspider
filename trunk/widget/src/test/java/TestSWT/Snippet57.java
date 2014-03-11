/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package TestSWT;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 18, 2007  
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class Snippet57 {

  public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    ProgressBar bar = new ProgressBar (shell, SWT.SMOOTH);
    bar.setBounds (10, 10, 200, 32);
    shell.open ();
    for (int i=0; i<=bar.getMaximum (); i++) {
      try {Thread.sleep (100);} catch (Throwable th) {}
      bar.setSelection (i);
    }
    while (!shell.isDisposed ()) {
      if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
  }
} 
