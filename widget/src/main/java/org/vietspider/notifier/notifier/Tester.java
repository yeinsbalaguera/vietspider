package org.vietspider.notifier.notifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class Tester {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Parent shell");
        shell.setSize(200, 200);
        shell.setLayout(new FillLayout());
      
        Button tester = new Button(shell, SWT.PUSH);
        tester.setText("Push me!");
        tester.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event event) {
//                NotifierDialogs.notify(Display.getCurrent().getActiveShell(), 9000,
//                    "Hi There! I'm a notification widget!", 
//                    "Today we are creating a widget that allows us to show notifications that fade in and out!",
//                    "", 
//                    null,
//                    null);
            }
            
        });
        shell.open();        
        

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();

    }

}
