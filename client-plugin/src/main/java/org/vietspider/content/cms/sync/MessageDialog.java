/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.cms.sync;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.ui.XPWidgetTheme;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 26, 2009  
 */
public class MessageDialog {

  private Shell shell;
  private Text text;
  
  private ArrayList<Message> messages = new ArrayList<Message>();

  public MessageDialog(Shell parent) {
    shell = new Shell(parent, SWT.CLOSE | SWT.RESIZE);
    
    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        new ShellSetter(MessageDialog.class, shell);
        shell.setVisible(false);
        e.doit = false;
      }
    });

    shell.setLayout(new GridLayout(1, false));
    GridData gridData = new GridData(GridData.FILL_BOTH);
    text = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    text.setLayoutData(gridData);
    text.setEditable(false);

    Composite bottom = new Composite(shell, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    // gridData.horizontalSpan = 2;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;

    Button butOk = new Button(bottom, SWT.PUSH);
    butOk.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        new ShellSetter(MessageDialog.class, shell);
        shell.setVisible(false);
      }
    });
    butOk.setText("Ok");

    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300) / 2;
    shell.setImage(parent.getImage());
    new ShellGetter(MessageDialog.class, shell, 400, 250, x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.setVisible(false);
  }
  
  public void addMessage(boolean showMessage, String value) {
    messages.add(new Message(value));
    if(!showMessage) {
      ClientLog.getInstance().setMessage(null, new Exception(value));
    }
  }
  
  public void setMessage(boolean showMessage, String value) {
    Iterator<Message> iterator = messages.iterator();
    while(iterator.hasNext()) {
      Message message = iterator.next();
      if(message.isTimeout()) iterator.remove();
    }
    
    messages.add(new Message(value));
    if(!showMessage) {
      ClientLog.getInstance().setMessage(null, new Exception(value));
    }
    StringBuilder builder = new StringBuilder();
    for(int i = messages.size() - 1; i > -1; i--) {
      if(messages.get(i).getValue().length() < 5) continue;
      if(builder.length() > 0) builder.append('\n');
      builder.append(messages.get(i).getValue());
    }
    text.setText(builder.toString());
    shell.setVisible(false);
    if(showMessage) {
      shell.setVisible(true);
    } 
  }


  private static class Message {
    private long start = System.currentTimeMillis();

    private String value;

    Message(String value) {
      this.value = value;
    }

    boolean isTimeout() {
      return System.currentTimeMillis() - start >=  30*1000;
    }

    public String getValue() {
      return value;
    }
  }
  
}
