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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class Snippet130 {

  public static void main(String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setLayout(new TableWrapLayout());

    ExpandableComposite ec1 = new ExpandableComposite(shell, ExpandableComposite.TREE_NODE | ExpandableComposite.EXPANDED);
    ec1.setText("This is the title");
    
    
    final FormText text = new FormText(ec1, SWT.MULTI);
    text.setText(getHTML(), true, true);
    final int[] nextId = new int[1];
    text.addHyperlinkListener(new HyperlinkAdapter() {
	    @Override
      public void linkActivated(HyperlinkEvent e) { 
	      System.out.println("Link active: "+e.getHref()); 
	   }
	  });
    ec1.setClient(text);
    Button b = new Button(shell, SWT.PUSH);
    b.setText("invoke long running job");
//    b.addSelectionListener(new SelectionAdapter() {
//      public void widgetSelected(SelectionEvent e) {
//        Runnable longJob = new Runnable() {
//          boolean done = false;
//          int id;
//          public void run() {
//            Thread thread = new Thread(new Runnable() {
//              public void run() {
//                id = nextId[0]++;
//                display.syncExec(new Runnable() {
//                  public void run() {
//                    if (text.isDisposed()) return;
//                    text.append("\nStart long running task "+id);
//                  }
//                });
//                for (int i = 0; i < 100000; i++) {
//                  if (display.isDisposed()) return;
//                  System.out.println("do task that takes a long time in a separate thread "+id);
//                }
//                if (display.isDisposed()) return;
//                display.syncExec(new Runnable() {
//                  public void run() {
//                    if (text.isDisposed()) return;
//                    text.append("\nCompleted long running task "+id);
//                  }
//                });
//                done = true;
//                display.wake();
//              }
//            });
//            thread.start();
//            while (!done && !shell.isDisposed()) {
//              if (!display.readAndDispatch())
//                display.sleep();
//            }
//          }
//        };
//        BusyIndicator.showWhile(display, longJob);
//      }
//    });
    shell.setSize(250, 150);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
  

  private static String getHTML() {

    StringBuffer buf = new StringBuffer();

    buf.append("<form>");

    buf.append("<p>");

    buf.append("Here is some plain text for the text to render ");

    buf.append("this text is at <a href=\"http://www.eclipse.org\" nowrap=\"true\">asdf</a> web site.");

    buf.append("</p>");

    buf.append("<p>");

    buf.append("<span color=\"header\" font=\"header\">This text is in header font and color.</span>");

    buf.append("</p>");

    buf.append("<p>This line will contain some <b>bold</b> and some <span font=\"text\">source</span> text. ");

    buf.append("We can also add <img href=\"image\"/> an image. ");
    
    buf.append("//dieuhoakhongkhi.vn/modules.php?name=Catalog&amp;opcase=viewcatalogcat&amp;maincat=22&amp;pcid=63");
    
    buf.append("</p>");

    buf.append("<li>A default (bulleted) list item.</li>");

    buf.append("<li>Another bullet list item.</li>");

    buf.append("<li style=\"text\" value=\"1.\">A list item with text.</li>");

    buf.append("<li style=\"text\" value=\"2.\">Another list item with text</li>");

    buf.append("<li style=\"image\" value=\"image\">List item with an image bullet</li>");

    buf.append("<li style=\"text\" bindent=\"20\" indent=\"40\" value=\"3.\">A list item with text.</li>");

    buf.append("<li style=\"text\" bindent=\"20\" indent=\"40\" value=\"4.\">A list item with text.</li>");

    buf.append("</form>");

    return buf.toString();

  }


  
}