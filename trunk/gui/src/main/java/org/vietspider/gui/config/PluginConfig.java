/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.config;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.vietspider.client.ClientPlugin;
import org.vietspider.client.PluginLoader;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 15, 2008  
 */
public class PluginConfig extends Composite {
  
  private Hyperlink [] items;
  private ClientPlugin [] plugins;

  public PluginConfig(Composite parent, ApplicationFactory factory) {
    super(parent, SWT.NONE);

    RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
    setLayout(rowLayout);
    
    factory.setComposite(this);
    
    PluginLoader pluginLoader = new PluginLoader();
    plugins = pluginLoader.createPlugins();
    String error = pluginLoader.getError();
    if(error.length() > 0) {
      ClientLog.getInstance().setException(getShell(), new Exception(error));
    }

    items = new Hyperlink[plugins.length];
    for(int i = 0; i < items.length; i++) {
      if(!plugins[i].isSetup()) continue;
      items[i] = new Hyperlink(this, SWT.LEFT);
      items[i].setText(plugins[i].getLabel());
//      link.setUnderlined(true);
      items[i].setFont(UIDATA.FONT_8V);
      items[i].setBackground(this.getBackground());
      items[i].addHyperlinkListener(new HyperlinkAdapter(){
        public void linkEntered(HyperlinkEvent e) {
          Hyperlink hyperlink = (Hyperlink)e.widget;
          hyperlink.setUnderlined(true);
        }

        public void linkExited(HyperlinkEvent e) {
          Hyperlink hyperlink = (Hyperlink)e.widget;
          hyperlink.setUnderlined(false);
        }
        
        public void linkActivated(HyperlinkEvent e) {
          invokeSetup((Hyperlink)e.widget);
        }
      });
      plugins[i].syncEnable(items[i]);
    }
  }
  
  private void invokeSetup(Hyperlink hyperLink) {
    for(int i = 0; i < items.length; i++) {
      if(items[i] != hyperLink) continue;
      plugins[i].invokeSetup(items[i]);
      break;
    }
  }
  
  public void saveSetup() {
    for(int i = 0; i < items.length; i++) {
      if(items[i] == null) continue;
      plugins[i].saveSetup(items[i]);
    }
  }
  
  public void resetSetup() {
    for(int i = 0; i < items.length; i++) {
      if(items[i] == null) continue;
      plugins[i].resetSetup();
    }
  }
}
