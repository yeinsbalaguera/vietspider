/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package jface.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 12, 2009  
 */
public abstract class TreeNode implements ITreeNode
{
  protected ITreeNode fParent;
  protected List fChildren;
    
  public TreeNode(ITreeNode parent) {
    fParent = parent;
  }
  
  public Image getImage() {
    return null; /* TODO */
  }
  
  public boolean hasChildren() {    
    return true;
  }
 
  public ITreeNode getParent() {    
    return fParent;
  }
  
  public List getChildren() 
  {
    if( fChildren != null )
      return fChildren;
    
    fChildren = new ArrayList();
    createChildren(fChildren);
      
    return fChildren;
  }
  
  /* subclasses should override this method and add the child nodes */
  protected abstract void createChildren(List children);
}
