/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 28, 2008  
 */
public class LoadTemplate {
  
 /* protected ISourceInfo iSourceInfo;
  protected Combo cboSourceType;
  
  protected Shell shell;
  
  public LoadTemplate(ISourceInfo iSourceInfo_) {
    this.iSourceInfo = iSourceInfo_;
    
    this.cboSourceType = iSourceInfo.<Combo>getField("cboSourceType");
    this.shell = cboSourceType.getShell();
  }
  
  public void execute() {
    while(cboSourceType.getItemCount() > 2) {
      cboSourceType.remove(2);
    }
    
    Worker excutor = new Worker() {

      String [] templates = null;
      Group group;
      String category;
      
      public void abort() {}

      public void before() {
        group = iSourceInfo.<Group>getField("group");
        category = iSourceInfo.<String>getField("category");
      }

      public void execute() {
        try {
          SourcesClientHandler client = new SourcesClientHandler(group.getType());
          int type = SourcesClientHandler.SOURCE_TEMPLATE;
          templates = client.loadSources(category, type);      
        } catch (Exception e) {
          ClientLog.getInstance().setException(shell, e);
        }
      }

      public void after() {
        cboSourceType.select(0);
        if(templates == null || templates.length < 1) return;
        ClientRM resource = new ClientRM("Creator");
        String label = resource.getLabel("template");
        for(String template : templates) {
          if(template.trim().isEmpty()) continue;
          int idx = template.indexOf('.');
          if(idx > 0) template = template.substring(idx+1);
          idx = template.indexOf(SourceFileFilter.TEMPLATE_SUFFIX);
          if(idx > 0) template = template.substring(0, idx);
          cboSourceType.add(label + ": "+ template);
        }
      }
    };
    new ThreadExecutor(excutor, cboSourceType).start();
  }*/
  
}
