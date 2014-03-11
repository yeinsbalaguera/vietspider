/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.ISourceInfo;
import org.vietspider.gui.creator.URLWidget;
import org.vietspider.link.pattern.model.JSPattern;
import org.vietspider.ui.action.Event;
import org.vietspider.ui.htmlexplorer.HTMLExplorer;
import org.vietspider.ui.htmlexplorer.HTMLExplorerEvent;
import org.vietspider.ui.htmlexplorer.TreeHandler;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 23, 2008  
 */
public class SelectVisitBlock extends SelectBlock {
  
  protected Creator creator; 

  public SelectVisitBlock(ISourceInfo fieldGetter_, Control control) {
    super(fieldGetter_, control);
    this.creator = iSourceInfo.<Creator>getField("creator");
    this.config.setDocType(JSPattern.LINK);
  }
  
  @Override
  public void execute() {
    URLWidget txtHome = iSourceInfo.<URLWidget>getField("txtHome");
    final String url = txtHome.getText().trim();
    if(url.length() == 0) return;
    
    Worker excutor = new Worker() {
      
      String message = null;

      public void abort() { 
        SelectVisitBlock.this.abort(url); 
      }

      public void before() {
      }

      public void execute() {
        try {
          doc = getDocument(url, true);
        }catch (Exception e) {
          message = e.getMessage() != null ? e.getMessage() : e.toString() ;
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        saveShellProperties(waitLoading.getWindow());
        control.setEnabled(true);
        
        if(doc == null) { 
          if(message != null && message.trim().length() > 0) {
            ClientLog.getInstance().setMessage(shell, new Exception(message));
          }
          return;
        }
        executeAfter();
        if(explorer == null) return;
        String charset = config.getCharset();
        if(charset == null) charset = "utf-8";
        iSourceInfo.<Combo>getField("cboCharset").setText(charset);
        explorer.addAction(new SelectEventListPath()); 
      }
    };
    
    control.setEnabled(false);
    waitLoading =  new WaitLoading(control, excutor, SWT.TITLE);
    loadShellProperties(waitLoading.getWindow());
    waitLoading.open();
  }
  
  @Override
  public void executeAfter() {
    HTMLExplorer htmlExplorer = creator.showHtmlExplorer();
    //new HTMLExplorer(shell, webClient, HTMLExplorer.CONTENT);
//    htmlExplorer.setType(HTMLExplorer.CONTENT);
    htmlExplorer.setRefer(iSourceInfo.getReferer());
    htmlExplorer.setDecode(config.isDecode());
    htmlExplorer.setCharset(config.getCharset());
    htmlExplorer.getActions().clear();
    htmlExplorer.clearErrorPath();
    htmlExplorer.addAction(this);
//    htmlExplorer.open();
    
    URLWidget txtHome = iSourceInfo.<URLWidget>getField("txtHome");
    htmlExplorer.setDocument(txtHome.getText(), doc);
    
    String [] path = iSourceInfo.getVisitRegions();
    for(String ele : path)  {
      htmlExplorer.setPath(ele, TreeHandler.SELECT);  
    }
    
    this.explorer = htmlExplorer;
  }
  
  @Override
  public void selectEvent(Event evt) {
    HTMLExplorerEvent event = (HTMLExplorerEvent)evt;
    if(event.getEventType() == HTMLExplorerEvent.EVENT_CANCEL) {
      creator.showMainForm();
      return;
    }
    iSourceInfo.setVisitRegions(event.getPath());
    
    if(event.getCharset() != null && iSourceInfo.<String>getField("charset") == null) {
      iSourceInfo.setCharset(event.getCharset());
    }
//    if(cboUpdateRegion.getItemCount() > 0) cboUpdateRegion.select(0);
    ((HTMLExplorer)explorer).getTree().setFocus();
    
    creator.showMainForm();
  }

}
