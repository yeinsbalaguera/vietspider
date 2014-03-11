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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.vietspider.generic.ColorCache;


/**
 * Draws a Toolbar button in one of the following states:
 * 	
 *  - Hover (light)
 * 	- Selected (darker)
 * 	- Selected and Hover (darkest)
 * 
 * This is very down to the pixel level and not very simply done, each mode has different highlighted pixels as well so it's
 * not just a matter of darkening or lightening a certain color.
 * 
 * @author Emil Crumhorn
 *
 */
public abstract class AbstractButtonPainter {

	private static int TOP_SPACING = 2;

	// SPLIT BUTTON (Mostly same colors as others, but fills and divider change)
	private Color mInnerTopPartTopTopSplit = ColorCache.getInstance().getColor(255, 254, 243);
	private Color mInnerTopPartTopBottomSplit = ColorCache.getInstance().getColor(255, 247, 217);
	private Color mInnerTopPartTopBottomTopSplit = ColorCache.getInstance().getColor(255, 241, 194);
	private Color mInnerTopPartTopBottomBottomSplit = ColorCache.getInstance().getColor(255, 240, 195);
	
	private Color mInnerTopPartTopTopSplit_Hover = ColorCache.getInstance().getColor(255, 253, 219);
	private Color mInnerTopPartTopBottomSplit_Hover = ColorCache.getInstance().getColor(255, 231, 144);
	private Color mInnerTopPartTopBottomTopSplit_Hover = ColorCache.getInstance().getColor(255, 215, 76);
	private Color mInnerTopPartTopBottomBottomSplit_Hover = ColorCache.getInstance().getColor(255, 211, 70);

	private Color mInnerBottomPartTopSplit = ColorCache.getInstance().getColor(255, 239, 199);
	private Color mInnerBottomPartBottomSplit = ColorCache.getInstance().getColor(255, 247, 223);
	private Color mInnerBottomPartTopSplit_Hover = ColorCache.getInstance().getColor(255, 212, 83);
	private Color mInnerBottomPartBottomSplit_Hover = ColorCache.getInstance().getColor(255, 234, 168);
	private Color mInnerBottomPartTopSplit_Selected = ColorCache.getInstance().getColor(249, 170, 78);
	private Color mInnerBottomPartBottomSplit_Selected = ColorCache.getInstance().getColor(252, 231, 160);

	private Color mInnerSplitDividerLeft = ColorCache.getInstance().getColor(219, 195, 116);
	private Color mInnerSplitDividerMid = ColorCache.getInstance().getColor(206, 190, 134);

	private Color mInnerSplitSmallDividerTop = ColorCache.getInstance().getColor(226, 214, 189);
	private Color mInnerSplitSmallDividerMid = ColorCache.getInstance().getColor(205, 181, 131);

	// SPLIT SMALL
	private Color mInnerTopPartTopTopSplitSmall = ColorCache.getInstance().getColor(255, 255, 251);
	private Color mInnerTopPartTopBottomSplitSmall = ColorCache.getInstance().getColor(255, 248, 224);
	private Color mInnerTopPartTopBottomTopSplitSmall = ColorCache.getInstance().getColor(255, 252, 192);
	private Color mInnerTopPartTopBottomBottomSplitSmall = ColorCache.getInstance().getColor(255, 251, 214);

	
/*	private Color mInnerBottomTopSplit = ColorCache.getInstance().getColor(255, 254, 243);
	private Color mInnerBottomBottomSplit = ColorCache.getInstance().getColor(255, 254, 243);
	private Color mInnerBottomTopSplit_Hover = ColorCache.getInstance().getColor(255, 254, 243);
	private Color mInnerBottomBottomSplit_Hover = ColorCache.getInstance().getColor(255, 254, 243);
*/	
	// HOVER COLORS
	private Color mBorderTop_Hover = ColorCache.getInstance().getColor(221, 207, 155); 
	private Color mBorderSideTopTop_Hover = ColorCache.getInstance().getColor(220, 206, 154);
	private Color mBorderSideTopBottom_Hover = ColorCache.getInstance().getColor(197, 174, 125); // 24 px high total
	private Color mBorderSideMidTop_Hover = ColorCache.getInstance().getColor(196, 173, 124); 
	private Color mBorderSideMidBottom_Hover = ColorCache.getInstance().getColor(198, 178, 131); // 27 px high total
	private Color mBorderSideBottomTop_Hover = ColorCache.getInstance().getColor(198, 180, 135);
	private Color mBorderSideBottomBottom_Hover = ColorCache.getInstance().getColor(211, 206, 185); // 13 px high total
	private Color mBorderBottom_Hover = ColorCache.getInstance().getColor(211, 207, 188);
	
	private Color mCornerFadeTopPixel_Hover = ColorCache.getInstance().getColor(234, 226, 168);
	private Color mCornerFadeBottomPixel_Hover = ColorCache.getInstance().getColor(235, 225, 189);
	
	private Color mBorderInnerTop_Hover = ColorCache.getInstance().getColor(255, 255, 247);
	private Color mBorderInnerSideTopTop_Hover = ColorCache.getInstance().getColor(255, 255, 247);
	private Color mBorderInnerSideTopBottom_Hover = ColorCache.getInstance().getColor(255, 248, 223); // 23 px high total
	private Color mBorderInnerSideMidTop_Hover = ColorCache.getInstance().getColor(255, 243, 206); 
	private Color mBorderInnerSideMidBottom_Hover = ColorCache.getInstance().getColor(255, 243, 173); // 27 px high total
	private Color mBorderInnerSideBottomTop_Hover = ColorCache.getInstance().getColor(255, 243, 170);
	private Color mBorderInnerSideBottomBottom_Hover = ColorCache.getInstance().getColor(255, 243, 93); // 12 px high total	
	private Color mBorderInnerBottomCorner_Hover = ColorCache.getInstance().getColor(255, 247, 146);
	private Color mBorderInnerBottomMidway_Hover = ColorCache.getInstance().getColor(255, 254, 247);
	
	private Color mInnerTop_Hover = ColorCache.getInstance().getColor(255, 253, 220);
	private Color mInnerTopBottom_Hover = ColorCache.getInstance().getColor(255, 231, 143);
	private Color mInnerBottomTop_Hover = ColorCache.getInstance().getColor(255, 215, 92);
	private Color mInnerBottomBottom_Hover = ColorCache.getInstance().getColor(255, 233, 164);
	
	// TEXT COLOR
	private Color mButtonTextColor = ColorCache.getInstance().getColor(21, 66, 139);
	
	// SELECTED COLORS
	private Color mBorderTop_Selected = ColorCache.getInstance().getColor(142, 129, 101);
	
	private Color mBorderSideTopTop_Selected = ColorCache.getInstance().getColor(142, 129, 101);
	private Color mBorderSideTopBottom_Selected = ColorCache.getInstance().getColor(142, 129, 101); // 24 px high total
	private Color mBorderSideMidTop_Selected = ColorCache.getInstance().getColor(142, 129, 101); 
	private Color mBorderSideMidBottom_Selected = ColorCache.getInstance().getColor(166, 155, 133); // 27 px high total
	private Color mBorderSideBottomTop_Selected = ColorCache.getInstance().getColor(168, 158, 136);
	private Color mBorderSideBottomBottom_Selected = ColorCache.getInstance().getColor(198, 192, 178); // 11 px high total
	
	private Color mCornerFadeTopPixel_Selected = ColorCache.getInstance().getColor(181, 167, 145);
	private Color mCornerFadeBottomPixel_Selected = ColorCache.getInstance().getColor(247, 229, 168);
	private Color mCornerFadeBottomInsidePixel_Selected = ColorCache.getInstance().getColor(254, 216, 93);
	
	private Color mBorderInnerSideTopTop_Selected = ColorCache.getInstance().getColor(243, 186, 95);
	private Color mBorderInnerSideTopBottom_Selected = ColorCache.getInstance().getColor(250, 195, 93); // 23 px high total
	private Color mBorderInnerSideMidTop_Selected = ColorCache.getInstance().getColor(248, 190, 81); 
	private Color mBorderInnerSideMidBottom_Selected = ColorCache.getInstance().getColor(253, 208, 73); // 27 px high total
	private Color mBorderInnerSideBottomTop_Selected = ColorCache.getInstance().getColor(253, 208, 70);
	private Color mBorderInnerSideBottomBottom_Selected = ColorCache.getInstance().getColor(255, 208, 49); // 11 px high total
	
	private Color mBorderInnerBottomCorner_Selected = ColorCache.getInstance().getColor(255, 207, 45);
	private Color mBorderInnerBottomMidway_Selected = ColorCache.getInstance().getColor(255, 233, 160);
	
	private Color mBorderTopInner_Selected = ColorCache.getInstance().getColor(182, 154, 120);
	private Color mBorderTopInnerTwo_Selected = ColorCache.getInstance().getColor(224, 182, 136);
	private Color mBorderTopInnerThree_Selected = ColorCache.getInstance().getColor(245, 201, 154);

	private Color mInnerTop_Selected = ColorCache.getInstance().getColor(253, 212, 168);
	private Color mInnerTopBottom_Selected = ColorCache.getInstance().getColor(251, 173, 95);
	private Color mInnerMidTop_Selected = ColorCache.getInstance().getColor(249, 148, 46);
	private Color mInnerMidBottom_Selected = ColorCache.getInstance().getColor(252, 226, 147);
	private Color mInnerBottomTop_Selected = ColorCache.getInstance().getColor(252, 230, 154);
	private Color mInnerBottomBottom_Selected = ColorCache.getInstance().getColor(253, 241, 176);

	// SELECTED AND HOVER
	private Color mBorderTop_SelectedHover = ColorCache.getInstance().getColor(142, 129, 101);
	
	private Color mBorderSideTopTop_SelectedHover = ColorCache.getInstance().getColor(142, 129, 101);
	private Color mBorderSideTopBottom_SelectedHover = ColorCache.getInstance().getColor(142, 129, 101); // 24 px high total
	private Color mBorderSideMidTop_SelectedHover = ColorCache.getInstance().getColor(142, 129, 101); 
	private Color mBorderSideMidBottom_SelectedHover = ColorCache.getInstance().getColor(166, 155, 133); // 27 px high total
	private Color mBorderSideBottomTop_SelectedHover = ColorCache.getInstance().getColor(168, 158, 136);
	private Color mBorderSideBottomBottom_SelectedHover = ColorCache.getInstance().getColor(198, 192, 178); // 11 px high total
	
	private Color mCornerFadeTopPixel_SelectedHover = ColorCache.getInstance().getColor(151, 137, 109);
	private Color mCornerFadeBottomPixel_SelectedHover = ColorCache.getInstance().getColor(204, 190, 165);
	private Color mCornerFadeBottomPixelCenter_SelectedHover = ColorCache.getInstance().getColor(225, 194, 113);
	
