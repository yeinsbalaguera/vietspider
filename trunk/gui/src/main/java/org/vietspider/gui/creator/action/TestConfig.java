/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator.action;

import java.util.Properties;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.ISourceInfo;
import org.vietspider.gui.creator.TestViewer;
import org.vietspider.gui.creator.test.TestPlugin;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.model.ExtractType;
import org.vietspider.model.Region;
import org.vietspider.model.Source;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.WaitLoading;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jan 23, 2008  
 */
public class TestConfig extends ClientDocumentLoader {

  protected Control control;
  protected Creator creator; 
  
  protected HTMLDocument doc;

  private WaitLoading waitLoading;

  public TestConfig(ISourceInfo iSourceInfo, Control control) {
    setISourceInfo(iSourceInfo);
    this.control = control;

    this.creator = iSourceInfo.<Creator>getField("creator");
  }

  public void execute() {
    if(!iSourceInfo.check()) return;
    final Source source = iSourceInfo.createSource();
    if(source == null) return;
    Worker excutor = new Worker() {

      String message = null;

      public void abort() {
        TestConfig.this.abort(source.getPattern());
      }

      public void before() {
        doc = null;
      }

      public void execute() {
        String url = source.getPattern(); 
        if(url == null || url.trim().length() == 0) {
          message = "No pattern address";
          return;
        }
        
        try {
          doc = getDocument(url, false);
//          File file  = UtilFile.getFile("track/test/", "test.html");
//          org.vietspider.common.io.RWData.getInstance().save(file, doc.getTextValue().getBytes("utf-8"));
          /*String referer = iSourceInfo.getReferer();
          if(webClient.getHost() == null) {
            if(HttpSessionUtils.isMultiProxies(proxy)) {
              webClient.setURL(referer, new URL(homepage), proxy);
            } else if(proxy != null && !proxy.trim().isEmpty()){
              webClient.setURL(referer, new URL(homepage));
              webClient.registryProxy(proxy);
            }  else {
              webClient.setURL(referer, new URL(homepage));
            }
          }
          loginWebsite.login();
          HTMLParserDetector detector = new HTMLParserDetector(charset);
          detector.setDecode(decode);
          
          doc = webClient.createDocument(iSourceInfo.getReferer(), url, false, detector);
          List<String> jsDocWriters = new JsDocWriterUI(iSourceInfo).getJsDocWriters();
          if(jsDocWriters.size() > 0) JsHandler.updateDocument(doc, jsDocWriters);*/
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
          message = e.toString() ;
        }
      }

      public void after() {
        saveShellProperties(waitLoading.getWindow());
        control.setEnabled(true);
        if(message != null && message.trim().length() > 0) {
          ClientLog.getInstance().setMessage(creator.getShell(), new Exception(message));
          return;
        }
        if(doc == null) return;
        executeAfter(source);
      }
    };
    control.setEnabled(false);
    waitLoading =  new WaitLoading(control, excutor, SWT.TITLE);
    loadShellProperties(waitLoading.getWindow());
    waitLoading.open();
  }

  private void executeAfter(Source source) {
    if(doc == null) return;
//    final String group = creator.getSelectedGroupName();
    NodePathParser pathParser = new NodePathParser();
    Region [] detachPaths = source.getExtractRegion();
    HTMLExtractor extractor = new HTMLExtractor();
    
    NodePath[] extractPaths = null;
    if(detachPaths != null && detachPaths.length > 0 && detachPaths[0] != null)  {
      try {
        extractPaths = pathParser.toNodePath(detachPaths[0].getPaths());
      } catch (Exception e) {
        ClientLog.getInstance().setException(creator.getShell(), e);
        extractPaths = null;
      }
    }
    
    if(extractPaths != null && extractPaths.length > 0) {
      if(source.getExtractType() == ExtractType.ROW) {
        doc = extractor.extractFirst(doc, extractPaths);
      } else {
        doc = extractor.extract(doc, extractPaths);
      }
    }
    
    NodePath [] removePaths = null;
    boolean isRemoveFrom = false;
    
    if(detachPaths != null && detachPaths.length > 1 && detachPaths[1] != null) {
      try {
        removePaths = pathParser.toNodePath(detachPaths[1].getPaths());
      } catch (Exception e) {
        ClientLog.getInstance().setException(creator.getShell(), e);
        removePaths = null;
      }

      Properties properties = detachPaths[1].getProperties();
      String property = properties.getProperty(Region.CLEAN_FROM);
      if(property != null) isRemoveFrom = Boolean.valueOf(property);
    }
    if(doc == null) return;
    
    extractor.remove(doc.getRoot(), isRemoveFrom, removePaths); 
    
//   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
//    File file = new File("D:\\Temp\\", "update_doc.html"); 
//    try {
//    writer.save(file, doc.getTextValue().getBytes("utf-8"));
//    } catch (Exception e) {
//      ClientLog.getInstance().setException(creator.getShell(), e);
//      return;
//    }
    
    Object testData = null;

    String group = creator.getSelectedGroupName();
    TestPlugin testPlugin = createTestPlugin(group);
    if(testPlugin != null) {
      try {
        testData = testPlugin.process(source, doc);
      } catch (Exception e) {
        ClientLog.getInstance().setException(creator.getShell(), e);
        return;
      }
    } else {
      testData = doc;
    } 
    
    TestViewer viewer = creator.showTestViewer();
    viewer.setSourceEditor(iSourceInfo);
    viewer.show(source.getEncoding(), testData);
  }

  private TestPlugin createTestPlugin(String group) {
    try {
      String name  = String.valueOf(group.charAt(0)) + group.substring(1).toLowerCase();
      Class<?> clazz = Class.forName("org.vietspider.gui.creator.test." + name + "TestPlugin");
      return (TestPlugin) clazz.newInstance();
    } catch (Exception e) {
//      e.printStackTrace();
      return null;
    } 
  }
  
  protected void loadShellProperties(Shell dialog) {
    if(dialog == null || dialog.isDisposed()) return;
    int x = dialog.getLocation().x;
    int y = dialog.getLocation().y;
    
    String name  = getClass().getSimpleName();
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    try {
      x = Integer.parseInt(prefs.get(name+"_location_x", ""));
    } catch (Exception e) {
    }
    
    try {
      y = Integer.parseInt(prefs.get(name+"_location_y", ""));
    } catch (Exception e) {
    }
    dialog.setLocation(x , y);
  }
  
  protected void saveShellProperties(Shell dialog) {
    if(dialog == null || dialog.isDisposed()) return;
    Preferences prefs2 = Preferences.userNodeForPackage(getClass());
    String name2  = getClass().getSimpleName();
    Point point = dialog.getLocation();
    try {
      prefs2.put(name2+"_location_x", String.valueOf(point.x));
      prefs2.put(name2+"_location_y", String.valueOf(point.y));
    } catch (Exception e) {
    }
  }
  
}
