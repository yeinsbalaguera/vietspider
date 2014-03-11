/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.ui.widget.images;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.services.ImageLoader;
import org.vietspider.ui.widget.ImageHyperlink;
import org.vietspider.ui.widget.action.IHyperlinkListener;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 30, 2008  
 */
public final class ToolbarResource {
  
  private static ToolbarResource instance ; 
  
  public final static ToolbarResource getInstance() { return instance; }
  
  public final static void createInstance(Display display, String resource, Class<?> clazz) {
    instance = new ToolbarResource(display, resource, clazz);
  }
  
  private String textBack;
  private Image imageBack1;
  private Image imageBack;
  
  private String textForward;
  private Image imageForward1;
  private Image imageForward;
  
  private String textRefesh;
  private Image imageRefesh1;
  private Image imageRefesh;
  
  private String textGo;
  private Image imageGo1;
  private Image imageGo;
  
  private String textStop;
  private Image imageStop1;
  private Image imageStop;
  
  private Image imageCancel1;
  private Image imageCancel;
  
  public ToolbarResource(Display display, String resource, Class<?> clazz) {
    ImageLoader imgResources = new ImageLoader();
    
    ClientRM txtResources = new ClientRM(resource);
    
    textBack = txtResources.getLabel(clazz.getName()+".backTip");
//    if(XPWidgetTheme.isPlatform()) {
//      imageBack1 = imgResources.load(display, "xBack1.png");
//      imageBack = imgResources.load(display, "xBack.png");
//    } else {
      imageBack1 = imgResources.load(display, "back1.png");
      imageBack = imgResources.load(display, "back.png");
//    }
    
    textForward = txtResources.getLabel(clazz.getName()+".forwardTip");
//    if(XPWidgetTheme.isPlatform()) {
//      imageForward1 = imgResources.load(display, "xForward1.png");
//      imageForward = imgResources.load(display, "xForward.png");
//    } else {
      imageForward1 = imgResources.load(display, "forward1.png");
      imageForward = imgResources.load(display, "forward.png");
//    }
    
    textRefesh = txtResources.getLabel(clazz.getName()+".refreshTip");
//    if(XPWidgetTheme.isPlatform()) {
//      imageRefesh1 = imgResources.load(display, "xRefresh1.png");
//      imageRefesh = imgResources.load(display, "xRefresh.png");
//    } else {
      imageRefesh1 = imgResources.load(display, "refresh1.png");
      imageRefesh = imgResources.load(display, "refresh.png");
//    }
    
    textGo = txtResources.getLabel(clazz.getName()+".goTip");
    imageGo = imgResources.load(display, "godom.png");
    imageGo1 = imgResources.load(display, "godom1.png");
    
    textStop = txtResources.getLabel(clazz.getName()+".stopTip");
    imageStop1 = imgResources.load(display, "stop1.png");
    imageStop = imgResources.load(display, "stop.png");
    
    imageCancel1 = imgResources.load(display, "xgocancel1.png");
    imageCancel = imgResources.load(display, "xgocancel.png");
  }
  
  public String getTextBack() {
    return textBack;
  }

  public Image getImageBack1() {
    return imageBack1;
  }

  public Image getImageBack() {
    return imageBack;
  }

  public String getTextForward() {
    return textForward;
  }

  public Image getImageForward1() {
    return imageForward1;
  }

  public Image getImageForward() {
    return imageForward;
  }

  public String getTextRefesh() {
    return textRefesh;
  }

  public Image getImageRefesh1() {
    return imageRefesh1;
  }

  public Image getImageRefesh() {
    return imageRefesh;
  }


  public String getTextStop() {
    return textStop;
  }

  public Image getImageStop1() {
    return imageStop1;
  }

  public Image getImageStop() {
    return imageStop;
  }

  public String getTextGo() {
    return textGo;
  }

  public void setTextGo(String textGo) {
    this.textGo = textGo;
  }

  public Image getImageGo1() {
    return imageGo1;
  }
  
  public Image getImageCancel1() { return imageCancel1; }
  public Image getImageCancel() { return imageCancel;  }

  public void setImageGo1(Image imageGo1) { this.imageGo1 = imageGo1; }
  public Image getImageGo() { return imageGo;  }
  
  public ImageHyperlink createIcon(Composite com, Image img, String tip, IHyperlinkListener listener) {    
    ImageHyperlink icon = new ImageHyperlink(com , SWT.CENTER);
    icon.setBackground(com.getBackground());
    icon.setImage(img);
    if(listener != null) icon.addHyperlinkListener(listener);
    icon.setToolTipText(tip);
    return icon;    
  } 

}