	private Color mBorderInnerSideTopTop_SelectedHover = ColorCache.getInstance().getColor(225, 153, 47);
	private Color mBorderInnerSideTopBottom_SelectedHover = ColorCache.getInstance().getColor(253, 226, 178); // 23 px high total
	private Color mBorderInnerSideMidTop_SelectedHover = ColorCache.getInstance().getColor(252, 226, 181); 
	private Color mBorderInnerSideMidBottom_SelectedHover = ColorCache.getInstance().getColor(254, 229, 160); // 27 px high total
	private Color mBorderInnerSideBottomTop_SelectedHover = ColorCache.getInstance().getColor(254, 228, 155);
	private Color mBorderInnerSideBottomBottom_SelectedHover = ColorCache.getInstance().getColor(255, 223, 113); // 11 px high total
	
	private Color mBorderBottom_SelectedHover = ColorCache.getInstance().getColor(212, 197, 173);
	
	private Color mBorderInnerBottomCorner_SelectedHover = ColorCache.getInstance().getColor(255, 207, 44);
	private Color mBorderInnerBottomMidway_SelectedHover = ColorCache.getInstance().getColor(255, 233, 160);
	
	private Color mBorderTopInner_SelectedHover = ColorCache.getInstance().getColor(168, 136, 94);
	private Color mBorderTopInnerTwo_SelectedHover = ColorCache.getInstance().getColor(209, 155, 101);
	private Color mBorderTopInnerThree_SelectedHover = ColorCache.getInstance().getColor(233, 169, 107);

	private Color mInnerTop_SelectedHover = ColorCache.getInstance().getColor(233, 169, 107);
	private Color mInnerTopBottom_SelectedHover = ColorCache.getInstance().getColor(227, 139, 78);
	private Color mInnerMidTop_SelectedHover = ColorCache.getInstance().getColor(229, 130, 50);
	private Color mInnerMidBottom_SelectedHover = ColorCache.getInstance().getColor(252, 203, 96);
	private Color mInnerBottomTop_SelectedHover = ColorCache.getInstance().getColor(249, 197, 94);
	private Color mInnerBottomBottom_SelectedHover = ColorCache.getInstance().getColor(252, 207, 100);
	
	// SMALL BUTTONS
	// HOVER
	private Color mSideTopTopSmall_Hover = ColorCache.getInstance().getColor(216, 202, 150);
	private Color mSideTopBottomSmall_Hover = ColorCache.getInstance().getColor(185, 160, 116);
	private Color mSideBottomTopSmall_Hover = ColorCache.getInstance().getColor(183, 158, 115);
	private Color mSideBottomBottomSmall_Hover = ColorCache.getInstance().getColor(184, 169, 142);
	private Color mBottomSmallMid_Hover = ColorCache.getInstance().getColor(203, 196, 170);
	private Color mBorderSmallSideBottomTop_Hover = ColorCache.getInstance().getColor(255, 242, 201);
	private Color mBorderSmallSideBottomBottom_Hover = ColorCache.getInstance().getColor(255, 246, 185); 
	private Color mBorderSmallInsidePixelTop_Hover = ColorCache.getInstance().getColor(233, 223, 159);
	private Color mBorderSmallInsidePixelBottom_Hover = ColorCache.getInstance().getColor(233, 219, 177);
	
	// SELECTED
	private Color mSmallTop_Selected = ColorCache.getInstance().getColor(167, 142, 102);
	private Color mSmallTopInner_Selected = ColorCache.getInstance().getColor(203, 180, 153);
	private Color mSmallTopInnerTwo_Selected = ColorCache.getInstance().getColor(234, 209, 178);
	private Color mSmallLeftSide_Selected = ColorCache.getInstance().getColor(158, 130, 85);
	private Color mSmallRightSide_Selected = mSmallTop_Selected;
	private Color mBottomSmallMid_Selected = ColorCache.getInstance().getColor(204, 197, 172);

	private Color mSmallFillTopTop_Selected = ColorCache.getInstance().getColor(248, 218, 183);
	private Color mSmallFillTopBottom_Selected = ColorCache.getInstance().getColor(254, 211, 139);
	private Color mSmallFillBottomTop_Selected = ColorCache.getInstance().getColor(253, 194, 92);
	private Color mSmallFillBottomBottom_Selected = ColorCache.getInstance().getColor(253, 234, 156);
	
	private Color mSideBottomBottomSmall_Selected = ColorCache.getInstance().getColor(184, 169, 142);
	private Color mBorderSmallSideTopTop_Selected = ColorCache.getInstance().getColor(239, 211, 176);
	private Color mBorderSmallSideTopBottom_Selected = ColorCache.getInstance().getColor(241, 178, 90); 
	private Color mBorderSmallSideBottomTop_Selected = ColorCache.getInstance().getColor(239, 172, 72);
	private Color mBorderSmallSideBottomBottom_Selected = ColorCache.getInstance().getColor(230, 177, 82); 
	private Color mBorderSmallInsidePixelTop_Selected = ColorCache.getInstance().getColor(178, 154, 120);
	private Color mBorderSmallInsidePixelBottom_Selected = ColorCache.getInstance().getColor(200, 163, 104);

	// SELECTED AND HOVER
	private Color mSmallTop_SelectedHover = ColorCache.getInstance().getColor(158, 130, 85);
	private Color mSmallTopInner_SelectedHover = ColorCache.getInstance().getColor(189, 140, 70);
	private Color mSmallTopInnerTwo_SelectedHover = ColorCache.getInstance().getColor(222, 164, 87);
	private Color mSmallLeftSide_SelectedHover = mSmallTop_SelectedHover;
	private Color mSmallRightSide_SelectedHover = mSmallTop_SelectedHover;
	private Color mBottomSmallMid_SelectedHover = ColorCache.getInstance().getColor(204, 197, 172);

	private Color mBottomSmallInnerSide_SelectedHover = ColorCache.getInstance().getColor(255, 173, 58);
	private Color mBottomSmallInnerMid_SelectedHover = ColorCache.getInstance().getColor(255, 201, 124);

	private Color mSmallFillTopTop_SelectedHover = ColorCache.getInstance().getColor(243, 179, 101);
	private Color mSmallFillTopBottom_SelectedHover = ColorCache.getInstance().getColor(251, 173, 89);
	private Color mSmallFillBottomTop_SelectedHover = ColorCache.getInstance().getColor(250, 156, 47);
	private Color mSmallFillBottomBottom_SelectedHover = ColorCache.getInstance().getColor(252, 182, 16);
	
	private Color mSideBottomBottomSmall_SelectedHover = ColorCache.getInstance().getColor(184, 169, 142);
	private Color mBorderSmallSideTopTop_SelectedHover = ColorCache.getInstance().getColor(231, 174, 95);
	private Color mBorderSmallSideTopBottom_SelectedHover = ColorCache.getInstance().getColor(250, 209, 135); 
	private Color mBorderSmallSideBottomTop_SelectedHover = ColorCache.getInstance().getColor(250, 203, 123);
	private Color mBorderSmallSideBottomBottom_SelectedHover = ColorCache.getInstance().getColor(254, 174, 56); 
	private Color mBorderSmallInsidePixelTop_SelectedHover = ColorCache.getInstance().getColor(158, 130, 85);
	private Color mBorderSmallInsidePixelTopTwo_SelectedHover = ColorCache.getInstance().getColor(208, 156, 84);
	private Color mBorderSmallInsidePixelBottom_SelectedHover = ColorCache.getInstance().getColor(216, 168, 97);
	
	// ARROW
	private Color mArrowColor = ColorCache.getInstance().getColor(86, 125, 177);
	private Color mArrowColorShadow = ColorCache.getInstance().getColor(234, 242, 249);
	private Color mArrowColor_Disabled = ColorCache.getInstance().getColor(183, 183, 183);
	private Color mArrowColorShadow_Disabled = ColorCache.getInstance().getColor(237, 237, 237);

	// SEPARATOR 
	private Color mSeparatorColor = ColorCache.getInstance().getColor(251, 252, 254);
	private Color mSeparatorColorShadow = ColorCache.getInstance().getColor(150, 180, 218);
	
	// DISABLED TEXT
	private Color mTextColor_Disabled = ColorCache.getInstance().getColor(165, 141, 159);
	private Color mTextFadedRight_Disabled = ColorCache.getInstance().getColor(178, 214, 241);
	private Color mTextFadedLeft_Disabled = ColorCache.getInstance().getColor(211, 226, 210);
	
