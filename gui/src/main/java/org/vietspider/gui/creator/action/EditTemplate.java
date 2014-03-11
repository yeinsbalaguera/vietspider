/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 28, 2008  
 */
public class EditTemplate {
  
 /* protected ISourceInfo iSourceInfo;
  protected Combo cboSourceType;
  
  protected Shell shell;
  
  public EditTemplate(ISourceInfo iSourceInfo_) {
    this.iSourceInfo = iSourceInfo_;
    
    this.cboSourceType = iSourceInfo.<Combo>getField("cboSourceType");
    this.shell = cboSourceType.getShell();
  }
  
  public void execute() {
    
    final int idx = cboSourceType.getSelectionIndex();
    if(idx < 2) return;
    
    Worker excutor = new Worker() {

      private Source source;
      
      private String name;
      
      private Group group;
      
      private String category;
      
      public void abort() {}

      public void before() {
        name = cboSourceType.getItem(idx);
        name = name.substring(name.indexOf(':')+1).trim();
        
        group = iSourceInfo.<Group>getField("group");
        category = iSourceInfo.<String>getField("category");
      }

      public void execute() {
        try {
          SourcesClientHandler  client = new SourcesClientHandler();
          String sourceName = name + SourceFileFilter.TEMPLATE_SUFFIX;
          source = client.loadSource(group.getType(), category, sourceName);     
        } catch (Exception e) {
          ClientLog.getInstance().setException(shell, e);
        }
      }

      public void after() {
        if(source == null) return;
        source.setName(name);
//        Button butSaveAndLoad = sourceInfo.getButtonSaveAndLoad();
//        if(butSaveAndLoad != null) butSaveAndLoad.setEnabled(false);
        iSourceInfo.setSource(source, 0);
        cboSourceType.select(1);
      }
    };
    new ThreadExecutor(excutor, cboSourceType).start();
  }
  */
}
