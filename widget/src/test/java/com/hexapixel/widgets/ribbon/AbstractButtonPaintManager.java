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
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.vietspider.generic.ColorCache;


/**
 * This class deals with all painting of buttons, depending on their size, their state and various decorations - such as 
 * split-over-two-lines text, arrows in different places (depending on split text and size of buttons - again) and so on.
 * 
 * There are a ton of colors created in this class.
 * 
 * Methods are split up for convenience so that it's possible to draw half a button in one style and the rest in another, which is necessary.
 * There are of course convenience methods that call the rest of the methods in order depending on need, such as the drawButton(...) method.
 * 
 * @author Emil
 *
 */
public class AbstractButtonPaintManager {
	
	public static final int SPLIT_NOT_SPLIT = 0;
	public static final int SPLIT_TOP_ACTIVE = 1;
	public static final int SPLIT_TOP_NOT_ACTIVE = 2;
	public static final int SPLIT_BOTTOM_ACTIVE = 3;
	public static final int SPLIT_BOTTOM_NOT_ACTIVE = 4;
	public static final int SPLIT_LEFT_ACTIVE = SPLIT_TOP_ACTIVE;
	public static final int SPLIT_LEFT_NOT_ACTIVE = SPLIT_TOP_NOT_ACTIVE;
	public static final int SPLIT_RIGHT_ACTIVE = SPLIT_BOTTOM_ACTIVE;
	public static final int SPLIT_RIGHT_NOT_ACTIVE = SPLIT_BOTTOM_NOT_ACTIVE;

	// corners (not buttons) 
	public static final int TOP_LEFT = 1;
	public static final int TOP_RIGHT = 2;
	public static final int BOTTOM_LEFT = 3;
	public static final int BOTTOM_RIGHT = 4;
		
	// button type
	public static final int TYPE_BUTTON_BIG = 1;
	public static final int TYPE_BUTTON_SMALL = 2;
	public static final int TYPE_BUTTON_BIG_SPLIT = 3;
	public static final int TYPE_BUTTON_SMALL_SPLIT = 4;
	
	// button state
	public static final int STATE_NONE = 0;
	public static final int STATE_HOVER = 1;
	public static final int STATE_SELECTED = 2;
	public static final int STATE_HOVER_AND_SELECTED = 3;
	
	// button size
	public static final int BUTTON_BIG = 1;
	public static final int BUTTON_SMALL = 2;

	// OUTER BORDERS HOVER
	// top border outer
	private Color mButtonOuterTop_Hover = ColorCache.getInstance().getColor(221, 207, 155);
	// side borders outer
	private Color mButtonOuterSideTopTop_Hover = ColorCache.getInstance().getColor(220, 206, 154);
	private Color mButtonOuterSideTopBottom_Hover = ColorCache.getInstance().getColor(197, 174, 125);
	private Color mButtonOuterSideBottomTop_Hover = ColorCache.getInstance().getColor(196, 173, 124);
	private Color mButtonOuterSideBottomBottom_Hover = ColorCache.getInstance().getColor(211, 206, 185);
	// top corner pixel on the inside of the corner 
	private Color mButtonTopCornerPixel_Hover = ColorCache.getInstance().getColor(233, 223, 159);
	// bottom one, same deal
	private Color mButtonBottomCornerPixel_Hover = ColorCache.getInstance().getColor(233, 219, 177);
	// bottom border outer
	private Color mButtonOuterBottomLeft_Hover = ColorCache.getInstance().getColor(209, 202, 173);
	private Color mButtonOuterBottomMiddle_Hover = mButtonOuterBottomLeft_Hover;
	private Color mButtonOuterBottomRight_Hover = mButtonOuterBottomLeft_Hover;

	// OUTER BORDERS HOVER - SMALL
	// top border outer
	private Color mButtonSmallOuterTop_Hover = ColorCache.getInstance().getColor(219, 206, 153);
	// side borders outer
	private Color mButtonSmallOuterSideTopTop_Hover = ColorCache.getInstance().getColor(216, 202, 150);
	private Color mButtonSmallOuterSideTopBottom_Hover = ColorCache.getInstance().getColor(185, 160, 116);
	private Color mButtonSmallOuterSideBottomTop_Hover = ColorCache.getInstance().getColor(183, 158, 115);
	private Color mButtonSmallOuterSideBottomBottom_Hover = ColorCache.getInstance().getColor(184, 169, 142);
	// top corner pixel on the inside of the corner 
	private Color mButtonSmallTopCornerPixel_Hover = ColorCache.getInstance().getColor(233, 223, 159);
	// bottom one, same deal
	private Color mButtonSmallBottomCornerPixel_Hover = ColorCache.getInstance().getColor(233, 219, 177);
	// bottom border outer
	private Color mButtonSmallOuterBottomLeft_Hover = ColorCache.getInstance().getColor(187, 174, 151);
	private Color mButtonSmallOuterBottomMiddle_Hover = ColorCache.getInstance().getColor(203, 195, 170);
	private Color mButtonSmallOuterBottomRight_Hover = mButtonSmallOuterBottomLeft_Hover;
	
	// OUTER BORDERS SELECTED	
	// top border outer
	private Color mButtonOuterTop_Selected = ColorCache.getInstance().getColor(142, 129, 101);
	// side borders outer
	private Color mButtonOuterSideTopTop_Selected = mButtonOuterTop_Selected;
	private Color mButtonOuterSideTopBottom_Selected = mButtonOuterTop_Selected;
	private Color mButtonOuterSideBottomTop_Selected = mButtonOuterTop_Selected;
	private Color mButtonOuterSideBottomBottom_Selected = ColorCache.getInstance().getColor(198, 192, 178);
	// top corner pixel on the inside of the corner 
	private Color mButtonTopCornerPixel_Selected = ColorCache.getInstance().getColor(181, 167, 145);
	// bottom one, same deal, 1px further in than hover
	private Color mButtonBottomCornerPixel_Selected = ColorCache.getInstance().getColor(254, 216, 93);
	// bottom border outer	
	private Color mButtonOuterBottomLeft_Selected = ColorCache.getInstance().getColor(255, 207, 45);
	private Color mButtonOuterBottomMiddle_Selected = ColorCache.getInstance().getColor(255, 233, 160);
	private Color mButtonOuterBottomRight_Selected = mButtonOuterBottomLeft_Selected;
	// very bottom pixel on outer sides
	private Color mButtonOuterBottomPixel_Selected = ColorCache.getInstance().getColor(247, 229, 168);

	// OUTER BORDERS SELECTED - SMALL
	// top border outer
	private Color mButtonSmallOuterTop_Selected = ColorCache.getInstance().getColor(167, 142, 102);
	// side borders outer
	private Color mButtonSmallOuterSideTopTop_Selected = ColorCache.getInstance().getColor(168, 130, 85);
	private Color mButtonSmallOuterSideTopBottom_Selected = mButtonSmallOuterSideTopTop_Selected;
	private Color mButtonSmallOuterSideBottomTop_Selected = mButtonSmallOuterSideTopTop_Selected;
	private Color mButtonSmallOuterSideBottomBottom_Selected = mButtonSmallOuterSideTopTop_Selected;
	// top corner pixel on the inside of the corner 
	private Color mButtonSmallTopCornerPixel_Selected = ColorCache.getInstance().getColor(178, 154, 120);
	// bottom one, same deal
	private Color mButtonSmallBottomCornerPixel_Selected = ColorCache.getInstance().getColor(200, 163, 104);
	// bottom border outer	
	private Color mButtonSmallOuterBottomLeft_Selected = ColorCache.getInstance().getColor(187, 174, 151);
	private Color mButtonSmallOuterBottomMiddle_Selected = ColorCache.getInstance().getColor(204, 196, 172);
	private Color mButtonSmallOuterBottomRight_Selected = mButtonSmallOuterBottomLeft_Selected;
	// very bottom pixel on outer sides
	private Color mButtonSmallOuterBottomPixel_Selected = mButtonSmallOuterSideTopTop_Selected;
	
	// OUTER BORDERS HOVER + SELECTED
	// top border outer
	private Color mButtonOuterTop_HoverSelected = ColorCache.getInstance().getColor(142, 129, 101);
	// side borders outer
	private Color mButtonOuterSideTopTop_HoverSelected = mButtonOuterTop_HoverSelected;
	private Color mButtonOuterSideTopBottom_HoverSelected = mButtonOuterTop_HoverSelected;
	private Color mButtonOuterSideBottomTop_HoverSelected = mButtonOuterTop_HoverSelected;
	private Color mButtonOuterSideBottomBottom_HoverSelected = ColorCache.getInstance().getColor(177, 163, 137);
	// top corner pixel on the inside of the corner 
	private Color mButtonTopCornerPixel_HoverSelected = ColorCache.getInstance().getColor(151, 137, 109);
	// bottom one, same deal
	private Color mButtonBottomCornerPixel_HoverSelected = ColorCache.getInstance().getColor(225, 194, 113);
	// bottom border outer
	private Color mButtonOuterBottomLeft_HoverSelected = ColorCache.getInstance().getColor(212, 197, 173);
	private Color mButtonOuterBottomMiddle_HoverSelected = mButtonOuterBottomLeft_HoverSelected;
	private Color mButtonOuterBottomRight_HoverSelected = mButtonOuterBottomLeft_HoverSelected;
	// very bottom pixel on outer sides
	private Color mButtonOuterBottomPixel_HoverSelected = ColorCache.getInstance().getColor(204, 190, 165);
	
	// OUTER BORDERS HOVER + SELECTED - SMALL
	// top border outer
	private Color mButtonSmallOuterTop_HoverSelected = ColorCache.getInstance().getColor(123, 102, 69);
	// side borders outer
	private Color mButtonSmallOuterSideTopTop_HoverSelected = mButtonSmallOuterTop_HoverSelected;
	private Color mButtonSmallOuterSideTopBottom_HoverSelected = mButtonSmallOuterTop_HoverSelected;
	private Color mButtonSmallOuterSideBottomTop_HoverSelected = mButtonSmallOuterTop_HoverSelected;
	private Color mButtonSmallOuterSideBottomBottom_HoverSelected = mButtonSmallOuterTop_HoverSelected;
	// top corner pixel on the inside of the corner 
	private Color mButtonSmallTopCornerPixel_HoverSelected = ColorCache.getInstance().getColor(139, 111, 77);
	// bottom one, same deal
	private Color mButtonSmallBottomCornerPixel_HoverSelected = ColorCache.getInstance().getColor(253, 170, 6);
	// bottom border outer
	private Color mButtonSmallOuterBottomLeft_HoverSelected = ColorCache.getInstance().getColor(253, 173, 3);
	private Color mButtonSmallOuterBottomMiddle_HoverSelected = ColorCache.getInstance().getColor(255, 174, 3);
	private Color mButtonSmallOuterBottomRight_HoverSelected = mButtonSmallOuterBottomLeft_HoverSelected;
	// very bottom pixel on outer sides
	private Color mButtonSmallOuterBottomPixel_HoverSelected = ColorCache.getInstance().getColor(169, 135, 73);

	// special, bottom left and right corners on a small hover+selected button
	private Color mButtonSmallBottomCornerInwards_HoverSelected = ColorCache.getInstance().getColor(249, 157, 25);
	
	// INNER BORDERS HOVER
	// top border inner
	private Color mButtonInnerTop_Hover = ColorCache.getInstance().getColor(255, 255, 247);
	// side borders inner
	private Color mButtonInnerSideTopTop_Hover = mButtonInnerTop_Hover;
	private Color mButtonInnerSideTopBottom_Hover = ColorCache.getInstance().getColor(255, 248, 223);
	private Color mButtonInnerSideBottomTop_Hover = ColorCache.getInstance().getColor(255, 243, 206);
	private Color mButtonInnerSideBottomBottom_Hover = ColorCache.getInstance().getColor(255, 243, 93);
	// bottom border inner
	private Color mButtonInnerBottomLeft_Hover = ColorCache.getInstance().getColor(255, 247, 146);
	private Color mButtonInnerBottomMiddle_Hover = ColorCache.getInstance().getColor(255, 254, 247);
	private Color mButtonInnerBottomRight_Hover = mButtonInnerBottomLeft_Hover;

	// INNER BORDERS HOVER - SMALL
	// top border inner
	private Color mButtonSmallInnerTop_Hover = ColorCache.getInstance().getColor(255, 255, 251);
	// side borders inner
	private Color mButtonSmallInnerSideTopTop_Hover = ColorCache.getInstance().getColor(255, 255, 250);
	private Color mButtonSmallInnerSideTopBottom_Hover = ColorCache.getInstance().getColor(255, 249, 227);
	private Color mButtonSmallInnerSideBottomTop_Hover = ColorCache.getInstance().getColor(255, 242, 201);
	private Color mButtonSmallInnerSideBottomBottom_Hover = ColorCache.getInstance().getColor(255, 246, 185);
	// bottom border inner
	private Color mButtonSmallInnerBottomLeft_Hover = ColorCache.getInstance().getColor(255, 250, 207);
	private Color mButtonSmallInnerBottomMiddle_Hover = ColorCache.getInstance().getColor(255, 255, 254);
	private Color mButtonSmallInnerBottomRight_Hover = mButtonSmallInnerBottomLeft_Hover;

	// INNER BORDERS SELECTED
	// top border inner
	private Color mButtonInnerTop_Selected = ColorCache.getInstance().getColor(182, 154, 120);
	// side borders inner
	private Color mButtonInnerSideTopTop_Selected = ColorCache.getInstance().getColor(230, 175, 86);
	private Color mButtonInnerSideTopBottom_Selected = ColorCache.getInstance().getColor(250, 195, 93);
	private Color mButtonInnerSideBottomTop_Selected = ColorCache.getInstance().getColor(248, 190, 81);
	private Color mButtonInnerSideBottomBottom_Selected = ColorCache.getInstance().getColor(255, 207, 45);

	// INNER BORDERS SELECTED - SMALL
	// top border inner
	private Color mButtonSmallInnerTop_Selected = ColorCache.getInstance().getColor(203, 180, 153);
	// side borders inner
	private Color mButtonSmallInnerSideTopTop_Selected = ColorCache.getInstance().getColor(239, 211, 176);
	private Color mButtonSmallInnerSideTopBottom_Selected = ColorCache.getInstance().getColor(241, 178, 90);
	private Color mButtonSmallInnerSideBottomTop_Selected = ColorCache.getInstance().getColor(239, 171, 72);
	private Color mButtonSmallInnerSideBottomBottom_Selected = ColorCache.getInstance().getColor(251, 186, 67);
	// bottom border inner
	private Color mButtonSmallInnerBottomLeft_Selected = ColorCache.getInstance().getColor(252, 191, 68);
	private Color mButtonSmallInnerBottomMiddle_Selected = ColorCache.getInstance().getColor(253, 227, 173);
	private Color mButtonSmallInnerBottomRight_Selected = mButtonSmallInnerBottomLeft_Selected;
	// second inside pixel counted from the top inner corner, it's 1 pixel further down on both sides in selected and hover+selected
	private Color mButtonSmallExtraTopBottomPixel_Selected = ColorCache.getInstance().getColor(226, 204, 178);
	
	// INNER BORDERS HOVER + SELECTED
	// top border inner
	private Color mButtonInnerTop_HoverSelected = ColorCache.getInstance().getColor(168, 136, 94);
	// side borders inner
	private Color mButtonInnerSideTopTop_HoverSelected = ColorCache.getInstance().getColor(225, 153, 47);
	private Color mButtonInnerSideTopBottom_HoverSelected = ColorCache.getInstance().getColor(253, 232, 192);
	private Color mButtonInnerSideBottomTop_HoverSelected = ColorCache.getInstance().getColor(254, 239, 210);
	private Color mButtonInnerSideBottomBottom_HoverSelected = ColorCache.getInstance().getColor(255, 223, 113);
	// bottom border inner
	private Color mButtonInnerBottomLeft_HoverSelected = ColorCache.getInstance().getColor(255, 207, 44);
	private Color mButtonInnerBottomMiddle_HoverSelected = ColorCache.getInstance().getColor(255, 233, 160);
	private Color mButtonInnerBottomRight_HoverSelected = mButtonInnerBottomLeft_HoverSelected;
	// second inside pixel counted from the top inner corner, it's 1 pixel further down on both sides in selected and hover+selected
	private Color mButtonSmallExtraTopBottomPixel_HoverSelected = ColorCache.getInstance().getColor(205, 156, 110);
	
