package org.vietspider.gui.wizard;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
public class ChannelWizardStep8 extends ChannelWizardComposite {

  private Browser browser;
  protected Text txtName;

  public ChannelWizardStep8(ChannelWizard wizard) {
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
  
  protected void createButton(GridData gridData) {
    Composite composite = new Composite(this, SWT.NONE);
    composite.setLayoutData(gridData);
    
    GridLayout gridLayout = new GridLayout(8, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 15;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    composite.setLayout(gridLayout);
    
    wizard.createToolBarButton(composite);
    
    createStatusLabel(composite);
    
    Label lblName = new Label(composite, SWT.NONE | SWT.RIGHT);
    lblName.setFont(UIDATA.FONT_9);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    lblName.setLayoutData(gridData);
    lblName.setText(ChannelWizard.getLabel("step8.channel.name"));

    txtName = new Text(composite, SWT.BORDER | SWT.SINGLE);
    txtName.setFont(UIDATA.FONT_9);
    gridData = new GridData();
    gridData.widthHint = 250;
    txtName.setLayoutData(gridData);
    
    Label lblSpace = new Label(composite, SWT.NONE | SWT.RIGHT);
    gridData = new GridData();
    lblSpace.setLayoutData(gridData);
    lblSpace.setText("    ");
    
    createHelpButton(composite);
    createPreviousButton(composite);
    createNextButton(composite);
  }
  
  void openHelp() {
    String youtube = "http://www.youtube.com/watch?v=SskL4eADdXo";
    String video = "http://vietspider.org/video/step9.avi";
    wizard.openHelp(youtube, video);
  }
  
  @Override
  public void show() {
    wizard.setTitle(" 9/10");
    String [] startPages = wizard.getSource().getHome();
    if(startPages == null || startPages.length < 1) {
      showPrevious(wizard);
      return;
    }
    String url = browser.getUrl();
    if(url == null || !url.startsWith("http")) {
      url = startPages[0];
      browser.setUrl(url);
    }
    txtName.setText(createChannelName(startPages[0]));
    txtName.setFocus();
  }
  
  private String createChannelName(String link) {
    try {
      URL url = new URL(link);
      String home = url.getHost();
      if(home.startsWith("www")) home = home.substring(home.indexOf(".")+1);
      if(home.indexOf(".") > -1) home = home.replace('.', ' ');
      if(home.length() < 2) return "";
      char c = home.charAt(0);
      if(!Character.isUpperCase(c)) {
        home = String.valueOf(Character.toUpperCase(c)) + home.substring(1);
      }
      return home;
    } catch(Exception exp) {
      
    }
    return "";
  }
  
  @Override
  public void next() {
    wizard.getSource().setName(txtName.getText());
    showNext(wizard);
  }

  @Override
  public boolean validateNext() {
    String name = txtName.getText().trim();
    if(name.length() < 1) {
      error(ChannelWizard.getLabel("step8.error.no.name"));
      txtName.setFocus();
      return false;
    }
    return true;
  }

  public void reset() {
    browser.setUrl("about:blank");
    txtName.setText("");
  }

}
