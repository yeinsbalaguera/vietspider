/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.widget;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 24, 2002
 */
public class Compusite extends Composite{
  
  public Compusite(Composite parent, int style) {
    super( parent, style); 
    addPaintListener(new PaintListener(){
      public void paintControl(PaintEvent event) {
        onPaint(event);
      }
    });
  }
  
 void onPaint(PaintEvent event) {
    Rectangle rect = getClientArea();   
    if (rect.width == 0 || rect.height == 0) return;
    GC gc = event.gc;
    gc.setForeground(new Color(getDisplay(), 250, 250, 250));   
    
    gc.drawRoundRectangle(0, 0, rect.width-6, rect.height-2 , 10, 10);
    gc.drawRoundRectangle(1, 1, rect.width-8, rect.height -4 , 10, 10);
  }

}
