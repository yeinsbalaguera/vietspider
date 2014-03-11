/***************************************************************************
 * Copyright 2003-2007 by VietSpider - All rights reserved.                *    
 **************************************************************************/
package org.vietspider.webui.cms;

import java.io.OutputStream;

import org.vietspider.common.io.RWData;
import org.vietspider.common.io.UtilFile;
import org.vietspider.server.handler.cms.FileWriter;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 28, 2007
 */
public class FileOutput implements FileWriter {
  
  protected String layout;

  public void write(OutputStream output, String name) throws Exception {
    output.write(RWData.getInstance().load(UtilFile.getFile("system/cms/"+layout, name)));
  }

}
