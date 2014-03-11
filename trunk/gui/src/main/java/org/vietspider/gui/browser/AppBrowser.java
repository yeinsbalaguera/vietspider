/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.gui.config.Config;
import org.vietspider.gui.crawler.Crawler;
import org.vietspider.gui.log.LogViewer2;
import org.vietspider.gui.wizard.ChannelWizard;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.user.User;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 3, 2008  
 */
public class AppBrowser extends ControlComponent {

  protected Toolbar toolbar;

  public AppBrowser(TabBrowser parent, Workspace workspace_) {
    super(parent, workspace_, SWT.TRANSPARENCY_ALPHA);
      this.workspace = workspace_;

      setBackgroundMode(SWT.INHERIT_DEFAULT);

      ApplicationFactory factory = new ApplicationFactory(this, "VietSpider", getClass().getName());
//    setBackground(new Color(getDisplay(), 255, 255, 255));

      GridLayout gridLayout = new GridLayout(1, false);
      gridLayout.marginHeight = 5;
      gridLayout.horizontalSpacing = 10;
      gridLayout.verticalSpacing = 15;
      gridLayout.marginWidth = 5;
      setLayout(gridLayout);

      toolbar = new Toolbar(this, parent);
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL); 
      toolbar.setLayoutData(gridData); 

      initIcons(factory);

//    configLink.addHyperlinkListener(listener);

      StatusBar statusBar = new StatusBar(workspace, this, SWT.TRANSPARENCY_ALPHA);
      gridData = new GridData(GridData.FILL_HORIZONTAL);
      statusBar.setLayoutData(gridData);  
      statusBar.setBackground(getBackground());
      statusBar.setComponent(this);

//    tab.setStatusBar(statusBar);
  }


  private void initIcons(ApplicationFactory factory)  {
    Composite center = null;
    if(Application.LICENSE == Install.PERSONAL) {
      center = new Composite(this, SWT.TRANSPARENCY_ALPHA);
      GridData gridData = new GridData(GridData.FILL_BOTH);
      center.setLayoutData(gridData);

      GridLayout gridLayout = new GridLayout(2, false);
      center.setLayout(gridLayout);

      Browser widget = null;

//      ClientLog.getInstance().setMessage(getShell(), new Exception( "buoc 1 da chay vao day roi " + widget.toString()));

      try {
        widget = new Browser(center, SWT.NONE);
//        ClientLog.getInstance().setMessage(getShell(), new Exception( " da chay vao day roi " + widget.toString()));
      } catch (Exception e) {
        widget = new Browser(center, SWT.NONE);
        ClientLog.getInstance().setException(null, e);
      }
      gridData = new GridData(GridData.FILL_BOTH);
      gridData.verticalSpan = 2;
      widget.setLayoutData(gridData);
      if (Application.GROUPS.length > 0 && Application.GROUPS[0].equals("XML")) {
        widget.setUrl("http://vietspider.org/webextractor/");
        toolbar.setText("http://vietspider.org/webextractor/");
      } else {
        widget.setUrl("http://nik.vn/tin/");
        //      widget.setUrl("http://thuannd:81/?fullscreen=true");
        toolbar.setText("http://nik.vn/tin");
      }
    } else {
      Label label = new Label(this, SWT.TRANSPARENCY_ALPHA);
      label.setText("");
      label.setLayoutData(new GridData(GridData.FILL_BOTH));
      center = this;
    }

//    label.setBackground(getBackground());

//    // Set the background gradient
//    label.setBackground(new Color[] {color1, color2, color3}, new int[] { 20, 100});
    Composite top = new Composite(center, SWT.TRANSPARENCY_ALPHA);
    GridData gridData = null;
    if(Application.LICENSE == Install.PERSONAL) {
      gridData = new GridData();
      gridData.widthHint =  350;
    } else {
      gridData = new GridData(GridData.FILL_BOTH);
    }
    top.setLayoutData(gridData);
//    top.setBackground(getBackground());

    RowLayout rowLayout = new RowLayout();
    rowLayout.wrap = true;
    rowLayout.pack = true;
    rowLayout.justify = true;
    rowLayout.type = SWT.HORIZONTAL;
    rowLayout.marginLeft = 5;
    rowLayout.marginTop = 5;
    rowLayout.marginRight = 5;
    rowLayout.marginBottom = 5;
    rowLayout.spacing = 20;
    top.setLayout(rowLayout);

    Composite composite = createItem(top);

    if(ClientConnector2.currentInstance().getPermission() != User.ROLE_ADMIN) return;

//    Color color = new Color(getDisplay(), 0, 37, 55);

    final ImageHyperlink browserImageLink = 
        new ImageHyperlink(composite,  SWT.CENTER | SWT.TRANSPARENCY_ALPHA);
    browserImageLink.setImage(factory.loadImage("large.dataexplorer.png"));
    browserImageLink.setToolTipText(factory.getLabel("browserLink"));
//    final Hyperlink browserLink = createLink(composite);
//    browserImageLink.setBackground(getBackground());
//    browserLink.setText(factory.getLabel("browserLink"));
//    browserLink.setForeground(color);
    HyperlinkAdapter listener = new HyperlinkAdapter() {
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        BrowserWidget browser = workspace.getTab().createItem();
        browser.viewPage();
      }
    };
    browserImageLink.addHyperlinkListener(listener);
