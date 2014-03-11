/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.search.ads;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.forms.widgets.Section;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 8, 2010  
 */
public class AdvPreview {
  
  private Image image = null;
  private Point origin = new Point (0, 0);
  private Canvas canvas;
  private Section section;
  
  
  public AdvPreview(Composite parent) {
   section = new Section(parent, Section.TWISTIE  | Section.EXPANDED);

    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    section.setLayoutData(gridData);
    gridData.horizontalSpan = 3;
    
    canvas = new Canvas (section, SWT.NO_BACKGROUND |
        SWT.NO_REDRAW_RESIZE | SWT.V_SCROLL | SWT.H_SCROLL);
    final ScrollBar hBar = canvas.getHorizontalBar ();
    hBar.addListener (SWT.Selection, new Listener () {
      @SuppressWarnings("unused")
      public void handleEvent (Event e) {
        if(image == null) return;
        int hSelection = hBar.getSelection ();
        int destX = -hSelection - origin.x;
        Rectangle rect = image.getBounds ();
        canvas.scroll (destX, 0, 0, 0, rect.width, rect.height, false);
        origin.x = -hSelection;
      }
    });
    
    final ScrollBar vBar = canvas.getVerticalBar ();
    vBar.addListener (SWT.Selection, new Listener () {
      @SuppressWarnings("unused")
      public void handleEvent (Event e) {
        if(image == null) return;
        int vSelection = vBar.getSelection ();
        int destY = -vSelection - origin.y;
        Rectangle rect = image.getBounds ();
        canvas.scroll (0, destY, 0, 0, rect.width, rect.height, false);
        origin.y = -vSelection;
      }
    });
    canvas.addListener (SWT.Resize,  new Listener () {
      @SuppressWarnings("unused")
      public void handleEvent (Event e) {
        if(image == null) return;
        Rectangle rect = image.getBounds ();
        Rectangle client = canvas.getClientArea ();
        hBar.setMaximum (rect.width);
        vBar.setMaximum (rect.height);
        hBar.setThumb (Math.min (rect.width, client.width));
        vBar.setThumb (Math.min (rect.height, client.height));
        int hPage = rect.width - client.width;
        int vPage = rect.height - client.height;
        int hSelection = hBar.getSelection ();
        int vSelection = vBar.getSelection ();
        if (hSelection >= hPage) {
          if (hPage <= 0) hSelection = 0;
          origin.x = -hSelection;
        }
        if (vSelection >= vPage) {
          if (vPage <= 0) vSelection = 0;
          origin.y = -vSelection;
        }
        canvas.redraw ();
      }
    });
    
    canvas.addListener (SWT.Paint, new Listener () {
      public void handleEvent (Event e) {
        GC gc = e.gc;
        if(image == null) return;
        gc.drawImage (image, origin.x, origin.y);
        Rectangle rect = image.getBounds ();
        Rectangle client = canvas.getClientArea ();
        int marginWidth = client.width - rect.width;
        if (marginWidth > 0) {
          gc.fillRectangle (rect.width, 0, marginWidth, client.height);
        }
        int marginHeight = client.height - rect.height;
        if (marginHeight > 0) {
          gc.fillRectangle (0, rect.height, client.width, marginHeight);
        }
      }
    });

    section.setClient(canvas);
    section.setExpanded(false);
  }
  
  public void setImage(Image _image) {
    this.image = _image;
    if(this.image == null) return;
    canvas.redraw ();
    section.setExpanded(true);
  }
  
}
