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

import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.generic.ColorCache;
import org.vietspider.generic.ImageCache;


public abstract class AbstractShellPainter {

	public static Color outerBorderNonMaximized = ColorCache.getInstance().getColor(59, 90, 130);
	public static Color outerBorderNonMaximized_Inactive = ColorCache.getInstance().getColor(151, 165, 183);

	// top menu bar background
	private Color veryTopBorderTopTop_1 = ColorCache.getInstance().getColor(227, 235, 246);
	private Color veryTopBorderTopBottom_1 = ColorCache.getInstance().getColor(218, 233, 233);	
	private Color veryTopBorderTopTop_2 = ColorCache.getInstance().getColor(213, 229, 250);
	private Color veryTopBorderTopBottom_2 = ColorCache.getInstance().getColor(217, 231, 249);
	
	private Color veryTopBorderBottomTop = ColorCache.getInstance().getColor(202, 222, 247);
	private Color veryTopBorderBottomBottom = ColorCache.getInstance().getColor(228, 239, 253);	
	private Color veryTopBorderBottomLine = ColorCache.getInstance().getColor(219, 244, 254);
	
	// top menu bar background - inactive
	private Color veryTopBorderTopTop_1_Inactive = ColorCache.getInstance().getColor(227, 231, 236);
	private Color veryTopBorderTopBottom_1_Inactive = ColorCache.getInstance().getColor(223, 230, 239);	
	private Color veryTopBorderTopTop_2_Inactive = ColorCache.getInstance().getColor(221, 228, 237);
	private Color veryTopBorderTopBottom_2_Inactive = ColorCache.getInstance().getColor(222, 229, 237);

	private Color veryTopBorderBottomTop_Inactive = ColorCache.getInstance().getColor(216, 225, 236);
	private Color veryTopBorderBottomBottom_Inactive = ColorCache.getInstance().getColor(227, 232, 239);	
	private Color veryTopBorderBottomLine_Inactive = ColorCache.getInstance().getColor(223, 235, 239);
	
	// text colors
	private Color shellTextColorFadedRight = ColorCache.getInstance().getColor(162, 211, 249);
	private Color shellTextColorFadedLeft = ColorCache.getInstance().getColor(215, 230, 211);
	private Color shellTextColor = ColorCache.getInstance().getColor(105, 112, 121);

	private Color shellTextColorFadedRight_Inactive = ColorCache.getInstance().getColor(192, 217, 237);
	private Color shellTextColorFadedLeft_Inactive = ColorCache.getInstance().getColor(221, 228, 213);
	private Color shellTextColor_Inactive = ColorCache.getInstance().getColor(160, 160, 160);
	
	// toolbar 
	private Color mToolbarOuterBorder = ColorCache.getInstance().getColor(246, 249, 252);
	private Color mToolbarBorder = ColorCache.getInstance().getColor(186, 204, 226);
	private Color mToolbarInnerBorder1 = ColorCache.getInstance().getColor(222, 231, 244);
	private Color mToolbarInnerBorder2 = ColorCache.getInstance().getColor(230, 238, 249);
	private Color mToolbarFillTopTop = ColorCache.getInstance().getColor(219, 231, 247);
	private Color mToolbarFillTopBottom = ColorCache.getInstance().getColor(199, 215, 237);
	private Color mToolbarFillBottomTop = mToolbarFillTopBottom;
	private Color mToolbarFillBottomBottom = ColorCache.getInstance().getColor(201, 217, 238);
	private Color mToolbarBottomBorder = ColorCache.getInstance().getColor(154, 179, 213);
	
	private int topHeight = 5;
	private int bottomHeight = 19;
	
	private RibbonShell mRibbonShell;
	private Shell mShell;	
	private Rectangle mBounds;
	private boolean mShellMaximized;
	private boolean mShellInactive;
	private RibbonTabFolder mTabFolder;
	private Rectangle mToolbarBounds;
	
	public void paintShell(RibbonShell shell, GC gc) {
		mRibbonShell = shell;
		mShell = shell.getShell();
		mTabFolder = shell.getRibbonTabFolder();
		mShellMaximized = mRibbonShell.getMaximized();
		mShellInactive = (Display.getDefault().getActiveShell() != mShell);
		mBounds = mShell.getBounds();

		// menu bar
		drawShellMenuBar(gc);
		
		// shell text
		drawShellText(gc);		
		
		// draw min, max, close, depending on shell settings as well
		drawMenuBarButtons(gc);
		
		// draw the "office" button
		//drawBigButton(gc);
	}
	
