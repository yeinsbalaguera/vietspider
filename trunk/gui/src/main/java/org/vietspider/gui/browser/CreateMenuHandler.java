/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.browser;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.SourceEditor;
import org.vietspider.gui.creator.URLTemplate;
import org.vietspider.gui.creator.URLWidget;
import org.vietspider.link.V_URL;
import org.vietspider.ui.services.ClientLog;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Aug 3, 2009  
 */
public class CreateMenuHandler {
  
  protected TabBrowser tabBrowser;
  
  public CreateMenuHandler(TabBrowser tab) {
    this.tabBrowser = tab;
  }
  
  public void putHomepage(String [] links) {
    if(links.length < 1) return;
    try {
      SourceEditor editor = searchEditor1(new V_URL(links[0]), false);
      URLWidget urlWidget = editor.<URLWidget>getField("txtHome");
      urlWidget.addItems(links);
    } catch (Exception e) {
      ClientLog.getInstance().setException(tabBrowser.getShell(), e);
    }
  }
  
  public void putSamplePage(String [] links) {
    if(links.length < 1) return;
    try {
      SourceEditor editor = searchEditor1(new  V_URL(links[0]), true);
      Text txtPattern = editor.<Text>getField("txtPattern");
      txtPattern.setText(links[0]);
      editor.selectExtractDataBlock(tabBrowser);
    } catch (Exception e) {
      ClientLog.getInstance().setException(tabBrowser.getShell(), e);
    }
  }
  
  public void testSamplePage(String [] links) {
    if(links.length < 1) return;
    try {
      SourceEditor editor = searchEditor1(new  V_URL(links[0]), true);
      Text txtPattern = editor.<Text>getField("txtPattern");
      txtPattern.setText(links[0]);
      editor.test();
    } catch (Exception e) {
      ClientLog.getInstance().setException(tabBrowser.getShell(), e);
    }
  }
  
  public void putTemplatePage(String [] links, String name) {
    if(links.length < 1) return;
    try {
      String template = TemplateDetector.toTemplate(links);
      if(template != null) {
        SourceEditor editor = searchEditor1(new  V_URL(links[0]), false);
        URLTemplate uiTemplate = editor.<URLTemplate>getField(name);
        uiTemplate.addItems(new String[]{template});
      } else {
        SourceEditor editor = searchEditor1(new  V_URL(links[0]), true);
        URLTemplate uiTemplate = editor.<URLTemplate>getField(name);
        uiTemplate.putText(links[0]);
      }
    
    } catch (Exception e) {
      ClientLog.getInstance().setException(tabBrowser.getShell(), e);
    }
  }
  
  public SourceEditor searchEditor1(V_URL url, boolean selected) throws Exception  {
    SourceEditor editor = searchEditor(url, selected);
    if(editor != null) return editor;
    editor = searchEditor(null, selected);
    if(editor != null) return editor;
    List<Creator> creators = tabBrowser.<Creator>getItems(Creator.class);
    return creators.get(0).getInfoControl().getSourceEditor();
  }


  public SourceEditor searchEditor(V_URL url, boolean selected) throws Exception  {
    List<Creator> creators = tabBrowser.<Creator>getItems(Creator.class);
    if(creators.size() < 1) {
      Creator creator = (Creator)tabBrowser.createTool(Creator.class, false, SWT.CLOSE);
      if(creator != null) creator.selectData(new Worker[0], null, null);
      return creator.getInfoControl().getSourceEditor();
    }

    for(int i = 0; i < creators.size(); i++) {
      SourceEditor _editor = creators.get(i).getInfoControl().getSourceEditor();
      URLWidget urlWidget = _editor.<URLWidget>getField("txtHome");
      String [] homes = urlWidget.getItems();
      if(url == null) {
        if(homes == null || homes.length < 1) {
          if(selected) tabBrowser.setSelectedItem(creators.get(i));
          return _editor;
        }
      } else {
        if(homes.length > 0) {
          V_URL url1 = new  V_URL(homes[0]);
          if(url.getHost().equalsIgnoreCase(url1.getHost())) {
            if(selected) tabBrowser.setSelectedItem(creators.get(i));
            return _editor;  
          }
        }
      }
    }
    return null;
  }
}
