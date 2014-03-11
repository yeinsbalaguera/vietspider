/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.crawler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 25, 2009  
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class VerticalWrapList {
  public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    shell.setLayout (new FillLayout());

    final Canvas myList = new Canvas (shell, SWT.BORDER);
    RowLayout rowLayout = new RowLayout ();
    rowLayout.type = SWT.VERTICAL;
    rowLayout.spacing = 6;
    myList.setLayout (rowLayout);

    for (int i = 0; i < 480; i++) {
      Label label = new Label (myList, SWT.NONE);
      label.setText ("CH" + i);
      label.addMouseListener (new MouseAdapter () {
        public void mouseDown (MouseEvent e) {
          System.out.println (e.widget);
          e.widget.dispose();
          myList.layout ();
        }
      });
    }


    shell.setSize (600, 800);
    shell.open ();

    while (!shell.isDisposed ()) {
      if (!display.readAndDispatch ())
        display.sleep ();
    }
    display.dispose ();
  }
}