	// INNER BORDERS HOVER + SELECTED - SMALL
	// top border inner
	private Color mButtonSmallInnerTop_HoverSelected = ColorCache.getInstance().getColor(175, 131, 92);
	// side borders inner
	private Color mButtonSmallInnerSideTopTop_HoverSelected = ColorCache.getInstance().getColor(229, 172, 118);
	private Color mButtonSmallInnerSideTopBottom_HoverSelected = ColorCache.getInstance().getColor(241, 150, 59);
	private Color mButtonSmallInnerSideBottomTop_HoverSelected = ColorCache.getInstance().getColor(237, 120, 4);
	private Color mButtonSmallInnerSideBottomBottom_HoverSelected = ColorCache.getInstance().getColor(253, 170, 6);
	
	// FILLS HOVER
	private Color mButtonFillTopTop_Hover = ColorCache.getInstance().getColor(255, 253, 219);
	private Color mButtonFillTopBottom_Hover = ColorCache.getInstance().getColor(255, 231, 144);
	private Color mButtonFillBottomTop_Hover = ColorCache.getInstance().getColor(255, 215, 76);
	private Color mButtonFillBottomBottom_Hover = ColorCache.getInstance().getColor(255, 231, 150);

	// FILLS HOVER - SMALL
	private Color mButtonSmallFillTopTop_Hover = ColorCache.getInstance().getColor(255, 253, 236);
	private Color mButtonSmallFillTopBottom_Hover = ColorCache.getInstance().getColor(255, 237, 185);
	private Color mButtonSmallFillBottomTop_Hover = ColorCache.getInstance().getColor(255, 217, 115);
	private Color mButtonSmallFillBottomBottom_Hover = ColorCache.getInstance().getColor(255, 233, 164);

	// FILLS SELECTED
	private Color mButtonFillTopTop_Selected = ColorCache.getInstance().getColor(253, 212, 168);
	private Color mButtonFillTopBottom_Selected = ColorCache.getInstance().getColor(250, 175, 96);
	private Color mButtonFillBottomTop_Selected = ColorCache.getInstance().getColor(249, 147, 46);
	private Color mButtonFillBottomBottom_Selected = ColorCache.getInstance().getColor(253, 241, 176);
	// 1px down
	private Color mButtonFillTopInnerLineOne_Selected = ColorCache.getInstance().getColor(224, 182, 136);
	// 2px down
	private Color mButtonFillTopInnerLineTwo_Selected = ColorCache.getInstance().getColor(245, 201, 154);

	// FILLS SELECTED - SMALL
	private Color mButtonSmallFillTopTop_Selected = ColorCache.getInstance().getColor(253, 218, 177);
	private Color mButtonSmallFillTopBottom_Selected = ColorCache.getInstance().getColor(252, 208, 135);
	private Color mButtonSmallFillBottomTop_Selected = ColorCache.getInstance().getColor(251, 188, 87);
	private Color mButtonSmallFillBottomBottom_Selected = ColorCache.getInstance().getColor(253, 231, 153);
	// 1px down
	private Color mButtonSmallFillTopInnerLineOne_Selected = ColorCache.getInstance().getColor(235, 209, 178);
	// 2px down
	private Color mButtonSmallFillTopInnerLineTwo_Selected = ColorCache.getInstance().getColor(248, 217, 182);
	
	// FILLS SELECTED + HOVER
	private Color mButtonFillTopTop_HoverSelected = ColorCache.getInstance().getColor(245, 176, 109);
	private Color mButtonFillTopBottom_HoverSelected = ColorCache.getInstance().getColor(232, 147, 82);
	private Color mButtonFillBottomTop_HoverSelected = ColorCache.getInstance().getColor(230, 132, 51);
	private Color mButtonFillBottomBottom_HoverSelected = ColorCache.getInstance().getColor(253, 209, 101);
	// 1px down
	private Color mButtonFillTopInnerLineOne_HoverSelected = ColorCache.getInstance().getColor(210, 158, 102);
	// 2px down
	private Color mButtonFillTopInnerLineTwo_HoverSelected = ColorCache.getInstance().getColor(233, 170, 107);
		
	// FILLS SELECTED + HOVER - SMALL
	private Color mButtonSmallFillTopTop_HoverSelected = ColorCache.getInstance().getColor(241, 175, 118);
	private Color mButtonSmallFillTopBottom_HoverSelected = ColorCache.getInstance().getColor(243, 144, 65);
	private Color mButtonSmallFillBottomTop_HoverSelected = ColorCache.getInstance().getColor(241, 114, 14);
	private Color mButtonSmallFillBottomBottom_HoverSelected = ColorCache.getInstance().getColor(252, 155, 72);
	// 1px down
	private Color mButtonSmallFillTopInnerLineOne_HoverSelected = ColorCache.getInstance().getColor(219, 161, 111);
	// 2px down
	private Color mButtonSmallFillTopInnerLineTwo_HoverSelected = ColorCache.getInstance().getColor(242, 176, 119);
	
	// SPECIAL FILLS - SPLIT BUTTON
	private Color mButtonSplitFillTopTop_NonSelectedHover = ColorCache.getInstance().getColor(255, 254, 242);
	private Color mButtonSplitFillTopBottom_NonSelectedHover = ColorCache.getInstance().getColor(255, 247, 217);
	private Color mButtonSplitFillBottomTop_NonSelectedHover = ColorCache.getInstance().getColor(255, 241, 194);
	private Color mButtonSplitFillBottomBottom_NonSelectedHover = ColorCache.getInstance().getColor(255, 239, 190);

	private Color mButtonSplitFillBottomPartTop_NonSelectedHover = ColorCache.getInstance().getColor(255, 240, 194);
	private Color mButtonSplitFillBottomPartBottom_NonSelectedHover = ColorCache.getInstance().getColor(255, 248, 224);

	private Color mButtonSplitFillBottomPartTop_SelectedHover = ColorCache.getInstance().getColor(253, 201, 104);
	private Color mButtonSplitFillBottomPartBottom_SelectedHover = ColorCache.getInstance().getColor(253, 240, 174);

	// TEXT
	private Color mTextColor = ColorCache.getInstance().getColor(21, 66, 139);
	// disabled text
	private Color mTextColor_Disabled = ColorCache.getInstance().getColor(165, 141, 159);
	private Color mTextFadedRight_Disabled = ColorCache.getInstance().getColor(178, 214, 241);
	private Color mTextFadedLeft_Disabled = ColorCache.getInstance().getColor(211, 226, 210);
	
	// ARROW
	private Color mArrowColor = ColorCache.getInstance().getColor(86, 125, 177);
	private Color mArrowColorShadow = ColorCache.getInstance().getColor(234, 242, 249);
	private Color mArrowColorShadow_HoverOrSelected = ColorCache.getInstance().getColor(255, 248, 203);
	private Color mArrowColor_Disabled = ColorCache.getInstance().getColor(183, 183, 183);
	private Color mArrowColorShadow_Disabled = ColorCache.getInstance().getColor(237, 237, 237);

	// SEPARATOR 
	private Color mSeparatorColor = ColorCache.getInstance().getColor(251, 252, 254);
	private Color mSeparatorColorShadow = ColorCache.getInstance().getColor(150, 180, 218);

	// SPLIT SPECIALS
	private Color mSplitBottomSelectedTopLineLeft_Hover = ColorCache.getInstance().getColor(255, 240, 191);
	private Color mSplitBottomSelectedTopLineMid_Hover = ColorCache.getInstance().getColor(255, 244, 215);
	private Color mSplitBottomSelectedTopLineLeft_Selected = ColorCache.getInstance().getColor(250, 192, 148);
	private Color mSplitBottomSelectedTopLineMid_Selected = ColorCache.getInstance().getColor(255, 213, 167);

	private Color mSplitDividerLineLeft_Hover = ColorCache.getInstance().getColor(219, 195, 116);
	private Color mSplitDividerLineMid_Hover = ColorCache.getInstance().getColor(205, 189, 136);
	private Color mSplitDividerLineLeft_Selected = ColorCache.getInstance().getColor(176, 125, 75);
	private Color mSplitDividerLineMid_Selected = ColorCache.getInstance().getColor(149, 123, 82);

	private Color mSplitDividerLineTopVertical_Hover = ColorCache.getInstance().getColor(226, 214, 189);
	private Color mSplitDividerLineMidVertical_Hover = ColorCache.getInstance().getColor(205, 181, 131);
	private Color mSplitDividerLineTopVertical_Selected = ColorCache.getInstance().getColor(184, 145, 101);
	private Color mSplitDividerLineMidVertical_Selected = ColorCache.getInstance().getColor(169, 132, 85);
	
	// CHECKBOX

	// disabled
	private Color mCheckBoxBorderOuterSquareTopLeft_Disabled = ColorCache.getInstance().getColor(224, 226, 229);
	private Color mCheckBoxBorderOuterSquareBottomRight_Disabled = ColorCache.getInstance().getColor(242, 243, 243);
	private Color mCheckBoxBorderInnerSquareTopLeft_Disabled = ColorCache.getInstance().getColor(238, 240, 242);
	private Color mCheckBoxBorderInnerSquareBottomRight_Disabled = ColorCache.getInstance().getColor(251, 251, 251);

	// normal
	private Color mCheckBoxBorderOuterSquareTopLeft_Normal = ColorCache.getInstance().getColor(162, 172, 185);
	private Color mCheckBoxBorderOuterSquareBottomRight_Normal = ColorCache.getInstance().getColor(228, 230, 234);
	private Color mCheckBoxBorderInnerSquareTopLeft_Normal = ColorCache.getInstance().getColor(202, 207, 213);
	private Color mCheckBoxBorderInnerSquareBottomRight_Normal = ColorCache.getInstance().getColor(246, 246, 246);

	// hover
	private Color mCheckBoxBorderOuterSquareTopLeft_Hover = ColorCache.getInstance().getColor(250, 213, 122);
	private Color mCheckBoxBorderOuterSquareBottomRight_Hover = ColorCache.getInstance().getColor(253, 241, 207);
	private Color mCheckBoxBorderInnerSquareTopLeft_Hover = ColorCache.getInstance().getColor(252, 231, 175);
	private Color mCheckBoxBorderInnerSquareBottomRight_Hover = ColorCache.getInstance().getColor(254, 248, 231);

	// same with borders 
	private Color mCheckBoxBorderOuter_Disabled = ColorCache.getInstance().getColor(174, 177, 181);
	private Color mCheckBoxBorderInner_Disabled = ColorCache.getInstance().getColor(255, 255, 255);

	private Color mCheckBoxBorderOuter_Normal = ColorCache.getInstance().getColor(171, 193, 222);
	private Color mCheckBoxBorderInner_Normal = ColorCache.getInstance().getColor(244, 244, 244);

	private Color mCheckBoxBorderOuter_Hover= ColorCache.getInstance().getColor(85, 119, 163);
	private Color mCheckBoxBorderInner_Hover = ColorCache.getInstance().getColor(222, 234, 250);

	// actual check mark
	private Color mCheckBoxCheckmarkDark = ColorCache.getInstance().getColor(78, 108, 141);
	private Color mCheckBoxCheckmarkDark2 = ColorCache.getInstance().getColor(102, 126, 149);
	private Color mCheckBoxCheckmarkDark3 = ColorCache.getInstance().getColor(142, 163, 189);
	private Color mCheckBoxCheckmarkDark4 = ColorCache.getInstance().getColor(178, 190, 204);

	private Color mCheckBoxCheckmarkLight = ColorCache.getInstance().getColor(230, 229, 222);
	private Color mCheckBoxCheckmarkLight2 = ColorCache.getInstance().getColor(213, 215, 209);
	private Color mCheckBoxCheckmarkLight3 = ColorCache.getInstance().getColor(253, 249, 245);
	
	// boredered toolbars
	private Color mOuterToolbarBorderColor = ColorCache.getInstance().getColor(160, 188, 228);
	private Color mOuterToolbarBorderMidpointColor = ColorCache.getInstance().getColor(121, 153, 194);
	private Color mInnerToolbarBorderColorTop = ColorCache.getInstance().getColor(213, 227, 241);
	private Color mInnerToolbarBorderColorBottom = ColorCache.getInstance().getColor(227, 237, 251);
	private Color mInnerToolbarBorderCornerColor = ColorCache.getInstance().getColor(180, 202, 227);
	
	private Color mInnerToolbarBorderSideMidpointColor = ColorCache.getInstance().getColor(234, 241, 251);
	
	private Color mInnerToolbarDividerColorLeft = ColorCache.getInstance().getColor(190, 212, 237); 
	
	private Color mToolbarFillColorTop = ColorCache.getInstance().getColor(202, 221, 241);
	private Color mToolbarFillColorBottomTop = ColorCache.getInstance().getColor(188, 208, 233);
	private Color mToolbarFillColorBottomBottom = ColorCache.getInstance().getColor(208, 225, 247);

	// buttons with hover
	private Color mToolbarFillColorTopTop_Hover = ColorCache.getInstance().getColor(254, 247, 213);
	private Color mToolbarFillColorTopBottom_Hover = ColorCache.getInstance().getColor(250, 229, 168);
	private Color mToolbarFillColorBottomTop_Hover = ColorCache.getInstance().getColor(255, 208, 72);
	private Color mToolbarFillColorBottomBottom_Hover = ColorCache.getInstance().getColor(255, 229, 159);
	
	private Color mToolbarInnerBorderTop_Hover = ColorCache.getInstance().getColor(255, 252, 232);
	private Color mToolbarInnerBorderTopBottom_Hover = ColorCache.getInstance().getColor(255, 252, 232);
	private Color mToolbarInnerBorderBottomTop_Hover = ColorCache.getInstance().getColor(255, 226, 143);
	private Color mToolbarInnerBorderBottomBottom_Hover = ColorCache.getInstance().getColor(255, 249, 204);

	private Color mToolbarOuterBorderTop_Selected = ColorCache.getInstance().getColor(203, 180, 153);
	private Color mToolbarOuterBorderTopTwo_Selected = ColorCache.getInstance().getColor(235, 209, 180);
	private Color mToolbarFillColorTopTop_Selected = ColorCache.getInstance().getColor(251, 219, 181);
	private Color mToolbarFillColorTopBottom_Selected = ColorCache.getInstance().getColor(254, 199, 120);
	private Color mToolbarFillColorBottomTop_Selected = ColorCache.getInstance().getColor(254, 180, 86);
	private Color mToolbarFillColorBottomBottom_Selected = ColorCache.getInstance().getColor(253, 235, 159);
	private Color mToolbarInnerBorderBottomBottomLine_Selected = ColorCache.getInstance().getColor(255, 228, 127);

	private Color mToolbarOuterBorderTop_HoverSelected = ColorCache.getInstance().getColor(158, 130, 85);
	private Color mToolbarOuterBorderTopTwo_HoverSelected = ColorCache.getInstance().getColor(204, 152, 95);
	private Color mToolbarOuterBorderTopThree_HoverSelected = ColorCache.getInstance().getColor(232, 174, 113);
	private Color mToolbarFillColorTopTop_HoverSelected = ColorCache.getInstance().getColor(248, 186, 121);
	private Color mToolbarFillColorTopBottom_HoverSelected = ColorCache.getInstance().getColor(255, 189, 121);
	private Color mToolbarFillColorBottomTop_HoverSelected = ColorCache.getInstance().getColor(254, 163, 53);
	private Color mToolbarFillColorBottomBottom_HoverSelected = ColorCache.getInstance().getColor(254, 224, 105);
	private Color mToolbarInnerBorderBottomBottomLine_HoverSelected = ColorCache.getInstance().getColor(252, 206, 107);
	
	// split toolbar button
	private Color mToolbarSplitFillColorTopTop_NonHover = ColorCache.getInstance().getColor(255, 254, 247);
	private Color mToolbarSplitFillColorTopBottom_NonHover = ColorCache.getInstance().getColor(255, 247, 223);
	private Color mToolbarSplitFillColorBottomTop_NonHover = ColorCache.getInstance().getColor(255, 238, 190);
	private Color mToolbarSplitFillColorBottomBottom_NonHover = ColorCache.getInstance().getColor(255, 246, 221);
	private Color mToolbarSplitInnerBorderTop_Hover = ColorCache.getInstance().getColor(255, 255, 253);
	private Color mToolbarSplitInnerBorderBottom_Hover = ColorCache.getInstance().getColor(255, 238, 187);	

