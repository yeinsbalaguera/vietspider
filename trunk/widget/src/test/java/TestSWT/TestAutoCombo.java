/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package TestSWT;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.ui.widget.CCombo2;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 11, 2007  
 */
public class TestAutoCombo {
  public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    shell.setLayout(new FillLayout());

    final CCombo2 combo = new CCombo2(shell,SWT.BORDER);
    combo.setItems(new String[]{"sdhfdsf", "sdfdsf", "aasdasd"});
    combo.text.addModifyListener(new ModifyListener(){

      public void modifyText(ModifyEvent arg0) {
        
      }
      
    });
    combo.text.addFocusListener(new FocusAdapter() {
      @SuppressWarnings("unused")
      public void focusGained(FocusEvent event) {
      }

      @SuppressWarnings("unused")
      public void focusLost(FocusEvent evt) {
      }
    });
    
    List list  = new List(shell, SWT.BORDER);
    list.setItems(new String[]{"fdsfdsfdsf", "sdfsdfdsf", "sdfkjdsfiewuyu"});
    
    shell.open ();
    while (!shell.isDisposed ()) {
        if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
}
}
