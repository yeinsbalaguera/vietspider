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
import org.eclipse.swt.graphics.Image;


public class RibbonButton extends AbstractRibbonGroupItem {

	private Object mParent;
	
	public RibbonButton(RibbonButtonGroup parent, Image image, String labelText, int style) {
		super(parent.getParent(), labelText, image, style);
		mParent = parent;
		setImageVerticalAlignment(SWT.CENTER);
		parent.addButton(this);
	}

	public RibbonButton(RibbonButtonGroup parent, Image image, Image disabledImage, String labelText, int style) {
		super(parent.getParent(), labelText, image, style);
		mParent = parent;
		setImageVerticalAlignment(SWT.CENTER);
		setDisabledImage(disabledImage);
		parent.addButton(this);
	}

	public RibbonButton(RibbonGroup parent, Image image, String labelText, int style) {				
		super(parent, labelText, image, style);
		mParent = parent;
		parent.toolItemAdded(this);
	}

	public RibbonButton(RibbonGroup parent, Image image, Image disabledImage, String labelText, int style) {				
		super(parent, labelText, image, style);
		mParent = parent;
		setDisabledImage(disabledImage);
		parent.toolItemAdded(this);
	}

	public RibbonButton(RibbonGroup parent, Image image, String labelText, int imgAlignment, int style) {				
		super(parent, labelText, image, style);
		mParent = parent;
		setImageVerticalAlignment(imgAlignment);
		parent.toolItemAdded(this);
	}

	public RibbonButton(RibbonGroup parent, Image image, Image disabledImage, String labelText, int imgAlignment, int style) {				
		super(parent, labelText, image, style);
		mParent = parent;
		setDisabledImage(disabledImage);
		setImageVerticalAlignment(imgAlignment);
		parent.toolItemAdded(this);
	}

	public RibbonButton(RibbonToolbarGrouping parent, Image image, Image disabledImage, int buttonStyle) {
		super(parent.getRibbonGroup(), null, image, disabledImage, buttonStyle);
		mParent = parent;
		setImageVerticalAlignment(SWT.CENTER);
		setToolbarButton(true);
		parent.toolItemAdded(this);
	}

	public RibbonButton(QuickAccessShellToolbar parent, Image image, Image disabledImage, int buttonStyle) {
		super(null, null, image, disabledImage, buttonStyle);
		createMenu(parent.getRibbonShell());
		mParent = parent;
		setImageVerticalAlignment(SWT.CENTER);
		setToolbarButton(true);
		parent.addButton(this);
	}
	
	public RibbonButton(RibbonTabFolder parent, Image image, Image disabledImage, int buttonStyle) {
		super(null, null, image, disabledImage, buttonStyle);
		createMenu(parent);
		mParent = parent;
		setImageVerticalAlignment(SWT.CENTER);
		setToolbarButton(true);
	}
	
	public void dispose() {
		if (mParent == null)
			return;
		
		if (mParent instanceof RibbonToolbarGrouping) {
			((RibbonToolbarGrouping)mParent).removeButton(this);
		}
		else if (mParent instanceof RibbonGroup) {
			((RibbonGroup)mParent).removeButton(this);
		}
		else if (mParent instanceof RibbonButtonGroup) {
			((RibbonButtonGroup)mParent).removeButton(this);
		}
		
/*		// dispose images
		ImageCache.dispose(getImage());
		ImageCache.dispose(getDisabledImage());
		
		if (getImage() != null) getImage().dispose();
		if (getDisabledImage() != null) getDisabledImage().dispose();
*/	}
}
