/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.ISourceInfo;
import org.vietspider.model.Source;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 28, 2008  
 */
public class LoadSourceVerions {
  
  protected ISourceInfo iSourceInfo;
  protected Object menuVersion;
  protected Shell shell;
  
  public LoadSourceVerions(ISourceInfo iSourceInfo_) {
    this.iSourceInfo = iSourceInfo_;
    
    this.menuVersion = iSourceInfo.<Object>getField("menuVersion");
    this.shell = iSourceInfo.<Text>getField("txtName").getShell();
  }
  
  public void execute(final Source source) {
    Worker excutor = new Worker() {

      String [] versions = null;
      
      public void abort() {}

      public void before() {
      }

      public void execute() {
        try {
          String group = source.getGroup();
          SourcesClientHandler  client = new SourcesClientHandler(group);
          String category = source.getCategory();
          String name = source.getName();
          versions = client.loadSourceVersions(category, name); 
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        if(shell.isDisposed()) return;
        int size = 0;
//        if( menuVersion instanceof  PopupMenu) {  
//          CMenu cmenu = ((PopupMenu)menuVersion).getMenu();
//          size = cmenu.getItemCount();
//        } else {
          size =   ((Menu)menuVersion).getItems().length;
//        }
        int i = 0;
        for(; i < Math.min(versions.length, size) ; i++) {
//          if( menuVersion instanceof  PopupMenu) {  
//            CMenu cmenu = ((PopupMenu)menuVersion).getMenu();
//            cmenu.getItem(i).setEnabled(true);
//          } else {
             ((Menu)menuVersion).getItem(i).setEnabled(true);
//          }
        }
        
        for(; i < size ; i++) {
//          if( menuVersion instanceof  PopupMenu) {  
//            CMenu cmenu = ((PopupMenu)menuVersion).getMenu();
//            cmenu.getItem(i).setEnabled(false);
//          } else {
             ((Menu)menuVersion).getItem(i).setEnabled(false);
//          }
          
        }
      }
    };
    new ThreadExecutor(excutor, shell).start();
  }
  
}
