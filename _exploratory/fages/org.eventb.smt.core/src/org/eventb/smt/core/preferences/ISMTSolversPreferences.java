/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.preferences;

import java.util.Map;

/**
 * @author Systerel (yguyot)
 *
 */
public interface ISMTSolversPreferences extends IPreferences {

	public abstract Map<String, ISMTSolver> getSolvers();

	public abstract ISMTSolver get(final String solverId);

	public abstract void add(final ISMTSolver solver);

	public abstract void add(final ISMTSolver solver,
	final boolean replace);

	public abstract void remove(final String solverToRemove);

}
