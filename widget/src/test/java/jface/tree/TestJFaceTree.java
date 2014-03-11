/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package jface.tree;

import java.io.File;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 12, 2009  
 */
public class TestJFaceTree {
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());

    TreeViewer tree = new TreeViewer(shell);
    tree.setContentProvider(new TreeContentProvider());
    tree.setLabelProvider(new TreeLabelProvider());
    tree.setInput(new FolderNode(new File("D:/temp")));

    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
}
