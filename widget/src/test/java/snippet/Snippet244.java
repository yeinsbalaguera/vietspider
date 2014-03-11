/***************************************************************************
 * Copyright 2001-2012 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/
package snippet;

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Jan 4, 2012  
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Snippet244 {
  static String SEARCH_STRING = "box";
  public static void main(String[] args) {
    final Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    final StyledText text = new StyledText(shell, SWT.NONE);
    StyleRange style = new StyleRange();
    text.addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent evt) {
        if(evt.keyCode == SWT.CR) {
          int delete = text.getCaretOffset();
          if(delete < 0) return;
          System.out.println(delete);
          String value = text.getText(0, delete);
          value = value.substring(0, value.length()-2);
          text.replaceTextRange(0, delete, value);
        }
      }
      
      @Override
      public void keyPressed(KeyEvent evt) {
       if(evt.keyCode == SWT.CR) {
         evt.keyCode = ' ';
         evt.doit = false;
       }
      }
    });
    style.borderColor = display.getSystemColor(SWT.COLOR_RED);
    style.borderStyle = SWT.BORDER_SOLID;
    StyleRange[] styles = {style};
    String contents = "This demonstrates drawing a box\naround every occurrence of the word\nbox in the StyledText";
    text.setText(contents);
    int index = contents.indexOf(SEARCH_STRING);
    while (index != -1) {
      text.setStyleRanges(0, 0, new int[] {index, SEARCH_STRING.length()+1}, styles);
      index = contents.indexOf(SEARCH_STRING, index + 1);
    }
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
  }
}