	public void drawItem(GC gc, AbstractRibbonGroupItem item) {
		if (item instanceof RibbonGroupSeparator) {
			RibbonGroupSeparator rgs = (RibbonGroupSeparator) item;
			Rectangle rgsBounds = rgs.getBounds();
			drawSeparator(gc, rgsBounds.x, rgsBounds.y, rgsBounds.height);
			return;
		}
		else if (item instanceof RibbonButton) {
			Rectangle buttonBounds = item.getBounds();
			int state = getState(item);

			if (state != STATE_NONE && item.isEnabled()) {
				if (item.isSplit()) {
					drawBigSplitButton(gc, state, item);
				}
				else {
					drawButtonOuterBorder(gc, state, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
					drawButtonFills(gc, state, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
					drawButtonInnerBorder(gc, state, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
					drawButtonDecorations(gc, state, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
				}
			}
			
			drawButtonImage(gc, TYPE_BUTTON_BIG, item);
			drawButtonText(gc, state, TYPE_BUTTON_BIG, item);
		}
		else if (item instanceof RibbonButtonGroup) {
			RibbonButtonGroup rbg = (RibbonButtonGroup) item;
			List<AbstractRibbonGroupItem> buttons = rbg.getButtons();
			for (AbstractRibbonGroupItem button : buttons) {
				int state = getState(button);

				if (button instanceof RibbonCheckbox) {		
					drawCheckbox(gc, getState(button), button);
					continue;
				}
				
				if (button.isSplit()) {
					drawSmallSplitButton(gc, state, button);
				}
				else {					
					Rectangle buttonBounds = button.getBounds();				
					if (state != STATE_NONE && button.isEnabled()) {
						drawButtonOuterBorder(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
						drawButtonFills(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
						drawButtonInnerBorder(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
						drawButtonDecorations(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);				
					}
				}
				
				drawButtonImage(gc, TYPE_BUTTON_SMALL, button);
				drawButtonText(gc, state, TYPE_BUTTON_SMALL, button);
			}
		}
		else {
			System.err.println("Unknown object to draw: " + item.getClass());
		}
	}
	
	protected void drawToolbar(GC gc, RibbonToolbar tb) {
		List<RibbonToolbarGrouping> groupings = tb.getGroupings();
		
		if (tb.getStyle() == RibbonToolbar.STYLE_NO_BORDER) {
			for (RibbonToolbarGrouping group : groupings) {					
				List<RibbonButton> buttons = group.getItems();
				for (RibbonButton button : buttons) {
					Rectangle buttonBounds = button.getBounds();									
					int state = getState(button);
					
					if (state != STATE_NONE && button.isEnabled()) {
						drawButtonOuterBorder(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
						drawButtonFills(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
						drawButtonInnerBorder(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
						drawButtonDecorations(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
					}

					// drawButtonText draws arrows as well even if we don't have any text
					drawButtonText(gc, state, TYPE_BUTTON_SMALL, button);
					drawButtonImage(gc, TYPE_BUTTON_SMALL, button);
				}
			}
		}
		else {
			// on bordered toolbars, basically everything is different
			drawBorderedToolbar(gc, tb);
		}
	}
	
	void drawMenuToolbarButton(GC gc, RibbonButton button) {
		int state = getState(button);
		Rectangle buttonBounds = button.getBounds();
		
		if (state != STATE_NONE && button.isEnabled()) {
			drawButtonOuterBorder(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
			drawButtonFills(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
			drawButtonInnerBorder(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
			drawButtonDecorations(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
		}

		// drawButtonText draws arrows as well even if we don't have any text
		drawButtonText(gc, state, TYPE_BUTTON_SMALL, button);
		Image toUse = button.isEnabled() ? button.getImage() : button.getDisabledImage();
		if (toUse == null)
			toUse = button.getImage();
		
		if (toUse == null)
			return;
		
		Rectangle imBounds = toUse.getBounds();
		int xSpacer = (buttonBounds.width / 2) - (imBounds.width / 2);
		int ySpacer = (buttonBounds.height / 2) - (imBounds.height / 2);
		
		gc.drawImage(toUse, buttonBounds.x + xSpacer, buttonBounds.y + ySpacer);
	}
	
	private void drawBorderedToolbar(GC gc, RibbonToolbar tb) {
		List<RibbonToolbarGrouping> groupings = tb.getGroupings();

		for (RibbonToolbarGrouping group : groupings) {					
			List<RibbonButton> buttons = group.getItems();
			
			// no buttons? no border
			if (buttons.size() == 0)
				continue;
			
			// first, draw the entire border
			gc.setForeground(mOuterToolbarBorderColor);
			Rectangle bounds = group.getBounds();
			Rectangle box = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height+1);
			// top line, starts 1px in, ends 1px in
			gc.drawLine(box.x+1, box.y, box.x+box.width-2, box.y);
			// bottom line, same
			gc.drawLine(box.x+1, box.y+box.height, box.x+box.width-2, box.y+box.height);
			
			// left side top
			gc.setBackground(mOuterToolbarBorderMidpointColor);
			gc.fillGradientRectangle(box.x, box.y+1, 1, 1+(box.height/2), true);
			// right side top
			gc.fillGradientRectangle(box.x+box.width-1, box.y+1, 1, 1+(box.height/2), true);
			
			// left side bottom
			gc.setForeground(mOuterToolbarBorderMidpointColor);
			gc.setBackground(mOuterToolbarBorderColor);
			gc.fillGradientRectangle(box.x, box.y+1+(box.height/2), 1, (box.height/2), true);
			// right side bottom
			gc.fillGradientRectangle(box.x+box.width-1, box.y+1+(box.height/2), 1, (box.height/2), true);
			
			// ok, the outer border is done, now we draw buttons, inner borders will be decorations
			// the button that differs completely here is (thankfully) the non-selected or non-hovered state, so we deal with that first
			for (RibbonButton button : buttons) {
				int state = getState(button);

				if (button.isEnabled()) {
					if (button.isSplit() && state != STATE_NONE) 
						drawSmallSplitToolbarButton(gc, state, button);
					else
						drawSmallToolbarButton(gc, state, button.getBounds(), button.getToolbarSide(), false);				
				}
				
				drawButtonImage(gc, STATE_NONE, button);
				drawButtonText(gc, state, TYPE_BUTTON_SMALL, button);				
			}
		}
	}
	
	// these are a bit of a pain in the neck.. there's little equality to other splits once one looks at the pixel-level
	private void drawSmallSplitToolbarButton(GC gc, int state, RibbonButton button) {
		boolean skipLeftSideBorders = false;
		boolean skipRightSideBorders = false;
		boolean skipLeftSideBordersOnRightSide = false;
		
		// if we have a double-sided (so to speak) item, we pretend it's one side first, then we simply draw the missing bits later
		// this was much easier than adding even more if-statements to the various draw methods
		int tbSide = button.getToolbarSide();
		if (tbSide == RibbonButton.TOOLBAR_SIDE_LEFT_AND_RIGHT)
			tbSide = RibbonButton.TOOLBAR_SIDE_RIGHT;
		
		if (button.isLeftHovered() || button.isLeftSelected()) {				
			if (button.isLeftSelected()) {
				// draw left in hover_select
				drawSmallToolbarButton(gc, state, button.getLeftBounds(), tbSide == RibbonButton.TOOLBAR_SIDE_LEFT ? RibbonButton.TOOLBAR_SIDE_LEFT : RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT, false);

				if (button.isLeftHovered()) {
					// draw right in just hover - but the special hover
					drawSmallToolbarButton(gc, STATE_HOVER, button.getRightBounds(), tbSide == RibbonButton.TOOLBAR_SIDE_RIGHT ? RibbonButton.TOOLBAR_SIDE_RIGHT : RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT, true);
				}
				else if (button.isRightHovered()) {
					// draw left in selected state, draw right in hover
					drawSmallToolbarButton(gc, STATE_SELECTED, button.getLeftBounds(), tbSide == RibbonButton.TOOLBAR_SIDE_LEFT ? RibbonButton.TOOLBAR_SIDE_LEFT : RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT, false);
					drawSmallToolbarButton(gc, STATE_HOVER, button.getRightBounds(), tbSide == RibbonButton.TOOLBAR_SIDE_RIGHT ? RibbonButton.TOOLBAR_SIDE_RIGHT : RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT, false);
					skipLeftSideBorders = true;
				}
				else {
					// draw right plainly
					drawSmallToolbarButton(gc, STATE_NONE, button.getRightBounds(), tbSide == RibbonButton.TOOLBAR_SIDE_RIGHT ? RibbonButton.TOOLBAR_SIDE_RIGHT : RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT, false);
				}
			}
			else {
				// just hovered
				drawSmallToolbarButton(gc, state, button.getLeftBounds(), tbSide == RibbonButton.TOOLBAR_SIDE_LEFT ? RibbonButton.TOOLBAR_SIDE_LEFT : RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT, false);			
				// draw right in just hover - but the special hover
				drawSmallToolbarButton(gc, STATE_HOVER, button.getRightBounds(), tbSide == RibbonButton.TOOLBAR_SIDE_RIGHT ? RibbonButton.TOOLBAR_SIDE_RIGHT : RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT, true);
			}
			
		}
		else if (button.isRightHovered() || button.isRightSelected()) {
			if (button.isRightSelected()) {
				drawSmallToolbarButton(gc, STATE_HOVER, button.getLeftBounds(), tbSide == RibbonButton.TOOLBAR_SIDE_LEFT ? RibbonButton.TOOLBAR_SIDE_LEFT : RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT, true);
				drawSmallToolbarButton(gc, STATE_HOVER_AND_SELECTED, button.getRightBounds(), tbSide == RibbonButton.TOOLBAR_SIDE_RIGHT ? RibbonButton.TOOLBAR_SIDE_RIGHT : RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT, false);
				skipLeftSideBordersOnRightSide = true;
			}
			else {
				drawSmallToolbarButton(gc, state, button.getLeftBounds(), tbSide == RibbonButton.TOOLBAR_SIDE_LEFT ? RibbonButton.TOOLBAR_SIDE_LEFT : RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT, true);
				drawSmallToolbarButton(gc, STATE_HOVER, button.getRightBounds(), tbSide == RibbonButton.TOOLBAR_SIDE_RIGHT ? RibbonButton.TOOLBAR_SIDE_RIGHT : RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT, false);				
			}
		}
		
		// draw split as it's easier doing here than having some if methods check it inside one of the other methods
		gc.setForeground(mSplitDividerLineTopVertical_Hover);
		gc.setBackground(mSplitDividerLineMidVertical_Hover);
		gc.fillGradientRectangle(button.getRightBounds().x-1, button.getBounds().y, 1, 10, true);

		gc.setForeground(mSplitDividerLineMidVertical_Hover);
		gc.setBackground(mSplitDividerLineTopVertical_Hover);
		gc.fillGradientRectangle(button.getRightBounds().x-1, button.getBounds().y+10, 1, 10, true);
		
		// regardless of side we fill the right side inner border again, as it's mostly "not used"
		if (button.isLeftHovered()) {
			if (!skipRightSideBorders) {
				int x = button.getRightBounds().x + button.getRightBounds().width;
				int y = button.getRightBounds().y;
				
				int top = 8;				
				int bottom = 12;
				
				if (button.getToolbarSide() == RibbonButton.TOOLBAR_SIDE_RIGHT) {
					y -= 1;
					top = 7;
					bottom = 11;
				}
				else {
					x -= 1;
					y -= 2;
				}
				
				gc.setForeground(mToolbarSplitFillColorTopTop_NonHover);
				gc.setBackground(mToolbarSplitFillColorTopBottom_NonHover);
				gc.fillGradientRectangle(x, y+2, 1, top, true);
				
				gc.setForeground(mToolbarSplitFillColorBottomTop_NonHover);
				gc.setBackground(mToolbarSplitFillColorBottomBottom_NonHover);
				gc.fillGradientRectangle(x, y+2+top, 1, bottom, true);
			}
		}
		else if (button.isRightHovered()) {
			if (!skipLeftSideBordersOnRightSide) {
				// fill right side's left inner border first
				gc.setForeground(mToolbarSplitInnerBorderTop_Hover);
				gc.setBackground(mToolbarSplitInnerBorderTop_Hover);
				gc.fillGradientRectangle(button.getRightBounds().x, button.getRightBounds().y, 1, 8, true);
				gc.setBackground(mToolbarSplitInnerBorderBottom_Hover);
				gc.fillGradientRectangle(button.getRightBounds().x, button.getRightBounds().y+8, 1, 12, true);
			}
			
			if (!skipLeftSideBorders) {
				// left side calculations				
				int x = button.getLeftBounds().x-1; //TODO: why do I need the -1 on this x? weird
				int y = button.getLeftBounds().y;
				
				int top = 8;				
				int bottom = 12;
				
				if (button.getToolbarSide() == RibbonButton.TOOLBAR_SIDE_LEFT) {
					y += 1;
					top = 7;
					bottom = 11;
				}							

				// top part
				gc.setForeground(mToolbarSplitFillColorTopTop_NonHover);
				gc.setBackground(mToolbarSplitFillColorTopBottom_NonHover);
				gc.fillGradientRectangle(x, y, 1, top, true);
			
				// fill right side of left side from top to bottom, it's never shorter
				gc.fillGradientRectangle(button.getLeftBounds().x+button.getLeftBounds().width-1, button.getLeftBounds().y, 1, 8, true);

				// bottom part
				gc.setForeground(mToolbarSplitFillColorBottomTop_NonHover);
				gc.setBackground(mToolbarSplitFillColorBottomBottom_NonHover);
				gc.fillGradientRectangle(x, y+top, 1, bottom, true);
			
				// fill right side of left side from top to bottom, it's never shorter
				gc.fillGradientRectangle(button.getLeftBounds().x+button.getLeftBounds().width-1, button.getLeftBounds().y+8, 1, 12, true);
			}

		}		
		
		// check the buttonstyle again, if it was a double-sided, deal with that
		// as we pretended the button was "right sided" we just draw what's missing on the left side
		if (button.getToolbarSide() == RibbonButton.TOOLBAR_SIDE_LEFT_AND_RIGHT) {
			Rectangle bounds = button.getBounds();
			gc.setForeground(mOuterToolbarBorderColor);
			// paint the two border corner dots to make the border go around "smoothly"
			gc.drawLine(bounds.x-1, bounds.y, bounds.x-1, bounds.y);
			gc.drawLine(bounds.x-1, bounds.y+bounds.height-1, bounds.x-1, bounds.y+bounds.height-1);
		}
	}
	
	private void drawSmallToolbarButton(GC gc, int state, Rectangle bounds, int tbSide, boolean useSplitColors) {
		int width = bounds.width;
		int height = bounds.height;
		
		if (state == STATE_NONE) {
			// start with the fill, inner borders will draw on top
			gc.setBackground(mToolbarFillColorTop);
			
			gc.fillRectangle(bounds.x, bounds.y, width, height);
			
			gc.setForeground(mToolbarFillColorBottomTop);
			gc.setBackground(mToolbarFillColorBottomBottom);
			
			gc.fillGradientRectangle(bounds.x, bounds.y+8, width, height-9, true);
		}
		else if (state == STATE_HOVER) {
			// fills
			if (useSplitColors) {
				gc.setForeground(mToolbarSplitFillColorTopTop_NonHover);
				gc.setBackground(mToolbarSplitFillColorTopBottom_NonHover);				
			}
			else {
				gc.setForeground(mToolbarFillColorTopTop_Hover);
				gc.setBackground(mToolbarFillColorTopBottom_Hover);
			}
			gc.fillGradientRectangle(bounds.x, bounds.y+1, width, 7, true);
			
			if (useSplitColors) {
				gc.setForeground(mToolbarSplitFillColorBottomTop_NonHover);
				gc.setBackground(mToolbarSplitFillColorBottomBottom_NonHover);
			}
			else {
				gc.setForeground(mToolbarFillColorBottomTop_Hover);
				gc.setBackground(mToolbarFillColorBottomBottom_Hover);				
			}
			gc.fillGradientRectangle(bounds.x, bounds.y+1+7, width, 11, true);				
		}
		else if (state == STATE_SELECTED) {
			// selected has 2 lines that draw over the border at the top
			gc.setForeground(mToolbarOuterBorderTop_Selected);
			gc.drawLine(bounds.x-1, bounds.y-1, bounds.x+bounds.width-1, bounds.y-1);
			gc.setForeground(mToolbarOuterBorderTopTwo_Selected);
			gc.drawLine(bounds.x-1, bounds.y, bounds.x+bounds.width-1, bounds.y);
			// now we can fill
			gc.setForeground(mToolbarFillColorTopTop_Selected);
			gc.setBackground(mToolbarFillColorTopBottom_Selected);
			gc.fillGradientRectangle(bounds.x-1, bounds.y+1, bounds.width+1, 5, true);

			gc.setForeground(mToolbarFillColorBottomTop_Selected);
			gc.setBackground(mToolbarFillColorBottomBottom_Selected);
			gc.fillGradientRectangle(bounds.x-1, bounds.y+1+5, bounds.width+1, 13, true);
			
			// bottom line, acts as an inner line
			gc.setForeground(mToolbarInnerBorderBottomBottomLine_Selected);
			gc.drawLine(bounds.x-1, bounds.y+5+13+1, bounds.x+bounds.width-1, bounds.y+5+13+1);
			
			// bottom left corner has a pixel
			if (tbSide == RibbonButton.TOOLBAR_SIDE_LEFT) {
				gc.setForeground(mOuterToolbarBorderColor);
				gc.drawLine(bounds.x-1, bounds.y+5+13+1, bounds.x-1, bounds.y+5+13+1);
			}
		}
		else if (state == STATE_HOVER_AND_SELECTED) {
			int xTra = 0;
			if (tbSide == RibbonButton.TOOLBAR_SIDE_RIGHT)
				xTra = 1;
			
			// selected has 2 lines that draw over the border at the top
			gc.setForeground(mToolbarOuterBorderTop_HoverSelected);
			if (tbSide == RibbonButton.TOOLBAR_SIDE_LEFT)
				gc.drawLine(bounds.x, bounds.y-1, bounds.x+bounds.width-1+xTra, bounds.y-1);
			else 
				gc.drawLine(bounds.x-1, bounds.y-1, bounds.x+bounds.width-1+xTra, bounds.y-1);

			gc.setForeground(mToolbarOuterBorderTopTwo_HoverSelected);
			gc.drawLine(bounds.x-1, bounds.y, bounds.x+bounds.width-1+xTra, bounds.y);
			gc.setForeground(mToolbarOuterBorderTopThree_HoverSelected);
			gc.drawLine(bounds.x-1, bounds.y+1, bounds.x+bounds.width-1+xTra, bounds.y+1);
			// now we can fill
			gc.setForeground(mToolbarFillColorTopTop_HoverSelected);
			gc.setBackground(mToolbarFillColorTopBottom_HoverSelected);
			gc.fillGradientRectangle(bounds.x-1, bounds.y+2, bounds.width+1+xTra, 5, true);

			gc.setForeground(mToolbarFillColorBottomTop_HoverSelected);
			gc.setBackground(mToolbarFillColorBottomBottom_HoverSelected);
			gc.fillGradientRectangle(bounds.x-1, bounds.y+2+5, bounds.width+1+xTra, 12, true);
			
			// bottom line, acts as an inner line
			gc.setForeground(mToolbarInnerBorderBottomBottomLine_HoverSelected);
			gc.drawLine(bounds.x-1, bounds.y+5+13+1, bounds.x+bounds.width-1+xTra, bounds.y+5+13+1);
			
			// bottom left corner has a pixel
			if (tbSide == RibbonButton.TOOLBAR_SIDE_LEFT) {
				gc.setForeground(mOuterToolbarBorderColor);
				gc.drawLine(bounds.x-1, bounds.y+5+13+1, bounds.x-1, bounds.y+5+13+1);
			}
			else if (tbSide == RibbonButton.TOOLBAR_SIDE_RIGHT) {
				gc.setForeground(mOuterToolbarBorderColor);
				gc.drawLine(bounds.x-1+bounds.width+1, bounds.y+5+13+1, bounds.x-1+bounds.width+1, bounds.y+5+13+1);				
			}
			
		}
		
		if (state != STATE_SELECTED && state != STATE_HOVER_AND_SELECTED) {
			// ok, lets draw inner borders, the top one is the same on all
			if (state == STATE_NONE)
				gc.setForeground(mInnerToolbarBorderColorTop);
			else if (state == STATE_HOVER)
				gc.setForeground(mToolbarInnerBorderTop_Hover);
			gc.drawLine(bounds.x, bounds.y, bounds.x+width-1, bounds.y);
		
			// draw bottom inner
			if (state == STATE_NONE)
				gc.setForeground(mInnerToolbarBorderColorBottom);
			else if (state == STATE_HOVER)
				gc.setForeground(mToolbarInnerBorderBottomBottom_Hover);		
			gc.drawLine(bounds.x-1, bounds.y+bounds.height-1, bounds.x+width, bounds.y+bounds.height-1);
		}
		
		// draw the left corner pixel if we happen to be the right button
		// selected state doesn't have it
		if (state != STATE_SELECTED && state != STATE_HOVER_AND_SELECTED) {
			if (tbSide == RibbonButton.TOOLBAR_SIDE_LEFT) {
				if (state == STATE_NONE)
					gc.setForeground(mInnerToolbarBorderCornerColor);
				else if (state == STATE_HOVER)
					gc.setForeground(mOuterToolbarBorderColor);
				
				gc.drawLine(bounds.x-1, bounds.y, bounds.x-1, bounds.y);
				gc.drawLine(bounds.x-1, bounds.y+bounds.height-1, bounds.x-1, bounds.y+bounds.height-1);
			}
		}
		
		if (tbSide == RibbonButton.TOOLBAR_SIDE_RIGHT) {
			if (state == STATE_NONE)				
				gc.setForeground(mInnerToolbarBorderCornerColor);
			else if (state == STATE_HOVER)
				gc.setForeground(mOuterToolbarBorderColor);
			
			// selected state doesn't have it
			if (state != STATE_SELECTED && state != STATE_HOVER_AND_SELECTED) {
				gc.drawLine(bounds.x+width, bounds.y, bounds.x+width, bounds.y);
				gc.drawLine(bounds.x+width, bounds.y+bounds.height-1, bounds.x+width, bounds.y+bounds.height-1);
			}
		}
						
		if (state == STATE_NONE) {
			// draw all dividers
			if (tbSide == RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT || tbSide == RibbonButton.TOOLBAR_SIDE_LEFT) {
				gc.setForeground(mInnerToolbarDividerColorLeft);
				gc.drawLine(bounds.x+bounds.width, bounds.y, bounds.x+bounds.width, bounds.y+bounds.height-1);
			}
			if (tbSide == RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT || tbSide == RibbonButton.TOOLBAR_SIDE_RIGHT) {
				gc.setForeground(mInnerToolbarBorderColorBottom);
				gc.drawLine(bounds.x-1, bounds.y, bounds.x-1, bounds.y+bounds.height-1);
			}
		}
		else if (state == STATE_HOVER || state == STATE_SELECTED || state == STATE_HOVER_AND_SELECTED) {
			// draw all dividers
			if (tbSide == RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT || tbSide == RibbonButton.TOOLBAR_SIDE_LEFT) {
				// hover gives the right bar a different color
				gc.setForeground(mOuterToolbarBorderColor);
				gc.drawLine(bounds.x+bounds.width, bounds.y, bounds.x+bounds.width, bounds.y+bounds.height-1);
			}
		}

		// ok, all that's left is the left side and right side inner borders, which are gradients
		if (state != STATE_SELECTED && state != STATE_HOVER_AND_SELECTED) {
			if (tbSide == RibbonButton.TOOLBAR_SIDE_LEFT 
					|| tbSide == RibbonButton.TOOLBAR_SIDE_RIGHT
					|| (tbSide == RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT && state == STATE_HOVER)) {
				
				if (state == STATE_NONE) {
					gc.setForeground(mInnerToolbarBorderColorTop);
					gc.setBackground(mInnerToolbarBorderSideMidpointColor);
				}
				else if (state == STATE_HOVER) {
					gc.setForeground(mToolbarInnerBorderTop_Hover);
					gc.setBackground(mToolbarInnerBorderTopBottom_Hover);
				}
				
				if (tbSide == RibbonButton.TOOLBAR_SIDE_LEFT) {
					gc.fillGradientRectangle(bounds.x-1, bounds.y+1, 1, 7, true);
					if (state == STATE_HOVER) 
						gc.fillGradientRectangle(bounds.x+bounds.width-1, bounds.y+1, 1, 7, true);
				}
				else {
					if (tbSide == RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT)
						gc.fillGradientRectangle(bounds.x+bounds.width-1, bounds.y+1, 1, 7, true);
					else
						gc.fillGradientRectangle(bounds.x+bounds.width, bounds.y+1, 1, 7, true);
					
					if (state == STATE_HOVER) {
						if (tbSide == RibbonButton.TOOLBAR_SIDE_RIGHT || tbSide == RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT)
							gc.fillGradientRectangle(bounds.x-1, bounds.y, 1, 8, true);
						else
							gc.fillGradientRectangle(bounds.x-1, bounds.y+1, 1, 7, true);
					}
				}
				
				if (state == STATE_NONE) {
					gc.setForeground(mInnerToolbarBorderSideMidpointColor);
					gc.setBackground(mInnerToolbarBorderColorBottom);
				}
				else if (state == STATE_HOVER) {
					gc.setForeground(mToolbarInnerBorderBottomTop_Hover);
					gc.setBackground(mToolbarInnerBorderBottomBottom_Hover);
				}
				
				if (tbSide == RibbonButton.TOOLBAR_SIDE_LEFT) {
					gc.fillGradientRectangle(bounds.x-1, bounds.y+1+7, 1, 11, true);
					if (state == STATE_HOVER)
						gc.fillGradientRectangle(bounds.x+bounds.width-1, bounds.y+1+7, 1, 11, true);
				}
				else {
					if (tbSide == RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT)
						gc.fillGradientRectangle(bounds.x+bounds.width-1, bounds.y+1+7, 1, 11, true);
					else
						gc.fillGradientRectangle(bounds.x+bounds.width, bounds.y+1+7, 1, 11, true);
					
					if (state == STATE_HOVER)
						gc.fillGradientRectangle(bounds.x-1, bounds.y+1+7, 1, 11, true);
				}
			}
				
			// hover bonus, draws one pixel further in at the top left and bottom left on the left side
			if (state == STATE_HOVER) {
				if (tbSide == RibbonButton.TOOLBAR_SIDE_LEFT) {
					gc.setForeground(mToolbarInnerBorderTop_Hover);
					gc.drawLine(bounds.x, bounds.y+1, bounds.x, bounds.y+1);					
					gc.setForeground(mToolbarInnerBorderBottomBottom_Hover);
					gc.drawLine(bounds.x, bounds.y+bounds.height-2, bounds.x, bounds.y+bounds.height-2);
				}
			}
		}
			
	}
	
	private void drawSmallSplitButton(GC gc, int state, AbstractRibbonGroupItem item) {
		Rectangle leftBounds = item.getLeftBounds();
		Rectangle rightBounds = item.getRightBounds();
		Rectangle buttonBounds = item.getBounds();
				
		if (item.isLeftHovered() || item.isLeftSelected()) {
						
			if (item.isLeftHovered() && !item.isLeftSelected()) {
				drawButtonOuterBorder(gc, state, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, true, true, true);
				drawButtonFills(gc, state, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, leftBounds.x+3, leftBounds.y, leftBounds.width-3, leftBounds.height);
				drawButtonFills(gc, state, SPLIT_RIGHT_NOT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x, rightBounds.y, rightBounds.width, rightBounds.height);
				drawButtonInnerBorder(gc, state, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, leftBounds.x, leftBounds.y, leftBounds.width, leftBounds.height, true, true, false, true);
				drawButtonDecorations(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);

				drawSplitButtonExtras(gc, STATE_HOVER, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x, rightBounds.y, rightBounds.width, rightBounds.height);
			}
			else {
				// left is selected
				if ((item.getStyle() & AbstractRibbonGroupItem.STYLE_TOGGLE) != 0) {					
					if (item.isLeftSelected() && item.isRightHovered()) {
						drawButtonOuterBorder(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width-2, buttonBounds.height, true, true, true, true);
						drawButtonFills(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
						drawButtonInnerBorder(gc, STATE_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, true, true, false);

						drawButtonOuterBorder(gc, STATE_HOVER, SPLIT_RIGHT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x-1, rightBounds.y, rightBounds.width+1, rightBounds.height, false, true, true, true);
						drawButtonFills(gc, STATE_HOVER, SPLIT_RIGHT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x, rightBounds.y, rightBounds.width, rightBounds.height);
					}
					else {
						if (state == STATE_HOVER_AND_SELECTED) {
							drawButtonOuterBorder(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width-2, buttonBounds.height, true, true, true, true);
							drawButtonFills(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
							drawButtonInnerBorder(gc, STATE_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, true, true, false);
	
							drawButtonOuterBorder(gc, STATE_HOVER, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x-1, rightBounds.y, rightBounds.width+1, rightBounds.height, false, true, true, true);
							drawButtonFills(gc, STATE_HOVER, SPLIT_RIGHT_NOT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x, rightBounds.y, rightBounds.width, rightBounds.height);
						}
						else if (state == STATE_SELECTED) {
							drawButtonOuterBorder(gc, STATE_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width-2, buttonBounds.height, true, true, true, true);
							drawButtonFills(gc, STATE_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
							drawButtonInnerBorder(gc, STATE_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, true, true, false);
	
							drawButtonOuterBorder(gc, STATE_SELECTED, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x-1, rightBounds.y, rightBounds.width+1, rightBounds.height, false, true, true, true);
							drawButtonFills(gc, STATE_SELECTED, SPLIT_RIGHT_NOT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x, rightBounds.y, rightBounds.width, rightBounds.height);						
						}
	
						drawButtonDecorations(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, false, true, false, true, false);
						drawButtonDecorations(gc, STATE_HOVER, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, false, true, false, true, false, false);
					}
					
				}					
				else {	
					drawButtonOuterBorder(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width-2, buttonBounds.height, true, true, true, true);
					drawButtonFills(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
					drawButtonInnerBorder(gc, STATE_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, true, true, false);

					drawButtonOuterBorder(gc, STATE_HOVER, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x-1, rightBounds.y, rightBounds.width+1, rightBounds.height, false, true, true, true);
					drawButtonFills(gc, STATE_HOVER, SPLIT_RIGHT_NOT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x, rightBounds.y, rightBounds.width, rightBounds.height);

					drawButtonDecorations(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, false, true, false, true, false);
					drawButtonDecorations(gc, STATE_HOVER, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, false, true, false, true, false, false);
				}				

				drawSplitButtonExtras(gc, STATE_HOVER_AND_SELECTED, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x, rightBounds.y, rightBounds.width, rightBounds.height);
			}
		}
		else if (item.isRightHovered() || item.isRightSelected()) {
			if (item.isRightHovered() && !item.isRightSelected()) {
				drawButtonOuterBorder(gc, state, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, true, true, true);
				drawButtonFills(gc, state, SPLIT_RIGHT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x, rightBounds.y, rightBounds.width, rightBounds.height);
				drawButtonFills(gc, state, SPLIT_LEFT_NOT_ACTIVE, TYPE_BUTTON_SMALL, leftBounds.x+3, leftBounds.y, leftBounds.width-3, leftBounds.height);
				drawButtonInnerBorder(gc, state, SPLIT_RIGHT_ACTIVE, TYPE_BUTTON_SMALL, leftBounds.x, leftBounds.y, leftBounds.width-1, leftBounds.height, true, true, true, true);
				drawButtonInnerBorder(gc, state, SPLIT_RIGHT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x-2, rightBounds.y, rightBounds.width+2, rightBounds.height, false, true, true, true);

				drawButtonDecorations(gc, state, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
				drawSplitButtonExtras(gc, STATE_HOVER, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x, rightBounds.y, rightBounds.width, rightBounds.height);
			}
			else {
				// right is selected, draw selected button first
				drawButtonOuterBorder(gc, STATE_HOVER, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
				drawButtonFills(gc, STATE_HOVER, SPLIT_LEFT_NOT_ACTIVE, TYPE_BUTTON_SMALL, leftBounds.x+3, leftBounds.y, leftBounds.width-3, leftBounds.height);

				drawButtonInnerBorder(gc, STATE_HOVER, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
				
				drawButtonOuterBorder(gc, STATE_SELECTED, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x-2, rightBounds.y, rightBounds.width+2, rightBounds.height, false, true, true, true);
				drawButtonOuterBorder(gc, STATE_HOVER_AND_SELECTED, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x-1, rightBounds.y, rightBounds.width+1, rightBounds.height, false, false, false, true);
				drawButtonFills(gc, STATE_SELECTED, SPLIT_LEFT_NOT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x, rightBounds.y, rightBounds.width, rightBounds.height);
				drawButtonInnerBorder(gc, STATE_SELECTED, TYPE_BUTTON_SMALL, rightBounds.x-2, rightBounds.y, rightBounds.width+2, rightBounds.height, false, true, true, false);

				drawButtonDecorations(gc, STATE_HOVER, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, false, true, false, false, false);
				drawButtonDecorations(gc, STATE_SELECTED, TYPE_BUTTON_SMALL, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, false, true, false, false, false, true);
				drawSplitButtonExtras(gc, STATE_HOVER, SPLIT_LEFT_ACTIVE, TYPE_BUTTON_SMALL, rightBounds.x, rightBounds.y, rightBounds.width, rightBounds.height);

			}
		}			
		
	}
	
	private void drawBigSplitButton(GC gc, int state, AbstractRibbonGroupItem item) {
		Rectangle topBounds = item.getTopBounds();
		Rectangle bottomBounds = item.getBottomBounds();
		Rectangle buttonBounds = item.getBounds();
							
		//System.err.println(state + " " + item + " " + item.isTopHovered() + " " + item.isTopSelected() + " " + item.isBottomHovered() + " " + item.isBottomSelected());

		// the following is an obvious no-no on a split button. You can't hover over a top while the bottom is selected (as there are no permanent selections on split buttons)
		// so how do we get to this combo of selection and hover?
		// this happens when a menu popped up and the user decides to click the top part of the split button instead of elsewhere (which closes the menu).
		// the bottom stays pressed while the menu is open and the button isn't quite registering what the user does while it is
		// so the various listeners are a little confused at this point, and therefore we need to help it by telling the button what the proper state should be
		if (item.isTopHovered() && item.isBottomSelected()) {
			item.setBottomSelected(false);
			item.setBottomHovered(false);
			item.setTopHovered(true);
			item.setTopSelected(true);
		}
		
		if (item.isTopHovered() || item.isTopSelected()) {
			if (item.isTopHovered() && !item.isTopSelected()) {
				drawButtonOuterBorder(gc, state, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, topBounds.x, topBounds.y, topBounds.width, topBounds.height-1, true, true, true, true);
				drawButtonOuterBorder(gc, state, SPLIT_BOTTOM_NOT_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1, true, true, true, true);
				drawButtonFills(gc, state, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, topBounds.x, topBounds.y, topBounds.width, topBounds.height-1);
				drawButtonFills(gc, state, SPLIT_BOTTOM_NOT_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1);
				drawButtonInnerBorder(gc, state, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, topBounds.x, topBounds.y, topBounds.width, topBounds.height-1, true, true, true, true);
				drawButtonInnerBorder(gc, state, SPLIT_BOTTOM_NOT_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1, true, true, true, true);
				drawButtonDecorations(gc, state, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
				
				drawSplitButtonExtras(gc, STATE_HOVER, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y, bottomBounds.width, bottomBounds.height);
			}
			else {
				// top is selected
				if ((item.getStyle() & AbstractRibbonGroupItem.STYLE_TOGGLE) != 0) {					
					if (item.isTopSelected() && item.isBottomHovered()) {
						drawButtonOuterBorder(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
						drawButtonFills(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
						drawButtonInnerBorder(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
		
						drawButtonOuterBorder(gc, STATE_HOVER, SPLIT_BOTTOM_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1, true, true, true, true);
						drawButtonFills(gc, STATE_HOVER, SPLIT_BOTTOM_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1);
						drawButtonInnerBorder(gc, STATE_HOVER, SPLIT_BOTTOM_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1, true, true, true, true);
						drawButtonDecorations(gc, STATE_HOVER_AND_SELECTED, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, true, true, true, true, true);
						
						drawSplitButtonExtras(gc, STATE_HOVER_AND_SELECTED, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y, bottomBounds.width, bottomBounds.height);								
					}
					else {
						if (state == STATE_HOVER_AND_SELECTED) {
							// top is selected				
							drawButtonOuterBorder(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
							drawButtonFills(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
							drawButtonInnerBorder(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
			
							drawButtonOuterBorder(gc, STATE_HOVER, SPLIT_BOTTOM_NOT_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1, true, true, true, true);
							drawButtonFills(gc, STATE_HOVER, SPLIT_BOTTOM_NOT_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1);
							drawButtonInnerBorder(gc, STATE_HOVER, SPLIT_BOTTOM_NOT_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1, true, true, true, true);
							drawButtonDecorations(gc, STATE_HOVER_AND_SELECTED, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, true, true, true, true, true);
							
							drawSplitButtonExtras(gc, STATE_HOVER_AND_SELECTED, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y, bottomBounds.width, bottomBounds.height);
						}
						else if (state == STATE_SELECTED) {
							drawButtonOuterBorder(gc, STATE_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
							drawButtonFills(gc, STATE_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
							drawButtonInnerBorder(gc, STATE_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
							drawButtonDecorations(gc, STATE_SELECTED, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, true, true, true, true, true);
							
							drawSplitButtonExtras(gc, STATE_HOVER_AND_SELECTED, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y, bottomBounds.width, bottomBounds.height);								
						}
					}
				}
				else {				
					// top is selected				
					drawButtonOuterBorder(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
					drawButtonFills(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
					drawButtonInnerBorder(gc, STATE_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
	
					drawButtonOuterBorder(gc, STATE_HOVER, SPLIT_BOTTOM_NOT_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1, true, true, true, true);
					drawButtonFills(gc, STATE_HOVER, SPLIT_BOTTOM_NOT_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1);
					drawButtonInnerBorder(gc, STATE_HOVER, SPLIT_BOTTOM_NOT_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1, true, true, true, true);
					drawButtonDecorations(gc, STATE_HOVER_AND_SELECTED, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, true, true, true, true, true);
					
					drawSplitButtonExtras(gc, STATE_HOVER_AND_SELECTED, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y, bottomBounds.width, bottomBounds.height);
				}
			}
		}
		else if (item.isBottomHovered() || item.isBottomSelected()) {
			if (item.isBottomHovered() && !item.isBottomSelected()) {
				drawButtonOuterBorder(gc, state, SPLIT_TOP_NOT_ACTIVE, TYPE_BUTTON_BIG, topBounds.x, topBounds.y, topBounds.width, topBounds.height-1, true, true, true, true);
				drawButtonOuterBorder(gc, state, SPLIT_BOTTOM_NOT_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1, true, true, true, true);
				drawButtonFills(gc, state, SPLIT_TOP_NOT_ACTIVE, TYPE_BUTTON_BIG, topBounds.x, topBounds.y, topBounds.width, topBounds.height-1);
				drawButtonFills(gc, state, SPLIT_BOTTOM_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1);
				drawButtonInnerBorder(gc, state, SPLIT_TOP_NOT_ACTIVE, TYPE_BUTTON_BIG, topBounds.x, topBounds.y, topBounds.width, topBounds.height-1, true, true, true, true);
				drawButtonInnerBorder(gc, state, SPLIT_BOTTOM_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y+1, bottomBounds.width, bottomBounds.height-1, true, true, true, true);
				drawButtonDecorations(gc, state, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
				
				drawSplitButtonExtras(gc, STATE_HOVER, SPLIT_BOTTOM_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y, bottomBounds.width, bottomBounds.height);
			}
			else {
				// bottom is selected, draw selected button first
				drawButtonOuterBorder(gc, STATE_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
				drawButtonFills(gc, STATE_HOVER_AND_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
				drawButtonInnerBorder(gc, STATE_SELECTED, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);

				// override by drawing the top
				drawButtonOuterBorder(gc, STATE_HOVER, SPLIT_TOP_ACTIVE, TYPE_BUTTON_BIG, topBounds.x, topBounds.y, topBounds.width, topBounds.height-1, true, true, true, true);
				drawButtonFills(gc, STATE_HOVER, SPLIT_TOP_NOT_ACTIVE, TYPE_BUTTON_BIG, topBounds.x, topBounds.y, topBounds.width, topBounds.height-1);
				// fill bottom
				drawButtonFills(gc, STATE_HOVER_AND_SELECTED, SPLIT_BOTTOM_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y, bottomBounds.width, bottomBounds.height);
				drawButtonInnerBorder(gc, STATE_HOVER, SPLIT_TOP_NOT_ACTIVE, TYPE_BUTTON_BIG, topBounds.x, topBounds.y, topBounds.width, topBounds.height-1, true, true, true, true);
				drawButtonDecorations(gc, STATE_HOVER_AND_SELECTED, SPLIT_BOTTOM_ACTIVE, TYPE_BUTTON_BIG, buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height, true, true, true, true, true, true);
				
				drawSplitButtonExtras(gc, STATE_HOVER_AND_SELECTED, SPLIT_BOTTOM_ACTIVE, TYPE_BUTTON_BIG, bottomBounds.x, bottomBounds.y, bottomBounds.width, bottomBounds.height);
			}
		}			
	}
	
	private int getState(AbstractRibbonGroupItem item) {
		int state = STATE_NONE;
		if (item.isHoverButton() && !item.isSelected())
			state = STATE_HOVER;
		else if (item.isSelected() && !item.isHoverButton())
			state = STATE_SELECTED;
		else if (item.isSelected() && item.isHoverButton())
			state = STATE_HOVER_AND_SELECTED;
		
		return state;
	}
	
	public void drawButtonImage(GC gc, int buttonSize, AbstractRibbonGroupItem item) {
		Image toUse = item.getImage(); 
		if (!item.isEnabled())
			toUse = item.getDisabledImage() == null ? toUse : item.getDisabledImage();
		
		if (toUse == null)
			return;
		
		Rectangle imBounds = toUse.getBounds();
		Rectangle itemBounds = item.getBounds();
		
		int border = 2*2; // -4 as border is 2 * 2 and we don't let any image take up border property
		
		if (buttonSize == BUTTON_BIG) {
			int maxSpace = itemBounds.width - border; 
			int diff = Math.abs(maxSpace - imBounds.width);
			int spacer = diff/2;
			int extra = (diff % 2 != 0) ? 1 : 0;
			
			gc.drawImage(toUse, itemBounds.x+2+spacer+extra, itemBounds.y+3);
		}
		else {
			int spacer = 0;
			int space = 22;
			space -= 4; // border
			int leftOver = space - imBounds.height;

			// vertical image alignment
			switch (item.getImageVerticalAlignment()) {
				default:
				case SWT.NONE:
				case SWT.CENTER:
					spacer = leftOver/2;
					break;
				case SWT.TOP:
					spacer = 1;
					break;
				case SWT.BOTTOM:
					spacer = (itemBounds.height - 4 - 1 - imBounds.height);
					break;
			}
			
			int offsetX = 0;
			int offsetY = 0;
			
			if (item.isToolbarButton()) {
				offsetX = -1;
				offsetY = -1;
			}
				
							
			gc.drawImage(toUse, itemBounds.x+4+offsetX, itemBounds.y+2+spacer+offsetY);
		}
	}
	
	public void drawButtonText(GC gc, int state, int buttonSize, AbstractRibbonGroupItem item) {
		String toDraw = item.getName();
		
		int maxSpace = item.getBounds().width - 4; // -4 as border is 2 * 2 and we don't let any image take up border property
		int spacer = 0;
		int extra = 0;
		
		boolean anyText = false;
		
		if (toDraw != null && toDraw.length() != 0) {
			anyText = true;
			Point strExtent = gc.stringExtent(toDraw);
			int diff = Math.abs(maxSpace - strExtent.x);
			spacer = diff/2;
			extra = (diff % 2 != 0) ? 1 : 0;		
		}
		
		int yStart = item.getBounds().y+38;
		if (buttonSize == BUTTON_SMALL)
			yStart = item.getBounds().y+4;
		
		Image toUse = item.getImage(); 
		if (!item.isEnabled())
			toUse = item.getDisabledImage() == null ? toUse : item.getDisabledImage();

		Rectangle imBounds = (toUse == null ? new Rectangle(0, 0, 0, 0) : toUse.getBounds());
		boolean drawArrow = (item.isArrow() || item.isSplit());
		
		int xStart = item.getBounds().x+3+spacer+extra;
		if (buttonSize == BUTTON_SMALL)
			xStart = item.getBounds().x+3+imBounds.width+4;
		
		if (buttonSize == BUTTON_BIG && item.isTwoLineText() && anyText) {
			StringTokenizer st = new StringTokenizer(toDraw, "\n");
			int curY = yStart;
			int count = 0;
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				
				Point tokenStrExtent = gc.stringExtent(token);
				int tokenDiff = Math.abs(maxSpace - tokenStrExtent.x);
				int tokenSpacer = tokenDiff/2;
				int tokenExtra = (tokenDiff % 2 != 0) ? 1 : 0;	
				
				xStart = item.getBounds().x+3+tokenSpacer+tokenExtra; 
				if (count == 1)
					xStart -= 5; // -5 = arrow width
				
				if (!item.isEnabled()) {
					gc.setForeground(mTextFadedRight_Disabled);
					gc.drawText(token, xStart, curY, true);							
					gc.setForeground(mTextFadedLeft_Disabled);
					gc.drawText(token, xStart, curY, true);							
					gc.setForeground(mTextColor_Disabled);
					gc.drawText(token, xStart, curY, true);							
				}
				else {
					gc.setForeground(mTextColor);
					gc.drawText(token, xStart, curY, true);							
				}

				if (count == 1 && drawArrow) {
					drawArrow(gc, xStart + tokenStrExtent.x + 3, curY+5, state, item.isEnabled());
				}

				curY += gc.stringExtent(token).y;				
				
				count++;
			}
		}
		else {
			xStart++;
			
			// draw text
			if (anyText) {
				if (item.getName() != null) {
					if (!item.isEnabled()) {
						gc.setForeground(mTextFadedRight_Disabled);
						gc.drawText(toDraw, xStart, yStart, true);							
						gc.setForeground(mTextFadedLeft_Disabled);
						gc.drawText(toDraw, xStart, yStart, true);							
						gc.setForeground(mTextColor_Disabled);
						gc.drawText(toDraw, xStart, yStart, true);							
					}
					else {
						gc.setForeground(mTextColor);
						gc.drawText(toDraw, xStart, yStart, true);							
					}
				}
			}
			
			if (drawArrow) {
				if (buttonSize == BUTTON_BIG) {
					//System.err.println(token +"" + tokenStrExtent.x);
					drawArrow(gc, xStart + (item.getBounds().width/2)-10, 58, state, item.isEnabled());
				}
				else {					
					xStart = item.getBounds().x+item.getBounds().width-4-5+1;
					
					drawArrow(gc, xStart, yStart+5, state, item.isEnabled());
				}
			}
		}
	}
		
	public void drawSplitButtonExtras(GC gc, int state, int split, int buttonSize, int x, int y, int width, int height) {
		// figure out the middle point
		int middleWidth = (width/2);
		int extra = 0;
		if (width % 2 != 0)
			extra = 1;

		// line just below the split, usually the top inner border, but it's different on a split button
		if (buttonSize == BUTTON_BIG) {
			if (state == STATE_HOVER_AND_SELECTED) {
				if (split == SPLIT_BOTTOM_ACTIVE) {
					gc.setForeground(mSplitBottomSelectedTopLineLeft_Hover);
					gc.setBackground(mSplitBottomSelectedTopLineMid_Hover);
					
					gc.fillGradientRectangle(x+1, y+2, middleWidth-1, 1, false);

					gc.setForeground(mSplitBottomSelectedTopLineMid_Hover);
					gc.setBackground(mSplitBottomSelectedTopLineLeft_Hover);

					gc.fillGradientRectangle(x+middleWidth-1, y+2, middleWidth+extra+1, 1, false);
				}
				else if (split == SPLIT_TOP_ACTIVE) {
					gc.setForeground(mSplitBottomSelectedTopLineLeft_Selected);
					gc.setBackground(mSplitBottomSelectedTopLineMid_Selected);
					
					gc.fillGradientRectangle(x+1, y+2, middleWidth-1, 1, false);

					gc.setForeground(mSplitBottomSelectedTopLineMid_Selected);
					gc.setBackground(mSplitBottomSelectedTopLineLeft_Selected);

					gc.fillGradientRectangle(x+middleWidth-1, y+2, middleWidth+extra+1, 1, false);					
				}
			}
			else if (state == STATE_HOVER) {
				// missing corner pixels of top inner corner of bottom split section
				gc.setForeground(mButtonInnerSideBottomTop_Hover);
				gc.drawLine(x+1, y+2, x+1, y+2);
				gc.drawLine(x+width-1, y+2, x+width-1, y+2);
			}
			
			// draw center split
			if (state == STATE_HOVER_AND_SELECTED && split == SPLIT_TOP_ACTIVE) {
				gc.setForeground(mSplitDividerLineLeft_Selected);
				gc.setBackground(mSplitDividerLineMid_Selected);
			}
			else {
				gc.setForeground(mSplitDividerLineLeft_Hover);
				gc.setBackground(mSplitDividerLineMid_Hover);				
			}
			
			gc.fillGradientRectangle(x+1, y+1, middleWidth-1, 1, false);

			if (state == STATE_HOVER_AND_SELECTED && split == SPLIT_TOP_ACTIVE) {
				gc.setForeground(mSplitDividerLineMid_Selected);
				gc.setBackground(mSplitDividerLineLeft_Selected);
			}
			else {
				gc.setForeground(mSplitDividerLineMid_Hover);
				gc.setBackground(mSplitDividerLineLeft_Hover);				
			}
			gc.fillGradientRectangle(x+middleWidth-1, y+1, middleWidth+extra+1, 1, false);
			
			if (state == STATE_HOVER_AND_SELECTED) {
				if (split == SPLIT_TOP_ACTIVE) {
					gc.setForeground(mButtonOuterSideBottomTop_Selected);
					gc.drawLine(x, y+2, x, y+2);
					gc.drawLine(x+width, y+2, x+width, y+2);
				}
				else {
					gc.setForeground(mButtonOuterSideBottomTop_Hover);
					gc.drawLine(x, y+2, x, y+2);
					gc.drawLine(x+width, y+2, x+width, y+2);				
				}
			}							
		}
		else if (buttonSize == BUTTON_SMALL) {
			// draw center split
			if (state == STATE_HOVER_AND_SELECTED && split == SPLIT_TOP_ACTIVE) {
				gc.setForeground(mSplitDividerLineTopVertical_Selected);
				gc.setBackground(mSplitDividerLineMidVertical_Selected);
			}
			else {
				gc.setForeground(mSplitDividerLineTopVertical_Hover);
				gc.setBackground(mSplitDividerLineMidVertical_Hover);				
			}
			
			gc.fillGradientRectangle(x+width-12, y+1, 1, 10, true);

			if (state == STATE_HOVER_AND_SELECTED && split == SPLIT_TOP_ACTIVE) {
				gc.setForeground(mSplitDividerLineMidVertical_Selected);
				gc.setBackground(mSplitDividerLineTopVertical_Selected);
			}
			else {
				gc.setForeground(mSplitDividerLineMidVertical_Hover);
				gc.setBackground(mSplitDividerLineTopVertical_Hover);				
			}
			
			gc.fillGradientRectangle(x+width-12, y+1+10, 1, 10, true);
		}
		
	}

	public void drawButtonDecorations(GC gc, int state, int buttonSize, int x, int y, int width, int height, boolean topLeftCorner, boolean topRightCorner, boolean bottomLeftCorner, boolean bottomRightCorner, boolean bottomLeftSpecial, boolean bottomRightSpecial) {
		drawButtonDecorations(gc, state, SPLIT_NOT_SPLIT, buttonSize, x, y, width, height, topLeftCorner, topRightCorner, bottomLeftCorner, bottomRightCorner, bottomLeftSpecial, bottomRightSpecial);
	}

	public void drawButtonDecorations(GC gc, int state, int buttonSize, int x, int y, int width, int height) {
		drawButtonDecorations(gc, state, SPLIT_NOT_SPLIT, buttonSize, x, y, width, height, true, true, true, true, true, true);
	}
	
	public void drawButtonDecorations(GC gc, int state, int split, int buttonSize, int x, int y, int width, int height, boolean topLeftCorner, boolean topRightCorner, boolean bottomLeftCorner, boolean bottomRightCorner, boolean bottomLeftSpecial, boolean bottomRightSpecial) {
		// CORNER PIXELS
		if (state == STATE_HOVER) {
			if (buttonSize == BUTTON_BIG)
				gc.setForeground(mButtonTopCornerPixel_Hover);
			else
				gc.setForeground(mButtonSmallTopCornerPixel_Hover);
		}
		else if (state == STATE_SELECTED) {
			if (buttonSize == BUTTON_BIG)
				gc.setForeground(mButtonTopCornerPixel_Selected);
			else
				gc.setForeground(mButtonSmallTopCornerPixel_Selected);
		}
		else if (state == STATE_HOVER_AND_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_BOTTOM_ACTIVE) {
					gc.setForeground(mButtonTopCornerPixel_Hover);
				}
				else
					gc.setForeground(mButtonTopCornerPixel_HoverSelected);
			}
			else
				gc.setForeground(mButtonSmallTopCornerPixel_HoverSelected);
		}
		// top left corner
		if (topLeftCorner)
			gc.drawLine(x+1, y+1, x+1, y+1);
		// top right corner
		if (topRightCorner)
			gc.drawLine(x+width-1, y+1, x+width-1, y+1);

		// small buttons have a further down pixel just below the above in selected and hover+selected modes  
		if (buttonSize == BUTTON_SMALL && (state == STATE_SELECTED || state == STATE_HOVER_AND_SELECTED)) {
			if (state == STATE_SELECTED)
				gc.setForeground(mButtonSmallExtraTopBottomPixel_Selected);
			else if (state == STATE_HOVER_AND_SELECTED)
				gc.setForeground(mButtonSmallExtraTopBottomPixel_HoverSelected);
			
			// top left corner #2
			if (topLeftCorner)
				gc.drawLine(x+1, y+2, x+1, y+2);
			// top right corner #2
			if (topRightCorner)
				gc.drawLine(x+width-1, y+2, x+width-1, y+2);
		}
				
		if (state == STATE_HOVER || state == STATE_HOVER_AND_SELECTED || buttonSize == BUTTON_SMALL) {
			if (state == STATE_HOVER) {
				if (buttonSize == BUTTON_BIG)
					gc.setForeground(mButtonBottomCornerPixel_Hover);
				else
					gc.setForeground(mButtonSmallBottomCornerPixel_Hover);
			}
			else if (state == STATE_SELECTED)
				gc.setForeground(mButtonSmallBottomCornerPixel_Selected);
			else if (state == STATE_HOVER_AND_SELECTED) {
				if (buttonSize == BUTTON_BIG) {
					if (split == SPLIT_TOP_ACTIVE)
						gc.setForeground(mButtonBottomCornerPixel_Hover);
					else
						gc.setForeground(mButtonBottomCornerPixel_HoverSelected);
				}
				else
					gc.setForeground(mButtonSmallBottomCornerPixel_HoverSelected);
			}
			
			if (state == STATE_HOVER_AND_SELECTED && split == SPLIT_BOTTOM_ACTIVE) {
				gc.setForeground(mButtonBottomCornerPixel_Selected);
				// also on split button
				// top bottom corner
				if (bottomLeftCorner)
					gc.drawLine(x+2, y+height-2, x+2, y+height-2);
				// top bottom corner
				if (bottomRightCorner)
					gc.drawLine(x+width-2, y+height-2, x+width-2, y+height-2);
			}
						
			// top bottom corner
			if (bottomLeftCorner)
				gc.drawLine(x+1, y+height-2, x+1, y+height-2);
			// top bottom corner
			if (bottomRightCorner)
				gc.drawLine(x+width-1, y+height-2, x+width-1, y+height-2);
		}
		else {
			gc.setForeground(mButtonBottomCornerPixel_Selected);
			// top bottom corner
			if (bottomLeftCorner)
				gc.drawLine(x+2, y+height-2, x+2, y+height-2);
			// top bottom corner
			if (bottomRightCorner)
				gc.drawLine(x+width-2, y+height-2, x+width-2, y+height-2);
		}
		
		// hover+selected small button has some extra colors drawn inwards a few pixels in the bottom left and right corners, 1px up from the very bottom
		// it's 5px wide, 
		if (state == STATE_HOVER_AND_SELECTED && buttonSize == BUTTON_SMALL && width > (5*2+(2*2))) {
			gc.setForeground(mButtonSmallBottomCornerInwards_HoverSelected);
			// left
			if (bottomLeftSpecial)
				gc.drawLine(x+2, y+height-2, x+2+5, y+height-2);
			// right
			if (bottomRightSpecial)
				gc.drawLine(x+width-2, y+height-2, x+width-2-5, y+height-2);
		}
	}
	public void drawButtonFills(GC gc, int state, int buttonSize, int x, int y, int width, int height) {		
		drawButtonFills(gc, state, SPLIT_NOT_SPLIT, buttonSize, x, y, width, height);
	}
	
	public void drawButtonFills(GC gc, int state, int split, int buttonSize, int x, int y, int width, int height) {		
		int topPart = 0;
		int bottomPart = 0;
		int yStart = 0;
		int xStart = 0;
		int xWidth = 0;
		
		// selected and hover+selected are slightly slightly different in gradient sizes as well as where we start
		if (buttonSize == BUTTON_BIG) {
			if (state == STATE_SELECTED || state == STATE_HOVER_AND_SELECTED) {
				yStart = y+4;
				xStart = x+2;
				xWidth = width-3;
				topPart = 22;
				bottomPart = 39;
				
				if (state == STATE_HOVER_AND_SELECTED && split == SPLIT_BOTTOM_ACTIVE) {
					yStart--;
				}
			}
			else {
				yStart = y+2;
				xStart = x+2;
				xWidth = width-3;
				topPart = 23;
				bottomPart = 39;
			}
		}
		else if (buttonSize == BUTTON_SMALL) {
			if (state == STATE_SELECTED || state == STATE_HOVER_AND_SELECTED) {
				yStart = y+4;
				xStart = x+2;
				xWidth = width-3;
				topPart = 9;
				bottomPart = 8;
			}
			else if (state == STATE_HOVER) {
				yStart = y+2;
				xStart = x+2;
				xWidth = width-3;
				topPart = 9;
				bottomPart = 9;
			}			
			
			if (split != SPLIT_NOT_SPLIT) {
				yStart = y+1;				
				xWidth = width+1;
				xStart = x-1;
				topPart = 10;
				bottomPart = 10;
			}
		}
		
		if (split != SPLIT_NOT_SPLIT) {
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_TOP_ACTIVE) {					
					topPart = 22;
					bottomPart = 12;
				}
				else if (split == SPLIT_BOTTOM_NOT_ACTIVE) {
					topPart = 0;
					bottomPart = 26;
				}
				else if (split == SPLIT_BOTTOM_ACTIVE) {
					topPart = 0;
					bottomPart = 26;					
					if (state == STATE_HOVER_AND_SELECTED && split == SPLIT_BOTTOM_ACTIVE) {
						bottomPart++;
					}
				}
				else if (split == SPLIT_TOP_NOT_ACTIVE) {
					topPart = 22;
					bottomPart = 12;					
				}
			}
		}
		
		// special at the top of selected and hover+selected buttons, just the first two lines at the very top
		if (split == SPLIT_NOT_SPLIT) {
			if (state == STATE_SELECTED || state == STATE_HOVER_AND_SELECTED) {
				if (state == STATE_SELECTED) {			
					if (buttonSize == BUTTON_BIG)
						gc.setForeground(mButtonFillTopInnerLineOne_Selected);
					else
						gc.setForeground(mButtonSmallFillTopInnerLineOne_Selected);
				}
				else if (state == STATE_HOVER_AND_SELECTED) {
					if (buttonSize == BUTTON_BIG)
						gc.setForeground(mButtonFillTopInnerLineOne_HoverSelected);
					else
						gc.setForeground(mButtonSmallFillTopInnerLineOne_HoverSelected);
				}
				
				gc.drawLine(xStart, yStart-2, xStart+xWidth, yStart-2);
				
				if (state == STATE_SELECTED) {
					if (buttonSize == BUTTON_BIG)
						gc.setForeground(mButtonFillTopInnerLineTwo_Selected);
					else
						gc.setForeground(mButtonSmallFillTopInnerLineTwo_Selected);
				}
				else if (state == STATE_HOVER_AND_SELECTED) {
					if (buttonSize == BUTTON_BIG)
						gc.setForeground(mButtonFillTopInnerLineTwo_HoverSelected);
					else
						gc.setForeground(mButtonSmallFillTopInnerLineTwo_HoverSelected);
				}
				gc.drawLine(xStart, yStart-1, xStart+xWidth, yStart-1);
			}
		}
			
		// top or left part
		if (state == STATE_HOVER) {
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_TOP_NOT_ACTIVE) {
					gc.setForeground(mButtonSplitFillTopTop_NonSelectedHover);
					gc.setBackground(mButtonSplitFillTopBottom_NonSelectedHover);						
				}
				else {
					gc.setForeground(mButtonFillTopTop_Hover);
					gc.setBackground(mButtonFillTopBottom_Hover);
				}
			}
			else {
				if (split == SPLIT_RIGHT_NOT_ACTIVE || split == SPLIT_LEFT_NOT_ACTIVE) {
					gc.setForeground(mButtonSplitFillTopTop_NonSelectedHover);
					gc.setBackground(mButtonSplitFillTopBottom_NonSelectedHover);	
				}
				else {
					gc.setForeground(mButtonSmallFillTopTop_Hover);
					gc.setBackground(mButtonSmallFillTopBottom_Hover);
				}
			}
		}
		else if (state == STATE_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_TOP_NOT_ACTIVE) {
					gc.setForeground(mButtonSplitFillBottomPartTop_NonSelectedHover);
					gc.setBackground(mButtonSplitFillBottomPartBottom_NonSelectedHover);						
				}
				else {
					gc.setForeground(mButtonFillTopTop_Selected);
					gc.setBackground(mButtonFillTopBottom_Selected);
				}
			}
			else {
				gc.setForeground(mButtonSmallFillTopTop_Selected);
				gc.setBackground(mButtonSmallFillTopBottom_Selected);				
			}
		}
		else if (state == STATE_HOVER_AND_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_TOP_NOT_ACTIVE) {
					gc.setForeground(mButtonSplitFillBottomPartTop_NonSelectedHover);
					gc.setBackground(mButtonSplitFillBottomPartBottom_NonSelectedHover);						
				}
				else {
					gc.setForeground(mButtonFillTopTop_HoverSelected);
					gc.setBackground(mButtonFillTopBottom_HoverSelected);
				}
			}
			else {
				gc.setForeground(mButtonSmallFillTopTop_HoverSelected);
				gc.setBackground(mButtonSmallFillTopBottom_HoverSelected);				
			}
		}
		
		gc.fillGradientRectangle(xStart, yStart, xWidth, topPart, true);

		// bottom part
		if (state == STATE_HOVER) {
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_BOTTOM_NOT_ACTIVE) {
					gc.setForeground(mButtonSplitFillBottomPartTop_NonSelectedHover);
					gc.setBackground(mButtonSplitFillBottomPartBottom_NonSelectedHover);						
				}
				else if (split == SPLIT_TOP_NOT_ACTIVE) {
					gc.setForeground(mButtonSplitFillBottomTop_NonSelectedHover);
					gc.setBackground(mButtonSplitFillBottomBottom_NonSelectedHover);						
				}
				else {
					gc.setForeground(mButtonFillBottomTop_Hover);
					gc.setBackground(mButtonFillBottomBottom_Hover);
				}
			}
			else {
				if (split == SPLIT_RIGHT_NOT_ACTIVE || split == SPLIT_LEFT_NOT_ACTIVE) {
					gc.setForeground(mButtonSplitFillBottomTop_NonSelectedHover);
					gc.setBackground(mButtonSplitFillBottomBottom_NonSelectedHover);	
				}
				else {
					gc.setForeground(mButtonSmallFillBottomTop_Hover);
					gc.setBackground(mButtonSmallFillBottomBottom_Hover);									
				}
			}
		}
		else if (state == STATE_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_TOP_NOT_ACTIVE) {
					gc.setForeground(mButtonSplitFillBottomPartTop_NonSelectedHover);
					gc.setBackground(mButtonSplitFillBottomPartBottom_NonSelectedHover);						
				}
				else {
					gc.setForeground(mButtonFillBottomTop_Selected);
					gc.setBackground(mButtonFillBottomBottom_Selected);
				}
			}
			else {
				gc.setForeground(mButtonSmallFillBottomTop_Selected);
				gc.setBackground(mButtonSmallFillBottomBottom_Selected);				
			}
		}
		else if (state == STATE_HOVER_AND_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_TOP_NOT_ACTIVE) {
					gc.setForeground(mButtonSplitFillBottomPartTop_NonSelectedHover);
					gc.setBackground(mButtonSplitFillBottomPartBottom_NonSelectedHover);						
				}
				else if (split == SPLIT_BOTTOM_ACTIVE) {
					gc.setForeground(mButtonSplitFillBottomPartTop_SelectedHover);
					gc.setBackground(mButtonSplitFillBottomPartBottom_SelectedHover);											
				}
				else {
					gc.setForeground(mButtonFillBottomTop_HoverSelected);
					gc.setBackground(mButtonFillBottomBottom_HoverSelected);
				}
			}
			else {
				gc.setForeground(mButtonSmallFillBottomTop_HoverSelected);
				gc.setBackground(mButtonSmallFillBottomBottom_HoverSelected);				
			}
		}
		
		gc.fillGradientRectangle(xStart, yStart+topPart, xWidth, bottomPart, true);
	}

	public void drawButtonInnerBorder(GC gc, int state, int buttonSize, int x, int y, int width, int height, boolean drawLeft, boolean drawTop, boolean drawRight, boolean drawBottom) {
		drawButtonInnerBorder(gc, state, SPLIT_NOT_SPLIT, buttonSize, x, y, width, height, drawLeft, drawTop, drawRight, drawBottom);
	}
	
	public void drawButtonInnerBorder(GC gc, int state, int buttonSize, int x, int y, int width, int height) {
		drawButtonInnerBorder(gc, state, SPLIT_NOT_SPLIT, buttonSize, x, y, width, height, true, true, true, true);
	}
	
	public void drawButtonInnerBorder(GC gc, int state, int split, int buttonSize, int x, int y, int width, int height, boolean drawLeft, boolean drawTop, boolean drawRight, boolean drawBottom) {
		// figure out the middle point
		int middleWidth = (width/2);
		int extra = 0;
		if (width % 2 != 0)
			extra = 1;
		
		int topPart = 0;
		int bottomPart = 0;
		
		int yStartSide = 0;

		// selected and hover+selected are slightly slightly different in gradient sizes as well as where we start
		if (buttonSize == BUTTON_BIG) {
			if (state == STATE_SELECTED || state == STATE_HOVER_AND_SELECTED) {
				yStartSide = y+2;
				topPart = 22;
				bottomPart = 41;
			}
			else {
				yStartSide = y+2;
				topPart = 23;
				bottomPart = 39;
			}
		}
		else if (buttonSize == BUTTON_SMALL) {
			if (state == STATE_SELECTED || state == STATE_HOVER_AND_SELECTED) {
				yStartSide = y+3;
				topPart = 10;
				bottomPart = 7;
			}
			else {
				yStartSide = y+2;
				topPart = 9;
				bottomPart = 9;
			}			
		}
		
		// split buttons need to adhere strongly to bounds numbers
		if (split != SPLIT_NOT_SPLIT) {
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_TOP_ACTIVE) {					
					topPart = 22;
					bottomPart = 12;
				}
				else if (split == SPLIT_BOTTOM_NOT_ACTIVE) {
					yStartSide = y+1;
					topPart = 0;
					bottomPart = 28;
				}
				else if (split == SPLIT_TOP_NOT_ACTIVE) {
					topPart = 22;
					bottomPart = 12;					
				}
				else if (split == SPLIT_BOTTOM_ACTIVE) {
					topPart = 0;
					bottomPart = 26;					
				}
			}
			else {
				
			}
		}		
		
		// top border
		if (state == STATE_HOVER) { 
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_BOTTOM_NOT_ACTIVE) 
					gc.setForeground(mButtonSplitFillBottomPartTop_NonSelectedHover); // TODO: this part should be gradient, even though it's barely noticeable to the eye
				else if (split == SPLIT_BOTTOM_ACTIVE)
					gc.setForeground(mButtonInnerSideBottomTop_Hover);
				else
					gc.setForeground(mButtonInnerTop_Hover);
			}
			else
				gc.setForeground(mButtonSmallInnerTop_Hover);
		}
		else if (state == STATE_SELECTED) {
			if (buttonSize == BUTTON_BIG) 
				gc.setForeground(mButtonInnerTop_Selected);
			else
				gc.setForeground(mButtonSmallInnerTop_Selected);
		}
		else if (state == STATE_HOVER_AND_SELECTED) {
			if (buttonSize == BUTTON_BIG)
				gc.setForeground(mButtonInnerTop_HoverSelected);
			else
				gc.setForeground(mButtonSmallInnerTop_HoverSelected);
		}
		
		// inner top line		
		if (drawTop) {
			if (buttonSize == BUTTON_SMALL && split != SPLIT_NOT_SPLIT)
				gc.drawLine(x+2, y+1, x+width-1, y+1);
			else
				gc.drawLine(x+2, y+1, x+width-2, y+1);
		}
		
		// side borders
		// inner left side
		if (state == STATE_HOVER) {
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_TOP_ACTIVE) {
					gc.setForeground(mButtonSplitFillTopTop_NonSelectedHover);
					gc.setBackground(mButtonSplitFillTopBottom_NonSelectedHover);
				}
				else {
					gc.setForeground(mButtonInnerSideTopTop_Hover);
					gc.setBackground(mButtonInnerSideTopBottom_Hover);
				}
			}
			else {
				gc.setForeground(mButtonSmallInnerSideTopTop_Hover);
				gc.setBackground(mButtonSmallInnerSideTopBottom_Hover);
			}
		}
		else if (state == STATE_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				gc.setForeground(mButtonInnerSideTopTop_Selected);
				gc.setBackground(mButtonInnerSideTopBottom_Selected);
			}
			else {
				gc.setForeground(mButtonSmallInnerSideTopTop_Selected);
				gc.setBackground(mButtonSmallInnerSideTopBottom_Selected);
			}
		}
		else if (state == STATE_HOVER_AND_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				gc.setForeground(mButtonInnerSideTopTop_HoverSelected);
				gc.setBackground(mButtonInnerSideTopBottom_HoverSelected);
			}
			else {
				gc.setForeground(mButtonSmallInnerSideTopTop_HoverSelected);
				gc.setBackground(mButtonSmallInnerSideTopBottom_HoverSelected);				
			}
		}
		
		// left side top
		if (drawLeft)
			gc.fillGradientRectangle(x+1, yStartSide, 1, topPart, true);
		
		// right side top, small split button doesn't have one
		if (drawRight) {
			//if (buttonSize != BUTTON_SMALL_NORMAL || split == SPLIT_NOT_SPLIT)
				gc.fillGradientRectangle(x+width-1, yStartSide, 1, topPart, true);
		}
		
		if (state == STATE_HOVER) {
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_TOP_ACTIVE) {
					gc.setForeground(mButtonSplitFillBottomTop_NonSelectedHover);
					gc.setBackground(mButtonSplitFillBottomBottom_NonSelectedHover);
				}
				else if (split == SPLIT_TOP_NOT_ACTIVE) {
					gc.setForeground(mButtonSplitFillBottomPartTop_NonSelectedHover);
					gc.setBackground(mButtonSplitFillBottomPartTop_NonSelectedHover);
				}
				else {
					gc.setForeground(mButtonInnerSideBottomTop_Hover);
					gc.setBackground(mButtonInnerSideBottomBottom_Hover);
				}
			}
			else {
				gc.setForeground(mButtonSmallInnerSideBottomTop_Hover);
				gc.setBackground(mButtonSmallInnerSideBottomBottom_Hover);				
			}
		}
		else if (state == STATE_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				gc.setForeground(mButtonInnerSideBottomTop_Selected);
				gc.setBackground(mButtonInnerSideBottomBottom_Selected);
			}
			else {
				gc.setForeground(mButtonSmallInnerSideBottomTop_Selected);
				gc.setBackground(mButtonSmallInnerSideBottomBottom_Selected);				
			}
		}
		else if (state == STATE_HOVER_AND_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				gc.setForeground(mButtonInnerSideBottomTop_HoverSelected);
				gc.setBackground(mButtonInnerSideBottomBottom_HoverSelected);
			}
			else {
				gc.setForeground(mButtonSmallInnerSideBottomTop_HoverSelected);
				gc.setBackground(mButtonSmallInnerSideBottomBottom_HoverSelected);						
			}
		}
		
		// left side bottom
		if (drawLeft)
			gc.fillGradientRectangle(x+1, yStartSide+topPart, 1, bottomPart, true);
		
		// right side bottom
		if (drawRight) {
			//if (buttonSize != BUTTON_SMALL_NORMAL || split == SPLIT_NOT_SPLIT)
				gc.fillGradientRectangle(x+width-1, yStartSide+topPart, 1, bottomPart, true);		
		}
			
		if (drawBottom) {
			if (split == SPLIT_BOTTOM_NOT_ACTIVE || split == SPLIT_BOTTOM_ACTIVE || split == SPLIT_NOT_SPLIT || buttonSize == BUTTON_SMALL) {
				// bottom border
				if (state == STATE_HOVER || (buttonSize == BUTTON_BIG && state == STATE_HOVER_AND_SELECTED) || (state == STATE_SELECTED && buttonSize == BUTTON_SMALL)) {		
					if (state == STATE_HOVER) {
						if (buttonSize == BUTTON_BIG) {
							gc.setForeground(mButtonInnerBottomLeft_Hover);
							gc.setBackground(mButtonInnerBottomMiddle_Hover);
						}
						else {
							gc.setForeground(mButtonSmallInnerBottomLeft_Hover);
							gc.setBackground(mButtonSmallInnerBottomMiddle_Hover);					
						}
					}
					else if (state == STATE_SELECTED) {
						if (buttonSize == BUTTON_SMALL) {
							gc.setForeground(mButtonSmallInnerBottomLeft_Selected);
							gc.setBackground(mButtonSmallInnerBottomMiddle_Selected);
						}
					}
					else if (state == STATE_HOVER_AND_SELECTED){				
						gc.setForeground(mButtonInnerBottomLeft_HoverSelected);
						gc.setBackground(mButtonInnerBottomMiddle_HoverSelected);
					}
					// left part
					gc.fillGradientRectangle(x+2, y+height-2, middleWidth-1, 1, false);
					if (state == STATE_HOVER) {
						if (buttonSize == BUTTON_BIG) {
							gc.setForeground(mButtonInnerBottomMiddle_Hover);
							gc.setBackground(mButtonInnerBottomRight_Hover);
						}
						else {
							gc.setForeground(mButtonSmallInnerBottomMiddle_Hover);
							gc.setBackground(mButtonSmallInnerBottomRight_Hover);					
						}
					}
					else if (state == STATE_SELECTED) {
						if (buttonSize == BUTTON_SMALL) {
							gc.setForeground(mButtonSmallInnerBottomMiddle_Selected);
							gc.setBackground(mButtonSmallInnerBottomRight_Selected);
						}
					}
					else if (state == STATE_HOVER_AND_SELECTED){
						gc.setForeground(mButtonInnerBottomMiddle_HoverSelected);
						gc.setBackground(mButtonInnerBottomRight_HoverSelected);							
					}
					
					// inner top line		
					if (buttonSize == BUTTON_SMALL && split != SPLIT_NOT_SPLIT)
						extra++;
	
					// draw right part
					gc.fillGradientRectangle(x+middleWidth-1, y+height-2, middleWidth+extra, 1, false);
				}
			}
		}
	}

	public void drawButtonOuterBorder(GC gc, int state, int buttonSize, int x, int y, int width, int height, boolean drawLeft, boolean drawTop, boolean drawRight, boolean drawBottom) {
		drawButtonOuterBorder(gc, state, SPLIT_NOT_SPLIT, buttonSize, x, y, width, height, drawLeft, drawTop, drawRight, drawBottom);
	}

	public void drawButtonOuterBorder(GC gc, int state, int buttonSize, int x, int y, int width, int height) {
		drawButtonOuterBorder(gc, state, SPLIT_NOT_SPLIT, buttonSize, x, y, width, height, true, true, true, true);
	}
	
	public void drawButtonOuterBorder(GC gc, int state, int split, int buttonSize, int x, int y, int width, int height, boolean drawLeft, boolean drawTop, boolean drawRight, boolean drawBottom) {
		// figure out the middle point
		int middleWidth = (width/2);
		int extra = 0;
		if (width % 2 != 0)
			extra = 1;
		
		// ----------------
		// OUTER TOP BORDER
		// ----------------
		if (state == STATE_HOVER) {
			if (buttonSize == BUTTON_BIG)
				gc.setForeground(mButtonOuterTop_Hover);
			else
				gc.setForeground(mButtonSmallOuterTop_Hover);
		}
		else if (state == STATE_SELECTED) {
			if (buttonSize == BUTTON_BIG)
				gc.setForeground(mButtonOuterTop_Selected);
			else 
				gc.setForeground(mButtonSmallOuterTop_Selected);
		}
		else if (state == STATE_HOVER_AND_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				if (split == SPLIT_TOP_NOT_ACTIVE) 
					gc.setForeground(mButtonOuterTop_Hover);
				else
					gc.setForeground(mButtonOuterTop_HoverSelected);
			}
			else
				gc.setForeground(mButtonSmallOuterTop_HoverSelected);
		}

		// draw outer top border, 1 line
		// the line starts 1 pixel in to the right from where the given x is, and ends 1 pixel in too (unless it's small and split)
		if (drawTop) {
			if (buttonSize == BUTTON_SMALL && split != SPLIT_NOT_SPLIT && split != SPLIT_LEFT_ACTIVE)
				gc.drawLine(x+1, y, x+width, y);
			else
				gc.drawLine(x+1, y, x+width-1, y);
		}
		
		// OUTER SIDE BORDERS
		if (state == STATE_HOVER) {
			if (buttonSize == BUTTON_BIG) {			
				gc.setForeground(mButtonOuterSideTopTop_Hover);
				gc.setBackground(mButtonOuterSideTopBottom_Hover);
			}
			else {
				gc.setForeground(mButtonSmallOuterSideTopTop_Hover);
				gc.setBackground(mButtonSmallOuterSideTopBottom_Hover);				
			}
		}
		else if (state == STATE_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				gc.setForeground(mButtonOuterSideTopTop_Selected);
				gc.setBackground(mButtonOuterSideTopBottom_Selected);
			}
			else {
				gc.setForeground(mButtonSmallOuterSideTopTop_Selected);
				gc.setBackground(mButtonSmallOuterSideTopBottom_Selected);			
			}
		}
		else if (state == STATE_HOVER_AND_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				gc.setForeground(mButtonOuterSideTopTop_HoverSelected);
				gc.setBackground(mButtonOuterSideTopBottom_HoverSelected);
			}
			else {
				gc.setForeground(mButtonSmallOuterSideTopTop_HoverSelected);
				gc.setBackground(mButtonSmallOuterSideTopBottom_HoverSelected);				
			}
		}
		
		int topOuterPart = 37;
		int bottomOuterPart = 27; 
		if (buttonSize == BUTTON_SMALL) {			
			topOuterPart = 10;
			bottomOuterPart = 10;
		}
		
		if (state == STATE_SELECTED || state == STATE_HOVER_AND_SELECTED) {
			if (buttonSize == BUTTON_SMALL) {		
				bottomOuterPart = 9;
			}
			else {
				bottomOuterPart = 26;
			}
		}
		
		int yStart = y+1;

		// split buttons need to adhere strongly to bounds numbers
		if (split != SPLIT_NOT_SPLIT) {
			if (buttonSize == BUTTON_BIG) {
				if (state == STATE_HOVER) {
					if (split == SPLIT_TOP_ACTIVE) {					
						topOuterPart = 36;
						bottomOuterPart = 0;
					}
					else if (split == SPLIT_BOTTOM_NOT_ACTIVE) {
						topOuterPart = 0;
						bottomOuterPart = 28;
					}
				}
				
				if (state == STATE_HOVER_AND_SELECTED) {
					if (split == SPLIT_TOP_NOT_ACTIVE) {
						topOuterPart = 39;
						bottomOuterPart = 24;					
					}
					else if (split == SPLIT_BOTTOM_ACTIVE) {
						yStart = 38;
						topOuterPart = 0;
						bottomOuterPart = 27;											
					}
					else if (split == SPLIT_TOP_ACTIVE) {
						topOuterPart = 39;
						bottomOuterPart = 24;											
					}
				}
			}			
		}
				
		// left, same pixel story
		// top part
		// left
		if (drawLeft) 
			gc.fillGradientRectangle(x, yStart, 1, topOuterPart, true);
		// right
		if (drawRight)
			gc.fillGradientRectangle(x+width, yStart, 1, topOuterPart, true);
	
		// bottom part
		if (state == STATE_HOVER) {
			if (buttonSize == BUTTON_BIG) {
				gc.setForeground(mButtonOuterSideBottomTop_Hover);
				gc.setBackground(mButtonOuterSideBottomBottom_Hover);
			}
			else {
				gc.setForeground(mButtonSmallOuterSideBottomTop_Hover);
				gc.setBackground(mButtonSmallOuterSideBottomBottom_Hover);				
			}
		}
		else if (state == STATE_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				gc.setForeground(mButtonOuterSideBottomTop_Selected);
				gc.setBackground(mButtonOuterSideBottomBottom_Selected);
			}
			else {
				gc.setForeground(mButtonSmallOuterSideBottomTop_Selected);
				gc.setBackground(mButtonSmallOuterSideBottomBottom_Selected);				
			}
		}
		else if (state == STATE_HOVER_AND_SELECTED) {
			if (buttonSize == BUTTON_BIG) {
				gc.setForeground(mButtonOuterSideBottomTop_HoverSelected);
				gc.setBackground(mButtonOuterSideBottomBottom_HoverSelected);					
			}
			else {
				gc.setForeground(mButtonSmallOuterSideBottomTop_HoverSelected);
				gc.setBackground(mButtonSmallOuterSideBottomBottom_HoverSelected);				
			}
		}
		// left
		if (drawLeft)
			gc.fillGradientRectangle(x, yStart+topOuterPart, 1, bottomOuterPart, true);
		// right
		if (drawRight)
			gc.fillGradientRectangle(x+width, yStart+topOuterPart, 1, bottomOuterPart, true);

		// bottom border
		if (drawBottom) {
			if (state == STATE_HOVER_AND_SELECTED && (split == SPLIT_BOTTOM_ACTIVE || split == SPLIT_TOP_ACTIVE) && buttonSize == BUTTON_BIG) {
				// do nada for now
			}
			else {			
				if (state == STATE_HOVER) {
					if (buttonSize == BUTTON_BIG) {
						gc.setForeground(mButtonOuterBottomLeft_Hover);
						gc.setBackground(mButtonOuterBottomMiddle_Hover);
					}
					else {
						gc.setForeground(mButtonSmallOuterBottomLeft_Hover);
						gc.setBackground(mButtonSmallOuterBottomMiddle_Hover);				
					}
				}
				else if (state == STATE_SELECTED) {
					if (buttonSize == BUTTON_BIG) {
						gc.setForeground(mButtonOuterBottomLeft_Selected);
						gc.setBackground(mButtonOuterBottomMiddle_Selected);
					}
					else {
						gc.setForeground(mButtonSmallOuterBottomLeft_Selected);
						gc.setBackground(mButtonSmallOuterBottomMiddle_Selected);			
					}
				}
				else if (state == STATE_HOVER_AND_SELECTED) {
					if (buttonSize == BUTTON_BIG) {
						gc.setForeground(mButtonOuterBottomLeft_HoverSelected);
						gc.setBackground(mButtonOuterBottomMiddle_HoverSelected);
					}
					else {
						gc.setForeground(mButtonSmallOuterBottomLeft_HoverSelected);
						gc.setBackground(mButtonSmallOuterBottomMiddle_HoverSelected);				
					}
				}		
				// draw left part
				gc.fillGradientRectangle(x+1, y+height-1, middleWidth, 1, false);
				
				if (state == STATE_HOVER) {
					if (buttonSize == BUTTON_BIG) {
						gc.setForeground(mButtonOuterBottomMiddle_Hover);
						gc.setBackground(mButtonOuterBottomRight_Hover);
					}
					else {
						gc.setForeground(mButtonSmallOuterBottomMiddle_Hover);
						gc.setBackground(mButtonSmallOuterBottomRight_Hover);				
					}				
				}
				else if (state == STATE_SELECTED) {
					if (buttonSize == BUTTON_BIG) {
						gc.setForeground(mButtonOuterBottomMiddle_Selected);
						gc.setBackground(mButtonOuterBottomRight_Selected);
					}
					else {
						gc.setForeground(mButtonSmallOuterBottomMiddle_Selected);
						gc.setBackground(mButtonSmallOuterBottomRight_Selected);				
					}
				}
				else if (state == STATE_HOVER_AND_SELECTED) {
					if (buttonSize == BUTTON_BIG) {
						gc.setForeground(mButtonOuterBottomMiddle_HoverSelected);
						gc.setBackground(mButtonOuterBottomRight_HoverSelected);
					}
					else {
						gc.setForeground(mButtonSmallOuterBottomMiddle_HoverSelected);
						gc.setBackground(mButtonSmallOuterBottomRight_HoverSelected);				
					}
				}
				// draw right part
				if (buttonSize == BUTTON_SMALL && split != SPLIT_NOT_SPLIT && split != SPLIT_LEFT_ACTIVE)
					extra += 1;
	
				gc.fillGradientRectangle(x+middleWidth, y+height-1, middleWidth+extra, 1, false);
			}
		}
			
		// special for selected, 1px extra
		if (state == STATE_SELECTED || state == STATE_HOVER_AND_SELECTED) {
			if (state == STATE_SELECTED) {
				if (buttonSize == BUTTON_BIG) 
					gc.setForeground(mButtonOuterBottomPixel_Selected);
				else
					gc.setForeground(mButtonSmallOuterBottomPixel_Selected);
			}
			else if (state == STATE_HOVER_AND_SELECTED) {
				if (buttonSize == BUTTON_BIG) {
					if (split == SPLIT_TOP_NOT_ACTIVE)
						gc.setForeground(mButtonOuterBottomPixel_Selected);
					else
						gc.setForeground(mButtonOuterBottomPixel_HoverSelected);
				}
				else
					gc.setForeground(mButtonSmallOuterBottomPixel_HoverSelected);
			}
			
			if ((buttonSize == BUTTON_BIG && (state == STATE_HOVER_AND_SELECTED || state == STATE_SELECTED)) || (state != STATE_HOVER_AND_SELECTED && split != SPLIT_BOTTOM_ACTIVE) || buttonSize == BUTTON_SMALL) {
				// left
				if (buttonSize != BUTTON_SMALL || split == SPLIT_NOT_SPLIT)
					gc.drawLine(x, y+1+topOuterPart+bottomOuterPart, x, y+1+topOuterPart+bottomOuterPart);
				// right
				gc.drawLine(x+width, y+1+topOuterPart+bottomOuterPart, x+width, y+1+topOuterPart+bottomOuterPart);
			}
		}
	}
	
	public void drawArrow(GC gc, int x, int y, int state, boolean enabled) {
		if (enabled)
			gc.setForeground(mArrowColor);
		else
			gc.setForeground(mArrowColor_Disabled);
		
		gc.drawLine(x, y, x+4, y);
		gc.drawLine(x+1, y+1, x+3, y+1);
		gc.drawLine(x+2, y+2, x+1, y+2);

		if (enabled) {
			if (state == STATE_NONE)
				gc.setForeground(mArrowColorShadow);
			else 
				gc.setForeground(mArrowColorShadow_HoverOrSelected);
		}
		else
			gc.setForeground(mArrowColorShadow_Disabled);
		
		gc.drawLine(x, y+1, x, y+1);
		gc.drawLine(x+1, y+2, x+1, y+2);
		gc.drawLine(x+2, y+3, x+2, y+3);
		gc.drawLine(x+3, y+2, x+3, y+2);
		gc.drawLine(x+4, y+1, x+4, y+1);
		
	}
	
	public void drawSeparator(GC gc, int x, int y, int height) {
		gc.setForeground(mSeparatorColor);
		gc.drawLine(x, y, x, y+height);
		gc.setForeground(mSeparatorColorShadow);
		gc.drawLine(x+1, y, x+1, y+height);
	}
	
	public void drawCheckbox(GC gc, int state, AbstractRibbonGroupItem item) {
		Rectangle bounds = item.getBounds();
		
		int x = bounds.x;
		int y = bounds.y;
		int cbWidth = 12;
		int cbHeight = 12;		
		
		if (state == STATE_NONE || state == STATE_SELECTED)
			gc.setForeground(mCheckBoxBorderOuter_Normal);
		else if (state == STATE_HOVER || state == STATE_HOVER_AND_SELECTED)
			gc.setForeground(mCheckBoxBorderOuter_Hover);
		
		if (!item.isEnabled())
			gc.setForeground(mCheckBoxBorderOuter_Disabled);
		
		gc.drawRectangle(x, y, cbWidth, cbHeight);
		
		if (state == STATE_NONE || state == STATE_SELECTED)
			gc.setForeground(mCheckBoxBorderInner_Normal);
		else if (state == STATE_HOVER || state == STATE_HOVER_AND_SELECTED)
			gc.setForeground(mCheckBoxBorderInner_Hover);

		if (!item.isEnabled())
			gc.setForeground(mCheckBoxBorderInner_Disabled);

		gc.drawRectangle(x+1, y+1, cbWidth-2, cbHeight-2);
		
		Color outerTopLeft = null;
		Color outerBottomRight = null;
		Color innerTopLeft = null;
		Color innerBottomRight = null;
		
		switch (state) {
			case STATE_NONE:
			case STATE_SELECTED:
				outerTopLeft = mCheckBoxBorderOuterSquareTopLeft_Normal;
				outerBottomRight = mCheckBoxBorderOuterSquareBottomRight_Normal;
				innerTopLeft = mCheckBoxBorderInnerSquareTopLeft_Normal;
				innerBottomRight = mCheckBoxBorderInnerSquareBottomRight_Normal;
				break;
			case STATE_HOVER:
			case STATE_HOVER_AND_SELECTED:
				outerTopLeft = mCheckBoxBorderOuterSquareTopLeft_Hover;
				outerBottomRight = mCheckBoxBorderOuterSquareBottomRight_Hover;
				innerTopLeft = mCheckBoxBorderInnerSquareTopLeft_Hover;
				innerBottomRight = mCheckBoxBorderInnerSquareBottomRight_Hover;
				break;
		}
		
		if (!item.isEnabled()) {
			outerTopLeft = mCheckBoxBorderOuterSquareTopLeft_Disabled;
			outerBottomRight = mCheckBoxBorderOuterSquareBottomRight_Disabled;
			innerTopLeft = mCheckBoxBorderInnerSquareTopLeft_Disabled;
			innerBottomRight = mCheckBoxBorderInnerSquareBottomRight_Disabled;			
		}
		
		Pattern p = new Pattern(gc.getDevice(), bounds.x+2, bounds.y+2, bounds.x+2+9, bounds.y+2+9, outerTopLeft, outerBottomRight);
		gc.setForegroundPattern(p);
		gc.setBackgroundPattern(p);
		gc.fillRectangle(new Rectangle(bounds.x+2, bounds.y+2, 9, 9));
		
		Pattern p2 = new Pattern(gc.getDevice(), bounds.x+3, bounds.y+3, bounds.x+3+7, bounds.y+3+7, innerTopLeft, innerBottomRight);
		gc.setForegroundPattern(p2);
		gc.setBackgroundPattern(p2);
		
		gc.fillRectangle(new Rectangle(bounds.x+3, bounds.y+3, 7, 7));
				
		gc.setForeground(mTextColor);
		if (!item.isEnabled())
			gc.setForeground(mTextColor_Disabled);
		
		if (item.getName() != null)
			gc.drawString(item.getName(), x+13+6, y, true);
		
		p.dispose();
		p2.dispose();
		gc.setForegroundPattern(null);
		gc.setBackgroundPattern(null);
		
		// force GC back to normal! 1x1 px drawing does not work otherwise (see bug #199658 I filed against this problem)
		gc.setAdvanced(false);
		
		// draw check mark
		if (item.isSelected()) {
			// center bottom 4px square
			gc.setForeground(mCheckBoxCheckmarkDark);
			gc.setBackground(mCheckBoxCheckmarkDark);
			gc.fillRectangle(x+5, y+8, 2, 2);
			
			// dark color on left of square, 2 pixels up
			gc.drawLine(x+4, y+7, x+4, y+7+1);
			
			// other side, similar but not-connected to square, corner to corner only
			gc.drawLine(x+7, y+6, x+7, y+6+1);
			
			// same deal again, top right of the above
			gc.drawLine(x+8, y+4, x+8, y+4+1);
			
			// and again, just 1px
			gc.drawLine(x+9, y+3, x+9, y+3);
			
			// bottom uses slightly lighter colors
			gc.setForeground(mCheckBoxCheckmarkDark3);
			
			// very bottom left
			gc.drawLine(x+4, y+9, x+4, y+9);
			
			// bottom bottom, 2px wide
			gc.drawLine(x+5, y+10, x+6, y+10);
			
			// top
			gc.drawLine(x+4, y+6, x+4, y+6);
			
			// next darkness level, slightly lighter
			gc.setForeground(mCheckBoxCheckmarkDark2);

			gc.drawLine(x+9, y+2, x+9, y+2);

			// lets start from the top and go down
			// left of the last 1px pixel
			gc.drawLine(x+8, y+3, x+8, y+3);
			
			// and below that same px
			gc.drawLine(x+9, y+4, x+9, y+4);
			
			// similar pattern a bit down
			gc.drawLine(x+7, y+5, x+7, y+5);
			gc.drawLine(x+8, y+6, x+8, y+6);
			
			// and again
			gc.drawLine(x+6, y+7, x+6, y+7);
			gc.drawLine(x+7, y+8, x+7, y+8);
			
			// and one on the left side
			gc.drawLine(x+3, y+7, x+3, y+7);
			
			// and lighter still
			gc.setForeground(mCheckBoxCheckmarkDark4);
			
			// start at top again
			// top right
			gc.drawLine(x+10, y+3, x+10, y+3);
			
			// left and down 1
			gc.drawLine(x+7, y+4, x+7, y+4);
			
			// right and down 1
			gc.drawLine(x+9, y+5, x+9, y+5);
			
			// left and down 1
			gc.drawLine(x+6, y+6, x+6, y+6);
			
			// right and down 1
			gc.drawLine(x+8, y+7, x+8, y+7);
			
			// and farthest down
			gc.drawLine(x+7, y+9, x+7, y+9);
		
			// lighter colors
			gc.setForeground(mCheckBoxCheckmarkLight);
			// left side
			gc.drawLine(x+3, y+8, x+3, y+8);
			
			// bit further right
			gc.drawLine(x+5, y+7, x+5, y+7);
			
			// lighter yet, outer corner of the v-part
			gc.setForeground(mCheckBoxCheckmarkLight2);
			gc.drawLine(x+3, y+6, x+3, y+6);
			
			// TODO: fill in more colors? it's hardly visible to the eye the difference at this point
			
			// white colors, make it stand out
			gc.setForeground(mCheckBoxCheckmarkLight3);
			gc.drawLine(x+8, y+8, x+8, y+8);

			// need to use alpha if we want these
			/*gc.drawLine(x+2, y+7, x+2, y+7);
			
			gc.drawLine(x+2, y+6, x+2, y+6);
			gc.drawLine(x+2, y+8, x+2, y+8);*/
			
		}
	}
}
