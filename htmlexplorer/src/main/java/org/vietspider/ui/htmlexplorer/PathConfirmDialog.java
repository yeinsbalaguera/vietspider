/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.generic.ColorCache;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 16, 2009  
 */
public class PathConfirmDialog extends Dialog {

  public final static short YES = 1;
  public final static short YES_TO_ALL = 2;
  public final static short NO = -1;
  public final static short NO_TO_ALL = -2;

  private short type = NO;
  
  public static String ERROR_PATH = "";

  public PathConfirmDialog(Shell parent, int style) {
    super (parent, style);
  }

  public PathConfirmDialog (Shell parent) {
    this (parent, 0);
  }

  public short open (String path) {
    Shell parent = getParent();
    final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.TITLE | SWT.APPLICATION_MODAL);
    shell.setText(getText());
    shell.setLayout(new GridLayout(1, false));
    shell.addShellListener(new ShellAdapter() {

      @SuppressWarnings("unused")
      public void shellClosed(ShellEvent e) {
        type  = NO_TO_ALL;
      }
    });
    
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 5;
    gridLayout.horizontalSpacing = 10;
    gridLayout.verticalSpacing = 5;
    gridLayout.marginWidth = 10;
    
    Composite main = new Composite(shell, SWT.NONE);
    main.setLayout(gridLayout);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    main.setLayoutData(gridData);
    
    Text text = new Text(main, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY | SWT.BORDER);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.heightHint = 50;
    text.setLayoutData(gridData);
    text.setText(path);
    text.setFont(UIDATA.FONT_8V);
    text.setBackground(ColorCache.getInstance().getWhite());
    
    ClientRM resource = new ClientRM("HTMLExplorer");
    
    ERROR_PATH = resource.getLabel("invalid_path");
    Label label = new Label(main, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    label.setText(ERROR_PATH);
    label.setLayoutData(gridData);
    label.setFont(UIDATA.FONT_9);
    
    Composite bottom = new Composite(main, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;
    
    Button button = new Button(bottom, SWT.PUSH);
    button.setText(resource.getLabel("remove.path.yes_to_all"));
    button.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        type  = YES_TO_ALL;
        shell.dispose();
      }      
    });    
    button.setFont(UIDATA.FONT_9);
    
    button = new Button(bottom, SWT.PUSH);
    button.setText(resource.getLabel("remove.path.yes"));
    button.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        type  = YES;
        shell.dispose();
      }      
    });
    button.setFont(UIDATA.FONT_9);
    
    button = new Button(bottom, SWT.PUSH);
    button.setText(resource.getLabel("remove.path.no"));
    button.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        type  = NO;
        shell.dispose();
      }      
    });
    button.setFont(UIDATA.FONT_9);
    
    button = new Button(bottom, SWT.PUSH);
    button.setText(resource.getLabel("remove.path.no_to_all"));
    button.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        type  = NO_TO_ALL;
        shell.dispose();
      }      
    });    
    button.setFont(UIDATA.FONT_9);

    shell.setLocation(parent.getLocation().x + 260, parent.getLocation().y + 300);
    shell.setSize(450, 180);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
    Display display = parent.getDisplay();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) display.sleep();
    }
    return type;
  }

}