	private void drawShellMenuBar(GC gc) {
		// top border is bigger when non-maximized
		int y = 0;

		if (mShellMaximized) {
			if (mShellInactive) {
				gc.setForeground(veryTopBorderTopTop_1_Inactive);
				gc.setBackground(veryTopBorderTopBottom_1_Inactive);	
			}
			else {
				gc.setForeground(veryTopBorderTopTop_1);
				gc.setBackground(veryTopBorderTopBottom_1);
			}
			gc.fillGradientRectangle(0, y, mBounds.width, topHeight, true);
			
			y += topHeight;
		}
		else {
			if (mShellInactive) {
				gc.setForeground(veryTopBorderTopTop_1_Inactive);
				gc.setBackground(veryTopBorderTopBottom_1_Inactive);
			}
			else {
				gc.setForeground(veryTopBorderTopTop_1);
				gc.setBackground(veryTopBorderTopBottom_1);				
			}
			gc.fillGradientRectangle(0, y, mBounds.width, topHeight, true);
			
			if (mShellInactive)
				gc.setForeground(outerBorderNonMaximized_Inactive);
			else
				gc.setForeground(outerBorderNonMaximized);
			gc.drawLine(0, 0, mBounds.width, 0);
			
			y += topHeight;
			if (mShellInactive) {
				gc.setForeground(veryTopBorderTopTop_2_Inactive);
				gc.setBackground(veryTopBorderTopBottom_2_Inactive);
			}
			else {
				gc.setForeground(veryTopBorderTopTop_2);
				gc.setBackground(veryTopBorderTopBottom_2);				
			}
			gc.fillGradientRectangle(0, y, mBounds.width, topHeight-1, true);			
			
			y += topHeight-1;
		}

		if (mShellInactive) {
			gc.setForeground(veryTopBorderBottomTop_Inactive);
			gc.setBackground(veryTopBorderBottomBottom_Inactive);
		}
		else {
			gc.setForeground(veryTopBorderBottomTop);
			gc.setBackground(veryTopBorderBottomBottom);			
		}
		gc.fillGradientRectangle(0, y, mBounds.width, bottomHeight, true); 
		
		y += bottomHeight;
		if (mShellInactive)
			gc.setForeground(veryTopBorderBottomLine_Inactive);
		else
			gc.setForeground(veryTopBorderBottomLine);
		gc.drawLine(0, y, mBounds.width, y);
	}
	
	private void drawShellText(GC gc) {
		int y = mShellMaximized ? 7 : 9;
		
		String toDraw = mShell.getText();
		if (toDraw == null || toDraw.length() == 0)
			return;
		
		Font font = gc.getFont();
		Font toUse = new Font(gc.getDevice(), font.getFontData()[0].getName(), 9, font.getFontData()[0].getStyle());
		gc.setFont(toUse);
		
		Point extent = gc.stringExtent(toDraw);
		int midPoint = (mBounds.width/2) - (extent.x/2);
			
		
		// don't let menu bar text draw on the toolbar area, with a little spacing
		if (mToolbarBounds != null) {
			//System.err.println((midPoint-1+5) + " < " + mToolbarBounds.x+)
			if ((midPoint-1+5) < (mToolbarBounds.x+mToolbarBounds.width)) {
				midPoint = mToolbarBounds.x + mToolbarBounds.width + 5;
			}
		}		
		
		if (mShellInactive)
			gc.setForeground(shellTextColorFadedRight_Inactive);
		else
			gc.setForeground(shellTextColorFadedRight);
		gc.drawString(toDraw, midPoint+1, y, true);
		
		if (mShellInactive)
			gc.setForeground(shellTextColorFadedLeft_Inactive);
		else
			gc.setForeground(shellTextColorFadedLeft);		
		gc.drawString(toDraw, midPoint-1, y, true);
		
		if (mShellInactive)
			gc.setForeground(shellTextColor_Inactive);
		else
			gc.setForeground(shellTextColor);
		gc.drawString(toDraw, midPoint, y, true);
		
		toUse.dispose();
		gc.setFont(font);
	}
	
