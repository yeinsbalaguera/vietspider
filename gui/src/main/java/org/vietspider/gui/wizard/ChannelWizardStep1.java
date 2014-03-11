package org.vietspider.gui.wizard;
import java.io.File;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.client.common.source.SourcesClientHandler;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.gui.creator.SourceEditor;
import org.vietspider.gui.creator.SourceEditorUtil;
import org.vietspider.gui.web.FastWebClient2;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;
import org.vietspider.ui.widget.waiter.WaitLoading;
import org.vietspider.user.AccessChecker;

/***************************************************************************
 * Copyright 2001-2011 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Dec 30, 2011  
 */
public class ChannelWizardStep1 extends ChannelWizardComposite {
  
  private Text txtAddress;
  private Browser browser;
  private String host = null;
  
  public ChannelWizardStep1(ChannelWizard wizard) {
    super(wizard);
    
    Composite composite = new Composite(this, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    composite.setLayoutData(gridData);
    
    GridLayout gridLayout = new GridLayout(2, false);
    composite.setLayout(gridLayout);
    
    Label lblAddress = new Label(composite, SWT.NONE);
    gridData = new GridData();
    lblAddress.setLayoutData(gridData);
    lblAddress.setText(ChannelWizard.getLabel("step1.address"));
    
    txtAddress = new Text(composite, SWT.BORDER);
    txtAddress.addModifyListener(new ModifyListener() {
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent arg0) {
        String link = txtAddress.getText().trim();
        if(link.length() < 1 || link.indexOf('.') < 0) return;
        try {
          URL url = new URL(link);
          if(!url.getHost().equalsIgnoreCase(host)) {
            host = url.getHost();
            LoadGroup worker = new LoadGroup(new LoadGroup.Handler(){
              public void showError(String error) {
                error(error);
              }
              public void handle(AccessChecker accessChecker, String[] groups, File file) {
                checkURL(accessChecker, groups, 0);
              }
            });
            ThreadExecutor waitLoading = new ThreadExecutor(worker, txtAddress);
            waitLoading.start();
          }
        } catch (Exception e) {
        }
      }
    });
    SourceEditorUtil.setDropTarget(txtAddress);
    txtAddress.setFont(UIDATA.FONT_9);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    txtAddress.setLayoutData(gridData);
    txtAddress.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if(e.keyCode == SWT.CR) {
          try {
            browser.setUrl(txtAddress.getText().trim());
          } catch (Exception exp) {
            browser.setText("<html><p>" + exp.toString() + "</p></html>");
          }
        }
      }
    });
    
    browser = ApplicationFactory.createBrowser(this, null);
    gridData = new GridData(GridData.FILL_BOTH);
    browser.setLayoutData(gridData);
    browser.addProgressListener(new ProgressAdapter() {
      @SuppressWarnings("unused")
      public void changed(ProgressEvent event) {
        String url = browser.getUrl();
        if(url != null && url.startsWith("http")) {
          if(!url.equalsIgnoreCase(txtAddress.getText())) {
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
    
    createButton(new GridData(GridData.FILL_HORIZONTAL));
    
    //for test
//    txtAddress.setText("http://nik.vn/tin/tonghoptin/detail/201112302244570000/Hang--cho--dua-nhau-vao-hoi-cho-khuyen-mai/");
//    browser.setUrl("http://nik.vn/tin/tonghoptin/detail/201112302244570000/Hang--cho--dua-nhau-vao-hoi-cho-khuyen-mai/");
  }
  
  protected void createButton(GridData gridData) {
    Composite composite = new Composite(this, SWT.NONE);
    composite.setLayoutData(gridData);
    
    GridLayout gridLayout = new GridLayout(4, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 15;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    composite.setLayout(gridLayout);
    
    wizard.createToolBarButton(composite);
    
    createStatusLabel(composite);
    createHelpButton(composite);
    createNextButton(composite);
  }
  
  @Override
  void openHelp() {
    String youtube = "http://www.youtube.com/watch?v=ThSS0krhcKA";
    String video = "http://vietspider.org/video/step1.avi";
    wizard.openHelp(youtube, video);
  }

  public boolean validateNext() {
    String url = browser.getUrl();
    if((url == null || url.trim().equals("about:blank"))
        && txtAddress.getText().trim().length() > 0) {
      browser.setUrl(txtAddress.getText().trim());
      return false;
    }
    
    if(url == null || !url.startsWith("http")) {
      error(ChannelWizard.getLabel("step1.error.no.url"));
      txtAddress.setFocus();
      return false;
    }
    
    clearMessage();
    return true; 
  }
  
  public void show() {
    wizard.setTitle(" 1/10");
  }
  
  @Override
  public void next() {
    final String url = browser.getUrl();
 
    Worker excutor = new Worker(new Worker[0]) {
      
      String message = null;

      public void abort() {
        Creator creator = wizard.getTemp("creator");
        SourceEditor editor = creator.getInfoControl().getSourceEditor();
        FastWebClient2 webClient = (FastWebClient2)editor.getWebClient();
        webClient.abort(url);
      }

      public void before() {
      }

      public void execute() {
        try {
          wizard.loadDataHTML(url);
        } catch (Exception e) {
          e.printStackTrace();
          ClientLog.getInstance().setThrowable(null, e);
          this.message = e.toString();
        }
      }

      public void after() {
        if(message != null && message.trim().length() > 0) {
          error(message);
          return;
        }
       
        wizard.getSource().setPattern(url);
        byte[] bytes = wizard.getTemp("page.data.content");
        if(bytes == null || bytes.length < 10) {
          error("step1.no.data");
          return;
        }
        showNext(wizard);
      }
    };
    WaitLoading waitLoading = new WaitLoading(this, excutor);
    waitLoading.open();
  }
  
  private void checkURL(final AccessChecker accessChecker, 
                        final String[] groups, final int index) {
    if(index >= groups.length) return;
    if(groups[index].equals("DUSTBIN")) return;
    if(accessChecker != null && !accessChecker.isPermitGroup(groups[index])) return;
    
    Worker excutor = new Worker() {
      
      private String link;
      private String savedUrl;
      private int type = 0;
      private String [] elements ;
      
      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        link = txtAddress.getText();
      }

      public void execute() {
        SourcesClientHandler handler = null;
        try {
          handler = new SourcesClientHandler(groups[index]);
        } catch (Exception e) {
          type = -1;
          savedUrl = e.toString();
        }
        if(handler == null) return;
        
        if(link == null || link.trim().isEmpty()) return;
        try {
          elements = handler.searchSourceByURL(link);
          if(elements != null 
              && elements.length > 0 
              && !elements[0].trim().isEmpty()) {
            type = 1;
            savedUrl = link;
          } else {
            elements = handler.searchSourceByHost(new URL(link).getHost());
            if(elements != null 
                && elements.length > 0 
                && !elements[0].trim().isEmpty()) {
              type  = 2;
              savedUrl = link;
            }
          }
        } catch (Exception e) {
          type = -1;
          savedUrl = e.toString();
        }
      }

      public void after() {
        if(type == -1) {
          error(savedUrl);
          return;
        }

        if(type == 0) {
          clearMessage();
          checkURL(accessChecker, groups, index+1);
          return;
        }
      
        String appendLabel = new ClientRM("Creator").getLabel("existURL");
        
        if(type == 1) {
          error(savedUrl + " " + appendLabel + " \""+ elements[0].trim() + "\"");
        } else if(type == 2) {
          try {
            error(new URL(savedUrl).getHost()+ " " + appendLabel + " \""+ elements[0].trim() + "\"");
          } catch (Exception e) {
            error(savedUrl + " " + appendLabel + " \""+ elements[0].trim() + "\"");
          }
        }
      }
    };
    new ThreadExecutor(excutor, txtAddress).start();
  }
  
  public void reset() {
    txtAddress.setText("");
    browser.setUrl("about:blank");
  }
}
