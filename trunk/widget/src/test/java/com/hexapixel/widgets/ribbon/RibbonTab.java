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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class RibbonTab {

	private String mName;
	private Rectangle mBounds;
	private boolean mHover;
	private boolean mSelected;
	private int mIndex;
	
	private RibbonTabComposite mFancyToolbar;
	private RibbonTabFolder mFancyTabFolder;
	
	public RibbonTab(RibbonTabFolder ftf, String name) {
		this.mName = name;
		this.mFancyTabFolder = ftf;
		this.mFancyToolbar = new RibbonTabComposite(ftf, SWT.NONE);
		
		ftf.tabAdded(this);
		ftf.tabControlSet(this);
	}
	
	public boolean isEmpty() {
		Control [] children = mFancyToolbar.getChildren();		
		return (children == null || children.length == 0);	
	}
	
	public void dispose() {
		mFancyToolbar.dispose();
	}

	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		this.mName = name;
	}
	
	public void setBounds(Rectangle bounds) {
		this.mBounds = bounds;
	}
	
	public Rectangle getBounds() {
		return mBounds;
	}
	
	public boolean isSelected() {
		return mSelected;
	}
	
	public boolean isHover() {
		return mHover;
	}
	
	public void setHover(boolean hover) {
		this.mHover = hover;
	}
	
	public void setSelected(boolean selected) {
		this.mSelected = selected;
		mFancyTabFolder.redraw();
	}
		
	RibbonTabComposite getFancyToolbar() {
		return mFancyToolbar;
	}
	
	public RibbonTabFolder getTabFolder() {
		return mFancyTabFolder;
	}
	
	/**
	 * This is used when the user scrollwheels to change tabs, we need to update hover and such for where the mouse is
	 * when the new tab is selected, this forces an update
	 */
	public void scrollWheelUpdate() {
		Control mouseFocus = Display.getDefault().getCursorControl();
		if (mouseFocus instanceof RibbonGroup) {
			RibbonGroup rg = (RibbonGroup) mouseFocus;
			Point cursor = rg.toControl(Display.getDefault().getCursorLocation());
			RibbonTabComposite rtg = (RibbonTabComposite) rg.getParent();
			rtg.scrollWheelUpdate(cursor);
		}
	}
	
	void setIndex(int index) {
		mIndex = index;
	}
	
	public int getIndex() {
		return mIndex;
	}
}
