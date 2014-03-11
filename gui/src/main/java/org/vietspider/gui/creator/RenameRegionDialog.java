/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

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
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 24, 2009  
 */
public class RenameRegionDialog {
  
  private Shell dialog;
  private DataSelectorExplorer explorer;

  public RenameRegionDialog(Shell parent, DataSelectorExplorer _explorer) {
    this.explorer = _explorer;
    dialog = new Shell (parent, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.APPLICATION_MODAL);
    FormLayout formLayout = new FormLayout ();
    formLayout.marginWidth = 10;
    formLayout.marginHeight = 10;
    formLayout.spacing = 10;
    dialog.setLayout (formLayout);

    ApplicationFactory factory = new ApplicationFactory(dialog, "Creator", RenameDialog.class.getName());

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
    text.setText(explorer.getSelectedRegion());
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
        explorer.renameRegion(text.getText());
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
