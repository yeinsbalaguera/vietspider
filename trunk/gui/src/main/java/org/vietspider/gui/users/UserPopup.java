/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.users;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.OrganizationClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 14, 2008  
 */
public class UserPopup {
  
  private List uiList;
  private ArrayList<String> listValues = new ArrayList<String>();
  
  private  final Shell shell ;
  
  public UserPopup(Shell parent) {
    shell = new Shell(parent, SWT.CLOSE | SWT.APPLICATION_MODAL);
    int x = parent.getLocation().x + 420;
    int y = parent.getLocation().y + 90;
    shell.setLocation(x, y);
    shell.setImage(parent.getImage());
    shell.setLayout(new GridLayout(1, true));
    shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    
    ApplicationFactory factory = new ApplicationFactory(shell, "Organization", getClass().getName());
    
    uiList = factory.createList(shell, SWT.BORDER | SWT.MULTI);
    uiList.setFont(UIDATA.FONT_10);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    uiList.setLayoutData(gridData); 
    
    Button button  = factory.createButton("butAdd", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        add();
      }      
    }, factory.loadImage("butAdd.png"));
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
    button.setLayoutData(gridData);

    load();
    
    shell.setSize(200, 350);
//    XPWindowTheme.setWin32Theme(shell);
    shell.open();
    
    while(!shell.isDisposed()) {
      Display display = shell.getDisplay();
      if(!display.readAndDispatch()) display.sleep(); 
    }
  }
  
  private void add() {
    Collections.addAll(listValues, uiList.getSelection());
    shell.dispose();
  }

  public ArrayList<String> getSelected() { return listValues; }
  
  private void load() {
    Worker excutor = new Worker() {

      private String [] elements ;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          elements = new OrganizationClientHandler().listUsers();
        }catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        if(elements != null) uiList.setItems(elements);
      }
    };
    new ThreadExecutor(excutor, uiList).start();
  }
}
