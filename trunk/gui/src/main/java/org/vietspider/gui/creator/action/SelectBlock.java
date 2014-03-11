/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.ISourceInfo;
import org.vietspider.html.HTMLDocument;
import org.vietspider.ui.action.ActionListener;
import org.vietspider.ui.action.Event;
import org.vietspider.ui.action.Executor;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 23, 2008  
 */
public abstract class SelectBlock  extends ClientDocumentLoader {

  protected Control control;
  protected Shell shell;

  protected ActionListener explorer;

  protected WaitLoading waitLoading = null;
  
  protected HTMLDocument doc;
  
  public SelectBlock(ISourceInfo sourceInfo, Control control) {
    super.setISourceInfo(sourceInfo);
    this.control = control;
    
    this.shell = control.getShell();

//    this.cboExtractRegion = iSourceInfo.<Combo>getField("cboExtractRegion");
  }

  public void execute() {
    Worker excutor = new Worker() {

      String url ;
      String message = null;

      public void abort() {
        SelectBlock.this.abort(url);
      }

      public void before() {
        url = iSourceInfo.<Text>getField("txtPattern").getText();
      }

      public void execute() {
        try {
          /*if(slow == 0) {
            Thread.sleep(5*1000);
          } else if(slow == 1) {
            Thread.sleep(10*1000);
          }*/
          doc = getDocument(url, true);
        } catch (Exception e) {
//          e.printStackTrace();
          message = e.toString() ;
        ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        saveShellProperties(waitLoading.getWindow());
        control.setEnabled(true);
        if(message == null || message.trim().length() < 0) {
          message = config.getMessage();
        }
        if(message != null && message.trim().length() > 0) {
          ClientLog.getInstance().setMessage(shell, new Exception(message));
        }
        if(doc == null) return;
        executeAfter();
        if(explorer == null) return;
        String charset = iSourceInfo.<String>getField("charset");
        if(charset != null) {
          iSourceInfo.<Combo>getField("cboCharset").setText(config.getCharset());
        }
        explorer.addAction(new SelectEventListPath()); 
      }
    };
    control.setEnabled(false);
    waitLoading =  new WaitLoading(control, excutor, SWT.TITLE);
    loadShellProperties(waitLoading.getWindow());
    waitLoading.open();
  }

  protected void saveShellProperties(Shell dialog) {
    if(dialog == null || dialog.isDisposed()) return;
    Preferences prefs2 = Preferences.userNodeForPackage(getClass());
    String name2  = getClass().getSimpleName();
    Point point = dialog.getLocation();
    try {
      prefs2.put(name2+"_location_x", String.valueOf(point.x));
      prefs2.put(name2+"_location_y", String.valueOf(point.y));
    } catch (Exception e) {
    }
  }

  protected void loadShellProperties(Shell dialog) {
    if(dialog == null || dialog.isDisposed()) return;
    int x = dialog.getLocation().x;
    int y = dialog.getLocation().y;

    String name  = getClass().getSimpleName();
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    try {
      x = Integer.parseInt(prefs.get(name+"_location_x", ""));
    } catch (Exception e) {
    }

    try {
      y = Integer.parseInt(prefs.get(name+"_location_y", ""));
    } catch (Exception e) {
    }
    dialog.setLocation(x , y);
  }

  protected class SelectEventListPath {
    @Executor()
    public void selected(Event event){
      selectEvent(event);
    }
  }

  public abstract  void executeAfter() ;

  @SuppressWarnings("unused")
  public void selectEvent(Event event){

  }

}
