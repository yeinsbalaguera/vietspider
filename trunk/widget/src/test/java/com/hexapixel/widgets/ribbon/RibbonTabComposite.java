/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/ 

package com.hexapixel.widgets.ribbon;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.vietspider.generic.ColorCache;


class RibbonTabComposite extends Composite {
	
	public static final int GROUP_HEIGHT = 85;
	
	public static final int TAB_HEIGHT = 18; // down to the top border but not including top border
	
	private Color mBorderBottom = ColorCache.getInstance().getColor(192, 249, 255);
	
	private Color mBorderBottomFadeoff1 = ColorCache.getInstance().getColor(150, 170, 196);
	private Color mBorderBottomFadeoff2 = ColorCache.getInstance().getColor(166, 188, 217);
	private Color mBorderBottomFadeoff3 = ColorCache.getInstance().getColor(177, 200, 231);
	
	private Color mTopTop = ColorCache.getInstance().getColor(219, 230, 244);
	private Color mTopBottom = ColorCache.getInstance().getColor(207, 221, 239);
	private Color mMidTop = ColorCache.getInstance().getColor(201, 217, 237);
	private Color mMidBottom = ColorCache.getInstance().getColor(216, 232, 245);
	private Color mBottomTop = ColorCache.getInstance().getColor(217, 232, 246);
	private Color mBottomBottom = ColorCache.getInstance().getColor(231, 242, 255);
	
	private AbstractRibbonGroupItem mHoverItem;
	private AbstractRibbonGroupItem mSelectedItem;
	private RibbonGroup mSelectedGroup;
	
	private List<RibbonGroup> mGroups;
	private Rectangle mBounds;
	private RibbonTabFolder mParent;

	public RibbonTabComposite(RibbonTabFolder parent, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		
		mParent = parent;
		mBounds = new Rectangle(0, 0, 0, 0);
		mGroups = new ArrayList<RibbonGroup>();
		
		setLayout(new ToolbarGroupLayout());
						
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				drawOntoGC(gc, getBounds());
			}			
		});
		
/*		addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				mParent.mouseDown(e);
			}

			public void mouseUp(MouseEvent e) {
				mParent.mouseUp(e);
			}
			
		});
*/		
		layout(true);
	}

	// group last affected by a selection
	public RibbonGroup getLastSelectedGroup() {
		return mSelectedGroup;
	}
	
	public AbstractRibbonGroupItem getLastSelectedItem() {
		return mSelectedItem;
	}
	
	public AbstractRibbonGroupItem getLastHoveredItem() {
		return mHoverItem;
	}
	
	void groupAdded(RibbonGroup group) {
		if (!mGroups.contains(group))
			mGroups.add(group);
	}
	
	// this method takes the bounds from outside as the method is also called in RibbonGroup to remove corner artifacts and more
	protected void drawOntoGC(GC gc, Rectangle bounds) {
		int x = bounds.x;
		int y = 0;
		int width = bounds.width;
		
		gc.setForeground(mTopTop);
		gc.setBackground(mTopBottom);
		gc.fillGradientRectangle(x, y, width, 16, true);
		
		gc.setForeground(mMidTop);
		gc.setBackground(mMidBottom);
		gc.fillGradientRectangle(x, y+16, width, 54, true);
		
		gc.setForeground(mBottomTop);
		gc.setBackground(mBottomBottom);
		gc.fillGradientRectangle(x, y+16+54, width, 18, true);
		
		gc.setForeground(mBorderBottom);
		gc.drawLine(x, y+17+54+17, x+width, y+17+54+17);				
		gc.setForeground(mBorderBottomFadeoff1);
		gc.drawLine(x, y+17+54+17+1, x+width, y+17+54+17+1);				
		gc.setForeground(mBorderBottomFadeoff2);
		gc.drawLine(x, y+17+54+17+2, x+width, y+17+54+17+2);				
		gc.setForeground(mBorderBottomFadeoff3);
		gc.drawLine(x, y+17+54+17+3, x+width, y+17+54+17+3);				
	}
	
	protected void toolItemHoverChanged(AbstractRibbonGroupItem item, RibbonGroup toolGroup) {
		mHoverItem = item;
		
		for (int i = 0; i < mGroups.size(); i++) {
			RibbonGroup group = mGroups.get(i);
			if (group != toolGroup)
				group.deHover();
		}
	}
	
	protected void toolItemSelectionChanged(AbstractRibbonGroupItem item, RibbonGroup toolGroup) {
		mSelectedItem = item;
	}
	
	class ToolbarGroupLayout extends Layout {
			
	    protected Point computeSize(Composite aComposite, int wHint, int hHint, boolean flushCache) {
            return getSize();
        }

        protected void layout(final Composite aComposite, boolean flushCache) {
            int top = 1;
            int left = 0;
            int spacer = 2;
            
            Control [] items = aComposite.getChildren();

            for (int i = 0; i < items.length; i++) {
                Control item = items[i];
                if (item instanceof RibbonGroup) {
                	RibbonGroup fg = (RibbonGroup) item;
                	if (!fg.isVisible())
                		continue;
                	
                    Rectangle groupBounds = fg.getBounds();
                    
                    item.setBounds(left, top, groupBounds.width, groupBounds.height);
                    left += groupBounds.width + spacer;                	
                }
            }
            
            mBounds = new Rectangle(1, 1, left, 86);
        }
	}
	
	public Rectangle getBounds() {
		checkWidget();
		return mParent.getClientArea();
	}
	
	public Rectangle getActualBounds() {
		return mBounds;
	}

	protected void scrollWheelUpdate(Point mouse) {
		Control [] children = getChildren();
		if (children != null) {
			for (Control child : children) {
				if (child instanceof RibbonGroup) {
					RibbonGroup rg = (RibbonGroup) child;
					
					if (isInside(mouse.x, mouse.y, rg.getBounds())) {
						Event fakeMove = new Event();
						fakeMove.x = mouse.x;
						fakeMove.y = mouse.y;
						fakeMove.widget = rg;
						rg.notifyListeners(SWT.MouseMove, fakeMove);
					}

					rg.redraw();
				}
			}
		}
	}

	private boolean isInside(int x, int y, Rectangle rect) {
		if (rect == null) {
			return false;
		}

		return x >= rect.x && y >= rect.y && x <= (rect.x + rect.width-1) && y <= (rect.y + rect.height-1);
	}
	
	@Override
	public Point getSize() {
		checkWidget();
		return new Point(mBounds.width, mBounds.height);
	}
	
	@Override
	public void dispose() {
		for (int i = 0; i < mGroups.size(); i++) 
			mGroups.get(i).dispose();
		
		mGroups.clear();

		super.dispose();
	}
	
}
