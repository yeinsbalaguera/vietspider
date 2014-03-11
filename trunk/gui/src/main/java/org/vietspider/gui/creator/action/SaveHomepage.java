/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.HomepagesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.homepage.URLHomepageDialog;
import org.vietspider.link.pattern.LinkPatternFactory;
import org.vietspider.link.pattern.LinkPatterns;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 23, 2008  
 */
public class SaveHomepage {

  private URLHomepageDialog dialog;
  
  public SaveHomepage(URLHomepageDialog dialog) {
    this.dialog = dialog;
    
    execute();
  }
  
  public void execute(){
    Worker excutor = new Worker() {
      
      private String group;
      private String category;
      private String name;
      
      private String [] templates;
      private String [] elements;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        if(dialog.getShell().isDisposed()) return;
        group = dialog.getGroup();
        category = dialog.getCategory();
        name = dialog.getName();
        
        templates = dialog.getTemplate().split("\n");
        elements = dialog.getTxtDialog().getText().split("\n");
        
      }

      public void execute() {
        StringBuilder builder = new StringBuilder(group);
        builder.append('.').append(category).append('.').append(name).append('\n');
        
        LinkPatterns patterns = LinkPatternFactory.createPatterns(LinkPatterns.class, templates);
        for(String ele : elements) {
          if(patterns.match(ele)) {
            builder.append(ele).append('\n');
            continue;
          } 
        }
        
        HomepagesClientHandler client = new HomepagesClientHandler();
        try {
          client.saveHomepages(builder.toString());
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        if(dialog.getShell().isDisposed()) return;
        new ListHomepage(dialog); 
        dialog.getTxtDialog().setText("");
      }
    };
    new ThreadExecutor(excutor, dialog.getShell()).start();
  }
  
  
}
