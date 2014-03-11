package org.vietspider.gui.wizard;
import static org.vietspider.link.pattern.LinkPatternFactory.createPatterns;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.vietspider.chars.URLEncoder;
import org.vietspider.gui.creator.URLTemplateList;
import org.vietspider.link.pattern.LinkPatterns;
import org.vietspider.model.Source;
import org.vietspider.ui.htmlexplorer.URLTemplateUtils;
import org.vietspider.ui.services.ClientRM;
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
public class ChannelWizardStep6 extends ChannelWizardComposite {
  
  private Browser browser;
  
  private Text txtAddress;
  private URLTemplateList dataTemplateList;
  
  public ChannelWizardStep6(ChannelWizard wizard) {
    super(wizard);
    
    ClientRM clientRM = ChannelWizard.getResources();
    ApplicationFactory factory = 
      new ApplicationFactory(wizard, clientRM, getClass().getName());
    
  /*  Composite composite = new Composite(this, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    composite.setLayoutData(gridData);
    
    GridLayout gridLayout = new GridLayout(2, false);
    composite.setLayout(gridLayout);
    
    Label lblAddress = new Label(composite, SWT.NONE);
    gridData = new GridData();
    lblAddress.setLayoutData(gridData);
    lblAddress.setText(ChannelWizard.getLabel("step6.data.link"));*/
    
    browser = ApplicationFactory.createBrowser(this, null);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    browser.setLayoutData(gridData);
    browser.addProgressListener(new ProgressAdapter() {
      @SuppressWarnings("unused")
      public void changed(ProgressEvent event) {
        String url = browser.getUrl();
        if(url != null && url.startsWith("http")) {
          if(!txtAddress.isFocusControl() && 
              !url.equalsIgnoreCase(txtAddress.getText())) {
            txtAddress.setText(url);
          }
        }
        
        String status = lblStatus.getText().toLowerCase().trim();
        if(status.length() < 1 || status.startsWith("waiting for")) {
          lblStatus.setText("Waiting for "+ browser.getUrl() +" ...");
        }
      }
      
      @SuppressWarnings("unused")
      public void completed(ProgressEvent event) {
        clearMessage();
      }
    });
    
//    composite = new Composite(this, SWT.NONE);
//    gridData = new GridData(GridData.FILL_HORIZONTAL);
//    composite.setLayoutData(gridData);
    
//    gridLayout = new GridLayout(2, false);
//    composite.setLayout(gridLayout);
    
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;

    factory.setComposite(this);
    Group group = factory.createGroup(gridData, gridLayout);
    group.setText(ChannelWizard.getLabel("step6.data.link.template"));
    group.setFont(UIDATA.FONT_9);
    
//    Label lblTemplate = new Label(composite, SWT.NONE);
//    gridData = new GridData();
//    lblTemplate.setLayoutData(gridData);
//    lblTemplate.setText(ChannelWizard.getLabel("step6.data.link.template"));
    
//    Composite templComposite = new Composite(composite, SWT.NONE);
//    gridLayout = new GridLayout(3, false);
//    gridLayout.marginHeight = 5;
//    gridLayout.horizontalSpacing = 5;
//    gridLayout.verticalSpacing = 0;
//    gridLayout.marginWidth = 0;
//    templComposite.setLayout(gridLayout);
//    factory.setComposite(templComposite);
//    gridData = new GridData(GridData.FILL_HORIZONTAL);
//    templComposite.setLayoutData(gridData);
    factory.setComposite(group);
    dataTemplateList = new URLTemplateList(factory);
    txtAddress = dataTemplateList.getTextComponent();
    txtAddress.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if(e.keyCode == SWT.CR){
          String url = txtAddress.getText().trim();
          if(url.startsWith("http")) browser.setUrl(url);
        }
      }
    });
    gridData = new GridData(GridData.FILL_BOTH);
    dataTemplateList.setLayoutData(gridData);
    
    factory.createStyleMenuItem(dataTemplateList.getMenu(), SWT.SEPARATOR);
    
    factory.createStyleMenuItem(dataTemplateList.getMenu(), "menuUseDataLink", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String text = txtAddress.getText();
        if((text = text.trim()).isEmpty()) return;
        dataTemplateList.putText(text);
      }
    });
    
    createButton(new GridData(GridData.FILL_HORIZONTAL));
  }
  
  void openHelp() {
    String youtube = "http://www.youtube.com/watch?v=2XxqRD52HEA";
    String video = "http://vietspider.org/video/step7.avi";
    wizard.openHelp(youtube, video);
  }

  @Override
  public void show() {
    wizard.setTitle(" 7/10");
    String url = browser.getUrl();
    if(url == null || !url.startsWith("http")) {
      url = wizard.getSource().getPattern();
      browser.setUrl(url);
    }
    txtAddress.setText(url);
    txtAddress.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
    if(dataTemplateList.getItemCount() < 1) {
      String template = URLTemplateUtils.handle(url);
      if(template != null && (
          template.indexOf('@') > -1 
          ||template.indexOf('*') > -1)
          || template.indexOf('$') > -1) {
        dataTemplateList.addItems(new String[]{template});
      }
    }
  }
  
  @Override
  public void next() {
    String template = dataTemplateList.getValue();
    wizard.getSource().getProperties().setProperty("URLPattern", template);
    showNext(wizard);
  }

  @Override
  public boolean validateNext() {
    if(dataTemplateList.getItemCount() < 1) return true;
    
    String link = txtAddress.getText().trim();
    if(link.isEmpty()) {
      link = browser.getUrl();
      if(link != null && !link.startsWith("http"))  link = null;
    }
    
    if(link != null && link.length() > 0) {
      LinkPatterns dataPatterns = createPatterns(LinkPatterns.class, dataTemplateList.getItems());
      txtAddress.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
      if(dataPatterns != null) {
        URLEncoder encoder = new URLEncoder();
        if(!dataPatterns.match(link) && !dataPatterns.match(encoder.encode(link))) {
          error(ChannelWizard.getLabel("step6.error.pattern"));
          
          MessageBox msg = new MessageBox (getShell(), SWT.APPLICATION_MODAL | SWT.OK);
          msg.setMessage(ChannelWizard.getLabel("step6.error.pattern"));
          msg.open();
        } 
      } 
    }
    return true;
  }

  @Override
  public void previous() {
    Source source = wizard.getSource();
    if(source.getGroup().equals("XML")) {
      showPrevious(wizard);  
    } else {
      showPrevious(wizard, 3);
    }
  }

  public void reset() {
    browser.setUrl("about:blank");
    dataTemplateList.clearAll();
    txtAddress.setText("");
  }
  

}
