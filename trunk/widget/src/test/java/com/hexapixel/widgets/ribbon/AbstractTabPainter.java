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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.vietspider.generic.ColorCache;


public abstract class AbstractTabPainter {
	
	// PLAIN
	private Color mTextColor = ColorCache.getInstance().getColor(93, 65, 160);
	private Color mTextFadedRight = ColorCache.getInstance().getColor(120, 197, 255);
	private Color mTextFadedLeft = ColorCache.getInstance().getColor(191, 219, 219);

	// SELECTED
	private Color mBorderTopLeft = ColorCache.getInstance().getColor(156, 188, 221); 
	private Color mBorderTopRight = ColorCache.getInstance().getColor(141, 178, 227); 
	
	private Color mBorderSideTop = ColorCache.getInstance().getColor(142, 178, 226);
	private Color mBorderSideBottom = ColorCache.getInstance().getColor(141, 178, 227);
	
	private Color mBorderSideInner = ColorCache.getInstance().getColor(207, 251, 255);
	private Color mBorderTopInner1 = ColorCache.getInstance().getColor(240, 247, 255);
	private Color mBorderTopInner2 = ColorCache.getInstance().getColor(243, 248, 254);
	private Color mBorderTopInner3 = ColorCache.getInstance().getColor(246, 250, 255);
	
	private Color mInnerTop = ColorCache.getInstance().getColor(236, 244, 254);
	private Color mInnerBottom = ColorCache.getInstance().getColor(222, 232, 246);
	
	private Color mBorderFadeoff1RightTop = ColorCache.getInstance().getColor(178, 204, 238);
	private Color mBorderFadeoff1RightBottom = ColorCache.getInstance().getColor(171, 196, 230);
	private Color mBorderFadeoff2RightTop = ColorCache.getInstance().getColor(188, 216, 251);
	private Color mBorderFadeoff2RightBottom = ColorCache.getInstance().getColor(181, 207, 242);	
	private Color mBorderFadeoff1LeftTop = ColorCache.getInstance().getColor(190, 217, 253);
	private Color mBorderFadeoff1LeftBottom = ColorCache.getInstance().getColor(184, 210, 247);
	private Color mBorderFadeoff2LeftTop = ColorCache.getInstance().getColor(190, 217, 253);
	private Color mBorderFadeoff2LeftBottom = ColorCache.getInstance().getColor(185, 213, 248);	
	
	private Color mFadedPixelTopOuter = ColorCache.getInstance().getColor(180, 210, 248);
	private Color mFadedPixelTopInner = ColorCache.getInstance().getColor(217, 234, 249);
	private Color mFadedPixelTopInnerTwo = ColorCache.getInstance().getColor(198, 237, 251);
	private Color mCornerPixelTop = ColorCache.getInstance().getColor(160, 194, 236);
	private Color mFadedPixelBottomInner = ColorCache.getInstance().getColor(232, 239, 248);
	
	private Color mTextColor_Selected = ColorCache.getInstance().getColor(112, 65, 159);
	private Color mTextFadedRight_Selected = ColorCache.getInstance().getColor(147, 218, 252);
	private Color mTextFadedLeft_Selected = ColorCache.getInstance().getColor(230, 238, 215);
	
	// HOVER
	private Color mBorderTopLeft_Hover = ColorCache.getInstance().getColor(162, 195, 238); 
	private Color mBorderTopRight_Hover = ColorCache.getInstance().getColor(153, 189, 233); 
	private Color mBorderTopInnerLeft_Hover = ColorCache.getInstance().getColor(216, 233, 254); 
	private Color mBorderTopInnerRight_Hover = ColorCache.getInstance().getColor(222, 236, 253); 
	
	private Color mBorderSideTop_Hover = ColorCache.getInstance().getColor(160, 194, 237);
	private Color mBorderSideBottom_Hover = ColorCache.getInstance().getColor(153, 187, 232);
	
	private Color mBorderSideInnerTop_Hover = ColorCache.getInstance().getColor(222, 236, 253);
	private Color mBorderSideInnerMid_Hover = ColorCache.getInstance().getColor(237, 244, 253);
	private Color mBorderSideInnerBottom_Hover = ColorCache.getInstance().getColor(199, 224, 255);
	private Color mBorderSideOuterTop_Hover = ColorCache.getInstance().getColor(203, 225, 254);
	private Color mBorderSideOuterMid_Hover = ColorCache.getInstance().getColor(219, 235, 254);
	private Color mBorderSideOuterBottom_Hover = ColorCache.getInstance().getColor(197, 222, 255);
		
	private Color mFadedPixelTopInnerTwo_Hover = ColorCache.getInstance().getColor(197, 221, 252);
	private Color mFadedPixelTopInnerThree_Hover = ColorCache.getInstance().getColor(209, 228, 254);
	
	private Color mTextColor_Hover = ColorCache.getInstance().getColor(107, 65, 159);
	private Color mTextFadedRight_Hover = ColorCache.getInstance().getColor(136, 210, 253);
	private Color mTextFadedLeft_Hover = ColorCache.getInstance().getColor(212, 228, 215);
	
