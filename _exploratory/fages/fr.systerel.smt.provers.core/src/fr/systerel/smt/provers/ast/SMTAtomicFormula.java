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

import fr.systerel.smt.provers.internal.core.IllegalTagException;
import fr.systerel.smt.provers.internal.core.Messages;

/**
 * Common class for SMT-LIB formulas built from arithmetic operators.
 */
public class SMTAtomicFormula extends SMTFormula {
	
	// =========================================================================
	// Constants
	// =========================================================================
	/** Offset of the corresponding tag-interval in the <tt>SMTNode</tt> class. */
	private final static int firstTag = FIRST_ARITHMETIC_FORMULA;
		
	/** The tags. */
	private final static String[] tags = {
		"=", 
		"<",
		"<=",
		">",
		">="
	};
	
	// =========================================================================
	// Variables
	// =========================================================================
	/** The children. */
	private final SMTTerm[] children;
	
	// =========================================================================
	// Constructor
	// =========================================================================

	/**
	 * Creates a new atomic formula with the specified tag.
	 * 
	 * @param tag node tag of this term
	 */
	SMTAtomicFormula(int tag, SMTTerm[] children) {
		super(tag);
		this.children = children.clone();
		if (this.getTag() < firstTag || this.getTag() >= firstTag + tags.length) {
			throw new IllegalTagException(this.getTag());
		} else if (children.length < 1) {
			throw new IllegalArgumentException(Messages.SmtNode_This_node_expected_some_child);
		}
	}
	
	/**
	 * Creates a new atomic formula with the specified tag.
	 */
	SMTAtomicFormula(SMTIdentifier id) {
		super(SMTNode.IDENTIFIER);
		this.children = new SMTTerm [1];
		this.children[0] = id;
	}
	
	// =========================================================================
	// Getters
	// =========================================================================
	
	/**
	 * Returns the children of this node.
	 * 
	 * @return a list of children
	 */
	public SMTTerm[] getChildren() {
		return children.clone();
	}
	
	// =========================================================================
	// Other useful methods
	// =========================================================================
	
	@Override
	public void toString(StringBuilder builder) {
		if (super.getTag() == SMTNode.IDENTIFIER) {
			builder.append(children[0].toString());
		} else {
			builder.append('(');
			String sep = tags[getTag() - firstTag] + " ";
			for (SMTTerm child: children) {
				builder.append(sep);
				sep = " ";
				child.toString(builder);
			}
			builder.append(')');
		}
	}

}