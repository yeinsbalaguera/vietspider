/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
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
public class LoadVerionSource {
  
  protected ISourceInfo iSourceInfo;
  protected Object menuVersion;
  
  protected Shell shell;
  
  protected int version = 0;
  
  public LoadVerionSource(ISourceInfo iSourceInfo_, int verion) {
    this.iSourceInfo = iSourceInfo_;
    this.version = verion;
    
    this.menuVersion = iSourceInfo.<Object>getField("menuVersion");
    this.shell = iSourceInfo.<Text>getField("txtName").getShell();
  }
  
  public void execute(final String group, final String category, final String name) {
    Worker excutor = new Worker() {
      Source source = null;

      public void abort() {}

      public void before() {
      }

      public void execute() {
        if(Application.LICENSE == Install.PERSONAL) return;
        try {
          SourcesClientHandler  client = new SourcesClientHandler(group);
          source = client.loadSource(category, name, version);
        } catch (Exception e) {
          ClientLog.getInstance().setException(shell, e);
        }
      }

      public void after() {
        if(source == null) return;
        iSourceInfo.setSource(source, version);
      }
    };
    new ThreadExecutor(excutor, shell).start();
  }
  
}
