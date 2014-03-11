/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.ISourceInfo;
import org.vietspider.gui.creator.URLWidget;
import org.vietspider.link.pattern.model.JSPattern;
import org.vietspider.ui.action.Event;
import org.vietspider.ui.htmlexplorer.HTMLExplorer;
import org.vietspider.ui.htmlexplorer.HTMLExplorerEvent;
import org.vietspider.ui.htmlexplorer.TreeHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 23, 2008  
 */
public class SelectExtractBlock extends SelectBlock {

  protected Creator creator; 
  
  public SelectExtractBlock(ISourceInfo fieldGetter_, Control control) {
    super(fieldGetter_, control);
    this.creator = iSourceInfo.<Creator>getField("creator");
    config.setDocType(JSPattern.DATA);
  }
  
  @Override
  public void execute() {
    Text txtPattern  = iSourceInfo.<Text>getField("txtPattern");
    
    if(txtPattern.getText().trim().length() == 0 
        && config.getSiteExplorer() == null){
      explorer = creator.showHtmlExplorer();
      HTMLExplorer htmlExplorer = (HTMLExplorer) explorer;
      //new HTMLExplorer(shell, webClient, HTMLExplorer.CONTENT);
//      htmlExplorer.setType( HTMLExplorer.CONTENT);
      htmlExplorer.setCache(true);
      htmlExplorer.setRefer(iSourceInfo.getReferer());
      htmlExplorer.setCharset(config.getCharset());
      htmlExplorer.getActions().clear();
      htmlExplorer.clearErrorPath();
      htmlExplorer.addAction(this);
//      htmlExplorer.open();
      
      htmlExplorer.setUrl(iSourceInfo.<URLWidget>getField("txtHome").getText()); 
      htmlExplorer.goAddress(iSourceInfo.<URLWidget>getField("txtHome").getText());
      
      explorer.addAction(new SelectEventListPath()); 
    } else {
      super.execute();
    }   
  }
  
  @Override
  public void executeAfter() {
    if(explorer == null) {
      explorer = creator.showHtmlExplorer();
    }
    HTMLExplorer htmlExplorer = (HTMLExplorer) explorer;
    //new HTMLExplorer(shell, webClient, HTMLExplorer.CONTENT);
    htmlExplorer.setRefer(iSourceInfo.getReferer());
//    htmlExplorer.setType(HTMLExplorer.CONTENT);
    htmlExplorer.setDecode(config.isDecode());
    htmlExplorer.setCharset(config.getCharset());
    htmlExplorer.clearErrorPath();
    htmlExplorer.getActions().clear();
    htmlExplorer.addAction(this);
//    htmlExplorer.open();
    
    Text txtPattern  = iSourceInfo.<Text>getField("txtPattern");
    htmlExplorer.setDocument(txtPattern.getText(), doc);
    
    String [] path = iSourceInfo.getExtractRegions();//cboExtractRegion.getItems();
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
//    cboExtractRegion.removeAll();
//    for(String ele : event.getPath()) cboExtractRegion.add(ele);
    iSourceInfo.setExtractRegions(event.getPath());
    
    Text txtPattern  = iSourceInfo.<Text>getField("txtPattern");
    txtPattern.setText(event.getUrl());
    
    if(event.getCharset() != null && iSourceInfo.<String>getField("charset") == null) {
      iSourceInfo.setCharset(event.getCharset());
    }
    
//    if(cboExtractRegion.getItemCount() > 0) cboExtractRegion.select(0);
    ((HTMLExplorer)explorer).getTree().setFocus();
    
    creator.showMainForm();
  }

}
