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

public class RibbonButtonGroup extends AbstractRibbonGroupItem {

	private List<AbstractRibbonGroupItem> mButtons;
	private RibbonGroup mParent;
	
	public RibbonButtonGroup(RibbonGroup parent) {
		super(parent, null, null, SWT.NONE);
		mParent = parent;
		setImageVerticalAlignment(SWT.TOP);
		init();
	}
	
	private void init() {
		mButtons = new ArrayList<AbstractRibbonGroupItem>();
		getParent().toolItemAdded(this);		
	}
	
	public void addButton(AbstractRibbonGroupItem rb) {
		if (!mButtons.contains(rb)) {
			mButtons.add(rb);
			getParent().updateBounds();
		}
	}
	
	public void removeButton(RibbonButton rb) {
		mButtons.remove(rb);
	}
	
	public List<AbstractRibbonGroupItem> getButtons() {
		return mButtons;
	}
	
	public void dispose(AbstractRibbonGroupItem item) {
		mButtons.remove(item);
	}
	
	public void dispose() {
		mButtons.clear(); 
	}
}
