package net.sf.swtaddons.autocomplete.combo;

import net.sf.swtaddons.autocomplete.ViewerContentProposalProvider;
import net.sf.swtaddons.autocomplete.ViewerInputContentProposalProvider;

import org.eclipse.swt.widgets.Combo;

public class AutocompleteComboInput extends AutocompleteCombo {
	
	public AutocompleteComboInput(Combo combo) {
		super(combo);
	}

	protected ViewerContentProposalProvider getContentProposalProvider(String[] proposals) {
		return new ViewerInputContentProposalProvider(proposals);
	}

}