	private void drawMenuBarButtons(GC gc) {
		Image minImage = null; 
		Image maxImage = null;
		Image closeImage = null;
		
		switch (mTabFolder.getMinButtonState()) {
			default:
			case RibbonTabFolder.STATE_NONE:
				minImage = ImageCache.getImage("mb_min.gif");
				break;
			case RibbonTabFolder.STATE_INACTIVE:
				minImage = ImageCache.getImage("mb_min_inactive.gif");
				break;
			case RibbonTabFolder.STATE_HOVER:
				minImage = ImageCache.getImage("mb_min_hover.gif");
				break;
			case RibbonTabFolder.STATE_HOVER_SELECTED:
				minImage = ImageCache.getImage("mb_min_hover_selected.gif");
				break;
		}
		switch (mTabFolder.getMaxButtonState()) {
			default:
			case RibbonTabFolder.STATE_NONE:
				if (mRibbonShell.getMaximized())
					maxImage = ImageCache.getImage("mb_restore.gif");
				else
					maxImage = ImageCache.getImage("mb_max.gif");
				break;
			case RibbonTabFolder.STATE_INACTIVE:
				if (mRibbonShell.getMaximized())
					maxImage = ImageCache.getImage("mb_restore_inactive.gif");
				else
					maxImage = ImageCache.getImage("mb_max_inactive.gif");
				break;
			case RibbonTabFolder.STATE_HOVER:
				if (mRibbonShell.getMaximized())
					maxImage = ImageCache.getImage("mb_restore_hover.gif");
				else
					maxImage = ImageCache.getImage("mb_max_hover.gif");
				break;
			case RibbonTabFolder.STATE_HOVER_SELECTED:
				if (mRibbonShell.getMaximized())
					maxImage = ImageCache.getImage("mb_restore_hover_selected.gif");
				else
					maxImage = ImageCache.getImage("mb_max_hover_selected.gif");
				break;
		}
		switch (mTabFolder.getCloseButtonState()) {
			default:
			case RibbonTabFolder.STATE_NONE:
				closeImage = ImageCache.getImage("mb_close.gif");
				break;
			case RibbonTabFolder.STATE_INACTIVE:
				closeImage = ImageCache.getImage("mb_close_inactive.gif");
				break;
			case RibbonTabFolder.STATE_HOVER:
				closeImage = ImageCache.getImage("mb_close_hover.gif");
				break;
			case RibbonTabFolder.STATE_HOVER_SELECTED:
				closeImage = ImageCache.getImage("mb_close_hover_selected.gif");
				break;
		}
		
		Rectangle minBounds = mTabFolder.getMinButtonBounds();
		Rectangle maxBounds = mTabFolder.getMaxButtonBounds();
		Rectangle closeBounds = mTabFolder.getCloseButtonBounds();
		
		if (minImage != null)
			gc.drawImage(minImage, minBounds.x, minBounds.y);		
		if (maxImage != null)
			gc.drawImage(maxImage, maxBounds.x, maxBounds.y);		
		if (closeImage != null)
			gc.drawImage(closeImage, closeBounds.x, closeBounds.y);		
		
	}
	
	public void drawBigButton(GC gc) {
		Image toDraw = null;
		
		switch (mTabFolder.getBigButtonState()) {
			default:
			case RibbonTabFolder.STATE_NONE:
			case RibbonTabFolder.STATE_INACTIVE:
				toDraw = ImageCache.getImage("big_button.gif");
				break;
			case RibbonTabFolder.STATE_HOVER:
				toDraw = ImageCache.getImage("big_button_hover.gif");
				break;
			case RibbonTabFolder.STATE_HOVER_SELECTED:
				toDraw = ImageCache.getImage("big_button_hover_selected.gif");
				break;
		}
		Rectangle bounds = mTabFolder.getBigButtonBounds();

		// draw it
		gc.drawImage(toDraw, bounds.x, bounds.y);		

		// draw the big button image on top, centered
		Image bigButtonImage = mRibbonShell.getButtonImage(); 
		if (bigButtonImage != null) {
			Rectangle imBounds = bigButtonImage.getBounds();
			int hSpacer = (bounds.width / 2) - (imBounds.width / 2);
			int vSpacer = (bounds.height / 2) - (imBounds.height / 2);
			
			gc.drawImage(bigButtonImage, bounds.x+hSpacer, bounds.y+vSpacer);
		}
		//gc.setBackground(ColorCache.getInstance().getBlack());
		//gc.fillRectangle(bounds.x, bounds.y + 20, 38, 18);
		
	}
		
