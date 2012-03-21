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

import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eventb.smt.core.internal.preferences.SolverConfigRegistry;

/**
 * @author Systerel (yguyot)
 * 
 */
public abstract class AbstractSolverConfigRegistry {
	public static IRegistry<?> getSolverConfigRegistry()
			throws InvalidRegistryObjectException, ExtensionLoadingException {
		return SolverConfigRegistry.getSolverConfigRegistry();
	}
}