	private Color mInnerTopTop_Hover = ColorCache.getInstance().getColor(196, 221, 254);
	private Color mInnerTopBottom_Hover = ColorCache.getInstance().getColor(223, 233, 239);
	private Color mInnerBottomTop_Hover = ColorCache.getInstance().getColor(213, 227, 239);
	private Color mInnerBottomBottom_Hover = ColorCache.getInstance().getColor(225, 210, 165);

	// HOVER & SELECTED
	private Color mBorderTopLeft_HoverSelected = ColorCache.getInstance().getColor(212, 188, 141); 
	private Color mBorderTopRight_HoverSelected = ColorCache.getInstance().getColor(215, 183, 127); 
	
	private Color mBorderSideTop_HoverSelected = ColorCache.getInstance().getColor(196, 172, 145);
	private Color mBorderSideBottom_HoverSelected = ColorCache.getInstance().getColor(254, 209, 94);
	
	private Color mBorderSideInnerTop_HoverSelected = ColorCache.getInstance().getColor(255, 255, 189);
	private Color mBorderSideInnerBottom_HoverSelected = mBorderSideInnerTop_HoverSelected;
	private Color mBorderTopInner1_HoverSelected = ColorCache.getInstance().getColor(247, 234, 197);
	private Color mBorderTopInner2_HoverSelected = ColorCache.getInstance().getColor(249, 236, 198);	
	private Color mBorderTopInner3_HoverSelected = ColorCache.getInstance().getColor(246, 250, 255);
	private Color mBorderInnerTwoTop_HoverSelected = ColorCache.getInstance().getColor(245, 249, 255);
	private Color mBorderInnerTwoBottom_HoverSelected = ColorCache.getInstance().getColor(236, 242, 250);

	private Color mInnerTop_HoverSelected = ColorCache.getInstance().getColor(239, 246, 254);
	private Color mInnerBottom_HoverSelected = ColorCache.getInstance().getColor(225, 235, 246);
	
	private Color mBorderFadeoff1RightTop_HoverSelected = ColorCache.getInstance().getColor(177, 193, 190);
	private Color mBorderFadeoff1RightBottom_HoverSelected = ColorCache.getInstance().getColor(176, 180, 155);
	private Color mBorderFadeoff2RightTop_HoverSelected = ColorCache.getInstance().getColor(197, 219, 231);
	private Color mBorderFadeoff2RightBottom_HoverSelected = ColorCache.getInstance().getColor(192, 204, 190);	
	private Color mBorderFadeoff1LeftTop_HoverSelected = ColorCache.getInstance().getColor(200, 207, 181);
	private Color mBorderFadeoff1LeftBottom_HoverSelected = ColorCache.getInstance().getColor(199, 201, 169);
	private Color mBorderFadeoff2LeftTop_HoverSelected = ColorCache.getInstance().getColor(199, 215, 207);
	private Color mBorderFadeoff2LeftBottom_HoverSelected = ColorCache.getInstance().getColor(195, 209, 200);	
	
	private Color mFadedPixelTopOuter_HoverSelected = ColorCache.getInstance().getColor(188, 197, 198);
	private Color mFadedPixelTopInner_HoverSelected = ColorCache.getInstance().getColor(234, 222, 185);
	private Color mFadedPixelTopInnerTwo_HoverSelected = ColorCache.getInstance().getColor(254, 209, 94);
	private Color mCornerPixelTop_HoverSelected = ColorCache.getInstance().getColor(174, 187, 191);
	private Color mFadedPixelBottomInner_HoverSelected = ColorCache.getInstance().getColor(232, 239, 248);
	
	private Color mTextColor_HoverSelected = ColorCache.getInstance().getColor(112, 65, 159);
	private Color mTextFadedRight_HoverSelected = ColorCache.getInstance().getColor(147, 218, 252);
	private Color mTextFadedLeft_HoverSelected = ColorCache.getInstance().getColor(230, 238, 215);
	
	// SURROUNDING CURVED LINE AND BACKGROUND
	private Color mBottomLineFaded1 = ColorCache.getInstance().getColor(185, 214, 251);
	private Color mBottomLineFaded2 = ColorCache.getInstance().getColor(165, 197, 240);
	private Color mBottomLineFaded3 = ColorCache.getInstance().getColor(146, 183, 230);
	private Color mBottomLineColor = ColorCache.getInstance().getColor(141, 178, 227);
	private Color mBottomLineColorBelow = ColorCache.getInstance().getColor(231, 239, 248);
	private Color mBottomLineSelectedTabFaded1 = ColorCache.getInstance().getColor(150, 190, 231);
	private Color mBottomLineSelectedTabFaded2 = ColorCache.getInstance().getColor(182, 227, 246);
	private Color mBottomLineSelectedTabFaded3 = ColorCache.getInstance().getColor(220, 247, 253);
	private Color mBottomLineSelectedTab = ColorCache.getInstance().getColor(224, 234, 246);
	
