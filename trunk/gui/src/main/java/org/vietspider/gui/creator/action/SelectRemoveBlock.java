/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.ISourceInfo;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.link.pattern.model.JSPattern;
import org.vietspider.ui.action.Event;
import org.vietspider.ui.htmlexplorer.HTMLExplorer;
import org.vietspider.ui.htmlexplorer.HTMLExplorerEvent;
import org.vietspider.ui.htmlexplorer.TreeHandler;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 23, 2008  
 */
public class SelectRemoveBlock extends SelectBlock {
  
  private Combo cboExtractType;
  
  protected Creator creator; 
  
  public SelectRemoveBlock(ISourceInfo iSourceInfo, Control control) {
    super(iSourceInfo, control);
    this.creator = iSourceInfo.<Creator>getField("creator");
    cboExtractType = iSourceInfo.<Combo>getField("cboExtractType");
    config.setDocType(JSPattern.DATA);
  }

  public  void executeAfter()  {
    if(doc == null) return ;
    explorer = creator.showHtmlExplorer();
    
    HTMLExplorer htmlExplorer = (HTMLExplorer) explorer;
    //new HTMLExplorer(shell, webClient, HTMLExplorer.NONE);
//    htmlExplorer.setType(HTMLExplorer.NONE);
    htmlExplorer.setDecode(config.isDecode());
    htmlExplorer.setRefer(iSourceInfo.getReferer());
    htmlExplorer.getActions().clear();
    htmlExplorer.clearErrorPath();
    htmlExplorer.addAction(this);
    
    HTMLDocument newDoc = null;
    NodePathParser pathParser = new NodePathParser();
    HTMLExtractor extractor = new HTMLExtractor();
    
    try{
      NodePath [] nodePaths  = pathParser.toNodePath(iSourceInfo.getExtractRegions());
      //cboExtractRegion.getItems());   
      if(cboExtractType.getSelectionIndex() > 0) {
        newDoc = extractor.extractFirst(doc, nodePaths);
      } else {
        newDoc = extractor.extract(doc, nodePaths);
      }
    } catch (Exception e) {
      ClientLog.getInstance().setException(shell, e);
      return;
    }
    
//    htmlExplorer.setEnableBrowser(false);
//    htmlExplorer.open();
    htmlExplorer.setDocument(newDoc);
    String [] split = iSourceInfo.getRemoveRegions();      
    
    for(String ele : split)  {
      htmlExplorer.setPath(ele, TreeHandler.SELECT);  
    }
    
  }
  
  public void selectEvent(Event evt) {
    HTMLExplorerEvent event = (HTMLExplorerEvent)evt;
    if(event.getEventType() == HTMLExplorerEvent.EVENT_CANCEL) {
      creator.showMainForm();
      return;
    }
    iSourceInfo.setRemoveRegions(event.getPath());
    creator.showMainForm();
  }
}
