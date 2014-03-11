/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.drupal;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.chars.refs.RefsEncoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.util.Worker;
import org.vietspider.content.cms.CMSChannelSelector;
import org.vietspider.content.cms.sync.SyncManager2;
import org.vietspider.content.drupal.XMLDrupalConfig.Category;
import org.vietspider.model.plugin.drupal.DrupalSyncData;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.XML2Object;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
public class SinglePostSelector extends CMSChannelSelector {
  
  private Category [] categories;
  
  public SinglePostSelector(Shell parent) {
    super("drupal", parent, DrupalSyncArticlePlugin.getResources(), "Drupal Plugin");
  }
  
  public void sync() {
    new ShellSetter(SinglePostSelector.this.getClass(), shell);
    shell.setVisible(false); 
    int index = getSelectedIndex();
//    StringBuilder builder = new StringBuilder(metaId);
    if(index < 0) index = 0;
    if(index >= butChannels.length) {
      ClientLog.getInstance().setMessage(shell, new Exception("No category"));
      return;
    }
    
    DrupalSyncData drupalSyncData = new DrupalSyncData();
    drupalSyncData.setArticleId(metaId);
    drupalSyncData.setCategoryId(categories[index].getCategoryId());
    drupalSyncData.setDebug(butDebug.getSelection());
    
    String text = txtLinkToSource.getText();
    RefsEncoder refsEncoder = new RefsEncoder();
    drupalSyncData.setLinkToSource(new String(refsEncoder.encode(text.toCharArray())));
    
    try {
      SyncManager2.getInstance(shell).sync(drupalSyncData);
    } catch (Exception e) {
      ClientLog.getInstance().setException(shell, e);
    }
  }
  
  public void invokeSetup() {
    new DrupalSetup(shell.getShell());
  }
  
  public void setMetaId(String metaId) {
    super.setMetaId(metaId);
    if(butChannels != null) loadDefaultCategory("drupal.sync.article.plugin");
  }
  
  public void loadCategries() {
    Worker excutor = new Worker() {

      private String error = null;
      private XMLDrupalConfig config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "load.file.by.gzip"),
              new BasicHeader("file", "system/plugin/drupal.config")
          };
          
          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);

          config = XML2Object.getInstance().toObject(XMLDrupalConfig.class, bytes);
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
          return;
        }
        if(config == null) return;
        categories = config.getCategories();
        
        String [] names = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
          names[i] = categories[i].getCategoryName() ;
        }
        createCategories("drupal", names);
        
        RefsDecoder decoder = new RefsDecoder();
        String text = config.getLinkToSource();
        if(text != null) {
          text = new String(decoder.decode(text.toCharArray()));
          txtLinkToSource.setText(text);
        }
        
        Rectangle displayRect = UIDATA.DISPLAY.getBounds();
        int x = (displayRect.width - 350) / 2;
        int y = (displayRect.height - 300)/ 2;
        new ShellGetter(SinglePostSelector.class, shell, 550, 300, x, y);
//        XPWidgetTheme.setWin32Theme(shell);
        shell.open();
        
        if(butChannels != null) loadDefaultCategory("drupal.sync.article.plugin");
      }
    };
    new ThreadExecutor(excutor, table).start();
  }

}
