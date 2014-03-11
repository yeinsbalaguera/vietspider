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
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.vietspider.generic.ColorCache;


public class RibbonGroup extends Canvas implements MouseMoveListener, MouseListener, MouseTrackListener {

	private String mGroupName;
	
	private int mBorderSize = 2;
	
	// gradient marks
	public static int BUTTON_TOP_HEIGHT = 13;
	public static int BUTTON_MID_HEIGHT = 54;
	public static int BUTTON_BOT_HEIGHT = 14;
	public static int BUTTON_SPACING = 0;
	
	public static int BUTTON_HEIGHT = 66;
	public static int TOOLBAR_BUTTON_HEIGHT = 22;
	public static int TOOLBAR_BUTTON_HEIGHT_BORDERED = 20;
	public static int TOOLBAR_BUTTON_WIDTH = 22;
	public static int TOOLBAR_BUTTON_WIDTH_BORDERED = 22;
	public static int CHECKBOX_HEIGHT = 13;
	
	private boolean mVisible = true;
	
	// COLORS
	private Color mBorderBlueTop = ColorCache.getInstance().getColor(197, 210, 223);
	private Color mBorderBlueSideTopTop = ColorCache.getInstance().getColor(195, 209, 222);
	private Color mBorderBlueSideTopBottom = ColorCache.getInstance().getColor(179, 198, 214);
	private Color mBorderBlueSideBottomTop = ColorCache.getInstance().getColor(175, 195, 213);
	private Color mBorderBlueSideBottomBottom = ColorCache.getInstance().getColor(157, 191, 219);
	private Color mBorderBlueBottom = ColorCache.getInstance().getColor(158, 191, 219);
	private Color mBorderFadePixelTop = ColorCache.getInstance().getColor(208, 220, 232);
	private Color mBorderFadePixelBottom = ColorCache.getInstance().getColor(187, 213, 232);

	private Color mBorderBlueTop_Hover = ColorCache.getInstance().getColor(173, 199, 222);
	private Color mBorderBlueSideTopTop_Hover = ColorCache.getInstance().getColor(172, 199, 222);
	private Color mBorderBlueSideTopBottom_Hover = ColorCache.getInstance().getColor(165, 196, 220);
	private Color mBorderBlueSideBottomTop_Hover = ColorCache.getInstance().getColor(165, 196, 220);
	private Color mBorderBlueSideBottomBottom_Hover = ColorCache.getInstance().getColor(127, 173, 211);
	private Color mBorderBlueBottom_Hover = ColorCache.getInstance().getColor(126, 173, 211);
	private Color mBorderFadePixelTop_Hover = ColorCache.getInstance().getColor(193, 212, 231);
	private Color mBorderFadePixelBottom_Hover = ColorCache.getInstance().getColor(167, 201, 228);

	private Color mBorderInnerFadePixelTop = ColorCache.getInstance().getColor(232, 239, 247);
	private Color mBorderInnerBlueTop = ColorCache.getInstance().getColor(237, 242, 248);
	private Color mBorderInnerSideTopTop = ColorCache.getInstance().getColor(236, 241, 248);
	private Color mBorderInnerSideTopBottom = ColorCache.getInstance().getColor(230, 237, 246);
	private Color mBorderInnerSideMidTop = ColorCache.getInstance().getColor(225, 234, 244);
	private Color mBorderInnerSideMidBottom = ColorCache.getInstance().getColor(234, 242, 248);
	private Color mBorderInnerSideBottomTop = ColorCache.getInstance().getColor(206, 224, 243);
	private Color mBorderInnerSideBottomBottom = ColorCache.getInstance().getColor(201, 221, 240);

	private Color mBorderInnerBlueTop_Hover = ColorCache.getInstance().getColor(255, 255, 255);
	private Color mBorderInnerSideTopTop_Hover = ColorCache.getInstance().getColor(255, 255, 255);
	private Color mBorderInnerSideTopBottom_Hover = ColorCache.getInstance().getColor(255, 255, 255);
	private Color mBorderInnerSideMidTop_Hover = ColorCache.getInstance().getColor(255, 255, 255);
	private Color mBorderInnerSideMidBottom_Hover = ColorCache.getInstance().getColor(255, 255, 255);
	private Color mBorderInnerSideBottomTop_Hover = ColorCache.getInstance().getColor(255, 255, 255);
	private Color mBorderInnerSideBottomBottom_Hover = ColorCache.getInstance().getColor(255, 255, 255);

	private Color mShadowTopTop = ColorCache.getInstance().getColor(237, 242, 248);
	private Color mShadowTopBottom = ColorCache.getInstance().getColor(230, 237, 246);
	private Color mShadowMidTop = ColorCache.getInstance().getColor(225, 234, 244);
	private Color mShadowMidBottom = ColorCache.getInstance().getColor(234, 242, 248);
	private Color mShadowBottomTop = ColorCache.getInstance().getColor(234, 242, 249);
	private Color mShadowBottomBottom = ColorCache.getInstance().getColor(239, 248, 253);
	private Color mShadowFadePixel = ColorCache.getInstance().getColor(231, 238, 247);
	
	private Color mInnerFillTopTop = ColorCache.getInstance().getColor(222, 232, 245);
	private Color mInnerFillTopBottom = ColorCache.getInstance().getColor(209, 223, 240);
	private Color mInnerFillMidTop = ColorCache.getInstance().getColor(199, 216, 237);
	private Color mInnerFillMidBottm = ColorCache.getInstance().getColor(216, 232, 245);
	private Color mInnerFillBottomTop = ColorCache.getInstance().getColor(194, 216, 241);
	private Color mInnerFillBottomBottom = ColorCache.getInstance().getColor(192, 216, 239);

	private Color mInnerFillTopTop_Hover = ColorCache.getInstance().getColor(228, 239, 253);
	private Color mInnerFillTopBottom_Hover = ColorCache.getInstance().getColor(232, 240, 252);
	private Color mInnerFillMidTop_Hover = ColorCache.getInstance().getColor(220, 234, 251);
	private Color mInnerFillMidBottm_Hover = ColorCache.getInstance().getColor(220, 232, 248);
	private Color mInnerFillBottomTop_Hover = ColorCache.getInstance().getColor(200, 224, 255);
	private Color mInnerFillBottomBottom_Hover = ColorCache.getInstance().getColor(203, 226, 255);
	private Color mInnerFillBottomBottomHighlight_Hover = ColorCache.getInstance().getColor(214, 237, 255);

	private Color mTextColorRight = ColorCache.getInstance().getColor(138, 207, 255);
	private Color mTextColor = ColorCache.getInstance().getColor(62, 106, 170);
	
	private Color mToolTipMarker = ColorCache.getInstance().getColor(102, 142, 175);
	private Color mToolTipMarkerShadow = ColorCache.getInstance().getColor(255, 255, 255);
	
	private Color mToolTipBorder = ColorCache.getInstance().getColor(219, 206, 153);
	private Color mToolTipFillTopTop = ColorCache.getInstance().getColor(255, 252, 223);
	private Color mToolTipFillTopBottom = ColorCache.getInstance().getColor(255, 239, 167);
	private Color mToolTipFillBottomTop = ColorCache.getInstance().getColor(255, 217, 117);
	private Color mToolTipFillBottomBottom = ColorCache.getInstance().getColor(255, 227, 152);

	private Color mToolTipBorder_Selected = ColorCache.getInstance().getColor(154, 143, 99);
	private Color mToolTipFillTopTop_Selected = ColorCache.getInstance().getColor(220, 209, 178);
	private Color mToolTipFillTopBottom_Selected = ColorCache.getInstance().getColor(234, 198, 141);
	private Color mToolTipFillBottomTop_Selected = ColorCache.getInstance().getColor(255, 167, 56);
	private Color mToolTipFillBottomBottom_Selected = ColorCache.getInstance().getColor(255, 204, 78);

	private List<AbstractRibbonGroupItem> mToolItems;
	
	private boolean isActiveHoverGroup;
	/*
	private int mTimeIncreaser = 80;
	private int mMaxIterations = 10;
	private int mCurrentIteraton = 1;
	private Runnable fadeInTimer = null;
	private Runnable fadeOutTimer = null;	
	private boolean mCreated = false;
	private boolean mEnableDoubleBuffering = true;
	*/
	
	private AbstractRibbonGroupItem mHoverButton;
	private AbstractRibbonGroupItem mLastSelectedButton; 
	private AbstractRibbonGroupItem mLastMouseUpButton; 
	private List<AbstractRibbonGroupItem> mSelectedButtons;
	private RibbonTabComposite mToolBarParent;
	private AbstractButtonPaintManager bpm;
		
	private boolean mShowToolTipMarker;
	private boolean mToolTipAreaActive;
	private boolean mToolTipAreaSelected;
	private Rectangle mTooltipArea;
	private RibbonTooltip mTooltip;
	
	private RibbonTab mParentTab;
	private boolean mIsHoverGroup;
	
	private List<SelectionListener> mSelectionListeners;

	private AbstractRibbonGroupItem mMouseDownEscapeButton; 
	private boolean mFakeMouseDown;
	
	private List<RibbonToolbar> mToolbars;
	
	private List<ITooltipListener> mTooltipListeners;
	private Listener deHoverListener;
	
	public RibbonGroup(RibbonTab parent, String groupName) {
		super(parent.getFancyToolbar(), SWT.NO_BACKGROUND);
		mParentTab = parent;
		mToolBarParent = parent.getFancyToolbar();
		mGroupName = groupName;
		mShowToolTipMarker = false;
		parent.getFancyToolbar().groupAdded(this);
		init();
	}

	public RibbonGroup(RibbonTab parent, String groupName, RibbonTooltip toolTip) {
		super(parent.getFancyToolbar(), SWT.NO_BACKGROUND);
		mParentTab = parent;
		mGroupName = groupName;
		mToolBarParent = parent.getFancyToolbar();
		mToolItems = new ArrayList<AbstractRibbonGroupItem>();
		mShowToolTipMarker = (toolTip != null);
		mTooltip = toolTip;
		parent.getFancyToolbar().groupAdded(this);
		init();
	}