	public void drawButton(GC gc, AbstractRibbonGroupItem item) {
		int x = item.getX();
		int y = item.getBounds().y;
		int width = item.getWidth();
		int yMax = RibbonTabComposite.GROUP_HEIGHT;
		yMax -= 3;
		yMax -= RibbonGroup.BUTTON_BOT_HEIGHT;
		int imgTopSpacer = 5;

		if (item instanceof RibbonGroupSeparator) {
			RibbonGroupSeparator rgs = (RibbonGroupSeparator) item;
			Rectangle rgsBounds = rgs.getBounds();
			drawSeparator(gc, rgsBounds.x, rgsBounds.y, rgsBounds.height);
		}
		else if (item instanceof RibbonButton) {
			if (item.isEnabled() && (item.getStyle() & AbstractRibbonGroupItem.STYLE_ARROW_DOWN_SPLIT) != 0) {
				drawSplitButton(gc, item);
				return;
			}
				
			if (item.isEnabled()) {
        		if (item.isHoverButton() && !item.isSelected()) {
        			// top two lines
        			gc.setForeground(mBorderTop_Hover);
        			gc.drawLine(x+1, y, x+width-2, y);
        			gc.setForeground(mBorderInnerTop_Hover);
        			gc.drawLine(x+1, y+1, x+width-2, y+1);			
        
        			// pixels in corners
        			gc.setForeground(mCornerFadeTopPixel_Hover);
        			gc.drawLine(x+1, y+1, x+1, y+1);
        			gc.drawLine(x+width-2, y+1, x+width-2, y+1);
        			
        			// sides are 3 steps we split them a bit more due to more gradients
        			// note that outer borders stretch 1px higher and lower in the top and bottom sections, but mid stays the same			
        
        			// outer sides 
        			gc.setForeground(mBorderSideTopTop_Hover);
        			gc.setBackground(mBorderSideTopBottom_Hover);
        			gc.fillGradientRectangle(x, y+1, 1, 24, true);
        			gc.fillGradientRectangle(x+width-1, y+1, 1, 24, true);
        			// inner sides 
        			gc.setForeground(mBorderInnerSideTopTop_Hover);
        			gc.setBackground(mBorderInnerSideTopBottom_Hover);
        			gc.fillGradientRectangle(x+1, y+2, 1, 23, true);
        			gc.fillGradientRectangle(x+width-2, y+2, 1, 23, true);
        			// outer mid
        			gc.setForeground(mBorderSideMidTop_Hover);
        			gc.setBackground(mBorderSideMidBottom_Hover);
        			gc.fillGradientRectangle(x, y+2+23, 1, 27, true);
        			gc.fillGradientRectangle(x+width-1, y+2+23, 1, 27, true);
        			// inner mid
        			gc.setForeground(mBorderInnerSideMidTop_Hover);
        			gc.setBackground(mBorderInnerSideMidBottom_Hover);
        			gc.fillGradientRectangle(x+1, y+2+23, 1, 27, true);
        			gc.fillGradientRectangle(x+width-2, y+2+23, 1, 27, true);
        			// outer bottom 
        			gc.setForeground(mBorderInnerSideBottomTop_Hover);
        			gc.setBackground(mBorderInnerSideBottomBottom_Hover);
        			gc.fillGradientRectangle(x+1, y+2+23+27, 1, 12, true);
        			gc.fillGradientRectangle(x+width-2, y+2+23+27, 1, 12, true);			
        			// inner bottom 
        			gc.setForeground(mBorderSideBottomTop_Hover);
        			gc.setBackground(mBorderSideBottomBottom_Hover);
        			gc.fillGradientRectangle(x, y+2+23+27, 1, 13, true);
        			gc.fillGradientRectangle(x+width-1, y+2+23+27, 1, 13, true);			
        			// pixel is just below so we do that now too
        			gc.setForeground(mCornerFadeBottomPixel_Hover);
        			gc.drawLine(x+1, y+2+23+27+12, x+1, y+2+23+27+12);
        			gc.drawLine(x+width-2, y+2+23+27+12, x+width-2, y+2+23+27+12);
        			
        			// draw bottom, outer is plain, inner is a radial gradient with highlight at width/2
        			gc.setForeground(mBorderBottom_Hover);
        			gc.drawLine(x+1, y+2+23+27+12+1, x+width-2, y+2+23+27+12+1);
        			// inner, left half
        			gc.setForeground(mBorderInnerBottomCorner_Hover);
        			gc.setBackground(mBorderInnerBottomMidway_Hover);
        			gc.fillGradientRectangle(x+2, y+2+23+27+12, width/2, 1, false);
        			// inner, right half
        			gc.setForeground(mBorderInnerBottomMidway_Hover);
        			gc.setBackground(mBorderInnerBottomCorner_Hover);
        			gc.fillGradientRectangle(x+2+(width/2), y+2+23+27+12, (width/2)-3, 1, false);
        			
        			// borders are all down, now we fill the center, which is the easy part compared
        			gc.setForeground(mInnerTop_Hover);
        			gc.setBackground(mInnerTopBottom_Hover);
        			gc.fillGradientRectangle(x+2, y+2, width-4, 23, true);
        
        			gc.setForeground(mInnerBottomTop_Hover);
        			gc.setBackground(mInnerBottomBottom_Hover);
        			gc.fillGradientRectangle(x+2, y+2+23, width-4, 39, true);
        			    		
        		}
        		else 
        		if (item.isSelected() && !item.isHoverButton()) {
        			// let's draw the outer box, start with top, sides, then bottom			
            		// top
            		gc.setForeground(mBorderTop_Selected);
            		gc.drawLine(x+1, TOP_SPACING, x+width-2, TOP_SPACING);
            		gc.setForeground(mBorderTopInner_Selected);
            		gc.drawLine(x+2, TOP_SPACING+1, x+width-3, TOP_SPACING+1);
            		gc.setForeground(mBorderTopInnerTwo_Selected);
            		gc.drawLine(x+2, TOP_SPACING+2, x+width-3, TOP_SPACING+2);
            		gc.setForeground(mBorderTopInnerThree_Selected);
            		gc.drawLine(x+2, TOP_SPACING+3, x+width-3, TOP_SPACING+3);
            		
            		// outer sides 
        			gc.setForeground(mBorderSideTopTop_Selected);
        			gc.setBackground(mBorderSideTopBottom_Selected);
        			gc.fillGradientRectangle(x, y+1, 1, 24, true);
        			gc.fillGradientRectangle(x+width-1, y+1, 1, 24, true);
        			// inner sides 
        			gc.setForeground(mBorderInnerSideTopTop_Selected);
        			gc.setBackground(mBorderInnerSideTopBottom_Selected);
        			gc.fillGradientRectangle(x+1, y+2, 1, 23, true);
        			gc.fillGradientRectangle(x+width-2, y+2, 1, 23, true);
        			// outer mid
        			gc.setForeground(mBorderSideMidTop_Selected);
        			gc.setBackground(mBorderSideMidBottom_Selected);
        			gc.fillGradientRectangle(x, y+2+23, 1, 27, true);
        			gc.fillGradientRectangle(x+width-1, y+2+23, 1, 27, true);
        			// inner mid
        			gc.setForeground(mBorderInnerSideMidTop_Selected);
        			gc.setBackground(mBorderInnerSideMidBottom_Selected);
        			gc.fillGradientRectangle(x+1, y+2+23, 1, 27, true);
        			gc.fillGradientRectangle(x+width-2, y+2+23, 1, 27, true);
        			// outer bottom 
        			gc.setForeground(mBorderInnerSideBottomTop_Selected);
        			gc.setBackground(mBorderInnerSideBottomBottom_Selected);
        			gc.fillGradientRectangle(x+1, y+2+23+27, 1, 12, true);
        			gc.fillGradientRectangle(x+width-2, y+2+23+27, 1, 12, true);			
        			// inner bottom 
        			gc.setForeground(mBorderSideBottomTop_Selected);
        			gc.setBackground(mBorderSideBottomBottom_Selected);
        			gc.fillGradientRectangle(x, y+2+23+27, 1, 12, true);
        			gc.fillGradientRectangle(x+width-1, y+2+23+27, 1, 12, true);		
            		    		
            		// draw pixel at top
            		gc.setForeground(mCornerFadeTopPixel_Selected);
            		gc.drawLine(x+1, TOP_SPACING+1, x+1, TOP_SPACING+1);
            		gc.drawLine(x+width-2, TOP_SPACING+1, x+width-2, TOP_SPACING+1);
            		// draw bottom pixel
            		gc.setForeground(mCornerFadeBottomPixel_Selected);
            		gc.drawLine(x, y+2+23+27+12, x, y+2+23+27+12);
            		gc.drawLine(x+width-1, y+2+23+27+12, x+width-1, y+2+23+27+12);
            		
            		// fills
            		gc.setForeground(mInnerTop_Selected);
            		gc.setBackground(mInnerTopBottom_Selected);
            		gc.fillGradientRectangle(x+2, y+4, width-4, 22, true);
            		
            		gc.setForeground(mInnerMidTop_Selected);
            		gc.setBackground(mInnerMidBottom_Selected);
            		gc.fillGradientRectangle(x+2, y+4+22, width-4, 26, true);
            		
            		gc.setForeground(mInnerBottomTop_Selected);
            		gc.setBackground(mInnerBottomBottom_Selected);
            		gc.fillGradientRectangle(x+2, y+4+22+26, width-4, 12, true);
            		
            		gc.setForeground(mInnerBottomBottom_Selected);
            		gc.drawLine(x+3, y+4+22+26+12, x+width-4, y+4+22+26+12);
            		
            		// bottom border
            		gc.setForeground(mBorderInnerBottomCorner_Selected);
            		gc.setBackground(mBorderInnerBottomMidway_Selected);
            		gc.fillGradientRectangle(x+1, y+4+22+26+12+1, (width/2)-2, 1, false);
            		gc.setForeground(mBorderInnerBottomMidway_Selected);
            		gc.setBackground(mBorderInnerBottomCorner_Selected);
            		gc.fillGradientRectangle(x+1+(width/2)-2, y+4+22+26+12+1, (width/2)+1, 1, false);
            		
            		// more pixels
            		gc.setForeground(mCornerFadeBottomInsidePixel_Selected);
            		gc.drawLine(x+2, y+4+22+26+12, x+2, y+4+22+26+12);
            		gc.drawLine(x+width-3, y+4+22+26+12, x+width-3, y+4+22+26+12);
            		gc.setForeground(mBorderInnerSideBottomBottom_Selected);
            		gc.drawLine(x+1, y+4+22+26+12, x+1, y+4+22+26+12);
            		gc.drawLine(x+width-2, y+4+22+26+12, x+width-2, y+4+22+26+12);
        		}
        		else if (item.isSelected() && item.isHoverButton()) {
        			// let's draw the outer box, start with top, sides, then bottom			
            		// top
            		gc.setForeground(mBorderTop_SelectedHover);
            		gc.drawLine(x+1, TOP_SPACING, x+width-2, TOP_SPACING);
            		gc.setForeground(mBorderTopInner_SelectedHover);
            		gc.drawLine(x+2, TOP_SPACING+1, x+width-3, TOP_SPACING+1);
            		gc.setForeground(mBorderTopInnerTwo_SelectedHover);
            		gc.drawLine(x+2, TOP_SPACING+2, x+width-3, TOP_SPACING+2);
            		gc.setForeground(mBorderTopInnerThree_SelectedHover);
            		gc.drawLine(x+2, TOP_SPACING+3, x+width-3, TOP_SPACING+3);
            		
            		// outer sides 
        			gc.setForeground(mBorderSideTopTop_SelectedHover);
        			gc.setBackground(mBorderSideTopBottom_SelectedHover);
        			gc.fillGradientRectangle(x, y+1, 1, 24, true);
        			gc.fillGradientRectangle(x+width-1, y+1, 1, 24, true);
        			// inner sides 
        			gc.setForeground(mBorderInnerSideTopTop_SelectedHover);
        			gc.setBackground(mBorderInnerSideTopBottom_SelectedHover);
        			gc.fillGradientRectangle(x+1, y+2, 1, 23, true);
        			gc.fillGradientRectangle(x+width-2, y+2, 1, 23, true);
        			// outer mid
        			gc.setForeground(mBorderSideMidTop_SelectedHover);
        			gc.setBackground(mBorderSideMidBottom_SelectedHover);
        			gc.fillGradientRectangle(x, y+2+23, 1, 27, true);
        			gc.fillGradientRectangle(x+width-1, y+2+23, 1, 27, true);
        			// inner mid
        			gc.setForeground(mBorderInnerSideMidTop_SelectedHover);
        			gc.setBackground(mBorderInnerSideMidBottom_SelectedHover);
        			gc.fillGradientRectangle(x+1, y+2+23, 1, 27, true);
        			gc.fillGradientRectangle(x+width-2, y+2+23, 1, 27, true);
        			// outer bottom 
        			gc.setForeground(mBorderInnerSideBottomTop_SelectedHover);
        			gc.setBackground(mBorderInnerSideBottomBottom_SelectedHover);
        			gc.fillGradientRectangle(x+1, y+2+23+27, 1, 12, true);
        			gc.fillGradientRectangle(x+width-2, y+2+23+27, 1, 12, true);			
        			// inner bottom 
        			gc.setForeground(mBorderSideBottomTop_SelectedHover);
        			gc.setBackground(mBorderSideBottomBottom_SelectedHover);
        			gc.fillGradientRectangle(x, y+2+23+27, 1, 12, true);
        			gc.fillGradientRectangle(x+width-1, y+2+23+27, 1, 12, true);		
            		    		
            		// draw pixel at top
            		gc.setForeground(mCornerFadeTopPixel_SelectedHover);
            		gc.drawLine(x+1, TOP_SPACING+1, x+1, TOP_SPACING+1);
            		gc.drawLine(x+width-2, TOP_SPACING+1, x+width-2, TOP_SPACING+1);
            		// draw bottom pixel
            		gc.setForeground(mCornerFadeBottomPixel_SelectedHover);
            		gc.drawLine(x, y+2+23+27+12, x, y+2+23+27+12);
            		gc.drawLine(x+width-1, y+2+23+27+12, x+width-1, y+2+23+27+12);
            		
            		// fills
            		gc.setForeground(mInnerTop_SelectedHover);
            		gc.setBackground(mInnerTopBottom_SelectedHover);
            		gc.fillGradientRectangle(x+2, y+4, width-4, 22, true);
            		
            		gc.setForeground(mInnerMidTop_SelectedHover);
            		gc.setBackground(mInnerMidBottom_SelectedHover);
            		gc.fillGradientRectangle(x+2, y+4+22, width-4, 26, true);
            		
            		gc.setForeground(mInnerBottomTop_SelectedHover);
            		gc.setBackground(mInnerBottomBottom_SelectedHover);
            		gc.fillGradientRectangle(x+2, y+4+22+26, width-4, 12, true);
            		
            		gc.setForeground(mInnerBottomBottom_SelectedHover);
            		gc.drawLine(x+3, y+4+22+26+12, x+width-4, y+4+22+26+12);
            		
            		// bottom border
            		gc.setForeground(mBorderInnerBottomCorner_SelectedHover);
            		gc.setBackground(mBorderInnerBottomMidway_SelectedHover);
            		gc.fillGradientRectangle(x+1, y+4+22+26+12, (width/2)-2, 1, false);
            		gc.setForeground(mBorderInnerBottomMidway_SelectedHover);
            		gc.setBackground(mBorderInnerBottomCorner_SelectedHover);
            		gc.fillGradientRectangle(x+1+(width/2)-2, y+4+22+26+12, (width/2)+1, 1, false);
            		
            		// bottom dark
            		gc.setForeground(mBorderBottom_SelectedHover);
            		gc.drawLine(x+1, y+4+22+26+12+1, x+width-2, y+4+22+26+12+1);
            		
            		// more pixels
            		gc.setForeground(mCornerFadeBottomPixelCenter_SelectedHover);
            		gc.drawLine(x+1, y+4+22+26+12, x+1, y+4+22+26+12);
            		gc.drawLine(x+width-2, y+4+22+26+12, x+width-2, y+4+22+26+12);
        		}        		
			}
			
			Image toDraw = item.getImage();
			// fallback is normal image
			if (!item.isEnabled())
				toDraw = item.getDisabledImage() == null ? toDraw : item.getDisabledImage();
			
			// draw image
    		if (toDraw != null) {
    			Rectangle imBounds = toDraw.getBounds();
    			int maxHeight = 32;
    			int horizAlignment = x+(width/2)-(imBounds.width/2);
    			switch (item.getImageVerticalAlignment()) {
    				default:
    				case SWT.TOP:
    				{
    					gc.drawImage(toDraw, horizAlignment, imgTopSpacer);
    					break;
    				}
    				case SWT.BOTTOM:
    				{
    					int botSpacer = imBounds.height-maxHeight; 
    					if (botSpacer < 0)
    						botSpacer = 0;
    					
    					botSpacer += imgTopSpacer;
    					botSpacer *= 2;
    										
    					gc.drawImage(toDraw, horizAlignment, botSpacer);
    					break;
    				}
    				case SWT.CENTER:
    				{
    					int botSpacer = Math.abs(imBounds.height-maxHeight); 
    					if (botSpacer < 0)
    						botSpacer = 0;
    					
    					if (botSpacer != 0)
    						botSpacer /= 2;
    
    					botSpacer += imgTopSpacer;
    										
    					gc.drawImage(toDraw, horizAlignment, botSpacer);
    					break;
    				}
    			}
    		}
    		
    		// draw text    		
    		if (item.getName() != null) {
    			if (item.isEnabled()) {
        			gc.setForeground(mButtonTextColor);
        			gc.drawText(item.getName(), x+3, 40, true);
    			}
    			else {
    				gc.setForeground(mTextFadedRight_Disabled);
        			gc.drawText(item.getName(), x+4, 40, true);
    				gc.setForeground(mTextFadedLeft_Disabled);
        			gc.drawText(item.getName(), x+2, 40, true);
    				gc.setForeground(mTextColor_Disabled);
        			gc.drawText(item.getName(), x+3, 40, true);
    			}
    		}			
		}
		else if (item instanceof RibbonButtonGroup) {
			RibbonButtonGroup rbg = (RibbonButtonGroup) item;
			List<AbstractRibbonGroupItem> buttons = rbg.getButtons();
			
			int curY = y;
			int curImgY = curY + imgTopSpacer - 2;
//			int curX = x;
			
			if (buttons != null) {
				int maxImgWidth = 0;
				for (int i = 0; i < buttons.size(); i++) {
					AbstractRibbonGroupItem button = buttons.get(i);
					Image toUse = button.getImage(); 
					if (!button.isEnabled())
						toUse = button.getDisabledImage() == null ? toUse : button.getDisabledImage();
					
					if (toUse != null)
						maxImgWidth = Math.max(maxImgWidth, toUse.getBounds().width);
				}
				
				for (int i = 0; i < buttons.size(); i++) {
					AbstractRibbonGroupItem button = buttons.get(i);

					if ((button.getStyle() & RibbonButton.STYLE_ARROW_DOWN_SPLIT) != 0)
						drawSmallSplitButton(gc, button, maxImgWidth);
					else						
						drawSmallButton(gc, button, maxImgWidth);
					
					curY += 22;
					curImgY += 22;
				}
			}
		}
	}
	
