/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.joomla;

import java.io.File;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.chars.refs.RefsEncoder;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.io.LicenseVerifier;
import org.vietspider.common.util.Worker;
import org.vietspider.content.cms.CMSChannelSelector;
import org.vietspider.content.cms.sync.SyncManager2;
import org.vietspider.model.plugin.joomla.JoomlaSyncData;
import org.vietspider.model.plugin.joomla.XMLJoomlaConfig;
import org.vietspider.model.plugin.joomla.XMLJoomlaConfig.Category;
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
class SinglePostSelector extends CMSChannelSelector {
  
  private Category [] categories;
  private Button butPublished;
  private Button butFeatured;
  
  private boolean isShowMessage = true;
  SinglePostSelector(Shell parent) {
    super("joomla", parent, 
        JoomlaSyncArticlesPlugin.getResources(), "Joomla Plugin");
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
    boolean license = LicenseVerifier.verify("joomla", licenseFile);

    factory.createLabel("lblLinkToSource");
    txtLinkToSource = factory.createText(SWT.BORDER |  SWT.MULTI | SWT.WRAP);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtLinkToSource.setEnabled(license);
    txtLinkToSource.setLayoutData(gridData);
    
    Composite articleComposite = new Composite(optionComposite, SWT.NONE);
    gridLayout = new GridLayout(1, false);
    articleComposite.setLayout(gridLayout);
    factory.setComposite(articleComposite);

    butPublished = factory.createButton(SWT.CHECK);
    butPublished.setText(factory.getLabel("published"));
    butPublished.setSelection(true);
    butPublished.setEnabled(license);

    butFeatured = factory.createButton(SWT.CHECK);
    butFeatured.setText(factory.getLabel("featured"));
    butFeatured.setSelection(true);
    
    butDebug.setSelection(true);
    butFeatured.setEnabled(license);
  }
  
  public void invokeSetup() {
    new JoomlaSetup(shell.getShell());
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
    JoomlaSyncData joomlaSyncData = new JoomlaSyncData();
    joomlaSyncData.setArticleId(metaId);
//    joomlaSyncData.setSectionId(categories[index].getSectionId());
    joomlaSyncData.setCategoryId(categories[index].getCategoryId());
    joomlaSyncData.setPublished(butPublished.getSelection());
    joomlaSyncData.setFeatured(butFeatured.getSelection());
    joomlaSyncData.setDebug(butDebug.getSelection());
    
    joomlaSyncData.setShowMessage(isShowMessage);
    
//    builder.append('\\').append(categories[index].getSectionId());
//    builder.append('\\').append(categories[index].getCategoryId());
    
    String text = txtLinkToSource.getText();
    RefsEncoder refsEncoder = new RefsEncoder();
    joomlaSyncData.setLinkToSource(new String(refsEncoder.encode(text.toCharArray())));
//    builder.append('\\').append(refsEncoder.encode(text.toCharArray()));
    
    SyncManager2.getInstance(shell).sync(joomlaSyncData);
  }
  
  public void setMetaId(String metaId) {
    super.setMetaId(metaId);
    if(butChannels != null) loadDefaultCategory("joomla.sync.article.plugin");
  }
  
  public void loadCategries() {
    Worker excutor = new Worker() {

      private String error = null;
      private XMLJoomlaConfig config;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          Header [] headers = new Header[] {
              new BasicHeader("action", "load.file.by.gzip"),
              new BasicHeader("file", "system/plugin/joomla.config")
          };
          
          ClientConnector2 connector = ClientConnector2.currentInstance();
          byte [] bytes = connector.postGZip(URLPath.FILE_HANDLER, new byte[0], headers);

          config = XML2Object.getInstance().toObject(XMLJoomlaConfig.class, bytes);
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
        createCategories("joomla", names);
        
        isShowMessage = config.isAlertMessage();
        
        butPublished.setSelection(config.isPublished());
        butFeatured.setSelection(config.isFeatured());
        
        RefsDecoder decoder = new RefsDecoder();
        String text = config.getLinkToSource();
        if(text != null) {
          text = new String(decoder.decode(text.toCharArray()));
          txtLinkToSource.setText(text);
        }
        
        Rectangle displayRect = UIDATA.DISPLAY.getBounds();
        int x = (displayRect.width - 340) / 2;
        int y = (displayRect.height - 280)/ 2;
        new ShellGetter(SinglePostSelector.class, shell, 550, 300, x, y);
//        XPWidgetTheme.setWin32Theme(shell);
        shell.open();
        
        if(butChannels != null) loadDefaultCategory("joomla.sync.article.plugin");
      }
    };
    new ThreadExecutor(excutor, table).start();
  }
  
  

}