	private Color mBottomCornerFadedOuter = ColorCache.getInstance().getColor(182, 211, 248);
	private Color mBottomCornerFadedInner = ColorCache.getInstance().getColor(200, 217, 240);
	private Color mBottomCornerBottomFadedInner1Right = ColorCache.getInstance().getColor(188, 229, 243); // below
	private Color mBottomCornerBottomFadedInner2Right = ColorCache.getInstance().getColor(202, 240, 252); // inside corner
	private Color mBottomCornerBottomFadedInner3Right = ColorCache.getInstance().getColor(190, 236, 249); // right
	private Color mBottomCornerBottomFadedInner1Left = ColorCache.getInstance().getColor(189, 237, 246); // below
	private Color mBottomCornerBottomFadedInner2Left = ColorCache.getInstance().getColor(207, 240, 252); // inside corner
	private Color mBottomCornerBottomFadedInner3Left = ColorCache.getInstance().getColor(194, 243, 252); // left
	private Color mBottomCornerBottomFadedOuter1 = ColorCache.getInstance().getColor(160, 181, 209); // bottom - left
	private Color mBottomCornerBottomFadedOuter2 = ColorCache.getInstance().getColor(158, 183, 214); // bottom actual corner
	private Color mBottomCornerBottomFadedOuter3 = ColorCache.getInstance().getColor(172, 195, 225); // all outer

	private Color mSideLineColorTop = ColorCache.getInstance().getColor(142, 178, 226);
	private Color mSideLineColorBottom = ColorCache.getInstance().getColor(145, 177, 219);
	private Color mSideLineInnerTopTop = ColorCache.getInstance().getColor(223, 235, 247);
	private Color mSideLineInnerTopBottom = ColorCache.getInstance().getColor(213, 235, 247);
	private Color mSideLineInnerBottomTop = ColorCache.getInstance().getColor(204, 232, 245);
	private Color mSideLineInnerBottomBottom = ColorCache.getInstance().getColor(194, 243, 252);

	private Color mSideLineRightColorTop = ColorCache.getInstance().getColor(147, 182, 229);
	private Color mSideLineRightColorBottom = ColorCache.getInstance().getColor(151, 187, 230);
	private Color mSideLineRightInnerTopTop = ColorCache.getInstance().getColor(226, 237, 247);
	private Color mSideLineRightInnerTopBottom = ColorCache.getInstance().getColor(213, 234, 247);
	private Color mSideLineRightInnerBottomTop = ColorCache.getInstance().getColor(205, 231, 245);
	private Color mSideLineRightInnerBottomBottom = ColorCache.getInstance().getColor(196, 249, 255);
	
	private Color mBottomLineInner = ColorCache.getInstance().getColor(192, 249, 255);
	private Color mBottomLine1 = ColorCache.getInstance().getColor(150, 170, 196);
	private Color mBottomLine2 = ColorCache.getInstance().getColor(166, 188, 217);
	private Color mBottomLine3 = ColorCache.getInstance().getColor(177, 200, 231);
	
	private Color mInnerFillTopTop = ColorCache.getInstance().getColor(223, 236, 247);
	private Color mInnerFillTopBottom = ColorCache.getInstance().getColor(209, 223, 240);
	private Color mInnerFillMidTop = ColorCache.getInstance().getColor(199, 216, 237);
	private Color mInnerFillMidBottom = ColorCache.getInstance().getColor(216, 233, 245);
	private Color mInnerFillBottomTop = ColorCache.getInstance().getColor(217, 232, 246);
	private Color mInnerFillBottomBottom = ColorCache.getInstance().getColor(231, 242, 255);

