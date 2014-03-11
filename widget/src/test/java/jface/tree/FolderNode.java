/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package jface.tree;

import java.io.File;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.vietspider.ui.services.ImageLoader;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 12, 2009  
 */
public class FolderNode extends TreeNode
{
  private File fFolder; /* actual data object */
  
  public FolderNode(File folder) {
    this(null, folder);
  }
  
  public FolderNode(ITreeNode parent, File folder) {
    super(parent);
    fFolder = folder;
  }
 
  public String getName() {   
    return "FOLDER: " + fFolder.getName();
  }
 
  public Image getImage() {
    ImageLoader loader = new ImageLoader();
    return loader.load(Display.getCurrent(), "butConnect.png"); /* TODO: Return Folder image */
  }
  
  protected void createChildren(List children) {     
    File[] childFiles = fFolder.listFiles();
    for(int i=0; i<childFiles.length; i++)
    {
      File childFile = childFiles[i];
      if( childFile.isDirectory() )
        children.add(new FolderNode(this, childFile));
      else
        children.add(new FileNode(this, childFile));
    }
  } 
}
 

