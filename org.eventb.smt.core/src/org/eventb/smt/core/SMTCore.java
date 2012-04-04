/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core;

import static org.eventb.core.seqprover.tactics.BasicTactics.composeUntilSuccess;
import static org.eventb.core.seqprover.tactics.BasicTactics.failTac;
import static org.eventb.core.seqprover.tactics.BasicTactics.reasonerTac;
import static org.eventb.smt.core.internal.provers.SMTProversCore.ALL_SOLVER_CONFIGURATIONS;
import static org.eventb.smt.core.internal.provers.SMTProversCore.NO_SOLVER_CONFIGURATION_ERROR;
import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;

import java.util.List;

import org.eventb.core.seqprover.ITactic;
import org.eventb.smt.core.internal.provers.ExternalSMTThroughPP;
import org.eventb.smt.core.internal.provers.ExternalSMTThroughVeriT;
import org.eventb.smt.core.internal.provers.SMTInput;
import org.eventb.smt.core.preferences.ISolverConfig;

/**
 * @author Systerel (yguyot)
 * 
 */
public class SMTCore {
	/**
	 * The plug-in identifier
	 */
	public static final String PLUGIN_ID = "org.eventb.smt.core";

	/**
	 * This tactic should be called by the parameterised auto tactic.
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param configId
	 *            the selected solver id
	 * @return the SMT tactic
	 */
	public static ITactic externalSMTThroughPP(boolean restricted,
			final String configId) {
		if (configId.isEmpty() || configId.equals(ALL_SOLVER_CONFIGURATIONS)) {
			final List<ISolverConfig> enabledConfigs = getPreferenceManager()
					.getSolverConfigsPrefs().getEnabledConfigs();
			if (enabledConfigs != null && !enabledConfigs.isEmpty()) {
				final int nbSolverConfigs = enabledConfigs.size();
				final ITactic smtTactics[] = new ITactic[nbSolverConfigs];
				for (int i = 0; i < nbSolverConfigs; i++) {
					smtTactics[i] = reasonerTac(new ExternalSMTThroughPP(),
							new SMTInput(restricted, enabledConfigs.get(i)));
				}
				return composeUntilSuccess(smtTactics);
			} else {
				return failTac(NO_SOLVER_CONFIGURATION_ERROR);
			}
		} else {
			final ISolverConfig config = getPreferenceManager()
					.getSolverConfigsPrefs().getSolverConfig(configId);
			return reasonerTac(new ExternalSMTThroughPP(), //
					new SMTInput(restricted, config));
		}
	}

	/**
	 * <p>
	 * Returns a tactic for applying the SMT prover to a proof tree node
	 * (sequent), translated using ppTrans. This is a convenience method, fully
	 * equivalent to:
	 * 
	 * <pre>
	 * externalSMTThroughPP(forces, DEFAULT_DELAY)
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @return a tactic for running SMTTacticProvider with the given forces
	 */
	public static ITactic externalSMTThroughPP(final boolean restricted) {
		return externalSMTThroughPP(restricted, ALL_SOLVER_CONFIGURATIONS);
	}

	/**
	 * This tactic should be called by the parameterised auto tactic.
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @param configId
	 *            the selected solver id
	 * @return the SMT tactic
	 */
	public static ITactic externalSMTThroughVeriT(boolean restricted,
			final String configId) {
		if (configId.isEmpty() || configId.equals(ALL_SOLVER_CONFIGURATIONS)) {
			final List<ISolverConfig> enabledConfigs = getPreferenceManager()
					.getSolverConfigsPrefs().getEnabledConfigs();
			if (enabledConfigs != null && !enabledConfigs.isEmpty()) {
				final int nbSolverConfigs = enabledConfigs.size();
				final ITactic smtTactics[] = new ITactic[nbSolverConfigs];
				for (int i = 0; i < nbSolverConfigs; i++) {
					smtTactics[i] = reasonerTac(new ExternalSMTThroughVeriT(),
							new SMTInput(restricted, enabledConfigs.get(i)));
				}
				return composeUntilSuccess(smtTactics);
			} else {
				return failTac(NO_SOLVER_CONFIGURATION_ERROR);
			}
		} else {
			final ISolverConfig config = getPreferenceManager()
					.getSolverConfigsPrefs().getSolverConfig(configId);
			return reasonerTac(new ExternalSMTThroughVeriT(), //
					new SMTInput(restricted, config));
		}
	}

	/**
	 * <p>
	 * Returns a tactic for applying the SMT prover to a proof tree node
	 * (sequent), translated using veriT. This is a convenience method, fully
	 * equivalent to:
	 * 
	 * <pre>
	 * externalSMTThroughPP(forces, DEFAULT_DELAY)
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param restricted
	 *            true iff only selected hypotheses should be considered by the
	 *            reasoner
	 * @return a tactic for running SMTTacticProvider with the given forces
	 */
	public static ITactic externalSMTThroughVeriT(final boolean restricted) {
		return externalSMTThroughVeriT(restricted, ALL_SOLVER_CONFIGURATIONS);
	}
}