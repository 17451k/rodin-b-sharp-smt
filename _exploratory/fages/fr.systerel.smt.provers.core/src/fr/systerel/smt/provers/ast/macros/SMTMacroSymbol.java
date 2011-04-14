/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast.macros;

import java.util.HashSet;
import java.util.Set;

import fr.systerel.smt.provers.ast.SMTPredicateSymbol;
import fr.systerel.smt.provers.ast.SMTSortSymbol;

/**
 * This class is used to store the symbols that represent the macros. They
 * extend SMTPredicateSymbol because the return sort of macros are boolean, and
 * veriT interprets the return values of predicates as elements of the Bool
 * sort. This class also stores the symbols that are defined only in the
 * extended SMT-LIB, like macro names.
 * 
 * 
 * @author vitor
 * 
 */
public class SMTMacroSymbol extends SMTPredicateSymbol {

	// VeriT Extended SMT-LIB Symbols
	public static final String BUNION = "union";
	public static final String BINTER = "inter";
	public static final String EMPTY = "emptyset";
	public static final String INTER = "inter";
	public static final String SETMINUS = "setminus";
	public static final String IN = "in";
	public static final String SUBSETEQ = "subseteq";
	public static final String SUBSET = "subset";
	public static final String RANGE_INTEGER = "range";
	public static final String PROD = "prod";
	public static final String DOM = "dom";
	public static final String IMG = "img";
	public static final String DOMR = "domr";
	public static final String DOMS = "doms";
	public static final String INV = "inv";
	public static final String OVR = "ovr";
	public static final String ID = "id";
	public static final String FCOMP = "fcomp";

	public static final String DIV = "/";

	public static final String ENUM = "enum";
	public static final String MOD = "mod";
	public static final String RANGE_SUBSTRACION = "rans";
	public static final String RANGE_RESTRICTION = "ranr";
	public static final String RELATION = "rel";
	public static final String TOTAL_RELATION = "totp";
	public static final String SURJECTIVE_RELATION = "surp";
	public static final String TOTAL_SURJECTIVE_RELATION = "totsurp";
	public static final String PARTIAL_FUNCTION = "pfun";
	public static final String TOTAL_FUNCTION = "tfun";
	public static final String MAPSTO = "pair";
	public static final String NAT = "Nat";
	public static final String NAT1 = "Nat1";
	public static final String PARTIAL_INJECTION = "pinj";
	public static final String TOTAL_INJECTION = "tinj";
	public static final String PARTIAL_SURJECTION = "psur";
	public static final String TOTAL_SURJECTION = "tsur";
	public static final String TOTAL_BIJECTION = "bij";
	public static final String CARTESIAN_PRODUCT = "prod";
	public static final String DOMAIN_RESTRICTION = "domr";
	public static final String DOMAIN_SUBSTRACTION = "doms";
	public static final String RELATIONAL_IMAGE = "img";
	public static final String LAMBDA = "lambda";
	public static final String CSET = "cset";
	public static final String ELEM = "elem";
	public static final String ISMIN = "ismin";
	public static final String ISMAX = "ismax";
	public static final String FINITE = "finite";
	public static final String CARD = "card";
	public static final String PAIR = "Pair";

	public static final String FUNP = "funp";
	public static final String INJP = "injp";
	public static final String RANGE = "ran";
	public static final String NOT_EQUAL = "neq";
	public static final String BCOMP = "bcomp";

	/**
	 * Creates a new macro symbol.
	 * 
	 * @param symbolName
	 *            The string representation of the symbol
	 * @param argSorts
	 *            The expected sorts of the arguments
	 * @param predefined
	 *            If it's predefined
	 */
	SMTMacroSymbol(String symbolName, SMTSortSymbol[] argSorts,
			boolean predefined) {
		super(symbolName, argSorts, predefined);
	}

	/**
	 * Creates a new, not predefined symbol.
	 * 
	 * @param symbolName
	 *            The string representation of the symbol
	 * @param argSorts
	 *            The expected sorts of the arguments
	 */
	SMTMacroSymbol(String symbolName, SMTSortSymbol[] argSorts) {
		super(symbolName, argSorts, false);
	}
}
