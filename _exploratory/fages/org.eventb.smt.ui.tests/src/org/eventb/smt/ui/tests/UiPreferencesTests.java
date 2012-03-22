/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.tests;

import static org.eventb.smt.core.preferences.AbstractPreferences.DEFAULT_SELECTED_CONFIG;
import static org.eventb.smt.core.preferences.AbstractPreferences.DEFAULT_TRANSLATION_PATH;
import static org.eventb.smt.core.provers.SolverKind.ALT_ERGO;
import static org.eventb.smt.core.provers.SolverKind.CVC3;
import static org.eventb.smt.core.provers.SolverKind.VERIT;
import static org.eventb.smt.core.provers.SolverKind.Z3;
import static org.eventb.smt.core.translation.SMTLIBVersion.V1_2;

import org.eventb.smt.core.preferences.AbstractPreferences;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.preferences.SolverConfigFactory;
import org.eventb.smt.core.provers.SolverKind;
import org.eventb.smt.core.translation.SMTLIBVersion;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author vitor
 * 
 */
public class UiPreferencesTests {
	/**
	 * In linux: '/home/username/bin/'
	 */
	protected static final String BIN_PATH = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "bin"
			+ System.getProperty("file.separator");

	protected static void setSolverPreferences(final String solverBinaryName,
			final SolverKind solver, final String solverArgs,
			final SMTLIBVersion smtlibVersion) {
		final AbstractPreferences smtPrefs = AbstractPreferences.getSMTPrefs();
		final String OS = System.getProperty("os.name");
		final String solverPath;

		if (OS.startsWith("Windows")) {
			solverPath = BIN_PATH + solverBinaryName + ".exe";
		} else {
			solverPath = BIN_PATH + solverBinaryName;
		}

		System.out.println(solverPath);

		smtPrefs.addSolverConfig(SolverConfigFactory.newConfig(
				solverBinaryName, solverBinaryName, solver.toString(),
				solverArgs, smtlibVersion));
		smtPrefs.setSelectedConfigID(false, DEFAULT_SELECTED_CONFIG);
		smtPrefs.setTranslationPath(DEFAULT_TRANSLATION_PATH);
		smtPrefs.setVeriTPath(BIN_PATH + VERIT);
	}

	protected static void setPreferencesForVeriTTest() {
		setSolverPreferences(VERIT.toString(), VERIT, "", V1_2);
	}

	protected void setPreferencesForCvc3Test() {
		setSolverPreferences(CVC3.toString(), CVC3, "-lang smt", V1_2);

	}

	protected void setPreferencesForZ3Test() {
		String solver = Z3.toString();
		if (System.getProperty("os.name").startsWith("Windows")) {
			solver = "bin" + System.getProperty("file.separator") + Z3
					+ System.getProperty("file.separator") + "bin"
					+ System.getProperty("file.separator") + Z3;
		}

		setSolverPreferences(solver, Z3, "", V1_2);
	}

	protected void setPreferencesForAltErgoTest() {
		setSolverPreferences(ALT_ERGO.toString(), ALT_ERGO, "", V1_2);
	}

	@Test
	public void testRecoverPreferences1() {
		setPreferencesForAltErgoTest();

		/**
		 * Get back preferences from UI
		 */
		final String expectedId = ALT_ERGO.toString();
		final String expectedName = ALT_ERGO.toString();
		final SolverKind expectedSolver = ALT_ERGO;
		final String args = "";
		final SMTLIBVersion smtlibVersion = V1_2;
		final String expectedVeriTPath = BIN_PATH + VERIT;

		final ISolverConfig expectedSolverConfig = SolverConfigFactory
				.newConfig(expectedName, expectedId, expectedSolver.toString(),
						args, smtlibVersion);
		final String expectedTranslationPath = DEFAULT_TRANSLATION_PATH;

		final AbstractPreferences smtPrefs = AbstractPreferences.getSMTPrefs();
		final ISolverConfig solverConfig = smtPrefs.getSelectedConfig();
		final String translationPath = smtPrefs.getTranslationPath();
		final String veritPath = smtPrefs.getVeriTPath();

		Assert.assertEquals(expectedSolverConfig, solverConfig);
		Assert.assertEquals(expectedTranslationPath, translationPath);
		Assert.assertEquals(expectedVeriTPath, veritPath);
	}
}
