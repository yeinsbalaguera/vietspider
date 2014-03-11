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
import org.vietspider.generic.ColorCache;


public class GraphicsHelper {

	public static Color lighter(Color color, int add) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		
		red += add;
		green += add;
		blue += add;
		
		if (red > 255)
			red = 255;
		if (green > 255)
			green = 255;
		if (blue > 255)
			blue = 255;
		
		return ColorCache.getInstance().getColor(red, green, blue);
	}
}