	private void init() {				
		mToolItems = new ArrayList<AbstractRibbonGroupItem>();
		mSelectedButtons = new ArrayList<AbstractRibbonGroupItem>();
		mSelectionListeners = new ArrayList<SelectionListener>();
		mToolbars = new ArrayList<RibbonToolbar>();
		mTooltipListeners = new ArrayList<ITooltipListener>();
		
		// TODO: Extract
		bpm = new DefaultButtonPaintManager(); 
		
		addMouseListener(this);
		addMouseTrackListener(this);
		addMouseMoveListener(this);
		
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent pe) {
				repaint(pe);
			}			
		});
		
	}
		
	protected void addToolbar(RibbonToolbar tb) {
		if (!mToolbars.contains(tb))
			mToolbars.add(tb);
		
		updateToolbarBounds();
	}
	
	protected void removeToolbar(RibbonToolbar tb) {
		mToolbars.remove(tb);
		
		updateToolbarBounds();
	}
	
	public void addTooltipListener(ITooltipListener listener) {
		if (!mTooltipListeners.contains(listener))
			mTooltipListeners.add(listener);
	}
	
	public void removeTooltipListener(ITooltipListener listener) {
		mTooltipListeners.remove(listener);
	}
	
	RibbonTooltip getTooltip(AbstractRibbonGroupItem item, boolean topOrLeft, boolean bottomOrRight) {
		for (int i = 0; i < mTooltipListeners.size(); i++) {
			RibbonTooltip rt = null;
			if (!topOrLeft && !bottomOrRight) {
				rt = mTooltipListeners.get(i).getTooltip(item);
			}
			else if (topOrLeft) {
				rt = mTooltipListeners.get(i).getTopOrLeftTooltip(item);
			}
			else if (bottomOrRight) {
				rt = mTooltipListeners.get(i).getBottomOrRightTooltip(item);
			}
			
			if (rt != null)
				return rt;
		}
		
		return null;
	}
	
	public RibbonTab getParentTab() {
		return mParentTab;
	}
	
	public void setTooltip(RibbonTooltip toolTip) {
		mShowToolTipMarker = (toolTip != null);
		mTooltip = toolTip;
	}
	
	public RibbonTooltip getTooltip() {
		return mTooltip;
	}
	
	public void mouseEnter(MouseEvent e) {
		mIsHoverGroup = true;
		redraw();
	}

	public void mouseExit(MouseEvent e) {
		// a little trick to not de-hover if the mouse didn't actually exit, but is still somewhere over a native widget
		if (!didMouseExit(e.x, e.y))
			return;
		
		mIsHoverGroup = false;
		deHover();
				
		if (mToolTipAreaActive) {
			mToolTipAreaActive = false;
			mToolTipAreaSelected = false;
			redraw(mTooltipArea.x-1, mTooltipArea.y-1, mTooltipArea.width+2, mTooltipArea.height+2, false);
		}

		redraw();
	}
	
	private boolean didMouseExit(int x, int y) {
		if (isInside(x, y, getBounds())) {
			// this deals with the problem that the mouse can escape due to the mouse exiting by the help of a menu
			// or other widget that blocks us, so if we sense any action outside of us then we dehover and remove
			// the listeners added to the display
			if (deHoverListener == null) {
				deHoverListener = new Listener() {
					public void handleEvent(Event event) {
						if (!isInside(event.x, event.y, getBounds())) {
							mIsHoverGroup = false;
							deHover();
							redraw();
						}
					}				
				};
				Display.getDefault().addFilter(SWT.MouseMove, deHoverListener);
				Display.getDefault().addFilter(SWT.MouseDown, deHoverListener);
			}
			return false;
		}

		return true;
	}

	public void mouseHover(MouseEvent me) {
		if (mTooltipArea != null && mTooltip != null) {
			if (isInside(me.x, me.y, mTooltipArea)) {
				RibbonTooltipDialog.makeDialog(mTooltip, toDisplay((new Point(mTooltipArea.x, getBounds().height + 5))));
			}
		}
		
		for (final AbstractRibbonGroupItem item : mToolItems) {
			if (item.getBounds() == null)
				continue;
							
			if (item instanceof RibbonButton) {
				if (isInside(me.x, me.y, item.getBounds())) {
					
					if (item.isSplit()) {
						RibbonTooltip tip = null;
						
						if (isInside(me.x, me.y, item.getTopBounds())) {
							if (item.getTopOrLeftToolTip() != null) 
								tip = item.getTopOrLeftToolTip();
							else
								tip = getTooltip(item, true, false);
						}
						else if (isInside(me.x, me.y, item.getBottomBounds())) {
							if (item.getBottomOrRightToolTip() != null)
								tip = item.getBottomOrRightToolTip();
							else
								tip = getTooltip(item, false, true);
						}
												
						if (tip != null) {
	    					RibbonTooltipDialog.makeDialog(tip, toDisplay((new Point(item.getBounds().x, getBounds().height + 5))));
							return;
						}
					}
					else {
						RibbonTooltip tip = null;

						if (item.getToolTip() != null) 							
							tip = item.getToolTip();
						else
							tip = getTooltip(item, false, false);
						
						if (tip != null) {
	    					RibbonTooltipDialog.makeDialog(item.getToolTip(), toDisplay((new Point(item.getBounds().x, getBounds().height + 5))));
							return;
						}
					}
				}
			}
			else if (item instanceof RibbonButtonGroup) {
				RibbonButtonGroup rbg = (RibbonButtonGroup) item;
				List<AbstractRibbonGroupItem> buttons = rbg.getButtons();
				for (AbstractRibbonGroupItem button : buttons) {
					if (button.getBounds() == null)
						continue;
										
					if (isInside(me.x, me.y, button.getBounds())) {
						if (item.isSplit()) {
							RibbonTooltip tip = null;
							
							if (isInside(me.x, me.y, item.getTopBounds())) {
								if (item.getTopOrLeftToolTip() != null) 
									tip = item.getTopOrLeftToolTip();
								else
									tip = getTooltip(item, true, false);								
							}
							else if (isInside(me.x, me.y, item.getBottomBounds())) {
								if (item.getBottomOrRightToolTip() != null)
									tip = item.getBottomOrRightToolTip();
								else
									tip = getTooltip(item, false, true);
							}
							
							if (tip != null) {
		    					RibbonTooltipDialog.makeDialog(tip, toDisplay((new Point(item.getBounds().x, getBounds().height + 5))));
								return;
							}
						}
						else {
							RibbonTooltip tip = null;

							if (item.getToolTip() != null) 							
								tip = item.getToolTip();
							else
								tip = getTooltip(item, false, false);
							
							if (tip != null) {
		    					RibbonTooltipDialog.makeDialog(button.getToolTip(), toDisplay((new Point(button.getBounds().x, getBounds().height + 5))));
								return;									
							}
						}
					}
				}
			}
		}
	}
	
	public void mouseDoubleClick(MouseEvent e) {
	}

	public void mouseDown(MouseEvent me) {
		RibbonTooltipDialog.kill();

		// as we allow mixing of native widgets they (for example, Buttons and Combos) will retain focus when we click around
		// as we aren't actually clicking any new widgets, we're just drawing as if it was true
		// therefore - move focus to the group which will remove focus from any native widget (which is the desired effect)
		if (Display.getDefault().getFocusControl() != RibbonGroup.this) 
			setFocus();
				
		if (mTooltipArea != null && isInside(me.x, me.y, mTooltipArea)) {
			fireSelection(me);
			mToolTipAreaActive = true;
			mToolTipAreaSelected = true;
			GC gc = new GC(RibbonGroup.this);
			drawToolTipMarker(gc);
			gc.dispose();					
//			return;
		}					
		
		// lets bunch together toolbar buttons with just the normal buttons, code-reuse done easy
		List<AbstractRibbonGroupItem> allItemsIncludingToolbar = new ArrayList<AbstractRibbonGroupItem>(mToolItems);
		for (RibbonToolbar tb : mToolbars) {
			List<RibbonToolbarGrouping> groups = tb.getGroupings();
			for (RibbonToolbarGrouping group : groups) {
				allItemsIncludingToolbar.addAll(group.getItems());
			}
		}
		
		for (final AbstractRibbonGroupItem item : allItemsIncludingToolbar) {
			if (item.getBounds() == null)
				continue;
							
			if (item instanceof RibbonButton) {

				if (!item.isEnabled())
					continue;

				if (isInside(me.x, me.y, item.getBounds())) {
					if (!item.isSelected()) {    							
						// button is part of a select group
						if (item.getButtonSelectGroup() != null) {
							deSelectGroup(item);
							
							item.setSelected(true);
							if (!mSelectedButtons.contains(item))
								mSelectedButtons.add(item);
														
							Rectangle noow = item.getBounds();
    						item.getParent().redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);	      
    						
    						if (item.isSplit() && !item.isToolbarButton()) {
								if (isInside(me.x, me.y, item.getTopBounds())) {
									if (!item.isTopSelected()) {
		        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);										
									}
									item.setTopSelected(true);
								}
								else
								if (isInside(me.x, me.y, item.getBottomBounds())) {
									if (!item.isBottomSelected()) {
		        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
									}
									item.setBottomSelected(true);
								}
    						}
    						else if (item.isToolbarButton()) {
    							// if split item, determine what part is hover
								if ((item.getStyle() & RibbonButton.STYLE_ARROW_DOWN_SPLIT) != 0) {
									if (isInside(me.x, me.y, item.getLeftBounds())) {
										if (!item.isLeftSelected()) {
											item.getParent().redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);										
										}
										
										// if it's a toggle, untoggle, but only left / top side
										if ((item.getStyle() & RibbonButton.STYLE_TOGGLE) != 0) {
											if (item.isLeftSelected()) {
												item.setLeftSelected(false);
												item.getParent().redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
											}
											else
												item.setLeftSelected(true);
										}
									}
									
									if (isInside(me.x, me.y, item.getRightBounds())) {
										if (!item.isRightSelected()) {
											item.getParent().redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
										}
										item.setRightSelected(true);
									}
								}
								else {
	    							item.getParent().redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
								}
    						}
    						
							if (!mFakeMouseDown) {
								item.notifySelectionListeners(me);
								mToolBarParent.toolItemSelectionChanged(item, RibbonGroup.this);							
								mLastSelectedButton = item;
							}
						}
						else {
							deSelectSingle(item);
							
							if (!mSelectedButtons.contains(item))
								mSelectedButtons.add(item);
							
							item.setSelected(true);
													
							Rectangle noow = item.getBounds();
    						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
    						
    						if (item.isSplit() && !item.isToolbarButton()) {
								if (isInside(me.x, me.y, item.getTopBounds())) {
									if (!item.isTopSelected()) {
		        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);										
									}
									
									// if it's a toggle, untoggle, but only left / top side
									if ((item.getStyle() & RibbonButton.STYLE_TOGGLE) != 0 && item.isTopSelected()) {
										item.setTopSelected(false);
										item.getParent().redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
									}
									else
										item.setTopSelected(true);
								}
								else
								if (isInside(me.x, me.y, item.getBottomBounds())) {
									if (!item.isBottomSelected()) {
		        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
									}
									item.setBottomSelected(true);
								}
    						}
    						else if (item.isToolbarButton()) {
    							// if split button, determine what part is hover
								if ((item.getStyle() & RibbonButton.STYLE_ARROW_DOWN_SPLIT) != 0) {
									if (isInside(me.x, me.y, item.getLeftBounds())) {
										if (!item.isLeftSelected()) {
			        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);										
										}
										item.setLeftSelected(true);
									}
									if (isInside(me.x, me.y, item.getRightBounds())) {
										if (!item.isRightSelected()) {
			        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
										}
										item.setRightSelected(true);
									}
								}
								else {
	        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
								}
    						}
    						
							if (!mFakeMouseDown) {
								item.notifySelectionListeners(me);
								mToolBarParent.toolItemSelectionChanged(item, RibbonGroup.this);						
								mLastSelectedButton = item;
							}
						}
					}
					else {
						if ((item.getStyle() & RibbonButton.STYLE_TOGGLE) != 0) {
							item.setSelected(false);
							Rectangle noow = item.getBounds();
							
							boolean any = false;
														
							// scenario: top was selected, so selected would've returned true, but used clicked bottom, deal with that
							if (isInside(me.x, me.y, item.getBottomBounds())) {
								item.setBottomSelected(true);
								any = true;
							}
							// same thing can happen on small buttons, so right-hand-side bounds apply
							if (isInside(me.x, me.y, item.getRightBounds())) {
								item.setRightSelected(true);
								any = true;
							}
							
							if (any) {
								item.notifySelectionListeners(me);
								mToolBarParent.toolItemSelectionChanged(item, RibbonGroup.this);						
								mLastSelectedButton = item;
							}
							
							redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
						}
					}
				}
			}
			else if (item instanceof RibbonButtonGroup) {
				RibbonButtonGroup rbg = (RibbonButtonGroup) item;
				List<AbstractRibbonGroupItem> buttons = rbg.getButtons();
				for (AbstractRibbonGroupItem button : buttons) {
					if (!button.isEnabled())
						continue;
					
					if (isInside(me.x, me.y, button.getBounds())) {
						
						if (button instanceof RibbonCheckbox) {

							if (!button.isEnabled())
								continue;

							if (isInside(me.x, me.y, button.getBounds())) {
								Rectangle noow = button.getBounds();

								// flip selection
								button.setSelected(!button.isSelected());
								
								// button is part of a select group
								if (button.getButtonSelectGroup() != null) {
									if (!mSelectedButtons.contains(button))
										mSelectedButtons.add(button);
																
									button.getParent().redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);	      
									
									if (!mFakeMouseDown) {
										button.notifySelectionListeners(me);
										mToolBarParent.toolItemSelectionChanged(button, RibbonGroup.this);							
										mLastSelectedButton = button;
									}
								}
								else {
									if (!mSelectedButtons.contains(button))
										mSelectedButtons.add(button);
															
									redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
															
									if (!mFakeMouseDown) {
										button.notifySelectionListeners(me);
										mToolBarParent.toolItemSelectionChanged(button, RibbonGroup.this);						
										mLastSelectedButton = button;
									}
								}
							}				
						}
						else if (button instanceof RibbonButton) { // instanceof else check
							
							// button is part of a select group
							if (button.getButtonSelectGroup() != null) {
								deSelectGroup(button);
								
								button.setSelected(true);
								if (!mSelectedButtons.contains(button))
									mSelectedButtons.add(button);
								
								Rectangle noow = button.getBounds();
								
								// if split button, determine what part is hover
								if ((button.getStyle() & RibbonButton.STYLE_ARROW_DOWN_SPLIT) != 0) {
									if (isInside(me.x, me.y, button.getLeftBounds())) {
										if (!button.isLeftSelected()) {
											button.getParent().redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);										
										}
										
										// if it's a toggle, untoggle, but only left / top side
										if ((button.getStyle() & RibbonButton.STYLE_TOGGLE) != 0) {
											if (button.isLeftSelected()) {
												button.setLeftSelected(false);
												button.getParent().redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
											}
											else
												button.setLeftSelected(true);
										}
									}
									
									if (isInside(me.x, me.y, button.getRightBounds())) {
										if (!button.isRightSelected()) {
											button.getParent().redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
										}
										button.setRightSelected(true);
									}
								}
								else {
	    							button.getParent().redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
								}
								
								if (!mFakeMouseDown) {
									button.notifySelectionListeners(me);
									mToolBarParent.toolItemSelectionChanged(button, RibbonGroup.this);							
									mLastSelectedButton = button;
								}
							}
							else {
								deSelectSingle(button);
								
								// if button style is to not depress, we never flag button as selected for long, see mouseUp()
								if ((button.getStyle() & AbstractRibbonGroupItem.STYLE_NO_DEPRESS) == 0) {
	    							if (!mSelectedButtons.contains(button))
	    								mSelectedButtons.add(button);
	    							
	    							button.setSelected(true);
								}
															
								Rectangle noow = button.getBounds();
	    						
	    						// if split button, determine what part is hover
								if ((button.getStyle() & RibbonButton.STYLE_ARROW_DOWN_SPLIT) != 0) {
									if (isInside(me.x, me.y, button.getLeftBounds())) {
										if (!button.isLeftSelected()) {
			        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);										
										}
										button.setLeftSelected(true);
									}
									if (isInside(me.x, me.y, button.getRightBounds())) {
										if (!button.isRightSelected()) {
			        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
										}
										button.setRightSelected(true);
									}
								}
								else {
	        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
								}
								
								if (!mFakeMouseDown) {
	    							button.notifySelectionListeners(me);
									mToolBarParent.toolItemSelectionChanged(button, RibbonGroup.this);							
									mLastSelectedButton = button;
								}
							}
						}

					}
				}
			}
		}
	}

	public void mouseUp(MouseEvent me) {
		if (mMouseDownEscapeButton != null) {
			mFakeMouseDown = false;
			if (isInside(me.x, me.y, mMouseDownEscapeButton.getBottomBounds())) {
				mouseDown(me);
			}
		}

		mFakeMouseDown = false;
		mMouseDownEscapeButton = null;

		// STYLE_NO_DEPRESS and STYLE_ARROW_DOWN_SPLIT buttons depress
		for (final AbstractRibbonGroupItem item : mSelectedButtons) {
			
			if (item instanceof RibbonButton) {
				if (isInside(me.x, me.y, item.getBounds())) 
					mLastMouseUpButton = item;
			}
			else if (item instanceof RibbonButtonGroup) {
				RibbonButtonGroup rbg = (RibbonButtonGroup) item;
				List<AbstractRibbonGroupItem> buttons = rbg.getButtons();
				for (AbstractRibbonGroupItem button : buttons) {
					if (isInside(me.x, me.y, button.getBounds())) 
						mLastMouseUpButton = item;					
				}
			}
			
			//if (isInside(me.x, me.y, item.getBounds())) {
				if ( ((item.getStyle() & AbstractRibbonGroupItem.STYLE_NO_DEPRESS) != 0) ||
					 ((item.getStyle() & AbstractRibbonGroupItem.STYLE_ARROW_DOWN_SPLIT) != 0) ) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (item.isSplit() && (item.getStyle() & AbstractRibbonGroupItem.STYLE_TOGGLE) != 0) {
								if (item.isLeftSelected() || item.isTopSelected()) {
									return;
								}
							}
								
							// if button style is to not depress, we never flag button as selected
							item.setSelected(false);
							Rectangle noow = item.getBounds();
    						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);						
							mSelectedButtons.remove(item);
							
						}            							
					});
				}
		//	}
		}
		
		// if corner was drawn down
		if (mToolTipAreaActive && mTooltipArea != null) {
			mToolTipAreaSelected = false;
			redraw(mTooltipArea.x, mTooltipArea.y, mTooltipArea.width, mTooltipArea.height, false);				
		}
		
	}
	
	public void mouseMove(MouseEvent me) {
		RibbonTooltipDialog.kill();
		
		// reset cursor, as resize cursor when we're in a RibbonShell can get stuck in the resize-mode despite being far away from the border
		if (Display.getDefault().getActiveShell() != null) {
			if (Display.getDefault().getActiveShell().getCursor() != null) 
				Display.getDefault().getActiveShell().setCursor(null);
		}
		
		// let's try to mimic an OS feature of "if mouse button is never depressed and mouse moves outside the bounds of the item 
		// it was originally pressed down on, pretend it's not in focus and was never pressed until mouse is actually released over the button"
		// (and if it's not released over the button the item was never truly clicked in the first place, unless it has a menu associated with it)
		if (me.stateMask == SWT.BUTTON1) {
			if (mHoverButton != null) {
				if (!isInside(me.x, me.y, mHoverButton.getBounds())) {

					// special case where user clicks a selected button and drags out, we should not de-select, so we cancel the action so to speak
					if (mHoverButton.isSelected() && mLastMouseUpButton == mLastSelectedButton) 
						return;
					
					Rectangle noow = mHoverButton.getBounds();
					mMouseDownEscapeButton = mHoverButton;
					mHoverButton.setSelected(false);
					mHoverButton.setHoverButton(false);
					mHoverButton = null;
					redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
				}
				return;
			}						
			
			if (mMouseDownEscapeButton != null) {
				if (isInside(me.x, me.y, mMouseDownEscapeButton.getBounds())) {
					mHoverButton = mMouseDownEscapeButton;
					// we need to fake a mousedown
					try {
						mFakeMouseDown = true;
						mouseDown(me);					
					}
					catch (Exception err) {
						err.printStackTrace();
					}
					finally {
						mFakeMouseDown = false;
					}
				}
				
				return;
			}
		}
						
/*		// if we're inside the area...
		if (isInside(me.x, me.y, getBounds())) {
			// we're not active, we need to fade in
			if (!isActiveHover) {
				isActiveHover = true;
				//fadeIn();
			}
		}
		else {					
			if (isActiveHover) {
				//fadeOut();
				isActiveHover = false;
			}
		}
*/		
		if (mTooltipArea != null) {
			if (isInside(me.x, me.y, mTooltipArea)) {
				if (!mToolTipAreaActive) {
					mToolTipAreaActive = true;
					redraw(mTooltipArea.x-1, mTooltipArea.y-1, mTooltipArea.width+2, mTooltipArea.height+2, false);
					return; // we can return as we're 100% not over an item in the toolbar
				}
			}
			else {
				if (mToolTipAreaActive) {
					mToolTipAreaActive = false;
					redraw(mTooltipArea.x-1, mTooltipArea.y-1, mTooltipArea.width+2, mTooltipArea.height+2, false);
				}
			}					
		}
		
		// lets bunch together toolbar buttons with just the normal buttons, code-reuse done easy
		List<AbstractRibbonGroupItem> allItemsIncludingToolbar = new ArrayList<AbstractRibbonGroupItem>(mToolItems);
		for (RibbonToolbar tb : mToolbars) {
			List<RibbonToolbarGrouping> groups = tb.getGroupings();
			for (RibbonToolbarGrouping group : groups) {
				allItemsIncludingToolbar.addAll(group.getItems());
			}
		}
		
		// check button hovers, redraw old areas exactly (+spacing) where we need to, less flicker
		for (AbstractRibbonGroupItem item : allItemsIncludingToolbar) {
			if (item.getBounds() == null)
				continue;
						
			if (isInside(me.x, me.y, item.getBounds())) {						
				//System.err.println("Inside group");

				if (item instanceof RibbonButton) {
					if (!item.isHoverButton()) {
						if (mHoverButton != null && mHoverButton != item) {
							Rectangle old = mHoverButton.getBounds();
							redraw(old.x-2, old.y, old.width+4, old.height, false);
						}
						item.setHoverButton(true);
						mHoverButton = item;
						mToolBarParent.toolItemHoverChanged(item, RibbonGroup.this);
						Rectangle noow = item.getBounds();
						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
					}
					
					// if split button, determine what part is hover
					if (item.isSplit() && !item.isToolbarButton()) {
						if (isInside(me.x, me.y, item.getTopBounds())) {
							if (!item.isTopHovered()) {
        						Rectangle noow = item.getBounds();
        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);										
							}
							item.setTopHovered(true);
						}
						// TODO: We could do an else check... I think
						if (isInside(me.x, me.y, item.getBottomBounds())) {
							if (!item.isBottomHovered()) {
        						Rectangle noow = item.getBounds();
        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
							}
							item.setBottomHovered(true);
						}
					}
					else if (item.isToolbarButton()) {
						Rectangle old = mHoverButton.getBounds();
						boolean changed = false;
						
						// if split item, determine what part is hover
						if ((item.getStyle() & RibbonButton.STYLE_ARROW_DOWN_SPLIT) != 0) {        								
							if (isInside(me.x, me.y, item.getLeftBounds())) {
								if (!item.isLeftHovered()) {
    								redraw(old.x-2, old.y, old.width+4, old.height, false);
    								changed = true;
								}
								item.setLeftHovered(true);
							}
							else if (isInside(me.x, me.y, item.getRightBounds())) {
								if (!item.isRightHovered()) {
    								redraw(old.x-2, old.y, old.width+4, old.height, false);
    								changed = true;
								}
								item.setRightHovered(true);
							}
						
							if (!changed)
								continue;
						}		    	
					}
				}
				else if (item instanceof RibbonButtonGroup) {							
					RibbonButtonGroup rbg = (RibbonButtonGroup) item;
					List<AbstractRibbonGroupItem> buttons = rbg.getButtons();
					for (AbstractRibbonGroupItem button : buttons) {
						if (isInside(me.x, me.y, button.getBounds())) {			
							
							if (button instanceof RibbonCheckbox) {
								if (!button.isHoverButton()) {
									if (mHoverButton != null && mHoverButton != button) {
										Rectangle old = mHoverButton.getBounds();
										redraw(old.x-2, old.y, old.width+4, old.height, false);
									}
									button.setHoverButton(true);
									mHoverButton = button;
									mToolBarParent.toolItemHoverChanged(button, RibbonGroup.this);
									Rectangle noow = button.getBounds();
									redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
								}
							} 
							else {
								if (mHoverButton != null && mHoverButton != button) {
									Rectangle old = mHoverButton.getBounds();
									redraw(old.x-2, old.y, old.width+4, old.height, false);
								}
								        							        							
	    						if (mHoverButton != null && mHoverButton == button && button.isHoverButton()) {	    								
	
	    							if (mHoverButton.isSplit()) {
	    								Rectangle old = mHoverButton.getBounds();
	    								boolean changed = false;
	    								
	    								// if split button, determine what part is hover
	        							if ((button.getStyle() & RibbonButton.STYLE_ARROW_DOWN_SPLIT) != 0) {        								
	        								if (isInside(me.x, me.y, button.getLeftBounds())) {
	        									if (!button.isLeftHovered()) {
	    	        								redraw(old.x-2, old.y, old.width+4, old.height, false);
	    	        								changed = true;
	        									}
	        									button.setLeftHovered(true);
	        								}
	        								else if (isInside(me.x, me.y, button.getRightBounds())) {
	        									if (!button.isRightHovered()) {
	    	        								redraw(old.x-2, old.y, old.width+4, old.height, false);
	    	        								changed = true;
	        									}
	        									button.setRightHovered(true);
	        								}
	        							
	        								if (!changed)
	        									continue;
	        							}		    								
	    							}
	    							else {
	    								continue;
	    							}
	    						}
								
	    						mHoverButton = button;
	
	    						button.setHoverButton(true);
	    						mToolBarParent.toolItemHoverChanged(button, RibbonGroup.this);
	    						Rectangle noow = button.getBounds();
	    						
	    						// if split button, determine what part is hover
								if ((button.getStyle() & RibbonButton.STYLE_ARROW_DOWN_SPLIT) != 0) {        								
									if (isInside(me.x, me.y, button.getLeftBounds())) {
										if (!button.isLeftHovered()) {
			        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);										
										}
										button.setLeftHovered(true);
									}
									// TODO: We could do an else check... I think
									if (isInside(me.x, me.y, button.getRightBounds())) {
										if (!button.isRightHovered()) {
			        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
										}
										button.setRightHovered(true);
									}
								}
								else {
	        						redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
								}
							}
						}
						else {
							if (button.isHoverButton()) {
								if (button.isHoverButton()) {
	    							Rectangle old = button.getBounds();
	    							redraw(old.x-2, old.y, old.width+4, old.height, false);
	    						}
	    						button.setHoverButton(false);
	    						mToolBarParent.toolItemHoverChanged(null, RibbonGroup.this);
							}
						}
					}
				}
				
			} // isInside() check
			else {						
				if (item instanceof RibbonButton) {
					if (item.isHoverButton()) {
						Rectangle old = item.getBounds();
						redraw(old.x-2, old.y, old.width+4, old.height, false);
					}
					item.setHoverButton(false);
					mToolBarParent.toolItemHoverChanged(null, RibbonGroup.this);
				}
				else if (item instanceof RibbonButtonGroup) {
					RibbonButtonGroup rbg = (RibbonButtonGroup) item;
					List<AbstractRibbonGroupItem> buttons = rbg.getButtons();
					for (AbstractRibbonGroupItem button : buttons) {
						
						if (button instanceof RibbonCheckbox) {
							if (button.isHoverButton()) {
								Rectangle old = button.getBounds();
								redraw(old.x-2, old.y, old.width+4, old.height, false);
							}
							button.setHoverButton(false);
							mToolBarParent.toolItemHoverChanged(null, RibbonGroup.this);					
						}
						else {
							if (button.isHoverButton()) {
	    						mToolBarParent.toolItemHoverChanged(null, RibbonGroup.this);								
								Rectangle old = button.getBounds();
								redraw(old.x-2, old.y, old.width+4, old.height, false);
	    						button.setHoverButton(false);
							}
						}
					}
				}
				
			}
			
		}
		
	}	

	public List<AbstractRibbonGroupItem> getSelection() {
		return mSelectedButtons;
	}
	
	protected AbstractRibbonGroupItem getHoverItem() {
		return mHoverButton;
	}
	
	protected void deSelect(AbstractRibbonGroupItem item) {
		if (item.getButtonSelectGroup() == null)
			deSelectSingle(item);
		else
			deSelectGroup(item);
	}
	
	protected void deSelectGroup(AbstractRibbonGroupItem item) {
		if (item == null)
			return;
				
		ButtonSelectGroup bsg = item.getButtonSelectGroup();
		AbstractRibbonGroupItem oldGroupSelection = bsg.getSelection();
		
		if (oldGroupSelection != null && oldGroupSelection != item) {
			oldGroupSelection.setSelected(false);
			Rectangle noow = oldGroupSelection.getBounds();
			// redraw the right parent! might not be us, we don't limit connected buttons to one visual area
			oldGroupSelection.getParent().redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
			mSelectedButtons.remove(oldGroupSelection);
		}
	}
	
	protected void deSelectSingle(AbstractRibbonGroupItem item) {
		if (item == null) 
			return;
	
		item.setSelected(false);
		Rectangle noow = item.getBounds();
		redraw(noow.x-2, noow.y, noow.width+4, noow.height, false);
		mSelectedButtons.remove(item);
	}
	
	protected void deHover() {
		if (mHoverButton != null) {
    		Rectangle rect = mHoverButton.getBounds();
    		redraw(rect.x-4, rect.y, rect.width+8, rect.height, false);
    		mHoverButton.setHoverButton(false);
    		mHoverButton = null;
		}
		
		// removes any display listeners if there are any
		if (deHoverListener != null) {
			Display.getDefault().removeFilter(SWT.MouseDown, deHoverListener);
			Display.getDefault().removeFilter(SWT.MouseMove, deHoverListener);
			deHoverListener = null;
		}
		
	/*	boolean redraw = false;
		
		for (int i = 0; i < mToolItems.size(); i++) {
			if (mToolItems.get(i).isHoverButton()) {
				redraw = true;			
				mToolItems.get(i).setHoverButton(false);
			}
		}
		
		if (redraw)
			redraw();*/
	}
	
	// the repaint event, whenever the composite needs to refresh the contents
	private void repaint(PaintEvent event) {
		GC gc = event.gc;
		drawOntoGC(gc);
 	}
	
	private void drawOntoGC(GC gc) {
		//mCurrentIteraton = 0;
		
		//System.err.println("DRAW");
		drawGroup(gc);
		drawText(gc);		
		drawToolItems(gc);		
		drawToolbars(gc);
		if (mShowToolTipMarker)
			drawToolTipMarker(gc);

	}
	
