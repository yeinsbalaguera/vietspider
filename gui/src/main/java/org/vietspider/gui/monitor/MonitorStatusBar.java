/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.monitor;

import org.eclipse.swt.widgets.Composite;
import org.vietspider.gui.browser.StatusBar;
import org.vietspider.gui.workspace.Workspace;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 2, 2009  
 */
public class MonitorStatusBar extends StatusBar {
  
  private int pluginType;
  
  public MonitorStatusBar(Workspace workspace, Composite parent, int pluginType) {
    super(workspace, parent, false);
    
    this.pluginType = pluginType;
  }

  @Override()
  public void createPluginItems() {
    disposeTools();
    createPluginItems(pluginType);
    liveSashForm.setMaximizedControl(tool);
    liveSashForm.setVisible(true);
    tool.layout();
//    if(items == null) return;
//    for(int i = 0; i < items.length; i++) {
//      items[i].addDisposeListener(new DisposeListener() {
//        @Override
//        public void widgetDisposed(DisposeEvent arg0) {
//          new Exception().printStackTrace();
//        }
//      });
//    }
  }

}
