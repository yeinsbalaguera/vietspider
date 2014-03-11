/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package snippet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 12, 2009  
 */
public class Snippet226 {
  public static void main(String [] args) {
    final Display display = new Display();
    Shell shell = new Shell(display);
    shell.setText("Custom gradient selection for Tree");
    shell.setLayout(new FillLayout());
    final Tree tree = new Tree(shell, SWT.MULTI | SWT.FULL_SELECTION);
    tree.setHeaderVisible(false);
    tree.setLinesVisible(false);
    int columnCount = 4;
    for (int i=0; i<columnCount; i++) {
      TreeColumn column = new TreeColumn(tree, SWT.NONE);
      column.setText("Column " + i);  
    }
    int itemCount = 3;
    for (int i=0; i<itemCount; i++) {
      TreeItem item1 = new TreeItem(tree, SWT.NONE);
      item1.setText("item "+i);
      for (int c=1; c < columnCount; c++) {
        item1.setText(c, "item ["+i+"-"+c+"]");
      }
      for (int j=0; j<itemCount; j++) {
        TreeItem item2 = new TreeItem(item1, SWT.NONE);
        item2.setText("item ["+i+" "+j+"]");
        for (int c=1; c<columnCount; c++) {
          item2.setText(c, "item ["+i+" "+j+"-"+c+"]");
        }
        for (int k=0; k<itemCount; k++) {
          TreeItem item3 = new TreeItem(item2, SWT.NONE);
          item3.setText("item ["+i+" "+j+" "+k+"]");
          for (int c=1; c<columnCount; c++) {
            item3.setText(c, "item ["+i+" "+j+" "+k+"-"+c+"]");
          }
        }
      }
    }

    /*
     * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
     * Therefore, it is critical for performance that these methods be
     * as efficient as possible.
     */
    tree.addListener(SWT.EraseItem, new Listener() {
      public void handleEvent(Event event) {
        event.detail &= ~SWT.HOT;
        if ((event.detail & SWT.SELECTED) != 0) {
          GC gc = event.gc;
          Rectangle area = tree.getClientArea();
          /*
           * If you wish to paint the selection beyond the end of
           * last column, you must change the clipping region.
           */
          int columnCount = tree.getColumnCount();
          if (event.index == columnCount - 1 || columnCount == 0) {
            int width = area.x + area.width - event.x;
            if (width > 0) {
              Region region = new Region();
              gc.getClipping(region);
              region.add(event.x, event.y, width, event.height); 
              gc.setClipping(region);
              region.dispose();
            }
          }
          gc.setAdvanced(true);
          if (gc.getAdvanced()) gc.setAlpha(127);               
          Rectangle rect = event.getBounds();
          Color foreground = gc.getForeground();
          Color background = gc.getBackground();
          gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
          gc.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
          gc.fillGradientRectangle(0, rect.y, 500, rect.height, false);
          // restore colors for subsequent drawing
          gc.setForeground(foreground);
          gc.setBackground(background);
          event.detail &= ~SWT.SELECTED;          
        }           
      }
    });   
    for (int i=0; i<columnCount; i++) {
      tree.getColumn(i).pack();
    } 
    tree.setSelection(tree.getItem(0));
    shell.setSize(500, 200);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();  
  }

}
