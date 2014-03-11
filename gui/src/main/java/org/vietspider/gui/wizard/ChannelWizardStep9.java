package org.vietspider.gui.wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.Creator;
import org.vietspider.model.Source;
import org.vietspider.ui.widget.UIDATA;

/***************************************************************************
 * Copyright 2001-2011 ArcSight, Inc. All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          thuannd2@fsoft.com.vn
 * Dec 30, 2011  
 */
public class ChannelWizardStep9 extends ChannelWizardComposite {
  
  private Button saveCrawlButton;
  private Button testButton;
  private Creator creator;
  
  public ChannelWizardStep9(ChannelWizard wizard) {
    super(wizard, null);
    
    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);
    
    creator = new Creator(this, wizard.getWorkspace());
    GridData gridData = new GridData(GridData.FILL_BOTH);
    creator.setLayoutData(gridData);
    wizard.putTemp("creator", creator);
    
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    createButton(gridData);
  }
  
  @Override
  public void show() {
    wizard.setTitle(" 10/10");
    String group = wizard.getSource().getGroup();
    String category = wizard.getSource().getCategory();
    creator.setSource(wizard.getSource());
    creator.selectGroup(group);
    creator.selectCategory(category);
    creator.selectData(new Worker[0], group, category);
//    new LoadGroupCategorySource(
//        creator.getListSources(), new Worker[0], group, category);
    creator.setEditMode(false);
  }

  protected void createButton(GridData gridData) {
    Composite composite = new Composite(this, SWT.NONE);
    composite.setLayoutData(gridData);
    
    GridLayout gridLayout = new GridLayout(6, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 15;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    composite.setLayout(gridLayout);
    
    createStatusLabel(composite);
    createHelpButton(composite);
    createPreviousButton(composite);
    
    testButton = new Button(composite, SWT.PUSH);
    testButton.setFont(UIDATA.FONT_9);
    testButton.setText("Test");
    testButton.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        creator.getInfoControl().getSourceEditor().test();
      }
    });
    gridData = new GridData();
    testButton.setLayoutData(gridData);
    
    butNext = new Button(composite, SWT.PUSH);
    butNext.setFont(UIDATA.FONT_9);
    butNext.setText(ChannelWizard.getLabel("done"));
    butNext.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        Source source = creator.getInfoControl().getSourceEditor().createSource();
        wizard.save(source, false);
      }
    });
    gridData = new GridData();
    butNext.setLayoutData(gridData);
    
    saveCrawlButton = new Button(composite, SWT.PUSH);
    saveCrawlButton.setFont(UIDATA.FONT_9);
    saveCrawlButton.setText(ChannelWizard.getLabel("done.crawl"));
    saveCrawlButton.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        Source source = creator.getInfoControl().getSourceEditor().createSource();
        wizard.save(source, true);
      }
    });
    gridData = new GridData();
    saveCrawlButton.setLayoutData(gridData);
  }
  
  void openHelp() {
    String youtube = "http://www.youtube.com/watch?v=y6up8i8vJdQ";
    String video = "http://vietspider.org/video/step10.avi";
    wizard.openHelp(youtube, video);
  }
  
  public void reset() {
    creator.getInfoControl().getSourceEditor().reset();
  }
}
