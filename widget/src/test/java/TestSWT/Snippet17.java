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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

public class Snippet17 {

  public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    Slider slider = new Slider (shell, SWT.HORIZONTAL);
    slider.setBounds (10, 10, 200, 32);
    slider.addListener (SWT.Selection, new Listener () {
      public void handleEvent (Event event) {
        String string = "SWT.NONE";
        switch (event.detail) {
        case SWT.DRAG: string = "SWT.DRAG"; break;
        case SWT.HOME: string = "SWT.HOME"; break;
        case SWT.END: string = "SWT.END"; break;
        case SWT.ARROW_DOWN: string = "SWT.ARROW_DOWN"; break;
        case SWT.ARROW_UP: string = "SWT.ARROW_UP"; break;
        case SWT.PAGE_DOWN: string = "SWT.PAGE_DOWN"; break;
        case SWT.PAGE_UP: string = "SWT.PAGE_UP"; break;
        }
        System.out.println ("Scroll detail -> " + string);
      }
    });
    shell.open ();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
  }
} 