//    browserLink.addHyperlinkListener(listener);

    composite = createItem(top);
    final ImageHyperlink creatorImageLink = 
        new ImageHyperlink(composite, SWT.CENTER | SWT.TRANSPARENCY_ALPHA);
    creatorImageLink.setImage(factory.loadImage("large.createsource.png"));
//    creatorImageLink.setBackground(getBackground());
    creatorImageLink.setToolTipText(factory.getLabel("creatorLink"));
//    final Hyperlink creatorLink = createLink(composite);
//    creatorLink.setText(factory.getLabel("creatorLink"));
//    creatorLink.setForeground(color);
    listener = new HyperlinkAdapter() {
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
//        creatorLink.setUnderlined(true);
      }

      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
//        creatorLink.setUnderlined(false);
      }
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
//        creatorLink.setUnderlined(false);
        try {
          ChannelWizard wizard = (ChannelWizard)workspace.getTab().createTool(
              ChannelWizard.class, false,  SWT.CLOSE);
        } catch (Exception exp) {
          ClientLog.getInstance().setException(null, exp);
        }
//        try {
//          Creator creator = (Creator)workspace.getTab().createTool(
//              Creator.class, false,  SWT.CLOSE);
//          creator.selectData(new Worker[0], null, null);
//        } catch (Exception exp) {
//          ClientLog.getInstance().setException(null, exp);
//        }
      }
    };
    creatorImageLink.addHyperlinkListener(listener);
//    creatorLink.addHyperlinkListener(listener);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    Composite bottom = new Composite(center, SWT.TRANSPARENCY_ALPHA);
    if(Application.LICENSE == Install.PERSONAL) {
      gridData = new GridData();
      gridData.widthHint =  350;
    } else {
      gridData = new GridData(GridData.FILL_BOTH);
    }
    bottom.setLayoutData(gridData);
//    bottom.setBackground(getBackground());

    rowLayout = new RowLayout();
    rowLayout.wrap = true;
    rowLayout.pack = true;
    rowLayout.justify = true;
    rowLayout.type = SWT.HORIZONTAL;
    rowLayout.marginLeft = 5;
    rowLayout.marginTop = 5;
    rowLayout.marginRight = 5;
    rowLayout.marginBottom = 5;
    rowLayout.spacing = 20;
    bottom.setLayout(rowLayout);

    if(Application.LICENSE  != Install.PERSONAL) {
      composite = createItem(top);
    } else {
      composite = createItem(bottom);
    }
    final ImageHyperlink crawlerImageLink =
        new ImageHyperlink(composite, SWT.CENTER | SWT.TRANSPARENCY_ALPHA);
    crawlerImageLink.setImage(factory.loadImage("large.crawler.png"));
//    crawlerImageLink.setBackground(getBackground());
    crawlerImageLink.setToolTipText(factory.getLabel("crawlerLink"));
//    final Hyperlink crawlerLink = createLink(composite);
//    crawlerLink.setText(factory.getLabel("crawlerLink"));
//    crawlerLink.setForeground(color);
    listener = new HyperlinkAdapter() {
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
//        crawlerLink.setUnderlined(true);
      }

      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
//        crawlerLink.setUnderlined(false);
      }
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
//        crawlerLink.setUnderlined(false);
        try {
          workspace.getTab().createTool(Crawler.class, true,  SWT.CLOSE);
        } catch (Exception exp) {
          ClientLog.getInstance().setException(getShell(), exp);
        }
      }
    };
    crawlerImageLink.addHyperlinkListener(listener);
