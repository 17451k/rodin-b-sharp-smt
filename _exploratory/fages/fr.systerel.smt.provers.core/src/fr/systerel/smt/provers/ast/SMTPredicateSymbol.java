/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

import java.util.Arrays;

/**
 * This class represents SMT Predicate Symbols. *
 */
public class SMTPredicateSymbol extends SMTSymbol implements
		Comparable<SMTPredicateSymbol> {

	/**
	 * The rank (as defined in SMT-LIB SMTSignature definition). Remind that it
	 * is possible to associate a predicate to the empty sequence rank, denoting
	 * that the predicate is a propositional predicate.
	 */
	final private SMTSortSymbol[] argSorts;

	/**
	 * True if the predicate is associative, false otherwise.
	 */
	final private boolean isAssociative;

	/**
	 * Constructs a new predicate symbol.
	 * 
	 * @param symbolName
	 *            The name of the symbol
	 * @param predefined
	 *            true if it's predefined, false otherwise
	 * @param argSorts
	 *            the expected sorts of the arguments.
	 */
	public SMTPredicateSymbol(final String symbolName,
			final boolean predefined, final SMTSortSymbol... argSorts) {
		super(symbolName, predefined);
		this.argSorts = argSorts.clone();
		isAssociative = false;
	}

	/**
	 * return true if it's associative, false otherwise.
	 * 
	 * @return true if it's associative, false otherwise.
	 */
	public boolean isAssociative() {
		return isAssociative;
	}

	/**
	 * return a list with the expected sorts of the arguments.
	 * 
	 * @return a list with the expected sorts of the arguments.
	 */
	public SMTSortSymbol[] getArgSorts() {
		return argSorts;
	}

	/**
	 * Constructs a new predicate symbol.
	 * 
	 * @param symbolName
	 *            The name of the symbol
	 * @param argSorts
	 *            the expected sorts of the arguments.
	 * @param predefined
	 *            true if it's predefined, false otherwise
	 * @param isAssociative
	 *            true if the predicate is associative, false otherwise
	 */
	public SMTPredicateSymbol(final String symbolName,
			final SMTSortSymbol argSorts[], final boolean predefined,
			final boolean isAssociative) {
		super(symbolName, predefined);
		this.argSorts = argSorts.clone();
		this.isAssociative = isAssociative;
	}

	/**
	 * returns true if the predicate symbol is propositional, false otherwise.
	 * 
	 * @return true if the predicate symbol is propositional, false otherwise.
	 */
	public boolean isPropositional() {
		return argSorts.length == 0;
	}

	/**
	 * returns true if the argument sorts of this predicate symbol is equal
	 * {@code argSorts2}, false otherwise.
	 * 
	 * @param argSorts2
	 *            the list of sort symbols.
	 * @return true if they are equal, false otherwise
	 */
	public boolean hasRank(final SMTSortSymbol[] argSorts2) {
		return Arrays.equals(argSorts, argSorts2);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(final StringBuilder buffer) {
		buffer.append(OPAR);
		buffer.append(name);
		for (final SMTSortSymbol sort : argSorts) {
			buffer.append(SPACE);
			buffer.append(sort);
		}
		buffer.append(CPAR);
	}

	@Override
	public int compareTo(final SMTPredicateSymbol symbol) {
		final int nameComp = name.compareTo(symbol.getName());
		if (nameComp == 0) {
			if (argSorts.length < symbol.argSorts.length) {
				return -1;
			} else if (argSorts.length > symbol.argSorts.length) {
				return 1;
			} else {
				for (int i = 0; i < argSorts.length; i++) {
					final int argComp = argSorts[i]
							.compareTo(symbol.argSorts[i]);
					if (argComp != 0) {
						return argComp;
					}
				}
				return 0;
			}
		} else {
			return nameComp;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(argSorts);
		return result;
	}

	/**
	 * This class represents the predicate symbol equals (=)
	 */
	public static class SMTEqual extends SMTPredicateSymbol {
		public SMTEqual(final SMTSortSymbol... sort) {
			super(EQUAL, PREDEFINED, sort);
		}
	}
}
