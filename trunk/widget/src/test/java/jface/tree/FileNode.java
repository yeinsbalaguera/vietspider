/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package jface.tree;

import java.io.File;
import java.util.List;

import org.eclipse.swt.graphics.Image;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 12, 2009  
 */
public class FileNode extends TreeNode
{
  private File fFile; /* actual data object */
  
  public FileNode(ITreeNode parent, File file)
  {
    super(parent);
    fFile = file;
  }
  
  public String getName() {   
    return "FILE: " + fFile.getName();
  }
 
  public Image getImage() {
    return null; /* TODO: Return File image */
  }
  
  protected void createChildren(List children) {    
  }
  
  public boolean hasChildren() {    
    return false;
  }
}
