/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.symbols;

import static org.eventb.smt.core.internal.ast.SMTFactory.SPACE;

/**
 * The SMT sorts.
 */
public class SMTSortSymbol extends SMTSymbol implements
		Comparable<SMTSortSymbol> {
	private final int arity;

	public SMTSortSymbol(final String symbolName, final int arity,
			final boolean predefined) {
		super(symbolName, predefined);
		this.arity = arity;
	}

	public SMTSortSymbol(final String symbolName, final boolean predefined) {
		this(symbolName, 0, predefined);
	}

	@Override
	public int compareTo(final SMTSortSymbol symbol) {
		return name.compareTo(symbol.getName());
	}

	/**
	 * Tells whether this sort is compatible with the given sort. Two sorts are
	 * compatible if either one is polymorphic or if they are equal.
	 * 
	 * @param other
	 *            the other sort to test for compatibility
	 * 
	 * @return whether this sort is compatible with the given sort
	 */
	public boolean isCompatibleWith(final SMTSortSymbol other) {
		return equals(other);
	}

	@Override
	public void toString(final StringBuilder builder) {
		builder.append(this.toString());
		builder.append(SPACE);
		builder.append(arity);
	}
}