//    crawlerLink.addHyperlinkListener(listener);


    if(Application.LICENSE  != Install.PERSONAL) {
      composite = createItem(bottom);
      final ImageHyperlink monitorImageLink = 
          new ImageHyperlink(composite, SWT.CENTER | SWT.TRANSPARENCY_ALPHA);
      monitorImageLink.setImage(factory.loadImage("large.log.png"));
//      monitorImageLink.setBackground(getBackground());
      monitorImageLink.setToolTipText(factory.getLabel("logLink"));
//      final Hyperlink monitorLink = createLink(composite);
//      monitorLink.setText(factory.getLabel("monitorLink"));
//      monitorLink.setForeground(color);
      listener = new HyperlinkAdapter() {
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e) {
          try {          
            workspace.getTab().createTool(LogViewer2.class, true, SWT.CLOSE);
          }catch (Exception exp) {
            ClientLog.getInstance().setException(workspace.getShell(), exp);
          }
        }
      };
      monitorImageLink.addHyperlinkListener(listener);
//      monitorLink.addHyperlinkListener(listener);
    }


    /*if(Application.LICENSE  != Install.PERSONAL) {
      composite = createItem(bottom);
      final ImageHyperlink userImageLink = 
        new ImageHyperlink(composite, SWT.CENTER | SWT.TRANSPARENCY_ALPHA);
      userImageLink.setImage(factory.loadImage("large.userfolder.png"));
//      userImageLink.setBackground(getBackground());
      userImageLink.setToolTipText(factory.getLabel("userLink"));
//      final Hyperlink userLink = createLink(composite);
//      userLink.setText(factory.getLabel("userLink"));
//      userLink.setForeground(color);
      listener = new HyperlinkAdapter() {
        @SuppressWarnings("unused")
        public void linkEntered(HyperlinkEvent e) {
//          userLink.setUnderlined(true);
        }

        @SuppressWarnings("unused")
        public void linkExited(HyperlinkEvent e) {
//          userLink.setUnderlined(false);
//          userLink.setFont(UIDATA.FONT_9VB);
        }
        @SuppressWarnings("unused")
        public void linkActivated(HyperlinkEvent e) {
//          userLink.setUnderlined(false);
          try {          
            workspace.getTab().createTool(Organization.class, true,  SWT.CLOSE);
          }catch (Exception exp) {
            ClientLog.getInstance().setException(workspace.getShell(), exp);
          }
        }
      };
      userImageLink.addHyperlinkListener(listener);
//      userLink.addHyperlinkListener(listener);
    }*/

    composite = createItem(bottom);

    final ImageHyperlink configImageLink = 
        new ImageHyperlink(composite, SWT.CENTER | SWT.TRANSPARENCY_ALPHA);
    configImageLink.setImage(factory.loadImage("large.settingsfolder.png"));
//    configImageLink.setBackground(getBackground());
    configImageLink.setToolTipText(factory.getLabel("configLink"));
//    final Hyperlink configLink = createLink(composite);
//    configLink.setText(factory.getLabel("configLink"));
//    configLink.setForeground(color);
    listener = new HyperlinkAdapter() {
      @SuppressWarnings("unused")
      public void linkEntered(HyperlinkEvent e) {
//        configLink.setUnderlined(true);
      }

      @SuppressWarnings("unused")
      public void linkExited(HyperlinkEvent e) {
//        configLink.setUnderlined(false);
      }
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
//        configLink.setUnderlined(false);
        try {          
          workspace.getTab().createTool(Config.class, true, SWT.CLOSE);
        }catch (Exception exp) {
          ClientLog.getInstance().setException(workspace.getShell(), exp);
        }
      }
    };
    configImageLink.addHyperlinkListener(listener);
  }


  public Browser invokePlugin() {
    BrowserWidget widget = workspace.getTab().createItem();
    return widget.getWidget();
  }

  private Composite createItem(Composite parent) {
    Composite composite = new Composite(parent, SWT.TRANSPARENCY_ALPHA);
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.makeColumnsEqualWidth = true;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    composite.setLayout(gridLayout);
//    composite.setBackground(getBackground());
    return composite;
  }

  public String getNameIcon() { return "window.png"; }

  /*private void paint() {
    try {
      // get the size of the drawing area
      Rectangle rect = getClientArea();
      if(rect.height < 1) return;
      // create a new image with that size
      Image newImage = new Image(getDisplay(), Math.max(1, rect.width), rect.height);
      // create a GC object we can use to draw with
      GC gc = new GC(newImage);

      // fill background
      gc.setForeground(_bgFgGradient);
      gc.setBackground(_bgBgGradient);
      gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height - rect.height/2, true);

      // draw shell edge
      gc.setLineWidth(2);
      gc.setForeground(_borderColor);
      gc.drawRectangle(rect.x, rect.y, rect.width, rect.height);
      // remember to dipose the GC object!
      gc.dispose();

      // now set the background image on the shell
      setBackgroundImage(newImage);

      // remember/dispose old used iamge
      if (_oldImage != null) _oldImage.dispose();
      _oldImage = newImage;
    } catch (Exception err) {
      err.printStackTrace();
    }
  }*/
}
