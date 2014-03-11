/***************************************************************************
 * Copyright 2001-2012 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Jan 4, 2012  
 */
public class PathBox3 extends Composite {
  
  private StyledText text;
  private StyledText list;
  
  public PathBox3(Composite parent) {
    super (parent, SWT.NONE);
    
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 15;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);
    
    Composite composite = new Composite(this, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    composite.setLayoutData(gridData);
    
    gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 5;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    composite.setLayout(gridLayout);
    
    text = new StyledText(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.BORDER);
    gridData = new GridData(GridData.FILL_BOTH);
    text.setLayoutData(gridData);
    text.setFont(UIDATA.FONT_9);
    
    /*Button butUpdate = new Button(composite, SWT.PUSH);
    butUpdate.setText("Update");
    gridData = new GridData();
    butUpdate.setLayoutData(gridData);
    butUpdate.setFont(UIDATA.FONT_9);*/
    
    Button butAdd = new Button(composite, SWT.PUSH);
    butAdd.setText("Add");
    gridData = new GridData();
    butAdd.setLayoutData(gridData);
    butAdd.setFont(UIDATA.FONT_9);
    
    list = new StyledText(this, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
    gridData = new GridData(GridData.FILL_BOTH);
    list.setLayoutData(gridData);
    list.setFont(UIDATA.FONT_9);
  }
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    

    PathBox3 box = new PathBox3(shell);

    shell.setSize(300, 300);
    shell.open();

    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
}
