/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.browser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.DataReader;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.gui.module.ToolWindow;
import org.vietspider.gui.module.UINews;
import org.vietspider.gui.workspace.VietSpiderClient;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 12, 2009  
 */
public class StatusControlPanel extends Composite {
  
  private Workspace workspace;
  
  private List<ImageHyperlink> icons;
  
  private ToolWindow toolWindow;
  
  public StatusControlPanel(Workspace _workspace, Composite parent) {
    super(parent, SWT.BORDER);
    this.workspace = _workspace;
    ApplicationFactory factory = new ApplicationFactory(this, "VietSpider", getClass().getName());
    
    icons = new ArrayList<ImageHyperlink>();
    
    Image imgAbout = factory.loadImage("status.information.png");
    String tipAbout  = factory.getLabel("itemAbout");
    createIcon(imgAbout, tipAbout, new HyperlinkAdapter() {
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        try {
          byte [] bytes = new byte[0];
          File folder = UtilFile.getFolder("client/resources/");
          File file = new File(folder, "about.htm");
          if(file.exists()) {
            bytes = RWData.getInstance().load(file);
          } else {
            String url = UINews.class.getResource("").toString()+"about.htm";
            bytes = RWData.getInstance().loadInputStream(new URL(url).openStream()).toByteArray();
          }
          String text  = new String(bytes, Application.CHARSET);
          text = text.replaceAll("@LICENSE@", Application.LICENSE.toString());
          workspace.getTab().createItem().setText(text);
        } catch (Exception exp) {
          ClientLog.getInstance().setException(getShell(), exp);
        }
      }
    });

    Image imgHelp = factory.loadImage("status.help.png");
    String tipHelp  = factory.getLabel("itemHelp");
    createIcon(imgHelp, tipHelp, new HyperlinkAdapter() {
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        try {
          File folder = UtilFile.getFolder("client/resources/");
          File file = new File(folder, "help.htm");
          byte [] bytes = new byte[0];
          if(file.exists()) {
            bytes = RWData.getInstance().load(file);
          } else {
            String url = UINews.class.getResource("").toString()+"help.htm";
            bytes = RWData.getInstance().loadInputStream(new URL(url).openStream()).toByteArray();
          }
          String text  = new String(bytes, Application.CHARSET);
          workspace.getTab().createItem().setText(text);
        } catch (Exception exp) {
          ClientLog.getInstance().setException(getShell(), exp);
        }

      }
    });

    if(Application.LICENSE  != Install.PERSONAL) {
      Image imgConnect = factory.loadImage("status.connect.png");
      String tipConnect = factory.getLabel("itemConnectAnother");
      createIcon(imgConnect, tipConnect, new HyperlinkAdapter() {
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e) {
          VietSpiderClient.connect(false);
        }
      });
    }
    
//    int permission = ClientConnector2.currentInstance().getPermission();
    
   /* if(permission == User.ROLE_ADMIN || permission == User.APPLICATION_MONITOR) {
      Image img = factory.loadImage("status.crawler.png");
      String tipConnect = factory.getLabel("itemCrawler");
      createIcon(img, tipConnect,  new HyperlinkAdapter(){
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e){
          try {          
            workspace.getTab().createTool(Crawler.class, true, "small.crawler.png", SWT.CLOSE);
          }catch (Exception exp) {
            ClientLog.getInstance().setException(workspace.getShell(), exp);
          }
        }
      });   
    }*/
    
   /* if(ClientConnector2.currentInstance().getPermission() != User.DATA_READ_ONLY) {
      Image img = factory.loadImage("status.create.source.png");
      String tipConnect = factory.getLabel("itemCreator");
      createIcon(img, tipConnect, new HyperlinkAdapter(){
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e){
          try {          
            Creator creator = (Creator)workspace.getTab().createTool(
                Creator.class, false, "small.createsource.png", SWT.CLOSE);
            if(creator != null) creator.selectData(new Worker[0], null, null);
          } catch (Error err) {
            err.printStackTrace();
          }catch (Exception exp) {
            exp.printStackTrace();
            ClientLog.getInstance().setException(getShell(), exp);
          }
        }
      });
    }*/
    
//    if(ClientConnector2.currentInstance().getPermission() != User.DATA_READ_ONLY) {
      Image imgConnect = factory.loadImage("status.folder.png");
      String tipConnect = factory.getLabel("itemTool");
      createIcon(imgConnect, tipConnect, new HyperlinkAdapter() {
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e) {
          if(toolWindow != null && !toolWindow.isDisposed()) {
            toolWindow.dispose();
            toolWindow = null;
          } else {
            toolWindow = new ToolWindow(workspace);
          }
        }
      });
      
      Image imgViewData = factory.loadImage("status.data.explorer.png");
      String tipViewData = factory.getLabel("itemViewData");
      createIcon(imgViewData, tipViewData, new HyperlinkAdapter() {
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e) {
          BrowserWidget widget = workspace.getTab().createItem();
          widget.viewPage();
        }
      });
//    }
    
    addListener(SWT.Resize, new Listener() {
      @SuppressWarnings("unused")
      public void handleEvent(Event e) {
        Rectangle rect = getClientArea();
        int x = 0;//rect.width/2 - icons.size()*15; 
        int y = rect.height - 15;
        int width = 25;
        int height = 25;
        
        for(ImageHyperlink icon : icons) {
          icon.setBounds(x, y - 8, width, height);
          icon.layout();
          x += width + 5;
        }
      }
    });
   
  }
  
  private ImageHyperlink createIcon(Image image, String tip, HyperlinkAdapter listener)  {
    ImageHyperlink icon = new ImageHyperlink(this , SWT.CENTER);
    icon.setBackground(getBackground());
    if(image != null) icon.setImage(image);
    if(tip != null) icon.setToolTipText(tip);
    icons.add(icon);
    if(listener != null) icon.addHyperlinkListener(listener);
    return icon;  
  }
  
}
