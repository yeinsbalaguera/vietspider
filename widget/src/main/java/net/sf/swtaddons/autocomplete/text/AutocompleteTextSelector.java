package net.sf.swtaddons.autocomplete.text;

import net.sf.swtaddons.autocomplete.ViewerContentProposalProvider;
import net.sf.swtaddons.autocomplete.ViewerSelectorContentProposalProvider;

import org.eclipse.swt.widgets.Text;

public class AutocompleteTextSelector extends AutocompleteText {

	public AutocompleteTextSelector(Text text, String[] selectionItems) {
		super(text, selectionItems);
	}

	protected ViewerContentProposalProvider getContentProposalProvider(String[] proposals) {
		return new ViewerSelectorContentProposalProvider(proposals, this.text);
	}

}
