/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package jface.tree;

import java.util.List;

import org.eclipse.swt.graphics.Image;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 12, 2009  
 */
public interface ITreeNode {
  public String getName();
  public Image getImage();
  public List getChildren();
  public boolean hasChildren();
  public ITreeNode getParent();
}

