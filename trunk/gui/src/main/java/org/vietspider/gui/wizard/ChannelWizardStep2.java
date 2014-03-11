package org.vietspider.gui.wizard;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.SortedMap;

import net.sf.swtaddons.autocomplete.combo.AutocompleteComboInput;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.vietspider.html.util.HTMLParserDetector;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;

/***************************************************************************
 * Copyright 2001-2011 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Dec 30, 2011  
 */
public class ChannelWizardStep2 extends ChannelWizardComposite {

  private Browser browser;
  protected Combo cboCharset;

  public ChannelWizardStep2(ChannelWizard wizard) {
    super(wizard);

    browser = ApplicationFactory.createBrowser(this, null);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    browser.setLayoutData(gridData);

    Composite composite = new Composite(this, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    composite.setLayoutData(gridData);
    GridLayout gridLayout = new GridLayout(3, false);
    composite.setLayout(gridLayout);

    gridData = new GridData(GridData.FILL_HORIZONTAL);
    createButton(gridData);
  }
  
  public void show() {
    wizard.setTitle(" 2/10");
    HTMLParserDetector detector = new HTMLParserDetector();
    byte[] bytes = wizard.getTemp("page.data.content");
    String charset = detector.detectCharset(bytes);
    if(charset != null) select(charset);
    showHTML();
  }
  
  private void select(String charset) {
    if(charset.trim().length() < 1) return;
    for(int i = 0; i < cboCharset.getItemCount(); i++) {
      if(cboCharset.getItem(i).equalsIgnoreCase(charset)) {
        cboCharset.select(i); 
        return;
      }
    }
    cboCharset.add(charset);
    cboCharset.select(cboCharset.getItemCount()-1);
  }
  
  private void showHTML() {
    String charset = cboCharset.getText();
    if(charset.trim().length() < 1) {
      cboCharset.select(0);
      charset = cboCharset.getText();
    }
    try {
      byte[] bytes = wizard.getTemp("page.data.content");
      browser.setText(new String(bytes, charset));
    } catch (Exception e) {
      error(e.toString());
    }
  }
  
  protected void createButton(GridData gridData) {
    Composite composite = new Composite(this, SWT.NONE);
    composite.setLayoutData(gridData);
    
    GridLayout gridLayout = new GridLayout(9, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 15;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    composite.setLayout(gridLayout);
    
    wizard.createToolBarButton(composite);
    
    createStatusLabel(composite);
    
    Label lblCharset = new Label(composite, SWT.NONE | SWT.RIGHT);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    lblCharset.setLayoutData(gridData);
    lblCharset.setText(ChannelWizard.getLabel("step2.encoding"));
    lblCharset.setFont(UIDATA.FONT_9);

    cboCharset = new Combo(composite, SWT.BORDER);
    cboCharset.setFont(UIDATA.FONT_9);
    cboCharset.setVisibleItemCount(10);
    String[] charsets = {"UTF-8", "windows-1252", "ISO-8859-1", "US-ASCII", "UTF-16", "GB2312", "Shift_JIS"};
    cboCharset.setItems(charsets);
    new AutocompleteComboInput(cboCharset);
    cboCharset.setText("UTF-8");
    cboCharset.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent arg0) {
        showHTML();
      }
    });
    cboCharset.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent evt) {
        if (evt.keyCode == SWT.CR) {
          select(cboCharset.getText());
          showHTML();
        }
      }
    });
    gridData = new GridData();
    gridData.heightHint = 26;
    Button butAddMore = new Button(composite, SWT.PUSH);
    butAddMore.setText(ChannelWizard.getLabel("step2.add.more"));
    butAddMore.addSelectionListener(new SelectionAdapter() {
      @Override
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        SortedMap<String, Charset> sortedMap = Charset.availableCharsets();
        Iterator<String> iter = sortedMap.keySet().iterator();
        cboCharset.removeAll();
        while (iter.hasNext()) {
          cboCharset.add(iter.next());
        }
      }
    });
    butAddMore.setFont(UIDATA.FONT_9);
    butAddMore.setLayoutData(gridData);
    
    Label lblSpace = new Label(composite, SWT.NONE | SWT.RIGHT);
    gridData = new GridData();
    lblSpace.setLayoutData(gridData);
    lblSpace.setText("    ");
    
    createHelpButton(composite);
    createPreviousButton(composite);
    createNextButton(composite);
  }
  
  @Override
  void openHelp() {
    String youtube = "http://www.youtube.com/watch?v=YZ3dLBkpGx8";
    String video = "http://vietspider.org/video/step2.avi";
    wizard.openHelp(youtube, video);
  }
  
  public void next() {
    String charset = cboCharset.getText();
    if(charset.trim().length() < 1) {
      cboCharset.select(0);
      charset = cboCharset.getText();
    }
    wizard.getSource().setEncoding(charset);
    
    showNext(wizard);
  }
  
  public void reset() {
    cboCharset.select(0);
    browser.setUrl("about:blank");
  }

}
