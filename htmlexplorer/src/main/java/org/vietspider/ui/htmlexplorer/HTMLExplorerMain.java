/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.browser.FastWebClient;
import org.vietspider.ui.action.Executor;
import org.vietspider.ui.widget.UIDATA;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 4, 2006
 */
public class HTMLExplorerMain {
  
  public HTMLExplorerMain() {
     this(HTMLExplorer.CONTENT);
  }
  
  public HTMLExplorerMain(int type){
//    if(parent == null) shell.setImage(factory.loadImage("VietSpider2.ico"));
//    if(parent != null){
//      shell = new Shell(parent,  SWT.CLOSE | SWT.TITLE | SWT.MAX | SWT.MIN | SWT.RESIZE);
//      shell.setImage(parent.getImage());
//      shell.setLocation(parent.getLocation().x+20, parent.getLocation().y+10);
//    } else{
     Shell shell = new Shell(UIDATA.DISPLAY, SWT.CLOSE | SWT.TITLE | SWT.MAX | SWT.MIN | SWT.RESIZE);
     shell.setText("HTMLExplorer");
     shell.setLayout(new FillLayout());
//    }
    
    HTMLExplorer explorer = new HTMLExplorer(shell);
//    explorer.setType(type);
    explorer.setWebClient(new FastWebClient());
    explorer.setUrl("http://www.google.com.vn/search?q=swt+text+editor+no+enter+code&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:en-US:official&client=firefox-a");
    explorer.selectAddress();
    explorer.addAction(this);
    open(shell);
  }
  
  public void open(Shell shell){    
    if(shell.getParent() != null){      
      shell.setMaximized(true);
      shell.open();   
      return;
    }
//    shell.setMaximized(true);
    shell.setSize(800,550); 
    shell.open();   
    Display display = shell.getDisplay();
    while (!shell.isDisposed()) {        
      if(!display.readAndDispatch()) display.sleep();
    }    
    display.dispose();
    System.exit(-1);    
  }

  
  @Executor()
  public void selected( HTMLExplorerEvent event){
    String [] items = event.getPath();
    if(items == null) return;
    for(String ele : items){
      System.out.println(ele);
    }
    System.out.println(event.getCharset());
  }

  public static void main(String[] args) {
    new HTMLExplorerMain();
  }

}
