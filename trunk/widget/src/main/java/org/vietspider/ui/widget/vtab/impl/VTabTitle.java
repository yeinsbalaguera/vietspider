/***************************************************************************
 * Copyright 2001-2003 The VietSpider Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.widget.vtab.impl;

/**
 * Created by VietSpider Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 6, 2006
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.vietspider.ui.widget.UIDATA;

public class VTabTitle extends Canvas {
  
  private static final String ELLIPSIS = "...";
  
  private int align = SWT.LEFT;
  
  private int hIndent = 3;
  
  private int vIndent = 3;
  
  private String text; 
  
  private Color[] gradientColors;
  
  private int[] gradientPercents;    
  
  private static int DRAW_FLAGS = SWT.DRAW_MNEMONIC | SWT.DRAW_TAB | SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER;
  
  private final Font onFont = UIDATA.FONT_10B;   
  
  private final Font exitFont = UIDATA.FONT_10B; 
  
  private Color c = new Color(getDisplay(), 240, 240, 240);
  
  private Color [] onColor = new Color[]{new Color(getDisplay(), 210, 210, 210), c};
  
  private Color [] exitColor= new Color[]{c, new Color(getDisplay(), 220, 220, 220)};
  
  private boolean areaMenu = false;  
  
  public VTabTitle(Composite parent) {
    this(parent, 
        new Color(parent.getDisplay(), 210, 210, 210), 
        new Color(parent.getDisplay(), 240, 240, 240),
        new Color(parent.getDisplay(), 220, 220, 220));
  }
  
  public VTabTitle(Composite parent, Color color1, Color color2, Color color3) {
    super(parent, SWT.LEFT );
    
    onColor =  new Color[]{color1, color2};
    exitColor =  new Color[]{color2, color3};
    
    setBackground(parent.getBackground());
    
    setFont(exitFont);
    setBackground(exitColor, new int[] {100});
    
    addMouseListener(new MouseListener(){
      @SuppressWarnings("unused")
      public void mouseDoubleClick(MouseEvent e){
        
      }
      public void mouseDown(MouseEvent e){
        Menu menu = getMenu();
        if(menu == null) return;
        areaMenu = checkAreaMenu(e);
        if(areaMenu) menu.setVisible(true);
      }
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e){
        
      }
    });
    
    addMouseMoveListener(new MouseMoveListener(){
      public void mouseMove(MouseEvent e){
        if(getMenu() == null) return;
        areaMenu = checkAreaMenu(e);
        redraw();
      }
    });
    
    addMouseTrackListener(new MouseTrackAdapter(){
      public void mouseHover(MouseEvent e) {
        if(getMenu() == null) return;
        areaMenu = checkAreaMenu(e);
        redraw();
      }
      public void mouseEnter(MouseEvent e){
        setBackground(onColor, new int[] {100});  
        setFont(onFont);
        if(getMenu() == null) return;
        areaMenu = checkAreaMenu(e);
        redraw();
      }
      public void mouseExit(MouseEvent e){
        setBackground(exitColor, new int[] {100});
        setFont(exitFont);
        if(getMenu() == null) return;
        areaMenu = checkAreaMenu(e);
        redraw();
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
    setForeground(new Color(getDisplay(), 0, 50, 100));       
    initAccessible();    
  }
  
  private Point getTotalSize(String txt) {
    Point size = new Point(0, 0);    
    GC gc = new GC(this);
    if (txt != null && txt.length() > 0) {
      Point e = gc.textExtent(txt, DRAW_FLAGS);
      size.x += e.x;
      size.y = Math.max(size.y, e.y);      
    } else {
      size.y = Math.max(size.y, gc.getFontMetrics().getHeight());
    }
    gc.dispose();    
    return size;
  }
  
  private boolean checkAreaMenu(MouseEvent event){
    Rectangle rect = getClientArea();
    if(event.y <  rect.y + 4   ||  event.y > rect.height + rect.y - 2) return false;    
    if(event.x < rect.width  + rect.x - 36 || event.x >  rect.width  + rect.x - 9) return false;        
    return true;
  }
  
  
  public String getText() { return text; }
  
  private void initAccessible() {
    Accessible accessible = getAccessible();
    accessible.addAccessibleListener(new AccessibleAdapter() {
      public void getName(AccessibleEvent e) {
        e.result = getText();
      }
      
      public void getHelp(AccessibleEvent e) {
        e.result = getToolTipText();
      }
      
    });
    
    accessible.addAccessibleControlListener(new AccessibleControlAdapter() {
      public void getChildAtPoint(AccessibleControlEvent e) {
        e.childID = ACC.CHILDID_SELF;
      }
      
      public void getLocation(AccessibleControlEvent e) {
        Rectangle rect = getDisplay().map(getParent(), null, getBounds());
        e.x = rect.x;
        e.y = rect.y;
        e.width = rect.width;
        e.height = rect.height;
      }
      
      public void getChildCount(AccessibleControlEvent e) {
        e.detail = 0;
      }
      
      public void getRole(AccessibleControlEvent e) {
        e.detail = ACC.ROLE_LABEL;
      }
      
      public void getState(AccessibleControlEvent e) {
        e.detail = ACC.STATE_READONLY;
      }
      
      public void getValue(AccessibleControlEvent e) {
        e.result = getText();
      }
    });
  }
  
  @SuppressWarnings("unused")
  void onDispose(DisposeEvent event) {
    gradientColors = null;
    gradientPercents = null;    
    text = null;    
  }
  
  
  void onPaint(PaintEvent event) {
    Rectangle rect = getClientArea();
    if (rect.width == 0 || rect.height == 0) return;
    GC gc = event.gc;
    
    boolean shortenText = false;
    String t = text;   
    int availableWidth = Math.max(0, rect.width - 2*hIndent);
    Point extent = getTotalSize(t);
    if (extent.x > availableWidth) {
      extent = getTotalSize(t);
      if (extent.x > availableWidth) {
        shortenText = true;
      }
    }
    
    String[] lines = text == null ? null : splitString(text); 
    
    if (shortenText) {
      extent.x = 0;
      for(int i = 0; i < lines.length; i++) {
        Point e = gc.textExtent(lines[i], DRAW_FLAGS);
        if (e.x > availableWidth) {
          lines[i] = shortenText(gc, lines[i], availableWidth);
          extent.x = Math.max(extent.x, getTotalSize(lines[i]).x);
        } else {
          extent.x = Math.max(extent.x, e.x);
        }
      }      
    } 
    
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
      gc.fillGradientRectangle(3, pos+3, rect.width-10, gradientHeight-6, true);
      pos += gradientHeight;      
    }
    
    gc.setForeground(new Color(getDisplay(), 245, 245, 245));   
    
    gc.drawRoundRectangle(0, 0, rect.width-6, rect.height-2 , 10, 10);
    gc.drawRoundRectangle(1, 1, rect.width-8, rect.height -4 , 10, 10);
    
    if (pos < rect.height) {
      gc.setBackground(getBackground());
      gc.fillRoundRectangle(0, pos, rect.width, rect.height - pos, 3, 4);
    }      
    
    gc.setBackground(oldBackground);
    
    // draw the text
    if (lines == null)  return;
    int lineHeight = gc.getFontMetrics().getHeight();
    int textHeight = lines.length * lineHeight;
    int lineY = Math.max(vIndent, rect.y + (rect.height - textHeight) / 2);
    gc.setForeground(getForeground());
    for (int i = 0; i < lines.length; i++) {
      int lineX = x;
      if (lines.length > 1) {
        if (align == SWT.CENTER) {
          int lineWidth = gc.textExtent(lines[i], DRAW_FLAGS).x;
          lineX = x + Math.max(0, (extent.x - lineWidth) / 2);
        }
        if (align == SWT.RIGHT) {
          int lineWidth = gc.textExtent(lines[i], DRAW_FLAGS).x;
          lineX = Math.max(x, rect.x + rect.width - hIndent - lineWidth);
        }
      }
      gc.drawText(lines[i], lineX + rect.width / 15, lineY, DRAW_FLAGS);
      lineY += lineHeight;
    }
    
    if(getMenu() == null) return;
    int px = rect.width - 25;
    int py = 9;
    int [] polyline1, polyline2;
    polyline1 = new int[] {px,py-1,
        px,py, px+1,py, px+1,py+1, px+2,py+1, px+2,py+2, px+3,py+2, px+3,py+3,
        px+3,py+2, px+4,py+2, px+4,py+1,  px+5,py+1, px+5,py, px+6,py, px+6,py-1};
    
    py += 4;
    polyline2 = new int [] {
        px,py-1, px,py, px+1,py, px+1,py+1, px+2,py+1, px+2,py+2, px+3,py+2, px+3,py+3,
        px+3,py+2, px+4,py+2, px+4,py+1,  px+5,py+1, px+5,py, px+6,py, px+6,py-1};
    gc.setForeground(new Color(getDisplay(), 0, 64, 64));
    gc.drawPolyline(polyline1);
    gc.drawPolyline(polyline2);
    
    if(areaMenu){
      gc.setForeground(lastColor);
      gc.drawRoundRectangle(rect.width - 35, 4, 25, rect.height - 9, 6, 6);
    }
  }  
  
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
    for (int i = 0; i < percents.length; ++i)    gradientPercents[i] = percents[i];
      
    redraw();
  }
  
  public void setFont(Font font) {
    super.setFont(font);
    redraw();
  }  
  
  public void setText(String text) {
    checkWidget();
    this.text = text;
  }
  
  protected String shortenText(GC gc, String t, int width) {
    if (t == null) return null;
    int w = gc.textExtent(ELLIPSIS, DRAW_FLAGS).x;
    int l = t.length();
    int pivot = l/2;
    int s = pivot;
    int e = pivot+1;
    while (s >= 0 && e < l) {
      String s1 = t.substring(0, s);
      String s2 = t.substring(e, l);
      int l1 = gc.textExtent(s1, DRAW_FLAGS).x;
      int l2 = gc.textExtent(s2, DRAW_FLAGS).x;
      if (l1+w+l2 < width) {
        t = s1 + ELLIPSIS + s2;
        break;
      }
      s--;
      e++;
    }
    return t;
  }
  
  private String[] splitString(String txt) {
    String[] lines = new String[1];
    int start = 0, pos;
    do {
      pos = txt.indexOf('\n', start);
      if (pos == -1) {
        lines[lines.length - 1] = txt.substring(start);
      } else {
        boolean crlf = (pos > 0) && (txt.charAt(pos - 1) == '\r');
        lines[lines.length - 1] = txt.substring(start, pos - (crlf ? 1 : 0));
        start = pos + 1;
        String[] newLines = new String[lines.length+1];
        System.arraycopy(lines, 0, newLines, 0, lines.length);
        lines = newLines;
      }
    } while (pos != -1);
    return lines;
  }  
}