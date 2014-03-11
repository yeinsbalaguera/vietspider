package org.vietspider.content.abix;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.widgets.Control;
import org.vietspider.client.ClientPlugin;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.util.Worker;
import org.vietspider.content.SyncContentPlugin;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 13, 2008  
 */
public class DrupalSyncArticlePlugin extends ClientPlugin {
  
  private FormSelector selector;
  
  protected String label;

  public DrupalSyncArticlePlugin () {
    ClientRM resources = new ClientRM("Plugin");
    Class<?>  clazz = SyncContentPlugin.class;
    label = resources.getLabel(clazz.getName()+".itemSyncContent");
  }
  
  @Override
  public boolean isValidType(int type) { return type == CONTENT; }
  
  public String getConfirmMessage() { return null; }

  public String getLabel() { return label; }
  
  public void invoke(Object...objects) {
    if(!enable || values == null || values.length < 1) return;
    final Control link = (Control) objects[0];
    if(selector == null || selector.isDispose()) {
      loadChannel(link, values[0]);
      return;
    }
    selector.setMetaId(values[0]);
    selector.show();
  }
  
  private void loadChannel(final Control link, final String metaId) {
    Worker excutor = new Worker() {

      private String error = null;
      private DrupalCategoriesConfig categoriesConfig;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "load.file.by.gzip"),
              new BasicHeader("file", "system/plugin/abix/categories.xml")
          };
          
          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);
          String xml  = new String(bytes, Application.CHARSET);
          if((xml = xml.trim()).isEmpty() || xml.equals("-1")) {
            categoriesConfig = new DrupalCategoriesConfig();
            Object2XML bean2XML = Object2XML.getInstance();
            xml = bean2XML.toXMLDocument(categoriesConfig).getTextValue();
            
            headers = new Header[] {
                new BasicHeader("action", "save"),
                new BasicHeader("file", "system/plugin/abix/categories.xml")
            };
            bytes = xml.getBytes(Application.CHARSET);
            connector.post(URLPath.FILE_HANDLER, bytes, headers);
          } else {
            XML2Object xml2Bean = XML2Object.getInstance();
            categoriesConfig = xml2Bean.toObject(DrupalCategoriesConfig.class, xml);
          }
        } catch (Exception e) {
          error = e.toString();
        }
      }

      public void after() {
        if(error != null && !error.isEmpty()) {
          ClientLog.getInstance().setMessage(link.getShell(), new Exception(error));
          return;
        }
        selector = new FormSelector(link.getShell());
        selector.setData(categoriesConfig);
        selector.setMetaId(metaId);
      }
    };
    new ThreadExecutor(excutor, link).start();
  }
  
  public boolean isSetup() { return true; }
  
  public void invokeSetup(Object...objects) {
    if(objects == null || objects.length < 1) return;
    final Control link = (Control) objects[0];
    new AbixSetup(link.getShell());
  }
  

}
