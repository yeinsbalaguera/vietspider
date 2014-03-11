/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.server.handler.cms.metas;

import java.io.OutputStream;

import org.vietspider.db.database.MetaList;
import org.vietspider.webui.cms.CMSService;
import org.vietspider.webui.cms.render.MetaListRenderer;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 26, 2007
 */
public abstract class MetaListHandler extends CMSHandler <MetaList> {
  
  protected String referer = null;
  
  public MetaListHandler(String type) {
    super(type);
  }
  
  public String render(OutputStream output, MetaList list, String cookies[], String...params) throws Exception {
    CMSService cms = CMSService.INSTANCE;;
    MetaListRenderer render = cms.createRender(MetaListRenderer.class);
    render.write(output, type, list, referer, cookies, params);
    return "text/html";
  }

}
