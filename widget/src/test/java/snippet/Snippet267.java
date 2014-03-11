/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package snippet;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 23, 2008  
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.internal.mozilla.nsIWebBrowser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/*
 * Browser example snippet: Toggle a Mozilla Browser between Design mode and View mode.
 * 
 * IMPORTANT: For this snippet to work properly all of the requirements
 * for using JavaXPCOM in a stand-alone application must be satisfied
 * (see http://www.eclipse.org/swt/faq.php#howusejavaxpcom).
 * 
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.3
 */
public class Snippet267 {
  
  static  {
    System.setProperty("org.eclipse.swt.browser.XULRunnerPath", "D:\\java\\working\\xulrunner\\"); 
  }
  
  static Browser browser;
  
  public static void main (String [] args) {
   
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout(2, true));
    shell.setText("Use Mozilla's Design Mode");
    try {
      browser = new Browser(shell, SWT.MOZILLA);
    } catch (SWTError e) {
      e.printStackTrace();
      System.out.println("Could not instantiate Browser: " + e.getMessage());
      return;
    }
    browser.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

    final Button offButton = new Button(shell, SWT.RADIO);
    offButton.setText("Design Mode Off");
    offButton.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        if (!offButton.getSelection()) return;
        setDesignMode("off");
      }
    });
    final Button onButton = new Button(shell, SWT.RADIO);
    onButton.setText("Design Mode On");
    onButton.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        if (!onButton.getSelection()) return;
        boolean success = setDesignMode("on");
        if (!success) {
          onButton.setSelection(false);
          offButton.setSelection(true);
        }
      }
    });
    offButton.setSelection(true);

    browser.setUrl("http://www.google.com");
    shell.setSize(400,400);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
  }
  
  public static boolean setDesignMode(String value) {
    nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser();
    if (webBrowser == null) {
      System.out.println("Could not get the nsIWebBrowser from the Browser widget");
      return false;
    }
//    nsIDOMWindow window = webBrowser.getContentDOMWindow();
//    nsIDOMDocument document = window.getDocument();
//    nsIDOMNSHTMLDocument nsDocument = (nsIDOMNSHTMLDocument)document.queryInterface(nsIDOMNSHTMLDocument.NS_IDOMNSHTMLDOCUMENT_IID);
//    nsDocument.setDesignMode(value);
    return true;
  }
}
