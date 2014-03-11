/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.widget;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 6, 2006
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class RButton extends Canvas {
  
  private int hIndent = 3;

  private Color[] gradientColors;

  private int[] gradientPercents;    

  private Color c = new Color(getDisplay(), 240, 240, 240);

  private final  Color [] onColor = new Color[]{c, new Color(getDisplay(), 230, 230, 230)};

  private final Color [] exitColor= new Color[]{new Color(getDisplay(), 255, 255, 255), c};

  public boolean top = false;

  private Image image;
  
  private int type = 0;


  public RButton(Composite parent, Image img, int t) {
    super(parent, SWT.LEFT );
    type = t;
    image = img;

    setBackground(exitColor, new int[] {100});

    addMouseTrackListener(new MouseTrackAdapter(){
      @SuppressWarnings("unused")
      public void mouseEnter(MouseEvent e){
        setBackground(onColor, new int[] {100}); 
      }
      @SuppressWarnings("unused")
      public void mouseExit(MouseEvent e){
        setBackground(exitColor, new int[] {100});
      }
    });

    addPaintListener(new PaintListener(){
      public void paintControl(PaintEvent event) {
        onPaint(event);
      }
    });

    addDisposeListener(new DisposeListener(){
      public void widgetDisposed(DisposeEvent event) {
        onDispose(event);
      }
    });

    setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));    
    setForeground(new Color(getDisplay(), 0, 64, 64));   
  }

  @SuppressWarnings("unused")
  void onDispose(DisposeEvent event) {
    gradientColors = null;
    gradientPercents = null; 
  }


  void onPaint(PaintEvent event) {
    Rectangle rect = getClientArea();
    if (rect.width == 0 || rect.height == 0) return;
    Point extent = new Point(0, 0);  

    GC gc = event.gc;

    int x = rect.x + hIndent;  

    final Color oldBackground = gc.getBackground();

    Color lastColor = gradientColors[0];
    if (lastColor == null) lastColor = oldBackground;
    int pos = 0;
    for (int i = 0; i < gradientPercents.length; ++i) {
      gc.setForeground(lastColor);
      lastColor = gradientColors[i + 1];
      if (lastColor == null) lastColor = oldBackground;
      gc.setBackground(lastColor);
      final int gradientHeight = (gradientPercents[i] * rect.height / 100) - pos;
      gc.fillGradientRectangle(2, pos+2, rect.width-9, gradientHeight-36, true);
      pos += gradientHeight;      
    }

    gc.setForeground(new Color(getDisplay(), 245, 245, 245));  
    gc.drawRectangle(0, 0, rect.width - 6, rect.height - 33);

    if (pos < rect.height) {
      gc.setBackground(getBackground());
      gc.fillRoundRectangle(0, pos, rect.width, rect.height - pos, 3, 4);
    }     
    Image img = image;
    if(img != null){
      Rectangle imageRect = img.getBounds();
      if(type == 0){
        gc.drawImage(img, 0, 0, imageRect.width, imageRect.height, 
            x+1, (rect.height-imageRect.height)/2-16, imageRect.width, imageRect.height);
      }else if(type == 1){
          gc.drawImage(img, 0, 0, imageRect.width, imageRect.height, 
              x+3, (rect.height-imageRect.height)/2-16, imageRect.width, imageRect.height);
      }else
        gc.drawImage(img, 0, 0, imageRect.width, imageRect.height, 
            x+3, (rect.height-imageRect.height)/2-16, imageRect.width, imageRect.height);
      x +=  imageRect.width + 5;
      extent.x -= imageRect.width + 5;
    }
  }
  
  @SuppressWarnings("hiding")
  public void setBackground(Color[] colors, int[] percents) { 
    checkWidget();
    if (getDisplay().getDepth() < 15) {
      colors = new Color[] {colors[colors.length - 1]};
      percents = new int[] { };
    }   

    final Color background = getBackground();

    gradientColors = new Color[colors.length];
    for (int i = 0; i < colors.length; ++i)
      gradientColors[i] = (colors[i] != null) ? colors[i] : background;
    gradientPercents = new int[percents.length];
    for (int i = 0; i < percents.length; ++i)  gradientPercents[i] = percents[i];

    redraw();
  }

  public Image getImage(){
    return image;
  }

  public void setImage(Image i){
    image = i;
  }
}