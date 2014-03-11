/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.filter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 24, 2009  
 */
public class RenameFilterDialog {
  
  private Shell window;

  public RenameFilterDialog(Shell parent, final FilterDataDialog dialog, final String name) {
    window = new Shell (parent, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.APPLICATION_MODAL);
    FormLayout formLayout = new FormLayout ();
    formLayout.marginWidth = 10;
    formLayout.marginHeight = 10;
    formLayout.spacing = 10;
    window.setLayout (formLayout);

    ClientRM clientRM = FilterDataPlugin.getResources();
    ApplicationFactory factory = new ApplicationFactory(window, clientRM, getClass().getName());

    Label label = factory.createLabel("name");//new Label (dialog, SWT.NONE);
    FormData data = new FormData ();
    label.setLayoutData (data);

    Button cancel = new Button(factory.getComposite(), SWT.PUSH);;
    cancel.setText("Đóng");
    data = new FormData ();
    data.width = 60;
    data.right = new FormAttachment (100, 0);
    data.bottom = new FormAttachment (100, 0);
    cancel.setLayoutData (data);
    cancel.addSelectionListener (new SelectionAdapter () {
      @SuppressWarnings("unused")
      public void widgetSelected (SelectionEvent e) {
        window.close ();
      }
    });

    final Text text = new Text (window, SWT.BORDER);
    text.setText(name);
    data = new FormData ();
    data.width = 200;
    data.left = new FormAttachment (label, 0, SWT.DEFAULT);
    data.right = new FormAttachment (100, 0);
    data.top = new FormAttachment (label, 0, SWT.CENTER);
    data.bottom = new FormAttachment (cancel, 0, SWT.DEFAULT);
    text.setLayoutData (data);

    Button ok = new Button(factory.getComposite(), SWT.PUSH);;
    ok.setText("Ok");
    data = new FormData ();
    data.width = 60;
    data.right = new FormAttachment (cancel, 0, SWT.DEFAULT);
    data.bottom = new FormAttachment (100, 0);
    ok.setLayoutData (data);
    ok.addSelectionListener (new SelectionAdapter () {
      @SuppressWarnings("unused")
      public void widgetSelected (SelectionEvent e) {
        dialog.deleteFilter(new String[]{name});
        String newName = text.getText();
        dialog.saveFilter(newName, dialog.getKeyWord());
        window.close ();
      }
    });

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    window.setImage(parent.getImage());
    window.setLocation(x, y);

    window.setDefaultButton (ok);
    window.pack ();
//    XPWindowTheme.setWin32Theme(dialog);
    window.open ();
  }
  
  

}
