/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.source.SourcesHandler;
import org.vietspider.model.Source;
import org.vietspider.net.server.CopySource;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 20, 2008  
 */
public class RenameDialog {

  private SourcesHandler handler;
  private CopySource copySource;
  private Shell dialog;

  public RenameDialog(Shell parent, SourcesHandler handler, CopySource copy) {
    dialog = new Shell (parent, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.APPLICATION_MODAL);
    FormLayout formLayout = new FormLayout ();
    formLayout.marginWidth = 10;
    formLayout.marginHeight = 10;
    formLayout.spacing = 10;
    dialog.setLayout (formLayout);

    this.copySource = copy;
    this.handler = handler;

    ApplicationFactory factory = new ApplicationFactory(dialog, "Creator", getClass().getName());

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
    text.setText(copySource.getSrcNames()[0]);
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
        rename(text.getText());
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

  private void rename(final String newName) {
    if(newName.trim().isEmpty()) return;
    
    Worker excutor = new Worker() {

      private String message = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {}

      public void execute() {
        String [] names = copySource.getSrcNames();
        try {
          String group = copySource.getSrcGroup();
          SourcesClientHandler client = new SourcesClientHandler(group);
          String category = copySource.getSrcCategory();
          Source source = client.loadSource(category, names[0]);
          if(source == null) return;;
          Source newSource = source.clone();
          newSource.setName(newName);
          client.saveSource(newSource);
          client.deleteSources(category, new String[]{names[0]});
        } catch (Exception e) {
          if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
            message = e.getMessage();
          } else {
            message = e.toString();
          }
        }
      }

      public void after() {
        if(message != null && !message.trim().isEmpty()) {
          MessageBox msg = new MessageBox (dialog, SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
          return;
        }
        handler.update();
        dialog.close ();
      }
    };
    WaitLoading loading = new WaitLoading(dialog, excutor);
    loading.open();
  }
}