/*	private void fadeIn() {
		if (fadeOutTimer != null) {
			//System.err.println("Killing fadeout timer!");
			Display.getDefault().timerExec(-1, fadeOutTimer);
			mCurrentIteraton = 0;
			fadeOutTimer = null;
		}
		
		fadeInTimer = new Runnable() {
			@Override
			public void run() {
				//System.err.println(" " + mCurrentIteraton);
				mCurrentIteraton++;
				
				// widget killed mid-loop
				if (FancyToolGroup.this.isDisposed())
					return;
				
				GC temp = new GC(FancyToolGroup.this);
				fadeIn(temp);
				temp.dispose();
				
				if (mCurrentIteraton < mMaxIterations) 
					Display.getDefault().timerExec(mTimeIncreaser, this);					
				else {
					//System.err.println("End");
					mCurrentIteraton = 0;
					fadeInTimer = null;

					GC temp2 = new GC(FancyToolGroup.this);
					paintBackground(temp2);
					paintBorder(temp2);
					paintText(temp2);
					temp2.dispose();
				}
			}			
		};
		Display.getDefault().timerExec(mTimeIncreaser, fadeInTimer);
			
	}
	
	private void fadeIn(GC gc) {
		Rectangle bounds = getBounds();
		Color topDark = getBetween(mBlueTop_Dark, mBlueTop_Hover_Dark);
		Color topLight = getBetween(mBlueTop_Light, mBlueTop_Hover_Light);
		
		gc.setBackground(topDark);
		gc.setForeground(topLight);
		gc.fillGradientRectangle(mBorderSize, mBorderSize, bounds.width-mBorderSize, mTopHeight, true);

		Color midDark = getBetween(mBlueMid_Dark, mBlueMid_Hover_Dark);
		Color midLight = getBetween(mBlueMid_Light, mBlueMid_Hover_Light);

		gc.setBackground(midLight);
		gc.setForeground(midDark);
		gc.fillGradientRectangle(mBorderSize, mBorderSize+mTopHeight, bounds.width-mBorderSize, mMidHeight, true);

		Color botDark = getBetween(mBlueBot_Dark, mBlueBot_Hover_Dark);
		Color botLight = getBetween(mBlueBot_Light, mBlueBot_Hover_Light);
		
		gc.setBackground(botLight);
		gc.setForeground(botDark);
		gc.fillGradientRectangle(mBorderSize, mBorderSize+mTopHeight+mMidHeight, bounds.width-mBorderSize, mBotHeight, true);
		
		paintText(gc);
		
		topDark.dispose();
		topLight.dispose();
		midDark.dispose();
		midLight.dispose();
		botDark.dispose();
		botLight.dispose();
	}
	
	private void fadeOut() {
		if (fadeInTimer != null) {
			//System.err.println("Killing fadein timer!");
			Display.getDefault().timerExec(-1, fadeInTimer);
			mCurrentIteraton = 0;
			fadeInTimer = null;
		}
		
		fadeOutTimer = new Runnable() {
			@Override
			public void run() {
				//System.err.println("fading out " + mCurrentIteraton);
				mCurrentIteraton++;
				
				// widget killed mid-loop
				if (FancyToolGroup.this.isDisposed())
					return;
				
				GC temp = new GC(FancyToolGroup.this);
				fadeOut(temp);
				temp.dispose();
				
				if (mCurrentIteraton < mMaxIterations) 
					Display.getDefault().timerExec(mTimeIncreaser, this);					
				else {
					//System.err.println("End");
					mCurrentIteraton = 0;
					fadeOutTimer = null;
					
					GC temp2 = new GC(FancyToolGroup.this);
					paintBackground(temp2);
					paintBorder(temp2);
					paintText(temp2);
					temp2.dispose();
				}
			}			
		};
		Display.getDefault().timerExec(mTimeIncreaser, fadeOutTimer);
	}
	
	private void fadeOut(GC gc) {
		Rectangle bounds = getBounds();
		Color topDark = getBetween(mBlueTop_Hover_Dark, mBlueTop_Dark);
		Color topLight = getBetween(mBlueTop_Hover_Light, mBlueTop_Light);
		
		gc.setBackground(topDark);
		gc.setForeground(topLight);
		gc.fillGradientRectangle(2, mBorderSize, bounds.width-mBorderSize, mTopHeight, true);

		Color midDark = getBetween(mBlueMid_Hover_Dark, mBlueMid_Dark);
		Color midLight = getBetween(mBlueMid_Hover_Light, mBlueMid_Light);

		gc.setBackground(midLight);
		gc.setForeground(midDark);
		gc.fillGradientRectangle(2, mBorderSize+mTopHeight, bounds.width-mBorderSize, mMidHeight, true);

		Color botDark = getBetween(mBlueBot_Hover_Dark, mBlueBot_Dark);
		Color botLight = getBetween(mBlueBot_Hover_Light, mBlueBot_Light);
		
		gc.setBackground(botLight);
		gc.setForeground(botDark);
		gc.fillGradientRectangle(2, mBorderSize+mTopHeight+mMidHeight, bounds.width-mBorderSize, mBotHeight, true);
		
		paintText(gc);
		
		topDark.dispose();
		topLight.dispose();
		midDark.dispose();
		midLight.dispose();
		botDark.dispose();
		botLight.dispose();
	}

	// gets a color mixed from two colors where color one is the dark and two is the light
	private Color getBetween(Color one, Color two) {
		RGB rgbTwo = two.getRGB();
		RGB rgbOne = one.getRGB();
		
		int rangeRed = rgbTwo.red - rgbOne.red;
		int rangeBlue = rgbTwo.blue - rgbOne.blue;
		int rangeGreen = rgbTwo.green - rgbOne.green;
		
		int increaserRed = rangeRed / mMaxIterations; 
		int increaserBlue = rangeBlue / mMaxIterations;
		int increaserGreen = rangeGreen / mMaxIterations;
		
		int finRed = rgbOne.red + (increaserRed * mCurrentIteraton);
		int finGreen = rgbOne.green + (increaserGreen * mCurrentIteraton);
		int finBlue = rgbOne.blue + (increaserBlue * mCurrentIteraton);
		if (finRed > 255)
			finRed = 255;
		if (finGreen > 255)
			finGreen = 255;
		if (finBlue > 255)
			finBlue = 255;
		
		Color col = new Color(Display.getDefault(), new RGB(
				finRed, 
				finGreen,
				finBlue
		));
		
		return col;
	}
	*/
	
	private void drawText(GC gc) {
		if (mGroupName == null)
			mGroupName = "";
				
		int xWidth = getBounds().width;
		Point extent = gc.stringExtent(mGroupName);
		int xStart = (xWidth/2) - (extent.x/2);
		int yStart = getBounds().height - BUTTON_BOT_HEIGHT - mBorderSize + (BUTTON_BOT_HEIGHT/2) - (extent.y/2);
		
		if (mShowToolTipMarker)
			xStart -= 7;
		
/*		gc.setForeground(mTextColorRight);
		gc.drawString(mGroupName, xStart+1, yStart, true);	
*/		gc.setForeground(mTextColor);
		gc.drawString(mGroupName, xStart, yStart, true);	
		
	}
	
	private void drawToolItems(GC gc) {
		for (AbstractRibbonGroupItem item : mToolItems) {
			bpm.drawItem(gc, item);
		}				
	}
	
	private void drawToolbars(GC gc) {
		for (RibbonToolbar tb : mToolbars) {
			bpm.drawToolbar(gc, tb);
		}
	}
	
	private void drawGroup(GC gc) {
		Rectangle outer = getBounds();
		
		// FIRST OF ALL, WE NEED TO MIMIC THE BACKGROUND OF OUR PARENT WHERE WE ARE
		// otherwise we'll get strange transparency and other artifacts left over depending on how we draw
		// to make this easy and to ensure exact pixel layover, let's just have the parent paint on us first!
		// then we'll just draw wherever we want, how nice!
		mToolBarParent.drawOntoGC(gc, outer);
		
		// top line
		if (!mIsHoverGroup) 
			gc.setForeground(mBorderBlueTop);
		else
			gc.setForeground(mBorderBlueTop_Hover);
		
		gc.drawLine(outer.x+2, 0, outer.x+outer.width-(!mIsHoverGroup ? 4 : 3), 0);
		// two pixels in corners, basically same color, but it's 1 pixel shade different, if we care in the future
		gc.drawLine(outer.x+1, 1, outer.x+1, 1);
		if (!mIsHoverGroup)
			gc.drawLine(outer.x+outer.width-3, 1, outer.x+outer.width-3, 1);
		else
			gc.drawLine(outer.x+outer.width-2, 1, outer.x+outer.width-2, 1);
		
		if (!mIsHoverGroup) {
			gc.setForeground(mBorderBlueSideTopTop);
			gc.setBackground(mBorderBlueSideTopBottom);
		}
		else {
			gc.setForeground(mBorderBlueSideTopTop_Hover);
			gc.setBackground(mBorderBlueSideTopBottom_Hover);
		}
		gc.fillGradientRectangle(outer.x, 2, 1, 13, true);
		// right side differs for hover or not
		if (!mIsHoverGroup)
			gc.fillGradientRectangle(outer.x+outer.width-2, 2, 1, 13, true);
		else
			gc.fillGradientRectangle(outer.x+outer.width-1, 2, 1, 13, true);
		
		if (!mIsHoverGroup) {
			gc.setForeground(mBorderBlueSideBottomTop);
			gc.setBackground(mBorderBlueSideBottomBottom);
		}
		else {
			gc.setForeground(mBorderBlueSideBottomTop_Hover);
			gc.setBackground(mBorderBlueSideBottomBottom_Hover);			
		}
		gc.fillGradientRectangle(outer.x, 2+13, 1, 68, true);		
		if (!mIsHoverGroup)
			gc.fillGradientRectangle(outer.x+outer.width-2, 2+13, 1, 68, true);
		else
			gc.fillGradientRectangle(outer.x+outer.width-1, 2+13, 1, 68, true);
		
		// bottom border and the one remaining pixel in the corner, same deal as above with being 1 pixel in color off, but fix in future or not
		if (!mIsHoverGroup)
			gc.setForeground(mBorderBlueBottom);
		else
			gc.setForeground(mBorderBlueBottom_Hover);
		gc.drawLine(outer.x+1, 2+13+68, outer.x+1, 2+13+68);
		gc.drawLine(outer.x+outer.width-3, 2+13+68, outer.x+outer.width-3, 2+13+68);
		// bottom outer
		gc.drawLine(outer.x+2, 2+13+68+1, outer.x+outer.width-(!mIsHoverGroup ? 4 : 3), 2+13+68+1);
		
		// INNER BORDER + DROPSHADOW
		
		// top border
		if (!mIsHoverGroup)
			gc.setForeground(mBorderInnerBlueTop);
		else
			gc.setForeground(mBorderInnerBlueTop_Hover);
		gc.drawLine(outer.x+3, 1, outer.x+outer.width-(!mIsHoverGroup ? 4 : 3), 1);

		// FILLS
		// now we fill the top part as we need to chomp a pixel off in the top corner of the top fill
		if (!mIsHoverGroup) {			
			gc.setForeground(mInnerFillTopTop);
			gc.setBackground(mInnerFillTopBottom);
		}
		else {
			gc.setForeground(mInnerFillTopTop_Hover);
			gc.setBackground(mInnerFillTopBottom_Hover);			
		}
		gc.fillGradientRectangle(outer.x+2, 2, outer.width-4, 13, true);
		// fill middle part
		if (!mIsHoverGroup) {
			gc.setForeground(mInnerFillMidTop);
			gc.setBackground(mInnerFillMidBottm);
		}
		else {
			gc.setForeground(mInnerFillMidTop_Hover);
			gc.setBackground(mInnerFillMidBottm_Hover);
		}
		gc.fillGradientRectangle(outer.x+2, 2+13, outer.width-4, 54, true);
		// fill bottom part
		if (!mIsHoverGroup) {
			gc.setForeground(mInnerFillBottomTop);
			gc.setBackground(mInnerFillBottomBottom);
			gc.fillGradientRectangle(outer.x+2, 2+13+54, outer.width-4, 15, true);
		}
		else {
			gc.setForeground(mInnerFillBottomTop_Hover);
			gc.setBackground(mInnerFillBottomBottom_Hover);
			gc.fillGradientRectangle(outer.x+2, 2+13+54, outer.width-4, 11, true);
			gc.setForeground(mInnerFillBottomBottom_Hover);
			gc.setBackground(mInnerFillBottomBottomHighlight_Hover);
			gc.fillGradientRectangle(outer.x+2, 2+13+54+11, outer.width-4, 4, true);
			gc.setForeground(mBorderInnerSideBottomBottom_Hover);
			gc.drawLine(outer.x+2, 2+13+54+11+3, outer.x+outer.width-3, 2+13+54+11+3);
		}

		// INNER LINES
		if (!mIsHoverGroup) {
			gc.setForeground(mBorderInnerSideTopTop);
			gc.setBackground(mBorderInnerSideTopBottom);
		} 
		else {
			gc.setForeground(mBorderInnerSideTopTop_Hover);
			gc.setBackground(mBorderInnerSideTopBottom_Hover);
		}
		gc.fillGradientRectangle(outer.x+1, 3, 1, 12, true);
		
		if (!mIsHoverGroup) {
			gc.setForeground(mBorderInnerSideMidTop);
			gc.setForeground(mBorderInnerSideMidBottom);			
		}
		else {
			gc.setForeground(mBorderInnerSideMidTop_Hover);
			gc.setForeground(mBorderInnerSideMidBottom_Hover);
		}
		gc.fillGradientRectangle(outer.x+1, 3+12, 1, 54, true);
		
		if (!mIsHoverGroup) {
			gc.setForeground(mBorderInnerSideBottomTop);
			gc.setBackground(mBorderInnerSideBottomBottom);
		}
		else {
			gc.setForeground(mBorderInnerSideBottomTop_Hover);
			gc.setBackground(mBorderInnerSideBottomBottom_Hover);
		}
		gc.fillGradientRectangle(outer.x+1, 3+12+54, 1, 14, true);
		
		// SHADOW
		if (!mIsHoverGroup) {
			gc.setForeground(mShadowTopTop);
			gc.setBackground(mShadowTopBottom);
			gc.fillGradientRectangle(outer.x+outer.width-1, 2, 1, 13, true);
			gc.setForeground(mShadowFadePixel);
			gc.drawLine(outer.x+outer.width-1, 2, outer.x+outer.width-1, 2);
			
			gc.setForeground(mShadowMidTop);
			gc.setBackground(mShadowMidBottom);
			gc.fillGradientRectangle(outer.x+outer.width-1, 2+13, 1, 54, true);
			
			gc.setForeground(mShadowBottomTop);
			gc.setBackground(mShadowBottomBottom);
			gc.fillGradientRectangle(outer.x+outer.width-1, 2+13+54, 1, 13, true);
		}
		else {
			gc.setForeground(mBorderInnerSideTopTop_Hover);
			gc.setBackground(mBorderInnerSideTopBottom_Hover);
			gc.fillGradientRectangle(outer.x+outer.width-2, 2, 1, 13, true);
/*			gc.setForeground(mShadowFadePixel);
			gc.drawLine(outer.x+outer.width-2, 2, outer.x+outer.width-2, 2);
*/			
			gc.setForeground(mBorderInnerSideMidTop_Hover);
			gc.setBackground(mBorderInnerSideMidBottom_Hover);
			gc.fillGradientRectangle(outer.x+outer.width-2, 2+13, 1, 54, true);
			
			gc.setForeground(mBorderInnerSideBottomTop_Hover);
			gc.setBackground(mBorderInnerSideBottomBottom_Hover);
			gc.fillGradientRectangle(outer.x+outer.width-2, 2+13+54, 1, 14, true);			
		}
		
		// FADED PIXELS IN CORNERS
		if (!mIsHoverGroup)
			gc.setForeground(mBorderFadePixelTop);
		else
			gc.setForeground(mBorderFadePixelTop_Hover);
		
		// left top
		gc.drawLine(outer.x, 1, outer.x, 1);
		gc.drawLine(outer.x+1, 0, outer.x+1, 0);

		if (!mIsHoverGroup) {
			// right top (is 1 further in towards the left as it has a "drop shadow")
			gc.drawLine(outer.x+outer.width-3, 0, outer.x+outer.width-3, 0);
			gc.drawLine(outer.x+outer.width-2, 1, outer.x+outer.width-2, 1);
		}
		else {
			gc.drawLine(outer.x+outer.width-2, 0, outer.x+outer.width-2, 0);
			gc.drawLine(outer.x+outer.width-1, 1, outer.x+outer.width-1, 1);	
		}
				
		if (!mIsHoverGroup) {
			gc.setForeground(mBorderInnerFadePixelTop);
			// left top (there is no right top or bottom right or left, only top left)
			gc.drawLine(outer.x+2, 1, outer.x+2, 1);
			gc.drawLine(outer.x+1, 2, outer.x+1, 2);
		}
		
		// draw bottom corners, same deal as top, but different colors
		if (!mIsHoverGroup)
			gc.setForeground(mBorderFadePixelBottom);
		else
			gc.setForeground(mBorderFadePixelBottom_Hover);
		
		// bottom left
		gc.drawLine(outer.x, 2+13+68, outer.x, 2+13+68);
		gc.drawLine(outer.x+1, 2+13+68+1, outer.x+1, 2+13+68+1);
		// bottom right
		if (!mIsHoverGroup) {
			gc.drawLine(outer.x+outer.width-2, 2+13+68, outer.x+outer.width-2, 2+13+68);
			gc.drawLine(outer.x+outer.width-3, 2+13+68+1, outer.x+outer.width-3, 2+13+68+1);
			gc.setForeground(mBorderBlueBottom);
			gc.drawLine(outer.x+outer.width-3, 2+13+68, outer.x+outer.width-3, 2+13+68);
		}
		else {
			gc.drawLine(outer.x+outer.width-1, 2+13+68, outer.x+outer.width-1, 2+13+68);
			gc.drawLine(outer.x+outer.width-2, 2+13+68+1, outer.x+outer.width-2, 2+13+68+1);
			gc.setForeground(mBorderBlueBottom_Hover);
			gc.drawLine(outer.x+outer.width-2, 2+13+68, outer.x+outer.width-2, 2+13+68);			
		}
		
		// TODO: (FIX HOW!?) top left corner - truly, as for some reason, transparency is failing for that one pixel, no biggie, but sucks, I hate hacks like this
		gc.setForeground(ColorCache.getInstance().getColor(218, 229, 244));
		gc.drawLine(outer.x, outer.y, outer.x, outer.y);
	}
	
	// TODO: Not exact yet when selected
	private void drawToolTipMarker(GC gc) {
		Rectangle bounds = getBounds();
		int left = bounds.width - 14;
		int top = bounds.height - 13;
		
		mTooltipArea = new Rectangle(bounds.width-18, bounds.height-16, 14, 13);
		int rectTop = mTooltipArea.y;
		int rectLeft = mTooltipArea.x;
		
		int oldAlpha = gc.getAlpha();
		
		if (mToolTipAreaActive) {
			if (!mToolTipAreaSelected) {
				// draw fill in background
				gc.setForeground(mToolTipBorder);
				gc.drawRectangle(rectLeft, rectTop, 14, 13);
				gc.setForeground(mToolTipFillTopTop);
				gc.setBackground(mToolTipFillTopBottom);
				gc.fillGradientRectangle(rectLeft+2, rectTop+2, 11, 5, true);
				gc.setForeground(mToolTipFillBottomTop);
				gc.setBackground(mToolTipFillBottomBottom);
				gc.fillGradientRectangle(rectLeft+2, rectTop+2+5, 11, 5, true);
			}
			else {
				gc.setForeground(mToolTipBorder_Selected);
				gc.drawRectangle(rectLeft, rectTop, 14, 13);
				gc.setForeground(mToolTipFillTopTop_Selected);
				gc.setBackground(mToolTipFillTopBottom_Selected);
				gc.fillGradientRectangle(rectLeft+1, rectTop+1, 13, 6, true);
				gc.setForeground(mToolTipFillBottomTop_Selected);
				gc.setBackground(mToolTipFillBottomBottom_Selected);
				gc.fillGradientRectangle(rectLeft+1, rectTop+1+6, 13, 6, true);
			}
		
			if (mToolTipAreaSelected)
				gc.setAlpha(127);
			
			gc.setForeground(mToolTipMarkerShadow);
			gc.drawRectangle(rectLeft+1, rectTop+1, 12, 11);
			
			if (mToolTipAreaSelected)
				gc.setAlpha(oldAlpha);
		}
		
		//TODO: Why don't stickouts draw on select + hover?
				
		// top left
		gc.setBackground(mToolTipMarker);
		gc.setForeground(mToolTipMarker);
		gc.drawLine(left, top, left+5, top);
		gc.drawLine(left, top, left, top+5);
		
		// dot
		gc.drawLine(left+3, top+3, left+3, top+3);
		
		// square
		gc.fillRectangle(left+4, top+4, 3, 3);
		
		// stick-outs
		gc.drawLine(left+3, top+6, left+3, top+6);
		gc.drawLine(left+6, top+3, left+6, top+3);
		
		// top left
				
		if (mToolTipAreaActive)
			gc.setAlpha(127);
		
		gc.setForeground(mToolTipMarkerShadow);
		gc.drawLine(left+1, top+1, left+5, top+1);
		gc.drawLine(left+1, top+1, left+1, top+5);
		
		// dot
		gc.drawLine(left+4, top+3, left+4, top+3);
		
		// square shadow
		gc.drawLine(left+7, top+3, left+7, top+3+4);
		gc.drawLine(left+4, top+7, left+4+3, top+7);
		
		// reset
		if (mToolTipAreaActive || mToolTipAreaSelected)
			gc.setAlpha(oldAlpha);
	}
		
	/*private void drawBackground(GC gc) {
		if (true)
			return;
		
		Rectangle bounds = getBounds();
		
		if (isActiveHover) {
    		gc.setBackground(mBlueTop_Hover_Dark);
    		gc.setForeground(mBlueTop_Hover_Light);			
		}
		else {
    		gc.setBackground(mBlueTop_Dark);
    		gc.setForeground(mBlueTop_Light);
		}
		gc.fillGradientRectangle(0, mBorderSize, bounds.width-mBorderSize, BUTTON_TOP_HEIGHT, true);

		if (isActiveHover) {
			gc.setBackground(mBlueMid_Hover_Light);
			gc.setForeground(mBlueMid_Hover_Dark);			
		} 
		else {
    		gc.setBackground(mBlueMid_Light);
    		gc.setForeground(mBlueMid_Dark);
		}
		gc.fillGradientRectangle(0, mBorderSize+BUTTON_TOP_HEIGHT, bounds.width-mBorderSize, BUTTON_MID_HEIGHT, true);

		if (isActiveHover) {
			gc.setBackground(mBlueBot_Hover_Light);
			gc.setForeground(mBlueBot_Hover_Dark);						
		}
		else {
			gc.setBackground(mBlueBot_Light);
			gc.setForeground(mBlueBot_Dark);
		}
		gc.fillGradientRectangle(0, mBorderSize+BUTTON_TOP_HEIGHT+BUTTON_MID_HEIGHT, bounds.width-mBorderSize, BUTTON_BOT_HEIGHT+1, true);
	}
		*/
	protected void toolItemAdded(AbstractRibbonGroupItem ti) {
		if (!mToolItems.contains(ti)) {
			mToolItems.add(ti);
			updateBounds();
			redraw();
			layout();
		}
	}
	
	protected void toolItemDisposed(AbstractRibbonGroupItem ti) {
		if (mToolItems.contains(ti)) {
			mToolItems.remove(ti);
			updateBounds();
			redraw();
			layout();
		}
	}
	
	protected void updateToolbarBounds() {
		
		for (RibbonToolbar tb : mToolbars) {
			List<RibbonToolbarGrouping> groupings = tb.getGroupings();
			int rows = tb.getRows();
			
			int topSpacer = 10;
			if (rows == 1)
				topSpacer = 22;
			
			int rowSpacerVertical = 7;
			
			int row1x = 4;
			int row2x = 4;
			int row3x = 4;
			int row1y = topSpacer;
			int row2y = topSpacer + TOOLBAR_BUTTON_HEIGHT + rowSpacerVertical;
			int row3y = 0; 
			
			if (rows == 3) {
				row1y = 2;
				row2y = row1y + TOOLBAR_BUTTON_HEIGHT;
				row3y = row1y + TOOLBAR_BUTTON_HEIGHT + TOOLBAR_BUTTON_HEIGHT;
			}
			
			int buttonHeight = TOOLBAR_BUTTON_HEIGHT;
			int buttonWidth = TOOLBAR_BUTTON_WIDTH;

			int hSpacer = 0;
			int groupHSpacer = 3;

			if (tb.getStyle() == RibbonToolbar.STYLE_BORDERED) {
				buttonHeight = TOOLBAR_BUTTON_HEIGHT_BORDERED;
				buttonWidth = TOOLBAR_BUTTON_WIDTH_BORDERED;
				hSpacer = 2;
			}
			
			for (int z = 0; z < groupings.size(); z++) {
				RibbonToolbarGrouping group = groupings.get(z);
				
				int row = group.getRow();

				int x = (row == 1 ? row1x : row == 2 ? row2x : row3x);
				int y = (row == 1 ? row1y : row == 2 ? row2y : row3y);
				int firstX = x; 
				// skip the border
				x += 2;
				
				int groupWidth = 2;

				List<RibbonButton> buttons = group.getItems();
				for (int i = 0; i < buttons.size(); i++) {
					RibbonButton button = buttons.get(i);
					int bStyle = button.getStyle();
					
					if (i == 0)
						button.setToolbarSide(RibbonButton.TOOLBAR_SIDE_LEFT);
					else if (i == buttons.size() - 1)
						button.setToolbarSide(RibbonButton.TOOLBAR_SIDE_RIGHT);
					else
						button.setToolbarSide(RibbonButton.TOOLBAR_SIDE_NOT_LEFT_OR_RIGHT);
					
					// left and right
					if (buttons.size() == 1)
						button.setToolbarSide(RibbonButton.TOOLBAR_SIDE_LEFT_AND_RIGHT);
					
					// if arrows are showing we need some extra space
	    			int arrowWidth = 0;

	    			if (((bStyle & RibbonButton.STYLE_ARROW_DOWN) != 0) || (bStyle & RibbonButton.STYLE_ARROW_DOWN_SPLIT) != 0) {
	    				if (((bStyle & RibbonButton.STYLE_ARROW_DOWN) != 0)) {
		    				arrowWidth += 2; // left spacer
		    				arrowWidth += 5; // arrow width
		    				arrowWidth += 2; // right spacer
	    				}
	    				else {
	        				arrowWidth += 4; // left spacer
	        				arrowWidth += 5; // arrow width
	        				arrowWidth += 2; // right spacer
	    				}
	    			}
	    			
	    			// buttons are inside the group bounds, so we push them down by 1px to not share bounds with the border
	    			int finY = y;
	    			if (tb.getStyle() == RibbonToolbar.STYLE_BORDERED)
	    				finY += 1;
	    			
					button.setBounds(new Rectangle(x, finY, buttonWidth + arrowWidth, buttonHeight));
					
					int addon = buttonWidth + arrowWidth + (i != buttons.size()-1 ? hSpacer : 0);
					x += addon;
					groupWidth += addon;
				}
				
				// remove that border we added above as it is part of us 
				x -= 2;
				groupWidth += 2;
				
				group.setBounds(new Rectangle(firstX, y, groupWidth, buttonHeight));

				int spacer = 0;
				if (z != groupings.size() - 1)
					spacer = groupHSpacer;

				switch (row) {
					case 1:
						row1x += groupWidth + spacer;
						break; 
					case 2:
						row2x += groupWidth + spacer;
						break;
					case 3:
						row3x += groupWidth + spacer;
						break;
				}
				
			} // end group loop
			
			
		}
	}
	
	protected void updateBounds() {
		int cur = 4;
		int sideSpacerMin = 6;

		GC gc = new GC(this);
		// set bounds of items
		for (AbstractRibbonGroupItem item : mToolItems) {
					
			if (item instanceof RibbonGroupSeparator) {
				// spacer + 2 + spacer
				item.setBounds(new Rectangle(cur+3, 7, 3+2+3, 55));
				
				cur += 3+2+3;
			}
			else if (item instanceof RibbonButton) {
    			// calculate size
    			String name = item.getName();
    			if (name == null)
    				name = "";
    			
    			int width = 0;
    			// min width if we have an image is 6px spacer on each side + imge width
    			int imgWidth = (sideSpacerMin*2) + (item.getImage() == null ? 0 : item.getImage().getBounds().width);
    			
    			int arrowWidth = 0;

    			if (((item.getStyle() & RibbonButton.STYLE_ARROW_DOWN) != 0) || (item.getStyle() & RibbonButton.STYLE_ARROW_DOWN_SPLIT) != 0) {
    				arrowWidth += 4; // left spacer
    				arrowWidth += 5; // arrow width
    				arrowWidth += 8; // right spacer
    			}
    			
    			if ((item.getStyle() & RibbonButton.STYLE_TWO_LINE_TEXT) != 0) {
					StringTokenizer st = new StringTokenizer(name, "\n");
					int maxStrWidth = 0;
					int count = 0;
					while (st.hasMoreTokens()) {
						String token = st.nextToken();
						int extentX = gc.stringExtent(token).x;
						// account for arrow space if it's an arrow button, which would be on the second row on a split-text button
						if (count == 1)
							extentX += arrowWidth;
						maxStrWidth = Math.max(extentX, maxStrWidth);
						
						count++;
					}
					maxStrWidth += sideSpacerMin;
						    		
	    			width += maxStrWidth;

	    			// what's bigger, our text or our image? use the biggest
	    			width = Math.max(imgWidth, width);
					
	    			item.setBounds(new Rectangle(cur, 2, width + sideSpacerMin, BUTTON_HEIGHT)); // TODO: Extract 4 (different than above)
	    			cur += width + sideSpacerMin;
				}
				else {    			
	    			Point p = gc.stringExtent(name);
	    			
	    			width += p.x;

	    			// what's bigger, our text or our image? use the biggest
	    			width = Math.max(imgWidth, width);
	    			
	    			item.setBounds(new Rectangle(cur, 2, width + sideSpacerMin, BUTTON_HEIGHT)); // TODO: Extract 4 (different than above)
	    			
	    			cur += width + sideSpacerMin;
				}
    			
    			// space them accordingly
    			cur += BUTTON_SPACING;

			}
			else if (item instanceof RibbonButtonGroup) {
				RibbonButtonGroup rbg = (RibbonButtonGroup) item;
				List<AbstractRibbonGroupItem> buttons = rbg.getButtons();
				
				int maxWidth = 0;
				int maxImgWidth = 0;
				int y = 2;
				int x = cur;
				
				int MAX_IN_COLUMN = 3;
				int verticalCount = 0;
								
				boolean hasCheckboxes = false;
				boolean hasButtons = false;
				
				// we calculate the image width based on the widest image or text will not be aligned with image,
				// ideally users stick to same-size images, but you never know, and this takes away weird drawing behavior if they are not the same size
				for (int i = 0; i < buttons.size(); i++) {
					AbstractRibbonGroupItem rb = buttons.get(i);
					
					if (rb.getImage() != null)
						maxImgWidth = Math.max(maxImgWidth, rb.getImage().getBounds().width);
					
					if (rb instanceof RibbonButton)
						hasButtons = true;
					else if (rb instanceof RibbonCheckbox)
						hasCheckboxes = true;
				}
				
				boolean mixOfBoth = hasButtons && hasCheckboxes;
				
				// loop again
				for (int i = 0; i < buttons.size(); i++) {
					AbstractRibbonGroupItem rb = buttons.get(i);

					verticalCount++;					
					int width = 0;
					
					if (rb instanceof RibbonCheckbox) {
						y += 4; // checkboxes are 4px down always, as they are different sizes than for example buttons
						
						width = 13;
						width += 6; // spacer between box and text
						
		    			String name = rb.getName();
		    			if (name == null)
		    				name = "";

						width += gc.stringExtent(name).x;
						width += sideSpacerMin;
						// if checkbuttons are mixed with buttons, checkboxes need a 1px further-to-the-right alignment
						// MS never seems to do this in Office, but I guess it could be possible
						int extra = mixOfBoth ? 1 : 0;
						rb.setBounds(new Rectangle(x+3+extra, y, width-1, CHECKBOX_HEIGHT)); 											
						
						y += CHECKBOX_HEIGHT + 5;
					
						maxWidth = Math.max(width, maxWidth);
					}
					else {						
		    			width = maxImgWidth;	    			
		    			width += 2; // img spacer, same regardless of button
	
		    			// calculate size
		    			String name = rb.getName();
		    			if (name == null)
		    				name = "";
		    												
		    			Point p = gc.stringExtent(name);	    			
				
		    			width += p.x; // text width
		    			
		    			// check if this item has an arrow attached to it
		    			if ( ((rb.getStyle() & RibbonButton.STYLE_ARROW_DOWN) != 0) || ((rb.getStyle() & RibbonButton.STYLE_ARROW_DOWN_SPLIT) != 0)) {
		    				width += 4; // spacer
		    				width += 5; // actual arrow
		    				width += 8; // right spacer
		    				
		    				// we'll need another bit of spacing if it's split and arrow
		    				if ((rb.getStyle() & RibbonButton.STYLE_ARROW_DOWN_SPLIT) != 0) {
		    					width += 2; 
		    				}
		    			}
		    			else {
			    			width += 8; // right spacer, border width is ~2px
		    			}		    					    		
						
						rb.setBounds(new Rectangle(x, y, width, 22)); // TODO: Extract 4 (different than above), and 22
						
		    			y += 22;
		    			maxWidth = Math.max(width, maxWidth);
					}
					
					if (verticalCount % MAX_IN_COLUMN == 0) {
						y = 2;
						x += maxWidth+1;
						verticalCount = 0;
					}
						
				}
				
    			rbg.setBounds(new Rectangle(cur, 2, x+maxWidth, BUTTON_HEIGHT)); // TODO: Extract 4 (different than above)
    			cur += maxWidth;    			
			}
						
		}		
		gc.dispose();

	}
	
	protected void toolItemRemoved(AbstractRibbonGroupItem ti) {
		mToolItems.remove(ti);
		redraw();
	}
	
	private boolean isInside(int x, int y, Rectangle rect) {
		if (rect == null) {
			return false;
		}

		return x >= rect.x && y >= rect.y && x <= (rect.x + rect.width-1) && y <= (rect.y + rect.height-1);
	}

	@Override
	public Rectangle getBounds() {		
		int toolWidths = 0;		
		int normalControlMaxWidth = 0;
		int normalControlBorderSpacer = mBorderSize * 2;
		
		// deal with native widgets inside our groups
		Control [] subItems = getChildren();
		for (int i = 0; i < subItems.length; i++) {
			Control item = subItems[i];
			Point size = item.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			if (normalControlMaxWidth == 0) 
				normalControlMaxWidth = normalControlBorderSpacer;
			
			normalControlMaxWidth = Math.max(normalControlMaxWidth, size.x + normalControlBorderSpacer);
		}
						
		// TODO: Bigger todo, we need to be smart about max 3 items height-wise for small items, we need layouts...
		for (int i = 0; i < mToolItems.size(); i++) {
			AbstractRibbonGroupItem item = mToolItems.get(i);
			if (item.getBounds() == null)
				continue; 
			
			if (item instanceof RibbonButton)
				toolWidths += item.getBounds().width;
			else if (item instanceof RibbonCheckbox)
				toolWidths = Math.max(toolWidths, item.getBounds().width);
			else if (item instanceof RibbonButtonGroup) {
				//toolWidths += item.getBounds().width;
				RibbonButtonGroup rbg = (RibbonButtonGroup) item;
				List<AbstractRibbonGroupItem> items = rbg.getButtons();
				
				int maxWidth = 0;
				
				// width = button.x + button.width - group.x
				for (AbstractRibbonGroupItem button : items) {
					maxWidth = Math.max(button.getBounds().x+button.getBounds().width-rbg.getBounds().x, maxWidth);
				}
				
				toolWidths += maxWidth;//button.getBounds().width;					
			}
			else 
				toolWidths += item.getBounds().width;
			
			if (i != mToolItems.size()-1)
				toolWidths += BUTTON_SPACING;
		}

	
		
		int widestToolbar = 0;
		for (int i = 0; i < mToolbars.size(); i++) {
			RibbonToolbar tb = mToolbars.get(i);
			boolean bordered = (tb.getStyle() == RibbonToolbar.STYLE_BORDERED);

			int minX = 0;
			int maxX = 0;
						
			List<RibbonToolbarGrouping> groups = tb.getGroupings();
			for (RibbonToolbarGrouping group : groups) {				
				minX = Math.min(minX, group.getBounds().x);
				maxX = Math.max(maxX, group.getBounds().x + group.getBounds().width - (bordered ? 4 : 0));
			}
			
			widestToolbar = Math.max(widestToolbar, maxX-minX);
		}
		
		if (widestToolbar > toolWidths)
			toolWidths = widestToolbar;
				
		if (toolWidths < normalControlMaxWidth)
			toolWidths = normalControlMaxWidth;	
		
		// check how long the group name is, we don't truncate it
		if (mGroupName != null) {
			GC gc = new GC(this);
			int w = gc.stringExtent(mGroupName).x;
			if (mShowToolTipMarker)
				w += 15 + 4; // office uses a 4px spacer for tooltip space between marker and text
			
			gc.dispose();
			
			if (w > toolWidths)
				toolWidths = w;
		}
				
		int extra = 1; // TODO: figure out what px is missing, until then, space by 1

		//if (mGroupName.equals("Checkboxes"))
			//System.err.println(mGroupName + " " + toolWidths + " " + (mBorderSize*2) + " 4 " + extra);

		return new Rectangle(0, 0, toolWidths+(mBorderSize*2)+4+extra, 85);
	}

	@Override
	public boolean isVisible() {
		return mVisible;
	}
	
	public void setVisible(boolean visible) {
		mVisible = visible;
	}
	
	public void addSelectionListener(SelectionListener listener) {
		if (!mSelectionListeners.contains(listener))
			mSelectionListeners.add(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		mSelectionListeners.remove(listener);
	}
	
	protected void fireSelection(MouseEvent me) {
		Event e = new Event();
		e.x = me.x;
		e.y = me.y;
		e.data = me.data;
		e.widget = me.widget;
		e.stateMask = me.stateMask;
		e.button = me.button;
		
		SelectionEvent se = new SelectionEvent(e);
		for (SelectionListener listener : mSelectionListeners)
			listener.widgetSelected(se);
	}
	
	@Override
	public void dispose() {
		for (int i = 0; i < mToolItems.size(); i++) 
			mToolItems.get(i).dispose();
		
		mToolItems.clear();
		
		super.dispose();
	}
	
	void removeButton(AbstractRibbonGroupItem item) {
		mToolItems.remove(item);
	}
			
	/*
	class GroupLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int hint, int hint2, boolean flushCache) {
			return null;
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			
			int innerTopSpacing = 8;
			int innerLeftSpacing = 3;
			
			Control [] subItems = composite.getChildren();
			for (int i = 0; i < subItems.length; i++) {
				Control item = subItems[i];
				item.setLocation(innerLeftSpacing, innerTopSpacing);
				Point size = item.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				item.setBounds(rect)
			}

		}
		
		
	}*/
	
	
}
