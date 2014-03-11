package org.vietspider.gui.wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.vietspider.ui.widget.UIDATA;

/***************************************************************************
 * Copyright 2001-2011 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Dec 30, 2011  
 */
public abstract class ChannelWizardComposite extends Composite implements IChannelWizardStep {
  
  protected Button butNext;
  protected Button butHelp;
  protected Button butPrevious;
  protected Label lblStatus;
  
  protected ChannelWizard wizard;
  
  public ChannelWizardComposite(ChannelWizard _wizard) {
    this(_wizard, null);
    
    GridLayout gridLayout = new GridLayout(1, true);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);
  }
  
  @SuppressWarnings("unused")  
  public ChannelWizardComposite(ChannelWizard _wizard, GridLayout gridLayout) {
    super(_wizard, SWT.NONE);
    
    this.wizard = _wizard;
  }
  
  protected void createButton(GridData gridData) {
    Composite composite = new Composite(this, SWT.NONE);
    composite.setLayoutData(gridData);
    
    GridLayout gridLayout = new GridLayout(5, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 15;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    composite.setLayout(gridLayout);
    
    wizard.createToolBarButton(composite);
    
    createStatusLabel(composite);
    createHelpButton(composite);
    createPreviousButton(composite);
    createNextButton(composite);
  }
  
  protected void createHelpButton(Composite composite) {
    butHelp = new Button(composite, SWT.PUSH);
    butHelp.setFont(UIDATA.FONT_9);
    butHelp.setText("Help");//ChannelWizard.getLabel("previous"));
    butHelp.addSelectionListener( new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        openHelp();
      }
    });
    GridData gridData = new GridData();
    butHelp.setLayoutData(gridData);
  }

  protected void createNextButton(Composite composite) {
    butNext = new Button(composite, SWT.PUSH);
    butNext.setFont(UIDATA.FONT_9);
    butNext.setText(ChannelWizard.getLabel("next"));
    butNext.addSelectionListener(new NextCompositeSelectionAdapter());
    GridData gridData = new GridData();
    butNext.setLayoutData(gridData);
  }

  protected void createPreviousButton(Composite composite) {
    butPrevious = new Button(composite, SWT.PUSH);
    butPrevious.setFont(UIDATA.FONT_9);
    butPrevious.setText(ChannelWizard.getLabel("previous"));
    butPrevious.addSelectionListener(new PreviousCompositeSelectionAdapter());
    GridData gridData = new GridData();
    butPrevious.setLayoutData(gridData);
  }

  protected void createStatusLabel(Composite composite) {
    lblStatus = new Label(composite, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    lblStatus.setLayoutData(gridData);
    lblStatus.setFont(UIDATA.FONT_9);
  }
  
  protected class NextCompositeSelectionAdapter extends SelectionAdapter {
    
    protected NextCompositeSelectionAdapter() {
    }

    @SuppressWarnings("unused")
    public void widgetSelected(SelectionEvent event) {
      if(!validateNext()) return;
      next();
    }
  }
  
  protected class PreviousCompositeSelectionAdapter extends SelectionAdapter {

    protected PreviousCompositeSelectionAdapter() {
    }

    @SuppressWarnings("unused")
    public void widgetSelected(SelectionEvent event) {
      if(!validatePrevious()) return;
      previous();
    }
  }
  
  public void error(String error) {
    lblStatus.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
    lblStatus.setText(error);
  }
  
  public void information(String information) {
    lblStatus.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
    lblStatus.setText(information);
  }
  
  public void clearMessage() {
    lblStatus.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
    lblStatus.setText("");
  }
  
  public boolean validateNext() { return true; }
  public boolean validatePrevious() { return true; }
  
  public void show() {
    
  }
  
  public void next() { showNext(wizard); }
  public void previous() { showPrevious(wizard); }
  
  public static void showNext(ChannelWizard wizard) {
    showNext(wizard, 1);
  }
  
  public static void showNext(ChannelWizard wizard, int step) {
    Control control = wizard.getLayout().topControl;
    Control[] children = wizard.getChildren();
    int i = 0;
    for (int n = children.length; i < n; i++) {
      Control child = children[i];
      if (child == control) break;
    }
    i += step;
    if (i >= children.length) i = children.length-1;
    wizard.getLayout().topControl = children[i];
    ((IChannelWizardStep)children[i]).show();
    wizard.layout();
  }
  
  public static void showPrevious(ChannelWizard wizard) {
    showPrevious(wizard, 1);
  }
  
  public static void showPrevious(ChannelWizard wizard, int step) {
    Control control = wizard.getLayout().topControl;
    Control[] children = wizard.getChildren();
    int i = children.length - 1;
    for (int n = 0; i >= n; i--) {
      Control child = children[i];
      if (child == control) break;
    }
    i -= step;
    if (i < 0) i = 0;
    wizard.getLayout().topControl = children[i];
    ((IChannelWizardStep)children[i]).show();
    wizard.layout();
  }
  
  abstract void openHelp();
  

}
