/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import java.io.File;
import java.util.prefs.Preferences;

import org.eclipse.swt.widgets.Combo;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.OrganizationClientHandler;
import org.vietspider.client.common.source.SimpleSourceClientHandler;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.io.RWData;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.source.SourcesExplorer;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.WaitLoading;
import org.vietspider.user.AccessChecker;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 23, 2009  
 */
public class LoadGroupCategorySource {

  private GroupCategorySource instance;

  private String group;
  private String category;
  private String [] sources;
  private AccessChecker accessChecker;

  public LoadGroupCategorySource(GroupCategorySource _instance, 
      Worker[]plugins, String group, String category, String...sources) {
    this.instance = _instance;

    this.group = group;
    this.category = category;
    this.sources = sources;

    LoadSourceWorker sourceWorker = new LoadSourceWorker(plugins);
    LoadCategoryWorker cateWorker = new LoadCategoryWorker(sourceWorker);

    LoadGroupWorker groupWorker = null;
    if(instance.getCreator().getUIGroupType().getItemCount() < 1) {
      groupWorker = new LoadGroupWorker(cateWorker);
    } else {
      instance.getCreator().selectGroup(group);
    }

    if(groupWorker != null) {
      new WaitLoading(instance.getCreator(), groupWorker).open();
    } else if(cateWorker != null) {
      new WaitLoading(instance.getCreator(), cateWorker).open();
    } else if(sourceWorker != null) {
      new WaitLoading(instance.getCreator(), sourceWorker).open();
    }
  }

  private class LoadSourceWorker extends Worker {

    private String error = null;
    private String [] listSources = null;
    private String [] listDisableSources = null;
    private String selectedGroup;
    private String selectedCategory;
    
    private LoadSourceWorker(Worker...plugins) {
      super(plugins);
    }

    public void abort() {
      ClientConnector2.currentInstance().abort();
    }

    public void before() {
      selectedGroup = instance.getCreator().getSelectedGroupName();
      selectedCategory = instance.getSelectedCategory();
    }

    public void execute() {
      if(selectedGroup == null || selectedCategory == null) return;
      try {
        SourcesClientHandler handler = new SourcesClientHandler(selectedGroup);
        listSources = handler.loadSources(selectedCategory);
        listDisableSources = handler.loadDisableSources(selectedCategory);
      } catch(Exception exp) {
        error = exp.toString();
      } 
    }


    public void after() {
      if(selectedGroup == null || selectedCategory == null) return;
      if(error != null && !error.isEmpty()) {
        ClientLog.getInstance().setMessage(instance.getCreator().getShell(), new Exception(error));
        return;
      }

      instance.setSources(listSources);
      instance.setDisableSources(listDisableSources);
      instance.setSelectedSources(getPlugins(), sources);
      this.plugins = null;
    }
  }


  private class LoadCategoryWorker extends Worker {

    private String error = null;
    private String [] categories = null;
    private String selectedGroup;
    private File file;

    private LoadCategoryWorker(Worker...plugins) {
      super(plugins);
    }

    public void abort() {
      ClientConnector2.currentInstance().abort();
    }

    public void before() {
      selectedGroup = instance.getCreator().getSelectedGroupName();
      file = new File(ClientConnector2.getCacheFolder("sources/type"), "group." + selectedGroup);
    }

    public void execute() {
      try {
        if(file.exists()) {
          byte [] bytes = RWData.getInstance().load(file);
          categories = new String(bytes, Application.CHARSET).trim().split("\n");
        } else {
          categories = new SourcesClientHandler(selectedGroup).loadCategories();
          if(accessChecker == null) {
            accessChecker = new OrganizationClientHandler().loadAccessChecker();
          }
        }
      } catch(Exception exp) {
        error = exp.toString();
      } 
    }


    public void after() {
      if(error != null && !error.isEmpty()) {
        ClientLog.getInstance().setMessage(instance.getCreator().getShell(), new Exception(error));
        return;
      }

      instance.setCategories(accessChecker, categories);
      instance.setSelectedCategory(category);
    }
  }

  private class LoadGroupWorker extends Worker {

    private String error = null;
    private String [] groups = null;
    private File file; 
    
    private Combo cboGroupType;

    private LoadGroupWorker(Worker...plugins) {
      super(plugins);
    }

    public void abort() {
      ClientConnector2.currentInstance().abort();
    }

    public void before() {
      file = new File(ClientConnector2.getCacheFolder("sources/type"), "groups");
      cboGroupType = instance.getCreator().getUIGroupType();
    }

    public void execute() {
      try {
        if(file.exists()) {
          byte [] bytes = RWData.getInstance().load(file);
          groups = new String(bytes, Application.CHARSET).trim().split("\n");
        } else {
          groups = new SimpleSourceClientHandler().loadGroups().getNames();
          accessChecker = new OrganizationClientHandler().loadAccessChecker();
        }
      } catch(Exception exp) {
        error = exp.toString();
      } 
    }


    public void after() {
      if(error != null && !error.isEmpty()) {
        ClientLog.getInstance().setMessage(instance.getCreator().getShell(), new Exception(error));
        return;
      }
      
      if(cboGroupType.isDisposed()) return;
      setGroups(accessChecker, file);
      
      instance.getCreator().selectGroup(group);
    }

    private void setGroups(AccessChecker accessChecker, File file) {
      //    System.err.println(" ====  >"+ selectedGroup + " / "+ selectedCategory + " / "+ selectedSource);
      //    new Exception().printStackTrace();
      if(groups == null || groups.length < 1 || cboGroupType.isDisposed()) return;
      cboGroupType.removeAll();
      if(accessChecker != null) {
        StringBuilder builder = new StringBuilder();
        for(String _group : groups) {
          if(!accessChecker.isPermitGroup(_group)) continue;
          cboGroupType.add(_group);
          if(builder.length() > 0) builder.append('\n');
          builder.append(_group);
        }
        try {
          byte [] bytes = builder.toString().getBytes(Application.CHARSET);
          RWData.getInstance().save(file, bytes);
        } catch (Exception e) {
          file.delete();
        }
      } else {
        for(String _group : groups) {
          cboGroupType.add(_group);
        } 
      }

      int selectedIndex = 0;
      try {
        Preferences prefs = Preferences.userNodeForPackage(SourcesExplorer.class);
        selectedIndex = Integer.parseInt(prefs.get("selectedGroup", ""));
      } catch (Exception e) {
        selectedIndex = 0;
      }

      if(selectedIndex < 0) selectedIndex = 0;
      if(selectedIndex >= cboGroupType.getItemCount()) {
        if(cboGroupType.getItemCount() > 0) cboGroupType.select(0);
        return;
      }
      cboGroupType.select(selectedIndex);
    }
  }


  public static interface GroupCategorySource  {

    abstract Creator getCreator();

    abstract void setCategories(AccessChecker accessChecker, String [] categories);
    abstract void setSelectedCategory(String cate);
    abstract String getSelectedCategory();

    abstract void setSources(String...sources);
    abstract void setDisableSources(String...sources);
    abstract void setSelectedSources(Worker[]plugins, String...sources);

    //    abstract void setSource(Source source);
  }

}