	public void drawTab(GC gc, RibbonTab tab, int yStart) {
		Rectangle bounds = tab.getBounds();
		int x = bounds.x;
		int y = yStart;
		int width = bounds.width;
						
		if (tab.isSelected() && !tab.isHover()) {
			// start drawing at the top
			gc.setForeground(mBorderTopLeft);
			gc.setBackground(mBorderTopRight);
			gc.fillGradientRectangle(x+4, y, width-9, 1, false);		
			
			gc.setForeground(mBorderTopInner1);
			gc.drawLine(x+4, y+1, x+width-6, y+1);
			gc.setForeground(mBorderTopInner2);
			gc.drawLine(x+4, y+2, x+width-5, y+2);
			gc.setForeground(mBorderTopInner3);
			gc.drawLine(x+4, y+3, x+width-6, y+3);
			
			// helper
			int neg = 6;

			// draw sides (left)
			gc.setForeground(mBorderFadeoff2LeftTop);
			gc.setBackground(mBorderFadeoff2LeftBottom);
			gc.fillGradientRectangle(x, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg+2, true);

			gc.setForeground(mBorderFadeoff1LeftTop);
			gc.setBackground(mBorderFadeoff1LeftBottom);
			gc.fillGradientRectangle(x+1, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg+1, true);
		
			// draw sides (right)
			gc.setForeground(mBorderFadeoff1RightTop);
			gc.setBackground(mBorderFadeoff1RightBottom);
			gc.fillGradientRectangle(x+width-2, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg+1, true);
		
			gc.setForeground(mBorderFadeoff2RightTop);
			gc.setBackground(mBorderFadeoff2RightBottom);
			gc.fillGradientRectangle(x+width-1, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg+2, true);
			
			// actual side
			gc.setForeground(mBorderSideTop);
			gc.setBackground(mBorderSideBottom);
			gc.fillGradientRectangle(x+2, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg+1, true);
			gc.fillGradientRectangle(x+width-3, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg+1, true); // right
			// inner sides
			gc.setForeground(mBorderSideInner);
			gc.setBackground(mBorderSideInner); // same right now
			gc.fillGradientRectangle(x+3, y+2, 1, RibbonTabFolder.TAB_HEIGHT-neg+3, true);
			gc.fillGradientRectangle(x+width-4, y+2, 1, RibbonTabFolder.TAB_HEIGHT-neg+3, true); // right
			// inner even more
			gc.setForeground(mBorderTopInner3);
			gc.drawLine(x+4, y+3, x+4, y+3+RibbonTabFolder.TAB_HEIGHT-neg);
			gc.drawLine(x+width-5, y+3, x+width-5, y+3+RibbonTabFolder.TAB_HEIGHT-neg); // right
			
			// fill
			gc.setForeground(mInnerTop);
			gc.setBackground(mInnerBottom);
			gc.fillGradientRectangle(x+5, y+4, width-10, 18, true);
			
			// TOP CORNERS (far from exact copy, but close enough)
			gc.setForeground(mCornerPixelTop);
			// left
			gc.drawLine(x+4, y, x+4, y);
			gc.drawLine(x+3, y+1, x+3, y+1);
			gc.drawLine(x+2, y+2, x+2, y+2);
			// right
			gc.drawLine(x+width-5, y, x+width-5, y);
			gc.drawLine(x+width-4, y+1, x+width-4, y+1);
			gc.drawLine(x+width-3, y+2, x+width-3, y+2);
						
			// faded pixel inside
			gc.setForeground(mFadedPixelTopInner);
			gc.drawLine(x+4, y+1, x+4, y+1);
			gc.drawLine(x+width-5, y+1, x+width-5, y+1);
			// other faded inside 
			gc.setForeground(mFadedPixelTopInnerTwo);
			gc.drawLine(x+3, y+2, x+3, y+2);
			
			// outer again
			gc.setForeground(mFadedPixelTopOuter);
			gc.drawLine(x+3, y, x+3, y);		
			gc.drawLine(x+2, y+1, x+2, y+1);		

			gc.drawLine(x+width-4, y, x+width-4, y);
			gc.drawLine(x+width-3, y+1, x+width-3, y+1);

			// BOTTOM CORNERS
			// left
			gc.setForeground(mCornerPixelTop);
			gc.drawLine(x+2, y+RibbonTabFolder.TAB_HEIGHT-neg+3, x+2, y+RibbonTabFolder.TAB_HEIGHT-neg+3);
			gc.drawLine(x+1, y+RibbonTabFolder.TAB_HEIGHT-neg+4, x+1, y+RibbonTabFolder.TAB_HEIGHT-neg+4);
			gc.drawLine(x+width-3, y+RibbonTabFolder.TAB_HEIGHT-neg+3, x+width-3, y+RibbonTabFolder.TAB_HEIGHT-neg+3);
			gc.drawLine(x+width-2, y+RibbonTabFolder.TAB_HEIGHT-neg+4, x+width-2, y+RibbonTabFolder.TAB_HEIGHT-neg+4);
			
			gc.setForeground(mFadedPixelBottomInner);
			gc.drawLine(x+4, y+RibbonTabFolder.TAB_HEIGHT-neg+4, x+4, y+RibbonTabFolder.TAB_HEIGHT-neg+4);
			gc.drawLine(x+width-5, y+RibbonTabFolder.TAB_HEIGHT-neg+4, x+width-5, y+RibbonTabFolder.TAB_HEIGHT-neg+4);
			
			gc.setForeground(mFadedPixelTopInnerTwo);
			gc.drawLine(x+2, y+RibbonTabFolder.TAB_HEIGHT-neg+4, x+2, y+RibbonTabFolder.TAB_HEIGHT-neg+4);
			gc.drawLine(x+width-3, y+RibbonTabFolder.TAB_HEIGHT-neg+4, x+width-3, y+RibbonTabFolder.TAB_HEIGHT-neg+4); 
		}
		else
		// hover
		// funny enough, this is almost completely different than a selected tab, typical...
		if (tab.isHover() && !tab.isSelected()) {
			int neg = 4;
			
			// start drawing at the top
			gc.setForeground(mBorderTopLeft_Hover);
			gc.setBackground(mBorderTopRight_Hover);
			gc.fillGradientRectangle(x+4, y, width-9, 1, false);
			
			gc.setForeground(mBorderTopInnerLeft_Hover);
			gc.setBackground(mBorderTopInnerRight_Hover);
			gc.fillGradientRectangle(x+4, y+1, width-9, 1, false);

			// draw sides
			gc.setForeground(mBorderSideTop_Hover);
			gc.setBackground(mBorderSideBottom_Hover);
			gc.fillGradientRectangle(x+2, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg, true);
			gc.fillGradientRectangle(x+width-3, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg, true); // right

			// inner side
			gc.setForeground(mBorderSideInnerTop_Hover);
			gc.setBackground(mBorderSideInnerMid_Hover);
			gc.fillGradientRectangle(x+3, y+3, 1, 10, true);
			gc.fillGradientRectangle(x+width-4, y+3, 1, 10, true); // right
			gc.setForeground(mBorderSideInnerMid_Hover);
			gc.setBackground(mBorderSideInnerBottom_Hover);
			gc.fillGradientRectangle(x+3, y+3+10, 1, 9, true);
			gc.fillGradientRectangle(x+width-4, y+3+10, 1, 9, true); // right

			// outer side
			gc.setForeground(mBorderSideOuterTop_Hover);
			gc.setBackground(mBorderSideOuterMid_Hover);
			gc.fillGradientRectangle(x+1, y+3, 1, 10, true);
			gc.fillGradientRectangle(x+width-2, y+3, 1, 10, true); // right
			gc.setForeground(mBorderSideOuterMid_Hover);
			gc.setBackground(mBorderSideOuterBottom_Hover);
			gc.fillGradientRectangle(x+1, y+3+10, 1, 9, true);
			gc.fillGradientRectangle(x+width-2, y+3+10, 1, 9, true); // right

			// fill (before drawing pixels)
			gc.setForeground(mInnerTopTop_Hover);
			gc.setBackground(mInnerTopBottom_Hover);
			gc.fillGradientRectangle(x+4, y+2, width-8, 10, true);
			gc.setForeground(mInnerBottomTop_Hover);
			gc.setBackground(mInnerBottomBottom_Hover);
			gc.fillGradientRectangle(x+4, y+2+10, width-8, 10, true);
			
			// TOP CORNERS (far from exact copy, but close enough)
			gc.setForeground(mCornerPixelTop);
			// left
			gc.drawLine(x+4, y, x+4, y);
			gc.drawLine(x+3, y+1, x+3, y+1);
			gc.drawLine(x+2, y+2, x+2, y+2);
			// right
			gc.drawLine(x+width-5, y, x+width-5, y);
			gc.drawLine(x+width-4, y+1, x+width-4, y+1);
			gc.drawLine(x+width-3, y+2, x+width-3, y+2);
						
			// faded pixel inside
			gc.setForeground(mFadedPixelTopInner);
			gc.drawLine(x+4, y+1, x+4, y+1);
			gc.drawLine(x+width-5, y+1, x+width-5, y+1);
			// other faded inside 
			gc.setForeground(mFadedPixelTopInnerTwo_Hover);
			gc.drawLine(x+4, y+1, x+4, y+1);
			gc.drawLine(x+width-5, y+1, x+width-5, y+1);
			gc.drawLine(x+3, y+2, x+3, y+2);
			gc.drawLine(x+width-4, y+2, x+width-4, y+2);
			// more 
			gc.setForeground(mFadedPixelTopInnerThree_Hover);
			gc.drawLine(x+4, y+2, x+4, y+2);
			gc.drawLine(x+width-5, y+2, x+width-5, y+2);

			// outer again
			gc.setForeground(mFadedPixelTopOuter);
			gc.drawLine(x+3, y, x+3, y);		
			gc.drawLine(x+2, y+1, x+2, y+1);		

			gc.drawLine(x+width-4, y, x+width-4, y);
			gc.drawLine(x+width-3, y+1, x+width-3, y+1);
		}
		else
		// hover and selected
		if (tab.isHover() && tab.isSelected()) {
			// start drawing at the top
			gc.setForeground(mBorderTopLeft_HoverSelected);
			gc.setBackground(mBorderTopRight_HoverSelected);
			gc.fillGradientRectangle(x+4, y, width-9, 1, false);		
			
			gc.setForeground(mBorderTopInner1_HoverSelected);
			gc.drawLine(x+4, y+1, x+width-6, y+1);
			gc.setForeground(mBorderTopInner2_HoverSelected);
			gc.drawLine(x+4, y+2, x+width-5, y+2);
			gc.setForeground(mBorderTopInner3_HoverSelected);
			gc.drawLine(x+4, y+3, x+width-6, y+3);
			
			// helper
			int neg = 6;

			// draw sides (left)
			gc.setForeground(mBorderFadeoff2LeftTop_HoverSelected);
			gc.setBackground(mBorderFadeoff2LeftBottom_HoverSelected);
			//gc.setForeground(ColorCache.getInstance().getColor(255, 0, 0));
			//gc.setBackground(ColorCache.getInstance().getColor(255, 0, 0));
			gc.fillGradientRectangle(x, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg+2, true);

			gc.setForeground(mBorderFadeoff1LeftTop_HoverSelected);
			gc.setBackground(mBorderFadeoff1LeftBottom_HoverSelected);
			gc.fillGradientRectangle(x+1, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg+1, true);
		
			// draw sides (right)
			gc.setForeground(mBorderFadeoff1RightTop_HoverSelected);
			gc.setBackground(mBorderFadeoff1RightBottom_HoverSelected);
			gc.fillGradientRectangle(x+width-2, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg+1, true);
		
			gc.setForeground(mBorderFadeoff2RightTop_HoverSelected);
			gc.setBackground(mBorderFadeoff2RightBottom_HoverSelected);
			gc.fillGradientRectangle(x+width-1, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg+2, true);
			
			// actual side
			gc.setForeground(mBorderSideTop_HoverSelected);
			gc.setBackground(mBorderSideBottom_HoverSelected);
			gc.fillGradientRectangle(x+2, y+2, 1, RibbonTabFolder.TAB_HEIGHT-neg+3, true);
			gc.fillGradientRectangle(x+width-3, y+2, 1, RibbonTabFolder.TAB_HEIGHT-neg+3, true); // right
			// inner sides
			gc.setForeground(mBorderSideInnerTop_HoverSelected);
			gc.setBackground(mBorderSideInnerBottom_HoverSelected); 
			gc.fillGradientRectangle(x+3, y+2, 1, RibbonTabFolder.TAB_HEIGHT-neg+3, true);
			gc.fillGradientRectangle(x+width-4, y+2, 1, RibbonTabFolder.TAB_HEIGHT-neg+3, true); // right
			// inner even more
			gc.setForeground(mBorderInnerTwoTop_HoverSelected);
			gc.setBackground(mBorderInnerTwoBottom_HoverSelected);
			gc.fillGradientRectangle(x+4, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg+1, true);
			gc.fillGradientRectangle(x+width-5, y+3, 1, RibbonTabFolder.TAB_HEIGHT-neg+1, true); // right
			
			// fill
			gc.setForeground(mInnerTop_HoverSelected);
			gc.setBackground(mInnerBottom_HoverSelected);
			gc.fillGradientRectangle(x+5, y+4, width-10, 18, true);
			
			// TOP CORNERS (far from exact copy, but close enough)
			gc.setForeground(mCornerPixelTop_HoverSelected);
			// left
			gc.drawLine(x+4, y, x+4, y);
			gc.drawLine(x+3, y+1, x+3, y+1);
			gc.drawLine(x+2, y+2, x+2, y+2);
			// right
			gc.drawLine(x+width-5, y, x+width-5, y);
			gc.drawLine(x+width-4, y+1, x+width-4, y+1);
			gc.drawLine(x+width-3, y+2, x+width-3, y+2);
						
			// faded pixel inside
			gc.setForeground(mFadedPixelTopInner_HoverSelected);
			gc.drawLine(x+4, y+1, x+4, y+1);
			gc.drawLine(x+width-5, y+1, x+width-5, y+1);
			// other faded inside 
			gc.setForeground(mFadedPixelTopInnerTwo_HoverSelected);
			gc.drawLine(x+3, y+2, x+3, y+2);
			
			// outer again
			gc.setForeground(mFadedPixelTopOuter_HoverSelected);
			gc.drawLine(x+3, y, x+3, y);		
			gc.drawLine(x+2, y+1, x+2, y+1);		

			gc.drawLine(x+width-4, y, x+width-4, y);
			gc.drawLine(x+width-3, y+1, x+width-3, y+1);

			// BOTTOM CORNERS
			// left
			gc.setForeground(mCornerPixelTop_HoverSelected);

			// bottom left & right
			gc.drawLine(x+1, y+RibbonTabFolder.TAB_HEIGHT-neg+4, x+1, y+RibbonTabFolder.TAB_HEIGHT-neg+4);
			gc.drawLine(x+width-2, y+RibbonTabFolder.TAB_HEIGHT-neg+4, x+width-2, y+RibbonTabFolder.TAB_HEIGHT-neg+4);
			
			gc.setForeground(mFadedPixelBottomInner);
			gc.drawLine(x+4, y+RibbonTabFolder.TAB_HEIGHT-neg+4, x+4, y+RibbonTabFolder.TAB_HEIGHT-neg+4);
			gc.drawLine(x+width-5, y+RibbonTabFolder.TAB_HEIGHT-neg+4, x+width-5, y+RibbonTabFolder.TAB_HEIGHT-neg+4);
			
		}
		
		// draw text (fake shadowing by drawing 1 pixel left and right with different colors that are more or less faded), gets us close enough to real result
		if (tab.isHover() && !tab.isSelected())
			gc.setForeground(mTextFadedRight_Hover);
		else
		if (tab.isSelected() && !tab.isHover())
			gc.setForeground(mTextFadedRight_Selected);
		else 
		if (tab.isSelected() && tab.isHover())
			gc.setForeground(mTextFadedRight_HoverSelected);
		else
			gc.setForeground(mTextFadedRight);
		
		gc.drawText(tab.getName(), 1+x+5+RibbonTabFolder.TEXT_SPACING_SIDE, yStart+5, true);
		
		if (tab.isHover() && !tab.isSelected())
			gc.setForeground(mTextFadedLeft_Hover);
		else
		if (tab.isSelected() && !tab.isHover())
			gc.setForeground(mTextFadedLeft_Selected);
		else 
		if (tab.isSelected() && tab.isHover())
			gc.setForeground(mTextFadedLeft_HoverSelected);
		else
			gc.setForeground(mTextFadedLeft);
		
		gc.drawText(tab.getName(), -1+x+5+RibbonTabFolder.TEXT_SPACING_SIDE, yStart+5, true);

		if (tab.isHover() && !tab.isSelected())
			gc.setForeground(mTextColor_Hover);
		else
		if (tab.isSelected() && !tab.isHover())
			gc.setForeground(mTextColor_Selected);
		else
		if (tab.isSelected() && tab.isHover())			
			gc.setForeground(mTextColor_HoverSelected);
		else
			gc.setForeground(mTextColor);

		gc.drawText(tab.getName(), x+5+RibbonTabFolder.TEXT_SPACING_SIDE, yStart+5, true);
		
	}
	
