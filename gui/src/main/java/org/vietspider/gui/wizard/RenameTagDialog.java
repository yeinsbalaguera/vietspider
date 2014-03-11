/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
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
public class RenameTagDialog {
  
  private Shell dialog;
  private List list;

  public RenameTagDialog(Shell parent, List _list) {
    this.list = _list;
    dialog = new Shell (parent, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.APPLICATION_MODAL);
    FormLayout formLayout = new FormLayout ();
    formLayout.marginWidth = 10;
    formLayout.marginHeight = 10;
    formLayout.spacing = 10;
    dialog.setLayout (formLayout);

    ClientRM clientRM = ChannelWizard.getResources();
    ApplicationFactory factory = new ApplicationFactory(dialog, clientRM, getClass().getName());

    Label label = factory.createLabel("name");//new Label (dialog, SWT.NONE);
    FormData data = new FormData ();
    label.setLayoutData (data);

    Button cancel = factory.createButton("cancel");
    data = new FormData ();
    data.width = 60;
    data.right = new FormAttachment (100, 0);
    data.bottom = new FormAttachment (100, 0);
    cancel.setLayoutData (data);
    cancel.addSelectionListener (new SelectionAdapter () {
      @SuppressWarnings("unused")
      public void widgetSelected (SelectionEvent e) {
        dialog.close ();
      }
    });

    final Text text = new Text (dialog, SWT.BORDER);
    text.setText(list.getSelection()[0]);
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
        int idx = list.getSelectionIndex();
        String name = text.getText();
        if(name == null || (name = name.trim()).isEmpty()) return;
        name = name.replace(' ', '_');
        list.setItem(idx, name);
        dialog.close ();
      }
    });

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300)/ 2;
    dialog.setImage(parent.getImage());
    dialog.setLocation(x, y);

    dialog.setDefaultButton (ok);
    dialog.pack ();
//    XPWindowTheme.setWin32Theme(dialog);
    dialog.open ();
  }

}
