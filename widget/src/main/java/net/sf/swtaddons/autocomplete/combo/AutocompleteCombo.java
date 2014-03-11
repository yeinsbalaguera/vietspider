package net.sf.swtaddons.autocomplete.combo;

import net.sf.swtaddons.autocomplete.AutocompleteWidget;

import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Combo;

public abstract class AutocompleteCombo extends AutocompleteWidget {
	
	private final class ProposalUpdateFocusListener implements FocusListener {
		public void focusGained(FocusEvent e) {
			provider.setProposals(combo.getItems());
		}

		public void focusLost(FocusEvent e) {
			// do nothing
		}
	}

	protected Combo combo = null;
	
	public AutocompleteCombo(Combo aCombo) {
		this.combo = aCombo;
		
		if (combo != null) {
			this.combo.addFocusListener(new ProposalUpdateFocusListener());
		
			provider = getContentProposalProvider(combo.getItems());
			adapter = new ContentProposalAdapter(combo, new ComboContentAdapter(), provider, getActivationKeystroke(), getAutoactivationChars());
			adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		}
	}

}
