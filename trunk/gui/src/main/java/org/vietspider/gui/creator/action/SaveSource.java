/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.ISourceInfo;
import org.vietspider.model.Source;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 23, 2008  
 */
public class SaveSource {
  
  protected ISourceInfo iSourceInfo;
  protected Button butSave;
//  protected int typeSource = 0;
//  private String template;
//  protected Combo cboSourceType;
  
  public SaveSource(ISourceInfo iSourceInfo_/*, int type, String template_*/) {
    this.iSourceInfo = iSourceInfo_;
    this.butSave = iSourceInfo.<Button>getField("butSave");
//    this.typeSource = type;
//    this.template = template_;
//    try {
//      this.cboSourceType = iSourceInfo.<Combo>getField("cboSourceType");
//    } catch (Exception e) {
//    }
  }
  
  public void execute(){
    final Source source = iSourceInfo.createSource();
    if(source == null) return;
    
    Worker excutor = new Worker() {

      private boolean overide = true;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        if(iSourceInfo.<Boolean>getField("isNewSource")) {
          String name = source.getName();
          try {
            SourcesClientHandler client = new SourcesClientHandler(source.getGroup());
            Source newSource = client.loadSource(source.getCategory(), name);
            overide = newSource == null;
          }catch (Exception e) {
            ClientLog.getInstance().setException(null, e);
          }
        }
      }

      public void after() {
        if(butSave.isDisposed()) return;
        if(!overide) {
          Shell shell = butSave.getShell();
          MessageBox msg = new MessageBox(shell, SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
          ClientRM resource = new ClientRM("Creator");
          msg.setMessage(resource.getLabel("msgAlertOverideSource"));
          if(msg.open() != SWT.YES) return;    
        } 
        saveSource(source); 
      }
    };
    new ThreadExecutor(excutor, butSave.getShell()).start();
    
  }
  
  private void saveSource(final Source source) {
    Worker excutor = new Worker() {

      String error = null;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
//          if(typeSource == 0) {
            source.setLastModified(System.currentTimeMillis());
            new SourcesClientHandler(source.getGroup()).saveSource(source);
//          } 
          /*else  if(typeSource == 1) {
            source.setName(source.getName()+ SourceFileFilter.TEMPLATE_SUFFIX);
            new SourcesClientHandler().saveSource(source);
          } else {
            new SourcesClientHandler().saveSource(source, template);
          }*/
//          if(addToLoader) new CrawlerClientHandler().addSourcesToLoader(source);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(butSave.getShell(), new Exception(error));
          return;
        }
        
        Creator creator = iSourceInfo.<Creator>getField("creator"); 
        if(creator == null) return;
        creator.selectData(new Worker[0], source.getGroup(), source.getCategory(), source.getName());
        iSourceInfo.setIsNewSource(false);
//        if(typeSource != 1) return;
//        new LoadTemplate(iSourceInfo).execute();
      }
    };
    new ThreadExecutor(excutor, butSave).start();
  }
  
}
