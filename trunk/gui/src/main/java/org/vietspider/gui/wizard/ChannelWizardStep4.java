package org.vietspider.gui.wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
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
public class ChannelWizardStep4 extends ChannelWizardComposite {
  
  private Browser browser;
  
  protected Text inputNameText;
  protected List tagNameList;
  
  public ChannelWizardStep4(ChannelWizard wizard) {
    super(wizard);
    
    ClientRM clientRM = ChannelWizard.getResources();
    ApplicationFactory factory = 
      new ApplicationFactory(wizard, clientRM, getClass().getName());
    
    SashForm mainSash = new SashForm(this, SWT.HORIZONTAL);
    mainSash.setBackground(getBackground());
    GridData gridData = new GridData(GridData.FILL_BOTH);
    mainSash.setLayoutData(gridData);

    browser = ApplicationFactory.createBrowser(mainSash, null);
    gridData = new GridData(GridData.FILL_BOTH);
    browser.setLayoutData(gridData);
    
    Composite right = new Composite(mainSash, SWT.NONE);
    gridData = new GridData(GridData.FILL_VERTICAL);
    right.setLayoutData(gridData);

    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginHeight = 1;
    gridLayout.horizontalSpacing = 2;
    gridLayout.verticalSpacing = 2;
    gridLayout.marginWidth = 1;
    right.setLayout(gridLayout);

    inputNameText = new Text(right, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL);
    inputNameText.setFont(UIDATA.FONT_9);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    inputNameText.setLayoutData(gridData);
    inputNameText.setFont(UIDATA.FONT_10B);
    inputNameText.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent event) {
        if (event.keyCode == SWT.CR) addTagName();
      }
    });
    inputNameText.setToolTipText(ChannelWizard.getLabel("step4.add.name.tip"));
    
    Button button = new Button(right, SWT.ARROW | SWT.DOWN);
    button.setToolTipText(ChannelWizard.getLabel("step4.add.name.tip"));
    button.addSelectionListener(new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        addTagName();
      }
      
    });
    gridData = new GridData();
    gridData.horizontalAlignment = SWT.CENTER;
    button.setLayoutData(gridData);

    tagNameList = new List(right, SWT.BORDER | SWT.V_SCROLL);
    tagNameList.setFont(UIDATA.FONT_9);
    gridData = new GridData(GridData.FILL_BOTH);
    tagNameList.setLayoutData(gridData);
    tagNameList.setFont(UIDATA.FONT_10B);
    Menu nameMenu = new Menu(getShell(), 8);
    tagNameList.setMenu(nameMenu);
    tagNameList.setFocus();
    
    //for test
//    tagNameList.setItems(new String[]{"tag_1", "tag_2"});

    factory.createStyleMenuItem(nameMenu, "menuRenameRegion", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int idx = tagNameList.getSelectionIndex();
        if(idx < 0) return;
        new RenameTagDialog(getShell(), tagNameList);
      }
    });
    factory.createStyleMenuItem(nameMenu, "menuUpRegion", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int idx = tagNameList.getSelectionIndex();
        if(idx < 1) return;
        String current = tagNameList.getItem(idx);
        String previous = tagNameList.getItem(idx - 1);
        tagNameList.setItem(idx - 1, current);
        tagNameList.setItem(idx, previous);
      }
    });
    factory.createStyleMenuItem(nameMenu, "menuDownRegion", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int idx = tagNameList.getSelectionIndex();
        if(idx >= tagNameList.getItemCount() - 1) return;
        String current = tagNameList.getItem(idx);
        String next = tagNameList.getItem(idx + 1);
        tagNameList.setItem(idx + 1, current);
        tagNameList.setItem(idx, next);
      }
    });
    factory.createStyleMenuItem(nameMenu, "menuRemoveRegion", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        int idx = tagNameList.getSelectionIndex();
        if(idx < 0) return;
        tagNameList.remove(idx);
      }
    });
    
    mainSash.setWeights(new int[] {80, 20 });
    
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    createButton(gridData);
  }
  
  @Override
  void openHelp() {
    String youtube = "http://www.youtube.com/watch?v=DQhciV0Hzzg";
    String video = "http://vietspider.org/video/step5.avi";
    wizard.openHelp(youtube, video);
  }
  
  public void show() {
    wizard.setTitle(" 5/10");
    String html = wizard.getTemp("extract.doc");
    if(html != null) browser.setText(html);
  }
  
  
  @Override
  public void next() {
    wizard.putTemp("tag.name", tagNameList.getItems());
    showNext(wizard);
  }
  
  public boolean validateNext() {
    if(tagNameList.getItemCount() < 1) {
      error(ChannelWizard.getLabel("step4.no.tag.name"));
      inputNameText.setFocus();
      return false;
    }
    return true;
  }

  private void addTagName() {
    String name = inputNameText.getText();
    if(name == null || (name = name.trim()).isEmpty()) return;
    name = name.replace(' ', '_');
    tagNameList.add(name);
    inputNameText.setText("");
  }

  public void reset() {
    browser.setUrl("about:blank");
    inputNameText.setText("");
  }
}
