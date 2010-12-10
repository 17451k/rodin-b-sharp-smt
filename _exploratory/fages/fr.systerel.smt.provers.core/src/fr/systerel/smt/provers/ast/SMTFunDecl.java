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

/**
 * @author guyot
 * 
 */
public class SMTFunDecl {
	private SMTIdentifier fun;
	private SMTSort sort;

	public SMTFunDecl(String varName, SMTSort sort) {
		this.fun = new SMTIdentifier(varName);
		this.sort = sort;
	}

	public void toString(final StringBuilder buffer) {
		buffer.append("(");
		this.fun.toString(buffer);
		buffer.append(" ");
		this.sort.toString(buffer);
		buffer.append(")");
	}

	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("(");
		this.fun.toString(buffer);
		buffer.append(" ");
		this.sort.toString(buffer);
		buffer.append(")");
		return buffer.toString();
	}
}