	public void drawTabBorder(GC gc, Rectangle bounds, RibbonTab selectedTab, boolean collapsedTabFolder, int yStart) {
		int leftSpacer = 1;
		int x = leftSpacer;
		int width = bounds.width;
		
		int minWidth = 0;
		
		if (selectedTab != null)
			minWidth = selectedTab.getFancyToolbar().getActualBounds().width+6;
		
		if (width < minWidth)
			width = minWidth;
		
		int y = yStart+RibbonTabFolder.TAB_HEIGHT + 2;
		
		if (!collapsedTabFolder) {
			// fill first, so that we can overwrite if necessary
			gc.setForeground(mInnerFillTopTop);
			gc.setBackground(mInnerFillTopBottom);
			gc.fillGradientRectangle(x+2, y, width-6, 18, true);
			gc.setForeground(mInnerFillMidTop);
			gc.setBackground(mInnerFillMidBottom);
			gc.fillGradientRectangle(x+2, y+18, width-6, 54, true);
			gc.setForeground(mInnerFillBottomTop);
			gc.setBackground(mInnerFillBottomBottom);
			gc.fillGradientRectangle(x+2, y+18+54, width-6, 17, true);
		}
						
		// draw bottom line (below tabs), guess it's the top line really, that runs across the entire tab folder
		gc.setForeground(mBottomLineColorBelow);
		if (collapsedTabFolder)
			gc.drawLine(x, y+1, x+width, y+1);
		else
			gc.drawLine(x+3, y+1, x+width-8, y+1);		
		
		gc.setForeground(mBottomLineColor);		
		if (collapsedTabFolder)
			gc.drawLine(x, y, x+width, y);
		else
			gc.drawLine(x+4, y, x+width-7, y); // top left

		if (collapsedTabFolder)
			return;		
		
		// draws the open tab "feel" on top of the line we just drew
		if (selectedTab != null) {
    		Rectangle tabBounds = selectedTab.getBounds();
    		
    		int tabX = tabBounds.x;
    		int tabWidth = tabBounds.width;
    		gc.setForeground(mBottomLineSelectedTabFaded1);
    		gc.drawLine(tabX, y, tabX, y);
    		gc.setForeground(mBottomLineSelectedTabFaded2);
    		gc.drawLine(tabX+1, y, tabX+1, y);
    		gc.setForeground(mBottomLineSelectedTabFaded3);
    		gc.drawLine(tabX+2, y, tabX+2, y);
    		gc.setForeground(mBottomLineSelectedTab);
    		gc.drawLine(tabX+3, y, tabX+3+tabWidth-6, y);

    		// reverse it, more or less
    		gc.setForeground(mBottomLineSelectedTabFaded1);
    		int xTwo = tabX+3+tabWidth-4;
    		gc.drawLine(xTwo, y, xTwo, y);
    		gc.setForeground(mBottomLineSelectedTabFaded2);
    		gc.drawLine(xTwo-1, y, xTwo-1, y);
    		gc.setForeground(mBottomLineSelectedTabFaded3);
    		gc.drawLine(xTwo-2, y, xTwo-2, y);
		}		
		
		// draw the rest, rounded corners on all sides and corners
		
		// left side
		gc.setForeground(mSideLineColorTop);
		gc.setBackground(mSideLineColorBottom);		
		gc.fillGradientRectangle(x, y+3, 1, 86, true);
		
		// inner side (left)
		gc.setForeground(mSideLineInnerTopTop);
		gc.setBackground(mSideLineInnerTopBottom);
		gc.fillGradientRectangle(x+1, y+3, 1, 15, true);
		gc.setForeground(mSideLineInnerBottomTop);
		gc.setBackground(mSideLineInnerBottomBottom);
		gc.fillGradientRectangle(x+1, y+3+15, 1, 71, true);

		// right side
		gc.setForeground(mSideLineRightColorTop);
		gc.setBackground(mSideLineRightColorBottom);		
		gc.fillGradientRectangle(x+width-4, y+3, 1, 86, true);
		
		// right side (left)
		gc.setForeground(mSideLineRightInnerTopTop);
		gc.setBackground(mSideLineRightInnerTopBottom);
		gc.fillGradientRectangle(x+width-5, y+3, 1, 15, true);
		gc.setForeground(mSideLineRightInnerBottomTop);
		gc.setBackground(mSideLineRightInnerBottomBottom);
		gc.fillGradientRectangle(x+width-5, y+3+15, 1, 71, true);

		// bottom side
		int bottom = y+3+15+71;
		gc.setForeground(mBottomLineInner);
		gc.drawLine(x+2, bottom, x+width-6, bottom);
		gc.setForeground(mBottomLine1);
		gc.drawLine(x+2, bottom+1, x+width-6, bottom+1);
		gc.setForeground(mBottomLine2);
		gc.drawLine(x+2, bottom+2, x+width-6, bottom+2);
		gc.setForeground(mBottomLine3);
		gc.drawLine(x+2, bottom+3, x+width-6, bottom+3);
		
		// corners x 4
		gc.setForeground(mBottomLineFaded2);
		gc.drawLine(x+1, y+1, x+1, y+1);
		gc.drawLine(x, y+2, x, y+2);

		// dark pixel in corner making the actual corner
		gc.setForeground(mBottomLineColor); 
		gc.drawLine(x+width-5, y+1, x+width-5, y+1); // top right

		gc.setForeground(mBottomLineFaded1);
		gc.drawLine(x+1, y, x+1, y); // top left
		gc.drawLine(x+width-5, y, x+width-5, y); // top right
		
		gc.setForeground(mBottomLineFaded2);
		gc.drawLine(x+2, y, x+2, y); // top left 
		gc.drawLine(x+width-6, y, x+width-6, y); // top right
		gc.drawLine(x+width-4, y+2, x+width-4, y+2); // top right (below right)
		
		gc.setForeground(mBottomLineFaded3);
		gc.drawLine(x+3, y, x+3, y); // top left		
		
		// outer faded pixels		
		gc.setForeground(mBottomCornerFadedOuter);
		gc.drawLine(x, y+1, x, y+1); // top left
		gc.drawLine(x+width-4, y+1, x+width-4, y+1); // top left
		gc.drawLine(x+width-5, y, x+width-5, y); // top left
		
		// inner faded
		gc.setForeground(mBottomCornerFadedInner);
		gc.drawLine(x+2, y+1, x+2, y+1); // top left
		gc.drawLine(x+1, y+2, x+1, y+2); // top left
		gc.drawLine(x+width-6, y+1, x+width-6, y+1); // top right
		gc.drawLine(x+width-5, y+2, x+width-5, y+2); // top right
		
		// bottom corners, different than top
		gc.setForeground(mBottomCornerBottomFadedInner1Right);
		gc.drawLine(x+width-5, bottom-1, x+width-5, bottom-1);
		gc.setForeground(mBottomCornerBottomFadedInner1Left);
		gc.drawLine(x+2, bottom, x+2, bottom);
		gc.setForeground(mBottomCornerBottomFadedInner2Right);
		gc.drawLine(x+width-6, bottom-1, x+width-6, bottom-1);
		gc.setForeground(mBottomCornerBottomFadedInner2Left);
		gc.drawLine(x+2, bottom-1, x+2, bottom-1);
		gc.setForeground(mBottomCornerBottomFadedInner3Right);
		gc.drawLine(x+width-6, bottom, x+width-6, bottom);
		gc.setForeground(mBottomCornerBottomFadedInner3Left);
		gc.drawLine(x+1, bottom-1, x+1, bottom-1);
		// outer
		gc.setForeground(mBottomCornerBottomFadedOuter1);
		gc.drawLine(x+width-6, bottom+1, x+width-6, bottom+1);
		gc.drawLine(x+2, bottom+1, x+2, bottom+1);
		gc.setForeground(mBottomCornerBottomFadedOuter2);
		gc.drawLine(x+width-5, bottom, x+width-5, bottom);
		gc.drawLine(x+1, bottom, x+1, bottom);
		gc.setForeground(mBottomCornerBottomFadedOuter3);
		gc.drawLine(x+width-6, bottom+2, x+width-6, bottom+2);
		gc.drawLine(x+width-5, bottom+1, x+width-5, bottom+1);
		gc.drawLine(x+width-4, bottom, x+width-4, bottom);
		gc.drawLine(x+2, bottom+2, x+2, bottom+2);
		gc.drawLine(x+1, bottom+1, x+1, bottom+1);
		gc.drawLine(x, bottom, x, bottom);
		
		
	}

}
