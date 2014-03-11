/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.article.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.handler.StandardRequestHandler;
import org.apache.solr.handler.component.DebugComponent;
import org.apache.solr.handler.component.FacetComponent;
import org.apache.solr.handler.component.HighlightComponent;
import org.apache.solr.handler.component.MoreLikeThisComponent;
import org.apache.solr.handler.component.StatsComponent;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 5, 2010  
 */
public class ArticleRequestHandler extends StandardRequestHandler {

  @Override
  protected List<String> getDefaultComponents() {
    ArrayList<String> names = new ArrayList<String>(6);
    names.add(ArticleQueryComponent.VS_COMPONENT_NAME );
    names.add(FacetComponent.COMPONENT_NAME );
    names.add(MoreLikeThisComponent.COMPONENT_NAME );
    names.add(HighlightComponent.COMPONENT_NAME );
    names.add(StatsComponent.COMPONENT_NAME );
    names.add(DebugComponent.COMPONENT_NAME );
    return names;
  }

}
