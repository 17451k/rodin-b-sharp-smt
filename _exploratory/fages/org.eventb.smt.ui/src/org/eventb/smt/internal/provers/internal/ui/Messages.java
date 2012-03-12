/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.provers.internal.ui;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author Systerel (yguyot)
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eventb.smt.internal.provers.internal.ui.messages"; //$NON-NLS-1$
	public static String SMTFailureTactic_NoSMTSolverSelected;
	public static String SMTFailureTactic_NoSMTSolverSet;
	public static String SMTFailureTactic_ProofTreeOriginError;
	public static String SMTFailureTactic_SMTSolverConfigError;
	public static String SMTFailureTactic_VeriTPathNotSet;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// do not instantiate
	}
}
