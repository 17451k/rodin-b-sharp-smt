/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast.commands;

import fr.systerel.smt.provers.ast.SMTNumeral;

/**
 * A script command modifying the assertion-set stack.
 */
public abstract class SMTStackCommand extends SMTCommand {
	// =========================================================================
	// Variables
	// =========================================================================
	/** The number of assertion sets. */
	private final SMTNumeral n;

	// =========================================================================
	// Constructor
	// =========================================================================

	/**
	 * Creates a command with the specified tag.
	 * 
	 * @param n
	 *            the number of assertion sets
	 * @param tag
	 *            node tag of this command
	 * 
	 */
	SMTStackCommand(SMTNumeral n, int tag) {
		super(tag);
		this.n = n;
	}

	// =========================================================================
	// Other useful methods
	// =========================================================================
	@Override
	public void toString(StringBuilder builder) {
		builder.append('(');
		builder.append(tags[getTag() - firstTag]);
		builder.append(" ");
		n.toString(builder);
		builder.append(')');
	}
}
