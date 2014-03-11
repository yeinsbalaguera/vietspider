package org.vietspider.content.joomla;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.vietspider.common.io.LicenseVerifier;
import org.vietspider.model.plugin.joomla.JoomlaSyncData;
import org.vietspider.model.plugin.joomla.XMLJoomlaConfig.Category;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.UIDATA;

public class SingleArticleSelector extends Composite {

  private String articleID;
  private Category[] categories;

  private Button butTitle;
  private Button butPublished;
  private Button butFeatured;
  private Combo cboCategory;

  private String defaultCategory;

  private boolean isShowMessage = true;

  private Section section;
  private int index;

  // private Text txtTitle;

  public SingleArticleSelector(Composite parent, int index) {
    super(parent, SWT.NONE);

    this.index = index;

    GridLayout gridLayout = new GridLayout(1, false);
    setLayout(gridLayout);

    GridData gridData;

    section = new Section(this, Section.TWISTIE  | Section.EXPANDED);

    gridData = new GridData(GridData.FILL_HORIZONTAL);
    section.setLayoutData(gridData);
    section.setFont(UIDATA.FONT_8TB);

    gridData = new GridData(GridData.FILL_BOTH);
    gridLayout = new GridLayout(1, false);

    Composite group = new Composite(section, SWT.NONE);
    group.setLayoutData(gridData);
    group.setLayout(gridLayout);
    section.setClient(group);

    butTitle = new Button(section, SWT.CHECK);
    butTitle.setSelection(true);
    gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END);
    gridData.horizontalSpan = 4;
    butTitle.setLayoutData(gridData);
    butTitle.setToolTipText("Post to Joomla?");
    butTitle.setFont(UIDATA.FONT_8TB);
    section.setTextClient(butTitle);

    Composite optionComposite = new Composite(group, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    optionComposite.setLayoutData(gridData);

    gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    gridLayout.marginHeight = 1;
    gridLayout.horizontalSpacing = 5;
    gridLayout.verticalSpacing = 5;
    gridLayout.marginWidth = 2;
    optionComposite.setLayout(gridLayout);
    
    File licenseFile = LicenseVerifier.loadLicenseFile();
    boolean license = LicenseVerifier.verify("joomla", licenseFile);

    ClientRM resources = new ClientRM("JoomlaSyncArticle");
    butPublished = new Button(optionComposite, SWT.CHECK);
    butPublished.setText(resources.getLabel("published"));
    butPublished.setFont(UIDATA.FONT_9);
    butPublished.setEnabled(license);

    butFeatured = new Button(optionComposite, SWT.CHECK);
    butFeatured.setText(resources.getLabel("featured"));
    butFeatured.setFont(UIDATA.FONT_9);
    butFeatured.setEnabled(license);

    cboCategory = new Combo(optionComposite, SWT.BORDER);
    cboCategory.setFont(UIDATA.FONT_9);
    gridData = new GridData();
    gridData.widthHint = 150;
    cboCategory.setLayoutData(gridData);
    cboCategory.addSelectionListener(new SelectionAdapter() {
      @Override
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        File licenseFile_ = LicenseVerifier.loadLicenseFile();
        boolean license_ = LicenseVerifier.verify("joomla", licenseFile_);
        if(license_) return;
        int index_ = cboCategory.getSelectionIndex();
        int max = 10;
        if(index_ < max) return; 
        Exception exp = new Exception("Free license: Only select from the first to 10th category!");
        ClientLog.getInstance().setMessage(cboCategory.getShell(), exp);
        while(cboCategory.getItemCount() > max) {
          cboCategory.remove(cboCategory.getItemCount()-1);
        }
        if(cboCategory.getItemCount() > 0) cboCategory.select(0);
      }

    });

  }

  void setArticle(String id, String title, String category) {
    this.articleID = id;
    section.setText(String.valueOf(index)+". " + title);
    section.setExpanded(false);
    butTitle.setEnabled(id != null && id.trim().length() > 0);
    butTitle.setSelection(true);

    cboCategory.setEnabled(id != null && id.trim().length() > 0);
    butFeatured.setSelection(true);
    butFeatured.setEnabled(id != null && id.trim().length() > 0);
    butPublished.setSelection(true);
    butPublished.setEnabled(id != null && id.trim().length() > 0);

    defaultCategory = category;

    selectDefaultCategory();

    section.setTitleBarForeground(new Color(getDisplay(), 0, 0, 0));
    section.setExpanded(true);
  }

  private void selectDefaultCategory() {
    for(int i = 0; i < cboCategory.getItemCount(); i++) {
      if(cboCategory.getItem(i).equalsIgnoreCase(defaultCategory)) {
        cboCategory.select(i);
        return;
      }
    }

    String lowCate = defaultCategory.toLowerCase();
    for(int i = 0; i < cboCategory.getItemCount(); i++) {
      String lower =  cboCategory.getItem(i).toLowerCase();
      if(lower.startsWith(lowCate) || lowCate.startsWith(lower)) {
        cboCategory.select(i);
        return;
      }
    }

    if (cboCategory.getItemCount() > 0) cboCategory.select(0);
  }

  public void setCategories(Category[] categories) {
    this.categories = categories;

    String[] items = new String[categories.length];
    for (int i = 0; i < categories.length; i++) {
      items[i] = categories[i].getCategoryName();
    }
    cboCategory.setItems(items);
    selectDefaultCategory();
  }


  public boolean enableSelection() {  return butTitle.isEnabled(); }

  public JoomlaSyncData getSyncData() {
    if(!butTitle.isEnabled()) return null;
    if(!butTitle.getSelection()) return null;
    int idx = cboCategory.getSelectionIndex();
    if(idx < 0) return null;
    JoomlaSyncData joomlaSyncData = new JoomlaSyncData();
    joomlaSyncData.setArticleId(articleID);
//    joomlaSyncData.setSectionId(categories[idx].getSectionId());
    joomlaSyncData.setCategoryId(categories[idx].getCategoryId());
    joomlaSyncData.setPublished(butPublished.getSelection());
    joomlaSyncData.setFeatured(butFeatured.getSelection());

    joomlaSyncData.setShowMessage(isShowMessage);

    butTitle.setEnabled(false);
    return joomlaSyncData;
  }

  void setShowMessage(boolean isShowMessage) {
    this.isShowMessage = isShowMessage;
  }
}
