/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
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

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 9, 2009  
 */
public class Snippet307 {

  public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    shell.setLayout (new FillLayout ());
    shell.setBounds (10,10,300,200);

    final Browser browser;
    try {
      browser = new Browser (shell, SWT.WEBKIT);
    } catch (SWTError e) {
      System.out.println ("Could not instantiate Browser: " + e.getMessage ());
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
    buffer.append ("function gotoSelectedText() {\n");
    buffer.append ("    var value;\n");
    buffer.append ("    try {\n");
    buffer.append ("        value = window.getSelection();\n");
    buffer.append ("    } catch(e) {\n");
    buffer.append ("        value = document.selection.createRange().text;\n");
    buffer.append ("    }\n");
    buffer.append ("    try {\n");
    buffer.append ("        result = theJavaFunction(value);\n");
    buffer.append ("    } catch (e) {\n");
    buffer.append ("        alert('a java error occurred: ' + e.message);\n");
    buffer.append ("        return;\n");
    buffer.append ("    }\n");
    buffer.append ("}\n");
    buffer.append ("</script>\n");
    buffer.append ("</head>\n");
    buffer.append ("<body onmouseup=\"gotoSelectedText();\">\n");
    buffer.append ("<input id=button type=\"button\" value=\"Push to Invoke Java\" onclick=\"gotoSelectedText();\">\n");
    buffer.append ("<p><a href=\"http://www.eclipse.org\">go to eclipse.org</a>\n");
    buffer.append ("<p>Joomla Plugin. Phiên bản mới nhất này cho phép bạn chỉ cần thực hiện ba bước thao tác rất đơn giản cho việc tích hợp với Joomla Plugin. Sau khi cài đặt, bạn có thể gửi nội dung từ VietSpider tới Joomla một các dễ dàng hơn. Các cải tiến khác bao gồm:");
    buffer.append ("</body>\n");
    buffer.append ("</html>\n");
    return buffer.toString ();
  }

}

