/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 28, 2008  
 */
public class DeleteTemplate {
  
  /*protected ISourceInfo iSourceInfo;
  protected Combo cboSourceType;
  
  protected Shell shell;
  
  public DeleteTemplate(ISourceInfo iSourceInfo_) {
    this.iSourceInfo = iSourceInfo_;
    
    this.cboSourceType = iSourceInfo.<Combo>getField("cboSourceType");
    this.shell = cboSourceType.getShell();
  }
  
  public void execute() {
    final int idx = cboSourceType.getSelectionIndex();
    if(idx < 2) return;
    
    MessageBox msg = new MessageBox (shell, SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
    ClientRM resource = new ClientRM("Creator");
    msg.setMessage(resource.getLabel(getClass().getName()+".msgAlertDeleteTemplate"));
    if(msg.open() != SWT.YES) return;    
    
    Worker excutor = new Worker() {
      
      private String name;
      private Group group;
      private String category;
      
      public void abort() {
        ClientConnector.currentInstance().abort();
      }

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
          client.deleteSources(group.getType(), category, new String[]{sourceName});
        } catch (Exception e) {
          ClientLog.getInstance().setException(shell, e);
        }
      }

      public void after() { 
        new LoadTemplate(iSourceInfo).execute();
      }
    };
    new ThreadExecutor(excutor, cboSourceType).start();
  }
  */
}
