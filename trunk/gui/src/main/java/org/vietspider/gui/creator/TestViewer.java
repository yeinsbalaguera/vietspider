/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vietspider.html.HTMLDocument;
import org.vietspider.ui.browser.PageMenu;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 2, 2008  
 */
public class TestViewer extends Composite {
  
  private Browser browser;
  private Creator creator;
  
  private ISourceInfo iSourceInfo;
  
  private Text txtURL;
  
  public TestViewer(Creator creator_, Composite parent) {
    super(parent, SWT.NONE);
    
    setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    
    this.creator = creator_;
    
    ApplicationFactory factory = new ApplicationFactory(this, "Creator", getClass().getName());
    
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);
    
    GridData gridData;
    
    browser = ApplicationFactory.createBrowser(this, PageMenu.class);
    browser.addStatusTextListener(new StatusTextListener(){

      @SuppressWarnings("unused")
      public void changed(StatusTextEvent evt) {
        String url = browser.getUrl();
        if(url == null || iSourceInfo == null) return;
        url = url.trim();
        if(url.isEmpty() 
            || "about:blank".equalsIgnoreCase(url)
            || !url.toLowerCase().startsWith("http")) return;
        Text txtPattern = iSourceInfo.<Text>getField("txtPattern");
        if(txtPattern.getText().trim().equalsIgnoreCase(url)) return;
        browser.stop();
        txtPattern.setText(url);
        txtURL.setText(url);
        ((SourceEditor)iSourceInfo).test();
      }
    });
    
    gridData = new GridData(GridData.FILL_BOTH);
    browser.setLayoutData(gridData);
    
    Composite bottom = new Composite(this, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    bottom.setLayoutData(gridData);
    bottom.setBackground(getBackground());
    
    gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 1;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 1;
    bottom.setLayout(gridLayout);
    
    factory.setComposite(bottom);
    
    txtURL = factory.createText();
    SourceEditorUtil.setDropTarget(txtURL);
    txtURL.setFont(UIDATA.FONT_10);
    txtURL.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 
    txtURL.addModifyListener(new ModifyListener(){
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent e){
        changeURL();
      }
    });
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtURL.setLayoutData(gridData);
    txtURL.addMouseListener(new MouseAdapter() {
      public void mouseDown(MouseEvent e) {
        if(e.count == 3) {
          txtURL.setText("");
          txtURL.paste();
          return;
        }       
      }
    });
    
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    Button button = factory.createButton("butCancel", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        creator.showMainForm();
      }   
    }); 
    button.setFont(UIDATA.FONT_9VB);
    button.setLayoutData(gridData);
    
  }
  
  private void changeURL() {
    final int time = 2*1000;
    final String pattern = txtURL.getText();
    final Text txtPattern = iSourceInfo.<Text>getField("txtPattern");
    if(pattern.equals(txtPattern.getText())) return;
    Runnable timer = new Runnable () {
      public void run () {
         if(txtURL.getText().equalsIgnoreCase(pattern)) {
           browser.stop();
           txtPattern.setText(pattern);
           ((SourceEditor)iSourceInfo).test();
         } 
      }
    };
    txtURL.getDisplay().timerExec (time, timer);
  }
  
  public void show(String charset, Object value) {
    if(value instanceof String) {
      browser.setText((String)value);
//      creator.showTestViewer();
      return;
    } else if(value instanceof URL) {
      browser.setUrl(((URL)value).toString());
//      creator.showTestViewer();
      return;
    }
    
    HTMLDocument document = (HTMLDocument) value;
//  creator.sho
    StringBuilder builder = new StringBuilder("<html>");
    if(charset == null || charset.trim().length() < 1) charset = "utf-8";
    builder.append("<head>\n")
          .append("\n<meta http-equiv=\"content-type\" content=\"text/head; charset=")
          .append(charset).append("\">")
          .append("\n</head><body>\n")
          .append(document != null ? document.getRoot().getTextValue() : "empty data")
          .append("\n</body></html>");

    browser.setText(builder.toString());
//    creator.showTestViewer();
  }

  public void setSourceEditor(ISourceInfo iSourceInfo) {
    this.iSourceInfo = iSourceInfo;
    Text txtPattern = iSourceInfo.<Text>getField("txtPattern");
    if(txtURL.getText().equals(txtPattern.getText())) return;
    txtURL.setText(txtPattern.getText());
  }

}
