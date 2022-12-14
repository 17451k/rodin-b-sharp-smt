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
package fr.systerel.decert.smt.ast;

/**
 * This class represents an identifier in SMT-LIB grammar.
 */
public final class SMTIdentifier extends SMTBaseTerm {

	/**
	 * Creates a new identifier.
	 * 
	 * @param identifier
	 *            the identifier
	 */
	SMTIdentifier(String identifier) {
		super(identifier, SMTNode.IDENTIFIER);
	}
}
