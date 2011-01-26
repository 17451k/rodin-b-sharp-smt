/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import java.math.BigInteger;

import org.eventb.core.ast.Type;

/**
 * This class is the factory class for all the AST nodes of an SMT-LIB formula.
 */
public final class SMTFactory {
	private final static SMTFactory DEFAULT_INSTANCE = new SMTFactory();

	public final static SMTTerm[] EMPTY_TERM = {};
	public final static SMTSortSymbol[] EMPTY_SORT = {};
	public final static SMTPredicateSymbol[] EMPTY_PREDICATE = {};

	public final static String OPAR = "(";
	public final static String CPAR = ")";
	public final static String SPACE = " ";
	public final static String QVAR = "?";

	public final static String ITE_TERM = "ite";

	/**
	 * Connective symbols
	 */
	public final static SMTConnective NOT = SMTConnective.NOT;
	public final static SMTConnective IMPLIES = SMTConnective.IMPLIES;
	public final static SMTConnective ITE_FORMULA = SMTConnective.ITE;
	public final static SMTConnective AND = SMTConnective.AND;
	public final static SMTConnective OR = SMTConnective.OR;
	public final static SMTConnective XOR = SMTConnective.XOR;
	public final static SMTConnective IFF = SMTConnective.IFF;

	/**
	 * Propositionnal atoms
	 */
	public final static SMTPredicateSymbol PTRUE = new SMTPredicateSymbol(
			"true", EMPTY_SORT, SMTSymbol.PREDEFINED);
	public final static SMTPredicateSymbol PFALSE = new SMTPredicateSymbol(
			"false", EMPTY_SORT, SMTSymbol.PREDEFINED);

	/**
	 * Quantifier symbols
	 */
	public final static SMTQuantifierSymbol EXISTS = SMTQuantifierSymbol.EXISTS;
	public final static SMTQuantifierSymbol FORALL = SMTQuantifierSymbol.FORALL;

	/**
	 * Returns the default instance of the factory.
	 * 
	 * @return the single instance of this class
	 */
	public static SMTFactory getDefault() {
		return DEFAULT_INSTANCE;
	}

	/**
	 * Creates a new atomic formula from a relation expression. {EQUAL, LT, LE,
	 * GT, GE}
	 */
	public SMTFormula makeEqual(final SMTPredicateSymbol equal, final SMTTerm[] args) {
		return new SMTAtom(equal, args);
	}

	public SMTFormula makeNotEqual(final SMTPredicateSymbol equal, final SMTTerm[] args) {
		final SMTFormula[] tabEqual = { makeEqual(equal, args) };
		return makeNot(tabEqual);
	}

	public SMTFormula makeLesserThan(final SMTPredicateSymbol lt, final SMTTerm[] args) {
		return new SMTAtom(lt, args);
	}

	public SMTFormula makeLesserEqual(final SMTPredicateSymbol le, final SMTTerm[] args) {
		return new SMTAtom(le, args);
	}

	public SMTFormula makeGreaterThan(final SMTPredicateSymbol gt, final SMTTerm[] args) {
		return new SMTAtom(gt, args);
	}

	public SMTFormula makeGreaterEqual(final SMTPredicateSymbol ge, final SMTTerm[] args) {
		return new SMTAtom(ge, args);
	}

	/**
	 * Creates a new arithmetic term. {PLUS, MINUS, MUL, UMINUS}
	 */
	public SMTTerm makePlus(final SMTFunctionSymbol plus, final SMTTerm[] args) {
		return new SMTFunApplication(plus, args);
	}

	public SMTTerm makeMinus(final SMTFunctionSymbol minus, final SMTTerm[] args) {
		return new SMTFunApplication(minus, args);
	}

	public SMTTerm makeMul(final SMTFunctionSymbol mul, final SMTTerm[] args) {
		return new SMTFunApplication(mul, args);
	}

