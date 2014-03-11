/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import java.util.List;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.DataSelectorExplorer;
import org.vietspider.gui.creator.ISourceInfo;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.link.pattern.model.JSPattern;
import org.vietspider.model.Region;
import org.vietspider.ui.action.Event;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 23, 2008  
 */
public class SelectDataBlock extends SelectBlock {
  
  protected Creator creator; 
  
  public SelectDataBlock(ISourceInfo fieldGetter_, Control control) {
    super(fieldGetter_, control);
    this.creator = iSourceInfo.<Creator>getField("creator");
    config.setDocType(JSPattern.DATA);
  }

  public  void executeAfter()  {
    if(doc == null) return ;
    HTMLDocument newDoc = null;
    NodePathParser pathParser = new NodePathParser();
    HTMLExtractor extractor = new HTMLExtractor();
    
    Button button = iSourceInfo.<Button>getField("butAllowRemoveFrom");
    
    Combo cboExtractType = iSourceInfo.<Combo>getField("cboExtractType");
    
    try {
      NodePath [] nodePaths  = pathParser.toNodePath(iSourceInfo.getExtractRegions());
          //getecboExtractRegion.getItems()); 
      if(cboExtractType.getSelectionIndex() > 0) {
        newDoc = extractor.extractFirst(doc, nodePaths);
      } else {
        newDoc = extractor.extract(doc, nodePaths);
      }

      NodePath [] removePaths = pathParser.toNodePath(iSourceInfo.getRemoveRegions());    ;
      Boolean isCleanFrom = button.getSelection();
      if(isCleanFrom && removePaths != null && removePaths.length > 0){
        extractor.removeFrom(newDoc.getRoot(), removePaths[removePaths.length-1]);
      }
      extractor.remove(newDoc.getRoot(), removePaths);
    } catch (Exception e) {
      ClientLog.getInstance().setException(shell, e);
      return;
    }

    this.explorer = creator.showDataExplorer();
    
    DataSelectorExplorer dataExplorer = (DataSelectorExplorer) explorer;
    List<Region> regions = iSourceInfo.getDataRegions();
    //new DataSelectorExplorer(shell, group, webClient);
    dataExplorer.getActions().clear();
    dataExplorer.addAction(this);
    dataExplorer.setDataRegions(regions);
//    dataExplorer.open();
    dataExplorer.setDocument(newDoc);
    
//    List<Region> dataRegions = iSourceInfo.getDataRegions();
//    if(dataRegions != null && dataRegions.size() > 0) {
//      for(String ele : pathDataRegions) {
//        dataExplorer.setPath(ele, TreeHandler.SELECT);  
//      }
//    }
  }
  
  public void selectEvent(Event event) {
    if(event.getEventType() == Event.EVENT_CANCEL) {
      creator.showMainForm();
      return;
    }
    iSourceInfo.setDataRegions(iSourceInfo.getDataRegions());
    creator.showMainForm();
  }
}
