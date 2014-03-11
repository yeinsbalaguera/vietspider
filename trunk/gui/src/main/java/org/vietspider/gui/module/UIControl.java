/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.module;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.browser.BrowserWidget;
import org.vietspider.gui.config.Config;
import org.vietspider.gui.crawler.Crawler;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.log.LogViewer2;
import org.vietspider.gui.wizard.ChannelWizard;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;
import org.vietspider.ui.widget.vtab.outlookbar.OutlookBarItem;
import org.vietspider.ui.widget.vtab.outlookbar.OutlookBarPane;
import org.vietspider.user.User;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 28, 2006
 */
public class UIControl {
  
  private OutlookBarPane pane;
  private Workspace workspace;
  
  public UIControl(ApplicationFactory factory, OutlookBarPane parent, Workspace workspace_) {    
    pane = parent;
    this.workspace = workspace_;
    
    factory.setClassName(getClass().getName());
    
    initItems(factory);
    parent.setSize();
    
    pane.getControl().layout();
  }
  
  private void initItems(ApplicationFactory factory) {
    Image img = null;
    OutlookBarItem item = null;
    
    img = factory.loadImage("dataexplorer.png");
    item = new OutlookBarItem(pane, factory.getLabel("itemNews"), img);   
    item.getImage().addHyperlinkListener(new HyperlinkAdapter(){
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e){
        BrowserWidget browser = workspace.getTab().createItem();
        browser.viewPage();
      }
    });
    
    if(ClientConnector2.currentInstance().getPermission() != User.DATA_READ_ONLY) {
      img = factory.loadImage("createsource.png");
      item = new OutlookBarItem(pane, factory.getLabel("itemChannelWizard"), img);   
      item.getImage().addHyperlinkListener(new HyperlinkAdapter(){
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e){
          try {          
            ChannelWizard wizard = (ChannelWizard)workspace.getTab().createTool(
                ChannelWizard.class, false,  SWT.CLOSE);
          } catch (Error err) {
            err.printStackTrace();
          }catch (Exception exp) {
            exp.printStackTrace();
            ClientLog.getInstance().setException(workspace.getShell(), exp);
          }
        }
      });
    }
    
    if(ClientConnector2.currentInstance().getPermission() != User.DATA_READ_ONLY) {
      img = factory.loadImage("webstore.png");
      item = new OutlookBarItem(pane, factory.getLabel("itemCreator"), img);   
      item.getImage().addHyperlinkListener(new HyperlinkAdapter(){
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e){
          try {          
            Creator creator = (Creator)workspace.getTab().createTool(Creator.class, false, SWT.CLOSE);
            if(creator != null) creator.selectData(new Worker[0], null, null);
            pane.getShell().dispose();
          } catch (Error err) {
            err.printStackTrace();
          }catch (Exception exp) {
            exp.printStackTrace();
            ClientLog.getInstance().setException(workspace.getShell(), exp);
          }
        }
      });
    }
    

    int permission = ClientConnector2.currentInstance().getPermission();
    
    if(permission == User.ROLE_ADMIN || permission == User.APPLICATION_MONITOR) {
      img = factory.loadImage("crawler.png");
      item = new OutlookBarItem(pane, factory.getLabel("itemSpider"), img);   
      item.getImage().addHyperlinkListener(new HyperlinkAdapter(){
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e){
          try {          
            workspace.getTab().createTool(Crawler.class, true, SWT.CLOSE);
            pane.getShell().dispose();
          }catch (Exception exp) {
            ClientLog.getInstance().setException(workspace.getShell(), exp);
          }
        }
      });   
    }
    
//    if(Application.LICENSE != Install.PERSONAL) {
//      img = factory.loadImage("monitor.png");
//      item = new OutlookBarItem(pane, factory.getLabel("itemMonitor"), img);   
//      item.getImage().addHyperlinkListener(new HyperlinkAdapter(){
//        @SuppressWarnings("unused")
//        public void linkActivated(HyperlinkEvent e){
//          try {       
//            workspace.getTab().createTool(SourceMonitor.class, true, SWT.CLOSE);
//            pane.getShell().dispose();
//          } catch (Exception exp) {
//            ClientLog.getInstance().setException(workspace.getShell(), exp);
//          }
//        }
//      });
//    }
    
    if(permission == User.ROLE_ADMIN || permission == User.APPLICATION_MONITOR) {
      img = factory.loadImage("log.png");
      item = new OutlookBarItem(pane, factory.getLabel("itemLog"), img);   
      item.getImage().addHyperlinkListener(new HyperlinkAdapter(){
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e){
          try {          
            workspace.getTab().createTool(LogViewer2.class, true, SWT.CLOSE);
            pane.getShell().dispose();
          }catch (Exception exp) {
            ClientLog.getInstance().setException(workspace.getShell(), exp);
          }
        }
      });   
    }
    
//    if(Application.LICENSE == Install.SEARCH_SYSTEM) {
//      img = factory.loadImage("webstore.png");
//      item = new OutlookBarItem(pane, factory.getLabel("itemWebStore"), img);   
//      item.getImage().addHyperlinkListener(new HyperlinkAdapter(){
//        @SuppressWarnings("unused")
//        public void linkActivated(HyperlinkEvent e){
//          try {       
//            workspace.getTab().createTool(WebsiteStore.class, false, SWT.CLOSE);
//            pane.getShell().dispose();
//          } catch (Exception exp) {
//            exp.printStackTrace();
//            ClientLog.getInstance().setException(workspace.getShell(), exp);
//          }
//        }
//      });
//    }
    
    if(permission != User.ROLE_ADMIN) return;
    
//    if(Application.LICENSE != Install.PERSONAL) {
//      img = factory.loadImage("userfolder.png");
//      item = new OutlookBarItem(pane, factory.getLabel("itemOrg"), img);   
//      item.getImage().addHyperlinkListener(new HyperlinkAdapter(){
//        @SuppressWarnings("unused")
//        public void linkActivated(HyperlinkEvent e){
//          try {          
//            workspace.getTab().createTool(Organization.class, true, SWT.CLOSE);
//            pane.getShell().dispose();
//          }catch (Exception exp) {
//            ClientLog.getInstance().setException(workspace.getShell(), exp);
//          }
//        }
//      });
//    }
    
    img = factory.loadImage("settingsfolder.png");
    item = new OutlookBarItem(pane, factory.getLabel("itemConfig"), img);   
    item.getImage().addHyperlinkListener(new HyperlinkAdapter(){
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e){
        try {          
          workspace.getTab().createTool(Config.class, true, SWT.CLOSE);
          pane.getShell().dispose();
        }catch (Exception exp) {
          ClientLog.getInstance().setException(workspace.getShell(), exp);
        }
      }
    });
  }
 
}
