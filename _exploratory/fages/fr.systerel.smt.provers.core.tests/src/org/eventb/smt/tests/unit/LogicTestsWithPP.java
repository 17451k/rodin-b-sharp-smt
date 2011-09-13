/*******************************************************************************
 * Copyright (c) 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.tests.unit;

import static org.eventb.smt.translation.SMTLIBVersion.V1_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pptrans.Translator;
import org.eventb.smt.ast.theories.SMTLogic;
import org.eventb.smt.ast.theories.SMTTheoryV1_2;
import org.eventb.smt.tests.AbstractTests;
import org.eventb.smt.translation.SMTThroughPP;
import org.junit.Test;

public class LogicTestsWithPP extends AbstractTests {
	private static final ITypeEnvironment defaultTe;
	private static final SMTLogic smtLibUnderlyingLogic;
	private static final SMTLogic boolLogic;
	static {
		defaultTe = mTypeEnvironment("a", "ℤ", "p", "BOOL", "P", "ℙ(BOOL)");
		boolLogic = new SMTLogic.SMTLogicPP(SMTLogic.UNKNOWN,
				SMTTheoryV1_2.Ints.getInstance(),
				SMTTheoryV1_2.Booleans.getInstance());
		smtLibUnderlyingLogic = SMTLogic.SMTLIBUnderlyingLogicV1_2
				.getInstance();
	}

	private static void testLogic(final ITypeEnvironment iTypeEnv,
			final String ppPredStr, final SMTLogic expectedSMTLogic) {
		final Predicate goalPredicate = parse(ppPredStr, iTypeEnv);
		assertTrue("\'" + ppPredStr + "\' isn't a valid input.",
				Translator.isInGoal(goalPredicate));

		final SMTLogic logic = SMTThroughPP.determineLogic(goalPredicate, V1_2);

		assertEquals("", expectedSMTLogic.toString(), logic.toString());
	}

	@Test
	public void testInt() {
		testLogic(defaultTe, "a = 1", smtLibUnderlyingLogic);
	}

	@Test
	public void testBool() {
		/**
		 * Reaches org.eventb.smt.translation.SMTThroughPP.BoolTheoryVisitor.
		 * visitTRUE(AtomicExpression)
		 */
		testLogic(defaultTe, "TRUE = p", boolLogic);
		/**
		 * Reaches org.eventb.smt.translation.SMTThroughPP.BoolTheoryVisitor.
		 * visitBOOL(AtomicExpression)
		 */
		testLogic(defaultTe, "a↦BOOL↦BOOL ∈ X", boolLogic);
		/**
		 * Reaches org.eventb.smt.translation.SMTThroughPP.BoolTheoryVisitor.
		 * visitBOUND_IDENT_DECL(BoundIdentDecl)
		 */
		testLogic(defaultTe,
				"∀ x ⦂ ℤ, X ⦂ ℙ(ℤ), P ⦂ BOOL · (x ∈ X ⇒ P = TRUE)", boolLogic);
		/**
		 * Reaches org.eventb.smt.translation.SMTThroughPP.BoolTheoryVisitor.
		 * enterIN(RelationalPredicate)
		 */
		testLogic(defaultTe, "p ∈ P", boolLogic);
	}
}