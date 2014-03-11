package org.vietspider.ui.htmlexplorer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TreeItem;
import org.vietspider.browser.FastWebClient;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.common.util.Worker;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.waiter.WaitLoading;

public class HTMLExplorer extends HTMLExplorerViewer {

  public final static int NONE = 0, SECTION = 1, CONTENT = 2;

  private boolean cache = false;
  private boolean decode = false;

  private String refer;
  
  public HTMLExplorer(Composite parent){
    super(parent, CONTENT);
  }
  
  public HTMLExplorer(Composite parent, int type){
    super(parent, type);
  }

  public void goAddress(String strAddress){    
    if(strAddress == null) {
      strAddress = browser.getUrl();
    } else {
      browser.setUrl(strAddress);
      this.currentURL = strAddress;
    }
    if(strAddress.indexOf("//") < 0) strAddress = "http://"+strAddress; 
    if(strAddress == null || strAddress.trim().length() == 0) strAddress = openFile();
    if(strAddress == null || strAddress.trim().length() < 1) return;
    try {
      File file = new File(strAddress);	
      if(file.exists()){
        URL url = file.toURI().toURL();
        strAddress = url.toString();
      }               
    } catch( Exception exp) {
      ClientLog.getInstance().setMessage(getShell(), exp);
    }
    Preferences prefs = Preferences.userNodeForPackage(HTMLExplorer.class);
    prefs.put("url.address", strAddress);
    charset = null;
  }

  String openFile(){
    Preferences prefs = Preferences.userNodeForPackage(getClass());     
    String path = prefs.get("path", "");
    FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
    if(path != null) dialog.setFilterPath( path);
    path = dialog.open();
    if( path == null || path.trim().equals("")) return null;
    prefs.put("path", path);
    charset = null;
    return path;   
  }

  public void selectAddress(final String ...paths) {
    Worker excutor = new Worker() {

      private String url ;

      private String html;  

      public void abort() {
        webClient.abort(url);
      }

      public void before() {
        url = browser.getUrl();
//        System.out.println(" ==============  >"+ url);
        if(url == null || url.equals("about:blank") || url.trim().isEmpty()) {
          url = currentURL;    
        } 
      }

      public void execute() {
        try {
          HTMLParserDetector detector = new HTMLParserDetector();
          detector.setDecode(decode);
          detector.setCharset(charset);

          if(html == null || html.trim().isEmpty()) {
            document = webClient.createDocument(refer, url, cache, detector);
          } else {
//                        System.out.println(html);
            document = detector.createDocument(html);
          }
//          System.out.println(" =============== > "+ document);
          if(charset == null) charset = detector.getCharset();
          removeIFrameSource(document.getRoot());
        } catch(Exception exp) {
          ClientLog.getInstance().setException(null, exp);
        }   
      }

      public void after() {
        currentURL = url;
//        toolbar.setText(url);
//        toolbar.addAddressToList(url);
        tree.removeAll();
        if(document != null) handler.createTreeItem(tree, document); 
        new AutoSelectDataNode2(HTMLExplorer.this, document, url, handler, tree);

        if(paths.length < 1) {
          HTMLExtractor extractor  = new HTMLExtractor();
          NodePathParser pathParser = new NodePathParser();
          if(hyperlinkUtil == null) hyperlinkUtil = new HyperLinkUtil();
          HTMLNode header = null;
          HTMLNode body = null;
          try {
            NodePath nodePath  = pathParser.toPath("HEAD");
            header = extractor.lookNode(document.getRoot(), nodePath);
            nodePath  = pathParser.toPath("BODY");
            body = extractor.lookNode(document.getRoot(), nodePath);
          } catch (Exception e) {
            ClientLog.getInstance().setException(getShell(), e);
          }

          if(header == null || body == null) return;

          try {
            String address = browser.getUrl();
            URL home = new URL(address);
            hyperlinkUtil.createFullNormalLink(document.getRoot(), home);
            hyperlinkUtil.createFullImageLink(document.getRoot(), home);
            Map<String,String> map = new HashMap<String,String>(); 
            map.put("link","href");
            map.put("script","src");
            hyperlinkUtil.createFullLink(header, map, home, null);
          } catch (Exception e) {
            ClientLog.getInstance().setException(getShell(), e);
          }

          searchRenderer.viewDocument(header, body, url);
        }

        if(document != null) {
          for(String path : paths) {
            setPath(path, TreeHandler.SELECT);
          }
        } else {
          browser.setUrl(url);
          for(String path : paths) box.addPath(path, false);
        }
      }
    };
    new WaitLoading(browser, excutor).open();
  }

  public void setPath(String path, int style){  
    if(path == null) return;
    try {
      handler.traverseTree(this, tree, path, style);
      box.addPath(path, false);
    } catch (Exception e) {
      ClientLog.getInstance().setMessage(tree.getShell(), e);
    }
    box.packList();
    selectTree();
  }

