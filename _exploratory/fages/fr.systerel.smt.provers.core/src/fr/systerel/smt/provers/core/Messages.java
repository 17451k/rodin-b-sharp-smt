/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.smt.provers.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "fr.systerel.smt.provers.core.messages"; //$NON-NLS-1$
	public static String SMTProversCore_NoSMTSolverSelected;
	public static String SMTProversCore_NoSMTSolverSet;
	public static String SMTProversCore_ProofTreeOriginError;
	public static String SMTProversCore_SMTSolverConfigError;
	public static String SMTProversCore_VeriTPathNotSet;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// do not instantiate
	}
}