	public void drawMenubarToolbar(GC gc) {
		if (mRibbonShell.getToolbar() == null)
			return;
				
		Image leftSide = ImageCache.getImage("mb_tb_left.gif");
		Image rightSide = ImageCache.getImage("mb_tb_right.gif");
		
		Point start = mTabFolder.getMenubarToolbarLocation();
		int x = start.x;
		int y = start.y;
		
		if (mRibbonShell.getMaximized())
			y -= 1;
		
		gc.drawImage(leftSide, x, y);
		
		x += leftSide.getBounds().width;
		
		DefaultButtonPaintManager dpm = new DefaultButtonPaintManager();
		
		// draw toolbar
		List<RibbonButton> buttons  = mRibbonShell.getToolbar().getButtons();
		
		int width = buttons.size() * RibbonGroup.TOOLBAR_BUTTON_WIDTH;
		
		int boundsX = x;
		int boundsY = y;
		
		// draw right side, as hovers may draw over
		gc.drawImage(rightSide, x+width, y);

		// draw background
		gc.setForeground(mToolbarOuterBorder);
		gc.drawLine(x, y, x+width, y);
		gc.setForeground(mToolbarBorder);
		gc.drawLine(x, y+1, x+width, y+1);
		gc.setForeground(mToolbarInnerBorder1);
		gc.drawLine(x, y+2, x+width, y+2);
		gc.setForeground(mToolbarInnerBorder2);
		gc.drawLine(x, y+3, x+width, y+3);
		gc.setForeground(mToolbarFillTopTop);
		gc.setBackground(mToolbarFillTopBottom);
		gc.fillGradientRectangle(x, y+4, width, 9, true);
		gc.setForeground(mToolbarFillBottomTop);
		gc.setBackground(mToolbarFillBottomBottom);
		gc.fillGradientRectangle(x, y+4+9, width, 9, true);
		gc.setForeground(mToolbarBottomBorder);
		gc.drawLine(x, y+4+9+9, x+width, y+4+9+9);
		
		for (int i = 0; i < buttons.size(); i++) {
			RibbonButton button = buttons.get(i);
						
			// we create the bounds for the button right here
			button.setBounds(new Rectangle(x, y+1, RibbonGroup.TOOLBAR_BUTTON_WIDTH, RibbonGroup.TOOLBAR_BUTTON_WIDTH));
			
			dpm.drawMenuToolbarButton(gc, button);
			
			// we don't care what flags are set on toolbars, groups etc, in this toolbar we draw things only one way, plain and "simple"
			x += RibbonGroup.TOOLBAR_BUTTON_WIDTH;
		}
				
		// draw arrow button
		x += 12;
		y -= 1;
		Image arrowButton = null;
		switch (mRibbonShell.getArrowButtonState()) {
			default:
			case AbstractButtonPaintManager.STATE_NONE:
				arrowButton = ImageCache.getImage("mb_qtb_arrow.gif");
				break;
			case AbstractButtonPaintManager.STATE_HOVER:
				arrowButton = ImageCache.getImage("mb_qtb_arrow_hover.gif");
				break;
			case AbstractButtonPaintManager.STATE_SELECTED:
			case AbstractButtonPaintManager.STATE_HOVER_AND_SELECTED:
				arrowButton = ImageCache.getImage("mb_qtb_arrow_hover_selected.gif");
				break;
		}
		
		Rectangle arrBounds = arrowButton.getBounds();
		gc.drawImage(arrowButton, x, y);
		mRibbonShell.setArrowButtonBounds(new Rectangle(x, y, arrBounds.width, arrBounds.height));		
		
		int boundsWidth = x - boundsX + arrBounds.width;
		mToolbarBounds = new Rectangle(boundsX, boundsY, boundsWidth, RibbonGroup.TOOLBAR_BUTTON_HEIGHT);
		mRibbonShell.getRibbonTabFolder().setQuickAccessBounds(mToolbarBounds);
	}
	
	

}
