/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.vietspider.ui.widget.vtab.outlookbar;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ScrollBar;

class ScrolledCompositeLayout extends Layout {

//  boolean inLayout = false;
  static final int DEFAULT_WIDTH	= 100;
  static final int DEFAULT_HEIGHT	= 64;

  protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
    ScrolledComposite sc = (ScrolledComposite)composite;
    if (sc.content == null) {
      int w = (wHint != SWT.DEFAULT) ? wHint : DEFAULT_WIDTH;
      int h = (hHint != SWT.DEFAULT) ? hHint : DEFAULT_HEIGHT;
      return new Point(w, h);
    }
    return sc.content.computeSize (wHint, hHint, flushCache);
  }

  @SuppressWarnings("unused")
  protected boolean flushCache(Control control) { return true; }

  @SuppressWarnings("unused")
  protected void layout(Composite composite, boolean flushCache) {
//    if (inLayout) return;
    ScrolledComposite sc = ((ScrolledComposite)composite);
    
    if (sc.content == null) return;
    Rectangle contentRect = sc.content.getBounds();
    
    ScrollBar hBar = sc.getHorizontalBar();
    ScrollBar vBar = sc.getVerticalBar();    
    
    if (hBar != null && hBar.getSize().y >= sc.getSize().y)  return;
    if (vBar != null && vBar.getSize().x >= sc.getSize().x) return;
    
//    inLayout = true;    
    
    Rectangle hostRect = sc.getClientArea();
    if (sc.expandHorizontal) {
      contentRect.width = Math.max(sc.minWidth, hostRect.width) ;	
    }
    if (sc.expandVertical) {
      contentRect.height = Math.max(sc.minHeight, hostRect.height);
    }   
    
    contentRect = new Rectangle(5, 0, contentRect.width - 10 , contentRect.height);

    if (hBar != null) {
      hBar.setMaximum (contentRect.width);
      int ht = Math.min (contentRect.width, hostRect.width);
      hBar.setThumb(ht);
      int hPage = contentRect.width - hostRect.width;
      if(ht == contentRect.height) hBar.setVisible(false);
      else{
        hBar.setVisible(true);
        int hSelection = hBar.getSelection ();
        if (hSelection >= hPage) {
          if (hPage <= 0) {
            hSelection = 0;
            hBar.setSelection(0);
          }
          contentRect.x = -hSelection;
        }
      }
    }        

    List<OutlookBarItem> items = ((OutlookBarPane)composite.getParent()).getItemList();
    int increate  = items.size() > 1 ? items.size()*110 : items.size()*400;
    if (vBar != null) {
      vBar.setMaximum (contentRect.height);
      int ht = Math.min(contentRect.height , hostRect.height + increate);
      if(ht == contentRect.height) {
        vBar.setVisible(false);      
      } else{
        vBar.setVisible(true);
        vBar.setThumb(ht);
        int vPage = contentRect.height - hostRect.height;
        int vSelection = vBar.getSelection ();
        if (vSelection >= vPage) {
          if (vPage <= 0) {
            vSelection = 0;
            vBar.setSelection(0);
          }
          contentRect.y = -vSelection;
        }
      }
    }
    
    sc.content.setBounds(contentRect); 
    sc.setVisible(true);
    sc.content.setVisible(true);
//    System.out.println(" width "+contentRect.width  + " height "+contentRect.height);
//    sc.content.setBounds(contentRect);
//    inLayout = false;
  }
}
