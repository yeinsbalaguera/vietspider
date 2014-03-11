/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.log;

import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.SourceEditor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 27, 2009  
 */
public class ProcessLinkWorker extends Worker {
  
  private String link;
  private Creator creator;
  private int type;
  
  public ProcessLinkWorker(Creator creator, String link, int type) {
    this.creator = creator;
    this.link = link;
    this.type = type;
  }
  
  public void abort() {
    ClientConnector2.currentInstance().abort();
  }

  public void after() {}

  public void before() {
    if(creator == null || link == null) return;
    SourceEditor sourceEditor = creator.getInfoControl().getSourceEditor();
    sourceEditor.getUIPattern().setText(link);
    if(type == 1) sourceEditor.test();
    else if(type == 2) sourceEditor.selectExtractDataBlock(sourceEditor);
  }
  
  public void execute() {}
}
