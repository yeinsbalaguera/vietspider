/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package TestSWT;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Snippet307_2 {

  public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    shell.setLayout (new FillLayout ());
    shell.setBounds (10,10,300,200);

    final Browser browser;
    try {
      browser = new Browser (shell, SWT.NONE);
    } catch (SWTError e) {
      System.out.println ("Could not instantiate Browser: " + e.getMessage ());
      display.dispose();
      return;
    }
    browser.setText (createHTML ());
    final BrowserFunction function = new CustomFunction (browser, "theJavaFunction");

    browser.addProgressListener (new ProgressAdapter () {
      public void completed (ProgressEvent event) {
        browser.addLocationListener (new LocationAdapter () {
          public void changed (LocationEvent event) {
            browser.removeLocationListener (this);
            System.out.println ("left java function-aware page, so disposed CustomFunction");
            function.dispose ();
          }
        });
      }
    });

    shell.open ();
    while (!shell.isDisposed ()) {
      if (!display.readAndDispatch ())
        display.sleep ();
    }
    display.dispose ();
  }

  static class CustomFunction extends BrowserFunction {
    CustomFunction (Browser browser, String name) {
      super (browser, name);
    }
    public Object function (Object[] arguments) {
      System.out.println ("theJavaFunction() called from javascript with args:");
      for (int i = 0; i < arguments.length; i++) {
        Object arg = arguments[i];
        if (arg == null) {
          System.out.println ("\t-->null");
        } else {
          System.out.println ("\t-->" + arg.getClass ().getName () + ": " + arg.toString ());
        }
      }
      Object returnValue = new Object[] {
          new Short ((short)3),
          new Boolean (true),
          null,
          new Object[] {"a string", new Boolean (false)},
          "hi",
          new Float (2.0f / 3.0f),
      };
      //int z = 3 / 0; // uncomment to cause a java error instead
      return returnValue;
    }
  }

  static String createHTML () {
    StringBuffer buffer = new StringBuffer ();
    buffer.append ("<html>\n");
    buffer.append ("<head>\n");
    buffer.append ("<script language=\"JavaScript\">\n");
    buffer.append ("function function1() {\n");
    buffer.append ("    var result;\n");
    buffer.append ("    try {\n");
    buffer.append ("        result = theJavaFunction(12, false, null, [3.6, ['swt', true]], 'eclipse');\n");
    buffer.append ("    } catch (e) {\n");
    buffer.append ("        alert('a java error occurred: ' + e.message);\n");
    buffer.append ("        return;\n");
    buffer.append ("    }\n");
    buffer.append ("    for (var i = 0; i < result.length; i++) {\n");
    buffer.append ("        alert('returned ' + i + ': ' + result[i]);\n");
    buffer.append ("    }\n");
    buffer.append ("}\n");
    buffer.append ("</script>\n");
    buffer.append ("</head>\n");
    buffer.append ("<body>\n");
    buffer.append ("<input id=button type=\"button\" value=\"Push to Invoke Java\" onclick=\"function1();\">\n");
    buffer.append ("<p><a href=\"http://www.eclipse.org\">go to eclipse.org</a>\n");
    buffer.append ("</body>\n");
    buffer.append ("</html>\n");
    return buffer.toString ();
  }

}
