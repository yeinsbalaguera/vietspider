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

public class RibbonGroupSeparator extends AbstractRibbonGroupItem {
	
	private RibbonGroup mParent;
	
	public RibbonGroupSeparator(RibbonGroup parent) {
		super(parent, null, null, SWT.NONE);
		mParent = parent;
		parent.toolItemAdded(this);
	}	

	public void dispose() {
		mParent.toolItemDisposed(this);
	}
}
