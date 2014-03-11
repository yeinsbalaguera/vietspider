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

import org.eclipse.swt.graphics.Rectangle;

public class RibbonToolbarGrouping {

	private RibbonGroup mRibbonGroup;
	
	private List<RibbonButton> mItems;
	private int mRow;
		
	private Rectangle mBounds;	
	
	public RibbonToolbarGrouping(RibbonToolbar toolbar, int row) {
		mRibbonGroup = toolbar.getRibbonGroup();
		mItems = new ArrayList<RibbonButton>();
		mRow = row;
		toolbar.addButtonGrouping(this);
	}
	
	public RibbonToolbarGrouping(QuickAccessShellToolbar toolbar) {
		mItems = new ArrayList<RibbonButton>();
		toolbar.addButtonGrouping(this);		
	}
		
	public RibbonGroup getRibbonGroup() {
		return mRibbonGroup;
	}
	
	protected void toolItemAdded(RibbonButton rb) {
		if (!mItems.contains(rb))
			mItems.add(rb);
		
		mRibbonGroup.updateToolbarBounds();
	}
	
	public void removeButton(RibbonButton rb) {
		mItems.remove(rb);
	}
	
	public List<RibbonButton> getItems() {
		return mItems;
	}
	
	public int getRow() {
		return mRow;
	}
	
	public void setRow(int row) {
		mRow = row;
	}
	
	public Rectangle getBounds() {
		return mBounds;
	}
	
	public void setBounds(Rectangle bounds) {
		mBounds = bounds;
	}
}
