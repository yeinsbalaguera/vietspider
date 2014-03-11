/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.client.common.source;

import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.ISourceHandler;
import org.vietspider.model.Groups;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 6, 2009  
 */
public class LocalSourceClientHandler implements ISourceHandler {
  
  private SourcesClientHandler handler;

  public void abort() {
    if(handler != null) handler.abort();
    ClientConnector2.currentInstance().abort();
  }

  @Override
  public void deleteCategories(String group, String[] categories) throws Exception {
    handler = new SourcesClientHandler(group);
    handler.deleteCategories(categories);
  }

  @Override
  public void deleteSources(String group, String category, String[] sources) throws Exception {
    handler = new SourcesClientHandler(group);
    handler.deleteSources(category, sources);
  }

  @Override
  public String[] loadCategories(String group) throws Exception {
    handler = new SourcesClientHandler(group);
    return handler.loadCategories();
  }

  @Override
  public Groups loadGroups() throws Exception {
    return new SimpleSourceClientHandler().loadGroups();
  }

  @Override
  public String[] loadSources(String group, String category) throws Exception {
    handler = new SourcesClientHandler(group);
    return handler.loadSources(category);
  }

}
