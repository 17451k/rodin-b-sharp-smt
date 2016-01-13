/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.solvers.opensmt;

import static org.eventb.smt.tests.ConfigProvider.LAST_OPENSMT;

import java.util.Arrays;
import java.util.List;

import org.eventb.smt.core.performance.solvers.SolverPerfWithVeriT;
import org.junit.Ignore;
import org.junit.Test;

public class OpenSMTPerfWithVeriTV2_0 extends SolverPerfWithVeriT {

	public OpenSMTPerfWithVeriTV2_0() {
		super(LAST_OPENSMT);
	}

	@Test
	@Ignore("Unexpected error when executed in the test suite")
	// TODO look for the error
	public void testUnsatOpenSMTCall() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("opensmt_unsat", hyps, "x < z", arith_te, !VALID);
	}

	@Test
	public void testSatOpenSMTCall() {
		final List<String> hyps = Arrays.asList( //
				"x < y", //
				"y < z");

		doTest("opensmt_sat", hyps, "x > z", arith_te, !VALID);
	}
}
