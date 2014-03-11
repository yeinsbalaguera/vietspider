/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.       *
 **************************************************************************/
package org.vietspider.gui.creator.action.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.ISourceInfo;
import org.vietspider.gui.creator.PropertiesComposite;
import org.vietspider.gui.creator.URLTemplate;
import org.vietspider.gui.creator.URLWidget;
import org.vietspider.gui.creator.action.ClientDocumentLoader;
import org.vietspider.html.HTMLDocument;
import org.vietspider.model.Region;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.ThreadExecutor;
import org.vietspider.ui.widget.waiter.WaitLoading;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 13, 2009  
 */
public class TPphpBB extends ClientDocumentLoader implements TextPatternModify {
  
  protected HTMLDocument doc;
  
  @Override
  public void execute(ISourceInfo iSourceInfo_, final String address) {
    int index = address.indexOf("viewtopic.php?");
    detect(iSourceInfo_, address, index < 0);
    if (index < 0 || address.indexOf("t=") < 0) return;
   
    String homepage_ = address.substring(0, index);
    URLWidget txtHome = iSourceInfo_.<URLWidget>getField("txtHome");
    if(txtHome.getItems() == null || txtHome.getItems().length < 1)  txtHome.setItem(homepage_);
    
    URLTemplate templateVisitLink = iSourceInfo_.<URLTemplate>getField("templateVisitLink");
    if(templateVisitLink.getItemCount() < 1) {
      String [] templates = {
          homepage_ + "viewforum.php?f=*&sid=*",
          homepage_ + "viewforum.php?f=*",
          homepage_ + "viewforum.php?f=*&st=*&sk=*&sd=*&start=*",
          homepage_ + "viewforum.php?f=*&sid=*&st=*&sk=*&sd=*&start=*"
      };
      templateVisitLink.setItems(templates);
    }
    
    URLTemplate templateDataLink = iSourceInfo_.<URLTemplate>getField("templateDataLink");
    if(templateDataLink.getItemCount() < 1) {
      String [] templates = {
          homepage_ + "viewtopic.php?f=*&t=*",
          homepage_ + "viewtopic.php?f=*&t=*&sid=*"
      };
      templateDataLink.setItems(templates);
    }
    
    Properties properties = iSourceInfo_.<Properties>getField("properties");
    properties.setProperty("SessionParameter", "sid=");
    PropertiesComposite propertiesComposite = iSourceInfo_.<PropertiesComposite>getField("propertiesComposite");
    propertiesComposite.setProperties(properties);
    
  }
  
  private void detect(ISourceInfo iSourceInfo_, final String address, boolean background) {
    if(iSourceInfo_.getExtractRegions().length > 0) return;
    
    setISourceInfo(iSourceInfo_);
    
    Creator creator = iSourceInfo_.getField("creator");
    
    final String group = creator.getSelectedGroupName();
    if(!"FORUM".equalsIgnoreCase(group)) return;
    
    Worker excutor = new Worker() {

      public void abort() {}

      public void before() {
      }

      public void execute() {
        try {
          doc = getDocument(address, true);
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {
        if(doc == null) return;
        
        if(doc.getTextValue().toLowerCase().indexOf("phpbb") < 0) return;
        
        if(iSourceInfo.getExtractRegions().length > 0) return;
        PHPBBExtractor extractor = new PHPBBExtractor();
        extractor.extract(doc.getRoot());
        
        List<String> extractPaths = extractor.getExtractPaths();
        iSourceInfo.setExtractRegions(extractPaths.toArray(new String[extractPaths.size()]));
        
        List<Region>  regions = new ArrayList<Region>();
        String path = extractor.getTitlePath();
        if(path != null) {
          regions.add(new Region("thread:title", new String[]{path}));
        } else {
          regions.add(new Region("thread:title", new String[]{}));
        }
        
        path = extractor.getUserPath();
        if(path != null) {
          regions.add( new Region("post:user", new String[]{path}));
        } else {
          regions.add(new Region("post:user", new String[]{}));
        }

        path = extractor.getPostPath();
        if(path != null) {
          regions.add(new Region("post:content", new String[]{path}));
        } else {
          regions.add( new Region("post:content", new String[]{}));
        }

        path = extractor.getPagePath();
        if(path != null) {
          regions.add(new Region("thread:page", new String[]{path}));
        } else {
          regions.add(new Region("thread:page", new String[]{}));
        }
          
        iSourceInfo.setDataRegions(regions);
//        cboDataRegion.setItems(regions.get(0).getPaths());
//        if(cboNameRegion.getItemCount() > 0) cboNameRegion.select(0);
//        if(cboDataRegion.getItemCount() > 0) cboDataRegion.select(0);
      }
    };
    final Text txtName = iSourceInfo_.<Text>getField("txtName");
    if(background) {
      new ThreadExecutor(excutor, txtName).start();
    } else {
      new WaitLoading(txtName, excutor, SWT.TITLE).open();
    }
  }
  
  public static void main(String[] args) {
    String address = "http://www.shcrewvn.com/viewtopic.php?f=104&t=5196";
    System.out.println(address.toLowerCase().indexOf("showthread.php?t="));
  }
}
