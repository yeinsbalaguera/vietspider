/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.gui.module;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.gui.workspace.Workspace;
import org.vietspider.notifier.notifier.NotifierShell;
import org.vietspider.ui.XPWidgetTheme;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ImageLoader;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.vtab.outlookbar.OutlookBarPane;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 12, 2009  
 */
public class ToolWindow extends NotifierShell {

  private CLabel closeLabel;
  private  Runnable run;
  

  public ToolWindow(Workspace workspace) {
    this.init(workspace.getShell());

    GridLayout gl = new GridLayout(2, false);
    gl.marginLeft = 5;
    gl.marginTop = 0;
    gl.marginRight = 5;
    gl.marginBottom = 5;
    gl.marginHeight = 0;
    gl.horizontalSpacing = 0;
    gl.verticalSpacing = 0;
    gl.marginWidth = 0;

    shell.setLayout(gl);

    ApplicationFactory factory = new ApplicationFactory(shell, "VietSpider", getClass().getName());

    CLabel label = new CLabel(shell, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.widthHint = 200;
    label.setLayoutData(gridData);
    label.setAlignment(SWT.CENTER);
    label.setFont(UIDATA.FONT_10B);
    label.setText(factory.getLabel("sectionTool"));
    /*if(XPWidgetTheme.isPlatform()) {
      //      label.setForeground(new Color(shell.getDisplay(), 176, 88, 0));
      label.setBackground(new Color[] {
          shell.getDisplay().getSystemColor(SWT.COLOR_WHITE),
          new Color(shell.getDisplay(), 194, 224, 224),
          shell.getDisplay().getSystemColor(SWT.COLOR_WHITE)}, new int[] {55, 100}, false);
    }*/
    closeLabel = new CLabel(shell, SWT.NONE);

    closeLabel.setCursor(new Cursor(shell.getDisplay(), SWT.CURSOR_HAND));
    closeLabel.setLayoutData(new GridData(
        GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_END));
    closeLabel.setImage(closeIcon);
    closeLabel.addMouseTrackListener(new MouseTrackListener() {
      @SuppressWarnings("unused")
      public void mouseHover(MouseEvent arg0) {
      }

      @SuppressWarnings("unused")
      public void mouseExit(MouseEvent arg0) {
        closeLabel.setImage(closeIcon);
        closeLabel.redraw();
      }

      @SuppressWarnings("unused")
      public void mouseEnter(MouseEvent arg0) {
        closeLabel.setImage(closeIcon1);
        closeLabel.redraw();
      }
    });
    closeLabel.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {        
        shell.dispose();
      }
    });

    OutlookBarPane outlookControl = factory.createOutlookBarItem(shell);
    new UIControl(factory, outlookControl, workspace);
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
    outlookControl.setLayoutData(gridData);
    
//    Label lblSeparator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
//    gridData = new GridData(GridData.FILL_HORIZONTAL);
//    gridData.horizontalSpan = 2;
//    lblSeparator.setLayoutData(gridData);
    
    AppIconPanel iconPanel = new AppIconPanel(factory, workspace, shell);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    gridData.heightHint = 27;
    iconPanel.setLayoutData(gridData);

    Rectangle clientArea = workspace.getShell().getBounds();
    shell.setSize(200, clientArea.height - 110);
//    shell.setAlpha(240);
    int startX = clientArea.x + 10;
    int startY = clientArea.y + clientArea.height - shell.getSize().y - 40;
    shell.setLocation(startX, startY);
    run = new Runnable() {
      @Override
      public void run() {
        if (shell == null 
            || shell.isDisposed()) return; 
        if(isFocus(shell)) {
          shell.setAlpha(240);
          shell.getDisplay().timerExec(3*1000, run);
        } else {
          fadeOut();
          if (shell == null 
              || shell.isDisposed()) return; 
          shell.getDisplay().timerExec(100, run);
        }
      }
    };
    shell.getDisplay().timerExec(5*1000, run);

    shell.open();
  }

  public boolean isDisposed() { return shell.isDisposed(); }

  public void dispose() { shell.dispose(); }

  public void remove() {
    if (oldImage != null) oldImage.dispose();
    shell.dispose();
  }

  public void init(Shell parent) {
    if(closeIcon == null) {
      ImageLoader imageLoader = new ImageLoader();
      closeIcon = imageLoader.load(parent.getDisplay(), "w_cls_h.gif");
      closeIcon1 = imageLoader.load(parent.getDisplay(), "w_cls_c.gif");
    }

    shell = new Shell(parent, SWT.NO_TRIM);
    shell.setLayout(new FillLayout());
    shell.setForeground(fgColor);
    shell.setBackgroundMode(SWT.INHERIT_DEFAULT);

    shell.addListener(SWT.Resize, new Listener() {
      @SuppressWarnings("unused")
      public void handleEvent(Event e) {
        try {
          // get the size of the drawing area
          Rectangle rect = shell.getClientArea();
          // create a new image with that size
          Image newImage = new Image(Display.getDefault(), Math.max(1, rect.width), rect.height);
          // create a GC object we can use to draw with
          GC gc = new GC(newImage);

          // fill background
          //          gc.setForeground(_bgFgGradient);
          //          gc.setBackground(_bgBgGradient);
          //          gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);

          // draw shell edge
          gc.setLineWidth(2);
          gc.setForeground(borderColor);
          gc.drawRectangle(rect.x, rect.y, rect.width, rect.height);
          // remember to dipose the GC object!
          gc.dispose();

          // now set the background image on the shell
          shell.setBackgroundImage(newImage);

          // remember/dispose old used iamge
          if (oldImage != null) oldImage.dispose();
          oldImage = newImage;
        } catch (Exception err) {
          ClientLog.getInstance().setThrowable(null, err);
        }
      }
    });
  }

  protected void fadeOut() {
    try {
      if (shell == null 
          || shell.isDisposed()) return; 
      
      String os_name = System.getProperty("os.name").toLowerCase();
      if(os_name.indexOf("linux") > -1) {
        shell.setAlpha(0);
        if (oldImage != null) oldImage.dispose();
        remove();
        return;
      }

      int cur = shell.getAlpha();
      cur -= FADE_OUT_STEP;

      if (cur <= 0) {
        shell.setAlpha(0);
        if (oldImage != null) oldImage.dispose();
        remove();
        return;
      }

      shell.setAlpha(cur);

    } catch (Exception err) {
      ClientLog.getInstance().setThrowable(null, err);
    }

  }

  public boolean isFocus(Composite composite)  { 
    if(composite.isDisposed()) return false;
    if(composite.isFocusControl()) return true;
    Control [] controls = composite.getChildren();
    for(int i = 0; i < controls.length; i++) {
      if(!(controls[i] instanceof Composite)) continue;
      if(isFocus((Composite)controls[i])) return true;
    }
    return false;
  }
  

}
