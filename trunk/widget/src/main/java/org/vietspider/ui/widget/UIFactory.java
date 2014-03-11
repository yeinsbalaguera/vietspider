/***************************************************************************
 * Copyright 2001-2005 VietSpider         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.vietspider.ui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.vietspider.ui.widget.action.IHyperlinkListener;

/**
 * @by thuannd (nhudinhthuan@yahoo.com)
 * @since Jul 12, 2005
 */
public class UIFactory extends WidgetFactory {
 
  protected Composite composite;  
  
  public Button createButton(int style){
    return super.createButton(composite, style);
  }
  
  public Button createButton( int style, String text){
    return super.createButton(composite, style, text);   
  }
  
  public Button createButton(String text){
    return super.createButton(composite, SWT.PUSH, text);   
  } 
  
  public Button createButton(String text,SelectionListener listener){
    return super.createButton(composite, SWT.PUSH, text, listener);   
  } 
  
  public Button createButton( String text, String tip,SelectionListener listener){
    return super.createButton(composite, SWT.PUSH, text, tip, listener);   
  } 
  
  public Button createButton( Image icon, String tip,SelectionListener listener){
    return super.createButton(composite, icon, tip, listener);   
  } 
  
  public RButton createIcon(Image img, String tip, int t, MouseListener listener) {
    return super.createIcon(composite, img, tip, t, listener);   
  } 
  
  public ImageHyperlink createIcon(Image img, String tip, IHyperlinkListener listener) {
    return super.createIcon(composite, img, tip, listener);   
  } 
  
  public org.eclipse.ui.forms.widgets.Hyperlink createLink(
      String text, org.eclipse.ui.forms.events.IHyperlinkListener listener) {
    return super.createLink(composite, text, listener);   
  } 
  
  public org.eclipse.ui.forms.widgets.Hyperlink createMenuLink(
      String text, HyperlinkAdapter...adapters) {
    return super.createMenuLink(composite, text, adapters);   
  } 
  
  public Label createLabel(int style){
    return super.createLabel(composite, style);
  }
  
  public Label createLabel(int style, String text){
    return super.createLabel(composite, style, text);  
  }
  
  public Label createLabel(String text){
    return super.createLabel(composite, SWT.NONE, text);  
  }  
  
  public Text createText(int style){
    return super.createText(composite, style);
  }
  
  public Text createText(String text){
    return super.createText(composite, SWT.BORDER | SWT.SINGLE, text);  
  }
  
  public Text createText(){
    return super.createText(composite, SWT.BORDER | SWT.SINGLE);  
  }
  
  public Spinner createSpinner(int style){
    return super.createSpinner(composite, style);
  }
  
  public Spinner createSpinner() {
    return super.createSpinner(composite, SWT.BORDER | SWT.READ_ONLY);
  }
  
  public Group createGroup(String text){
    return super.createGroup(this.composite, SWT.NONE, text);
  } 
  
  public Group createGroup( String text, Object layoutData){
    return super.createGroup(composite, SWT.NONE, text, layoutData);
  } 
  
  public Group createGroup(String text, Object layoutData, Layout layout){
    return super.createGroup(composite, SWT.NONE, text, layoutData, layout);
  } 
  
  public Combo createCombo(int style){
    return super.createCombo(composite, style);
  }
  
  public Combo createCombo(int style, String[] txt){
    return super.createCombo(composite, style, txt);   
  }
  
  public Combo createCombo(String[] txt){
    return super.createCombo(composite, SWT.BORDER | SWT.READ_ONLY , txt);   
  }
  
  public List createList(int style, String[] text){
    return super.createList(composite, style, text);   
  }
 
  public List createList( int style, String[] text, SelectionListener listener){
    return super.createList(composite, style, text, listener);   
  }
  
  public List createList(String[] text, SelectionListener listener){
    return super.createList(composite, 
        SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, text, listener);   
  }
  
  public Table createTable( int style, String [] cols, int [] w, SelectionListener listener){
    return super.createTable(composite, style, cols, w, listener);   
  }
  
  public Table createTable(String [] cols, int [] w, SelectionListener listener){
    return super.createTable(composite, 
      SWT.BORDER |  SWT.FULL_SELECTION | SWT.SINGLE, cols, w, listener);   
  }
  
  public Table createTableWithStyle(String [] cols, int [] w, SelectionListener listener, int style){
    return super.createTable(composite,  style, cols, w, listener);   
  }
 
  public MenuItem createMenuItem( Menu menu, String text){
    return super.createMenuItem( menu, SWT.CASCADE, text);    
  }
  
  public MenuItem createMenuItem( Menu menu, String text, SelectionListener listener){
    return super.createMenuItem( menu, SWT.CASCADE, text, listener);   
  }
  
  public ProgressBar createProgress( GridData gridData){
    return super.createProgress(composite, gridData);
  }
  
  public Composite getComposite(){
    return this.composite;
  }
  
  public void setComposite( Composite composite){
    this.composite = composite;
  } 

}
