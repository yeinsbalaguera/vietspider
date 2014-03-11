package org.vietspider.content.bds;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.vietspider.model.plugin.bds.BdsSyncData;
import org.vietspider.model.plugin.bds.XMLBdsConfig.Category;
import org.vietspider.model.plugin.bds.XMLBdsConfig.Region;
import org.vietspider.ui.widget.UIDATA;

public class SingleArticleSelector extends Composite {

  private String articleID;
  private Category[] categories;
  private Region[] regions;

  private Button butTitle;
  private Combo cboCategory;
  private Combo cboRegion;

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
    butTitle.setToolTipText("Đăng tin?");
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
    
//    File licenseFile = LicenseVerifier.loadLicenseFile();
//    boolean license = LicenseVerifier.verify("joomla", licenseFile);

    cboCategory = new Combo(optionComposite, SWT.BORDER);
    cboCategory.setFont(UIDATA.FONT_9);
    gridData = new GridData();
    gridData.widthHint = 150;
    cboCategory.setLayoutData(gridData);   
    
    cboRegion = new Combo(optionComposite, SWT.BORDER);
    cboRegion.setFont(UIDATA.FONT_9);
    gridData = new GridData();
    gridData.widthHint = 150;
    cboRegion.setLayoutData(gridData);   
  }

  void setArticle(String id, String title, String category) {
    this.articleID = id;
    section.setText(String.valueOf(index)+". " + title);
    section.setExpanded(false);
    butTitle.setEnabled(id != null && id.trim().length() > 0);
    butTitle.setSelection(true);

    cboCategory.setEnabled(id != null && id.trim().length() > 0);

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

  public void setCategories(Category[] categories, Region[] regions) {
    this.categories = categories;
    this.regions = regions;

    String[] items = new String[categories.length];
    for (int i = 0; i < categories.length; i++) {
      items[i] = categories[i].getCategoryName();
    }
    cboCategory.setItems(items);
    selectDefaultCategory();
    
    items = new String[regions.length];
    for (int i = 0; i < regions.length; i++) {
      items[i] = regions[i].getRegionName();
    }
    cboRegion.setItems(items);
    
    if (cboRegion.getItemCount() > 0) cboRegion.select(0);
  }

  public boolean enableSelection() {  return butTitle.isEnabled(); }

  public BdsSyncData getSyncData() {
    if(!butTitle.isEnabled()) return null;
    if(!butTitle.getSelection()) return null;
    
    BdsSyncData syncData = new BdsSyncData();
    syncData.setArticleId(articleID);
    
    int idx = cboCategory.getSelectionIndex();
    if(idx < 0) return null;
    syncData.setCategoryId(categories[idx].getCategoryId());
    
    idx = cboRegion.getSelectionIndex();
    if(idx < 0) return null;
    syncData.setRegionId(regions[idx].getRegionId());

    syncData.setShowMessage(isShowMessage);

    butTitle.setEnabled(false);
    return syncData;
  }

  void setShowMessage(boolean isShowMessage) {
    this.isShowMessage = isShowMessage;
  }
  
  
  
}
