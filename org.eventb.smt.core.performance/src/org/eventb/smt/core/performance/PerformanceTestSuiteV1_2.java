/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.performance;

import org.eventb.smt.core.performance.solvers.altergo.AltErgoPerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.altergo.AltErgoPerfWithVeriTV1_2;
import org.eventb.smt.core.performance.solvers.cvc3.Cvc3PerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.cvc3.Cvc3PerfWithVeriTV1_2;
import org.eventb.smt.core.performance.solvers.cvc4.Cvc4PerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.cvc4.Cvc4PerfWithVeriTV1_2;
import org.eventb.smt.core.performance.solvers.mathsat5.MathSat5PerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.mathsat5.MathSat5PerfWithVeriTV1_2;
import org.eventb.smt.core.performance.solvers.opensmt.OpenSMTPerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.opensmt.OpenSMTPerfWithVeriTV1_2;
import org.eventb.smt.core.performance.solvers.verit.VeriTPerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.verit.VeriTPerfWithVeriTV1_2;
import org.eventb.smt.core.performance.solvers.z3.Z3PerfWithPPV1_2;
import org.eventb.smt.core.performance.solvers.z3.Z3PerfWithVeriTV1_2;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { AltErgoPerfWithPPV1_2.class, Cvc3PerfWithPPV1_2.class,
		Cvc4PerfWithPPV1_2.class, MathSat5PerfWithPPV1_2.class,
		OpenSMTPerfWithPPV1_2.class, VeriTPerfWithPPV1_2.class,
		Z3PerfWithPPV1_2.class, AltErgoPerfWithVeriTV1_2.class,
		Cvc3PerfWithVeriTV1_2.class, Cvc4PerfWithVeriTV1_2.class,
		MathSat5PerfWithVeriTV1_2.class, OpenSMTPerfWithVeriTV1_2.class,
		VeriTPerfWithVeriTV1_2.class, Z3PerfWithVeriTV1_2.class })
public class PerformanceTestSuiteV1_2 {
	// Just for tests
}
