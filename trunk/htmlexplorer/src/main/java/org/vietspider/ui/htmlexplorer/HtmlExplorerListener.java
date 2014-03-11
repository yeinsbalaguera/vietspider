package org.vietspider.ui.htmlexplorer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.vietspider.browser.FastWebClient;
import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.path2.HTMLExtractor;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.ui.action.ActionListener;
import org.vietspider.ui.action.Listeners;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 27, 2006  
 */
public abstract class HtmlExplorerListener 
    extends Composite implements ActionListener, JsSearchText {
  
  protected Browser browser;
  protected JsSearchTextRenderer searchRenderer;
  
  protected HTMLDocument document;

  protected FastWebClient webClient;
  
  protected TreeHandler handler;  
  protected Tree tree;
  
  protected PathBox box;
  
  protected Label lblStatus;
  protected Button butRemovePath;
//  protected Button butUp;
//  protected Button butDown;
//  protected Button butRemoveAll;
  protected String errorPath = null;
  protected String suggestTip;
  
  private List<Object> actions = new LinkedList<Object>();
  
  protected List<String> errorPaths = new ArrayList<String>();
  
  public HtmlExplorerListener(Composite parent) {
    this(parent, SWT.NONE);
  }
  
  public HtmlExplorerListener(Composite parent, int type) {
    super(parent, type);
    setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    searchRenderer = new JsSearchTextRenderer(this);
  }
  
  public void addAction(Object action){ 
    actions.add(action); 
  }
  
  @Listeners()
  public List<Object> getActions() {  return actions; }
  
  public void removeAction(Object action){
    actions.remove(action);
  }

  public Browser getBrowser() { return browser;  }
  
  public String getSearchName() { return "theJavaFunction"; }
  
  public void search(String value){   
    if(document == null) return;
    String split[] = value.split("\n");   
    if(split.length == 1) {
      search(split[0].trim(), null);
    } else {
      search(split[0].trim(), split[split.length-1].trim());
    }
  }

  private void search(String start, String end){
    try {
      HTMLNode body = document.lookupNode(Name.BODY);
      if(body == null) body = document.getRoot();
//      System.out.println(" ===  > "+ webClient);
//      System.out.println(" hehe  "+ start +  " : "+ end + " : "+ body);
      NodePath path = webClient.findNodeByText(body, start, end);   
      if(path == null) return; 
      handler.traverseTree(this, tree, path, TreeHandler.NONE);      
    } catch(Exception exp) {
      ClientLog.getInstance().setException(null, exp);
      ClientLog.getInstance().setMessage(tree.getShell(), exp);
    }  
  }
  
  protected String[] getAttrs(String path) {
    NodePathParser pathParser = new NodePathParser();
    NodePath nodePath = null;
    try {
      nodePath = pathParser.toPath(path);
    } catch (Exception exp) {
      return null;
    }
    HTMLExtractor extractor  = new HTMLExtractor();
    HTMLNode node = extractor.lookNode(document.getRoot(), nodePath);
    if(node == null) return null;

    java.util.List<String> lattrs = new ArrayList<String>();
    for(Attribute ele : node.getAttributes()){
      lattrs.add(ele.getName()+"='" + ele.getValue()+ "'");
    }
    return lattrs.toArray(new String[0]);
  }
  
  protected void traverseByPath(String path) {
    NodePathParser pathParser = new NodePathParser();
    NodePath nodePath = null;
    try {
      nodePath = pathParser.toPath(path);
    } catch (Exception exp) {
      return;
    }
    handler.traverseTree(this, tree, nodePath, path);
  }
  
  
  public Tree getTree() { return tree; }
  
  public abstract String [] addItems();
  
  public abstract boolean hasSelectedItem();
  
  public HTMLDocument getDocument() { return document; }
  
  public boolean isErrorPath(String path) { return errorPaths.contains(path); }
  
//  public void addErrorPath(String path) {  errorPaths.add(path); }
  
  public void addErrorPath(String path) { 
    errorPaths.add(path);
    Runnable timer = new Runnable () {
      public void run () {
        if(box.isDisposed()) return;
        box.showErrorPath(errorPaths);
      }
    };
    getDisplay().timerExec (1000, timer);
  }
  
  public void clearErrorPath() { errorPaths.clear(); }
  
  public int totalErrorPath() { return errorPaths.size(); }

 /* public void showErrorPath(String path) {
    errorPath = path;
    butRemovePath.setVisible(false);
    lblStatus.setVisible(false);
    if(path == null) return;
    Runnable timer = new Runnable () {
      public void run () {
        if(lblStatus.isDisposed()) return;
        butRemovePath.setVisible(true);
        lblStatus.setVisible(true);
        lblStatus.setText(PathConfirmDialog.ERROR_PATH);
        lblStatus.getParent().pack();
      }
    };
    getDisplay().timerExec (500, timer);
  }*/
  
  public void showErrorPath(String path) {
    if(lblStatus.isDisposed()) return;
    errorPath = path;
    butRemovePath.setVisible(false);
//    lblStatus.setVisible(false);
    if(path == null) return;
    butRemovePath.setVisible(true);
//    lblStatus.setVisible(true);
    ClientRM resource = new ClientRM("HTMLExplorer");
    lblStatus.setText(resource.getLabel("invalid_path"));
    lblStatus.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
    lblStatus.getParent().pack();
  }
  
  public final void showError(String message) {
    if(lblStatus.isDisposed()) return;
    butRemovePath.setVisible(false);
    lblStatus.setText(message);
    lblStatus.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
    lblStatus.getParent().pack();
  }
  
  public final void showInformation(String text) {
    if(lblStatus.isDisposed()) return;
    butRemovePath.setVisible(false);
    lblStatus.setText(text);
    lblStatus.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
    lblStatus.getParent().pack();
  }
  
  public final void clearInformation() {
    if(lblStatus.isDisposed()) return;
    butRemovePath.setVisible(false);
    lblStatus.setText("");
    lblStatus.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
    lblStatus.getParent().pack();
  }
}