	public SMTTerm makeUMinus(final SMTFunctionSymbol uminus, final SMTTerm[] arg) {
		return new SMTFunApplication(uminus, arg);
	}

	/**
	 * Creates a new connective formula. {NOT, IMPLIES, IF_THEN_ELSE, AND, OR,
	 * XOR, IFF}
	 */
	public SMTFormula makeNot(SMTFormula[] formula) {
		return new SMTConnectiveFormula(NOT, formula);
	}

	public SMTFormula makeImplies(SMTFormula[] formulas) {
		return new SMTConnectiveFormula(IMPLIES, formulas);
	}

	public SMTFormula makeIfThenElse(SMTFormula[] formulas) {
		return new SMTConnectiveFormula(ITE_FORMULA, formulas);
	}

	public SMTFormula makeAnd(SMTFormula[] formulas) {
		return new SMTConnectiveFormula(AND, formulas);
	}

	public SMTFormula makeOr(SMTFormula[] formulas) {
		return new SMTConnectiveFormula(OR, formulas);
	}

	public SMTFormula makeXor(SMTFormula[] formulas) {
		return new SMTConnectiveFormula(XOR, formulas);
	}

	public SMTFormula makeIff(SMTFormula[] formulas) {
		return new SMTConnectiveFormula(IFF, formulas);
	}

	/**
	 * Creates a new numeral.
	 * 
	 * @param value
	 *            the value for this numeral
	 * @return the newly created numeral
	 */
	public SMTNumeral makeNumeral(BigInteger value) {
		return new SMTNumeral(value);
	}

	/**
	 * Creates a new boolean.
	 */
	// TODO When BOOL theory implemented
	/*
	 * public SMTBoolean makeBoolean() { return new SMTBoolean(); }
	 */

	/**
	 * Creates a new ITE_FORMULA term.
	 * 
	 * @param formula
	 *            a SMT formula
	 * @param tTerm
	 *            an SMT term
	 * @param fTerm
	 *            an SMT term
	 * @return the newly created ITE_FORMULA term
	 */
	public SMTITETerm makeITETerm(SMTFormula formula, SMTTerm tTerm,
			SMTTerm fTerm) {
		return new SMTITETerm(formula, tTerm, fTerm);
	}

	/**
	 * Creates a new propositional atom. {FALSE, TRUE}
	 */
	public SMTFormula makePTrue() {
		return this.makePropAtom(PTRUE);
	}

	public SMTFormula makePFalse() {
		return this.makePropAtom(PFALSE);
	}

	public SMTTerm makeVar(final String identifier, final Type type) {
		// FIXME this is not correct
		return new SMTVar(new SMTVarSymbol(identifier, new SMTSortSymbol(
				type.toString(), !SMTSymbol.PREDEFINED), !SMTSymbol.PREDEFINED));
	}

	/**
	 * Creates a new quantified pred.
	 */
	public SMTFormula makeForAll(final SMTTerm[] terms, final SMTFormula formula) {
		return new SMTQuantifiedFormula(FORALL, null, formula);
	}

	public SMTFormula makeExists(final SMTTerm[] terms, final SMTFormula formula) {
		return new SMTQuantifiedFormula(EXISTS, null, formula);
	}

	public SMTTerm makeFunApplication(final SMTFunctionSymbol functionSymbol,
			final SMTTerm[] args) {
		return new SMTFunApplication(functionSymbol, args);
	}

	public SMTFormula makeAtom(final SMTPredicateSymbol predicateSymbol,
			final SMTTerm[] args) {
		return new SMTAtom(predicateSymbol, args);
	}

	public SMTTerm makeConstant(final SMTFunctionSymbol functionSymbol) {
		return makeFunApplication(functionSymbol, EMPTY_TERM);
	}

	public SMTFormula makePropAtom(final SMTPredicateSymbol predicateSymbol) {
		return makeAtom(predicateSymbol, EMPTY_TERM);
	}
}
