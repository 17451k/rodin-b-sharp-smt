/*******************************************************************************
 * Copyright (c) 2011, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.ast.theories;

import org.eventb.smt.core.internal.ast.symbols.SMTSortSymbol;

/**
 * This is the interface for integer sort
 */
public interface IIntSort {

	/**
	 * returns the integer sort.
	 * 
	 * @return the integer sort.
	 */
	public SMTSortSymbol getIntegerSort();
}
