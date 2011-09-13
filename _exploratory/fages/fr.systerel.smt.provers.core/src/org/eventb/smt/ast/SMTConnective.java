/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents SMT Connectives.
 * 
 * @author guyot
 * 
 */
public enum SMTConnective {
	NOT("not"), //
	IMPLIES("implies"), //
	ITE("if_then_else"), //
	AND("and"), //
	OR("or"), //
	XOR("xor"), //
	IFF("iff");

	/**
	 * The symbol of the connective
	 */
	private String symbol;

	/**
	 * Constructs a new SMT connective with a symbol.
	 * 
	 * @param symbol
	 *            the connective symbol
	 */
	SMTConnective(final String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return symbol;
	}

	/**
	 * Retrieves all the SMT connective symbols.
	 * 
	 * @return the list with all the connective symbols.
	 */
	public static final List<String> getConnectiveSymbols() {
		final SMTConnective[] smtConnectives = SMTConnective.values();
		final List<String> connectives = new ArrayList<String>(
				smtConnectives.length);
		for (final SMTConnective connective : smtConnectives) {
			connectives.add(connective.toString());
		}
		return connectives;
	}
}