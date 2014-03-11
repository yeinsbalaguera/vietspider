/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.module;

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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.workspace.VietSpiderClient;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;
import org.vietspider.user.User;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 12, 2009  
 */
public class AppIconPanel extends Composite {

  private Workspace workspace;

  private List<ImageHyperlink> icons;


  public AppIconPanel(ApplicationFactory factory, Workspace _workspace, Composite parent) {
    super(parent, SWT.TRANSPARENCY_ALPHA);
    this.workspace = _workspace;

    //    setBackground(new Color(getDisplay(), 225, 240, 240));//getDisplay().getSystemColor(SWT.COLOR_WHITE));

    icons = new ArrayList<ImageHyperlink>();

    if(Application.LICENSE != Install.PERSONAL) {
      if(ClientConnector2.currentInstance().getPermission() == User.ROLE_ADMIN) {
        Image imgShutdown = factory.loadImage("status.shutdown.png");
        String tipShutdown = factory.getLabel("itemShutdown");
        createIcon(imgShutdown, tipShutdown, new HyperlinkAdapter() {
          @SuppressWarnings("unused")
          public void linkActivated(HyperlinkEvent e) {
            ClientRM resource = new ClientRM("VietSpider");
            MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO);
            msg.setMessage(resource.getLabel("msgAlertShutdownServer"));
            if(msg.open() != SWT.YES) return ;
            shutdownServer();
          }
        });
      }

      Image imgLogout = factory.loadImage("status.logout.png");
      String tipLogout = factory.getLabel("itemLogout");
      createIcon(imgLogout, tipLogout, new HyperlinkAdapter() {
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e) {
          VietSpiderClient.connect(true);
        }
      });
    }

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
          File file = new File(folder.getParentFile().getParentFile().getParentFile().getAbsoluteFile(), 
              "help/User Guide.pdf");
          if(file.exists() && file.length() > 0) {
            workspace.getTab().createItem().setUrl(file.toURI().toURL().toString());
            return;
          }
          file = new File(folder, "help.htm");
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

    /*Image imgViewData = factory.loadImage("status.data.explorer.png");
    String tipViewData = factory.getLabel("itemViewData");
    createIcon(imgViewData, tipViewData, new HyperlinkAdapter() {
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        BrowserWidget widget = workspace.getTab().createItem();
        widget.viewPage();
      }
    });*/
    //    }

    addListener(SWT.Resize, new Listener() {
      @SuppressWarnings("unused")
      public void handleEvent(Event e) {
        Rectangle rect = getClientArea();
        int x = 10;//rect.width/2 - icons.size()*15; 
        int y = rect.height - 15;
        int width = 25;
        int height = 25;

        for(ImageHyperlink icon : icons) {
          icon.setBounds(x, y - 10, width, height);
          icon.layout();
          x += width + 5;
        }
      }
    });

  }

  private ImageHyperlink createIcon(Image image, String tip, HyperlinkAdapter listener)  {
    ImageHyperlink icon = new ImageHyperlink(this , SWT.CENTER);
    icon.setBackground(UIDATA.BCOLOR);
    if(image != null) icon.setImage(image);
    if(tip != null) icon.setToolTipText(tip);
    icons.add(icon);
    if(listener != null) icon.addHyperlinkListener(listener);
    return icon;  
  }

  private void shutdownServer() {
    if(ClientConnector2.currentInstance().getPermission() != User.ROLE_ADMIN ) return;

    Worker excutor = new Worker() {

      private String message = "";

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
      }

      public void execute() {
        try {
          DataClientHandler.getInstance().exitApplication();
        } catch (Exception e) {
          if(e.getMessage() != null && e.getMessage().trim().length() > 0) {
            message = e.getMessage();
          } else {
            message = e.toString();
          }
        }
      }

      public void after() {
        if(message != null && !message.trim().isEmpty()) {
          MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(message);
          msg.open();
        }
      }
    };
    WaitLoading loading = new WaitLoading(getShell().getParent(), excutor);
    loading.open();
  }
}
