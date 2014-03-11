package net.sf.swtaddons.autocomplete.text;

import net.sf.swtaddons.autocomplete.ViewerContentProposalProvider;
import net.sf.swtaddons.autocomplete.ViewerInputContentProposalProvider;

import org.eclipse.swt.widgets.Text;

public class AutocompleteTextInput extends AutocompleteText {
	
	public AutocompleteTextInput(Text text, String[] selectionItems) {
		super(text, selectionItems);
	}

	protected ViewerContentProposalProvider getContentProposalProvider(String[] proposals) {
		return new ViewerInputContentProposalProvider(proposals);
	}

}
