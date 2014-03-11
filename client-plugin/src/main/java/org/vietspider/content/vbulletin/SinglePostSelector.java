/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.vbulletin;

import java.io.File;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.chars.refs.RefsEncoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.io.LicenseVerifier;
import org.vietspider.common.util.Worker;
import org.vietspider.content.cms.CMSChannelSelector2;
import org.vietspider.content.cms.sync.SyncManager2;
import org.vietspider.model.plugin.Category;
import org.vietspider.model.plugin.vbulletin.VBulletinSyncData;
import org.vietspider.model.plugin.vbulletin.XMLVBulletinConfig;
import org.vietspider.net.server.URLPath;
import org.vietspider.serialize.XML2Object;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ShellGetter;
import org.vietspider.ui.widget.ShellSetter;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
class SinglePostSelector extends CMSChannelSelector2 {
  
  private boolean isShowMessage = true;
  
  private List<Category> categories;
  
  SinglePostSelector(Shell parent) {
    super("vbulletin", parent, VBulletinSyncArticlesPlugin.getResources(), "vBulletin Plugin");
  }
  
  @Override
  @SuppressWarnings("unused")
  protected void createOption(String plugin, ApplicationFactory factory) {
    Composite optionComposite = new Composite(factory.getComposite(), SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
//    gridData.horizontalSpan = 2;
    optionComposite.setLayoutData(gridData);
//    rowLayout = new RowLayout();
    optionComposite.setLayout(new GridLayout(2, false));
//    rowLayout.justify = true;
    
    Composite userComposite = new Composite(optionComposite, SWT.NONE);
    userComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    GridLayout gridLayout = new GridLayout(1, false);
    userComposite.setLayout(gridLayout);
    factory.setComposite(userComposite);
    
    File licenseFile = LicenseVerifier.loadLicenseFile();
    boolean license = LicenseVerifier.verify("vbulletin", licenseFile);

    factory.createLabel("lblLinkToSource");
    txtLinkToSource = factory.createText(SWT.BORDER |  SWT.MULTI | SWT.WRAP);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtLinkToSource.setEnabled(license);
    txtLinkToSource.setLayoutData(gridData);
    
    Composite articleComposite = new Composite(optionComposite, SWT.NONE);
    gridLayout = new GridLayout(1, false);
    articleComposite.setLayout(gridLayout);
    factory.setComposite(articleComposite);
  }
  
  public void invokeSetup() {
    new VBulletinSetup(shell.getShell());
  }
  
  public void sync() {
    new ShellSetter(SinglePostSelector.this.getClass(), shell);
    shell.setVisible(false); 
//    StringBuilder builder = new StringBuilder(metaId);
    String cateId = tree.getSelectedId();
    
    VBulletinSyncData vbulletinSyncData = new VBulletinSyncData();
    vbulletinSyncData.setArticleId(metaId);
    vbulletinSyncData.setCategoryId(cateId);
    vbulletinSyncData.setDebug(butDebug.getSelection());
    
    vbulletinSyncData.setShowMessage(isShowMessage);
    
//    builder.append('\\').append(categories[index].getSectionId());
//    builder.append('\\').append(categories[index].getCategoryId());
    
    String text = txtLinkToSource.getText();
    RefsEncoder refsEncoder = new RefsEncoder();
    vbulletinSyncData.setLinkToSource(new String(refsEncoder.encode(text.toCharArray())));
//    builder.append('\\').append(refsEncoder.encode(text.toCharArray()));
    
    SyncManager2.getInstance(shell).sync(vbulletinSyncData);
  }
  
  public void setMetaId(String metaId) {
    super.setMetaId(metaId);
  }
  
  public void loadCategries() {
    Worker excutor = new Worker() {

      private String error = null;
      private XMLVBulletinConfig config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "load.file.by.gzip"),
              new BasicHeader("file", "system/plugin/vbulletin.config")
          };
          
          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);

          config = XML2Object.getInstance().toObject(XMLVBulletinConfig.class, bytes);
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
        
        tree.getTree().removeAll();
        categories = config.getCategories();
        tree.createCategories(categories);
        
        isShowMessage = config.isAlertMessage();
        
        isShowMessage = config.isAlertMessage();
        
        RefsDecoder decoder = new RefsDecoder();
        String text = config.getLinkToSource();
        if(text != null && text.trim().length() > 0) {
          text = new String(decoder.decode(text.toCharArray()));
          txtLinkToSource.setText(text);
        }
        
        Rectangle displayRect = UIDATA.DISPLAY.getBounds();
        int x = (displayRect.width - 340) / 2;
        int y = (displayRect.height - 280)/ 2;
        new ShellGetter(SinglePostSelector.class, shell, 300, 200, x, y);
//        XPWidgetTheme.setWin32Theme(shell);
        shell.open();
      }
    };
    new ThreadExecutor(excutor, tree.getTree()).start();
  }
  

}
