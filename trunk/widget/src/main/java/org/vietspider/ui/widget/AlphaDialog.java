/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.widget;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 7, 2009  
 */
public class AlphaDialog {

  protected  Shell popup;

  protected int  fadeTimer    = 50;
  protected int  fadeInStep  = 30;
  protected int  fadeOutStep = 8;
  protected int  finalAlpha   = 255;
  protected int  displayTime  = 4500;
  
  public AlphaDialog(Shell parent, int style) {
    popup = new Shell(parent, style);
    String os_name = System.getProperty("os.name").toLowerCase();
    if(os_name.indexOf("linux") > -1) fadeTimer = 1000;
  }

  public Shell getPopup() { return popup; }

  public boolean isDisposed() { return popup.isDisposed(); }
  public void dispose() { popup.dispose (); }

  public void setVisible(boolean value) { popup.setVisible(value); }
  public boolean getVisible() { return popup.getVisible(); }
  public boolean isVisible() { return popup.isVisible(); }

  public void setBackgroundMode(int mode) { popup.setBackgroundMode(mode); }
  public void setBackground(Color color) { popup.setBackground(color); }

  public Control getParent() { return popup.getParent(); }

  public void setAlpha(int value) { popup.setAlpha(value); }
  public int getAlpha() { return popup.getAlpha(); }

  public boolean isFocusControl() { return popup.isFocusControl(); }

  public void setBounds(int x, int y, int w, int h) {
    popup.setBounds(x, y, w, h);
  }

  protected void showFadeIn() {
    popup.setVisible(true);
    popup.setAlpha(0);
    fadeIn();
  }

  void fadeIn() {
    Runnable run = new Runnable() {
      @Override
      public void run() {
        try {
          if (popup == null || popup.isDisposed())  return;

          int cur = popup.getAlpha();
          cur += fadeInStep;

          if (cur > finalAlpha) {
            popup.setAlpha(finalAlpha);
            startTimer();
            return;
          }

          popup.setAlpha(cur);
          Display.getDefault().timerExec(fadeTimer, this);
        } catch (Exception err) {
        }
      }

    };
    Display.getDefault().timerExec(fadeTimer, run);
  }

  private void startTimer() {
    Runnable run = new Runnable() {
      @Override
      public void run() {
        try {
          if (popup == null || popup.isDisposed())  return;
          fadeOut();
        } catch (Exception err) {
          err.printStackTrace();
        }
      }

    };
    Display.getDefault().timerExec(displayTime, run);
  }

  public void fadeOut() {
    final Runnable run = new Runnable() {
      @Override
      public void run() {
        try {
          if (popup == null || popup.isDisposed()) return;
          
          String os_name = System.getProperty("os.name").toLowerCase();
          if(os_name.indexOf("linux") > -1) {
            popup.setVisible(false);
            return;
          }

          int cur = popup.getAlpha();
          cur -= fadeOutStep;

          if (cur <= 0) {
            popup.setAlpha(0);
            popup.setVisible(false);
            return;
          }
          popup.setAlpha(cur);

          Display.getDefault().timerExec(fadeTimer, this);
        } catch (Exception err) {
        }
      }
    };
    Display.getDefault().timerExec(fadeTimer, run);
  }

  public int getFadeTimer() { return fadeTimer; }
  public void setFadeTimer(int fadeTimer) { this.fadeTimer = fadeTimer; }

  public int getFadeInStep() { return fadeInStep; }
  public void setFadeInStep(int fadeInStep) { this.fadeInStep = fadeInStep; }

  public int getFadeOutStep() { return fadeOutStep; }
  public void setFadeOutStep(int fadeOutStep) { this.fadeOutStep = fadeOutStep; }

  public int getFinalAlpha() { return finalAlpha; }
  public void setFinalAlpha(int finalAlpha) { this.finalAlpha = finalAlpha; }

  public int getDisplayTime() { return displayTime; }
  public void setDisplayTime(int displayTime) { this.displayTime = displayTime; }

}