	private void drawSmallSplitButton(GC gc, AbstractRibbonGroupItem button, int maxImgWidth) {
		int x = button.getX();
		int y = button.getBounds().y;
		int yMax = RibbonTabComposite.GROUP_HEIGHT;
		yMax -= 3;
		yMax -= RibbonGroup.BUTTON_BOT_HEIGHT;
		int imgTopSpacer = 5;

		Rectangle bounds = button.getBounds();					
		int bx = bounds.x;
		int by = bounds.y;
		int bw = bounds.width;
		int bh = bounds.height;
									
		int curY = y;
		int curImgY = curY + imgTopSpacer - 2;
		int curX = x;
		
		// TODO: Setbounds on buttons seems off, and half is obviously 1 px off too, half is fine, but pick end pixel differently
		if (button.isEnabled()) {
			if (!button.isSelected() && button.isHoverButton()) {
				gc.setForeground(mBorderTop_Hover);
    			gc.drawLine(bx+1, by, bx+bw-2, by);
    			gc.setForeground(mBorderInnerTop_Hover);
    			gc.drawLine(bx+1, by+1, bx+bw-2, by+1);			
    			
    			gc.setForeground(mSideTopTopSmall_Hover);
    			gc.setBackground(mSideTopBottomSmall_Hover);
    			gc.fillGradientRectangle(bx, by+1, 1, 10, true); // left
    			gc.fillGradientRectangle(bx+bw-1, by+1, 1, 10, true); // right
    			gc.setForeground(mSideBottomTopSmall_Hover);
    			gc.setBackground(mSideBottomBottomSmall_Hover);
    			gc.fillGradientRectangle(bx, by+1+10, 1, 10, true); // left
    			gc.fillGradientRectangle(bx+bw-1, by+1+10, 1, 10, true); // right
    			
    			gc.setForeground(mSideBottomBottomSmall_Hover);
    			gc.setBackground(mBottomSmallMid_Hover);
    			int half = (bw-2)/2;
    			gc.fillGradientRectangle(bx+1, by+21, half, 1, false);
    			// add -1 and start on the right so that if the half value is 1px off, it's ok as middle is highlight anyway
    			gc.fillGradientRectangle(bx+bw-1, by+21, -half-1, 1, false);
    			
    			// fill
    			if (button.isLeftHovered()) {
    				int width = button.getLeftBounds().width;
    				// fill left
    				gc.setForeground(mInnerTopPartTopTopSplitSmall);
    				gc.setBackground(mInnerTopPartTopBottomSplitSmall);
    				gc.fillGradientRectangle(bx+2, by+2, width-1, 9, true);
    				gc.setForeground(mInnerTopPartTopBottomTopSplitSmall);
    				gc.setBackground(mInnerTopPartTopBottomBottomSplitSmall);
        			gc.fillGradientRectangle(bx+2, by+2+9, width-1, 9, true);    			
        			
        			// fill right
        			gc.setForeground(mInnerTop_Hover);
        			gc.setBackground(mInnerTopBottom_Hover);
        			gc.fillGradientRectangle(button.getRightBounds().x, by+2, button.getRightBounds().width, 9, true);
        			gc.setForeground(mInnerBottomTop_Hover);
        			gc.setBackground(mInnerBottomBottom_Hover);
        			gc.fillGradientRectangle(button.getRightBounds().x, by+2+9, button.getRightBounds().width, 9, true);        			
    			}
    			else {
    				// fill left
        			gc.setForeground(mInnerTop_Hover);
        			gc.setBackground(mInnerTopBottom_Hover);
        			gc.fillGradientRectangle(bx+2, by+2, bw-4, 9, true);
        			gc.setForeground(mInnerBottomTop_Hover);
        			gc.setBackground(mInnerBottomBottom_Hover);
        			gc.fillGradientRectangle(bx+2, by+2+9, bw-4, 9, true);
        			
        			// fill right
        			gc.setForeground(mInnerTopPartTopTopSplitSmall);
        			gc.setBackground(mInnerTopPartTopBottomSplitSmall);
        			gc.fillGradientRectangle(button.getRightBounds().x, by+2, button.getRightBounds().width, 9, true);
        			gc.setForeground(mInnerTopPartTopBottomTopSplitSmall);
        			gc.setBackground(mInnerTopPartTopBottomBottomSplitSmall);
        			gc.fillGradientRectangle(button.getRightBounds().x, by+2+9, button.getRightBounds().width, 9, true);        			
    			}    			

    			// inner borders
    			gc.setForeground(mBorderInnerSideTopTop_Hover);
    			gc.setBackground(mBorderInnerSideTopBottom_Hover);
    			gc.fillGradientRectangle(bx+1, by+2, 1, 9, true);
    			gc.fillGradientRectangle(bx+bw-2, by+2, 1, 9, true);
    			gc.setForeground(mBorderSmallSideBottomTop_Hover);
    			gc.setBackground(mBorderSmallSideBottomBottom_Hover);
    			gc.fillGradientRectangle(bx+1, by+2+9, 1, 9, true);
    			gc.fillGradientRectangle(bx+bw-2, by+2+9, 1, 9, true);
    			// bottom border
    			gc.setForeground(mBorderSmallSideBottomBottom_Hover);
    			gc.drawLine(bx+1, by+2+9+9, bx+bw-2, by+2+9+9);
    			
    			gc.setForeground(mBorderSmallInsidePixelTop_Hover);
    			gc.drawLine(bx+1, by+1, bx+1, by+1);
    			gc.drawLine(bx+bw-2, by+1, bx+bw-2, by+1);
    			gc.setForeground(mBorderSmallInsidePixelBottom_Hover);
    			gc.drawLine(bx+1, by+1+9+9+1, bx+1, by+1+9+9+1);
    			gc.drawLine(bx+bw-2, by+1+9+9+1, bx+bw-2, by+1+9+9+1);
    			
    			// draw divider
    			gc.setForeground(mInnerSplitSmallDividerTop);
    			gc.setBackground(mInnerSplitSmallDividerMid);    			
    			gc.fillGradientRectangle(button.getRightBounds().x, y+1, 1, (bh/2)-1, true);
    			gc.setForeground(mInnerSplitSmallDividerMid);
    			gc.setBackground(mInnerSplitSmallDividerTop);
    			gc.fillGradientRectangle(button.getRightBounds().x, y+(bh/2), 1, (bh/2)-1, true);
    			
			}
			else if (button.isSelected()) {				
				// draw hover border first
				gc.setForeground(mBorderTop_Hover);
    			gc.drawLine(bx+1, by, bx+bw-2, by);
    			gc.setForeground(mBorderInnerTop_Hover);
    			gc.drawLine(bx+1, by+1, bx+bw-2, by+1);			
    			
    			gc.setForeground(mSideTopTopSmall_Hover);
    			gc.setBackground(mSideTopBottomSmall_Hover);
    			gc.fillGradientRectangle(bx, by+1, 1, 10, true); // left
    			gc.fillGradientRectangle(bx+bw-1, by+1, 1, 10, true); // right
    			gc.setForeground(mSideBottomTopSmall_Hover);
    			gc.setBackground(mSideBottomBottomSmall_Hover);
    			gc.fillGradientRectangle(bx, by+1+10, 1, 10, true); // left
    			gc.fillGradientRectangle(bx+bw-1, by+1+10, 1, 10, true); // right
    			
    			gc.setForeground(mSideBottomBottomSmall_Hover);
    			gc.setBackground(mBottomSmallMid_Hover);
    			int halfx = (bw-2)/2;
    			gc.fillGradientRectangle(bx+1, by+21, halfx, 1, false);
    			// add -1 and start on the right so that if the half value is 1px off, it's ok as middle is highlight anyway
    			gc.fillGradientRectangle(bx+bw-1, by+21, -halfx-1, 1, false);
    			    			
				int xx = bx+1; 		// left x
				int xw = bx+bw-2;	// width								
				
				if (button.isLeftSelected()) {
					xw = button.getRightBounds().x;
				}
				else {
					xx = button.getRightBounds().x;
				}
				
				// draw outer
				gc.setForeground(mSmallTop_SelectedHover);
    			gc.drawLine(xx, by, xw, by);
    			gc.setForeground(mSmallTopInner_SelectedHover);
    			gc.drawLine(xx, by+1, xw, by+1);			
    			
    			gc.setForeground(mSmallLeftSide_SelectedHover);
    			gc.drawLine(xx-1, by+1, xx-1, by+1+19);
    			gc.setForeground(mSmallRightSide_SelectedHover);
    			gc.drawLine(xw+1, by+1, xw+1, by+1+19);

    			gc.setForeground(mSideBottomBottomSmall_SelectedHover);
    			gc.setBackground(mBottomSmallMid_SelectedHover);
    			int halfy = (bw-2)/2;
    			gc.fillGradientRectangle(xx, by+21, halfy, 1, false);
    			// add -1 and start on the right so that if the half value is 1px off, it's ok as middle is highlight anyway
    			gc.fillGradientRectangle(xw+1, by+21, -halfy-1, 1, false);

				
				gc.setForeground(mSmallTop_Selected);
    			gc.drawLine(xx, by, xw, by);
    			gc.setForeground(mSmallTopInner_Selected);
    			gc.drawLine(xx, by+1, xw, by+1);			
    			
    			gc.setForeground(mSmallLeftSide_Selected);
    			gc.drawLine(xx-1, by+1, xx-1, by+1+19);
    			gc.setForeground(mSmallRightSide_Selected);
    			gc.drawLine(xw+1, by+1, xw+1, by+1+19);

    			gc.setForeground(mSideBottomBottomSmall_Selected);
    			gc.setBackground(mBottomSmallMid_Selected);
    			int half = (bw-2)/2;
    			gc.fillGradientRectangle(xx, by+21, half, 1, false);
    			// add -1 and start on the right so that if the half value is 1px off, it's ok as middle is highlight anyway
    			gc.fillGradientRectangle(xw+1, by+21, -half-1, 1, false);
    			
    			// fill
    			if (button.isLeftSelected()) {
    				// left
        			gc.setForeground(mSmallFillTopTop_SelectedHover);
        			gc.setBackground(mSmallFillTopBottom_SelectedHover);
        			gc.fillGradientRectangle(bx+2, by+2, bw-4, 9, true);
        			gc.setForeground(mSmallFillBottomTop_SelectedHover);
        			gc.setBackground(mSmallFillBottomBottom_SelectedHover);
        			gc.fillGradientRectangle(bx+2, by+2+9, bw-4, 9, true);

        			// fill right
        			gc.setForeground(mInnerTopPartTopTopSplitSmall);
        			gc.setBackground(mInnerTopPartTopBottomSplitSmall);
        			gc.fillGradientRectangle(button.getRightBounds().x, by+2, button.getRightBounds().width, 9, true);
        			gc.setForeground(mInnerTopPartTopBottomTopSplitSmall);
        			gc.setBackground(mInnerTopPartTopBottomBottomSplitSmall);
        			gc.fillGradientRectangle(button.getRightBounds().x, by+2+9, button.getRightBounds().width, 9, true);       
    			}
    			else if (button.isRightSelected()) {
    				// fill left
        			gc.setForeground(mInnerTopPartTopTopSplitSmall);
        			gc.setBackground(mInnerTopPartTopBottomSplitSmall);
        			gc.fillGradientRectangle(bx+2, by+2, bw-4, 9, true);
        			gc.setForeground(mInnerTopPartTopBottomTopSplitSmall);
        			gc.setBackground(mInnerTopPartTopBottomBottomSplitSmall);
        			gc.fillGradientRectangle(bx+2, by+2+9, bw-4, 9, true);
        			
        			// fill right
        			gc.setForeground(mSmallFillTopTop_SelectedHover);
        			gc.setBackground(mSmallFillTopBottom_SelectedHover);
        			gc.fillGradientRectangle(button.getRightBounds().x, by+2, button.getRightBounds().width, 9, true);
        			gc.setForeground(mSmallFillBottomTop_SelectedHover);
        			gc.setBackground(mSmallFillBottomBottom_SelectedHover);
        			gc.fillGradientRectangle(button.getRightBounds().x, by+2+9, button.getRightBounds().width, 9, true);     
    			}    			
    			
    			// draw hover first
    			// inner borders
    			gc.setForeground(mBorderInnerSideTopTop_Hover);
    			gc.setBackground(mBorderInnerSideTopBottom_Hover);
    			gc.fillGradientRectangle(bx+1, by+2, 1, 9, true);
    			gc.fillGradientRectangle(bx+bw-2, by+2, 1, 9, true);
    			gc.setForeground(mBorderSmallSideBottomTop_Hover);
    			gc.setBackground(mBorderSmallSideBottomBottom_Hover);
    			gc.fillGradientRectangle(bx+1, by+2+9, 1, 9, true);
    			gc.fillGradientRectangle(bx+bw-2, by+2+9, 1, 9, true);
    			// bottom border
    			gc.setForeground(mBorderSmallSideBottomBottom_Hover);
    			gc.drawLine(bx+1, by+2+9+9, bx+bw-2, by+2+9+9);
    			
    			gc.setForeground(mBorderSmallInsidePixelTop_Hover);
    			gc.drawLine(bx+1, by+1, bx+1, by+1);
    			gc.drawLine(bx+bw-2, by+1, bx+bw-2, by+1);
    			gc.setForeground(mBorderSmallInsidePixelBottom_Hover);
    			gc.drawLine(bx+1, by+1+9+9+1, bx+1, by+1+9+9+1);
    			gc.drawLine(bx+bw-2, by+1+9+9+1, bx+bw-2, by+1+9+9+1);
    			// --- end
    			
    			// draw selected border second
    			// third to top line as below inner borders will overdraw on sides 
    			gc.setForeground(mSmallTopInnerTwo_SelectedHover);
    			gc.drawLine(xx, by+2, xw, by+2);			

    			// inner borders
    			gc.setForeground(mBorderSmallSideTopTop_SelectedHover);
    			gc.setBackground(mBorderSmallSideTopBottom_SelectedHover);
    			gc.fillGradientRectangle(xx, by+2, 1, 9, true);
    			gc.fillGradientRectangle(xw, by+2, 1, 9, true);
    			gc.setForeground(mBorderSmallSideBottomTop_SelectedHover);
    			gc.setBackground(mBorderSmallSideBottomBottom_SelectedHover);
    			gc.fillGradientRectangle(xx, by+2+9, 1, 9, true);
    			gc.fillGradientRectangle(xw, by+2+9, 1, 9, true);
    			// bottom border
    			gc.setForeground(mBorderSmallSideBottomBottom_SelectedHover);
    			gc.drawLine(xx, by+2+9+9, xw, by+2+9+9);
    			// inner bottom
    			gc.setForeground(mBottomSmallInnerSide_SelectedHover);
    			gc.setBackground(mBottomSmallInnerMid_SelectedHover);
    			gc.fillGradientRectangle(xx, by+20, half, 1, false);
    			// add -1 and start on the right so that if the half value is 1px off, it's ok as middle is highlight anyway
    			gc.fillGradientRectangle(xw+1, by+20, -half-1, 1, false);
    			
    			gc.setForeground(mBorderSmallInsidePixelTop_SelectedHover);
    			gc.drawLine(xx, by+1, xx, by+1);
    			gc.drawLine(xw, by+1, xw, by+1);
    			gc.setForeground(mBorderSmallInsidePixelTopTwo_SelectedHover);
    			gc.drawLine(xx, by+2, xx, by+2);
    			gc.drawLine(xw, by+2, xw, by+2);
    			gc.setForeground(mBorderSmallInsidePixelBottom_SelectedHover);
    			gc.drawLine(xx, by+1+9+9+1, xx, by+1+9+9+1);
    			gc.drawLine(xw, by+1+9+9+1, xw, by+1+9+9+1);
    			// --- end    			    		
			}
		}
			
		Image toUse = button.getImage(); 
		if (!button.isEnabled())
			toUse = button.getDisabledImage() == null ? toUse : button.getDisabledImage();

		// draw image
		if (toUse != null) 
			gc.drawImage(toUse, curX + 3, curImgY);
		
		//if ((button.getStyle() & RibbonButton.STYLE_ARROW_DOWN) != 0)
		drawArrow(gc, curX + bw-9, curImgY+6, button.isEnabled());

		// draw text
		if (button.getName() != null) {
			if (!button.isEnabled()) {
				gc.setForeground(mTextFadedRight_Disabled);
				gc.drawText(button.getName(), curX+5+maxImgWidth+3, curImgY, true);							
				gc.setForeground(mTextFadedLeft_Disabled);
				gc.drawText(button.getName(), curX+3+maxImgWidth+3, curImgY, true);							
				gc.setForeground(mTextColor_Disabled);
				gc.drawText(button.getName(), curX+4+maxImgWidth+3, curImgY, true);							
			}
			else {
				gc.setForeground(mButtonTextColor);
				gc.drawText(button.getName(), curX+4+maxImgWidth+3, curImgY, true);							
			}
		}
	}
	
