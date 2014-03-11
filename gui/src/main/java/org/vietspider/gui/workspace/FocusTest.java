/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.workspace;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 4, 2011  
 */
public class FocusTest
{
    public static void main (String [] args)
    {
        final Display display = new Display ();
        final Shell shell = new Shell (display);


        shell.open ();


        Timer t = new Timer ();
        TimerTask task = new TimerTask ()
        {
            @Override
            public void run ()
            {
                display.asyncExec (new Runnable ()
                {
                    public void run ()
                    {
                        shell.forceActive();
                    }
                });
            }
        };


        int time = 5000;
        t.scheduleAtFixedRate (task, 0, time);


        while (!shell.isDisposed ())
        {
            if (!display.readAndDispatch ())
                display.sleep ();
        }


        t.cancel ();
        display.dispose ();
    }
}