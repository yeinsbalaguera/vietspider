/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.log;

import java.io.File;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.vietspider.ui.widget.ImageHyperlink;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 29, 2011  
 */
public interface ITextViewer {

  public Shell getShell();
  
  public void setFile(File file);
  
  public String getFolder();
  
  public String getFileName();
  
  public ImageHyperlink getButGo();
  
  public List getListDate();
  
  public void setPages(File file, String labelPage);
  
  public Combo getCboFilter();
  
  public Spinner getSpinPage();
  
  public boolean isHTML();
}