	private void drawSmallButton(GC gc, AbstractRibbonGroupItem button, int maxImgWidth) {
		int x = button.getX();
		int y = button.getBounds().y;
		int yMax = RibbonTabComposite.GROUP_HEIGHT;
		yMax -= 3;
		yMax -= RibbonGroup.BUTTON_BOT_HEIGHT;
		int imgTopSpacer = 5;

		Rectangle bounds = button.getBounds();					
		int bx = bounds.x;
		int by = bounds.y;
		int bw = bounds.width;
									
		int curY = y;
		int curImgY = curY + imgTopSpacer - 2;
		int curX = x;
		
		// TODO: Setbounds on buttons seems off, and half is obviously 1 px off too, half is fine, but pick end pixel differently
		if (button.isEnabled()) {
			if (!button.isSelected() && button.isHoverButton()) {
				gc.setForeground(mBorderTop_Hover);
    			gc.drawLine(bx+1, by, bx+bw-2, by);
    			gc.setForeground(mBorderInnerTop_Hover);
    			gc.drawLine(bx+1, by+1, bx+bw-2, by+1);			
    			
    			gc.setForeground(mSideTopTopSmall_Hover);
    			gc.setBackground(mSideTopBottomSmall_Hover);
    			gc.fillGradientRectangle(bx, by+1, 1, 10, true); // left
    			gc.fillGradientRectangle(bx+bw-1, by+1, 1, 10, true); // right
    			gc.setForeground(mSideBottomTopSmall_Hover);
    			gc.setBackground(mSideBottomBottomSmall_Hover);
    			gc.fillGradientRectangle(bx, by+1+10, 1, 10, true); // left
    			gc.fillGradientRectangle(bx+bw-1, by+1+10, 1, 10, true); // right
    			
    			gc.setForeground(mSideBottomBottomSmall_Hover);
    			gc.setBackground(mBottomSmallMid_Hover);
    			int half = (bw-2)/2;
    			gc.fillGradientRectangle(bx+1, by+21, half, 1, false);
    			// add -1 and start on the right so that if the half value is 1px off, it's ok as middle is highlight anyway
    			gc.fillGradientRectangle(bx+bw-1, by+21, -half-1, 1, false);
    			
    			// fill
    			gc.setForeground(mInnerTop_Hover);
    			gc.setBackground(mInnerTopBottom_Hover);
    			gc.fillGradientRectangle(bx+2, by+2, bw-4, 9, true);
    			gc.setForeground(mInnerBottomTop_Hover);
    			gc.setBackground(mInnerBottomBottom_Hover);
    			gc.fillGradientRectangle(bx+2, by+2+9, bw-4, 9, true);

    			// inner borders
    			gc.setForeground(mBorderInnerSideTopTop_Hover);
    			gc.setBackground(mBorderInnerSideTopBottom_Hover);
    			gc.fillGradientRectangle(bx+1, by+2, 1, 9, true);
    			gc.fillGradientRectangle(bx+bw-2, by+2, 1, 9, true);
    			gc.setForeground(mBorderSmallSideBottomTop_Hover);
    			gc.setBackground(mBorderSmallSideBottomBottom_Hover);
    			gc.fillGradientRectangle(bx+1, by+2+9, 1, 9, true);
    			gc.fillGradientRectangle(bx+bw-2, by+2+9, 1, 9, true);
    			// bottom border
    			gc.setForeground(mBorderSmallSideBottomBottom_Hover);
    			gc.drawLine(bx+1, by+2+9+9, bx+bw-2, by+2+9+9);
    			
    			gc.setForeground(mBorderSmallInsidePixelTop_Hover);
    			gc.drawLine(bx+1, by+1, bx+1, by+1);
    			gc.drawLine(bx+bw-2, by+1, bx+bw-2, by+1);
    			gc.setForeground(mBorderSmallInsidePixelBottom_Hover);
    			gc.drawLine(bx+1, by+1+9+9+1, bx+1, by+1+9+9+1);
    			gc.drawLine(bx+bw-2, by+1+9+9+1, bx+bw-2, by+1+9+9+1);
    			
			}
			else if (!button.isHoverButton() && button.isSelected()) {
				gc.setForeground(mSmallTop_Selected);
    			gc.drawLine(bx+1, by, bx+bw-2, by);
    			gc.setForeground(mSmallTopInner_Selected);
    			gc.drawLine(bx+1, by+1, bx+bw-2, by+1);			
    			
    			gc.setForeground(mSmallLeftSide_Selected);
    			gc.drawLine(bx, by+1, bx, by+1+19);
    			gc.setForeground(mSmallRightSide_Selected);
    			gc.drawLine(bx+bw-1, by+1, bx+bw-1, by+1+19);

    			gc.setForeground(mSideBottomBottomSmall_Selected);
    			gc.setBackground(mBottomSmallMid_Selected);
    			int half = (bw-2)/2;
    			gc.fillGradientRectangle(bx+1, by+21, half, 1, false);
    			// add -1 and start on the right so that if the half value is 1px off, it's ok as middle is highlight anyway
    			gc.fillGradientRectangle(bx+bw-1, by+21, -half-1, 1, false);
    			
    			// fill
    			gc.setForeground(mSmallFillTopTop_Selected);
    			gc.setBackground(mSmallFillTopBottom_Selected);
    			gc.fillGradientRectangle(bx+2, by+2, bw-4, 9, true);
    			gc.setForeground(mSmallFillBottomTop_Selected);
    			gc.setBackground(mSmallFillBottomBottom_Selected);
    			gc.fillGradientRectangle(bx+2, by+2+9, bw-4, 9, true);

    			// third to top line as below inner borders will overdraw on sides 
    			gc.setForeground(mSmallTopInnerTwo_Selected);
    			gc.drawLine(bx+1, by+2, bx+bw-2, by+2);			

    			// inner borders
    			gc.setForeground(mBorderSmallSideTopTop_Selected);
    			gc.setBackground(mBorderSmallSideTopBottom_Selected);
    			gc.fillGradientRectangle(bx+1, by+2, 1, 9, true);
    			gc.fillGradientRectangle(bx+bw-2, by+2, 1, 9, true);
    			gc.setForeground(mBorderSmallSideBottomTop_Selected);
    			gc.setBackground(mBorderSmallSideBottomBottom_Selected);
    			gc.fillGradientRectangle(bx+1, by+2+9, 1, 9, true);
    			gc.fillGradientRectangle(bx+bw-2, by+2+9, 1, 9, true);
    			// bottom border
    			gc.setForeground(mBorderSmallSideBottomBottom_Selected);
    			gc.drawLine(bx+1, by+2+9+9, bx+bw-2, by+2+9+9);
    			
    			gc.setForeground(mBorderSmallInsidePixelTop_Selected);
    			gc.drawLine(bx+1, by+1, bx+1, by+1);
    			gc.drawLine(bx+bw-2, by+1, bx+bw-2, by+1);
    			gc.setForeground(mBorderSmallInsidePixelBottom_Selected);
    			gc.drawLine(bx+1, by+1+9+9+1, bx+1, by+1+9+9+1);
    			gc.drawLine(bx+bw-2, by+1+9+9+1, bx+bw-2, by+1+9+9+1);
    			

			}
			else if (button.isHoverButton() && button.isSelected()) {
				gc.setForeground(mSmallTop_SelectedHover);
    			gc.drawLine(bx+1, by, bx+bw-2, by);
    			gc.setForeground(mSmallTopInner_SelectedHover);
    			gc.drawLine(bx+1, by+1, bx+bw-2, by+1);			
    			
    			gc.setForeground(mSmallLeftSide_SelectedHover);
    			gc.drawLine(bx, by+1, bx, by+1+19);
    			gc.setForeground(mSmallRightSide_SelectedHover);
    			gc.drawLine(bx+bw-1, by+1, bx+bw-1, by+1+19);

    			gc.setForeground(mSideBottomBottomSmall_SelectedHover);
    			gc.setBackground(mBottomSmallMid_SelectedHover);
    			int half = (bw-2)/2;
    			gc.fillGradientRectangle(bx+1, by+21, half, 1, false);
    			// add -1 and start on the right so that if the half value is 1px off, it's ok as middle is highlight anyway
    			gc.fillGradientRectangle(bx+bw-1, by+21, -half-1, 1, false);
    			
    			// fill
    			gc.setForeground(mSmallFillTopTop_SelectedHover);
    			gc.setBackground(mSmallFillTopBottom_SelectedHover);
    			gc.fillGradientRectangle(bx+2, by+2, bw-4, 9, true);
    			gc.setForeground(mSmallFillBottomTop_SelectedHover);
    			gc.setBackground(mSmallFillBottomBottom_SelectedHover);
    			gc.fillGradientRectangle(bx+2, by+2+9, bw-4, 9, true);

    			// third to top line as below inner borders will overdraw on sides 
    			gc.setForeground(mSmallTopInnerTwo_SelectedHover);
    			gc.drawLine(bx+1, by+2, bx+bw-2, by+2);			

    			// inner borders
    			gc.setForeground(mBorderSmallSideTopTop_SelectedHover);
    			gc.setBackground(mBorderSmallSideTopBottom_SelectedHover);
    			gc.fillGradientRectangle(bx+1, by+2, 1, 9, true);
    			gc.fillGradientRectangle(bx+bw-2, by+2, 1, 9, true);
    			gc.setForeground(mBorderSmallSideBottomTop_SelectedHover);
    			gc.setBackground(mBorderSmallSideBottomBottom_SelectedHover);
    			gc.fillGradientRectangle(bx+1, by+2+9, 1, 9, true);
    			gc.fillGradientRectangle(bx+bw-2, by+2+9, 1, 9, true);
    			// bottom border
    			gc.setForeground(mBorderSmallSideBottomBottom_SelectedHover);
    			gc.drawLine(bx+1, by+2+9+9, bx+bw-2, by+2+9+9);
    			// inner bottom
    			gc.setForeground(mBottomSmallInnerSide_SelectedHover);
    			gc.setBackground(mBottomSmallInnerMid_SelectedHover);
    			gc.fillGradientRectangle(bx+1, by+20, half, 1, false);
    			// add -1 and start on the right so that if the half value is 1px off, it's ok as middle is highlight anyway
    			gc.fillGradientRectangle(bx+bw-1, by+20, -half-1, 1, false);
    			
    			gc.setForeground(mBorderSmallInsidePixelTop_SelectedHover);
    			gc.drawLine(bx+1, by+1, bx+1, by+1);
    			gc.drawLine(bx+bw-2, by+1, bx+bw-2, by+1);
    			gc.setForeground(mBorderSmallInsidePixelTopTwo_SelectedHover);
    			gc.drawLine(bx+1, by+2, bx+1, by+2);
    			gc.drawLine(bx+bw-2, by+2, bx+bw-2, by+2);
    			gc.setForeground(mBorderSmallInsidePixelBottom_SelectedHover);
    			gc.drawLine(bx+1, by+1+9+9+1, bx+1, by+1+9+9+1);
    			gc.drawLine(bx+bw-2, by+1+9+9+1, bx+bw-2, by+1+9+9+1);
			}
		}
			
		Image toUse = button.getImage(); 
		if (!button.isEnabled())
			toUse = button.getDisabledImage() == null ? toUse : button.getDisabledImage();

		// draw image
		if (toUse != null) 
			gc.drawImage(toUse, curX + 3, curImgY);
		
		if ((button.getStyle() & RibbonButton.STYLE_ARROW_DOWN) != 0)
			drawArrow(gc, curX + bw-9, curImgY+5, button.isEnabled());

		// draw text
		if (button.getName() != null) {
			if (!button.isEnabled()) {
				gc.setForeground(mTextFadedRight_Disabled);
				gc.drawText(button.getName(), curX+5+maxImgWidth+3, curImgY, true);							
				gc.setForeground(mTextFadedLeft_Disabled);
				gc.drawText(button.getName(), curX+3+maxImgWidth+3, curImgY, true);							
				gc.setForeground(mTextColor_Disabled);
				gc.drawText(button.getName(), curX+4+maxImgWidth+3, curImgY, true);							
			}
			else {
				gc.setForeground(mButtonTextColor);
				gc.drawText(button.getName(), curX+4+maxImgWidth+3, curImgY, true);							
			}
		}
	}
	
