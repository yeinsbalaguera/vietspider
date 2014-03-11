/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.ui.htmlexplorer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.path2.NodePath;
import org.vietspider.html.path2.NodePathParser;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;
import org.vietspider.ui.widget.ApplicationFactory;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * May 13, 2006
 */
public class NodeInfoViewer {
  
  private Text txtName, txtValue, txtChildren, txtPathName, txtPathIndex, txtAttributes ;
  private Shell shell;
  
  public NodeInfoViewer(Shell parent) { 
    this(parent, parent.getLocation().x+120, parent.getLocation().y +130);
  }

  public NodeInfoViewer(Shell parent, int x, int y) {    
    shell = new Shell(parent, SWT.CLOSE);
    shell.setImage(parent.getImage());
    ApplicationFactory factory = new ApplicationFactory(shell, "HTMLExplorer", getClass().getName());
    factory.setComposite(shell);
    shell.setParent(parent);
    shell.setLocation(x, y);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;    
    shell.setLayout( gridLayout); 
    factory.setComposite(shell);

    factory.createLabel("nodeName");     
    txtName = factory.createText();
    txtName.setEditable(false);
    GridData gridData = new GridData();    
    gridData.grabExcessHorizontalSpace = true;
    gridData.widthHint = 200;
    txtName.setLayoutData(gridData); 
    
    factory.createLabel("nodeValue");    
    txtValue = factory.createText(SWT.BORDER | SWT.WRAP);
    txtValue.setEditable(false);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtValue.setLayoutData(gridData); 
    
    factory.createLabel("nodeTotalChildren"); 
    txtChildren = factory.createText();
    txtChildren.setEditable(false);
    gridData = new GridData();
    gridData.widthHint = 100;
    txtChildren.setLayoutData(gridData); 
    
    factory.createLabel("nodePathName");    
    txtPathName = factory.createText(SWT.BORDER | SWT.WRAP);
    txtPathName.setEditable(false);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtPathName.setLayoutData(gridData);   
    
    factory.createLabel("nodeAttributes");    
    txtAttributes = factory.createText(SWT.BORDER | SWT.WRAP);
    txtAttributes.setEditable(false);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtAttributes.setLayoutData(gridData);     
  }
  
  public void setName(String name){ txtName.setText(name); }
  
  public void setValue(String value) { txtValue.setText(value); }
  
  public void setTotalChildren(int value) { txtChildren.setText(String.valueOf(value)); }
  
  public void setPathName(String value) { txtPathName.setText(value); }
  
  public void setPathIndex(String value) { txtPathIndex.setText(value); }
  
  public void setAttributes(Attributes list){
    String value = "";
    for(Attribute ele : list){
      value += "  ["+ele.getName() +" : "+ele.getValue()+"]  ";
    }
    txtAttributes.setText(value);
  }
  
  public void setNode(HTMLNode node){
    setName(node.getName().toString());
    setValue(new String(node.getValue()));    
    NodePath path = new NodePathParser().toPath(node);
    setPathName(path.toString());
    if(node.getChildren() != null ){ 
      setTotalChildren(node.getChildren().size());
      setAttributes(node.getAttributes());
    }
    shell.pack();
    shell.open();
  }
  
  public void close() {
    if(shell == null) return ;
    if(shell.isDisposed()) return ;
    shell.dispose();
  }
}