  String selectTree() {
    TreeItem[] items = tree.getSelection();
    if( items == null || items.length  < 1) return null;
    final TreeItem item = items[0];
    final String txt = handler.getConfig(item);

//    treeAddButton.computeShowArea(item);

    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();

    try {
      NodePath nodePath  = pathParser.toPath(txt);
      HTMLNode node = extractor.lookNode(document.getRoot(), nodePath);
      if(node == null) return txt;
      if(node.isNode(Name.CONTENT) 
          || node.isNode(Name.COMMENT) 
          || node.isNode(Name.UNKNOWN)) {
        browser.setText(node.getTextValue());
        return txt;
      }

      NodePath headerPath  = pathParser.toPath("HEAD");
      HTMLNode header = extractor.lookNode(document.getRoot(), headerPath);

      String address = browser.getUrl();
      if(address != null && address.startsWith("http") && address.length() > 7) {
        if(hyperlinkUtil == null) hyperlinkUtil = new HyperLinkUtil();
        try {
          URL home = new URL(address);

          hyperlinkUtil.createFullNormalLink(node, home);
          hyperlinkUtil.createFullImageLink(node, home);
          HashMap<String, String> map = new HashMap<String,String>(); 
          map.put("link","href");
          map.put("script","src");
          hyperlinkUtil.createFullLink(header, map, home, null);
        } catch(MalformedURLException me) {
        } catch (Exception e) {
          ClientLog.getInstance().setException(getShell(), e);
        }
      }

      StringBuilder builder = new StringBuilder();
      builder.append("<html>");
      builder.append("<head>");
      if(/*toolbar.isShowAll() &&*/ header != null && header.getChildren() != null) {
        for(HTMLNode ele : header.getChildren()){
          builder.append(ele.getTextValue()).append('\n');
        }
      }
      //      String baseHref = HTMLExplorer.class.getResource("").toString();
      //      builder.append("<base href=\""+baseHref+"\">");
      if(node.isNode(Name.BODY)) {
        HTMLNode body = null;
        try {
          nodePath  = pathParser.toPath("BODY");
          body = extractor.lookNode(document.getRoot(), nodePath);
        } catch (Exception e) {
          ClientLog.getInstance().setException(getShell(), e);
        }

        if(header == null || body == null) return txt;
        searchRenderer.viewDocument(header, body, null);
      } else {
        searchRenderer.viewDocument(null, node, null);
      }
    } catch(Exception exp){
      ClientLog.getInstance().setException(getShell(), exp);
    }    
    return txt;
  }  

  public String[] addItems(){
    TreeItem[] items = tree.getSelection();
    if( items == null || items.length  < 1) return null;
    for(TreeItem item : items) {
      box.addPath(handler.getConfig(item), false);    
    }
//    box.setPath("");
    return box.getItems();
  }

  void removeItem() throws Exception {
    TreeItem[] items = tree.getSelection();
    if( items == null || items.length  < 1) return;
    List<String> list = new ArrayList<String>();
    for(TreeItem item : items) {
      String path = handler.getConfig(item);
      box.removePath(path);
      list.add(path);
    }
    traverseTree(TreeHandler.REMOVE, list.toArray(new String[list.size()]));
    box.setSuggestPath("");
  }

  void viewItem(){
    TreeItem[] items = tree.getSelection();
    if( items == null || items.length  < 1) return;
    int x = getShell().getLocation().x+120, y = getShell().getLocation().y +130;
    HTMLExtractor extractor  = new HTMLExtractor();
    NodePathParser pathParser = new NodePathParser();
    for(TreeItem item : items) {
      String pathIndex = handler.getConfig(item); 
      try {
        NodeInfoViewer viewer = new NodeInfoViewer(getShell(), x, y);
        NodePath nodePath = pathParser.toPath(pathIndex);
        HTMLNode node = extractor.lookNode(document.getRoot(), nodePath);
        viewer.setNode(node);   
        x += 10;
        y += 10;
        nodeViewers.add(viewer);
      } catch(Exception exp) {
        ClientLog.getInstance().setMessage(getShell(), exp);
      }
    }
  }

  public void traverseTree(int style, String[] paths) throws Exception { 
    NodePathParser pathParser = new NodePathParser();
    if(paths != null) {
      for(String path : paths) {
        NodePath nodePath = pathParser.toPath(path);
        handler.traverseTree(this, tree, nodePath, path, style);
      }
    }
  }

  public void search(String value){    
    createDocument();
    super.search(value);
  }

  private void createDocument(){
    if(document == null){
      selectAddress();
      return;
    }
    String browserURL = browser.getUrl();
    if(browserURL == null || browserURL.trim().length() == 0) return;
    if(!SWProtocol.isHttp(browserURL)) return;
    if(browserURL.equals(currentURL)) return; 
    selectAddress();      
  }  

  public void reset() {
    browser.setText("<html></html>");
    box.removeAll();
    tree.removeAll();
    charset = null;
    currentURL = null;  
    document = null;
    decode = false;
  }

  public void setWebClient(FastWebClient webClient) { this.webClient = webClient; }

  public void setRefer(String refer) { this.refer = refer; }

  public void setCache(boolean cache) { this.cache = cache; }

  public void setDecode(boolean decode) { this.decode = decode; }
  
  public boolean hasSelectedItem() { 
    return box != null && box.getSelectedIndex() > -1; 
  }
}
