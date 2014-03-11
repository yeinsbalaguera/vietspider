/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.log;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 5, 2010  
 */
public class Snippet244 {
  
  static String SEARCH_STRING = "box";
  
  public static void main(String[] args) {
    final Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    
//    final StyledText text = new StyledText(shell, SWT.NONE);
//    StyleRange style = new StyleRange();
//    style.borderColor = display.getSystemColor(SWT.COLOR_RED);
//    style.borderStyle = SWT.BORDER_SOLID;
//    StyleRange[] styles = {style};
//    String contents = "This demonstrates drawing a box\naround every occurrence of the word\nbox in the StyledText";
//    text.setText(contents);
//    int index = contents.indexOf(SEARCH_STRING);
//    while (index != -1) {
//      text.setStyleRanges(0, 0, new int[] {index, SEARCH_STRING.length()}, styles);
//      index = contents.indexOf(SEARCH_STRING, index + 1);
//    }
    
    FormToolkit toolkit = new FormToolkit(shell.getDisplay());
    ScrolledForm form = toolkit.createScrolledForm(shell);
    form.setText("Hello, Eclipse Forms");
    TableWrapLayout layout = new TableWrapLayout();
    form.getBody().setLayout(layout);

    final FormText rtext = toolkit.createFormText(form.getBody(), false);
    rtext.setFont(UIDATA.FONT_10);
    rtext.addSelectionListener(new SelectionListener() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent arg0) {
        rtext.redraw();
      }
      
      @SuppressWarnings("unused")
      public void widgetDefaultSelected(SelectionEvent arg0) {
        // TODO Auto-generated method stub
        
      }
    });
    
    TableWrapData td = new TableWrapData(TableWrapData.FILL);
    rtext.setLayoutData(td);
    
    StringBuffer buf = new StringBuffer();
    buf.append("<form>");
//    buf.append("<p>");
//    buf.append("Here is some plain text for the text to render; ");
//    buf.append("this text is at <a href=\"http://www.eclipse.org\" nowrap=\"true\">http://www.eclipse.org</a> web site.");
//    buf.append("</p>");
//    buf.append("<p>");
//    buf.append("<span color=\"header\" font=\"header\">This text is in header font and color.</span>");
//    buf.append("</p>");
//    buf.append("<p>This line will contain some <b>bold</b> and some <span font=\"code\">source</span> text. ");
//    buf.append("We can also add <img href=\"image\"/> an image. ");
//    buf.append("</p>");
    buf.append("<p>SOURCE: <a href=\"#ARTICLE.C&#244;ng ngh&#7879;.Ng&#432;&#7901;i lao &#273;&#7897;ng\">ARTICLE.C&#244;ng ngh&#7879;.Ng&#432;&#7901;i lao &#273;&#7897;ng</a> - : 15/11/2010 23:51:47: java.util.ConcurrentModificationException</p><br/>");
    buf.append("<li>A default (bulleted) list item.</li>");
//    buf.append("<li>Another bullet list item.</li>");
//    buf.append("<li style=\"text\" value=\"1.\">A list item with text.</li>");
//    buf.append("<li style=\"text\" value=\"2.\">Another list item with text</li>");
//    buf.append("<li style=\"image\" value=\"image\">List item with an image bullet</li>");
//    buf.append("<li style=\"text\" bindent=\"20\" indent=\"40\" value=\"3.\">A list item with text.</li>");
//    buf.append("<li style=\"text\" bindent=\"20\" indent=\"40\" value=\"4.\">A list item with text.</li>");
//    buf.append("<p>     leading blanks;      more white \n\n new lines   <br/><br/><br/> \n more <b>   bb   </b>  white  . </p>");
    buf.append("</form>");
    
    rtext.setText(buf.toString(), true, false);
    rtext.addHyperlinkListener(new HyperlinkAdapter() {
      public void linkActivated(HyperlinkEvent e) {
        System.out.println("Link active: "+e.getHref());
      }
    });
    
    toolkit.paintBordersFor(form.getBody());
    
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
  }
}
