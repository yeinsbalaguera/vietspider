/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.workspace;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.ClientProperties;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellSetter;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 26, 2006
 */
public class VSWindow {
  
  private Shell shell;
//  private VTab bar;
  private SashForm sash;
  private Workspace workspace;
  
  public VSWindow(String title) {    
    ApplicationFactory factory = new ApplicationFactory("VietSpider", getClass() , 800, 520);
    shell = (Shell) factory.getComposite(); 
    shell.setLayout(new FillLayout());   
    shell.setText(title+shell.getText());
    
    sash = new SashForm(shell, SWT.HORIZONTAL);  
    sash.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    
    workspace = new Workspace(factory, sash, SWT.NONE); 
    
    shell.addShellListener(new ShellAdapter(){       
      public void shellClosed(ShellEvent e){
//        new ShellSetter(getClass(), shell);
        closeShell();  
        String mode = ClientProperties.getInstance().getValue("mode");
        if("exit".equalsIgnoreCase(mode)) {
          VietSpiderClient.dispose();
        } else {
          e.doit = false;
          shell.setVisible(false);
        }
      }     
    });
    
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
  }
  
  public Workspace getWorkspace() { return workspace; }

  public Shell getShell() { return shell; }
  
  public void closeShell(){
    new ShellSetter(getClass(), shell);
  }
}
