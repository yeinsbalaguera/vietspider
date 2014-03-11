/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.notifier.notifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.notifier.cache.ColorCache;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ImageLoader;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 9, 2009  
 */
public abstract class NotifierShell {

  protected  int  fadeTimer    = 50;
  protected int   fadeInStep  = 30;
  protected int   FADE_OUT_STEP = 8;
  protected int   FINAL_ALPHA   = 225;
  protected  Color titleFgColor = ColorCache.getColor(0, 0, 59);
  protected Color textFgColor = ColorCache.getColor(255, 0, 0);
  protected Color fgColor      = titleFgColor;

  protected Color bgFgGradient = ColorCache.getColor(226, 239, 249);
  public Color    bgBgGradient = ColorCache.getColor(255, 255, 255);
  protected Color borderColor  = ColorCache.getColor(40, 73, 97);

  protected Font titleFont = UIDATA.FONT_11B;
  protected Font textFont = UIDATA.FONT_10;
  
  protected int width = 350;

  protected Image   oldImage;
  protected volatile Shell       shell;
  protected int displayTime  = 9000;

  protected volatile Image closeIcon;
  protected volatile Image closeIcon1;

  public NotifierShell(Notifier notifier) {
    init(notifier);
  }

  public NotifierShell() {
  }

  public void init(Notifier notifier) {
    this.displayTime = notifier.getDisplayTime();
    this.init(notifier.getParent());
  }

  public void init(Shell parent) {
//    if(parent == null || parent.isDisposed()) return;
    String os_name = System.getProperty("os.name").toLowerCase();
    if(os_name.indexOf("linux") > -1) fadeTimer  = 1000;

    if(closeIcon == null) {
      ImageLoader imageLoader = new ImageLoader();
      closeIcon = imageLoader.load(parent.getDisplay(), "w_cls_h.gif");
      closeIcon1 = imageLoader.load(parent.getDisplay(), "w_cls_c.gif");
    }

    shell = new Shell(parent, SWT.NO_TRIM | SWT.ON_TOP);
    shell.setLayout(new FillLayout());
    shell.setForeground(fgColor);
    shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
    shell.addListener(SWT.Dispose, new Listener() {
      @SuppressWarnings("unused")
      public void handleEvent(Event event) {
        NotifierDialogs.activeShells.remove(shell);
      }
    });

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
          gc.setForeground(bgFgGradient);
          gc.setBackground(bgBgGradient);
          gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);

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
          err.printStackTrace();
        }
      }
    });
  }

  public Shell getShell() { return shell;  }

  public void fadeIn() {
    Runnable run = new Runnable() {

      @Override
      public void run() {
        try {
          if (shell == null || shell.isDisposed())  return;

          int cur = shell.getAlpha();
          cur += fadeInStep;

          if (cur > FINAL_ALPHA) {
            shell.setAlpha(FINAL_ALPHA);
            if(!shell.isDisposed()) startTimer();
            return;
          }
          shell.setAlpha(cur);
          Display.getDefault().timerExec(fadeTimer, this);
        } catch (Exception err) {
          err.printStackTrace();
        }
      }

    };
    Display.getDefault().timerExec(fadeTimer, run);
  }

  protected void fadeOut() {
    final Runnable run = new Runnable() {

      @Override
      public void run() {
        try {
          if (shell == null || shell.isDisposed()) return;

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

          Display.getDefault().timerExec(fadeTimer, this);

        } catch (Exception err) {
          ClientLog.getInstance().setThrowable(null, err);
        }
      }

    };
    Display.getDefault().timerExec(fadeTimer, run);
  }

  protected void startTimer() {
    Runnable run = new Runnable() {

      @Override
      public void run() {
        try {
          if (shell == null || shell.isDisposed()) { return; }
          fadeOut();
        } catch (Exception err) {
          ClientLog.getInstance().setThrowable(null, err);
        }
      }
    };
    Display.getDefault().timerExec(displayTime, run);
  }

  public abstract void remove() ;

  public Color getTitleFgColor() {
    return titleFgColor;
  }

  public void setTitleFgColor(Color titleFgColor) {
    this.titleFgColor = titleFgColor;
  }

  public Color getTextFgColor() {
    return textFgColor;
  }

  public void setTextFgColor(Color textFgColor) {
    this.textFgColor = textFgColor;
  }

  public Color getFgColor() {
    return fgColor;
  }

  public void setFgColor(Color fgColor) {
    this.fgColor = fgColor;
  }

  public Color getBgFgGradient() {
    return bgFgGradient;
  }

  public void setBgFgGradient(Color bgFgGradient) {
    this.bgFgGradient = bgFgGradient;
  }

  public Color getBgBgGradient() {
    return bgBgGradient;
  }

  public void setBgBgGradient(Color bgBgGradient) {
    this.bgBgGradient = bgBgGradient;
  }

  public Color getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }

  public Font getTitleFont() {
    return titleFont;
  }

  public void setTitleFont(Font titleFont) {
    this.titleFont = titleFont;
  }

  public Font getTextFont() {
    return textFont;
  }

  public void setTextFont(Font textFont) {
    this.textFont = textFont;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }
  
  
}
