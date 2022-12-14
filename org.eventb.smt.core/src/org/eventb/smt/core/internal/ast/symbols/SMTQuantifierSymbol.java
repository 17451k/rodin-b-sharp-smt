/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.symbols;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents SMT quantifier symbols
 */
public enum SMTQuantifierSymbol {
	EXISTS("exists"), //
	FORALL("forall");

	/**
	 * the symbol string
	 */
	private String symbol;

	/**
	 * Constructs a new SMT quantifier symbol
	 * 
	 * @param symbol
	 *            the symbol string
	 */
	private SMTQuantifierSymbol(final String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return symbol;
	}

	/**
	 * returns a list with the quantifier symbols
	 * 
	 * @return a list with the quantifier symbols
	 */
	public static final List<String> getQuantifierSymbols() {
		final SMTQuantifierSymbol[] smtQuantifiers = SMTQuantifierSymbol
				.values();
		final List<String> quantifiers = new ArrayList<String>(
				smtQuantifiers.length);
		for (final SMTQuantifierSymbol quantifier : smtQuantifiers) {
			quantifiers.add(quantifier.toString());
		}
		return quantifiers;
	}
}
