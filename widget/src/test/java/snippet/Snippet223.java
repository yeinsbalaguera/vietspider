/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package snippet;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 4, 2008  
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;

public class Snippet223 {

public static void main (String [] args) {
  Display display = new Display ();
  Shell shell = new Shell (display);
  shell.setLayout(new FillLayout());
  shell.setText("ExpandBar Example");
  ExpandBar bar = new ExpandBar (shell, SWT.V_SCROLL);
  Image image = display.getSystemImage(SWT.ICON_QUESTION);
  
  // First item
  Composite composite = new Composite (bar, SWT.NONE);
  GridLayout layout = new GridLayout ();
  layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
  layout.verticalSpacing = 10;
  composite.setLayout(layout);
  Button cbutton = new Button (composite, SWT.PUSH);
  cbutton.setText("SWT.PUSH");
  Button button = new Button (composite, SWT.RADIO);
  button.setText("SWT.RADIO");
  button = new Button (composite, SWT.CHECK);
  button.setText("SWT.CHECK");
  button = new Button (composite, SWT.TOGGLE);
  button.setText("SWT.TOGGLE");
  ExpandItem item0 = new ExpandItem (bar, SWT.NONE, 0);
  item0.setText("What is your favorite button");
  item0.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
  item0.setControl(composite);
  item0.setImage(image);
  
  // Second item
  composite = new Composite (bar, SWT.NONE);
  layout = new GridLayout (2, false);
  layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
  layout.verticalSpacing = 10;
  composite.setLayout(layout);  
  Label label = new Label (composite, SWT.NONE);
  label.setImage(display.getSystemImage(SWT.ICON_ERROR));
  label = new Label (composite, SWT.NONE);
  label.setText("SWT.ICON_ERROR");
  label = new Label (composite, SWT.NONE);
  label.setImage(display.getSystemImage(SWT.ICON_INFORMATION));
  label = new Label (composite, SWT.NONE);
  label.setText("SWT.ICON_INFORMATION");
  label = new Label (composite, SWT.NONE);
  label.setImage(display.getSystemImage(SWT.ICON_WARNING));
  label = new Label (composite, SWT.NONE);
  label.setText("SWT.ICON_WARNING");
  label = new Label (composite, SWT.NONE);
  label.setImage(display.getSystemImage(SWT.ICON_QUESTION));
  label = new Label (composite, SWT.NONE);
  label.setText("SWT.ICON_QUESTION");
  ExpandItem item1 = new ExpandItem (bar, SWT.NONE, 1);
  item1.setText("What is your favorite icon");
  item1.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
  item1.setControl(composite);
  item1.setImage(image);
  
  // Third item
  composite = new Composite (bar, SWT.NONE);
  layout = new GridLayout (2, true);
  layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
  layout.verticalSpacing = 10;
  composite.setLayout(layout);
  label = new Label (composite, SWT.NONE);
  label.setText("Scale"); 
  new Scale (composite, SWT.NONE);
  label = new Label (composite, SWT.NONE);
  label.setText("Spinner"); 
  new Spinner (composite, SWT.BORDER);
  label = new Label (composite, SWT.NONE);
  label.setText("Slider");  
  new Slider (composite, SWT.NONE);
  ExpandItem item2 = new ExpandItem (bar, SWT.NONE, 2);
  item2.setText("What is your favorite range widget");
  item2.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
  item2.setControl(composite);
  item2.setImage(image);
  
  item1.setExpanded(true);
  bar.setSpacing(8);
  shell.setSize(400, 350);
  shell.open();
  while (!shell.isDisposed ()) {
    if (!display.readAndDispatch ()) {
      display.sleep ();
    }
  }
  image.dispose();
  display.dispose();
}

}