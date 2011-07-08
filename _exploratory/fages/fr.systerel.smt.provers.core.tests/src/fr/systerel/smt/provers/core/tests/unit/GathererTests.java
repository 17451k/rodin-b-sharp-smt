/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.core.tests.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pptrans.Translator;
import org.junit.Test;

import br.ufrn.smt.solver.translation.Gatherer;
import fr.systerel.smt.provers.core.tests.AbstractTests;

/**
 * 
 * @author Laurent Voisin
 */
public class GathererTests extends AbstractTests {

	private static class Option {
		private final boolean isTrue;

		public Option(final boolean isTrue) {
			this.isTrue = isTrue;
		}

		public void check(final boolean actual) {
			assertEquals(isTrue, actual);
		}

	}

	static class AtomicBoolExp extends Option {
		static final AtomicBoolExp FOUND = new AtomicBoolExp(true);
		static final AtomicBoolExp NOT_FOUND = new AtomicBoolExp(false);

		private AtomicBoolExp(final boolean isTrue) {
			super(isTrue);
		}
	}

	static class AtomicIntegerExp extends Option {
		static final AtomicIntegerExp FOUND = new AtomicIntegerExp(true);
		static final AtomicIntegerExp NOT_FOUND = new AtomicIntegerExp(false);

		private AtomicIntegerExp(final boolean isTrue) {
			super(isTrue);
		}
	}

	static class BoolTheory extends Option {
		static final BoolTheory FOUND = new BoolTheory(true);
		static final BoolTheory NOT_FOUND = new BoolTheory(false);

		private BoolTheory(final boolean isTrue) {
			super(isTrue);
		}
	}

	static class TruePredicate extends Option {
		static final TruePredicate FOUND = new TruePredicate(true);
		static final TruePredicate NOT_FOUND = new TruePredicate(false);

		private TruePredicate(final boolean isTrue) {
			super(isTrue);
		}
	}

	private static void doTest(final ITypeEnvironment typenv,
			final AtomicBoolExp atomicBoolExp,
			final AtomicIntegerExp atomicIntegerExp,
			final BoolTheory boolTheory, final TruePredicate truePredicate,
			final String[] expectedMonadicPreds, final String[] hypotheses,
			final String goal) {

		final List<Predicate> preds = new ArrayList<Predicate>();
		for (final String hypothesis : hypotheses) {
			final Predicate h = parse(hypothesis, typenv);
			assertTrue("Predicate: " + h.toString()
					+ " is not in the PP sub-language.", Translator.isInGoal(h));
			preds.add(h);
		}
		final Predicate goalP = parse(goal, typenv);
		assertTrue("Predicate: " + goalP.toString()
				+ " is not in the PP sub-language.", Translator.isInGoal(goalP));
		final Gatherer result = Gatherer.gatherFrom(preds, goalP);

		checkResult(atomicBoolExp, atomicIntegerExp, boolTheory, truePredicate,
				expectedMonadicPreds, result);
	}

	/**
	 * @param atomicBoolExp
	 * @param atomicIntegerExp
	 * @param boolTheory
	 * @param truePredicate
	 * @param expectedMonadicPreds
	 * @param result
	 */
	private static void checkResult(final AtomicBoolExp atomicBoolExp,
			final AtomicIntegerExp atomicIntegerExp,
			final BoolTheory boolTheory, final TruePredicate truePredicate,
			final String[] expectedMonadicPreds, final Gatherer result) {
		atomicBoolExp.check(result.foundAtomicBoolExp());
		atomicIntegerExp.check(result.foundAtomicIntegerExp());
		boolTheory.check(result.usesBoolTheory());
		truePredicate.check(result.usesTruePredicate());
		checkMonadicPreds(expectedMonadicPreds, result.getSetsForMonadicPreds());
	}

	private static String notEncounteredMonadicPredMessage(
			final String monadicPred) {
		return "The monadic predicate: " + monadicPred.toString()
				+ " was not expected";
	}

	private static String monadicPredsErrorMessage(
			final Set<FreeIdentifier> setsOfMonadicPreds) {
		return "The encountered monadic predicates were: "
				+ setsOfMonadicPreds.toString();
	}

	private static void checkMonadicPreds(final String[] expectedMonadicPreds,
			final Set<FreeIdentifier> setsForMonadicPreds) {
		assertEquals(monadicPredsErrorMessage(setsForMonadicPreds),
				expectedMonadicPreds.length, setsForMonadicPreds.size());

		final List<String> eMonPreds = Arrays.asList(expectedMonadicPreds);

		for (final FreeIdentifier monadicPred : setsForMonadicPreds) {
			assertTrue(
					notEncounteredMonadicPredMessage(monadicPred.toString()),
					eMonPreds.contains(monadicPred.toString()));
		}
	}

	@Test
	public void test1() {
		final String[] expectedMonadicPreds = {};

		doTest(mTypeEnvironment("a", "ℤ", "b", "ℤ"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.NOT_FOUND, //
				TruePredicate.NOT_FOUND, //
				expectedMonadicPreds, new String[] {},// No Monadic Pre
				"(a = b)");
	}

	@Test
	public void test2() {
		final String[] expectedMonadicPreds = {};

		doTest(mTypeEnvironment("a", "ℤ"),//
				AtomicBoolExp.FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.FOUND, //
				expectedMonadicPreds, new String[] {}, "a↦BOOL↦BOOL ∈ X");
	}

	@Test
	public void test3() {
		final String[] expectedMonadicPreds = {};

		doTest(mTypeEnvironment(),//
				AtomicBoolExp.FOUND, //
				AtomicIntegerExp.FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.FOUND, //
				expectedMonadicPreds, new String[] {}, // No Monadic Pred
				"(a↦BOOL↦ℤ ∈ X) ∧ (a = TRUE) ");
	}

	@Test
	public void test4() {
		final String[] expectedMonadicPreds = { "G" };

		doTest(mTypeEnvironment("a", "BOOL", "g", "ℤ", "G", "ℙ(ℤ)"),//
				AtomicBoolExp.FOUND, //
				AtomicIntegerExp.FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.FOUND, //
				expectedMonadicPreds, new String[] {},// Monadic Pred
				"(a↦BOOL↦ℤ ∈ X) ∧ (g ∈ G)");
	}

	@Test
	public void test5() {
		final String[] expectedMonadicPreds = { "G" };

		doTest(mTypeEnvironment("a", "BOOL", "g", "ℤ", "G", "ℙ(ℤ)"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.FOUND, //
				expectedMonadicPreds, new String[] {}, "(a↦ℤ ∈ X) ∧ (g ∈ G)");
	}

	@Test
	public void test5_1() {
		final String[] expectedMonadicPreds = {};

		doTest(mTypeEnvironment("x", "BOOL", "z", "BOOL"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.NOT_FOUND, //
				BoolTheory.FOUND, //
				TruePredicate.NOT_FOUND, //
				expectedMonadicPreds, new String[] {}, //
				"(x = z)");
	}

	@Test
	public void test6() {
		final String[] expectedMonadicPreds = { "X" };

		doTest(mTypeEnvironment("a", "ℤ"),//
				AtomicBoolExp.NOT_FOUND, //
				AtomicIntegerExp.FOUND, //
				BoolTheory.NOT_FOUND, //
				TruePredicate.NOT_FOUND, //
				expectedMonadicPreds, new String[] {},//
				// FIXME Goal
				"(ℤ↦a ∈ P) ∧ (a ∈ X)");
	}
}