/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.smt.provers.internal.ui;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

import br.ufrn.smt.solver.preferences.SMTPreferences;
import br.ufrn.smt.solver.preferences.ui.SMTPreferencePage;
import fr.systerel.smt.provers.core.SMTProversCore;

public class SMTPP extends DefaultTacticProvider implements ITacticProvider {

	public class SMTPPApplication extends DefaultPredicateApplication {

		@Override
		public ITactic getTactic(final String[] inputs, final String globalInput) {
			try {
				final SMTPreferences smtPreferences = SMTPreferencePage
						.getSMTPreferencesForPP();

				return SMTProversCore
						.externalSMTThroughPP(smtPreferences, true);
			} catch (final PatternSyntaxException pse) {
				pse.printStackTrace(System.err);
				return SMTProversCore.smtSolverError();
			} catch (final IllegalArgumentException iae) {
				if (iae.equals(SMTPreferences.NoSMTSolverSelectedException)) {
					return SMTProversCore.noSMTSolverSelected();
				} else if (iae.equals(SMTPreferences.NoSMTSolverSetException)) {
					return SMTProversCore.noSMTSolverSet();
				} else {
					return SMTProversCore.smtSolverError();
				}
			}
		}
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			final IProofTreeNode node, final Predicate hyp,
			final String globalInput) {
		if (node != null && node.isOpen()) {
			final ITacticApplication appli = new SMTPPApplication();
			return singletonList(appli);
		}
		return emptyList();
	}
}