	public void drawSplitButton(GC gc, AbstractRibbonGroupItem item) {
		int x = item.getX();
		int y = item.getBounds().y;
		int width = item.getWidth();
		int yMax = RibbonTabComposite.GROUP_HEIGHT;
		yMax -= 3;
		yMax -= RibbonGroup.BUTTON_BOT_HEIGHT;
		int imgTopSpacer = 5;

		if (item.isEnabled()) {
    		if (item.isHoverButton() && !item.isSelected()) {
    			// top two lines
    			gc.setForeground(mBorderTop_Hover);
    			gc.drawLine(x+1, y, x+width-2, y);
    			gc.setForeground(mBorderInnerTop_Hover);
    			gc.drawLine(x+1, y+1, x+width-2, y+1);			
    
    			// pixels in corners
    			gc.setForeground(mCornerFadeTopPixel_Hover);
    			gc.drawLine(x+1, y+1, x+1, y+1);
    			gc.drawLine(x+width-2, y+1, x+width-2, y+1);
    			
    			// sides are 3 steps we split them a bit more due to more gradients
    			// note that outer borders stretch 1px higher and lower in the top and bottom sections, but mid stays the same			
    
    			// outer sides 
    			gc.setForeground(mBorderSideTopTop_Hover);
    			gc.setBackground(mBorderSideTopBottom_Hover);
    			gc.fillGradientRectangle(x, y+1, 1, 24, true);
    			gc.fillGradientRectangle(x+width-1, y+1, 1, 24, true);
    			// inner sides 
    			gc.setForeground(mBorderInnerSideTopTop_Hover);
    			gc.setBackground(mBorderInnerSideTopBottom_Hover);
    			gc.fillGradientRectangle(x+1, y+2, 1, 23, true);
    			gc.fillGradientRectangle(x+width-2, y+2, 1, 23, true);
    			// outer mid
    			gc.setForeground(mBorderSideMidTop_Hover);
    			gc.setBackground(mBorderSideMidBottom_Hover);
    			gc.fillGradientRectangle(x, y+2+23, 1, 27, true);
    			gc.fillGradientRectangle(x+width-1, y+2+23, 1, 27, true);
    			// inner mid
    			gc.setForeground(mBorderInnerSideMidTop_Hover);
    			gc.setBackground(mBorderInnerSideMidBottom_Hover);
    			gc.fillGradientRectangle(x+1, y+2+22, 1, 28, true);
    			gc.fillGradientRectangle(x+width-2, y+2+22, 1, 28, true);
    			// outer bottom 
    			gc.setForeground(mBorderInnerSideBottomTop_Hover);
    			gc.setBackground(mBorderInnerSideBottomBottom_Hover);
    			gc.fillGradientRectangle(x+1, y+2+23+27, 1, 12, true);
    			gc.fillGradientRectangle(x+width-2, y+2+23+27, 1, 12, true);			
    			// inner bottom 
    			gc.setForeground(mBorderSideBottomTop_Hover);
    			gc.setBackground(mBorderSideBottomBottom_Hover);
    			gc.fillGradientRectangle(x, y+2+23+27, 1, 13, true);
    			gc.fillGradientRectangle(x+width-1, y+2+23+27, 1, 13, true);			
    			// pixel is just below so we do that now too
    			gc.setForeground(mCornerFadeBottomPixel_Hover);
    			gc.drawLine(x+1, y+2+23+27+12, x+1, y+2+23+27+12);
    			gc.drawLine(x+width-2, y+2+23+27+12, x+width-2, y+2+23+27+12);
    			
    			// draw bottom
    			gc.setForeground(mBorderBottom_Hover);
    			gc.drawLine(x+1, y+2+23+27+12+1, x+width-2, y+2+23+27+12+1);
    			// inner, left half
    			gc.setForeground(mBorderInnerBottomCorner_Hover);
    			gc.setBackground(mBorderInnerBottomMidway_Hover);
    			gc.fillGradientRectangle(x+2, y+2+23+27+12, width/2, 1, false);
    			// inner, right half
    			gc.setForeground(mBorderInnerBottomMidway_Hover);
    			gc.setBackground(mBorderInnerBottomCorner_Hover);
    			gc.fillGradientRectangle(x+2+(width/2), y+2+23+27+12, (width/2)-3, 1, false);
    			
    			// fills
    			if (item.isTopHovered()) {
	    			gc.setForeground(mInnerTopPartTopTopSplit_Hover);
	    			gc.setBackground(mInnerTopPartTopBottomSplit_Hover);
    			}
    			else {
	    			gc.setForeground(mInnerTopPartTopTopSplit);
	    			gc.setBackground(mInnerTopPartTopBottomSplit);    				
    			}
	    		
    			gc.fillGradientRectangle(x+2, y+2, width-4, 22, true);
	    			
    			if (item.isTopHovered()) {
	    			gc.setForeground(mInnerTopPartTopBottomTopSplit_Hover);
	    			gc.setBackground(mInnerTopPartTopBottomBottomSplit_Hover);
    			}
    			else {
	    			gc.setForeground(mInnerTopPartTopBottomTopSplit);
	    			gc.setBackground(mInnerTopPartTopBottomBottomSplit);
    			}
    			gc.fillGradientRectangle(x+2, y+2+22, width-4, 12, true);    				
    			
    			gc.setForeground(mInnerSplitDividerLeft);
    			gc.setBackground(mInnerSplitDividerMid);
        		gc.fillGradientRectangle(x+1, y+2+22+12, (width/2)-2, 1, false);
    			gc.setForeground(mInnerSplitDividerMid);
    			gc.setBackground(mInnerSplitDividerLeft);
        		gc.fillGradientRectangle(x+1+(width/2)-2, y+2+22+12, (width/2)+1, 1, false);
        		    			    	   
        		if (item.isBottomHovered()) {
        			gc.setForeground(mInnerBottomPartTopSplit_Hover);
        			gc.setBackground(mInnerBottomPartBottomSplit_Hover);        			
        		}
        		else {
        			gc.setForeground(mInnerBottomPartTopSplit);
        			gc.setBackground(mInnerBottomPartBottomSplit);        			
        		}
        		
    			gc.fillGradientRectangle(x+2, y+2+22+12+1, width-4, 27, true);
    		}
    		else if (item.isSelected())
    		{
    			if (item.isTopSelected()) {
    				// top two lines
        			gc.setForeground(mBorderTop_SelectedHover);
        			gc.drawLine(x+1, y, x+width-2, y);
        
        			// pixels in corners
        			gc.setForeground(mCornerFadeTopPixel_SelectedHover);
        			gc.drawLine(x+1, y+1, x+1, y+1);
        			gc.drawLine(x+width-2, y+1, x+width-2, y+1);
        			
    				// outer sides
    				// --------------------
    				// TODO: This is a repeat of code, we draw the unselected button first, then overdraw, needs some cleaner way
    				// --------------------
        			gc.setForeground(mBorderSideTopTop_Hover);
        			gc.setBackground(mBorderSideTopBottom_Hover);
        			gc.fillGradientRectangle(x, y+1, 1, 24, true);
        			gc.fillGradientRectangle(x+width-1, y+1, 1, 24, true);
        			// inner sides 
        			gc.setForeground(mBorderInnerSideTopTop_Hover);
        			gc.setBackground(mBorderInnerSideTopBottom_Hover);
        			gc.fillGradientRectangle(x+1, y+2, 1, 23, true);
        			gc.fillGradientRectangle(x+width-2, y+2, 1, 23, true);
        			// outer mid
        			gc.setForeground(mBorderSideMidTop_Hover);
        			gc.setBackground(mBorderSideMidBottom_Hover);
        			gc.fillGradientRectangle(x, y+2+23, 1, 27, true);
        			gc.fillGradientRectangle(x+width-1, y+2+23, 1, 27, true);
        			// inner mid
        			gc.setForeground(mBorderInnerSideMidTop_Hover);
        			gc.setBackground(mBorderInnerSideMidBottom_Hover);
        			gc.fillGradientRectangle(x+1, y+2+22, 1, 28, true);
        			gc.fillGradientRectangle(x+width-2, y+2+22, 1, 28, true);
        			// outer bottom 
        			gc.setForeground(mBorderInnerSideBottomTop_Hover);
        			gc.setBackground(mBorderInnerSideBottomBottom_Hover);
        			gc.fillGradientRectangle(x+1, y+2+23+27, 1, 12, true);
        			gc.fillGradientRectangle(x+width-2, y+2+23+27, 1, 12, true);			
        			// inner bottom 
        			gc.setForeground(mBorderSideBottomTop_Hover);
        			gc.setBackground(mBorderSideBottomBottom_Hover);
        			gc.fillGradientRectangle(x, y+2+23+27, 1, 13, true);
        			gc.fillGradientRectangle(x+width-1, y+2+23+27, 1, 13, true);			
        			// pixel is just below so we do that now too
        			gc.setForeground(mCornerFadeBottomPixel_Hover);
        			gc.drawLine(x+1, y+2+23+27+12, x+1, y+2+23+27+12);
        			gc.drawLine(x+width-2, y+2+23+27+12, x+width-2, y+2+23+27+12);
        			
        			// draw bottom
        			gc.setForeground(mBorderBottom_Hover);
        			gc.drawLine(x+1, y+2+23+27+12+1, x+width-2, y+2+23+27+12+1);
        			// inner, left half
        			gc.setForeground(mBorderInnerBottomCorner_Hover);
        			gc.setBackground(mBorderInnerBottomMidway_Hover);
        			gc.fillGradientRectangle(x+2, y+2+23+27+12, width/2, 1, false);
        			// inner, right half
        			gc.setForeground(mBorderInnerBottomMidway_Hover);
        			gc.setBackground(mBorderInnerBottomCorner_Hover);
        			gc.fillGradientRectangle(x+2+(width/2), y+2+23+27+12, (width/2)-3, 1, false);
        			// -------------------- END ----------------------
        			
    				// let's draw the outer box, start with top, sides, then bottom			
            		// top
            		gc.setForeground(mBorderTop_SelectedHover);
            		gc.drawLine(x+1, TOP_SPACING, x+width-2, TOP_SPACING);
            		gc.setForeground(mBorderTopInner_SelectedHover);
            		gc.drawLine(x+2, TOP_SPACING+1, x+width-3, TOP_SPACING+1);
            		gc.setForeground(mBorderTopInnerTwo_SelectedHover);
            		gc.drawLine(x+2, TOP_SPACING+2, x+width-3, TOP_SPACING+2);
            		gc.setForeground(mBorderTopInnerThree_SelectedHover);
            		gc.drawLine(x+2, TOP_SPACING+3, x+width-3, TOP_SPACING+3);
            		
            		// outer sides 
        			gc.setForeground(mBorderSideTopTop_SelectedHover);
        			gc.setBackground(mBorderSideTopBottom_SelectedHover);
        			gc.fillGradientRectangle(x, y+1, 1, 24, true);
        			gc.fillGradientRectangle(x+width-1, y+1, 1, 24, true);
        			// inner sides 
        			gc.setForeground(mBorderInnerSideTopTop_SelectedHover);
        			gc.setBackground(mBorderInnerSideTopBottom_SelectedHover);
        			gc.fillGradientRectangle(x+1, y+2, 1, 23, true);
        			gc.fillGradientRectangle(x+width-2, y+2, 1, 23, true);
        			// outer mid
        			gc.setForeground(mBorderSideMidTop_SelectedHover);
        			gc.setBackground(mBorderSideMidBottom_SelectedHover);
        			gc.fillGradientRectangle(x, y+2+23, 1, 12, true);
        			gc.fillGradientRectangle(x+width-1, y+2+23, 1, 12, true);
        			// inner mid
        			gc.setForeground(mBorderInnerSideMidTop_SelectedHover);
        			gc.setBackground(mBorderInnerSideMidBottom_SelectedHover);
        			gc.fillGradientRectangle(x+1, y+2+23, 1, 12, true);
        			gc.fillGradientRectangle(x+width-2, y+2+23, 1, 12, true);
            		    		
            		// draw pixel at top
            		gc.setForeground(mCornerFadeTopPixel_SelectedHover);
            		gc.drawLine(x+1, TOP_SPACING+1, x+1, TOP_SPACING+1);
            		gc.drawLine(x+width-2, TOP_SPACING+1, x+width-2, TOP_SPACING+1);
            		// draw bottom pixel
            		gc.setForeground(mCornerFadeBottomPixel_SelectedHover);
            		gc.drawLine(x, y+2+23+27+12, x, y+2+23+27+12);
            		gc.drawLine(x+width-1, y+2+23+27+12, x+width-1, y+2+23+27+12);
            		
            		// fill
            		gc.setForeground(mInnerTop_SelectedHover);
            		gc.setBackground(mInnerTopBottom_SelectedHover);
            		gc.fillGradientRectangle(x+2, y+4, width-4, 22, true);
            		
            		gc.setForeground(mInnerMidTop_SelectedHover);
            		gc.setBackground(mInnerMidBottom_SelectedHover);
            		gc.fillGradientRectangle(x+2, y+4+22, width-4, 12, true);
            		
            		// bottom
        			gc.setForeground(mInnerBottomPartTopSplit);
        			gc.setBackground(mInnerBottomPartBottomSplit);        			        	
        			gc.fillGradientRectangle(x+2, y+2+22+12+1, width-4, 27, true);
    			}
    			else if (item.isBottomSelected()) {
         			gc.setForeground(mBorderTop_Hover);
        			gc.drawLine(x+1, y, x+width-2, y);
        			gc.setForeground(mBorderInnerTop_Hover);
        			gc.drawLine(x+1, y+1, x+width-2, y+1);			

        			// pixels in corners
        			gc.setForeground(mCornerFadeTopPixel_SelectedHover);
        			gc.drawLine(x+1, y+1, x+1, y+1);
        			gc.drawLine(x+width-2, y+1, x+width-2, y+1);

    				// outer sides
    				// --------------------
    				// TODO: This is a repeat of code, we draw the unselected button first, then overdraw, needs some cleaner way
    				// --------------------
        			gc.setForeground(mBorderSideTopTop_Hover);
        			gc.setBackground(mBorderSideTopBottom_Hover);
        			gc.fillGradientRectangle(x, y+1, 1, 24, true);
        			gc.fillGradientRectangle(x+width-1, y+1, 1, 24, true);
        			// inner sides 
        			gc.setForeground(mBorderInnerSideTopTop_Hover);
        			gc.setBackground(mBorderInnerSideTopBottom_Hover);
        			gc.fillGradientRectangle(x+1, y+2, 1, 23, true);
        			gc.fillGradientRectangle(x+width-2, y+2, 1, 23, true);
        			// outer mid
        			gc.setForeground(mBorderSideMidTop_Hover);
        			gc.setBackground(mBorderSideMidBottom_Hover);
        			gc.fillGradientRectangle(x, y+2+23, 1, 27, true);
        			gc.fillGradientRectangle(x+width-1, y+2+23, 1, 27, true);
        			// inner mid
        			gc.setForeground(mBorderInnerSideMidTop_Hover);
        			gc.setBackground(mBorderInnerSideMidBottom_Hover);
        			gc.fillGradientRectangle(x+1, y+2+22, 1, 28, true);
        			gc.fillGradientRectangle(x+width-2, y+2+22, 1, 28, true);
        			// outer bottom 
        			gc.setForeground(mBorderInnerSideBottomTop_Hover);
        			gc.setBackground(mBorderInnerSideBottomBottom_Hover);
        			gc.fillGradientRectangle(x+1, y+2+23+27, 1, 12, true);
        			gc.fillGradientRectangle(x+width-2, y+2+23+27, 1, 12, true);			
        			// inner bottom 
        			gc.setForeground(mBorderSideBottomTop_Hover);
        			gc.setBackground(mBorderSideBottomBottom_Hover);
        			gc.fillGradientRectangle(x, y+2+23+27, 1, 13, true);
        			gc.fillGradientRectangle(x+width-1, y+2+23+27, 1, 13, true);			
        			// pixel is just below so we do that now too
        			gc.setForeground(mCornerFadeBottomPixel_Hover);
        			gc.drawLine(x+1, y+2+23+27+12, x+1, y+2+23+27+12);
        			gc.drawLine(x+width-2, y+2+23+27+12, x+width-2, y+2+23+27+12);
        			
        			// draw bottom
        			gc.setForeground(mBorderBottom_Hover);
        			gc.drawLine(x+1, y+2+23+27+12+1, x+width-2, y+2+23+27+12+1);
        			// inner, left half
        			gc.setForeground(mBorderInnerBottomCorner_Hover);
        			gc.setBackground(mBorderInnerBottomMidway_Hover);
        			gc.fillGradientRectangle(x+2, y+2+23+27+12, width/2, 1, false);
        			// inner, right half
        			gc.setForeground(mBorderInnerBottomMidway_Hover);
        			gc.setBackground(mBorderInnerBottomCorner_Hover);
        			gc.fillGradientRectangle(x+2+(width/2), y+2+23+27+12, (width/2)-3, 1, false);
        			// -------------------- END ----------------------
        			
        			// fills
	    			gc.setForeground(mInnerTopPartTopTopSplit);
	    			gc.setBackground(mInnerTopPartTopBottomSplit);    				
        			gc.fillGradientRectangle(x+2, y+2, width-4, 22, true);
	    			gc.setForeground(mInnerTopPartTopBottomTopSplit);
	    			gc.setBackground(mInnerTopPartTopBottomBottomSplit);
        			gc.fillGradientRectangle(x+2, y+2+22, width-4, 12, true);    				
        			            		    			    	   
        			gc.setForeground(mInnerBottomPartTopSplit_Selected);
        			gc.setBackground(mInnerBottomPartBottomSplit_Selected);        			            		
        			gc.fillGradientRectangle(x+2, y+2+22+12+1, width-4, 27, true);
    			}
    			    			
    			gc.setForeground(mInnerSplitDividerLeft);
    			gc.setBackground(mInnerSplitDividerMid);
        		gc.fillGradientRectangle(x+1, y+2+22+12, (width/2)-2, 1, false);
    			gc.setForeground(mInnerSplitDividerMid);
    			gc.setBackground(mInnerSplitDividerLeft);
        		gc.fillGradientRectangle(x+1+(width/2)-2, y+2+22+12, (width/2)+1, 1, false);        		    			
    		}
		}
		
		// TODO: Account for multi-row text
		if ((item.getStyle() & RibbonButton.STYLE_ARROW_DOWN) != 0)
			drawArrow(gc, item.getBounds().width/2, 56, item.isEnabled());
		
		Image toDraw = item.getImage();
		// fallback is normal image
		if (!item.isEnabled())
			toDraw = item.getDisabledImage() == null ? toDraw : item.getDisabledImage();
		
		// draw image
		if (toDraw != null) {
			Rectangle imBounds = toDraw.getBounds();
			int maxHeight = 32;
			int horizAlignment = x+(width/2)-(imBounds.width/2);
			switch (item.getImageVerticalAlignment()) {
				default:
				case SWT.TOP:
				{
					gc.drawImage(toDraw, horizAlignment, imgTopSpacer);
					break;
				}
				case SWT.BOTTOM:
				{
					int botSpacer = imBounds.height-maxHeight; 
					if (botSpacer < 0)
						botSpacer = 0;
					
					botSpacer += imgTopSpacer;
					botSpacer *= 2;
										
					gc.drawImage(toDraw, horizAlignment, botSpacer);
					break;
				}
				case SWT.CENTER:
				{
					int botSpacer = Math.abs(imBounds.height-maxHeight); 
					if (botSpacer < 0)
						botSpacer = 0;
					
					if (botSpacer != 0)
						botSpacer /= 2;

					botSpacer += imgTopSpacer;
										
					gc.drawImage(toDraw, horizAlignment, botSpacer);
					break;
				}
			}
		}
		
		// draw text    		
		if (item.getName() != null) {
			if (item.isEnabled()) {
    			gc.setForeground(mButtonTextColor);
    			gc.drawText(item.getName(), x+3, 40, true);
			}
			else {
				gc.setForeground(mTextFadedRight_Disabled);
    			gc.drawText(item.getName(), x+4, 40, true);
				gc.setForeground(mTextFadedLeft_Disabled);
    			gc.drawText(item.getName(), x+2, 40, true);
				gc.setForeground(mTextColor_Disabled);
    			gc.drawText(item.getName(), x+3, 40, true);
			}
		}		
	}

	public void drawSeparator(GC gc, int x, int y, int height) {
		gc.setForeground(mSeparatorColor);
		gc.drawLine(x, y, x, y+height);
		gc.setForeground(mSeparatorColorShadow);
		gc.drawLine(x+1, y, x+1, y+height);
	}
	
	public void drawArrow(GC gc, int x, int y, boolean enabled) {
		if (enabled)
			gc.setForeground(mArrowColor);
		else
			gc.setForeground(mArrowColor_Disabled);
		
		gc.drawLine(x, y, x+4, y);
		gc.drawLine(x+1, y+1, x+3, y+1);
		gc.drawLine(x+2, y+2, x+1, y+2);

		if (enabled)
			gc.setForeground(mArrowColorShadow);
		else
			gc.setForeground(mArrowColorShadow_Disabled);
		
		gc.drawLine(x, y+1, x, y+1);
		gc.drawLine(x+1, y+2, x+1, y+2);
		gc.drawLine(x+2, y+3, x+2, y+3);
		gc.drawLine(x+3, y+2, x+3, y+2);
		gc.drawLine(x+4, y+1, x+4, y+1);
		
	}
	
}
