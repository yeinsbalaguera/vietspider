/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.crawler;

import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.CrawlerClientHandler;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 4, 2009  
 */
class ViewPoolInformation extends BackgroupLoader {
  
  private String value;
  private Text text;

  ViewPoolInformation(Crawler crawler, Text text) {
    super(crawler, text);
    this.text = text;
  }

  public void finish() {
    /*ClientRM resources = new ClientRM("Crawler");
    String [] buttons = {
        resources.getLabel("pool.clear.queue"),
        resources.getLabel("pool.update")
    };

    SelectionAdapter [] adapters = {
        new SelectionAdapter() {
          @SuppressWarnings("unused")
          public void widgetSelected(SelectionEvent evt) {
            Properties properties_ = ClientProperties.getInstance().getProperties();
            ClientRM resources_ = new ClientRM("Crawler");
            int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
            MessageBox msg = new MessageBox (text.getShell(), style);
            msg.setMessage(resources_.getLabel("pool.clear.queue.confirm"));
            if(msg.open() != SWT.YES) return ;
            new ClearPoolQueue(getCrawler());
          }
        },
        new SelectionAdapter(){
          @SuppressWarnings("unused")
          public void widgetSelected(SelectionEvent evt) {
            new ThreadExecutor(ViewPoolInformation.this, text).start();
          }
        } 
    };*/
    
    text.setText(value);
  }

  public void load() throws Exception {
    value =  new CrawlerClientHandler().viewPool();
  }
  
}
