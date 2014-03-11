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

public class ButtonSelectGroup {

	private List<AbstractRibbonGroupItem> mItems;
	
	public ButtonSelectGroup() {
		mItems = new ArrayList<AbstractRibbonGroupItem>();
	}
	
	public void add(AbstractRibbonGroupItem item) {
		if (!mItems.contains(item))
			mItems.add(item);
	}
	
	public void remove(AbstractRibbonGroupItem item) {
		mItems.remove(item);
	}
	
	public boolean isSelected() {
		return (getSelection() != null);
	}
	
	public boolean isHover() {
		return (getHover() != null);
	}
	
	public AbstractRibbonGroupItem getSelection() {
		for (AbstractRibbonGroupItem item : mItems) {
			if (item instanceof RibbonButtonGroup) {
				RibbonButtonGroup rbg = (RibbonButtonGroup) item;
				for (AbstractRibbonGroupItem button : rbg.getButtons()) {
					if (button.isSelected())
						return button;
				}
			}
			else if (item instanceof RibbonButton) {
    			if (item.isSelected())
    				return item;
			}
		}
		
		return null;
	}
	
	public AbstractRibbonGroupItem getHover() {
		for (AbstractRibbonGroupItem item : mItems) {
			if (item instanceof RibbonButtonGroup) {
				RibbonButtonGroup rbg = (RibbonButtonGroup) item;
				for (AbstractRibbonGroupItem button : rbg.getButtons()) {
					if (button.isHoverButton())
						return button;
				}
			}
			else if (item instanceof RibbonButton) {
    			if (item.isHoverButton())
    				return item;
			}
		}
		
		return null;
	}

	public boolean deSelect() {
		AbstractRibbonGroupItem selection = getSelection();
		if (selection == null)
			return false;
		
		
		
		selection.setSelected(false);
		return true;
		
/*		if (selection instanceof RibbonButton) {
			((RibbonButton)selection).getParent().redraw();
		}
		else if (selection instanceof RibbonButtonGroup) {
			RibbonButtonGroup rbg = (RibbonButtonGroup) selection;
			List<RibbonButton> buttons = rbg.getButtons();
			for (RibbonButton button : buttons) {
				button.setSelected(false);
			}
			
			rbg.getParent().redraw();
		}
*/		
//		return false;
	}
	
	public void deHover() {
		
	}
}
