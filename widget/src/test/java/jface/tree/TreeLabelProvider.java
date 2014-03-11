/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package jface.tree;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 12, 2009  
 */
public class TreeLabelProvider extends LabelProvider {
  public String getText(Object element) {   
    return ((ITreeNode)element).getName();
  }
  
  public Image getImage(Object element) {   
    return ((ITreeNode)element).getImage();
  }
}
