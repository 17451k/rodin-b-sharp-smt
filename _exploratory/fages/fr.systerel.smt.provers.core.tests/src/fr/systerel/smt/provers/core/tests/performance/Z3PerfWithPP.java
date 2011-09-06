/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.smt.provers.core.tests.performance;

import static fr.systerel.smt.provers.internal.core.SMTSolver.Z3;
import fr.systerel.smt.provers.core.tests.SolverPerfWithPP;

public class Z3PerfWithPP extends SolverPerfWithPP {

	public Z3PerfWithPP() {
		super(Z3);
	}

}
