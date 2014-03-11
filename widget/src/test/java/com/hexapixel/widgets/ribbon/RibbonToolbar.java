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

public class RibbonToolbar {

	private RibbonGroup mParentGroup;
	
	private List<RibbonToolbarGrouping> items;
	
	public static final int STYLE_BORDERED = 0;
	public static final int STYLE_NO_BORDER = 1;
	
	private int mStyle = STYLE_BORDERED;
	private int mRows;
	
	public RibbonToolbar(RibbonGroup parent, int style, int rows) {
		items = new ArrayList<RibbonToolbarGrouping>();
		parent.addToolbar(this);
		mStyle = style;
		mParentGroup = parent;
		mRows = rows;
	}
	
	RibbonToolbar(RibbonShell shell) {
		items = new ArrayList<RibbonToolbarGrouping>();
		mRows = 1;
	}
	
	public int getRows() {
		return mRows;
	}
	
	public void setRows(int rows) {
		if (rows > 3)
			rows = 3;
		if (rows < 1)
			rows = 1;
		
		mRows = rows;
	}
	
	public int getStyle() {
		return mStyle;
	}
	
	public void setStyle(int style) {
		mStyle = style;
	}
	
	public RibbonGroup getRibbonGroup() {
		return mParentGroup;
	}

	public void addButtonGrouping(RibbonToolbarGrouping group) {
		if (!items.contains(group))
			items.add(group);
	}
	
	public List<RibbonToolbarGrouping> getGroupings() {
		return items;
	}
	
	public void dispose() {
		mParentGroup.removeToolbar(this);
	}
		
}
