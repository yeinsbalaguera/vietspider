import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentFragment;
import org.mozilla.interfaces.nsIDOMDocumentRange;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMHTMLDocument;
import org.mozilla.interfaces.nsIDOMRange;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsISelectElement;
import org.mozilla.interfaces.nsISelection;
import org.mozilla.interfaces.nsISelection2;
import org.mozilla.interfaces.nsISelectionController;
import org.mozilla.interfaces.nsISelectionDisplay;
import org.mozilla.interfaces.nsISelectionPrivate;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIWebBrowser;
import org.vietspider.ui.browser.PageMenu;
import org.vietspider.ui.widget.BrowserMenu;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;

/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 5, 2009  
 */
public class Snippet267 {

  static {
    System.setProperty("org.eclipse.swt.browser.XULRunnerPath", "D:\\java\\working\\xulrunner1.8\\xulrunner\\");
  }

  static Browser browser;
  static BrowserMenu menu;

  public static void main (String [] args) {
    System.setProperty("vietspider.data.path", "D:\\java\\headvances3\\trunk\\vietspider\\startup\\src\\test\\data\\");
    
    Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setLayout(new GridLayout(2, true));
    shell.setText("Use Mozilla's Design Mode");
    
    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        new ShellSetter(Snippet267.class, shell);
      }
    });
    
    try {
      System.setProperty("org.eclipse.swt.browser.XULRunnerPath", "D:\\java\\working\\xulrunner1.8\\xulrunner.win32\\");
      browser = new Browser(shell, SWT.MOZILLA);
    } catch (SWTError e) {
      System.out.println("Could not instantiate Browser: " + e.getMessage());
      display.dispose();
      return;
    }
    browser.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

    browser.addProgressListener (new ProgressAdapter () {
      @SuppressWarnings("unused")
      public void completed (ProgressEvent event) {
        nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser ();
        nsIDOMWindow domWindow = webBrowser.getContentDOMWindow ();
        nsIDOMEventTarget target = (nsIDOMEventTarget)domWindow.queryInterface (nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
        nsIDOMEventListener listener = new nsIDOMEventListener () {
          public nsISupports queryInterface (String uuid) {
            if (uuid.equals (nsIDOMEventListener.NS_IDOMEVENTLISTENER_IID) ||
                uuid.equals (nsIDOMEventListener.NS_ISUPPORTS_IID)) {
              return this;
            }
            return null;
          }
          
          public void handleEvent (nsIDOMEvent event) {
            if(menu != null) menu.dispose();
//            menu = BrowserMenu.createMenu(browser, event); 
            
           /* nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser ();
            nsIDOMWindow dw = webBrowser.getContentDOMWindow();
            nsIDOMDocument nsDoc = dw.getDocument();
            
            nsISelection selection = dw.getSelection();
            nsIDOMRange idomRange = selection.getRangeAt(0);
            System.out.println(selection.getRangeAt(0));
            System.out.println(selection.getRangeAt(1));
            System.out.println(selection.getFocusNode().getNodeName());
            System.out.println(selection.getAnchorNode().getNodeName());*/
            
            
          }

        };
        target.addEventListener ("contextmenu", listener, false);
      }
    });
    

    browser.setUrl("http://thuannd:9244/");
    new ShellGetter(Snippet267.class, shell, 400, 400);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
  }
  
  

}
